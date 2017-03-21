package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;

public class ClassSettings extends InstanceSettings {

	protected final IClass clazz;
	protected final Map<IAttribute, AttributeSettings> attributeSettings = new LinkedHashMap<>();

	public ClassSettings(SettingsConfiguration configurationSettings, IClass cls) {
		super(configurationSettings);
		clazz = cls;

		for (IAttribute attr : cls.allAttributes()) {
			attributeSettings.put(attr, new AttributeSettings(configurationSettings, attr, !attr.owner().equals(clazz)));
		}
	}

	public Map<IAttribute, AttributeSettings> getAttributeSettings() {
		return attributeSettings;
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
