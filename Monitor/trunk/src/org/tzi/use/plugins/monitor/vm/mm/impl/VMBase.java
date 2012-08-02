/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.impl;

import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMIdentifiable;

/**
 * @author Lars Hamann
 *
 */
public abstract class VMBase<T> implements VMIdentifiable {
	
	private final T vmId;
	
	protected VMAdapter adapter;
	
	public VMBase(VMAdapter adapter, T vmId) {
		this.adapter = adapter;
		this.vmId = vmId;
	}
	
	protected VMAdapter getAdapter() {
		return this.adapter;
	}
	
	public T getVmId() {
		return vmId;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getId()
	 */
	@Override
	public T getId() {
		return vmId;
	}
}
