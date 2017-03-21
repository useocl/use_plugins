package org.tzi.kodkod.model.visitor;

import java.util.Iterator;
import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.impl.DerivedAssociation;
import org.tzi.kodkod.model.impl.DerivedAttribute;
import org.tzi.kodkod.model.type.BooleanType;
import org.tzi.kodkod.model.type.IntegerType;
import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.type.TypeLiterals;

/**
 * Visitor to bind the bounds to the relations.
 * 
 * @author Hendrik Reitmann
 */
public class BoundsVisitor extends SimpleVisitor {

	private Bounds bounds;
	private TupleFactory tupleFactory;

	public BoundsVisitor(Bounds bounds, TupleFactory tupleFactory) {
		this.bounds = bounds;
		this.tupleFactory = tupleFactory;
	}

	@Override
	public void visitClass(IClass clazz) {
		bounds.bound(clazz.relation(), clazz.lowerBound(tupleFactory), clazz.upperBound(tupleFactory));
		if (clazz.existsInheritance()) {
			bounds.bound(clazz.inheritanceRelation(), clazz.inheritanceLowerBound(tupleFactory), clazz.inheritanceUpperBound(tupleFactory));
		}
		super.visitClass(clazz);
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
		if(attribute instanceof DerivedAttribute){
			return;
		}
		bounds.bound(attribute.relation(), attribute.lowerBound(tupleFactory), attribute.upperBound(tupleFactory));
	}

	@Override
	public void visitAssociation(IAssociation association) {
		if(association instanceof DerivedAssociation){
			return;
		}
		bounds.bound(association.relation(), association.lowerBound(tupleFactory), association.upperBound(tupleFactory));
	}

	@Override
	public void visitType(Type type) {
		bounds.bound(type.relation(), type.lowerBound(tupleFactory), type.upperBound(tupleFactory));
	}

	@Override
	public void visitTypeLiterals(TypeLiterals type) {
		super.visitTypeLiterals(type);

		Map<String, Expression> typeLiterals = type.typeLiterals();
		TupleSet bound;
		for (String name : typeLiterals.keySet()) {
			bound = tupleFactory.noneOf(1);
			bound.add(tupleFactory.tuple(name));

			bounds.boundExactly((Relation) typeLiterals.get(name), bound);
		}
	}

	@Override
	public void visitStringType(StringType stringType) {
		super.visitStringType(stringType);
		bounds.boundExactly(stringType.toStringMap(), stringType.toStringMapBound(tupleFactory));
	}

	@Override
	public void visitBooleanType(BooleanType booleanType) {
		visitTypeAtoms(booleanType);
		TupleSet bound = tupleFactory.noneOf(1);
		bound.add(tupleFactory.tuple(TypeConstants.TRUE));
		bounds.boundExactly((Relation) booleanType.typeLiterals().get(TypeConstants.BOOLEAN_TRUE), bound);

		bound = tupleFactory.noneOf(1);
		bound.add(tupleFactory.tuple(TypeConstants.FALSE));
		bounds.boundExactly((Relation) booleanType.typeLiterals().get(TypeConstants.BOOLEAN_FALSE), bound);
	}

	@Override
	public void visitIntegerType(IntegerType integerType) {
		TupleSet tupleSet = integerType.upperBound(tupleFactory);
		Iterator<Tuple> iterator = tupleSet.iterator();

		Tuple tuple;
		while (iterator.hasNext()) {
			tuple = iterator.next();
			bounds.boundExactly((Integer) tuple.atom(0), tupleFactory.setOf(tuple));
		}
	}
}
