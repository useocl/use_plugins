package org.tzi.use.kodkod;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.helper.ProofHelper;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.use.api.UseApiException;
import org.tzi.use.kodkod.solution.ObjectDiagramCreator;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.sys.MSystem;

/**
 * Class for a simple model validation with subsequent object diagram
 * creation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseKodkodModelValidator extends KodkodModelValidator {

	protected static final Logger LOG = Logger.getLogger(UseKodkodModelValidator.class);

	protected MSystem mSystem;

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
		if(createObjectDiagram(solution.instance().relationTuples())){
			LOG.info("USE found errors in the solution. Try to find a new solution!");
			
			mSystem.reset();
			newSolution(solution.instance().relationTuples());
		}
		evaluateInactiveInvariants();
	}

	@Override
	protected void trivially_unsatisfiable() {
		handleFailure();
	}

	@Override
	protected void unsatisfiable() {
		handleFailure();
	}

	private void handleFailure() {
		LOG.info(ProofHelper.buildProofString(solution.proof(), true));
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
		} catch (UseApiException ex) {
			if (LOG.isDebugEnabled()) {
				LOG.error(LogMessages.objDiagramCreationError, ex);
			} else {
				LOG.error(LogMessages.objDiagramCreationError + " Reason: " + ex.getMessage());
			}
			return false;
		}
	}

	protected boolean checkForDiagramErrors(Map<Relation, TupleSet> relationTuples) {
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		boolean foundErrors = !mSystem.state().checkStructure(out);
		if (foundErrors) {
			String result = buffer.toString();

			boolean aggregationcyclefreeness = DefaultConfigurationValues.aggregationcyclefreeness;
			boolean forbiddensharing = DefaultConfigurationValues.forbiddensharing;
			if (model.getConfigurator() instanceof ModelConfigurator) {
				aggregationcyclefreeness = ((ModelConfigurator) model.getConfigurator()).isAggregationCycleFree();
				forbiddensharing = ((ModelConfigurator) model.getConfigurator()).isForbiddensharing();
			}

			if (aggregationcyclefreeness) {
				if (result.contains("cycle")) {
					return true;
				}
			}
			if(forbiddensharing){
				if (result.contains("shared")) {
					return true;
				}
			}
		}

		// check invariants for correctness
		MModel gModel = mSystem.generator().gModel();
		Evaluator evaluator = new Evaluator();
		for (IInvariant invariant : model.classInvariants()) {
			if (invariant.isActivated()) {
				BooleanValue result = (BooleanValue) evaluator.eval(gModel.getClassInvariant(invariant.name()).expandedExpression(),
						mSystem.state());
				if ((invariant.isNegated() && result.isTrue()) || (!invariant.isNegated() && result.isFalse())) {
					LOG.info(LogMessages.unexpectedInvariantResult(invariant));
					//return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Starts a model validation to find a new solution.
	 */
	protected void newSolution(Map<Relation, TupleSet> relationTuples) {
		ModelConfigurator modelConfigurator = (ModelConfigurator) model.getConfigurator();
		modelConfigurator.forbid(relationTuples);
		validate(model);
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
