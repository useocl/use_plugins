package org.tzi.use.kodkod.plugin;

import java.io.File;

import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.kodkod.UseScrollingKodkodModelValidator;

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
			if (checkValidator()) {
				validator.nextSolution();
			}
		} else if (arguments.equals("previous")) {
			if (checkValidator()) {
				validator.previousSolution();
			}
		} else if (arguments.matches("show\\([1-9]\\d*\\)")) {
			if (checkValidator()) {
				int index = Integer.parseInt(arguments.substring(5, arguments.length()-1));
				validator.showSolution(index);
			}
		} else {
			File file = new File(arguments);

			if (file.exists() && file.canRead() && !file.isDirectory()) {
				extractConfigureAndValidate(file);
			} else {
				LOG.error(LogMessages.pagingCmdError);
			}
		}
	}

	private boolean checkValidator() {
		if (validator == null) {
			LOG.error(LogMessages.pagingCmdFileFirst);
			return false;
		}
		return true;
	}

	@Override
	protected KodkodModelValidator createValidator() {
		validator = new UseScrollingKodkodModelValidator(mSystem);
		return validator;
	}
}
