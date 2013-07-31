package org.tzi.kodkod;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.engine.Evaluator;
import kodkod.engine.Solution;
import kodkod.engine.Statistics;
import kodkod.engine.fol2sat.TranslationRecord;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IModel;

/**
 * Abstract base class for all validation functionalities.
 * 
 * @author Hendrik Reitmann
 * 
 */
public abstract class KodkodModelValidator {

	private static final Logger LOG = Logger.getLogger(KodkodModelValidator.class);

	protected IModel model;
	protected Solution solution;
	protected Evaluator evaluator;

	protected final PrintWriter out;
	
	public KodkodModelValidator(PrintWriter out) {
		this.out = out;
	}
	
	/**
	 * Validates the given model.
	 * 
	 * @param model
	 */
	public void validate(IModel model) {
		this.model = model;

		KodkodSolver kodkodSolver = new KodkodSolver();
		try {
			solution = kodkodSolver.solve(model, out);
		} catch (Exception e) {
			out.println(LogMessages.validationException);
			if (LOG.isDebugEnabled()) {
				LOG.error(LogMessages.validationException, e);
			}
			return;
		}

		out.println(solution.outcome());

		Statistics statistics = solution.stats();
		out.println(LogMessages.kodkodStatistics(statistics));

		if (solution.proof() != null) {
			solution.proof().minimize(null);
			out.println("Unsatisfiable proof:");
			Iterator<TranslationRecord> iter = solution.proof().core(); 
			while (iter.hasNext()) {
				TranslationRecord rec = iter.next();
				out.println(rec.toString()); 
			}
		}
		
		switch (solution.outcome()) {
		case SATISFIABLE:
			satisfiable(kodkodSolver);
			satisfiable();
			break;
		case TRIVIALLY_SATISFIABLE:
			satisfiable(kodkodSolver);
			trivially_satisfiable();
			break;
		case TRIVIALLY_UNSATISFIABLE:
			trivially_unsatisfiable();
			break;
		case UNSATISFIABLE:
			unsatisfiable();
			break;
		default:
		}

	}

	private void satisfiable(KodkodSolver kodkodSolver) {
		logSolutionTuples();
		evaluator = kodkodSolver.evaluator();
	}

	private void logSolutionTuples() {
		if (LOG.isDebugEnabled()) {
			Map<Relation, TupleSet> relationTuples = solution.instance().relationTuples();
			for (Relation relation : relationTuples.keySet()) {
				LOG.debug(relation.name() + "\n\t" + relationTuples.get(relation));
			}

			LOG.debug("Integer\n\t" + solution.instance().intTuples());
		}
	}

	protected abstract void satisfiable();

	protected abstract void trivially_satisfiable();

	protected abstract void trivially_unsatisfiable();

	protected abstract void unsatisfiable();
}
