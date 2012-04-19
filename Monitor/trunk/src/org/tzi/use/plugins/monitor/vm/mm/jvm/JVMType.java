/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.mm.MClass;

import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.InterfaceType;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;

/**
 * @author Lars Hamann
 *
 */
public class JVMType extends JVMBase implements VMType {

	private final ReferenceType type;
		
	private MClass useClass;
	
	public JVMType(JVMAdapter adapter, ReferenceType mappedType) {
		super(adapter);
		this.type = mappedType;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getName()
	 */
	@Override
	public String getName() {
		return type.name();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getSuperClasses()
	 */
	@Override
	public Set<VMType> getSuperClasses() {
		if (!isClassType()) return Collections.emptySet();
		
		Set<VMType> res = new HashSet<VMType>();
		
		if (type instanceof ClassType) {
			ClassType cType = (ClassType)type;
			res.add(adapter.getVMType(cType.superclass().name()));
			
			for (InterfaceType iType : cType.interfaces()) {
				res.add(adapter.getVMType(iType.name()));
			}
		} else if (type instanceof InterfaceType) {
			InterfaceType iType = (InterfaceType)type;
			for (InterfaceType superInterfaceType : iType.superinterfaces()) {
				res.add(adapter.getVMType(superInterfaceType.name()));
			}
		}
		
		return res;
	}

	public ReferenceType getType() {
		return this.type;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getSubClasses()
	 */
	@Override
	public Set<VMType> getSubClasses() {
		if (!isClassType()) return Collections.emptySet();
		
		Set<VMType> res = new HashSet<VMType>();
		
		if (type instanceof ClassType) {
			ClassType cType = (ClassType)type;
			for (ClassType subType : cType.subclasses()) {
				VMType t = adapter.getVMType(subType.name());
				res.add(t);
				
				for (InterfaceType iType : subType.interfaces()) {
					res.add(adapter.getVMType(iType.name()));
				}
			}
		} else if (type instanceof InterfaceType) {
			InterfaceType iType = (InterfaceType)type;
			for (InterfaceType superInterfaceType : iType.subinterfaces()) {
				res.add(adapter.getVMType(superInterfaceType.name()));
			}
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#isClassType()
	 */
	@Override
	public boolean isClassType() {
		return type instanceof ClassType || type instanceof InterfaceType;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getInstances()
	 */
	@Override
	public Set<VMObject> getInstances() {
		return adapter.readInstances(this);
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMType#getMethodsByName(java.lang.String)
	 */
	@Override
	public List<VMMethod> getMethodsByName(String methodName) {
		List<Method> methods = type.methodsByName(methodName);
		List<VMMethod> vmMethods = new LinkedList<VMMethod>();
		
		for (Method m : methods) {
			vmMethods.add(new JVMMethod(getAdapter(), m));
		}
		
		return vmMethods;
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
	public VMField getFieldByName(String vmFieldName) {
		Field f = type.fieldByName(vmFieldName);
		
		if (f != null) {
			return new JVMField(adapter, f);
		} else {
			return null;
		}
	}

}
