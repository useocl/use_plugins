package org.tzi.use.kodkod.plugin;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.kodkod.UseScrollingKodkodModelValidator;
import org.tzi.use.main.shell.Shell;

/**
 * Cmd-Class for the scrolling in the solutions.
 * 
 * @author Hendrik Reitmann
 * @author Frank Hilken
 */
public class KodkodScrollingValidateCmd extends KodkodValidateCmd {

	protected static UseScrollingKodkodModelValidator validator = null;

	@Override
	protected void noArguments() {
		LOG.info(LogMessages.pagingCmdError);
	}

	@Override
	protected void handleArguments(String arguments) {
		arguments = arguments.trim();
		
		if (arguments.equalsIgnoreCase("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (arguments.equalsIgnoreCase("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else {
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m = showPattern.matcher(arguments);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					int index = Integer.parseInt(m.group(1));
					validator.showSolution(index);
				}
			} else {
				String fileToOpen = Shell.getInstance().getFilenameToOpen(arguments, false);
				File file = new File(fileToOpen);
	
				if (file.exists() && file.canRead() && !file.isDirectory()) {
					resetValidator();
					extractConfigureAndValidate(file);
				} else {
					LOG.error(LogMessages.pagingCmdError);
				}
			}
		}
	}

	protected void resetValidator() {
		validator = new UseScrollingKodkodModelValidator(session);
	}

	protected boolean checkValidatorPresent() {
		if (validator == null) {
			LOG.error(LogMessages.pagingCmdFileFirst);
			return false;
		}
		return true;
	}

	@Override
	protected UseScrollingKodkodModelValidator createValidator() {
		return validator;
	}
}
