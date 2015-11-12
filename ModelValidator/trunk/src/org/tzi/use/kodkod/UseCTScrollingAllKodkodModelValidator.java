package org.tzi.use.kodkod;

import org.tzi.use.main.Session;

/**
 * Class for the model validation with scrolling functionality using classifier terms.
 * 
 * @author Frank Hilken
 */
public class UseCTScrollingAllKodkodModelValidator extends UseCTScrollingKodkodModelValidator {

	public UseCTScrollingAllKodkodModelValidator(Session session) {
		super(session);
	}

	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			readSolutionTerms(session.system().state());
			solutions.add(solution.instance().relationTuples());
			LOG.info("Found solution no. " + solutions.size());
		} else {
			session.reset();
		}
		newSolution(solution.instance().relationTuples());
	}
	
	@Override
	protected void trivially_unsatisfiable() {
		super.trivially_unsatisfiable();
		finished();
	}

	@Override
	protected void unsatisfiable() {
		super.unsatisfiable();
		finished();
	}
	
	private void finished(){
		solutionIndex = solutions.size()-1;
		LOG.info("Found "+solutions.size()+" solutions");
	}
}
