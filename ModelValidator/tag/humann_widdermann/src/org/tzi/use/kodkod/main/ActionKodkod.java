package org.tzi.use.kodkod.main;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.kodkod.gui.KodkodView;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MSystem;

/**
 * Kodkod Plugin Action class
 * provides the Action which will be performed if the 
 * corresponding Plugin Action Delegate in the application
 * @author Torsten Humann
 */
public class ActionKodkod implements IPluginActionDelegate {

	public ActionKodkod(){
	}

	//Action Method called from the Action Proxy
	//starts KodkodView if model contains no collection types 
	public void performAction(IPluginAction pluginAction){
		Session curSession = pluginAction.getSession();
		MSystem curSystem = curSession.system();
		MainWindow curMainWindow = pluginAction.getParent();
		
		ArrayList<String> except = new ArrayList<String>();
		
		for(int i = 0; i < curSystem.model().classes().size(); i++){
			MClass cla = (MClass) curSystem.model().classes().toArray()[i];
			for(int j = 0; j < cla.attributes().size(); j++){
				MAttribute att = (MAttribute) cla.attributes().toArray()[j];
				if(att.type().isCollection(true)){
					except.add(cla.name() + " : " + att.name());
				}
			}
		}
		
		if(except.size() == 0){
			KodkodView kv = new KodkodView(curMainWindow, curSystem);
			kv.setVisible(true);
			ViewFrame vf = new ViewFrame("Kodkod", kv, "");
			JComponent c = (JComponent) vf.getContentPane();
			c.setLayout(new BorderLayout());
			c.add(new JScrollPane(kv), BorderLayout.CENTER);
			curMainWindow.addNewViewFrame(vf);
		}else{
			curMainWindow.logWriter().println("cannot use kodkod plugin");
			curMainWindow.logWriter().println("no collection types allowed");
			curMainWindow.logWriter().println("collection types:");
			for(int i = 0; i < except.size(); i++){
				curMainWindow.logWriter().println(except.get(i));
			}
			boolean checkState = curSystem.state().checkStructure(curMainWindow.logWriter());
			if(checkState){
				curMainWindow.logWriter().println("ok");
			}else{
				curMainWindow.logWriter().println("found errors");	
			}
		}
	}
}