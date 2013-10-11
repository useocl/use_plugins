package org.tzi.kodkod.model.visitor;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.ConfigurableType;

/**
 * Visitor to reset the model to the transformation state.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ResetVisitor extends SimpleVisitor {

	@Override
	public void visitModel(IModel model) {
		model.resetConfigurator();
		super.visitModel(model);
	}

	@Override
	public void visitClass(IClass clazz) {
		clazz.resetConfigurator();
		super.visitClass(clazz);
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
		attribute.resetConfigurator();
		super.visitAttribute(attribute);
	}

	@Override
	public void visitAssociation(IAssociation association) {
		association.resetConfigurator();
		super.visitAssociation(association);
	}

	@Override
	public void visitInvariant(IInvariant invariant) {
		invariant.reset();
		super.visitInvariant(invariant);
	}

	@Override
	public void visitConfigurableType(ConfigurableType type) {
		type.resetConfigurator();
		super.visitConfigurableType(type);
	}
}
