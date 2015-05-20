package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.type.TypeConstants;

public class SettingsString extends Settings {
	
	private String name = TypeConstants.STRING;
	private boolean enabled;
	
	public SettingsString(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}
	
	public String name() {
		return this.name;
	}

}
