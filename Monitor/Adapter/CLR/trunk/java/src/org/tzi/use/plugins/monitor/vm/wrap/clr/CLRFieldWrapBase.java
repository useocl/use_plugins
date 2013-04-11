/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.wrap.clr;

/**
 * Base class for CLR field wrappers.
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 */
public abstract class CLRFieldWrapBase {
	protected final long token;

	public CLRFieldWrapBase(long token) {
		this.token = token;
	}
	
	public long getToken() {
		return token;
	}	
}
