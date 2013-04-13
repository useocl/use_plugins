/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.wrap.clr;

/**
 * Wrapper class for primitive field values.
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 */
public class CLRFieldWrapValue extends CLRFieldWrapBase {

	private final Object value;
	
	public CLRFieldWrapValue(long token, Object value) {
		super(token);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "CLRFieldWrapValue [value=" + this.value + "]";
	}	
}
