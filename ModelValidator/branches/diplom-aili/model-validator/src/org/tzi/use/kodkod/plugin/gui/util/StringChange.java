package org.tzi.use.kodkod.plugin.gui.util;

import java.util.ArrayList;
import java.util.List;

final public class StringChange {
	
	private StringChange() {
	}
	
	/**
	 * @param values must be seperated with "," to function properly
	 * @return an ArrayList with the elements from the given string
	 */
	public static List<String> toArrayList(String values) {
		if (values != null) {
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
		} else { 
			return null;
		}
	}
	
/*	*//**
	 * @param values in the String must be splitted through ","
	 * @return an ArrayList with every element of the given String listed
	 *//*
	public static List<String> prepareForSettings(String values) {
		if (values != null) {
			String [] sepValues = values.split("[,]");
			sepValues[0] = "Set{" + sepValues[0].trim();
			for (int i = 1; i < sepValues.length-1; i++) {
				sepValues[i] = sepValues[i].trim();
			}
			sepValues[sepValues.length-1] = sepValues[sepValues.length-1].trim() + "}"; 
			List<String> listValues = new ArrayList<>();
			for (String value : sepValues) {
				listValues.add(value);
			}
			return listValues;
		} else { 
			return null;
		}
	}*/
	
	/*public static String prepareForTable(Object arrayList) {
		String string;
		if (!(arrayList instanceof String)) {
			if (arrayList == null) {
				return "";
			}
			string = arrayList.toString().trim();
			if (!string.equals("[]")){
				return string.substring(5, string.length()-2);
			} else {
				return "";
			}
		} else {
			string = (String) arrayList;
			string = string.trim();
			return string.substring(4,string.length()-1);
		}
	}*/
	
	public static List<String> formatPropertyListForSetting(Object arrayList) {
		String string;
		if (!(arrayList instanceof String)) {
			if (arrayList == null) {
				string = "";
			} else {
				string = arrayList.toString().trim();
				if (!string.equals("[]")){
					string = string.substring(5, string.length()-2);
				} else {
					string = "";
				}
			}
		} else {
			string = (String) arrayList;
			string = string.trim();
			string = string.substring(4,string.length()-1);
		}
		
		String [] sepValues = string.split("[,]");
		sepValues[0] = sepValues[0].trim();
		for (int i = 1; i < sepValues.length-1; i++) {
			sepValues[i] = sepValues[i].trim();
		}
		sepValues[sepValues.length-1] = sepValues[sepValues.length-1].trim(); 
		List<String> listValues = new ArrayList<>();
		for (String value : sepValues) {
			listValues.add(value);
		}
		return listValues;
	}
	
	public static List<String> formatSettingValuesForProperty(Object arrayList) {
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
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
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
	
	public static String backward(String string){
		return new StringBuilder(string).reverse().toString();
	}

}
