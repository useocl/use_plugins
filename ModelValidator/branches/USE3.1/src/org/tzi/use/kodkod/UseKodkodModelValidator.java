package org.tzi.use.kodkod;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.kodkod.solution.ObjectDiagramCreator;
import org.tzi.use.uml.sys.MSystem;

/**
 * Class to for a simple model validation with subsequent object diagram
 * creation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseKodkodModelValidator extends KodkodModelValidator {

	protected static final Logger LOG = Logger.getLogger(UseKodkodModelValidator.class);

	private MSystem mSystem;

	public UseKodkodModelValidator(MSystem mSystem) {
		this.mSystem = mSystem;
	}

	@Override
	protected void satisfiable() {
		handleSolution();
	}
	@Override
	protected void trivially_satisfiable() {
		handleSolution();
	}

	/**
	 * Handles a satisfiable solution.
	 */
	protected void handleSolution() {
		createObjectDiagram(solution.instance().relationTuples());
		evaluateInactiveInvariants();
	}

	@Override
	protected void trivially_unsatisfiable() {
	}

	@Override
	protected void unsatisfiable() {
	}

	/**
	 * Starts the object diagram creation.
	 * 
	 * @param relationTuples
	 */
	protected boolean createObjectDiagram(Map<Relation, TupleSet> relationTuples) {
		LOG.info(LogMessages.objDiagramCreation);

		mSystem.reset();
		ObjectDiagramCreator diagramCreator = new ObjectDiagramCreator(model, mSystem);
		try {
			diagramCreator.create(relationTuples);
			return checkForDiagramErrors(relationTuples);
		} catch (Exception exception) {
			if (LOG.isDebugEnabled()) {
				LOG.error(LogMessages.objDiagramCreationError, exception);
			} else {
				LOG.error(LogMessages.objDiagramCreationError);
			}
			return true;
		}
	}

	protected boolean checkForDiagramErrors(Map<Relation, TupleSet> relationTuples) {
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		boolean foundErrors = !mSystem.state().checkStructure(out);
		if (foundErrors) {
			String result = buffer.toString();

			boolean aggregationcyclefreeness = DefaultConfigurationValues.aggregationcyclefreeness;
			if (model.getConfigurator() instanceof ModelConfigurator) {
				aggregationcyclefreeness = ((ModelConfigurator) model.getConfigurator()).isAggregationCycleFree();
			}

			if (aggregationcyclefreeness) {
				if (result.contains("cycle")) {
					mSystem.reset();
					ModelConfigurator modelConfigurator = (ModelConfigurator) model.getConfigurator();
					modelConfigurator.forbid(relationTuples);
					validate(model);
					
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Evaluation of the inactive invariants.
	 */
	private void evaluateInactiveInvariants() {
		boolean info = false;
		for (IClass clazz : model.classes()) {
			for (IInvariant invariant : clazz.invariants()) {
				if (!invariant.isActivated()) {
					if (!info) {
						LOG.debug(LogMessages.inactiveInvariantEval);
						info = true;
					}

					LOG.debug("Invariant " + invariant.name() + ": " + evaluator.evaluate(invariant.formula()));
				}
			}
		}
	}
}
