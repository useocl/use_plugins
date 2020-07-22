package org.tzi.use.gui.plugins.objectdiagram;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.List;

import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.objectdiagram.ObjDiagramOptions;
import org.tzi.use.gui.views.diagrams.objectdiagram.ObjectNode;
import org.tzi.use.gui.plugins.OtcSystemApi;
import org.tzi.use.gui.plugins.Utilities;
import org.tzi.use.gui.plugins.data.TConstants;
import org.tzi.use.gui.plugins.data.TStatus;
import org.tzi.use.gui.views.diagrams.util.Util;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;

public class InputObjectNode extends ObjectNode {

	private OtcSystemApi otcApi;

	private final ObjDiagramOptions fOpt;
	private final InputObjectDiagramView fParent;
	private final MObject fObject;
	private String fLabel;
	private String[] fValues;

//	private Color normalColor;
//	private Color selectedColor;

	public InputObjectNode(MObject obj, InputObjectDiagramView parent, ObjDiagramOptions opt) {
		super(obj, parent, opt);
		fObject = obj;
		fParent = parent;
		fOpt = opt;

		otcApi = new OtcSystemApi(fParent.system());

		updateContent();
	}
	
	private boolean isDashed = false;
	
	@Override
	protected void onDraw(Graphics2D g) {
		Rectangle2D.Double currentBounds = getRoundedBounds();

		if (!Util.enlargeRectangle(currentBounds, 10).intersects(g.getClipBounds())) {
			return;
		}

		double x = currentBounds.getX();
		int y = (int) currentBounds.getY();

		int labelWidth = g.getFontMetrics().stringWidth(fLabel);

//		if (fOpt.grayscale()) { FIXME currently always uses greyscale
			if (isSelected()) {
				g.setColor(TConstants.GRAY_SELECTED_NODE_COLOR);
			} else {
				g.setColor(TConstants.GRAY_NODE_COLOR);
			}
//		} else if (isSelected()) {
//			g.setColor(selectedColor);
//		} else {
//			g.setColor(normalColor);
//		}
		g.fill(currentBounds);
		g.setColor(fOpt.getNODE_FRAME_COLOR());
		
		if(isDashed) {
			g.setStroke(TConstants.dashedBoxBasicStroke);
		} else {
			g.setStroke(TConstants.defaultBasicStroke);
		}
		
		g.draw(currentBounds);

		x = (currentBounds.getCenterX() - labelWidth / 2);
		y = (int) currentBounds.getY() + g.getFontMetrics().getAscent() + 2;

		g.setColor(fOpt.getColor(DiagramOptions.NODE_LABEL_COLOR));
		g.drawString(fLabelA.getIterator(), Math.round(x), y);

		if (fOpt.isShowAttributes()) {
			// compartment divider
			Line2D.Double lineAttrDivider = new Line2D.Double(currentBounds.getX(), y + 3, currentBounds.getMaxX(),
					y + 3);
			g.draw(lineAttrDivider);
			x = currentBounds.getX() + 5;
			y += 3;
			for (int i = 0; i < fValues.length; i++) {
				y += g.getFontMetrics().getHeight();
				g.drawString(fValues[i], Math.round(x), y);
			}
		}
	}

	@Override
	public void doCalculateSize(Graphics2D g) {
		FontMetrics fm = g.getFontMetrics();

		updateContent();

		nameRect.width = fm.stringWidth(fLabel);
		nameRect.height = fm.getHeight();

		attributesRect.width = 0;
		for (int i = 0; i < fValues.length; ++i) {
			attributesRect.width = Math.max(attributesRect.width, fm.stringWidth(fValues[i]));
		}
		attributesRect.height = fm.getHeight() * fValues.length + 3;

		calculateBounds();
	}

	public void updateContent() {
		MObjectState objState = fObject.state(fParent.system().state());
		if (objState == null) {
			return;
		}

		List<MObject> slots = otcApi.getSlotsOfObject(fObject, true);

		fLabel = Utilities.getMainDisplayFromObject(objState);
		fLabelA = new AttributedString(fLabel);
		fLabelA.addAttribute(TextAttribute.FONT, fParent.getFont());
		fLabelA.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

		if (slots == null) {
			fValues = new String[0];
		} else {
			fValues = new String[slots.size()];
			int i = 0;
			for (MObject slot : slots) {
				MObjectState slotState = slot.state(fParent.system().state());
				fValues[i++] = Utilities.getObjectDisplayNameFromSlot(slotState);
			}
		}

		TStatus objectStatus = Utilities.getCurrentStatus(fParent.system().state(), fObject, slots);
		switch (objectStatus) {
		case COMPLETE:
			//normalColor = TConstants.COMPLETE_COLOR;
			//selectedColor = TConstants.COMPLETE_SELECTED_COLOR;
			isDashed = false;
			break;
		case MISSING:
			//normalColor = TConstants.MISSING_COLOR;
			//selectedColor = TConstants.MISSING_SELECTED_COLOR;
			isDashed = true;
			break;
		case CONFLICT:
			//normalColor = TConstants.CONFLICT_COLOR;
			//selectedColor = TConstants.CONFLICT_SELECTED_COLOR;
			isDashed = true;
			break;
		default:
			//normalColor = TConstants.DEFAULT_COLOR;
			//selectedColor = TConstants.DEFAULT_SELECTED_COLOR;
			isDashed = false;
			break;
		}
	}
}