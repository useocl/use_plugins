package org.tzi.use.kodkod.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.util.StringUtil;

public class PropertiesWriter {

	private final IModel iModel;

	private BufferedWriter writer;
	private boolean isDefaultConfiguration = false;

	public PropertiesWriter(IModel model){
		iModel = model;
	}

	/**
	 * Default configuration files have special handling for attributes.
	 */
	public void setIsDefaultConfiguration(boolean isDefault) {
		isDefaultConfiguration = isDefault;
	}

	public void writeToFile(File file, Map<String, Configuration> pCs) throws IOException {
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		writer = new BufferedWriter(new FileWriter(file));
		Iterator<String> sectionsIterator = pCs.keySet().iterator();
		while (sectionsIterator.hasNext()) {
			String section = sectionsIterator.next();
			writeSection(section, pCs.get(section));
		}
		writer.close();
	}

	public void writeToFile(File file, Configuration pc) throws IOException {
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		writer = new BufferedWriter(new FileWriter(file));
		writeSection(PropertyEntry.DEFAULT_SECTION_NAME, pc);
		writer.close();
	}

	private void writeSection(String section, Configuration pc) throws IOException {
		write("["+section+"]");
		writeNewLine();
		writeBasicTypes(pc);
		write(PropertyEntry.STRONG_DIVIDE_LINE);
		int i = 0;
		for (IClass clazz : iModel.classes()) {
			writeClass(clazz, pc);
			writeNewLine();
			writeAttributes(clazz.attributes(), pc);
			i++;

			List<IAssociation> classAssociations = new ArrayList<>();
			for (IAssociation association : iModel.associations()) {
				if (association.associationEnds().get(0).associatedClass().equals(clazz)) {
					classAssociations.add(association);
				}
			}
			for (IAssociation association : classAssociations) {
				writeAssociation(association, pc);
			}
			if (!(i >= iModel.classes().size())) {
				write(PropertyEntry.LIGHT_DIVIDE_LINE);
			}
		}
		
		writeInvariants(iModel.classInvariants(), pc);
		writeOptions(pc);
		writeNewLine();
	}

	private void writeBasicTypes(Configuration pc) throws IOException {
		boolean needSeperator = false;
		String integer = TypeConstants.INTEGER;
		String integerMin = integer + PropertyEntry.integerValuesMin;
		String integerMax = integer + PropertyEntry.integerValuesMax;
		if (pc.containsKey(integer) && pc.getProperty(integer) != null) {
			writeProperty(integer, propertyToString(pc.getProperty(integer)));
			needSeperator = true;
		}
		if (pc.containsKey(integerMin)) {
			writeProperty(integerMin, pc.getInt(integerMin, DefaultConfigurationValues.integerMin));
			needSeperator = true;
		}
		if (pc.containsKey(integerMax)) {
			writeProperty(integerMax, pc.getInt(integerMax, DefaultConfigurationValues.integerMax));
			needSeperator = true;
		}

		if(needSeperator){
			writeNewLine();
			needSeperator = false;
		}
		
		String string = TypeConstants.STRING;
		String stringMin = string + PropertyEntry.stringValuesMin;
		String stringMax = string + PropertyEntry.stringValuesMax;
		if (pc.containsKey(string) && pc.getProperty(string) != null) {
			writeProperty(string, propertyToString(pc.getProperty(string)));
			needSeperator = true;
		}
		if (pc.containsKey(stringMin)) {
			writeProperty(stringMin, pc.getInt(stringMin, DefaultConfigurationValues.stringMin));
			needSeperator = true;
		}
		if (pc.containsKey(stringMax)) {
			writeProperty(stringMax, pc.getInt(stringMax, DefaultConfigurationValues.stringMax));
			needSeperator = true;
		}

		if(needSeperator){
			writeNewLine();
			needSeperator = false;
		}
		
		String real = TypeConstants.REAL;
		String realMin = real + PropertyEntry.realValuesMin;
		String realMax = real + PropertyEntry.realValuesMax;
		String realStep = real + PropertyEntry.realStep;
		if (pc.containsKey(real) && pc.getProperty(real) != null) {
			writeProperty(real, propertyToString(pc.getProperty(real)));
		}
		if (pc.containsKey(realMin)) {
			writeProperty(realMin, pc.getDouble(realMin, DefaultConfigurationValues.realMin));
		}
		if (pc.containsKey(realMax)) {
			writeProperty(realMax, pc.getDouble(realMax, DefaultConfigurationValues.realMax));
		}
		if (pc.containsKey(realStep)) {
			writeProperty(realStep, pc.getDouble(realStep, DefaultConfigurationValues.realStep));
		}
	}

