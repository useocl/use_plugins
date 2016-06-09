package org.tzi.kodkod.model.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.type.IntegerType;
import org.tzi.kodkod.model.type.SetType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeAtoms;
import org.tzi.kodkod.model.type.TypeConstants;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Configurator for attributes.
 * 
 * @author Hendrik Reitmann
 * @author Frank Hilken
 */
public class AttributeConfigurator extends Configurator<IAttribute> {

	private static final Logger LOG = Logger.getLogger(AttributeConfigurator.class);

	private Set<String> domainValues;
	private IAttribute attribute;
	private TupleSet specified;
	private boolean allValuesDefined = false;
	private boolean unboundedDefinedValues = true;
	private boolean unboundedCollectionSize = true;
	private int minCollectionSize = 1;
	private int maxCollectionSize = -1;
	private int definedObjects = 0;
	private int min, max;

	public AttributeConfigurator(IAttribute attribute) {
		super();
		this.attribute = attribute;
		domainValues = new HashSet<String>();
	}

	@Override
	public TupleSet lowerBound(IAttribute attribute, int arity, TupleFactory tupleFactory) {
		specified = tupleFactory.noneOf(1);
		TupleSet lower = tupleFactory.noneOf(2);

		Type type = attribute.type();
		String object;
		for (String[] specific : specificValues) {
			object = specific[0];
			for (int i = 1; i < specific.length; i++) {
				if ((type.isInteger() || type.isIntegerCollection())
						&& !(specific[i].equals(TypeConstants.UNDEFINED) || specific[i].equals(TypeConstants.UNDEFINED_SET))) {
					lower.add(tupleFactory.tuple(object, Integer.valueOf(specific[i])));
				} else {
					if (type.isCollection()) {
						type = ((SetType) type).elemType();
					}

					lower.add(tupleFactory.tuple(object, getValue(type, specific[i])));
				}
			}
			specified.add(tupleFactory.tuple(object));
		}

		return lower;
	}

	protected String getValue(Type type, String value) {
		if (type instanceof TypeAtoms && !type.isBoolean()) {
			if (!(value.equals(TypeConstants.UNDEFINED) || value.equals(TypeConstants.UNDEFINED_SET))) {
				value = ((TypeAtoms) type).name() + "_" + value;
			}
		}
		return value;
	}

	@Override
	public TupleSet upperBound(IAttribute attribute, int arity, TupleFactory tupleFactory) {
		Type type = attribute.type();

		IClass owner = attribute.owner();
		TupleSet remaining;
		if (!owner.existsInheritance()) {
			remaining = owner.upperBound(tupleFactory).clone();
		} else {
			remaining = owner.inheritanceUpperBound(tupleFactory);
		}
		remaining.removeAll(specified);

		TupleSet upper = tupleFactory.noneOf(2);
		if (domainValues.isEmpty()) {
			TupleSet typeUpperUndefined = tupleFactory.noneOf(1);
			typeUpperUndefined.addAll(type.upperBound(tupleFactory));
			typeUpperUndefined.add(tupleFactory.tuple(TypeConstants.UNDEFINED));
			if (type.isSet()) {
				typeUpperUndefined.add(tupleFactory.tuple(TypeConstants.UNDEFINED_SET));
			}

			if(attribute.type().isInteger()){
				IntegerType t = (IntegerType) attribute.type();
				
				final int lowerBound;
				final int upperBound;
				final boolean useMinMaxBounds;
				if(!t.getConfigurator().getRanges().isEmpty()){
					lowerBound = t.getConfigurator().getRanges().get(0).getLower();
					upperBound = t.getConfigurator().getRanges().get(0).getUpper();
					useMinMaxBounds = true;
				} else {
					lowerBound = Integer.MIN_VALUE;
					upperBound = Integer.MIN_VALUE;
					useMinMaxBounds = false;
				}
				final Collection<Integer> sValues = Collections2.transform(t.getConfigurator().getSpecificValues(), new Function<String[], Integer>() {
					@Override
					public Integer apply(String[] input) {
						return Integer.valueOf(input[0]);
					}
				});
				// filter values that are not defined in properties file
				// e.g. literals from invariants
				TupleSet rawInput = typeUpperUndefined;
				typeUpperUndefined = tupleFactory.noneOf(1);
				typeUpperUndefined.addAll(Collections2.filter(rawInput, new Predicate<Tuple>() {
					@Override
					public boolean apply(Tuple input) {
						if(input.atom(0) instanceof Integer){
							int n = (Integer) input.atom(0);
							return (useMinMaxBounds && n >= lowerBound && n <= upperBound) || sValues.contains(n);
						}
						return true;
					}
				}));
			}
			
			TupleSet remaining_objectsUpper = remaining.product(typeUpperUndefined);
			upper = remaining_objectsUpper;
		} else {
			TupleSet domain = tupleFactory.noneOf(1);
			for (String domainValue : domainValues) {
				if (type.isInteger() || type.isIntegerCollection()) {
					domain.add(tupleFactory.tuple(Integer.valueOf(domainValue)));
				} else {
					if (type.isCollection()) {
						type = ((SetType) type).elemType();
					}

					domain.add(tupleFactory.tuple(getValue(type, domainValue)));
				}
			}
			upper = remaining.product(domain);
		}

		upper.addAll(lowerBound(attribute, arity, tupleFactory));

		return upper;
	}

