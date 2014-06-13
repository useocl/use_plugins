/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2010 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

// $Id$

package org.tzi.use.gui.views.diagrams.behavior.communicationdiagram;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.tzi.use.gui.util.PersistHelper;
import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.DiagramViewWithObjectNode;
import org.tzi.use.gui.views.diagrams.ObjectNodeActivity;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.EdgeBase;
import org.tzi.use.gui.views.diagrams.elements.positioning.StrategyFixed;
import org.tzi.use.gui.views.diagrams.elements.positioning.StrategyUnmovable;
import org.tzi.use.gui.views.diagrams.event.ActionHideCommunicationDiagram;
import org.tzi.use.gui.views.diagrams.event.ActionLoadLayout;
import org.tzi.use.gui.views.diagrams.event.ActionSaveLayout;
import org.tzi.use.gui.views.diagrams.event.DiagramInputHandling;
import org.tzi.use.gui.views.selection.objectselection.ObjectSelection;
import org.tzi.use.gui.xmlparser.LayoutTags;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkObjectImpl;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectImpl;
import org.tzi.use.uml.sys.MOperationCall;
import org.tzi.use.util.StringUtil;
import org.w3c.dom.Element;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

/**
 * Same as class diagram and object diagram, there are also common things
 * between them and communication diagram. So that, this class is a inheritance
 * of {@link DiagramViewWithObjectNode} which is subclass of {@link DiagramView}
 * .
 * 
 * @author Quang Dung Nguyen
 * 
 */
@SuppressWarnings("serial")
public class CommunicationDiagram extends DiagramViewWithObjectNode {

	public static class CommunicationDiagramData implements DiagramData {

		private Map<MObject, ObjectBoxNode> objectToNodeMap;

		private Map<MObject, CommunicationDiagramEdge> objectToCreateEdgeMap;

		private Map<MObject, CommunicationDiagramEdge> objectToReflexEdgeMap;

		private Map<MLink, LinkBoxNode> linkToNodeMap;

		private Map<MLink, CommunicationDiagramEdge> linkToCreateEdgeMap;

		private Map<MLink, List<CommunicationDiagramEdge>> linkToAssociationEdges;

		private Map<MObject, ObjectBoxNode> destroyedObjectNodes;

		private Map<MLink, LinkBoxNode> destroyedLinkNodes;

		private List<CommunicationDiagramEdge> allEdges;

		public CommunicationDiagramData() {
			objectToNodeMap = new HashMap<MObject, ObjectBoxNode>();
			objectToCreateEdgeMap = new HashMap<MObject, CommunicationDiagramEdge>();
			objectToReflexEdgeMap = new HashMap<MObject, CommunicationDiagramEdge>();
			destroyedObjectNodes = new HashMap<MObject, ObjectBoxNode>();
			destroyedLinkNodes = new HashMap<MLink, LinkBoxNode>();
			linkToNodeMap = new HashMap<MLink, LinkBoxNode>();
			linkToCreateEdgeMap = new ConcurrentHashMap<MLink, CommunicationDiagramEdge>();
			linkToAssociationEdges = new ConcurrentHashMap<MLink, List<CommunicationDiagramEdge>>();
			allEdges = new ArrayList<CommunicationDiagramEdge>();
		}

		@Override
		public Set<PlaceableNode> getNodes() {
			Set<PlaceableNode> result = new HashSet<PlaceableNode>();
			result.addAll(objectToNodeMap.values());
			result.addAll(destroyedObjectNodes.values());
			result.addAll(destroyedLinkNodes.values());
			result.addAll(linkToNodeMap.values());
			return result;
		}

		@Override
		public Set<EdgeBase> getEdges() {
			Set<EdgeBase> result = new HashSet<EdgeBase>();
			result.addAll(allEdges);
			return result;
		}

		@Override
		public boolean hasNodes() {
			return !(objectToNodeMap.isEmpty() && destroyedObjectNodes.isEmpty() && linkToNodeMap.isEmpty() && destroyedLinkNodes.isEmpty());
		}

		/**
		 * Copies all data to the target object
		 * 
		 * @param hiddenData
		 */
		public void copyTo(CommunicationDiagramData target) {
			target.objectToNodeMap.putAll(this.objectToNodeMap);
			target.destroyedObjectNodes.putAll(this.destroyedObjectNodes);
			target.destroyedLinkNodes.putAll(this.destroyedLinkNodes);
			target.linkToNodeMap.putAll(this.linkToNodeMap);
			target.linkToAssociationEdges.putAll(this.linkToAssociationEdges);
			target.objectToCreateEdgeMap.putAll(this.objectToCreateEdgeMap);
			target.objectToReflexEdgeMap.putAll(this.objectToReflexEdgeMap);
			target.linkToCreateEdgeMap.putAll(this.linkToCreateEdgeMap);
		}

		/**
		 * Removes all data
		 */
		public void clear() {
			this.objectToNodeMap.clear();
			this.destroyedObjectNodes.clear();
			this.destroyedLinkNodes.clear();
			this.linkToNodeMap.clear();
			this.linkToAssociationEdges.clear();
			this.objectToCreateEdgeMap.clear();
			this.objectToReflexEdgeMap.clear();
			this.linkToCreateEdgeMap.clear();
		}

	}
	
	public static final Color ACTIVATED_MESSAGE_COLOR = new Color(80, 136, 252);

	private CommunicationDiagramView fParent;
	private ActorNode actorSymbolNode;
	private ActorChangeNameDialog actorNameDialog;

	private CommunicationDiagramData visibleData = new CommunicationDiagramData();
	private CommunicationDiagramData hiddenData = new CommunicationDiagramData();

	/**
	 * The position of the next object node. This is either set to a random
	 * value or to a specific position when an object is created by drag & drop.
	 */
	private Point2D.Double nextNodePosition = new Point2D.Double();

	private ObjectSelection fSelection;
	private DiagramInputHandling inputHandling;

	private Stack<MOperationCall> operationsStack;
	private List<MObject> operationsCaller = new ArrayList<MObject>();

	private List<Integer> sequenceNumbers;
	private Vector<MMessage> messages;
	private MessagesNavigationDialog naviDialog;
	private CommunicationDiagramEdge activatedEdge;
	private MessagesGroup activatedMessage;

	/**
	 * Creates a new empty diagram.
	 */
	CommunicationDiagram(CommunicationDiagramView parent, PrintWriter log) {
		this(parent, log, new CommunicationDiagramOptions());
	}

