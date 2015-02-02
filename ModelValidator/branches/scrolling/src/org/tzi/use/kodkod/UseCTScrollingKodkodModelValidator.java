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

	protected static class ClassifierTerm {
		private final Expression oclExpr;
		private final Node kodkodExpr;
		
		public ClassifierTerm(Expression oclExpr, Node kodkodExpr) {
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

	protected List<ClassifierTerm> classifierTerms = new ArrayList<ClassifierTerm>();
	protected List<Map<ClassifierTerm, Value>> termSolutions = new ArrayList<>();

	public UseCTScrollingKodkodModelValidator(Session session) {
		super(session);
		solutions = new ArrayList<Map<Relation, TupleSet>>();
	}
	
	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			readSolutionTerms(session.system().state());
			
			solutions.add(solution.instance().relationTuples());
			LOG.info(LogMessages.pagingNext);
			previousLog();
		} else {
			session.reset();
			newSolution(solution.instance().relationTuples());
		}
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
	
	@Override
	protected void newSolution(Map<Relation, TupleSet> relationTuples) {
		Formula f = null;
		
		for (Map<ClassifierTerm, Value> solutions : termSolutions) {
			Formula currSolution = null;
			
			for (ClassifierTerm ct : classifierTerms) {
				Value value = solutions.get(ct);
				Formula solFormula = encodeSolutionValue(ct.kodkodExpr, value);
				currSolution = (currSolution == null) ? solFormula : currSolution.and( solFormula ) ;
			}
			
			f = (f == null) ? currSolution.not() : f.and(currSolution.not()) ;
		}
		
		((ModelConfigurator) model.getConfigurator()).setSolutionFormula(f);
		validate(model);
	}

	protected void readSolutionTerms(MSystemState state) {
		Evaluator eval = new Evaluator();
		Map<ClassifierTerm, Value> solutionMap = new HashMap<>();
		int i = 1;
		List<String> solutionPrints = new ArrayList<String>(classifierTerms.size());
		for (ClassifierTerm ct : classifierTerms) {
			Value val = eval.eval(ct.oclExpr, state);
			solutionMap.put(ct, val);
			solutionPrints.add("Term " + i++ + ": " + val);
		}
		LOG.info("Term values for this solution: " + StringUtil.fmtSeq(solutionPrints, "; "));
		termSolutions.add(solutionMap);
	}
	
	public void addObservationTerm(Expression term, Node termKodkod){
		classifierTerms.add(new ClassifierTerm(term, termKodkod));
	}
	
}
