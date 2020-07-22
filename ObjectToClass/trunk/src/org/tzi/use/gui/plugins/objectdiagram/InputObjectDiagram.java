package org.tzi.use.gui.plugins.objectdiagram;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.views.diagrams.elements.AssociationName;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.Rolename;
import org.tzi.use.gui.views.diagrams.elements.edges.BinaryAssociationOrLinkEdge;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.elements.edges.LinkEdge;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagram;
import org.tzi.use.gui.views.diagrams.objectdiagram.ObjDiagramOptions;
import org.tzi.use.gui.views.diagrams.objectdiagram.ObjectNode;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.gui.plugins.Utilities;
import org.tzi.use.gui.plugins.data.MMConstants;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkEnd;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;


@SuppressWarnings("serial")
public class InputObjectDiagram extends NewObjectDiagram {

	private InputObjectDiagramView fParent;

	InputObjectDiagram(InputObjectDiagramView parent, PrintWriter log, ObjDiagramOptions options) {
		super(parent, log, options);
		fParent = parent;

		options.setShowAssocNames(true);

		// remove old listeners
		fParent.removeKeyListener(inputHandling);
		removeMouseListener(inputHandling);
		removeMouseListener(showObjectPropertiesViewMouseListener);
		// create new listeners
		inputHandling = new InputLinkInputHandling(fNodeSelection, fEdgeSelection, this);
		ObjectMouseListener objectMouseListener = new ObjectMouseListener();
		// add new listeners
		fParent.addKeyListener(inputHandling);
		addMouseListener(inputHandling);
		addMouseListener(objectMouseListener);

	}

	@Override
	public void addObject(MObject obj) {
		InputObjectNode n = new InputObjectNode(obj, fParent, getOptions());
		n.setPosition(nextNodePosition);
		n.setMinWidth(minClassNodeWidth);
		n.setMinHeight(minClassNodeHeight);

		getRandomNextPosition();

		fGraph.add(n);
		visibleData.fObjectToNodeMap.put(obj, n);
		fLayouter = null;
	}

	void deleteDisplayLink(MObject linkObject) {
		MLink displayLink = getDisplayLinkByName(linkObject.name());
		if (displayLink != null) {
			deleteLink(displayLink);
		}
	}

	private MLink getDisplayLinkByName(String linkName) {
		Set<MLink> visibleBinaryLinks = visibleData.fBinaryLinkToEdgeMap.keySet();
		Iterator<MLink> iterator = visibleBinaryLinks.iterator();
		while (iterator.hasNext()) {
			MLink link = iterator.next();
			if (link.association().name().equals(linkName)) {
				return link;
			}
		}
		return null;
	}

	// TODO nach OtcSystemApi
	MObject getMObjectByName(String linkObjectName) {
		return fParent.system().state().objectByName(linkObjectName);
	}

