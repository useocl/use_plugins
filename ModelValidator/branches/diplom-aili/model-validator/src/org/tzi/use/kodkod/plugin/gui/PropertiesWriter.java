package org.tzi.use.kodkod.plugin.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.impl.AssociationClass;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;

public class PropertiesWriter {
	
	private static BufferedWriter writer;
	private static MModel model;
	
	public static void writeToFile(Hashtable<String,PropertiesConfiguration> pCs, File file, MModel modl) throws Exception {
		if (file.exists()) {
			file.delete();
		} 
		file.createNewFile();
		writer = new BufferedWriter(new FileWriter(file));
		model = modl;
		
		
		Iterator<String> sectionsIterator = pCs.keySet().iterator();
		while (sectionsIterator.hasNext()) {
			String section = sectionsIterator.next();
			writeSection(section, pCs.get(section));
		}
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}
	
	private static void writeSection(String section, PropertiesConfiguration pc) {
		writeDivideLine("["+section+"]");
		writeNewLine();
		writeBasicTypes(pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		int i = 0;
		for (MClass clazz : model.classes()) {
			writeClass(clazz, pc);
			writeNewLine();
			writeAttributes(clazz.attributes().iterator(), pc);
			i++;
			if (!(i >= model.classes().size())) writeDivideLine(PropertyEntry.LIGHT_DIVIDE_LINE);
		}
		writeAssociations(model.associations().iterator(), pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeInvariants(model.classInvariants().iterator(), pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeOptions(pc);
		writeNewLine();
		writeNewLine();
	}
	
	private static void writeBasicTypes(PropertiesConfiguration pc) {
		String real = "Real";
		String realMin = real+PropertyEntry.realValueMin;
		String realMax = real+PropertyEntry.realValueMax;
		String realStep = real+PropertyEntry.realStep;
		if (pc.containsKey(real) && pc.getProperty(real) != null) {
			write(real, propertyToString(pc.getProperty(real)));
		}
		if (pc.containsKey(realMin)) {
			write(realMin, pc.getInt(realMin,DefaultConfigurationValues.realMin));
		}
		if (pc.containsKey(realMax)) {
			write(realMax, pc.getInt(realMax,DefaultConfigurationValues.realMax));
		}
		if (pc.containsKey(realStep)) {
			write(realStep, pc.getDouble(realStep,DefaultConfigurationValues.realStep));
		}
		
		String string = "String";
		String stringMin = string+PropertyEntry.stringValuesMin;
		String stringMax = string+PropertyEntry.stringValuesMax;
		if (pc.containsKey(string) && pc.getProperty(string) != null) {
			write(string, propertyToString(pc.getProperty(string)));
		}
		if (pc.containsKey(stringMin)) {
			write(stringMin, pc.getInt(stringMin,DefaultConfigurationValues.stringMin));
		}
		if (pc.containsKey(stringMax)) {
			write(stringMax, pc.getInt(stringMax,DefaultConfigurationValues.stringMax));
		}
		
		String integer = "Integer";
		String integerMin = integer + PropertyEntry.integerValueMin;
		String integerMax = integer + PropertyEntry.integerValueMax;
		if (pc.containsKey(integer) && pc.getProperty(integer) != null) {
			write(integer, propertyToString(pc.getProperty(integer)));
		}
		if (pc.containsKey(integerMin)) {
			write(integerMin, pc.getInt(integerMin,DefaultConfigurationValues.integerMin));
		}
		if (pc.containsKey(integerMax)) {
			write(integerMax, pc.getInt(integerMax,DefaultConfigurationValues.integerMax));
		}
	}
	
	private static void writeClass(MClass clazz, PropertiesConfiguration pc) {
		String cls = clazz.name();
		String clsMin = cls + PropertyEntry.objMin;
		String clsMax = cls + PropertyEntry.objMax;
		if (!clazz.isAbstract() && pc.containsKey(cls) && pc.getProperty(cls) != null) {
			write(cls, propertyToString(pc.getProperty(cls)));
		}
		if (clazz instanceof MAssociationClass) {
			return;
		}
		if (pc.containsKey(clsMin)) {
			write(clsMin, pc.getInt(clsMin,DefaultConfigurationValues.objectsPerClassMin));
		}
		
		if (pc.containsKey(clsMax)) {
			write(clsMax, pc.getInt(clsMax,DefaultConfigurationValues.objectsPerClassMax));
		}
	}
	
	private static void writeAttributes(Iterator<MAttribute> iterator, PropertiesConfiguration pc) {
		while (iterator.hasNext()) {
			MAttribute attribute = iterator.next();
			String attr = attribute.owner().name() + "_" + attribute.name();
			String attrMin = attr + PropertyEntry.attributeDefValuesMin;
			String attrMax = attr + PropertyEntry.attributeDefValuesMax;
			String attrMinSize = attr + PropertyEntry.attributeColSizeMin;
			String attrMaxSize = attr + PropertyEntry.attributeColSizeMax;
			if (pc.containsKey(attr) && pc.getProperty(attr) != null) {
				write(attr, propertyToString(pc.getProperty(attr)));
			}
			if (pc.containsKey(attrMin)) {
				write(attrMin, pc.getInt(attrMin,DefaultConfigurationValues.attributesPerClassMin));
			}
			if (pc.containsKey(attrMax)) {
				write(attrMax, pc.getInt(attrMax,DefaultConfigurationValues.attributesPerClassMax));
			}
			if (pc.containsKey(attrMinSize)) {
				write(attrMinSize, pc.getInt(attrMinSize,DefaultConfigurationValues.attributesColSizeMin));
			}
			if (pc.containsKey(attrMaxSize)) {
				write(attrMaxSize, pc.getInt(attrMaxSize,DefaultConfigurationValues.attributesColSizeMax));
			}
		}
	}
	
	private static void writeAssociations(Iterator<MAssociation> iterator, PropertiesConfiguration pc) {
		while (iterator.hasNext()) {
			MAssociation association = iterator.next();
			String assoc = association.name();
			String assocMin = assoc+PropertyEntry.linksMin;
			String assocMax = assoc+PropertyEntry.linksMax;
			if (!association.getClass().equals(AssociationClass.class)){
				writeAssociationLine(association);
				writeNewLine();
				if ((pc.containsKey(assoc) && (pc.getProperty(assoc) != null))) {
					write(assoc, propertyToString(pc.getProperty(assoc)));
				}
				if (pc.containsKey(assocMin)) {
					write(assocMin, pc.getInt(assocMin, DefaultConfigurationValues.linksPerAssocMin));
				}
				if (pc.containsKey(assocMax)) {
					write(assocMax, pc.getInt(assocMax, DefaultConfigurationValues.linksPerAssocMax));
				}
			}
		}
	}
	
	private static void writeInvariants(Iterator<MClassInvariant> iterator, PropertiesConfiguration pc) {
		while (iterator.hasNext()) {
			MClassInvariant invariant = iterator.next();
			//String invKey = invariant.name().replaceFirst("::", "_");
			String inv = invariant.cls().name()+"_"+invariant.name();
			if (pc.containsKey(inv)) {
				write(inv, pc.getString(inv));
			}
		}
	}
	
	private static void writeOptions(PropertiesConfiguration pc) {
		if (pc.containsKey("aggregationcyclefreeness")) {
			write("aggregationcyclefreeness", pc.getString("aggregationcyclefreeness"));
		}
		if (pc.containsKey("forbiddensharing")) {
			write("forbiddensharing", pc.getString("forbiddensharing"));
		}
	}
	
	private static String propertyToString(Object arrayList) {
		String string;
		if (!arrayList.getClass().equals(String.class)) {
			string = arrayList.toString().trim();
			return string.substring(1, string.length()-1);
		} else {
			string = (String) arrayList;
			string = string.trim();
			return string;
		}
	}
	
	private static void writeNewLine() {
		try {
			writer.newLine();
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}
	
	private static void writeDivideLine(String line) {
		try {
			writer.write(line);
			writer.newLine();
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}
	
	private static void write(String name, double value) {
		try {
			writer.write(name + " = " + value);
			writer.newLine();
			//writer.newLine();
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}
	
	private static void write(String name, int value) {
		try {
			writer.write(name + " = " + value);
			writer.newLine();
			//writer.newLine();
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}
	
	private static void write(String name, String value) {
		try {
			writer.write(name + " = " + value);
			writer.newLine();
			//writer.newLine();
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}
	
	private static void writeAssociationLine(MAssociation association) {
		String writerString = "";
		Iterator<MAssociationEnd> aes = association.associationEnds().iterator();
		writerString = writerString + "-- ";
		writerString = writerString + association.name();
		writerString = writerString + "(";
		while (aes.hasNext()) {
			MAssociationEnd ae = aes.next();
			writerString = writerString + ae.name() + ":" + ae.cls();
			if (aes.hasNext()) {
				writerString = writerString + ",";
			}
		}
		writerString = writerString + ") ";
		int writerStringLength = writerString.length();
		if (writerStringLength < PropertyEntry.PUNCHED_CARD_LENGTH) {
			for (int i=0; i < (PropertyEntry.PUNCHED_CARD_LENGTH - writerStringLength); i++) {
				writerString = writerString + "-";
			}
		}
		try {
			writer.write(writerString);
		} catch (IOException e) {
			System.out.println(e.getMessage()); //TODO: Exceptionhandling
		}
	}

}
