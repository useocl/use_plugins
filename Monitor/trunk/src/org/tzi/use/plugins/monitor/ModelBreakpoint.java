package org.tzi.use.plugins.monitor;

import org.tzi.use.uml.mm.MModelElement;

/**
 * An abstract breakpoint at the model level.
 * Can be used to suspend the runnuing system if a model breakpoint is hit.
 * Possible location are:
 * <ul>
 * 	<li>Operation call</li>
 *  <li>Operation exit</li>
 *  <li>Attribute assignment</li>
 *  <li>Link change (insert/delete)</li>
 * </ul>
 * 
 * @author Lars Hamann
 */
public abstract class ModelBreakpoint {
	
	private MModelElement modelElement;

	public ModelBreakpoint(MModelElement element) {
		this.modelElement = element;
	}

	/**
	 * The {@link MModelElement} this breakpoint is related to.
	 * @return the modelElement
	 */
	public MModelElement getModelElement() {
		return modelElement;
	}
}
