package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.use.uml.mm.MClassInvariant;

public class SettingsInvariant {
	private MClassInvariant invariant;
	
	private Boolean active = true;
	private Boolean negate = false;
	
	public SettingsInvariant(MClassInvariant invariant) {
		super();
		this.invariant = invariant;
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
