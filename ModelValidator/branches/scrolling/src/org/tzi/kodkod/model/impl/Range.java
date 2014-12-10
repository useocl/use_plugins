package org.tzi.kodkod.model.impl;

/**
 * Represents a single multiplicity range.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class Range {

	private int lower;
	private int upper;

	public Range(int lower, int upper) {
		this.lower = lower;
		this.upper = upper;
	}

	/**
	 * Returns the lower bound.
	 * 
	 * @return
	 */
	public int getLower() {
		return lower;
	}

	/**
	 * Returns the upper bound.
	 * 
	 * @return
	 */
	public int getUpper() {
		return upper;
	}

}
