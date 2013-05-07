/**
 * 
 */
package org.tzi.use.monitor.adapter.clr;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.tzi.use.plugins.monitor.MonitorException;
import org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter;
import org.tzi.use.plugins.monitor.vm.adapter.InvalidAdapterConfiguration;
import org.tzi.use.plugins.monitor.vm.adapter.VMAdapterSetting;
import org.tzi.use.plugins.monitor.vm.mm.VMField;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMMethodCall;
import org.tzi.use.plugins.monitor.vm.mm.VMObject;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.plugins.monitor.vm.mm.clr.CLRField;
import org.tzi.use.plugins.monitor.vm.mm.clr.CLRObject;
import org.tzi.use.plugins.monitor.vm.mm.clr.CLRType;
import org.tzi.use.plugins.monitor.vm.wrap.clr.CLRFieldWrapRefArray;
import org.tzi.use.plugins.monitor.vm.wrap.clr.CLRFieldWrapBase;
import org.tzi.use.plugins.monitor.vm.wrap.clr.CLRFieldWrapReference;
import org.tzi.use.plugins.monitor.vm.wrap.clr.CLRFieldWrapValue;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.RealValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;

/**
 * @author Lars Hamann
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 *
 */
public class CLRAdapter extends AbstractVMAdapter {

	/**
	 * Static initializer to load the native clr adapter library.
	 */
	static {
		System.loadLibrary("clradapter");
	}	
	
	private static final int SETTING_PID = 0;
	
	private static final int SETTING_MAXINSTANCES = 1;
	
	/**
	 * true if the adapter is connected to a JVM.
	 */
	private boolean isConnected = false;
			
	/**
	 * The process id of the monitored application.
	 */
	private int pid;
	
