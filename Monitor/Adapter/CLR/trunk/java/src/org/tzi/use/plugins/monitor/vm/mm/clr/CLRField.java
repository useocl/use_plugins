/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.clr;

import org.tzi.use.monitor.adapter.clr.CLRAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;

/**
 * This class represents a CLR field.
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 */
public class CLRField extends CLRBase implements VMField {
	
	final String name;
	
	public CLRField(CLRAdapter adapter, String name, long token) {
		super(adapter, token);
		this.name = name;
	}

	private MAttribute useAttribute = null;
	
	private MAssociationEnd useAssociationEnd = null;	
	

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#setUSEAttribute(org.tzi.use.uml.mm.MAttribute)
	 */
	@Override
	public void setUSEAttribute(MAttribute attr) {
		useAttribute = attr;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getUSEAttribute()
	 */
	@Override
	public MAttribute getUSEAttribute() {
		return useAttribute;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#setUSEAssociationEnd(org.tzi.use.uml.mm.MAssociationEnd)
	 */
	@Override
	public void setUSEAssociationEnd(MAssociationEnd end) {
		useAssociationEnd = end;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getUSEAssociationEnd()
	 */
	@Override
	public MAssociationEnd getUSEAssociationEnd() {
		return useAssociationEnd;
	}

}
