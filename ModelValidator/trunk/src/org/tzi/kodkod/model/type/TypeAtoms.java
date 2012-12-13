package org.tzi.kodkod.model.type;

import java.util.List;

import org.tzi.kodkod.model.visitor.Visitor;

import kodkod.ast.Relation;

/**
 * Abstract base class for all types with atoms.
 * 
 * @author Hendrik Reitmann
 */
public abstract class TypeAtoms extends Type {

	private String name;
	protected List<Object> atoms;

	TypeAtoms(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the type.
	 * 
	 * @return
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the list with the atoms.
	 * 
	 * @return
	 */
	public List<Object> atoms() {
		if (atoms == null) {
			atoms = createAtomList();
		}
		return atoms;
	}

	@Override
	protected Relation createRelation() {
		return Relation.unary(name);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitTypeAtoms(this);
	}

	/**
	 * Creates the list with atoms for this type.
	 * 
	 * @return
	 */
	protected abstract List<Object> createAtomList();
}
