package org.tzi.use.plugin.filmstrip.logic;

public class TransformationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TransformationException() {
	}

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(Throwable cause) {
		super(cause);
	}

	public TransformationException(String message, Throwable cause) {
		super(message, cause);
	}

}
