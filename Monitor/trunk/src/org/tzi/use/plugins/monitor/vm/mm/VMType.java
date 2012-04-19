/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm;

import java.util.List;
import java.util.Set;

import org.tzi.use.uml.mm.MClass;


/**
 * @author Lars Hamann
 *
 */
public interface VMType {
	/**
	 * The full name of this type.
	 * @return
	 */
	String getName();
	
	/**
	 * This operation returns all direct super classes
	 * of a type. Since USE does not support interfaces,
	 * this includes implemented interfaces.
	 * @return A possible empty <code>Set</code> of all super classes.
	 */
	Set<VMType> getSuperClasses();
	
	Set<VMType> getSubClasses();
	
	boolean isClassType();

	/**
	 * Returns all instances of this type present in the VM.
	 * @return
	 */
	Set<VMObject> getInstances();

	/**
	 * Searches for methods with the given name and
	 * returns the abstract representation of them.
	 * @param methodName The name of the methods to look for.
	 * @return A list of all methods with the given name.
	 */
	List<VMMethod> getMethodsByName(String methodName);
	
	/**
	 * Gets the mapped USE class, if any.
	 * @return The represented USE class or <code>null</code> if no user defined class is mapped. 
	 */
	MClass getUSEClass();
	
	/**
	 * Called by the monitor to save the mapping.
	 * @param cls
	 */
	void setUSEClass(MClass cls);

	/**
	 * @param javaFieldName
	 * @return
	 */
	VMField getFieldByName(String javaFieldName);
}
