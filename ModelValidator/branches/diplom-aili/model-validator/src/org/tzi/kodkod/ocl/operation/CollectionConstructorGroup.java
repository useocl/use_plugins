package org.tzi.kodkod.ocl.operation;

import kodkod.ast.Expression;
import kodkod.ast.Variable;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLOperationGroup;

/**
 * Transformation methods for collection constructors.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class CollectionConstructorGroup extends OCLOperationGroup {

	protected static final Logger LOG = Logger.getLogger(CollectionConstructorGroup.class);

	public CollectionConstructorGroup(TypeFactory typeFactory) {
		super(typeFactory);
	}

	@Override
	public boolean returnsSet(String opName) {
		return true;
	}

	// OCL: x..y

	public Expression mkSetRange(Expression from, Expression to) {
		final Variable i = Variable.unary("i");

		return from.eq(undefined).or(to.eq(undefined))
				.thenElse(undefined_Set, i.sum().gte(from.sum()).and(i.sum().lte(to.sum())).comprehension(i.oneOf(Expression.INTS)));
	}

	// OCL: Set{e_1,e_2,...}, where e_i may be a non-collection value or a range
	// expression

	public Expression setLiteral(Expression... elem) {
		Expression set = Expression.NONE;
		if (elem.length == 0) {
			return set;
		}
		for (Expression e : elem) {
			set = set.union(e);
		}

		return undefined_Set.in(set).thenElse(undefined_Set, set);
	}

	public Expression setLiteral() {
		return Expression.NONE;
	}

	public Expression bagLiteral(Expression... elem) {
		LOG.warn(LogMessages.unsupportedCollectionWarning("bags"));
		return setLiteral(elem);
	}

	public Expression bagLiteral() {
		LOG.warn(LogMessages.unsupportedCollectionWarning("bags"));
		return setLiteral();
	}

	public Expression orderedSetLiteral(Expression... elem) {
		LOG.warn(LogMessages.unsupportedCollectionWarning("orderedSets"));
		return setLiteral(elem);
	}

	public Expression orderedSetLiteral() {
		LOG.warn(LogMessages.unsupportedCollectionWarning("orderedSets"));
		return setLiteral();
	}

	public Expression sequenceLiteral(Expression... elem) {
		LOG.warn(LogMessages.unsupportedCollectionWarning("sequences"));
		return setLiteral(elem);
	}

	public Expression sequenceLiteral() {
		LOG.warn(LogMessages.unsupportedCollectionWarning("sequences"));
		return setLiteral();
	}
}
