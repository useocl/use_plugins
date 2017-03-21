package org.tzi.use.kodkod.transform;

/**
 * Exception class for transformation errors.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class TransformationException extends RuntimeException {

	private static final long serialVersionUID = 2890361985116057241L;

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(String message, Throwable e) {
		super(message, e);
	}
}
