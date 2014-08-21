package org.tzi.kodkod.model.config.impl;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.iface.IClass;

public class ClassQueryConfigurator extends ClassConfigurator{

	@Override
	public TupleSet lowerBound(IClass clazz, int arity, TupleFactory tupleFactory) {
		TupleSet lower = tupleFactory.noneOf(1);
		if (!clazz.isAbstract()) {
			for (String[] specific : specificValues) {
				lower.add(tupleFactory.tuple(clazz.name() + "_" + specific[0]));
				clazz.objectType().addTypeLiteral(specific[0]);
			}
			int i = specificValues.size();
			while ( lower.size() < min ) {
				if (lower.add(tupleFactory.tuple(clazz.name() + "_" + clazz.name().toLowerCase() + (i + 1)))) {
					clazz.objectType().addTypeLiteral(clazz.name().toLowerCase() + (i + 1));
				}
				i++;
			}
		}

		return lower;
	}
	
	@Override
	public TupleSet upperBound(IClass clazz, int arity, TupleFactory tupleFactory) {
		TupleSet upper = tupleFactory.noneOf(1);
		if (!clazz.isAbstract()) {
			upper.addAll(lowerBound(clazz, arity, tupleFactory));
			int i = specificValues.size();
			while ( upper.size() < max ) {
				if (upper.add(tupleFactory.tuple(clazz.name() + "_" + clazz.name().toLowerCase() + (i + 1)))) {
					clazz.objectType().addTypeLiteral(clazz.name().toLowerCase() + (i + 1));
				}
				i++;
			}
		}
		return upper;
	}
	
}
