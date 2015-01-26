package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.type.TypeConstants;

public class SettingsInteger extends Settings {
	
	private String name = TypeConstants.INTEGER;
	
	public SettingsInteger(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
		this.getBounds().setLower(DefaultConfigurationValues.integerMin);
		this.getBounds().setUpper(DefaultConfigurationValues.integerMax);
	}
	
	public String name() {
		return this.name;
	}

}
