package org.tzi.use.kodkod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.main.Session;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MSystemState;
import org.tzi.use.util.StringUtil;

/**
 * Class for the model validation with scrolling functionality using classifier terms.
 *
 * @author Frank Hilken
 */
public class UseCTScrollingKodkodModelValidator extends UseScrollingKodkodModelValidator {

	protected static class ClassifyingTerm {
		private final Expression oclExpr;
		private final Node kodkodExpr;
		
		public ClassifyingTerm(Expression oclExpr, Node kodkodExpr) {
			this.oclExpr = oclExpr;
			this.kodkodExpr = kodkodExpr;
		}

		public Expression oclExpression() {
			return oclExpr;
		}

		public Node kodkodExpression() {
			return kodkodExpr;
		}
	}

	protected final List<ClassifyingTerm> classifyingTerms = new ArrayList<ClassifyingTerm>();
	protected final List<Map<ClassifyingTerm, Value>> termSolutions = new ArrayList<>();

	protected final UseCTScrollingKodkodModelValidator parent;
	protected final Map<ClassifyingTerm, Value> termValues;
	
	public UseCTScrollingKodkodModelValidator(Session session, UseCTScrollingKodkodModelValidator parent) {
		super(session);
		this.parent = parent;
		if(parent != null){
			termValues = parent.termSolutions.get(parent.solutionIndex);
		} else {
			termValues = null;
		}
	}
	
	public UseCTScrollingKodkodModelValidator(Session session) {
		this(session, null);
	}
	
	public void copyParentSolutionAndUpdateSolutionTerms(MSystemState systemstate){
		solutions.add(parent.solutions.get(parent.solutionIndex));
		solutionIndex = solutions.size();
		readSolutionTerms(systemstate);
	}
	
	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			readSolutionTerms(session.system().state());
			
			solutions.add(solution.instance().relationTuples());
			solutionIndex = solutions.size()-1;
			LOG.info(LogMessages.pagingNext);
			previousLog();
		} else {
			session.reset();
			newSolution(solution.instance().relationTuples());
		}
	}

	@Override
	protected void newSolution(Map<Relation, TupleSet> relationTuples) {
		Formula f = genClassifyingTermFormula();
		((ModelConfigurator) model.getConfigurator()).setFixedFormula(f);
		validate(model);
	}

	protected Formula genClassifyingTermFormula() {
		Formula f = genFixCTFormula();
		
		for (Map<ClassifyingTerm, Value> solutions : termSolutions) {
			Formula currSolution = encodeClassifyingValues(solutions);
			f = f.and(currSolution.not());
		}
		return f;
	}
	
	protected Formula genFixCTFormula(){
		UseCTScrollingKodkodModelValidator validator = this;
		Formula f = Formula.TRUE;
		
		while(validator != null && validator.termValues != null){
			f = f.and(encodeClassifyingValues(validator.termValues));
			validator = validator.parent;
		}
		
		return f;
	}

	protected Formula encodeClassifyingValues(Map<ClassifyingTerm, Value> solutions) {
		Formula currSolution = null;
		
		for (Map.Entry<ClassifyingTerm, Value> ct : solutions.entrySet()){
			Formula solFormula = encodeSolutionValue(ct.getKey().kodkodExpr, ct.getValue());
			currSolution = (currSolution == null) ? solFormula : currSolution.and( solFormula ) ;
		}
		return (currSolution == null) ? Formula.TRUE : currSolution ;
	}
	
	/**
	 * Constructs a Kodkod {@link Formula} checking the equality of the given
	 * expression and value.
	 */
	private Formula encodeSolutionValue(kodkod.ast.Node exp, Value value) {
		if(exp instanceof kodkod.ast.Expression){
			if(value instanceof IntegerValue){
				return ((kodkod.ast.Expression) exp).eq(IntConstant.constant(((IntegerValue) value).value()).toExpression());
			}
			else if(value instanceof BooleanValue) {
				Map<String, kodkod.ast.Expression> typeLiterals = model.typeFactory().booleanType().typeLiterals();
				if(((BooleanValue) value).value()){
					return ((kodkod.ast.Expression) exp).eq(typeLiterals.get(TypeConstants.BOOLEAN_TRUE));
				} else {
					return ((kodkod.ast.Expression) exp).eq(typeLiterals.get(TypeConstants.BOOLEAN_FALSE));
				}
			}
			else {
				throw new TransformationException("Unsupported expression type found. (" + exp.getClass().toString() + " --- " + value.getClass().toString() + ")");
			}
		}
		else if(exp instanceof Formula){
			return ((BooleanValue) value).value() ? (Formula) exp : ((Formula) exp).not() ;
		}
		else if(exp instanceof IntExpression){
			return ((IntExpression) exp).eq(IntConstant.constant(((IntegerValue) value).value()));
		}
		throw new TransformationException("Unsupported expression type found. (" + exp.getClass().toString() + " --- " + value.getClass().toString() + ")");
	}

	protected void readSolutionTerms(MSystemState state) {
		Evaluator eval = new Evaluator();
		Map<ClassifyingTerm, Value> solutionMap = new HashMap<>();
		int i = 1;
		List<String> solutionPrints = new ArrayList<String>(classifyingTerms.size());
		for (ClassifyingTerm ct : classifyingTerms) {
			Value val = eval.eval(ct.oclExpr, state);
			solutionMap.put(ct, val);
			solutionPrints.add("Term " + i++ + ": " + val);
		}
		LOG.info("Term values for this solution: " + StringUtil.fmtSeq(solutionPrints, "; "));
		termSolutions.add(solutionMap);
	}
	
	public void addClassifyingTerm(Expression term, Node termKodkod){
		classifyingTerms.add(new ClassifyingTerm(term, termKodkod));
	}
	
	public int classifyingTermCount(){
		return classifyingTerms.size();
	}

	//FIXME
	public void exploreMore() {
		Map<ClassifyingTerm, Value> ctValues = termSolutions.get(solutionIndex);
		// force ct values in solution
		Formula theValues = encodeClassifyingValues(ctValues);
		
		boolean useCT = true;
		UseScrollingKodkodModelValidator useScrollingKodkodModelValidator;
		if(useCT){
			useScrollingKodkodModelValidator = new UseCTScrollingKodkodModelValidator(session, this);
		} else {
			useScrollingKodkodModelValidator = new UseScrollingKodkodModelValidator(session);
		}
	}
	
}
