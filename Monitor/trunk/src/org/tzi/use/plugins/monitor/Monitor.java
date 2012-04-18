/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2010 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id$

package org.tzi.use.plugins.monitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MElementAnnotation;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MModelElement;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.mm.MPrePostCondition;
import org.tzi.use.uml.ocl.expr.ExpObjRef;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MOperationCall;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.StatementEvaluationResult;
import org.tzi.use.uml.sys.ppcHandling.DoNothingPPCHandler;
import org.tzi.use.uml.sys.ppcHandling.PPCHandler;
import org.tzi.use.uml.sys.ppcHandling.PostConditionCheckFailedException;
import org.tzi.use.uml.sys.ppcHandling.PreConditionCheckFailedException;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MEnterOperationStatement;
import org.tzi.use.uml.sys.soil.MExitOperationStatement;
import org.tzi.use.uml.sys.soil.MLinkDeletionStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;
import org.tzi.use.uml.sys.soil.MObjectDestructionStatement;
import org.tzi.use.uml.sys.soil.MRValue;
import org.tzi.use.util.Log;
import org.tzi.use.util.StringUtil;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.ArrayType;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.InvalidStackFrameException;
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
 * This class handles the monitoring of a Java application
 * via the remote debugger.
 * It connects to the virtual machine and keeps track of
 * operation calls and instance creation when attached.
 * 
 * @author Lars Hamann
 */
public class Monitor implements ChangeListener {
	/**
	 * The host name where the monitored VM is running on
	 */
	private String host;
	/**
	 * The port where the monitored VM is providing debugging events
	 */
	private int port;
	/**
	 * The USE session which provides information about the relevant
	 * classes and operations to listen for.
	 * The session is required (and not only the {@link MModel}), because
	 * the monitor has to react on changes like loading another model.
	 */
	private Session session;
	/**
	 * The monitored virtual machine
	 */
	private VirtualMachine monitoredVM = null;
	/**
	 * True when monitoring, e.g., USE monitor is connected
	 * to a VM
	 */
    private boolean isRunning = false;
    /**
	 * True when monitoring and the VM is paused
	 */
    private boolean isPaused = false;
    
    /**
     * The connector used to connect to the JVM.
     */
    private GenericAttachingConnector connector;
    
    /**
     * This thread handles breakpoint events. E.g. call of a method.
     */
    private Thread breakpointWatcher;
    
    /**
     * Saves the mapping between USE and Java objects.
     */
    private Map<ObjectReference, MObject> instanceMapping;
    
    /**
     * Has a snapshot been taken already?
     */
    private boolean hasSnapshot = false;
    
    /**
     * A lock to wait for user input after an operation has
     * failed the pre- or postcondition checks.
     */
    private Object failedOperationLock = new Object();
    
    /**
     * True if an operation call or return has failed.
     */
    private boolean hasFailedOperation = false;
    
    /**
     * For statistics: Number of read instances.
     */
    private int countInstances;
    
    /**
     * For statistics: Number of read links.
     */
    private int countLinks;
    
    /**
     * For statistics: Number of read attribute values.
     */
    private int countAttributes;
    
    /**
     * List of listeners interested in state changes of the monitor. 
     */
    private List<MonitorStateListener> stateListener = new LinkedList<MonitorStateListener>();
    
    /**
     * Collection of listeners that listen for the snapshot taking progress.
     */
    private List<ProgressListener> snapshotProgressListener = new LinkedList<ProgressListener>();
    
    /**
     * Collection of listeners that listen for messages.
     */
    private List<LogListener> logListener = new LinkedList<LogListener>();
    
    /**
     * When the monitor resets the system state, this variable is set to true.
     * Otherwise the state change event of the {@link Session} would force
     * a reset of the monitor.
     */
    private boolean isResetting = false;
    
    /**
     * The maximum number of instances which are read for a single type.
     */
    private long maxInstances = 10000;
    
    /**
     * When true, Soil statements are used for system state manipulation.
     * Otherwise the objects are created directly by using operations of
     * system and system state
     */
    private boolean useSoil = true;
    
    /**
     * Provides helper functions to get qualified names of
     * model elements.
     */
    private IdentifierMappingHelper mappingHelper;
    
    /**
     * A cache for the model classes to runtime types mappings.
     * A single model class can be represented by more then one
     * runtime type, because the runtime sub classes could be ignored
     * in the model.
     */
    private Map<MClass, Set<ReferenceType>> classMappings;
    
    /**
     * A cache for the qualified Java class name to
     * the corresponding USE class.
     * A single model class can be represented by more then one
     * runtime type, because the runtime sub classes could be ignored
     * in the model.
     */
    private Map<String, MClass> classNamesMap;
    
    /**
     * A map containing a collection of {@link ModelBreakpoint}s for
     * {@link MModelElement}s.  
     */
    // TODO: Model break points
    //private Map<MModelElement, Collection<ModelBreakpoint>> modelBreakPoints = new HashMap<MModelElement, Collection<ModelBreakpoint>>();
            
    public Monitor() { }
    
    /**
     * Returns the {@link MSystem} used by the monitor.
     * @return The <code>MSystem</code> used by this monitor.
     */
    private MSystem getSystem() {
    	return session.system();
    }
    
    /**
     * Configures the monitor to attach to the specified <code>host</code> on <code>port</code>
     * using the provided session.
     * <p>
     * If <code>host</code> is <code>null</code> or an empty string the current {@link MModel} is queried
     * for an annotation value <code>@Monitor(host="...")</code>. If no such annotation is present, <code>localhost</code>
     * is used.
     * </p> 
     * <p>
     * If <code>port</code> is <code>null</code> or an empty string the current {@link MModel} is queried
     * for an annotation value <code>@Monitor(port="...")</code>. If no such annotation is present <code>6000</code>
     * is used.
     * </p>
     * @param session The USE session to use for the monitoring process. The monitor reacts on state changes of the session.
     * @param host The host which runs a JVM with enabled remote debugger capabilities.
     * @param port The port the JVM is listening for remote debugger connections.
     * @throws IllegalArgumentException If an invalid port number is provided as an argument or specified in the model annotation.
     */
    public void configure(Session session, String host, String port) {
    	this.session = session;
    	this.session.addChangeListener(this);
    	this.mappingHelper = new IdentifierMappingHelper(session.system().model());
    	
    	MElementAnnotation modelAnnotation = getSystem().model().getAnnotation("Monitor");
    	
		if (host == null || host.equals("")) {
			if (modelAnnotation != null
					&& modelAnnotation.hasAnnotationValue("host")) {
				this.host = modelAnnotation.getAnnotationValue("host");
			} else {
				this.host = "localhost";
			}
		} else {
			this.host = host;
		}
    	
		if (port == null || port.equals("")) {
			if (modelAnnotation != null
					&& modelAnnotation.hasAnnotationValue("port")) {
				try {
					this.port = Integer.parseInt(modelAnnotation
							.getAnnotationValue("port"));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(
							"Invalid port specified in model annotation: "
									+ modelAnnotation
											.getAnnotationValue("port"));
				}
			} else {
				this.port = 6000;
			}
		} else {
			try {
				this.port = Integer.parseInt(port);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(port
						+ " is not a valid port number.");
			}
		}
    }
    
    public long getMaxInstances() {
		return maxInstances;
	}

	public void setMaxInstances(long newValue) {
		maxInstances = newValue;
	}
	
