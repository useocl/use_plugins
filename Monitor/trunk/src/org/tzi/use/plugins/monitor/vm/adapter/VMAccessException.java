/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.adapter;

/**
 * @author Lars Hamann
 *
 */
public class VMAccessException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2141347317738835388L;

	public VMAccessException(String message) {
		super(message);
	}
	
	public VMAccessException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public VMAccessException(Throwable cause) {
		super(cause);
	}
}
