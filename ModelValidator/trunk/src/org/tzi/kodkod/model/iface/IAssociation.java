package org.tzi.kodkod.model.iface;

import java.util.List;

import org.tzi.kodkod.model.config.IConfigurator;

/**
 * Instances of the type IAssocation represent associations in a model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IAssociation extends IModelElement {

	/**
	 * Adds an association end to the end of the association end list.
	 * 
	 * @param associationEnd
	 */
	public void addAssociationEnd(IAssociationEnd associationEnd);

	/**
	 * Returns all association ends of the association.
	 * 
	 * @return
	 */
	public List<IAssociationEnd> associationEnds();

	/**
	 * Sets an assocation class
	 * 
	 * @param associationClass
	 */
	public void setAssociationClass(IAssociationClass associationClass);

	/**
	 * Returns the association class, null if no association class is set.
	 * 
	 * @return
	 */
	public IAssociationClass associationClass();

	/**
	 * Returns true if this is a binary association.
	 * 
	 * @return
	 */
	public boolean isBinaryAssociation();

	/**
	 * Sets the configurator.
	 * 
	 * @param configurator
	 */
	public void setConfigurator(IConfigurator<IAssociation> configurator);

	/**
	 * Returns the configurator.
	 * 
	 * @return
	 */
	public IConfigurator<IAssociation> getConfigurator();
}
