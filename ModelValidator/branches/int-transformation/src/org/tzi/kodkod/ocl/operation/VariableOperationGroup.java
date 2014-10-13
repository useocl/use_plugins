package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;

import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Contains transformation methods for variable operations.
 * 
 * @author Hendrik
 */
public class VariableOperationGroup extends OCLOperationGroup {

	private boolean returnsSet = false;

	public VariableOperationGroup(TypeFactory typeFactory) {
		super(typeFactory);
	}

	public final Expression navigation(Expression srcExpr, Expression assoc, Integer from_role, Integer to_role, Boolean assoc_class,
			Boolean object_type_end) {
		Expression res = assoc;

		if (from_role < to_role) {
			int i = 1;

			if (assoc_class)
				i = 0;

			for (; i < from_role; i++) {
				res = Expression.UNIV.join(res);
			}

			res = srcExpr.join(res);

			i += 1;
			for (; i < to_role; i++) {
				res = Expression.UNIV.join(res);
			}
			for (i = res.arity(); i > 1; i--) {
				res = res.join(Expression.UNIV);
			}
		} else {
			int i = res.arity();
			if (assoc_class)
				i -= 1;
			for (; i > from_role; i--) {
				res = res.join(Expression.UNIV);
			}

			res = res.join(srcExpr);
			i -= 1;
			for (; i > to_role; i--) {
				res = res.join(Expression.UNIV);
			}
			for (i = res.arity(); i > 1; i--) {
				res = Expression.UNIV.join(res);
			}
		}

		if (object_type_end) {
			returnsSet = false;
			return srcExpr.eq(undefined).thenElse(undefined, res);
		} else {
			returnsSet = true;
			return srcExpr.eq(undefined).thenElse(undefined_Set, res);
		}
	}

	public final Expression access(Expression srcExpr, Expression attribute, Boolean set_type) {
		if (set_type) {
			returnsSet = true;
			return srcExpr.eq(undefined).thenElse(undefined_Set, srcExpr.join(attribute));
		} else {
			returnsSet = false;
			return srcExpr.eq(undefined).thenElse(undefined, srcExpr.join(attribute));
		}
	}

	@Override
	public boolean returnsSet(String opName) {
		return returnsSet;
	}
}
