package org.tzi.kodkod.ocl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Variable;

import org.apache.log4j.Logger;

/**
 * Loads the transformation method for an ocl operation using Reflection.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class OCLOperationLoader {

	protected static final Logger LOG = Logger.getLogger(OCLOperationLoader.class);

	private String operatorName;
	private boolean needVariableArray = false;
	private boolean needExpressionArray = false;
	private OCLOperationGroup oclOperationGroup;
	private List<Integer> variableIndexes;
	private List<Integer> expressionIndexes;
	private OCLGroupRegistry registry;

	public OCLOperationLoader() {
		registry = OCLGroupRegistry.INSTANCE;
	}

	/**
	 * Returns the transformation method for the given ocl operation.
	 * 
	 * @param opName
	 * @param arguments
	 * @param setOperation
	 * @return
	 */
	public Method getOperationMethod(String opName, List<Object> arguments, boolean setOperation) {
		operatorName = opName;

		Class<?>[] parameterTypes = extractParameterTypes(arguments);

		if (registry.getSymbolOperationMapping().containsKey(opName)) {
			operatorName = registry.getSymbolOperationMapping().get(opName);
		}

		LOG.debug("Search: " + operatorName + " - set operation: " + setOperation + " - args: " + arguments.size());

		Method method = searchMethod(operatorName, parameterTypes, setOperation);

		if (method == null) {
			method = research(opName, setOperation, parameterTypes);
		}

		return method;
	}

	/**
	 * Extracts the classes of the arguments.
	 * 
	 * @param arguments
	 * @return
	 */
	private Class<?>[] extractParameterTypes(List<Object> arguments) {
		variableIndexes = new ArrayList<Integer>();
		expressionIndexes = new ArrayList<Integer>();
		Class<?>[] parameterTypes = new Class[arguments.size()];

		Object currentArgument;
		for (int i = 0; i < parameterTypes.length; i++) {
			currentArgument = arguments.get(i);

			if (currentArgument instanceof Variable) {
				parameterTypes[i] = Variable.class;
				variableIndexes.add(i);
				 expressionIndexes.add(i);
			} else if (currentArgument instanceof Expression) {
				parameterTypes[i] = Expression.class;
				expressionIndexes.add(i);
			} else if (currentArgument instanceof Formula) {
				parameterTypes[i] = Formula.class;
			} else {
				parameterTypes[i] = currentArgument.getClass();
			}
		}
		return parameterTypes;
	}

	/**
	 * Search the transformation method.
	 * 
	 * @param opName
	 * @param parameterTypes
	 * @param setOperation
	 * @return
	 */
	private Method searchMethod(String opName, Class<?>[] parameterTypes, boolean setOperation) {
		Method method = null;

		for (OCLOperationGroup operation : registry.getOperationGroups()) {
			try {
				method = operation.getClass().getMethod(opName, parameterTypes);
				if (method != null) {
					oclOperationGroup = operation;
					if (oclOperationGroup.isSetOperationGroup() == setOperation) {
						LOG.debug("Find: " + oclOperationGroup.getClass().getSimpleName() + " - " + method.getName());
						break;
					} else {
						method = null;
					}
				}
			} catch (NoSuchMethodException e) {
			}
		}

		return method;
	}

	/**
	 * Search the transformation method with a different approach.
	 * 
	 * @param opName
	 * @param setOperation
	 * @param parameterTypes
	 * @return
	 */
	private Method research(String opName, boolean setOperation, Class<?>[] parameterTypes) {
		Method method = null;
		if (variableIndexes.size() > 0) {
			method = researchWithExpression(operatorName, setOperation, parameterTypes);
		}
		if (method == null && expressionIndexes.size() > 0) {
			method = researchWithArray(opName, setOperation, parameterTypes, Expression[].class, expressionIndexes.get(0));
			if (method != null) {
				needExpressionArray = true;
			}
		}
		return method;
	}

	/**
	 * A kodkod variable is a subclass of a kodkod expression. Search with
	 * expressen parametey types instead of variables.
	 * 
	 * @param opName
	 * @param setOperation
	 * @param parameterTypes
	 * @return
	 */
	private Method researchWithExpression(String opName, boolean setOperation, Class<?>[] parameterTypes) {
		for (int i = 0; i < variableIndexes.size(); i++) {
			parameterTypes[variableIndexes.get(i)] = Expression.class;
		}

		Method method = searchMethod(opName, parameterTypes, setOperation);
		if (method == null) {
			method = researchWithLastVariable(opName, setOperation, parameterTypes, 1);
			if (method == null) {
				method = researchWithLastVariable(opName, setOperation, parameterTypes, 2);
			}
		}

		if (method == null) {
			method = researchWithArray(opName, setOperation, parameterTypes, Variable[].class, variableIndexes.get(0));
			if (method != null) {
				needVariableArray = true;
			}
		}

		return method;
	}

	/**
	 * Search the transformation method with expression parameter types for
	 * variables except the last 'lastVariables'.
	 * 
	 * @param opName
	 * @param setOperation
	 * @param parameterTypes
	 * @param lastVariables
	 * @return
	 */
	private Method researchWithLastVariable(String opName, boolean setOperation, Class<?>[] parameterTypes, int lastVariables) {
		if (variableIndexes.size() - lastVariables >= 0) {
			for (int i = variableIndexes.size() - 1; i >= variableIndexes.size() - lastVariables; i--) {
				parameterTypes[variableIndexes.get(i)] = Variable.class;
			}

			return searchMethod(opName, parameterTypes, setOperation);
		}
		return null;
	}

	/**
	 * Replaces all variable parameter types with the Expression.class
	 * 
	 * @param parameterTypes
	 */
	public void replaceAllVariables(Class<?>[] parameterTypes) {
		for (Integer index : variableIndexes) {
			parameterTypes[index] = Expression.class;
		}
	}

	/**
	 * Search the transformation method with an array of variables instead of
	 * single variables.
	 * 
	 * @param opName
	 * @param setOperation
	 * @param parameterTypes
	 * @param arrayClass
	 * @param arrayIndex
	 * @return
	 */
	private Method researchWithArray(String opName, boolean setOperation, Class<?>[] parameterTypes, Class<?> arrayClass, int arrayIndex) {
		int firstVariableIndex = arrayIndex;
		Class<?>[] newParameterTypes = new Class<?>[firstVariableIndex + 1];
		for (int i = 0; i < firstVariableIndex; i++) {
			newParameterTypes[i] = parameterTypes[i];
		}

		newParameterTypes[firstVariableIndex] = arrayClass;

		return searchMethod(opName, newParameterTypes, setOperation);
	}

	/**
	 * Returns the group where the transformation method was found, null
	 * otherwise.
	 * 
	 * @return
	 */
	public OCLOperationGroup getOperationClass() {
		return oclOperationGroup;
	}

	/**
	 * Returns true if the transformation method has to be called with an array
	 * of variables.
	 * 
	 * @return
	 */
	public boolean needVariableArray() {
		return needVariableArray;
	}

	/**
	 * Returns true if the transformation method has to be called with an array
	 * of expressions.
	 * 
	 * @return
	 */
	public boolean needExpressionArray() {
		return needExpressionArray;
	}

	public int getFirstArrayIndex() {
		if (needVariableArray) {
			return variableIndexes.get(0);
		} else if (needExpressionArray) {
			return expressionIndexes.get(0);
		}
		throw new NoSuchElementException("No array.");
	}

	/**
	 * Returns true if the result translated operation returns a set.
	 * 
	 * @return
	 */
	public boolean returnsSet() {
		LOG.debug("Method for operator " + operatorName + " returns set: " + oclOperationGroup.returnsSet(operatorName));
		return oclOperationGroup.returnsSet(operatorName);
	}
}
