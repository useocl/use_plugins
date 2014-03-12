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

	/**
	 * Returns the name of this association end.
	 * @return
	 */
	public String name();

	/**
	 * Returns the multiplicity of this association end.
	 * @return
	 */
	public Multiplicity multiplicity();

	/**
	 * Returns the associated class of this association end.
	 * @return
	 */
	public IClass associatedClass();
}
