package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;

public class SettingsConfiguration {

	private final MModel model;
	
	private SettingsInteger integerTypeSettings = new SettingsInteger(this);
	private SettingsReal realTypeSettings = new SettingsReal(this);
	private SettingsString stringTypeSettings = new SettingsString(this);
	private SettingsOption optionSettings = new SettingsOption(this);
	private Map<MClass,SettingsClass> classSettingsMap = new HashMap<>();
	private Map<MClassInvariant, SettingsInvariant> invariantSettingsMap = new HashMap<>();
	private boolean changed = false;

	public SettingsConfiguration(MModel model) {
		super();
		this.model = model;
		
		for (MClass cls : model.classes()) {
			classSettingsMap.put(cls, new SettingsClass(cls, this));
		}
		
		for (MClassInvariant inv : model.classInvariants()) {
			invariantSettingsMap.put(inv, new SettingsInvariant(inv, this));
		}
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public SettingsInteger getIntegerTypeSettings() {
		return integerTypeSettings;
	}

	public void setIntegerTypeSettings(SettingsInteger integerTypeSettings) {
		this.integerTypeSettings = integerTypeSettings;
	}

	public SettingsReal getRealTypeSettings() {
		return realTypeSettings;
	}

	public void setRealTypeSettings(SettingsReal realTypeSettings) {
		this.realTypeSettings = realTypeSettings;
	}

	public SettingsString getStringTypeSettings() {
		return stringTypeSettings;
	}

	public void setStringTypeSettings(SettingsString stringTypeSettings) {
		this.stringTypeSettings = stringTypeSettings;
	}
	
	public SettingsClass getClassSettings(String clsName) {
		return getClassSettings(model.getClass(clsName));
	}
	
	public SettingsClass getClassSettings(MClass cls) {
		return classSettingsMap.get(cls);
	}
	
	public ArrayList<SettingsClass> getAllClassesSettings() {
		return new ArrayList<SettingsClass>(classSettingsMap.values());
	}

	public SettingsOption getOptionSettings() {
		return optionSettings;
	}

	public SettingsInvariant getInvariantSetting(String invName) {
		return getInvariantSetting(model.getClassInvariant(invName));
	}
	
	public SettingsInvariant getInvariantSetting(MClassInvariant inv) {
		return invariantSettingsMap.get(inv);
	}
	
	public ArrayList<SettingsInvariant> getAllInvariantsSettings() {
		return new ArrayList<SettingsInvariant>(invariantSettingsMap.values());
	}
	
}