package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.Derivable;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.Type;

import kodkod.ast.Formula;

/**
 * Implementation for derived attributes.
 * 
 * @author Frank Hilken
 */
public class DerivedAttribute extends Attribute implements Derivable {

	private org.tzi.use.uml.ocl.expr.Expression derivedExpression = null;
	
	DerivedAttribute(IModel model, String name, Type type, IClass owner) {
		super(model, name, type, owner);
	}
	
	@Override
	public void setDerivedExpression(org.tzi.use.uml.ocl.expr.Expression derivedExpr) {
		this.derivedExpression = derivedExpr;
	}
	
	@Override
	public org.tzi.use.uml.ocl.expr.Expression derivedExpression() {
		return derivedExpression;
	}
	
	@Override
	public Formula constraints() {
		//TODO domain check?
		// Class.allInstances()->forAll( c | thisType.allInstances()->including(null)->includes( expr(c) ))
		return Formula.TRUE;
	}
		
}
