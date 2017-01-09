package org.tzi.kodkod.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.tzi.kodkod.KodkodModelValidatorConfiguration;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;

public final class SolverLibraryHelper {

	/*
	 * Unzipping is limited to at most 10 files of 10 mb each to prevent abuse.
	 * Files that are too large and directories count towards the limit of
	 * 10 files.
	 */
	private static final int MAX_DOWNLOADED_FILES = 10;
	private static final int MAX_DOWNLOADED_INDIVIDUAL_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

	private static final String SOLVER_BASE_URL = "http://www.db.informatik.uni-bremen.de/kodkod-solvers/1/";
	private static final Map<SystemInformation, String> SOLVER_LIBRARY = ImmutableMap.<SystemInformation, String>builder()
			.put(SystemInformation.UNIX_32BIT, "linux_x86_32.zip")
			.put(SystemInformation.UNIX_64BIT, "linux_x86_64.zip")
			.put(SystemInformation.WINDOWS_64BIT, "win_x86_64.zip")
			.build();
	
	private SolverLibraryHelper() {
	}
	
	public static SolverInstallResult downloadAndExtractSolversForSystem(SystemInformation si) throws IOException {
		String filename = SOLVER_LIBRARY.get(si);
		String solverPath = KodkodModelValidatorConfiguration.getSolverFolder(si);
		
		if(filename == null || solverPath == null){
			throw new FileNotFoundException("No external solver libraries available for the system.");
		}
		
		return downloadAndExtractZipFile(si, SOLVER_BASE_URL + filename, solverPath);
	}
	
	private static SolverInstallResult downloadAndExtractZipFile(SystemInformation si, String url, String destFolder) throws IOException {
		
		final SolverInstallResult res = new SolverInstallResult();

		try(ZipInputStream zipStream = new ZipInputStream(new URL(url).openStream())) {
			int numFiles = 0;
			
			ZipEntry entry;
			while((entry = zipStream.getNextEntry()) != null && numFiles < MAX_DOWNLOADED_FILES){
				numFiles++;
				if(entry.isDirectory()){
					continue;
				}
				
				long size = entry.getSize();
				
				if(size > MAX_DOWNLOADED_INDIVIDUAL_FILE_SIZE){
					continue;
				}
				
				String fileLocation = destFolder + File.separatorChar + entry.getName();
				File outFile = new File(fileLocation);
				
				if(!outFile.exists()){
					if(!outFile.getParentFile().isDirectory()){
						outFile.getParentFile().mkdirs();
					}
					
					try(FileOutputStream outStream = new FileOutputStream(outFile)){
						ByteStreams.copy(zipStream, outStream);
						res.addInstalledSolver(entry.getName());
					} catch(IOException ex){
						res.addFailedSolver(entry.getName());
					}
				} else {
					res.addAlreadyInstalledSolver(entry.getName());
				}
			}
		}
		
		return res;
	}
	
	public static class SolverInstallResult {
		private final List<String> installed;
		private final List<String> failed;
		private final List<String> alreadyInstalled;
		
		public SolverInstallResult() {
			installed = new LinkedList<String>();
			failed = new LinkedList<String>();
			alreadyInstalled = new LinkedList<String>();
		}
		
		public void addInstalledSolver(String solver) {
			installed.add(solver);
		}
		
		public void addFailedSolver(String solver) {
			failed.add(solver);
		}
		
		public void addAlreadyInstalledSolver(String solver) {
			alreadyInstalled.add(solver);
		}
		
		public String[] getInstalledSolvers() {
			return installed.toArray(new String[installed.size()]);
		}
		
		public String[] getFailedSolvers() {
			return failed.toArray(new String[failed.size()]);
		}
		
		public String[] getAlreadyInstalledSolvers() {
			return alreadyInstalled.toArray(new String[alreadyInstalled.size()]);
		}
	}
	
	public static String[] tryAvailableSolvers(){
		Set<String> res = new LinkedHashSet<String>();
		String currentSolver = KodkodModelValidatorConfiguration.getInstance().satFactory().toString();
		
		Set<String> solverList = new LinkedHashSet<String>(KodkodModelValidatorConfiguration.SOLVER_MAP.values());
		solverList.add(KodkodModelValidatorConfiguration.PLINGELING_NAME);
		
		for(String solver : solverList){
			try {
				KodkodModelValidatorConfiguration.getInstance().setSatFactory(solver);
				// if no error occurred, add solver to the list
				res.add(solver);
			} catch (IOException e) {
				// if an error occurs, solver is not available
			}
		}
		
		// reset to original solver
		try {
			KodkodModelValidatorConfiguration.getInstance().setSatFactory(currentSolver);
		} catch (IOException e) {
			// reseting failed, oopsie
		}
		
		return res.toArray(new String[res.size()]);
	}
	
}
