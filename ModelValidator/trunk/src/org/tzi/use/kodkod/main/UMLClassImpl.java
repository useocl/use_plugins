package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;

/**
 * creates the relations for an class, creates the formula,
 * creates the bounds
 * attribute relations, formulas and bounds for attributes of the associated
 * class are created too
 * max and min number of objects of the class are represented
 * by variable lowerBound and upperBound
 * @author  Torsten Humann
 */
public class UMLClassImpl implements UMLClass{
	
	private MSystemState curState;
	private final MClass mClass;
	private int lowerBound;
	private int upperBound;
	private ArrayList<UMLAttributeNames> boundNames;
	private Relation claRelation;
	private Relation parRelation;
	private HashMap<String, Relation> attRelations = new HashMap<String, Relation>();
	private HashMap<String, Relation> priRelations = new HashMap<String, Relation>();
	
	// ASSL-Extensions
	private HashMap<String, Expression> additionalSetForArribute = new HashMap<String, Expression>();
	private boolean usingAsslBounds = false;
	
	public UMLClassImpl(MSystem curSys, MClass mCla){
		curState = curSys.state();
		mClass = mCla;
		lowerBound = curSys.state().objectsOfClass(mCla).size();
		upperBound = curSys.state().objectsOfClass(mCla).size();
		boundNames = new ArrayList<UMLAttributeNames>();
		claRelation = Relation.unary(mClass.name());
		parRelation = Relation.unary("--" + mClass.name());
		for(int i = 0; i < mClass.allAttributes().size(); i++){
			MAttribute att = (MAttribute) mClass.allAttributes().get(i);
			attRelations.put(mClass.name() + "_-" + att.name(), Relation.nary(mClass.name() + "_-" + att.name(), 2));
			if(att.type().isReal() || att.type().isString()){
				priRelations.put(mClass.name() + "_-" + att.name() + "-" + att.type().toString(), Relation.unary(mClass.name() + "_-" + att.name() + "-" + att.type().toString()));
			}
			boundNames.add(new UMLAttributeNames(att.name(), att.type()));
		}
	}

	public void setLowerBound(int lb){
		lowerBound = lb;
	}
	
	public int getLowerBound(){
		return lowerBound;
	}
	
	public void setUpperBound(int ub){
		upperBound = ub;
	}
	
	public int getUpperBound(){
		return upperBound;
	}
	
	public MClass getMClass(){
		return mClass;
	}
	
	public String getName(){
		return mClass.name();
	}
	
	public String getTableName(){
		if(mClass.isAbstract()){
			return mClass.name() + " (abstract)";
		}else{
			return mClass.name();
		}
	}
	
	public Relation getClaRelation(){
		return claRelation;
	}
	
	public HashMap<String, Relation> getAttRelations(){
		return attRelations;
	}
	
