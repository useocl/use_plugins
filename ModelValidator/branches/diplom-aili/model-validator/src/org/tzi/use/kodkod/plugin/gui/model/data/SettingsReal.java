package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.util.ChangeString;

public class SettingsReal {
	
	private String name = TypeConstants.REAL;
	private Double minimum;
	private Double maximum;
	private Double realStep;
	private List<String> values = new ArrayList<>();
	private SettingsConfiguration configurationSettings;
	
	public SettingsReal(SettingsConfiguration configurationSettings) {
		this.configurationSettings = configurationSettings;
	}
	
	public String name() {
		return this.name;
	}
	
	public SettingsConfiguration getConfigurationSettings() {
		return configurationSettings;
	}

	public Double getMinimum() {
		return this.minimum;
	}
	
	public void setMinimum(Object minimum) {
		if (minimum == null) {
			this.minimum = null;
		} else {
			if (minimum.equals("")) {
				this.minimum = null;
			} else {
				this.minimum = (Double) minimum;
			}
			this.configurationSettings.setChanged(true);
		}
	}
	
	public Double getMaximum() {
		return this.maximum;
	}
	
	public void setMaximum(Object maximum) {
		if (maximum == null) {
			this.maximum = null;
		} else {
			if (maximum.equals("")) {
				this.minimum = null;
			} else {
				this.maximum = (Double) maximum;
			}
			this.configurationSettings.setChanged(true);
		}
	}

	public Double getStep() {
		return realStep;
	}

	public void setStep(Object realStep) {
		if (realStep == null) {
			this.realStep = null;
		} else {
			if (realStep instanceof String) {
				if (realStep.equals("")) {
					this.realStep = null;
				} else {
					this.realStep = Double.parseDouble((String)realStep);
				}
			} else if (realStep instanceof Double) {
				this.realStep = (Double) realStep;
			}
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
