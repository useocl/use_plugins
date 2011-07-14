package org.tzi.use.kodkod.main;

import org.tzi.use.runtime.IPlugin;
import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.runtime.impl.Plugin;

/**
 * Main class of the Kodkod Plugin
 * @author Torsten Humann
 * 
 */
public class KodkodPlugin extends Plugin implements IPlugin {

	final protected String PLUGIN_ID = "Kodkod Plugin";

	public String getName() {
		return PLUGIN_ID;
	}

	public void run(IPluginRuntime pluginRuntime) throws Exception {
		// Nothing to initialize
	}
}