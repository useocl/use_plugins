package org.tzi.use.kodkod.plugin.gui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsAssociation;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsAttribute;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsClass;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsInteger;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsInvariant;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsOption;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsReal;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsString;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClassImpl;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MModel;

final public class ConfigurationChange {
	
	private ConfigurationChange(){
	}
	
	public static PropertiesConfiguration toProperties(SettingsConfiguration settings, MModel model) {
		//TODO extract all data from ConfigurationSettings object and put them into PropertiesConfiguration object
		//and return it
		//Do it with help of:
		//the values of getValues() are prepared for adding easily through iteration into a PropertiesConfiguration object, like:
		//if (values.hasNext()) propertiesConfiguration.setProperty(key, values.next());
		//while (values.hasNext()) {
		//  propertiesConfiguration.addProperty(key, values.next());
		//}
		
		//TODO NICHT VERGESSEN: Die Namen muessen in PropertiesConfiguration gerechter Form zurueck
		//gewechselt werden: Klasse::invariante -> Klasse_invariante
		//mit: string.replaceFirst("::", "_");
		PropertiesConfiguration pc = new PropertiesConfiguration();
		SettingsInteger integerSettings = settings.getIntegerTypeSettings();
		SettingsReal realSettings = settings.getRealTypeSettings();
		SettingsString stringSettings = settings.getStringTypeSettings();
		SettingsOption optionsSettings = settings.getOptionSettings();
		List<SettingsClass> classesSettings = settings.getAllClassesSettings();
		List<SettingsInvariant> invariantsSettings = settings.getAllInvariantsSettings();
		
		pc.setProperty(integerSettings.name()+PropertyEntry.integerValueMin, integerSettings.getBounds().getLower());
		pc.setProperty(integerSettings.name()+PropertyEntry.integerValueMax, integerSettings.getBounds().getUpper());
		if (!integerSettings.getValuesForProperties().isEmpty()) {
			Iterator<String> integerValues = integerSettings.getValuesForProperties().iterator();
			if (integerValues.hasNext()) {
				pc.setProperty(integerSettings.name(), integerValues.next().toString());
			}
			while (integerValues.hasNext()) {
				pc.addProperty(integerSettings.name(), integerValues.next().toString());
			}
		}
		
		pc.setProperty(realSettings.name()+PropertyEntry.realValueMin,  realSettings.getBounds().getLower());
		pc.setProperty(realSettings.name()+PropertyEntry.realValueMax,  realSettings.getBounds().getUpper());
		pc.setProperty(realSettings.name()+PropertyEntry.realStep,  realSettings.getStep());
		if (!realSettings.getValuesForProperties().isEmpty()) {
			Iterator<String> realValues = realSettings.getValuesForProperties().iterator();
			if (realValues.hasNext()) {
				pc.setProperty(realSettings.name(), realValues.next().toString());
			}
			while (realValues.hasNext()) {
				pc.addProperty(realSettings.name(), realValues.next().toString());
			}
		}
		
		pc.setProperty(stringSettings.name()+PropertyEntry.stringValuesMin, stringSettings.getBounds().getLower());
		pc.setProperty(stringSettings.name()+PropertyEntry.stringValuesMax, stringSettings.getBounds().getUpper());
		if (!stringSettings.getValuesForProperties().isEmpty()) {
			Iterator<String> stringValues = stringSettings.getValuesForProperties().iterator();
			if (stringValues.hasNext()) {
				pc.setProperty(stringSettings.name(), stringValues.next().toString());
			}
			while (stringValues.hasNext()) {
				pc.addProperty(stringSettings.name(), stringValues.next().toString());
			}
		}
		
		if (optionsSettings.getAggregationcyclefreeness()) {
			pc.setProperty(PropertyEntry.aggregationcyclefreeness, "on");
		} else {
			pc.setProperty(PropertyEntry.aggregationcyclefreeness, "off");
		}
		if (optionsSettings.getForbiddensharing()) {
			pc.setProperty(PropertyEntry.forbiddensharing, "on");
		} else {
			pc.setProperty(PropertyEntry.forbiddensharing, "off");
		}
		
		Iterator<SettingsClass> classesIterator = classesSettings.iterator();
		while (classesIterator.hasNext()) {
			SettingsClass classSettings = classesIterator.next();
			if (!classSettings.getIsAssociationClass()) {
				pc.setProperty(classSettings.getCls().name()+PropertyEntry.objMin, classSettings.getBounds().getLower());
				pc.setProperty(classSettings.getCls().name()+PropertyEntry.objMax, classSettings.getBounds().getUpper());
				if (!classSettings.getValuesForProperties().isEmpty()) {
					Iterator<String> classValues = classSettings.getValuesForProperties().iterator();
					if (classValues.hasNext()) {
						pc.setProperty(classSettings.getCls().name(), classValues.next().toString());
					}
					while (classValues.hasNext()) {
						pc.addProperty(classSettings.getCls().name(), classValues.next().toString());
					}
				}
			} else {
				if (!classSettings.getValuesForProperties().isEmpty()) {
					Iterator<String> classValues = classSettings.getValuesForProperties().iterator();
					if (classValues.hasNext()) {
						pc.setProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS, classValues.next().toString());
					}
					while (classValues.hasNext()) {
						pc.addProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS, classValues.next().toString());
					}
				}
			}
			Iterator<SettingsAttribute> attributesIterator = classSettings.getAttributeSettings().values().iterator();
			while (attributesIterator.hasNext()) {
				SettingsAttribute attributeSettings = attributesIterator.next();
				String attribute = attributeSettings.getAttribute().owner().name()+"_"+attributeSettings.getAttribute().name();
				pc.setProperty(attribute+PropertyEntry.attributeDefValuesMin, attributeSettings.getBounds().getLower());
				pc.setProperty(attribute+PropertyEntry.attributeDefValuesMax, attributeSettings.getBounds().getUpper());
				pc.setProperty(attribute+PropertyEntry.attributeColSizeMin, attributeSettings.getCollectionSize().getLower());
				pc.setProperty(attribute+PropertyEntry.attributeColSizeMax, attributeSettings.getCollectionSize().getUpper());
				if (!attributeSettings.getValuesForProperties().isEmpty()) {
					Iterator<String> attributeValues = attributeSettings.getValuesForProperties().iterator();
					if (attributeValues.hasNext()) {
						pc.setProperty(attribute, attributeValues.next().toString());
					}
					while (attributeValues.hasNext()) {
						pc.addProperty(attribute, attributeValues.next().toString());
					}
				}
			}
			Iterator<SettingsAssociation> associationsIterator = classSettings.getAssociationSettings().values().iterator();
			while (associationsIterator.hasNext()) {
				SettingsAssociation associationSettings = associationsIterator.next();
				String association = associationSettings.getAssociation().name();
				pc.setProperty(association+PropertyEntry.linksMin, associationSettings.getBounds().getLower());
				pc.setProperty(association+PropertyEntry.linksMax, associationSettings.getBounds().getUpper());
				if (!associationSettings.getValuesForProperties().isEmpty()) {
					Iterator<String> associationValues = associationSettings.getValuesForProperties().iterator();
					if (associationValues.hasNext()) {
						pc.setProperty(association, associationValues.next().toString());
					}
					while (associationValues.hasNext()) {
						pc.addProperty(association, associationValues.next().toString());
					}
				}
			}
		}
		
		Iterator<SettingsInvariant> invariantsIterator = invariantsSettings.iterator();
		while (invariantsIterator.hasNext()) {
			SettingsInvariant invariantSettings = invariantsIterator.next();
			String set = "inactive";
			if (invariantSettings.getActive() && invariantSettings.getNegate()){
				set = "negate";
			} else if (invariantSettings.getActive() && !invariantSettings.getNegate()) {
				set = "active";
			}
			pc.setProperty(invariantSettings.getInvariant().cls().name()+"_"+invariantSettings.getInvariant().name(), set);
		}
		
		return pc;
	}
	
	public static SettingsConfiguration toSettings(MModel model, PropertiesConfiguration pc, SettingsConfiguration givenSettings) {
		
		SettingsConfiguration settings = givenSettings;
		List<String> classes = new ArrayList<>();
		List<String> attributes = new ArrayList<>();
		List<String> associations = new ArrayList<>();
		List<String> invariants = new ArrayList<>();
		
		//from this point on, the possible keys which could appear in the PropertiesConfiguration are collected
		//from the model
		Iterator<?> classesIterator = model.classes().iterator();
		while (classesIterator.hasNext()) {
			MClass clazz = (MClass) classesIterator.next();
			String cls = clazz.name();
			if (model.associations().contains(clazz)) {
				classes.add(cls+"_ac");
			} else {
				classes.add(cls);
				classes.add(cls+PropertyEntry.objMin);
				classes.add(cls+PropertyEntry.objMax);
			}

			Iterator<MAttribute> attributesIterator = clazz.allAttributes().iterator();
			while (attributesIterator.hasNext()) {
				MAttribute attribute = attributesIterator.next();
				String attr = attribute.owner().name()+"::"+attribute.name();
				attributes.add(attr);
				attributes.add(attr+PropertyEntry.attributeDefValuesMin);
				attributes.add(attr+PropertyEntry.attributeDefValuesMax);
				attributes.add(attr+PropertyEntry.attributeColSizeMin);
				attributes.add(attr+PropertyEntry.attributeColSizeMax);
			}
		}
		
		Iterator<MAssociation> associationsIterator = model.associations().iterator();
		while (associationsIterator.hasNext()) {
			MAssociation association = associationsIterator.next();
			String assoc = association.associationEnds().iterator().next().cls().name()+"::"+association.name();
			if (association instanceof MAssociationClassImpl) {
				assoc = association.name()+"::"+association.name();
			}
			associations.add(assoc);
			associations.add(assoc+PropertyEntry.linksMin);
			associations.add(assoc+PropertyEntry.linksMax);
		}
		
		Iterator<MClassInvariant> invariantsIterator = model.classInvariants().iterator();
		while (invariantsIterator.hasNext()) {
			MClassInvariant invariant = invariantsIterator.next();
			//for a better visual presentation, the class name and invariant name are
			//seperated with two colons, this will be changed back to an underline, 
			//when the settings are converted back as PropertiesConfiguration object
			//with the method toProperties() in this class.
			invariants.add(invariant.cls().name()+"::"+invariant.name());
		}
		
		//from this point on the keys from the given PropertiesConfiguration are compared with the
		//possible keys collected before 
		Iterator<?> keys = pc.getKeys();
		while (keys.hasNext()) {
			String propertiesKey = keys.next().toString();
			String first = "";
			String second = "";
			String third = "";
			HashSet<String> classNames = new HashSet<>();
			HashSet<String> assocNames = new HashSet<>();
			Iterator<MClass> classIterator = model.classes().iterator();
			Iterator<MAssociation> assocIterator = model.associations().iterator();
			while (classIterator.hasNext()) {
				classNames.add(classIterator.next().name());
			}
			while (assocIterator.hasNext()) {
				assocNames.add(assocIterator.next().name());
			}
			
			if (classNames.contains(propertiesKey) || assocNames.contains(propertiesKey)
					|| propertiesKey.equals(PropertyEntry.aggregationcyclefreeness) 
					|| propertiesKey.equals(PropertyEntry.forbiddensharing)
					|| propertiesKey.equals(TypeConstants.INTEGER) || propertiesKey.equals(TypeConstants.REAL) 
					|| propertiesKey.equals(TypeConstants.STRING) || propertiesKey.equals(TypeConstants.BOOLEAN)
					|| propertiesKey.endsWith("_ac")) {
				first = propertiesKey;
			} else {
				Iterator<String> allEndings = PropertyEntry.allEndings.iterator();
				while (allEndings.hasNext()) {
					String currentEnding = allEndings.next();
					if (propertiesKey.endsWith(currentEnding)) {
						third = currentEnding;
					}
				}
				if (third.equals("")) {
					second = propertiesKey.substring(propertiesKey.lastIndexOf("_")+1, propertiesKey.length());
					first = propertiesKey.substring(0, propertiesKey.indexOf("_"+second));
				} else {
					second = propertiesKey.substring(0, propertiesKey.lastIndexOf(third));
					if (classNames.contains(second) || assocNames.contains(second) 
							|| second.equals(TypeConstants.INTEGER) || second.equals(TypeConstants.REAL) 
							|| second.equals(TypeConstants.STRING) || second.equals(TypeConstants.BOOLEAN)) {
						first = second;
						second = "";
					} else {
						second = second.substring(second.lastIndexOf("_")+1, second.length());
						first = propertiesKey.substring(0, propertiesKey.indexOf("_"+second+third));
					}
				}
			}
			
			String switchKey = first+"::"+second+third;
			if (second.equals("")) {
				switchKey = first+third;
			}
			if (assocNames.contains(first) && (second.equals(""))) {
				second = first;
				MAssociation association = model.getAssociation(second);
				if (association instanceof MAssociationClassImpl) {
					first = association.name();
				} else {
					first = association.associationEnds().iterator().next().cls().name();
				}
				switchKey = first+"::"+second+third;
			}
			
			if (classes.contains(propertiesKey)) {
				if (propertiesKey.endsWith(PropertyEntry.objMin)) {
					String clazz = propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.objMin));
					settings.getClassSettings(clazz).getBounds().setLower(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.objMax)) {
					String clazz = propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.objMax));
					settings.getClassSettings(clazz).getBounds().setUpper(pc.getInt(propertiesKey));
				} else {
					if (pc.getProperty(propertiesKey) != null) {
						if (propertiesKey.endsWith("_ac")) {
							settings.getClassSettings(propertiesKey.substring(0, propertiesKey.indexOf("_ac")))
								.setValues(StringChange.prepareForTable(pc.getProperty(propertiesKey)));
						} else {
							settings.getClassSettings(propertiesKey)
								.setValues(StringChange.prepareForTable(pc.getProperty(propertiesKey)));
						}
					}
				}
			} else if (attributes.contains(switchKey)) {
				if (propertiesKey.endsWith(PropertyEntry.attributeDefValuesMin)) {
					String clazz = first;
					String attr = second;
					MAttribute attribute = model.getClass(clazz).attribute(attr, true);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).getBounds().setLower(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.attributeDefValuesMax)) {
					String clazz = first;
					String attr = second;
					MAttribute attribute = model.getClass(clazz).attribute(attr, true);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).getBounds().setUpper(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.attributeColSizeMin)) {
					String clazz = first;
					String attr = second;
					MAttribute attribute = model.getClass(clazz).attribute(attr, true);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).getCollectionSize().setLower(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.attributeColSizeMax)) {
					String clazz = first;
					String attr = second;
					MAttribute attribute = model.getClass(clazz).attribute(attr, true);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).getCollectionSize().setUpper(pc.getInt(propertiesKey));
				} else {
					if (pc.getProperty(propertiesKey) != null) {
						String clazz = first;
						String attr = second;
						MAttribute attribute = model.getClass(clazz).attribute(attr, true);
						settings.getClassSettings(clazz).getAttributeSettings().get(attribute)
							.setValues(StringChange.prepareForTable(pc.getProperty(propertiesKey)));
					}
				}
			} else if (associations.contains(switchKey)) {
				if (propertiesKey.endsWith(PropertyEntry.linksMin)) {
					String clazz = first;
					String assoc = second;
					MAssociation association = model.getAssociation(assoc);
					settings.getClassSettings(clazz).getAssociationSettings().get(association)
						.getBounds().setLower(pc.getInt(assoc+PropertyEntry.linksMin));
				} else if (propertiesKey.endsWith(PropertyEntry.linksMax)) {
					String clazz = first;
					String assoc = second;
					MAssociation association = model.getAssociation(assoc);
					settings.getClassSettings(clazz).getAssociationSettings().get(association)
						.getBounds().setUpper(pc.getInt(assoc+PropertyEntry.linksMax));
				} else {
					if (pc.getProperty(propertiesKey) != null) {
						String clazz = first;
						String assoc = second;
						MAssociation association = model.getAssociation(assoc);
						settings.getClassSettings(clazz).getAssociationSettings().get(association)
							.setValues(StringChange.prepareForTable(pc.getProperty(assoc)));
					}
				}
			} else if (invariants.contains(switchKey)) {
				String invKey = first+"_"+second;
				Boolean active = true;
				Boolean negate = false;
				if (pc.getString(invKey).equalsIgnoreCase("active")) {
					active = true;
					negate = false;
				} else if (pc.getString(invKey).equalsIgnoreCase("inactive")) {
					active = false;
					negate = false;
				} else if (pc.getString(invKey).equalsIgnoreCase("negate")) {
					active = true;
					negate = true;
				}
				invKey = first+"::"+second; 
				settings.getInvariantSetting(invKey).setActive(active);
				settings.getInvariantSetting(invKey).setNegate(negate);
			} else {
				switch (propertiesKey) {
				case PropertyEntry.aggregationcyclefreeness:
					settings.getOptionSettings().setAggregationcyclefreeness(pc.getBoolean(propertiesKey));
					break;
				case PropertyEntry.forbiddensharing:
					settings.getOptionSettings().setForbiddensharing(pc.getBoolean(propertiesKey));
					break;
				case TypeConstants.INTEGER:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getIntegerTypeSettings().setValues(StringChange.prepareForTable(pc.getProperty(propertiesKey)));
					}
					break;
				case TypeConstants.INTEGER + PropertyEntry.integerValueMin:
					settings.getIntegerTypeSettings().getBounds().setLower(pc.getInt(propertiesKey));
					break;
				case TypeConstants.INTEGER + PropertyEntry.integerValueMax:
					settings.getIntegerTypeSettings().getBounds().setUpper(pc.getInt(propertiesKey));
					break;
				case TypeConstants.REAL:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getRealTypeSettings().setValues(StringChange.prepareForTable(pc.getProperty(propertiesKey)));
					}
					break;
				case TypeConstants.REAL + PropertyEntry.realValueMin:
					settings.getRealTypeSettings().getBounds()
							.setLower(pc.getInt(propertiesKey));
					break;
				case TypeConstants.REAL + PropertyEntry.realValueMax:
					settings.getRealTypeSettings().getBounds()
							.setUpper(pc.getInt(propertiesKey));
					break;
				case TypeConstants.REAL + PropertyEntry.realStep:
					settings.getRealTypeSettings().setStep(pc.getDouble(propertiesKey));
					break;
				case TypeConstants.STRING:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getStringTypeSettings().setValues(StringChange.prepareForTable(pc.getProperty(propertiesKey)));
					}
					break;
				case TypeConstants.STRING + PropertyEntry.stringValuesMin:
					settings.getStringTypeSettings().getBounds()
							.setLower(pc.getInt(propertiesKey));
					break;
				case TypeConstants.STRING + PropertyEntry.stringValuesMax:
					settings.getStringTypeSettings().getBounds()
							.setUpper(pc.getInt(propertiesKey));
				}
			}
		}
		
		return settings;
	}

}
