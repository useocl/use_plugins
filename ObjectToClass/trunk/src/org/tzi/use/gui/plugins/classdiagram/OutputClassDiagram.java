package org.tzi.use.gui.plugins.classdiagram;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.gui.views.diagrams.classdiagram.ClassDiagram;
import org.tzi.use.gui.views.diagrams.elements.PlaceableNode;
import org.tzi.use.gui.views.diagrams.elements.edges.BinaryAssociationOrLinkEdge;
import org.tzi.use.gui.main.ModelBrowserSorting;
import org.tzi.use.gui.plugins.Utilities;
import org.tzi.use.gui.plugins.data.TAssociation;
import org.tzi.use.gui.plugins.data.TAttribute;
import org.tzi.use.gui.plugins.data.TClass;
import org.tzi.use.gui.plugins.data.TLogicException;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MMultiplicity;

@SuppressWarnings("serial")
public class OutputClassDiagram extends ClassDiagram {

	private OutputClassDiagramView fParent;
	private UseModelApi modelApi;
	private Map<Integer, String> tClassIDToMClassName;
	private Map<MAssociation, TAssociation> mAssocToTAssocMap;

	OutputClassDiagram(OutputClassDiagramView parent, PrintWriter log, OutputClassDiagramOptions opt) {
		super(parent, log, opt);
		fParent = parent;
		
		opt.setShowAssocNames(true);
		
		modelApi = new UseModelApi("ObjectToClassModel");
		tClassIDToMClassName = new HashMap<Integer, String>();
		mAssocToTAssocMap = new HashMap<MAssociation, TAssociation>();
	}

	void addClass(TClass cls) {
		// Find a random new position. getWidth and getHeight return 0
		// if we are called on a new diagram.
		int fNextNodeX = (int) (Math.random() * Math.max(100, fParent.getWidth() - 50));
		int fNextNodeY = (int) (Math.random() * Math.max(100, fParent.getHeight() - 50));

		MClass tentativeClass = getTentativeClass(cls);
		OutputClassNode n = new OutputClassNode(tentativeClass, fOpt, cls);
		n.setPosition(fNextNodeX, fNextNodeY);

		n.setMinWidth(minClassNodeWidth);
		n.setMinHeight(minClassNodeHeight);

		fGraph.add(n);
		visibleData.fClassToNodeMap.put(tentativeClass, n);
		fLayouter = null;
	}

	private MClass getTentativeClass(TClass cls) {
		try {
			String clsName = cls.getClassName();
			if (clsName == null) {
				clsName = "nullClassName" + cls.getID();
			}
			MClass ret = modelApi.createClass(clsName, false);
			int nullAttrNameCounter = 0;
			for (TAttribute a : cls.getAttributes()) {
				String attrName = a.getName();
				if (attrName == null) {
					attrName = "nullAttrName" + nullAttrNameCounter++;
				}
				modelApi.createAttribute(clsName, attrName, a.getSingleType().toString());
			}
			tClassIDToMClassName.put(cls.getID(), clsName);
			return ret;
		} catch (UseApiException e) {
			e.printStackTrace();
			return null;
		}
	}

	void addAssociation(TAssociation asso) {
		super.addAssociation(getTentativeAssociation(asso));
	}

	@Override
	protected BinaryAssociationOrLinkEdge createBinaryAssociationOrLinkEdge(PlaceableNode source, PlaceableNode target,
			MAssociationEnd sourceEnd, MAssociationEnd targetEnd, DiagramView diagram, MAssociation assoc) {
		TAssociation tAssoc = mAssocToTAssocMap.get(assoc);
		return new OutputEdge(source, target, sourceEnd, targetEnd, this, assoc, tAssoc);
	}

