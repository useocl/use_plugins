package org.tzi.kodkod.model.impl;

import kodkod.ast.Formula;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Implementation of IInvariant.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class Invariant implements IInvariant {

	private static final Logger LOG = Logger.getLogger(Invariant.class);

	private String name;
	private IClass clazz;

	private Formula original;
	private Formula formula = Formula.TRUE;
	private boolean activated = true;
	private boolean negated = false;

	Invariant(String name, IClass clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public void setFormula(Formula formula) {
		this.formula = formula;
		original = formula;
	}

	@Override
	public Formula formula() {
		LOG.debug("Invariant " + name + ": " + PrintHelper.prettyKodkod(formula));

		return formula;
	}

	@Override
	public IClass clazz() {
		return clazz;
	}

	@Override
	public void activate() {
		activated = true;
	}

	@Override
	public void deactivate() {
		activated = false;
	}

	@Override
	public void negate() {
		if(negated){
			return;
		}
		formula = formula.not();
		negated = true;
	}
	
	@Override
	public void denegate() {
		formula = original;
		negated = false;
	}

	@Override
	public boolean isActivated() {
		return activated;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitInvariant(this);
	}

	@Override
	public boolean isNegated() {
		return negated;
	}

	@Override
	public void reset() {
		activate();
		if (isNegated()) {
			formula = original;
			negated = false;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name + ": ");
		builder.append("active: " + isActivated() + "; ");
		builder.append("negated: " + isNegated());
		return builder.toString();
	}
}
