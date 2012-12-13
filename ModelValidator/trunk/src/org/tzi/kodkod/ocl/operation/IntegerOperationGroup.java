package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;

import org.tzi.kodkod.model.type.StringType;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Contains transformation methods for integer operations.
 * 
 * @author Hendrik
 */
public class IntegerOperationGroup extends OCLOperationGroup {

	public IntegerOperationGroup(TypeFactory typeFactory) {
		super(typeFactory);

		symbolOperationMapping.put("*", "multiply");
		symbolOperationMapping.put("-", "minus");
		symbolOperationMapping.put("/", "div");
		symbolOperationMapping.put("+", "plus");
		symbolOperationMapping.put("<", "less");
		symbolOperationMapping.put("<=", "lessOrEqual");
		symbolOperationMapping.put(">", "greater");
		symbolOperationMapping.put(">=", "greaterOrEqual");
	}

	public final Expression abs(Expression e) {
		return e.eq(undefined).thenElse(undefined, e.sum().abs().toExpression());
	}

	public final Expression div(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).or(r.sum().eq(IntConstant.constant(0)))
				.thenElse(undefined, l.sum().divide(r.sum()).toExpression());
	}

	public final Expression floor(Expression e) {
		return e.eq(undefined).thenElse(undefined, e);
	}

	public final Formula greater(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().gt(r.sum()));
	}

	public final Formula greaterOrEqual(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().gte(r.sum()));
	}

	public final Formula less(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().lt(r.sum()));
	}

	public final Formula lessOrEqual(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().lte(r.sum()));
	}

	public final Expression max(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().gte(r.sum()).thenElse(l, r));
	}

	public final Expression min(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().lte(r.sum()).thenElse(l, r));
	}

	public final Expression minus(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().minus(r.sum()).toExpression());
	}

	public final Expression mod(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).or(r.sum().eq(IntConstant.constant(0)))
				.thenElse(undefined, l.sum().modulo(r.sum()).toExpression());
	}

	public final Expression multiply(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().multiply(r.sum()).toExpression());
	}

	public final Expression negation(Expression e) {
		return e.eq(undefined).thenElse(undefined, e.sum().negate().toExpression());
	}

	public final Expression plus(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().plus(r.sum()).toExpression());
	}

	public final Expression round(Expression e) {
		return e;
	}

	public final Expression toString(Expression src) {
		StringType stringType = (StringType) typeFactory.stringType();
		return src.eq(undefined).thenElse(undefined, src.join(stringType.toStringMap()));
	}
}
