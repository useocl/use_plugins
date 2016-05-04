package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;

import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * 
 * Contains all transformation methods for classes
 * 
 * @author Hendrik
 */
public class ClassOperationGroup extends OCLOperationGroup {

	public ClassOperationGroup(TypeFactory typeFactory) {
		super(typeFactory);
	}

	@Override
	public boolean returnsSet(String opName) {
		if (opName.equals("allInstances")) {
			return true;
		}
		return super.returnsSet(opName);
	}

	public Expression allInstances(Expression cls) {
		return cls;
	}
}
