package org.tzi.use.gui.plugins.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.tzi.use.uml.mm.MMultiplicity;

public class TAssociation {
	static int currentAssocID = TConstants.MIN_ID_ASSOCIATION;
	private final int id;
	private String assocName;
	private int kind;
	private TClass firstEndClass;
	private TClass secondEndClass;
	private Set<String> firstEndRoleNames;
	private Set<String> secondEndRoleNames;
	private MMultiplicity firstEndMult;
	private MMultiplicity secondEndMult;
	private boolean isReflexive;
	private boolean isShared;
	private boolean isCyclic;

	public TAssociation(String assocName, TClass firstEndClass, TClass secondEndClass, String firstEndRoleName,
			String secondEndRoleName, int kind) {
		id = currentAssocID++;
		if (id > TConstants.MAX_ID_ASSOCIATION) {
			// TODO error output or exception
			System.out.println("Too many associations");
		}

		this.assocName = assocName;
		this.firstEndClass = firstEndClass;
		this.secondEndClass = secondEndClass;
		this.kind = kind;

		setFirstRoleName(firstEndRoleName);
		setSecondRoleName(secondEndRoleName);

		if (firstEndClass != null && firstEndClass == secondEndClass) {
			isReflexive = true;
		}
	}

	@Override
	public String toString() {
		return "(ID: " + id + " AssocName: " + assocName + ", Text1: " + getDisplayText1() + ", Text2: "
				+ getDisplayText2() + ")";
	}

	public int getID() {
		return id;
	}

	public String getAssociationName() {
		return assocName;
	}
	
	public int getKind() {
		return kind;
	}

	public void setAssociationName(String assocName) {
		this.assocName = assocName;
	}

	public TClass getFirstEndClass() {
		return firstEndClass;
	}

	public TClass getSecondEndClass() {
		return secondEndClass;
	}

	public boolean containsFirstEndRoleName(String roleName) {
		return firstEndRoleNames.contains(roleName);
	}

	public boolean containsSecondEndRoleName(String roleName) {
		return secondEndRoleNames.contains(roleName);
	}

	/**
	 * @return the only first end role name or null if there is none
	 * @throws TLogicException
	 *             if there is more than one first end role name
	 */
	public String getOnlyFirstEndRoleName() throws TLogicException {
		return getOnlyRoleName(firstEndRoleNames);
	}

	/**
	 * @return the only second end role name or null if there is none
	 * @throws TLogicException
	 *             if there is more than one second end role name
	 */
	public String getOnlySecondEndRoleName() throws TLogicException {
		return getOnlyRoleName(secondEndRoleNames);
	}

	private String getOnlyRoleName(Set<String> roleNames) throws TLogicException {
		if (roleNames == null || roleNames.isEmpty()) {
			return null;
		}
		if (roleNames.size() == 1) {
			String[] arr = new String[1];
			roleNames.toArray(arr);
			return arr[0];
		}
		throw new TLogicException("Tried to get a single role name but multiple role names were found");
	}

	public String getDisplayText1() {
		return getRoleNameDisplayText(firstEndRoleNames);
	}

	public String getDisplayText2() {
		return getRoleNameDisplayText(secondEndRoleNames);
	}

	private String getRoleNameDisplayText(Set<String> multipleRoleNames) {
		if (multipleRoleNames.size() == 0) {
			return "";
		}

		LinkedList<String> textList = new LinkedList<String>(multipleRoleNames);
		String firstElement = textList.pop();
		if (multipleRoleNames.size() == 1) {
			return firstElement;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(TConstants.CONFLICT_MARKER);
		builder.append(" ");
		builder.append(firstElement);
		for (String text : textList) {
			builder.append(", ");
			builder.append(text);
		}
		return builder.toString();
	}

	public void addToFirstEndRoleNames(String newFirstEndRoleName) {
		addToRoleNames(firstEndRoleNames, newFirstEndRoleName);
	}

	public void addToSecondEndRoleNames(String newSecondEndRoleName) {
		addToRoleNames(secondEndRoleNames, newSecondEndRoleName);
	}

	private void addToRoleNames(Set<String> currentRoleNames, String newRoleName) {
		if (newRoleName != null && !newRoleName.isEmpty()) {
			currentRoleNames.add(newRoleName);
		}
	}

	public void setFirstRoleName(String roleName) {
		firstEndRoleNames = new HashSet<String>();
		addToFirstEndRoleNames(roleName);
	}

	public void setSecondRoleName(String roleName) {
		secondEndRoleNames = new HashSet<String>();
		addToSecondEndRoleNames(roleName);
	}

	public MMultiplicity getFirstEndMultiplicity() {
		return firstEndMult;
	}

	public MMultiplicity getSecondEndMultiplicity() {
		return secondEndMult;
	}

	public void setFirstEndMultiplicity(MMultiplicity mult) {
		firstEndMult = mult;
	}

	public void setSecondEndMultiplicity(MMultiplicity mult) {
		secondEndMult = mult;
	}

	public boolean isReflexive() {
		return isReflexive;
	}

	public boolean connenctedTo(TClass cls) {
		if (cls == null) {
			return false;
		}
		if (cls == firstEndClass) {
			return true;
		}
		if (cls == secondEndClass) {
			return true;
		}
		return false;
	}

	public boolean hasBothRoleNames() {
		if (firstEndRoleNames == null || secondEndRoleNames == null || firstEndRoleNames.isEmpty()
				|| secondEndRoleNames.isEmpty()) {
			return false;
		}
		return true;
	}

	private boolean wasAmbiguouslyMerged = false;

	public void setAsAmbiguouslyMerged() {
		wasAmbiguouslyMerged = true;
	}
	public void setIsShared(Boolean isShared) {
		this.isShared = isShared;
	}
	public Boolean getIsShared() {
		return isShared;
	}
	public void setIsCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;
	}
	
	public Boolean getIsCyclic() {
		return isCyclic;
	}
	public TStatus getCurrentStatus() {
		// conflict
		if (firstEndRoleNames.size() > 1 || secondEndRoleNames.size() > 1) {
			return TStatus.CONFLICT;
		}
		if (isShared||isCyclic ) {
			return TStatus.CONFLICT;
		}
		// missing
		if (assocName == null || assocName.isEmpty()) {
			return TStatus.MISSING;
		}
		if (firstEndRoleNames.isEmpty() || secondEndRoleNames.isEmpty()) {
			return TStatus.MISSING;
		}
		if (wasAmbiguouslyMerged) {
			return TStatus.MISSING;
		}

		
		// complete
		return TStatus.COMPLETE;
	}
}