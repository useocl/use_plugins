package org.tzi.use.plugin.filmstrip.logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.tzi.use.plugin.filmstrip.gui.ErrorFormatter;
import org.tzi.use.plugin.filmstrip.gui.ProgressWindow;
import org.tzi.use.plugin.filmstrip.logic.TransformationInputException.ModelElements;
import org.tzi.use.util.StringUtil;

public class FilmstripTransformerTask extends SwingWorker<Void, Void> {
	
	private FilmstripOptions options;
	private ProgressWindow pw;
	
	public FilmstripTransformerTask(FilmstripOptions options, ProgressWindow pw) {
		this.options = options;
		this.pw = pw;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		FilmstripTransformer ft = new FilmstripTransformer(options);
		ft.transformAndSave();
		
		return null;
	}
	
	@Override
	protected void done() {
		
		try {
			get();
		}
		catch (ExecutionException e) {
			pw.dispose();
			Throwable origEx = e.getCause();
			
			String errorTitle;
			String errorMsg;
			if(origEx instanceof TransformationInputException){
				errorTitle = "Error";
				
				ModelElements elems = ((TransformationInputException) origEx).getErrors();
				StringBuilder sb = new StringBuilder(
						"The input model contains elements with reserved names.");
				sb.append(StringUtil.NEWLINE);
				sb.append(ErrorFormatter.formatInputElementErrors(elems));
				errorMsg = sb.toString();
			}
			else if(origEx instanceof TransformationException){
				errorTitle = "Error";
				errorMsg = "An error occured while transforming the model." + StringUtil.NEWLINE + origEx.getMessage();
			}
			else if(origEx instanceof FileNotFoundException){
				errorTitle = "Error";
				errorMsg = "Could not open outputfile." + StringUtil.NEWLINE + origEx.getMessage();
			}
			else if(origEx instanceof IOException){
				errorTitle = "Error";
				errorMsg = "Could not write outputfile." + StringUtil.NEWLINE + origEx.getMessage();
			}
			else {
				errorTitle = "Error";
				errorMsg = "An unknown error occured.";
			}
			
			JOptionPane.showMessageDialog(pw.getOwner(), errorMsg, errorTitle, JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (Exception e) {
			pw.dispose();
			JOptionPane.showMessageDialog(pw.getOwner(),
					"An unkown error occured.", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		pw.done();
		JOptionPane.showMessageDialog(pw,
				"The model is successfully transformed and saved.", "Success!",
				JOptionPane.INFORMATION_MESSAGE);
		pw.dispose();
	}

}
