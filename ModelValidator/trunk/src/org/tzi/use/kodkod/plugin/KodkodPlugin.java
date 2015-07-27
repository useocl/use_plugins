package org.tzi.use.kodkod.plugin;

import java.io.File;

import org.apache.log4j.xml.DOMConfigurator;
import org.tzi.kodkod.helper.PathHelper;
import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.runtime.impl.Plugin;

public class KodkodPlugin extends Plugin {

	private final String PLUGIN_ID = "ModelValidatorPlugin";

	@Override
	public String getName() {
		return PLUGIN_ID;
	}

	protected void doRun(IPluginRuntime pluginRuntime) {
		File externLog = new File(PathHelper.getPluginPath(), "log4j.xml");
		if(externLog.exists()){
			DOMConfigurator.configure(externLog.getPath());
		}
		else{
			DOMConfigurator.configure(this.getResource("log4j/log4j.xml"));
		}
	}
}
