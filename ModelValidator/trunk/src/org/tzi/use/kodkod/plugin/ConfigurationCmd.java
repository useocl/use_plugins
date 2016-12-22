package org.tzi.use.kodkod.plugin;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.tzi.kodkod.KodkodModelValidatorConfiguration;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.helper.SolverLibraryHelper;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;

import kodkod.engine.satlab.SATFactory;

/**
 * Cmd-Class for the Configuration of the kodkod solver.
 * 
 * @author Hendrik Reitmann
 * @author Frank Hilken
 */
public class ConfigurationCmd implements IPluginShellCmdDelegate {

	protected Shell useShell = null;
	private final KodkodModelValidatorConfiguration CFG = KodkodModelValidatorConfiguration.getInstance();
	
	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {

		useShell = pluginCommand.getShell();
		String arguments = pluginCommand.getCmdArguments();

		if (arguments != null && arguments.length() > 1) {
			arguments = arguments.substring(1);

			for (String argument : arguments.split("\\s*;\\s*")) {
				if (argument.trim().equalsIgnoreCase("save")) {
					saveCFG();
				} else {
					String[] split = argument.split("\\s*:=\\s*");
					if (split.length != 2) {
						useShell.getErr().println(LogMessages.solverConfigSyntaxError(argument));
					} else {
						String name = split[0];
						if (name.equalsIgnoreCase(KodkodModelValidatorConfiguration.SATSOLVER_KEY)) {
							setSatSolver(split[1]);
						} else if (name.equalsIgnoreCase(KodkodModelValidatorConfiguration.BITWIDTH_KEY)) {
							try {
								int value = Integer.parseInt(split[1]);
								setBitwidth(value);
							} catch (NumberFormatException exception) {
								useShell.getErr().println(LogMessages.solverConfigBitwidthError);
							}
						} else if (name.equalsIgnoreCase(KodkodModelValidatorConfiguration.DIAGRAMEXTREACTION_KEY) || name.equalsIgnoreCase(KodkodModelValidatorConfiguration.DIAGRAMEXTREACTION_KEY_SHORT)) {
							if (split[1].equalsIgnoreCase("on") || split[1].equalsIgnoreCase("off")) {
								boolean value = split[1].equalsIgnoreCase("on") ? true : false;
								setAutomaticDiagramExtraction(value);
							} else {
								useShell.getErr().println(LogMessages.configDiagramExtractionError);
							}
						} else if(name.equalsIgnoreCase(KodkodModelValidatorConfiguration.DEBUG_BOUNDS_PRINTOUT_KEY)){
							boolean value = split[1].equalsIgnoreCase("on") ? true : false;
							setDebugBoundsPrint(value);
						} else {
							useShell.getErr().println(LogMessages.solverConfigWrongArgumentError(name));
						}
					}
				}
			}
		} else {
			printInfo();
		}
	}
	
	private void printInfo() {
		SATFactory solver = CFG.satFactory();
		useShell.getOut().println("Current solver: " + (solver == null ? "None" : solver.toString()));
		useShell.getOut().println("Current bitwidth: " + CFG.bitwidth());
		useShell.getOut().println("Automatic extraction of an available object diagram is currently: " + (CFG.isAutomaticDiagramExtraction() ? "enabled" : "disabled"));
	}

	private void saveCFG() {
		try {
			printInfo();
			CFG.saveFile();
			useShell.getOut().println(LogMessages.solverConfigSaved);
		} catch (ConfigurationException e) {
			useShell.getErr().println("Error while saving KodkodSolver configuration");
		}
	}

	private void setSatSolver(String solver) {
		boolean error = false;
		try {
			CFG.setSatFactory(solver);
			useShell.getOut().println(LogMessages.newSatSolver(CFG.satFactory().toString()));
		} catch (FileNotFoundException e) {
			useShell.getOut().println(LogMessages.noSatSolverWarning(solver));
			error = true;
		} catch (IOException e) {
			useShell.getErr().println(LogMessages.noSatSolverLibraryError(solver));
			error = true;
		}
		
		if(error){
			if(CFG.getAvailableSolvers() == null){
				// analyze available solvers
				CFG.setAvailableSolvers(SolverLibraryHelper.tryAvailableSolvers());
			}
			useShell.getOut().println(LogMessages.availableSatSolvers(CFG.getAvailableSolvers()));
			
			if(CFG.satFactory() != null){
				useShell.getOut().println("Staying with solver " + CFG.satFactory().toString() + ".");
			} else {
				// try SAT4J as a fallback
				try {
					CFG.setSatFactory(KodkodModelValidatorConfiguration.DEFAULT_SATFACTORY);
					useShell.getOut().println(LogMessages.defaultSolverLibraryFallback(CFG.satFactory().toString()));
				} catch (IOException e) {
					useShell.getOut().println(LogMessages.NO_SOLVER_SETUP);
				}
			}
		}
	}
	
	private void setBitwidth(int bitwidth) {
		try {
			CFG.setBitwidth(bitwidth);
			useShell.getOut().println(LogMessages.newBitwidth(bitwidth));
		} catch (ConfigurationException e) {
			useShell.getErr().println(LogMessages.WRONG_BITWIDTH_WARNING);
			useShell.getOut().println("Bitwidth is now: " + CFG.bitwidth());
		}
	}
	
	private void setAutomaticDiagramExtraction(boolean on) {
		CFG.setAutomaticDiagramExtraction(on);
		useShell.getOut().println(LogMessages.newAutomaticDiagramExtraction(on));
	}
	
	private void setDebugBoundsPrint(boolean on) {
		CFG.setDebugBoundsPrint(on);
		useShell.getOut().println("Debug bound printout is now: " + (on ? "on":"off"));
	}
	
}
