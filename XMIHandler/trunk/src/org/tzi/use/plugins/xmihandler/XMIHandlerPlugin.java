package org.tzi.use.plugins.xmihandler;

import java.io.File;
import java.io.PrintWriter;

import org.tzi.use.main.Session;
import org.tzi.use.plugins.xmihandler.backend.XMIExporter;
import org.tzi.use.plugins.xmihandler.backend.XMIImporter;
import org.tzi.use.plugins.xmihandler.utils.Utils;
import org.tzi.use.runtime.impl.Plugin;

public class XMIHandlerPlugin extends Plugin {

	private static String PLUGIN_NAME = "XMIHandler";

	private static XMIHandlerPlugin xmiHandlerPlugin = new XMIHandlerPlugin();

	public static XMIHandlerPlugin getXMIHandlerPluginInstance() {
		return xmiHandlerPlugin;
	}

	@Override
	public String getName() {
		return PLUGIN_NAME;
	}

	public void exportToXMI(File file, Session session, PrintWriter logWriter) {
		Utils.setLogWriter(logWriter);
		try {
			XMIExporter.exportToXMI(file, session);
		} catch (Exception ex) {
			Utils.error(ex);
			Utils.out("Export failed.");
		}
	}

	public void importFromXMI(File file, Session session, PrintWriter logWriter) {
		Utils.setLogWriter(logWriter);
		try {
			XMIImporter.importFromXMI(file, session);
		} catch (Exception ex) {
			Utils.error(ex);
			Utils.out("Import failed.");
		}
	}

}
