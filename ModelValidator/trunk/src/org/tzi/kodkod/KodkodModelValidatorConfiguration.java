package org.tzi.kodkod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;
import java.util.Set;

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
import org.tzi.kodkod.helper.SystemInformation;
import org.tzi.use.kodkod.transform.enrich.ModelEnricher;
import org.tzi.use.kodkod.transform.enrich.NullModelEnricher;
import org.tzi.use.kodkod.transform.enrich.ObjectDiagramModelEnricher;
import org.tzi.use.util.StringUtil;

import com.google.common.collect.ImmutableMap;

import kodkod.engine.satlab.SATAbortedException;
import kodkod.engine.satlab.SATFactory;

/**
 * Singleton to store the configuration data for the model validator.
 * 
 * @author Hendrik Reitmann
 * @author Frank Hilken
 */
public class KodkodModelValidatorConfiguration {

	private static final Logger LOG = Logger.getLogger(KodkodModelValidatorConfiguration.class);

	public static final String FOLDER_NAME = "/modelValidatorPlugin";
	public static final String INI_FILENAME = "mv.ini";

	public static final String SATSOLVER_KEY = "SatSolver";
	public static final String BITWIDTH_KEY = "bitwidth";
	public static final String DIAGRAMEXTREACTION_KEY = "AutomaticDiagramExtraction";
	public static final String DIAGRAMEXTREACTION_KEY_SHORT = "objExtraction";
	public static final String DEBUG_BOUNDS_PRINTOUT_KEY = "dBoundPrintout";
	
	public static final String DEFAULT_SATFACTORY = "DefaultSAT4J";
	public static final int DEFAULT_BITWIDTH = 8;
	public static final boolean DEFAULT_DIAGRAMEXTRACTION = false;

	private SATFactory satFactory = null;
	private int bitwidth = DEFAULT_BITWIDTH;
	private boolean automaticDiagramExtraction = DEFAULT_DIAGRAMEXTRACTION;
	private boolean debugBoundsPrint = false;

	public static final Map<String, String> SOLVER_MAP = ImmutableMap.<String, String>builder()
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
	
	private final File iniSaveFile;
	private boolean read = false;

	private static final KodkodModelValidatorConfiguration INSTANCE = new KodkodModelValidatorConfiguration();
	private KodkodModelValidatorConfiguration() {
		iniSaveFile = new File(PathHelper.getPluginPath() + FOLDER_NAME, INI_FILENAME);

		LOG.info("Use `modelvalidator -downloadSolvers' to automatically download and install additional solver libraries.");
		
		extractSolverLibraries();
		addSolverFolders();

		readFile();
	}

	/**
	 * Adds the folders with the extracted solver libraries to the
	 * 'java.library.path'.
	 */
	private void addSolverFolders() {
		try {
			SystemInformation si = SystemInformation.getSystemInformation();
			String path = getSolverFolder(si);
			
			if(path == null){
				throw new IOException("Unknown jvm architecture.");
			} else {
				LibraryPathHelper.addDirectory(path);
			}
		} catch (IOException e) {
			LOG.warn(LogMessages.libraryPathWarning(DEFAULT_SATFACTORY, e.getMessage()));
		}
	}
	
