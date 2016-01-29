package org.tzi.use.kodkod.plugin;

import org.tzi.use.kodkod.UseScrollingAllKodkodModelValidator;

public class KodkodScrollingAllValidateCmd extends KodkodScrollingValidateCmd {

	@Override
	protected void resetValidator() {
		activeValidator = new UseScrollingAllKodkodModelValidator(session);
	}
}
