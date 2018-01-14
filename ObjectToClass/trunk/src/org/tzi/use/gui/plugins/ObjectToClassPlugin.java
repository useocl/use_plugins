package org.tzi.use.gui.plugins;

import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.runtime.impl.Plugin;

/**
 * This is the main class of the Object to Class Plugin.
 * 
 * @author Andreas Kaestner
 */
public class ObjectToClassPlugin extends Plugin {

	final protected String PLUGIN_ID = "useObjectToClassPlugin";

	public String getName() {
		return this.PLUGIN_ID;
	}

	public void run(IPluginRuntime pluginRuntime) throws Exception {
		// Nothing to initialize
	}
}