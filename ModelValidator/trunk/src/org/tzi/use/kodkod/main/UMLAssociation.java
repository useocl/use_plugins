package org.tzi.use.kodkod.main;

import java.util.ArrayList;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;

import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MAssociation;

/**
 * interface for UMLAssociationImpl class
 * @author  Torsten Humann
 */
public interface UMLAssociation{
	
	public void setLowerBound(int lb);
	
	public int getLowerBound();
	
	public void setUpperBound(int ub);
	
	public int getUpperBound();
	
	public MAssociation getMAssociation();
	
	public String getName();
	
	public Relation getAssRelation();
	
	public Formula getFormula(SetKodkodStruc skks);
	
	public Bounds getAssociationBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks);
	
	public int getLinkCount();
	
	public ArrayList<String> getAssociatedClassAndSubclassName(int ind);
	
	public int getAssociatedClassMin(int ind);
	
	public int getAssociatedClassMax(int ind);

	public Bounds getAssociationBoundsAssl(Bounds bou, TupleFactory tFa,
			SetKodkodStruc setKodkodStruc, AsslTranslation asslTranslation);
	
	public void usingAsslBounds(boolean isUsingAsslBound);
}