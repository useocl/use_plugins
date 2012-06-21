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
import java.util.Stack;
import java.util.logging.Level;

import org.tzi.use.plugins.monitor.MonitorException;
import org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAccessException;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapterSetting;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMField;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMMethod;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMObject;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMType;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.TupleValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.util.StringUtil;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ThreadReference;
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
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.VMDeathRequest;
import com.sun.tools.jdi.GenericAttachingConnector;
import com.sun.tools.jdi.SocketAttachingConnector;

/**
 * The adapter for monitoring Java Virtual Machines.
 * @author Lars Hamann
 *
 */
public class JVMAdapter extends AbstractVMAdapter {

	private static final int SETTING_HOST = 0;
	
	private static final int SETTING_PORT = 1;
	
	private static final int SETTING_MAXINSTANCES = 2;
	
	/**
	 * The host name where the monitored VM is running on
	 */
	private String host;
	
	/**
	 * The port where the monitored VM is providing debugging events
	 */
	private int port;
	
	/**
	 * The maximum number of instances to read for a single type.
	 */
	private long maxInstances;
	
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
	private Map<String, VMType> typeMapping = new HashMap<String, VMType>();
	
	public JVMAdapter() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter#validateSettings()
	 */
	@Override
	protected void validateSettings() throws InvalidAdapterConfiguration {
		if (settings.get(SETTING_HOST) == null || settings.get(SETTING_HOST).equals("") )
			throw new InvalidAdapterConfiguration("The hostname is missing!"); 
		
		this.host = settings.get(SETTING_HOST).value;
		
		try {
			this.port = Integer.parseInt(settings.get(SETTING_PORT).value);
		} catch (NumberFormatException e) {
			throw new InvalidAdapterConfiguration("Port is not a number!");
		}
		
		try {
			this.maxInstances = Long.parseLong(settings.get(SETTING_MAXINSTANCES).value);
		} catch (NumberFormatException e) {
			throw new InvalidAdapterConfiguration("Max. instances is not a number!");
		}
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter#createSettings(java.util.List)
	 */
	@Override
	protected void createSettings(List<VMAdapterSetting> settings) {
		settings.add(SETTING_HOST, new VMAdapterSetting("Host", "localhost"));
		settings.add(SETTING_PORT, new VMAdapterSetting("Port", "6000"));
		settings.add(SETTING_MAXINSTANCES, new VMAdapterSetting("Max. instances", "10000"));
	}

	/**
	 * @return
	 */
	public long getMaxInstances() {
		return maxInstances;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#attachToVM()
	 */
	@Override
	public void attachToVM() throws MonitorException {
		this.typeMapping = new HashMap<String, VMType>();
		
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
		
		this.typeMapping = null;
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
			//FIXME: Dynamic
			
			while (isConnected) {
				try {
					EventSet events = monitoredVM.eventQueue().remove();
					if (!isConnected) return;
					
					controller.newLogMessage(this, Level.FINER, "Handling JVM events.");

					for (com.sun.jdi.event.Event e : events) {
						if (e instanceof BreakpointEvent) {
							BreakpointEvent be = (BreakpointEvent)e;
							controller.newLogMessage(this, Level.FINE, "Handling operation call.");
							onMethodCall(be);
						} else if (e instanceof ClassPrepareEvent) {
							ClassPrepareEvent ce = (ClassPrepareEvent)e;
							controller.newLogMessage(this, Level.FINE, "Java class loaded:" + ce.referenceType().name() + ".");
							JVMType type = new JVMType(JVMAdapter.this, ce.referenceType());
							controller.onNewVMTypeLoaded(ce, type);
						} else if (e instanceof ModificationWatchpointEvent) {
							ModificationWatchpointEvent we = (ModificationWatchpointEvent)e;
							controller.newLogMessage(this, Level.FINE, "Handling modification watchpoint " + we.field().toString() + ".");
							onUpdateAttribute(we.object(), we.field(), we.valueToBe());
						} else if (e instanceof MethodExitEvent) {
							MethodExitEvent me = (MethodExitEvent)e;
							controller.newLogMessage(this, Level.FINE, "Handling operation exit watchpoint " + me.method().toString() + ".");
							onMethodExit(me);
						} else if (e instanceof VMDeathEvent) {
							controller.newLogMessage(this, Level.INFO, "JVM terminated.");
							controller.end();
							return;
						}

					}
					events.resume();
					controller.newLogMessage(this, Level.FINER, "Resumed threads after handling VM events.");
				} catch (InterruptedException e) {
					// VM is away np
				} catch (VMDisconnectedException e) {
					isConnected = false;
					monitoredVM = null;
					controller.newLogMessage(this, Level.WARNING, "Monitored application has terminated.");
					controller.end();
				} catch (Exception ex) {
					controller.newLogMessage(this, 
							Level.SEVERE,
							"Error while listening to break points: ["
									+ ex.getClass().getSimpleName() + "] "
									+ ex.getMessage());
					controller.newLogMessage(this, Level.SEVERE, "Disonnecting");
					controller.end();
					return;
				}
			}
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "JVMAdapter.BreakPointWatcher";
		}
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vmadapter.VMAdapter#registerOperationCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMMethod)
	 */
	@Override
	public void registerOperationCallInterest(VMMethod m) {
		JVMMethod jvmMethod = (JVMMethod)m;
		
		if (jvmMethod.getMethod().location() == null) {
			controller.newLogMessage(this, Level.WARNING, "Cannot set breakpoints for abstract or interface operation " + m.toString());
			return;
		}
		
		BreakpointRequest req = monitoredVM.eventRequestManager().createBreakpointRequest(jvmMethod.getMethod().location());
		req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
		req.enable();
	}
	
	private void onUpdateAttribute(ObjectReference obj, Field field, com.sun.jdi.Value javaValue) {
    	
		controller.newLogMessage(this, Level.FINE, "updateAttribute: " + field.name());
    	controller.onUpdateAttribute(obj, field, getUSEValue(javaValue));
    	
    }
	
	private void onMethodCall(BreakpointEvent breakpointEvent) {
		if (breakpointEvent.location().method().isConstructor()) {
    		handleConstructorCall(breakpointEvent);
    	} else {    		
    		handleMethodCall(breakpointEvent);
    	}
    }
	
	private void onMethodExit(MethodExitEvent ex) {
		controller.onMethodExit(ex, ex.method());
	}
	
	private void handleConstructorCall(BreakpointEvent breakpointEvent) {
    	
    	controller.newLogMessage(this, Level.FINE, "onConstructorCall: " + breakpointEvent.location().method().toString());
        	
    	StackFrame currentFrame;
		try {
			ThreadReference thread = breakpointEvent.thread();
			currentFrame = thread.frame(0);
	    	// Check if we are a nested constructor of the same thisObject
			ObjectReference thisObject = currentFrame.thisObject();
			
			for (int index = 1; index < breakpointEvent.thread().frameCount(); ++index) {
				StackFrame frame = thread.frame(index);
				if (thisObject.equals(frame.thisObject()) && frame.location().method().isConstructor()) {
					controller.newLogMessage(this, Level.FINE, "Nested constructor call.");
					return;
				}
			}
		} catch (IncompatibleThreadStateException e) {
			controller.newLogMessage(this, Level.SEVERE, "Could not retrieve stack frame");
			return;
		}
		
    	ObjectReference javaObject = currentFrame.thisObject();    	
    	VMObject newInstance = new JVMObject(this, javaObject);
    	
    	controller.onNewVMObject(newInstance);
    }

	private void handleMethodCall(BreakpointEvent breakpointEvent) {
		Method m = breakpointEvent.location().method();
    	controller.newLogMessage(this, Level.FINE, "onMethodCall: " + m.toString());
    	
    	JVMMethod vmMethod = (JVMMethod)controller.getVMMethod(m); 
    	
    	JVMMethodCall call = new JVMMethodCall(this, vmMethod, breakpointEvent);
    	controller.onMethodCall(call);
    }

	/**
     * Returns the mapped USE value of <code>javaValue</code>.
     * The following mapping is done:
     * <ul>
     * <li><b>null</b> -> undefined</li>
     * <li><b>ObjectReference</b>
     * 	<ol>
     *    <li><code>to existing VM Object</code> -> ObjectValue</li>
     *    <li><code>java.lang.Integer</code> -> IntegerValue</li>
     *    <li><code>java.util.ArrayList</code> -> SequenceValue with mapped values (rec. invocation).</li>
     *    <li><code>java.util.HashSet</code> -> SetValue with mapped values (rec. invocation).</li>
     *    <li><code>java.util.TreeMap</code> -> SetValue with mapped key and values (rec. invocation) as <code>Tuple(key, value)</code>.</li>
     * 	</ol>
     * <li><b>ArrayReference</b> -> SequenceValue with mapped values (rec. invocation).</li>
     * <li><b>StringReference</b> -> StringValue</li>
     * <li><b>IntegerValue</b> -> IntegerValue</li>
     * <li><b>BooleanValue</b> -> BooleanValue</li>
     * 
     * </ul>
     * Java-value of type {@link ObjectReference} are tried to be mapped to
     * corresponding USE-objects. If no object is found <code>UndefinedValue</code>
     * is returned.
     * @param javaValue
     * @return
     */
    public Value getUSEValue(com.sun.jdi.Value javaValue) {
    	Value v = UndefinedValue.instance;
		
    	if (javaValue == null) return v;
    	
    	if (controller.existsVMObject(javaValue)) {
    		VMObject obj = controller.getVMObject(javaValue);
    		v = new ObjectValue(obj.getUSEObject().type(), obj.getUSEObject());
    	} else if (javaValue instanceof ArrayReference) {
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
			} else if (refValue.type().name().equals("java.util.TreeMap")) {
				List<Value> useValues = new LinkedList<Value>();
				
				// TreeMap uses nodes starting with the private field "map:root"
		    	Field field = refValue.referenceType().fieldByName("root");
		    	com.sun.jdi.Value root = refValue.getValue(field);
		    	
		    	TupleType.Part keyPart = new TupleType.Part("key", TypeFactory.mkOclAny());
		    	TupleType.Part valPart = new TupleType.Part("value", TypeFactory.mkOclAny());
		    	
		    	TupleType resType = TypeFactory.mkTuple(new TupleType.Part[]{keyPart, valPart});
		    	
		    	if (root != null) {
			    	ObjectReference currentNode = (ObjectReference)root;
			    	Field keyField   = currentNode.referenceType().fieldByName("key");
			    	Field valueField = currentNode.referenceType().fieldByName("value");
			    	Field leftField  = currentNode.referenceType().fieldByName("left");
			    	Field rightField = currentNode.referenceType().fieldByName("right");
			    	
			    	Value key;
			    	Value value;
			    	
			    	ObjectReference child;
			    	
			    	// Values are stored in the fields "left" and "right"
			    	Stack<ObjectReference> toDo = new Stack<ObjectReference>();
			    	toDo.add(currentNode);
			    	Map<String,Value> parts = new HashMap<String, Value>();
			    	
			    	while (!toDo.isEmpty()) { 
			    		currentNode = toDo.pop();
			    		
			    		key   = getUSEValue(currentNode.getValue(keyField));
			    		value = getUSEValue(currentNode.getValue(valueField));
			    		parts.put("key", key);
			    		parts.put("value", value);
			    		
			    		useValues.add(new TupleValue(resType, parts));
			    		
			    		child = (ObjectReference)currentNode.getValue(leftField);
			    		if (child != null)
			    			toDo.push(child);
			    		
			    		child = (ObjectReference)currentNode.getValue(rightField);
			    		if (child != null)
			    			toDo.push(child);
			    	}
			    }
		    	v = new SetValue(TypeFactory.mkVoidType(), useValues);
			} else {
				controller.newLogMessage(this, Level.WARNING, "Unhandled type:" + javaValue.toString());
			}
			
		} else {
			controller.newLogMessage(this, Level.WARNING, "Unhandled type:" + javaValue.toString());
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
			controller.newLogMessage(this, Level.WARNING, "Unhandled enum type:" + javaValue.type().name());
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
			controller.newLogMessage(this, Level.WARNING, e.getMessage());
		}
		
		return UndefinedValue.instance;
	}
	
	/**
	 * @param jvmType
	 * @return
	 */
	public Set<VMObject> readInstances(JVMType jvmType) {
		List<ObjectReference> instances = jvmType.getType().instances(getMaxInstances());
		
		if (instances.size() == getMaxInstances()) {
			controller.newLogMessage(
					this,
					Level.WARNING,
					"More than max instances of "
							+ StringUtil.inQuotes(jvmType.getName()) + " in JVM!");
		}
		
		Set<VMObject> vmObjects = new HashSet<VMObject>(instances.size());
		for (ObjectReference ref : instances) {
			JVMObject obj = createVMObject(ref);
			vmObjects.add(obj);
		}
		
		return vmObjects;
	}

	private JVMObject createVMObject(ObjectReference ref) {
		JVMObject obj = new JVMObject(this, ref);
		return obj;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JVMAdapter";
	}

	/**
	 * @param thread
	 */
	public VMObject getThisObjectForThread(ThreadReference thread) throws VMAccessException {
		StackFrame currentFrame;
		try {
			currentFrame = thread.frame(0);
		} catch (IncompatibleThreadStateException e) {
			throw new VMAccessException(e);
		}
		
    	ObjectReference javaObject = currentFrame.thisObject();
    	VMObject thisValue = controller.getVMObject(javaObject);
    	
    	return thisValue;
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerMethodExit(org.tzi.use.plugins.monitor.vm.mm.VMMethodCall)
	 */
	@Override
	public void registerMethodExit(VMMethodCall call) {
		JVMMethodCall jvmCall = (JVMMethodCall)call;
		
		MethodExitRequest req = monitoredVM.eventRequestManager().createMethodExitRequest();
		
		// JVMAdapter stores an ObjectReference as Id.
		ObjectReference instance;
		try {
			instance = (ObjectReference)call.getThisObject().getId();
		} catch (VMAccessException e) {
			controller.newLogMessage(this, Level.SEVERE, "Could not retrieve this object from VM.");
			return;
		}

		req.addInstanceFilter(instance);
		req.addThreadFilter(jvmCall.getBreakpointEvent().thread());
		req.enable();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#unregisterOperationeExit(java.lang.Object)
	 */
	@Override
	public void unregisterOperationeExit(Object adapterExitInformation) {
		MethodExitEvent exitEvent = (MethodExitEvent)adapterExitInformation;
		monitoredVM.eventRequestManager().deleteEventRequest(exitEvent.request());
		controller.newLogMessage(
				this,
				Level.FINE,
				"Removed exit operation request "
						+ StringUtil.inQuotes(exitEvent.request().toString()));
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#getMethodResultValue(java.lang.Object)
	 */
	@Override
	public Value getMethodResultValue(Object adapterExitInformation) {
		MethodExitEvent exitEvent = (MethodExitEvent)adapterExitInformation;
		return getUSEValue(exitEvent.returnValue());
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerConstructorCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMType)
	 */
	@Override
	public void registerConstructorCallInterest(VMType vmType) {
		JVMType type = (JVMType)vmType;
		
		for (Method m : type.getType().methods()) {
			if (m.isConstructor()) {
				controller.newLogMessage(this, Level.FINE, "Registering constructor " + m.toString());
				BreakpointRequest req = monitoredVM.eventRequestManager().createBreakpointRequest(m.location());
				req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
				req.enable();
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerFieldModificationInterest(org.tzi.use.plugins.monitor.vm.mm.VMField)
	 */
	@Override
	public void registerFieldModificationInterest(VMField f) {
		JVMField jvmField = (JVMField)f;
		ModificationWatchpointRequest req = monitoredVM.eventRequestManager()
				.createModificationWatchpointRequest(jvmField.getField());
		req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
		req.enable();
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#unregisterClassPrepareInterest(java.lang.Object)
	 */
	@Override
	public void unregisterClassPrepareInterest(Object adapterEventInformation) {
		ClassPrepareEvent e = (ClassPrepareEvent)adapterEventInformation;
		monitoredVM.eventRequestManager().deleteEventRequest(e.request());
		
		controller.newLogMessage(
				this,
				Level.FINE,
				"Removed ClassPrepareRequest " + StringUtil.inQuotes(e.request().toString()));
	}
}
