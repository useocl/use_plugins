package org.tzi.kodkod.model.type;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Represents a set type.
 * 
 * @author Hendrik Reitmann
 */
public class SetType extends Type {

	private Type elemType;

	SetType(Type elemType) {
		this.elemType = elemType;
	}

	public Type elemType() {
		return elemType;
	}

	@Override
	public Expression expression() {
		return elemType.expression();
	}

	@Override
	protected Relation createRelation() {
		return elemType.relation();
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return elemType.lowerBound(tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return elemType.upperBound(tupleFactory);
	}

	@Override
	public boolean isSet() {
		return true;
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public boolean isIntegerCollection() {
		return elemType.isInteger();
	}
}
