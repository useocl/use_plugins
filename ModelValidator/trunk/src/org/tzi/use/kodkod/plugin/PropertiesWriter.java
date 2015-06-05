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

//TODO use PropertyEntry.COMMENT_LABEL
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
		writeSection("default", pc);
		writer.close();
	}

	private void writeSection(String section, Configuration pc) throws IOException {
		writeDivideLine("["+section+"]");
		writeNewLine();
		writeBasicTypes(pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
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
				writeDivideLine(PropertyEntry.LIGHT_DIVIDE_LINE);
			}
		}
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeInvariants(iModel.classInvariants(), pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeOptions(pc);
		writeNewLine();
	}

	private void writeBasicTypes(Configuration pc) throws IOException {
		String integer = TypeConstants.INTEGER;
		String integerMin = integer + PropertyEntry.integerValueMin;
		String integerMax = integer + PropertyEntry.integerValueMax;
		if (pc.containsKey(integer) && pc.getProperty(integer) != null) {
			write(integer, propertyToString(pc.getProperty(integer)));
		}
		if (pc.containsKey(integerMin)) {
			write(integerMin, pc.getInt(integerMin, DefaultConfigurationValues.integerMin));
		}
		if (pc.containsKey(integerMax)) {
			write(integerMax, pc.getInt(integerMax, DefaultConfigurationValues.integerMax));
		}
		
		String string = TypeConstants.STRING;
		String stringMin = string + PropertyEntry.stringValuesMin;
		String stringMax = string + PropertyEntry.stringValuesMax;
		if (pc.containsKey(string) && pc.getProperty(string) != null) {
			write(string, propertyToString(pc.getProperty(string)));
		}
		if (pc.containsKey(stringMin)) {
			write(stringMin, pc.getInt(stringMin, DefaultConfigurationValues.stringMin));
		}
		if (pc.containsKey(stringMax)) {
			write(stringMax, pc.getInt(stringMax, DefaultConfigurationValues.stringMax));
		}

		String real = TypeConstants.REAL;
		String realMin = real + PropertyEntry.realValueMin;
		String realMax = real + PropertyEntry.realValueMax;
		String realStep = real + PropertyEntry.realStep;
		if (pc.containsKey(real) && pc.getProperty(real) != null) {
			write(real, propertyToString(pc.getProperty(real)));
		}
		if (pc.containsKey(realMin)) {
			write(realMin, pc.getDouble(realMin, DefaultConfigurationValues.realMin));
		}
		if (pc.containsKey(realMax)) {
			write(realMax, pc.getDouble(realMax, DefaultConfigurationValues.realMax));
		}
		if (pc.containsKey(realStep)) {
			write(realStep, pc.getDouble(realStep, DefaultConfigurationValues.realStep));
		}
	}

	private void writeClass(IClass clazz, Configuration pc) throws IOException {
		String cls = clazz.name();
		String clsMin = cls + PropertyEntry.objMin;
		String clsMax = cls + PropertyEntry.objMax;
		if (!clazz.isAbstract()) {
			if ((clazz instanceof IAssociationClass) && pc.containsKey(cls + PropertyEntry.ASSOCIATIONCLASS)
					&& pc.getProperty(cls + PropertyEntry.ASSOCIATIONCLASS) != null) {
				write(cls+PropertyEntry.ASSOCIATIONCLASS, propertyToString(pc.getProperty(cls+PropertyEntry.ASSOCIATIONCLASS)));
			} else if (!(clazz instanceof IAssociationClass)) {
				if (pc.containsKey(cls) && pc.getProperty(cls) != null) {
					write(cls, propertyToString(pc.getProperty(cls)));
				}
				if (pc.containsKey(clsMin) && pc.getProperty(clsMin) != null) {
					write(clsMin, pc.getInt(clsMin, DefaultConfigurationValues.objectsPerClassMin));
				}

				if (!(clazz instanceof IAssociationClass) && pc.getProperty(clsMax) != null) {
					write(clsMax, pc.getInt(clsMax, DefaultConfigurationValues.objectsPerClassMax));
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
				writer.write("-- " + attr + " = Set{ ... }");
				writeNewLine();
			} else {
				if (pc.containsKey(attr) && pc.getProperty(attr) != null) {
					write(attr, propertyToString(pc.getProperty(attr)));
				}
				if (pc.containsKey(attrMin)) {
					write(attrMin, pc.getInt(attrMin, DefaultConfigurationValues.attributesPerClassMin));
				}
				if (pc.containsKey(attrMax)) {
					write(attrMax, pc.getInt(attrMax, DefaultConfigurationValues.attributesPerClassMax));
				}
				if (pc.containsKey(attrMinSize)) {
					write(attrMinSize, pc.getInt(attrMinSize, DefaultConfigurationValues.attributesColSizeMin));
				}
				if (pc.containsKey(attrMaxSize)) {
					write(attrMaxSize, pc.getInt(attrMaxSize, DefaultConfigurationValues.attributesColSizeMax));
				}
			}
		}
	}

	private void writeAssociation(IAssociation association, Configuration pc) throws IOException {
		String assoc = association.name();
		String assocMin = assoc+PropertyEntry.linksMin;
		String assocMax = assoc+PropertyEntry.linksMax;
		writeAssociationLine(association);
		writeNewLine();
		if ((pc.containsKey(assoc) && (pc.getProperty(assoc) != null))) {
			write(assoc, propertyToString(pc.getProperty(assoc)));
		}
		if (pc.containsKey(assocMin) && (pc.getProperty(assocMin) != null)) {
			write(assocMin, pc.getInt(assocMin, DefaultConfigurationValues.linksPerAssocMin));
		}
		if (pc.containsKey(assocMax) && (pc.getProperty(assocMax) != null)) {
			write(assocMax, pc.getInt(assocMax, DefaultConfigurationValues.linksPerAssocMax));
		}
	}

	private void writeInvariants(Collection<IInvariant> invariants, Configuration pc) throws IOException {
		for(IInvariant invariant : invariants){
			String inv = invariant.clazz().name()+"_"+invariant.name();
			if (pc.containsKey(inv) && (pc.getProperty(inv) != null)) {
				write(inv, pc.getString(inv));
			}
		}
	}

	private void writeOptions(Configuration pc) throws IOException {
		if (pc.containsKey(PropertyEntry.aggregationcyclefreeness) && (pc.getProperty(PropertyEntry.aggregationcyclefreeness) != null)) {
			write(PropertyEntry.aggregationcyclefreeness, pc.getString(PropertyEntry.aggregationcyclefreeness));
		}
		if (pc.containsKey(PropertyEntry.forbiddensharing) && (pc.getProperty(PropertyEntry.forbiddensharing) != null)) {
			write(PropertyEntry.forbiddensharing, pc.getString(PropertyEntry.forbiddensharing));
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

	private void writeDivideLine(String line) throws IOException {
		writer.write(line);
		writer.newLine();
	}

	private void write(String name, double value) throws IOException {
		writer.write(name + " = " + value);
		writer.newLine();
	}

	private void write(String name, int value) throws IOException {
		writer.write(name + " = " + value);
		writer.newLine();
	}

	private void write(String name, String value) throws IOException {
		writer.write(name + " = " + value);
		writer.newLine();
	}

	private void writeAssociationLine(IAssociation association) throws IOException {
		StringBuilder associationString = new StringBuilder();
		associationString.append(association.name());
		associationString.append("(");
		StringUtil.fmtSeq(associationString, association.associationEnds(), ",", new StringUtil.IElementFormatter<IAssociationEnd>() {
			@Override
			public String format(IAssociationEnd element) {
				return element.name() + ":" + element.associatedClass().name();
			}
		});
		associationString.append(")");

		writeLabeledLine(associationString.toString());
	}

	private void writeLabeledLine(String string) throws IOException {
		StringBuilder writerString = new StringBuilder();
		writerString.append("-- ");
		writerString.append(string);
		if (writerString.length() < PropertyEntry.PUNCHED_CARD_LENGTH) {
			if (writerString.length() % 2 == 1) {
				writerString.append(" ");
			}
			while (writerString.length() < PropertyEntry.PUNCHED_CARD_LENGTH) {
				writerString.append(" -");
			}
		}

		writer.write(writerString.toString());
	}

}
