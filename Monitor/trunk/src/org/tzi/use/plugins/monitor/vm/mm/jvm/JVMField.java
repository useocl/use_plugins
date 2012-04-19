/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;

import com.sun.jdi.Field;

/**
 * @author Lars Hamann
 *
 */
public class JVMField extends JVMBase implements VMField {

	private final Field field;
	
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
	
	public Field getField() {
		return this.field;
	}
}
