package org.tzi.use.kodkod.plugin.gui.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.kodkod.plugin.gui.model.data.AssociationSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.AttributeSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.ClassSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.IntegerSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.InvariantSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.OptionSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.RealSettings;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.model.data.StringSettings;

import com.google.common.collect.Sets;

public final class ChangeConfiguration {
	
	private ChangeConfiguration(){
	}
	
	public static Configuration toProperties(final SettingsConfiguration settings, final IModel model) {
		PropertiesConfiguration pc = new PropertiesConfiguration();
		
		IntegerSettings integerSettings = settings.getIntegerTypeSettings();
		if(integerSettings.isEnabled()){
			pc.setProperty(TypeConstants.INTEGER + PropertyEntry.integerValueMin, integerSettings.getMinimum());
			pc.setProperty(TypeConstants.INTEGER + PropertyEntry.integerValueMax, integerSettings.getMaximum());
			
			if (!integerSettings.getValues().isEmpty()) {
				pc.clearProperty(TypeConstants.INTEGER);
				
				Iterator<String> integerValues = ChangeString.formatSettingValuesForProperty(integerSettings.getValues()).iterator();
				while (integerValues.hasNext()) {
					pc.addProperty(TypeConstants.INTEGER, integerValues.next().toString());
				}
			}
		}
		
		StringSettings stringSettings = settings.getStringTypeSettings();
		if(stringSettings.isEnabled()){
			pc.setProperty(TypeConstants.STRING + PropertyEntry.stringValuesMin, stringSettings.getLowerBound());
			pc.setProperty(TypeConstants.STRING + PropertyEntry.stringValuesMax, stringSettings.getUpperBound());
			if (!stringSettings.getInstanceNames().isEmpty()) {
				pc.clearProperty(TypeConstants.STRING);
				
				Iterator<String> stringValues = ChangeString.formatSettingValuesForProperty(stringSettings.getInstanceNames()).iterator();
				while (stringValues.hasNext()) {
					pc.addProperty(TypeConstants.STRING, stringValues.next().toString());
				}
			}
		}
		
		RealSettings realSettings = settings.getRealTypeSettings();
		if(realSettings.isEnabled()){
			pc.setProperty(TypeConstants.REAL + PropertyEntry.realValueMin, realSettings.getMinimum());
			pc.setProperty(TypeConstants.REAL + PropertyEntry.realValueMax, realSettings.getMaximum());
			pc.setProperty(TypeConstants.REAL + PropertyEntry.realStep, realSettings.getStep());
			
			if (!realSettings.getValues().isEmpty()) {
				pc.clearProperty(TypeConstants.REAL);
				
				Iterator<String> realValues = ChangeString.formatSettingValuesForProperty(realSettings.getValues()).iterator();
				while (realValues.hasNext()) {
					pc.addProperty(TypeConstants.REAL, realValues.next().toString());
				}
			}
		}
		
		OptionSettings optionsSettings = settings.getOptionSettings();
		pc.setProperty(PropertyEntry.aggregationcyclefreeness, optionsSettings.isAggregationcyclefreeness()?"on":"off");
		pc.setProperty(PropertyEntry.forbiddensharing, optionsSettings.isForbiddensharing()?"on":"off");
		
		List<ClassSettings> classesSettings = settings.getAllClassesSettings();
		for(ClassSettings classSettings : classesSettings){
			if(!(classSettings.getCls() instanceof IAssociationClass)){
				pc.setProperty(classSettings.getCls().name() + PropertyEntry.objMin, classSettings.getLowerBound());
				pc.setProperty(classSettings.getCls().name() + PropertyEntry.objMax, classSettings.getUpperBound());
			}
			if (!classSettings.getInstanceNames().isEmpty()) {
				pc.clearProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS);
				
				Iterator<String> classValues = ChangeString.formatSettingValuesForProperty(classSettings.getInstanceNames()).iterator();
				while (classValues.hasNext()) {
					pc.addProperty(classSettings.getCls().name()+PropertyEntry.ASSOCIATIONCLASS, classValues.next().toString());
				}
			}
			
			for(AttributeSettings attributeSettings : classSettings.getAttributeSettings().values()){
				String attribute = attributeSettings.getAttribute().owner().name() + "_" + attributeSettings.getAttribute().name();
				pc.setProperty(attribute+PropertyEntry.attributeDefValuesMin, attributeSettings.getLowerBound());
				pc.setProperty(attribute+PropertyEntry.attributeDefValuesMax, attributeSettings.getUpperBound());
				pc.setProperty(attribute+PropertyEntry.attributeColSizeMin, attributeSettings.getCollectionSizeMin());
				pc.setProperty(attribute+PropertyEntry.attributeColSizeMax, attributeSettings.getCollectionSizeMax());
				
				if (!attributeSettings.getInstanceNames().isEmpty()) {
					pc.clearProperty(attribute);
					
					Iterator<String> attributeValues = ChangeString.formatSettingValuesForProperty(attributeSettings.getInstanceNames()).iterator();
					while (attributeValues.hasNext()) {
						pc.addProperty(attribute, attributeValues.next().toString());
					}
				}
			}
			
			for(AssociationSettings associationSettings : classSettings.getAssociationSettings().values()){
				String association = associationSettings.getAssociation().name();
				pc.setProperty(association + PropertyEntry.linksMin, associationSettings.getLowerBound());
				pc.setProperty(association + PropertyEntry.linksMax, associationSettings.getUpperBound());
				
				if (!associationSettings.getInstanceNames().isEmpty()) {
					pc.clearProperty(association);
					
					Iterator<String> associationValues = ChangeString.formatSettingValuesForProperty(associationSettings.getInstanceNames()).iterator();
					while (associationValues.hasNext()) {
						pc.addProperty(association, associationValues.next().toString());
					}
				}
			}
		}
		
