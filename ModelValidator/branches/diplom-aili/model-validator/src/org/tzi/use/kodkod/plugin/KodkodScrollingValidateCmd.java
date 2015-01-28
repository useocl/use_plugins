package org.tzi.use.kodkod.plugin;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.config.Options;
import org.tzi.use.kodkod.UseScrollingKodkodModelValidator;
import org.tzi.use.main.shell.Shell;

/**
 * Cmd-Class for the scrolling in the solutions.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class KodkodScrollingValidateCmd extends KodkodValidateCmd {

	protected static UseScrollingKodkodModelValidator validator;

	@Override
	protected void noArguments() {
		LOG.info(LogMessages.pagingCmdError);
	}

	@Override
	protected void handleArguments(String arguments) {
		arguments = arguments.trim();
		
		if (arguments.equals("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (arguments.equals("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else {
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)");
			Matcher m = showPattern.matcher(arguments);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					int index = Integer.parseInt(m.group(1));
					validator.showSolution(index);
				}
			} else {
				String fileToOpen = Shell.getInstance().getFilenameToOpen(arguments, false);
				fileToOpen = Options.getFilenameToOpen(fileToOpen);
				File file = new File(fileToOpen);
	
				if (file.exists() && file.canRead() && !file.isDirectory()) {
					extractConfigureAndValidate(file);
				} else {
					LOG.error(LogMessages.pagingCmdError);
				}
			}
		}
	}

	private boolean checkValidatorPresent() {
		if (validator == null) {
			LOG.error(LogMessages.pagingCmdFileFirst);
			return false;
		}
		return true;
	}

	@Override
	protected KodkodModelValidator createValidator() {
		validator = new UseScrollingKodkodModelValidator(session);
		return validator;
	}
}
