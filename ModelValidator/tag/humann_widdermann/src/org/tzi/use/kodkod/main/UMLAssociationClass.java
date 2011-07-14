package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.HashMap;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MClass;

/**
 * interface for UMLAssociationClassImpl class
 * @author  Torsten Humann
 */
public interface UMLAssociationClass extends UMLClass, UMLAssociation{
	
	public void setLowerBound(int lb);
	
	public int getLowerBound();
	
	public void setUpperBound(int ub);
	
	public int getUpperBound();
	
	public MClass getMClass();
	
	public MAssociation getMAssociation();
	
	public MAssociationClass getMAssociationClass();
	
	public String getName();
	
	public String getTableName();
	
	public Relation getClaRelation();
	
	public Relation getAssRelation();
	
	public HashMap<String, Relation> getAttRelations();
	
	public Formula getFormula(SetKodkodStruc skks);
	
	public Bounds getClassBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks);
	
	public Bounds getAssociationBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks);
	
	public String getBoundName();
	
	public int getObjectCount();
	
	public int getLinkCount();
	
	public String getAttributeBoundName(int ind);
	
	public void setBoundNames(String attName, ArrayList<String> bn);
	
	public ArrayList<String> getBoundNames(String attName);
	
	public void setUMLAttributeNames(ArrayList<UMLAttributeNames> an);
	
	public ArrayList<UMLAttributeNames> getUMLAttributeNames();
	
	public boolean isAttributeEditable(String attName);
	
	public int getMaxIntValue(String attName);
	
	public int getMinIntValue(String attName);
	
	public ArrayList<String> getAssociatedClassAndSubclassName(int ind);
	
	public int getAssociatedClassMin(int ind);
	
	public int getAssociatedClassMax(int ind);
	
}