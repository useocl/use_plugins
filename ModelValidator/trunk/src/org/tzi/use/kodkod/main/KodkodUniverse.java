package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.instance.Universe;

import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;

/**
 * Class to create the Kodkod universe
 * @author Torsten Humann
 * 
 */
public final class KodkodUniverse{
	
	private final MSystemState curSysState;
	
	public KodkodUniverse(MSystemState curState){
		curSysState = curState;
	}
	
	//creates the Kodkod universe with all atoms of
	//classrelation, associationclassrelation, enumrelations,
	//boolrelations, idrelations
	//atoms have same names as in relations
	//checks if any max count of objects of classes and assciationclasses
	//is > 0
	public Universe createUniverse(HashMap<String, UMLClass> umlCla, HashMap<String, UMLAssociationClass> umlAssCla, ArrayList<String> eNums, HashMap<String, GenericRelation> genericRelations, int maxInt){
		
		final ArrayList<String> atoms = new ArrayList<String>();
		
		boolean checkBounds = false;
		
		for(int i = 0; i < umlCla.size(); i++){
			UMLClass tmpUMLCla = (UMLClass) umlCla.values().toArray()[i];
			for(int j = 0; j < tmpUMLCla.getUpperBound(); j++){
				checkBounds = true;
				if(j < tmpUMLCla.getObjectCount()){
					MObject obj = (MObject) curSysState.objectsOfClass(tmpUMLCla.getMClass()).toArray()[j];
					atoms.add(tmpUMLCla.getBoundName() + (j + 1) + "_" + obj.name());
				}else{
					atoms.add(tmpUMLCla.getBoundName() + (j + 1));
				}
			}
			for(int j = 0; j < tmpUMLCla.getMClass().allAttributes().size(); j++){
				MAttribute att = (MAttribute) tmpUMLCla.getMClass().allAttributes().get(j);
				if(att.type().isString() || att.type().isReal()){
					for(int k = 0; k < tmpUMLCla.getObjectCount(); k++){
						MObject obj = (MObject) curSysState.objectsOfClass(tmpUMLCla.getMClass()).toArray()[k];
						atoms.add(tmpUMLCla.getAttributeBoundName(j) + (k + 1) + "_" + obj.state(curSysState).attributeValue(att).toString());
					}
					
					for(int k = 0; k < tmpUMLCla.getBoundNames(att.name()).size(); k++){
						if(att.type().isString()){
							atoms.add(tmpUMLCla.getAttributeBoundName(j) + (k + 1 + tmpUMLCla.getObjectCount()) + "_'" + tmpUMLCla.getBoundNames(att.name()).get(k) + "'");
						}else{
							atoms.add(tmpUMLCla.getAttributeBoundName(j) + (k + 1 + tmpUMLCla.getObjectCount()) + "_" + tmpUMLCla.getBoundNames(att.name()).get(k));
						}
					}
				}
			}
		}
		for(int i = 0; i < umlAssCla.size(); i++){
			UMLAssociationClass tmpUMLAssCla = (UMLAssociationClass) umlAssCla.values().toArray()[i];
			for(int j = 0; j < tmpUMLAssCla.getUpperBound(); j++){
				checkBounds = true;
				if(j < tmpUMLAssCla.getObjectCount()){
					MObject obj = (MObject) curSysState.objectsOfClass(tmpUMLAssCla.getMClass()).toArray()[j];
					atoms.add(tmpUMLAssCla.getBoundName() + (j + 1) + "_" + obj.name());
				}else{
					atoms.add(tmpUMLAssCla.getBoundName() + (j + 1));
				}
			}
			for(int j = 0; j < tmpUMLAssCla.getMClass().allAttributes().size(); j++){
				MAttribute att = (MAttribute) tmpUMLAssCla.getMClass().allAttributes().get(j);
				if(att.type().isString() || att.type().isReal()){
					for(int k = 0; k < tmpUMLAssCla.getObjectCount(); k++){
						MObject obj = (MObject) curSysState.objectsOfClass(tmpUMLAssCla.getMClass()).toArray()[k];
						atoms.add(tmpUMLAssCla.getAttributeBoundName(j) + (k + 1) + "_" + obj.state(curSysState).attributeValue(att).toString());
					}
					for(int k = 0; k < tmpUMLAssCla.getBoundNames(att.name()).size(); k++){
						if(att.type().isString()){
							atoms.add(tmpUMLAssCla.getAttributeBoundName(j) + (k + 1 + tmpUMLAssCla.getObjectCount()) + "_'" + tmpUMLAssCla.getBoundNames(att.name()).get(k) + "'");
						}else{
							atoms.add(tmpUMLAssCla.getAttributeBoundName(j) + (k + 1 + tmpUMLAssCla.getObjectCount()) + "_" + tmpUMLAssCla.getBoundNames(att.name()).get(k));
						}
					}
				}
			}
		}
		
		// adding values of generic relation to universe
		Set<String> keys = genericRelations.keySet();
		Iterator<String> genericRelationIterator = keys.iterator();
		while(genericRelationIterator.hasNext()) {
			String key = genericRelationIterator.next();
			GenericRelation genericRelation = genericRelations.get(key);
			if(genericRelation.containsNewAtoms()) {
				atoms.addAll(genericRelation.getValuesAsList());
			}
		}
		
		if(checkBounds){
			addStaticBound(atoms, maxInt, eNums);
			return new Universe(atoms);
		}else{
			return null;
		}
	}
	
