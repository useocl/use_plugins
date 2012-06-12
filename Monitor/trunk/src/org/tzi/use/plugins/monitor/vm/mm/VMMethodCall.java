/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm;

import java.util.List;

import org.tzi.use.uml.ocl.value.Value;

/**
 * @author Lars Hamann
 *
 */
public interface VMMethodCall {
	
	List<Value> getArgumentValues();

	/**
	 * @return
	 */
	VMMethod getMethod();

	/**
	 * @return
	 */
	VMObject getThisObject();

	/**
	 * @return
	 */
	int getNumArguments();
}
