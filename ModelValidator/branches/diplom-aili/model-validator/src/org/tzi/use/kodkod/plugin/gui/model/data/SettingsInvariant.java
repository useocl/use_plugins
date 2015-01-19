package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.use.uml.mm.MClassInvariant;

public class SettingsInvariant {
	private MClassInvariant invariant;
	
	private Boolean active;
	private Boolean negate;
	
	public SettingsInvariant(MClassInvariant invariant) {
		super();
		this.invariant = invariant;
		this.active = DefaultConfigurationValues.INVARIANT_ACTIVE;
		this.negate = DefaultConfigurationValues.INVARIANT_NEGATE;
	}

	public MClassInvariant getInvariant() {
		return invariant;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getNegate() {
		return negate;
	}

	public void setNegate(Boolean negate) {
		this.negate = negate;
	}
	
}
