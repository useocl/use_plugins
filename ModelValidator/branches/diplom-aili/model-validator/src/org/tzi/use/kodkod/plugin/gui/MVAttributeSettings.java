package org.tzi.use.kodkod.plugin.gui;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.uml.mm.MAttribute;

public class MVAttributeSettings {

	private final MAttribute attribute;
	
	private Bounds definingInstances = new Bounds();
	
	private Bounds collectionSize = new Bounds();
	
	List<String> values = new ArrayList<>();

	/**
	 * @param attribute
	 */
	public MVAttributeSettings(MAttribute attribute) {
		super();
		this.attribute = attribute;
	}

	/**
	 * @return the attribute
	 */
	public MAttribute getAttribute() {
		return attribute;
	}


	/**
	 * @return the definingInstances
	 */
	public Bounds getDefiningInstances() {
		return definingInstances;
	}

	/**
	 * @param definingInstances the definingInstances to set
	 */
	public void setDefiningInstances(Bounds definingInstances) {
		this.definingInstances = definingInstances;
	}

	/**
	 * @return the collectionSize
	 */
	public Bounds getCollectionSize() {
		return collectionSize;
	}

	/**
	 * @param collectionSize the collectionSize to set
	 */
	public void setCollectionSize(Bounds collectionSize) {
		this.collectionSize = collectionSize;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
}
