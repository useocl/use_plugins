package org.tzi.use.kodkod.transform.ocl;

import java.util.LinkedList;
import java.util.List;

import org.tzi.use.uml.ocl.expr.ExpStdOp;

public class UnrelatedTypeCheckVisitor extends ExpressionTraversalVisitor {

	private final List<String> errors = new LinkedList<String>();
	
	public List<String> getErrors() {
		return errors;
	}
	
	@Override
	public void visitStdOp(ExpStdOp exp) {
		super.visitStdOp(exp);
		
		String warning = exp.getOperation().checkWarningUnrelatedTypes(exp.args());
		if(warning != null){
			errors.add(warning);
		}
	}
	
}
