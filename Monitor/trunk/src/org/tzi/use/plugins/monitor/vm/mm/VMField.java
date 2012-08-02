/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm;

import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;


/**
 * @author Lars Hamann
 *
 */
public interface VMField extends VMIdentifiable {
	
	String getName();

	void setUSEAttribute(MAttribute attr);
	
	MAttribute getUSEAttribute();
		
	void setUSEAssociationEnd(MAssociationEnd end);
	
	MAssociationEnd getUSEAssociationEnd();
}