    /**
     * The current state of the monitor (<strong>not of the JVM!</strong>).<br/>
     * When a the monitor is connected to a JVM this getter returns <code>true</code>.
     * @return <code>true</code> if the monitor is connected.
     */
    public boolean isRunning() {
    	return this.isRunning;
    }
    
    /**
     * Returns <code>true</code> if the monitor is connected to a JVM and
     * the JVM is suspended.
     * @return <code>true</code> if the JVM is suspended.
     */
    public boolean isPaused() {
    	return this.isRunning && this.isPaused;
    }
    
    /**
     * Resets the session. Sets {@link #isResetting} to <code>true</code> before the reset and
     * <code>false</code> afterwards.
     */
    protected void doUSEReset() {
    	isResetting = true;
    	session.reset();
    	isResetting = false;
    }
    
    /**
     * Waits until {@link #failedOperationLock} is released.
     */
    private void waitForUserInput() {
    	synchronized (failedOperationLock) {
    		try {
    			isPaused = true;
		    	fireMonitorPause();
    			fireNewLogMessage(Level.FINER, "Waiting for user input.");
    			failedOperationLock.wait();
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
		}
    }
    
    /**
     * Starts the monitoring process.
     * In detail the following steps are executed:
     * <ol>
     *   <li>Reset of the USE session.</li>
     *   <li>Connect to the VM with the settings provided to {@link #configure(Session, String, String)}. </li>
     *   <li>Register for important events in the VM</li>
     *   <li>Resume the VM</li>
     *   <li>If <code>suspend</code> is <code>true</code> call {@link #pause(boolean)} without reseting.</li>
     * </ol> 
     * @param suspend If <code>true</code> the VM is suspended directly after a connection was established.
     */
    public void start(boolean suspend) {
    	// We need a clean system
    	doUSEReset();
    	this.instanceMapping = new HashMap<ObjectReference, MObject>();
    	this.hasSnapshot = false;
    	
    	try {
			attachToVM();
		} catch (MonitorException e) {
			fireNewLogMessage(Level.SEVERE, "Error connecting to the VM: " + e.getMessage());
			return;
		}
		
		registerClassPrepareEvents();
		
		registerOperationBreakPoints();
		
		isRunning = true;
				
		breakpointWatcher = new Thread(new BreakPointWatcher(), "Monitor breakpoint watcher");
		breakpointWatcher.start();
		
		monitoredVM.resume();
		isPaused = false;
		
		fireMonitorStart();
		
		if (suspend) {
			pause(false);
		}
    }

    /**
     * Pauses the monitored VM without reseting the USE session.
     */
    public void pause() {
    	pause(true);
    }
    
    /**
     * Pauses the monitored VM.
     * @param doReset If <code>true</code> the USE session is reseted (see {@link Session#reset()}).
     */
    protected void pause(boolean doReset) {
    	monitoredVM.suspend();
		
    	if (doReset) {
    		doUSEReset();
    	}
    	
		long start = System.currentTimeMillis();
		countInstances = 0;
		countLinks = 0;
		countAttributes = 0;
		
		fireNewLogMessage(Level.INFO, "Creating snapshot using" + (useSoil ? " SOIL statements." : " using system operations." ));
    	readInstances();
    	
    	long end = System.currentTimeMillis();
    	fireNewLogMessage(Level.INFO, String.format("Read %,d instances and %,d links in %,dms.", countInstances, countLinks, (end - start)));
    	
    	calculateCurrentCallStack();
    	
    	hasSnapshot = true;
    	isPaused = true;
    	fireMonitorPause();
    }

    private void calculateCurrentCallStack() {
		// Find the thread we are monitoring!
    	ThreadReference identifiedThread = null;
    	
    	for (ThreadReference threadRef : monitoredVM.allThreads()) {
    		try {
				for (StackFrame frame : threadRef.frames()) {
					if (instanceMapping.containsKey(frame.thisObject())) {
						identifiedThread = threadRef;
						break;
					}
				}
			} catch (IncompatibleThreadStateException e) {
				fireNewLogMessage(Level.SEVERE, "Could not retrieve stack frame: " + e.getMessage());
			}
    		
    		if (identifiedThread != null)
    			break;
    	}
    	
    	if (identifiedThread == null) {
    		fireNewLogMessage(Level.FINE, "Calculated call stack is empty.");
    		return;
    	}
    	
    	// Seek for matching operations in reverse order of the frame list
    	// First operation calls first.  
    	try {
			for (int index = identifiedThread.frameCount() - 1; index >= 0; --index) {
				StackFrame frame = identifiedThread.frame(index);
				Method method = frame.location().method();
				
				// Call on a known instance?
				if (instanceMapping.containsKey(frame.thisObject()) &&
					operationMappings.containsKey(method)) {
					MObject useObject = instanceMapping.get(frame.thisObject());
					MOperation useOperation = operationMappings.get(method);
					
					createOperationCall(useObject, useOperation, frame, false);
				}
			}
		} catch (IncompatibleThreadStateException e) {
			// I think, this cannot happen
			fireNewLogMessage(Level.SEVERE, "Could not retrieve stack frame of identified thread: " + e.getMessage());
			return;
		}
	}

    /**
     * Generates and executes a USE operation call from the provided values.
     * @param self The MObject representing self for the operation call. 
     * @param useOperation
     * @param frame
     * @param validatePreConditions
     */
	private boolean createOperationCall(MObject self, MOperation useOperation,
			StackFrame frame, boolean validatePreConditions) {
		
		List<com.sun.jdi.Value> javaArgs = frame.getArgumentValues();
    	Map<String, Expression> arguments = new HashMap<String, Expression>();
    	
    	int numArgs = useOperation.allParams().size();
    	for (int index = 0; index < numArgs; index++) {
    		Value val = getUSEValue(javaArgs.get(index), useOperation.allParams().get(index).type());
    		arguments.put(useOperation.allParams().get(index).name(), new ExpressionWithValue(val));
    	}
    	
    	PPCHandler handler = (validatePreConditions ? ppcHandler : DoNothingPPCHandler.getInstance());
    	
		MEnterOperationStatement operationCall = new MEnterOperationStatement(
				new ExpObjRef(self), useOperation, arguments, handler );
    	
    	try {
    		StatementEvaluationResult result = getSystem().evaluateStatement(operationCall);
    		if (result.wasSuccessfull()) {
    			StringBuilder message = new StringBuilder("USE operation call ");
    			message.append(self.name()).append(".").append(useOperation.name()).append("(");
    			StringUtil.fmtSeq(message, arguments.values(), ",");
    			message.append(") was succesfull.");
    			fireNewLogMessage(Level.INFO,  message.toString());
    		}
		} catch (MSystemException e) {
			fireNewLogMessage(Level.SEVERE, e.getMessage());
			return false;
		}
		
		MethodExitRequest req = monitoredVM.eventRequestManager().createMethodExitRequest();
		req.addInstanceFilter(frame.thisObject());
		req.enable();
		
		return true;
	}

	/**
     * Resumes the monitoring process, i. e., resumes the suspended VM.
     */
    public void resume() {
    	synchronized (failedOperationLock) {
    		if (hasFailedOperation) {
        		failedOperationLock.notify();
        		hasFailedOperation = false;
        	}
		}
    	
    	monitoredVM.resume();
    	isPaused = false;
    	
    	fireNewLogMessage(Level.INFO, "VM resumed.");
    	fireMonitorResume();
    }
    
    /**
     * Ends the monitoring process by closing the connection to the VM.
     * The USE system state is kept untouched.
     */
    public void end() {
    	if (monitoredVM != null)
    		monitoredVM.dispose();
    	
    	instanceMapping = null;
    	isRunning = false;
    	isPaused = false;
    	
    	fireNewLogMessage(Level.INFO, "The monitor was disconnected. The snapshot is still available.");
    	fireMonitorEnd();
    }
    
    /**
     * Returns <code>true</code>, if a snapshot has been taken since the last call to {@link #start(boolean)}.
     * @return <code>true</code>, if a snapshot has been taken, <code>false</code> otherwise.
     */
    public boolean hasSnapshot() {
    	return hasSnapshot;
    }
    
    /**
     * 
     * @throws MonitorException
     */
    private void attachToVM() throws MonitorException {
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
		
    	fireNewLogMessage(Level.INFO, "Connected to virtual machine");
	}
    
	private void registerClassPrepareEvents() {
		for (MClass cls : getSystem().model().classes()) {
			String javaClassName = mappingHelper.getJavaClassName(cls);
			
			ClassPrepareRequest req = monitoredVM.eventRequestManager().createClassPrepareRequest();
			req.addClassFilter(javaClassName);
			req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
			req.enable();
		}
	}
	
	/**
	 * Sets a break point for each defined USE operation (if it matches a Java method) 
	 * of all currently loaded classes in the VM.
	 */
    private void registerOperationBreakPoints() {
    	for (MClass cls : getSystem().model().classes()) {
    		try {
    			registerOperationBreakPoints(cls);
    		} catch (Exception e) {
    			fireNewLogMessage(Level.SEVERE, "Error while setting break points for class " + cls.name());
    		}
    	}
    }

    private Map<Method, MOperation> operationMappings = new HashMap<Method, MOperation>();
    
    /**
	 * Sets a break point for each defined USE operation (if it matches a Java method) 
	 * of the specified USE class, if the corresponding Java class 
	 * is loaded classes in the VM.
	 */
	private void registerOperationBreakPoints(MClass cls) {
		ReferenceType refType = getReferenceClass(cls);
		  
		if (refType == null) {
			fireNewLogMessage(Level.INFO, "No runtime class found for model class " + cls.name() + ", yet.");
		} else {
			fireNewLogMessage(Level.FINE, "Registering operation breakpoints for class " + cls.name());
			for (MOperation op : cls.operations()) {
				String isQuery = op.getAnnotationValue("Monitor", "isQuery");
				if (isQuery.equals("true")) continue;
				
				String methodName = mappingHelper.getJavaMethodName(op);
				
				List<com.sun.jdi.Method> methods = refType.methodsByName(methodName);
				for (com.sun.jdi.Method m : methods) {
					// TODO: Check parameter types
					
					try {
						if (m.argumentTypes().size() == op.allParams().size()) {
							fireNewLogMessage(Level.FINE, "Registering operation breakpoint for operation " + m.toString());
							
							if (m.location() == null) {
								fireNewLogMessage(Level.WARNING, "Cannot set breakpoints for abstract or interface operation " + m.toString());
								continue;
							}
							
							BreakpointRequest req = monitoredVM.eventRequestManager().createBreakpointRequest(m.location());
							req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
							req.enable();
							operationMappings.put(m, op);
						}
					} catch (ClassNotLoadedException e) {
						fireNewLogMessage(Level.SEVERE, "Could not validate number of arguments for method " + m.toString());
					}
				}
			}
			
			// Breakpoints for constructors
			for (Method m : refType.methods()) {
				if (m.isConstructor()) {
					fireNewLogMessage(Level.FINE, "Registering constructor " + m.toString());
					BreakpointRequest req = monitoredVM.eventRequestManager().createBreakpointRequest(m.location());
					req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
					req.enable();
				}
			}
			
			for (MAttribute a : cls.attributes()) {
				String aName = mappingHelper.getJavaFieldName(a);
				Field f = refType.fieldByName(aName);
				if (f == null) {
					fireNewLogMessage(Level.WARNING, "Unknown attribute " + StringUtil.inQuotes(a.name()));
					continue;
				}
				
				ModificationWatchpointRequest req = monitoredVM.eventRequestManager().createModificationWatchpointRequest(f);
				req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
				req.enable();
			}
			
			// Association ends with multiplicity 1 can be handled also
			for (Map.Entry<String, MNavigableElement> end : cls.navigableEnds().entrySet()) {
				if (!end.getValue().isCollection()) {
					Field f = refType.fieldByName(end.getKey());
					if (f != null) {
						ModificationWatchpointRequest req = monitoredVM.eventRequestManager().createModificationWatchpointRequest(f);
						req.setSuspendPolicy(EventRequest.SUSPEND_NONE);
						req.enable();
					}
				}
			}
		}
	}

	private void registerOperationBreakPoints(ReferenceType refType) {
		// Get the last element of the qualified name
		String name = refType.name();
		name = name.substring(name.lastIndexOf(".") + 1);
		
		MClass cls = getSystem().model().getClass(name);
		if (cls != null) {
			registerOperationBreakPoints(cls);
		}
	}
	
	private ReferenceType getReferenceClass(MClass cls) {
    	
    	List<ReferenceType> classes = monitoredVM.classesByName(mappingHelper.getJavaClassName(cls));
    	
    	if (classes.size() == 1) {
    		return classes.get(0);
    	} else {
    		return null;
    	}
    }

	/**
	 * Constructs a map which maps each use class
	 * to the reference types that must be read. 
	 */
	private void setupClassMappings() {
		Collection<MClass> useClasses = getSystem().model().classes();
		classMappings = new HashMap<MClass, Set<ReferenceType>>(useClasses.size());
		
		// Build a map of Java names to USE classes
		classNamesMap = new HashMap<String, MClass>(useClasses.size());
		
		for (MClass useClass : useClasses) {
			if (!useClass.getAnnotationValue("Monitor", "ignore").equals("true")) {
				classNamesMap.put(mappingHelper.getJavaClassName(useClass), useClass);
				classMappings.put(useClass, new HashSet<ReferenceType>());
			}
		}
		
		//TODO: Use monitoredVM.classesByName(className)
		for (ReferenceType type : monitoredVM.allClasses()) {
			if (!(type instanceof ClassType)) continue;

			ClassType cType = (ClassType)type;
			
			if (classNamesMap.containsKey(cType.name())) {
				MClass useClass = classNamesMap.get(cType.name());
				Set<ReferenceType> javaClasses = classMappings.get(useClass);
				javaClasses.add(cType);
				
				if (useClass.getAnnotationValue("Monitor", "ignoreSubclasses").equals("true"))
					continue;

				Stack<ClassType> toDo = new Stack<ClassType>();
				toDo.push(cType);
				while (!toDo.isEmpty()) {
					ClassType toCheck = toDo.pop();
					for (ClassType subType : toCheck.subclasses()) {
						if (!classNamesMap.containsKey(subType.name())) {
							javaClasses.add(subType);
							classNamesMap.put(subType.name(), useClass);
							toDo.push(subType);
						}
					}
				}
			}
		}
	}
	
	private void readInstances() {
		Collection<MClass> classes = getSystem().model().classes();
		
		fireSnapshotStart("Initializing...", classes.size());
		
		instanceMapping = new HashMap<ObjectReference, MObject>();
		setupClassMappings();
		
		long start = System.currentTimeMillis();
		ProgressArgs args = new ProgressArgs("Reading instances", 0, classes.size());
		// Create current system state
    	for (MClass cls : classes) {
    		fireSnapshotProgress(args);
    		if (!cls.getAnnotationValue("Monitor", "ignore").equals("true")) {
    			readInstances(cls);
    		}
    		args.setCurrent(args.getCurrent() + 1);
    	}
    	
    	long end = System.currentTimeMillis();
    	long duration = (end - start);
    	long instPerSecond = Math.round((double)countInstances / ((double)duration / 1000));
		
		fireNewLogMessage(Level.INFO, String.format(" Created %,d instances in %,dms (%,d instances/s).", countInstances, duration, instPerSecond));
    	
    	readAttributtesAndLinks();
    	classMappings = null;
    	
    	fireSnapshotEnd();
	}
	
    private void readInstances(MClass cls) {
    	
    	// Find all subclasses of the reference type which are not modeled in
    	// the USE file, because instances() only returns concrete instances
    	// of a type (and not of subclasses) 
    	Set<ReferenceType> typesToRead = classMappings.get(cls);

    	if (typesToRead.isEmpty()) {
    		fireNewLogMessage(Level.FINE, "Java class "
					+ StringUtil.inQuotes(mappingHelper.getJavaClassName(cls))
					+ " could not be found for USE class "
					+ StringUtil.inQuotes(cls.name())
					+ ". Maybe not loaded, yet.");
			return;
    	}

		for (ReferenceType refType : typesToRead) {
	    	List<ObjectReference> refInstances = refType.instances(getMaxInstances());
			
			for (ObjectReference objRef : refInstances) {
				if (objRef.isCollected()) continue;
				
				try {
					createInstance(cls, objRef);
					++countInstances;
				} catch (MSystemException e) {
					Log.error(e);
					return;
				}
			}
		}
    }

	/**
	 * Creates a new instance of the given class and
	 * adds a mapping to the runtime instance. 
	 * @param cls The use class of the new object
	 * @param objRef The runtime instance
	 * @return The new instance
	 * @throws MSystemException
	 */
	protected MObject createInstance(MClass cls, ObjectReference objRef)
			throws MSystemException {
		if (useSoil) {
			MNewObjectStatement stmt = new MNewObjectStatement(cls);
			getSystem().evaluateStatement(stmt);
			instanceMapping.put(objRef, stmt.getCreatedObject());
			return stmt.getCreatedObject();
		} else {
			String name = getSystem().state().uniqueObjectNameForClass(cls);
			MObject o = getSystem().state().createObject(cls, name);
			instanceMapping.put(objRef, o);
			return o;
		}
	}
    
	/**
     * Creates MDeleteStatements for garbage collected objects.
     */
    private void checkForDeletedInstances() {
    	// Need an iterator to be able to remove elements
    	Iterator<ObjectReference> iter = this.instanceMapping.keySet().iterator();
    	
		while (iter.hasNext()) {
			ObjectReference refObject = iter.next();
			if (refObject.isCollected()) {
				MObject useObj = instanceMapping.get(refObject);
				
				ObjectValue valObject = new ObjectValue(useObj.type(), useObj); 
				MObjectDestructionStatement delStmt = new MObjectDestructionStatement(valObject);
				try {
					this.getSystem().evaluateStatement(delStmt);
				} catch (MSystemException e) {
					this.fireNewLogMessage(Level.INFO, "Error executing delete statement: " + e.getMessage());
				}
				
				// Supported by KeySet
				iter.remove();
				
				this.fireNewLogMessage(Level.INFO, "The object " + StringUtil.inQuotes(useObj) + " was destroyed.");
			}
		}
	}
    
    /**
     * Reads all attributes of all read instances.
     * Must be done after instance creation to allow
     * link creation.
     */
    private void readAttributtesAndLinks() {
    	long start = System.currentTimeMillis();

    	int progressEnd = instanceMapping.size() * 2;
    	fireSnapshotStart("Reading attributes and links...", progressEnd);
    	ProgressArgs args = new ProgressArgs("Reading attributes", progressEnd);
    	// Maximum number of progress calls 50
    	int step = progressEnd / 50;
    	int counter = 0;
    	
    	for (Map.Entry<ObjectReference, MObject> entry : instanceMapping.entrySet()) {
    		readAttributes(entry.getKey(), entry.getValue());
    		
    		if (step > 0 && counter % step == 0) {
    			args.setCurrent(counter);
    			fireSnapshotProgress(args);
    		}
    		counter++;
    	}
    	    	
    	long end = System.currentTimeMillis();
    	long duration = (end - start);
		fireNewLogMessage(Level.INFO, String.format(
				" Setting %,d attributes took %,dms (%,.0f attributes/s).",
				countAttributes, duration, (double) countAttributes
						/ (duration / 1000)));
		
		start = System.currentTimeMillis();
		
		args.setDescription("Reading links");
    	for (Map.Entry<ObjectReference, MObject> entry : instanceMapping.entrySet()) {
    		readLinks(entry.getKey(), entry.getValue());
    		
    		if (step > 0 && counter % step == 0) {
    			args.setCurrent(counter);
    			fireSnapshotProgress(args);
    		}
    		counter++;
    	}
    	
    	fireSnapshotEnd();
    	
    	end = System.currentTimeMillis();
    	duration = (end - start);
		fireNewLogMessage(Level.INFO, String.format(
				" Creating %,d links took %,dms (%,.0f links/s).", countLinks,
				duration, ((double) countLinks) / (duration / 1000)));
    }
    
    private void updateAttribute(ObjectReference obj, Field field, com.sun.jdi.Value javaValue) {
    	if (!hasSnapshot()) return;
    	fireNewLogMessage(Level.FINE, "updateAttribute: " + field.name());

    	MObject useObject = instanceMapping.get(obj);
    	
    	if (useObject == null) {
			fireNewLogMessage(
					Level.WARNING,
					"No coresponding USE-object found to set value of attribute "
							+ StringUtil.inQuotes(field.name())
							+ " to "
							+ StringUtil
									.inQuotes(javaValue == null ? "undefined"
											: javaValue.toString()));
			return;
    	}
    	
    	MAttribute attr = mappingHelper.getUseAttribute(useObject.cls(), field.name());
    	    	
    	if (attr == null) {
    		// Link end?
    		MNavigableElement end = useObject.cls().navigableEnd(field.name());
    		if (end != null && !end.isCollection()) {
    			// Destroy possible existing link
    			List<MAssociationEnd> ends = new ArrayList<MAssociationEnd>(end.association().associationEnds());
    			ends.remove(end);
    			
    			List<MObject> objects = getSystem().state().getNavigableObjects(useObject, ends.get(0), end, Collections.<Value>emptyList());
    			
    			if (objects.size() > 0) {
    				// Align objects to USE specification
    				MObject[] linkObjects = new MObject[2];
    				if (end.association().associationEnds().get(0).equals(end) ) {
						linkObjects[0] = objects.get(0);
						linkObjects[1] = useObject;
					} else {
						linkObjects[0] = useObject;
						linkObjects[1] = objects.get(0);
					}
    				
    				//FIXME: Qualifier values empty
					MLinkDeletionStatement delStmt = new MLinkDeletionStatement(
							end.association(), linkObjects,
							Collections.<List<MRValue>> emptyList());
    				
	    			try {
	    				getSystem().evaluateStatement(delStmt);
					} catch (MSystemException e) {
						fireNewLogMessage(
								Level.WARNING,
								"Link of association "
										+ StringUtil.inQuotes(end.association())
										+ " could not be deleted. Reason: "
										+ e.getMessage());
						return;
					}
    			}
    			
    			// Create link if needed
				if (javaValue != null) {
					
					Value newValueV = getUSEObject(javaValue);
					if (newValueV.isUndefined()) return;
					
					MObject newValue = ((ObjectValue)newValueV).value();
					MObject[] linkObjects = new MObject[2];
					
					if (end.association().associationEnds().get(0).equals(end) ) {
						linkObjects[0] = newValue;
						linkObjects[1] = useObject;
					} else {
						linkObjects[0] = useObject;
						linkObjects[1] = newValue;
					}
					
					try {
						MLinkInsertionStatement createStmt = new MLinkInsertionStatement(
								end.association(), linkObjects, Collections.<List<Value>>emptyList()
						);
						getSystem().evaluateStatement(createStmt);
					} catch (MSystemException e) {
						fireNewLogMessage(Level.WARNING, "Could not create new link:" + e.getMessage());
					}
				}
    		}
    	} else {
    		Value v = getUSEValue(javaValue, attr.type());
			MAttributeAssignmentStatement stmt = new MAttributeAssignmentStatement(
					new ExpObjRef(useObject), attr, v);
    		
			try {
				getSystem().evaluateStatement(stmt);
			} catch (MSystemException e) {
				fireNewLogMessage(Level.WARNING, "Attribute " + StringUtil.inQuotes(attr.toString()) + " could not be set!");
			}
    	}
    }
    
    /**
     * Checks the {@link ReferenceType} of <code>objRef</code> for the attributes
     * of the USE class of <code>o</code>. If an attribute is found the value of the 
     * USE attribute is set. After that all associations the USE class is participating
     * in are checked for rolenames matching an attribute name. For each match corresponding
     * links are created.  
     * @param objRef The Java instance to read the values from 
     * @param o The use object to set the values for.
     */
    private void readAttributes(ObjectReference objRef, MObject o) {
    	for (MAttribute attr : o.cls().allAttributes()) {
    		
    		if (!readSpecialAttributeValue(objRef, o, attr)) {
	    		Field field;
	    		field = objRef.referenceType().fieldByName(mappingHelper.getJavaFieldName(attr));
	    			    		
	    		if (field != null) {
	    			com.sun.jdi.Value val = objRef.getValue(field);
	    			Value v = getUSEValue(val, attr.type());
	    			
	    			try {
	    				if (useSoil) {
	    					MAttributeAssignmentStatement stmt = 
	    						new MAttributeAssignmentStatement(o, attr, v);
	    					getSystem().evaluateStatement(stmt);
	    				} else {
	    					o.state(getSystem().state()).setAttributeValue(attr, v);
	    				}
	    				++countAttributes;
	    			} catch (IllegalArgumentException e) {
	    				fireNewLogMessage(Level.SEVERE, "Error setting attribute value: " + e.getMessage());
	    			} catch (MSystemException e) {
	    				fireNewLogMessage(Level.SEVERE, "Error setting attribute value: " + e.getMessage());
					}
	    		}
    		}
    	}
    }

    /**
     * Checks the attribute for special annotations and reads the corresponding
     * value if such an annotation is present, e. g., <code>@Monitor(value="classname")</code> 
     * for the name of the instance type.
     * @param objRef
     * @param o
     * @param attr
     * @return <code>true</code>, if the attribute <code>attr</code> has a special value  
     */
    private boolean readSpecialAttributeValue(ObjectReference objRef,
			MObject o, MAttribute attr) {
    	
    	String annotation = attr.getAnnotationValue("Monitor", "value");
    	if (annotation.equals("")) return false;
    	
		Value v = UndefinedValue.instance; 
		if (annotation.equals("classname")) {
			v = new StringValue(objRef.referenceType().name());
		} else {
			v = new StringValue("undefined special value " + StringUtil.inQuotes(annotation));
		}

		o.state(getSystem().state()).setAttributeValue(attr, v);
		
		return true;
	}

    private Value getUSEObject(com.sun.jdi.Value javaValue) {
    	if (javaValue instanceof ObjectReference) {
    		// Only search for an instance
    		MObject useObject = instanceMapping.get((ObjectReference)javaValue);
    		
    		if (useObject != null)
    			return new ObjectValue(useObject.type(), useObject);
    	}
    	
		fireNewLogMessage(Level.WARNING, "USE object for Java value " + javaValue.toString() + " not found!");
		return UndefinedValue.instance;
    }
    
	/**
     * Returns the mapped USE value of <code>javaValue</code>.
     * Java-value of type {@link ObjectReference} are tried to be mapped to
     * corresponding USE-objects. If no object is found <code>UndefinedValue</code>
     * is returned.
     * @param javaValue
     * @return
     */
    private Value getUSEValue(com.sun.jdi.Value javaValue, Type expectedType) {
    	Value v = UndefinedValue.instance;
		
    	if (javaValue == null) return v;
    	
    	if (expectedType.isObjectType()) {
    		v = getUSEObject(javaValue);
    	} else if (javaValue instanceof ArrayReference) {
    		//FIXME: Correct handling of array parameter!
    		ArrayReference javaArray = (ArrayReference)javaValue;
    		List<com.sun.jdi.Value> javaValues = javaArray.getValues();
    		Value[] useValues = new Value[javaValues.size()];
    				
    		for (int i = 0; i < javaValues.size(); ++i) {
    			useValues[i] = getUSEValue(javaValues.get(i), expectedType);
    		}
    		
    		SequenceValue result = new SequenceValue(TypeFactory.mkVoidType(), useValues);
    		
    		v = result;
    		    		
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
			} else if (expectedType.isEnum()) {
				v = getUSEEnumValue((EnumType)expectedType, javaValue);
			} else {		
				// Could be an instance 
				MObject useObject = instanceMapping.get(((ObjectReference)javaValue));
				if (useObject != null)
					v = new ObjectValue(useObject.type(), useObject);
				else
					fireNewLogMessage(Level.WARNING, "Unhandled type:" + javaValue.getClass().getName());
			}
			
		} else {
			fireNewLogMessage(Level.WARNING, "Unhandled type:" + javaValue.getClass().getName());
		}
		
		return v;
    }
    
