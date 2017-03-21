package org.tzi.kodkod.model.iface;

import org.tzi.use.uml.ocl.expr.Expression;

public interface Derivable {

	public void setDerivedExpression(Expression derivedExpression);
	public Expression derivedExpression();
	
}
