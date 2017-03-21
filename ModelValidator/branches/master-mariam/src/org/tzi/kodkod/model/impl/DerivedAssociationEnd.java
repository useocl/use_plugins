package org.tzi.kodkod.model.impl;

import java.util.Collections;
import java.util.List;

import org.tzi.kodkod.model.iface.Derivable;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.use.uml.ocl.expr.Expression;

/**
 * @author Frank Hilken
 */
public class DerivedAssociationEnd extends AssociationEnd implements Derivable {

	public static class Parameter {
		private final String name;
		private final IClass cls;
		
		public Parameter(String name, IClass cls) {
			this.name = name;
			this.cls = cls;
		}
		
		public String getName() {
			return name;
		}
		
		public IClass getCls() {
			return cls;
		}
	}
	
	protected Expression derivedExpression;
	protected List<Parameter> derivedParameters;
	
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
	
	public void setDerivedParameters(List<Parameter> derivedParameters) {
		this.derivedParameters = derivedParameters;
	}
	
	public List<Parameter> derivedParameters(){
		return Collections.unmodifiableList(derivedParameters);
	}

}
