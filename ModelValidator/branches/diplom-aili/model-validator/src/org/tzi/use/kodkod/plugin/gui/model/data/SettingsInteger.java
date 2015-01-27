package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

public class SettingsInteger {
	
	private String name = TypeConstants.INTEGER;
	private int minimum = DefaultConfigurationValues.integerMin;
	private int maximum = DefaultConfigurationValues.integerMax;
	private List<String> values = new ArrayList<>();
	protected SettingsConfiguration configurationSettings;
	
	public SettingsInteger(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
	}
	
	public String name() {
		return this.name;
	}
	
	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}
	
	public int getMinimum() {
		return this.minimum;
	}
	
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	
	public int getMaximum() {
		return this.maximum;
	}
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
		
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(String values) {
		this.values = ChangeString.toArrayList((String)values);
		this.configurationSettings.setChanged(true);
	}
	
	public void setValues(List<String> values) {
		this.values = values;
		this.configurationSettings.setChanged(true);
	}
	
	public void deleteValues() {
		values.clear();
	}

}