	@Override
	protected PopupMenuInfo unionOfPopUpMenu() {
		// create the popupMenu
		final JPopupMenu popupMenu = new JPopupMenu();
		PopupMenuInfo popupInfo = new PopupMenuInfo(popupMenu);
		// position for the popupMenu items
		int pos = 0;

		final Set<MObject> selectedLinks = new HashSet<MObject>();
		final List<MObject> selectedObjects = new ArrayList<MObject>();

		// Split selected nodes into model elements
		for (PlaceableNode node : fNodeSelection) {
			if (node instanceof ObjectNode) {
				selectedObjects.add(((ObjectNode) node).object());
			} else if (node instanceof AssociationName) {
				AssociationName assocName = (AssociationName) node;
				String selectedLinkObjectName = assocName.getLink().association().name();
				MObject linkObject = getMObjectByName(selectedLinkObjectName);
				selectedLinks.add(linkObject);
			} else if (node instanceof Rolename) {
				Rolename rolename = (Rolename) node;
				String selectedLinkObjectName = rolename.getEnd().association().name();
				MObject linkObject = getMObjectByName(selectedLinkObjectName);
				selectedLinks.add(linkObject);
			}
		}

		for (EdgeBase selectedEdge : fEdgeSelection) {
			if (selectedEdge instanceof LinkEdge) {
				LinkEdge aEdge = (LinkEdge) selectedEdge;
				String selectedLinkObjectName = aEdge.getLink().association().name();
				MObject linkObject = getMObjectByName(selectedLinkObjectName);
				selectedLinks.add(linkObject);
			}
		}

		if (selectedObjects.size() == 1 && selectedLinks.isEmpty()) {
			MObject object = selectedObjects.iterator().next();
			popupMenu.insert(new AbstractAction("Edit object") {
				@Override
				public void actionPerformed(ActionEvent e) {
					openInputObjectPropertiesView(object.name());
				}
			}, pos++);

			popupMenu.insert(new AbstractAction("Destroy object") {
				@Override
				public void actionPerformed(ActionEvent e) {
					// remove selection
					fNodeSelection.clear();
					fParent.startObjectDestruction(object);
				}
			}, pos++);

			popupMenu.insert(new AbstractAction("Clone object") {
				@Override
				public void actionPerformed(ActionEvent e) {
					fParent.startObjectCloning(object);
				}
			}, pos++);

			// popupMenu.insert(new JSeparator(), pos++);
			// popupMenu.insert(new AbstractAction("Add attribute") {
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// fParent.startSlotCreation(object);
			// }
			// }, pos++);
			//
			// for (MObject slot : fParent.getSlotsFromMainObject(object)) {
			// popupMenu.insert(new JSeparator(), pos++);
			// MObjectState slotState = slot.state(fParent.system().state());
			// String name = Utilities.getDisplayNameFromSlot(slotState);
			// popupMenu.insert(new ActionShowProperties("Edit '" + name + "'",
			// slot), pos++);
			// popupMenu.insert(new AbstractAction("Destroy " + name) {
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// fParent.startSlotDestruction(slot, object);
			// }
			// }, pos++);
			// }
		}

		if (selectedObjects.size() == 2 && selectedLinks.isEmpty()) {
			Iterator<MObject> iterator = selectedObjects.iterator();
			MObject object1 = iterator.next();
			MObject object2 = iterator.next();
			popupMenu.insert(new AbstractAction("Insert link") {
				@Override
				public void actionPerformed(ActionEvent e) {
					fParent.startLinkCreation(object1, object2);
				}
			}, pos++);
		}

		if (selectedObjects.size() == 2 && selectedLinks.isEmpty()) {
			Iterator<MObject> iterator = selectedObjects.iterator();
			MObject object1 = iterator.next();
			MObject object2 = iterator.next();
			popupMenu.insert(new AbstractAction("Insert Composition") {
				@Override
				public void actionPerformed(ActionEvent e) {
					fParent.startCompositionCreation(object2, object1);
				}
			}, pos++);
		}
		
		if (selectedObjects.size() == 2 && selectedLinks.isEmpty()) {
			Iterator<MObject> iterator = selectedObjects.iterator();
			MObject object1 = iterator.next();
			MObject object2 = iterator.next();
			popupMenu.insert(new AbstractAction("Insert Aggregation") {
				@Override
				public void actionPerformed(ActionEvent e) {
					fParent.startAggregationCreation(object2, object1);
				}
			}, pos++);
		}
		
		if (selectedObjects.isEmpty() && selectedLinks.size() == 1 && selectedLinks.iterator().next().name().startsWith("Link") ) {
			MObject linkObject = selectedLinks.iterator().next();
			// popupMenu.insert(new ActionShowProperties("Edit link",
			// linkObject), pos++);
			popupMenu.insert(new AbstractAction("Destroy link") {
				@Override
				public void actionPerformed(ActionEvent e) {
					// remove selection
					fEdgeSelection.clear();
					fParent.startLinkDestruction(linkObject);
					
				}
			}, pos++);
		}

		if (selectedObjects.isEmpty() && selectedLinks.size() == 1 && selectedLinks.iterator().next().name().startsWith("CompLink")) {
			MObject compositionObject = selectedLinks.iterator().next();
			// popupMenu.insert(new ActionShowProperties("Edit link",
			// linkObject), pos++);
			popupMenu.insert(new AbstractAction("Destroy composition") {
				@Override
				public void actionPerformed(ActionEvent e) {
					// remove selection
					fEdgeSelection.clear();
					fParent.startLinkDestruction(compositionObject);
				}
			}, pos++);
		}
		
		
		if (selectedObjects.isEmpty() && selectedLinks.size() == 1 && selectedLinks.iterator().next().name().startsWith("AggLink")) {
			MObject aggregationObject = selectedLinks.iterator().next();
			// popupMenu.insert(new ActionShowProperties("Edit link",
			// linkObject), pos++);
			popupMenu.insert(new AbstractAction("Destroy Aggregation") {
				@Override
				public void actionPerformed(ActionEvent e) {
					// remove selection
					fEdgeSelection.clear();
					fParent.startLinkDestruction(aggregationObject);
				}
			}, pos++);
		}
		if (pos > 0) {
			popupMenu.insert(new JSeparator(), pos++);
		}

		popupMenu.insert(new AbstractAction("Add object") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fParent.startObjectCreation();
			}
		}, pos++);

		popupMenu.insert(new JSeparator(), pos++);

		popupMenu.insert(new AbstractAction("Transform to class diagram") {
			@Override
			public void actionPerformed(ActionEvent e) {
				fParent.startTransformation();
			}
		}, pos++);

		// addShowOptions(popupMenu);
		popupMenu.addSeparator();
		Utilities.addShowHideOptions(popupMenu, this, fOpt);
		addCosmeticOptions(popupMenu);
		addLayoutMenuItems(popupMenu);

		return popupInfo;
	}

	// class ActionShowProperties extends AbstractAction {
	// private MObject fObject;
	//
	// ActionShowProperties(String text, MObject object) {
	// super(text);
	// fObject = object;
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// editObjectProperties(fObject);
	// }
	// }
	//
	// void editObjectProperties(MObject mObject) {
	// ObjectPropertiesView v =
	// MainWindow.instance().showObjectPropertiesView();
	// v.selectObject(mObject.name());
	// }

	private void addCosmeticOptions(JPopupMenu popupMenu) {
		final JCheckBoxMenuItem cbAntiAliasing = getMenuItemAntiAliasing();
		final JCheckBoxMenuItem cbShowGrid = getMenuItemShowGrid();
		final JCheckBoxMenuItem cbGrayscale = getMenuItemGrayscale();

		popupMenu.addSeparator();
		popupMenu.add(cbAntiAliasing);
		popupMenu.add(cbShowGrid);
		popupMenu.add(cbGrayscale);
	}

	@Override
	protected BinaryAssociationOrLinkEdge createBinaryAssociationOrLinkEdge(PlaceableNode source, PlaceableNode target,
			MLinkEnd sourceEnd, MLinkEnd targetEnd, NewObjectDiagram diagram, MLink link) {
		
		String selectedLinkObjectName = link.association().name();
		MObject linkObject = fParent.system().state().objectByName(selectedLinkObjectName);
		return new InputEdge(source, target, sourceEnd.associationEnd(), targetEnd.associationEnd(), this, link,
				linkObject.state(fParent.system().state()));
		
	}

	void editLinkName(MObject linkObject) {
		if (linkObject.name().contains("CompLink") ) {
			editCompositionAttribute(linkObject, "Edit Composition name", MMConstants.CLS_COMPOSITION_ATTR_COMP);
		}else if (linkObject.name().contains("AggLink")) {
			editAggregationAttribute(linkObject, "Edit Aggregation name", MMConstants.CLS_AGGREGATION_ATTR_AGGR);
		}
		else if (linkObject.name().contains("Link")) {
		editLinkAttribute(linkObject, "Edit link name", MMConstants.CLS_LINK_ATTR_ASSOC);
		}
	}

	void editLinkFirstRoleName(MObject linkObject) {
		if (linkObject.name().startsWith("CompLink")) {
			editCompositionAttribute(linkObject, "Edit role name", MMConstants.CLS_COMPOSITION_ATTR_FIRSTR);
		}else if (linkObject.name().startsWith("AggLink")) {
			editAggregationAttribute(linkObject, "Edit role name", MMConstants.CLS_AGGREGATION_ATTR_FIRSTR);
		}else  if (linkObject.name().startsWith("Link")) {
			editLinkAttribute(linkObject, "Edit role name", MMConstants.CLS_LINK_ATTR_FIRSTR);
		}
		
	}

	void editLinkSecondRoleName(MObject linkObject) {
		if (linkObject.name().startsWith("CompLink")) {
			editCompositionAttribute(linkObject, "Edit role name", MMConstants.CLS_COMPOSITION_ATTR_SECONDR);
		}else if (linkObject.name().startsWith("AggLink")) {
			editAggregationAttribute(linkObject, "Edit role name", MMConstants.CLS_AGGREGATION_ATTR_SECONDR);
		} 
		else {
			editLinkAttribute(linkObject, "Edit role name", MMConstants.CLS_LINK_ATTR_SECONDR);
		}
		
	}

	private void editLinkAttribute(MObject linkObject, String title, String attributeName) {
		MSystem system = fParent.system();
		MObjectState linkObjState = linkObject.state(system.state());
		String initialSelectionValue = Utilities.trim(linkObjState.attributeValue(attributeName).toString());
		String newValue = (String) JOptionPane.showInputDialog(this, null, title, JOptionPane.PLAIN_MESSAGE, null, null,
				initialSelectionValue);
		if (newValue == null) {
			// dialog cancelled
			return;
		}
		newValue = Utilities.setApostrophes(newValue);
		MAttribute attribute = system.model().getClass(MMConstants.CLS_LINK_NAME).attribute(attributeName, false);
		StringWriter errorOutput = new StringWriter();
		Expression valueAsExpression = OCLCompiler.compileExpression(system.model(), system.state(), newValue,
				"<input>", new PrintWriter(errorOutput, true), system.varBindings());
		if (valueAsExpression == null) {
			JOptionPane.showMessageDialog(this, errorOutput, "Error compiling expression " + newValue,
					JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				system.execute(new MAttributeAssignmentStatement(linkObject, attribute, valueAsExpression));
			} catch (MSystemException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void editCompositionAttribute(MObject compositionObject, String title, String attributeName) {
		MSystem system = fParent.system();
		MObjectState linkObjState = compositionObject.state(system.state());
		String initialSelectionValue = Utilities.trim(linkObjState.attributeValue(attributeName).toString());
		String newValue = (String) JOptionPane.showInputDialog(this, null, title, JOptionPane.PLAIN_MESSAGE, null, null,
				initialSelectionValue);
		if (newValue == null) {
			// dialog cancelled
			return;
		}
		newValue = Utilities.setApostrophes(newValue);
		MAttribute attribute = system.model().getClass(MMConstants.CLS_COMPOSITION_NAME).attribute(attributeName, false);
		StringWriter errorOutput = new StringWriter();
		Expression valueAsExpression = OCLCompiler.compileExpression(system.model(), system.state(), newValue,
				"<input>", new PrintWriter(errorOutput, true), system.varBindings());
		if (valueAsExpression == null) {
			JOptionPane.showMessageDialog(this, errorOutput, "Error compiling expression " + newValue,
					JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				system.execute(new MAttributeAssignmentStatement(compositionObject, attribute, valueAsExpression));
			} catch (MSystemException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void editAggregationAttribute(MObject aggregationObject, String title, String attributeName) {
		MSystem system = fParent.system();
		MObjectState linkObjState = aggregationObject.state(system.state());
		String initialSelectionValue = Utilities.trim(linkObjState.attributeValue(attributeName).toString());
		String newValue = (String) JOptionPane.showInputDialog(this, null, title, JOptionPane.PLAIN_MESSAGE, null, null,
				initialSelectionValue);
		if (newValue == null) {
			// dialog cancelled
			return;
		}
		newValue = Utilities.setApostrophes(newValue);
		MAttribute attribute = system.model().getClass(MMConstants.CLS_AGGREGATION_NAME).attribute(attributeName, false);
		StringWriter errorOutput = new StringWriter();
		Expression valueAsExpression = OCLCompiler.compileExpression(system.model(), system.state(), newValue,
				"<input>", new PrintWriter(errorOutput, true), system.varBindings());
		if (valueAsExpression == null) {
			JOptionPane.showMessageDialog(this, errorOutput, "Error compiling expression " + newValue,
					JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				system.execute(new MAttributeAssignmentStatement(aggregationObject, attribute, valueAsExpression));
			} catch (MSystemException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private class ObjectMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				PlaceableNode pickedObjectNode = findNode(e.getX(), e.getY());
				if (pickedObjectNode instanceof ObjectNode) {
					ObjectNode obj = (ObjectNode) pickedObjectNode;
					openInputObjectPropertiesView(obj.object().name());
				}
			}
		}
	}

	private void openInputObjectPropertiesView(String objectName) {
		InputObjectPropertiesView iopv = new InputObjectPropertiesView(fParent.system(), objectName, fParent);
		ViewFrame f = new ViewFrame("Input object properties", iopv, "ObjectProperties.gif");
		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(iopv, BorderLayout.CENTER);
		MainWindow.instance().addNewViewFrame(f);
	}
}