		List<InvariantSettings> invariantsSettings = settings.getAllInvariantsSettings();
		for(InvariantSettings invariantSettings : invariantsSettings){
			String set = PropertyEntry.INVARIANT_ACTIVE;
			if (!invariantSettings.isActive() || invariantSettings.isNegate()) {
				if (!invariantSettings.isActive()){
					set = PropertyEntry.INVARIANT_INACTIVE;
				} else if (invariantSettings.isNegate()) {
					set = PropertyEntry.INVARIANT_NEGATE;
				}
				pc.setProperty(invariantSettings.getInvariant().clazz().name()+"_"+invariantSettings.getInvariant().name(), set);
			}
		}
		
		return pc;
	}
	
	public static void toSettings(IModel model, final Configuration pc, SettingsConfiguration givenSettings) {
		SettingsConfiguration settings = givenSettings;
		List<String> classes = new ArrayList<>();
		List<String> attributes = new ArrayList<>();
		List<String> associations = new ArrayList<>();
		List<String> invariants = new ArrayList<>();
		
		//from this point on, the possible keys which could appear in the PropertiesConfiguration are collected
		//from the model. Additionally, if there are no values set in the properties, the regarding setting will
		//be set to null.
		for(IClass clazz : model.classes()){
			String cls = clazz.name();
			if (model.associations().contains(clazz)) {
				classes.add(cls+PropertyEntry.ASSOCIATIONCLASS);
				if (!pc.containsKey(cls+PropertyEntry.ASSOCIATIONCLASS)) {
					settings.getClassSettings(cls).clearValues();
				}
			} else {
				classes.add(cls);
				classes.add(cls+PropertyEntry.objMin);
				classes.add(cls+PropertyEntry.objMax);
				if (!pc.containsKey(cls))
					settings.getClassSettings(cls).clearValues();
			}

			for(IAttribute attribute : clazz.allAttributes()){
				String attr = attribute.owner().name()+"::"+attribute.name();
				attributes.add(attr);
				attributes.add(attr+PropertyEntry.attributeDefValuesMin);
				attributes.add(attr+PropertyEntry.attributeDefValuesMax);
				attributes.add(attr+PropertyEntry.attributeColSizeMin);
				attributes.add(attr+PropertyEntry.attributeColSizeMax);
				if (!pc.containsKey(attribute.owner().name()+"_"+attribute.name())) {
					settings.getClassSettings(cls).getAttributeSettings().get(attribute).clearValues();
				}
			}
		}
		
		for(IAssociation association : model.associations()){
			String cls = association.associationEnds().get(0).associatedClass().name();
			String assoc = cls+"::"+association.name();
			if (association instanceof IAssociationClass) {
				assoc = association.name()+"::"+association.name();
			}
			associations.add(assoc);
			associations.add(assoc+PropertyEntry.linksMin);
			associations.add(assoc+PropertyEntry.linksMax);
			if (!pc.containsKey(association.name())) {
				if (!(association instanceof IAssociationClass)) {
					settings.getClassSettings(cls).getAssociationSettings().get(association).clearValues();
				} else {
					settings.getClassSettings(association.name()).getAssociationSettings().get(association).clearValues();
				}
			}
		}
		
		for(IInvariant invariant : model.classInvariants()){
			//for a better visual presentation, the class name and invariant name are
			//seperated with two colons, this will be changed back to an underline, 
			//when the settings are converted back as PropertiesConfiguration object
			//with the method toProperties() in this class.
			invariants.add(invariant.clazz().name()+"::"+invariant.name());
		}
		
		if (!pc.containsKey(TypeConstants.INTEGER)) {
			settings.getIntegerTypeSettings().clearValues();
		}
		if (!pc.containsKey(TypeConstants.REAL)) {
			settings.getRealTypeSettings().clearValues();
		}
		if (!pc.containsKey(TypeConstants.STRING)) {
			settings.getStringTypeSettings().clearValues();
		}
		
		//from this point on the keys from the given PropertiesConfiguration are compared with the
		//possible keys collected from the model, and than their values are added to the settings
		Iterator<String> keys = pc.getKeys();
		
		boolean integerSettingsExist = false;
		boolean stringSettingsExist = false;
		boolean realSettingsExist = false;
		
		while (keys.hasNext()) {
			String propertiesKey = keys.next();
			String first = "";
			String second = "";
			String third = "";
			HashSet<String> classNames = new HashSet<>();
			HashSet<String> assocNames = new HashSet<>();
			Iterator<IClass> classIterator = model.classes().iterator();
			Iterator<IAssociation> assocIterator = model.associations().iterator();
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
				IAssociation association = model.getAssociation(second);
				if (association instanceof IAssociationClass) {
					first = association.name();
				} else {
					first = association.associationEnds().iterator().next().associatedClass().name();
				}
				switchKey = first+"::"+second+third;
			}
			
			if (classes.contains(propertiesKey)) {
				if (propertiesKey.endsWith(PropertyEntry.objMin)) {
					String clazz = propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.objMin));
					settings.getClassSettings(clazz).setLowerBound(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.objMax)) {
					String clazz = propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.objMax));
					settings.getClassSettings(clazz).setUpperBound(pc.getInt(propertiesKey));
				} else {
					if (pc.getProperty(propertiesKey) != null) {
						if (propertiesKey.endsWith(PropertyEntry.ASSOCIATIONCLASS)) {
							settings.getClassSettings(propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.ASSOCIATIONCLASS)))
								.setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey))));
						} else {
							settings.getClassSettings(propertiesKey)
								.setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey))));
						}
					} else {
						settings.getClassSettings(propertiesKey.substring(0, propertiesKey.indexOf(PropertyEntry.ASSOCIATIONCLASS)))
							.setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey))));
						settings.getClassSettings(propertiesKey)
							.setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey))));
					}
				}
			} else if (attributes.contains(switchKey)) {
				if (propertiesKey.endsWith(PropertyEntry.attributeDefValuesMin)) {
					String clazz = first;
					String attr = second;
					IAttribute attribute = model.getClass(clazz).getAttribute(attr);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).setLowerBound(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.attributeDefValuesMax)) {
					String clazz = first;
					String attr = second;
					IAttribute attribute = model.getClass(clazz).getAttribute(attr);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).setUpperBound(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.attributeColSizeMin)) {
					String clazz = first;
					String attr = second;
					IAttribute attribute = model.getClass(clazz).getAttribute(attr);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).setCollectionSizeMin(pc.getInt(propertiesKey));
				} else if (propertiesKey.endsWith(PropertyEntry.attributeColSizeMax)) {
					String clazz = first;
					String attr = second;
					IAttribute attribute = model.getClass(clazz).getAttribute(attr);
					settings.getClassSettings(clazz).getAttributeSettings().get(attribute).setCollectionSizeMax(pc.getInt(propertiesKey));
				} else {
					if (pc.getProperty(propertiesKey) != null) {
						String clazz = first;
						String attr = second;
						IAttribute attribute = model.getClass(clazz).getAttribute(attr);
						settings.getClassSettings(clazz).getAttributeSettings().get(attribute)
							.setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey))));
					}
				}
			} else if (associations.contains(switchKey)) {
				if (propertiesKey.endsWith(PropertyEntry.linksMin)) {
					String clazz = first;
					String assoc = second;
					IAssociation association = model.getAssociation(assoc);
					settings.getClassSettings(clazz).getAssociationSettings().get(association)
						.setLowerBound(pc.getInt(assoc+PropertyEntry.linksMin));
				} else if (propertiesKey.endsWith(PropertyEntry.linksMax)) {
					String clazz = first;
					String assoc = second;
					IAssociation association = model.getAssociation(assoc);
					settings.getClassSettings(clazz).getAssociationSettings().get(association)
						.setUpperBound(pc.getInt(assoc+PropertyEntry.linksMax));
				} else {
					if (pc.getProperty(propertiesKey) != null) {
						String clazz = first;
						String assoc = second;
						IAssociation association = model.getAssociation(assoc);
						settings.getClassSettings(clazz).getAssociationSettings().get(association)
							.setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(assoc))));
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
						/*
						 * TODO update apache configurations library for generics
						 * incompatible with some other code
						 */
						settings.getIntegerTypeSettings().setValues(objectToIntegerSet(pc.getList(propertiesKey)));
						integerSettingsExist = true;
					}
					break;
				case TypeConstants.INTEGER + PropertyEntry.integerValueMin:
					settings.getIntegerTypeSettings().setMinimum(pc.getInt(propertiesKey));
					integerSettingsExist = true;
					break;
				case TypeConstants.INTEGER + PropertyEntry.integerValueMax:
					settings.getIntegerTypeSettings().setMaximum(pc.getInt(propertiesKey));
					integerSettingsExist = true;
					break;
				case TypeConstants.REAL:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getRealTypeSettings().setValues(objectToDoubleSet(pc.getList(propertiesKey)));
						realSettingsExist = true;
					}
					break;
				case TypeConstants.REAL + PropertyEntry.realValueMin:
					settings.getRealTypeSettings().setMinimum(pc.getDouble(propertiesKey));
					realSettingsExist = true;
					break;
				case TypeConstants.REAL + PropertyEntry.realValueMax:
					settings.getRealTypeSettings().setMaximum(pc.getDouble(propertiesKey));
					realSettingsExist = true;
					break;
				case TypeConstants.REAL + PropertyEntry.realStep:
					settings.getRealTypeSettings().setStep(pc.getDouble(propertiesKey));
					realSettingsExist = true;
					break;
				case TypeConstants.STRING:
					if (pc.getProperty(propertiesKey) != null) {
						settings.getStringTypeSettings().setInstanceNames(Sets.newLinkedHashSet(ChangeString.formatPropertyListForSetting(pc.getProperty(propertiesKey))));
						stringSettingsExist = true;
					}
					break;
				case TypeConstants.STRING + PropertyEntry.stringValuesMin:
					settings.getStringTypeSettings().setLowerBound(pc.getInt(propertiesKey));
					stringSettingsExist = true;
					break;
				case TypeConstants.STRING + PropertyEntry.stringValuesMax:
					settings.getStringTypeSettings().setUpperBound(pc.getInt(propertiesKey));
					stringSettingsExist = true;
					break;
				}
			}
			
			settings.getIntegerTypeSettings().setEnabled(integerSettingsExist);
			settings.getStringTypeSettings().setEnabled(stringSettingsExist);
			settings.getRealTypeSettings().setEnabled(realSettingsExist);
		}
	}
	
	public static void resetSettings(SettingsConfiguration settings) {
		settings.getIntegerTypeSettings().reset();
		settings.getStringTypeSettings().reset();
		settings.getRealTypeSettings().reset();
		settings.getOptionSettings().reset();
		
		Iterator<ClassSettings> classSettingsIterator = settings.getAllClassesSettings().iterator();
		while (classSettingsIterator.hasNext()) {
			ClassSettings classSettings = classSettingsIterator.next();
			classSettings.reset();
			
			Iterator<AttributeSettings> attributeSettingsIterator = classSettings.getAttributeSettings().values().iterator();
			while (attributeSettingsIterator.hasNext()) {
				AttributeSettings attributeSettings = attributeSettingsIterator.next();
				attributeSettings.reset();
			}
			
			Iterator<AssociationSettings> associationSettingsIterator = classSettings.getAssociationSettings().values().iterator();
			while (associationSettingsIterator.hasNext()) {
				AssociationSettings associationSettings = associationSettingsIterator.next();
				associationSettings.reset();
			}
		}
		
		Iterator<InvariantSettings> invariantSettingsIterator = settings.getAllInvariantsSettings().iterator();
		while (invariantSettingsIterator.hasNext()) {
			InvariantSettings invariantSettings = invariantSettingsIterator.next();
			invariantSettings.reset();
		}
	}

	private static Set<Integer> objectToIntegerSet(List<Object> values){
		Set<Integer> res = new LinkedHashSet<>();
		
		fixList(values);
		
		for(Object val : values){
			String s = val.toString();
			res.add(Integer.valueOf(s));
		}
		
		return res;
	}
	
	private static Set<Double> objectToDoubleSet(List<Object> values){
		Set<Double> res = new LinkedHashSet<>();
		
		fixList(values);
		
		for(Object val : values){
			String s = val.toString();
			res.add(Double.valueOf(s));
		}
		
		return res;
	}

	/**
	 * Removes extra 'Set{' and '}' from the first and last list elements.
	 */
	private static void fixList(List<Object> values) {
		String arg1 = values.get(0).toString().replaceFirst("Set\\s*\\{", "");
		String argL = values.get(values.size()-1).toString().replace("}", "");
		
		values.set(0, arg1);
		values.set(values.size()-1, argL);
	}
	
}
