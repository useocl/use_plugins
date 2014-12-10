package org.tzi.kodkod.model.config.impl;

import java.util.ArrayList;
import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.SetType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.type.TypeLiterals;
import org.tzi.use.util.StringUtil;

/**
 * Configurator for the model.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ModelConfigurator extends Configurator<IModel> {

	private static final Logger LOG = Logger.getLogger(ModelConfigurator.class);
	
	private IModel model;
	private Formula solutionFormula;
	private boolean aggregationcyclefree;
	private boolean forbiddensharing;
	
	public ModelConfigurator(IModel model) {
		this.model = model;
		solutionFormula = Formula.TRUE;
		aggregationcyclefree = DefaultConfigurationValues.aggregationcyclefreeness;
		forbiddensharing = DefaultConfigurationValues.forbiddensharing;
	}

	@Override
	public Formula constraints(IModel model) {
		Formula formula = super.constraints(model);

		return formula.and(solutionFormula);
	}

	/**
	 * Creates a formula to forbid the given solution.
	 * 
	 * @param relationTuples
	 */
	public void forbid(Map<Relation, TupleSet> relationTuples) {
		Formula formula = Formula.TRUE;

		Relation relation;
		TupleSet tupleSet;

		try {
			for (IClass clazz : model.classes()) {
				relation = clazz.relation();
				tupleSet = relationTuples.get(relation);

				if (!tupleSet.isEmpty()) {
					formula = formula.and(relation.eq(createClassRelationExpression(relationTuples.get(relation))));
				}

				for (IAttribute attribute : clazz.attributes()) {
					relation = attribute.relation();
					tupleSet = relationTuples.get(relation);

					if (!tupleSet.isEmpty()) {
						formula = formula.and(relation.eq(createAttributeRelationExpression(relationTuples.get(relation), attribute.type())));
					}
				}
			}

			for (IAssociation association : model.associations()) {
				relation = association.relation();
				tupleSet = relationTuples.get(relation);

				if (!tupleSet.isEmpty()) {
					formula = formula.and(relation.eq(createAssociationRelationExpression(relationTuples.get(relation), association)));
				}
			}

			solutionFormula = solutionFormula.and(formula.not());
		} catch (Exception e) {
			LOG.error(LogMessages.solutionForbidError);
			LOG.error(e.getMessage());
			if(LOG.isDebugEnabled()){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the relations to forbid the objects of the solution.
	 * 
	 * @param tupleSet
	 * @return
	 * @throws Exception
	 */
	private Expression createClassRelationExpression(TupleSet tupleSet) throws Exception {
		Expression relationExpression = null;
		for (Tuple tuple : tupleSet) {
			Expression objectLiteral = getObjectLiteral(tuple.atom(0));

			if (relationExpression == null) {
				relationExpression = objectLiteral;
			} else {
				relationExpression = relationExpression.union(objectLiteral);
			}
		}
		return relationExpression;
	}

	/**
	 * Creates the relations to forbid the special attribute values.
	 * 
	 * @param tupleSet
	 * @param attributeType
	 * @return
	 * @throws Exception
	 */
	private Expression createAttributeRelationExpression(TupleSet tupleSet, Type attributeType) throws Exception {
		Expression relationExpression = null;
		for (Tuple tuple : tupleSet) {
			Expression objectLiteral = getObjectLiteral(tuple.atom(0));

			if (attributeType.isIntegerCollection()) {
				attributeType = ((SetType) attributeType).elemType();
			}

			Expression valueLiteral = getValueLiteral(attributeType, tuple);

			Expression tupleExpression = objectLiteral.product(valueLiteral);
			if (relationExpression == null) {
				relationExpression = tupleExpression;
			} else {
				relationExpression = relationExpression.union(tupleExpression);
			}
		}
		return relationExpression;
	}

	/**
	 * Creates the relations to forbid the links.
	 * 
	 * @param tupleSet
	 * @param association
	 * @return
	 * @throws Exception
	 */
	private Expression createAssociationRelationExpression(TupleSet tupleSet, IAssociation association) throws Exception {
		Expression relationExpression = null;

		ArrayList<IAssociationEnd> associationEnds = new ArrayList<IAssociationEnd>();
		if (association.associationClass() != null) {
			associationEnds.add(association.associationClass());
		}
		associationEnds.addAll(association.associationEnds());

		for (Tuple tuple : tupleSet) {

			Expression linkExpression = null;
			for (int j = 0; j < tuple.arity(); j++) {
				Expression objectLiteral = getObjectLiteral(tuple.atom(j));

				if (linkExpression == null) {
					linkExpression = objectLiteral;
				} else {
					linkExpression = linkExpression.product(objectLiteral);
				}
			}

			if (relationExpression == null) {
				relationExpression = linkExpression;
			} else {
				relationExpression = relationExpression.union(linkExpression);
			}
		}
		return relationExpression;
	}

	/**
	 * Returns the literal of an atom for an object.
	 * 
	 * @param objectType
	 * @param objectAtom
	 * @return
	 * @throws Exception
	 */
	private Expression getObjectLiteral(Object objectAtom) throws Exception {
		if (objectAtom.toString().equals(TypeConstants.UNDEFINED)) {
			return model.typeFactory().undefinedType().expression();
		}
		
		String[] split = objectAtom.toString().split("_");
		
		String className;
		String object;
		if(split.length == 2){
			className = split[0];
			object = split[1];
		}
		else {
			int middle = split.length/2;
			StringBuilder left = new StringBuilder(split[0]);
			StringBuilder right = new StringBuilder(split[middle]);
			for(int i = 1; i < middle; i++){
				left.append("_");
				left.append(split[i]);
				right.append("_");
				right.append(split[(middle)+i]);
			}
			
			className = left.toString();
			object = right.toString();
		}


		IClass clazz = model.getClass(className);
		if (clazz != null) {
			clazz.objectType().addTypeLiteral(object);
			return clazz.objectType().getTypeLiteral(object);
		} else {
			throw new Exception("Could not map object atom "
					+ StringUtil.inQuotes(objectAtom.toString())
					+ " to a class from the model.");
		}
	}

	/**
	 * Returns the literal of an atom.
	 * 
	 * @param attributeType
	 * @param tuple
	 * @return
	 */
	private Expression getValueLiteral(Type attributeType, Tuple tuple) {
		Object valueAtom = tuple.atom(1);

		if (valueAtom.toString().equals(TypeConstants.UNDEFINED)) {
			return model.typeFactory().undefinedType().expression();
		} else if (valueAtom.toString().equals(TypeConstants.UNDEFINED_SET)) {
			return model.typeFactory().undefinedSetType().expression();
		} else if (attributeType.isInteger()) {
			return getNotUndefinedValueLiteral(valueAtom.toString(), attributeType);
		} else {
			String value;
			if (attributeType.isBoolean()) {
				String first = valueAtom.toString().substring(0, 1).toUpperCase();
				value = first + valueAtom.toString().substring(1);
			} else {
				value = valueAtom.toString().split("_")[1];
			}
			return getNotUndefinedValueLiteral(value, attributeType);
		}
	}

	/**
	 * Returns the literal for a value of a type.
	 * 
	 * @param value
	 * @param attributeType
	 * @return
	 */
	private Expression getNotUndefinedValueLiteral(String value, Type attributeType) {
		if (attributeType instanceof TypeLiterals) {
			TypeLiterals type = (TypeLiterals) attributeType;
			type.addTypeLiteral(value);
			return type.getTypeLiteral(value);

		}
		return null;
	}

	public void setAggregationCycleFreeness(boolean aggregationcyclefreeness) {
		this.aggregationcyclefree = aggregationcyclefreeness;
	}

	public boolean isAggregationCycleFree() {
		return aggregationcyclefree;
	}

	public void setForbiddensharing(boolean forbiddensharing) {
		this.forbiddensharing = forbiddensharing;
	}
	
	public boolean isForbiddensharing() {
		return forbiddensharing;
	}
}
