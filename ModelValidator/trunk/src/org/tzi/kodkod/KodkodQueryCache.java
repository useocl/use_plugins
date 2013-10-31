package org.tzi.kodkod;

import kodkod.engine.Evaluator;

/**
 * Cache to store the Kodkod-Evaluator for a solution. The evaluator can be used
 * for queries.
 * 
 * @author Hendrik Reitmann
 * 
 */

public enum KodkodQueryCache {

	INSTANCE;

	private Evaluator evaluator;
	private boolean queryEnabled = false;

	private KodkodQueryCache() {
	}

	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}

	public Evaluator getEvaluator() throws NullPointerException {
		if (evaluator == null) {
			throw new NullPointerException("No solution and therefore no evaluator available!");
		}

		return evaluator;
	}

	public void setQueryEnabled(boolean queryEnabled) {
		this.queryEnabled = queryEnabled;
	}

	public boolean isQueryEnabled() {
		return queryEnabled;
	}
}
