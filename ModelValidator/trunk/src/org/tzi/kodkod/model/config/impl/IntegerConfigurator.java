package org.tzi.kodkod.model.config.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.impl.Range;
import org.tzi.kodkod.model.type.ConfigurableType;

/**
 * Configurator for the integer type.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class IntegerConfigurator extends TypeConfigurator {

	@Override
	public TupleSet lowerBound(ConfigurableType type, int arity, TupleFactory tupleFactory) {
		TupleSet lower = tupleFactory.noneOf(1);

		for(Object atom : type.atoms()){
			lower.add(tupleFactory.tuple(atom));
		}
		
		return lower;
	}

	@Override
	public TupleSet upperBound(ConfigurableType type, int arity, TupleFactory tupleFactory) {
		return lowerBound(type, arity, tupleFactory);
	}

	@Override
	public Set<Object> atoms(ConfigurableType m, List<Object> literals) {
		Set<Object> atoms = new LinkedHashSet<Object>();
		atoms.addAll(literals);

		for (Range range : ranges) {
			for (int i = range.getLower(); i <= range.getUpper(); i++) {
				atoms.add(Integer.valueOf(i));
			}
		}

		for (String[] specific : allValues()) {
			atoms.add(Integer.valueOf(specific[0]));
		}

		return new LinkedHashSet<Object>(atoms);
	}
}
