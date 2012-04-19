/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.mm.MOperation;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;
import com.sun.jdi.Type;

/**
 * @author Lars Hamann
 *
 */
public class JVMMethod extends JVMBase implements VMMethod {

	private final Method method;
	
	private MOperation useOperation;
	
	/**
	 * @param adapter
	 */
	public JVMMethod(JVMAdapter adapter, Method m) {
		super(adapter);
		this.method = m;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethod#getName()
	 */
	@Override
	public String getName() {
		return method.name();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethod#getArgumentTypes()
	 */
	@Override
	public List<VMType> getArgumentTypes() {
		try {
			List<VMType> types = new ArrayList<VMType>(method.argumentTypes().size());
			
			for (Type t : method.argumentTypes()) {
				types.add(adapter.getVMType(t.name()));
			}
			
			return types;
		} catch (ClassNotLoadedException e) {
			//FIXME: How to handle??
		}
		
		return Collections.emptyList();
	}

	/**
	 * Returns the encapsulated JVM method.
	 * @return
	 */
	public Method getMethod() {
		return method;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethod#getUSEOperation()
	 */
	@Override
	public MOperation getUSEOperation() {
		return useOperation;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethod#setUSEOperation(org.tzi.use.uml.mm.MOperation)
	 */
	@Override
	public void setUSEOperation(MOperation useOperation) {
		this.useOperation = useOperation;
	}

}
