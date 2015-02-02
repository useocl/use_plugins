package org.tzi.use.kodkod.plugin;

import org.tzi.use.kodkod.UseCTScrollingAllKodkodModelValidator;

/**
 * Scrolling through all solutions using classifier terms
 * 
 * @author Frank Hilken
 */
public class KodkodCTScrollingAllValidateCmd extends KodkodCTScrollingValidateCmd {

	@Override
	protected void resetValidator() {
		validator = new UseCTScrollingAllKodkodModelValidator(session);
	}
}