	/**
	 * Returns the folder used for storing solver libraries for the given
	 * system.
	 */
	public static String getSolverFolder(SystemInformation si){
		String basePath = PathHelper.getPluginPath() + FOLDER_NAME;
		
		switch (si) {
		case UNIX_64BIT:
		case WINDOWS_64BIT:
			return basePath + "/x64";
		case UNIX_32BIT:
		case WINDOWS_32BIT:
			return basePath + "/x86";
		default:
			return null;
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
	private void readFile() {
		if (!read) {
			if (iniSaveFile.exists() && iniSaveFile.canRead()) {
				try {
					HierarchicalINIConfiguration config = new HierarchicalINIConfiguration(iniSaveFile);
					try {
						setSatFactory(config.getString(SATSOLVER_KEY, DEFAULT_SATFACTORY));
					} catch (IOException e) {
						try {
							setSatFactory(DEFAULT_SATFACTORY);
						} catch (IOException e1) {
							// cannot load any solver
						}
					}
					setBitwidth(config.getInt(BITWIDTH_KEY, DEFAULT_BITWIDTH));
					setAutomaticDiagramExtraction(config.getBoolean(DIAGRAMEXTREACTION_KEY, DEFAULT_DIAGRAMEXTRACTION));
				} catch (ConfigurationException e) {
					// error while loading user config file
					// stick with default values
				}
			} else {
				try {
					setSatFactory(DEFAULT_SATFACTORY);
				} catch (IOException e) {
				}
				bitwidth = DEFAULT_BITWIDTH;
				setAutomaticDiagramExtraction(DEFAULT_DIAGRAMEXTRACTION);
			}
			read = true;
		}
	}

	/**
	 * Saves the bitwidth and SATFactory.
	 */
	public void saveFile() throws ConfigurationException {
		HierarchicalINIConfiguration iniFile = new HierarchicalINIConfiguration();
		
		if(satFactory != null){
			iniFile.setProperty(SATSOLVER_KEY, satFactory.toString());
		}
		iniFile.setProperty(BITWIDTH_KEY, bitwidth);
		iniFile.setProperty(DIAGRAMEXTREACTION_KEY, automaticDiagramExtraction);
		
		iniFile.save(iniSaveFile);
		read = true;
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
	public void setSatFactory(String solverName) throws FileNotFoundException, IOException {
		
		if(solverName == null){
			satFactory = null;
			return;
		}
		
		boolean notFound = false;
		boolean cantLoad = false;
		
		try {
			if(solverName.equalsIgnoreCase(PLINGELING_SOLVERNAME)){
				
				File plingeling = new File(PathHelper.getPluginPath() + FOLDER_NAME + File.separatorChar + PLINGELING_SOLVERNAME);
				if(plingeling.exists()){
					Path solverPath = plingeling.toPath();
					if(!Files.isExecutable(solverPath)){
						// try to make the file executable
						try {
							Set<PosixFilePermission> perms = Files.getPosixFilePermissions(solverPath);
							if(perms.contains(PosixFilePermission.OWNER_READ)){
								perms.add(PosixFilePermission.OWNER_EXECUTE);
							}
							if(perms.contains(PosixFilePermission.GROUP_READ)){
								perms.add(PosixFilePermission.GROUP_EXECUTE);
							}
							if(perms.contains(PosixFilePermission.OTHERS_READ)){
								perms.add(PosixFilePermission.OTHERS_EXECUTE);
							}
							Files.setPosixFilePermissions(solverPath, perms);
						} catch (IOException e) {
							// we tried
							cantLoad = true;
						}
					}
					
					if(!cantLoad){
						satFactory = SATFactory.plingeling();
						satFactory.instance();
					}
				} else {
					notFound = true;
				}
			} else {
				String kodkodSolverName = SOLVER_MAP.get(solverName.toLowerCase());
				if(kodkodSolverName != null){
					Field field = SATFactory.class.getField(kodkodSolverName);
					satFactory = (SATFactory) field.get(null);
					satFactory.instance();
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
			throw new FileNotFoundException("Could not find solver library for " + StringUtil.inQuotes(solverName));
		} else if(cantLoad){
			throw new IOException("Could not load solver library " + StringUtil.inQuotes(solverName));
		}
	}
	
	/**
	 * Returns the bitwidth.
	 */
	public int bitwidth() {
		return bitwidth;
	}

	/**
	 * Sets the bitwidth.
	 */
	public void setBitwidth(int bitwidth) throws ConfigurationException {
		if (bitwidth >= 1 && bitwidth <= 32) {
			this.bitwidth = bitwidth;
		} else {
			throw new ConfigurationException("Invalid bitwidth range.");
		}
	}

	public boolean isAutomaticDiagramExtraction() {
		return automaticDiagramExtraction;
	}
	
	public void setAutomaticDiagramExtraction(boolean automaticDiagramExtraction) {
		this.automaticDiagramExtraction = automaticDiagramExtraction;
	}
	
	public boolean isDebugBoundsPrint() {
		return debugBoundsPrint;
	}
	
	public void setDebugBoundsPrint(boolean debugBoundsPrint) {
		this.debugBoundsPrint = debugBoundsPrint;
	}
	
	public String[] getAvailableSolvers() {
		return availableSolvers;
	}
	
	public void setAvailableSolvers(String[] availableSolvers) {
		this.availableSolvers = availableSolvers;
	}
	
	public ModelEnricher getModelEnricher() {
		if(automaticDiagramExtraction){
			return new ObjectDiagramModelEnricher();
		}
		return new NullModelEnricher();
	}
	
	/**
	 * Calculates the number of bits required to represent the given values in
	 * two complement encoding.
	 */
	public static int calculateRequiredBitwidth(int value) {
		if(value < 0){
			value = Math.abs(value)-1;
		}
		// +1 for twos complement
		return (32 - Integer.numberOfLeadingZeros(value))+1;
	}
	
	public static KodkodModelValidatorConfiguration getInstance() {
		return INSTANCE;
	}
	
}
