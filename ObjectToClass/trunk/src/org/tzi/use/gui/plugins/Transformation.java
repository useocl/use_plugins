package org.tzi.use.gui.plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.tzi.use.gui.plugins.data.TAssociation;
import org.tzi.use.gui.plugins.data.TAttribute;
import org.tzi.use.gui.plugins.data.TClass;
import org.tzi.use.gui.plugins.data.TStatus;
import org.tzi.use.gui.plugins.data.TLink;
import org.tzi.use.gui.plugins.data.TLogicException;
import org.tzi.use.gui.plugins.data.TObject;
import org.tzi.use.uml.mm.MMultiplicity;

public class Transformation {

	private List<TObject> objects;
	private List<TLink> links;
	private List<TClass> classes;
	private List<TAssociation> associations;
	private Map<Integer, TClass> objectIdToClass;
	private Map<Integer, List<TObject>> classIdToObjects;
	private Map<Integer, List<TLink>> associationIdToLinks;

	public Transformation(List<TObject> objects, List<TLink> links) {
		this.objects = objects;
		this.links = links;
		classes = new LinkedList<TClass>();
		associations = new LinkedList<TAssociation>();
		objectIdToClass = new HashMap<Integer, TClass>();
		classIdToObjects = new HashMap<Integer, List<TObject>>();
		associationIdToLinks = new HashMap<Integer, List<TLink>>();

		transformObjects();
		transformLinks();

		System.out.println("objectIdToClass: " + objectIdToClass.toString());
		System.out.println("classIdToObjects: " + classIdToObjects.toString());
		System.out.println("associationIdToLinks: " + associationIdToLinks.toString());
	}

	public List<TClass> getClasses() {
		return classes;
	}

	public List<TAssociation> getAssociations() {
		return associations;
	}

	private void transformObjects() {
		addOrMergeClasses();
		solveAllAttributeConflicts();
	}

	private void addOrMergeClasses() {
		Map<String, TClass> classesWithName = new HashMap<String, TClass>();
		List<TClass> classesWithoutName = new LinkedList<TClass>();

		for (TObject currentObject : objects) {
			String currentClassName = currentObject.getClassName();
			List<TAttribute> currentAttributes = new LinkedList<TAttribute>();
			for (TAttribute a : currentObject.getAttributes()) {
				currentAttributes.add(new TAttribute(a.getName(), a.getSingleType()));
			}

			if (classesWithName.containsKey(currentClassName)) {
				// merge part
				TClass existingClass = classesWithName.get(currentClassName);
				existingClass.addAll(currentAttributes);
				// link objects and classes from both sides
				objectIdToClass.put(currentObject.getID(), existingClass);
				classIdToObjects.get(existingClass.getID()).add(currentObject);
			} else {
				// add part
				TClass newClass = new TClass(currentClassName);
				newClass.addAll(currentAttributes);
				if (currentClassName == null) {
					classesWithoutName.add(newClass);
				} else {
					classesWithName.put(currentClassName, newClass);
				}
				// link objects and classes from both sides
				objectIdToClass.put(currentObject.getID(), newClass);
				List<TObject> sourceObjects = new LinkedList<TObject>();
				sourceObjects.add(currentObject);
				classIdToObjects.put(newClass.getID(), sourceObjects);
			}
		}
		classesWithName.forEach((k, v) -> classes.add(v));
		classes.addAll(classesWithoutName);
	}

	private void solveAllAttributeConflicts() {
		for (TClass classs : classes) {
			solveAttributeConflicts(classs);
		}
	}

	private void solveAttributeConflicts(TClass classs) {
		// TreeMap also sorts by alphabet
		Map<String, TAttribute> tempAttributes = new TreeMap<String, TAttribute>();
		List<TAttribute> attributesWithoutName = new LinkedList<TAttribute>();
		for (TAttribute currentAttribute : classs.getAttributes()) {
			String currentAttributeName = currentAttribute.getName();
			if (currentAttributeName == null) {
				attributesWithoutName.add(currentAttribute);
			} else if (tempAttributes.containsKey(currentAttributeName)) {
				TAttribute existingAttribute = tempAttributes.get(currentAttributeName);
				// existingAttribute.mergeTypes(currentAttribute);
				Set<TAttribute.Type> mergedTypes = mergeTypes(existingAttribute.getAllTypes(),
						currentAttribute.getAllTypes());
				existingAttribute.setAllTypes(mergedTypes);
			} else {
				tempAttributes.put(currentAttributeName, currentAttribute);
			}
		}
		List<TAttribute> solvedAttributes = new LinkedList<TAttribute>();
		solvedAttributes.addAll(attributesWithoutName);
		tempAttributes.forEach((k, v) -> solvedAttributes.add(v));
		classs.setAttributes(solvedAttributes);
	}

