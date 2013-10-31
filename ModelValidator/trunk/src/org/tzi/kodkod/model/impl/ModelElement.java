package org.tzi.kodkod.model.impl;

import kodkod.ast.Relation;

import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelElement;

/**
 * Abstract implementation of IModelElement.
 * 
 * @author Hendrik Reitmann
 * 
 */
public abstract class ModelElement implements IModelElement {

	private final String name;
	protected Relation relation;
	protected IModel model;

	ModelElement(IModel model, final String name) {
		this.model = model;
		this.name = name;
		resetConfigurator();
	}	
	
	@Override
	public String name() {
		return name;
	}

	@Override
	public Relation relation() {
		return relation;
	}

	@Override
	public IModel model() {
		return model;
	}

}
