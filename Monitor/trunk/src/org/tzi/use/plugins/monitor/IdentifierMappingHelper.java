package org.tzi.use.plugins.monitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.tzi.use.plugins.monitor.vm.mm.jvm.JVMType;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MObject;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;


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

	public IdentifierMappingHelper(MModel model) {
		this.model = model;
	}
	
	private void setupCashes() {
		// To be able to quickly check if a VMType is handled by the model
		Collection<MClass> useClasses = model.classes();
		
		HashSet<String> allHandledClassNames = new HashSet<String>(useClasses.size());
		for (MClass useClass : useClasses) {
			allHandledClassNames.add(getJavaClassName(useClass));
		}
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
    public String getJavaClassName(MClass cls) {
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
    public String getJavaFieldName(MAttribute a) {
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
    public String getJavaFieldName(MAssociationEnd end) {
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
	public String getJavaMethodName(MOperation operation) {
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
	public boolean methodMatches(Method method, MOperation useOperation) {
		// FIXME Handle parameter types
		try {
			if (getJavaMethodName(useOperation).equals(method.name()) &&
				method.argumentTypes().size() == useOperation.allParams().size()) {
				return true;
			}
		} catch (ClassNotLoadedException e) {
			return false;
		}
		
		return false;
	}

	/**
	 * Tries to map a runtime method to an operation defined in USE.  
	 * @param useObject The <code>MObject</code> to check for the operation.
	 * @param method The runtime method which should be mapped
	 * @return The corresponding USE operation or <code>null</code> if no operation is defined which matches <code>method</code>.
	 */
	public MOperation getUseOperation(MObject useObject, Method method) {
		for (MOperation op : useObject.cls().allOperations()) {
			if (methodMatches(method, op))
				return op;
		}
		
		return null;
	}

	private static class AttributeMapping {
		private final MClass cls;
		private final String attributeName;
		private final int hashCode;
		
		public AttributeMapping(MClass cls, String attributeName) {
			this.cls = cls;
			this.attributeName = attributeName;
			this.hashCode =  (cls.name() + attributeName).hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() { 
			return hashCode;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			AttributeMapping other = (AttributeMapping)obj;
			if (cls == other.cls && attributeName.equals(other.attributeName))
				return true;
			else
				return false;
		}
	}
	
	private Map<AttributeMapping, MAttribute> implementationAttributeMapping = new HashMap<AttributeMapping, MAttribute>();
	
	/**
	 * @param cls
	 * @param implementationName
	 * @return
	 */
	public MAttribute getUseAttribute(MClass cls, String implementationName) {
		AttributeMapping key = new AttributeMapping(cls, implementationName);
		
		if (!implementationAttributeMapping.containsKey(key)) {
			MAttribute attr = cls.attribute(implementationName, true);
			if (attr == null) {
				for (MAttribute at : cls.allAttributes()) {
					if (at.getAnnotationValue("Monitor", "name").equals(implementationName)) {
						attr = at;
						break;
					}
				}
			}
			
			implementationAttributeMapping.put(key, attr);
		}
		
		return implementationAttributeMapping.get(key);
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
	 * @param type
	 */
	public MClass getUseClass(JVMType type) {
		//FIXME: Implement
		return null;
	}
}
