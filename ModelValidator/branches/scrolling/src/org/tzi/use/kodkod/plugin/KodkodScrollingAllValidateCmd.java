package org.tzi.use.kodkod.plugin;

import org.tzi.kodkod.KodkodModelValidator;
import org.tzi.use.kodkod.UseScrollingAllKodkodModelValidator;

public class KodkodScrollingAllValidateCmd extends KodkodScrollingValidateCmd{

	@Override
	protected KodkodModelValidator createValidator() {
		if(validator == null){
			validator = new UseScrollingAllKodkodModelValidator(session);
		}
		return validator;
	}
}
