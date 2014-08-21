package org.tzi.kodkod.model.type;

import java.util.LinkedHashSet;
import java.util.Set;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;


/**
 * Represents the undefined type of the model.
 * 
 * @author Hendrik Reitmann
 */
public class UndefinedType extends TypeAtoms {

	UndefinedType() {
		super(TypeConstants.UNDEFINED);
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		final TupleSet undefined_SetLower = tupleFactory.noneOf(1);
		undefined_SetLower.add(tupleFactory.tuple(name()));
		return undefined_SetLower;
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return lowerBound(tupleFactory);
	}

	@Override
	public boolean isUndefined() {
		return true;
	}

	@Override
	protected Set<Object> createAtomList() {
		Set<Object> atoms=new LinkedHashSet<Object>();
		atoms.add(name());
		return atoms;
	}
}
