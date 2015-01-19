package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.type.TypeConstants;

public class SettingsReal extends Settings{
	
	private String name = TypeConstants.REAL;
	private Double realStep = DefaultConfigurationValues.realStep;
	
	public SettingsReal() {
		super();
		this.getBounds().setLower(DefaultConfigurationValues.realMin);
		this.getBounds().setUpper(DefaultConfigurationValues.realMax);
	}
	
	public String name() {
		return this.name;
	}

	public Double getStep() {
		return realStep;
	}

	public void setStep(Object realStep) {
		if (realStep instanceof String) {
			this.realStep = Double.parseDouble((String)realStep);
		}
	}

}
