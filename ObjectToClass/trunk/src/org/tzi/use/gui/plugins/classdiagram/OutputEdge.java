package org.tzi.use.gui.plugins.classdiagram;

import java.awt.Color;
import java.awt.Graphics2D;

import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.BinaryAssociationOrLinkEdge;
import org.tzi.use.gui.plugins.data.TAssociation;
import org.tzi.use.gui.plugins.data.TConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MMultiplicity;

import com.google.common.collect.Multimap;

public class OutputEdge extends BinaryAssociationOrLinkEdge {

	private final TAssociation tAssoc;
	private final Color edgeColor;
	private final Color edgeSelectedColor;

	public OutputEdge(PlaceableNode source, PlaceableNode target, MAssociationEnd sourceEnd, MAssociationEnd targetEnd,
			DiagramView diagram, MAssociation mAssoc, TAssociation tAssoc) {
		super(source, target, sourceEnd, targetEnd, diagram, mAssoc);

		this.tAssoc = tAssoc;

		switch (tAssoc.getCurrentStatus()) {
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

		String tAssocName = tAssoc.getAssociationName();
		if (tAssocName == null || tAssocName.isEmpty()) {
			tAssocName = TConstants.ASSOCIATION_PLACEHOLDER_NAME;
		}
		// FIXME source = 1 und target = 2 immer richtig?
		getAssocName().setName(tAssocName);

		String displayText1 = tAssoc.getDisplayText1();
		if (displayText1 == null || displayText1.isEmpty()) {
			displayText1 = TConstants.ASSOCIATION_PLACEHOLDER_ROLE1;
		}
		getSourceRolename().setName(displayText1);
		String displayText2 = tAssoc.getDisplayText2();
		if (displayText2 == null || displayText2.isEmpty()) {
			displayText2 = TConstants.ASSOCIATION_PLACEHOLDER_ROLE2;
		}
		getTargetRolename().setName(displayText2);

		MMultiplicity mult1 = tAssoc.getFirstEndMultiplicity();
		String displayRoleName1;
		if (mult1 == null) {
			displayRoleName1 = " ";
		} else {
			displayRoleName1 = mult1.toString();
		}
		getSourceMultiplicity().setName(displayRoleName1);
		MMultiplicity mult2 = tAssoc.getSecondEndMultiplicity();
		String displayRoleName2;
		if (mult2 == null) {
			displayRoleName2 = " ";
		} else {
			displayRoleName2 = mult2.toString();
		}
		getTargetMultiplicity().setName(displayRoleName2);
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