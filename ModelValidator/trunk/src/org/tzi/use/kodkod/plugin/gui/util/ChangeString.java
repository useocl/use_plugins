package org.tzi.use.kodkod.plugin.gui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final public class ChangeString {
	
	private ChangeString() {
	}
	
	/**
	 * @param values must be seperated with "," to function properly
	 * @return an ArrayList with the elements from the given string
	 */
	public static List<String> toArrayList(String values) {
		if (values == null) {
			return null;
		}
		
		String [] sepValues = values.split("[,]");
		sepValues[0] = sepValues[0].trim();
		for (int i = 1; i < sepValues.length-1; i++) {
			sepValues[i] = sepValues[i].trim();
		}
		List<String> listValues = new ArrayList<>();
		for (String value : sepValues) {
			listValues.add(value);
		}
		return listValues;
	}
	
	public static List<String> formatPropertyListForSetting(Object arrayList) {
		//TODO strengthen method (get rid of Object type parameter)
		//TODO return Set (LinkedHashSet) instead of List, then search for occurrences of "Sets.newLinkedHashSet("
		
		String string = "";
		if(arrayList == null){
			string = "";
		} else if(arrayList instanceof String){
			string = (String) arrayList;
			string = string.trim();
			string = string.substring(4,string.length()-1);
		} else if(arrayList instanceof Collection<?>){
			//XXX unsafe
			string = arrayList.toString().trim();
			if (!string.equals("[]")){
				string = string.substring(5, string.length()-2);
			} else {
				string = "";
			}
		}
		
		String [] sepValues = string.split(",");
		for (int i = 0; i < sepValues.length; i++) {
			sepValues[i] = sepValues[i].trim();
		}
		List<String> listValues = new ArrayList<>();
		for (String value : sepValues) {
			listValues.add(value);
		}
		return listValues;
	}
	
	public static List<String> formatSettingValuesForProperty(Object arrayList) {
		//TODO strengthen method (get rid of Object type parameter)
		//TODO return Set (LinkedHashSet) instead of List, then search for occurrences of "Sets.newLinkedHashSet("
		
		String string;
		if (!(arrayList instanceof String)) {
			if (arrayList == null) {
				string = "";
			} else {
				string = arrayList.toString().trim();
				if (!string.equals("[]")){
					string = string.substring(1, string.length()-1);
				} else {
					string = "";
				}
			}
		} else {
			string = (String) arrayList;
			string = string.trim();
		}
		
		String [] sepValues = string.split("[,]");
		sepValues[0] = "Set{"+sepValues[0].trim();
		for (int i = 1; i < sepValues.length-1; i++) {
			sepValues[i] = sepValues[i].trim();
		}
		sepValues[sepValues.length-1] = (sepValues[sepValues.length-1]+"}").trim(); 
		List<String> listValues = new ArrayList<>();
		for (String value : sepValues) {
			listValues.add(value);
		}
		return listValues;
	}
	
	public static String html(String string) {
		return "<html>"+string+"</html>";
	}
	
	public static String bold(String string) {
		return "<b>"+string+"</b>";
	}
	
	public static String italic(String string) {
		return "<i>"+string+"</i>";
	}

}
