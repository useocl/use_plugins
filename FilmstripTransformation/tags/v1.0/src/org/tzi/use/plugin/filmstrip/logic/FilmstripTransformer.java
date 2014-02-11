package org.tzi.use.plugin.filmstrip.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.tzi.use.uml.mm.MMPrintVisitor;
import org.tzi.use.uml.mm.MMVisitor;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.util.Log;

public class FilmstripTransformer {

	private MModel model;

	public FilmstripTransformer(MModel model) {
		this.model = model;
	}

	public MModel transform() throws TransformationInputException,
			TransformationException {
		// validate
		ModelValidator.validate(model);

		// transform
		return FilmstripMMVisitor.transformModel(model);
	}

	public void transformAndSave(File saveFile)
			throws TransformationInputException, TransformationException,
			FileNotFoundException, IOException {
		MModel res = transform();

		// Write result
		FileOutputStream out = new FileOutputStream(saveFile, false);

		try {
			StringWriter sw = new StringWriter();
			MMVisitor v = new MMPrintVisitor(new PrintWriter(sw, true));
			res.processWithVisitor(v);
			String filmstripModel = sw.toString();
			//XXX hack to remove syntactic nonsense
			filmstripModel = filmstripModel.replace(".succ.pred", "");
			out.write(filmstripModel.getBytes());
			out.close();
		}
		finally {
			try {
				out.close();
			}
			catch (IOException e) {
				Log.error("Could not close resource.", e);
			}
		}
	}
	
}
