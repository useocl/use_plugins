package org.tzi.use.kodkod.plugin.gui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
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

final public class ChangeConfiguration {
	
	private ChangeConfiguration(){
	}
	
	public static PropertiesConfiguration toProperties(final SettingsConfiguration settings, final MModel model) {
		PropertiesConfiguration pc = new PropertiesConfiguration();
		SettingsInteger integerSettings = settings.getIntegerTypeSettings();
		SettingsReal realSettings = settings.getRealTypeSettings();
		SettingsString stringSettings = settings.getStringTypeSettings();
		SettingsOption optionsSettings = settings.getOptionSettings();
		List<SettingsClass> classesSettings = settings.getAllClassesSettings();
		List<SettingsInvariant> invariantsSettings = settings.getAllInvariantsSettings();
		
		if (integerSettings.getMinimum() != null)
			pc.setProperty(integerSettings.name()+PropertyEntry.integerValueMin, integerSettings.getMinimum());
		if (integerSettings.getMaximum() != null)
			pc.setProperty(integerSettings.name()+PropertyEntry.integerValueMax, integerSettings.getMaximum());
		if (!integerSettings.getValues().isEmpty()) {
			Iterator<String> integerValues = ChangeString.formatSettingValuesForProperty(integerSettings.getValues()).iterator();
			if (integerValues.hasNext()) {
				pc.setProperty(integerSettings.name(), integerValues.next().toString());
			}
			while (integerValues.hasNext()) {
				pc.addProperty(integerSettings.name(), integerValues.next().toString());
			}
		} else {
			pc.clearProperty(integerSettings.name());
		}
		
		if (realSettings.getMinimum() != null)
			pc.setProperty(realSettings.name()+PropertyEntry.realValueMin,  realSettings.getMinimum());
		if (realSettings.getMaximum() != null)
			pc.setProperty(realSettings.name()+PropertyEntry.realValueMax,  realSettings.getMaximum());
		if (realSettings.getStep() != null)
			pc.setProperty(realSettings.name()+PropertyEntry.realStep,  realSettings.getStep());
		if (!realSettings.getValues().isEmpty()) {
			Iterator<String> realValues = ChangeString.formatSettingValuesForProperty(realSettings.getValues()).iterator();
			if (realValues.hasNext()) {
				pc.setProperty(realSettings.name(), realValues.next().toString());
			}
			while (realValues.hasNext()) {
				pc.addProperty(realSettings.name(), realValues.next().toString());
			}
		} else {
			pc.clearProperty(realSettings.name());
		}
		
		if (stringSettings.getBounds().getLower() != null)
			pc.setProperty(stringSettings.name()+PropertyEntry.stringValuesMin, stringSettings.getBounds().getLower());
		if (stringSettings.getBounds().getUpper() != null)
			pc.setProperty(stringSettings.name()+PropertyEntry.stringValuesMax, stringSettings.getBounds().getUpper());
		if (!stringSettings.getValues().isEmpty()) {
			Iterator<String> stringValues = ChangeString.formatSettingValuesForProperty(stringSettings.getValues()).iterator();
			if (stringValues.hasNext()) {
				pc.setProperty(stringSettings.name(), stringValues.next().toString());
			}
			while (stringValues.hasNext()) {
				pc.addProperty(stringSettings.name(), stringValues.next().toString());
			}
		} else {
			pc.clearProperty(stringSettings.name());
		}
		
		if (optionsSettings.getAggregationcyclefreeness() != null) {
			if (optionsSettings.getAggregationcyclefreeness()) {
				pc.setProperty(PropertyEntry.aggregationcyclefreeness, "on");
			} else {
				pc.setProperty(PropertyEntry.aggregationcyclefreeness, "off");
			}
		}
		if (optionsSettings.getForbiddensharing() != null) {
			if (optionsSettings.getForbiddensharing()) {
				pc.setProperty(PropertyEntry.forbiddensharing, "on");
			} else {
				pc.setProperty(PropertyEntry.forbiddensharing, "off");
			}
		}
		
		Iterator<SettingsClass> classesIterator = classesSettings.iterator();
		while (classesIterator.hasNext()) {
			SettingsClass classSettings = classesIterator.next();
			if (!classSettings.isAssociationClass()) {
				if (classSettings.getBounds().getLower() != null)
					pc.setProperty(classSettings.getCls().name()+PropertyEntry.objMin, classSettings.getBounds().getLower());
				if (classSettings.getBounds().getUpper() != null)
					pc.setProperty(classSettings.getCls().name()+PropertyEntry.objMax, classSettings.getBounds().getUpper());
				if (!classSettings.getValues().isEmpty()) {
					Iterator<String> classValues = ChangeString.formatSettingValuesForProperty(classSettings.getValues()).iterator();
					if (classValues.hasNext()) {
						pc.setProperty(classSettings.getCls().name(), classValues.next().toString());
					}
					while (classValues.hasNext()) {
						pc.addProperty(classSettings.getCls().name(), classValues.next().toString());
					}
				} else {
					pc.clearProperty(classSettings.getCls().name());
				}
			} else {
				if (!classSettings.getValues().isEmpty()) {
					Iterator<String> classValues = ChangeString.formatSettingValuesForProperty(classSettings.getValues()).iterator();
					if (classValues.hasNext()) {
						pc.setProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS, classValues.next().toString());
					}
					while (classValues.hasNext()) {
						pc.addProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS, classValues.next().toString());
					}
				} else {
					pc.clearProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS);
				}
			}
			Iterator<SettingsAttribute> attributesIterator = classSettings.getAttributeSettings().values().iterator();
			while (attributesIterator.hasNext()) {
				SettingsAttribute attributeSettings = attributesIterator.next();
				String attribute = attributeSettings.getAttribute().owner().name()+"_"+attributeSettings.getAttribute().name();
				if (attributeSettings.getBounds().getLower() != null)
					pc.setProperty(attribute+PropertyEntry.attributeDefValuesMin, attributeSettings.getBounds().getLower());
				if (attributeSettings.getBounds().getUpper() != null)
					pc.setProperty(attribute+PropertyEntry.attributeDefValuesMax, attributeSettings.getBounds().getUpper());
				if (attributeSettings.getCollectionSize().getLower() != null)
					pc.setProperty(attribute+PropertyEntry.attributeColSizeMin, attributeSettings.getCollectionSize().getLower());
				if (attributeSettings.getCollectionSize().getUpper() != null)
					pc.setProperty(attribute+PropertyEntry.attributeColSizeMax, attributeSettings.getCollectionSize().getUpper());
				if (!attributeSettings.getValues().isEmpty()) {
					Iterator<String> attributeValues = ChangeString.formatSettingValuesForProperty(attributeSettings.getValues()).iterator();
					if (attributeValues.hasNext()) {
						pc.setProperty(attribute, attributeValues.next().toString());
					}
					while (attributeValues.hasNext()) {
						pc.addProperty(attribute, attributeValues.next().toString());
					}
				} else {
					pc.clearProperty(attribute);
				}
			}
			Iterator<SettingsAssociation> associationsIterator = classSettings.getAssociationSettings().values().iterator();
			while (associationsIterator.hasNext()) {
				SettingsAssociation associationSettings = associationsIterator.next();
				String association = associationSettings.getAssociation().name();
				if (associationSettings.getBounds().getLower() != null)
					pc.setProperty(association+PropertyEntry.linksMin, associationSettings.getBounds().getLower());
				if (associationSettings.getBounds().getUpper() != null)
					pc.setProperty(association+PropertyEntry.linksMax, associationSettings.getBounds().getUpper());
				if (!associationSettings.getValues().isEmpty()) {
					Iterator<String> associationValues = ChangeString.formatSettingValuesForProperty(associationSettings.getValues()).iterator();
					if (associationValues.hasNext()) {
						pc.setProperty(association, associationValues.next().toString());
					}
					while (associationValues.hasNext()) {
						pc.addProperty(association, associationValues.next().toString());
					}
				} else {
					pc.clearProperty(association);
				}
			}
		}
		
		Iterator<SettingsInvariant> invariantsIterator = invariantsSettings.iterator();
		while (invariantsIterator.hasNext()) {
			SettingsInvariant invariantSettings = invariantsIterator.next();
			String set = PropertyEntry.INVARIANT_INACTIVE;
			if (invariantSettings.getActive() != null || invariantSettings.getNegate() != null) {
				if (invariantSettings.getActive() && invariantSettings.getNegate()){
					set = PropertyEntry.INVARIANT_NEGATE;
				} else if (invariantSettings.getActive() && !invariantSettings.getNegate()) {
					set = PropertyEntry.INVARIANT_ACTIVE;
				}
				pc.setProperty(invariantSettings.getInvariant().cls().name()+"_"+invariantSettings.getInvariant().name(), set);
			}
		}
		
		return pc;
	}
	
	public static void toSettings(MModel model, final PropertiesConfiguration pc, SettingsConfiguration givenSettings) {
		SettingsConfiguration settings = givenSettings;
		List<String> classes = new ArrayList<>();
		List<String> attributes = new ArrayList<>();
		List<String> associations = new ArrayList<>();
		List<String> invariants = new ArrayList<>();
		
		//from this point on, the possible keys which could appear in the PropertiesConfiguration are collected
		//from the model. Additionally, if there are no values set in the properties, the regarding setting will
		//be set to null.
		Iterator<?> classesIterator = model.classes().iterator();
		while (classesIterator.hasNext()) {
			MClass clazz = (MClass) classesIterator.next();
			String cls = clazz.name();
			if (model.associations().contains(clazz)) {
				classes.add(cls+PropertyEntry.ASSOCIATIONCLASS);
				if (!pc.containsKey(cls+PropertyEntry.ASSOCIATIONCLASS)) {
					settings.getClassSettings(cls).deleteValues();
				}
			} else {
				classes.add(cls);
				classes.add(cls+PropertyEntry.objMin);
				classes.add(cls+PropertyEntry.objMax);
				if (!pc.containsKey(cls))
					settings.getClassSettings(cls).deleteValues();
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
				if (!pc.containsKey(attribute.owner().name()+"_"+attribute.name())) {
					settings.getClassSettings(cls).getAttributeSettings().get(attribute).deleteValues();
				}
			}
		}
		
		Iterator<MAssociation> associationsIterator = model.associations().iterator();
		while (associationsIterator.hasNext()) {
			MAssociation association = associationsIterator.next();
			String cls = association.associationEnds().iterator().next().cls().name();
			String assoc = cls+"::"+association.name();
			if (association instanceof MAssociationClassImpl) {
				assoc = association.name()+"::"+association.name();
			}
			associations.add(assoc);
			associations.add(assoc+PropertyEntry.linksMin);
			associations.add(assoc+PropertyEntry.linksMax);
			if (!pc.containsKey(association.name())) {
				if (!(association instanceof MAssociationClassImpl)) {
					settings.getClassSettings(cls).getAssociationSettings().get(association).deleteValues();
				} else {
					settings.getClassSettings(association.name()).getAssociationSettings().get(association).deleteValues();
				}
			}
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
		
		if (!pc.containsKey(TypeConstants.INTEGER)) {
			settings.getIntegerTypeSettings().deleteValues();
		}
		if (!pc.containsKey(TypeConstants.REAL)) {
			settings.getRealTypeSettings().deleteValues();
		}
		if (!pc.containsKey(TypeConstants.STRING)) {
			settings.getStringTypeSettings().deleteValues();
		}
		
		//from this point on the keys from the given PropertiesConfiguration are compared with the
		//possible keys collected from the model, and than their values are added to the settings
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
					|| propertiesKey.endsWith(PropertyEntry.ASSOCIATIONCLASS)) {
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
						if (propertiesKey.endsWith(PropertyEntry.ASSOCIATIONCLASS)) {
							settings.getClassSettings(propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.ASSOCIATIONCLASS)))
								.setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
						} else {
							settings.getClassSettings(propertiesKey)
								.setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
						}
					} else {
						settings.getClassSettings(propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.ASSOCIATIONCLASS)))
							.setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
						settings.getClassSettings(propertiesKey)
							.setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
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
							.setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
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
							.setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(assoc)));
					}
				}
			} else if (invariants.contains(switchKey)) {
				String invKey = first+"_"+second;
				Boolean active = true;
				Boolean negate = false;
				if (pc.getString(invKey).equalsIgnoreCase(PropertyEntry.INVARIANT_ACTIVE)) {
					active = true;
					negate = false;
				} else if (pc.getString(invKey).equalsIgnoreCase(PropertyEntry.INVARIANT_INACTIVE)) {
					active = false;
					negate = false;
				} else if (pc.getString(invKey).equalsIgnoreCase(PropertyEntry.INVARIANT_NEGATE)) {
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
						settings.getIntegerTypeSettings().setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
					}
					break;
				case TypeConstants.INTEGER + PropertyEntry.integerValueMin:
					settings.getIntegerTypeSettings().setMinimum(pc.getInt(propertiesKey));
					break;
				case TypeConstants.INTEGER + PropertyEntry.integerValueMax:
					settings.getIntegerTypeSettings().setMaximum(pc.getInt(propertiesKey));
					break;
				case TypeConstants.REAL:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getRealTypeSettings().setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
					}
					break;
				case TypeConstants.REAL + PropertyEntry.realValueMin:
					settings.getRealTypeSettings().setMinimum(pc.getDouble(propertiesKey));
					break;
				case TypeConstants.REAL + PropertyEntry.realValueMax:
					settings.getRealTypeSettings().setMaximum(pc.getDouble(propertiesKey));
					break;
				case TypeConstants.REAL + PropertyEntry.realStep:
					settings.getRealTypeSettings().setStep(pc.getDouble(propertiesKey));
					break;
				case TypeConstants.STRING:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getStringTypeSettings().setValues(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey)));
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
	}
	
	public static void toDefaultSettings(SettingsConfiguration settings) {
		settings.getIntegerTypeSettings().setMinimum(DefaultConfigurationValues.integerMin);
		settings.getIntegerTypeSettings().setMaximum(DefaultConfigurationValues.integerMax);
		settings.getIntegerTypeSettings().deleteValues();
		settings.getRealTypeSettings().setMinimum(DefaultConfigurationValues.realMin);
		settings.getRealTypeSettings().setMaximum(DefaultConfigurationValues.realMax);
		settings.getRealTypeSettings().setStep(DefaultConfigurationValues.realStep);
		settings.getRealTypeSettings().deleteValues();
		settings.getStringTypeSettings().getBounds().setLower(DefaultConfigurationValues.stringMin);
		settings.getStringTypeSettings().getBounds().setUpper(DefaultConfigurationValues.stringMax);
		settings.getStringTypeSettings().deleteValues();
		
		settings.getOptionSettings().setAggregationcyclefreeness(DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS);
		settings.getOptionSettings().setForbiddensharing(DefaultConfigurationValues.FORBIDDENSHARING);
		
		Iterator<SettingsClass> classSettingsIterator = settings.getAllClassesSettings().iterator();
		while (classSettingsIterator.hasNext()) {
			SettingsClass classSettings = classSettingsIterator.next();
			classSettings.getBounds().setLower(DefaultConfigurationValues.objectsPerClassMin);
			classSettings.getBounds().setUpper(DefaultConfigurationValues.objectsPerClassMax);
			classSettings.deleteValues();
			Iterator<SettingsAttribute> attributeSettingsIterator = classSettings.getAttributeSettings().values().iterator();
			while (attributeSettingsIterator.hasNext()) {
				SettingsAttribute attributeSettings = attributeSettingsIterator.next();
				attributeSettings.getBounds().setLower(DefaultConfigurationValues.attributesPerClassMin);
				attributeSettings.getBounds().setUpper(DefaultConfigurationValues.attributesPerClassMax);
				attributeSettings.getCollectionSize().setLower(DefaultConfigurationValues.attributesColSizeMin);
				attributeSettings.getCollectionSize().setUpper(DefaultConfigurationValues.attributesColSizeMax);
				attributeSettings.deleteValues();
			}
			Iterator<SettingsAssociation> associationSettingsIterator = classSettings.getAssociationSettings().values().iterator();
			while (associationSettingsIterator.hasNext()) {
				SettingsAssociation associationSettings = associationSettingsIterator.next();
				associationSettings.getBounds().setLower(DefaultConfigurationValues.linksPerAssocMin);
				associationSettings.getBounds().setUpper(DefaultConfigurationValues.linksPerAssocMax);
				associationSettings.deleteValues();
			}
		}
		
		Iterator<SettingsInvariant> invariantSettingsIterator = settings.getAllInvariantsSettings().iterator();
		while (invariantSettingsIterator.hasNext()) {
			SettingsInvariant invariantSettings = invariantSettingsIterator.next();
			invariantSettings.setActive(DefaultConfigurationValues.INVARIANT_ACTIVE);
			invariantSettings.setNegate(DefaultConfigurationValues.INVARIANT_NEGATE);
		}
	}
	
	public static void clearSettings(SettingsConfiguration settings) {
		settings.getIntegerTypeSettings().setMinimum(null);
		settings.getIntegerTypeSettings().setMaximum(null);
		settings.getIntegerTypeSettings().deleteValues();
		settings.getRealTypeSettings().setMinimum(null);
		settings.getRealTypeSettings().setMaximum(null);
		settings.getRealTypeSettings().setStep(null);
		settings.getRealTypeSettings().deleteValues();
		settings.getStringTypeSettings().getBounds().setLower(null);
		settings.getStringTypeSettings().getBounds().setUpper(null);
		settings.getStringTypeSettings().deleteValues();
		
		settings.getOptionSettings().setAggregationcyclefreeness(null);
		settings.getOptionSettings().setForbiddensharing(null);
		
		Iterator<SettingsClass> classSettingsIterator = settings.getAllClassesSettings().iterator();
		while (classSettingsIterator.hasNext()) {
			SettingsClass classSettings = classSettingsIterator.next();
			classSettings.getBounds().setLower(null);
			classSettings.getBounds().setUpper(null);
			classSettings.deleteValues();
			Iterator<SettingsAttribute> attributeSettingsIterator = classSettings.getAttributeSettings().values().iterator();
			while (attributeSettingsIterator.hasNext()) {
				SettingsAttribute attributeSettings = attributeSettingsIterator.next();
				attributeSettings.getBounds().setLower(null);
				attributeSettings.getBounds().setUpper(null);
				attributeSettings.getCollectionSize().setLower(null);
				attributeSettings.getCollectionSize().setUpper(null);
				attributeSettings.deleteValues();
			}
			Iterator<SettingsAssociation> associationSettingsIterator = classSettings.getAssociationSettings().values().iterator();
			while (associationSettingsIterator.hasNext()) {
				SettingsAssociation associationSettings = associationSettingsIterator.next();
				associationSettings.getBounds().setLower(null);
				associationSettings.getBounds().setUpper(null);
				associationSettings.deleteValues();
			}
		}
		
		Iterator<SettingsInvariant> invariantSettingsIterator = settings.getAllInvariantsSettings().iterator();
		while (invariantSettingsIterator.hasNext()) {
			SettingsInvariant invariantSettings = invariantSettingsIterator.next();
			invariantSettings.setActive(null);
			invariantSettings.setNegate(null);
		}
	}

}
