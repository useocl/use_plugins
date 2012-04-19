/**
 * 
 */
package org.tzi.use.plugins.monitor.vm.adapter.jvm;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.tzi.use.plugins.monitor.Monitor;
import org.tzi.use.plugins.monitor.MonitorException;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMMethod;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMObject;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMType;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.VMDeathRequest;
import com.sun.tools.jdi.GenericAttachingConnector;
import com.sun.tools.jdi.SocketAttachingConnector;

/**
 * @author Lars Hamann
 *
 */
public class JVMAdapter implements VMAdapter {

	/**
	 * Controller for the monitor instance to be able
	 * to notify the monitor about certain events. 
	 */
	private Monitor.Controller controller;
	
	/**
	 * The host name where the monitored VM is running on
	 */
	private String host;
	/**
	 * The port where the monitored VM is providing debugging events
	 */
	private int port;
	
	/**
	 * true if the adapter is connected to a JVM.
	 */
	private boolean isConnected = false;
	
	/**
     * The connector used to connect to the JVM.
     */
    private GenericAttachingConnector connector;
    
    /**
	 * The monitored virtual machine
	 */
	private VirtualMachine monitoredVM = null;
    
	/**
     * This thread handles breakpoint events. E.g. call of a method.
     */
    private Thread breakpointWatcher;
    
    /**
     * In memory mapping of type mapping from JVM to the USE monitor type abstraction.
     */
	private final Map<String, VMType> typeMapping = new HashMap<String, VMType>();
	
	private final Map<ObjectReference, VMObject> objectMapping = new HashMap<ObjectReference, VMObject>();
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#configure(Monitor.Controller, java.util.Map)
	 */
	@Override
	public void configure(Monitor.Controller ctr, Map<String, String> arguments)
			throws InvalidAdapterConfiguration {
		if (!arguments.containsKey("host") || arguments.get("host").equals("") )
			throw new InvalidAdapterConfiguration("The hostname is missing!"); 
		
		if (!arguments.containsKey("port") || arguments.get("port").equals("") )
			throw new InvalidAdapterConfiguration("The port is missing!");
		
		this.host = arguments.get("host");
		try {
			this.port = Integer.parseInt(arguments.get("port"));
		} catch (NumberFormatException e) {
			throw new InvalidAdapterConfiguration("Port is not a number!");
		}
		
		this.controller = ctr;
	}

