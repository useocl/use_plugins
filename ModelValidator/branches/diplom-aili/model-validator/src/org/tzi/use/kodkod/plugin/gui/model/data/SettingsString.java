package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.type.TypeConstants;

public class SettingsString extends Settings {
	
	private String name = TypeConstants.STRING;
	
	public SettingsString() {
		super();
		this.getBounds().setLower(DefaultConfigurationValues.stringMin);
		this.getBounds().setUpper(DefaultConfigurationValues.stringMax);
	}
	
	public String name() {
		return this.name;
	}

}
