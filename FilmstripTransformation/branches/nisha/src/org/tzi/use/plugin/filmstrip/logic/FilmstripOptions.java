package org.tzi.use.plugin.filmstrip.logic;

import java.io.File;

import org.tzi.use.uml.mm.MModel;

public class FilmstripOptions {

	private final MModel model;
	private final String modelName;
	private final File savepath;
	private final boolean makeCopy;
	private final boolean transformSoil;
	private final String transformationMethod;
	
	public FilmstripOptions(MModel model, String modelName, File savepath, boolean makeCopy, boolean transformSoil, String transformationMethod) {
		this.model = model;
		this.modelName = modelName;
		this.savepath = savepath;
		this.makeCopy = makeCopy;
		this.transformSoil = transformSoil;
		this.transformationMethod = transformationMethod;
	}

	/**
	 * @return the model
	 */
	public MModel getModel() {
		return model;
	}
	
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return modelName;
	}
	
	/**
	 * @return the savepath
	 */
	public File getSavepath() {
		return savepath;
	}

	/**
	 * @return makeCopy
	 */
	public boolean makeCopy() {
		return makeCopy;
	}

	/**
	 * @return transformSoil
	 */
	public boolean transformSoil() {
		return transformSoil;
	}
	
	/**
	 * @return transformationMethod
	 */
	public String getTransformationMethod() {
		return transformationMethod;
	}
	
}