	private void writeClass(IClass clazz, Configuration pc) throws IOException {
		String cls = clazz.name();
		String clsMin = cls + PropertyEntry.objMin;
		String clsMax = cls + PropertyEntry.objMax;
		if (!clazz.isAbstract()) {
			if ((clazz instanceof IAssociationClass) && pc.containsKey(cls + PropertyEntry.ASSOCIATIONCLASS)
					&& pc.getProperty(cls + PropertyEntry.ASSOCIATIONCLASS) != null) {
				writeProperty(cls+PropertyEntry.ASSOCIATIONCLASS, propertyToString(pc.getProperty(cls+PropertyEntry.ASSOCIATIONCLASS)));
			} else if (!(clazz instanceof IAssociationClass)) {
				if (pc.containsKey(cls) && pc.getProperty(cls) != null) {
					writeProperty(cls, propertyToString(pc.getProperty(cls)));
				}
				if (pc.containsKey(clsMin) && pc.getProperty(clsMin) != null) {
					writeProperty(clsMin, pc.getInt(clsMin, DefaultConfigurationValues.objectsPerClassMin));
				}

				if (!(clazz instanceof IAssociationClass) && pc.getProperty(clsMax) != null) {
					writeProperty(clsMax, pc.getInt(clsMax, DefaultConfigurationValues.objectsPerClassMax));
				}
			}
		}
	}

	private void writeAttributes(Collection<IAttribute> attributes, Configuration pc) throws IOException {
		for(IAttribute attribute : attributes){
			String attr = attribute.owner().name() + "_" + attribute.name();
			String attrMin = attr + PropertyEntry.attributeDefValuesMin;
			String attrMax = attr + PropertyEntry.attributeDefValuesMax;
			String attrMinSize = attr + PropertyEntry.attributeColSizeMin;
			String attrMaxSize = attr + PropertyEntry.attributeColSizeMax;

			if(isDefaultConfiguration){
				writer.write(PropertyEntry.COMMENT_LABEL + attr + " = Set{ ... }");
				writeNewLine();
			} else {
				if (pc.containsKey(attr) && pc.getProperty(attr) != null) {
					writeProperty(attr, propertyToString(pc.getProperty(attr)));
				}
				if (pc.containsKey(attrMin)) {
					writeProperty(attrMin, pc.getInt(attrMin, DefaultConfigurationValues.attributesPerClassMin));
				}
				if (pc.containsKey(attrMax)) {
					writeProperty(attrMax, pc.getInt(attrMax, DefaultConfigurationValues.attributesPerClassMax));
				}
				if (pc.containsKey(attrMinSize)) {
					writeProperty(attrMinSize, pc.getInt(attrMinSize, DefaultConfigurationValues.attributesColSizeMin));
				}
				if (pc.containsKey(attrMaxSize)) {
					writeProperty(attrMaxSize, pc.getInt(attrMaxSize, DefaultConfigurationValues.attributesColSizeMax));
				}
			}
		}
	}

