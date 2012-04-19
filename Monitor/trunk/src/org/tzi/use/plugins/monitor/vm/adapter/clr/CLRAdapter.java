/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.adapter.clr;

import java.util.Map;

import org.tzi.use.plugins.monitor.Monitor;
import org.tzi.use.plugins.monitor.MonitorException;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;

/**
 * @author Lars Hamann
 *
 */
public abstract class CLRAdapter implements VMAdapter {

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#configure(org.tzi.use.uml.sys.MSystem, java.util.Map)
	 */
	@Override
	public void configure(Monitor.Controller ctr, Map<String, String> arguments)
			throws InvalidAdapterConfiguration {

		System.loadLibrary("clradapter.dll");
		
		String pid = arguments.get("pid");
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#attachToVM()
	 */
	@Override
	public void attachToVM() throws MonitorException {
		
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#getVMType(java.lang.String)
	 */
	@Override
	public VMType getVMType(String name) {
		VMType res = null;
		String id = getTypeId(name);
		
		if (id != null) {
			res = new CLRType(this, id, name);
		}
		
		return res;
	}
	
	private native VMObject[] getInstances(VMType type);
	
	private native String getTypeId(String name);
}