	private Set<TAttribute.Type> mergeTypes(Set<TAttribute.Type> types1, Set<TAttribute.Type> types2) {
		Set<TAttribute.Type> mergedTypes = new HashSet<TAttribute.Type>();
		mergedTypes.addAll(types1);
		mergedTypes.addAll(types2);

		if (mergedTypes.size() > 1) {
			// if there is more than 1 type, try to remove the void type
			mergedTypes.remove(TAttribute.Type.VOID);
		}

		return mergedTypes;
	}

	private void transformLinks() {
		adoptAssociationsFromLinks();
		startUnambiguousMerge();
		startAmbiguousMerge();
		setMultiplicities();
	}

	private void adoptAssociationsFromLinks() {
		for (TLink link : links) {
			TClass firstClass = objectIdToClass.get(link.getFirstEndObject().getID());
			TClass secondClass = objectIdToClass.get(link.getSecondEndObject().getID());
			TAssociation association = new TAssociation(link.getLinkName(), firstClass, secondClass,
					link.getFirstEndRoleName(), link.getSecondEndRoleName());
			associations.add(association);

			List<TLink> newList = new LinkedList<TLink>();
			newList.add(link);
			associationIdToLinks.put(association.getID(), newList);
		}
	}

	private void startUnambiguousMerge() {
		TAssociation[] a = associations.toArray(new TAssociation[associations.size()]);
		for (int i = 0; i < a.length; ++i) {
			for (int j = i + 1; j < a.length; ++j) {
				if (a[i] != null && a[j] != null) {
					// a[i] and a[j] were not used as source before
					if (isBetweenSameClasses(a[i], a[j])) {
						// a[i], a[j] guaranteed to be between the same classes
						if (a[i].getAssociationName() != null
								&& a[i].getAssociationName().equals(a[j].getAssociationName())) {
							// a[i], a[j] guaranteed to have the same non-null
							// name
							boolean mergeHappened = false;
							if (a[i].isReflexive()) {
								if (a[i].hasBothRoleNames()) {
									mergeHappened = unambiguouslyMergeReflexiveAssociations(a[i], a[j]);
								} else if (a[j].hasBothRoleNames()) {
									mergeHappened = unambiguouslyMergeReflexiveAssociations(a[j], a[i]);
									if (mergeHappened) {
										// switch positions in array
										TAssociation tmp = a[i];
										a[i] = a[j];
										a[j] = tmp;
									}
								}
							} else {
								mergeHappened = unambiguouslyMergeNonReflexiveAssociations(a[i], a[j]);
							}
							if (mergeHappened) {
								updateDataStructuresAfterMerge(a[i], a[j]);
								// mark as already handled
								a[j] = null;
							}
						}
					}
				}
			}
		}
	}

	private void updateDataStructuresAfterMerge(TAssociation target, TAssociation source) {
		associations.remove(source);

		List<TLink> removedList = associationIdToLinks.remove(source.getID());
		List<TLink> expansibleList = associationIdToLinks.get(target.getID());
		// if expansibleList is null, an error happened
		expansibleList.addAll(removedList);
	}

	/**
	 * @param target
	 *            The target of the merge.
	 * @param source
	 *            The source of the merge. Can not have multiple role names for
	 *            a single end.
	 * @return Did a merge happen?
	 */
	private boolean unambiguouslyMergeNonReflexiveAssociations(TAssociation target, TAssociation source) {
		String sourceRoleName1;
		String sourceRoleName2;
		try {
			sourceRoleName1 = source.getOnlyFirstEndRoleName();
			sourceRoleName2 = source.getOnlySecondEndRoleName();
		} catch (TLogicException e) {
			// since source is expected to have only single role names, this is
			// not supposed to happen
			e.printStackTrace();
			return false;
		}

		if (hasSameOrder(target, source)) {
			target.addToFirstEndRoleNames(sourceRoleName1);
			target.addToSecondEndRoleNames(sourceRoleName2);
			return true;
		}
		if (hasReverseOrder(target, source)) {
			target.addToFirstEndRoleNames(sourceRoleName2);
			target.addToSecondEndRoleNames(sourceRoleName1);
			return true;
		}

		System.out.println("logic error");
		return false;
	}

