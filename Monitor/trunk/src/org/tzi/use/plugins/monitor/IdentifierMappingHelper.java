package org.tzi.use.plugins.monitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.tzi.use.plugins.monitor.vm.adapter.VMAccessException;
import org.tzi.use.plugins.monitor.vm.mm.VMMethod;
import org.tzi.use.plugins.monitor.vm.mm.VMType;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MObject;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;


/**
 * This class is used to map names of model elements to 
 * names of monitored elements, i. e., runtime elements.
 * 
 * This class could be static right now, but maybe some sort
 * of caching is used later.
 *  
 * @author Lars Hamann
 */
public class IdentifierMappingHelper {
	
	/**
	 * A reference to the model, to allow to check annotation values.
	 */
	private MModel model;

	/**
	 * To be able to quickly check if a VMType is handled by the model and by which class.
	 * The key is {@link VMType#getName()}.
	 * A single USE class can be used for more than one runtime class (abstracted superclass).
	 */
	Map<String, MClass> vmTypeToUSEClass;
	
	/**
     * A cache for the model classes to runtime types mappings.
     * A single model class can be represented by more than one
     * runtime type, because the runtime sub classes could be ignored
     * in the model (abstracted superclass).
     */
    private SetMultimap<MClass, VMType> useClassToVMTypes;
    
	public IdentifierMappingHelper(MModel model) {
		this.model = model;
		setupCashes();
	}
	
	/**
	 * Sets up some caches for faster access.
	 */
	private void setupCashes() {
		Collection<MClass> useClasses = model.classes();
		vmTypeToUSEClass = new HashMap<String, MClass>(useClasses.size());
		
		for (MClass useClass : useClasses) {
			vmTypeToUSEClass.put(getVMClassName(useClass), useClass);
		}
		
		useClassToVMTypes = HashMultimap.create(useClasses.size(), 1);
	}
	
	/**
     * Returns the Java class name of an {@link MClass}.
     * The qualified name (<code>package.classname</code>) is constructed as follows:
     * <h1>package</h1>
     * <p>
     * If the class is annotated with <code>@Monitor(package="...")</code>, this package name
     * is used. If not the model is queried for an annotation value <code>@Monitor(defaultPackage="...")</code>. 
     * If such a value is present it is used as the package name. Otherwise no package name is used.
     * </p>
     * 
     * <h1>classname</h1>
     * <p>If the class is annotated with <code>@Monitor(name="...")</code>, this name is used.
     *    Otherwise the name of <code>cls</code> is used.
     * </p>
     * 
     * If <code>package</code> is empty, only <code>classname</code> is used.
     * @param cls The class to get the runtime name for.
     * @return The specified runtime name of the class.
     */
    public String getVMClassName(MClass cls) {
    	String classPackage = cls.getAnnotationValue("Monitor", "package");
    	String className = cls.getAnnotationValue("Monitor", "name");
    	
    	if (className == "")
    		className = cls.name();
    	
    	if (classPackage == "")
    		classPackage = model.getAnnotationValue("Monitor", "defaultPackage");
    	
    	return classPackage + (classPackage.equals("") ? "" : ".") + className;
    }
    
    /**
     * Returns the runtime name of an {@link MAttribute}.
     * If the attribute has an annotation <code>@Monitor(name="aName")</code>, <code>aName</code> is returned.
     * Otherwise <code>a.name()</code>.
     * @param a The attribute to get the name for.
     * @return The runtime name of <code>a</code>.
     */
    public String getVMFieldName(MAttribute a) {
		String name = a.getAnnotationValue("Monitor", "name");
		if (name == "") {
			name = a.name();
		}
		return name;
	}
    
    /**
     * Returns the runtime name of an {@link MAssociationEnd}.
     * If the end has an annotation <code>@Monitor(name="aName")</code>, <code>aName</code> is returned.
     * Otherwise <code>end.nameAsRolename()</code>.
     * @param end The association end to get the name for.
     * @return The runtime name of <code>end</code>.
     */
    public String getVMFieldName(MAssociationEnd end) {
		String name = end.getAnnotationValue("Monitor", "name");
		if (name == "") {
			name = end.nameAsRolename();
		}
		return name;
	}

    /**
     * Returns the runtime name of an {@link MOperation}.
     * If the operation has an annotation <code>@Monitor(name="aName")</code>, <code>aName</code> is returned.
     * Otherwise <code>operation.name()</code>.
     * @param operation The operation to get the name for.
     * @return The runtime name of <code>operation</code>.
     */
	public String getVMMethodName(MOperation operation) {
		String name = operation.getAnnotationValue("Monitor", "name");
		if (name == "") {
			name = operation.name();
		}
		return name;
	}

	/**
	 * Returns <code>true</code> if the runtime method <code>method</code> matches
	 * the USE operation <code>useOperation</code>.
	 * @param method The runtime method.
	 * @param useOperation The use method to check against.
	 * @return <code>true</code> if both operations match.
	 */
	public boolean methodMatches(VMMethod method, MOperation useOperation) {
		// FIXME Handle parameter types
		try {
			if (getVMMethodName(useOperation).equals(method.getName()) &&
				method.getArgumentTypes().size() == useOperation.paramList().size()) {
				return true;
			}
		} catch (VMAccessException e) {}
		
		return false;
	}

	/**
	 * Tries to map a runtime method to an operation defined in USE.  
	 * @param useObject The <code>MObject</code> to check for the operation.
	 * @param method The runtime method which should be mapped
	 * @return The corresponding USE operation or <code>null</code> if no operation is defined which matches <code>method</code>.
	 */
	public MOperation getUseOperation(MObject useObject, VMMethod method) {
		for (MOperation op : useObject.cls().allOperations()) {
			if (methodMatches(method, op))
				return op;
		}
		
		return null;
	}
	
	/**
	 * @param cls
	 * @param implementationName
	 * @return
	 */
	public MAttribute getUseAttribute(MClass cls, String implementationName) {
		
		MAttribute attr = cls.attribute(implementationName, true);
		if (attr == null) {
			for (MAttribute at : cls.allAttributes()) {
				if (at.getAnnotationValue("Monitor", "name").equals(implementationName)) {
					attr = at;
					break;
				}
			}
		}
		
		return attr;
	}

	/**
	 * @param t
	 * @return
	 */
	public Object getVMEnumName(EnumType t) {
		String classPackage = t.getAnnotationValue("Monitor", "package");
    	String className = t.getAnnotationValue("Monitor", "name");
    	
    	if (className == "")
    		className = t.name();
    	
    	if (classPackage == "")
    		classPackage = model.getAnnotationValue("Monitor", "defaultPackage");
    	
    	return classPackage + (classPackage.equals("") ? "" : ".") + className;
	}

	/**
	 * Returns the USE Class for a given VMType identified by its name. 
	 * @param type
	 */
	public MClass getUseClass(VMType type) {
		return vmTypeToUSEClass.get(type.getName());
	}
	
	/**
	 * Returns all VMTypes linked to the given USE class.
	 * @param cls
	 * @return
	 */
	public Set<VMType> getVMTypes(MClass cls) {
		return useClassToVMTypes.get(cls);
	}
	
	/**
	 * Returns <code>true</code> if the given VMType is mapped to
	 * a USE class.
	 * @param t
	 * @return
	 */
	public boolean isVMTypeMapped(VMType t) {
		return vmTypeToUSEClass.containsKey(t.getName());
	}
	
	public void addHandledVMType(VMType type, MClass useClass) {
		vmTypeToUSEClass.put(type.getName(), useClass);
		useClassToVMTypes.put(useClass, type);
	}
}
