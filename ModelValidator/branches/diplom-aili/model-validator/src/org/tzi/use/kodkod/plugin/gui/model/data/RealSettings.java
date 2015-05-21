package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.LinkedHashSet;
import java.util.Set;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;

public class RealSettings extends Settings {

	protected double minimum = DefaultConfigurationValues.realMin;
	protected double maximum = DefaultConfigurationValues.realMax;
	protected double realStep = DefaultConfigurationValues.realStep;
	protected boolean enabled = DefaultConfigurationValues.realEnabled;
	protected Set<Double> values = new LinkedHashSet<>();

	public RealSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}

	public double getMinimum() {
		return minimum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
		settingsConfiguration.setChanged(true);
	}

	public double getMaximum() {
		return maximum;
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
		settingsConfiguration.setChanged(true);
	}

	public double getStep() {
		return realStep;
	}

	public void setStep(double realStep) {
		this.realStep = realStep;
		settingsConfiguration.setChanged(true);
	}

	public Set<Double> getValues() {
		return values;
	}

	public void setValues(Set<Double> values) {
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
		values.clear();
	}

	@Override
	public void reset() {
		minimum = DefaultConfigurationValues.realMin;
		maximum = DefaultConfigurationValues.realMax;
		realStep = DefaultConfigurationValues.realStep;
		enabled = DefaultConfigurationValues.realEnabled;
		clearValues();
	}
	
}
