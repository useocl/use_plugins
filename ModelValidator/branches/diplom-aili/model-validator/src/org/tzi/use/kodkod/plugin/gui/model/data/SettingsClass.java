package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.HashMap;
import java.util.Map;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClassImpl;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;

public class SettingsClass extends Settings {
	
	private final MClass clazz;
	private final Map<MAttribute,SettingsAttribute> attributeSettings = new HashMap<>();
	private final Map<MAssociation,SettingsAssociation> associationSettings = new HashMap<>();
	private final Boolean isAssociationClass;

	public SettingsClass(MClass cls) {
		super();
		this.clazz = cls;
		this.isAssociationClass = cls instanceof MAssociationClassImpl;
		
		for (MAttribute attr : cls.allAttributes()) {
			attributeSettings.put(attr, new SettingsAttribute(attr));
		}
		
		for (MAssociation assoc : cls.allAssociations()) {
			if (!(assoc instanceof MAssociationClassImpl)) {
				if (assoc.associationEnds().iterator().next().cls().equals(this.clazz)) {
					associationSettings.put(assoc,  new SettingsAssociation(assoc, false));
				}
			}
		}
		
		if (this.isAssociationClass) {
			associationSettings.put((MAssociation) this.clazz, new SettingsAssociation((MAssociation) this.clazz, true));
		}
	}

	public Boolean getIsAssociationClass() {
		return isAssociationClass;
	}

	public Map<MAttribute,SettingsAttribute> getAttributeSettings() {
		return attributeSettings;
	}
	
	public Map<MAssociation,SettingsAssociation> getAssociationSettings() {
		return associationSettings;
	}

	public MClass getCls() {
		return clazz;
	}
}
