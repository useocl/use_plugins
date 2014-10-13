package org.tzi.kodkod.model.iface;

import kodkod.ast.Formula;

/**
 * Instances of the type IInvariant represent invariants in a model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IInvariant extends IElement {

	/**
	 * Returns the name of this invariant.
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Returns the formula for this invariant.
	 * 
	 * @return
	 */
	public Formula formula();

	/**
	 * Sets the formula of this invariant.
	 * 
	 * @param formula
	 */
	public void setFormula(Formula formula);

	/**
	 * Returns the context class of this invariant.
	 * 
	 * @return
	 */
	public IClass clazz();

	/**
	 * Activates the invariant.
	 */
	public void activate();

	/**
	 * Deactivates the invariant.
	 */
	public void deactivate();

	/**
	 * Negates the invariant.
	 */
	public void negate();

	/**
	 * Reverses a possible negation of the invariant.
	 */
	void denegate();
	
	/**
	 * Returns true if the invariant is activated.
	 * 
	 * @return
	 */
	public boolean isActivated();

	/**
	 * Returns true if the invariant is negated.
	 * 
	 * @return
	 */
	public boolean isNegated();

	/**
	 * Resets the invariant to the normal state which means. the invariant is
	 * activated and not negated.
	 */
	public void reset();

}
