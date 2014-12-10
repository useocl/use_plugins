package org.tzi.use.kodkod.plugin;

import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.use.kodkod.UseScrollingAllKodkodModelValidator;

public class KodkodScrollingAllValidateCmd extends KodkodScrollingValidateCmd{

	@Override
	protected KodkodModelValidator createValidator() {
		validator = new UseScrollingAllKodkodModelValidator(session);
		return validator;
	}
}
