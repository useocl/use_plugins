package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.use.uml.mm.MAttribute;

public class SettingsAttribute extends Settings {

	private final MAttribute attribute;
	private final SettingsClass classSettings;
	private final Boolean inherited;

	private Bounds collectionSize = new Bounds();

	public SettingsAttribute(MAttribute attribute, SettingsClass classSettings, Boolean inherited) {
		super();
		this.attribute = attribute;
		this.classSettings = classSettings;
		this.inherited = inherited;
	}
	
	public SettingsClass getClassSettings() {
		return classSettings;
	}
	
	public Boolean isInherited() {
		return this.inherited;
	}

	public MAttribute getAttribute() {
		return attribute;
	}

	public Bounds getCollectionSize() {
		return collectionSize;
	}

}