	/**
	 * @param expectedType
	 * @param javaValue
	 * @return
	 */
	private Value getUSEEnumValue(EnumType expectedType, com.sun.jdi.Value javaValue) {
		if (!(javaValue instanceof ObjectReference))
			return UndefinedValue.instance;
		
		ObjectReference enumValue = (ObjectReference)javaValue;
		Field nameField = enumValue.referenceType().fieldByName("name");
		
		StringReference enumLiteral = (StringReference)enumValue.getValue(nameField);
		String litString = enumLiteral.value();
		
		try {
			EnumValue v = new EnumValue(expectedType, litString);
			return v;
		} catch (IllegalArgumentException e) {
			fireNewLogMessage(Level.WARNING, e.getMessage());
		}
		
		return UndefinedValue.instance;
	}

	private void readLinks(ObjectReference objRef, MObject o) {
		Set<MClass> allClasses = new HashSet<MClass>(o.cls().allParents());
		allClasses.add(o.cls());
		
		for (MClass cls : allClasses) {
			for (MAssociation ass : cls.associations()) {
				if (ass instanceof MAssociationClass) {
					// FIXME: Consider association classes
					continue;
				}
				
				List<MNavigableElement> reachableEnds = ass.navigableEndsFrom(cls);
			
    			// Check if object has link in java vm
        		for (MNavigableElement reachableElement : reachableEnds) {
        			MAssociationEnd reachableEnd = (MAssociationEnd)reachableElement;
        			
					if (reachableEnd.getAnnotationValue("Monitor", "ignore")
							.equalsIgnoreCase("true"))
        				continue;
        			
        			if (reachableEnd.multiplicity().isCollection()) {
        				readLinks(objRef, o, reachableEnd);
        			} else {
        				readLink(objRef, o, reachableEnd);
        			}
        		}
    		}
    	}
	}
    
