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

	private static UseScrollingKodkodModelValidator validator;

	@Override
	protected void noArguments() {
		out.println(LogMessages.pagingCmdError);
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
		} else {
			File file = new File(arguments);

			if (file.exists() && file.canRead() && !file.isDirectory()) {
				extractConfigureAndValidate(file);
			} else {
				out.println(LogMessages.pagingCmdError);
			}
		}
	}

	private boolean checkValidator() {
		if (validator == null) {
			out.println(LogMessages.pagingCmdFileFirst);
			return false;
		}
		return true;
	}

	@Override
	protected KodkodModelValidator createValidator() {
		validator = new UseScrollingKodkodModelValidator(mSystem, out);
		return validator;
	}
}
