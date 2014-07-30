package org.tzi.kodkod.model.config;

import java.util.Collection;
import java.util.List;

import kodkod.ast.Formula;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.iface.IElement;
import org.tzi.kodkod.model.impl.Range;

/**
 * Instances of the type IConfigurator represent configurators for instances of
 * IElement.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IConfigurator<M extends IElement> {

	/**
	 * Returns the lower bound for this element.
	 * 
	 * @param m
	 * @param arity
	 * @param tupleFactory
	 * @return
	 */
	public TupleSet lowerBound(M m, int arity, TupleFactory tupleFactory);

	/**
	 * Returns the upper bound for this element.
	 * 
	 * @param m
	 * @param arity
	 * @param tupleFactory
	 * @return
	 */
	public TupleSet upperBound(M m, int arity, TupleFactory tupleFactory);

	/**
	 * Returns the constraint for this element.
	 * 
	 * @param m
	 * @return
	 */
	public Formula constraints(M m);

	/**
	 * Sets the ranges of existing values of this element.
	 * 
	 * @param ranges
	 * @throws Exception
	 */
	public void setRanges(List<Range> ranges) throws Exception;

	/**
	 * Sets specific values for this element.
	 * <p>
	 * The parameter is a collection of arrays for the definition of predefined
	 * links and association classes in the properties file. The property
	 * <code>Association = Set{(obj1,obj2)}</code> will be represented by one
	 * array with two entries "obj1" and "obj2".
	 * 
	 * @param specificValues
	 */
	public void setSpecificValues(Collection<String[]> specificValues);

	/**
	 * Adds a specific value for this element.
	 * 
	 * @param specificValue
	 */
	public void addSpecificValue(String[] specificValue);
}
