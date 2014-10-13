package org.tzi.kodkod.helper;

import kodkod.ast.Expression;

/**
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ConstraintHelper {

	public Expression univ_r(Expression expression, int n) {
		if (n > 0) {
			return univ_r(expression, n - 1).join(Expression.UNIV);
		}
		return expression;
	}

	public Expression univ_l(Expression expression, int n) {
		if (n > 0) {
			return Expression.UNIV.join(univ_l(expression, n - 1));
		}
		return expression;
	}
}
