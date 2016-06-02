package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.Derivable;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.uml.ocl.expr.Expression;

/**
 * @author Frank Hilken
 */
public class DerivedAssociationEnd extends AssociationEnd implements Derivable {

	//TODO derive parameter
	protected Expression derivedExpression;
	
	DerivedAssociationEnd(String name, Multiplicity multiplicity, int aggregationKind, IClass associatedClass) {
		super(name, multiplicity, aggregationKind, associatedClass);
	}
	
	@Override
	public void setDerivedExpression(org.tzi.use.uml.ocl.expr.Expression derivedExpr) {
		this.derivedExpression = derivedExpr;
	}
	
	@Override
	public org.tzi.use.uml.ocl.expr.Expression derivedExpression() {
		return derivedExpression;
	}

}
