package org.tzi.use.kodkod;

import org.tzi.use.uml.sys.MSystem;

public class UseScrollingAllKodkodModelValidator extends UseScrollingKodkodModelValidator {

	public UseScrollingAllKodkodModelValidator(MSystem mSystem) {
		super(mSystem);
	}

	@Override
	protected void handleSolution() {
		boolean errors = createObjectDiagram(solution.instance().relationTuples());
		if (!errors) {
			solutions.add(solution.instance().relationTuples());
		} else {
			mSystem.reset();
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
