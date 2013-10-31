package org.tzi.kodkod.helper;

import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Formula;

import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.type.TypeFactory;

/**
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ExpressionHelper {

	/**
	 * Converts a kodkod expression in a kodkod formula.
	 * 
	 * @param e
	 * @param typeFactory
	 * @return
	 */
	public static final Formula boolean_expr2formula(Expression e, TypeFactory typeFactory) {
		Map<String, Expression> typeLiterals = typeFactory.booleanType().typeLiterals();
		return e.eq(typeLiterals.get(TypeConstants.BOOLEAN_TRUE));
	}

	/**
	 * Converts a kodkod formula in a kodkod expression.
	 * 
	 * @param f
	 * @param typeFactory
	 * @return
	 */
	public static final Expression boolean_formula2expr(Formula f, TypeFactory typeFactory) {
		Map<String, Expression> typeLiterals = typeFactory.booleanType().typeLiterals();
		return f.thenElse(typeLiterals.get(TypeConstants.BOOLEAN_TRUE), typeLiterals.get(TypeConstants.BOOLEAN_FALSE));
	}
}