	/**
	 * @return
	 */
	public long getMaxInstances() {
		//TODO: Provide argument
		return 10000;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#attachToVM()
	 */
	@Override
	public void attachToVM() throws MonitorException {
		connector = new SocketAttachingConnector();
    	@SuppressWarnings("unchecked")
		Map<String, Argument> args = connector.defaultArguments();
    	
    	args.get("hostname").setValue(host);
    	args.get("port").setValue(Integer.toString(port));
    	
    	try {
			monitoredVM = connector.attach(args);
			// We want to get notified if the VM terminates.
			VMDeathRequest deathReq = monitoredVM.eventRequestManager().createVMDeathRequest();
			deathReq.enable();
		} catch (IOException e) {
			throw new MonitorException("Could not connect to virtual machine", e);
		} catch (IllegalConnectorArgumentsException e) {
			throw new MonitorException("Could not connect to virtual machine", e);
		}
    	
    	isConnected = true;
    	breakpointWatcher = new Thread(new BreakPointWatcher(), "Monitor breakpoint watcher");
		breakpointWatcher.start();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#resume()
	 */
	@Override
	public void resume() {
		monitoredVM.resume();
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#stop()
	 */
	@Override
	public void stop() {
		isConnected = false;
		if (monitoredVM != null)
    		monitoredVM.dispose();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#suspend()
	 */
	@Override
	public void suspend() {
		monitoredVM.suspend();		
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#getVMType(java.lang.String)
	 */
	@Override
	public VMType getVMType(String name) {
		JVMType res = null;
		
		if (!typeMapping.containsKey(name)) {
			List<ReferenceType> classes = monitoredVM.classesByName(name);
    	
			if (classes.size() == 1) {
				res = new JVMType(this, classes.get(0));
			}
			
			// Put JVMType or null into mapping.
			// null either means a type is not loaded yet or it does not exist.
			typeMapping.put(name, res);
		}
		
		return typeMapping.get(name);
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#registerClassPrepareEvent(java.lang.String)
	 */
	@Override
	public void registerClassPrepareEvent(String javaClassName) {
		ClassPrepareRequest req = monitoredVM.eventRequestManager().createClassPrepareRequest();
		req.addClassFilter(javaClassName);
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#isVMTypeLoaded(java.lang.String)
	 */
	@Override
	public boolean isVMTypeLoaded(String javaClassName) {
		return getVMType(javaClassName) != null;
	}
	
	private final class BreakPointWatcher implements Runnable {
		@Override
		public void run() {
			//FIXME: Dynamic monitoring
			/*
			while (isConnected) {
				try {
					EventSet events = monitoredVM.eventQueue().remove();
					if (!isConnected) return;
					
					controller.newLogMessage(Level.FINER, "Handling JVM events.");
					
					for (com.sun.jdi.event.Event e : events) {
						if (e instanceof BreakpointEvent) {
							BreakpointEvent be = (BreakpointEvent)e;
							controller.newLogMessage(Level.FINER, "Handling operation call.");
							boolean opCallResult = onMethodCall(be);
							if (!opCallResult) {
								controller.newLogMessage(Level.SEVERE, "Enter of method " + be.location().method().toString() + " failed.");
								hasFailedOperation = true;
								waitForUserInput();
							}
						} else if (e instanceof ClassPrepareEvent) {
							ClassPrepareEvent ce = (ClassPrepareEvent)e;
							controller.newLogMessage(Level.FINER, "Registering operations of prepared Java class " + ce.referenceType().name() + ".");
							registerOperationBreakPoints(ce.referenceType());
						} else if (e instanceof ModificationWatchpointEvent) {
							ModificationWatchpointEvent we = (ModificationWatchpointEvent)e;
							controller.newLogMessage(Level.FINER, "Handling modification watchpoint " + we.field().toString() + ".");
							updateAttribute(we.object(), we.field(), we.valueToBe());
						} else if (e instanceof MethodExitEvent) {
							MethodExitEvent me = (MethodExitEvent)e;
							controller.newLogMessage(Level.FINER, "Handling operation exit watchpoint " + me.method().toString() + ".");
							boolean opCallResult = onMethodExit(me);
							if (!opCallResult) {
								controller.newLogMessage(Level.WARNING, "Exit of method " + me.method().toString() + " failed.");
								hasFailedOperation = true;
								waitForUserInput();
							}
						} else if (e instanceof VMDeathEvent) {
							controller.newLogMessage(Level.INFO, "JVM terminated.");
							controller.end();
							return;
						}
					}
					events.resume();
					controller.newLogMessage(Level.FINER, "Resumed threads after handling VM events.");
				} catch (InterruptedException e) {
					// VM is away np
				} catch (VMDisconnectedException e) {
					isConnected = false;
					monitoredVM = null;
					controller.newLogMessage(Level.WARNING, "Monitored application has terminated.");
					controller.end();
				} catch (Exception ex) {
					controller.newLogMessage(
							Level.SEVERE,
							"Error while listening to break points: ["
									+ ex.getClass().getSimpleName() + "] "
									+ ex.getMessage());
					controller.newLogMessage(Level.SEVERE, "Disonnecting");
					controller.end();
					return;
				}
			}
			*/
		}
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#registerOperationCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMMethod)
	 */
	@Override
	public void registerOperationCallInterest(VMMethod m) {
		JVMMethod jvmMethod = (JVMMethod)m;
		
		if (jvmMethod.getMethod().location() == null) {
			controller.newLogMessage(Level.WARNING, "Cannot set breakpoints for abstract or interface operation " + m.toString());
			return;
		}
		
		BreakpointRequest req = monitoredVM.eventRequestManager().createBreakpointRequest(jvmMethod.getMethod().location());
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
	}
	
	/**
     * Returns the mapped USE value of <code>javaValue</code>.
     * Java-value of type {@link ObjectReference} are tried to be mapped to
     * corresponding USE-objects. If no object is found <code>UndefinedValue</code>
     * is returned.
     * @param javaValue
     * @return
     */
    public Value getUSEValue(com.sun.jdi.Value javaValue) {
    	Value v = UndefinedValue.instance;
		
    	if (javaValue == null) return v;
    	
    	if (objectMapping.containsKey(javaValue)) {
    		VMObject obj = objectMapping.get(javaValue);
    		v = new ObjectValue(obj.getUSEObject().type(), obj.getUSEObject());
    	} else if (javaValue instanceof ArrayReference) {
    		//FIXME: Correct handling of array parameter!
    		ArrayReference javaArray = (ArrayReference)javaValue;
    		List<com.sun.jdi.Value> javaValues = javaArray.getValues();
    		Value[] useValues = new Value[javaValues.size()];
    				
    		for (int i = 0; i < javaValues.size(); ++i) {
    			useValues[i] = getUSEValue(javaValues.get(i));
    		}
    		
    		v = new SequenceValue(TypeFactory.mkVoidType(), useValues);
    		
		} else if (javaValue instanceof StringReference) {
			v = new StringValue(((StringReference)javaValue).value());
		// Primitive integer, i.e., int
		} else if (javaValue instanceof IntegerValue) {
			v = org.tzi.use.uml.ocl.value.IntegerValue.valueOf(((IntegerValue)javaValue).intValue());
		} else if (javaValue instanceof BooleanValue) {
			boolean b = ((BooleanValue)javaValue).booleanValue();
			v = org.tzi.use.uml.ocl.value.BooleanValue.get(b);
		} else if (javaValue instanceof ObjectReference) {
			ObjectReference refValue = (ObjectReference)javaValue;

			// Reference integer, i. e., Integer
			if (refValue.referenceType().name().equals("java.lang.Integer")) {
				// TODO: Don't hard code!
				Field fValue = refValue.referenceType().fieldByName("value");
				IntegerValue iValue = (IntegerValue)refValue.getValue(fValue);
				
				v = org.tzi.use.uml.ocl.value.IntegerValue.valueOf(iValue.intValue());
			} else if (refValue.type() instanceof ClassType && ((ClassType)refValue.type()).isEnum()) {
				v = getUSEEnumValue(refValue);
			} else if (refValue.type().name().equals("java.util.ArrayList")) {
				// ArrayList uses the private field "elementData:Object" to store the values
		    	Field field = refValue.referenceType().fieldByName("elementData");
		    	ArrayReference elements = (ArrayReference)refValue.getValue(field);
		    	
		    	List<com.sun.jdi.Value> values = elements.getValues();
		    	Value[] useValues = new Value[values.size()];
		    	int index = 0;
		    	
		    	for (com.sun.jdi.Value value : values) {
		    		if (value == null) {
		    			useValues[index] = UndefinedValue.instance;
		    		} else {
		    			useValues[index] = getUSEValue(value);
		    		}
		    		++index;
		    	}
		    	
		    	v = new SequenceValue(TypeFactory.mkVoidType(), useValues);
			} else if (refValue.type().name().equals("java.util.HashSet")) {
				List<Value> useValues = new LinkedList<Value>();
				
				// HashSet uses the private field "map:HashMap" to store the values
		    	Field field = refValue.referenceType().fieldByName("map");
		    	ObjectReference mapValue = (ObjectReference)refValue.getValue(field);
		    	
		    	// Values are stored in the field "table:Entry"
		    	field = mapValue.referenceType().fieldByName("table");
		    	ArrayReference tableValue = (ArrayReference)mapValue.getValue(field);
		    	
		    	List<com.sun.jdi.Value> mapEntries = tableValue.getValues();
		    	Field fieldKey = null;
		    	Field fieldNext = null;
		    	
		    	for (com.sun.jdi.Value value : mapEntries) {
		    		if (value != null) {
		    			ObjectReference mapEntry = (ObjectReference)value;
		    		
		    			if (fieldKey == null) {
		    				fieldKey = mapEntry.referenceType().fieldByName("key");
		    				fieldNext = mapEntry.referenceType().fieldByName("next");
		    			}

		    			ObjectReference referencedObject = (ObjectReference)mapEntry.getValue(fieldKey);
		    			useValues.add(getUSEValue(referencedObject));
		    			
		    			ObjectReference nextEntry = (ObjectReference)mapEntry.getValue(fieldNext);
		    			while (nextEntry != null) {
		    				referencedObject = (ObjectReference)nextEntry.getValue(fieldKey);
		    				useValues.add(getUSEValue(referencedObject));
		    				nextEntry = (ObjectReference)nextEntry.getValue(fieldNext);
		    			}
		    		}
		    	}
		    	
		    	v = new SetValue(TypeFactory.mkVoidType(), useValues);
			} else {
				controller.newLogMessage(Level.WARNING, "Unhandled type:" + javaValue.getClass().getName());
			}
			
		} else {
			controller.newLogMessage(Level.WARNING, "Unhandled type:" + javaValue.getClass().getName());
		}
		
		return v;
    }

    /**
	 * @param expectedType
	 * @param javaValue
	 * @return
	 */
	private Value getUSEEnumValue(ObjectReference javaValue) {
		org.tzi.use.uml.ocl.type.EnumType enumType = controller.getEnumerationType(javaValue.type().name());
				
		if (enumType == null) {
			controller.newLogMessage(Level.WARNING, "Unhandled enum type:" + javaValue.type().name());
			return UndefinedValue.instance;
		}
		
		ObjectReference enumValue = (ObjectReference)javaValue;
		Field nameField = enumValue.referenceType().fieldByName("name");
		
		StringReference enumLiteral = (StringReference)enumValue.getValue(nameField);
		String litString = enumLiteral.value();
		
		try {
			EnumValue v = new EnumValue(enumType, litString);
			return v;
		} catch (IllegalArgumentException e) {
			controller.newLogMessage(Level.WARNING, e.getMessage());
		}
		
		return UndefinedValue.instance;
	}
	
	/**
	 * @param jvmType
	 * @return
	 */
	public Set<VMObject> readInstances(JVMType jvmType) {
		List<ObjectReference> instances = jvmType.getType().instances(getMaxInstances());
		
		Set<VMObject> vmObjects = new HashSet<VMObject>(instances.size());
		for (ObjectReference ref : instances) {
			JVMObject obj = new JVMObject(this, ref); 
			vmObjects.add(obj);
			objectMapping.put(ref, obj);
		}
		
		return vmObjects;
	}
}
