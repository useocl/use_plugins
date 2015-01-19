package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.type.TypeConstants;

public class SettingsInteger extends Settings {
	
	private String name = TypeConstants.INTEGER;
	
	public SettingsInteger() {
		super();
	}
	
	public String name() {
		return this.name;
	}

}
