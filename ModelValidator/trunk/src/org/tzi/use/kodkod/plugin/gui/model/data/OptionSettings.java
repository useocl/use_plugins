package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;


public class OptionSettings extends Settings {

	protected boolean aggregationcyclefreeness = DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS;
	protected boolean forbiddensharing = DefaultConfigurationValues.FORBIDDENSHARING;
	//TODO add bitwidth

	public OptionSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}

	public boolean getAggregationcyclefreeness() {
		return aggregationcyclefreeness;
	}

	public void setAggregationcyclefreeness(boolean aggregationcyclefreeness) {
		this.aggregationcyclefreeness = aggregationcyclefreeness;
		settingsConfiguration.setChanged(true);
	}

	public boolean getForbiddensharing() {
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
