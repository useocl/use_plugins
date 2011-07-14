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

package org.tzi.use.kodkod.assl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.tzi.use.gen.assl.statics.GAttributeAssignment;
import org.tzi.use.gen.assl.statics.GIfThenElse;
import org.tzi.use.gen.assl.statics.GInstrAny_Seq;
import org.tzi.use.gen.assl.statics.GInstrCreateN_C_Integer;
import org.tzi.use.gen.assl.statics.GInstrCreate_C;
import org.tzi.use.gen.assl.statics.GInstrDelete_Assoc_Linkends;
import org.tzi.use.gen.assl.statics.GInstrDelete_Object;
import org.tzi.use.gen.assl.statics.GInstrInsert_Assoc_Linkends;
import org.tzi.use.gen.assl.statics.GInstrSub_Seq;
import org.tzi.use.gen.assl.statics.GInstrSub_Seq_Integer;
import org.tzi.use.gen.assl.statics.GInstrTry_Assoc_LinkendSeqs;
import org.tzi.use.gen.assl.statics.GInstrTry_Seq;
import org.tzi.use.gen.assl.statics.GInstruction;
import org.tzi.use.gen.assl.statics.GInstructionList;
import org.tzi.use.gen.assl.statics.GLoop;
import org.tzi.use.gen.assl.statics.GOCLExpression;
import org.tzi.use.gen.assl.statics.GValueInstruction;
import org.tzi.use.gen.assl.statics.GVariableAssignment;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpBagLiteral;
import org.tzi.use.uml.ocl.expr.ExpCollect;
import org.tzi.use.uml.ocl.expr.ExpCollectionLiteral;
import org.tzi.use.uml.ocl.expr.ExpConstBoolean;
import org.tzi.use.uml.ocl.expr.ExpConstEnum;
import org.tzi.use.uml.ocl.expr.ExpConstInteger;
import org.tzi.use.uml.ocl.expr.ExpConstReal;
import org.tzi.use.uml.ocl.expr.ExpConstString;
import org.tzi.use.uml.ocl.expr.ExpEmptyCollection;
import org.tzi.use.uml.ocl.expr.ExpExists;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpIf;
import org.tzi.use.uml.ocl.expr.ExpIsKindOf;
import org.tzi.use.uml.ocl.expr.ExpIsTypeOf;
import org.tzi.use.uml.ocl.expr.ExpIsUnique;
import org.tzi.use.uml.ocl.expr.ExpIterate;
import org.tzi.use.uml.ocl.expr.ExpLet;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpObjAsSet;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpOne;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpReject;
import org.tzi.use.uml.ocl.expr.ExpSelect;
import org.tzi.use.uml.ocl.expr.ExpSequenceLiteral;
import org.tzi.use.uml.ocl.expr.ExpSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpSortedBy;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral;
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.type.SetType;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkObject;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystemState;


/**
 * Analyses and translates ASSL procedures 
 * 
 * @author Juergen Widdermann 
 */
public class AsslTranslation {

	/*   ***********Configuration********** */
	private GInstructionList instructionList;

	/*  ************ASSL-Bounds*************** */
	private HashMap<String, ArrayList<MObject>> classObjectBound;
	private HashMap<String, ArrayList<Vector<MObject>>> associationBoundLower;
	private HashMap<String, ArrayList<Vector<MObject>>> associationBoundUpper;
	private HashMap<String, ArrayList<MObject>> associationClassLowerObjectBound;
	private HashMap<String, ArrayList<String>> associationClassUpperObjectBound;
	private HashMap<String, ArrayList<Vector<MObject>>> associationClassBoundLower;
	private HashMap<String, ArrayList<Vector<Object>>> associationClassBoundUpper;
	// Map: Class -> Object -> Attribute -> Value
	private HashMap<String, HashMap<MObject, HashMap<String, String>>> objectAttributeLowerMapping;
	private HashMap<String, HashMap<MObject, HashMap<String, ArrayList<String>>>> objectAttributeUpperMapping;
	// Map: AssociationClass -> LinkObject -> Attribute -> Value
	private ArrayList<String> asslUniverse;
	// Saves the Bounds before every Try-Instruction
	private HashMap<GInstruction, HashMap<String, ArrayList<Vector<MObject>>>> associationBoundUpperSave;
	private HashMap<GInstruction, HashMap<String, ArrayList<String>>> associationClassUpperObjectBoundSave;
	private HashMap<GInstruction, HashMap<String, ArrayList<Vector<Object>>>> associationClassBoundUpperSave;
	private HashMap<GInstruction, HashMap<String, HashMap<MObject, HashMap<String, ArrayList<String>>>>> objectAttributeUpperMappingSave;
	
	private HashMap<MObject, String> objectNameMappig;

	private int maxInteger = 1;

	private HashMap<String, Integer> classNumberCount;
	
