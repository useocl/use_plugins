package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;

public class SettingsConfiguration {

	private final MModel model;
	
	private IntegerSettings integerTypeSettings = new IntegerSettings(this);
	private StringSettings stringTypeSettings = new StringSettings(this);
	private RealSettings realTypeSettings = new RealSettings(this);
	private OptionSettings optionSettings = new OptionSettings(this);
	private Map<MClass, ClassSettings> classSettingsMap = new HashMap<>();
	private Map<MClassInvariant, InvariantSettings> invariantSettingsMap = new HashMap<>();
	private boolean changed = false;

	public SettingsConfiguration(MModel model) {
		this.model = model;
		
		for (MClass cls : model.classes()) {
			classSettingsMap.put(cls, new ClassSettings(cls, this));
		}
		
		for (MClassInvariant inv : model.classInvariants()) {
			invariantSettingsMap.put(inv, new InvariantSettings(inv, this));
		}
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public IntegerSettings getIntegerTypeSettings() {
		return integerTypeSettings;
	}
	
	public void setIntegerTypeSettings(IntegerSettings integerTypeSettings) {
		this.integerTypeSettings = integerTypeSettings;
	}
	
	public StringSettings getStringTypeSettings() {
		return stringTypeSettings;
	}
	
	public void setStringTypeSettings(StringSettings stringTypeSettings) {
		this.stringTypeSettings = stringTypeSettings;
	}
	
	public RealSettings getRealTypeSettings() {
		return realTypeSettings;
	}
	
	public void setRealTypeSettings(RealSettings realTypeSettings) {
		this.realTypeSettings = realTypeSettings;
	}
	
	public ClassSettings getClassSettings(String clsName) {
		return getClassSettings(model.getClass(clsName));
	}
	
	public ClassSettings getClassSettings(MClass cls) {
		return classSettingsMap.get(cls);
	}
	
	public List<ClassSettings> getAllClassesSettings() {
		return new ArrayList<ClassSettings>(classSettingsMap.values());
	}

	public OptionSettings getOptionSettings() {
		return optionSettings;
	}

	public InvariantSettings getInvariantSetting(String invName) {
		return getInvariantSetting(model.getClassInvariant(invName));
	}
	
	public InvariantSettings getInvariantSetting(MClassInvariant inv) {
		return invariantSettingsMap.get(inv);
	}
	
	public List<InvariantSettings> getAllInvariantsSettings() {
		return new ArrayList<InvariantSettings>(invariantSettingsMap.values());
	}
	
}