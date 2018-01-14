package org.tzi.use.plugin.otc.data;

public class TLogicException extends Exception {
	private static final long serialVersionUID = -4812458002779734205L;

	public TLogicException(String message) {
		super(message);
	}

	public TLogicException(String message, Throwable cause) {
		super(message, cause);
	}

	public TLogicException(Throwable cause) {
		super(cause);
	}
}