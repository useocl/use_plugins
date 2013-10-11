package org.tzi.kodkod.model.iface;

import org.tzi.kodkod.model.impl.Multiplicity;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeFactory;

/**
 * An Instance of the type IModelFactory represents a factory to create a model
 * with the elements.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface IModelFactory {

	/**
	 * Creates an instance of IModel.
	 * 
	 * @param name
	 * @param modelFactory
	 * @param typeFactory
	 * @return
	 */
	public IModel createModel(String name, IModelFactory modelFactory, TypeFactory typeFactory);

	/**
	 * Creates an instance of IClass.
	 * 
	 * @param model
	 * @param name
	 * @param abstractC
	 * @return
	 */
	public IClass createClass(IModel model, String name, boolean abstractC);

	/**
	 * Creates an instance of IAttribute
	 * 
	 * @param model
	 * @param name
	 * @param type
	 * @param kClass
	 * @return
	 */
	public IAttribute createAttribute(IModel model, String name, Type type, IClass kClass);

	/**
	 * Creates an instance of IAssociation.
	 * 
	 * @param model
	 * @param name
	 * @return
	 */
	public IAssociation createAssociation(IModel model, String name);

	/**
	 * Creates an instance of IAssociationEnd.
	 * 
	 * @param name
	 * @param multiplicity
	 * @param associatedClass
	 * @return
	 */
	public IAssociationEnd createAssociationEnd(String name, Multiplicity multiplicity, IClass associatedClass);

	/**
	 * Creates an instance of IAssociationClass.
	 * 
	 * @param model
	 * @param name
	 * @return
	 */
	public IAssociationClass createAssociationClass(IModel model, String name);

	/**
	 * Creates an instance of IInvariant.
	 * 
	 * @param name
	 * @param clazz
	 * @return
	 */
	public IInvariant createInvariant(String name, IClass clazz);
}
