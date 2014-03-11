package org.tzi.use.plugin.filmstrip.logic;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.type.Type;

/**
 * This interface provides functions to transform model elements.
 * 
 * @author frank
 */
public interface MModelConnector {
	
	//TODO rename to mapX()
	public MClass processClass(MClass cls);
	public MAttribute processAttribute(MAttribute attr);
	public MOperation processOperation(MOperation operation);
	public MAssociation processAssociation(MAssociation assoc);
	public MAssociationClass processAssociationClass(MAssociationClass cls);
	public MNavigableElement processNavigableElement(MNavigableElement source);
	public MAssociationEnd processAssociationEnd(MAssociationEnd end);
	public Type processType(Type t);
	
}
