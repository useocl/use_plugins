package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.use.uml.mm.MAttribute;

public class SettingsAttribute extends Settings {

	private final MAttribute attribute;
	private final SettingsClass classSettings;
	private final Boolean inherited;
	private Bounds collectionSize = new Bounds();

	public SettingsAttribute(MAttribute attribute, SettingsClass classSettings, Boolean inherited, SettingsConfiguration configurationSettings) {
		super(configurationSettings);
		this.getBounds().setLower(DefaultConfigurationValues.attributesPerClassMin);
		this.getBounds().setUpper(DefaultConfigurationValues.attributesPerClassMax);
		this.getCollectionSize().setLower(DefaultConfigurationValues.attributesColSizeMin);
		this.getCollectionSize().setUpper(DefaultConfigurationValues.attributesColSizeMax);
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
