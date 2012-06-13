/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm;

import java.util.List;

import org.tzi.use.plugins.monitor.vm.adapter.VMAccessException;
import org.tzi.use.uml.ocl.value.Value;

/**
 * Abstraction of an operation call inside of
 * a virtual machine.
 * @author Lars Hamann
 *
 */
public interface VMMethodCall {
	
	/**
	 * The actual parameter values of this operation call in their defined order.
	 * @return
	 * @throws VMAccessException
	 */
	List<Value> getArgumentValues() throws VMAccessException;

	/**
	 * The abstract VMMethod which is called.
	 * @return
	 */
	VMMethod getMethod();

	/**
	 * The abstract instance on which the operation was called.
	 * @return
	 * @throws VMAccessException 
	 */
	VMObject getThisObject() throws VMAccessException;

	/**
	 * The number of arguments this call contains.
	 * @return
	 * @throws VMAccessException 
	 */
	int getNumArguments() throws VMAccessException;
}
