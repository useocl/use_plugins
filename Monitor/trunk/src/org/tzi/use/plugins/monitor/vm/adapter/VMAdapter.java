package org.tzi.use.plugins.monitor.vm.adapter;

import java.util.Map;

import org.tzi.use.plugins.monitor.Monitor;
import org.tzi.use.plugins.monitor.MonitorException;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.ocl.value.Value;

/**
 * Interface for virtual machine (VM) adapters which
 * connect to a VM and are used by the USE monitor
 * to query the state of the VM, to set break points, etc.
 * @author Lars Hamann
 */
public interface VMAdapter {
		
	void configure(Monitor.Controller controller, Map<String, String> arguments) throws InvalidAdapterConfiguration;
	
	/**
	 * Called to establish a connection to a possible
	 * remote located virtual machine
	 */
	void attachToVM() throws MonitorException;

	/**
	 * Resumes the monitored VM
	 */
	void resume();

	/**
	 * Suspends the monitored VM
	 */
	void suspend();

	/**
	 * Stops the monitoring process by closing the connection to the VM.
	 */
	void stop();
	
	
	VMType getVMType(String name);

	/**
	 * @param javaClassName
	 */
	void registerClassPrepareEvent(String javaClassName);

	/**
	 * @param javaClassName
	 * @return
	 */
	boolean isVMTypeLoaded(String javaClassName);
	
	/**
	 * The monitor indicates by a call to this operation, that
	 * he is interested in call events to the given {@link VMMethod}.
	 * If the method is called, the adapter must notify the call to the
	 * monitor by calling {@link Monitor.Controller#onMethodCall}.  
	 * @param m
	 */
	void registerOperationCallInterest(VMMethod m);

	/**
	 * After an successful call to an operation the monitor
	 * registers for the exit of an operation to check post conditions, etc.
	 * @param call
	 */
	void registerMethodExit(VMMethodCall call);

	/**
	 * Called if an operation exit in USE is executed.  
	 * @param adapterExitInformation The previously provided exit information by the adapter.
	 */
	void unregisterOperationeExit(Object adapterExitInformation);

	/**
	 * @param adapterExitInformation
	 * @return
	 */
	Value getMethodResultValue(Object adapterExitInformation);

	/**
	 * @param vmType
	 */
	void registerConstructorCallInterest(VMType vmType);
}
