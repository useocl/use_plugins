package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

public class Bounds {
	
	private Integer lower;
	private Integer upper;
	private boolean lowerLimited = true;
	private boolean upperLimited = true;
	private SettingsConfiguration configurationSettings;
	
	/**
	 * Constructs a new instance using the 
	 * default values 1 for lower and upper bound and
	 * them limited.
	 */
	public Bounds() {
		super();
	}
	
	/**
	 * @param lower
	 * @param upper
	 * @param lowerLimited
	 * @param upperLimited
	 */
	public Bounds(int lower, int upper, boolean lowerLimited, boolean upperLimited) {
		super();
		this.lower = lower;
		this.upper = upper;
		this.lowerLimited = lowerLimited;
		this.upperLimited = upperLimited;
	}
	
	public boolean lowerIsLimited() {
		return lowerLimited;
	}

	public boolean upperIsLimited() {
		return upperLimited;
	}

	public void setLowerLimited(boolean lowerLimited) {
		this.lowerLimited = lowerLimited;
	}

	public void setUpperLimited(boolean upperLimited) {
		this.upperLimited = upperLimited;
	}

	/**
	 * @return the lower
	 */
	public Integer getLower() {
		return lower;
	}
	/**
	 * @param lower the lower to set
	 */
	public void setLower(Object lower) {
		if (lower instanceof String) {
			if (lower.equals("")) {
				this.lower = null;
			} else	if (ChangeString.isInteger((String)lower)) {
				int low = Integer.parseInt((String) lower);
				if (!(lowerIsLimited() && low < 0)) {
					this.lower = low;
				}
			}
		} else if (lower instanceof Integer) {
			if (!(lowerIsLimited() && (int) lower < 0)) {
				this.lower = (int) lower;
			}
		}
		if (this.configurationSettings != null) {
			this.configurationSettings.setChanged(true);
		}
	}
	
	/**
	 * @return the upper
	 */
	public Integer getUpper() {
		return upper;
	}
	
	/**
	 * @param upper the upper to set
	 */
	public void setUpper(Object upper) {
		if (upper instanceof String) {
			if (upper.equals("")) {
				upper = null;
			} else if (ChangeString.isInteger((String)upper)) {
				int upp = Integer.parseInt((String)upper);
				if (!(upperIsLimited() && upp < 0)) {
					this.upper = Integer.parseInt((String) upper);
				}
			}
		} else if (upper instanceof Integer) {
			if (!(upperIsLimited() && (int) upper < 0)) {
				this.upper = (int) upper;
			}
		}
		if (this.configurationSettings != null) {
			this.configurationSettings.setChanged(true);
		}
	}

	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}

	public void setConfigurationSettings(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
	}
}
