package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.HashMap;
import java.util.Map;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClassImpl;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;

public class ClassSettings extends InstanceSettings {
	
	private final MClass clazz;
	private final Map<MAttribute,AttributeSettings> attributeSettings = new HashMap<>();
	private final Map<MAssociation,AssociationSettings> associationSettings = new HashMap<>();
	private final Boolean isAssociationClass;

	public ClassSettings(MClass cls, SettingsConfiguration configurationSettings) {
		super(configurationSettings);
		this.clazz = cls;
		this.isAssociationClass = cls instanceof MAssociationClassImpl;
		
		for (MAttribute attr : cls.allAttributes()) {
			attributeSettings.put(attr, new AttributeSettings(attr, this, !clazz.attributes().contains(attr), configurationSettings));
		}
		
		for (MAssociation assoc : cls.allAssociations()) {
			if (!(assoc instanceof MAssociationClassImpl)) {
				if (assoc.associationEnds().iterator().next().cls().equals(this.clazz)) {
					associationSettings.put(assoc,  new AssociationSettings(assoc, false, configurationSettings));
				}
			}
		}
		
		if (this.isAssociationClass) {
			associationSettings.put((MAssociation) this.clazz, new AssociationSettings((MAssociation) this.clazz, true, configurationSettings));
		}
	}

	public Boolean isAssociationClass() {
		return isAssociationClass;
	}

	public Map<MAttribute,AttributeSettings> getAttributeSettings() {
		return attributeSettings;
	}
	
	public Map<MAssociation,AssociationSettings> getAssociationSettings() {
		return associationSettings;
	}

	public MClass getCls() {
		return clazz;
	}
}
