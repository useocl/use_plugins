package org.tzi.use.plugin.otc.objectdiagram;

import java.awt.Color;
import java.awt.Graphics2D;

import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.BinaryAssociationOrLinkEdge;
import org.tzi.use.plugin.otc.Utilities;
import org.tzi.use.plugin.otc.data.TConstants;
import org.tzi.use.plugin.otc.data.TLink;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObjectState;

import com.google.common.collect.Multimap;

public class InputEdge extends BinaryAssociationOrLinkEdge {

	private final TLink tLink;
	private final Color edgeColor;
	private final Color edgeSelectedColor;

	public InputEdge(PlaceableNode source, PlaceableNode target, MAssociationEnd sourceEnd, MAssociationEnd targetEnd,
			DiagramView diagram, MLink mLink, MObjectState linkObjectState) {
		super(source, target, sourceEnd, targetEnd, diagram, mLink);

		//FIXME the adjacent objects get lost here.
		tLink = Utilities.getTLinkWithoutId(linkObjectState);

		switch (tLink.getCurrentStatus()) {
		case COMPLETE:
			edgeColor = TConstants.COMPLETE_COLOR;
			edgeSelectedColor = TConstants.COMPLETE_SELECTED_COLOR;
			break;
		case MISSING:
			edgeColor = TConstants.MISSING_COLOR;
			edgeSelectedColor = TConstants.MISSING_SELECTED_COLOR;
			break;
		case CONFLICT:
			edgeColor = TConstants.CONFLICT_COLOR;
			edgeSelectedColor = TConstants.CONFLICT_SELECTED_COLOR;
			break;
		default:
			edgeColor = Color.BLACK;
			edgeSelectedColor = TConstants.DEFAULT_SELECTED_COLOR;
			break;
		}
	}

	@Override
	protected void initializeProperties(Multimap<PropertyOwner, EdgeProperty> properties) {
		super.initializeProperties(properties);

		String tAssocName = tLink.getLinkName();
		if (tAssocName == null || tAssocName.isEmpty()) {
			tAssocName = TConstants.LINK_PLACEHOLDER_NAME;
		}
		// FIXME source = 1 und target = 2 immer richtig?
		getAssocName().setName(tAssocName);

		String displayText1 = tLink.getFirstEndRoleName();
		if (displayText1 == null || displayText1.isEmpty()) {
			displayText1 = TConstants.LINK_PLACEHOLDER_ROLE1;
		}
		getSourceRolename().setName(displayText1);
		String displayText2 = tLink.getSecondEndRoleName();
		if (displayText2 == null || displayText2.isEmpty()) {
			displayText2 = TConstants.LINK_PLACEHOLDER_ROLE2;
		}
		getTargetRolename().setName(displayText2);
	}

	@Override
	protected void drawBinaryEdge(Graphics2D g) {
		if (fOpt.grayscale()) {
			if (isSelected()) {
				g.setColor(TConstants.GRAY_SELECTED_EDGE_COLOR);
			} else {
				g.setColor(TConstants.GRAY_EDGE_COLOR);
			}
		} else if (isSelected()) {
			g.setColor(edgeSelectedColor);
		} else {
			g.setColor(edgeColor);
		}

		drawEdge(g);

		g.setColor(fOpt.getEDGE_COLOR());
	}
}