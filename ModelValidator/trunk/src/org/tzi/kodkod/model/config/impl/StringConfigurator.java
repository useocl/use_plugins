package org.tzi.kodkod.model.config.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.type.ConfigurableType;
import org.tzi.kodkod.model.type.TypeConstants;

/**
 * Configurator for the string type
 * 
 * @author Hendrik Reitmann
 * 
 */
public class StringConfigurator extends TypeConfigurator {

	@Override
	public TupleSet lowerBound(ConfigurableType type, int arity, TupleFactory tupleFactory) {
		TupleSet lower = tupleFactory.noneOf(1);

		for (String[] specific : allValues()) {
			lower.add(tupleFactory.tuple(type.name() + "_" + specific[0]));
		}

		if(ranges.size() > 0){
			int max = ranges.get(0).getUpper();
			//FIXME might fail to add a value if the calculated name is already in the specific values
			for (int i = allValues().size(); i < max; i++) {
				lower.add(tupleFactory.tuple(type.name() + "_string" + i));
			}
		}

		return lower;
	}

	@Override
	public TupleSet upperBound(ConfigurableType type, int arity, TupleFactory tupleFactory) {
		TupleSet upper = tupleFactory.noneOf(1);
		upper.addAll(lowerBound(type, arity, tupleFactory));

		if(ranges.size() > 0){
			int max = ranges.get(0).getUpper();
			//FIXME might fail to add a value if the calculated name is already in the specific values
			for (int i = allValues().size(); i < max; i++) {
				upper.add(tupleFactory.tuple(type.name() + "_string" + i));
			}
		}

		return upper;
	}

	@Override
	public List<Object> atoms(ConfigurableType type, List<Object> literals) {
		Set<Object> atoms = new HashSet<Object>();
		atoms.addAll(literals);

		atoms.add(TypeConstants.STRING_TRUE);
		atoms.add(TypeConstants.STRING_FALSE);
		for (String[] specific : allValues()) {
			atoms.add(type.name() + "_" + specific[0]);
		}

		if(ranges.size() > 0){
			int max = ranges.get(0).getUpper();
			for (int i = allValues().size(); i < max; i++) {
				atoms.add(type.name() + "_string" + i);
			}
		}

		return new ArrayList<Object>(atoms);
	}
}
