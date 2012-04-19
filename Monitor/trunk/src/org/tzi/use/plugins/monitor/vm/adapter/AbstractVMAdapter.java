package org.tzi.use.plugins.monitor.vm.adapter;

import java.util.Map;

import org.tzi.use.plugins.monitor.Monitor;
import org.tzi.use.uml.sys.MSystem;

public abstract class AbstractVMAdapter implements VMAdapter {
	
	protected MSystem system;

	protected Map<String, String> settings;
	
	/**
	 * @return the system
	 */
	public MSystem getSystem() {
		return system;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#configure(org.tzi.use.uml.sys.MSystem, java.util.Map)
	 */
	@Override
	public void configure(Monitor.Controller ctr, Map<String, String> settings) throws InvalidAdapterConfiguration {
		this.settings = settings;
		validateSettings();
	}

	protected abstract void validateSettings() throws InvalidAdapterConfiguration;
}
