package org.tzi.kodkod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import kodkod.engine.satlab.SATFactory;

import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LibraryPathHelper;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.helper.PathHelper;
import org.tzi.use.kodkod.transform.enrich.ModelEnricher;
import org.tzi.use.kodkod.transform.enrich.NullModelEnricher;
import org.tzi.use.kodkod.transform.enrich.ObjectDiagramModelEnricher;

/**
 * Singleton to store the configuration data for the model validator.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum KodkodModelValidatorConfiguration {

	INSTANCE;

	private final Logger LOG = Logger.getLogger(KodkodModelValidatorConfiguration.class);

	private final String FOLDER_NAME = "/modelValidatorPlugin";
	private final String INI_FILENAME = "mv.ini";

	public final String SATSOLVER_KEY = "SatSolver";
	public final String BITWIDTH_KEY = "bitwidth";
	public final String DIAGRAMEXTREACTION_KEY = "AutomaticDiagramExtraction";

	private final SATFactory DEFAULT_SATFACTORY = SATFactory.DefaultSAT4J;
	private final int DEFAULT_BITWIDTH = 8;
	private final boolean DEFAULT_DIAGRAMEXTRACTION = false;

	private SATFactory satFactory = DEFAULT_SATFACTORY;
	private int bitwidth = DEFAULT_BITWIDTH;

	private boolean automaticDiagramExtraction = DEFAULT_DIAGRAMEXTRACTION;

	private File file;
	private boolean read = false;

	private KodkodModelValidatorConfiguration() {
		file = new File(PathHelper.getPluginPath() + FOLDER_NAME, INI_FILENAME);

		extractSolverLibraries();
		addSolverFolders();

		readFile();
	}

	private enum Architecture {
		X86, X64
	}
	
	/**
	 * Adds the folders with the extracted solver libraries to the
	 * 'java.library.path'.
	 */
	private void addSolverFolders() {
		try {
			Architecture arch = getArchitecture();
			switch (arch) {
			case X64:
				LibraryPathHelper.addDirectory(PathHelper.getPluginPath() + FOLDER_NAME + "/x64");
				break;
			case X86:
				LibraryPathHelper.addDirectory(PathHelper.getPluginPath() + FOLDER_NAME + "/x86");
				break;
			default:
				throw new Exception("Unknown jvm architecture.");
			}
		} catch (Exception e) {
			LOG.warn(LogMessages.libraryPathWarning(DEFAULT_SATFACTORY.toString(), e.getMessage()));
		}
	}

	private Architecture getArchitecture() {
		String arch = System.getProperty("os.arch");
		if(arch != null && arch.contains("64")){
			return Architecture.X64;
		}
		else {
			return Architecture.X86;
		}
	}

	/**
	 * Extracts the solver libraries contained in the jar file.
	 */
	private void extractSolverLibraries() {
		try {
			FileSystemManager fsManager = VFS.getManager();
			FileObject jarFile = fsManager.resolveFile("jar:" + PathHelper.getJarFile());

			FileObject source = jarFile.getChild("solver");
			FileObject dest = fsManager.resolveFile("file:" + PathHelper.getPluginPath() + FOLDER_NAME);

			dest.copyFrom(source, Selectors.SELECT_ALL);
		} catch (Exception e) {
			LOG.warn(LogMessages.extractSatSolverWarning(DEFAULT_SATFACTORY.toString()));
		}
	}

	/**
	 * Reads the file with the saved data.
	 */
	public void readFile() {
		if (!read) {
			if (file.exists() && file.canRead()) {
				try {
					HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(file);
					setSatFactory(config.getString(SATSOLVER_KEY, DEFAULT_SATFACTORY.toString()));
					setBitwidth(config.getInt(BITWIDTH_KEY, DEFAULT_BITWIDTH));
					setAutomaticDiagramExtraction(config.getBoolean(DIAGRAMEXTREACTION_KEY, DEFAULT_DIAGRAMEXTRACTION));
				} catch (Exception e) {
					LOG.warn(LogMessages.solverConfigReadWarning(DEFAULT_SATFACTORY.toString(), DEFAULT_BITWIDTH));
					satFactory = DEFAULT_SATFACTORY;
					bitwidth = DEFAULT_BITWIDTH;
				}
			} else {
				setSatFactory(DEFAULT_SATFACTORY.toString());
				setBitwidth(DEFAULT_BITWIDTH);
				setAutomaticDiagramExtraction(DEFAULT_DIAGRAMEXTRACTION);
			}
			read = true;
		}
	}

	/**
	 * Returns the bitwidth.
	 * 
	 * @return
	 */
	public int bitwidth() {
		return bitwidth;
	}

	/**
	 * Returns the SATFactory.
	 * 
	 * @return
	 */
	public SATFactory satFactory() {
		return satFactory;
	}

	/**
	 * Sets the SATFactory to the given name.
	 * 
	 * @param solverName
	 */
	public void setSatFactory(String solverName) {
		Field field;
		try {
			field = SATFactory.class.getField(solverName);
			satFactory = (SATFactory) field.get(null);
			satFactory.instance();

			LOG.info(LogMessages.newSatSolver(satFactory.toString()));
		} catch (Exception e1) {
			LOG.warn(LogMessages.noSatSolverWarning(solverName, DEFAULT_SATFACTORY.toString()));
			satFactory = DEFAULT_SATFACTORY;
		} catch (UnsatisfiedLinkError e2) {
			LOG.error(LogMessages.noSatSolverLibraryError(satFactory.toString(), DEFAULT_SATFACTORY.toString()));
			satFactory = DEFAULT_SATFACTORY;
		}
	}

	/**
	 * Sets the bitwidth.
	 * 
	 * @param bitwidth
	 */
	public void setBitwidth(int bitwidth) {
		if (bitwidth >= 1 && bitwidth <= 32) {
			this.bitwidth = bitwidth;
			LOG.info(LogMessages.newBitwidth(bitwidth));
		} else {
			LOG.warn(LogMessages.wrongBitwidthWarning(DEFAULT_BITWIDTH, bitwidth));
			this.bitwidth = DEFAULT_BITWIDTH;
		}
	}

	/**
	 * Saves the bitwidth and SATFactory.
	 */
	public void save() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(SATSOLVER_KEY + " = " + satFactory.toString());
			writer.newLine();
			writer.write(BITWIDTH_KEY + " = " + bitwidth);
			writer.newLine();
			writer.write(DIAGRAMEXTREACTION_KEY + " = " + automaticDiagramExtraction);
			writer.close();

			read = true;

			LOG.info(LogMessages.solverConfigSaved);
		} catch (Exception e) {
			LOG.error("Error while saving KodkodSolver configuration");
		}
	}
	
	public void setAutomaticDiagramExtraction(boolean automaticDiagramExtraction) {
		this.automaticDiagramExtraction = automaticDiagramExtraction;
		LOG.info(LogMessages.newAutomaticDiagramExtraction(automaticDiagramExtraction));
	}

	public boolean isAutomaticDiagramExtraction() {
		return automaticDiagramExtraction;
	}
	
	public ModelEnricher getModelEnricher() {
		if(automaticDiagramExtraction){
			return new ObjectDiagramModelEnricher();
		}
		return new NullModelEnricher();
	}
}
