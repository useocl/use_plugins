package org.tzi.use.kodkod;

import org.tzi.use.main.Session;

public class UseScrollingAllKodkodModelValidator extends UseScrollingKodkodModelValidator {

	public UseScrollingAllKodkodModelValidator(Session session) {
		super(session);
	}

	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			solutions.add(solution.instance().relationTuples());
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
