package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

public class SettingsReal {
	
	private String name = TypeConstants.REAL;
	private Double minimum = 1.0 * DefaultConfigurationValues.realMin;
	private Double maximum = 1.0 * DefaultConfigurationValues.realMax;
	private Double realStep = DefaultConfigurationValues.realStep;
	private List<String> values = new ArrayList<>();
	protected SettingsConfiguration configurationSettings;
	
	public SettingsReal(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
	}
	
	public String name() {
		return this.name;
	}
	
	public Double getMinimum() {
		return this.minimum;
	}
	
	public void setMinimum(Double minimum) {
		this.minimum = minimum;
	}
	
	public Double getMaximum() {
		return this.maximum;
	}
	
	public void setMaximum(Double maximum) {
		this.maximum = maximum;
	}

	public Double getStep() {
		return realStep;
	}
	
	public void setStep(Double step) {
		this.realStep = step;
	}

	public void setStep(Object realStep) {
		if (realStep instanceof String) {
			this.realStep = Double.parseDouble((String)realStep);
			this.configurationSettings.setChanged(true);
		} else if (realStep instanceof Double) {
			this.realStep = (Double) realStep;
			this.configurationSettings.setChanged(true);
		}
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(String values) {
		this.values = ChangeString.toArrayList((String)values);
		this.configurationSettings.setChanged(true);
	}
	
	public void setValues(List<String> values) {
		this.values = values;
		this.configurationSettings.setChanged(true);
	}
	
	public void deleteValues() {
		values.clear();
	}

}
