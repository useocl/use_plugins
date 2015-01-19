package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;

public class SettingsOption {
	
	private Boolean aggregationcyclefreeness = DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS;
	private Boolean forbiddensharing = DefaultConfigurationValues.FORBIDDENSHARING;
	
	public SettingsOption() {
		super();
	}

	public Boolean getAggregationcyclefreeness() {
		return aggregationcyclefreeness;
	}

	public void setAggregationcyclefreeness(Boolean aggregationcyclefreeness) {
		this.aggregationcyclefreeness = aggregationcyclefreeness;
	}

	public Boolean getForbiddensharing() {
		return forbiddensharing;
	}

	public void setForbiddensharing(Boolean forbiddensharing) {
		this.forbiddensharing = forbiddensharing;
	}

}
