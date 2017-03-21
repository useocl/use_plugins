package org.tzi.kodkod.model.type;

import org.tzi.kodkod.model.visitor.Visitor;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Represents the OclAny-Type.
 * @author Hendrik Reitmann
 *
 */
public class AnyType extends Type {

	AnyType() {
	}
	
	@Override
	public Expression expression() {
		return Expression.UNIV;
	}
	
	/**
	 * Returns null because Any is directly available through
	 * Expression.NONE.
	 */
	@Override
	protected Relation createRelation() {
		return null;
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return tupleFactory.noneOf(1);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return tupleFactory.noneOf(1);
	}

	@Override
	public boolean isAny() {
		return true;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitAnyType(this);
	}
}
