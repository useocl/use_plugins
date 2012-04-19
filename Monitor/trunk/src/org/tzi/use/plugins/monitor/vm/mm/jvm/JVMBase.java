/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;

/**
 * Base class for JVM meta-model elements.
 * @author Lars Hamann
 *
 */
public abstract class JVMBase {
	protected JVMAdapter adapter;
	
	public JVMBase(JVMAdapter adapter) {
		this.adapter = adapter;
	}
	
	protected JVMAdapter getAdapter() {
		return this.adapter;
	}
}
