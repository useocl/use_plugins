package org.tzi.kodkod;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.InvariantHelper;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;

/**
 * Checks the invariant independence.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class InvariantIndepChecker extends KodkodModelValidator {

	private static final Logger LOG = Logger.getLogger(InvariantIndepChecker.class);

	private List<IInvariant> inactiveInvariants;
	private List<IInvariant> negatedInvariants;
	private IInvariant currentInvariant;

	public InvariantIndepChecker(PrintWriter out) {
		super(out);
	}
	
	/**
	 * Validation to check the independence of a single invariant.
	 * 
	 * @param model
	 * @param className
	 * @param invariantName
	 */
	public void validate(IModel model, String className, String invariantName, PrintWriter out) {

		List<IInvariant> invariants = InvariantHelper.getAllInvariants(model);
		activateAllInvariants(invariants);

		IClass clazz = model.getClass(className);
		if (clazz == null) {
			out.println(LogMessages.noClassError(className));
			return;
		}
		currentInvariant = clazz.getInvariant(invariantName);
		if (currentInvariant == null) {
			out.println(LogMessages.noClassInvariantError(className, invariantName));
			return;
		}

		checkInvariant(model);

		resetInvariantStates(invariants);
	}

	@Override
	public void validate(IModel model) {
		List<IInvariant> invariants = InvariantHelper.getAllInvariants(model);
		activateAllInvariants(invariants);

		for (IInvariant invariant : invariants) {
			currentInvariant = invariant;
			checkInvariant(model);
		}

		resetInvariantStates(invariants);
	}

	private void checkInvariant(IModel model) {
		currentInvariant.negate();
		super.validate(model);
		currentInvariant.reset();
	}

	private void activateAllInvariants(List<IInvariant> invariants) {
		inactiveInvariants = new ArrayList<IInvariant>();
		negatedInvariants = new ArrayList<IInvariant>();

		for (IInvariant inv : invariants) {
			if (!inv.isActivated()) {
				inactiveInvariants.add(inv);
			} else if (inv.isNegated()) {
				negatedInvariants.add(inv);
			}

			inv.reset();
		}
	}

	private void resetInvariantStates(List<IInvariant> invariants) {
		for (IInvariant inv : negatedInvariants) {
			inv.negate();
		}
		for (IInvariant inv : inactiveInvariants) {
			inv.deactivate();
		}
	}

	@Override
	protected void satisfiable() {
		out.println(currentInvariant.name() + ": " + solution.outcome());
	}

	@Override
	protected void trivially_satisfiable() {
		out.println(currentInvariant.name() + ": " + solution.outcome());
	}

	@Override
	protected void trivially_unsatisfiable() {
		out.println(currentInvariant.name() + ": " + solution.outcome());
	}

	@Override
	protected void unsatisfiable() {
		out.println(currentInvariant.name() + ": " + solution.outcome());
	}
}
