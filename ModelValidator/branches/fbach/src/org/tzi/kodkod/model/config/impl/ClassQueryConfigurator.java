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
			for (int i = specificValues.size(); i < min; i++) {
				lower.add(tupleFactory.tuple(clazz.name() + "_" + clazz.name().toLowerCase() + (i + 1)));
				clazz.objectType().addTypeLiteral(clazz.name().toLowerCase() + (i + 1));
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
				clazz.objectType().addTypeLiteral(clazz.name().toLowerCase() + (i + 1));
			}
		}
		return upper;
	}
	
}
