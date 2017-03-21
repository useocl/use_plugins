package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;


public class OptionSettings extends Settings {

	protected boolean aggregationcyclefreeness;
	protected boolean forbiddensharing;
	//TODO add bitwidth

	public OptionSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}

	public boolean isAggregationcyclefreeness() {
		return aggregationcyclefreeness;
	}

	public void setAggregationcyclefreeness(boolean aggregationcyclefreeness) {
		this.aggregationcyclefreeness = aggregationcyclefreeness;
		settingsConfiguration.setChanged(true);
	}

	public boolean isForbiddensharing() {
		return forbiddensharing;
	}

	public void setForbiddensharing(boolean forbiddensharing) {
		this.forbiddensharing = forbiddensharing;
		settingsConfiguration.setChanged(true);
	}

	@Override
	public void reset() {
		aggregationcyclefreeness = DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS;
		forbiddensharing = DefaultConfigurationValues.FORBIDDENSHARING;
	}
	
}