	/* **************Variables for Source Analysing ****************** */
	//Stores for every variable a the Try-Instruction if it contains a try-value
	private HashMap<String, GInstruction> variableTryMapping;
	//Stores for every Try if its calculated value is used in ohter calculations
	private HashMap<GInstruction, Boolean> tryInstructionUsedMapping;
	//Stores for an object-attribute if it contains a try value
	private HashMap<String, Map<String, ArrayList<GInstruction>>> objectAttributeTryMapping;
	//Stores for every Association-Class if any object is created by try-value
	private HashMap<String, ArrayList<GInstruction>> associationTryMapping;


	/**
	 * Creates a new AsslTranslation Object
	 */
	public AsslTranslation() {
		this.classObjectBound = new HashMap<String, ArrayList<MObject>>();
		this.associationBoundLower = new HashMap<String, ArrayList<Vector<MObject>>>();
		this.associationBoundUpper = new HashMap<String, ArrayList<Vector<MObject>>>();
		this.associationBoundUpperSave = new HashMap<GInstruction, HashMap<String, ArrayList<Vector<MObject>>>>();
		this.associationClassLowerObjectBound = new HashMap<String, ArrayList<MObject>>();
		this.associationClassUpperObjectBound = new HashMap<String, ArrayList<String>>();
		this.associationClassUpperObjectBoundSave = new HashMap<GInstruction, HashMap<String, ArrayList<String>>>();
		this.associationClassBoundLower = new HashMap<String, ArrayList<Vector<MObject>>>();
		this.associationClassBoundUpper = new HashMap<String, ArrayList<Vector<Object>>>();
		this.associationClassBoundUpperSave = new HashMap<GInstruction, HashMap<String, ArrayList<Vector<Object>>>>();
		this.objectAttributeLowerMapping = new HashMap<String, HashMap<MObject, HashMap<String, String>>>();
		this.objectAttributeUpperMapping = new HashMap<String, HashMap<MObject, HashMap<String, ArrayList<String>>>>();
		this.objectAttributeUpperMappingSave = new HashMap<GInstruction, HashMap<String, HashMap<MObject, HashMap<String, ArrayList<String>>>>>();
		
		this.asslUniverse = new ArrayList<String>();
		this.classNumberCount = new HashMap<String, Integer>();
		
		this.variableTryMapping = new HashMap<String, GInstruction>();
		this.objectAttributeTryMapping = new HashMap<String, Map<String, ArrayList<GInstruction>>>();
		this.associationTryMapping = new HashMap<String, ArrayList<GInstruction>>();
		this.tryInstructionUsedMapping = new HashMap<GInstruction, Boolean>();
		
		this.objectNameMappig = new HashMap<MObject, String>();
	}

	/**
	 * Adds an object to Lower-Bound
	 * @param object object of State
	 */
	public void addObjectToLowerClassBound(MObject object) {
		if (classObjectBound.containsKey(object.cls().name())) {
			classObjectBound.get(object.cls().name()).add(object);
		} else {
			ArrayList<MObject> tmpList = new ArrayList<MObject>();
			tmpList.add(object);
			classObjectBound.put(object.cls().name(), tmpList);
		}
	}

	/**
	 * Adds a link to lower-bounds
	 * @param associationName name of association
	 * @param linkedObjects list of connected objects
	 */
	public void addLinkToLowerAssociationBound(String associationName,
			Vector<MObject> linkedObjects) {
		if (associationBoundLower.containsKey(associationName)) {
			associationBoundLower.get(associationName).add(linkedObjects);
		} else {
			ArrayList<Vector<MObject>> tmpList = new ArrayList<Vector<MObject>>();
			tmpList.add(linkedObjects);
			associationBoundLower.put(associationName, tmpList);
		}
	}

	/**
	 * Adds a link to upper-bounds
	 * @param associationName name of association
	 * @param linkedObjects list of connected objects
	 */
	public void addLinkToUpperAssociationBound(String associationName,
			Vector<MObject> linkedObjects) {
		if (associationBoundUpper.containsKey(associationName)) {
			associationBoundUpper.get(associationName).add(linkedObjects);
		} else {
			ArrayList<Vector<MObject>> tmpList = new ArrayList<Vector<MObject>>();
			tmpList.add(linkedObjects);
			associationBoundUpper.put(associationName, tmpList);
		}
	}

