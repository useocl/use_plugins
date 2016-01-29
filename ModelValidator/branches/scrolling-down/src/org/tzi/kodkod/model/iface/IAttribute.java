package org.tzi.kodkod.model.iface;

import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.type.Type;

/**
 * Instances of the type IAttribute represent attributes of a class.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IAttribute extends IModelElement {

	/**
	 * Returns the class of this attribute.
	 * 
	 * @return
	 */
	public IClass owner();

	/**
	 * Returns the type of this attribute.
	 * 
	 * @return
	 */
	public Type type();

	/**
	 * Sets the configurator for this attribute.
	 * 
	 * @param configurator
	 */
	public void setConfigurator(IConfigurator<IAttribute> configurator);

	/**
	 * Returns the configurator of this attribute.
	 * 
	 * @return
	 */
	public IConfigurator<IAttribute> getConfigurator();
}
