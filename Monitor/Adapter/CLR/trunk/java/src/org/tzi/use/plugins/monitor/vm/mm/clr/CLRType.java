/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.clr;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.tzi.use.monitor.adapter.clr.CLRAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.mm.MClass;

/**
 * This class represents a CLR type.
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 */
public class CLRType extends CLRBase implements VMType {
		
	private final String name;
	
	private MClass useClass;
	
	/**
	 * @param id
	 */
	public CLRType(CLRAdapter adapter, long id, String name) {
		super(adapter, id);
		this.name = name;
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
		return adapter.getSuperClasses(this);
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getSubClasses()
	 */
	@Override
	public Set<VMType> getSubClasses() {
		return adapter.getSubClasses(this);
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#isClassType()
	 */
	@Override
	public boolean isClassType() {
		return adapter.isClassType(this);
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getInstances()
	 */
	@Override
	public Set<VMObject> getInstances() {
		return adapter.readInstances(this);
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#getMethodsByName(java.lang.String)
	 */
	@Override
	public List<VMMethod> getMethodsByName(String methodName) {
		//TODO: implement me!
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#getUSEClass()
	 */
	@Override
	public MClass getUSEClass() {
		return this.useClass;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#setUSEClass(org.tzi.use.uml.mm.MClass)
	 */
	@Override
	public void setUSEClass(MClass cls) {
		this.useClass = cls;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMType#getFieldByName(java.lang.String)
	 */
	@Override
	public VMField getFieldByName(String javaFieldName) {
		return adapter.getCLRFieldByName(this, javaFieldName);
	}

}
