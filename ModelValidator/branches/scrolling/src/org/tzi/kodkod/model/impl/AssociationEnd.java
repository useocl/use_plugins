package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IClass;

/**
 * Implementation of IAssociationEnd.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AssociationEnd implements IAssociationEnd {

	private String name;
	private Multiplicity multiplicity;
	private IClass associatedClass;

	AssociationEnd(String name, Multiplicity multiplicity, IClass associatedClass) {
		this.name = name;
		this.multiplicity = multiplicity;
		this.associatedClass = associatedClass;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Multiplicity multiplicity() {
		return multiplicity;
	}

	@Override
	public IClass associatedClass() {
		return associatedClass;
	}

	@Override
	public String toString() {
		return name;
	}
	
}