    private void readLink(ObjectReference objRef, MObject source, MAssociationEnd end) {
    	Field field = objRef.referenceType().fieldByName(mappingHelper.getJavaFieldName(end));
    	
    	if (field == null) {
    		return;
    	}
    	
    	// Get the referenced object
    	com.sun.jdi.Value fieldValue = objRef.getValue(field);
    	if (fieldValue instanceof ArrayReference) {
    		ArrayReference arrayRef = (ArrayReference)fieldValue;
    		List<org.tzi.use.uml.ocl.value.Value> qualifierValues;
    		ObjectReference refTarget = null;
    			
    		try {
    			List<com.sun.jdi.Value> arrayValues = arrayRef.getValues();
    			
				for (int index1 = 0; index1 < arrayValues.size(); index1++) {
					com.sun.jdi.Value elementValue = arrayValues.get(index1);
					
					if (elementValue instanceof ArrayReference) {
						List<com.sun.jdi.Value> arrayValues2 = ((ArrayReference)elementValue).getValues();
						for (int index2 = 0; index2 < arrayValues2.size(); index2++) {
							com.sun.jdi.Value elementValue2 = arrayValues2.get(index2);
							
							qualifierValues = new ArrayList<org.tzi.use.uml.ocl.value.Value>(2);
							qualifierValues.add(org.tzi.use.uml.ocl.value.IntegerValue.valueOf(index1));
							qualifierValues.add(org.tzi.use.uml.ocl.value.IntegerValue.valueOf(index2));
							refTarget = (ObjectReference)elementValue2;
							
							if (refTarget != null) {
					    		MObject target = instanceMapping.get(refTarget);

					    		if (target != null) {
					    			createLink(source, end, target, qualifierValues);
					    		}
					    	}							
						}
					} else {
						refTarget = (ObjectReference)elementValue;
						if (refTarget != null) {
							qualifierValues = new ArrayList<org.tzi.use.uml.ocl.value.Value>(1);
							qualifierValues.add(org.tzi.use.uml.ocl.value.IntegerValue.valueOf(index1));
							
				    		MObject target = instanceMapping.get(refTarget);

				    		if (target != null) {
				    			createLink(source, end, target, qualifierValues);
				    		}
				    	}
					}
				}
			} catch (Exception e) {
				fireNewLogMessage(Level.SEVERE, "ERROR: " + e.getMessage()); 
			}
    		
    	} else {
	    	ObjectReference referencedObject = (ObjectReference)objRef.getValue(field);
	    	
	    	if (referencedObject != null) {
	    		MObject target = instanceMapping.get(referencedObject);
	    		if (target != null) {
	    			createLink(source, end, target);
	    		}
	    	}
    	}
    }

