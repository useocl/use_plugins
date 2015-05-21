package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.iface.IAssociation;

public class AssociationSettings extends InstanceSettings {
	
	protected final IAssociation association;
	
	public AssociationSettings(SettingsConfiguration settingsConfiguration, IAssociation association) {
		super(settingsConfiguration);
		this.association = association;
		lowerBound = DefaultConfigurationValues.linksPerAssocMin;
		upperBound = DefaultConfigurationValues.linksPerAssocMax;
	}

	public IAssociation getAssociation() {
		return association;
	}
	
	@Override
	public void reset() {
		super.reset();
		lowerBound = DefaultConfigurationValues.linksPerAssocMin;
		upperBound = DefaultConfigurationValues.linksPerAssocMax;
	}
	
}
