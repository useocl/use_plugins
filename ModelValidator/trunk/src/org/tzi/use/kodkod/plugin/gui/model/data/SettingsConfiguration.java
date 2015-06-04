package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;

public class SettingsConfiguration {

	private final IModel model;
	
	private final IntegerSettings integerTypeSettings = new IntegerSettings(this);
	private final StringSettings stringTypeSettings = new StringSettings(this);
	private final RealSettings realTypeSettings = new RealSettings(this);
	private final OptionSettings optionSettings = new OptionSettings(this);
	private final Map<IClass, ClassSettings> classSettingsMap = new HashMap<>();
	private final Map<IInvariant, InvariantSettings> invariantSettingsMap = new HashMap<>();
	private boolean changed = false;

	public SettingsConfiguration(IModel model) {
		this.model = model;
		
		for (IClass cls : model.classes()) {
			classSettingsMap.put(cls, new ClassSettings(this, cls));
		}
		
		for (IInvariant inv : model.classInvariants()) {
			invariantSettingsMap.put(inv, new InvariantSettings(this, inv));
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
	
	public StringSettings getStringTypeSettings() {
		return stringTypeSettings;
	}
	
	public RealSettings getRealTypeSettings() {
		return realTypeSettings;
	}
	
	public ClassSettings getClassSettings(String clsName) {
		return getClassSettings(model.getClass(clsName));
	}
	
	public ClassSettings getClassSettings(IClass cls) {
		return classSettingsMap.get(cls);
	}
	
	public List<ClassSettings> getAllClassesSettings() {
		return new ArrayList<ClassSettings>(classSettingsMap.values());
	}

	public OptionSettings getOptionSettings() {
		return optionSettings;
	}

	public InvariantSettings getInvariantSetting(String invName) {
		return getInvariantSetting(model.getInvariant(invName));
	}
	
	public InvariantSettings getInvariantSetting(IInvariant inv) {
		return invariantSettingsMap.get(inv);
	}
	
	public List<InvariantSettings> getAllInvariantsSettings() {
		return new ArrayList<InvariantSettings>(invariantSettingsMap.values());
	}
	
}