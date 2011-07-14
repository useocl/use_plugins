package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.HashMap;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;

import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MClass;

/**
 * interface for UMLClassImpl class
 * @author  Torsten Humann
 */
public interface UMLClass{
	
	public void setLowerBound(int lb);
	
	public int getLowerBound();
	
	public void setUpperBound(int ub);
	
	public int getUpperBound();
	
	public MClass getMClass();
	
	public String getName();
	
	public String getTableName();
	
	public Relation getClaRelation();
	
	public HashMap<String, Relation> getAttRelations();
	
	public Formula getFormula(SetKodkodStruc skks);
	
	public Bounds getClassBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks, ArrayList<String> possibleAttributes);
	
	public Bounds getClassBoundsAssl(Bounds bou, TupleFactory tFa, SetKodkodStruc skks, AsslTranslation asslTranslation);
	
	public String getBoundName();
	
	public int getObjectCount();
	
	public String getAttributeBoundName(int ind);
	
	public void setBoundNames(String attName, ArrayList<String> bn);
	
	public ArrayList<String> getBoundNames(String attName);
	
	public void setUMLAttributeNames(ArrayList<UMLAttributeNames> an);
	
	public ArrayList<UMLAttributeNames> getUMLAttributeNames();
	
	public boolean isAttributeEditable(String attName);
	
	public int getMaxIntValue(String attName);
	
	public int getMinIntValue(String attName);
	
	public ArrayList<String> getPossibleObjects();

	/**
	 * @param values
	 * @param name
	public void setAdditionalSetForAttribute(Expression values, String name);
	 */
	
	public void usingAsslBounds(boolean usingAsslBounds);
}