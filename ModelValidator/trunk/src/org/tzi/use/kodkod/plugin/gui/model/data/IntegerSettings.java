package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;

public class IntegerSettings extends Settings {

	protected int minimum;
	protected int maximum;
	protected boolean enabled;
	protected Set<Integer> values = new LinkedHashSet<>();

	public IntegerSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}

	public Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
		settingsConfiguration.setChanged(true);
	}

	public Integer getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
		settingsConfiguration.setChanged(true);
	}

	public Set<Integer> getValues() {
		return values;
	}

	public void setValues(Set<Integer> values) {
		this.values = values;
		settingsConfiguration.setChanged(true);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		settingsConfiguration.setChanged(true);
	}

	public void clearValues() {
		if(values != null){
			values.clear();
		}
	}

	@Override
	public void reset() {
		minimum = DefaultConfigurationValues.integerMin;
		maximum = DefaultConfigurationValues.integerMax;
		enabled = DefaultConfigurationValues.integerEnabled;
		clearValues();
	}

}
