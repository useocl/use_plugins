package org.tzi.kodkod.ocl.operation;

import java.util.ArrayList;
import java.util.List;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Transformation methods for set operations.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class SetOperationGroup extends OCLOperationGroup {

	private List<String> operationsReturningSet;

	public SetOperationGroup(TypeFactory typeFactory) {
		super(typeFactory);

		operationsReturningSet = new ArrayList<String>();
		operationsReturningSet.add("asSet");
		operationsReturningSet.add("closure");
		operationsReturningSet.add("collect");
		operationsReturningSet.add("collectNested");
		operationsReturningSet.add("minus");
		operationsReturningSet.add("flatten");
		operationsReturningSet.add("excluding");
		operationsReturningSet.add("including");
		operationsReturningSet.add("intersection");
		operationsReturningSet.add("select");
		operationsReturningSet.add("selectByKind");
		operationsReturningSet.add("selectByType");
		operationsReturningSet.add("reject");
		operationsReturningSet.add("symmetricDifference");
		operationsReturningSet.add("union");
	}

	@Override
	public boolean isSetOperationGroup() {
		return true;
	}

	@Override
	public boolean returnsSet(String opName) {
		if (operationsReturningSet.contains(opName)) {
			return true;
		}
		return false;
	}

	// As the core translation does not support nested collections, the
	// result value of the any operation is object valued. In Kodkod the
	// result is a singleton relation.

	// The user has the responsibility to ensure that at most one element of the
	// source collection (src) fulfills the body formula (body). Otherwise, the
	// evaluation result may be corrupted => The translation returns the
	// undefined value.

	// OCL: srcExpr->any(var | bodyExpr) = srcExpr->select(var |
	// bodyExpr)->any(true)

	public final Expression any(Expression src, Formula body, Variable var) {
		final Expression select = select(src, body, var);
		return src.eq(undefined_Set).or(select.no()).or(select.count().gt(IntConstant.constant(1))).thenElse(undefined, select);
	}

	public final Expression any(Expression src, Expression body, Variable var) {
		return src.eq(undefined_Set).not().and(src.one()).and(body.eq(booleanTrue).forSome(var.oneOf(src))).thenElse(src, undefined);
	}

	// OCL: srcExpr->asSet()

	public final Expression asSet(Expression src) {
		return src;
	}

	public Expression closure(Expression src, Expression src_type, Expression body, Variable var) {
		final Variable y = Variable.unary("y");

		final Expression generalClosure = y.in(body)
				.comprehension(var.oneOf(src_type).and(y.oneOf(src_type)))
				.closure();
		// closure in OCL 2.4 is defined as the reflexive transitive closure,
		// therefore we add the src manually
		final Expression expression = src.join(generalClosure).union(src);

		return src.eq(undefined_Set).thenElse(undefined_Set, expression);
	}

	// OCL: srcExpr->collect(var | bodyExpr)

	public final Expression collect(Expression src, Expression body, Variable var) {
		final Variable res = Variable.unary("res");

		final Expression functionApplication = Expression.UNIV.join(Formula.TRUE.comprehension(var.oneOf(src).and(res.oneOf(body))));

		final Expression flattenedResult = functionApplication.difference(undefined_Set).count().lt(functionApplication.count())
				.thenElse(functionApplication.difference(undefined_Set).union(undefined), functionApplication);

		return src.eq(undefined_Set).thenElse(undefined_Set, flattenedResult);
	}

	public final Expression collect(Expression src, Formula body, Variable var) {
		final Expression select = select(src, body, var);
		return src.eq(undefined_Set).thenElse(
				undefined_Set,
				src.no().thenElse(Expression.NONE,
						src.eq(select).thenElse(booleanTrue, select.some().thenElse(booleanTrue.union(booleanFalse), booleanFalse))));
	}

	public final Expression collectNested(Expression src, Expression body, Variable var) {
		return collect(src, body, var);
	}

	public final Expression collectNested(Expression src, Formula body, Variable var) {
		return collect(src, body, var);
	}

	// OCL: srcExpr->count(argExpr)

	public Expression count(Expression src, Expression arg) {
		return arg.in(src).thenElse(IntConstant.constant(1), IntConstant.constant(0)).toExpression();
	}

	// OCL: setExpr_l - setExpr_r

	public Expression minus(Expression l, Expression r) {
		return l.eq(undefined_Set).or(r.eq(undefined_Set)).thenElse(undefined_Set, l.difference(r));
	}

	// OCL: expr_l = expr_r

	public final Formula equality(Expression l, Expression r) {
		return l.eq(r);
	}

	// OCL: srcExpr->excludes(argExpr)

	public Formula excludes(Expression src, Expression arg) {
		return src.eq(undefined_Set).not().and(arg.in(src).not());
	}

	// OCL: srcExpr->excludesAll(argExpr)

	public Formula excludesAll(Expression src, Expression arg) {
		return src.eq(undefined_Set).not().and(arg.eq(undefined_Set).not()).and(src.intersection(arg).no());
	}

	// OCL: srcExpr->excluding(argExpr)

	public Expression excluding(Expression src, Expression arg) {
		return src.eq(undefined_Set).thenElse(undefined_Set, src.difference(arg));
	}

	// OCL: srcExpr->exists(v1,...,vn | bodyExpr)

	public Formula exists(Expression src, Formula body, Variable... vars) {
		Decls d = vars[0].oneOf(src);
		for (int i = 1; i < vars.length; i++) {
			d = d.and(vars[i].oneOf(src));
		}
		return src.eq(undefined_Set).not().and(body.forSome(d));
	}

	public Formula exists(Expression src, Expression bodyExpression, Variable... vars) {
		Decls d = vars[0].oneOf(src);
		for (int i = 1; i < vars.length; i++) {
			d = d.and(vars[i].oneOf(src));
		}

		return src.eq(undefined_Set).not().and(src.some().and(bodyExpression.eq(booleanTrue).forSome(d)));
	}

	// OCL: srcExpr->flatten()

	public final Expression flatten(Expression src) {
		return src;
	}

	// OCL: srcExpr->forAll(v1,...,vn | bodyExpr)

	public Formula forAll(Expression src, Formula body, Variable... vars) {
		Decls d = vars[0].oneOf(src);
		for (int i = 1; i < vars.length; i++) {
			d = d.and(vars[i].oneOf(src));
		}
		return src.eq(undefined_Set).not().and(body.forAll(d));
	}

	public Formula forAll(Expression src, Expression body, Variable... vars) {
		Decls d = vars[0].oneOf(src);
		for (int i = 1; i < vars.length; i++) {
			d = d.and(vars[i].oneOf(src));
		}
		return src.eq(undefined_Set).not().and(src.no().or(body.eq(booleanTrue).forAll(d)));
	}

	// OCL: srcExpr->includes(argExpr)

	public Formula includes(Expression src, Expression arg) {
		return arg.in(src);
	}

	// OCL: srcExpr->includesAll(argExpr)

	public Formula includesAll(Expression src, Expression arg) {
		return src.eq(undefined_Set).not().and(arg.eq(undefined_Set).not()).and(arg.in(src));
	}

	// OCL: srcExpr->including(argExpr)

	public Expression including(Expression src, Expression arg) {
		return src.eq(undefined_Set).thenElse(undefined_Set, src.union(arg));
	}

	// OCL: expr_l <> expr_r

	public final Formula inequality(Expression l, Expression r) {
		return l.eq(r).not();
	}

	// OCL: srcExpr->intersection(argExpr)

	public Expression intersection(Expression src, Expression arg) {
		return src.eq(undefined_Set).or(arg.eq(undefined_Set)).thenElse(undefined_Set, src.intersection(arg));
	}

	// OCL: srcExpr->isDefined()
	public final Formula isDefined(Expression src) {
		return src.eq(undefined_Set).not();
	}

	public Formula isDefined(Formula src) {
		return Formula.TRUE;
	}

	// OCL: srcExpr->isEmpty()

	public final Formula isEmpty(Expression src, Boolean object_type_nav) {
		if (object_type_nav)
			return src.eq(undefined);
		else
			return src.no();
	}

	// OCL: srcExpr->isUnique(v | bodyExpr)

	public Formula isUnique(Expression src, Expression body1, Expression body2, Variable var1, Variable var2) {
		AnyOperationGroup anyOperation = new AnyOperationGroup(typeFactory, false);
		BooleanOperationGroup booleanOperation = new BooleanOperationGroup(typeFactory);

		return forAll(src, booleanOperation.implies(anyOperation.inequality(var1, var2), anyOperation.inequality(body1, body2)), var1, var2);
	}

	public Formula isUnique(Expression src, Formula body1, Formula body2, Variable var1, Variable var2) {
		AnyOperationGroup anyOperation = new AnyOperationGroup(typeFactory, false);
		BooleanOperationGroup booleanOperation = new BooleanOperationGroup(typeFactory);

		return forAll(src, booleanOperation.implies(anyOperation.inequality(var1, var2), anyOperation.inequality(body1, body2)), var1, var2);
	}

	// OCL: srcExpr->min()

	public final Expression min(Expression src) {
		final Variable i = Variable.unary("i");
		final Variable j = Variable.unary("j");

		return src.eq(undefined_Set).or(undefined.in(src)).or(src.no())
				.thenElse(undefined, i.sum().lte(j.sum()).forAll(j.oneOf(src)).comprehension(i.oneOf(src)));
	}

	// OCL: srcExpr->max()

	public final Expression max(Expression src) {
		final Variable i = Variable.unary("i");
		final Variable j = Variable.unary("j");

		return src.eq(undefined_Set).or(undefined.in(src)).or(src.no())
				.thenElse(undefined, i.sum().gte(j.sum()).forAll(j.oneOf(src)).comprehension(i.oneOf(src)));
	}

	// OCL: srcExpr->notEmpty()

	public final Formula notEmpty(Expression src, Boolean object_type_nav) {
		if (object_type_nav)
			return src.eq(undefined).not();
		else
			return src.eq(undefined_Set).not().and(src.some());
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

	// OCL: srcExpr->one(var | bodyExpr)

	public Formula one(Expression src, Formula body, Variable var) {
		final Expression select = body.comprehension(var.oneOf(src));
		return src.eq(undefined_Set).not().and(select.one());
	}

	public Formula one(Expression src, Expression body, Variable var) {
		return src.eq(undefined_Set).not().and(src.one()).and(body.eq(booleanTrue).forSome(var.oneOf(src)));
	}

	// OCL: srcExpr->reject(var | bodyExpr)

	public final Expression reject(Expression src, Formula body, Variable var) {
		return src.eq(undefined_Set).thenElse(undefined_Set, body.not().comprehension(var.oneOf(src)));
	}

	public final Expression reject(Expression src, Expression body, Variable var) {
		return src.eq(undefined_Set).thenElse(undefined_Set, body.eq(booleanTrue).not().comprehension(var.oneOf(src)));
	}

	// OCL: srcExpr->select(var | bodyExpr)

	public final Expression select(Expression src, Formula body, Variable var) {
		//return src.eq(undefined_Set).thenElse(undefined_Set, body.comprehension(var.oneOf(src)));
		return body.comprehension(var.oneOf(src));
	}

	public final Expression select(Expression src, Expression body, Variable var) {
		//return src.eq(undefined_Set).thenElse(undefined_Set, body.eq(booleanTrue).comprehension(var.oneOf(src)));
		return body.eq(booleanTrue).comprehension(var.oneOf(src));
	}

	public final Expression selectByKind(Expression src, Expression type){
		return src.intersection(type);
	}
	
	public final Expression selectByType(Expression src, Expression type){
		// type must be the inheritance type
		return src.intersection(type);
	}
	
	// OCL: srcExpr->size()

	public final Expression size(Expression src, Boolean object_type_nav) {
		if (object_type_nav)
			return src.eq(undefined).thenElse(IntConstant.constant(0).toExpression(), IntConstant.constant(1).toExpression());
		else
			return src.eq(undefined_Set).thenElse(undefined_Set, src.count().toExpression());
	}

	// OCL: srcExpr->sum()

	public final Expression sum(Expression src) {
		return src.eq(undefined_Set).or(undefined.in(src)).thenElse(undefined, src.sum().toExpression());
	}

	// OCL: srcExpr->symmetricDifference(argExpr)

	public Expression symmetricDifference(Expression src, Expression arg) {
		return src.eq(undefined_Set).or(arg.eq(undefined_Set)).thenElse(undefined_Set, src.difference(arg).union(arg.difference(src)));
	}

	// OCL: srcExpr->union(argExpr)

	public Expression union(Expression src, Expression arg) {
		return src.eq(undefined_Set).or(arg.eq(undefined_Set)).thenElse(undefined_Set, src.union(arg));
	}

	public final Formula isUndefined(Expression src) {
		return src.eq(undefined_Set);
	}

	public Formula isUndefined(Formula src) {
		return Formula.FALSE;
	}
}
