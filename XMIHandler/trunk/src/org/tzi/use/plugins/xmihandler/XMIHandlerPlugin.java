package org.tzi.use.plugins.xmihandler;

import java.io.File;

import org.tzi.use.main.Session;
import org.tzi.use.runtime.impl.Plugin;
import org.tzi.use.uml.mm.MModel;

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

  public void exportToXMI(File file, MModel useModel) {
    XMIExporter.exportToXMI(file, useModel);
  }
  
  public void importFromXMI(File file, Session session) {
    XMIImporter.importFromXMI(file, session);
  }
  
}
