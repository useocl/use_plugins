package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;

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
	
	public final IntExpression abs(IntExpression e) {
		return e.abs();
	}

	public final Expression div(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).or(r.sum().eq(IntConstant.constant(0)))
				.thenElse(undefined, l.sum().divide(r.sum()).toExpression());
	}
	
	public final Expression div(Expression l, IntExpression r) {
		return l.eq(undefined).or(r.eq(IntConstant.constant(0)))
				.thenElse(undefined, l.sum().divide(r).toExpression());
	}
	
	public final Expression div(IntExpression l, Expression r) {
		return r.eq(undefined).or(r.sum().eq(IntConstant.constant(0)))
				.thenElse(undefined, l.divide(r.sum()).toExpression());
	}
	
	public final Expression div(IntExpression l, IntExpression r) {
		return r.eq(IntConstant.constant(0)).thenElse(undefined, l.divide(r).toExpression());
	}

	public final Expression floor(Expression e) {
		return e.eq(undefined).thenElse(undefined, e);
	}

	public final IntExpression floor(IntExpression e) {
		return e;
	}

	public final Formula greater(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().gt(r.sum()));
	}
	
	public final Formula greater(Expression l, IntExpression r) {
		return l.eq(undefined).not().and(l.sum().gt(r));
	}
	
	public final Formula greater(IntExpression l, Expression r) {
		return r.eq(undefined).not().and(l.gt(r.sum()));
	}
	
	public final Formula greater(IntExpression l, IntExpression r) {
		return l.gt(r);
	}
	
	public final Formula greaterOrEqual(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().gte(r.sum()));
	}
	
	public final Formula greaterOrEqual(Expression l, IntExpression r) {
		return l.eq(undefined).not().and(l.sum().gte(r));
	}
	
	public final Formula greaterOrEqual(IntExpression l, Expression r) {
		return r.eq(undefined).not().and(l.gte(r.sum()));
	}

	public final Formula greaterOrEqual(IntExpression l, IntExpression r) {
		return l.gte(r);
	}
	
	public final Formula less(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().lt(r.sum()));
	}
	
	public final Formula less(Expression l, IntExpression r) {
		return l.eq(undefined).not().and(l.sum().lt(r));
	}
	
	public final Formula less(IntExpression l, Expression r) {
		return r.eq(undefined).not().and(l.lt(r.sum()));
	}

	public final Formula less(IntExpression l, IntExpression r) {
		return l.lt(r);
	}

	public final Formula lessOrEqual(Expression l, Expression r) {
		return l.eq(undefined).not().and(r.eq(undefined).not()).and(l.sum().lte(r.sum()));
	}
	
	public final Formula lessOrEqual(IntExpression l, Expression r) {
		return r.eq(undefined).not().and(l.lte(r.sum()));
	}
	
	public final Formula lessOrEqual(Expression l, IntExpression r) {
		return l.eq(undefined).not().and(l.sum().lte(r));
	}
	
	public final Formula lessOrEqual(IntExpression l, IntExpression r) {
		return l.lte(r);
	}

	public final Expression max(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().gte(r.sum()).thenElse(l, r));
	}
	
	public final Expression max(Expression l, IntExpression r) {
		return l.eq(undefined).thenElse(undefined, l.sum().gte(r).thenElse(l, r.toExpression()));
	}
	
	public final Expression max(IntExpression l, Expression r) {
		return r.eq(undefined).thenElse(undefined, l.gte(r.sum()).thenElse(l.toExpression(), r));
	}

	public final IntExpression max(IntExpression l, IntExpression r) {
		return l.gte(r).thenElse(l, r);
	}

	public final Expression min(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().lte(r.sum()).thenElse(l, r));
	}
	
	public final Expression min(Expression l, IntExpression r) {
		return l.eq(undefined).thenElse(undefined, l.sum().lte(r).thenElse(l, r.toExpression()));
	}
	
	public final Expression min(IntExpression l, Expression r) {
		return r.eq(undefined).thenElse(undefined, l.lte(r.sum()).thenElse(l.toExpression(), r));
	}
	
	public final IntExpression min(IntExpression l, IntExpression r) {
		return l.lte(r).thenElse(l, r);
	}

	public final Expression minus(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().minus(r.sum()).toExpression());
	}
	
	public final Expression minus(Expression l, IntExpression r) {
		return l.eq(undefined).thenElse(undefined, l.sum().minus(r).toExpression());
	}
	
	public final Expression minus(IntExpression l, Expression r) {
		return r.eq(undefined).thenElse(undefined, l.minus(r.sum()).toExpression());
	}
	
	public final IntExpression minus(IntExpression l, IntExpression r) {
		return l.minus(r);
	}
	
	public final Expression minus(Expression e) {
		// minus with 1 param = negation
		return e.eq(undefined).thenElse(undefined, e.sum().negate().toExpression());
	}
	
	public final IntExpression minus(IntExpression e) {
		// minus with 1 param = negation
		return e.negate();
	}
	
	public final Expression mod(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).or(r.sum().eq(IntConstant.constant(0)))
				.thenElse(undefined, l.sum().modulo(r.sum()).toExpression());
	}
	
	public final Expression mod(Expression l, IntExpression r) {
		return l.eq(undefined).or(r.eq(IntConstant.constant(0)))
				.thenElse(undefined, l.sum().modulo(r).toExpression());
	}
	
	public final Expression mod(IntExpression l, Expression r) {
		return r.eq(undefined).or(r.sum().eq(IntConstant.constant(0)))
				.thenElse(undefined, l.modulo(r.sum()).toExpression());
	}

	public final Expression mod(IntExpression l, IntExpression r) {
		return r.eq(IntConstant.constant(0))
				.thenElse(undefined, l.modulo(r).toExpression());
	}

	public final Expression multiply(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().multiply(r.sum()).toExpression());
	}
	
	public final Expression multiply(Expression l, IntExpression r) {
		return l.eq(undefined).thenElse(undefined, l.sum().multiply(r).toExpression());
	}
	
	public final Expression multiply(IntExpression l, Expression r) {
		return r.eq(undefined).thenElse(undefined, l.multiply(r.sum()).toExpression());
	}
	
	public final IntExpression multiply(IntExpression l, IntExpression r) {
		return l.multiply(r);
	}

	public final Expression plus(Expression l, Expression r) {
		return l.eq(undefined).or(r.eq(undefined)).thenElse(undefined, l.sum().plus(r.sum()).toExpression());
	}
	
	public final Expression plus(Expression l, IntExpression r) {
		return l.eq(undefined).thenElse(undefined, l.sum().plus(r).toExpression());
	}
	
	public final Expression plus(IntExpression l, Expression r) {
		return r.eq(undefined).thenElse(undefined, l.plus(r.sum()).toExpression());
	}
	
	public final IntExpression plus(IntExpression l, IntExpression r) {
		return l.plus(r);
	}

	public final Expression round(Expression e) {
		return e;
	}
	
	public final IntExpression round(IntExpression e) {
		return e;
	}

	public final Expression toString(Expression src) {
		StringType stringType = (StringType) typeFactory.stringType();
		return src.eq(undefined).thenElse(undefined, src.join(stringType.toStringMap()));
	}
	
	public final Expression toString(IntExpression src) {
		StringType stringType = (StringType) typeFactory.stringType();
		return src.toExpression().count().eq(IntConstant.constant(0)).thenElse(undefined, src.toExpression().join(stringType.toStringMap()));
	}
}
