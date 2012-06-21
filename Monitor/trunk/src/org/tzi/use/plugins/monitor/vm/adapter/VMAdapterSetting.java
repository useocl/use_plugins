/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.adapter;

/**
 * @author Lars Hamann
 *
 */
public class VMAdapterSetting {
	
	public final String name;
	public String value;
	
	public VMAdapterSetting(String name) {
		this(name, null);
	}
	
	public VMAdapterSetting(String name, String defaultValue) {
		this.name = name;
		this.value = defaultValue;
	}
}
