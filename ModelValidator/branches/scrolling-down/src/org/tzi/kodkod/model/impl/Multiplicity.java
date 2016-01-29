package org.tzi.kodkod.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store the multiplicity ranges of an association end.
 * 
 * @author Hendrik Reitmann
 * 
 */
public final class Multiplicity {

	public static final int MANY = -1;

	public List<Range> ranges;

	public Multiplicity() {
		ranges = new ArrayList<Range>();
	}

	/**
	 * Adds a range to this multiplicity.
	 * 
	 * @param range
	 */
	public void addRange(Range range) {
		if (range.getLower() == 0 && range.getUpper() == MANY) {
			ranges.clear();
		}
		if (!isZeroMany()) {
			ranges.add(range);
		}
	}

	/**
	 * Returns a list with the ranges of this multiplicity.
	 * 
	 * @return
	 */
	public List<Range> getRanges() {
		return ranges;
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		for (Range range : ranges) {
			stringBuffer.append(range.getLower() + ".." + range.getUpper() + ", ");
		}
		return stringBuffer.toString();
	}

	/**
	 * Returns true if this multiplicity has the range 0..*.
	 * 
	 * @return
	 */
	public boolean isZeroMany() {
		if (ranges.size() > 0) {
			Range range = ranges.get(0);
			return ranges.size() == 1 && range.getLower() == 0 && range.getUpper() == MANY;
		}
		return false;
	}

	/**
	 * Returns true if this multiplicity has the range 0..1.
	 * 
	 * @return
	 */
	public boolean isZeroOne() {
		if (ranges.size() > 0) {
			Range range = ranges.get(0);
			return ranges.size() == 1 && range.getLower() == 0 && range.getUpper() == 1;
		}
		return false;
	}

	/**
	 * Return true if this multiplicity represents an object type end.
	 * 
	 * @return
	 */
	public boolean isObjectTypeEnd() {
		boolean equal = false;
		if (ranges.size() > 0) {
			Range range = ranges.get(0);
			equal = ranges.size() == 1 && range.getLower() == 1 && range.getUpper() == 1;
		}
		return equal || isZeroOne();
	}
}