	//builds the Kodkod formula for the associated class
	public Formula getFormula(SetKodkodStruc skks){
		
		Formula form = null;
		
		Expression exp = null;
		Relation tmpAttRelation;
		Relation tmpPriRelation;
		Variable p = Variable.unary("p");
		Relation tmpRelation = claRelation; // BUGFIX: used at the end for the last formula
											
		//children are in the associationclass relation
		if(mClass.children().size() > 0){
			exp = parRelation;
			for(int i = 0; i < mClass.children().size(); i++){
				MClass child = (MClass) mClass.children().toArray()[i];
				exp = exp.union(skks.getClassRelation(child.name()));
			}
			form = exp.eq(claRelation);
			// if subclasses, use the parRelation for the last formula
			tmpRelation = parRelation;
		}
		
		for(int i = 0; i < mClass.allParents().size(); i++){
			MClass parent = (MClass) mClass.allParents().toArray()[i];
			if(form == null){
				form = claRelation.in(skks.getClassRelation(parent.name()));
			}else{
				form = form.and(claRelation.in(skks.getClassRelation(parent.name())));
			}
		}
		
		//formula part for attributes of class
		for(int i = 0; i < mClass.allAttributes().size(); i++){
			MAttribute att = (MAttribute) mClass.allAttributes().get(i);
			tmpAttRelation = attRelations.get(mClass.name() + "_-" + att.name());
			if(att.type().isInteger()){
				tmpPriRelation = skks.getIDRelation();
			}else if(att.type().isBoolean()){
				tmpPriRelation = skks.getBoolRelation();
			}else if(att.type().isEnum()){
				tmpPriRelation = skks.getENumRelation(att.type().toString());
			}else if(att.type().isObjectType()){
				tmpPriRelation = skks.getClassRelation(att.type().toString());
			}else{
				tmpPriRelation = priRelations.get(mClass.name() + "_-" + att.name() + "-" + att.type().toString());
			}
			if(form == null){
				form = tmpAttRelation.join(Expression.UNIV).in(claRelation);
			}else{
				form = form.and(tmpAttRelation.join(Expression.UNIV).in(claRelation));
			}
			// get Additional Set
			//Expression additionalSet = this.additionalSetForArribute.get(att.name());
			if(usingAsslBounds) {
				form = form.and(Expression.UNIV.join(tmpAttRelation).in(tmpPriRelation.union(skks.getUndefinedRelation())));
			} else {
				form = form.and(Expression.UNIV.join(tmpAttRelation).in(tmpPriRelation));
			}
			if(att.type().isInteger()){
//				form = form.and(p.join(tmpAttRelation).count().gte(IntConstant.constant(getMinIntValue(att.name()))).forAll(p.oneOf(claRelation)));
//				form = form.and(p.join(tmpAttRelation).count().lte(IntConstant.constant(getMaxIntValue(att.name()))).forAll(p.oneOf(claRelation)));
				form = form.and(p.join(tmpAttRelation).count().gte(IntConstant.constant(getMinIntValue(att.name()))).forAll(p.oneOf(tmpRelation)));
				form = form.and(p.join(tmpAttRelation).count().lte(IntConstant.constant(getMaxIntValue(att.name()))).forAll(p.oneOf(tmpRelation)));
			//}else if(att.type().isBoolean()){
			//	form = form.and(p.join(tmpAttRelation).lone().forAll(p.oneOf(claRelation)));
			}else{
				//BUGFIX: form = form.and(p.join(tmpAttRelation).one().forAll(p.oneOf(claRelation)));
				form = form.and(p.join(tmpAttRelation).one().forAll(p.oneOf(tmpRelation)));
			}
		}
		return form;
	}
	
