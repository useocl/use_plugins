package org.tzi.use.gui.plugins.classdiagram;

import org.tzi.use.gui.views.diagrams.classdiagram.ClassDiagramOptions;

public class OutputClassDiagramOptions extends ClassDiagramOptions {
	protected boolean fSimplifiedMult = false;

	public boolean isSimplifiedMult() {
		return fSimplifiedMult;
	}

	public void setSimplifiedMult(boolean simplifiedMult) {
		fSimplifiedMult = simplifiedMult;
		onOptionChanged("SIMPLIFIEDMULT"); // FIXME implement
	}
}
