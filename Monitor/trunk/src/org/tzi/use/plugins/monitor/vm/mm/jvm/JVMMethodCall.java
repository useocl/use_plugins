/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.mm.jvm;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.tzi.use.plugins.monitor.vm.adapter.jvm.JVMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.uml.ocl.value.Value;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.event.BreakpointEvent;

/**
 * @author Lars Hamann
 *
 */
public class JVMMethodCall extends JVMBase implements VMMethodCall {

	private final JVMMethod method;
	
	private final BreakpointEvent breakpointEvent;
	
	public JVMMethodCall(JVMAdapter adapter, JVMMethod m, BreakpointEvent e) {
		super(adapter);
		this.method = m;
		this.breakpointEvent = e;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethodCall#getArgumentValues()
	 */
	@Override
	public List<Value> getArgumentValues() {
		//FIXME: Exception handling (VMAccess exception?)!
		List<Value> args = new LinkedList<Value>();
		List<com.sun.jdi.Value> javaArgs;
		
		try {
			javaArgs = breakpointEvent.thread().frame(0).getArgumentValues();
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
		
		for (com.sun.jdi.Value v : javaArgs) {
			Value val = adapter.getUSEValue(v);
			args.add(val);
		}
		
		return args;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethodCall#getMethod()
	 */
	@Override
	public JVMMethod getMethod() {
		return method;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethodCall#getThisObject()
	 */
	@Override
	public VMObject getThisObject() {
    	return adapter.getThisObjectForThread(breakpointEvent.thread());
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.mm.VMMethodCall#getNumArguments()
	 */
	@Override
	public int getNumArguments() {
		//FIXME: Exception handling!
		try {
			return breakpointEvent.thread().frame(0).getArgumentValues().size();
		} catch (IncompatibleThreadStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public BreakpointEvent getBreakpointEvent() {
		return this.breakpointEvent;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Method call to " + method.getName();
	}

}
