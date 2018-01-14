package org.tzi.use.gui.plugins.objectdiagram;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.impl.UseSystemApiUndoable;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.views.diagrams.objectdiagram.NewObjectDiagramView;
import org.tzi.use.gui.views.diagrams.objectdiagram.ObjDiagramOptions;
import org.tzi.use.gui.plugins.OtcSystemApi;
import org.tzi.use.gui.plugins.Transformation;
import org.tzi.use.gui.plugins.Utilities;
import org.tzi.use.gui.plugins.classdiagram.OutputClassDiagramView;
import org.tzi.use.gui.plugins.data.MMConstants;
import org.tzi.use.gui.plugins.data.TLink;
import org.tzi.use.gui.plugins.data.TObject;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.events.AttributeAssignedEvent;
import org.tzi.use.uml.sys.events.LinkDeletedEvent;
import org.tzi.use.uml.sys.events.LinkInsertedEvent;
import org.tzi.use.uml.sys.events.ObjectCreatedEvent;
import org.tzi.use.uml.sys.events.ObjectDestroyedEvent;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class InputObjectDiagramView extends NewObjectDiagramView {
	private InputObjectDiagram fInputObjectDiagram;

	private UseSystemApiUndoable useSystemApi;
	private OtcSystemApi otcApi;

	private final MClass objectClass;
	private final MClass slotClass;
	private final MClass linkClass;
	private final MAssociation obj_attrAssociation;
	private final MAssociation link_obj1Association;
	private final MAssociation link_obj2Association;

	public InputObjectDiagramView(MainWindow mainWindow, MSystem system) {
		super(mainWindow, system);

		useSystemApi = new UseSystemApiUndoable(fSystem);
		otcApi = new OtcSystemApi(fSystem);

		objectClass = fSystem.model().getClass(MMConstants.CLS_OBJECT_NAME);
		slotClass = fSystem.model().getClass(MMConstants.CLS_SLOT_NAME);
		linkClass = fSystem.model().getClass(MMConstants.CLS_LINK_NAME);
		obj_attrAssociation = fSystem.model().getAssociation(MMConstants.ASSO_OBJECT_SLOT_NAME);
		link_obj1Association = fSystem.model().getAssociation(MMConstants.ASSO_LINK_OBJ1_NAME);
		link_obj2Association = fSystem.model().getAssociation(MMConstants.ASSO_LINK_OBJ2_NAME);

		actuallyInitState();
	}

	@Override
	public void initDiagram(boolean loadDefaultLayout, ObjDiagramOptions opt) {
		ObjDiagramOptions newOptions = opt;
		if (newOptions == null) {
			newOptions = new ObjDiagramOptions();
		}
		newOptions.setShowRolenames(true);
		newOptions.setShowAttributes(true);

		// save specifically as a NewInputObjectDiagram
		fInputObjectDiagram = new InputObjectDiagram(this, fMainWindow.logWriter(), newOptions);
		// but also save it in parent class
		fObjectDiagram = fInputObjectDiagram;

		fObjectDiagram.setStatusBar(fMainWindow.statusBar());
		this.removeAll();
		add(new JScrollPane(fObjectDiagram));
		// do NOT initState();
	}

	private void actuallyInitState() {
		for (MObject mainObject : otcApi.getAllMainObjects()) {
			fObjectDiagram.addObject(mainObject);
		}
		for (MObject linkObject : otcApi.getAllLinkObjects()) {
			createAndShowLink(linkObject);
		}

		// copied two lines from super.initState()
		fObjectDiagram.initialize();
		viewcount++;
	}

	private class MInputLinkImpl extends MLinkImplCopy {
		public MInputLinkImpl(MAssociation assoc, List<MObject> objects) throws MSystemException {
			super(assoc, objects, null);
		}
	}

	private void createAndShowLink(MObject linkObject) {
		MObject blackEndObject = otcApi.getBlackFromLinkObject(linkObject);
		MObject whiteEndObject = otcApi.getWhiteFromLinkObject(linkObject);

		// FIXME warum tritt das bei einstein.soil auf?
		if (blackEndObject == null || whiteEndObject == null) {
			return;
		}

		List<MObject> connectedObjects = new LinkedList<MObject>();
		connectedObjects.add(blackEndObject);
		connectedObjects.add(whiteEndObject);

		MAssociation assoc = Utilities.getMAssociation(linkObject.name());

		try {
			MLink link = new MInputLinkImpl(assoc, connectedObjects);
			fObjectDiagram.addLink(link);
		} catch (MSystemException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startObjectCreation() {
		try {
			useSystemApi.createObjectEx(objectClass, null);
		} catch (UseApiException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startSlotCreation(MObject objectForNewSlot) {
		try {
			MObject newSlot = useSystemApi.createObjectEx(slotClass, null);
			MObject[] connectedObjects = { objectForNewSlot, newSlot };
			useSystemApi.createLinkEx(obj_attrAssociation, connectedObjects);
		} catch (UseApiException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startLinkCreation(MObject black, MObject white) {
		try {
			MObject newLink = useSystemApi.createObjectEx(linkClass, null);
			MObject[] objectsBlack = { newLink, black };
			MObject[] objectsWhite = { newLink, white };
			useSystemApi.createLinkEx(link_obj1Association, objectsBlack);
			useSystemApi.createLinkEx(link_obj2Association, objectsWhite);
		} catch (UseApiException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@Subscribe
	public void onObjectCeated(ObjectCreatedEvent e) {
		MObject createdObject = e.getCreatedObject();
		if (createdObject instanceof MLink) {
			return;
		}
		if (createdObject.cls().name().equals(MMConstants.CLS_OBJECT_NAME)) {
			fObjectDiagram.addObject(createdObject);
		} else if (createdObject.cls().name().equals(MMConstants.CLS_SLOT_NAME)) {
			// nothing
		} else if (createdObject.cls().name().equals(MMConstants.CLS_LINK_NAME)) {
			// nothing
		} else {
			// this should never happen
		}
		fObjectDiagram.invalidateContent(true);
	}

	@Override
	@Subscribe
	public void onLinkCeated(LinkInsertedEvent e) {
		MLink createdLink = e.getLink();
		if (createdLink.association().name().equals(MMConstants.ASSO_OBJECT_SLOT_NAME)) {
			// nothing
		} else if (createdLink.association().name().equals(MMConstants.ASSO_LINK_OBJ1_NAME)) {
			finishBlackCreation(createdLink);
		} else if (createdLink.association().name().equals(MMConstants.ASSO_LINK_OBJ2_NAME)) {
			finishWhiteCreation(createdLink);
		} else {
			// this should never happen
		}
		fObjectDiagram.invalidateContent(true);
	}

	private void finishBlackCreation(MLink blackLink) {
		// TODO
	}

	private void finishWhiteCreation(MLink whiteLink) {
		// assumes linkW at 0
		MObject linkWEnd = whiteLink.getLinkEnd(0).object();

		// FIXME nimmt an dass black schon existiert
		createAndShowLink(linkWEnd);
	}

	public void onAttributeAssigned(AttributeAssignedEvent e) {
		MObject changedObject = e.getObject();

		if (changedObject.cls().name().equals(MMConstants.CLS_OBJECT_NAME)) {
			fObjectDiagram.updateObject(changedObject);
		} else if (changedObject.cls().name().equals(MMConstants.CLS_SLOT_NAME)) {
			// FIXME seems to work. if not, use following two lines
			// MObject mainObject = otcApi.getObjectOfSlot(changedObject);
			// fObjectDiagram.updateObject(mainObject);
		} else if (changedObject.cls().name().equals(MMConstants.CLS_LINK_NAME)) {
			// FIXME delete/add nur vorlaeufig: man verliert zB selected
			fInputObjectDiagram.deleteDisplayLink(changedObject);
			createAndShowLink(changedObject);
			repaint();
		}

		fObjectDiagram.invalidateContent(true);
	}

	void startTransformation() {
		Map<MObject, TObject> mObjectToObjectShapeMap = new HashMap<MObject, TObject>();

		List<TObject> objectShapes = new LinkedList<TObject>();
		for (MObject mObject : otcApi.getAllMainObjects()) {
			TObject currentObjectShape = Utilities.createNewTObject(fSystem.state(), mObject,
					otcApi.getSlotsOfObject(mObject, false));
			objectShapes.add(currentObjectShape);
			mObjectToObjectShapeMap.put(mObject, currentObjectShape);
		}

		List<TLink> linkShapes = new LinkedList<TLink>();
		for (MObject linkObject : otcApi.getAllLinkObjects()) {
			MObjectState linkObjState = linkObject.state(fSystem.state());
			MObject black = otcApi.getBlackFromLinkObject(linkObject);
			MObject white = otcApi.getWhiteFromLinkObject(linkObject);
			if (black != null && white != null) {
				// only add to linkShapes if the linkObj has two correct links
				TLink currentLinkShape = Utilities.createNewTLink(linkObjState, mObjectToObjectShapeMap.get(black),
						mObjectToObjectShapeMap.get(white));

				linkShapes.add(currentLinkShape);
			}
		}

		Transformation t = new Transformation(objectShapes, linkShapes);

		OutputClassDiagramView cdv = new OutputClassDiagramView(fMainWindow, fSystem, t.getClasses(),
				t.getAssociations());
		ViewFrame f = new ViewFrame("Output class diagram", cdv, "ClassDiagram.gif");
		JComponent c = (JComponent) f.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(cdv, BorderLayout.CENTER);
		fMainWindow.addNewViewFrame(f);
		// needed?
		// fMainWindow.getClassDiagrams().add(cdv);
	}

	void startObjectDestruction(MObject object) {
		try {
			useSystemApi.deleteObjectEx(object);
			for (MObject slot : otcApi.getSlotsOfObject(object, false)) {
				useSystemApi.deleteObjectEx(slot);
			}
			// links get deleted from system when objects are deleted
		} catch (UseApiException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startSlotDestruction(MObject slot, MObject object) {
		try {
			useSystemApi.deleteObjectEx(slot);
			// link gets deleted from system when object is deleted
		} catch (UseApiException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	void startLinkDestruction(MObject link) {
		try {
			useSystemApi.deleteObjectEx(link);
			// links get deleted from system when linkObject is deleted
		} catch (UseApiException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	@Subscribe
	public void onObjectDestroyed(ObjectDestroyedEvent e) {
		if (e.getDestroyedObject() instanceof MLink) {
			return;
		}

		MObject destroyedObject = e.getDestroyedObject();
		if (destroyedObject.cls().name().equals(MMConstants.CLS_OBJECT_NAME)) {
			fObjectDiagram.deleteObject(destroyedObject);
		} else if (destroyedObject.cls().name().equals(MMConstants.CLS_SLOT_NAME)) {
			// nothing
		} else if (destroyedObject.cls().name().equals(MMConstants.CLS_LINK_NAME)) {
			fInputObjectDiagram.deleteDisplayLink(destroyedObject);
		}

		fObjectDiagram.invalidateContent(true);
	}

	@Override
	@Subscribe
	public void onLinkDeleted(LinkDeletedEvent e) {
		MLink deletedLink = e.getLink();
		if (deletedLink.association().name().equals(MMConstants.ASSO_OBJECT_SLOT_NAME)) {
			// nothing
		} else if (deletedLink.association().name().equals(MMConstants.ASSO_LINK_OBJ1_NAME)) {
			// assumes linkB at 0
			MObject linkObject = deletedLink.getLinkEnd(0).object();
			try {
				// TODO more tests if this really works without contains
				// if (linkObjects.contains(linkObject)) {
				useSystemApi.deleteObjectEx(linkObject);
				// }
			} catch (UseApiException e1) {
				// Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (deletedLink.association().name().equals(MMConstants.ASSO_LINK_OBJ2_NAME)) {
			// assumes linkW at 0
			MObject linkObject = deletedLink.getLinkEnd(0).object();
			try {
				// if (linkObjects.contains(linkObject)) {
				useSystemApi.deleteObjectEx(linkObject);
				// }
			} catch (UseApiException e1) {
				// Auto-generated catch block
				e1.printStackTrace();
			}
		}

		fObjectDiagram.invalidateContent(true);
	}
}