	/**
	 * The maximum number of instances to read for a single type.
	 */
	private long maxInstances;
	
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.AbstractVMAdapter#validateSettings()
	 */
	@Override
	protected void validateSettings() throws InvalidAdapterConfiguration {
		if (settings.get(SETTING_PID) == null || settings.get(SETTING_PID).equals("") )
			throw new InvalidAdapterConfiguration("The PID is missing!"); 
				
		try {
			this.pid = Integer.parseInt(settings.get(SETTING_PID).value);
		} catch (NumberFormatException e) {
			throw new InvalidAdapterConfiguration("PID is not a number!");
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
		settings.add(SETTING_PID, new VMAdapterSetting("Process ID", ""));
		settings.add(SETTING_MAXINSTANCES, new VMAdapterSetting("Max. CLR instances", "10000"));
	}
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#attachToVM()
	 */
	@Override
	public void attachToVM() throws MonitorException {
		int res = 0;
		try {
			res = attachToCLR(pid);
		} catch (UnsatisfiedLinkError e){
			throw new MonitorException("Could not find CLRAdapter.attachToCLR().");
		}
		if (res != 0)
			throw new MonitorException("Could not connect to virtual machine with process ID " + Long.toString(pid));
		
		int i = 0;
		do
		{
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				throw new MonitorException("Could not wait until adapter is initialized.");
			}
			i++;
		} while (!isCLRAdapterInitialized() && i <= 3);
		
		
    	isConnected = true;
   	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#resume()
	 */
	@Override
	public void resume() {
		int res = resumeCLR();
		if (res != 0)
			controller.newLogMessage(this, Level.WARNING, "Could not resume CLR.");
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#suspend()
	 */
	@Override
	public void suspend() {
		int res = suspendCLR();
		if (res != 0)
			controller.newLogMessage(this, Level.WARNING, "Could not suspend CLR.");
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#stop()
	 */
	@Override
	public void stop() {
		int res = stopCLR();
    	isConnected = false;
		if (res != 0)
			controller.newLogMessage(this, Level.WARNING, "Could not stop CLR correctly.");
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#getVMType(java.lang.String)
	 */
	@Override
	public VMType getVMType(String name) {	
		return getCLRType(name);
	}

	public Set<VMObject> readInstances(CLRType clrType) {
		return getInstances(clrType);
	}
	
    public Value getUSEValue(CLRFieldWrapBase field) {
    	Value v = UndefinedValue.instance;
    	
    	if (field == null)
    		return v;
    	
    	if (field instanceof CLRFieldWrapValue) {
    		Object o = ((CLRFieldWrapValue) field).getValue();
    		
    		if (o instanceof Boolean) {
    			boolean b = ((Boolean) o).booleanValue();
    			v = BooleanValue.get(b);
    		} else if (o instanceof Character) {
    			String s = String.valueOf((Character) o);
    			v = new StringValue(s);    			
    		} else if (o instanceof Byte) {
    			v = IntegerValue.valueOf((Byte) o);
    		} else if (o instanceof Short) {
    			v = IntegerValue.valueOf((Short) o);
    		} else if (o instanceof Integer) {
    			v = IntegerValue.valueOf((Integer) o);
    		} else if (o instanceof Long) {
    			v = IntegerValue.valueOf((Integer) o);
    		} else if (o instanceof Float) {
    			v = new RealValue((Double) o);
    		} else if (o instanceof Double) {
    			v = new RealValue((Double) o);
    		} else if (o instanceof String) {
    			v = new StringValue((String) o);
    		} else {
    			controller.newLogMessage(this, Level.WARNING, "Unhandled type:" + o.getClass().toString());
    		}   		
    	} else if (field instanceof CLRFieldWrapReference) {
    		long ref = ((CLRFieldWrapReference) field).getReference();
    		
    		if (controller.existsVMObject(ref)) {
    			VMObject obj = controller.getVMObject(ref);
    			v = new ObjectValue(obj.getUSEObject().type(), obj.getUSEObject());
    		}
    	} else if (field instanceof CLRFieldWrapRefArray) {
    		// TODO: Treat other element types, too.
    		Set<Long> addresses = ((CLRFieldWrapRefArray) field).getReferences();
    		Value[] useValues = new Value[addresses.size()];
    		int i = 0;
    		
    		for (long ref : addresses) {
        		if (controller.existsVMObject(ref)) {
        			VMObject obj = controller.getVMObject(ref);
        			useValues[i] = new ObjectValue(obj.getUSEObject().type(), obj.getUSEObject());
        		}
        		++i;
			}
    		
    		v = new SequenceValue(TypeFactory.mkVoidType(), useValues);
		} else {
			controller.newLogMessage(this, Level.WARNING, "Unhandled type:" + field.toString());
		}
    	
    	return v;
    }
	
	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerClassPrepareEvent(java.lang.String)
	 */
	@Override
	public void registerClassPrepareEvent(String javaClassName) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#unregisterClassPrepareInterest(java.lang.Object)
	 */
	@Override
	public void unregisterClassPrepareInterest(Object adapterEventInformation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#isVMTypeLoaded(java.lang.String)
	 */
	@Override
	public boolean isVMTypeLoaded(String javaClassName) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerOperationCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMMethod)
	 */
	@Override
	public void registerOperationCallInterest(VMMethod m) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerMethodExit(org.tzi.use.plugins.monitor.vm.mm.VMMethodCall)
	 */
	@Override
	public void registerMethodExit(VMMethodCall call) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#unregisterOperationeExit(java.lang.Object)
	 */
	@Override
	public void unregisterOperationeExit(Object adapterExitInformation) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerConstructorCallInterest(org.tzi.use.plugins.monitor.vm.mm.VMType)
	 */
	@Override
	public void registerConstructorCallInterest(VMType vmType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#registerFieldModificationInterest(org.tzi.use.plugins.monitor.vm.mm.VMField)
	 */
	@Override
	public void registerFieldModificationInterest(VMField f) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.tzi.use.plugins.monitor.vm.adapter.VMAdapter#getMethodResultValue(java.lang.Object)
	 */
	@Override
	public Value getMethodResultValue(Object adapterExitInformation) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Microsoft CLR";
	}
	
	public CLRField getCLRFieldByName(CLRType type, String name)
	{
		return getFieldByName(type, name);
	}
	
	public CLRFieldWrapBase getFieldWrap(CLRType type, CLRObject object, CLRField field)
	{
		return getWrappedField(type, object, field);
	}
	
	public Set<VMType> getSuperClasses(CLRType type)
	{
		return getCLRSuperClasses(type);
	}
	
	public Set<VMType> getSubClasses(CLRType type)
	{
		return getCLRSubClasses(type);
	}
	
	public boolean isClassType(CLRType type)
	{
		return isCLRClassType(type);
	}
	
	private native Set<VMObject> getInstances(CLRType clrType);
	
	private native CLRType getCLRType(String name);
	
	private native int attachToCLR(long pid);
	
	private native int resumeCLR();

	private native int suspendCLR();
		
	private native int stopCLR();
	
	private native CLRField getFieldByName(CLRType type, String name);
	
	private native CLRFieldWrapBase getWrappedField(CLRType type, CLRObject object, CLRField field);
	
	private native boolean isCLRAdapterInitialized();
	
	private native boolean isCLRClassType(CLRType type);
	
	private native Set<VMType> getCLRSuperClasses(CLRType type);
	
	private native Set<VMType> getCLRSubClasses(CLRType type);	
	
	// debug information
	private native int getNumOfInstances();
	
	private native int getNumOfTypes();
	
	private native int getNumOfModules();
}
