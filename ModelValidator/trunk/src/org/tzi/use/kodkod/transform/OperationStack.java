package org.tzi.use.kodkod.transform;

import java.util.Stack;

import org.tzi.use.uml.mm.MOperation;

/**
 * Singleton to detect recursion in transformed operations.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum OperationStack {

	INSTANCE;
	
	private Stack<MOperation> operationStack;

	private OperationStack() {
		operationStack = new Stack<MOperation>();
	}
	
	public boolean contains(MOperation operation){
		return operationStack.contains(operation);
	}
	
	public void push(MOperation operation){
		operationStack.push(operation);
	}
	
	public void pop(){
		operationStack.pop();
	}
	
	public void clear()	{
		operationStack.clear();
	}
	
	public boolean isEmpty(){
		return operationStack.isEmpty();
	}
}
