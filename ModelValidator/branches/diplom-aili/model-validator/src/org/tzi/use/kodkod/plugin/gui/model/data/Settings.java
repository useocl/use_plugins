package org.tzi.use.kodkod.plugin.gui.model.data;

import java.util.ArrayList;
import java.util.List;

import org.tzi.use.kodkod.plugin.gui.util.StringChange;

public abstract class Settings {
	
	//TODO: Fuer alle relevanten Konstrukturen, sollen die Werte aus DefaultConfigurationValues.java als default Werte
	//uebernommen werden
	
	//TODO: die Values sollten nur noch die Werte beinhalten, ohne das "Set{,}" Zeug. Eine Methode muss geschrieben
	//werden, die dann diese Werte in diese "Set{}"-Form ueberfuehrt. Und eine weitere Methode, um das ganze zurueck
	//zu formatieren
	
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
	
	public String getValues() {
		return StringChange.prepareForTable(this.values);
	}
	
	public List<String> getValuesForProperties() {
		return values;
	}
	
	public void setValues(Object values) {
		if (values instanceof String) {
			this.values = StringChange.prepareForSettings((String)values);
		}
	}

}
