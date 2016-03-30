package org.tzi.use.plugin.filmstrip;

import org.tzi.use.plugin.filmstrip.layout.FilmstripObjectDiagramLayouter;

public class LayoutObjectDiagramActionHorizontal extends LayoutObjectDiagramAction {

	@Override
	protected void performLayouting(FilmstripObjectDiagramLayouter layouter) {
		layouter.groupObjects(false);
	}

}
