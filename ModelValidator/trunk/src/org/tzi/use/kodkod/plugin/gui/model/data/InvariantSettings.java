package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.iface.IInvariant;

public class InvariantSettings extends Settings {

	protected final IInvariant invariant;
	protected boolean active = DefaultConfigurationValues.INVARIANT_ACTIVE;
	protected boolean negate = DefaultConfigurationValues.INVARIANT_NEGATE;

	public InvariantSettings(SettingsConfiguration configurationSettings, IInvariant invariant) {
		super(configurationSettings);
		this.invariant = invariant;
	}

	public IInvariant getInvariant() {
		return invariant;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		settingsConfiguration.setChanged(true);
	}

	public boolean isNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		this.negate = negate;
		settingsConfiguration.setChanged(true);
	}

	@Override
	public void reset() {
		active = DefaultConfigurationValues.INVARIANT_ACTIVE;
		negate = DefaultConfigurationValues.INVARIANT_NEGATE;
	}
	
}
