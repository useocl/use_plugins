package org.tzi.use.plugin.otc.data;

public class IdResetter {
	public static void resetAllIds() {
		TObject.currentObjectID = TConstants.MIN_ID_OBJECT;
		TLink.currentLinkID = TConstants.MIN_ID_LINK;
		TClass.currentClassID = TConstants.MIN_ID_CLASS;
		TAssociation.currentAssocID = TConstants.MIN_ID_ASSOCIATION;
	}
}