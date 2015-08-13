package org.tzi.use.kodkod.plugin.gui.util;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IClass;
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
import org.tzi.use.util.StringUtil;

public final class ChangeConfiguration {
	
	private ChangeConfiguration(){
	}
	
	public static Configuration toProperties(final SettingsConfiguration settings, final IModel model) {
		PropertiesConfiguration pc = new PropertiesConfiguration();
		
		IntegerSettings integerSettings = settings.getIntegerTypeSettings();
		if(integerSettings.isEnabled()){
			pc.setProperty(TypeConstants.INTEGER + PropertyEntry.integerValuesMin, integerSettings.getMinimum());
			pc.setProperty(TypeConstants.INTEGER + PropertyEntry.integerValuesMax, integerSettings.getMaximum());
			
			if (!integerSettings.getValues().isEmpty()) {
				pc.setProperty(TypeConstants.INTEGER, toListProperty(integerSettings.getValues()));
			}
		}
		
		StringSettings stringSettings = settings.getStringTypeSettings();
		if(stringSettings.isEnabled()){
			pc.setProperty(TypeConstants.STRING + PropertyEntry.stringValuesMin, stringSettings.getLowerBound());
			pc.setProperty(TypeConstants.STRING + PropertyEntry.stringValuesMax, stringSettings.getUpperBound());
			
			if (!stringSettings.getInstanceNames().isEmpty()) {
				pc.setProperty(TypeConstants.STRING, toListProperty(stringSettings.getInstanceNames()));
			}
		}
		
		RealSettings realSettings = settings.getRealTypeSettings();
		if(realSettings.isEnabled()){
			pc.setProperty(TypeConstants.REAL + PropertyEntry.realValuesMin, realSettings.getMinimum());
			pc.setProperty(TypeConstants.REAL + PropertyEntry.realValuesMax, realSettings.getMaximum());
			pc.setProperty(TypeConstants.REAL + PropertyEntry.realStep, realSettings.getStep());
			
			if (!realSettings.getValues().isEmpty()) {
				pc.setProperty(TypeConstants.REAL, toListProperty(realSettings.getValues()));
			}
		}
		
		for(ClassSettings classSettings : settings.getAllClassesSettings()){
			IClass cls = classSettings.getCls();
			if(!(cls instanceof IAssociationClass)){
				pc.setProperty(cls.name() + PropertyEntry.objMin, classSettings.getLowerBound());
				pc.setProperty(cls.name() + PropertyEntry.objMax, classSettings.getUpperBound());
			}
			if (!classSettings.getInstanceNames().isEmpty()) {
				String propertyName = cls.name() + ((cls instanceof IAssociationClass) ? PropertyEntry.ASSOCIATIONCLASS : "");
				pc.setProperty(propertyName, toListProperty(classSettings.getInstanceNames()));
			}
			
			for(AttributeSettings attributeSettings : classSettings.getAttributeSettings().values()){
				String attribute = attributeSettings.getAttribute().owner().name() + "_" + attributeSettings.getAttribute().name();
				pc.setProperty(attribute + PropertyEntry.attributeDefValuesMin, attributeSettings.getLowerBound());
				pc.setProperty(attribute + PropertyEntry.attributeDefValuesMax, attributeSettings.getUpperBound());
				pc.setProperty(attribute + PropertyEntry.attributeColSizeMin, attributeSettings.getCollectionSizeMin());
				pc.setProperty(attribute + PropertyEntry.attributeColSizeMax, attributeSettings.getCollectionSizeMax());
				
				if (!attributeSettings.getInstanceNames().isEmpty()) {
					pc.setProperty(attribute, toListProperty(attributeSettings.getInstanceNames()));
				}
			}
		}
		
		for(AssociationSettings associationSettings : settings.getAllAssociationSettings()){
			String association = associationSettings.getAssociation().name();
			pc.setProperty(association + PropertyEntry.linksMin, associationSettings.getLowerBound());
			pc.setProperty(association + PropertyEntry.linksMax, associationSettings.getUpperBound());
			
			if (!associationSettings.getInstanceNames().isEmpty()) {
				pc.setProperty(association, toListProperty(associationSettings.getInstanceNames()));
			}
		}
		
		for(InvariantSettings invariantSettings : settings.getAllInvariantsSettings()){
			String set = PropertyEntry.INVARIANT_ACTIVE;
			if (!invariantSettings.isActive() || invariantSettings.isNegate()) {
				if (!invariantSettings.isActive()){
					set = PropertyEntry.INVARIANT_INACTIVE;
				} else if (invariantSettings.isNegate()) {
					set = PropertyEntry.INVARIANT_NEGATE;
				}
				pc.setProperty(invariantSettings.getInvariant().clazz().name() + "_" + invariantSettings.getInvariant().name(), set);
			}
		}
		
		OptionSettings optionsSettings = settings.getOptionSettings();
		pc.setProperty(PropertyEntry.aggregationcyclefreeness, optionsSettings.isAggregationcyclefreeness() ? "on" : "off");
		pc.setProperty(PropertyEntry.forbiddensharing, optionsSettings.isForbiddensharing() ? "on" : "off");

		return pc;
	}
	
