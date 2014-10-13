package org.tzi.kodkod.model.type;

import java.util.Map;

import org.tzi.kodkod.model.visitor.Visitor;

import kodkod.ast.Expression;

/**
 * Abstract base class for all types which can have literals.
 * 
 * @author Hendrik Reitmann
 */
public abstract class TypeLiterals extends TypeAtoms {

	protected Map<String, Expression> typeLiterals;

	TypeLiterals(String name) {
		super(name);
	}

	/**
	 * Returns a map with all literal expressions.
	 * 
	 * @return
	 */
	public Map<String, Expression> typeLiterals() {
		if (typeLiterals == null) {
			createTypeLiterals();
		}
		return typeLiterals;
	}

	/**
	 * Returns the expression of the literal with the given name, null if the
	 * name does not exist.
	 * 
	 * @param literal
	 * @return
	 */
	public Expression getTypeLiteral(String literal) {
		String literalName = name() + "_" + literal;
		return typeLiterals.get(literalName);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitTypeLiterals(this);
	}

	public abstract void addTypeLiteral(String literal);

	protected abstract void createTypeLiterals();
}
