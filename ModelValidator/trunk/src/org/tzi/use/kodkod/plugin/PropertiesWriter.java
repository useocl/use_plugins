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
		for (IClass clazz : iModel.classes()) {
			writeClass(clazz, pc);
			writeAttributes(clazz.attributes(), pc);

			List<IAssociation> classAssociations = new ArrayList<>();
			for (IAssociation association : iModel.associations()) {
				if (association.associationEnds().get(0).associatedClass().equals(clazz)) {
					classAssociations.add(association);
				}
			}
			for (IAssociation association : classAssociations) {
				writeAssociation(association, pc);
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
		if(isDefaultConfiguration){
			write(PropertyEntry.COMMENT_LABEL + TypeConstants.INTEGER + " = Set{ ... }");
		}

		if(needSeperator){
			writeNewLine();
			needSeperator = false;
		}
		
		String string = TypeConstants.STRING;
//		String stringMin = string + PropertyEntry.stringValuesMin;
		String stringMax = string + PropertyEntry.stringValuesMax;
		if (pc.containsKey(string) && pc.getProperty(string) != null) {
			writeProperty(string, propertyToString(pc.getProperty(string)));
			needSeperator = true;
		}
//		if (pc.containsKey(stringMin)) {
//			writeProperty(stringMin, pc.getInt(stringMin, DefaultConfigurationValues.stringMin));
//			needSeperator = true;
//		}
		if (pc.containsKey(stringMax)) {
			writeProperty(stringMax, pc.getInt(stringMax, DefaultConfigurationValues.stringMax));
			needSeperator = true;
		}
		if(isDefaultConfiguration){
			write(PropertyEntry.COMMENT_LABEL + TypeConstants.STRING + " = Set{ ... }");
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
		writeLabeledLine(' ' + cls, "-", false);
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
		if(attributes.size() > 0){
			writeNewLine();
			for(IAttribute attribute : attributes){
				String attr = attribute.owner().name() + "_" + attribute.name();
				String attrMin = attr + PropertyEntry.attributeDefValuesMin;
				String attrMax = attr + PropertyEntry.attributeDefValuesMax;
				String attrMinSize = attr + PropertyEntry.attributeColSizeMin;
				String attrMaxSize = attr + PropertyEntry.attributeColSizeMax;
				
				if(isDefaultConfiguration){
					write(PropertyEntry.COMMENT_LABEL + attr + " = Set{ ... }");
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
	}

	private void writeAssociation(IAssociation association, Configuration pc) throws IOException {
		String assoc = association.name();
		String assocMin = assoc+PropertyEntry.linksMin;
		String assocMax = assoc+PropertyEntry.linksMax;
		writeNewLine();
		writeAssociationHeader(association);
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
					writeLabeledLine("", "-", true);
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
				writeLabeledLine("", "-", true);
				first = false;
			}
			writeProperty(PropertyEntry.aggregationcyclefreeness, pc.getString(PropertyEntry.aggregationcyclefreeness));
		}
		if (pc.containsKey(PropertyEntry.forbiddensharing) && (pc.getProperty(PropertyEntry.forbiddensharing) != null)) {
			if(first){
				writeLabeledLine("", "-", true);
				first = false;
			}
			writeProperty(PropertyEntry.forbiddensharing, pc.getString(PropertyEntry.forbiddensharing));
		}
	}

	private String propertyToString(Object arg) {
		if(arg instanceof Collection<?>){
			return StringUtil.fmtSeq((Collection<?>) arg, ", ");
		} else {
			return arg.toString().trim();
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
		write(name + " = " + value);
	}

	private void writeProperty(String name, int value) throws IOException {
		write(name + " = " + value);
	}

	private void writeProperty(String name, String value) throws IOException {
		write(name + " = " + value);
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

		writeLabeledLine(sb.toString(), " -", true);
	}

	private void writeLabeledLine(String label, String lineChars, boolean prepend) throws IOException {
		if(lineChars.isEmpty()){
			throw new IllegalArgumentException("Line characters may not be empty (infinite loop)");
		}
		StringBuilder sb = new StringBuilder();
		final int reservedLength = label.length() + PropertyEntry.COMMENT_LABEL.length();
		
		sb.append(PropertyEntry.COMMENT_LABEL);
		if(prepend){
			sb.append(label);
		}
		
		int charsToFill = PropertyEntry.LINE_WIDTH - reservedLength;
		if(prepend){
			int padding = charsToFill % lineChars.length();
			for(int i = 0; i < padding; i++){
				sb.append(' ');
				charsToFill--;
			}
		}
		
		while(charsToFill > 0){
			if(charsToFill >= lineChars.length()){
				sb.append(lineChars);
				charsToFill -= lineChars.length();
			} else {
				sb.append(' ');
				charsToFill--;
			}
		}
		
		if(!prepend){
			sb.append(label);
		}
		
		write(sb.toString());
	}

}
