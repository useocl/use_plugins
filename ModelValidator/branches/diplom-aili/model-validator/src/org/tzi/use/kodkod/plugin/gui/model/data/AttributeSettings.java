package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.use.uml.mm.MAttribute;

public class AttributeSettings extends InstanceSettings {

	private final MAttribute attribute;
	private final ClassSettings classSettings;
	private final Boolean inherited;
	private Bounds collectionSize = new Bounds();

	public AttributeSettings(MAttribute attribute, ClassSettings classSettings, Boolean inherited, SettingsConfiguration configurationSettings) {
		super(configurationSettings);
		this.getBounds().setLowerLimited(false);
		this.getBounds().setUpperLimited(false);
		this.getCollectionSize().setUpperLimited(false);
		this.attribute = attribute;
		this.classSettings = classSettings;
		this.inherited = inherited;
	}

	public ClassSettings getClassSettings() {
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
