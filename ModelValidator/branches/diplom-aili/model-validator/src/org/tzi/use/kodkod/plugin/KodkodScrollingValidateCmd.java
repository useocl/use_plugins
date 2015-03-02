package org.tzi.use.kodkod.plugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.use.kodkod.UseScrollingKodkodModelValidator;
import org.tzi.use.util.StringUtil;

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
	protected void handleArguments(String[] arguments) {
		String firstArgument = arguments[0];
		
		if (firstArgument.equalsIgnoreCase("next")) {
			if (checkValidatorPresent()) {
				validator.nextSolution();
			}
		} else if (firstArgument.equalsIgnoreCase("previous")) {
			if (checkValidatorPresent()) {
				validator.previousSolution();
			}
		} else {
			String argumentsAsString = StringUtil.fmtSeq(arguments, " ");
			Pattern showPattern = Pattern.compile("show\\s*\\(\\s*(\\d+)\\s*\\)", Pattern.CASE_INSENSITIVE);
			Matcher m = showPattern.matcher(argumentsAsString);
			
			if (m.matches()) {
				if (checkValidatorPresent()) {
					int index = Integer.parseInt(m.group(1));
					validator.showSolution(index);
				}
			} else {
				super.handleArguments(arguments);
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
