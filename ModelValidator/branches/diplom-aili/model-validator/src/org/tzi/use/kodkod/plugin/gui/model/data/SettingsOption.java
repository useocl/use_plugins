package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;

public class SettingsOption {
	
	private Boolean aggregationcyclefreeness = DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS;
	private Boolean forbiddensharing = DefaultConfigurationValues.FORBIDDENSHARING;
	private SettingsConfiguration configurationSettings;
	
	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}

	public SettingsOption(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
	}

	public Boolean getAggregationcyclefreeness() {
		return aggregationcyclefreeness;
	}

	public void setAggregationcyclefreeness(Boolean aggregationcyclefreeness) {
		this.aggregationcyclefreeness = aggregationcyclefreeness;
		this.configurationSettings.setChanged(true);
	}

	public Boolean getForbiddensharing() {
		return forbiddensharing;
	}

	public void setForbiddensharing(Boolean forbiddensharing) {
		this.forbiddensharing = forbiddensharing;
		this.configurationSettings.setChanged(true);
	}

}
