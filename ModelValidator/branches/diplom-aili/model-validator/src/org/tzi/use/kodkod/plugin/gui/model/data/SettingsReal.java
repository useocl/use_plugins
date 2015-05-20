package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

public class SettingsReal {
	
	private String name = TypeConstants.REAL;
	private boolean enabled;
	private Double minimum;
	private Double maximum;
	private Double realStep;
	private List<String> values = new ArrayList<>();
	private SettingsConfiguration configurationSettings;
	
	public SettingsReal(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
	}
	
	public String name() {
		return this.name;
	}
	
	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}

	public Double getMinimum() {
		return this.minimum;
	}
	
	public void setMinimum(Double minimum) {
		this.minimum = minimum;
		this.configurationSettings.setChanged(true);
	}
	
	public Double getMaximum() {
		return this.maximum;
	}
	
	public void setMaximum(Double maximum) {
		this.maximum = maximum;
		this.configurationSettings.setChanged(true);
	}

	public Double getStep() {
		return realStep;
	}

	public void setStep(Double realStep) {
		this.realStep = realStep;
		this.configurationSettings.setChanged(true);
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(String values) {
		this.values = ChangeString.toArrayList(values);
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
