package org.tzi.use.kodkod.transform;

import org.tzi.use.uml.mm.MOperation;

/**
 * Singleton to detect recursion in transformed operations.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum OperationRecursionDetector {

	INSTANCE;

	private MOperation currentOperation;

	private OperationRecursionDetector() {
	}

	/**
	 * The visitor is in a new operation.
	 * 
	 * @param operation
	 * @throws OperationRecursionException
	 */
	public void addOperation(MOperation operation) throws OperationRecursionException {
		if (operation == currentOperation) {
			throw new OperationRecursionException();
		}
		currentOperation = operation;
	}

	/**
	 * The transformation of the operation has finished.
	 */
	public void finishOperation() {
		currentOperation = null;
	}

	public class OperationRecursionException extends Exception {

		private static final long serialVersionUID = 8504855096621571855L;

	}
}
