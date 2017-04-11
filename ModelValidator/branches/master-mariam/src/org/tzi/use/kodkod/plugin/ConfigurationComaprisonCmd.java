package org.tzi.use.kodkod.plugin;

import org.tzi.use.kodkod.compare.ConfigurationComparator;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.uml.mm.MModel;

public class ConfigurationComaprisonCmd implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		
		String[] cmdArgumentList = pluginCommand.getCmdArgumentList();
		Shell shell = pluginCommand.getShell();
		
		if(cmdArgumentList.length != 3){
			shell.getErr().println("Wrong number of parameters.");
			shell.getErr().println("Usage: " + pluginCommand.getCmd() + " <filname> <configname1> <configname2>");
			return;
		}
		
		String filename = cmdArgumentList[0];
		String config1 = cmdArgumentList[1];
		String config2 = cmdArgumentList[2];
		
		MModel model = pluginCommand.getSession().system().model();
		
		System.out.println("filename: " + filename + "; c1: " + config1 + "; c2: " + config2);
		// load file into HierarchicalINIConfiguration
		// make sure that configurations are part of the file
		// call comparison class to compare configurations
		
		ConfigurationComparator comp = new ConfigurationComparator();
//		comp.compare(config1, config2);
	}

}
