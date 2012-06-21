package org.tzi.use.plugins.monitor.vm.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.tzi.use.plugins.monitor.Monitor;

public abstract class AbstractVMAdapter implements VMAdapter {

	/**
	 * Controller for the monitor instance to be able
	 * to notify the monitor about certain events. 
	 */
	protected Monitor.Controller controller;
		
	protected List<VMAdapterSetting> settings = new LinkedList<VMAdapterSetting>();

	public AbstractVMAdapter() {
		List<VMAdapterSetting> toInitialize = new LinkedList<VMAdapterSetting>();
		createSettings(toInitialize);
		settings = Collections.unmodifiableList(toInitialize);
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#configure(org.tzi.use.uml.sys.MSystem, java.util.Map)
	 */
	@Override
	public void configure(Monitor.Controller ctr) throws InvalidAdapterConfiguration {
		this.controller = ctr;
		validateSettings();
	}

	/**
	 * Returns the settings of the adapter.
	 * The list is unmodifiable.
	 */
	public List<VMAdapterSetting> getSettings() {
		return settings;
	}
	
	protected abstract void validateSettings() throws InvalidAdapterConfiguration;
	
	protected abstract void createSettings(List<VMAdapterSetting> settings);
}