	private MAssociation getTentativeAssociation(TAssociation asso) {
		try {
			String assoName = asso.getAssociationName();
			if (assoName == null || assoName.isEmpty()) {
				assoName = "nullAssoName";
			}
			assoName += asso.getID();

			String roleName1;
			try {
				roleName1 = asso.getOnlyFirstEndRoleName();
				if (roleName1 == null) {
					roleName1 = "nullRole1Name";
				}
			} catch (TLogicException e) {
				roleName1 = "conflictRole1Name";
			}
			roleName1 += asso.getID();

			String roleName2;
			try {
				roleName2 = asso.getOnlySecondEndRoleName();
				if (roleName2 == null) {
					roleName2 = "nullRole2Name";
				}
			} catch (TLogicException e) {
				roleName2 = "conflictRole2Name";
			}
			roleName2 += asso.getID();

			MMultiplicity mult1 = asso.getFirstEndMultiplicity();
			String mult1Text;
			if (mult1 == null) {
				mult1Text = "*";
			} else {
				mult1Text = mult1.toString();
			}

			MMultiplicity mult2 = asso.getSecondEndMultiplicity();
			String mult2Text;
			if (mult2 == null) {
				mult2Text = "*";
			} else {
				mult2Text = mult2.toString();
			}

			MAssociation mAssoc = modelApi.createAssociation(assoName,
					tClassIDToMClassName.get(asso.getFirstEndClass().getID()), roleName1, mult1Text, 0,
					tClassIDToMClassName.get(asso.getSecondEndClass().getID()), roleName2, mult2Text, 0);
			mAssocToTAssocMap.put(mAssoc, asso);
			return mAssoc;
		} catch (UseApiException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected PopupMenuInfo unionOfPopUpMenu() {
		final JPopupMenu popupMenu = new JPopupMenu();
		PopupMenuInfo info = new PopupMenuInfo(popupMenu);

		popupMenu.add(new AbstractAction("Export Model") {
			@Override
			public void actionPerformed(ActionEvent e) {
				Utilities.exportUSEModel(fParent, modelApi);
			}
		});
		popupMenu.addSeparator();

		addGroupOption(popupMenu);
		addMultOptions(popupMenu);
		popupMenu.addSeparator();
		Utilities.addShowHideOptions(popupMenu, this, fOpt);
		popupMenu.addSeparator();
		addCosmeticOptions(popupMenu);
		addLayoutMenuItems(popupMenu);

		return info;
	}

	private void addGroupOption(JPopupMenu popupMenu) {
		final JCheckBoxMenuItem cbGroupMR = new JCheckBoxMenuItem("Group multiplicities / role names");
		cbGroupMR.setState(fOpt.isGroupMR());
		cbGroupMR.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				fOpt.setGroupMR(ev.getStateChange() == ItemEvent.SELECTED);
				repaint();
			}
		});
		popupMenu.add(cbGroupMR);
	}
	
	private void addMultOptions(JPopupMenu popupMenu) {
		OutputClassDiagramOptions ocdo;
		if(fOpt instanceof OutputClassDiagramOptions) {
			ocdo = (OutputClassDiagramOptions) fOpt;
		} else {
			return; // should not happen
		}
		
		final JCheckBoxMenuItem cbMultOpt = new JCheckBoxMenuItem("Show simplified multiplicities");
		cbMultOpt.setState(ocdo.isSimplifiedMult());
		cbMultOpt.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				ocdo.setSimplifiedMult(ev.getStateChange() == ItemEvent.SELECTED);
				repaint();
			}
		});
		popupMenu.add(cbMultOpt);
	}

	private void addCosmeticOptions(JPopupMenu popupMenu) {
		final JCheckBoxMenuItem cbAntiAliasing = getMenuItemAntiAliasing();
		final JCheckBoxMenuItem cbShowGrid = getMenuItemShowGrid();
		final JCheckBoxMenuItem cbGrayscale = getMenuItemGrayscale();

		popupMenu.add(cbAntiAliasing);
		popupMenu.add(cbShowGrid);
		popupMenu.add(cbGrayscale);
	}

	@Override
	protected void onClosing() {
		// same as super, except super.super.onClosing is not called. this stops
		// saving the layout as a clt file
		fParent.getModelBrowser().removeHighlightChangeListener(this);
		fParent.getModelBrowser().removeSelectionChangedListener(this);
		ModelBrowserSorting.getInstance().removeSortChangeListener(this);
	}
}