	public void addStaticBound(List<String> atoms, int maxInt, List<String> eNums) { 
		atoms.addAll(eNums);
		for(int i = 0; i < maxInt; i++){
			atoms.add("@id" + i);
		}
		atoms.add("@true");
		atoms.add("@false");
		atoms.add("Undefined");
		atoms.add("Undefined_Set");
	}
	
	public Universe createAsslUniverse(ArrayList<String> eNums, HashMap<String, UMLClass> umlCla, HashMap<String, UMLAssociationClass> assCla, AsslTranslation asslTranslation) {
		ArrayList<String> atoms = asslTranslation.getAsslUniverse(); 
		int maxInt = asslTranslation.getMaxInteger();
		addStaticBound(atoms, maxInt, eNums);
		Map<String, ArrayList<MObject>> classObjectBound = asslTranslation.getClassObjectBound();
		Map<String, ArrayList<MObject>> associationClassLowerObjectBound = asslTranslation.getAssociationClassLowerObjectBound();
		Map<String, HashMap<MObject, HashMap<String, String>>> objectAttributeLowerMapping = asslTranslation.getObjectAttributeLowerMapping();
		Map<String, HashMap<MObject, HashMap<String, ArrayList<String>>>> objectAttributeUpperMapping = asslTranslation.getObjectAttributeUpperMapping();
		Map<String, ArrayList<String>> associationClassUpperObjectBound = asslTranslation.getAssociationClassUpperObjectBound();
		
		// setting Undefined for Unbound Variable-Attributes
		MSystem system = curSysState.system();
		
		for(MClass className : system.model().classes()) {
			ArrayList<MObject> objects = classObjectBound.get(className.name());
			if(objects != null) {
				int i = 0;
				for(MAttribute attr: className.allAttributes()) {
					for(MObject object : objects) {
						UMLClass cla = umlCla.get(className.name());
						String attBouName = cla.getAttributeBoundName(i);
//						if(!(objectAttributeUpperMapping.get(className.name()) != null 
//								&& objectAttributeUpperMapping.get(className.name()).get(object) != null
//								&& objectAttributeUpperMapping.get(className.name()).get(object).get(attr.name()) != null
//								||
//								objectAttributeLowerMapping.get(className.name()) != null 
//								&& objectAttributeLowerMapping.get(className.name()).get(object) != null
//								&& objectAttributeLowerMapping.get(className.name()).get(object).get(attr.name()) != null)) {
//							// Add Undefined
//							String objName = "";
//							if(attr.type().isString() || attr.type().isReal()) {
//								objName = attBouName + 1 + "_" + "'Undefined'";
//							}
//							if(!atoms.contains(objName))
//								atoms.add(objName);
//						} else {
							if(objectAttributeLowerMapping.get(className.name()) != null 
									&& objectAttributeLowerMapping.get(className.name()).get(object) != null
									&& objectAttributeLowerMapping.get(className.name()).get(object).get(attr.name()) != null) {
								String objName = attBouName + (1) + "_" + objectAttributeLowerMapping.get(className.name()).get(object).get(attr.name());
								if(!atoms.contains(objName))
									atoms.add(objName);
							} else if(objectAttributeUpperMapping.get(className.name()) != null 
									&& objectAttributeUpperMapping.get(className.name()).get(object) != null
									&& objectAttributeUpperMapping.get(className.name()).get(object).get(attr.name()) != null) {
								ArrayList<String> values = objectAttributeUpperMapping.get(className.name()).get(object).get(attr.name());
								for(int k = 0; k < values.size(); k++) {
									String objName = attBouName + (k + 1) + "_" + values.get(k);
									if(!atoms.contains(objName))
										atoms.add(objName);
								}
							}
						//}
					}
					i++;
				}
			}
		}
		for(MClass className : system.model().getAssociationClassesOnly()) {
			ArrayList<MObject> objects = associationClassLowerObjectBound.get(className.name());
			if(objects != null) {
				int i = 0;
				for(MAttribute attr: className.allAttributes()) {
					for(MObject object : objects) {
						UMLAssociationClass cla = assCla.get(className.name());
						String attBouName = cla.getAttributeBoundName(i);
						if(objectAttributeLowerMapping.get(className.name()) != null 
								&& objectAttributeLowerMapping.get(className.name()).get(object) != null
								&& objectAttributeLowerMapping.get(className.name()).get(object).get(attr.name()) != null) {
							String objName = attBouName + (1) + "_" + objectAttributeLowerMapping.get(className.name()).get(object).get(attr.name());
							if(!atoms.contains(objName))
								atoms.add(objName);
						} else if((objectAttributeUpperMapping.get(className.name()) != null 
								&& objectAttributeUpperMapping.get(className.name()).get(object) != null
								&& objectAttributeUpperMapping.get(className.name()).get(object).get(attr.name()) != null)) {
							ArrayList<String> values = objectAttributeUpperMapping.get(className.name()).get(object).get(attr.name());
							for(int k = 0; k < values.size(); k++) {
								String objName = attBouName + (k + 1) + "_" + values.get(k);
								if(!atoms.contains(objName))
									atoms.add(objName);
							}
						}
					}
					i++;
				}
			}
		}
		Set<String> classNames = associationClassUpperObjectBound.keySet();
		for(String className : classNames) {
			int offset = asslTranslation.getClassObjectCount(className);
			int i = 1;
			for(String objectLink : associationClassUpperObjectBound.get(className)) {
				atoms.add(objectLink+"@"+(offset + i));
				i++;
			}
		}
		return new Universe(atoms);
	}
}