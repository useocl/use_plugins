package org.tzi.use.plugin.filmstrip.logic;

import java.io.File;

import org.tzi.use.uml.mm.MModel;

public class FilmstripOptions {

	private MModel model;
	private String modelName;
	private File savepath;
	private boolean makeCopy;
	private boolean transformSoil;
	
	public FilmstripOptions(MModel model, String modelName, File savepath, boolean makeCopy, boolean transformSoil) {
		this.model = model;
		this.modelName = modelName;
		this.savepath = savepath;
		this.makeCopy = makeCopy;
		this.transformSoil = transformSoil;
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
	
}
