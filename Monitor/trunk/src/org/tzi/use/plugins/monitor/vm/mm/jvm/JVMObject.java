/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;

import com.sun.jdi.ObjectReference;

/**
 * @author Lars Hamann
 *
 */
public class JVMObject extends JVMBase implements VMObject {

	private final ObjectReference reference;
	
	private MObject useObject;
	
	/**
	 * @param adapter
	 */
	public JVMObject(JVMAdapter adapter, ObjectReference ref) {
		super(adapter);
		this.reference = ref;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#isAlive()
	 */
	@Override
	public boolean isAlive() {
		return !reference.isCollected();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#getUSEObject()
	 */
	@Override
	public MObject getUSEObject() {
		return useObject;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#setUSEObject(org.tzi.use.uml.sys.MObject)
	 */
	@Override
	public void setUSEObject(MObject obj) {
		useObject = obj;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#getType()
	 */
	@Override
	public VMType getType() {
		//TODO: Save?
		return adapter.getVMType(reference.type().name());
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#getValue(org.tzi.use.plugins.monitor.vm.mm.VMField)
	 */
	@Override
	public Value getValue(VMField field) {
		JVMField f = (JVMField)field;
		com.sun.jdi.Value val = reference.getValue(f.getField());
		
		return adapter.getUSEValue(val);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.reference.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof JVMObject))
			return false;
		
		return this.reference.equals(((JVMObject)obj).reference);
	}
}
