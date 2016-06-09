package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.IModel;

import kodkod.ast.Formula;

public class UnionAssociation extends Association {

	UnionAssociation(IModel model, String name) {
		super(model, name);
	}
	
	@Override
	public Formula constraints() {
		// TODO Auto-generated method stub
		return super.constraints();
	}
	
}
