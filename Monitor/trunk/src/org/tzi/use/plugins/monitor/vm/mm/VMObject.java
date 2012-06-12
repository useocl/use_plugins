/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm;

import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;

/**
 * @author Lars Hamann
 *
 */
public interface VMObject extends VMValue {
	
	/**
	 * An object which uniquely identifies the object in
	 * the VM.
	 * @return
	 */
	Object getId();
	
	boolean isAlive();
		
	VMType getType();
	
	/**
	 * The USE object linked to this VMObject.
	 * @return
	 */
	MObject getUSEObject();
	
	/**
	 * Called by the monitor to set the USE object linked to this VMObject. 
	 * @param obj
	 */
	void setUSEObject(MObject obj);

	/**
	 * Returns the current value for the given field as a USE value.
	 * @param field
	 * @return
	 */
	Value getValue(VMField field);
}
