package org.tzi.use.gui.plugins.data;

public class TLink {
	static int currentLinkID = TConstants.MIN_ID_LINK;
	private final int id;
	private String linkName;
	private TObject firstEndObject;
	private TObject secondEndObject;
	private String firstEndRoleName;
	private String secondEndRoleName;
	private boolean isReflexive;
	private int kind ;
	public TLink(String linkName, TObject firstEndObject, TObject secondEndObject, String firstEndRoleName,
			String secondEndRoleName, int kind) {
		id = currentLinkID++;
		if (id > TConstants.MAX_ID_LINK) {
			// TODO error output or exception
			System.out.println("Too many links");
		}
		this.kind = kind;
		this.linkName = linkName;
		this.firstEndObject = firstEndObject;
		this.secondEndObject = secondEndObject;
		this.firstEndRoleName = firstEndRoleName;
		this.secondEndRoleName = secondEndRoleName;
		if (firstEndObject != null && firstEndObject == secondEndObject) {
			isReflexive = true;
		}
	}

	public static TLink getNewLinkWithoutIncreasingID() {
		return new TLink();
	}

	private TLink() {
		id = TConstants.DEFAULT_ID;
	}

	@Override
	public String toString() {
		return "(ID: " + id + " LinkName: " + linkName + ", FirstEndRoleName: " + firstEndRoleName
				+ ", SecondEndRoleName: " + secondEndRoleName + ")";
	}

	public int getID() {
		return id;
	}
	
	public int getKind() {
		return kind;
	}
	
	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public TObject getFirstEndObject() {
		return firstEndObject;
	}

	public TObject getSecondEndObject() {
		return secondEndObject;
	}

	public String getFirstEndRoleName() {
		return firstEndRoleName;
	}

	public String getSecondEndRoleName() {
		return secondEndRoleName;
	}

	public void setFirstEndRoleName(String firstEndRoleName) {
		this.firstEndRoleName = firstEndRoleName;
	}

	public void setSecondEndRoleName(String secondEndRoleName) {
		this.secondEndRoleName = secondEndRoleName;
	}

	public boolean isReflexive() {
		return isReflexive;
	}

	public boolean connenctedTo(TObject obj) {
		if (obj == null) {
			return false;
		}
		if (obj == firstEndObject) {
			return true;
		}
		if (obj == secondEndObject) {
			return true;
		}
		return false;
	}

	public TStatus getCurrentStatus() {
		if (linkName == null || linkName.isEmpty()) {
			return TStatus.MISSING;
		}
		if (firstEndRoleName == null || firstEndRoleName.isEmpty()) {
			return TStatus.MISSING;
		}
		if (secondEndRoleName == null || secondEndRoleName.isEmpty()) {
			return TStatus.MISSING;
		}

		return TStatus.COMPLETE;
	}
}