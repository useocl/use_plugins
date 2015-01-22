package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

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
	public void setLower(Object lower) {
		if (lower instanceof String) {
			if (ChangeString.isInteger((String)lower)) {
				this.lower = Integer.parseInt((String) lower);
			}
		} else if (lower instanceof Integer) {
			this.lower = (int) lower;
		}
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
	public void setUpper(Object upper) {
		if (upper instanceof String) {
			if (ChangeString.isInteger((String)upper)) {
				this.upper = Integer.parseInt((String) upper);
			}
		} else if (upper instanceof Integer) {
			this.upper = (int) upper;
		}
	}
}
