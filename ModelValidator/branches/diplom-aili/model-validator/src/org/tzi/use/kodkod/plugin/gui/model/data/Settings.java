package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.kodkod.plugin.gui.util.StringChange;

public abstract class Settings {
	
	private Bounds bounds = new Bounds();
	//the values are prepared for adding easily through iteration into a PropertiesConfiguration object, like:
	//if (values.hasNext()) propertiesConfiguration.setProperty(key, values.next());
	//while (values.hasNext()) {
	//  propertiesConfiguration.addProperty(key, values.next());
	//}
	private List<String> values = new ArrayList<>();
	
	public Bounds getBounds() {
		return bounds;
	}
		
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(String values) {
		this.values = StringChange.toArrayList((String)values);
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}

}
