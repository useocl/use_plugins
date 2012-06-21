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

import org.tzi.use.main.ChangeEvent;
import org.tzi.use.main.ChangeListener;
import org.tzi.use.main.Session;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAccessException;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapter;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMType;
import org.tzi.use.uml.mm.Annotatable;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.mm.MPrePostCondition;
import org.tzi.use.uml.ocl.expr.ExpObjRef;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.TupleValue;
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
import org.tzi.use.util.StringUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * This class handles the monitoring of a VM application
 * via a VMAdapter.
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
     * Internal mapping for the adapters from a VM specific key
     * to the intermediate representation as a {@link VMObject}.
     */
    private BiMap<Object, VMObject> adapterObjectMapping;
    
    /**
     * Internal mapping for the adapters from a VM specific key
     * to the intermediate representation as a {@link VMType}.
     */
    private BiMap<Object, VMType> adapterTypeMapping;
    
    /**
     * Internal mapping for the adapters from a VM specific key
     * to the intermediate representation as a {@link VMOperation}.
     */
    private BiMap<Object, VMMethod> adapterMethodMapping;
    
    /**
     * Internal mapping for the adapters from a VM specific key
     * to the intermediate representation as a {@link VMOperation}.
     */
    private BiMap<Object, VMField> adapterFieldMapping;
    
    private Stack<VMMethodCall> monitoredCalls;
        
    public Monitor() { }
    
    /**
     * Returns the {@link MSystem} used by the monitor.
     * @return The <code>MSystem</code> used by this monitor.
     */
    private MSystem getSystem() {
    	return session.system();
    }
    
    /**
     * Configures the monitor to attach to the specified virtual machine configured for the specified adapter.
     * @param session The USE session to use for the monitoring process. The monitor reacts on state changes of the session.
     * @throws InvalidAdapterConfiguration If an invalid configuration for the selected adapter is given by <code>args</code>.
     */
    public void configure(Session session, VMAdapter adapter) throws InvalidAdapterConfiguration {
    	this.adapter = adapter;
    	this.adapter.configure(new Controller());
    	
    	this.session = session;
    	this.session.addChangeListener(this);
    	this.mappingHelper = new IdentifierMappingHelper(session.system().model());
    }
	
    /**
     * If <code>true</code>, SOIL Statements are used for all state manipulating
     * commands.
     * Otherwise, the direct implementation is used which does not allow to undo/redo commands 
     * or to save the state as a script. 
	 * @return the useSoil
	 */
	public boolean isUseSoil() {
		return useSoil;
	}

	/**
	 * @param useSoil the useSoil to set
	 */
	public void setUseSoil(boolean useSoil) {
		this.useSoil = useSoil;
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
    	
    	this.adapterObjectMapping = HashBiMap.create();
    	this.adapterTypeMapping = HashBiMap.create();
    	this.adapterMethodMapping = HashBiMap.create();
    	this.adapterFieldMapping = HashBiMap.create();
    	
    	this.monitoredCalls = new Stack<VMMethodCall>();
    	
    	this.mappingHelper = new IdentifierMappingHelper(session.system().model());
    	
    	this.hasSnapshot = false;
    	
    	try {
    		adapter.attachToVM();
    		fireNewLogMessage(Level.INFO, "Connected to virtual machine");
		} catch (MonitorException e) {
			fireNewLogMessage(Level.SEVERE, "Error connecting to the VM: " + e.getMessage());
			return;
		}
		
    	setupClassMappings();
    	
		registerClassPrepareEvents();
		
		registerOperationBreakPoints();
		
		isRunning = true;
		isPaused = false;
		
		if (suspend) {
			pause(false);
		} else {
			adapter.resume();
		}
		
		fireMonitorStart();
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
	private boolean createOperationCall(MObject self, VMMethodCall call, boolean validatePreConditions) {

		List<Value> vmArguments = null;
		try {
			vmArguments = call.getArgumentValues();
		} catch (VMAccessException e1) {
			fireNewLogMessage("getting number of arguments for operation call", e1);
			return false;
		}
		
		Map<String, Expression> arguments = new HashMap<String, Expression>();
    	
		MOperation useOperation = call.getMethod().getUSEOperation();
		List<VarDecl> allParams = useOperation.allParams();
		
    	int numArgs = allParams.size();
    	
    	for (int index = 0; index < numArgs; index++) {    		
    		arguments.put(allParams.get(index).name(), new ExpressionWithValue(vmArguments.get(index)));
    	}
    	
    	PPCHandler handler = (validatePreConditions ? ppcHandler : DoNothingPPCHandler.getInstance());
    	
		MEnterOperationStatement operationCall = new MEnterOperationStatement(
				new ExpObjRef(self), useOperation, arguments, handler );
    	
    	try {
    		StatementEvaluationResult result = getSystem().execute(operationCall);
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
		
    	monitoredCalls.push(call);
    	adapter.registerMethodExit(call);
		
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
    	    	
    	adapterObjectMapping = null;
    	adapterTypeMapping = null;
    	adapterMethodMapping = null;
    	adapterFieldMapping = null;
    	
    	mappingHelper = null;
    	
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
			if (elementShouldBeIgnored(cls))
				continue;
			
			String javaClassName = mappingHelper.getVMClassName(cls);
			
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
    		if (elementShouldBeIgnored(cls))
    			continue;
    		
    		try {
    			registerOperationBreakPoints(cls);
    		} catch (Exception e) {
    			fireNewLogMessage(Level.SEVERE, "Error while setting break points for class " + cls.name() + ". Reason: " + e.getMessage());
    		}
    	}
    }

    /**
	 * Sets a break point for each defined USE operation (if it matches a Java method) 
	 * of the specified USE class, if the corresponding Java class 
	 * is loaded classes in the VM.
	 */
	private void registerOperationBreakPoints(MClass cls) {
		VMType vmType = getVMType(cls);  
		
		if (vmType == null) {
			fireNewLogMessage(Level.INFO, "No runtime class found for model class " + cls.name() + ", yet.");
			return;
		}
							
		fireNewLogMessage(Level.FINE, "Registering operation call interest for class " + cls.name());
		for (MOperation op : cls.operations()) {
			
			if (elementShouldBeIgnored(op))
				continue;
			
			String methodName = mappingHelper.getVMMethodName(op);
			List<VMMethod> methods = vmType.getMethodsByName(methodName);
			
			for (VMMethod m : methods) {
				if (mappingHelper.methodMatches(m, op)) {
					fireNewLogMessage(Level.FINER, "Registering operation call interest for operation " + m.toString());
					m.setUSEOperation(op);
					adapterMethodMapping.put(m.getId(), m);
					adapter.registerOperationCallInterest(m);
				}
			}
		}
		
		// Breakpoints for constructors
		fireNewLogMessage(Level.FINE, "Registering constructor call interest for class " + cls.name());
		adapter.registerConstructorCallInterest(vmType);
		
		// watch points for attributes
		fireNewLogMessage(Level.FINE, "Registering attribute modification interest for class " + cls.name());
		for (MAttribute a : cls.attributes()) {
			if (elementShouldBeIgnored(a))
				continue;
			
			VMField f = getVMField(vmType, a);
			if (f == null) {
				fireNewLogMessage(Level.WARNING, "Unknown attribute " + StringUtil.inQuotes(a.name()));
				continue;
			}
			
			adapter.registerFieldModificationInterest(f);
		}
		
		fireNewLogMessage(Level.FINE, "Registering link modification interest for class " + cls.name());
		// Association ends with multiplicity 1 can be handled also
		for (Map.Entry<String, MNavigableElement> end : cls.navigableEnds().entrySet()) {
			
			if (!end.getValue().isCollection() && end.getValue() instanceof MAssociationEnd) {
				MAssociationEnd assEnd = (MAssociationEnd)end.getValue();
				
				if (elementShouldBeIgnored(assEnd))
					continue;
				
				VMField f = getVMField(vmType, assEnd);

				if (f == null) {
					fireNewLogMessage(Level.WARNING, "Unknown attribute " + StringUtil.inQuotes(assEnd.name()));
					continue;
				}
				
				adapter.registerFieldModificationInterest(f);
			}
		}
	}

	/**
	 * Returns the {@link VMType} for the given {@link MClass}
	 * by first translating the USE name to the implementation name using
	 * annotations and then asking the used link {@link VMAdapter}. 
	 * @param cls
	 * @return
	 */
	private VMType getVMType(MClass cls) {
    	return adapter.getVMType(mappingHelper.getVMClassName(cls));
    }

	private VMField getVMField(VMType type, MAttribute attr) {
		String vmName = mappingHelper.getVMFieldName(attr);
		VMField field = getVMField(type, vmName);
		
		if (field != null) {
			field.setUSEAttribute(attr);
		}
		
		return field;
	}
	
	private VMField getVMField(VMType type, MAssociationEnd end) {
		String vmName = mappingHelper.getVMFieldName(end);
		VMField field = getVMField(type, vmName); 
		
		if (field != null) {
			field.setUSEAssociationEnd(end);
		}
		
		return field;
	}
	
	private VMField getVMField(VMType type, String vmName) {
		VMField f = type.getFieldByName(vmName);
		
		if (f == null) {
			return null;
		}
		
		if (!adapterFieldMapping.containsKey(f.getId())) {
			adapterFieldMapping.put(f.getId(), f);
		}
		
		return adapterFieldMapping.get(f.getId());
	}
	
	private boolean elementShouldBeIgnored(Annotatable e) {
		return e.getAnnotationValue("Monitor", "ignore").equalsIgnoreCase("true") ||
			   e.getAnnotationValue("Monitor", "isQuery").equalsIgnoreCase("true");
	}
	
	/**
	 * Constructs a map which maps each use class
	 * to the reference types that must be read. 
	 */
	private void setupClassMappings() {
		Collection<MClass> useClasses = getSystem().model().classes();
		
		// This loop builds-up the mapping of use classes to VMTypes.
		// A single USE class can represent more than one VMType.
		for (MClass useClass : useClasses) {
			if (elementShouldBeIgnored(useClass)) {
				// Ignore this class
				continue;
			}
			
			VMType vmType = getVMType(useClass);
			
			if (vmType != null && vmType.isClassType()) {
				Stack<VMType> toDo = new Stack<VMType>();
				toDo.push(vmType);
				
				while (!toDo.isEmpty()) {
					VMType workingType = toDo.pop();
					
					workingType.setUSEClass(useClass);
					mappingHelper.addHandledVMType(workingType, useClass);
					
					if (!elementShouldBeIgnored(useClass)) {		
						//FIXME: Change to allSubClasses()
						for (VMType t : workingType.getSubClasses()) {
							// Subtype is handled by its own class?
							if (!mappingHelper.isVMTypeMapped(t)) {
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
		
		this.adapterObjectMapping = HashBiMap.create();
		
		long start = System.currentTimeMillis();
		ProgressArgs args = new ProgressArgs("Reading instances", 0, classes.size());
		// Create current system state
    	for (MClass cls : classes) {
    		fireSnapshotProgress(args);
    		
    		if (!elementShouldBeIgnored(cls)) {
    			readInstances(cls);
    		}
    		
    		args.setCurrent(args.getCurrent() + 1);
    	}
    	
    	long end = System.currentTimeMillis();
    	long duration = (end - start);
    	long instPerSecond = Math.round((double)countInstances / ((double)duration / 1000));
		
		fireNewLogMessage(Level.INFO, String.format(" Created %,d instances in %,dms (%,d instances/s).", countInstances, duration, instPerSecond));
    	
    	readAttributtesAndLinks();
    	    	
    	fireSnapshotEnd();
	}
	
    private void readInstances(MClass cls) {
    	
    	// Find all subclasses of the reference type which are not modeled in
    	// the USE file, because instances() only returns concrete instances
    	// of a type (and not of subclasses) 
    	Set<VMType> typesToRead = mappingHelper.getVMTypes(cls);

    	if (typesToRead.isEmpty()) {
    		fireNewLogMessage(Level.FINE, "VM class "
					+ StringUtil.inQuotes(mappingHelper.getVMClassName(cls))
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
					fireNewLogMessage(Level.SEVERE, e.getMessage());
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
			getSystem().execute(stmt);
			useObject = stmt.getCreatedObject();
		} else {
			String name = getSystem().state().uniqueObjectNameForClass(cls);
			useObject = getSystem().state().createObject(cls, name);
		}
						
		vmObj.setUSEObject(useObject);
		adapterObjectMapping.put(vmObj.getId(), vmObj);
		
		return useObject;
	}
    
	/**
     * Creates MDeleteStatements for garbage collected objects.
     */
    private void checkForDeletedInstances() {
    	// Need an iterator to be able to remove elements
    	Iterator<Map.Entry<Object, VMObject>> iter = this.adapterObjectMapping.entrySet().iterator();
    	
		while (iter.hasNext()) {
			Map.Entry<Object, VMObject> entry = iter.next();
			VMObject refObject = entry.getValue();
			
			if (!refObject.isAlive()) {
				MObject useObj = refObject.getUSEObject();
				
				ObjectValue valObject = new ObjectValue(useObj.type(), useObj); 
				MObjectDestructionStatement delStmt = new MObjectDestructionStatement(valObject);
				try {
					this.getSystem().execute(delStmt);
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

    	int progressEnd = adapterObjectMapping.size() * 2;
    	fireSnapshotStart("Reading attributes and links...", progressEnd);
    	ProgressArgs args = new ProgressArgs("Reading attributes", progressEnd);
    	// Maximum number of progress calls 50
    	int step = progressEnd / 50;
    	int counter = 0;
    	
    	// Read all attributes
    	for (Map.Entry<Object, VMObject> entry : adapterObjectMapping.entrySet()) {
    		readAttributes(entry.getValue(), entry.getValue().getUSEObject());
    		
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
    	for (Map.Entry<Object, VMObject> entry : adapterObjectMapping.entrySet()) {
    		readLinks(entry.getValue(), entry.getValue().getUSEObject());
    		
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
    
    /**
     * Checks the {@link VMType} of <code>objRef</code> for the attributes
     * of the USE class of <code>o</code>. If an attribute is found the value of the 
     * USE attribute is set. After that all associations the USE class is participating
     * in are checked for role names matching an attribute name. For each match corresponding
     * links are created.  
     * @param objRef The VM instance to read the values from 
     * @param o The use object to set the values for.
     */
    private void readAttributes(VMObject objRef, MObject o) {
    	for (MAttribute attr : o.cls().allAttributes()) {
    		
    		if (elementShouldBeIgnored(attr))
    			continue;
    		
    		if (!readSpecialAttributeValue(objRef, o, attr)) {
	    		VMField field = getVMField(objRef.getType(), attr);

	    		if (field != null) {
	    			Value v = objRef.getValue(field);
	    			
	    			try {
	    				if (useSoil) {
	    					MAttributeAssignmentStatement stmt = 
	    						new MAttributeAssignmentStatement(o, attr, v);
	    					getSystem().execute(stmt);
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
        			
					if (elementShouldBeIgnored(reachableEnd))
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
    	VMField field = getVMField(objRef.getType(), end);
    	
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
    	} else if (fieldValue.isSet() && ((SetValue)fieldValue).elemType().isTupleType(true)) {
    		// Qualified association
    		SetValue setValue = (SetValue)fieldValue;
    			
    		try {
    			readQualifiedLinks(source, end, setValue);
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
     * Reads qualified links from a Set(Tuple(key,value))
     * @param source
     * @param end
     * @param seqValue
     * @param qualifierValues
     */
    private void readQualifiedLinks(MObject source, MAssociationEnd end, SetValue setValue) {
    	List<Value> qualifier = new LinkedList<Value>();
    	
    	for (Value val : setValue.collection()) {
    		qualifier.clear();
    		
    		TupleValue entry = (TupleValue)val;
    		Value key = entry.getElementValue("key");
    		Value value = entry.getElementValue("value");
    		
    		if (!value.isUndefined()) {
    			qualifier.add(key);
    		
    			if (value != null) {
    				createLink(source, end, ((ObjectValue)value).value(), qualifier);
    			}
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
					getSystem().execute(stmt);
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
    	VMField field = getVMField(objRef.getType(), end);
    	
    	if (field == null) {
    		fireNewLogMessage(
					Level.FINE,
					"Association end "
							+ StringUtil.inQuotes(end.association() + "::" + end)
							+ " could not be retrieved as a field inside of the VM.");
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
    
    protected void fireNewLogMessage(String task, VMAccessException e) {
    	fireNewLogMessage(Level.SEVERE, "VM access error while " + task + ": " + e.getMessage());
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
		
		public void newLogMessage(Object source, Level level, String message) {
			fireNewLogMessage(level, source.toString() + ": " + message);
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
		
		public VMObject getVMObject(Object key) {
			return adapterObjectMapping.get(key);
		}
		
		public boolean existsVMObject(Object key) {
			return adapterObjectMapping.containsKey(key);
		}
		
		private void storeVMObject(Object key, VMObject obj) {
			adapterObjectMapping.put(key, obj);
		}
		
		public VMType getVMType(Object key) {
			return adapterTypeMapping.get(key);
		}
		
		public boolean existsVMType(Object key) {
			return adapterTypeMapping.containsKey(key);
		}
		
		public void storeVMType(Object key, VMType type) {
			adapterTypeMapping.put(key, type);
		}
		
		public VMMethod getVMMethod(Object key) {
			return adapterMethodMapping.get(key);
		}
		
		public boolean existsVMMethod(Object key) {
			return adapterMethodMapping.containsKey(key);
		}
		
		public void storeVMMethod(Object key, VMMethod type) {
			adapterMethodMapping.put(key, type);
		}
		
		/**
		 * Needs to be called if a method is called
		 * which the monitor registered for.
		 * The monitor will call all other adapter methods.  
		 * @param vmMethodId The id of the method returned by {@link VMMethod#getId()}
		 */
		public void onMethodCall(VMMethodCall vmMethodCall) {
			/*
			 * If calls inside current operation should be ignored,
			 * i. e., @Monitor(ignoreSubCalls="true") is specified,
			 * we can exit here
			 */
			MOperationCall currentUseOperationCall = getSystem().getCurrentOperation();
			if (currentUseOperationCall != null) {
				MOperation currentOperation = currentUseOperationCall.getOperation();
				if (currentOperation.getAnnotationValue("Monitor", "ignoreSubCalls").equalsIgnoreCase("true")) {
					fireNewLogMessage(Level.FINE, "Ignoring sub calls in " + currentOperation.toString());
					return;
				}
			}

			VMMethod calledMethod = vmMethodCall.getMethod();
			
			MOperation useOperation = calledMethod.getUSEOperation();
			if (useOperation == null) {
				fireNewLogMessage(Level.SEVERE, "No USE operation defined for VM method " + calledMethod.toString());
	    		return;
	    	}
			
			VMObject vmThisObject = null;
			try {
				vmThisObject = vmMethodCall.getThisObject();
			} catch (VMAccessException e1) {
				fireNewLogMessage("getting this object for operation call", e1);
			}
			
			if (vmThisObject == null ) {
				fireNewLogMessage(Level.WARNING, "Could not retrieve object for operation call " + vmMethodCall.toString() + ".");
	    		return;
	    	}
			
	    	MObject self = vmThisObject.getUSEObject();
	    	
			try {
				if (useOperation.allParams().size() != vmMethodCall.getNumArguments()) {
					fireNewLogMessage(Level.WARNING, "Wrong number of arguments!");
					return;
				}
			} catch (VMAccessException e) {
				fireNewLogMessage(Level.SEVERE, e.getMessage());
			}
			
	    	checkForDeletedInstances();
	    	
	    	if (!createOperationCall(self, vmMethodCall, true)) {
	    		waitForUserInput();
	    	}
		}

		public void onMethodExit(Object adapterExitInformation, Object adapterMethodId) {
			MOperationCall currentUseOperationCall = getSystem().getCurrentOperation();
			
			if (currentUseOperationCall == null) {
				fireNewLogMessage(Level.WARNING, "Too much method exits events. USE call stack is empty.");
	    		return;
	    	}
			
			// Ignore not matching events, because events for the JVM are generated
	    	// for all method exits of an instance.
	    	if (!adapterMethodMapping.containsKey(adapterMethodId))
	    		return;
	    	
	    	VMMethod vmMethod = adapterMethodMapping.get(adapterMethodId);
	    	VMMethodCall currentCall = monitoredCalls.peek();
	    	
	    	if (!vmMethod.getUSEOperation().equals(currentCall.getMethod().getUSEOperation()))
	    		return;

	    	MOperation currentUseOperation = currentUseOperationCall.getOperation();
	    	
	    	MObject useObject = null;
			try {
				useObject = currentCall.getThisObject().getUSEObject();
			} catch (VMAccessException e1) {
				fireNewLogMessage("get this object for operation call", e1);
			}
	    	
	    	if (!currentUseOperationCall.getSelf().equals(useObject))
	    		return;
	    	
	    	boolean success = false;
	    	
	    	monitoredCalls.pop();
	    	adapter.unregisterOperationeExit(adapterExitInformation);
	    	
	    	ExpressionWithValue result = null;
	    	if (currentUseOperation.hasResultType()) {
	    		Value resultValue = adapter.getMethodResultValue(adapterExitInformation);
				result = new ExpressionWithValue(resultValue);
			}

			MExitOperationStatement stmt = new MExitOperationStatement(result, ppcHandler);
			
			try {
				StatementEvaluationResult statResult = getSystem().execute(stmt);
				if (statResult.wasSuccessfull()) {				
	    			StringBuilder message = new StringBuilder("USE operation exit ");
	    			message.append(currentUseOperationCall.toLegacyString());
	    			message.append(" was succesfull.");
	    			fireNewLogMessage(Level.INFO,  message.toString());
	    			success = true;
				} else {
					fireNewLogMessage(Level.WARNING, "Exit of method "
							+ currentUseOperation.toString() + " failed.");
				}
			} catch (MSystemException e) {
				fireNewLogMessage(Level.SEVERE,
						"Error while exiting " + currentUseOperation.toString()
								+ ": " + e.getMessage());
			}
						
			if (!success) {
				hasFailedOperation = true;
				waitForUserInput();
			}
	    }

		
		/**
		 * @param type
		 */
		public void onNewVMTypeLoaded(Object adapterEventInformation, JVMType type) {
			fireNewLogMessage(Level.INFO, "New runtime class loaded: " + type.toString());
			MClass useClass = mappingHelper.getUseClass(type);
			registerOperationBreakPoints(useClass);
			//FIXME: Possibly a sub class of an abstracted superclass
			adapter.unregisterClassPrepareInterest(adapterEventInformation);
		}

		/**
		 * @param newInstance
		 */
		public void onNewVMObject(VMObject newInstance) {

			VMType vmType = newInstance.getType();
			
			try {
				MObject newObject = createInstance(vmType.getUSEClass(), newInstance);
				storeVMObject(newInstance.getId(), newInstance);
				fireNewLogMessage(Level.INFO, "New object " + newObject.name() + ":" + newObject.cls().name() + " created.");
			} catch (MSystemException e) {
				fireNewLogMessage(Level.SEVERE, "USE object for new instance of type " + vmType.getName() + " could not be created.");
			}
		}

		/**
		 * @param obj
		 * @param field
		 * @param useValue
		 */
		public void onUpdateAttribute(Object objId, Object fieldId, Value useValue) {
			if (!adapterObjectMapping.containsKey(objId))
				fireNewLogMessage(Level.WARNING, "No VMObject for id " + objId + " to set attribute for.");
			
			VMObject vmObject = adapterObjectMapping.get(objId);
			MObject useObject = vmObject.getUSEObject();
	    	
	    	if (useObject == null) {
				fireNewLogMessage(Level.WARNING,
						"USE-object was not set for VMObject " + vmObject);
				return;
	    	}
	    	
	    	if (!adapterFieldMapping.containsKey(fieldId)) {
	    		fireNewLogMessage(Level.WARNING,
						"Unknown VMField " + fieldId);
				return;
	    	}
	    	
	    	VMField field = adapterFieldMapping.get(fieldId);
	    	
	    	if (field.getUSEAttribute() != null) {
				MAttributeAssignmentStatement stmt = new MAttributeAssignmentStatement(new ExpObjRef(useObject), field.getUSEAttribute(), useValue);
	    		
				try {
					getSystem().execute(stmt);
				} catch (MSystemException e) {
					fireNewLogMessage(Level.WARNING, "Attribute " + StringUtil.inQuotes(field.getUSEAttribute().toString()) + " could not be set!");
				}
				
	    	} else if (field.getUSEAssociationEnd() != null) {
	    		// Link end
	    		MNavigableElement end = field.getUSEAssociationEnd();
	    		if (!end.isCollection()) {
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
		    				getSystem().execute(delStmt);
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
					if (useValue != null && !useValue.isUndefined()) {
						
						if (!useValue.isObject()) {
							fireNewLogMessage(Level.WARNING, "Need an USE object value for links!");
						}
						
						MObject newValue = ((ObjectValue)useValue).value();
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
							getSystem().execute(createStmt);
						} catch (MSystemException e) {
							fireNewLogMessage(Level.WARNING, "Could not create new link:" + e.getMessage());
						}
					}
	    		}
	    	} else {
	    		fireNewLogMessage(Level.WARNING, "No USE-Attribute or -Association end for VMField " + field + " was set.");
	    	}
		}
	}
}
