package org.tzi.kodkod.ocl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kodkod.ast.Expression;
import kodkod.ast.Variable;

import org.tzi.use.kodkod.transform.TransformationException;

/**
 * Invoker of the transformation method.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class OCLMethodInvoker {

	private boolean set;
	private Object object;

	/**
	 * Search the operation method using the OCLOperationLoader and calls the
	 * transformation method.
	 * 
	 * @param opName
	 * @param arguments
	 * @param setOperation
	 * @param object_type_nav
	 */
	public void invoke(String opName, List<Object> arguments, boolean setOperation, boolean object_type_nav) {
		OCLOperationLoader operationLoader = new OCLOperationLoader();
		Method method = operationLoader.getOperationMethod(opName, arguments, setOperation);

		if (method == null) {
			arguments.add(new Boolean(object_type_nav));
			method = operationLoader.getOperationMethod(opName, arguments, setOperation);

			if (method == null) {
				throw new TransformationException("OCL operation " + opName + " is not supported.");
			}
		}

		try {
			if (operationLoader.needVariableArray()) {
				replaceSublistWithVariableArray(arguments, operationLoader.getFirstArrayIndex());
			} else if (operationLoader.needExpressionArray()) {
				replaceSublistWithExpressionArray(arguments, operationLoader.getFirstArrayIndex());
			}

			object = method.invoke(operationLoader.getOperationClass(), arguments.toArray());
			this.set = operationLoader.returnsSet();

		} catch (Exception e) {
			throw new TransformationException("Error while invoking method for operation " + opName + ".", e);
		}
	}

	/**
	 * Replaces a part of the arguments with an array of variables.
	 * 
	 * @param arguments
	 * @param fromIndex
	 */
	private void replaceSublistWithVariableArray(List<Object> arguments, int fromIndex) {
		List<Variable> variables = new ArrayList<Variable>();
		for (Object object : arguments.subList(fromIndex, arguments.size())) {
			if (object instanceof Variable) {
				variables.add((Variable) object);
			}
		}
		arguments.removeAll(variables);
		arguments.add(variables.toArray(new Variable[variables.size()]));
	}

	/**
	 * Replaces a part of the arguments with an array of expression.
	 * 
	 * @param arguments
	 * @param fromIndex
	 */
	private void replaceSublistWithExpressionArray(List<Object> arguments, int fromIndex) {
		List<Expression> expressions = new ArrayList<Expression>();
		for (Object object : arguments.subList(fromIndex, arguments.size())) {
			if (object instanceof Expression) {
				expressions.add((Expression) object);
			}
		}
		arguments.removeAll(expressions);
		arguments.add(expressions.toArray(new Expression[expressions.size()]));
	}

	/**
	 * Returns the resulting object.
	 * 
	 * @return
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Returns true if the result object represents a set.
	 * 
	 * @return
	 */
	public boolean isSet() {
		return set;
	}
}
