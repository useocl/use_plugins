package org.tzi.kodkod.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kodkod.ast.Expression;
import kodkod.ast.Relation;

import org.tzi.kodkod.model.config.impl.RealConfigurator;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Represents the real type of the model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class RealType extends ConfigurableType {

	RealType() {
		super(TypeConstants.REAL);
	}

	@Override
	public void addTypeLiteral(String literal) {
		String literalName = name() + "_" + literal;

		if (!typeLiterals().containsKey(literalName)) {
			Relation literalRelation = Relation.unary(literalName);
			typeLiterals().put(literalName, literalRelation);
		}
	}

	@Override
	protected void createTypeLiterals() {
		typeLiterals = new HashMap<String, Expression>();
	}

	@Override
	protected List<Object> createAtomList() {
		return configurator.atoms(this, new ArrayList<Object>());
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public void resetConfigurator() {
		configurator = new RealConfigurator();
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitRealType(this);
	}
}
