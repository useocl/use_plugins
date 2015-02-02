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
		private final Expression expr;
		private final Node exprKodkod;
		
		public ClassifierTerm(Expression expr, Node exprKodkod) {
			this.expr = expr;
			this.exprKodkod = exprKodkod;
		}

		public Expression expression() {
			return expr;
		}

		public Node expressionKodkod() {
			return exprKodkod;
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

	private Formula encodeSolutionValue(kodkod.ast.Node exp, Value value) {
		Formula currF;
		if(exp instanceof kodkod.ast.Expression){
			if(value instanceof IntegerValue){
				currF = ((kodkod.ast.Expression) exp).eq(IntConstant.constant(((IntegerValue) value).value()).toExpression());
			}
			else if(value instanceof BooleanValue) {
				Map<String, kodkod.ast.Expression> typeLiterals = model.typeFactory().booleanType().typeLiterals();
				if(((BooleanValue) value).value()){
					currF = ((kodkod.ast.Expression) exp).eq(typeLiterals.get(TypeConstants.BOOLEAN_TRUE));
				} else {
					currF = ((kodkod.ast.Expression) exp).eq(typeLiterals.get(TypeConstants.BOOLEAN_FALSE));
				}
			}
			else {
				throw new RuntimeException("Unsupported expression type found. (" + exp.getClass().toString() + " --- " + value.getClass().toString() + ")");
			}
		}
		else if(exp instanceof Formula){
			currF = ((BooleanValue) value).value() ? (Formula) exp : ((Formula) exp).not() ;
		}
		else if(exp instanceof IntExpression){
			currF = ((IntExpression) exp).eq(IntConstant.constant(((IntegerValue) value).value()));
		}
		else {
			throw new RuntimeException("Unsupported expression type found. (" + exp.getClass().toString() + " --- " + value.getClass().toString() + ")");
		}
		
		return currF;
	}
	
	@Override
	protected void newSolution(Map<Relation, TupleSet> relationTuples) {
		Formula f = null;
		
		for (Map<ClassifierTerm, Value> solutions : termSolutions) {
			Formula currSolution = null;
			
			for (ClassifierTerm ct : classifierTerms) {
				Value value = solutions.get(ct);
				Formula solFormula = encodeSolutionValue(ct.exprKodkod, value);
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
			Value val = eval.eval(ct.expr, state);
			solutionMap.put(ct, val);
			solutionPrints.add("Term " + i++ + ": " + val);
		}
		LOG.info("Term values for this solution: " + StringUtil.fmtSeq(solutionPrints, "; "));
		termSolutions.add(solutionMap);
	}
	
	public void addObservationTerm(Expression term, Node termKodkod){
		ClassifierTerm ct = new ClassifierTerm(term, termKodkod);
		classifierTerms.add(ct);
	}
	
}
