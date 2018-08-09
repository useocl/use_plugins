package org.tzi.use.gui.plugins.classdiagram;

import java.awt.Graphics2D;
import java.util.List;

import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.elements.EdgeProperty;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.BinaryAssociationOrLinkEdge;
import org.tzi.use.gui.plugins.data.TAssociation;
import org.tzi.use.gui.plugins.data.TConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.mm.MMultiplicity.Range;

import com.google.common.collect.Multimap;

public class OutputEdge extends BinaryAssociationOrLinkEdge {

	private final TAssociation tAssoc;
	// private final Color edgeColor;
	// private final Color edgeSelectedColor;
	private final boolean isDashed;

	public OutputEdge(PlaceableNode source, PlaceableNode target, MAssociationEnd sourceEnd, MAssociationEnd targetEnd,
			DiagramView diagram, MAssociation mAssoc, TAssociation tAssoc) {
		super(source, target, sourceEnd, targetEnd, diagram, mAssoc);

		this.tAssoc = tAssoc;

		switch (tAssoc.getCurrentStatus()) {
		case COMPLETE:
			// edgeColor = TConstants.COMPLETE_COLOR;
			// edgeSelectedColor = TConstants.COMPLETE_SELECTED_COLOR;
			isDashed = false;
			break;
		case MISSING:
			// edgeColor = TConstants.MISSING_COLOR;
			// edgeSelectedColor = TConstants.MISSING_SELECTED_COLOR;
			isDashed = true;
			break;
		case CONFLICT:
			// edgeColor = TConstants.CONFLICT_COLOR;
			// edgeSelectedColor = TConstants.CONFLICT_SELECTED_COLOR;
			isDashed = true;
			break;
		default:
			// edgeColor = Color.BLACK;
			// edgeSelectedColor = TConstants.DEFAULT_SELECTED_COLOR;
			isDashed = false;
			break;
		}
	}

	private String complexSourceMult;
	private String simpleSourceMult;
	private String complexTargetMult;
	private String simpleTargetMult;

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
		complexSourceMult = getComplexMultText(mult1);
		simpleSourceMult = getSimpleMultText(mult1);

		MMultiplicity mult2 = tAssoc.getSecondEndMultiplicity();
		complexTargetMult = getComplexMultText(mult2);
		simpleTargetMult = getSimpleMultText(mult2);
	}

	private String getComplexMultText(MMultiplicity mult) {
		if (mult == null) {
			return " ";
		} else {
			return mult.toString();
		}
	}

	private String getSimpleMultText(MMultiplicity mult) {
		if (mult == null) {
			return " ";
		}

		List<Range> ranges = mult.getRanges();
		if (ranges.size() == 1) {
			Range range = ranges.get(0);
			int lower = range.getLower();
			int upper = range.getUpper();

			MMultiplicity retMult;
			if (lower == 0 && upper == 1) {
				retMult = MMultiplicity.ZERO_ONE;
			} else if (lower == 1 && upper == 1) {
				retMult = MMultiplicity.ONE;
			} else if (lower >= 1) {
				retMult = MMultiplicity.ONE_MANY;
			} else {
				retMult = MMultiplicity.ZERO_MANY;
			}

			return retMult.toString();
		} else {
			return " ";
		}
	}

	@Override
	protected void drawBinaryEdge(Graphics2D g) {
		// if (fOpt.grayscale()) { FIXME currently always uses greyscale
		if (isSelected()) {
			g.setColor(TConstants.GRAY_SELECTED_EDGE_COLOR);
		} else {
			g.setColor(TConstants.GRAY_EDGE_COLOR);
		}
		// } else if (isSelected()) {
		// g.setColor(edgeSelectedColor);
		// } else {
		// g.setColor(edgeColor);
		// }
		// setDashed(true); FIXME alternative version for dashed lines

		if (isDashed) {
			g.setStroke(TConstants.dashedLineBasicStroke);
		} else {
			g.setStroke(TConstants.defaultBasicStroke);
		}

		drawEdge(g);

		g.setColor(fOpt.getEDGE_COLOR());
	}

	@Override
	public void drawProperties(Graphics2D g) {

		OutputClassDiagramOptions ocdo;
		if (fOpt instanceof OutputClassDiagramOptions) {
			ocdo = (OutputClassDiagramOptions) fOpt;
		} else {
			return; // should not happen
		}

		if (ocdo.isSimplifiedMult()) {
			getSourceMultiplicity().setName(simpleSourceMult);
			getTargetMultiplicity().setName(simpleTargetMult);
		} else {
			getSourceMultiplicity().setName(complexSourceMult);
			getTargetMultiplicity().setName(complexTargetMult);
		}

		super.drawProperties(g);
	}
}