	public static void toSettings(IModel model, final Configuration pc, SettingsConfiguration settings) {
		settings.reset();
		
		// Integer
		boolean integerExist = false;
		IntegerSettings is = settings.getIntegerTypeSettings();
		
		if(pc.containsKey(TypeConstants.INTEGER + PropertyEntry.integerValuesMin)){
			is.setMinimum(pc.getInt(TypeConstants.INTEGER + PropertyEntry.integerValuesMin));
			integerExist = true;
		}
		if(pc.containsKey(TypeConstants.INTEGER + PropertyEntry.integerValuesMax)){
			is.setMaximum(pc.getInt(TypeConstants.INTEGER + PropertyEntry.integerValuesMax));
			integerExist = true;
		}
		if(pc.containsKey(TypeConstants.INTEGER)){
			is.setValues(objectToIntegerSet(pc.getList(TypeConstants.INTEGER)));
			integerExist = true;
		}
		is.setEnabled(integerExist);
		
		// String
		boolean stringExist = false;
		StringSettings ss = settings.getStringTypeSettings();
		
		if(pc.containsKey(TypeConstants.STRING + PropertyEntry.stringValuesMin)){
			ss.setLowerBound(pc.getInt(TypeConstants.STRING + PropertyEntry.stringValuesMin));
			stringExist = true;
		}
		if(pc.containsKey(TypeConstants.STRING + PropertyEntry.stringValuesMax)){
			ss.setUpperBound(pc.getInt(TypeConstants.STRING + PropertyEntry.stringValuesMax));
			stringExist = true;
		}
		if(pc.containsKey(TypeConstants.STRING)){
			ss.setInstanceNames(objectToInstanceNameList(pc.getList(TypeConstants.STRING)));
			stringExist = true;
		}
		ss.setEnabled(stringExist);
		
		// Real
		boolean realExist = false;
		RealSettings rs = settings.getRealTypeSettings();
		
		if(pc.containsKey(TypeConstants.REAL + PropertyEntry.realValuesMin)){
			rs.setMinimum(pc.getDouble(TypeConstants.REAL + PropertyEntry.realValuesMin));
			realExist = true;
		}
		if(pc.containsKey(TypeConstants.REAL + PropertyEntry.realValuesMax)){
			rs.setMaximum(pc.getDouble(TypeConstants.REAL + PropertyEntry.realValuesMax));
			realExist = true;
		}
		if(pc.containsKey(TypeConstants.REAL + PropertyEntry.realStep)){
			rs.setStep(pc.getDouble(TypeConstants.REAL + PropertyEntry.realStep));
			realExist = true;
		}
		if(pc.containsKey(TypeConstants.REAL)){
			rs.setValues(objectToDoubleSet(pc.getList(TypeConstants.REAL)));
			realExist = true;
		}
		rs.setEnabled(realExist);
		
		// Classes
		for(ClassSettings cs : settings.getAllClassesSettings()){
			String className = cs.getCls().name();
			if(!(cs.getCls() instanceof IAssociationClass)){
				if(pc.containsKey(className + PropertyEntry.objMin)){
					cs.setLowerBound(pc.getInt(className + PropertyEntry.objMin));
				}
				if(pc.containsKey(className + PropertyEntry.objMax)){
					cs.setUpperBound(pc.getInt(className + PropertyEntry.objMax));
				}
			}
			String propertyName = className + ((cs.getCls() instanceof IAssociationClass) ? PropertyEntry.ASSOCIATIONCLASS : "" );
			if(pc.containsKey(propertyName)){
				cs.setInstanceNames(objectToInstanceNameList(pc.getList(propertyName)));
			}
			
			// Attributes
			for(AttributeSettings attrS : cs.getAttributeSettings().values()){
				String attrName = attrS.getAttribute().owner().name() + "_" + attrS.getAttribute().name();
				if(pc.containsKey(attrName + PropertyEntry.attributeDefValuesMin)){
					attrS.setLowerBound(pc.getInt(attrName + PropertyEntry.attributeDefValuesMin));
				}
				if(pc.containsKey(attrName + PropertyEntry.attributeDefValuesMax)){
					attrS.setUpperBound(pc.getInt(attrName + PropertyEntry.attributeDefValuesMax));
				}
				if(pc.containsKey(attrName + PropertyEntry.attributeColSizeMin)){
					attrS.setCollectionSizeMin(pc.getInt(attrName + PropertyEntry.attributeColSizeMin));
				}
				if(pc.containsKey(attrName + PropertyEntry.attributeColSizeMax)){
					attrS.setCollectionSizeMax(pc.getInt(attrName + PropertyEntry.attributeColSizeMax));
				}
				if(pc.containsKey(attrName)){
					attrS.setInstanceNames(objectToInstanceNameList(pc.getList(attrName)));
				}
			}
		}
		
		// Associations
		for(AssociationSettings as : settings.getAllAssociationSettings()){
			String assocName = as.getAssociation().name();
			if(pc.containsKey(assocName + PropertyEntry.linksMin)){
				as.setLowerBound(pc.getInt(assocName + PropertyEntry.linksMin));
			}
			if(pc.containsKey(assocName + PropertyEntry.linksMax)){
				as.setUpperBound(pc.getInt(assocName + PropertyEntry.linksMax));
			}
			if(pc.containsKey(assocName)){
				String unformattedList = pc.getList(assocName).toString();
				Pattern p = Pattern.compile("\\(.+?\\)");
				Matcher m = p.matcher(unformattedList);
				LinkedHashSet<String> links = new LinkedHashSet<String>();
				while(m.find()){
					links.add(m.group().replaceAll("\\s+", " "));
				}
				as.setInstanceNames(links);
			}
		}
		
		// Invariants
		for(InvariantSettings invS : settings.getAllInvariantsSettings()){
			String propertyName = invS.getInvariant().clazz().name() + "_" + invS.getInvariant().name();
			if(pc.containsKey(propertyName)){
				String value = pc.getString(propertyName);
				if(value.equalsIgnoreCase(PropertyEntry.INVARIANT_INACTIVE)){
					invS.setActive(false);
				} else if(value.equalsIgnoreCase(PropertyEntry.INVARIANT_NEGATE)){
					invS.setNegate(true);
				}
			}
		}
		
		// Options
		OptionSettings os = settings.getOptionSettings();
		if(pc.containsKey(PropertyEntry.aggregationcyclefreeness)){
			os.setAggregationcyclefreeness(pc.getString(PropertyEntry.aggregationcyclefreeness).equalsIgnoreCase("on"));
		}
		if(pc.containsKey(PropertyEntry.forbiddensharing)){
			os.setForbiddensharing(pc.getString(PropertyEntry.forbiddensharing).equalsIgnoreCase("on"));
		}
	}

