package org.tzi.use.kodkod.plugin.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;

public class MVClassSettings {
	
	private final MClass cls;
	
	private Bounds bounds;
	
	private List<String> values = new ArrayList<>();
	
	private Map<MAttribute,MVAttributeSettings> attributeSettings;

	/**
	 * @param cls
	 */
	public MVClassSettings(MClass cls) {
		super();
		this.cls = cls;
		
		for (MAttribute attr : cls.allAttributes()) {
			attributeSettings.put(attr,  new MVAttributeSettings(attr));
		}
	}

	/**
	 * @return the bounds
	 */
	public Bounds getBounds() {
		return bounds;
	}

	/**
	 * @param bounds the bounds to set
	 */
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

	/**
	 * @return the attributeSettings
	 */
	public Map<MAttribute, MVAttributeSettings> getAttributeSettings() {
		return attributeSettings;
	}

	/**
	 * @return the cls
	 */
	public MClass getCls() {
		return cls;
	}
}
