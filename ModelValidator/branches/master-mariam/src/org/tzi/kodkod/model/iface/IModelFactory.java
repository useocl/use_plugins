package org.tzi.kodkod.model.iface;

import org.tzi.kodkod.model.impl.Multiplicity;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.use.uml.ocl.expr.Expression;

/**
 * An Instance of the type IModelFactory represents a factory to create a model
 * with the elements.
 * 
 * @author Hendrik Reitmann
 */
public interface IModelFactory {

	/**
	 * Creates an instance of IModel.
	 */
	public IModel createModel(String name, IModelFactory modelFactory, TypeFactory typeFactory);

	/**
	 * Creates an instance of IClass.
	 */
	public IClass createClass(IModel model, String name, boolean abstractC);

	/**
	 * Creates an instance of IAttribute
	 */
	public IAttribute createAttribute(IModel model, String name, Type type, IClass kClass);
	
	public IAttribute createDerivedAttribute(IModel model, String name, Type type, IClass kClass, Expression derivedExpression);

	/**
	 * Creates an instance of IAssociation.
	 */
	public IAssociation createAssociation(IModel model, String name);
	
	public IAssociation createDerivedAssociation(IModel model, String name);

	/**
	 * Creates an instance of IAssociationEnd.
	 */
	public IAssociationEnd createAssociationEnd(String name, Multiplicity multiplicity, int aggregationKind, IClass associatedClass);
	
	public IAssociationEnd createDerivedAssociationEnd(String name, Multiplicity multiplicity, int aggregationKind, IClass associatedClass, Expression derivedExpression);

	/**
	 * Creates an instance of IAssociationClass.
	 */
	public IAssociationClass createAssociationClass(IModel model, String name, boolean abstrac);

	/**
	 * Creates an instance of IInvariant.
	 */
	public IInvariant createInvariant(String name, IClass clazz);
}
