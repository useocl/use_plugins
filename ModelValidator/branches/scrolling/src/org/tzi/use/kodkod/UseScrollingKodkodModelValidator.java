package org.tzi.use.kodkod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.use.main.Session;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.Value;

/**
 * Class for the model validation with scrolling functionality.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseScrollingKodkodModelValidator extends UseKodkodModelValidator {

	protected int solutionIndex = 0;
	protected List<Map<Relation, TupleSet>> solutions;
	
	protected Expression obsTerm;
	protected kodkod.ast.Expression obsTermKodkod;
	protected boolean obsTermInteger;
	protected Set<Value> termSolutions = new HashSet<Value>();

	public UseScrollingKodkodModelValidator(Session session) {
		super(session);
		solutions = new ArrayList<Map<Relation, TupleSet>>();
	}
	
	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			readSolutionTerm();
			
			solutions.add(solution.instance().relationTuples());
			LOG.info(LogMessages.pagingNext);
			previousLog();
		} else {
			session.reset();
			newSolution(solution.instance().relationTuples());
		}
	}

	protected void readSolutionTerm() {
		Evaluator eval = new Evaluator();
		Value val = eval.eval(obsTerm, session.system().state());
		termSolutions.add(val);
	}

	@Override
	protected void trivially_unsatisfiable() {
		super.trivially_unsatisfiable();
		previousLog();
	}

	@Override
	protected void unsatisfiable() {
		super.unsatisfiable();
		previousLog();
	}

	private void previousLog() {
		if (solutions.size() > 0) {
			LOG.info(LogMessages.pagingPrevious);
		}
	}

	/**
	 * Scrolls to the next solution.
	 */
	public void nextSolution() {
		solutionIndex++;
		if (solutionIndex == solutions.size()) {
			newSolution(solutions.get(solutionIndex-1));
		} else {
			createObjectDiagram(solutions.get(solutionIndex));
		}
	}

	/**
	 * Scrolls to the previous solution.
	 */
	public void previousSolution() {
		if (solutionIndex > 0) {
			solutionIndex--;
			createObjectDiagram(solutions.get(solutionIndex));
		} else {
			LOG.info(LogMessages.pagingFirst);
		}
	}
	
	public void showSolution(int index){
		if(index < 1){
			LOG.info(LogMessages.showSolutionIndexToSmall);
			return;
		}else if(index > solutions.size()){
			LOG.info(LogMessages.showSolutionIndexToBig(solutions.size()));
			return;
		}
		
		LOG.info(LogMessages.showSolution(index));
		solutionIndex = index-1;
		createObjectDiagram(solutions.get(solutionIndex));
	}
	
	/* 
	 * Overwrite Observation Terms.
	 * Comment out to use default scrolling.
	 */
	@Override
	protected void newSolution(Map<Relation, TupleSet> relationTuples) {
		Formula f = Formula.TRUE;
		for(Value v : termSolutions){
			kodkod.ast.Expression resVal;
			if(obsTermInteger){
				resVal = IntConstant.constant(((IntegerValue)v).value()).toExpression();
			} else {
				Set<kodkod.ast.Expression> literals = new HashSet<kodkod.ast.Expression>();
				for(Value v2 : ((CollectionValue)v).collection()){
					literals.add(IntConstant.constant(((IntegerValue)v2).value()).toExpression());
				}
				resVal = kodkod.ast.Expression.union(literals);
			}
			f = f.and(obsTermKodkod.eq(resVal).not());
		}
		((ModelConfigurator)model.getConfigurator()).setSolutionFormula(f);
		validate(model);
	}

	public void setObservationTerm(Expression term) {
		obsTerm = term;
		obsTermInteger = term.type().isInteger();
	}
	
	public void setObservationTermKodkod(kodkod.ast.Expression observationTermKodkod) {
		obsTermKodkod = observationTermKodkod;
	}
	
}
