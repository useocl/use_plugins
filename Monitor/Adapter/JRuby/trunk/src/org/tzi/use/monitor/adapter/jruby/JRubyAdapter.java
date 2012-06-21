/**
 * 
 */
package org.tzi.use.monitor.adapter.jruby;

import java.util.List;

import org.tzi.use.plugins.monitor.MonitorException;
import org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapterSetting;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.ocl.value.Value;

/**
 * @author Lars Hamann
 *
 */
public class JRubyAdapter extends AbstractVMAdapter {

	private static final int SETTING_HOST = 0;
	
	private static final int SETTING_PORT = 1;
	
	private static final int SETTING_MAXINSTANCES = 2;
	
	/**
	 * The host name where the monitored VM is running on
	 */
	private String host;
	
	/**
	 * The port where the monitored VM is providing debugging events
	 */
	private int port;
	
	/**
	 * The maximum number of instances to read for a single type.
	 */
	private long maxInstances;
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter#validateSettings()
	 */
	@Override
	protected void validateSettings() throws InvalidAdapterConfiguration {
		if (settings.get(SETTING_HOST) == null || settings.get(SETTING_HOST).equals("") )
			throw new InvalidAdapterConfiguration("The hostname is missing!"); 
		
		this.host = settings.get(SETTING_HOST).value;
		
		try {
			this.port = Integer.parseInt(settings.get(SETTING_PORT).value);
		} catch (NumberFormatException e) {
			throw new InvalidAdapterConfiguration("Port is not a number!");
		}
		
		try {
			this.maxInstances = Long.parseLong(settings.get(SETTING_MAXINSTANCES).value);
		} catch (NumberFormatException e) {
			throw new InvalidAdapterConfiguration("Max. instances is not a number!");
		}
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter#createSettings(java.util.List)
	 */
	@Override
	protected void createSettings(List<VMAdapterSetting> settings) {
		settings.add(SETTING_HOST, new VMAdapterSetting("Host", "localhost"));
		settings.add(SETTING_PORT, new VMAdapterSetting("Port", "8000"));
		settings.add(SETTING_MAXINSTANCES, new VMAdapterSetting("Max. Ruby instances", "10000"));
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#attachToVM()
	 */
	@Override
	public void attachToVM() throws MonitorException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#resume()
	 */
	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#suspend()
	 */
	@Override
	public void suspend() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#getVMType(java.lang.String)
	 */
	@Override
	public VMType getVMType(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerClassPrepareEvent(java.lang.String)
	 */
	@Override
	public void registerClassPrepareEvent(String javaClassName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#unregisterClassPrepareInterest(java.lang.Object)
	 */
	@Override
	public void unregisterClassPrepareInterest(Object adapterEventInformation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#isVMTypeLoaded(java.lang.String)
	 */
	@Override
	public boolean isVMTypeLoaded(String javaClassName) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerOperationCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMMethod)
	 */
	@Override
	public void registerOperationCallInterest(VMMethod m) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerMethodExit(org.tzi.use.plugins.monitor.vm.mm.VMMethodCall)
	 */
	@Override
	public void registerMethodExit(VMMethodCall call) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#unregisterOperationeExit(java.lang.Object)
	 */
	@Override
	public void unregisterOperationeExit(Object adapterExitInformation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerConstructorCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMType)
	 */
	@Override
	public void registerConstructorCallInterest(VMType vmType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerFieldModificationInterest(org.tzi.use.plugins.monitor.vm.mm.VMField)
	 */
	@Override
	public void registerFieldModificationInterest(VMField f) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#getMethodResultValue(java.lang.Object)
	 */
	@Override
	public Value getMethodResultValue(Object adapterExitInformation) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JRuby";
	}
}
