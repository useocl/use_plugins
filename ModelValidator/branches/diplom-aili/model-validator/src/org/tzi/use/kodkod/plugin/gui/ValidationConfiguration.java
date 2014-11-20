package org.tzi.use.kodkod.plugin.gui;

import java.util.HashMap;
import java.util.Map;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;

public class ValidationConfiguration {

	private final MModel model;
	
	private Map<MClass,MVClassSettings> classSettings = new HashMap<>();

	/**
	 * @param model
	 */
	public ValidationConfiguration(MModel model) {
		super();
		this.model = model;
		
		for (MClass cls : model.classes()) {
			classSettings.put(cls, new MVClassSettings(cls));
		}
	}

	public MVClassSettings getClassSettings(String clsName) {
		return getClassSettings(model.getClass(clsName));
	}
	
	public MVClassSettings getClassSettings(MClass cls) {
		return classSettings.get(cls);
	}
}
