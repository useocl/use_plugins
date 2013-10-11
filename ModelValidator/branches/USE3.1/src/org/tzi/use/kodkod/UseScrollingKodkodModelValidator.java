package org.tzi.use.kodkod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.use.uml.sys.MSystem;

/**
 * Class for the model validation with scrolling functionality.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseScrollingKodkodModelValidator extends UseKodkodModelValidator {

	private int solutionIndex = 0;
	private List<Map<Relation, TupleSet>> solutions;

	public UseScrollingKodkodModelValidator(MSystem mSystem) {
		super(mSystem);
		solutions = new ArrayList<Map<Relation, TupleSet>>();
	}

	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			solutions.add(solution.instance().relationTuples());
			LOG.info(LogMessages.pagingNext);
			previousLog();
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

	private void previousLog() {
		if (solutions.size() > 0) {
			LOG.info(LogMessages.pagingPrevious);
		}
	}

	/**
	 * Scrolls to the next solution.
	 */
	public void nextSolution() {
		if (solutionIndex == solutions.size() - 1) {
			newSolution();
		} else {
			createObjectDiagram(solutions.get(solutionIndex));
		}
		solutionIndex++;
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

	/**
	 * Starts a model validation to find a new solution.
	 */
	protected void newSolution() {
		ModelConfigurator modelConfigurator = (ModelConfigurator) model.getConfigurator();
		modelConfigurator.forbid(solutions.get(solutionIndex));
		validate(model);
	}
}
