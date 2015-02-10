package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.use.uml.mm.MAssociation;

public class SettingsAssociation extends Settings {
	
	private final MAssociation association;
	public final Boolean isAssociationClass;
	
	public SettingsAssociation(MAssociation association, Boolean isAC, SettingsConfiguration settingsConfiguration) {
		super(settingsConfiguration);
		this.getBounds().setLower(DefaultConfigurationValues.linksPerAssocMin);
		this.getBounds().setUpper(DefaultConfigurationValues.linksPerAssocMax);
		this.getBounds().setUpperLimited(false);
		this.association = association;
		this.isAssociationClass = isAC;
	}

	public MAssociation getAssociation() {
		return association;
	}
	
}
