package org.tzi.kodkod.model.impl;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.config.impl.AttributeConfigurator;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.visitor.Visitor;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Implementation of IAttribute.
 * 
 * @author Hendrik Reitmann
 */
public class Attribute extends ModelElement implements IAttribute {

	private static final Logger LOG = Logger.getLogger(Attribute.class);

	protected Type type;
	protected IClass owner;
	protected IConfigurator<IAttribute> configurator;

	Attribute(IModel model, String name, Type type, IClass owner) {
		super(model, name);
		this.type = type;
		this.owner = owner;

		relation = Relation.binary(owner.name() + "_" + name);
	}
	
	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return configurator.lowerBound(this, 2, tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return configurator.upperBound(this, 2, tupleFactory);
	}

	@Override
	public Formula constraints() {
		Formula formula = Formula.and(domainDefinition(), typeDefinition(), multiplicityDefinition());
		return formula.and(configurator.constraints(this));
	}

	/**
	 * Creates the formula for the domain definition.
	 */
	private Formula domainDefinition() {
		Formula formula = relation.join(Expression.UNIV).in(getOwnerRelation());

		LOG.debug("Domain of " + name() + ": " + PrintHelper.prettyKodkod(formula));
		return formula;
	}

	private Formula typeDefinition() {
		Relation undefined = model.typeFactory().undefinedType().relation();
		Relation undefinedSet = model.typeFactory().undefinedSetType().relation();

		Formula formula;
		if (type.isSet()) {
			formula = Expression.UNIV.join(relation).in(type.expression().union(undefined).union(undefinedSet));
		} else {
			formula = Expression.UNIV.join(relation).in(type.expression().union(undefined));
		}

		LOG.debug("Type of " + name() + ": " + PrintHelper.prettyKodkod(formula));
		return formula;
	}

	/**
	 * Creates the formula for the multiplicity definition of an attribute.
	 */
	private Formula multiplicityDefinition() {
		final Variable c = Variable.unary("c");
		Relation ownerRelation = getOwnerRelation();

		Formula formula;
		if (type.isSet()) {
			Relation undefinedSet = model.typeFactory().undefinedSetType().relation();
			formula = undefinedSet.in(c.join(relation)).implies(c.join(relation).one()).forAll(c.oneOf(ownerRelation));
		} else {
			formula = c.join(relation).one().forAll(c.oneOf(ownerRelation));
		}

		LOG.debug("Mult for " + name() + ": " + PrintHelper.prettyKodkod(formula));
		return formula;
	}
	
	/**
	 * Returns the relation of the owner.
	 */
	private Relation getOwnerRelation() {
		if (owner.existsInheritance()) {
			return owner.inheritanceRelation();
		}
		return owner.relation();
	}

	@Override
	public IClass owner() {
		return owner;
	}

	@Override
	public Type type() {
		return type;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitAttribute(this);
	}

	@Override
	public void setConfigurator(IConfigurator<IAttribute> configurator) {
		this.configurator = configurator;
	}

	@Override
	public IConfigurator<IAttribute> getConfigurator() {
		return configurator;
	}

	@Override
	public void resetConfigurator() {
		configurator = new AttributeConfigurator(this);
	}
}
