package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;
import kodkod.ast.Formula;

import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Transformation methods for conditionla operations.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ConditionalOperationGroup extends OCLOperationGroup {

	public ConditionalOperationGroup(TypeFactory typeFactory) {
		super(typeFactory);
	}

	public final Expression if_then_else(Expression cond, Expression l, Expression r) {
		return cond.eq(undefined).thenElse(undefined, cond.eq(booleanTrue).thenElse(l, r));
	}

	public final Expression if_then_else(Expression cond, Formula l, Expression r) {
		return cond.eq(undefined).thenElse(undefined, cond.eq(booleanTrue).thenElse(l.thenElse(booleanTrue, booleanFalse), r));
	}

	public final Expression if_then_else(Expression cond, Expression l, Formula r) {
		return cond.eq(undefined).thenElse(undefined, cond.eq(booleanTrue).thenElse(l, r.thenElse(booleanTrue, booleanFalse)));
	}

	public final Expression if_then_else(Expression cond, Formula l, Formula r) {
		return cond.eq(undefined).thenElse(undefined,
				cond.eq(booleanTrue).thenElse(l.thenElse(booleanTrue, booleanFalse), r.thenElse(booleanTrue, booleanFalse)));
	}

	public final Expression if_then_else(Formula cond, Expression l, Expression r) {
		return cond.thenElse(l, r);
	}

	public final Expression if_then_else(Formula cond, Formula l, Expression r) {
		return cond.thenElse(l.thenElse(booleanTrue, booleanFalse), r);
	}

	public final Expression if_then_else(Formula cond, Expression l, Formula r) {
		return cond.thenElse(l, r.thenElse(booleanTrue, booleanFalse));
	}

	public final Formula if_then_else(Formula cond, Formula l, Formula r) {
		return cond.implies(l).and(cond.not().implies(r));
	}
}
