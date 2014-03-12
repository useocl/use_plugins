package org.tzi.kodkod.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.config.impl.StringConfigurator;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Represents the string type of the model.
 * 
 * @author Hendrik Reitmann
 */
public class StringType extends ConfigurableType {

	private TypeAtoms integerType;
	private Relation toStringMap;
	
	StringType(TypeAtoms integerType) {
		super(TypeConstants.STRING);
		this.integerType=integerType;
	}

	@Override
	public boolean isString() {
		return true;
	}
	
	@Override
	public void addTypeLiteral(String literal) {
		if (literal.equals("false")) {
			literal = literal.replace('f', 'F');
		} else if (literal.equals("true")) {
			literal = literal.replace('t', 'T');
		}

		String literalName = name()+"_"+literal;
		
		if(!typeLiterals().containsKey(literalName)){			
			Relation literalRelation = Relation.unary(literalName);		
			typeLiterals().put(literalName, literalRelation);
		}
	}
	
	@Override
	protected void createTypeLiterals() {
		typeLiterals=new HashMap<String, Expression>();
		typeLiterals.put(TypeConstants.STRING_TRUE, Relation.unary(TypeConstants.STRING_TRUE));
		typeLiterals.put(TypeConstants.STRING_FALSE, Relation.unary(TypeConstants.STRING_FALSE));
	}
	
	public Relation toStringMap() {
		if (toStringMap == null) {
			toStringMap = Relation.binary(TypeConstants.TO_STRING_MAP);
		}
		return toStringMap;
	}

	public TupleSet toStringMapBound(TupleFactory tupleFactory) {
		final TupleSet toStringMapBound = tupleFactory.noneOf(2);
		toStringMapBound.add(tupleFactory.tuple(TypeConstants.TRUE, TypeConstants.STRING_TRUE));
		toStringMapBound.add(tupleFactory.tuple(TypeConstants.FALSE, TypeConstants.STRING_FALSE));
		for (Object integerAtom : integerType.atoms()) {
			toStringMapBound.add(tupleFactory.tuple(integerAtom, "String_" + integerAtom));
		}
		return toStringMapBound;
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitStringType(this);
	}
	
	@Override
	protected List<Object> createAtomList() {
		return configurator.atoms(this,new ArrayList<Object>(typeLiterals().keySet()));
	}
	
	@Override
	public void resetConfigurator() {
		configurator = new StringConfigurator();
	}
}