	private void createLink(MObject source, MAssociationEnd end, MObject target) {
		createLink(source, end, target, Collections.<org.tzi.use.uml.ocl.value.Value>emptyList());
	}
	
	private void createLink(MObject source, MAssociationEnd end, MObject target, 
							List<org.tzi.use.uml.ocl.value.Value> qualifierValues) {
		List<MObject> linkedObjects = new ArrayList<MObject>();
		if (end.association().associationEnds().indexOf(end) == 0) {
			linkedObjects.add(target);
			linkedObjects.add(source);
		} else {
			linkedObjects.add(source);
			linkedObjects.add(target);
		}

		List<List<org.tzi.use.uml.ocl.value.Value>> qv = new ArrayList<List<org.tzi.use.uml.ocl.value.Value>>();
		if (end.association().associationEnds().get(0).hasQualifiers()) {
			qv.add(qualifierValues);
			qv.add(Collections.<org.tzi.use.uml.ocl.value.Value>emptyList());
		} else {
			qv.add(Collections.<org.tzi.use.uml.ocl.value.Value>emptyList());
			qv.add(qualifierValues);
		}
		
		// Maybe the link is already created by reading the other instance
		try {
			if (!getSystem().state().hasLink(end.association(), linkedObjects, qv)) {
				if (useSoil) {
					MLinkInsertionStatement stmt = 
						new MLinkInsertionStatement(end.association(), linkedObjects.toArray(new MObject[2]), qv);
					getSystem().evaluateStatement(stmt);
				} else {
					getSystem().state().createLink(end.association(), linkedObjects, qv);
				}
				++countLinks;
			}
		} catch (MSystemException e) {
			fireNewLogMessage(Level.SEVERE, "Link could not be created! " + e.getMessage());
		}
	}
    
