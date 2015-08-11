package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;

public class SettingsConfiguration {

	private final IModel model;
	
	private final IntegerSettings integerTypeSettings = new IntegerSettings(this);
	private final StringSettings stringTypeSettings = new StringSettings(this);
	private final RealSettings realTypeSettings = new RealSettings(this);
	private final OptionSettings optionSettings = new OptionSettings(this);
	private final Map<IClass, ClassSettings> classSettings = new LinkedHashMap<>();
	private final Map<IAssociation, AssociationSettings> associationSettings = new LinkedHashMap<>();
	private final Map<IInvariant, InvariantSettings> invariantSettings = new LinkedHashMap<>();
	private boolean changed = false;

	public SettingsConfiguration(IModel model) {
		this.model = model;
		
		for (IClass cls : model.classes()) {
			classSettings.put(cls, new ClassSettings(this, cls));
		}
		
		for(IAssociation assoc : model.associations()){
			associationSettings.put(assoc, new AssociationSettings(this, assoc));
		}
		
		for (IInvariant inv : model.classInvariants()) {
			invariantSettings.put(inv, new InvariantSettings(this, inv));
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
		return classSettings.get(cls);
	}
	
	public List<ClassSettings> getAllClassesSettings() {
		return new ArrayList<ClassSettings>(classSettings.values());
	}

	public AssociationSettings getAssociationSettings(IAssociation assoc) {
		return associationSettings.get(assoc);
	}
	
	public List<AssociationSettings> getAllAssociationSettings(){
		return new ArrayList<AssociationSettings>(associationSettings.values());
	}

	public OptionSettings getOptionSettings() {
		return optionSettings;
	}

	public InvariantSettings getInvariantSetting(String invName) {
		return getInvariantSetting(model.getInvariant(invName));
	}
	
	public InvariantSettings getInvariantSetting(IInvariant inv) {
		return invariantSettings.get(inv);
	}
	
	public List<InvariantSettings> getAllInvariantsSettings() {
		return new ArrayList<InvariantSettings>(invariantSettings.values());
	}
	
	/**
	 * Resets the settings to default values.
	 */
	public void reset(){
		integerTypeSettings.reset();
		stringTypeSettings.reset();
		realTypeSettings.reset();
		optionSettings.reset();
		
		for (ClassSettings cs : classSettings.values()) {
			cs.reset();
			
			for (AttributeSettings attrS : cs.getAttributeSettings().values()) {
				attrS.reset();
			}
		}
		
		for (AssociationSettings as : associationSettings.values()) {
			as.reset();
		}
		
		for (InvariantSettings is : invariantSettings.values()) {
			is.reset();
		}
	}
	
}