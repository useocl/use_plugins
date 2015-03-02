package org.tzi.kodkod.model.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Decl;
import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
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

	private Map<IClass, List<Variable>> classVariables = new HashMap<IClass, List<Variable>>();
	private Map<String, Variable> literalMapper = new HashMap<String, Variable>();
	
	public ModelConfigurator(IModel model) {
		this.model = model;
		solutionFormula = Formula.TRUE;
		aggregationcyclefree = DefaultConfigurationValues.AGGREGATIONCYCLEFREENESS;
		forbiddensharing = DefaultConfigurationValues.FORBIDDENSHARING;
	}

	@Override
	public Formula constraints(IModel model) {
		Formula formula = super.constraints(model);

		return formula.and(solutionFormula);
	}

	public void setSolutionFormula(Formula formula){
		solutionFormula = formula;
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

		Decls globDecl = null;
		classVariables.clear();
		literalMapper.clear();
		
		for(IClass cls : model.classes()){
			int clsNum = relationTuples.get(cls.relation()).size();
			
			List<Variable> varList = new ArrayList<Variable>(clsNum);
			
			for(int i = 0; i < clsNum; i++){
				Variable obj = Variable.unary(cls.name().charAt(0) + String.valueOf(i));
				Decl decl = obj.oneOf(cls.relation());
				
				varList.add(obj);
				
				globDecl = (globDecl == null) ? decl : globDecl.and(decl);
			}
			
			classVariables.put(cls, varList);
		}
		
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

			solutionFormula = solutionFormula.and(formula.forSome(globDecl).not());
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
			relationExpression = (relationExpression == null) ? objectLiteral : relationExpression.union(objectLiteral);
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
			relationExpression = (relationExpression == null) ? tupleExpression : relationExpression.union(tupleExpression);
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
				linkExpression = (linkExpression == null) ? objectLiteral : linkExpression.product(objectLiteral) ;
			}
			relationExpression = (relationExpression == null) ? linkExpression : relationExpression.union(linkExpression) ;
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
		if(literalMapper.containsKey(objectAtom)){
			return literalMapper.get(objectAtom);
		}
		
		IClass clazz = extractClassFromAtom(objectAtom.toString());
		if (clazz == null) {
			throw new Exception("Could not map object atom "
					+ StringUtil.inQuotes(objectAtom.toString())
					+ " to a class from the model.");
		}
		
		if(!classVariables.containsKey(clazz)){
			throw new Exception("Class variable initialization failed during encoding of previous solutions.");
		}
		
		Variable placeHolder;
		try {
			placeHolder = classVariables.get(clazz).remove(0);
		}
		catch(IndexOutOfBoundsException ex){
			throw new Exception("More solution instances than placeholders for class " + clazz.name(), ex);
		}
		literalMapper.put(objectAtom.toString(), placeHolder);
		return placeHolder;
	}

	/**
	 * Searches for a class in the model that prefixes (by string comparison)
	 * the object atom.
	 * <p>
	 * This process is not deterministic, i. e. classes can hide other classes
	 * if they contain underscores and share prefixes. Example: Class
	 * {@code Car} hides class {@code Car_BMW}.
	 * 
	 * @return class of the object atom, null if no class could be determined
	 */
	private IClass extractClassFromAtom(String objectAtom) {
		String[] split = objectAtom.toString().split("_");
		
		if(split.length <= 1){
			return model.getClass(objectAtom);
		}
		
		StringBuilder className = new StringBuilder();
		boolean first = true;
		for(int i = 0; i < split.length; i++){
			if(!first){
				className.append("_");
			}
			className.append(split[i]);
			first = false;
			
			IClass cls = model.getClass(className.toString());
			if(cls != null){
				return cls;
			}
		}
		return null;
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
