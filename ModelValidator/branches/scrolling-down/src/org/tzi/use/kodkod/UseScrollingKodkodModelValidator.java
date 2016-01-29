package org.tzi.use.kodkod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.main.Session;

/**
 * Class for the model validation with scrolling functionality.
 * 
 * @author Hendrik Reitmann
 */
public class UseScrollingKodkodModelValidator extends UseKodkodModelValidator {

	protected int solutionIndex = 0;
	protected List<Map<Relation, TupleSet>> solutions;

	public UseScrollingKodkodModelValidator(Session session) {
		super(session);
		solutions = new ArrayList<Map<Relation, TupleSet>>();
	}
	
	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			solutions.add(solution.instance().relationTuples());
			LOG.info(LogMessages.pagingNext);
			previousLog();
		} else {
			session.reset();
			newSolution(solution.instance().relationTuples());
		}
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

	protected void previousLog() {
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
}
