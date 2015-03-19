package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

public abstract class Settings {
	
	private Bounds bounds = new Bounds();
	private List<String> values = new ArrayList<>();
	private SettingsConfiguration configurationSettings;
	
	public Settings(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
		bounds.setConfigurationSettings(configurationSettings);
	}
	
	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}

	public Bounds getBounds() {
		return bounds;
	}
		
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(String values) {
		if (!(values.equals("") || values == null)) {
			this.values = ChangeString.toArrayList((String)values);
			this.configurationSettings.setChanged(true);
		} else {
			deleteValues();
		}
	}
	
	public void setValues(List<String> values) {
		this.values = values;
		this.configurationSettings.setChanged(true);
	}
	
	public void deleteValues() {
		values.clear();
	}

}
