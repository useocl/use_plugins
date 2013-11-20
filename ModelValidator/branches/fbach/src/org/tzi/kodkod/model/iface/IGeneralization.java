package org.tzi.kodkod.model.iface;

import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Instances of the type IGeneralization represent a type with generalization.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IGeneralization {

	/**
	 * Returns true if there exists a generalization.
	 * 
	 * @return
	 */
	public boolean existsInheritance();

	/**
	 * Returns the relation to represent the generalization.
	 * 
	 * @return
	 */
	public Relation inheritanceRelation();

	/**
	 * Returns the lower bound for the generalization relation.
	 * 
	 * @param tupleFactory
	 * @return
	 */
	public TupleSet inheritanceLowerBound(TupleFactory tupleFactory);

	/**
	 * Returns the upper bound for the generalization relation.
	 * 
	 * @param tupleFactory
	 * @return
	 */
	public TupleSet inheritanceUpperBound(TupleFactory tupleFactory);
}
