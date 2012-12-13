package org.tzi.kodkod.model.config.impl;

import java.util.Collection;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.iface.IClass;

/**
 * Configurator for classes.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ClassConfigurator extends Configurator<IClass> {

	private int min, max;

	@Override
	public TupleSet lowerBound(IClass clazz, int arity, TupleFactory tupleFactory) {
		TupleSet lower = tupleFactory.noneOf(1);
		if (!clazz.isAbstract()) {
			for (String[] specific : specificValues) {
				lower.add(tupleFactory.tuple(clazz.name() + "_" + specific[0]));
			}
			for (int i = specificValues.size(); i < min; i++) {
				lower.add(tupleFactory.tuple(clazz.name() + "_" + clazz.name().toLowerCase() + (i + 1)));
			}
		}

		return lower;
	}

	@Override
	public TupleSet upperBound(IClass clazz, int arity, TupleFactory tupleFactory) {
		TupleSet upper = tupleFactory.noneOf(1);
		if (!clazz.isAbstract()) {
			upper.addAll(lowerBound(clazz, arity, tupleFactory));
			for (int i = specificValues.size(); i < max; i++) {
				upper.add(tupleFactory.tuple(clazz.name() + "_" + clazz.name().toLowerCase() + (i + 1)));
			}
		}
		return upper;
	}

	@Override
	public void setSpecificValues(Collection<String[]> specificValues) {
		super.setSpecificValues(specificValues);
		setLimits(specificValues.size(), specificValues.size());
	}

	/**
	 * Sets the minimum and maximum number of objects for a class.
	 * 
	 * @param min
	 * @param max
	 */
	public void setLimits(int min, int max) {
		if (min >= specificValues.size()) {
			this.min = min;
		}

		if (max >= specificValues.size() && max >= min) {
			this.max = max;
		} else if (max <= min) {
			this.max = min;
		}
	}

	public int getMax() {
		return max;
	}
}
