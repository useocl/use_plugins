package org.tzi.kodkod.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.config.impl.ClassConfigurator;
import org.tzi.kodkod.model.config.impl.Configurator;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.ObjectType;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Implementation of IClass.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class Class extends ModelElement implements IClass {

	private static final Logger LOG = Logger.getLogger(Class.class);

	private boolean abstractC;
	private Map<String, IAttribute> attributes;
	private Map<String, IInvariant> invariants;
	private Map<String, IClass> parents;
	private Map<String, IClass> children;
	private Relation inheritanceRelation;
	private ObjectType objectType;
	private Configurator<IClass> configurator;

	Class(IModel model, String name, boolean abstractC) {
		super(model, name);
		this.abstractC = abstractC;

		attributes = new TreeMap<String, IAttribute>();
		invariants = new TreeMap<String, IInvariant>();
		parents = new TreeMap<String, IClass>();
		children = new TreeMap<String, IClass>();

		relation = Relation.unary(name());

		objectType = new ObjectType(this);
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return configurator.lowerBound(this, 1, tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return configurator.upperBound(this, 1, tupleFactory);
	}

	@Override
	public void addAttribute(IAttribute attribute) {
		attributes.put(attribute.name(), attribute);
	}

	@Override
	public Collection<IAttribute> attributes() {
		return attributes.values();
	}

	@Override
	public Collection<IAttribute> allAttributes() {
		Set<IAttribute> allAttributes = new HashSet<IAttribute>(attributes.values());
		for (IClass parent : parents.values()) {
			allAttributes.addAll(parent.allAttributes());
		}
		return allAttributes;
	}

	@Override
	public IAttribute getAttribute(String name) {
		IAttribute attribute = attributes.get(name);
		if (attribute == null) {
			for (IClass parent : parents.values()) {
				if (parent.getAttribute(name) != null) {
					attribute = parent.getAttribute(name);
					break;
				}
			}
		}
		return attribute;
	}

	@Override
	public void addInvariant(IInvariant invariant) {
		invariants.put(invariant.name(), invariant);
	}

	@Override
	public Collection<IInvariant> invariants() {
		return invariants.values();
	}

	@Override
	public Collection<IInvariant> allInvariants() {
		Set<IInvariant> allInvariants = new HashSet<IInvariant>(invariants.values());
		for (IClass parent : parents.values()) {
			allInvariants.addAll(parent.allInvariants());
		}
		return allInvariants;
	}

	@Override
	public IInvariant getInvariant(String name) {
		IInvariant invariant = invariants.get(name);
		if (invariant == null) {
			for (IClass parent : parents.values()) {
				if (parent.getInvariant(name) != null) {
					invariant = parent.getInvariant(name);
					break;
				}
			}
		}
		return invariant;
	}

	@Override
	public void addParent(IClass parent) {
		parents.put(parent.name(), parent);
	}

	@Override
	public Collection<IClass> parents() {
		return parents.values();
	}

	@Override
	public void addChild(IClass child) {
		children.put(child.name(), child);
	}

	@Override
	public Collection<IClass> children() {
		return children.values();
	}

	@Override
	public boolean isAbstract() {
		return abstractC;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitClass(this);
	}

	@Override
	public Formula constraints() {
		Formula formula = Formula.TRUE;
		if (existsInheritance()) {
			formula = inheritanceDefinition();
		}
		return formula.and(configurator.constraints(this));
	}

	@Override
	public boolean existsInheritance() {
		return children.size() > 0;
	}

	@Override
	public Relation inheritanceRelation() {
		if (existsInheritance() && inheritanceRelation == null) {
			inheritanceRelation = Relation.unary(name() + "_inh");
		}
		return inheritanceRelation;
	}

	@Override
	public TupleSet inheritanceLowerBound(TupleFactory tupleFactory) {
		TupleSet inheritanceLowerBound = tupleFactory.noneOf(1);
		inheritanceLowerBound.addAll(lowerBound(tupleFactory));
		for (IClass clazz : children.values()) {
			inheritanceLowerBound.addAll(clazz.inheritanceLowerBound(tupleFactory));
		}

		return inheritanceLowerBound;
	}

	@Override
	public TupleSet inheritanceUpperBound(TupleFactory tupleFactory) {
		TupleSet inheritanceUpperBound = tupleFactory.noneOf(1);
		inheritanceUpperBound.addAll(upperBound(tupleFactory));
		for (IClass clazz : children.values()) {
			inheritanceUpperBound.addAll(clazz.inheritanceUpperBound(tupleFactory));
		}

		return inheritanceUpperBound;
	}

	/**
	 * Returns the formula for the generalization constraint.
	 * 
	 * @return
	 */
	private Formula inheritanceDefinition() {
		Expression expression = relation();

		for (IClass clazz : children.values()) {
			if (clazz.existsInheritance()) {
				expression = expression.union(clazz.inheritanceRelation());
			} else {
				expression = expression.union(clazz.relation());
			}
		}

		Formula formula = inheritanceRelation().eq(expression);

		LOG.debug("Inheritance for " + name() + ": " + PrintHelper.prettyKodkod(formula));
		return formula;
	}

	@Override
	public ObjectType objectType() {
		return objectType;
	}

	@Override
	public void setConfigurator(Configurator<IClass> configurator) {
		this.configurator = configurator;
	}

	@Override
	public Configurator<IClass> getConfigurator() {
		return configurator;
	}

	@Override
	public void resetConfigurator() {
		configurator = new ClassConfigurator();
	}
}
