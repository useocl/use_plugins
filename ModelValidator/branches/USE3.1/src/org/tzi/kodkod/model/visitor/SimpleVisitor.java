package org.tzi.kodkod.model.visitor;

import java.util.Iterator;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IElement;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.AnyType;
import org.tzi.kodkod.model.type.BooleanType;
import org.tzi.kodkod.model.type.ConfigurableType;
import org.tzi.kodkod.model.type.IntegerType;
import org.tzi.kodkod.model.type.RealType;
import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeAtoms;
import org.tzi.kodkod.model.type.TypeLiterals;

/**
 * Simple implementation of the visitor interface for the model
 * 
 * @author Hendrik Reitmann
 * 
 */
public class SimpleVisitor implements Visitor {

	protected void iterate(Iterator<?> iter) {
		while (iter.hasNext()) {
			((IElement) iter.next()).accept(this);
		}
	}

	@Override
	public void visitModel(IModel model) {
		iterate(model.typeFactory().buildInTypes().iterator());
		iterate(model.enumTypes().iterator());
		iterate(model.classes().iterator());
		iterate(model.associations().iterator());
	}

	@Override
	public void visitClass(IClass clazz) {
		clazz.objectType().accept(this);
		iterate(clazz.attributes().iterator());
		for (IInvariant invariant : clazz.invariants()) {
			invariant.accept(this);
		}
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
	}

	@Override
	public void visitAssociation(IAssociation association) {
	}

	@Override
	public void visitInvariant(IInvariant invariant) {
	}

	@Override
	public void visitType(Type type) {
	}

	@Override
	public void visitTypeAtoms(TypeAtoms type) {
		visitType(type);
	}

	@Override
	public void visitTypeLiterals(TypeLiterals type) {
		visitTypeAtoms(type);
	}

	@Override
	public void visitConfigurableType(ConfigurableType type) {
		visitTypeLiterals(type);
	}

	@Override
	public void visitStringType(StringType stringType) {
		visitConfigurableType(stringType);
	}

	@Override
	public void visitRealType(RealType realType) {
		visitConfigurableType(realType);
	}

	@Override
	public void visitBooleanType(BooleanType booleanType) {
		visitTypeLiterals(booleanType);
	}

	@Override
	public void visitIntegerType(IntegerType integerType) {
		visitConfigurableType(integerType);
	}

	@Override
	public void visitAnyType(AnyType anyType) {
	}

}