	CommunicationDiagram(CommunicationDiagramView parent, PrintWriter log, CommunicationDiagramOptions options) {
		super(options, log);
		this.computeNextRandomPoint();

		operationsStack = new Stack<MOperationCall>();
		sequenceNumbers = new ArrayList<Integer>();
		sequenceNumbers.add(1);

		messages = new Vector<MMessage>();
		messages.add(new MMessage());

		fParent = parent;
		parent.setFont(getFont());

		fSelection = new ObjectSelection(this, fParent.system());

		fActionSaveLayout = new ActionSaveLayout("USE communication diagram layout", "olt", this);
		fActionLoadLayout = new ActionLoadLayout("USE communications diagram layout", "olt", this);

		inputHandling = new DiagramInputHandling(fNodeSelection, fEdgeSelection, this);

		fParent.addKeyListener(inputHandling);
		addMouseListener(inputHandling);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				fLayouter = null;
			}
		});

		initializeActor();
		startLayoutThread();
	}

	private void initializeActor() {
		actorSymbolNode = new ActorNode(new Actor());
		actorSymbolNode.setPosition(getWidth() / 2, getHeight() / 2);
		computeNextRandomPoint();
		fGraph.add(actorSymbolNode);
	}

	/**
	 * Finds a new position for the next object node.
	 */
	private void computeNextRandomPoint() {
		nextNodePosition.x = (0.4 + Math.random() * 0.6) * Math.max(200, getWidth() - 50);
		nextNodePosition.y = (0.4 + Math.random() * 0.6) * Math.max(200, getHeight() - 50);
	}

	public CommunicationDiagramView getParentDiagram() {
		return fParent;
	}

	@Override
	protected PopupMenuInfo unionOfPopUpMenu() {
		// context menu on right mouse click
		JPopupMenu popupMenu = new JPopupMenu();
		PopupMenuInfo popupInfo = new PopupMenuInfo(popupMenu);

		// position for the popupMenu items
		int pos = 0;

		final List<Object> selectedObjects = new ArrayList<Object>();

		// Split selected nodes into model elements
		for (PlaceableNode node : fNodeSelection) {
			if (node instanceof ObjectNodeActivity) {
				selectedObjects.add(((ObjectNodeActivity) node).object());
			} else if (node instanceof LinkBoxNode) {
				selectedObjects.add(((LinkBoxNode) node).getLink());
			}
		}

		// Just to be sure to delete an object only once
		Set<Object> selectedObjectsSet = new HashSet<Object>(selectedObjects);

		// This text is reused often
		String selectedObjectsText = null;
		if (selectedObjects.size() == 1) {
			String objectName = "";
			if (selectedObjects.get(0) instanceof MObjectImpl) {
				objectName = "Object Node '" + ((MObject) selectedObjects.get(0)).name() + "'";
			} else if (selectedObjects.get(0) instanceof MLink) {
				objectName = "Link Node '" + ((MLink) selectedObjects.get(0)).association().toString() + "'";
			}
			selectedObjectsText = objectName;
		} else if (selectedObjects.size() > 1) {
			selectedObjectsText = selectedObjects.size() + " Nodes";
		}

		if (!selectedObjects.isEmpty()) {
			popupMenu.insert(new ActionHideCommunicationDiagram("Hide " + selectedObjectsText, selectedObjectsSet, fNodeSelection, fGraph, this), pos++);
			popupMenu.insert(new ActionHideCommunicationDiagram("Crop " + selectedObjectsText, getNoneSelectedNodes(selectedObjectsSet), fNodeSelection,
					fGraph, this), pos++);
			popupMenu.insert(new JSeparator(), pos++);
		}

		final JMenu showHideCrop = new JMenu("Show/hide/crop objects");
		showHideCrop.add(fSelection.getSelectionWithOCLViewAction());
		showHideCrop.add(fSelection.getSelectionObjectView());
		popupMenu.insert(showHideCrop, pos++);

		if (!(hiddenData.objectToNodeMap.isEmpty() && hiddenData.destroyedObjectNodes.isEmpty() && hiddenData.linkToNodeMap.isEmpty() && hiddenData.destroyedLinkNodes
				.isEmpty())) {
			final JMenuItem showAllObjects = new JMenuItem("Show hidden objects");
			showAllObjects.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					showAll();
					invalidateContent(true);
				}
			});
			popupMenu.insert(showAllObjects, pos++);
		}

		if (fGraph.size() > 0) {
			popupMenu.insert(fSelection.getSubMenuHideObject(), pos++);
		}

		if (!(hiddenData.objectToNodeMap.isEmpty() && hiddenData.destroyedObjectNodes.isEmpty())) {
			popupMenu.insert(fSelection.getSubMenuShowObject(), pos++);
		}

		popupMenu.insert(new JSeparator(), pos++);

		// new menu item "Show all life states"
		final JCheckBoxMenuItem objectBoxStatesItem = new JCheckBoxMenuItem("Show all life states");
		objectBoxStatesItem.setState(((CommunicationDiagramOptions) fOpt).isShowLifeStates());
		objectBoxStatesItem.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				((CommunicationDiagramOptions) fOpt).setShowLifeStates(ev.getStateChange() == ItemEvent.SELECTED);
				invalidateContent(true);
			}
		});

		popupMenu.insert(objectBoxStatesItem, pos++);
		popupMenu.insert(new JSeparator(), pos++);

		final JCheckBoxMenuItem messagesItem = new JCheckBoxMenuItem("Show communication messages");
		messagesItem.setState(((CommunicationDiagramOptions) fOpt).isShowCommunicationMessages());
		messagesItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				((CommunicationDiagramOptions) fOpt).setShowCommunicationMessages(ev.getStateChange() == ItemEvent.SELECTED);
				invalidateContent(true);
			}
		});

		popupMenu.insert(messagesItem, pos++);

		final JMenuItem navigationItem = new JMenuItem("Messages navigation");
		navigationItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				naviDialog = new MessagesNavigationDialog(fParent.getCommunicationDiagram());
				naviDialog.setVisible(true);
			}
		});

		popupMenu.insert(navigationItem, pos++);
		popupMenu.insert(new JSeparator(), pos++);

		final JMenuItem fixActorItem;

		if (actorSymbolNode.isUnmovable()) {
			// new menu item "Set actor movable"
			fixActorItem = new JMenuItem("Set actor movable");
			fixActorItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					actorSymbolNode.setStrategy(StrategyFixed.instance);
					actorSymbolNode.setUnmovable(false);
					getStatusBar().showTmpMessage("Actor was set to movable");
				}
			});
		} else {
			// new menu item "Set actor unmovable"
			fixActorItem = new JMenuItem("Set actor unmovable");
			fixActorItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					actorSymbolNode.setStrategy(StrategyUnmovable.instance);
					actorSymbolNode.setUnmovable(true);
					getStatusBar().showTmpMessage("Actor was set to unmovable");
				}
			});
		}

		popupMenu.insert(fixActorItem, pos++);

		final JMenuItem actorNameItem = new JMenuItem("Change actor name...");
		actorNameItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				actorNameDialog = new ActorChangeNameDialog(fParent.getCommunicationDiagram());
				actorNameDialog.setVisible(true);
			}
		});

		popupMenu.insert(actorNameItem, pos++);
		popupMenu.insert(new JSeparator(), pos++);

		popupMenu.add(getMenuItemCommentNode(popupInfo));
        popupMenu.addSeparator();
		popupMenu.add(getMenuAlign());
		popupMenu.add(getMenuItemAntiAliasing());
		popupMenu.add(getMenuItemShowGrid());

		addLayoutMenuItems(popupMenu);

		return popupInfo;
	}

	/**
	 * Finds all nodes which are not selected.
	 * 
	 * @param selectedNodes Nodes which are selected at this point in the
	 *            diagram.
	 * @return A HashSet of the none selected objects in the diagram.
	 */
	private Set<Object> getNoneSelectedNodes(Set<Object> selectedNodes) {
		Set<Object> noneSelectedNodes = new HashSet<Object>();

		Iterator<PlaceableNode> it = fGraph.iterator();
		while (it.hasNext()) {
			PlaceableNode o = it.next();
			if (o instanceof ObjectNodeActivity) {
				MObject obj = ((ObjectNodeActivity) o).object();
				if (!selectedNodes.contains(obj)) {
					noneSelectedNodes.add(obj);
				}
			} else if (o instanceof LinkBoxNode) {
				MLink link = ((LinkBoxNode) o).getLink();
				if (!selectedNodes.contains(link)) {
					noneSelectedNodes.add(link);
				}
			}
		}
		return noneSelectedNodes;
	}

	/**
	 * @param fNodesToHide
	 */
	public void hideElementsInCommunicationDiagram(Set<Object> nodesToHide) {
		Iterator<?> it = nodesToHide.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof MObjectImpl) {
				hideObject((MObjectImpl) obj);
			} else if (obj instanceof MLink) {
				hideLink((MLink) obj);
			}
		}
	}

	/**
	 * Show all objects contained in <code>objects</code>
	 * 
	 * @param objects
	 */
	public void showNodes(Set<Object> objects) {
		for (Object o : objects) {
			showNode(o);
		}
	}

	@Override
	protected void onClosing() {
		super.onClosing();
		fParent.removeKeyListener(inputHandling);
	}

	@Override
	public void resetLayout() {
		fParent.initDiagram(false, (CommunicationDiagramOptions) fOpt);
		fParent.validate();
	}

	@Override
	public Set<? extends PlaceableNode> getHiddenNodes() {
		return hiddenData.getNodes();
	}

	@Override
	public void storePlacementInfos(PersistHelper helper, Element rootElement) {
		actorSymbolNode.storePlacementInfo(helper, rootElement, false);
		storePlacementInfos(helper, rootElement, true);
		storePlacementInfos(helper, rootElement, false);
	}

	/**
	 * @param helper
	 * @param rootElement
	 * @param visible
	 */
	private void storePlacementInfos(PersistHelper helper, Element rootElement, boolean visible) {
		CommunicationDiagramData data = (visible ? visibleData : hiddenData);

		for (LinkBoxNode n : data.linkToNodeMap.values()) {
			n.storePlacementInfo(helper, rootElement, !visible);
		}

		for (LinkBoxNode n : data.destroyedLinkNodes.values()) {
			n.storePlacementInfo(helper, rootElement, !visible);
		}

		for (ObjectBoxNode n : data.objectToNodeMap.values()) {
			n.storePlacementInfo(helper, rootElement, !visible);
		}

		for (ObjectBoxNode n : data.destroyedObjectNodes.values()) {
			n.storePlacementInfo(helper, rootElement, !visible);
		}

		for (CommunicationDiagramEdge e : data.allEdges) {
			e.storePlacementInfo(helper, rootElement, !visible);
		}
	}

	private Set<Object> hiddenObjects = new HashSet<Object>();

	@Override
	public void restorePlacementInfos(PersistHelper helper, int version) {
		if (version < 12)
			return;

		AutoPilot ap = new AutoPilot(helper.getNav());

		restoreUserNode(helper, version, ap);

		restoreCommunicationEdges(helper, version, ap);

		restoreLinkNodes(helper, version, ap);

		restoreObjectNodes(helper, version, ap);

		// Hide elements
		hideElementsInCommunicationDiagram(hiddenObjects);
	}

	private boolean isHidden(PersistHelper helper, int version) {
		return helper.getElementBooleanValue(LayoutTags.HIDDEN);
	}

	/**
	 * @param helper
	 * @param version
	 * @param ap
	 */
	private void restoreObjectNodes(PersistHelper helper, int version, AutoPilot ap) {
		try {
			// Restore object nodes
			ap.selectXPath("./node[@type='Object Node']");
			try {
				while (ap.evalXPath() != -1) {
					String name = helper.getElementStringValue("name");
					ObjectBoxNode obn = null;
					for (ObjectBoxNode o : visibleData.destroyedObjectNodes.values()) {
						if (o.name().equals(name)) {
							obn = o;
							break;
						}
					}

					if (obn != null) {
						obn.restorePlacementInfo(helper, version);
						if (isHidden(helper, version))
							hiddenObjects.add(obn.object());
					} else {
						String nameWithoutSequenceNumber = name.split("\\.")[0];
						MObject obj = fParent.system().state().objectByName(nameWithoutSequenceNumber);
						obn = visibleData.objectToNodeMap.get(obj);
						if (obn != null) {
							obn.restorePlacementInfo(helper, version);
							if (isHidden(helper, version))
								hiddenObjects.add(obn.object());
						}
					}
				}
			} catch (XPathEvalException e) {
				fLog.append(e.getMessage());
			} catch (NavException e) {
				fLog.append(e.getMessage());
			}
		} catch (XPathParseException e) {
			fLog.append(e.getMessage());
		}
	}

	/**
	 * @param helper
	 * @param version
	 * @param ap
	 */
	private void restoreLinkNodes(PersistHelper helper, int version, AutoPilot ap) {
		try {
			ap.selectXPath("./node[@type='Link Node']");
			try {
				while (ap.evalXPath() != -1) {
					String name = helper.getElementStringValue("name");
					LinkBoxNode lbn = null;
					for (LinkBoxNode l : visibleData.destroyedLinkNodes.values()) {
						if (l.name().equals(name)) {
							lbn = l;
							break;
						}
					}

					if (lbn != null) {
						lbn.restorePlacementInfo(helper, version);
						if (isHidden(helper, version))
							hiddenObjects.add(lbn.getLink());
					} else {
						String nameWithoutSequenceNumber = name.split("\\.")[0];
						for (MLink link : visibleData.linkToNodeMap.keySet()) {
							if (link.toString().equals(nameWithoutSequenceNumber)) {
								lbn = visibleData.linkToNodeMap.get(link);
								lbn.restorePlacementInfo(helper, version);
								if (isHidden(helper, version))
									hiddenObjects.add(lbn.getLink());
								break;
							}
						}
					}
				}
			} catch (XPathEvalException e) {
				fLog.append(e.getMessage());
			} catch (NavException e) {
				fLog.append(e.getMessage());
			}
		} catch (XPathParseException e) {
			fLog.append(e.getMessage());
		}
	}

	/**
	 * @param helper
	 * @param version
	 * @param ap
	 */
	private void restoreCommunicationEdges(PersistHelper helper, int version, AutoPilot ap) {
		try {
			// Restore edges
			ap.selectXPath("./edge[@type='Communication Edge']");
			try {
				while (ap.evalXPath() != -1) {
					String edgeName = helper.getElementStringValue("name");

					for (EdgeBase edge : visibleData.getEdges()) {
						if (edge.getName().equals(edgeName)) {
							edge.restorePlacementInfo(helper, version);
							break;
						}
					}
				}
			} catch (XPathEvalException e) {
				fLog.append(e.getMessage());
			} catch (NavException e) {
				fLog.append(e.getMessage());
			}
		} catch (XPathParseException e) {
			fLog.append(e.getMessage());
		}
	}

	/**
	 * @param helper
	 * @param version
	 * @param ap
	 */
	private void restoreUserNode(PersistHelper helper, int version, AutoPilot ap) {
		try {
			ap.selectXPath("./node[@type='User Node']");
			try {
				while (ap.evalXPath() != -1) {
					String name = helper.getElementStringValue("name");
					ActorNode node = actorSymbolNode;
					if (!name.equals("Actor")) {
						node.getActorData().setUserName(name);
					}
					node.restorePlacementInfo(helper, version);
				}
			} catch (XPathEvalException e) {
				fLog.append(e.getMessage());
			} catch (NavException e) {
				fLog.append(e.getMessage());
			}

		} catch (XPathParseException e) {
			fLog.append(e.getMessage());
		}
	}
	
	/**
	 * @return the activatedEdge
	 */
	public CommunicationDiagramEdge getActivatedEdge() {
		return activatedEdge;
	}

	/**
	 * @param activatedEdge the activatedEdge to set
	 */
	public void setActivatedEdge(CommunicationDiagramEdge activatedEdge) {
		this.activatedEdge = activatedEdge;
	}

	/**
	 * @return the activatedMessage
	 */
	public MessagesGroup getActivatedMessage() {
		return activatedMessage;
	}

	/**
	 * @param activatedMessage the activatedMessage to set
	 */
	public void setActivatedMessage(MessagesGroup activatedMessage) {
		this.activatedMessage = activatedMessage;
	}

	void showOrHideActivatedMessage(boolean on) {
		if (on) {
			if (activatedMessage != null) {
				activatedMessage.setActivatedMessageColor(ACTIVATED_MESSAGE_COLOR);
				repaint();
			}
		} else {
			if (activatedMessage != null) {
				activatedMessage.setActivatedMessageColor(new Color(255, 255, 255, 0));
				repaint();
			}
		}
	}

	CommunicationDiagramEdge getEdge(MMessage message) {
		if (message.getSequenceNumber().equals("")) {
			return null;
		}
		for (CommunicationDiagramEdge cde : visibleData.allEdges) {
			if (cde.getMessages().contains(message))
				return cde;
		}
		return null;
	}

	public Vector<MMessage> getMessages() {
		return messages;
	}

	void changeActorName(String newName) {
		actorSymbolNode.getActorData().setUserName(newName);
	}

	public String getActorName() {
		return actorSymbolNode.getActorData().getUserName();
	}

	/**
	 * This is called by the LayoutThread to generate a new layout. The layouter
	 * object can be reused as long as the graph and the size of the drawing
	 * area does not change.
	 */
	@SuppressWarnings("unchecked")
	protected synchronized void autoLayout() {
		if (fLayouter == null) {
			int w = getWidth();
			int h = getHeight();
			if (w == 0 || h == 0)
				return;
			fLayouter = new CommunicationDiagramLayout<PlaceableNode>(fGraph, w, h, 20, 20);
		}
		fLayouter.layout();
		repaint();
	}

	@Override
	public void hideObject(MObject obj) {
		showOrHideCreateEdgeToObjectBoxNode(obj, false);
		showOrHideObjectBoxNode(obj, false);
		this.invalidateContent(false);
	}

	private void showNode(Object obj) {
		if (obj instanceof MObject) {
			showObject((MObject) obj);
		} else if (obj instanceof MLink) {
			showLink((MLink) obj);
		}
	}

	private void showLink(MLink link) {
		LinkBoxNode lbn = hiddenData.linkToNodeMap.get(link);

		if (lbn == null) {
			lbn = hiddenData.destroyedLinkNodes.get(link);
		}

		if (lbn != null) {
			CommunicationDiagramEdge createLinkEdge = hiddenData.linkToCreateEdgeMap.get(link);
			List<CommunicationDiagramEdge> cdesList = hiddenData.linkToAssociationEdges.get(link);

			fGraph.add(lbn);

			if (createLinkEdge != null) {
				if (fGraph.contains(createLinkEdge.source())) {
					fGraph.addEdge(createLinkEdge);
				}
				visibleData.linkToCreateEdgeMap.put(link, createLinkEdge);
				hiddenData.linkToCreateEdgeMap.remove(link);
			}

			if (cdesList != null) {
				for (int i = 0; i < cdesList.size(); i++) {
					if (fGraph.contains(cdesList.get(i).target()) && fGraph.contains(cdesList.get(i).source())) {
						fGraph.addEdge(cdesList.get(i));
						if (visibleData.linkToAssociationEdges.get(link) == null) {
							visibleData.linkToAssociationEdges.put(link, new ArrayList<CommunicationDiagramEdge>());
							visibleData.linkToAssociationEdges.get(link).add(cdesList.get(i));
						} else {
							visibleData.linkToAssociationEdges.get(link).add(cdesList.get(i));
						}
						hiddenData.linkToAssociationEdges.get(link).remove(cdesList.get(i));
					}
				}
			}

			visibleData.linkToNodeMap.put(link, lbn);
			hiddenData.linkToNodeMap.remove(link);
			fLayouter = null;
		} else {
			List<CommunicationDiagramEdge> cdesList = visibleData.linkToAssociationEdges.get(link);
			for (CommunicationDiagramEdge cde : cdesList) {
				if (fGraph.contains(cde.target()) && fGraph.contains(cde.source()))
					fGraph.addEdge(cde);
			}
		}
	}

	private void hideLink(MLink link) {
		LinkBoxNode lbn = visibleData.linkToNodeMap.get(link);

		if (lbn == null) {
			lbn = visibleData.destroyedLinkNodes.get(link);
		}

		if (lbn != null) {
			CommunicationDiagramEdge createLinkEdge = visibleData.linkToCreateEdgeMap.get(link);
			List<CommunicationDiagramEdge> cdesList = visibleData.linkToAssociationEdges.get(link);

			// if link was not created by actor
			if (createLinkEdge != null) {
				fGraph.removeEdge(createLinkEdge);
				hiddenData.linkToCreateEdgeMap.put(link, createLinkEdge);
			}

			if (cdesList != null) {
				for (CommunicationDiagramEdge cde : cdesList) {
					fGraph.removeEdge(cde);
				}
				hiddenData.linkToAssociationEdges.put(link, cdesList);
			}

			fGraph.remove(lbn);
			hiddenData.linkToNodeMap.put(link, lbn);
			visibleData.linkToNodeMap.remove(link);
			fLayouter = null;
		}
	}

	@Override
	public void showObject(MObject obj) {
		showOrHideObjectBoxNode(obj, true);
		showOrHideCreateEdgeToObjectBoxNode(obj, true);

		List<MLink> allLinks = new ArrayList<MLink>();

		for (MLink link : hiddenData.linkToCreateEdgeMap.keySet()) {
			allLinks.add(link);
		}
		for (MLink link : visibleData.linkToCreateEdgeMap.keySet()) {
			allLinks.add(link);
		}

		for (MLink link : allLinks) {
			if (link.linkedObjects().contains(obj)) {
				for (MObject linkedO : link.linkedObjects()) {
					if (visibleData.objectToNodeMap.containsKey(linkedO) || visibleData.destroyedObjectNodes.containsKey(linkedO)) {
						showLink(link);
						break;
					}
				}
			}
		}

		showOutAndInGoingEdges(obj);
		this.invalidateContent(false);
	}

	private void showOrHideObjectBoxNode(MObject obj, boolean show) {
		CommunicationDiagramData source = (show ? hiddenData : visibleData);
		CommunicationDiagramData target = (show ? visibleData : hiddenData);

		boolean objIsDestroyed = false;

		ObjectBoxNode obn = source.objectToNodeMap.get(obj);
		if (obn == null) {
			obn = source.destroyedObjectNodes.get(obj);
			objIsDestroyed = true;
		}
		CommunicationDiagramEdge cde = source.objectToReflexEdgeMap.get(obj);

		if (obn != null) {
			if (show) {
				fGraph.add(obn);
				if (cde != null) {
					// If the node had reflexive edge then show it.
					fGraph.addEdge(cde);
				}
			} else {
				fGraph.remove(obn);
			}

			if (cde != null) {
				target.objectToReflexEdgeMap.put(obj, cde);
				source.objectToReflexEdgeMap.remove(obj);
			}

			if (objIsDestroyed) {
				target.destroyedObjectNodes.put(obj, obn);
				source.destroyedObjectNodes.remove(obj);
			} else {
				target.objectToNodeMap.put(obj, obn);
				source.objectToNodeMap.remove(obj);
			}

			fLayouter = null;
		}
	}

	/**
	 * @param obj
	 * @param b
	 */
	private void showOrHideCreateEdgeToObjectBoxNode(MObject obj, boolean show) {
		CommunicationDiagramData source = (show ? hiddenData : visibleData);
		CommunicationDiagramData target = (show ? visibleData : hiddenData);

		CommunicationDiagramEdge cde = source.objectToCreateEdgeMap.get(obj);

		if (cde != null) {
			if (show) {
				if (fGraph.getNodes().contains(cde.source()))
					fGraph.addEdge(cde);
			} else {
				fGraph.removeEdge(cde);
			}

			target.objectToCreateEdgeMap.put(obj, cde);
			source.objectToCreateEdgeMap.remove(obj);
			fLayouter = null;
		}
	}

	private void showOutAndInGoingEdges(MObject obj) {
		ObjectBoxNode obn = visibleData.objectToNodeMap.get(obj);
		if (obn == null) {
			obn = visibleData.destroyedObjectNodes.get(obj);
		}
		if (obn != null)
			for (ObjectBoxNode node : visibleData.objectToNodeMap.values()) {
				if (fGraph.getNodes().contains(node)) {
					for (CommunicationDiagramEdge cde : visibleData.allEdges) {
						if ((cde.source().equals(obn) && cde.target().equals(node)) || (cde.target().equals(obn) && cde.source().equals(node))) {
							fGraph.addEdge(cde);
						}
					}
				}

			}
	}

	@Override
	public void showAll() {
		while (!hiddenData.objectToNodeMap.isEmpty()) {
			showObject(hiddenData.objectToNodeMap.keySet().iterator().next());
		}

		for (ObjectBoxNode obn : hiddenData.destroyedObjectNodes.values()) {
			fGraph.add(obn);
		}

		for (LinkBoxNode lbn : hiddenData.linkToNodeMap.values()) {
			fGraph.add(lbn);
		}

		for (LinkBoxNode lbn : hiddenData.destroyedLinkNodes.values()) {
			fGraph.add(lbn);
		}

		for (CommunicationDiagramEdge cde : visibleData.allEdges) {
			if (!fGraph.getEdges().contains(cde)) {
				fGraph.addEdge(cde);
			}
		}

		this.hiddenData.copyTo(this.visibleData);
		this.hiddenData.clear();
	}

	/**
	 * Hides all currently visible elements. The diagram is not repainted!
	 */
	@Override
	public void hideAll() {
		// Copy all elements to hidden and remove all elements from graph
		this.visibleData.copyTo(this.hiddenData);
		this.visibleData.clear();
		this.fGraph.clear();
		this.fGraph.add(this.actorSymbolNode);
		this.invalidateContent(true);
	}

	@Override
	protected String getDefaultLayoutFileSuffix() {
		return "_comdia.clt";
	}

	@Override
	public DiagramData getVisibleData() {
		return visibleData;
	}

	@Override
	public DiagramData getHiddenData() {
		return hiddenData;
	}

	public String getSequenceNumber() {
		String sequenceNumber = sequenceNumbers.get(0).toString();
		for (int i = 1; i < sequenceNumbers.size(); i++) {
			sequenceNumber += "." + sequenceNumbers.get(i);
		}
		return sequenceNumber;
	}

	private void raiseSequenceNumber() {
		if (sequenceNumbers.size() > 0) {
			int lastNumber = sequenceNumbers.get(sequenceNumbers.size() - 1);
			sequenceNumbers.set(sequenceNumbers.size() - 1, lastNumber + 1);
		}
	}

	protected void addObject(MObject obj) {
		PlaceableNode callOpNode;
		boolean isCallOpNodeVisible = true;

		if (operationsStack.isEmpty()) {
			callOpNode = actorSymbolNode;
		} else {
			MObject callOpObject = operationsStack.peek().getSelf();
			if (visibleData.objectToNodeMap.containsKey(callOpObject)) {
				callOpNode = visibleData.objectToNodeMap.get(callOpObject);
			} else {
				callOpNode = hiddenData.objectToNodeMap.get(callOpObject);
				isCallOpNodeVisible = false;
			}
		}

		ObjectBoxNode newObjectBoxNode = new ObjectBoxNode(obj, getSequenceNumber(), fParent, fOpt);
		newObjectBoxNode.addState(" { new } ");
		newObjectBoxNode.calculateBounds();
		newObjectBoxNode.setPosition(nextNodePosition);
		computeNextRandomPoint();

		MMessage mess = new MMessage(getSequenceNumber(), "create");
		messages.add(mess);
		raiseSequenceNumber();

		CommunicationDiagramEdge cde = CommunicationDiagramEdge.create(callOpNode, newObjectBoxNode, this, false);
		cde.addNewMessage(mess);

		fGraph.add(newObjectBoxNode);

		if (isCallOpNodeVisible) {
			fGraph.addEdge(cde);
			visibleData.objectToCreateEdgeMap.put(obj, cde);
		} else {
			hiddenData.objectToCreateEdgeMap.put(obj, cde);
		}
		visibleData.allEdges.add(cde);
		visibleData.objectToNodeMap.put(obj, newObjectBoxNode);
		fLayouter = null;
	}

	/**
	 * @param obj
	 */
	protected void deleteObject(MObject obj) {
		PlaceableNode callOpNode;
		ObjectBoxNode objectNodeToDestroy;
		boolean isCallOpNodeVisible = true;
		boolean isObjectNodeToDestroyVisible = true;

		if (operationsStack.isEmpty()) {
			callOpNode = actorSymbolNode;
		} else {
			MObject callOpObject = operationsStack.peek().getSelf();
			if (visibleData.objectToNodeMap.containsKey(callOpObject)) {
				callOpNode = visibleData.objectToNodeMap.get(callOpObject);
			} else {
				callOpNode = hiddenData.objectToNodeMap.get(callOpObject);
				isCallOpNodeVisible = false;
			}
		}

		if (visibleData.objectToNodeMap.containsKey(obj)) {
			objectNodeToDestroy = visibleData.objectToNodeMap.get(obj);
		} else {
			objectNodeToDestroy = hiddenData.objectToNodeMap.get(obj);
			isObjectNodeToDestroyVisible = false;
		}

		// Case of redo
		if (objectNodeToDestroy == null)
			return;

		MMessage mess = new MMessage(getSequenceNumber(), "destroy");
		messages.add(mess);
		raiseSequenceNumber();

		objectNodeToDestroy.updateState(" { destroyed } ");

		CommunicationDiagramEdge existedEdge = null;

		for (EdgeBase edge : visibleData.allEdges) {
			if ((edge.source().equals(callOpNode) && edge.target().equals(objectNodeToDestroy))
					|| (edge.source().equals(objectNodeToDestroy) && edge.target().equals(callOpNode))) {
				existedEdge = (CommunicationDiagramEdge) edge;
				break;
			}
		}

		if (existedEdge == null) {
			existedEdge = new CommunicationDiagramEdge(callOpNode, objectNodeToDestroy, this, false);
			existedEdge.addNewMessage(mess);
			if (isCallOpNodeVisible && isObjectNodeToDestroyVisible) {
				fGraph.addEdge(existedEdge);
			}
			visibleData.allEdges.add(existedEdge);
		} else {
			existedEdge.addNewMessage(mess);
		}

		if (isObjectNodeToDestroyVisible) {
			visibleData.destroyedObjectNodes.put(obj, objectNodeToDestroy);
			visibleData.objectToNodeMap.remove(obj);
		} else {
			hiddenData.destroyedObjectNodes.put(obj, objectNodeToDestroy);
			hiddenData.objectToNodeMap.remove(obj);
		}
		fLayouter = null;
	}

	/**
	 * @param link
	 */
	protected void addLink(MLink link) {
		PlaceableNode callOpNode;
		boolean isCallOpNodeVisible = true;

		if (operationsStack.isEmpty()) {
			callOpNode = actorSymbolNode;
		} else {
			MObject callOpObject = operationsStack.peek().getSelf();
			if (visibleData.objectToNodeMap.containsKey(callOpObject)) {
				callOpNode = visibleData.objectToNodeMap.get(callOpObject);
			} else {
				callOpNode = hiddenData.objectToNodeMap.get(callOpObject);
				isCallOpNodeVisible = false;
			}
		}

		boolean linkNodeIsCreated = false;
		boolean isLinkNodeVisible = true;

		if (visibleData.linkToNodeMap.containsKey(link) || hiddenData.linkToNodeMap.containsKey(link)) {
			linkNodeIsCreated = true;
			if (hiddenData.linkToNodeMap.containsKey(link)) {
				isLinkNodeVisible = false;
			}
		}

		if (!linkNodeIsCreated) {
			LinkBoxNode newLinkNode = new LinkBoxNode(link, getSequenceNumber(), fParent, fOpt);
			newLinkNode.addState(" { new } ");
			newLinkNode.setPosition(nextNodePosition);
			fGraph.add(newLinkNode);

			CommunicationDiagramEdge cde;
			cde = CommunicationDiagramEdge.create(callOpNode, newLinkNode, this, false);
			String messageLabel = "insert(";

			List<CommunicationDiagramEdge> edges = new ArrayList<CommunicationDiagramEdge>();

			for (MObject obj : link.linkedObjects()) {
				BaseNode linkedNode;
				boolean islinkedNodeVisible = false;

				if (obj instanceof MObjectImpl) {
					if (visibleData.objectToNodeMap.containsKey(obj)) {
						islinkedNodeVisible = true;
						linkedNode = visibleData.objectToNodeMap.get(obj);
					} else {
						linkedNode = hiddenData.objectToNodeMap.get(obj);
					}
				} else {
					if (visibleData.linkToNodeMap.containsKey(obj)) {
						islinkedNodeVisible = true;
						linkedNode = visibleData.linkToNodeMap.get(obj);
					} else {
						linkedNode = hiddenData.linkToNodeMap.get(obj);
					}
				}

				messageLabel += String.format("@%s,", obj.name());
				if (linkedNode.equals(callOpNode)) {
					continue;
				}
				CommunicationDiagramEdge linkEdge = CommunicationDiagramEdge.create(newLinkNode, linkedNode, this, false);
				if (islinkedNodeVisible)
					fGraph.addEdge(linkEdge);
				edges.add(linkEdge);
			}

			messageLabel = messageLabel.substring(0, messageLabel.length() - 1) + ")";

			MMessage mess = new MMessage(getSequenceNumber(), messageLabel);
			messages.add(mess);
			cde.addNewMessage(mess);

			if (isCallOpNodeVisible) {
				fGraph.addEdge(cde);
				visibleData.linkToCreateEdgeMap.put(link, cde);
				visibleData.linkToAssociationEdges.put(link, edges);
			} else {
				hiddenData.linkToCreateEdgeMap.put(link, cde);
				hiddenData.linkToAssociationEdges.put(link, edges);
			}
			visibleData.linkToNodeMap.put(link, newLinkNode);
			visibleData.allEdges.addAll(edges);
			visibleData.allEdges.add(cde);
			fLayouter = null;
		} else {
			CommunicationDiagramEdge cde;
			LinkBoxNode createdLinkNode;

			if (isLinkNodeVisible) {
				cde = visibleData.linkToCreateEdgeMap.get(link);
				createdLinkNode = visibleData.linkToNodeMap.get(link);
			} else {
				cde = hiddenData.linkToCreateEdgeMap.get(link);
				createdLinkNode = hiddenData.linkToNodeMap.get(link);
			}

			MMessage mess = new MMessage(getSequenceNumber(), cde.getMessages().get(0).getMessageName());
			messages.add(mess);
			cde.addNewMessage(mess);
			createdLinkNode.addState(" { new } ");
			fLayouter = null;
		}
		raiseSequenceNumber();
	}

	/**
	 * Removes a link from the diagram.
	 */
	protected void deleteLink(MLink link) {
		PlaceableNode callOpNode;
		LinkBoxNode linkNodeToDelete;
		boolean isCallOpNodeVisible = true;
		boolean isLinkNodeToDeleteVisible = true;

		if (operationsStack.isEmpty()) {
			callOpNode = actorSymbolNode;
		} else {
			MObject callOpObject = operationsStack.peek().getSelf();
			if (visibleData.objectToNodeMap.containsKey(callOpObject)) {
				callOpNode = visibleData.objectToNodeMap.get(callOpObject);
			} else {
				callOpNode = hiddenData.objectToNodeMap.get(callOpObject);
				isCallOpNodeVisible = false;
			}
		}

		if (visibleData.linkToNodeMap.containsKey(link)) {
			linkNodeToDelete = visibleData.linkToNodeMap.get(link);
		} else {
			linkNodeToDelete = hiddenData.linkToNodeMap.get(link);
			isLinkNodeToDeleteVisible = false;
		}

		// Case of redo
		if (linkNodeToDelete == null)
			return;

		MMessage mess = new MMessage(getSequenceNumber(), "delete");
		messages.add(mess);
		raiseSequenceNumber();

		linkNodeToDelete.updateState(" { transient } ");
		CommunicationDiagramEdge existedEdge = null;

		for (EdgeBase edge : visibleData.allEdges) {
			if ((edge.source().equals(callOpNode) && edge.target().equals(linkNodeToDelete))
					|| (edge.source().equals(linkNodeToDelete) && edge.target().equals(callOpNode))) {
				existedEdge = (CommunicationDiagramEdge) edge;
				break;
			}
		}

		if (existedEdge == null) {
			existedEdge = new CommunicationDiagramEdge(callOpNode, linkNodeToDelete, this, false);
			existedEdge.addNewMessage(mess);
			if (isCallOpNodeVisible && isLinkNodeToDeleteVisible) {
				fGraph.addEdge(existedEdge);
			}
			visibleData.allEdges.add(existedEdge);
		} else {
			existedEdge.addNewMessage(mess);
		}

		if (isLinkNodeToDeleteVisible) {
			visibleData.destroyedLinkNodes.put(link, linkNodeToDelete);
		} else {
			hiddenData.destroyedLinkNodes.put(link, linkNodeToDelete);
		}
		fLayouter = null;
	}

	protected void assignAttribute(MObject object, MAttribute attribute, Value value) {
		PlaceableNode callOpNode;
		BaseNode objectNodeToAssign;
		boolean isCallOpNodeVisible = true;
		boolean isObjectNodeToAssignVisible = true;

		if (operationsStack.isEmpty()) {
			callOpNode = actorSymbolNode;
		} else {
			MObject callOpObject = operationsStack.peek().getSelf();

			if (callOpObject instanceof MObjectImpl) {
				if (visibleData.objectToNodeMap.containsKey(callOpObject)) {
					callOpNode = visibleData.objectToNodeMap.get(callOpObject);
				} else {
					callOpNode = hiddenData.objectToNodeMap.get(callOpObject);
					isCallOpNodeVisible = false;
				}
			} else {
				if (visibleData.linkToNodeMap.containsKey(callOpObject)) {
					callOpNode = visibleData.linkToNodeMap.get(callOpObject);
				} else {
					callOpNode = hiddenData.linkToNodeMap.get(callOpObject);
					isCallOpNodeVisible = false;
				}
			}
		}

		if (object instanceof MObjectImpl) {
			if (visibleData.objectToNodeMap.containsKey(object)) {
				objectNodeToAssign = visibleData.objectToNodeMap.get(object);
			} else {
				objectNodeToAssign = hiddenData.objectToNodeMap.get(object);
				isObjectNodeToAssignVisible = false;
			}
		} else if (object instanceof MLinkObjectImpl) {
			if (visibleData.linkToNodeMap.containsKey(object)) {
				objectNodeToAssign = visibleData.linkToNodeMap.get(object);
			} else {
				objectNodeToAssign = hiddenData.linkToNodeMap.get(object);
				isObjectNodeToAssignVisible = false;
			}
		} else {
			return;
		}

		MMessage mess = new MMessage(getSequenceNumber(), String.format("set %s := %s", attribute.name(), value.toString()));
		messages.add(mess);
		raiseSequenceNumber();
		CommunicationDiagramEdge existedEdge = null;

		if (callOpNode.equals(objectNodeToAssign)) {
			if (isCallOpNodeVisible) {
				existedEdge = visibleData.objectToReflexEdgeMap.get(object);
			} else {
				existedEdge = hiddenData.objectToReflexEdgeMap.get(object);
			}

			if (existedEdge == null) {
				existedEdge = new CommunicationDiagramEdge(callOpNode, callOpNode, this, false);
				if (isCallOpNodeVisible) {
					visibleData.objectToReflexEdgeMap.put(object, existedEdge);
					fGraph.addEdge(existedEdge);
				} else {
					hiddenData.objectToReflexEdgeMap.put(object, existedEdge);
				}
				visibleData.allEdges.add(existedEdge);
			}
		} else {
			for (CommunicationDiagramEdge cde : visibleData.allEdges) {
				if ((cde.source().equals(callOpNode) && cde.target().equals(objectNodeToAssign))
						|| (cde.source().equals(objectNodeToAssign) && cde.target().equals(callOpNode))) {
					existedEdge = cde;
					break;
				}
			}

			if (existedEdge == null) {
				existedEdge = new CommunicationDiagramEdge(callOpNode, objectNodeToAssign, this, false);
				if (isCallOpNodeVisible && isObjectNodeToAssignVisible) {
					fGraph.addEdge(existedEdge);
				}
				visibleData.allEdges.add(existedEdge);
			}
		}
		existedEdge.addNewMessage(mess);
		fLayouter = null;
	}

	void enterOperation(MOperationCall operationCall) {
		operationsStack.add(operationCall);
		MOperation operation = operationCall.getOperation();

		StringBuilder msgLabel = new StringBuilder();
		msgLabel.append(operation.name());
		msgLabel.append("(");
		StringUtil.fmtSeq(msgLabel, operationCall.getArgumentsAsNamesAndValues().values(), ",");
		msgLabel.append(")");
		
		MMessage mess = new MMessage(getSequenceNumber(), msgLabel.toString());
		messages.add(mess);
		MObject enterOpObject = operationCall.getSelf();

		BaseNode enterOpNode;
		boolean isEnterOpNodeVisible = true;

		if (enterOpObject instanceof MObjectImpl) {
			if (visibleData.objectToNodeMap.containsKey(enterOpObject)) {
				enterOpNode = visibleData.objectToNodeMap.get(enterOpObject);
			} else {
				enterOpNode = hiddenData.objectToNodeMap.get(enterOpObject);
				isEnterOpNodeVisible = false;
			}
		} else if (enterOpObject instanceof MLinkObjectImpl) {
			if (visibleData.linkToNodeMap.containsKey(enterOpObject)) {
				enterOpNode = visibleData.linkToNodeMap.get(enterOpObject);
			} else {
				enterOpNode = hiddenData.linkToNodeMap.get(enterOpObject);
				isEnterOpNodeVisible = false;
			}
		} else {
			return;
		}

		CommunicationDiagramEdge existedEdge = null;

		if (!operationsCaller.isEmpty()) {
			MObject callOpObject = operationsCaller.get(operationsCaller.size() - 1);
			ObjectBoxNode callOpNode;
			boolean isCallOpNodeVisible = true;

			if (visibleData.objectToNodeMap.containsKey(callOpObject)) {
				callOpNode = visibleData.objectToNodeMap.get(callOpObject);
			} else {
				callOpNode = hiddenData.objectToNodeMap.get(callOpObject);
				isCallOpNodeVisible = false;
			}

			boolean existedEdgeBetweenSourceAndTarget = false;
			CommunicationDiagramEdge edgeBetweenSourceTarget = null;

			for (CommunicationDiagramEdge cde : visibleData.allEdges) {
				if ((cde.source().equals(callOpNode) && cde.target().equals(enterOpNode))
						|| (cde.source().equals(enterOpNode) && cde.target().equals(callOpNode))) {
					existedEdgeBetweenSourceAndTarget = true;
					edgeBetweenSourceTarget = cde;
					break;
				}
			}

			if (!existedEdgeBetweenSourceAndTarget) {
				existedEdge = new CommunicationDiagramEdge(callOpNode, enterOpNode, this, false);
				if (callOpNode.equals(enterOpNode)) {
					if (isCallOpNodeVisible) {
						visibleData.objectToReflexEdgeMap.put(enterOpObject, existedEdge);
					} else {
						hiddenData.objectToReflexEdgeMap.put(enterOpObject, existedEdge);
					}
				}
				if (isEnterOpNodeVisible && isCallOpNodeVisible) {
					fGraph.addEdge(existedEdge);
				}
				visibleData.allEdges.add(existedEdge);
			} else {
				existedEdge = edgeBetweenSourceTarget;
				if (!existedEdge.source().equals(callOpNode)) {
					mess.setDirection(MMessage.BACKWARD);
				}
			}
		} else {
			if (isEnterOpNodeVisible) {
				if (enterOpObject instanceof MObjectImpl) {
					existedEdge = visibleData.objectToCreateEdgeMap.get(enterOpObject);
				} else {
					existedEdge = visibleData.linkToCreateEdgeMap.get(enterOpObject);
				}
			} else {
				if (enterOpObject instanceof MObjectImpl) {
					existedEdge = hiddenData.objectToCreateEdgeMap.get(enterOpObject);
				} else {
					existedEdge = hiddenData.linkToCreateEdgeMap.get(enterOpObject);
				}
			}
		}

		operationsCaller.add(enterOpObject);
		existedEdge.addNewMessage(mess);
		fLayouter = null;
		sequenceNumbers.add(1);
	}

	/**
	 * @param operationCall
	 */
	void exitOperation(MOperationCall operationCall) {
		MObject obj = operationCall.getSelf();
		ObjectBoxNode obn;
		boolean isObjectNodeOfOperationCallVisible = true;

		if (visibleData.objectToNodeMap.containsKey(obj)) {
			obn = visibleData.objectToNodeMap.get(obj);
		} else {
			obn = hiddenData.objectToNodeMap.get(obj);
			isObjectNodeOfOperationCallVisible = false;
		}

		CommunicationDiagramEdge existedEdge = null;
		if (!operationsCaller.isEmpty()) {
			operationsCaller.remove(operationsCaller.size() - 1);
		}

		if (operationCall.getResultValue() != null) {
			MMessage mess = new MMessage(getSequenceNumber(), operationCall.getResultValue().toString());
			mess.setDirection(MMessage.RETURN);
			if (!operationCall.exitedSuccessfully()) {
				mess.setFailedReturn(true);
			}
			messages.add(mess);

			if (!operationsCaller.isEmpty()) {

				MObject sourceObject = operationsCaller.get(operationsCaller.size() - 1);
				ObjectBoxNode sourceNode;
				boolean isOwnerNodeVisible = true;

				if (visibleData.objectToNodeMap.containsKey(sourceObject)) {
					sourceNode = visibleData.objectToNodeMap.get(sourceObject);
				} else {
					sourceNode = hiddenData.objectToNodeMap.get(sourceObject);
					isOwnerNodeVisible = false;
				}

				boolean existedEdgeBetweenSourceAndTarget = false;
				CommunicationDiagramEdge edgeBetweenSourceTarget = null;

				for (CommunicationDiagramEdge cde : visibleData.allEdges) {
					if ((cde.source().equals(sourceNode) && cde.target().equals(obn)) || (cde.source().equals(obn) && cde.target().equals(sourceNode))) {
						existedEdgeBetweenSourceAndTarget = true;
						edgeBetweenSourceTarget = cde;
						break;
					}
				}

				if (!existedEdgeBetweenSourceAndTarget) {
					existedEdge = new CommunicationDiagramEdge(sourceNode, obn, this, false);
					if (sourceNode.equals(obn)) {
						if (isOwnerNodeVisible) {
							visibleData.objectToReflexEdgeMap.put(obj, existedEdge);
							fGraph.addEdge(existedEdge);
						} else {
							hiddenData.objectToReflexEdgeMap.put(obj, existedEdge);
						}
					}
					visibleData.allEdges.add(existedEdge);
				} else {
					if (edgeBetweenSourceTarget != null) {
						existedEdge = edgeBetweenSourceTarget;
					}
				}
			} else {
				if (isObjectNodeOfOperationCallVisible) {
					existedEdge = visibleData.objectToCreateEdgeMap.get(obj);
				} else {
					existedEdge = hiddenData.objectToCreateEdgeMap.get(obj);
				}
			}
			existedEdge.addNewMessage(mess);
			fLayouter = null;
		}

		if (!operationsStack.isEmpty()) {
			operationsStack.pop();
		}
		sequenceNumbers.remove(sequenceNumbers.size() - 1);
		raiseSequenceNumber();
	}

}