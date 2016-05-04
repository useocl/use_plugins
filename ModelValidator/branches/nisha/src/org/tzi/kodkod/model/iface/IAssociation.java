package org.tzi.kodkod.model.iface;

import java.util.List;

import org.tzi.kodkod.model.config.IConfigurator;

/**
 * Instances of the type IAssocation represent associations in a model.
 * 
 * @author Hendrik Reitmann
 */
public interface IAssociation extends IModelElement {

	/**
	 * Adds an association end to the end of the association end list.
	 */
	public void addAssociationEnd(IAssociationEnd associationEnd);

	/**
	 * Returns all association ends of the association.
	 */
	public List<IAssociationEnd> associationEnds();

	/**
	 * Sets an association class
	 */
	public void setAssociationClass(IAssociationClass associationClass);

	/**
	 * Returns the association class, null if no association class is set.
	 */
	public IAssociationClass associationClass();

	/**
	 * @return returns whether this association is an association class or not
	 */
	public boolean isAssociationClass();
	
	/**
	 * @return Returns true if this is a binary association.
	 */
	public boolean isBinaryAssociation();

	/**
	 * Sets the configurator.
	 */
	public void setConfigurator(IConfigurator<IAssociation> configurator);

	/**
	 * Returns the configurator.
	 */
	public IConfigurator<IAssociation> getConfigurator();
}
