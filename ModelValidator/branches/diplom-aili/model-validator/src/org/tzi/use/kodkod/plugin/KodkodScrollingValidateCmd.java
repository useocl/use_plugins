package org.tzi.use.kodkod.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	protected void handleArguments(String[] arguments) {
		String firstArgument = arguments[0];
		
		if (firstArgument.equals("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (firstArgument.equals("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else if (firstArgument.startsWith("show")) {
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)");
			Matcher m = showPattern.matcher(firstArgument);
			if (checkValidatorPresent()) {
				int index = Integer.parseInt(m.group(1));
				validator.showSolution(index);
			}
		} else {
			super.handleArguments(arguments);
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
