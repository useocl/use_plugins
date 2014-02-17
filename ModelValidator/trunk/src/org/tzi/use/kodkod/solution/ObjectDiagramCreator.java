package org.tzi.use.kodkod.solution;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;

/**
 * Class to create an object diagram from a solution of kodkod.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ObjectDiagramCreator {

	private IModel model;
	private MSystem mSystem;
	private MModel mModel;

	private Map<String, MObjectState> objectStates;

	public ObjectDiagramCreator(IModel model, MSystem mSystem) {
		this.model = model;
		this.mSystem = mSystem;
		// mSystem.reset();
		mModel = mSystem.model();
	}

	/**
	 * Creates the object diagram.
	 * 
	 * @param relations
	 * @throws Exception
	 */
	public void create(Map<Relation, TupleSet> relations) throws Exception {
		objectStates = new HashMap<String, MObjectState>();

		try {
			Map<Relation, TupleSet> withoutClasses = createObjects(relations);

			createAssociationClasses(relations);

			createElements(withoutClasses);

		} catch (Exception exception) {
			throw exception;
		}
	}

	private Map<Relation, TupleSet> createObjects(
			Map<Relation, TupleSet> relations) throws MSystemException {
		Map<Relation, TupleSet> withoutClasses = new HashMap<Relation, TupleSet>(
				relations);

		String relationName;
		ElementStrategy strategy;

		for (Relation relation : relations.keySet()) {
			strategy = null;
			relationName = relation.name();

			if (!isNegateRelation(relationName) && !isType(relationName)) {
				if (isClass(relation) && !isAssociationClass(relation.name())) {
					strategy = new ObjectStrategy(mSystem.state(), mModel,
							objectStates, relationName);
					iterateTuplesAndCreate(strategy, relations.get(relation));

					withoutClasses.remove(relation);
				}
			}
		}

		return withoutClasses;
	}

	private Map<String, Relation> createElements(
			Map<Relation, TupleSet> relations) throws MSystemException {
		Map<String, Relation> associationClassesRelations = new HashMap<String, Relation>();

		String relationName;
		ElementStrategy strategy;

		for (Relation relation : relations.keySet()) {
			strategy = null;
			relationName = relation.name();

			if (!isNegateRelation(relationName) && !isType(relationName)) {
				String[] nameSplit = relationName.split("_", 2);

				if (!isType(nameSplit[0]) && !isToStringMap(nameSplit[0])) {
					if (isAssociation(relationName, relation)) {
						strategy = new AssociationStrategy(mSystem.state(),
								mModel, objectStates, relationName);

					} else if (isAttributeOfSimpleObject(relationName, relation)) {
						strategy = new AttributeStrategy(mSystem.state(),
								mModel, objectStates,
								getPartAfterLastSeparator(relationName));

					} else {
						associationClassesRelations.put(relationName, relation);
					}

					if (strategy != null) {
						iterateTuplesAndCreate(strategy,
								relations.get(relation));
					}
				}
			}
		}

		return associationClassesRelations;
	}

	private void createAssociationClasses(Map<Relation, TupleSet> relations)
			throws MSystemException {

		Map<String, Relation> associationClassesRelations = new HashMap<String, Relation>();
		ElementStrategy strategy;

		for (Relation relation : relations.keySet()) {
			strategy = null;
			String relationName = relation.name();

			if (!isNegateRelation(relationName) && !isType(relationName)) {
				String[] nameSplit = relationName.split("_", 2);

				if (!isType(nameSplit[0]) && !isToStringMap(nameSplit[0])) {
					if (!isAssociation(relationName, relation)
							&& !isAttributeOfSimpleObject(relationName,
									relation)) {
						associationClassesRelations.put(relationName, relation);
					}
				}
			}
		}

		for (MAssociationClass mAssociationClass : mModel
				.getAssociationClassesOnly()) {
			String associationClassName = mAssociationClass.name() + "_assoc";
			associationClassesRelations.remove(mAssociationClass.name());
			Relation relation = associationClassesRelations
					.remove(associationClassName);
			strategy = new AssociationClassStrategy(mSystem.state(), mModel,
					objectStates, mAssociationClass);

			iterateTuplesAndCreate(strategy, relations.get(relation));
		}

		for (String relationName : associationClassesRelations.keySet()) {
			String attributeName = getPartAfterLastSeparator(relationName);
			Relation relation = associationClassesRelations.get(relationName);
			strategy = new AttributeStrategy(mSystem.state(), mModel,
					objectStates, attributeName);

			iterateTuplesAndCreate(strategy, relations.get(relation));
		}
	}

	/**
	 * Iterates all tuples and creates the elements with the given strategy.
	 * 
	 * @param strategy
	 * @param tupleSet
	 * @throws MSystemException
	 */
	private void iterateTuplesAndCreate(ElementStrategy strategy,
			TupleSet tupleSet) throws MSystemException {
		if (strategy.canDo()) {
			Iterator<Tuple> iterator = tupleSet.iterator();
			Tuple currentTuple;
			while (iterator.hasNext()) {
				currentTuple = iterator.next();

				strategy.createElement(currentTuple);
			}
		}
	}

	private boolean isClass(Relation relation) {
		return model.getClass(relation.name()) != null && relation.arity() == 1;
	}

	private boolean isAssociationClass(String relationName) {
		return model.getClass(relationName) instanceof IAssociationClass;
	}

	private boolean isAssociation(String relationName, Relation relation) {
		return model.getAssociation(relationName) != null
				&& relation.arity() >= 2;
	}

	private boolean isAttributeOfSimpleObject(String relationName,
			Relation relation) {
		String className = getPartBeforeLastSeparator(relationName);
		String attrName = getPartAfterLastSeparator(relationName);

		return !isAssociationClass(className)
				&& model.getClass(className) != null
				&& model.getClass(className).getAttribute(attrName) != null;
	}

	private String getPartBeforeLastSeparator(String relationName) {
		int separatorIndex = relationName.lastIndexOf("_");
		if (separatorIndex == -1) {
			return relationName;
		}

		return relationName.substring(0, separatorIndex);
	}

	private String getPartAfterLastSeparator(String relationName) {
		int separatorIndex = relationName.lastIndexOf("_");
		return relationName.substring(separatorIndex + 1);
	}

	private boolean isType(String name) {
		return model.getEnumType(name) != null
				|| model.typeFactory().buildInType(name) != null;
	}

	private boolean isToStringMap(String name) {
		return name.equals(TypeConstants.TO_STRING_MAP);
	}

	private boolean isNegateRelation(String relationName) {
		return relationName.startsWith("$");
	}
}
