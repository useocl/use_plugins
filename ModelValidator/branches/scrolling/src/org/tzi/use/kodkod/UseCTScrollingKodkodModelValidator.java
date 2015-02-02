package org.tzi.use.kodkod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		
		@Override
		public int hashCode() {
			return expr.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null){
				return false;
			}
			else if(obj instanceof ClassifierTerm){
				return expr.equals(((ClassifierTerm) obj).expr);
			}
			else {
				return false;
			}
		}
	}

	protected List<ClassifierTerm> classifierTerms = new ArrayList<ClassifierTerm>();
	protected Map<ClassifierTerm, Set<Value>> cTermSolutions = new HashMap<>();

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

	@Override
	protected void newSolution(Map<Relation, TupleSet> relationTuples) {
		Formula f = null;
		
		for (ClassifierTerm ct : classifierTerms) {
			Set<Value> solutions = cTermSolutions.get(ct);
			Formula ctF = null;
			
			for (Value value : solutions) {
				Formula currF;
				if(ct.exprKodkod instanceof kodkod.ast.Expression){
					if(value instanceof IntegerValue){
						currF = ((kodkod.ast.Expression) ct.exprKodkod).eq(IntConstant.constant(((IntegerValue) value).value()).toExpression()).not();
					}
					else if(value instanceof BooleanValue) {
						Map<String, kodkod.ast.Expression> typeLiterals = model.typeFactory().booleanType().typeLiterals();
						if(((BooleanValue) value).value()){
							currF = ((kodkod.ast.Expression) ct.exprKodkod).eq(typeLiterals.get(TypeConstants.BOOLEAN_TRUE)).not();
						} else {
							currF = ((kodkod.ast.Expression) ct.exprKodkod).eq(typeLiterals.get(TypeConstants.BOOLEAN_FALSE)).not();
						}
					}
					else {
						throw new RuntimeException("Unsupported expression type found. (" + ct.exprKodkod.getClass().toString() + " --- " + value.getClass().toString() + ")");
					}
				}
				else if(ct.exprKodkod instanceof Formula){
					currF = ((BooleanValue) value).value() ? ((Formula) ct.exprKodkod).not() : (Formula) ct.exprKodkod;
				}
				else if(ct.exprKodkod instanceof IntExpression){
					currF = ((IntExpression) ct.exprKodkod).eq(IntConstant.constant(((IntegerValue) value).value())).not();
				}
				else {
					throw new RuntimeException("Unsupported expression type found. (" + ct.exprKodkod.getClass().toString() + " --- " + value.getClass().toString() + ")");
				}
				ctF = (ctF == null) ? currF : ctF.and(currF);
			}
			f = (f == null) ? ctF : f.or(ctF);
		}
		
		((ModelConfigurator)model.getConfigurator()).setSolutionFormula(f);
		validate(model);
	}

	protected void readSolutionTerms(MSystemState state) {
		Evaluator eval = new Evaluator();
		for (ClassifierTerm ct : classifierTerms) {
			Value val = eval.eval(ct.expr, state);
			cTermSolutions.get(ct).add(val);
		}
	}
	
	public void addObservationTerm(Expression term, Node termKodkod){
		ClassifierTerm ct = new ClassifierTerm(term, termKodkod);
		classifierTerms.add(ct);
		cTermSolutions.put(ct, new HashSet<Value>());
	}
	
}
