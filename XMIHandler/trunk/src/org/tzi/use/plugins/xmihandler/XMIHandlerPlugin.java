package org.tzi.use.plugins.xmihandler;

import org.tzi.use.runtime.impl.Plugin;

public class XMIHandlerPlugin extends Plugin {

	private static String PLUGIN_NAME = "XMIHandler";
	
	public static XMIHandlerPlugin getXMIHandlerPluginInstance() {
		return (XMIHandlerPlugin)pluginInstance;
	}
	
	@Override
	public String getName() {
		return PLUGIN_NAME;
	}

}
