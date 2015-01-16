package org.tzi.use.kodkod.plugin.gui.model.data;

public class SettingsOption {
	
	private Boolean aggregationcyclefreeness = false;
	private Boolean forbiddensharing = true;
	
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
