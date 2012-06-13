/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;

import com.sun.jdi.Field;

/**
 * @author Lars Hamann
 *
 */
public class JVMField extends JVMBase implements VMField {

	private final Field field;
	
	private MAttribute useAttribute = null;
	
	private MAssociationEnd useAssociationEnd = null;
	
	/**
	 * @param adapter
	 */
	public JVMField(JVMAdapter adapter, Field field) {
		super(adapter);
		this.field = field;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getName()
	 */
	@Override
	public String getName() {
		return this.field.name();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMField#getId()
	 */
	@Override
	public Object getId() {
		return field;
	}
	
	public Field getField() {
		return this.field;
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
