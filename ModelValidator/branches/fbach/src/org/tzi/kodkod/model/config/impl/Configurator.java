package org.tzi.kodkod.model.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.iface.IElement;
import org.tzi.kodkod.model.impl.Range;

/**
 * Default configurator.
 * 
 * @author Hendrik Reitmann
 * 
 * @param <M>
 */
public class Configurator<M extends IElement> implements IConfigurator<M> {

	protected List<Range> ranges;
	protected Set<String[]> specificValues;

	public Configurator() {
		ranges = new ArrayList<Range>();
		ranges.add(new Range(0, 0));
		specificValues = new HashSet<String[]>();
	}

	@Override
	public TupleSet lowerBound(M m, int arity, TupleFactory tupleFactory) {
		return tupleFactory.noneOf(arity);
	}

	@Override
	public TupleSet upperBound(M m, int arity, TupleFactory tupleFactory) {
		return tupleFactory.noneOf(arity);
	}

	@Override
	public Formula constraints(M m) {
		return Formula.TRUE;
	}

	@Override
	public void setRanges(List<Range> ranges) throws Exception {
		if (ranges.size() == 0) {
			throw new IllegalArgumentException("At least one range has to be defined!");
		}
		this.ranges = ranges;
	}

	@Override
	public void setSpecificValues(Collection<String[]> specificValues) {
		this.specificValues = new HashSet<String[]>(specificValues);
	}

	@Override
	public void addSpecificValue(String[] specificValue) {
		this.specificValues.add(specificValue);
	}
}
