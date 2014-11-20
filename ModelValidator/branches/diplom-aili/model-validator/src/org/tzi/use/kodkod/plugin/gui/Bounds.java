package org.tzi.use.kodkod.plugin.gui;

public class Bounds {
	
	private int lower = -1;
	private int upper = -1;
	
	/**
	 * Constructs a new instance using the 
	 * default values -1 and -1 for lower and upper bound.
	 */
	public Bounds() {
		super();
	}
	
	/**
	 * @param lower
	 * @param upper
	 */
	public Bounds(int lower, int upper) {
		super();
		this.lower = lower;
		this.upper = upper;
	}
	/**
	 * @return the lower
	 */
	public int getLower() {
		return lower;
	}
	/**
	 * @param lower the lower to set
	 */
	public void setLower(int lower) {
		this.lower = lower;
	}
	/**
	 * @return the upper
	 */
	public int getUpper() {
		return upper;
	}
	/**
	 * @param upper the upper to set
	 */
	public void setUpper(int upper) {
		this.upper = upper;
	}
}
