package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.type.TypeConstants;

public class SettingsString extends Settings {
	
	private String name = TypeConstants.STRING;
	
	public SettingsString() {
		super();
	}
	
	public String name() {
		return this.name;
	}

}
