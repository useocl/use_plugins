package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class InstanceSettings extends Settings {

	protected int lowerBound;
	protected int upperBound;
	protected Set<String> instanceNames = new LinkedHashSet<>();

	public InstanceSettings(SettingsConfiguration configurationSettings) {
		super(configurationSettings);
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
		settingsConfiguration.setChanged(true);
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
		settingsConfiguration.setChanged(true);
	}

	public Set<String> getInstanceNames() {
		return instanceNames;
	}

	public void setInstanceNames(Set<String> values) {
		instanceNames = values;
		settingsConfiguration.setChanged(true);
	}

	public void clearValues() {
		instanceNames.clear();
	}

	@Override
	public void reset() {
		clearValues();
	}
	
}
