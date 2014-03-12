package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;
import kodkod.ast.Formula;

import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Contains all ocl any transformation methods
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AnyOperationGroup extends OCLOperationGroup {

	public AnyOperationGroup(TypeFactory typeFactory, boolean registerSymbols) {
		super(typeFactory);

		if (registerSymbols) {
			symbolOperationMapping.put("=", "equality");
			symbolOperationMapping.put("<>", "inequality");
		}
	}

	public Formula equality(Expression l, Expression r) {
		return l.eq(r);
	}

	public Formula equality(Formula l, Expression r) {
		return l.and(r.eq(booleanTrue)).or(l.not().and(r.eq(booleanFalse)));
	}

	public Formula equality(Expression l, Formula r) {
		return l.eq(booleanTrue).and(r).or(l.eq(booleanFalse).and(r.not()));
	}

	public Formula equality(Formula l, Formula r) {
		return l.iff(r);
	}

	public Formula inequality(Expression l, Expression r) {
		return l.eq(r).not();
	}

	public Formula inequality(Formula l, Expression r) {
		return r.eq(booleanTrue).implies(l.not()).and(r.eq(booleanFalse).implies(l));
	}

	public Formula inequality(Expression l, Formula r) {
		return l.eq(booleanTrue).implies(r.not()).and(l.eq(booleanFalse).implies(r));
	}

	public Formula inequality(Formula l, Formula r) {
		return l.iff(r).not();
	}

	public Formula isDefined(Expression src) {
		return src.eq(undefined).not();
	}

	public Formula isDefined(Formula src) {
		return Formula.TRUE;
	}

	public Expression oclAsType(Expression src, Expression cls) {
		return src.in(cls).thenElse(src, undefined);
	}

	public Formula oclAsType(Formula src, Expression cls) {
		return src;
	}

	public Formula oclIsKindOf(Expression src, Expression cls) {
		return src.eq(undefined).or(src.in(cls));
	}

	public Formula oclIsKindOf(Formula src, Expression cls) {
		return cls.eq(typeFactory.booleanType().relation()).or(cls.eq(Expression.UNIV));
	}

	public Formula oclIsTypeOf(Expression src, Expression cls) {
		return cls.eq(Expression.UNIV).not().and(src.in(cls));
	}

	public Formula oclIsTypeOf(Formula src, Expression cls) {
		return cls.eq(typeFactory.booleanType().relation());
	}

	public Formula isUndefined(Expression src) {
		return src.eq(undefined);
	}

	public Formula isUndefined(Formula src) {
		return Formula.FALSE;
	}
}