	/**
	 * Adds a attribute to the lower-bound
	 * @param object the object
	 * @param attributeName the assigned attribute
	 * @param value value of attribute
	 */
	public void addAttributeToObjectLowerBound(MObject object, String attributeName, Value value) {
		if(!value.isUndefined()) {
			String valueString = value.toString();
			if (value.isInteger()) {
				int val = (new Integer(value.toString())).intValue();
				setMaxInteger(val);
			}
			if (value.type().isEnum()) {
				EnumValue val = (EnumValue) value;
				valueString = val.value();
			}
			if (value.type().isObjectType()) {
				valueString = AsslTranslation.getKodKodObjectName(value.type()
						.toString(), valueString.substring(1));
			}
			String className = object.cls().name();
			if (objectAttributeLowerMapping.containsKey(className)) {
				HashMap<MObject, HashMap<String, String>> objectsMapping = objectAttributeLowerMapping
						.get(className);
				if (objectsMapping.containsKey(object)) {
					HashMap<String, String> attributeMapping = objectsMapping
							.get(object);
					attributeMapping.put(attributeName, valueString);
				} else {
					HashMap<String, String> attributeMapping = new HashMap<String, String>();
					attributeMapping.put(attributeName, valueString);
					objectsMapping.put(object, attributeMapping);
				}
			} else {
				HashMap<MObject, HashMap<String, String>> objectsMapping = new HashMap<MObject, HashMap<String, String>>();
				HashMap<String, String> attributeMapping = new HashMap<String, String>();
				attributeMapping.put(attributeName, valueString);
				objectsMapping.put(object, attributeMapping);
				objectAttributeLowerMapping.put(className, objectsMapping);
			}
		}
	}
	
	/**
	 * Adds a attribute to the upper-bound
	 * @param object the object
	 * @param attributeName the assigned attribute
	 * @param value value of attribute
	 * @param resetList Reset bound before adding
	 */
	public void addAttributeToObjectUpperBound(MObject object,
			String attributeName, Value value, boolean resetList) {
		String valueString = null;
		if(value != null) {
			valueString = value.toString();
			if (value.isInteger()) {
				int val = (new Integer(value.toString())).intValue();
				setMaxInteger(val);
			}
			if (value.type().isEnum()) {
				EnumValue val = (EnumValue) value;
				valueString = val.value();
			}
			if (value.type().isObjectType()) {
				valueString = AsslTranslation.getKodKodObjectName(value.type()
						.toString(), valueString.substring(1));
			}
		}
		String className = object.cls().name();
		if (objectAttributeUpperMapping.containsKey(className)) {
			HashMap<MObject, HashMap<String, ArrayList<String>>> objectsMapping = objectAttributeUpperMapping
					.get(className);
			if (objectsMapping.containsKey(object)) {
				HashMap<String, ArrayList<String>> attributeMapping = objectsMapping
						.get(object);
				ArrayList<String> valueList = null;
				if (attributeMapping.get(attributeName) != null) {
					valueList = attributeMapping.get(attributeName);
				} else {
					valueList = new ArrayList<String>();
				}
				if (resetList) {
					valueList.clear();
				}
				valueList.add(valueString);
				attributeMapping.put(attributeName, valueList);
			} else {
				HashMap<String, ArrayList<String>> attributeMapping = new HashMap<String, ArrayList<String>>();
				ArrayList<String> valueList = new ArrayList<String>();
				valueList.add(valueString);
				attributeMapping.put(attributeName, valueList);
				objectsMapping.put(object, attributeMapping);
			}
		} else {
			HashMap<MObject, HashMap<String, ArrayList<String>>> objectsMapping = new HashMap<MObject, HashMap<String, ArrayList<String>>>();
			HashMap<String, ArrayList<String>> attributeMapping = new HashMap<String, ArrayList<String>>();
			ArrayList<String> valueList = new ArrayList<String>();
			valueList.add(valueString);
			attributeMapping.put(attributeName, valueList);
			objectsMapping.put(object, attributeMapping);
			objectAttributeUpperMapping.put(className, objectsMapping);
		}
	}

	/**
	 * Adds an linkobject to lower-bound 
	 * @param object object to add
	 */
	public void addObjectToLowerAssociationClassBound(MObject object) {
		String objectType = object.cls().name();
		if (associationClassLowerObjectBound.containsKey(objectType)) {
			associationClassLowerObjectBound.get(objectType).add(object);
		} else {
			ArrayList<MObject> tmpList = new ArrayList<MObject>();
			tmpList.add(object);
			associationClassLowerObjectBound.put(objectType, tmpList);
		}
	}

	/**
	 * Adds an linkobject to upper-bound 
	 * @param objectType type of object
	 * @param objectName name of object 
	 */
	public void addObjectToUpperAssociationClassBound(String objectType, String objectName) {
		if (associationClassUpperObjectBound.containsKey(objectType)) {
			associationClassUpperObjectBound.get(objectType).add(objectName);
		} else {
			ArrayList<String> tmpList = new ArrayList<String>();
			tmpList.add(objectName);
			associationClassUpperObjectBound.put(objectType, tmpList);
		}
	}

