package org.tzi.use.plugin.filmstrip;

import javax.swing.JOptionPane;

import org.tzi.use.gui.views.View;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagram;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagramView;
import org.tzi.use.plugin.filmstrip.layout.FilmstripObjectDiagramLayouter;
import org.tzi.use.plugin.filmstrip.logic.FilmstripUtil;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

public abstract class LayoutObjectDiagramAction implements IPluginActionDelegate {
	
	@Override
	public void performAction(IPluginAction pluginAction) {
		if(!pluginAction.getSession().hasSystem()){
			JOptionPane.showMessageDialog(
							pluginAction.getParent(),
							"No model loaded. The transformation is based on the currently loaded model. Please load a model.",
							"No Model", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!FilmstripUtil.isFilmstripModel(pluginAction.getSession().system().model())){
			JOptionPane.showMessageDialog(
					pluginAction.getParent(),
					"The layout is only applicable to system states of filmstrip models.",
					"Not a Filmstrip Model", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		final View selectedView = pluginAction.getParent().getSelectedView();
		if(!(selectedView instanceof NewObjectDiagramView)){
			JOptionPane.showMessageDialog(
					pluginAction.getParent(),
					"The layout is applied to the current view, which has to be an object diagram.",
					"No Object Diagram Selected", JOptionPane.ERROR_MESSAGE);
			return;
		}

		final NewObjectDiagram objDia = ((NewObjectDiagramView) selectedView).getDiagram();
		FilmstripObjectDiagramLayouter layouter = new FilmstripObjectDiagramLayouter(objDia, pluginAction.getSession().system());
		performLayouting(layouter);
	}
	
	protected abstract void performLayouting(FilmstripObjectDiagramLayouter layouter);
	
}
