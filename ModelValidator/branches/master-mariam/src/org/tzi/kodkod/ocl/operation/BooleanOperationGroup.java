package org.tzi.kodkod.ocl.operation;

import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Formula;

import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Contains all boolean transformation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class BooleanOperationGroup extends OCLOperationGroup {

	public BooleanOperationGroup(TypeFactory typeFactory) {
		super(typeFactory);
	}

	public final Formula and(Expression l, Expression r) {
		return l.eq(booleanTrue).and(r.eq(booleanTrue));
	}

	public final Formula and(Formula l, Expression r) {
		return l.and(r.eq(booleanTrue));
	}

	public final Formula and(Expression l, Formula r) {
		return l.eq(booleanTrue).and(r);
	}

	public final Formula and(Formula l, Formula r) {
		return l.and(r);
	}

	public final Formula implies(Expression l, Expression r) {
		return l.eq(booleanFalse).not().implies(r.eq(booleanTrue));
	}

	public final Formula implies(Formula l, Expression r) {
		return l.implies(r.eq(booleanTrue));
	}

	public final Formula implies(Expression l, Formula r) {
		return l.eq(booleanFalse).not().implies(r);
	}

	public final Formula implies(Formula l, Formula r) {
		return l.implies(r);
	}

	public final Formula not(Expression e) {
		return e.eq(booleanFalse);
	}

	public final Formula not(Formula e) {
		return e.not();
	}

	public final Formula or(Expression l, Expression r) {
		return l.eq(booleanTrue).or(r.eq(booleanTrue));
	}

	public final Formula or(Formula l, Expression r) {
		return l.or(r.eq(booleanTrue));
	}

	public final Formula or(Expression l, Formula r) {
		return l.eq(booleanTrue).or(r);
	}

	public final Formula or(Formula l, Formula r) {
		return l.or(r);
	}

	public final Expression toString(Expression src) {
		StringType stringType = (StringType) typeFactory.stringType();
		return src.eq(undefined).thenElse(undefined, src.join(stringType.toStringMap()));
	}

	public final Expression toString(Formula src) {
		StringType stringType = (StringType) typeFactory.stringType();
		Map<String, Expression> typeLiterals = stringType.typeLiterals();
		return src.thenElse(typeLiterals.get(TypeConstants.STRING_TRUE), typeLiterals.get(TypeConstants.STRING_FALSE));
	}

	public final Formula xor(Expression l, Expression r) {
		return l.eq(booleanTrue).or(r.eq(booleanTrue)).and(l.eq(booleanFalse).or(r.eq(booleanFalse)));
	}

	public final Formula xor(Formula l, Expression r) {
		return l.or(r.eq(booleanTrue)).and(l.not().or(r.eq(booleanFalse)));
	}

	public final Formula xor(Expression l, Formula r) {
		return l.eq(booleanTrue).or(r).and(l.eq(booleanFalse).or(r.not()));

	}

	public final Formula xor(Formula l, Formula r) {
		return l.and(r.not()).or(l.not().and(r));
	}
}