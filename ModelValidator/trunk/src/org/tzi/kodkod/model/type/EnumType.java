package org.tzi.kodkod.model.type;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Instances of this class represent enums of the model.
 * 
 * @author Hendrik Reitmann
 */
public class EnumType extends TypeLiterals {

	private final List<String> literals;
	
	EnumType(String name,List<String> literals) {
		super(name);
		this.literals=literals;
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		final TupleSet enumLower = tupleFactory.noneOf(1);
		for(Object atom : atoms()){
			enumLower.add(tupleFactory.tuple(atom));
		}
		return enumLower;
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return lowerBound(tupleFactory);
	}


	@Override
	protected Set<Object> createAtomList() {
		Set<Object> atoms=new LinkedHashSet<Object>();
		for(String literal : literals){
			atoms.add(name()+"_"+literal);
		}
		return atoms;
	}
	
	@Override
	public boolean isEnum() {
		return true;
	}

	@Override
	public void addTypeLiteral(String literal) {
		String literalName = name()+"_"+literal;
		
		if(!typeLiterals().containsKey(literalName)){			
			Relation literalRelation = Relation.unary(literalName);		
			typeLiterals().put(literalName, literalRelation);
		}
	}
	
	@Override
	protected void createTypeLiterals() {
		typeLiterals = new HashMap<String, Expression>();
	}
}