	private static String toListProperty(Collection<?> l){
		return "Set{" + StringUtil.fmtSeq(l, ", ") + "}";
	}
	
	private static Set<Integer> objectToIntegerSet(List<Object> values){
		Set<Integer> res = new LinkedHashSet<>();
		
		fixList(values);
		
		for(Object val : values){
			String s = val.toString().trim();
			//TODO silently ignore non integers?
			res.add(Integer.valueOf(s));
		}
		
		return res;
	}
	
	private static Set<Double> objectToDoubleSet(List<Object> values){
		Set<Double> res = new LinkedHashSet<>();
		
		fixList(values);
		
		for(Object val : values){
			String s = val.toString().trim();
			res.add(Double.valueOf(s));
		}
		
		return res;
	}

	private static Set<String> objectToInstanceNameList(List<Object> values){
		Set<String> res = new LinkedHashSet<>();
		
		fixList(values);
		
		for(Object val : values){
			res.add(val.toString().trim());
		}
		
		return res;
	}
	
	/**
	 * Removes extra 'Set{' and '}' from the first and last list elements.
	 */
	private static void fixList(List<Object> values) {
		if(values.size() == 1 && values.get(0).toString().matches("Set\\s*\\{\\}")){
			values.remove(0);
			return;
		}
		
		String arg1 = values.get(0).toString().replaceFirst("Set\\s*\\{", "");
		values.set(0, arg1);
		
		String argL = values.get(values.size()-1).toString().replace("}", "");
		values.set(values.size()-1, argL);
	}
	
}
