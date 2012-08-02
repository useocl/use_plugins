/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.impl;

import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;

import com.sun.jdi.Field;

/**
 * @author Lars Hamann
 *
 */
public class VMFieldImpl<T> extends VMBase<T> implements VMField {

	private MAttribute useAttribute = null;
	
	private MAssociationEnd useAssociationEnd = null;
	
	private String name;
	
	/**
	 * @param adapter
	 */
	public VMFieldImpl(VMAdapter adapter, T id, String name) {
		super(adapter, id);
		this.name = name;
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

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
}
