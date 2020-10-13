package org.tzi.use.gui.plugins;

import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.runtime.impl.Plugin;

/**
 * This is the main class of the OCLComplexity Plugin.
 * 
 * @author Timo Stüber
 */
public class OCLComplexityPlugin extends Plugin {

	final protected String PLUGIN_ID = "OCLComplexityPlugin";

	public String getName() {
		return this.PLUGIN_ID;
	}

	public void run(IPluginRuntime pluginRuntime) throws Exception {
	}
}
