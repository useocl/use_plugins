package org.tzi.use.plugin.filmstrip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import org.tzi.use.config.Options;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.plugin.filmstrip.gui.ErrorFormatter;
import org.tzi.use.plugin.filmstrip.logic.FilmstripTransformer;
import org.tzi.use.plugin.filmstrip.logic.TransformationException;
import org.tzi.use.plugin.filmstrip.logic.TransformationInputException;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.util.Log;

public class TransformIntoFilmstripCommand implements IPluginShellCmdDelegate {

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {
		PrintStream out = Log.out();
		String fileName = pluginCommand.getCmdArguments().trim();
		
		out.println("Transforming model ...");
		File saveFile = new File(Options.getLastDirectory().toFile(), fileName);
		
		FilmstripTransformer ft = new FilmstripTransformer(pluginCommand.getSession().system().model());
		try {
			ft.transformAndSave(saveFile);
		}
		catch (TransformationInputException e) {
			out.println("The input model contains elements with reserved names.");
			out.println();
			out.println(ErrorFormatter.formatInputElementErrors(e.getErrors()));
			out.println();
			out.println("The transformation is not possible until the problems are resolved.");
			return;
		}
		catch(TransformationException e){
			out.println("An error occured while transforming the model.");
			out.println(e.getMessage());
			return;
		} catch (FileNotFoundException e) {
			out.println("Could not open outputfile.");
			out.println(e.getMessage());
			return;
		} catch (IOException e) {
			out.println("Could not write outputfile.");
			out.println(e.getMessage());
			return;
		}
		
		out.println("Done.");
	}

}
