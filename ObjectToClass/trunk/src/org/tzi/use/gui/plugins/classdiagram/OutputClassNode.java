package org.tzi.use.gui.plugins.classdiagram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.classdiagram.ClassNode;
import org.tzi.use.gui.plugins.data.TAttribute;
import org.tzi.use.gui.plugins.data.TClass;
import org.tzi.use.gui.plugins.data.TStatus;
import org.tzi.use.gui.plugins.data.TConstants;
import org.tzi.use.uml.mm.MClass;

public class OutputClassNode extends ClassNode {

	private final TClass tClass;
	private final TStatus classStatus;
	// private final Color normalColor;
	// private final Color selectedColor;
	private final boolean isDashed;

	OutputClassNode(MClass dummyCls, DiagramOptions opt, TClass tClass) {
		super(dummyCls, opt);

		this.tClass = tClass;

		if (tClass.isAnonymous()) {
			fLabel = TConstants.PLACEHOLDER;
		}

		classStatus = tClass.getCurrentStatus();
		switch (classStatus) {
		case COMPLETE:
			// normalColor = TConstants.COMPLETE_COLOR;
			// selectedColor = TConstants.COMPLETE_SELECTED_COLOR;
			isDashed = false;
			break;
		case MISSING:
			// normalColor = TConstants.MISSING_COLOR;
			// selectedColor = TConstants.MISSING_SELECTED_COLOR;
			isDashed = true;
			break;
		case CONFLICT:
			// normalColor = TConstants.CONFLICT_COLOR;
			// selectedColor = TConstants.CONFLICT_SELECTED_COLOR;
			isDashed = true;
			break;
		default:
			// normalColor = TConstants.DEFAULT_COLOR;
			// selectedColor = TConstants.DEFAULT_SELECTED_COLOR;
			isDashed = false;
			break;

		}
	}

	/**
	 * this is a workaround
	 */
	@Override
	protected void onDraw(Graphics2D g) {
		fOpt.registerTypeColor(DiagramOptions.NODE_FRAME_COLOR, Color.BLACK, Color.BLACK);

		if (isSelected()) {
			fOpt.registerTypeColor(DiagramOptions.NODE_SELECTED_COLOR, TConstants.GRAY_SELECTED_NODE_COLOR,
					TConstants.GRAY_SELECTED_NODE_COLOR);
		} else {
			fOpt.registerTypeColor(DiagramOptions.NODE_COLOR, TConstants.GRAY_NODE_COLOR, TConstants.GRAY_NODE_COLOR);
		}
		if (isDashed) {
			g.setStroke(TConstants.dashedBoxBasicStroke);
		} else {
			g.setStroke(TConstants.defaultBasicStroke);
		}
		super.onDraw(g);
	}

	/**
	 * this only works for drawing attributes. results for drawing operations would
	 * be wrong
	 */
	@Override
	protected int drawCompartment(Graphics2D g, int y, String[] values, Color[] colors, Rectangle2D roundedBounds) {
		int i = 0;
		for (TAttribute tAttr : tClass.getAttributes()) {
			values[i++] = tAttr.getDisplayTextForClass();
		}
		return super.drawCompartment(g, y, values, colors, roundedBounds);
	}

	@Override
	protected void calculateAttributeRectSize(Graphics2D g, Rectangle2D.Double rect) {
		String[] values = new String[tClass.getAttributes().size()];
		int i = 0;
		for (TAttribute a : tClass.getAttributes()) {
			values[i++] = a.getDisplayTextForClass();
		}
		calculateCompartmentRectSize(g, rect, values);
	}
}