	/**
	 * Adds a link of associationclass to lower-bound 
	 * @param associationName name of association
	 * @param linkedObjects list of linked objects
	 */
	public void addLinkToLowerAssociationClassBound(String associationName,
			Vector<MObject> linkedObjects) {
		if (associationClassBoundLower.containsKey(associationName)) {
			associationClassBoundLower.get(associationName).add(linkedObjects);
		} else {
			ArrayList<Vector<MObject>> tmpList = new ArrayList<Vector<MObject>>();
			tmpList.add(linkedObjects);
			associationClassBoundLower.put(associationName, tmpList);
		}
	}

	/**
	 * Adds a link of associationclass to upper-bound 
	 * @param associationName name of association
	 * @param linkedObjects list of linked objects
	 */
	public void addLinkToUpperAssociationClassBound(String associationName,
			Vector<Object> linkedObjects) {
		if (associationClassBoundUpper.containsKey(associationName)) {
			associationClassBoundUpper.get(associationName).add(linkedObjects);
		} else {
			ArrayList<Vector<Object>> tmpList = new ArrayList<Vector<Object>>();
			tmpList.add(linkedObjects);
			associationClassBoundUpper.put(associationName, tmpList);
		}
	}

	/**
	 * Sets the Max-Integer Value
	 * @param val integer-value, stores the Value only if its the maximum
	 */
	private void setMaxInteger(int val) {
		if (this.maxInteger < val) {
			this.maxInteger = val;
		}
	}

	/**
	 * Returns the maximal integer value of the assl procedure
	 * @return maximal integer value
	 */
	public int getMaxInteger() {
		return this.maxInteger;
	}

	/**
	 * Get the lower-bounds of classes
	 * @return map of lower-bounds of all classes
	 */
	public Map<String, ArrayList<MObject>> getClassObjectBound() {
		return classObjectBound;
	}

	/**
	 * Get the lower-bounds of associations
	 * @return map of lower-bounds of all associations
	 */
	public Map<String, ArrayList<Vector<MObject>>> getAssociationBoundLower() {
		return associationBoundLower;
	}

	/**
	 * Get the upper-bounds of associations
	 * @return map of upper-bounds of all associations
	 */
	public Map<String, ArrayList<Vector<MObject>>> getAssociationBoundUpper() {
		return associationBoundUpper;
	}

	/**
	 * Get the lower-bounds of objects of associationclasses
	 * @return map of lower-bounds of objects of all associationclasses
	 */
	public Map<String, ArrayList<MObject>> getAssociationClassLowerObjectBound() {
		return associationClassLowerObjectBound;
	}

	/**
	 * Get the upper-bounds of objects of associationclasses
	 * @return map of upper-bounds of objects of all associationclasses
	 */
	public Map<String, ArrayList<String>> getAssociationClassUpperObjectBound() {
		return associationClassUpperObjectBound;
	}

	/**
	 * Get the lower-bounds of links of associationclasses
	 * @return map of lower-bounds of links of all associationclasses
	 */
	public Map<String, ArrayList<Vector<MObject>>> getAssociationClassBoundLower() {
		return associationClassBoundLower;
	}

	/**
	 * Get the upper-bounds of links of associationclasses
	 * @return map of upper-bounds of links of all associationclasses
	 */
	public Map<String, ArrayList<Vector<Object>>> getAssociationClassBoundUpper() {
		return associationClassBoundUpper;
	}

	/**
	 * Get the lower-bounds of attributes
	 * @return map of lower-bounds of all attributes
	 */
	public Map<String, HashMap<MObject, HashMap<String, String>>> getObjectAttributeLowerMapping() {
		return objectAttributeLowerMapping;
	}

	/**
	 * Get the upper-bounds of attributes
	 * @return map of upper-bounds of all attributes
	 */
	public Map<String, HashMap<MObject, HashMap<String, ArrayList<String>>>> getObjectAttributeUpperMapping() {
		return objectAttributeUpperMapping;
	}

	/**
	 * Get the Universe of the lower-bounds
	 * @return universe of lower-bounds
	 */
	public ArrayList<String> getAsslUniverse() {
		return asslUniverse;
	}

	/**
	 * Gets a unique name for an object
	 * @param object a object
	 * @return unique name for the given object
	 */
	public String getKodKodObjectName(MObject object) {
		return objectNameMappig.get(object);
	}
	
	/**
	 * Gets a unique name for an object-type and objectname
	 * @param objectType
	 * @param objectName
	 * @return unique name
	 */
	public static String getKodKodObjectName(String objectType,
			String objectName) {
		return getKodKodObjectName(objectType, objectName, 0);
	}
	
	/**
	 * Returns a unique name bei the given object-type, object-name and offset 
	 * @param objectType
	 * @param objectName
	 * @param offset
	 * @return unique name
	 */
	public static String getKodKodObjectName(String objectType,
			String objectName, int offset) {
		int typeNameLenght = objectType.length();
		if(objectName.contains("@")) {
			return objectName;
		}
		int number = Integer.parseInt(objectName.substring(typeNameLenght));
		return objectName.substring(0,1).toLowerCase() + objectName.substring(1, typeNameLenght) + "@"
				+ (number + offset);
	}

