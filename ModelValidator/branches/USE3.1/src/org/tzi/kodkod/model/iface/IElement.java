package org.tzi.kodkod.model.iface;

import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Instances of the type IElement represent an element in the model
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IElement {

	/**
	 * Process this element with a visitor.
	 * 
	 * @param visitor
	 */
	public void accept(Visitor visitor);
}
