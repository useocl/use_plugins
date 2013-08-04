/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.clr;

import org.tzi.use.monitor.adapter.clr.CLRAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.plugins.monitor.vm.wrap.clr.CLRFieldWrapBase;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;

/**
 * This class represents a CLR heap object.
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 */
public class CLRObject extends CLRBase implements VMObject {

	private final VMType type;
	
	private MObject useObject;	
	
	public CLRObject(CLRAdapter adapter, VMType type, long address)
	{
		super(adapter, address);
		this.type = type;
	}
	

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#isAlive()
	 */
	@Override
	public boolean isAlive() {
		// TODO Get alive status
		return true;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#getType()
	 */
	@Override
	public VMType getType() {
		return type;
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
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMObject#getValue(org.tzi.use.plugins.monitor.vm.mm.VMField)
	 */
	@Override
	public Value getValue(VMField field) {
		CLRField f = (CLRField) field;
		CLRFieldWrapBase w = adapter.getFieldWrap((CLRType)type, this, f);
		return adapter.getUSEValue(w);
	}

}
