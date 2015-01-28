package org.tzi.kodkod.model.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.iface.IClass;

/**
 * Represents an object type in the model.
 * 
 * @author Hendrik Reitmann
 */
public class ObjectType extends TypeLiterals {

	private IClass clazz;
	private Set<String[]> values;
	private int valueSize;

	public ObjectType(IClass clazz) {
		super(clazz.name());
		this.clazz = clazz;
		valueSize = 0;
		values = new HashSet<String[]>();
	}

	/**
	 * Returns the class of this type.
	 * 
	 * @return
	 */
	public IClass clazz() {
		return clazz;
	}

	@Override
	protected Relation createRelation() {
		return clazz.relation();
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return clazz.lowerBound(tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return clazz.upperBound(tupleFactory);
	}

	@Override
	public boolean isObjectType() {
		return true;
	}

	@Override
	public void addTypeLiteral(String literal) {
		String literalName = name() + "_" + literal;

		if (!typeLiterals().containsKey(literalName)) {
			Relation literalRelation = Relation.unary(literalName);
			typeLiterals().put(literalName, literalRelation);
		}
	}

	@Override
	protected void createTypeLiterals() {
		typeLiterals = new HashMap<String, Expression>();
	}

	@Override
	protected Set<Object> createAtomList() {
		Set<Object> atoms = new LinkedHashSet<Object>();
		atoms.addAll(typeLiterals().keySet());

		for (String[] specific : values) {
			if(atoms.size() >= valueSize){
				break;
			}
			atoms.add(name() + "_" + specific[0]);
		}
		
		int i = atoms.size() + 1;
		while (atoms.size() < valueSize) {
			atoms.add(name() + "_" + name().toLowerCase() + i);
			i++;
		}

		return atoms;
	}

	/**
	 * Sets the values of this type.
	 * 
	 * @param values
	 */
	public void setValues(List<String[]> values) {
		this.values = new HashSet<String[]>(values);
		atoms = createAtomList();
	}

	/**
	 * Adds a value of this type.
	 * 
	 * @param value
	 */
	public void addValue(String[] value) {
		this.values.add(value);
		atoms = createAtomList();
	}

	/**
	 * Sets the maximum number of atoms from this type.
	 * 
	 * @param size
	 */
	public void setValueSize(int size) {
		valueSize = size;
		atoms = createAtomList();
	}
}