	/**
	 * @param target
	 *            The target of the merge. Both role names have to be non-null.
	 * @param source
	 *            The source of the merge. Can not have multiple role names for
	 *            a single end.
	 * @return Did a merge happen?
	 */
	private boolean unambiguouslyMergeReflexiveAssociations(TAssociation target, TAssociation source) {
		if (!target.hasBothRoleNames()) {
			System.out.println("logic error");
			return false;
		}
		String sourceRoleName1;
		String sourceRoleName2;
		try {
			sourceRoleName1 = source.getOnlyFirstEndRoleName();
			sourceRoleName2 = source.getOnlySecondEndRoleName();
		} catch (TLogicException e) {
			// since source is expected to have only single role names, this is
			// not supposed to happen
			e.printStackTrace();
			return false;
		}

		boolean target1CompatibleSource1 = sourceRoleName1 == null || sourceRoleName1.isEmpty()
				|| target.containsFirstEndRoleName(sourceRoleName1);
		boolean target1CompatibleSource2 = sourceRoleName2 == null || sourceRoleName2.isEmpty()
				|| target.containsFirstEndRoleName(sourceRoleName2);
		boolean target2CompatibleSource1 = sourceRoleName1 == null || sourceRoleName1.isEmpty()
				|| target.containsSecondEndRoleName(sourceRoleName1);
		boolean target2CompatibleSource2 = sourceRoleName2 == null || sourceRoleName2.isEmpty()
				|| target.containsSecondEndRoleName(sourceRoleName2);

		if (target1CompatibleSource1 && target2CompatibleSource2) {
			// same order, no conflict
			target.addToFirstEndRoleNames(sourceRoleName1);
			target.addToSecondEndRoleNames(sourceRoleName2);
		} else if (target1CompatibleSource2 && target2CompatibleSource1) {
			// reverse order, no conflict
			target.addToFirstEndRoleNames(sourceRoleName2);
			target.addToSecondEndRoleNames(sourceRoleName1);
		} else if (target1CompatibleSource1 || target2CompatibleSource2) {
			// same order, creates a conflict
			target.addToFirstEndRoleNames(sourceRoleName1);
			target.addToSecondEndRoleNames(sourceRoleName2);
		} else if (target1CompatibleSource2 || target2CompatibleSource1) {
			// reverse order, creates a conflict
			target.addToFirstEndRoleNames(sourceRoleName2);
			target.addToSecondEndRoleNames(sourceRoleName1);
		} else {
			// guess order, creates a conflict
			target.addToFirstEndRoleNames(sourceRoleName1);
			target.addToSecondEndRoleNames(sourceRoleName2);
		}
		return true;
	}

