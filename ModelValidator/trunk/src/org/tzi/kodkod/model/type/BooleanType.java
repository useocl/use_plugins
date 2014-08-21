package org.tzi.kodkod.model.type;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.visitor.Visitor;

/**
 * The boolean type of the model.
 * 
 * @author Hendrik Reitmann
 */
public class BooleanType extends TypeLiterals {

	BooleanType() {
		super(TypeConstants.BOOLEAN);
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		final TupleSet booleanLower = tupleFactory.noneOf(1);
		booleanLower.add(tupleFactory.tuple(TypeConstants.TRUE));
		booleanLower.add(tupleFactory.tuple(TypeConstants.FALSE));
		return booleanLower;
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return lowerBound(tupleFactory);
	}

	@Override
	public boolean isBoolean() {
		return true;
	}

	@Override
	protected Set<Object> createAtomList() {
		Set<Object> atoms = new LinkedHashSet<Object>();
		atoms.add(TypeConstants.TRUE);
		atoms.add(TypeConstants.FALSE);
		return atoms;
	}

	@Override
	public void addTypeLiteral(String literal) {
		// Nothing to do here
	}

	@Override
	protected void createTypeLiterals() {
		typeLiterals = new HashMap<String, Expression>();
		typeLiterals.put(TypeConstants.BOOLEAN_TRUE, Relation.unary(TypeConstants.BOOLEAN_TRUE));
		typeLiterals.put(TypeConstants.BOOLEAN_FALSE, Relation.unary(TypeConstants.BOOLEAN_FALSE));
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitBooleanType(this);
	}
}
