package org.tzi.use.gui.plugins.objectdiagram;

import org.tzi.use.gui.plugins.data.MMConstants;
import org.tzi.use.gui.util.Selection;
import org.tzi.use.gui.views.diagrams.elements.AssociationName;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.Rolename;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.event.DiagramInputHandling;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.sys.MObject;

public class InputLinkInputHandling extends DiagramInputHandling {

	private final InputObjectDiagram inputObjectDiagram;

	public InputLinkInputHandling(Selection<PlaceableNode> nodeSelection, Selection<EdgeBase> edgeSelection,
			InputObjectDiagram diagram) {
		super(nodeSelection, edgeSelection, diagram);
		inputObjectDiagram = diagram;
	}

	@Override
	protected void handleDoubleClickForEdgeProperty(EdgeProperty edgeProperty) {
		// default behavior: setToAutoPosition
		// super.handleDoubleClickForEdgeProperty(edgeProperty);

		// additional behavior
		if (edgeProperty instanceof AssociationName) {
			AssociationName assocName = (AssociationName) edgeProperty;
			String selectedLinkObjectName = assocName.getLink().association().name();
			MObject linkObject = inputObjectDiagram.getMObjectByName(selectedLinkObjectName);
			inputObjectDiagram.editLinkName(linkObject);
		} else if (edgeProperty instanceof Rolename) {
			Rolename rolename = (Rolename) edgeProperty;
			MAssociationEnd assocEnd = rolename.getEnd();
			String selectedLinkObjectName = assocEnd.association().name();
			MObject linkObject = inputObjectDiagram.getMObjectByName(selectedLinkObjectName);
			// inputObjectDiagram.editObjectProperties(linkObject);
			if (assocEnd.name().startsWith(MMConstants.CLS_LINK_ATTR_FIRSTR)) {
				inputObjectDiagram.editLinkFirstRoleName(linkObject);
			} else if (assocEnd.name().startsWith(MMConstants.CLS_LINK_ATTR_SECONDR)) {
				inputObjectDiagram.editLinkSecondRoleName(linkObject);
			}
		}
	}

}