    private void readLinks(ObjectReference objRef, MObject o, MAssociationEnd end) {
    	Field field = objRef.referenceType().fieldByName(end.nameAsRolename());
    	
    	if (field == null) {
    		return;
    	}
    	
    	// Get the referenced objects
    	ObjectReference objects = (ObjectReference)objRef.getValue(field);
    	
    	if (objects == null) {
    		return;
    	}
    	
    	if (objects.type() instanceof ClassType) {
    		ClassType collectionType = (ClassType)objects.type();
    	
    		if (collectionType.name().equals("java.util.HashSet")) {
    			readLinksHashSet(objects, o, end);
    		} else if (collectionType.name().equals("java.util.ArrayList")) {
    			readLinksArrayList(objects, o, end);
    		} else {
				fireNewLogMessage(
						Level.SEVERE,
						"Multi-valued association end "
								+ StringUtil.inQuotes(end.toString())
								+ " is implemented as "
								+ StringUtil.inQuotes(collectionType.name())
								+ " which is currently not supported");
    		}
    	} else if (objects.type() instanceof ArrayType) {
    		readQualifiedLinks(objects, o, end);
    	}
    	
    }
    
    private void readQualifiedLinks(ObjectReference array, MObject o, MAssociationEnd end) {
    
    }
    
