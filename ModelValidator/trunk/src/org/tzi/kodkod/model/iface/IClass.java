package org.tzi.kodkod.model.iface;

import java.util.Collection;

import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.type.ObjectType;

/**
 * Instances of the type IClass represent classes in a model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IClass extends IModelElement, IGeneralization {

	/**
	 * Adds an attribute to this class.
	 * 
	 * @param attribute
	 */
	public void addAttribute(IAttribute attribute);

	/**
	 * Returns the attributes of this class.
	 * 
	 * @return
	 */
	public Collection<IAttribute> attributes();

	/**
	 * Returns a collection with all attributes of this and the parent classes.
	 * 
	 * @return
	 */
	public Collection<IAttribute> allAttributes();

	/**
	 * Returns the attribute with the given name, null if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public IAttribute getAttribute(String name);

	/**
	 * Adds a parent class.
	 */
	public void addParent(IClass parent);

	/**
	 * Returns a collection of all direct parent classes.
	 */
	public Collection<IClass> parents();
	
	/**
	 * Returns a collection of all parent classes.
	 */
	public Collection<IClass> allParents();

	/**
	 * Adds a child for this class.
	 */
	public void addChild(IClass child);

	/**
	 * Returns all direct child classes.
	 */
	public Collection<IClass> children();
	
	/**
	 * Returns all child classes.
	 */
	public Collection<IClass> allChildren();

	/**
	 * Returns ture if this class is abstract.
	 * 
	 * @return
	 */
	public boolean isAbstract();

	/**
	 * Adds an invariant to this class.
	 * 
	 * @param invariant
	 */
	public void addInvariant(IInvariant invariant);

	/**
	 * Returns all invariants of this class.
	 * 
	 * @return
	 */
	public Collection<IInvariant> invariants();

	/**
	 * Returns a collection with all invariants of this and all parent classes.
	 * 
	 * @return
	 */
	public Collection<IInvariant> allInvariants();

	/**
	 * Returns the invariant with the given namen, null if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public IInvariant getInvariant(String name);

	/**
	 * Return the object type of this class.
	 * 
	 * @return
	 */
	public ObjectType objectType();

	/**
	 * Stes the configurator for this class.
	 * 
	 * @param configurator
	 */
	public void setConfigurator(IConfigurator<IClass> configurator);

	/**
	 * Returns the configurator of this class.
	 * 
	 * @return
	 */
	public IConfigurator<IClass> getConfigurator();
}
