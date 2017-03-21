package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;


public class StringSettings extends InstanceSettings {
	
	protected boolean enabled;
	
	public StringSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		settingsConfiguration.setChanged(true);
	}
	
	@Override
	public void reset() {
		super.reset();
		lowerBound = DefaultConfigurationValues.stringMin;
		upperBound = DefaultConfigurationValues.stringMax;
		enabled = DefaultConfigurationValues.stringEnabled;
	}
	
}
