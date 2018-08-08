package org.tzi.use.gui.plugins.classdiagram;

import java.util.List;

import javax.swing.JScrollPane;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.views.diagrams.classdiagram.ClassDiagramOptions;
import org.tzi.use.gui.views.diagrams.classdiagram.ClassDiagramView;
import org.tzi.use.gui.plugins.data.TAssociation;
import org.tzi.use.gui.plugins.data.TClass;
import org.tzi.use.uml.sys.MSystem;

@SuppressWarnings("serial")
public class OutputClassDiagramView extends ClassDiagramView {

	private List<TClass> classes;
	private List<TAssociation> associations;

	private OutputClassDiagram ocd;

	public OutputClassDiagramView(MainWindow mainWindow, MSystem system, List<TClass> classes,
			List<TAssociation> associations) {
		super(mainWindow, system, true);

		this.classes = classes;
		this.associations = associations;
		actuallyInitState();
	}

	@Override
	public void initDiagram(boolean loadDefaultLayout, ClassDiagramOptions opt) {
		OutputClassDiagramOptions newOptions = (OutputClassDiagramOptions) opt;
		if (newOptions == null) {
			newOptions = new OutputClassDiagramOptions();
		}
		newOptions.setShowRolenames(true);
		newOptions.setShowAttributes(true);		
		newOptions.setShowMutliplicities(true);
		//newOptions.setGroupMR(true); FIXME
		
		// save specifically as a OutputClassDiagram
		ocd = new OutputClassDiagram(this, fMainWindow.logWriter(), newOptions);
		// but also save it in parent class
		fClassDiagram = ocd;

		fClassDiagram.setStatusBar(fMainWindow.statusBar());
		this.removeAll();
		add(new JScrollPane(fClassDiagram));

		// do NOT initState();

		if (loadDefaultLayout) {
			fClassDiagram.loadDefaultLayout();
		}
	}

	private void actuallyInitState() {
		for (TClass cls : classes) {
			ocd.addClass(cls);
		}

		for (TAssociation asso : associations) {
			ocd.addAssociation(asso);
		}

		fClassDiagram.initialize();
	}
}