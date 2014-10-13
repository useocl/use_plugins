package org.tzi.use.kodkod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.main.Session;

/**
 * Class for a model validation with the default search space.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseDefaultConfigKodkodModelValidator extends UseKodkodModelValidator {

	private File configFile;
	private boolean allInactive = false;
	private Collection<IInvariant> allInvariants;

	public UseDefaultConfigKodkodModelValidator(Session session, File configFile) {
		super(session);
		this.configFile = configFile;
	}

	@Override
	protected void satisfiable() {
		if (!allInactive) {
			super.satisfiable();
		} else {
			reactivateSuccessfulInvariants();
			allInactive = false;
			super.validate(model);
		}
	}

	@Override
	protected void trivially_satisfiable() {
		if (!allInactive) {
			super.trivially_satisfiable();
		} else {
			reactivateSuccessfulInvariants();
			allInactive = false;
			super.validate(model);
		}
	}

	@Override
	protected void trivially_unsatisfiable() {
		deactivateAllInvariants();
	}

	@Override
	protected void unsatisfiable() {
		deactivateAllInvariants();
	}

	/**
	 * Reactivates the invariants which are true for the solution.
	 */
	protected void reactivateSuccessfulInvariants() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile, true))) {
			for (IInvariant invariant : allInvariants) {
				if (evaluator.evaluate(invariant.formula())) {
					invariant.activate();
				} else {
					String name = invariant.name().replaceFirst("::", "_");
					writer.append(name + " = inactive");
					writer.newLine();
					writer.newLine();
				}
			}
		} catch (IOException e) {
			LOG.error(LogMessages.propertiesConfigurationWriteError);
		}
	}

	/**
	 * Deactivation of all invariants.
	 */
	private void deactivateAllInvariants() {
		if (!allInactive) {
			allInvariants = model.classInvariants();
			for (IInvariant invariant : allInvariants) {
				invariant.deactivate();
			}
			allInactive = true;
			super.validate(model);
		}
	}
}
