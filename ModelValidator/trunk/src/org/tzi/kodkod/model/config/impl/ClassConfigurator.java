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

	protected int min, max;
	
	private TupleSet generateObjectsTuple(TupleFactory tupleFactory, IClass clazz, int bound) {
		TupleSet boundTupleSet = tupleFactory.noneOf(1);
		int objectsCounter = 0;
		if (!clazz.isAbstract()) {
			for (String[] specific : specificValues) {
				if (objectsCounter >= bound) break;
				boundTupleSet.add(tupleFactory.tuple(clazz.name() + "_" + specific[0]));
				objectsCounter += 1;
			}
			int i = boundTupleSet.size() + 1;
			while (boundTupleSet.size() < bound) {
				boundTupleSet.add(tupleFactory.tuple(clazz.name() + "_" + clazz.name().toLowerCase() + (i)));
				i++;
			}
		}
		return boundTupleSet;
	}

	@Override
	public TupleSet lowerBound(IClass clazz, int arity, TupleFactory tupleFactory) {
		return generateObjectsTuple(tupleFactory, clazz, min);
	}

	@Override
	public TupleSet upperBound(IClass clazz, int arity, TupleFactory tupleFactory) {
		return generateObjectsTuple(tupleFactory, clazz, max);
	}

	@Override
	public void setSpecificValues(Collection<String[]> specificValues) {
		super.setSpecificValues(specificValues);
		setLimits(specificValues.size(), specificValues.size());
	}

	/**
	 * Sets the minimum and maximum number of objects for a class.
	 * If the minimum is bigger than the maximum, then both will be
	 * set to the minimum value.
	 * 
	 * @param min
	 * @param max
	 */
	public void setLimits(int min, int max) {
		if (min > max) {
			this.min = min;
			this.max = min;
		} else {
			this.min = min;
			this.max = max;
		}
			
	}

	public int getMax() {
		return max;
	}
}
