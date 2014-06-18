package org.tzi.kodkod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.api.UseApiException;
import org.tzi.use.kodkod.solution.ObjectDiagramCreator;
import org.tzi.use.uml.sys.MSystem;

/**
 * Checks the invariant independence.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class InvariantIndepChecker extends KodkodModelValidator {

	private static final Logger LOG = Logger.getLogger(InvariantIndepChecker.class);

	private MSystem system;
	
	private Map<Logger, Level> logLevels;
	private List<IInvariant> inactiveInvariants;
	private List<IInvariant> negatedInvariants;
	private IInvariant currentInvariant;

	private boolean validateSingleInvariant = false;
	
	public InvariantIndepChecker(MSystem system) {
		this.system = system;
	}
	
	/**
	 * Validation to check the independence of a single invariant.
	 * 
	 * @param model
	 * @param className
	 * @param invariantName
	 */
	public void validate(IModel model, String className, String invariantName) {
		validateSingleInvariant = true;
		changeLogLevels();

		Collection<IInvariant> invariants = model.classInvariants();
		activateAllInvariants(invariants);

		IClass clazz = model.getClass(className);
		if (clazz == null) {
			LOG.error(LogMessages.noClassError(className));
			return;
		}
		currentInvariant = clazz.getInvariant(invariantName);
		if (currentInvariant == null) {
			LOG.error(LogMessages.noClassInvariantError(className, invariantName));
			return;
		}

		checkInvariant(model);

		resetInvariantStates(invariants);
		resetLogLevels();
	}

	@Override
	public void validate(IModel model) {
		validateSingleInvariant = false;
		Logger.getLogger(KodkodModelValidator.class).setLevel(Level.WARN);
		Logger.getLogger(KodkodSolver.class).setLevel(Level.WARN);
		LOG.setLevel(Level.INFO);

		Collection<IInvariant> invariants = model.classInvariants();
		activateAllInvariants(invariants);

		for (IInvariant invariant : invariants) {
			currentInvariant = invariant;
			checkInvariant(model);
		}

		resetInvariantStates(invariants);
		Logger.getRootLogger().setLevel(Level.INFO);
	}

	private void checkInvariant(IModel model) {
		currentInvariant.negate();
		super.validate(model);
		currentInvariant.reset();
	}

	private void activateAllInvariants(Collection<IInvariant> invariants) {
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

	private void resetInvariantStates(Collection<IInvariant> invariants) {
		for (IInvariant inv : negatedInvariants) {
			inv.negate();
		}
		for (IInvariant inv : inactiveInvariants) {
			inv.deactivate();
		}
	}

	private void changeLogLevels() {
		Logger mv = Logger.getLogger(KodkodModelValidator.class);
		Logger ks = Logger.getLogger(KodkodSolver.class);

		logLevels = new HashMap<Logger, Level>();
		logLevels.put(mv, mv.getEffectiveLevel());
		logLevels.put(ks, ks.getEffectiveLevel());
		logLevels.put(LOG, LOG.getEffectiveLevel());

		LOG.setLevel(Level.INFO);
	}

	private void resetLogLevels() {
		for (Logger log : logLevels.keySet()) {
			log.setLevel(logLevels.get(log));
		}
	}

	private void createObjectDiagram(Map<Relation, TupleSet> relationTuples){
		LOG.info(LogMessages.objDiagramCreation);

		system.reset();
		ObjectDiagramCreator diagramCreator = new ObjectDiagramCreator(model, system);
		try {
			diagramCreator.create(relationTuples);
			diagramCreator.hasDiagramErrors();
		} catch (UseApiException ex) {
			if (LOG.isDebugEnabled()) {
				LOG.error(LogMessages.objDiagramCreationError, ex);
			} else {
				LOG.error(LogMessages.objDiagramCreationError + " Reason: " + ex.getMessage());
			}
		}
	}
	
	@Override
	protected void satisfiable() {
		LOG.info(currentInvariant.name() + ": Independent");
		if(validateSingleInvariant){
			createObjectDiagram(solution.instance().relationTuples());
		}
	}

	@Override
	protected void trivially_satisfiable() {
		LOG.info(currentInvariant.name() + ": Independent");
		if(validateSingleInvariant){
			createObjectDiagram(solution.instance().relationTuples());
		}
	}

	@Override
	protected void trivially_unsatisfiable() {
		LOG.info(currentInvariant.name() + ": Dependent");
	}

	@Override
	protected void unsatisfiable() {
		LOG.info(currentInvariant.name() + ": Not independent for given properties");
	}
}
