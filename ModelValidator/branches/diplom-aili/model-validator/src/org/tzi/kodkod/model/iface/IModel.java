package org.tzi.kodkod.model.iface;

import java.util.Collection;

import kodkod.ast.Formula;

import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.type.EnumType;
import org.tzi.kodkod.model.type.TypeFactory;

/**
 * Instances of the type IModel represent a model of the model validator.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IModel extends IConfigurableElement {

	/**
	 * Returns the name of this model.
	 * 
	 * @return
	 */
	public String name();

	/**
	 * Adds an enum type to this model.
	 * 
	 * @param enumType
	 */
	public void addEnumType(EnumType enumType);

	/**
	 * Returns the enum type with the given name, null if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public EnumType getEnumType(String name);

	/**
	 * Adds a class to this model.
	 * 
	 * @param clazz
	 */
	public void addClass(IClass clazz);

	/**
	 * Adds an association to the model.
	 * 
	 * @param association
	 */
	public void addAssociation(IAssociation association);

	/**
	 * Returns a collection with all enum types.
	 * 
	 * @return
	 */
	public Collection<EnumType> enumTypes();

	/**
	 * Returns the class with the given name, null if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public IClass getClass(String name);

	/**
	 * Returns a collection with all classes.
	 * 
	 * @return
	 */
	public Collection<IClass> classes();

	/**
	 * Returns the association with the given name, null if it does not exist.
	 * 
	 * @param name
	 * @return
	 */
	public IAssociation getAssociation(String name);

	/**
	 * Returns a collection with all associations.
	 * 
	 * @return
	 */
	public Collection<IAssociation> associations();

	/**
	 * @return a collection with all class invariants.
	 */
	public Collection<IInvariant> classInvariants();
	
	/**
	 * Returns the model factory to create the elements of this model.
	 * 
	 * @return
	 */
	public IModelFactory modelFactory();

	/**
	 * Returns the type factory to create the types of this model.
	 * 
	 * @return
	 */
	public TypeFactory typeFactory();

	/**
	 * Returns the formula for the model.
	 * 
	 * @return
	 */
	public Formula constraints();

	/**
	 * Resets the model to the back to the transformation state.
	 */
	public void reset();

	/**
	 * Sets the configurator for this model.
	 * 
	 * @param configurator
	 */
	public void setConfigurator(IConfigurator<IModel> configurator);

	/**
	 * Returns the configurator of this model.
	 * 
	 * @return
	 */
	public IConfigurator<IModel> getConfigurator();

	public abstract IInvariant getInvariant(String name);

	public abstract void addInvariant(IInvariant inv);
}
