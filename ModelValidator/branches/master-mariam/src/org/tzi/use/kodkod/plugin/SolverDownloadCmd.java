package org.tzi.use.kodkod.plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.KodkodModelValidatorConfiguration;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.helper.SolverLibraryHelper;
import org.tzi.kodkod.helper.SolverLibraryHelper.SolverInstallResult;
import org.tzi.kodkod.helper.SystemInformation;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;
import org.tzi.use.util.StringUtil;

/**
 * Cmd-Class for automatically downloading appropriate solvers and installing
 * them to the correct folders.
 * 
 * @author Frank Hilken
 */
public class SolverDownloadCmd implements IPluginShellCmdDelegate {

	protected Shell useShell = null;
	
	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {

		useShell = pluginCommand.getShell();
		SystemInformation si = SystemInformation.getSystemInformation();
		SolverInstallResult solvers;
		
		useShell.getOut().println("Downloading solver libraries (this may take a while) ...");
		try {
			solvers = SolverLibraryHelper.downloadAndExtractSolversForSystem(si);
		} catch (IOException e) {
			useShell.getErr().println("Error while downloading solvers.");
			useShell.getErr().println(e.getMessage());
			return;
		}
		
		for(String solver : solvers.getInstalledSolvers()) {
			useShell.getOut().println("Installed: " + StringUtil.inQuotes(solver));
		}
		for(String solver : solvers.getFailedSolvers()) {
			useShell.getOut().println("Failed to install: " + StringUtil.inQuotes(solver));
		}
		for(String solver : solvers.getAlreadyInstalledSolvers()) {
			useShell.getOut().println("Was already installed: " + StringUtil.inQuotes(solver));
		}
		
		if(solvers.getInstalledSolvers().length == 0){
			useShell.getOut().println("No new solver libraries were installed.");
			return;
		}
		
		final KodkodModelValidatorConfiguration CFG = KodkodModelValidatorConfiguration.getInstance();
		String[] availableSolvers = SolverLibraryHelper.tryAvailableSolvers();
		CFG.setAvailableSolvers(availableSolvers);
		
		useShell.getOut().println(LogMessages.availableSatSolvers(CFG.getAvailableSolvers()));
		
		List<String> solverList = Arrays.asList(availableSolvers);
		boolean newSolverSet = false;
		
		switch (si) {
		case UNIX_64BIT:
		case UNIX_32BIT:
			// try Lingeling
			if(!newSolverSet && solverList.contains(KodkodModelValidatorConfiguration.LINGELING_NAME)){
				newSolverSet = trySetSatSolver(KodkodModelValidatorConfiguration.LINGELING_NAME);
			}
			// try MiniSat
			if(!newSolverSet && solverList.contains(KodkodModelValidatorConfiguration.MINISAT_NAME)){
				newSolverSet = trySetSatSolver(KodkodModelValidatorConfiguration.MINISAT_NAME);
			}
			break;
		case WINDOWS_64BIT:
		case WINDOWS_32BIT:
			// try MiniSat
			if(!newSolverSet && solverList.contains(KodkodModelValidatorConfiguration.MINISAT_NAME)){
				newSolverSet = trySetSatSolver(KodkodModelValidatorConfiguration.MINISAT_NAME);
			}
			break;
		default:
			break;
		}
		
		// set better solver as default by saving
		if(newSolverSet){
			try {
				CFG.saveFile();
				useShell.getOut().println("Saved "
								+ StringUtil.inQuotes(CFG.satFactory().toString())
								+ " as default solver.");
			} catch (ConfigurationException e) {
				// sadly could not save as default
			}
		}
		
		/*
		 * TODO System.loadLibrary() ignores subsequent calls for the same lib.
		 * Since SolverLibraryHelper.tryAvailableSolvers() loads all solvers
		 * once, they cannot be re-tested after the download. Maybe we can hack
		 * that? Until then the workaround is this message.
		 */
		useShell.getOut().println("Some solvers might only be available after a restart of USE.");
	}
	
	private boolean trySetSatSolver(String solvername){
		try {
			KodkodModelValidatorConfiguration.getInstance().setSatFactory(solvername);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
