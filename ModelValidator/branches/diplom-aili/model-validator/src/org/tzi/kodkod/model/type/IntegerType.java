package org.tzi.kodkod.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kodkod.ast.Expression;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;

import org.tzi.kodkod.model.config.impl.IntegerConfigurator;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Represents the integer type of the model.
 * 
 * @author Hendrik Reitmann
 */
public class IntegerType extends ConfigurableType {

	private List<Object> literalValues;

	IntegerType() {
		super(TypeConstants.INTEGER);
		literalValues = new ArrayList<Object>();
	}

	@Override
	public Expression expression() {
		return Expression.INTS;
	}

	/**
	 * Returns null because Integer is directly available through
	 * Expression.INTS.
	 */
	@Override
	protected Relation createRelation() {
		return null;
	}

	@Override
	public boolean isInteger() {
		return true;
	}

	@Override
	public void addTypeLiteral(String literal) {
		Integer value = Integer.valueOf(literal);

		String literalName = name() + "_" + literal;
		if (!typeLiterals().containsKey(literalName)) {
			typeLiterals().put(literalName, IntConstant.constant(value).toExpression());
			literalValues.add(value);
		}
	}

	@Override
	protected void createTypeLiterals() {
		typeLiterals = new HashMap<String, Expression>();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitIntegerType(this);
	}

	public List<Object> toStringAtoms() {
		List<Object> toStringAtoms = new ArrayList<Object>();
		for (Object atom : atoms()) {
			toStringAtoms.add("String_" + atom);
		}
		return toStringAtoms;
	}

	@Override
	protected List<Object> createAtomList() {
		return configurator.atoms(this, literalValues);
	}

	@Override
	public void resetConfigurator() {
		configurator = new IntegerConfigurator();
	}
}
