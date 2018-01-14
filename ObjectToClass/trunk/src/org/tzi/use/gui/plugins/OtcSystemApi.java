package org.tzi.use.gui.plugins;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.tzi.use.gui.plugins.data.MMConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;

public class OtcSystemApi {

	private MSystem fSystem;

	private final MClass OBJ_CLS;
	private final MClass LINK_CLS;
	private final MAssociation OBJ_SLOT_ASSOC;
	private final MAssociation LINK_BLACK_ASSOC;
	private final MAssociation LINK_WHITE_ASSOC;

	public OtcSystemApi(MSystem system) {
		fSystem = system;
		MModel model = fSystem.model();
		OBJ_CLS = model.getClass(MMConstants.CLS_OBJECT_NAME);
		LINK_CLS = model.getClass(MMConstants.CLS_LINK_NAME);
		OBJ_SLOT_ASSOC = model.getAssociation(MMConstants.ASSO_OBJECT_SLOT_NAME);
		LINK_BLACK_ASSOC = model.getAssociation(MMConstants.ASSO_LINK_OBJ1_NAME);
		LINK_WHITE_ASSOC = model.getAssociation(MMConstants.ASSO_LINK_OBJ2_NAME);
	}

	public MObject getObjectOfSlot(MObject slotObject) {
		for (MLink link : fSystem.state().linksOfAssociation(OBJ_SLOT_ASSOC).links()) {
			// assumes object at 0
			MObject objectEnd = link.getLinkEnd(0).object();
			// assumes slot at 1
			MObject slotEnd = link.getLinkEnd(1).object();
			if (slotEnd.equals(slotObject)) {
				return objectEnd;
			}
		}
		return null;
	}

	public List<MObject> getSlotsOfObject(MObject mainObject, boolean orderMatters) {
		List<MObject> slotObjects = new ArrayList<MObject>();
		for (MLink link : fSystem.state().linksOfAssociation(OBJ_SLOT_ASSOC).links()) {
			// assumes object at 0
			MObject objectEnd = link.getLinkEnd(0).object();
			// assumes slot at 1
			MObject slotEnd = link.getLinkEnd(1).object();
			if (objectEnd.equals(mainObject)) {
				slotObjects.add(slotEnd);
			}
		}
		if (orderMatters) {
			// sort by object name to get consistent order
			slotObjects.sort(new SlotObjectComparator());
		}
		return slotObjects;
	}

	private class SlotObjectComparator implements Comparator<MObject> {
		@Override
		public int compare(MObject slot1, MObject slot2) {
			Integer slot1Number = Integer.parseInt(slot1.name().replaceFirst(MMConstants.CLS_SLOT_NAME, ""));
			Integer slot2Number = Integer.parseInt(slot2.name().replaceFirst(MMConstants.CLS_SLOT_NAME, ""));
			return slot1Number.compareTo(slot2Number);
		}
	}

	public MObject getBlackFromLinkObject(MObject linkObject) {
		for (MLink link : fSystem.state().linksOfAssociation(LINK_BLACK_ASSOC).links()) {
			// assumes linkB at 0
			MObject linkBEnd = link.getLinkEnd(0).object();
			// assumes black at 1
			MObject blackEnd = link.getLinkEnd(1).object();
			if (linkBEnd.equals(linkObject)) {
				return blackEnd;
			}
		}
		return null;
	}

	public MObject getWhiteFromLinkObject(MObject linkObject) {
		for (MLink link : fSystem.state().linksOfAssociation(LINK_WHITE_ASSOC).links()) {
			// assumes linkW at 0
			MObject linkWEnd = link.getLinkEnd(0).object();
			// assumes white at 1
			MObject whiteEnd = link.getLinkEnd(1).object();
			if (linkWEnd.equals(linkObject)) {
				return whiteEnd;
			}
		}
		return null;
	}

	public Set<MObject> getAllMainObjects() {
		return fSystem.state().objectsOfClass(OBJ_CLS);
	}

	public Set<MObject> getAllLinkObjects() {
		return fSystem.state().objectsOfClass(LINK_CLS);
	}

	public String getIdentityOfObject(MObject mainObject) {
		MObjectState objState = mainObject.state(fSystem.state());
		return objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_IDENT).toString();
	}

	public String getClassNameOfObject(MObject mainObject) {
		MObjectState objState = mainObject.state(fSystem.state());
		return objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_CLASSN).toString();
	}
}