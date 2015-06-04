package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.HashMap;
import java.util.Map;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;

public class ClassSettings extends InstanceSettings {

	protected final IClass clazz;
	protected final Map<IAttribute, AttributeSettings> attributeSettings = new HashMap<>();
	//TODO move to general settings class, required for full list of associations in GUI
	protected final Map<IAssociation, AssociationSettings> associationSettings = new HashMap<>();

	public ClassSettings(SettingsConfiguration configurationSettings, IClass cls) {
		super(configurationSettings);
		clazz = cls;

		for (IAttribute attr : cls.allAttributes()) {
			attributeSettings.put(attr, new AttributeSettings(configurationSettings, attr, !attr.owner().equals(clazz)));
		}

		for (IAssociation assoc : cls.allAssociations()) {
			if (!(assoc instanceof IAssociationClass)
					&& assoc.associationEnds().get(0).associatedClass().equals(clazz)) {
				associationSettings.put(assoc, new AssociationSettings(configurationSettings, assoc));
			}
		}

		if (clazz instanceof IAssociationClass) {
			IAssociation assoc = clazz.model().getAssociation(clazz.name());
			associationSettings.put(assoc, new AssociationSettings(configurationSettings, assoc));
		}
	}

	public Map<IAttribute, AttributeSettings> getAttributeSettings() {
		return attributeSettings;
	}

	public Map<IAssociation, AssociationSettings> getAssociationSettings() {
		return associationSettings;
	}

	public IClass getCls() {
		return clazz;
	}
	
	@Override
	public void reset() {
		super.reset();
		lowerBound = DefaultConfigurationValues.objectsPerClassMin;
		upperBound = DefaultConfigurationValues.objectsPerClassMax;
	}
	
}