	/**
	 * Reads the current systemstate an put it into lower-bounds
	 * @param state current State
	 */
	public void readSystemState(MSystemState state) {
		// collect all objects
		for (MObject object : state.allObjects()) {
			setUniqueNameForObject(object);
			if(object.cls() instanceof MAssociationClass) {
				addObjectToLowerAssociationClassBound(object);
			} else {
				addObjectToLowerClassBound(object);
			}
			Map<MAttribute, Value> attributes = object.state(state)
					.attributeValueMap();
			Set<MAttribute> attributesKey = attributes.keySet();
			for (MAttribute attribute : attributesKey) {
				addAttributeToObjectLowerBound(object, attribute.name(), attributes.get(attribute));
			}
		}
		// collect all Links
		for (MLink linkObject : state.allLinks()) {
			String associationName = linkObject.association().name();
			Vector<MObject> linkObjects = new Vector<MObject>();
			if(linkObject instanceof MLinkObject) {
				MLinkObject tmpObject = (MLinkObject) linkObject;
				linkObjects.add(tmpObject);
			}
			for(MObject object:linkObject.linkedObjectsAsArray()){
				linkObjects.add(object);
			}
			if(linkObject instanceof MLinkObject){
				addLinkToLowerAssociationClassBound(associationName, linkObjects);
			} else {
				addLinkToLowerAssociationBound(associationName, linkObjects);
			}
		}
	}
	
	/**
	 * Get Number of Objects per class
	 * @param className
	 * @return number of Objects
	 */
	public int getClassObjectCount(String className) {
		return getClassObjectCount(classNumberCount, className);
	}
	
	/**
	 * Get Number of Objects per class
	 * @param counterType
	 * @param className
	 * @return number of Objects
	 */
	public int getClassObjectCount(Map<String, Integer> counterType, String className) {
		Integer result = counterType.get(className);
		if (result != null)
			return result.intValue();
		return 0;
	}
	
	/**
	 * reset die internal storage for the universe of the lower bound
	 */
	public void resetAsslUniverse(){
		asslUniverse.clear();
	}
	
	/**
	 * start the analysis of the assl procedure
	 */
	public void checkTryInstructions() {
		checkTryInstructions(instructionList);
	}
	
	/**
	 * Checks if a variable with Try is used in the following
	 * @param instructionList list of instructions
	 */
	public void checkTryInstructions(GInstructionList instructionList) {
		for(GInstruction instruction : instructionList.instructions()) {
			checkTryInstructions(instruction);
		}
	}
	
