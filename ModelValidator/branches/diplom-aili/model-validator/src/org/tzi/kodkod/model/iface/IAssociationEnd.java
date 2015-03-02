package org.tzi.kodkod.model.iface;

import org.tzi.kodkod.model.impl.Multiplicity;

/**
 * Instances of the type IAssociationEnd represent association ends of an
 * association.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IAssociationEnd {

	public final int REGULAR = 0;
	public final int AGGREGATION = 1;
	public final int COMPOSITION = 2;
	/**
	 * Returns the name of this association end.
	 */
	public String name();

	/**
	 * Returns the multiplicity of this association end.
	 */
	public Multiplicity multiplicity();

	/**
	 * Returns the associated class of this association end.
	 */
	public IClass associatedClass();
	
	/**
	 * Returns the kind of this association end.
	 * 
	 * @return One of {@link IAssociationEnd#REGULAR},
	 *         {@link IAssociationEnd#AGGREGATION} and
	 *         {@link IAssociationEnd#COMPOSITION}.
	 */
	public int aggregationKind();
}
