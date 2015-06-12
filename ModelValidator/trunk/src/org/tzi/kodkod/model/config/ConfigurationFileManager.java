package org.tzi.kodkod.model.config;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.tzi.kodkod.model.config.impl.PropertyEntry;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.kodkod.plugin.PropertiesWriter;
import org.tzi.use.kodkod.plugin.gui.model.data.SettingsConfiguration;
import org.tzi.use.kodkod.plugin.gui.util.ChangeConfiguration;

public class ConfigurationFileManager {

	public static final String DEFAULT_CONFIG_PREFIX = "config";
	public static final int MAX_CONFIGURATION_NAME_LENGTH = 64;
	
	private final IModel model;
	private final SettingsConfiguration sc;
	
	private final Map<String, Configuration> configurations = new LinkedHashMap<String, Configuration>();

	public ConfigurationFileManager(IModel model, SettingsConfiguration sc, File configFile) throws ConfigurationException {
		this.model = model;
		this.sc = sc;
		
		HierarchicalINIConfiguration iniFile = new HierarchicalINIConfiguration(configFile);
		for(String section : iniFile.getSections()){
			String sectionName = (section != null) ? section : createDefaultConfigName() ;
			configurations.put(sectionName, iniFile.getSection(section));
		}
	}
	
	public ConfigurationFileManager(IModel model, SettingsConfiguration sc) {
		this.model = model;
		this.sc = sc;
		createDefaultConfiguration();
	}
	
	private void createDefaultConfiguration() {
		ChangeConfiguration.resetSettings(sc);
		Configuration c = ChangeConfiguration.toProperties(sc, model);
		addOrUpdateConfiguration(createDefaultConfigName(), c);
	}
	
	public String[] getConfigurationNames() {
		return configurations.keySet().toArray(new String[0]);
	}
	
	public int getConfigutationCount(){
		return configurations.size();
	}
	
	public Configuration getConfiguration(String name) {
		return configurations.get(name);
	}
	
	public boolean isConfigNameTaken(String name){
		return configurations.containsKey(name);
	}

	public void addOrUpdateConfiguration(String name, Configuration c) {
		configurations.put(name, c);
	}
	
	public void removeConfiguration(String name) {
		configurations.remove(name);
		if(configurations.isEmpty()){
			createDefaultConfiguration();
		}
	}

	public String createNewConfigName(String prefix){
		int i = 1;
		while(configurations.containsKey(prefix + i)){
			++i;
		}
		
		return prefix + i;
	}
	
	public String createDefaultConfigName(){
		if(!configurations.containsKey(PropertyEntry.DEFAULT_SECTION_NAME)){
			return PropertyEntry.DEFAULT_SECTION_NAME;
		}
		
		return createNewConfigName(PropertyEntry.DEFAULT_SECTION_NAME);
	}
	
	public void save(File configFile) throws IOException {
		PropertiesWriter pw = new PropertiesWriter(model);
		pw.writeToFile(configFile, configurations);
	}
	
}
