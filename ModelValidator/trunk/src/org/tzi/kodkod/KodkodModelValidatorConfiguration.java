package org.tzi.kodkod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import kodkod.engine.satlab.SATAbortedException;
import kodkod.engine.satlab.SATFactory;

import org.apache.commons.configuration.ConfigurationException;
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

import com.google.common.collect.ImmutableMap;

/**
 * Singleton to store the configuration data for the model validator.
 * 
 * @author Hendrik Reitmann
 */
public enum KodkodModelValidatorConfiguration {

	INSTANCE;

	private final Logger LOG = Logger.getLogger(KodkodModelValidatorConfiguration.class);

	private final String FOLDER_NAME = "/modelValidatorPlugin";
	private final String INI_FILENAME = "mv.ini";

	public final String SATSOLVER_KEY = "SatSolver";
	public final String BITWIDTH_KEY = "bitwidth";
	public final String DIAGRAMEXTREACTION_KEY = "AutomaticDiagramExtraction";
	public final String DIAGRAMEXTREACTION_KEY_SHORT = "objExtraction";
	public final String DEBUG_BOUNDS_PRINTOUT_KEY = "dBoundPrintout";

	private final SATFactory DEFAULT_SATFACTORY = SATFactory.DefaultSAT4J;
	private final int DEFAULT_BITWIDTH = 8;
	private final boolean DEFAULT_DIAGRAMEXTRACTION = false;

	private SATFactory satFactory = DEFAULT_SATFACTORY;
	private int bitwidth = DEFAULT_BITWIDTH;

	private boolean automaticDiagramExtraction = DEFAULT_DIAGRAMEXTRACTION;
	private boolean debugBoundsPrint = false;

	private final Map<String, String> SOLVER_MAP = ImmutableMap.<String, String>builder()
			.put("defaultsat4j", "DefaultSAT4J")
			.put("lightsat4j", "LightSAT4J")
			.put("lingeling", "Lingeling")
			.put("minisat", "MiniSat")
			.put("minisatprover", "MiniSatProver")
			.put("cryptominisat", "CryptoMiniSat")
			.put("zchaffmincost", "ZChaffMincost")
			.build();
	public static final String PLINGELING_SOLVERNAME = "plingeling";
	private String[] availableSolvers = null;
	
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
				throw new IOException("Unknown jvm architecture.");
			}
		} catch (IOException e) {
			LOG.warn(LogMessages.libraryPathWarning(DEFAULT_SATFACTORY.toString(), e.getMessage()));
		}
	}

	private Architecture getArchitecture() {
		String arch = System.getProperty("os.arch");
		if(arch != null && arch.contains("64")){
			return Architecture.X64;
		} else {
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
		} catch (IOException e) {
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
				} catch (ConfigurationException e) {
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
	 */
	public void setSatFactory(String solverName) {
		boolean notFound = false;
		boolean cantLoad = false;
		
		try {
			if(solverName.equalsIgnoreCase(PLINGELING_SOLVERNAME)){
				satFactory = SATFactory.plingeling();
				satFactory.instance();
				
				LOG.info(LogMessages.newSatSolver(satFactory.toString()));
			} else {
				String kodkodSolverName = SOLVER_MAP.get(solverName.toLowerCase());
				if(kodkodSolverName != null){
					Field field = SATFactory.class.getField(kodkodSolverName);
					satFactory = (SATFactory) field.get(null);
					satFactory.instance();
					
					LOG.info(LogMessages.newSatSolver(satFactory.toString()));
				} else {
					notFound = true;
				}
			}
		} catch (NoSuchFieldException | NoClassDefFoundError | SecurityException | IllegalAccessException e) {
			notFound = true;
		} catch (UnsatisfiedLinkError | SATAbortedException e2) {
			cantLoad = true;
		}
		
		if(notFound){
			LOG.warn(LogMessages.noSatSolverWarning(solverName, DEFAULT_SATFACTORY.toString()));
		} else if(cantLoad){
			LOG.error(LogMessages.noSatSolverLibraryError(solverName, DEFAULT_SATFACTORY.toString()));
		}
		if(notFound || cantLoad){
			errorAndPrint();
		}
	}
	
	private void errorAndPrint(){
		satFactory = DEFAULT_SATFACTORY;
		
		if(availableSolvers == null){
			// analyze available solvers
			availableSolvers = tryAvailableSolvers();
		}
		
		LOG.info(LogMessages.availableSatSolvers(availableSolvers));
	}
	
	private String[] tryAvailableSolvers(){
		Set<String> res = new LinkedHashSet<String>();
		
		for(String solver : SOLVER_MAP.values()){
			try {
				((SATFactory) SATFactory.class.getField(solver).get(null)).instance();
				// if no error occurred, add solver to the list
				res.add(solver);
			} catch(NoSuchFieldException | NoClassDefFoundError | SecurityException | IllegalAccessException | UnsatisfiedLinkError e){
				// if an error occurs, solver is not available
			}
		}
		
		// try plingeling extra due to different instantiation
		try {
			SATFactory.plingeling().instance();
			res.add(PLINGELING_SOLVERNAME);
		} catch(SATAbortedException | NoClassDefFoundError | SecurityException | UnsatisfiedLinkError ex){
			// if an error occurs, solver is not available
		}
		
		return res.toArray(new String[res.size()]);
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
			LOG.warn(LogMessages.wrongBitwidthWarning(DEFAULT_BITWIDTH));
			this.bitwidth = DEFAULT_BITWIDTH;
		}
	}
	
	public void setDebugBoundsPrint(boolean debugBoundsPrint) {
		this.debugBoundsPrint = debugBoundsPrint;
		LOG.info("Debug bound printout is now " + (debugBoundsPrint?"on":"off"));
	}
	
	public boolean isDebugBoundsPrint() {
		return debugBoundsPrint;
	}

	/**
	 * Saves the bitwidth and SATFactory.
	 */
	public void save() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
			writer.write(SATSOLVER_KEY + " = " + satFactory.toString());
			writer.newLine();
			writer.write(BITWIDTH_KEY + " = " + bitwidth);
			writer.newLine();
			writer.write(DIAGRAMEXTREACTION_KEY + " = " + automaticDiagramExtraction);
			writer.close();

			read = true;

			LOG.info(LogMessages.solverConfigSaved);
		} catch (IOException e) {
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
