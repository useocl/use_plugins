package org.tzi.use.kodkod.plugin.gui.model.data;

public abstract class Settings {

	protected final SettingsConfiguration settingsConfiguration;

	public Settings(SettingsConfiguration settingsConfiguration) {
		this.settingsConfiguration = settingsConfiguration;
	}

	public SettingsConfiguration getSettingsConfiguration() {
		return settingsConfiguration;
	}
	
	/**
	 * Resets the settings to default values.
	 */
	public abstract void reset();
	
}
