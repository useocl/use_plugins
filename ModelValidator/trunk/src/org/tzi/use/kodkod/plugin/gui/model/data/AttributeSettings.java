package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.iface.IAttribute;

public class AttributeSettings extends InstanceSettings {

	protected final IAttribute attribute;
	protected boolean inherited; //TODO check usefulness
	protected int collectionSizeMin;
	protected int collectionSizeMax;

	public AttributeSettings(SettingsConfiguration configurationSettings, IAttribute attribute, boolean inherited) {
		super(configurationSettings);
		this.attribute = attribute;
		this.inherited = inherited;
	}

	public IAttribute getAttribute() {
		return attribute;
	}

	public boolean isInherited() {
		return inherited;
	}
	
	public int getCollectionSizeMin() {
		return collectionSizeMin;
	}

	public void setCollectionSizeMin(int collectionSizeMin) {
		this.collectionSizeMin = collectionSizeMin;
		settingsConfiguration.setChanged(true);
	}

	public int getCollectionSizeMax() {
		return collectionSizeMax;
	}

	public void setCollectionSizeMax(int collectionSizeMax) {
		this.collectionSizeMax = collectionSizeMax;
		settingsConfiguration.setChanged(true);
	}

	@Override
	public void reset() {
		super.reset();
		lowerBound = DefaultConfigurationValues.attributesPerClassMin;
		upperBound = DefaultConfigurationValues.attributesPerClassMax;
		collectionSizeMin = DefaultConfigurationValues.attributesColSizeMin;
		collectionSizeMax = DefaultConfigurationValues.attributesColSizeMax;
	}
	
}
