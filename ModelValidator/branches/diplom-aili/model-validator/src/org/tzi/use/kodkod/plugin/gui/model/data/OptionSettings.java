package org.tzi.use.kodkod.plugin.gui.model.data;


public class OptionSettings {
	
	private Boolean aggregationcyclefreeness;
	private Boolean forbiddensharing;
	private SettingsConfiguration configurationSettings;
	
	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}

	public OptionSettings(SettingsConfiguration configurationSettings) {
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
