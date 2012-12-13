package org.tzi.use.kodkod.plugin;

import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodSolverConfiguration;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.main.shell.runtime.IPluginShellCmd;
import org.tzi.use.runtime.shell.IPluginShellCmdDelegate;

/**
 * Cmd-Class for the configuration of the kodkod solver.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class SolverConfigurationCmd implements IPluginShellCmdDelegate {

	private static final Logger LOG = Logger.getLogger(SolverConfigurationCmd.class);

	@Override
	public void performCommand(IPluginShellCmd pluginCommand) {

		KodkodSolverConfiguration configuration = KodkodSolverConfiguration.INSTANCE;
		String arguments = pluginCommand.getCmdArguments();

		if (arguments != null && arguments.length() > 1) {
			arguments = arguments.substring(1);

			for (String argument : arguments.split(";")) {
				if (argument.trim().equalsIgnoreCase("save")) {
					configuration.save();
				} else {
					String[] split = argument.split("\\s*:=\\s*");
					if (split.length != 2) {
						LOG.error(LogMessages.solverConfigSyntaxError(argument));
					} else {
						String name = split[0];
						if (name.equalsIgnoreCase(configuration.SATSOLVER_KEY)) {
							configuration.setSatFactory(split[1]);

						} else if (name.equalsIgnoreCase(configuration.BITWIDTH_KEY)) {
							try {
								int value = Integer.parseInt(split[1]);
								configuration.setBitwidth(value);

							} catch (NumberFormatException exception) {
								LOG.error(LogMessages.solverConfigBitwidthError);
							}
						} else {
							LOG.error(LogMessages.solverConfigWrongArgumentError(name));
						}
					}
				}
			}
		}
	}
}