	//sets the bounds of relations of the class (with all attributes)
	public Bounds getClassBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks, ArrayList<String> possibleAttributes){
		
		ArrayList<String> attributeNames = new ArrayList<String>();
		ArrayList<String> attributeObjects = new ArrayList<String>();
		ArrayList<String[]> attributesLower = new ArrayList<String[]>();
		ArrayList<ArrayList<String>> attributesUpper = new ArrayList<ArrayList<String>>();
		
		int val = 0;
		
		TupleSet tsClassLower = tFa.noneOf(1);
		TupleSet tsClassUpper = tFa.noneOf(1);
		TupleSet tsParClaLower = tFa.noneOf(1);
		TupleSet tsParClaUpper = tFa.noneOf(1);
		TupleSet tsOnlClaLower = tFa.noneOf(1);
		TupleSet tsOnlClaUpper = tFa.noneOf(1);
		TupleSet tsAttributeLower = tFa.noneOf(2);
		TupleSet tsAttributeUpper = tFa.noneOf(2);
		TupleSet tsPrimitive = tFa.noneOf(1);
		
		Relation tmpRelation;
		
		if(bou.lowerBound(claRelation) != null){
			tsClassLower.addAll(bou.lowerBound(claRelation));
		}
		if(bou.upperBound(claRelation) != null){
			tsClassUpper.addAll(bou.upperBound(claRelation));
		}
		for(int i = 0; i < lowerBound; i++){
			if(i < curState.objectsOfClass(mClass).size()){
				MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[i];
				tsClassLower.add(tFa.tuple(getBoundName() + (i + 1) + "_" + obj.name()));
				tsOnlClaLower.add(tFa.tuple(getBoundName() + (i + 1) + "_" + obj.name()));
			}else{
				tsClassLower.add(tFa.tuple(getBoundName() + (i + 1)));
				tsOnlClaLower.add(tFa.tuple(getBoundName() + (i + 1)));
			}
		}
		tsClassUpper.addAll(tsClassLower);
		tsOnlClaUpper.addAll(tsOnlClaLower);
		for(int i = lowerBound; i < upperBound; i++){
			tsClassUpper.add(tFa.tuple(getBoundName() + (i + 1)));
			tsOnlClaUpper.add(tFa.tuple(getBoundName() + (i + 1)));
		}
		if(tsClassLower.size() == 0){
			bou.bound(claRelation, tsClassUpper);
		}else{
			bou.bound(claRelation, tsClassLower, tsClassUpper);
		}
		if(mClass.children().size() > 0){
			if(tsOnlClaLower.size() == 0){
				bou.bound(parRelation, tsOnlClaUpper);
			}else{
				bou.bound(parRelation, tsOnlClaLower, tsOnlClaUpper);
			}
		}
		
		//sets bounds in bound of parent classes too
		for(int i = 0; i < mClass.allParents().size(); i++){
			MClass parent = (MClass) mClass.allParents().toArray()[i];
			tsParClaLower.clear();
			tsParClaUpper.clear();
			tmpRelation = skks.getClassRelation(parent.name());
			if(bou.lowerBound(tmpRelation) != null){
				tsParClaLower.addAll(bou.lowerBound(tmpRelation));
			}
			if(bou.upperBound(tmpRelation) != null){
				tsParClaUpper.addAll(bou.upperBound(tmpRelation));
			}
			tsParClaLower.addAll(tsClassLower);
			tsParClaUpper.addAll(tsClassUpper);
			if(tsParClaLower.size() == 0){
				bou.bound(tmpRelation, tsParClaUpper);
			}else{
				bou.bound(tmpRelation, tsParClaLower, tsParClaUpper);
			}
		}
		
		//sets the bounds for attributes
		for(int i = 0; i < mClass.allAttributes().size(); i++){
			tsAttributeLower.clear();
			tsAttributeUpper.clear();
			tsPrimitive.clear();
			MAttribute att = (MAttribute) mClass.allAttributes().get(i);

			//attribute type is enumeration
			if(att.type().isEnum()){
				attributeNames.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for(int j = 0; j < upperBound; j++){
					if(j < curState.objectsOfClass(mClass).size()){
						MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[j];
						EnumType enType = (EnumType) att.type();
						attributeNames.add(getBoundName() + (j + 1) + "_" + obj.name());
						if(!obj.state(curState).attributeValue(att).toString().equals("Undefined")){
							tsAttributeLower.add(tFa.tuple(getBoundName() + (j + 1) + "_" + obj.name(), enType.name() + "_" + obj.state(curState).attributeValue(att).toString()));
						}
					}else{
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				attributesUpper.add(attributeNames);
				EnumType enType = (EnumType) att.type();
				Iterator<?> it = enType.literals();
				while (it.hasNext() ) {
		            String lit = (String) it.next();
		            attributeObjects.add(enType.name() + "_#" + lit);
		        }
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for(int j = 0; j < attributesUpper.size(); j++){
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
			//attribute type is integer (number of pairs represent the value of attribute)
			}else if(att.type().isInteger()){
				for(int j = 0; j < upperBound; j++){
					if(j < curState.objectsOfClass(mClass).size()){
						MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[j];
						if(obj.state(curState).attributeValue(att).toString().equals("Undefined")){
							val = 1;
						}else{
							val = new Integer(obj.state(curState).attributeValue(att).toString()).intValue();
						}
						if(val > skks.getMaxIntValue()){
							val = skks.getMaxIntValue();
						}
						for(int k = 0; k < val; k++){
							tsAttributeLower.add(tFa.tuple(getBoundName() + (j + 1) + "_" + obj.name(), "@id" + k));
							tsAttributeUpper.add(tFa.tuple(getBoundName() + (j + 1) + "_" + obj.name(), "@id" + k));
						}
					}else{
						for(int k = 0; k < getMaxIntValue(att.name()); k++){
							tsAttributeUpper.add(tFa.tuple(getBoundName() + (j + 1), "@id" + k));
						}
					}
				}
			//attribute type is boolean
			}else if(att.type().isBoolean()){
				attributeNames.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for(int j = 0; j < upperBound; j++){
					if(j < curState.objectsOfClass(mClass).size()){
						MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[j];
						if(!obj.state(curState).attributeValue(att).toString().equals("Undefined")){
							tsAttributeLower.add(tFa.tuple(getBoundName() + (j + 1) + "_" + obj.name(), "@" + obj.state(curState).attributeValue(att).toString()));
							tsAttributeUpper.add(tFa.tuple(getBoundName() + (j + 1) + "_" + obj.name(), "@" + obj.state(curState).attributeValue(att).toString()));
						}else{
							attributeNames.add(getBoundName() + (j + 1) + "_" + obj.name());
						}
					}else{
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				attributeObjects.add("@true");
				attributeObjects.add("@false");
				attributesUpper.add(attributeNames);
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for(int j = 0; j < attributesUpper.size(); j++){
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
			//attribute type is String or Real
			}else if(att.type().isString() || att.type().isReal()){
				attributeNames.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for(int j = 0; j < upperBound; j++){
					if(j < curState.objectsOfClass(mClass).size()){
						MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[j];
						attributeNames.add(getBoundName() + (j + 1) + "_" + obj.name());
						if(!obj.state(curState).attributeValue(att).toString().equals("Undefined")){
							tsPrimitive.add(tFa.tuple(getAttributeBoundName(i) + (j + 1) + "_" + obj.state(curState).attributeValue(att).toString()));
							tsAttributeLower.add(tFa.tuple(getBoundName() + (j + 1) + "_" + obj.name(), getAttributeBoundName(i) + (j + 1) + "_" + obj.state(curState).attributeValue(att).toString()));
							attributeObjects.add(getAttributeBoundName(i) + (j + 1) + "_" + obj.state(curState).attributeValue(att).toString());
						}
					}else{
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				for(int j = 0; j < getBoundNames(att.name()).size(); j++){
					if(att.type().isString()){
						tsPrimitive.add(tFa.tuple(getAttributeBoundName(i) + (j + 1 + curState.objectsOfClass(mClass).size()) + "_'" + getBoundNames(att.name()).get(j) + "'"));
						attributeObjects.add(getAttributeBoundName(i) + (j + 1 + curState.objectsOfClass(mClass).size()) + "_'" + getBoundNames(att.name()).get(j) + "'");
					}else{
						tsPrimitive.add(tFa.tuple(getAttributeBoundName(i) + (j + 1 + curState.objectsOfClass(mClass).size()) + "_" + getBoundNames(att.name()).get(j)));
						attributeObjects.add(getAttributeBoundName(i) + (j + 1 + curState.objectsOfClass(mClass).size()) + "_" + getBoundNames(att.name()).get(j));
					}
				}
				attributesUpper.add(attributeNames);
				attributesUpper.add(attributeObjects);
				// ADDING ASSL-Specific possible attribute values
     			attributeObjects.addAll(possibleAttributes);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for(int j = 0; j < attributesUpper.size(); j++){
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
			//attribute type is class or associationclass
			}else if(att.type().isObjectType()){
				attributeNames.clear();
				attributesLower.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for(int j = 0; j < upperBound; j++){
					if(j < curState.objectsOfClass(mClass).size()){
						MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[j];
						attributeNames.add(getBoundName() + (j + 1) + "_" + obj.name());
						attributesLower.add(new String[]{getBoundName() + (j + 1) + "_" + obj.name(), obj.state(curState).attributeValue(att).toString()});
					}else{
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				UMLClass tmpUMLCla = skks.getUMLClass(att.type().toString());
				for(int j = 0; j < tmpUMLCla.getUpperBound(); j++){
					if(j < tmpUMLCla.getObjectCount()){
						MObject obj = (MObject) curState.objectsOfClass(tmpUMLCla.getMClass()).toArray()[j];
						for(int k = 0; k < attributesLower.size(); k++){
							if(attributesLower.get(k)[1].substring(1).equals(obj.name())){
								tsAttributeLower.add(tFa.tuple(attributesLower.get(k)[0], tmpUMLCla.getBoundName() + (j + 1) + "_" + obj.name()));
							}
						}
						attributeObjects.add(tmpUMLCla.getBoundName() + (j + 1) + "_" + obj.name());
					}else{
						attributeObjects.add(tmpUMLCla.getBoundName() + (j + 1));
					}
				}
				attributesUpper.add(attributeNames);
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for(int j = 0; j < attributesUpper.size(); j++){
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
			}
			if(lowerBound == 0){
				bou.bound(attRelations.get(mClass.name() + "_-" + att.name()), tsAttributeUpper);
			}else{
				bou.bound(attRelations.get(mClass.name() + "_-" + att.name()), tsAttributeLower, tsAttributeUpper);
			}
			if(att.type().isString() || att.type().isReal()){
				bou.bound(priRelations.get(mClass.name() + "_-" + att.name() + "-" + att.type().toString()), tsPrimitive);
			}
		}
		return bou;
	}
	
	//sets the bounds of relations of the class (with all attributes)
	public Bounds getClassBoundsAssl(Bounds bou, TupleFactory tFa, SetKodkodStruc skks, AsslTranslation asslTranslation){
		
		ArrayList<String> attributeNames = new ArrayList<String>();
		ArrayList<String> attributeObjects = new ArrayList<String>();
		ArrayList<ArrayList<String>> attributesUpper = new ArrayList<ArrayList<String>>();

		ArrayList<MObject> asslObjects = asslTranslation.getClassObjectBound().get(getName());
		HashMap<MObject, HashMap<String, ArrayList<String>>> asslObjectAttributeMapping = asslTranslation.getObjectAttributeUpperMapping().get(getName());
		HashMap<MObject, HashMap<String, String>> asslObjectAttributeLowerMapping = asslTranslation.getObjectAttributeLowerMapping().get(getName());
		
		int val = 0;
		
		TupleSet tsClassExact = tFa.noneOf(1);
		TupleSet tsParClaExact = tFa.noneOf(1);
		TupleSet tsOnlClaExact = tFa.noneOf(1);
		TupleSet tsAttributeLower = tFa.noneOf(2);
		TupleSet tsAttributeUpper = tFa.noneOf(2);
		TupleSet tsPrimitive = tFa.noneOf(1);
		
		Relation tmpRelation;
		
		if(bou.lowerBound(claRelation) != null){
			tsClassExact.addAll(bou.lowerBound(claRelation));
		}
		
		if(asslObjects != null) {
			for(MObject asslObject : asslObjects) {
				tsClassExact.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject)));
				tsOnlClaExact.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject)));
			}
		}
		//tsClassExact.addAll(tFa.setOf(asslObjects));
		//tsOnlClaExact.addAll(tFa.setOf(asslObjects));
		
		bou.boundExactly(claRelation, tsClassExact);
		
		if(mClass.children().size() > 0){
			bou.boundExactly(parRelation, tsOnlClaExact);
		}
		
		//sets bounds in bound of parent classes too
		for(int i = 0; i < mClass.allParents().size(); i++){
			MClass parent = (MClass) mClass.allParents().toArray()[i];
			tsParClaExact.clear();
			tmpRelation = skks.getClassRelation(parent.name());
			if(bou.upperBound(tmpRelation) != null){
				tsParClaExact.addAll(bou.upperBound(tmpRelation));
			}
			tsParClaExact.addAll(tsClassExact);
			bou.boundExactly(tmpRelation, tsParClaExact);
		}
		
		//sets the bounds for attributes
		for(int i = 0; i < mClass.allAttributes().size(); i++){
			tsAttributeLower.clear();
			tsAttributeUpper.clear();
			tsPrimitive.clear();
			MAttribute att = (MAttribute) mClass.allAttributes().get(i);
			
			// When no Objects, dont try to fill them with attributes
			if(asslObjects != null) {
				//attribute type is enumeration
				if(att.type().isEnum()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();
					EnumType enType = (EnumType) att.type();
					// add assl Attributes
					for(MObject asslObject : asslObjects) {
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = enType.name() + "_#" + asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								String value = enType.name() + "_#" + objectAttributeValue;
								tsPrimitive.add(tFa.tuple(value));
								tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), objName));
						}
					}
				//attribute type is integer (number of pairs represent the value of attribute)
				}else if(att.type().isInteger()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();
					
					for(MObject asslObject : asslObjects) {
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							val = (new Integer(asslObjectAttributeLowerMapping.get(asslObject).get(att.name()))).intValue();
							for(int k = 0; k < val; k++){
								tsPrimitive.add(tFa.tuple("@id"+k));
								tsAttributeLower.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), "@id"+k));
								tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), "@id"+k));
							}
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								val = (new Integer(objectAttributeValue)).intValue();
								for(int k = 0; k < val; k++){
									tsPrimitive.add(tFa.tuple("@id"+k));
									tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), "@id"+k));
								}
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), objName));
						}
					}
					tsAttributeUpper.addAll(tsAttributeLower);
				//attribute type is boolean
				} else if(att.type().isBoolean()){
					attributeNames.clear();
					attributeObjects.clear();
					
					// add assl Attributes
					for(MObject asslObject : asslObjects) {
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = "@" + asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								String value = "@" + objectAttributeValue;
								tsPrimitive.add(tFa.tuple(value));
								tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), objName));
						}
					}
				//attribute type is String or Real
				} else  if(att.type().isString() || att.type().isReal()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();
					
					// add assl Attributes
					for(MObject asslObject : asslObjects) {
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = getAttributeBoundName(i) + 1 + "_" + asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							int k = 0;
							for(String objectAttributeValue: objectAttributeValues) {
								String value = getAttributeBoundName(i) + (k + 1) + "_" + objectAttributeValue;
								tsPrimitive.add(tFa.tuple(value));
								tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
								k++;
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), objName));
						}
					}
				//attribute type is class or associationclass
				} else if(att.type().isObjectType()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();

					// add assl Attributes
					for(MObject asslObject : asslObjects) {
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								tsPrimitive.add(tFa.tuple(objectAttributeValue));
								tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), objectAttributeValue));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(asslTranslation.getKodKodObjectName(asslObject), objName));
						}
					}
				}
			}
			if(tsAttributeLower.size() == 0){
				bou.bound(attRelations.get(mClass.name() + "_-" + att.name()), tsAttributeUpper);
			}else{
				bou.bound(attRelations.get(mClass.name() + "_-" + att.name()), tsAttributeLower, tsAttributeUpper);
			}
			if(att.type().isString() || att.type().isReal()){
				bou.bound(priRelations.get(mClass.name() + "_-" + att.name() + "-" + att.type().toString()), tsPrimitive);
			}
		}
		return bou;
	}
	
	//creating a unique name for the atom
	public String getBoundName(){
		return mClass.name().substring(0, 1).toLowerCase().concat(mClass.name().substring(1)) + "@";
	}
	
	public int getObjectCount(){
		return curState.objectsOfClass(mClass).size();
	}
	
	//creating a unique name for a value of an attribute
	public String getAttributeBoundName(int ind){
		MAttribute att = (MAttribute) mClass.allAttributes().get(ind);
		return (mClass.name() + "_" + att.name()).substring(0, 1).toLowerCase() + (mClass.name() + "_" + att.name()).substring(1);
	}
	
	//sets the possible values of an attribute
	public void setBoundNames(String attName, ArrayList<String> bn){
		for(int i = 0; i < boundNames.size(); i++){
			if(boundNames.get(i).getAttributeName().equals(attName)){
				boundNames.get(i).setBoundNames(bn);
			}
		}
	}
	
	//get the possible values of an attribute
	public ArrayList<String> getBoundNames(String attName){
		for(int i = 0; i < boundNames.size(); i++){
			if(boundNames.get(i).getAttributeName().equals(attName)){
				return boundNames.get(i).getBoundNames();
			}
		}
		return null;
	}
	
	public void setUMLAttributeNames(ArrayList<UMLAttributeNames> an){
		boundNames.clear();
		boundNames = an;
	}
	
	public ArrayList<UMLAttributeNames> getUMLAttributeNames(){
		return boundNames;
	}
	
	public boolean isAttributeEditable(String attName){
		for(int i = 0; i < boundNames.size(); i++){
			if(boundNames.get(i).getAttributeName().equals(attName)){
				return boundNames.get(i).isEditable();
			}
		}
		return false;
	}
	
	public int getMaxIntValue(String attName){
		for(int i = 0; i < boundNames.size(); i++){
			if(boundNames.get(i).getAttributeName().equals(attName)){
				return boundNames.get(i).getMaxValue();
			}
		}
		return 0;
	}
	
	public int getMinIntValue(String attName){
		for(int i = 0; i < boundNames.size(); i++){
			if(boundNames.get(i).getAttributeName().equals(attName)){
				return boundNames.get(i).getMinValue();
			}
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.kodkod.main.UMLClass#getPossibleObjects()
	 */
	@Override
	public ArrayList<String> getPossibleObjects() {
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < lowerBound; i++){
			if(i < curState.objectsOfClass(mClass).size()){
				MObject obj = (MObject) curState.objectsOfClass(mClass).toArray()[i];
				result.add(getBoundName() + (i + 1) + "_" + obj.name());
			}else{
				result.add(getBoundName() + (i + 1));
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.kodkod.main.UMLClass#setAdditionalSetForAttribute(kodkod.ast.Expression, java.lang.String)
	 */
	public void setAdditionalSetForAttribute(Expression values, String name) {
		Expression val = additionalSetForArribute.get(name);
		if(val != null){
			additionalSetForArribute.put(name, val.union(values));
		} else {
			additionalSetForArribute.put(name, values);
		}
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.kodkod.main.UMLClass#usingAsslBounds(boolean)
	 */
	@Override
	public void usingAsslBounds(boolean usingAsslBounds) {
		// TODO Auto-generated method stub
		this.usingAsslBounds = usingAsslBounds;
	}
}