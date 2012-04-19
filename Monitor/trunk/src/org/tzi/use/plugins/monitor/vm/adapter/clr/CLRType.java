/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.adapter.clr;

import java.util.List;
import java.util.Set;

import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.mm.MClass;

/**
 * @author Lars Hamann
 *
 */
public class CLRType implements VMType {
	
	private final CLRAdapter clrAdpater;
	
	private final String id, name;
	
	/**
	 * @param id
	 */
	public CLRType(CLRAdapter adapter, String id, String name) {
		this.id = id;
		this.name = name;
		this.clrAdpater = adapter;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getSuperClasses()
	 */
	@Override
	public Set<VMType> getSuperClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getSubClasses()
	 */
	@Override
	public Set<VMType> getSubClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#isClassType()
	 */
	@Override
	public boolean isClassType() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getInstances()
	 */
	@Override
	public Set<VMObject> getInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#getMethodsByName(java.lang.String)
	 */
	@Override
	public List<VMMethod> getMethodsByName(String methodName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#getUSEClass()
	 */
	@Override
	public MClass getUSEClass() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#setUSEClass(org.tzi.use.uml.mm.MClass)
	 */
	@Override
	public void setUSEClass(MClass cls) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#getFieldByName(java.lang.String)
	 */
	@Override
	public VMField getFieldByName(String javaFieldName) {
		// TODO Auto-generated method stub
		return null;
	}

}
