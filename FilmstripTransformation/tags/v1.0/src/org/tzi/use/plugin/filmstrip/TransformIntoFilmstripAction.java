package org.tzi.use.plugin.filmstrip;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.tzi.use.config.Options;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.plugin.filmstrip.gui.ProgressWindow;
import org.tzi.use.plugin.filmstrip.logic.FilmstripTransformerTask;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;

public class TransformIntoFilmstripAction implements IPluginActionDelegate {
	
	@Override
	public void performAction(IPluginAction pluginAction) {
		
		// grab desired savepath
		JFileChooser fc = new JFileChooser(Options.getLastDirectory().toFile());
		fc.setFileFilter(new ExtFileFilter("use", "USE Model"));
		fc.setDialogTitle("Choose save file");
		fc.setMultiSelectionEnabled(false);
		
		int option;
		
		do {
			option = JOptionPane.YES_OPTION;
			if(fc.showSaveDialog(pluginAction.getParent()) != JFileChooser.APPROVE_OPTION){
				return;
			}
			
			Options.setLastDirectory(fc.getCurrentDirectory().toPath());
			
			if(fc.getSelectedFile().exists()){
				option = JOptionPane.showConfirmDialog(
						pluginAction.getParent(), String.format(
								"Overwrite existing file '%s'?", fc
										.getSelectedFile().getName()),
						"Please confirm", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if(option == JOptionPane.CANCEL_OPTION){
					return;
				}
			}
		} while(option != JOptionPane.YES_OPTION);
		
		ProgressWindow pw = new ProgressWindow(pluginAction.getParent(), "Please wait");
		FilmstripTransformerTask task = new FilmstripTransformerTask(pluginAction.getSession().system().model(), fc.getSelectedFile(), pw);
		task.execute();
	}
	
}