	private void startAmbiguousMerge() {
		TAssociation[] a = associations.toArray(new TAssociation[associations.size()]);
		for (int i = 0; i < a.length; ++i) {
			for (int j = i + 1; j < a.length; ++j) {
				if (a[i] != null && a[j] != null) {
					// a[i] and a[j] were not used as source before
					if (isBetweenSameClasses(a[i], a[j])) {
						// a[i], a[j] guaranteed to be between the same classes
						if (a[i].getCurrentStatus() != TStatus.CONFLICT
								&& a[i].getCurrentStatus() != TStatus.CONFLICT) {
							// a[i], a[j] guaranteed to not be conflicted
							boolean mergeHappened = ambiguouslyMergeAssociations(a[i], a[j]);
							if (mergeHappened) {
								// because of the ambiguous merge, always mark
								// as incomplete
								a[i].setAsAmbiguouslyMerged();

								updateDataStructuresAfterMerge(a[i], a[j]);
								// mark as already handled
								a[j] = null;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param target
	 *            The target of the merge. Can not have multiple role names for
	 *            a single end.
	 * @param source
	 *            The source of the merge. Can not have multiple role names for
	 *            a single end.
	 * @return Did a merge happen?
	 */
	private boolean ambiguouslyMergeAssociations(TAssociation target, TAssociation source) {
		String targetName = target.getAssociationName();
		String sourceName = source.getAssociationName();
		if (!isCompatible(targetName, sourceName)) {
			// name is not compatible, no merge happens.
			return false;
		}
		// association names guaranteed to be compatible from this point on

		String targetRoleName1;
		String targetRoleName2;
		String sourceRoleName1;
		String sourceRoleName2;
		try {
			targetRoleName1 = target.getOnlyFirstEndRoleName();
			targetRoleName2 = target.getOnlySecondEndRoleName();
			sourceRoleName1 = source.getOnlyFirstEndRoleName();
			sourceRoleName2 = source.getOnlySecondEndRoleName();
		} catch (TLogicException e) {
			// all four ends should only have one role name, because the
			// associations are not incomplete
			e.printStackTrace();
			return false;
		}

		ConstantDataForMerge cdfm = new ConstantDataForMerge(target, targetName, sourceName, targetRoleName1,
				targetRoleName2);
		if (target.isReflexive() && source.isReflexive()) {
			boolean firstGuessWasSuccessful = tryAmbiguousMerge(cdfm, sourceRoleName1, sourceRoleName2);
			if (firstGuessWasSuccessful) {
				// a merge happened
				return true;
			}
			boolean secondGuessWasSuccessful = tryAmbiguousMerge(cdfm, sourceRoleName2, sourceRoleName1);
			if (secondGuessWasSuccessful) {
				// a merge happened
				return true;
			}
			// no merge happened
			return false;
		}
		if (hasSameOrder(target, source)) {
			return tryAmbiguousMerge(cdfm, sourceRoleName1, sourceRoleName2);
		} else if (hasReverseOrder(target, source)) {
			return tryAmbiguousMerge(cdfm, sourceRoleName2, sourceRoleName1);
		}

		System.out.println("logic error");
		return false;
	}

	/**
	 * This class helps to keep a very specific set of data together.
	 */
	private class ConstantDataForMerge {
		private TAssociation target;
		private String targetName;
		private String sourceName;
		private String targetRoleName1;
		private String targetRoleName2;

		private ConstantDataForMerge(TAssociation target, String targetName, String sourceName, String targetRoleName1,
				String targetRoleName2) {
			this.target = target;
			this.targetName = targetName;
			this.sourceName = sourceName;
			this.targetRoleName1 = targetRoleName1;
			this.targetRoleName2 = targetRoleName2;
		}
	}

	/**
	 * Checks role names from the target and candidates from the source if they
	 * are compatible. If they are, a merge happens.
	 * 
	 * @return Did a merge happen?
	 */
	private boolean tryAmbiguousMerge(ConstantDataForMerge cdfm, String sourceCandidateForTarget1,
			String sourceCandidateForTarget2) {
		boolean target1CompatibleSource = isCompatible(cdfm.targetRoleName1, sourceCandidateForTarget1);
		boolean target2CompatibleSource = isCompatible(cdfm.targetRoleName2, sourceCandidateForTarget2);
		if (!(target1CompatibleSource && target2CompatibleSource)) {
			// role name is not compatible, no merge happens.
			return false;
		}

		String mergedAssociationName;
		String mergedFirstRoleName;
		String mergedSecondRoleName;
		try {
			mergedAssociationName = mergeStrings(cdfm.targetName, cdfm.sourceName);
			mergedFirstRoleName = mergeStrings(cdfm.targetRoleName1, sourceCandidateForTarget1);
			mergedSecondRoleName = mergeStrings(cdfm.targetRoleName2, sourceCandidateForTarget2);
		} catch (TLogicException e) {
			e.printStackTrace();
			return false;
		}
		// make sure no exception happens for all String merges and then set
		// the values
		cdfm.target.setAssociationName(mergedAssociationName);
		cdfm.target.setFirstRoleName(mergedFirstRoleName);
		cdfm.target.setSecondRoleName(mergedSecondRoleName);
		return true;
	}

	private boolean isCompatible(String s1, String s2) {
		if (s1 == null || s1.isEmpty()) {
			return true;
		}
		if (s2 == null || s2.isEmpty()) {
			return true;
		}
		if (s1.equals(s2)) {
			return true;
		}
		return false;
	}

	private String mergeStrings(String s1, String s2) throws TLogicException {
		if (s1 == null || s1.isEmpty()) {
			return s2;
		}
		if (s2 == null || s2.isEmpty()) {
			return s1;
		}
		if (s1.equals(s2)) {
			return s1;
		}
		throw new TLogicException("Tried to merge non-compatible Strings");
	}

	private boolean isBetweenSameClasses(TAssociation a1, TAssociation a2) {
		if (a1.getFirstEndClass() == null || a1.getSecondEndClass() == null || a2.getFirstEndClass() == null
				|| a2.getSecondEndClass() == null) {
			// cant be between the same classes if any end is null
			return false;
		}
		if (a1.getFirstEndClass() == a2.getFirstEndClass() && a1.getSecondEndClass() == a2.getSecondEndClass()) {
			return true;
		}
		if (a1.getFirstEndClass() == a2.getSecondEndClass() && a1.getSecondEndClass() == a2.getFirstEndClass()) {
			return true;
		}
		return false;
	}

	private boolean hasSameOrder(TAssociation a1, TAssociation a2) {
		if (a1.getFirstEndClass() == null || a1.getSecondEndClass() == null || a2.getFirstEndClass() == null
				|| a2.getSecondEndClass() == null) {
			// cant have the same order if any end is null
			return false;
		}
		boolean sameFirstClass = a1.getFirstEndClass() == a2.getFirstEndClass();
		boolean sameSecondClass = a1.getSecondEndClass() == a2.getSecondEndClass();
		return sameFirstClass && sameSecondClass;
	}

	private boolean hasReverseOrder(TAssociation a1, TAssociation a2) {
		if (a1.getFirstEndClass() == null || a1.getSecondEndClass() == null || a2.getFirstEndClass() == null
				|| a2.getSecondEndClass() == null) {
			// cant have reverse order if any end is null
			return false;
		}
		boolean reverseFirstClass = a1.getFirstEndClass() == a2.getSecondEndClass();
		boolean reverseSecondClass = a1.getSecondEndClass() == a2.getFirstEndClass();
		return reverseFirstClass && reverseSecondClass;
	}

	private void setMultiplicities() {
		for (TAssociation currentAssociation : associations) {
			if (currentAssociation.getCurrentStatus() != TStatus.CONFLICT) {
				// only set multiplicities for COMPLETE associations
				TClass firstEnd = currentAssociation.getFirstEndClass();
				TClass secondEnd = currentAssociation.getSecondEndClass();
				// at this point a COMPLETE association is needed
				String firstRoleName = null;
				String secondRoleName = null;
				try {
					firstRoleName = currentAssociation.getOnlyFirstEndRoleName();
					secondRoleName = currentAssociation.getOnlySecondEndRoleName();
				} catch (TLogicException e) {
					e.printStackTrace();
				}
				currentAssociation
						.setFirstEndMultiplicity(getMultiplicity(firstRoleName, secondEnd, currentAssociation));
				currentAssociation
						.setSecondEndMultiplicity(getMultiplicity(secondRoleName, firstEnd, currentAssociation));
			}
		}
	}

	private MMultiplicity getMultiplicity(String wantedRoleName, TClass oppositeSideClass, TAssociation association) {
		int lowerBound = Integer.MAX_VALUE;
		int upperBound = 0;
		// to get the value for the current side of the association, use the
		// data from the opposite site
		for (TObject oppositeObject : classIdToObjects.get(oppositeSideClass.getID())) {
			// int numberOfPlayingRole = numberOfPlayingRole(o.getId(),
			// association.getName());
			int numberOfPlayingRole = numberOfPlayingRole(oppositeObject, wantedRoleName, association);
			lowerBound = Math.min(lowerBound, numberOfPlayingRole);
			upperBound = Math.max(upperBound, numberOfPlayingRole);
		}
		return new MMultiplicity(lowerBound, upperBound);
	}

	private int numberOfPlayingRole(TObject oppositeObject, String wantedRoleName, TAssociation association) {
		int count = 0;
		for (TLink l : associationIdToLinks.get(association.getID())) {
			if (playsRole(l, oppositeObject, wantedRoleName)) {
				++count;
			}
		}
		return count;
	}

	private boolean playsRole(TLink currentLink, TObject oppositeObject, String wantedRoleName) {
		if (oppositeObject == currentLink.getFirstEndObject()) {
			// the string needs to be from the other side
			String currentText = currentLink.getSecondEndRoleName();
			if (currentText == null || currentText.length() == 0) {
				// if the source linkRole has no name, everything is fine
				return true;
			}
			if (currentText.equals(wantedRoleName)) {
				return true;
			}
		}
		if (oppositeObject == currentLink.getSecondEndObject()) {
			// the string needs to be from the other side
			String currentText = currentLink.getFirstEndRoleName();
			if (currentText == null || currentText.length() == 0) {
				// if the source linkRole has no name, everything is fine
				return true;
			}
			if (currentText.equals(wantedRoleName)) {
				return true;
			}
		}
		return false;
	}
}