	/**
	 * Analyses a single instruction
	 * @param instruction
	 */
	public void checkTryInstructions(GInstruction instruction) {
		// check Type of instruction
		if(instruction instanceof GAttributeAssignment) {
			//Attribute assigmen
			GAttributeAssignment tmpInstruction = (GAttributeAssignment) instruction;
			// use try instruction?
			if(tmpInstruction.sourceInstr() instanceof GInstrTry_Seq){
				setTryInstructionForClassAttribute(tmpInstruction.targetAttribute().owner().name(),tmpInstruction.targetAttribute().name(), tmpInstruction.sourceInstr());
			}
			//check parts of the instruction
			checkTryInstructions(tmpInstruction.sourceInstr());
			checkTryInstructions(tmpInstruction.targetObjectInstr());
		}
		else if(instruction instanceof GIfThenElse) {
			// if then else
			GIfThenElse tmpInstruction = (GIfThenElse) instruction;
			checkTryInstructions(tmpInstruction.conditionInstr());
			checkTryInstructions(tmpInstruction.thenInstructionList());
			checkTryInstructions(tmpInstruction.elseInstructionList());
		}
		else if(instruction instanceof GInstrAny_Seq) {
			//any
			GInstrAny_Seq tmpInstruction = (GInstrAny_Seq) instruction;
			checkTryInstructions(tmpInstruction.sequenceInstr());
		}
		else if(instruction instanceof GInstrCreate_C) {
			//Create
			// Do Nothing
		}
		else if(instruction instanceof GInstrCreateN_C_Integer) {
			//CreateN
			GInstrCreateN_C_Integer tmpInstruction = (GInstrCreateN_C_Integer) instruction;
			checkTryInstructions(tmpInstruction.integerInstr());
		}
		else if(instruction instanceof GInstrDelete_Assoc_Linkends) {
			//Delete association
			GInstrDelete_Assoc_Linkends tmpInstruction = (GInstrDelete_Assoc_Linkends) instruction;
			for(GValueInstruction instr : tmpInstruction.linkEnds()) {
				checkTryInstructions(instr);
			}
		}
		else if(instruction instanceof GInstrDelete_Object) {
			//Delete object
			GInstrDelete_Object tmpInstruction = (GInstrDelete_Object) instruction;
			checkTryInstructions(tmpInstruction.objectInstr());
		}
		else if(instruction instanceof GInstrInsert_Assoc_Linkends) {
			//Insert association
			GInstrInsert_Assoc_Linkends tmpInstruction = (GInstrInsert_Assoc_Linkends) instruction;
			for(GValueInstruction instr : tmpInstruction.linkEnds()) {
				checkTryInstructions(instr);
			}
		}
		else if(instruction instanceof GInstrSub_Seq_Integer) {
			// Sub with size
			GInstrSub_Seq_Integer tmpInstruction = (GInstrSub_Seq_Integer) instruction;
			checkTryInstructions(tmpInstruction.integerInstr());
			checkTryInstructions(tmpInstruction.sequenceInstr());
		}
		else if(instruction instanceof GInstrSub_Seq) {
			// Sub
			GInstrSub_Seq tmpInstruction = (GInstrSub_Seq) instruction;
			checkTryInstructions(tmpInstruction.sequenceInstr());
		}
		else if(instruction instanceof GInstrTry_Assoc_LinkendSeqs) {
			// Try association
			GInstrTry_Assoc_LinkendSeqs tmpInstruction = (GInstrTry_Assoc_LinkendSeqs) instruction;
			setTryMappingForAssociation(tmpInstruction.association().name(), tmpInstruction);
			for(GValueInstruction instr : tmpInstruction.linkendSequences()) {
				checkTryInstructions(instr);
			}
		}
		else if(instruction instanceof GInstrTry_Seq) {
			// Try sequence
			GInstrTry_Seq tmpInstruction = (GInstrTry_Seq) instruction;
			checkTryInstructions(tmpInstruction.sequenceInstr());
		}
		else if(instruction instanceof GLoop) {
			// Loop
			GLoop tmpInstruction = (GLoop) instruction;
			checkTryInstructions(tmpInstruction.sequenceInstr());
			checkTryInstructions(tmpInstruction.instructionList());
		}
		else if(instruction instanceof GOCLExpression) {
			// OCL Expression
			GOCLExpression tmpInstruction = (GOCLExpression) instruction;
			Expression expr = tmpInstruction.expression();
			if(expr != null) {
				searchForTryVariables(expr);
			}
		}
		else if(instruction instanceof GVariableAssignment) {
			// Variable assigment
			GVariableAssignment tmpInstruction = (GVariableAssignment) instruction;
			if(tmpInstruction.sourceInstr() instanceof GInstrTry_Seq){
				variableTryMapping.put(tmpInstruction.target(), (GInstrTry_Seq)tmpInstruction.sourceInstr());
			} else {
				variableTryMapping.put(tmpInstruction.target(), null);
			}
			checkTryInstructions(tmpInstruction.sourceInstr());
		}
	}
	

