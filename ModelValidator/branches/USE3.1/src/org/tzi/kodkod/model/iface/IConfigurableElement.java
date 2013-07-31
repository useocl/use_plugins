package org.tzi.kodkod.model.iface;

/**
 * Instances of the type IConfigurableElement represent configurable model
 * elements (not types).
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IConfigurableElement extends IElement {

	/**
	 * Resets the configurator of the element to the default one.
	 */
	public void resetConfigurator();
}
