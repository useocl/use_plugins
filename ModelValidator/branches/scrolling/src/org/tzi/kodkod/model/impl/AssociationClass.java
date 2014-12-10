package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;

/**
 * Implementation of IAssociationClass.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AssociationClass extends Class implements IAssociationClass{

	AssociationClass(IModel model, String name) {
		super(model, name, false);
	}

	@Override
	public Multiplicity multiplicity() {
		return null;
	}

	@Override
	public IClass associatedClass() {
		return this;
	}

}
