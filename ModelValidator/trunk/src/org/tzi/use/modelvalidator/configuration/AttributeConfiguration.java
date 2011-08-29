package org.tzi.use.modelvalidator.configuration;

import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MAttribute;

public class AttributeConfiguration {

	private MAttribute attr;
	private Map<String, String> concreteMandatoryValues;
	private Boolean setElementsFix;
	private List<String> domain;
	private int minimumNumberOfDefinedValues;
	private int maximumNumberOfDefinedValues;

	public AttributeConfiguration(MAttribute attr,
			Map<String, String> concreteMandatoryValues, Boolean undefinedFix,
			Boolean setElementsFix, List<String> domain,
			int minimumNumberOfDefinedValues, int maximumNumberOfDefinedValues) {
		this.attr = attr;
		this.concreteMandatoryValues = concreteMandatoryValues;
		this.setElementsFix = setElementsFix;
		this.domain = domain;
		this.minimumNumberOfDefinedValues = minimumNumberOfDefinedValues;
		this.maximumNumberOfDefinedValues = maximumNumberOfDefinedValues;
	}	
	
	public MAttribute getAttr() {
		return attr;
	}

	public void setAttr(MAttribute attr) {
		this.attr = attr;
	}

	public Map<String, String> getConcreteMandatoryValues() {
		return concreteMandatoryValues;
	}

	public void setConcreteMandatoryValues(
			Map<String, String> concreteMandatoryValues) {
		this.concreteMandatoryValues = concreteMandatoryValues;
	}

	public Boolean getSetElementsFix() {
		return setElementsFix;
	}

	public void setSetElementsFix(Boolean setElementsFix) {
		this.setElementsFix = setElementsFix;
	}

	public List<String> getDomain() {
		return domain;
	}

	public void setDomain(List<String> domain) {
		this.domain = domain;
	}

	public int getMinimumNumberOfDefinedValues() {
		return minimumNumberOfDefinedValues;
	}

	public void setMinimumNumberOfDefinedValues(int minimumNumberOfDefinedValues) {
		this.minimumNumberOfDefinedValues = minimumNumberOfDefinedValues;
	}

	public int getMaximumNumberOfDefinedValues() {
		return maximumNumberOfDefinedValues;
	}

	public void setMaximumNumberOfDefinedValues(int maximumNumberOfDefinedValues) {
		this.maximumNumberOfDefinedValues = maximumNumberOfDefinedValues;
	}
}
