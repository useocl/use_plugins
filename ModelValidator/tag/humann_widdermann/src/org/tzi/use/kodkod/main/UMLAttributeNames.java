package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.Iterator;

import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.Type;

/**
 * represents an attribute of a class or associationclass
 * contains the possible values of the attribute and its type
 * @author  Torsten Humann
 */
public class UMLAttributeNames{
	
	private String attributeName;
	private Type attributeType;
	private ArrayList<String> boundNames;
	private int maxValue;
	private int minValue;
	
	public UMLAttributeNames(String attName, Type attType){
		attributeName = attName;
		attributeType = attType;
		boundNames = new ArrayList<String>();
		minValue = 1;
		maxValue = 20;
		if(attributeType.isBoolean()){
			boundNames.add("true");
			boundNames.add("false");
		}
		if(attributeType.isReal()){
			for(int i = 0; i <= 20; i++){
				Integer I = i * 50;
				boundNames.add(I.toString());
			}
		}
	}
	
	public String getAttributeName(){
		return attributeName;
	}
	
	public ArrayList<String> getBoundNames(){
		if(boundNames.size()>0){
			return boundNames;
		}else{
			ArrayList<String> ret = new ArrayList<String>();
			ret.add("Default");
			return ret;
		}
	}
	
	public void setBoundNames(ArrayList<String> bn){
		boundNames.clear();
		boundNames = bn;
	}
	
	public Type getAttributeType(){
		return attributeType;
	}
	
	public boolean isEditable(){
		return attributeType.isString();
	}
	
	public boolean isInteger(){
		return attributeType.isInteger();
	}
	
	public void setMinValue(int v){
		minValue = v;
	}
	
	public int getMinValue(){
		return minValue;
	}
	
	public void setMaxValue(int v){
		maxValue = v;
	}
	
	public int getMaxValue(){
		return maxValue;
	}
	
	public boolean isObject(){
		return attributeType.isObjectType();
	}
	
	public String objectTypeName(){
		if(attributeType.isObjectType()){
			return attributeType.shortName();
		}else{
			return "";
		}
	}
	
	public boolean isENum(){
		return attributeType.isEnum();
	}
	
	public ArrayList<String> eNums(){
		ArrayList<String> ret = new ArrayList<String>();
		if(attributeType.isEnum()){
			EnumType enType = (EnumType) attributeType;
			Iterator<?> it = enType.literals();
			while (it.hasNext() ) {
	            ret.add((String) it.next());
			}
		}
		return ret;
	}
	
}