package org.tzi.use.modelvalidator.main;

import org.tzi.use.runtime.IPlugin;
import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.runtime.impl.Plugin;

/**
 * Main class of the USE Model Validator
 * @author Mirco Kuhlmann
 * 
 */
public class Main extends Plugin implements IPlugin {

	final protected String PLUGIN_ID = "USE Model Validator";

	public String getName() {
		return PLUGIN_ID;
	}

	public void run(IPluginRuntime pluginRuntime) throws Exception {
		// Nothing to initialize
	}
}