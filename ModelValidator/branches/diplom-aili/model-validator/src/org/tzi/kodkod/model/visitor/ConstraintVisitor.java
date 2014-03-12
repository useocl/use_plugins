package org.tzi.kodkod.model.visitor;

import kodkod.ast.Formula;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;

/**
 * Visitor to build the constraints for kodkod.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ConstraintVisitor extends SimpleVisitor {

	private Formula formula = null;

	public ConstraintVisitor() {
		formula = Formula.TRUE;
	}

	@Override
	public void visitModel(IModel model) {
		formula = formula.and(model.constraints());
		super.visitModel(model);
	}

	@Override
	public void visitClass(IClass clazz) {
		formula = formula.and(clazz.constraints());
		super.visitClass(clazz);
	}

	@Override
	public void visitAttribute(IAttribute attribute) {
		formula = formula.and(attribute.constraints());
	}

	@Override
	public void visitAssociation(IAssociation association) {
		formula = formula.and(association.constraints());
	}

	@Override
	public void visitInvariant(IInvariant invariant) {
		if (invariant.isActivated()) {
			formula = formula.and(invariant.formula());
		}
	}

	public Formula getFormula() {
		return formula;
	}
}
