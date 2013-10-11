package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelFactory;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeFactory;

/**
 * Factory to create the elements for the model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class SimpleFactory implements IModelFactory {

	@Override
	public IModel createModel(String name, IModelFactory modelFactory, TypeFactory typeFactory) {
		return new Model(name, modelFactory, typeFactory);
	}

	@Override
	public IClass createClass(IModel model, String name, boolean abstractC) {
		return new Class(model, name, abstractC);
	}

	@Override
	public IAttribute createAttribute(IModel model, String name, Type type, IClass kClass) {
		return new Attribute(model, name, type, kClass);
	}

	@Override
	public IAssociation createAssociation(IModel model, String name) {
		return new Association(model, name);
	}

	@Override
	public IAssociationEnd createAssociationEnd(String name, Multiplicity multiplicity, IClass associatedClass) {
		return new AssociationEnd(name, multiplicity, associatedClass);
	}

	@Override
	public IAssociationClass createAssociationClass(IModel model, String name) {
		return new AssociationClass(model, name);
	}

	@Override
	public IInvariant createInvariant(String name, IClass clazz) {
		return new Invariant(name, clazz);
	}

}
