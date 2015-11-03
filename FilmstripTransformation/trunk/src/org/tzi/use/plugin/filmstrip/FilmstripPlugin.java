package org.tzi.use.plugin.filmstrip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.tzi.use.parser.use.USECompiler;
import org.tzi.use.plugin.filmstrip.gui.ErrorFormatter;
import org.tzi.use.plugin.filmstrip.logic.FilmstripMMVisitor;
import org.tzi.use.plugin.filmstrip.logic.FilmstripOptions;
import org.tzi.use.plugin.filmstrip.logic.FilmstripTransformer;
import org.tzi.use.plugin.filmstrip.logic.TransformationException;
import org.tzi.use.plugin.filmstrip.logic.TransformationInputException;
import org.tzi.use.plugin.filmstrip.logic.TransformationInputException.ModelElements;
import org.tzi.use.runtime.impl.Plugin;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.ModelFactory;

public class FilmstripPlugin extends Plugin {

	protected final String PLUGIN_ID = "FilmstripPlugin";
	
	@Override
	public String getName() {
		return PLUGIN_ID;
	}
	
	public static void main(String[] args) {
		if(args.length < 2){
			System.err.println("Usage: java inputModel.use outputPath.use");
			System.exit(1);
			return;
		}
		String inputFileName = args[0];
		String outputFileName = args[1];
		
		File inputFile = new File(inputFileName);
		File outputFile = new File(System.getProperty("user.dir"), outputFileName);
		
		MModel model;
		try (FileInputStream inStream = new FileInputStream(inputFile)) {
			model = USECompiler.compileSpecification(inStream, inputFile.getName(),
					new PrintWriter(System.err), new ModelFactory());
		} catch (IOException e) {
			System.err.println("Could not open inputfile. " + e.getMessage());
			System.exit(1);
			return;
		}
		
		if(model == null){
			System.err.println("Could not read input model.");
			System.exit(1);
			return;
		}
		
		FilmstripOptions options = new FilmstripOptions(model, model.name(), outputFile, false, false, FilmstripMMVisitor.NAME);
		FilmstripTransformer ft = new FilmstripTransformer(options);
		try {
			ft.transformAndSave();
		} catch (TransformationException e) {
			System.err.println("An error occured while transforming the model.\n" + e.getMessage());
			System.exit(1);
			return;
		} catch (TransformationInputException e) {
			System.err.println("The input model contains elements with reserved names.");
			
			// print malicious elements
			ModelElements elems = e.getErrors();
			System.err.println(ErrorFormatter.formatInputElementErrors(elems));
			
			System.exit(1);
			return;
		} catch (FileNotFoundException e) {
			System.err.println("Could not open outputfile.\n" + e.getMessage());
			System.exit(1);
			return;
		} catch (IOException e) {
			System.err.println("Could not write outputfile.\n" + e.getMessage());
			System.exit(1);
			return;
		}
		
		System.out.println("Successfully transformed and saved the model.");
	}
	
}
