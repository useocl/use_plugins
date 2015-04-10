package org.tzi.use.kodkod.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.impl.AssociationClass;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClassImpl;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;

public class PropertiesWriter {
	
	private BufferedWriter writer;
	private MModel mModel;
	private IModel iModel;
	private boolean isDefaultConfiguration = false;

	public PropertiesWriter(MModel model){
		mModel = model;
	}
	
	public PropertiesWriter(IModel model){
		iModel = model;
	}
	
	/**
	 * Default configuration files have special handling for attributes.
	 */
	public void setIsDefaultConfiguration(boolean isDefault) {
		isDefaultConfiguration = isDefault;
	}
	
	public void writeToFile(File file, Map<String, PropertiesConfiguration> pCs) throws IOException {
		if(mModel == null){
			throw new IllegalArgumentException("MModel has to be provided for this method to work.");
		}
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
	
	public void writeToFile(File file, PropertiesConfiguration pc) throws IOException {
		if(iModel == null){
			throw new IllegalArgumentException("IModel has to be provided for this method to work.");
		}
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		
		writer = new BufferedWriter(new FileWriter(file));
		writeIModelSection("default", pc);
		writer.close();
	}
	
	private void writeSection(String section, PropertiesConfiguration pc) throws IOException {
		writeDivideLine("["+section+"]");
		writeNewLine();
		writeBasicTypes(pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		int i = 0;
		for (MClass clazz : mModel.classes()) {
			writeClass(clazz, pc);
			writeNewLine();
			writeAttributes(clazz.attributes().iterator(), pc);
			i++;
			
			List<MAssociation> classAssociations = new ArrayList<>();
			for (MAssociation association : mModel.associations()) {
				if (association.associationEnds().iterator().next().cls().equals(clazz)) {
					classAssociations.add(association);
				}
			}
			for (MAssociation association : classAssociations) {
				writeAssociation(association, pc);
			}
			if (!(i >= mModel.classes().size())) writeDivideLine(PropertyEntry.LIGHT_DIVIDE_LINE);
		}
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeInvariants(mModel.classInvariants().iterator(), pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeOptions(pc);
		writeNewLine();
		writeNewLine();
	}
	
	private void writeIModelSection(String section, PropertiesConfiguration pc) throws IOException {
		writeDivideLine("["+section+"]");
		writeNewLine();
		writeBasicTypes(pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		int i = 0;
		for (IClass clazz : iModel.classes()) {
			writeIClass(clazz, pc);
			writeNewLine();
			writeIAttributes(clazz.attributes().iterator(), pc);
			i++;
			
			List<IAssociation> classAssociations = new ArrayList<>();
			for (IAssociation association : iModel.associations()) {
				if (association.associationEnds().iterator().next().associatedClass().equals(clazz)) {
					classAssociations.add(association);
				}
			}
			for (IAssociation association : classAssociations) {
				writeIAssociation(association, pc);
			}
			if (!(i >= iModel.classes().size())) writeDivideLine(PropertyEntry.LIGHT_DIVIDE_LINE);
		}
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeIInvariants(iModel.classInvariants().iterator(), pc);
		writeDivideLine(PropertyEntry.STRONG_DIVIDE_LINE);
		writeOptions(pc);
		writeNewLine();
		writeNewLine();
	}
	
	private void writeBasicTypes(PropertiesConfiguration pc) throws IOException {
		String real = TypeConstants.REAL;
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
		
		String string = TypeConstants.STRING;
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
		
		String integer = TypeConstants.INTEGER;
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
	
	private void writeClass(MClass clazz, PropertiesConfiguration pc) throws IOException {
		String cls = clazz.name();
		String clsMin = cls + PropertyEntry.objMin;
		String clsMax = cls + PropertyEntry.objMax;
		if (!clazz.isAbstract()) {
			if ((clazz instanceof MAssociationClassImpl) && pc.containsKey(cls+PropertyEntry.ASSOCIATIONCLASS) 
					&& pc.getProperty(cls+PropertyEntry.ASSOCIATIONCLASS) != null) {
				write(cls+PropertyEntry.ASSOCIATIONCLASS, propertyToString(pc.getProperty(cls+PropertyEntry.ASSOCIATIONCLASS)));
			} else if (!(clazz instanceof MAssociationClassImpl)) {
				if (pc.containsKey(cls) && pc.getProperty(cls) != null) {
					write(cls, propertyToString(pc.getProperty(cls)));
				}
				if (pc.containsKey(clsMin) && pc.getProperty(clsMin) != null) {
					write(clsMin, pc.getInt(clsMin,DefaultConfigurationValues.objectsPerClassMin));
				}
				
				if (!(clazz instanceof MAssociationClassImpl) && pc.getProperty(clsMax) != null) {
					write(clsMax, pc.getInt(clsMax,DefaultConfigurationValues.objectsPerClassMax));
				}
			}
		}
	}
	
	private void writeIClass(IClass clazz, PropertiesConfiguration pc) throws IOException {
				String cls = clazz.name();
				String clsMin = cls + PropertyEntry.objMin;
				String clsMax = cls + PropertyEntry.objMax;
				if (!clazz.isAbstract()) {
					if ((clazz instanceof AssociationClass) && pc.containsKey(cls+PropertyEntry.ASSOCIATIONCLASS) 
							&& pc.getProperty(cls+PropertyEntry.ASSOCIATIONCLASS) != null) {
						write(cls+PropertyEntry.ASSOCIATIONCLASS, propertyToString(pc.getProperty(cls+PropertyEntry.ASSOCIATIONCLASS)));
					} else if (!(clazz instanceof AssociationClass)) {
						if (pc.containsKey(cls) && pc.getProperty(cls) != null) {
							write(cls, propertyToString(pc.getProperty(cls)));
						}
						if (pc.containsKey(clsMin) && pc.getProperty(clsMin) != null) {
							write(clsMin, pc.getInt(clsMin,DefaultConfigurationValues.objectsPerClassMin));
						}
						
						if (!(clazz instanceof AssociationClass) && pc.getProperty(clsMax) != null) {
							write(clsMax, pc.getInt(clsMax,DefaultConfigurationValues.objectsPerClassMax));
						}
					}
				}
	}
	
	private void writeAttributes(Iterator<MAttribute> iterator, PropertiesConfiguration pc) throws IOException {
		while (iterator.hasNext()) {
			MAttribute attribute = iterator.next();
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
	}
	
	private void writeIAttributes(Iterator<IAttribute> iterator, PropertiesConfiguration pc) throws IOException {
		while (iterator.hasNext()) {
			IAttribute attribute = iterator.next();
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
	}
	
	private void writeAssociation(MAssociation givenAssociation, PropertiesConfiguration pc) throws IOException {
			MAssociation association = givenAssociation;
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
	
	private void writeIAssociation(IAssociation givenAssociation, PropertiesConfiguration pc) throws IOException {
			IAssociation association = givenAssociation;
			String assoc = association.name();
			String assocMin = assoc+PropertyEntry.linksMin;
			String assocMax = assoc+PropertyEntry.linksMax;
			writeIAssociationLine(association);
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
	
	private void writeInvariants(Iterator<MClassInvariant> iterator, PropertiesConfiguration pc) throws IOException {
		while (iterator.hasNext()) {
			MClassInvariant invariant = iterator.next();
			
			String inv = invariant.cls().name()+"_"+invariant.name();
			if (pc.containsKey(inv) && (pc.getProperty(inv) != null)) {
				write(inv, pc.getString(inv));
			}
		}
	}
	
	private void writeIInvariants(Iterator<IInvariant> iterator, PropertiesConfiguration pc) throws IOException {
		while (iterator.hasNext()) {
			IInvariant invariant = iterator.next();
			
			String inv = invariant.clazz().name()+"_"+invariant.name();
			if (pc.containsKey(inv) && (pc.getProperty(inv) != null)) {
				write(inv, pc.getString(inv));
			}
		}
	}
	
	private void writeOptions(PropertiesConfiguration pc) throws IOException {
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
	
	private void writeAssociationLine(MAssociation association) throws IOException {
		StringBuilder associationString = new StringBuilder();
		Iterator<MAssociationEnd> aes = association.associationEnds().iterator();
		associationString.append(association.name());
		associationString.append("(");
		while (aes.hasNext()) {
			MAssociationEnd ae = aes.next();
			associationString.append(ae.name() + ":" + ae.cls().name());
			if (aes.hasNext()) {
				associationString.append(",");
			}
		}
		associationString.append(")");
		
		writeLabeledLine(associationString.toString());
	}
	
	private void writeIAssociationLine(IAssociation association) throws IOException {
		StringBuilder associationString = new StringBuilder();
		Iterator<IAssociationEnd> aes = association.associationEnds().iterator();
		associationString.append(association.name());
		associationString.append("(");
		while (aes.hasNext()) {
			IAssociationEnd ae = aes.next();
			associationString.append(ae.name() + ":" + ae.associatedClass().name());
			if (aes.hasNext()) {
				associationString.append(",");
			}
		}
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
