package org.tzi.use.plugin.filmstrip.logic;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.tzi.use.api.UseApiException;
import org.tzi.use.plugin.filmstrip.logic.addon.CopyOperation;
import org.tzi.use.plugin.filmstrip.logic.addon.CopySOILTransformer;
import org.tzi.use.uml.mm.MMPrintVisitor;
import org.tzi.use.uml.mm.MMVisitor;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.util.Log;

public class FilmstripTransformer {

	private FilmstripOptions options;

	public FilmstripTransformer(FilmstripOptions options) {
		this.options = options;
	}

	public MModel transform() throws TransformationInputException,
			TransformationException {
		// validate
		ModelValidator.validate(options.getModel());

		// transform
		MModel res;
		if(FilmstripMMVisitor.NAME.equals(options.getTransformationMethod())){
			res = FilmstripMMVisitor.transformModel(options.getModel(), options.getModelName());
		}
		else if(FilmstripMVCompatibleVisitor.NAME.equals(options.getTransformationMethod())){
			res = FilmstripMVCompatibleVisitor.transformModel(options.getModel(), options.getModelName());
		}
		
		else if(FilmstripMVCOpCSnapAssocVisitor.NAME.equals(options.getTransformationMethod())){
			res = FilmstripMVCOpCSnapAssocVisitor.transformModel(options.getModel(), options.getModelName());
		}
		else {
			throw new TransformationException("No valid transformation method chosen");
		}
		
		//TODO replace with addon API
		if(FilmstripMMVisitor.NAME.equals(options.getTransformationMethod())){
			if(options.makeCopy()){
				// add operation to copy a snapshot 1:1
				try {
					CopyOperation co = new CopyOperation(res);
					co.createAndAddCopyOperation();
				}
				catch(UnsupportedOperationException | UseApiException ex){
					//TODO
					ex.printStackTrace();
					Log.warn("Snapshot::copy() only supports binary associations. No operation was added.");
				}
				
				if(options.transformSoil()){
					try {
						CopySOILTransformer cst = new CopySOILTransformer(res);
						cst.transformSoilOperations();
					}
					catch(Exception ex){
						//TODO
						ex.printStackTrace();
						Log.warn("Something went wrong with editing the SOIL ops.");
					}
				}
			}
		}
		
		return res;
	}

	public void transformAndSave()
			throws TransformationInputException, TransformationException,
			FileNotFoundException, IOException {
		if(options.getSavepath() == null){
			throw new IOException("No filepath given.");
		}
		
		MModel res = transform();
		
		// Write result
		try (FileOutputStream out = new FileOutputStream(options.getSavepath(), false)) {
			StringWriter sw = new StringWriter();
			MMVisitor v = new MMPrintVisitor(new PrintWriter(sw, true));
			res.processWithVisitor(v);
			String filmstripModel = sw.toString();
			//XXX hack to remove syntactic nonsense
			filmstripModel = filmstripModel.replace(".succ.pred", "");
			out.write(filmstripModel.getBytes());
			out.close();
		}
	}
	
}