	/**
	 * Search for used variables with try
	 * @param expr OCL-Expression to search used Trys
	 */
	public void searchForTryVariables(Expression expr) {
		if(expr instanceof ExpAllInstances){
			String className = ((SetType)expr.type()).elemType().toString();
			ArrayList<GInstruction> instructions = associationTryMapping.get(className);
			// All Instances on Association-Class with Try
			if(instructions != null) {
				for(GInstruction instruction : instructions) {
					tryInstructionUsedMapping.put(instruction, new Boolean(true));
				}
			//allInstances on Normal-Class
			} else {
				// Adding all Try-Instructions dealing with this class to not to use
				Map<String, ArrayList<GInstruction>> attributeTryMapping = objectAttributeTryMapping.get(className);
				if(attributeTryMapping != null) {
					for(String key : attributeTryMapping.keySet()) {
						instructions = attributeTryMapping.get(key);
						if(instructions != null) {
							for(GInstruction instruction : instructions) {
								tryInstructionUsedMapping.put(instruction, new Boolean(true));
							}
						}
					}
				}
			}
		}
		if(expr instanceof ExpAny){
			searchForTryVariables(((ExpAny) expr).getQueryExpression());
			searchForTryVariables(((ExpAny) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpAsType){
			//DO NOTHING
		}
		
		if(expr instanceof ExpAttrOp){
			MAttribute attr = ((ExpAttrOp) expr).attr();
			Map<String, ArrayList<GInstruction>> attributeTryMapping = objectAttributeTryMapping.get(attr.owner().name());
			if(attributeTryMapping != null) {
				ArrayList<GInstruction> instructions = attributeTryMapping.get(attr.name());
				if(instructions != null) {
					for(GInstruction instruction : instructions){
						tryInstructionUsedMapping.put(instruction, new Boolean(true));
					}
				}
			}
			searchForTryVariables(((ExpAttrOp) expr).objExp());
		}
		
		if(expr instanceof ExpBagLiteral){
			for(Expression e:((ExpBagLiteral) expr).getElemExpr()) {
				searchForTryVariables(e);
			}
		}
		
		if(expr instanceof ExpCollect){
			searchForTryVariables(((ExpCollect) expr).getQueryExpression());
			searchForTryVariables(((ExpCollect) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpCollectionLiteral){
			for(Expression e:((ExpCollectionLiteral) expr).getElemExpr()){
				searchForTryVariables(e);
			}
		}
		
		if(expr instanceof ExpConstBoolean){
			// DO NOTHING
		}
		
		if(expr instanceof ExpConstEnum){
			// DO NOTHING
		}
		
		if(expr instanceof ExpConstInteger){
			// DO NOTHING
		}
		
		if(expr instanceof ExpConstReal){
			// DO NOTHING
		}
		
		if(expr instanceof ExpConstString){
			// DO NOTHING
		}
		
		if(expr instanceof ExpEmptyCollection){
			// DO NOTHING
		}
		
		if(expr instanceof ExpExists){
			searchForTryVariables(((ExpExists) expr).getQueryExpression());
			searchForTryVariables(((ExpExists) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpForAll){
			searchForTryVariables(((ExpForAll) expr).getQueryExpression());
			searchForTryVariables(((ExpForAll) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpIf){
			searchForTryVariables(((ExpIf) expr).getElseExpression());
			searchForTryVariables(((ExpIf) expr).getThenExpression());
			searchForTryVariables(((ExpIf) expr).getCondition());
		}
		
		if(expr instanceof ExpIsKindOf){
			// DO NOTHING
		}
		
		if(expr instanceof ExpIsTypeOf){
			// DO NOTHING
		}
		
		if(expr instanceof ExpIsUnique){
			searchForTryVariables(((ExpIsUnique) expr).getQueryExpression());
			searchForTryVariables(((ExpIsUnique) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpIterate){
			searchForTryVariables(((ExpIterate) expr).getQueryExpression());
			searchForTryVariables(((ExpIterate) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpLet){
			searchForTryVariables(((ExpLet) expr).getInExpression());
			searchForTryVariables(((ExpLet) expr).getVarExpression());
		}
		
		if(expr instanceof ExpNavigation){
			MAssociation association = ((ExpNavigation) expr).getDestination().association();
			ArrayList<GInstruction> instructions = associationTryMapping.get(association.name());
			// All Instances on Association-Class with Try
			if(instructions != null) {
				for(GInstruction instruction : instructions) {
					tryInstructionUsedMapping.put(instruction, new Boolean(true));
				}
			//allInstances on Normal-Class
			} 
			searchForTryVariables(((ExpNavigation) expr).getObjectExpression());
		}
		
		if(expr instanceof ExpObjAsSet){
			// DO NOTHING
		}
		
		if(expr instanceof ExpObjOp){
			// DO NOTHING
		}
		
		if(expr instanceof ExpOne){
			searchForTryVariables(((ExpOne) expr).getQueryExpression());
			searchForTryVariables(((ExpOne) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpQuery){
			searchForTryVariables(((ExpQuery) expr).getQueryExpression());
			searchForTryVariables(((ExpQuery) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpReject){
			searchForTryVariables(((ExpReject) expr).getQueryExpression());
			searchForTryVariables(((ExpReject) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpressionWithValue){
			// DO NOTHING
		}
		
		if(expr instanceof ExpSelect){
			searchForTryVariables(((ExpSelect) expr).getQueryExpression());
			searchForTryVariables(((ExpSelect) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpSequenceLiteral){
			for(Expression e:((ExpSequenceLiteral) expr).getElemExpr()) {
				searchForTryVariables(e);
			}
		}
		
		if(expr instanceof ExpSetLiteral){
			for(Expression e:((ExpSetLiteral) expr).getElemExpr()) {
				searchForTryVariables(e);
			}
		}
		
		if(expr instanceof ExpSortedBy){
			searchForTryVariables(((ExpSortedBy) expr).getQueryExpression());
			searchForTryVariables(((ExpSortedBy) expr).getRangeExpression());
		}
		
		if(expr instanceof ExpStdOp){
			for(Expression e : ((ExpStdOp) expr).args()) {
				searchForTryVariables(e);
			}
		}
		
		if(expr instanceof ExpTupleLiteral){
			// DO NOTHING
		}
		
		if(expr instanceof ExpTupleSelectOp){
			// DO NOTHING
		}
		
		if(expr instanceof ExpUndefined){
			// DO NOTHING
		}
		
		if(expr instanceof ExpVariable){
			// Check if a variable with try is used
			GInstruction instruction = variableTryMapping.get(expr.toString());
			if(instruction != null) {
				tryInstructionUsedMapping.put(instruction, new Boolean(true));
			}
		}
	}
	
	/**
	 * Sets the try Instruction a class attribute pair
	 * @param className 
	 * @param attributeName
	 * @param tryInstruction
	 */
	private void setTryInstructionForClassAttribute(String className, String attributeName, GInstruction tryInstruction){
		Map<String, ArrayList<GInstruction>> attributeTryMapping = objectAttributeTryMapping.get(className);
		if(attributeTryMapping == null) {
			attributeTryMapping = new HashMap<String, ArrayList<GInstruction>>();
			ArrayList<GInstruction> instructions = new ArrayList<GInstruction>();
			instructions.add(tryInstruction);
			attributeTryMapping.put(attributeName, instructions);
			objectAttributeTryMapping.put(className, attributeTryMapping);
		} else {
			ArrayList<GInstruction> instructions = attributeTryMapping.get(attributeName);
			instructions.add(tryInstruction);
		}
	}
	
	/**
	 * Sets try instruction for association or associationclass
	 * @param associationName
	 * @param tryInstruction
	 */
	private void setTryMappingForAssociation(String associationName, GInstruction tryInstruction) {
		ArrayList<GInstruction> instructions = associationTryMapping.get(associationName);
		if(instructions != null) {
			instructions.add(tryInstruction);
		} else {
			instructions = new ArrayList<GInstruction>();
			instructions.add(tryInstruction);
			associationTryMapping.put(associationName, instructions);
		}
	}
	
	/**
	 * Checks if a Try-Instruction value is used
	 * @param tryInstruction instruction to check
	 * @return true, when value of Try is used
	 */
	public boolean isTryInstructionValueUsed(GInstruction tryInstruction) {
		Boolean result = tryInstructionUsedMapping.get(tryInstruction);
		if(result != null) {
			return result.booleanValue();
		}
		return false;
	}
	
	/**
	 * sets the ASSL-Procedure for Translation
	 * @param instructionList
	 */
	public void setInstructionList(GInstructionList instructionList){
		this.instructionList = instructionList;
	}
	
	/**
	 * resets the lower bounds
	 */
	public void resetLowerBounds() {
		asslUniverse.clear();
		classObjectBound.clear();
		associationBoundLower.clear();
		associationClassBoundLower.clear();
		associationClassLowerObjectBound.clear();
		classNumberCount.clear();
		objectNameMappig.clear();
	}
	
	/**
	 * sets the unique name for an object
	 * @param object
	 */
	public void setUniqueNameForObject(MObject object) {
		Integer value = classNumberCount.get(object.cls().name());
		if(value == null) {
			value = new Integer(0);
		}
		value++;
		String objectName = object.cls().name().substring(0, 1).toLowerCase() + object.cls().name().substring(1)+"@"+value;
		objectNameMappig.put(object, objectName);
		classNumberCount.put(object.cls().name(), value);
		asslUniverse.add(objectName);
	}
	
	/**
	 * Saves the upper bound before a try instruction
	 * @param tryIntruction
	 */
	@SuppressWarnings("unchecked")
	public void saveUpperBounds(GInstruction tryIntruction) {
		associationBoundUpperSave.put(tryIntruction, (HashMap<String, ArrayList<Vector<MObject>>>) associationBoundUpper.clone());
		associationClassUpperObjectBoundSave.put(tryIntruction, (HashMap<String, ArrayList<String>>) associationClassUpperObjectBound.clone());
		associationClassBoundUpperSave.put(tryIntruction,(HashMap<String, ArrayList<Vector<Object>>>) associationClassBoundUpper.clone());
		objectAttributeUpperMappingSave.put(tryIntruction, (HashMap<String, HashMap<MObject, HashMap<String, ArrayList<String>>>>) objectAttributeUpperMapping.clone());
	}
	
	/**
	 * load the upper bounds before a try instruction
	 * @param tryIntruction
	 */
	@SuppressWarnings("unchecked")
	public void loadSavedBounds(GInstruction tryIntruction){
		associationBoundUpper.clear();
		associationBoundUpper = (HashMap<String, ArrayList<Vector<MObject>>>) associationBoundUpperSave.get(tryIntruction).clone();
		associationClassUpperObjectBound.clear();
		associationClassUpperObjectBound = (HashMap<String, ArrayList<String>>) associationClassUpperObjectBoundSave.get(tryIntruction).clone();
		associationClassBoundUpper.clear();
		associationClassBoundUpper = (HashMap<String, ArrayList<Vector<Object>>>) associationClassBoundUpperSave.get(tryIntruction).clone();
		objectAttributeUpperMapping.clear();
		objectAttributeUpperMapping = (HashMap<String, HashMap<MObject, HashMap<String, ArrayList<String>>>>) objectAttributeUpperMappingSave.get(tryIntruction).clone();
	}
}
