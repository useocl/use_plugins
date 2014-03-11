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
	
	public MClass mapClass(MClass cls);
	public MAttribute mapAttribute(MAttribute attr);
	public MOperation mapOperation(MOperation operation);
	public MAssociation mapAssociation(MAssociation assoc);
	public MAssociationClass mapAssociationClass(MAssociationClass cls);
	public MNavigableElement mapNavigableElement(MNavigableElement source);
	public MAssociationEnd mapAssociationEnd(MAssociationEnd end);
	public Type mapType(Type t);
	
}
