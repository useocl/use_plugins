package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.type.TypeConstants;

public class StringSettings extends InstanceSettings {
	
	private String name = TypeConstants.STRING;
	private boolean enabled;
	
	public StringSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}
	
	public String name() {
		return this.name;
	}

}
