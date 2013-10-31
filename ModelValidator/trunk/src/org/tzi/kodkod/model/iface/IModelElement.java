package org.tzi.kodkod.model.iface;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Instances of the type IModelElement represent a model element (Class,
 * Association, Attribute) in a model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IModelElement extends IConfigurableElement {

	/**
	 * Returns the name of this element.
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Returns the relation for this element.
	 * 
	 * @return
	 */
	public Relation relation();

	/**
	 * Returns the lower bound for the relation.
	 * 
	 * @param tupleFactory
	 * @return
	 */
	public TupleSet lowerBound(TupleFactory tupleFactory);

	/**
	 * Returns the upper bound for the relation.
	 * 
	 * @param tupleFactory
	 * @return
	 */
	public TupleSet upperBound(TupleFactory tupleFactory);

	/**
	 * Returns the model in which the element is contained
	 * 
	 * @return
	 */
	public IModel model();

	/**
	 * Returns the formula for this element.
	 * 
	 * @return
	 */
	public Formula constraints();
}
