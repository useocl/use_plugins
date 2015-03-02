package org.tzi.kodkod.helper;

import kodkod.ast.Expression;

/**
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ConstraintHelper {

	public static Expression univRightN(Expression expression, int n) {
		for(int i = 0; i < n; i++){
			expression = expression.join(Expression.UNIV);
		}
		return expression;
	}

	public static Expression univLeftN(Expression expression, int n) {
		for(int i = 0; i < n; i++){
			expression = Expression.UNIV.join(expression);
		}
		return expression;
	}
}
