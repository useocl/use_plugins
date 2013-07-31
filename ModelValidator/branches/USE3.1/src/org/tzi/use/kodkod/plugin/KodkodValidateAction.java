package org.tzi.use.kodkod.plugin;

import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

public class KodkodValidateAction extends KodkodValidateCmd implements IPluginActionDelegate {

	@Override
	public void performAction(IPluginAction pluginAction) {
		initialize(pluginAction.getSession(), pluginAction.getParent());
		noArguments();
	}
}