	private void writeAssociation(IAssociation association, Configuration pc) throws IOException {
		String assoc = association.name();
		String assocMin = assoc+PropertyEntry.linksMin;
		String assocMax = assoc+PropertyEntry.linksMax;
		writeAssociationHeader(association);
		writeNewLine();
		if ((pc.containsKey(assoc) && (pc.getProperty(assoc) != null))) {
			writeProperty(assoc, propertyToString(pc.getProperty(assoc)));
		}
		if (pc.containsKey(assocMin) && (pc.getProperty(assocMin) != null)) {
			writeProperty(assocMin, pc.getInt(assocMin, DefaultConfigurationValues.linksPerAssocMin));
		}
		if (pc.containsKey(assocMax) && (pc.getProperty(assocMax) != null)) {
			writeProperty(assocMax, pc.getInt(assocMax, DefaultConfigurationValues.linksPerAssocMax));
		}
	}

	private void writeInvariants(Collection<IInvariant> invariants, Configuration pc) throws IOException {
		boolean first = true;
		for(IInvariant invariant : invariants){
			String inv = invariant.clazz().name()+"_"+invariant.name();
			if (pc.containsKey(inv) && (pc.getProperty(inv) != null)) {
				if(first){
					write(PropertyEntry.STRONG_DIVIDE_LINE);
					first = false;
				}
				writeProperty(inv, pc.getString(inv));
			}
		}
	}

	private void writeOptions(Configuration pc) throws IOException {
		boolean first = true;
		if (pc.containsKey(PropertyEntry.aggregationcyclefreeness) && (pc.getProperty(PropertyEntry.aggregationcyclefreeness) != null)) {
			if(first){
				write(PropertyEntry.STRONG_DIVIDE_LINE);
				first = false;
			}
			writeProperty(PropertyEntry.aggregationcyclefreeness, pc.getString(PropertyEntry.aggregationcyclefreeness));
		}
		if (pc.containsKey(PropertyEntry.forbiddensharing) && (pc.getProperty(PropertyEntry.forbiddensharing) != null)) {
			if(first){
				write(PropertyEntry.STRONG_DIVIDE_LINE);
				first = false;
			}
			writeProperty(PropertyEntry.forbiddensharing, pc.getString(PropertyEntry.forbiddensharing));
		}
	}

	private String propertyToString(Object arrayList) {
		String string;
		if (!(arrayList instanceof String)) {
			string = arrayList.toString().trim();
			return string.substring(1, string.length()-1);
		} else {
			string = (String) arrayList;
			string = string.trim();
			return string;
		}
	}

	private void writeNewLine() throws IOException {
		writer.newLine();
	}

	private void write(String line) throws IOException {
		writer.write(line);
		writer.newLine();
	}

	private void writeProperty(String name, double value) throws IOException {
		writer.write(name + " = " + value);
		writer.newLine();
	}

	private void writeProperty(String name, int value) throws IOException {
		writer.write(name + " = " + value);
		writer.newLine();
	}

	private void writeProperty(String name, String value) throws IOException {
		writer.write(name + " = " + value);
		writer.newLine();
	}

	private void writeAssociationHeader(IAssociation association) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(association.name());
		sb.append(" (");
		StringUtil.fmtSeq(sb, association.associationEnds(), ", ", new StringUtil.IElementFormatter<IAssociationEnd>() {
			@Override
			public String format(IAssociationEnd element) {
				return element.name() + ":" + element.associatedClass().name();
			}
		});
		sb.append(")");

		writeLabeledLine(sb.toString());
	}

	private void writeLabeledLine(String string) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(PropertyEntry.COMMENT_LABEL);
		sb.append(string);
		if (sb.length() < PropertyEntry.PUNCHED_CARD_LENGTH) {
			if (sb.length() % 2 == 1) {
				sb.append(" ");
			}
			while (sb.length() < PropertyEntry.PUNCHED_CARD_LENGTH) {
				sb.append(" -");
			}
		}

		writer.write(sb.toString());
	}

}