    private void readLinksArrayList(ObjectReference arrayList, MObject o, MAssociationEnd end) {

    	if (!(end.isOrdered() || end.hasQualifiers())) {
			fireNewLogMessage(
					Level.WARNING,
					"Association end "
							+ StringUtil.inQuotes(end.toString())
							+ " is implemented as an ArrayList but not marked as {ordered} nor it defines qualifiers.");
    	}
    	
    	// ArrayList uses the private field "elementData:Object" to store the values
    	Field field = arrayList.referenceType().fieldByName("elementData");
    	ArrayReference elements = (ArrayReference)arrayList.getValue(field);
    	
    	List<com.sun.jdi.Value> mapEntries = elements.getValues();
    	int index = 0;
    	
    	for (com.sun.jdi.Value value : mapEntries) {
    		if (value != null) {
    			ObjectReference entry = (ObjectReference)value;
    			createLink(o, end, instanceMapping.get(entry));
    		}
    	}
    }

    
    private void readLinksHashSet(ObjectReference hashset, MObject o, MAssociationEnd end) {
    	// HashSet uses the private field "map:HashMap" to store the values
    	Field field = hashset.referenceType().fieldByName("map");
    	ObjectReference mapValue = (ObjectReference)hashset.getValue(field);
    	
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
    			if (instanceMapping.containsKey(referencedObject)) {
    				createLink(o, end, instanceMapping.get(referencedObject));
    			}
    			
    			ObjectReference nextEntry = (ObjectReference)mapEntry.getValue(fieldNext);
    			while (nextEntry != null) {
    				referencedObject = (ObjectReference)nextEntry.getValue(fieldKey);
    				if (instanceMapping.containsKey(referencedObject)) {
        				createLink(o, end, instanceMapping.get(referencedObject));
        			}
    				nextEntry = (ObjectReference)nextEntry.getValue(fieldNext);
    			}
    		}
    	}
    }
    
    private boolean onMethodCall(BreakpointEvent breakpointEvent) {
    	if (breakpointEvent.location().method().isConstructor()) {
    		return handleConstructorCall(breakpointEvent);
    	} else {
    		if (!hasSnapshot()) return true;
    		
    		return handleMethodCall(breakpointEvent);
    	}
    }
    
    private boolean handleConstructorCall(BreakpointEvent breakpointEvent) {
    	fireNewLogMessage(Level.FINE, "onConstructorCall: " + breakpointEvent.location().method().toString());
        	
    	StackFrame currentFrame;
		try {
			ThreadReference thread = breakpointEvent.thread();
			currentFrame = thread.frame(0);
	    	// Check if we are a nested constructor of the same thisObject
			ObjectReference thisObject = currentFrame.thisObject();
			
			for (int index = 1; index < breakpointEvent.thread().frameCount(); ++index) {
				StackFrame frame = thread.frame(index);
				if (thisObject.equals(frame.thisObject()) && frame.location().method().isConstructor()) {
					fireNewLogMessage(Level.FINE, "Nested constructor call.");
					return true;
				}
			}
		} catch (IncompatibleThreadStateException e) {
			fireNewLogMessage(Level.SEVERE, "Could not retrieve stack frame");
			return true;
		}
		
    	ObjectReference javaObject = currentFrame.thisObject();
    	MClass cls = getUSEClass(javaObject);
    	
    	try {
			MObject newObject = createInstance(cls, javaObject);
			fireNewLogMessage(Level.INFO, "New object " + newObject.name() + ":" + newObject.cls().name() + " created.");
		} catch (MSystemException e) {
			fireNewLogMessage(Level.SEVERE, "USE object for new instance of type " + javaObject.type().name() + " could not be created.");
			return true;
		}
		
		return true;
    }
    
    /**
	 * @param javaObject
	 * @return
	 */
	private MClass getUSEClass(ObjectReference javaObject) {
		// Try to find a class by the default name
		String className = javaObject.type().name();
		fireNewLogMessage(Level.FINE, "getUSEClass: " + className);
		
		MClass cls = this.classNamesMap.get(className);
		
		fireNewLogMessage(Level.FINE, "getUSEClass result: " + cls.name());
		
		return cls;
	}

	private boolean handleMethodCall(BreakpointEvent breakpointEvent) {
		Method m = breakpointEvent.location().method();
    	fireNewLogMessage(Level.FINE, "onMethodCall: " + m.toString());
    	
		// If calls inside current operation should be ignored,
		// i. e., @Monitor(ignoreSubCalls="true") is specified,
		// we can return here.
		MOperationCall currentUseOperationCall = getSystem().getCurrentOperation();
		if (currentUseOperationCall != null) {
			MOperation currentOperation = currentUseOperationCall.getOperation();
			if (currentOperation.getAnnotationValue("Monitor", "ignoreSubCalls").equalsIgnoreCase("true")) {
				fireNewLogMessage(Level.FINE, "Ignoring sub calls in " + currentOperation.toString());
				return true;
			}
		}
		
    	StackFrame currentFrame;
		try {
			currentFrame = breakpointEvent.thread().frame(0);
		} catch (IncompatibleThreadStateException e) {
			fireNewLogMessage(Level.SEVERE, "Could not retrieve stack frame");
			return true;
		}
		
    	ObjectReference javaObject = currentFrame.thisObject();
    	
    	Value selfValue = getUSEObject(javaObject);
    	
    	if (selfValue.isUndefined()) {
    		fireNewLogMessage(Level.WARNING, "Could not retrieve object for operation call " + m.toString() + ".");
    		return true;
    	}
    	
    	MObject self = ((ObjectValue)selfValue).value();
    	MOperation useOperation = operationMappings.get(m);
    	   	
    	if (useOperation == null) {
    		fireNewLogMessage(Level.SEVERE, "Could not find USE operation for method " + m.toString());
    	}
    	
    	try {
			if (useOperation.allParams().size() != currentFrame.getArgumentValues().size()) {
				fireNewLogMessage(Level.WARNING, "Wrong number of arguments!");
				return true;
			}
		} catch (InvalidStackFrameException e) {
			fireNewLogMessage(Level.SEVERE, "Could not validate argument size");
			return true;
		}

    	checkForDeletedInstances();
    	
    	return createOperationCall(self, useOperation, currentFrame, true);
    }
    
    private boolean onMethodExit(MethodExitEvent exitEvent) {
    	MOperationCall currentUseOperationCall = getSystem().getCurrentOperation();
    	
    	if (currentUseOperationCall == null) {
    		fireNewLogMessage(Level.WARNING, "MethodExitEvent for " + exitEvent.method().toString() + " was not removed!");
    		this.monitoredVM.eventRequestManager().deleteEventRequest(exitEvent.request());
    		return true;
    	}

    	MOperation currentUseOperation = currentUseOperationCall.getOperation();
    	
    	// Ignore not matching events, because events are generated
    	// for all method exits of an instance.
		if (!mappingHelper.methodMatches(exitEvent.method(),
				currentUseOperation))
    		return true;

    	ExpressionWithValue result = null;
		this.monitoredVM.eventRequestManager().deleteEventRequest(exitEvent.request());
    	
    	if (currentUseOperation.hasResultType()) {
			result = new ExpressionWithValue(getUSEValue(exitEvent.returnValue(), currentUseOperation.resultType()));
		}

		MExitOperationStatement stmt = new MExitOperationStatement(result, ppcHandler);
		
		try {
			StatementEvaluationResult statResult = getSystem().evaluateStatement(stmt);
			if (statResult.wasSuccessfull()) {				
    			StringBuilder message = new StringBuilder("USE operation exit ");
    			message.append(currentUseOperationCall.toLegacyString());
    			message.append(" was succesfull.");
    			fireNewLogMessage(Level.INFO,  message.toString());
			}
		} catch (MSystemException e) {
			fireNewLogMessage(Level.SEVERE, "Error while exiting " + exitEvent.method().toString() + ": " + e.getMessage());
			return false;
		}
		
		return true;
    }
    
    protected MonitorPPCHandler ppcHandler = new MonitorPPCHandler();
    
    private final class BreakPointWatcher implements Runnable {
		@Override
		public void run() {
			while (isRunning) {
				try {
					EventSet events = monitoredVM.eventQueue().remove();
					if (!isRunning) return;
					
					fireNewLogMessage(Level.FINER, "Handling VM events.");
					
					for (com.sun.jdi.event.Event e : events) {
						if (e instanceof BreakpointEvent) {
							BreakpointEvent be = (BreakpointEvent)e;
							fireNewLogMessage(Level.FINER, "Handling operation call.");
							boolean opCallResult = onMethodCall(be);
							if (!opCallResult) {
								fireNewLogMessage(Level.SEVERE, "Enter of method " + be.location().method().toString() + " failed.");
								hasFailedOperation = true;
								waitForUserInput();
							}
						} else if (e instanceof ClassPrepareEvent) {
							ClassPrepareEvent ce = (ClassPrepareEvent)e;
							fireNewLogMessage(Level.FINER, "Registering operations of prepared Java class " + ce.referenceType().name() + ".");
							registerOperationBreakPoints(ce.referenceType());
						} else if (e instanceof ModificationWatchpointEvent) {
							ModificationWatchpointEvent we = (ModificationWatchpointEvent)e;
							fireNewLogMessage(Level.FINER, "Handling modification watchpoint " + we.field().toString() + ".");
							updateAttribute(we.object(), we.field(), we.valueToBe());
						} else if (e instanceof MethodExitEvent) {
							MethodExitEvent me = (MethodExitEvent)e;
							fireNewLogMessage(Level.FINER, "Handling operation exit watchpoint " + me.method().toString() + ".");
							boolean opCallResult = onMethodExit(me);
							if (!opCallResult) {
								fireNewLogMessage(Level.WARNING, "Exit of method " + me.method().toString() + " failed.");
								hasFailedOperation = true;
								waitForUserInput();
							}
						} else if (e instanceof VMDeathEvent) {
							fireNewLogMessage(Level.INFO, "VM terminated.");
							end();
							return;
						}
					}
					events.resume();
					fireNewLogMessage(Level.FINER, "Resumed threads after andling VM events.");
				} catch (InterruptedException e) {
					// VM is away np
				} catch (VMDisconnectedException e) {
					isRunning = false;
					monitoredVM = null;
					fireNewLogMessage(Level.WARNING, "Monitored application has terminated.");
				} catch (Exception ex) {
					fireNewLogMessage(
							Level.SEVERE,
							"Error while listening to break points: ["
									+ ex.getClass().getSimpleName() + "] "
									+ ex.getMessage());
					fireNewLogMessage(Level.SEVERE, "Disonnecting");
					end();
					return;
				}
			}
		}
	}

	protected class MonitorPPCHandler implements PPCHandler {

		/* (non-Javadoc)
		 * @see org.tzi.use.uml.sys.ppcHandling.PPCHandler#handlePreConditions(org.tzi.use.uml.sys.MSystem, org.tzi.use.uml.sys.MOperationCall)
		 */
		@Override
		public void handlePreConditions(MSystem system,
				MOperationCall operationCall)
				throws PreConditionCheckFailedException {
			Map<MPrePostCondition, Boolean> evaluationResults = 
					operationCall.getPreConditionEvaluationResults();
			
			boolean oneFailed = false;
			
			for (Entry<MPrePostCondition, Boolean> entry : evaluationResults.entrySet()) {
				MPrePostCondition preCondition = entry.getKey();
				if (!entry.getValue().booleanValue()) {
					fireNewLogMessage(Level.WARNING, "Precondition "
							+ StringUtil.inQuotes(preCondition.name())
							+ " failed!");
					
					oneFailed = true;
				}
			}
			
			if (oneFailed) {
				throw new PreConditionCheckFailedException(operationCall);
			}
			
		}

		/* (non-Javadoc)
		 * @see org.tzi.use.uml.sys.ppcHandling.PPCHandler#handlePostConditions(org.tzi.use.uml.sys.MSystem, org.tzi.use.uml.sys.MOperationCall)
		 */
		@Override
		public void handlePostConditions(MSystem system,
				MOperationCall operationCall)
				throws PostConditionCheckFailedException {
			boolean oneFailed = false;
			
			Map<MPrePostCondition, Boolean> evaluationResults = 
				operationCall.getPostConditionEvaluationResults();
			
			for (Entry<MPrePostCondition, Boolean> entry : evaluationResults.entrySet()) {
				MPrePostCondition preCondition = entry.getKey();
				if (!entry.getValue().booleanValue()) {
					fireNewLogMessage(Level.WARNING, "Postcondition "
							+ StringUtil.inQuotes(preCondition.name())
							+ " failed!");
					
					oneFailed = true;
				}
			}
			
			if (oneFailed) {
				throw new PostConditionCheckFailedException(operationCall);
			}
			
		}
    }
    
    public void addStateChangedListener(MonitorStateListener listener) {
    	this.stateListener.add(listener);
    }
    
    public void removeStateChangedListener(MonitorStateListener listener) {
    	this.stateListener.remove(listener);
    }
    
    protected void fireMonitorStart() {
    	for (MonitorStateListener listener : stateListener) {
    		listener.monitorStarted(this);
    		listener.monitorStateChanged(this);
    	}
    }
    
    protected void fireMonitorPause() {
    	for (MonitorStateListener listener : stateListener) {
    		listener.monitorPaused(this);
    		listener.monitorStateChanged(this);
    	}
    }
    
    protected void fireMonitorResume() {
    	for (MonitorStateListener listener : stateListener) {
    		listener.monitorResumed(this);
    		listener.monitorStateChanged(this);
    	}
    }
    
    protected void fireMonitorEnd() {
    	for (MonitorStateListener listener : stateListener) {
    		listener.monitorEnded(this);
    		listener.monitorStateChanged(this);
    	}
    }

    public void addSnapshotProgressListener(ProgressListener listener) {
    	this.snapshotProgressListener.add(listener);
    }
    
    public void removeSnapshotProgressListener(ProgressListener listener) {
    	this.snapshotProgressListener.remove(listener);
    }
    
    protected void fireSnapshotStart(String description, int numClasses) {
    	ProgressArgs args = new ProgressArgs(description, numClasses);
    	for (ProgressListener listener : this.snapshotProgressListener) {
    		listener.progressStart(args);
    	}
    }
    
    protected void fireSnapshotProgress(ProgressArgs args) {
    	for (ProgressListener listener : this.snapshotProgressListener) {
    		listener.progress(args);
    	}
    }
    
    protected void fireSnapshotEnd() {
    	for (ProgressListener listener : this.snapshotProgressListener) {
    		listener.progressEnd();
    	}
    }
    
    public void addLogListener(LogListener listener) {
    	this.logListener.add(listener);
    }
    
    public void removeLogListener(LogListener listener) {
    	this.logListener.remove(listener);
    }
    
    protected void fireNewLogMessage(Level level, String message) {
    	for (LogListener listener : this.logListener) {
    		listener.newLogMessage(this, level, message);
    	}
    }
    
	@Override
	public void stateChanged(ChangeEvent e) {
		if (!isResetting && isRunning()) {
			end();
		}
	}
}