	@Override
	public Formula constraints(IAttribute attribute) {
		Formula formula = numberOfDefinedValues(attribute);
		if (attribute.type().isCollection()) {
			formula = formula.and(numberOfCollectionValues(attribute));
		}
		return formula;
	}

	@Override
	public void setSpecificValues(Collection<String[]> specificValues) {
		this.specificValues = new LinkedHashSet<String[]>(specificValues);
		countDefinedObjects();
		setLimits(definedObjects, definedObjects);
	}

	private void countDefinedObjects() {
		Set<String> objects = new HashSet<String>();
		for (String[] specificValue : this.specificValues) {
			if (attribute.type().isCollection()) {
				if (!(specificValue[1].equals(TypeConstants.UNDEFINED_SET))) {
					objects.add(specificValue[0]);
				}
			} else {
				if (!(specificValue[1].equals(TypeConstants.UNDEFINED))) {
					objects.add(specificValue[0]);
				}
			}
		}

		definedObjects = objects.size();
	}

	/**
	 * Sets the number of defined values for an attribute.
	 * 
	 * @param min
	 *            minimum number
	 * @param max
	 *            maximum number
	 */
	public void setLimits(int min, int max) {
		if (min >= definedObjects) {
			this.min = min;
		}

		if (max >= definedObjects && max >= min) {
			this.max = max;
		} else if (max <= min) {
			if (max == -1) {
				this.max = max;
			} else {
				this.max = min;
			}
		}

		unboundedDefinedValues = (max != -1);
		allValuesDefined = (min == -1);
	}

	/**
	 * Sets the minimum and maximum size for a collection.
	 * 
	 * @param min
	 * @param max
	 */
	public void setCollectionSize(int min, int max) {
		if (min >= 0) {
			minCollectionSize = min;
		} else {
			minCollectionSize = 0;
		}

		if (max != -1) {
			unboundedCollectionSize = false;
			if (max >= min) {
				maxCollectionSize = max;
			} else {
				maxCollectionSize = min;
			}
		} else {
			unboundedCollectionSize = true;
		}
	}

	/**
	 * Sets the domain values for an attribute.
	 * 
	 * @param domainValues
	 */
	public void setDomainValues(Collection<String> domainValues) {
		this.domainValues = new HashSet<String>(domainValues);
	}

	/**
	 * Creates the formula for the number of defined attribute values.
	 * 
	 * @param attribute
	 * @return
	 */
	public Formula numberOfDefinedValues(IAttribute attribute) {
		Relation relation = attribute.relation();
		Relation undefined = attribute.model().typeFactory().undefinedType().relation();
		Relation undefinedSet = attribute.model().typeFactory().undefinedSetType().relation();

		Formula formula = null;
		IntExpression numberOfDefinedObjects;
		if (attribute.type().isSet()) {
			numberOfDefinedObjects = attribute.owner().relation().count().minus(relation.join(undefinedSet).count());
		} else {
			numberOfDefinedObjects = relation.join(Expression.UNIV.difference(undefined)).count();
		}
		
		Formula minFormula = Formula.TRUE;
		if (allValuesDefined) {
			if (attribute.type().isSet()) {
				minFormula = undefinedSet.in(Expression.UNIV.join(relation)).not();
			} else {
				minFormula = undefined.in(Expression.UNIV.join(relation)).not();
			}
		} else if (min != 0) {
			if (min == max) {
				formula = numberOfDefinedObjects.eq(IntConstant.constant(min));
			}
			minFormula = numberOfDefinedObjects.gte(IntConstant.constant(min));
		}

		Formula maxFormula = Formula.TRUE;
		if (!unboundedDefinedValues && !allValuesDefined) {
			maxFormula = numberOfDefinedObjects.lte(IntConstant.constant(max));
		}

		if (formula == null) {
			formula = minFormula.and(maxFormula);
		}

		LOG.debug("Quantity of " + attribute.name() + ": " + PrintHelper.prettyKodkod(formula));
		return formula;
	}

	/**
	 * Creates the formulation for the collection size.
	 */
	private Formula numberOfCollectionValues(IAttribute attribute) {
		List<Formula> constraints = new ArrayList<Formula>();
		Variable variable = Variable.unary("setAttrMult");

		IntExpression numberOfValues = variable.join(attribute.relation()).count();
		if(minCollectionSize > 0){
			constraints.add(numberOfValues.gte(IntConstant.constant(minCollectionSize)));
		}
		if (!unboundedCollectionSize) {
			constraints.add(numberOfValues.lte(IntConstant.constant(maxCollectionSize)));
		}

		return Formula.and(constraints).forAll(variable.oneOf(attribute.owner().relation()));
	}
}
