package org.tzi.use.kodkod.solution;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.DefaultConfigurationValues;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Evaluator;
import org.tzi.use.uml.ocl.value.BooleanValue;
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

	public enum ErrorType {
		NONE, STRUCTURE, INVARIANT
	}
	
	protected static final Logger LOG = Logger.getLogger(ObjectDiagramCreator.class);
	
	private IModel model;
	private UseSystemApi systemApi;

	private Map<String, MObjectState> objectStates;

	public ObjectDiagramCreator(IModel model, Session session) {
		this.model = model;
		systemApi = UseSystemApi.create(session);
	}

	/**
	 * Creates the object diagram.
	 * 
	 * @param relations
	 * @throws MSystemException 
	 * @throws Exception
	 */
	public void create(Map<Relation, TupleSet> relations) throws UseApiException {
		objectStates = new HashMap<String, MObjectState>();

		Map<Relation, TupleSet> withoutClasses = createObjects(relations);

		createAssociationClasses(relations);

		createElements(withoutClasses);
	}

	public ErrorType hasDiagramErrors() {
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		MSystem mSystem = systemApi.getSystem();
		boolean foundErrors = !mSystem.state().checkStructure(out);
		if (foundErrors) {
			String result = buffer.toString();

			boolean aggregationcyclefreeness = DefaultConfigurationValues.aggregationcyclefreeness;
			boolean forbiddensharing = DefaultConfigurationValues.forbiddensharing;
			if (model.getConfigurator() instanceof ModelConfigurator) {
				aggregationcyclefreeness = ((ModelConfigurator) model.getConfigurator()).isAggregationCycleFree();
				forbiddensharing = ((ModelConfigurator) model.getConfigurator()).isForbiddensharing();
			}

			if (aggregationcyclefreeness) {
				if (result.contains("cycle")) {
					return ErrorType.STRUCTURE;
				}
			}
			if(forbiddensharing){
				if (result.contains("shared")) {
					return ErrorType.STRUCTURE;
				}
			}
		}

		// check invariants for correctness
		MModel mModel = mSystem.model();
		Evaluator evaluator = new Evaluator();
		boolean invariantError = false;
		for (IInvariant invariant : model.classInvariants()) {
			if (invariant.isActivated()) {
				BooleanValue result = (BooleanValue) evaluator.eval(mModel.getClassInvariant(invariant.name()).expandedExpression(),
						mSystem.state());
				if ((invariant.isNegated() && result.isTrue()) || (!invariant.isNegated() && result.isFalse())) {
					LOG.info(LogMessages.unexpectedInvariantResult(invariant));
					invariantError = true;
				}
			}
		}
		
		return invariantError? ErrorType.INVARIANT : ErrorType.NONE;
	}
	
	private Map<Relation, TupleSet> createObjects(
			Map<Relation, TupleSet> relations) throws UseApiException {
		Map<Relation, TupleSet> withoutClasses = new HashMap<Relation, TupleSet>(
				relations);

		String relationName;
		ElementStrategy strategy;

		for (Relation relation : relations.keySet()) {
			strategy = null;
			relationName = relation.name();

			if (!isNegateRelation(relationName) && !isType(relationName)) {
				if (isClass(relation) && !isAssociationClass(relation.name())) {
					strategy = new ObjectStrategy(systemApi, objectStates, relationName);
					iterateTuplesAndCreate(strategy, relations.get(relation));

					withoutClasses.remove(relation);
				}
			}
		}

		return withoutClasses;
	}

	private Map<String, Relation> createElements(
			Map<Relation, TupleSet> relations) throws UseApiException {
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
						strategy = new LinkStrategy(systemApi, objectStates, relationName);

					} else if (isAttributeOfSimpleObject(relationName, relation)) {
						strategy = new AttributeStrategy(systemApi, objectStates,
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
			throws UseApiException {

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

		for (MAssociationClass mAssociationClass : systemApi.getSystem().model()
				.getAssociationClassesOnly()) {
			String associationClassName = mAssociationClass.name() + "_assoc";
			associationClassesRelations.remove(mAssociationClass.name());
			Relation relation = associationClassesRelations
					.remove(associationClassName);
			strategy = new LinkObjectStrategy(systemApi, objectStates, mAssociationClass);

			// filter association relations without a linkobject
			TupleSet validRelations = relations.get(relation).clone();
			for (Iterator<Tuple> it = validRelations.iterator(); it.hasNext();) {
				Tuple tuple = (Tuple) it.next();
				if(tuple.atom(0).equals(TypeConstants.UNDEFINED)){
					it.remove();
				}
			}
			iterateTuplesAndCreate(strategy, validRelations);
		}

		for (String relationName : associationClassesRelations.keySet()) {
			String attributeName = getPartAfterLastSeparator(relationName);
			Relation relation = associationClassesRelations.get(relationName);
			strategy = new AttributeStrategy(systemApi, objectStates, attributeName);

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
			TupleSet tupleSet) throws UseApiException {
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
