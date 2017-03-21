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
	 * @return Returns true if there exists a generalization.
	 */
	public boolean existsInheritance();

	/**
	 * @return Returns the relation to represent the generalization.
	 */
	public Relation inheritanceRelation();

	/**
	 * @return Returns the inheritance relation if one exists otherwise the
	 *         regular relation.
	 */
	public Relation inheritanceOrRegularRelation();
	
	/**
	 * Returns the lower bound for the generalization relation.
	 */
	public TupleSet inheritanceLowerBound(TupleFactory tupleFactory);

	/**
	 * Returns the upper bound for the generalization relation.
	 */
	public TupleSet inheritanceUpperBound(TupleFactory tupleFactory);
}
