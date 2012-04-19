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
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MModelElement;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.mm.MPrePostCondition;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MOperationCall;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.ppcHandling.PPCHandler;
import org.tzi.use.uml.sys.ppcHandling.PostConditionCheckFailedException;
import org.tzi.use.uml.sys.ppcHandling.PreConditionCheckFailedException;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;
import org.tzi.use.uml.sys.soil.MObjectDestructionStatement;
import org.tzi.use.util.Log;
import org.tzi.use.util.StringUtil;

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.MethodExitEvent;

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
	 * The adapter used to retrieve information
	 * from a virtual machine.
	 */
	private VMAdapter adapter;
	
	/**
	 * The USE session which provides information about the relevant
	 * classes and operations to listen for.
	 * The session is required (and not only the {@link MModel}), because
	 * the monitor has to react on changes like loading another model.
	 */
	private Session session;
	
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
     * Saves the mapping between USE objects and VM objects.
     */
    private Map<VMObject, MObject> instanceMapping;
    
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
    private Map<MClass, Set<VMType>> classMappings;
    
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
     * @throws InvalidAdapterConfiguration If an invalid configuration for the selected adapter is given by <code>args</code>. 
     * @throws IllegalArgumentException If an invalid port number is provided as an argument or specified in the model annotation.
     */
    public void configure(Session session, VMAdapter adapter, Map<String, String> args) throws InvalidAdapterConfiguration {
    	this.adapter = adapter;
    	this.adapter.configure(new Controller(), args);
    	
    	this.session = session;
    	this.session.addChangeListener(this);
    	this.mappingHelper = new IdentifierMappingHelper(session.system().model());
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
    	this.instanceMapping = new HashMap<VMObject, MObject>();
    	this.hasSnapshot = false;
    	
    	try {
    		adapter.attachToVM();
    		fireNewLogMessage(Level.INFO, "Connected to virtual machine");
		} catch (MonitorException e) {
			fireNewLogMessage(Level.SEVERE, "Error connecting to the VM: " + e.getMessage());
			return;
		}
		
		registerClassPrepareEvents();
		
		registerOperationBreakPoints();
		
		isRunning = true;
		
		// TODO: Check if really needed!
		adapter.resume();
		
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
    	adapter.suspend();
		
    	if (doReset) {
    		doUSEReset();
    	}
    	
		long start = System.currentTimeMillis();
		countInstances = 0;
		countLinks = 0;
		countAttributes = 0;
		
		fireNewLogMessage(Level.INFO, "Creating snapshot using" + (useSoil ? " SOIL statements." : " using system operations." ));
    	readSnapshot();
    	
    	long end = System.currentTimeMillis();
    	fireNewLogMessage(Level.INFO, String.format("Read %,d instances and %,d links in %,dms.", countInstances, countLinks, (end - start)));
    	
    	calculateCurrentCallStack();
    	
    	hasSnapshot = true;
    	isPaused = true;
    	fireMonitorPause();
    }

    private void calculateCurrentCallStack() {
    	//FIXME: Dynamic monitoring
    	
    	/*
    	 
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
		
		*/
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
		return true;
		//FIXME: Dynamic monitoring
		/*
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
		*/
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
    	
    	adapter.resume();
    	isPaused = false;
    	
    	fireNewLogMessage(Level.INFO, "VM resumed.");
    	fireMonitorResume();
    }
    
    /**
     * Ends the monitoring process by closing the connection to the VM.
     * The USE system state is kept untouched.
     */
    public void end() {
    	adapter.stop();
    	    	
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
    
	private void registerClassPrepareEvents() {
		for (MClass cls : getSystem().model().classes()) {
			String javaClassName = mappingHelper.getJavaClassName(cls);
			
			if (!adapter.isVMTypeLoaded(javaClassName)) {
				adapter.registerClassPrepareEvent(javaClassName);
			}
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

    /**
	 * Sets a break point for each defined USE operation (if it matches a Java method) 
	 * of the specified USE class, if the corresponding Java class 
	 * is loaded classes in the VM.
	 */
	private void registerOperationBreakPoints(MClass cls) {
		VMType vmType = getReferenceClass(cls);  
		
		if (vmType == null) {
			fireNewLogMessage(Level.INFO, "No runtime class found for model class " + cls.name() + ", yet.");
		} else {
			fireNewLogMessage(Level.FINE, "Registering operation breakpoints for class " + cls.name());
			for (MOperation op : cls.operations()) {
				String isQuery = op.getAnnotationValue("Monitor", "isQuery");
				if (isQuery.equals("true")) continue;
				
				String methodName = mappingHelper.getJavaMethodName(op);
				List<VMMethod> methods = vmType.getMethodsByName(methodName);
				
				for (VMMethod m : methods) {
					// TODO: Check parameter types
					if (m.getArgumentTypes().size() == op.allParams().size()) {
						fireNewLogMessage(Level.FINE, "Registering operation breakpoint for operation " + m.toString());
						m.setUSEOperation(op);
						adapter.registerOperationCallInterest(m);
					}
				}
			}
			
			//FIXME: Dynamic monitoring
			/*
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
			*/
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
	
	private VMType getReferenceClass(MClass cls) {
    	return adapter.getVMType(mappingHelper.getJavaClassName(cls));
    }

	/**
	 * Constructs a map which maps each use class
	 * to the reference types that must be read. 
	 */
	private void setupClassMappings() {
		Collection<MClass> useClasses = getSystem().model().classes();
		classMappings = new HashMap<MClass, Set<VMType>>(useClasses.size());
		
		// To be able to quickly check if a VMType is handled by the model 
		HashSet<String> allHandledClassNames = new HashSet<String>(useClasses.size());
		for (MClass useClass : useClasses) {
			allHandledClassNames.add(mappingHelper.getJavaClassName(useClass));
		}
		
		// This loop builds-up the mapping of use classes to VMTypes.
		// A single USE class can represent more than one VMType.
		for (MClass useClass : useClasses) {
			if (useClass.getAnnotationValue("Monitor", "ignore").equals("true")) {
				// Ignore this class
				continue;
			}
			Set<VMType> mappedTypes = new HashSet<VMType>();
			classMappings.put(useClass, mappedTypes);
			
			VMType vmType = getReferenceClass(useClass);
			
			if (vmType != null && vmType.isClassType()) {
				Stack<VMType> toDo = new Stack<VMType>();
				toDo.push(vmType);
				
				while (!toDo.isEmpty()) {
					VMType workingType = toDo.pop();
					
					mappedTypes.add(workingType);
					workingType.setUSEClass(useClass);
					
					if (!useClass.getAnnotationValue("Monitor", "ignoreSubclasses").equals("true")) {		
						//FIXME: Change to allSubClasses()
						for (VMType t : workingType.getSubClasses()) {
							// Subtype is handled by its own class?
							if (!allHandledClassNames.contains(t.getName())) {
								toDo.push(t);
							}
						}
					}
				}
			}
		}
	}
	
	private void readSnapshot() {
		Collection<MClass> classes = getSystem().model().classes();
		
		fireSnapshotStart("Initializing...", classes.size());
		
		instanceMapping = new HashMap<VMObject, MObject>();
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
    	Set<VMType> typesToRead = classMappings.get(cls);

    	if (typesToRead.isEmpty()) {
    		fireNewLogMessage(Level.FINE, "VM class "
					+ StringUtil.inQuotes(mappingHelper.getJavaClassName(cls))
					+ " could not be found for USE class "
					+ StringUtil.inQuotes(cls.name())
					+ ". Maybe not loaded, yet.");
			return;
    	}

		for (VMType refType : typesToRead) {
	    	Set<VMObject> refInstances = refType.getInstances();
			
			for (VMObject vmObj : refInstances) {
				if (!vmObj.isAlive()) continue;
				
				try {
					createInstance(cls, vmObj);
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
	 * @param vmObj The runtime instance
	 * @return The new instance
	 * @throws MSystemException
	 */
	protected MObject createInstance(MClass cls, VMObject vmObj)
			throws MSystemException {
		MObject useObject;
		
		if (useSoil) {
			MNewObjectStatement stmt = new MNewObjectStatement(cls);
			getSystem().evaluateStatement(stmt);
			useObject = stmt.getCreatedObject();
		} else {
			String name = getSystem().state().uniqueObjectNameForClass(cls);
			useObject = getSystem().state().createObject(cls, name);
		}
						
		vmObj.setUSEObject(useObject);
		instanceMapping.put(vmObj, useObject);
		
		return useObject;
	}
    
	/**
     * Creates MDeleteStatements for garbage collected objects.
     */
    private void checkForDeletedInstances() {
    	// Need an iterator to be able to remove elements
    	Iterator<Map.Entry<VMObject, MObject>> iter = this.instanceMapping.entrySet().iterator();
    	
		while (iter.hasNext()) {
			Map.Entry<VMObject, MObject> entry = iter.next();
			VMObject refObject = entry.getKey();
			
			if (!refObject.isAlive()) {
				MObject useObj = refObject.getUSEObject();
				
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
    	
    	// Read all attributes
    	for (Map.Entry<VMObject, MObject> entry : instanceMapping.entrySet()) {
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
		
		// Read all links
		args.setDescription("Reading links");
    	for (Map.Entry<VMObject, MObject> entry : instanceMapping.entrySet()) {
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
    	//FIXME: Dynamic monitoring
    	/*
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
    	*/
    }
    
    /**
     * Checks the {@link VMType} of <code>objRef</code> for the attributes
     * of the USE class of <code>o</code>. If an attribute is found the value of the 
     * USE attribute is set. After that all associations the USE class is participating
     * in are checked for rolenames matching an attribute name. For each match corresponding
     * links are created.  
     * @param objRef The VM instance to read the values from 
     * @param o The use object to set the values for.
     */
    private void readAttributes(VMObject objRef, MObject o) {
    	for (MAttribute attr : o.cls().allAttributes()) {
    		
    		if (!readSpecialAttributeValue(objRef, o, attr)) {
	    		VMField field;
	    		field = objRef.getType().getFieldByName(mappingHelper.getJavaFieldName(attr));
	    			    		
	    		if (field != null) {
	    			Value v = objRef.getValue(field);
	    			
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
    private boolean readSpecialAttributeValue(VMObject objRef, MObject o, MAttribute attr) {
    	String annotation = attr.getAnnotationValue("Monitor", "value");
    	if (annotation.equals("")) return false;
    	
		Value v = UndefinedValue.instance; 
		if (annotation.equals("classname")) {
			v = new StringValue(objRef.getType().getName());
		} else {
			v = new StringValue("undefined special value " + StringUtil.inQuotes(annotation));
		}

		o.state(getSystem().state()).setAttributeValue(attr, v);
		
		return true;
	}

	private void readLinks(VMObject objRef, MObject o) {
		Set<MClass> allClasses = new HashSet<MClass>(o.cls().allParents());
		allClasses.add(o.cls());
		
		for (MClass cls : allClasses) {
			for (MAssociation ass : cls.associations()) {
				if (ass instanceof MAssociationClass) {
					// FIXME: Consider association classes
					continue;
				}
				
				List<MNavigableElement> reachableEnds = ass.navigableEndsFrom(cls);
			
    			// Check if object has link in vm
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
    
	/**
	 * Reads a link or multiple links (if qualified association)
	 * for an association end with upper multiplicity of 1.  
	 * @param objRef
	 * @param source
	 * @param end
	 */
    private void readLink(VMObject objRef, MObject source, MAssociationEnd end) {
    	VMField field = objRef.getType().getFieldByName(mappingHelper.getJavaFieldName(end));
    	
    	if (field == null) {
			fireNewLogMessage(
					Level.FINE,
					"Association end "
							+ StringUtil.inQuotes(end)
							+ "could not be retrieved as a field inside of the VM.");
    		return;
    	}
    	
    	// Get the referenced object
    	Value fieldValue = objRef.getValue(field);
    	
    	if (fieldValue.isUndefined())
    		return;
    	
    	if (fieldValue instanceof SequenceValue) {
    		// Qualified association
    		SequenceValue seqValue = (SequenceValue)fieldValue;
    			
    		try {
    			readQualifiedLinks(source, end, seqValue, new ArrayList<Value>());
			} catch (Exception e) {
				fireNewLogMessage(Level.SEVERE, "ERROR: " + e.getMessage()); 
			}
    		
    	} else {
	    	try {
	    		ObjectValue oValue = (ObjectValue)fieldValue; 
	    		createLink(source, end, oValue.value());
	    	} catch (Exception ex) {
	    		fireNewLogMessage(Level.SEVERE, "Error while reading link: " + ex.getMessage());
	    	}
    	}
    }

	/**
	 * @param i
	 * @param qualifierValues
	 * @return
	 */
	private void readQualifiedLinks(MObject source, MAssociationEnd end, SequenceValue seqValue, List<Value> qualifierValues) {
		for (int i = 0; i < seqValue.size(); ++i) {
			List<Value> qualifierValuesNew = new ArrayList<Value>(qualifierValues);
			qualifierValuesNew.add(IntegerValue.valueOf(i));
			Value val = seqValue.get(i);
			
			if (val.isSequence()) {
				readQualifiedLinks(source, end, (SequenceValue)val, qualifierValuesNew);
			} else if (val.isObject()) {
				ObjectValue target = (ObjectValue)val;
				if (target != null) {
	    			createLink(source, end, target.value(), qualifierValuesNew);
	    		}
			}
			
			// Remove last added integer value
			qualifierValuesNew.remove(qualifierValuesNew.size() - 1);
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
    
	/**
	 * Reads links at an association end with an upper
	 * multiplicity of more than 1.
	 * @param objRef
	 * @param o
	 * @param end
	 */
    private void readLinks(VMObject objRef, MObject o, MAssociationEnd end) {
    	VMField field = objRef.getType().getFieldByName(end.nameAsRolename());
    	
    	if (field == null) {
    		fireNewLogMessage(
					Level.FINE,
					"Association end "
							+ StringUtil.inQuotes(end)
							+ "could not be retrieved as a field inside of teh VM.");
    		return;
    	}
    	
    	// Get the referenced objects
    	Value objects = objRef.getValue(field);
    	
    	if (objects == null) {
    		return;
    	}
    	
    	if (!objects.isCollection()) {
    		fireNewLogMessage(
					Level.SEVERE,
					"Reading of multi-valued association end " + StringUtil.inQuotes(end.toString()) + " resulted in a single value!");
    		return;
    	}
    	
    	
    	if (objects.isSet() && end.isOrdered()) {
			// Just warn
			fireNewLogMessage(
					Level.WARNING,
					"Association end "
							+ StringUtil.inQuotes(end.toString())
							+ " was read as a set value but is marked as {ordered}.");
    	} else if (objects.isSequence() && !(end.isOrdered() || end.hasQualifiers())) {
			fireNewLogMessage(
					Level.WARNING,
					"Association end "
							+ StringUtil.inQuotes(end.toString())
							+ " was read as an index based value, but is not marked as {ordered} nor it defines qualifiers.");
    	}
    	
    	CollectionValue objCollection = (CollectionValue)objects;
    	
    	for (Value v : objCollection) {
    		if (!v.isObject()) {
    			fireNewLogMessage(Level.WARNING, "Encountered non object during reading of association end " + StringUtil.inQuotes(end));
    			return;
    		}
    		
    		createLink(o, end, ((ObjectValue)v).value());
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
    	//FIXME: Dynamic monitoring
    	/*
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
		*/
		return true;
    }

	private boolean handleMethodCall(BreakpointEvent breakpointEvent) {
		return true;
		//FIXME: Dynamic monitoring
		/*
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
    	*/
    }
    
    private boolean onMethodExit(MethodExitEvent exitEvent) {
    	//FIXME: Dynamic monitoring
    	
    	/*
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
		*/
		return true;
    }
    
    protected MonitorPPCHandler ppcHandler = new MonitorPPCHandler();
    
    

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
	
	/**
	 * This class encapsulates control functions exposed
	 * only to the adapter.
	 * This allows to hide adapter operations from the public interface of
	 * the monitor.
	 * @author Lars Hamann
	 *
	 */
	public class Controller {
		/**
		 * Only the monitor can create instances
		 */
		private Controller() {}
		
		/**
		 * Ends the monitoring process.
		 */
		public void end() {
			Monitor.this.end();
		}
		
		public void newLogMessage(Level level, String message) {
			fireNewLogMessage(level, message);
		}

		/**
		 * @param name
		 * @return
		 */
		public EnumType getEnumerationType(String name) {
			for (EnumType t : session.system().model().enumTypes()) {
				if (mappingHelper.getVMEnumName(t).equals(name))
					return t;
			}
			
			return null;
		}
	}
}
