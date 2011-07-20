package org.tzi.use.modelvalidator.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tzi.use.uml.mm.MClass;

public class ClassConfiguration {

	private MClass cls;
	private List<String> concreteMandatoryObjects;
	private List<String> concreteOptionalObjects;
	private int minimumNumberOfObjects;
	private int maximumNumberOfObjects;

	public ClassConfiguration(	MClass cls,
								List<String> concreteMandatoryObjects,
								List<String> concreteOptionalObjects, 
								int minimumNumberOfObjects,
								int maximumNumberOfObjects) {
		this.cls = cls;
		this.concreteMandatoryObjects = concreteMandatoryObjects;
		this.concreteOptionalObjects = concreteOptionalObjects;		
		this.minimumNumberOfObjects = minimumNumberOfObjects;
		this.maximumNumberOfObjects = maximumNumberOfObjects;
	}

	public MClass getCls() {
		return cls;
	}

	public List<String> getConcreteMandatoryObjects() {
		return concreteMandatoryObjects;
	}

	public List<String> getConcreteOptionalObjects() {
		return concreteOptionalObjects;
	}

	public int getMinimumNumberOfObjects() {
		return minimumNumberOfObjects;
	}

	public int getMaximumNumberOfObjects() {
		return maximumNumberOfObjects;
	}
	
}
