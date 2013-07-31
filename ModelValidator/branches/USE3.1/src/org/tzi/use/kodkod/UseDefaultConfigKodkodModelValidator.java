package org.tzi.use.kodkod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.tzi.kodkod.helper.InvariantHelper;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.uml.sys.MSystem;

/**
 * Class for a model validation with the default search space.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseDefaultConfigKodkodModelValidator extends UseKodkodModelValidator {

	private File configFile;
	private boolean allInactive = false;
	private List<IInvariant> allInvariants;

	public UseDefaultConfigKodkodModelValidator(MSystem mSystem, File configFile) {
		super(mSystem);
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
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(configFile, true));

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
		} catch (Exception e) {
			LOG.error(LogMessages.propertiesConfigurationWriteError);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					LOG.error(LogMessages.propertiesConfigurationCloseError + ". " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Deactivation of all invariants.
	 */
	private void deactivateAllInvariants() {
		if (!allInactive) {
			allInvariants = InvariantHelper.getAllInvariants(model);
			for (IInvariant invariant : allInvariants) {
				invariant.deactivate();
			}
			allInactive = true;
			super.validate(model);
		}
	}
}
