/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm;

import java.util.List;

import org.tzi.use.uml.mm.MOperation;

/**
 * @author Lars Hamann
 *
 */
public interface VMMethod {
	/**
	 * The name of this method
	 * @return
	 */
	String getName();
	
	/**
	 * The list of all argument types.
	 * Names are not always present in a VM,
	 * therefore the order is important.
	 * @return
	 */
	List<VMType> getArgumentTypes();
	
	/**
	 * The USE operation represented by this operation.
	 * Can be <code>null</code>.
	 * @return
	 */
	MOperation getUSEOperation();
	
	/**
	 * Called by the monitor to save the represented
	 * USE operation.
	 * @param useOperation
	 */
	void setUSEOperation(MOperation useOperation);
}
