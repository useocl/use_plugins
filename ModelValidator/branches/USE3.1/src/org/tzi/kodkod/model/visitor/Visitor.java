package org.tzi.kodkod.model.visitor;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
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
 * Visitor interface for model elements.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface Visitor {

	public void visitModel(IModel model);

	public void visitClass(IClass clazz);

	public void visitAttribute(IAttribute attribute);

	public void visitAssociation(IAssociation association);

	public void visitInvariant(IInvariant invariant);

	public void visitType(Type type);

	public void visitTypeAtoms(TypeAtoms type);

	public void visitTypeLiterals(TypeLiterals type);

	public void visitConfigurableType(ConfigurableType type);

	public void visitIntegerType(IntegerType integerType);

	public void visitBooleanType(BooleanType booleanType);

	public void visitStringType(StringType stringType);

	public void visitRealType(RealType realType);

	public void visitAnyType(AnyType anyType);
}
