package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.EnumType;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.type.TypeLiterals;
import org.tzi.kodkod.ocl.OCLMethodInvoker;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.kodkod.transform.TypeConverter;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpBagLiteral;
import org.tzi.use.uml.ocl.expr.ExpCollectionLiteral;
import org.tzi.use.uml.ocl.expr.ExpConstBoolean;
import org.tzi.use.uml.ocl.expr.ExpConstEnum;
import org.tzi.use.uml.ocl.expr.ExpConstInteger;
import org.tzi.use.uml.ocl.expr.ExpConstReal;
import org.tzi.use.uml.ocl.expr.ExpConstString;
import org.tzi.use.uml.ocl.expr.ExpIf;
import org.tzi.use.uml.ocl.expr.ExpIsKindOf;
import org.tzi.use.uml.ocl.expr.ExpIsTypeOf;
import org.tzi.use.uml.ocl.expr.ExpIterate;
import org.tzi.use.uml.ocl.expr.ExpLet;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpObjAsSet;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpOrderedSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpSequenceLiteral;
import org.tzi.use.uml.ocl.expr.ExpSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.type.Type;

/**
 * Default visitor implementation for the transform use expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class DefaultExpressionVisitor extends SimpleExpressionVisitor {

	protected IModel model;
	protected Relation undefined;
	protected Relation undefined_Set;
	protected Map<String, Node> variables;
	protected List<String> collectionVariables;
	protected Map<String, IClass> variableClasses;
	protected Map<String, List<Variable>> replaceVariables;

	protected Object object;
	protected boolean set;
	protected boolean object_type_nav;

	public DefaultExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, List<Variable>> replaceVariables, List<String> collectionVariables) {
		this.model = model;
		this.variables = variables;
		this.variableClasses = variableClasses;
		this.replaceVariables = replaceVariables;
		this.collectionVariables = collectionVariables;

		undefined = model.typeFactory().undefinedType().relation();
		undefined_Set = model.typeFactory().undefinedSetType().relation();

		set = false;
		object_type_nav = false;
	}

	/**
	 * Returns the transformation result.
	 * 
	 * @return
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Returns true if the result object represents a set.
	 * 
	 * @return
	 */
	public boolean isSet() {
		return set;
	}

	/**
	 * Returns if the result object is a navigation to an object end.
	 * 
	 * @return
	 */
	public boolean isObject_type_nav() {
		return object_type_nav;
	}

	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		super.visitAllInstances(exp);
		IClass clazz = model.getClass(exp.getSourceType().cls().name());

		List<Object> arguments = new ArrayList<Object>();
		if (!clazz.existsInheritance()) {
			arguments.add(clazz.relation());
		} else {
			arguments.add(clazz.inheritanceRelation());
		}
		invokeMethod("allInstances", arguments, false);
	}

	@Override
	public void visitAsType(ExpAsType exp) {
		super.visitAsType(exp);
		try {
			visitTypeOperation("oclAsType", exp.getSourceExpr(), exp.getTargetType());
		} catch (NoSuchMethodError e) {
			throw new TransformationException(LogMessages.noSuchMethodError, e);
		}
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		super.visitAttrOp(exp);
		visitVariableOperation(exp);
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		super.visitBagLiteral(exp);
		visitCollectionLiteral(exp, "bagLiteral");
	}

	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
		super.visitConstBoolean(exp);
		Map<String, Expression> typeLiterals = model.typeFactory().booleanType().typeLiterals();
		if (exp.value()) {
			object = typeLiterals.get(TypeConstants.BOOLEAN_TRUE);
		} else {
			object = typeLiterals.get(TypeConstants.BOOLEAN_FALSE);
		}
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
		super.visitConstEnum(exp);
		EnumType enumType = model.getEnumType(exp.type().shortName());
		if (enumType != null) {
			enumType.addTypeLiteral(exp.value());
			object = enumType.getTypeLiteral(exp.value());
		} else {
			throw new TransformationException("Cannot find enum " + exp.type().shortName() + ".");
		}
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
		super.visitConstInteger(exp);
		visitConstInteger(exp.value());
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
		super.visitConstReal(exp);

		Double value = new Double(exp.value());
		LOG.warn(LogMessages.constRealWarning(value));

		visitConstInteger(value.intValue());
	}

	@Override
	public void visitConstString(ExpConstString exp) {
		LOG.debug("ExpConstString");

		TypeLiterals stringType = model.typeFactory().stringType();
		stringType.addTypeLiteral(exp.value());
		object = stringType.getTypeLiteral(exp.value());
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		super.visitObjOp(exp);
		DefaultExpressionVisitor visitor = new OperationExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.processWithVisitor(visitor);
		object = visitor.getObject();
		set = visitor.isSet();
		object_type_nav = visitor.isObject_type_nav();
	}

	@Override
	public void visitLet(ExpLet exp) {
		super.visitLet(exp);
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getVarExpression().processWithVisitor(visitor);
		Object varExpression = visitor.getObject();

		if (varExpression instanceof Node) {
			variables.put(exp.getVarname(), (Node) varExpression);
			if (visitor.isSet()) {
				collectionVariables.add(exp.getVarname());
			}

			visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
			exp.getInExpression().processWithVisitor(visitor);
			object = visitor.getObject();
			set = visitor.isSet();
			object_type_nav = visitor.isObject_type_nav();

			variables.remove(exp.getVarname());
		} else {
			LOG.warn(LogMessages.letNotReachableWarning);
		}
	}

	@Override
	public void visitIf(ExpIf exp) {
		super.visitIf(exp);

		List<Object> arguments = new ArrayList<Object>();

		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getCondition().processWithVisitor(visitor);
		arguments.add(visitor.getObject());

		visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getThenExpression().processWithVisitor(visitor);
		arguments.add(visitor.getObject());

		visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getElseExpression().processWithVisitor(visitor);
		arguments.add(visitor.getObject());

		invokeMethod("if_then_else", arguments, false);
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		super.visitIsKindOf(exp);
		visitTypeOperation("oclIsKindOf", exp.getSourceExpr(), exp.getTargetType());
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		super.visitIsTypeOf(exp);
		visitTypeOperation("oclIsTypeOf", exp.getSourceExpr(), exp.getTargetType());
	}

	@Override
	public void visitIterate(ExpIterate exp) {
		super.visitIterate(exp);
		throw new TransformationException("Iterate not supported");
	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		super.visitNavigation(exp);
		visitVariableOperation(exp);
	}

	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {
		super.visitObjAsSet(exp);
		visitVariableOperation(exp.getObjectExpression());
		set = true;
	}

	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
		super.visitOrderedSetLiteral(exp);
		visitCollectionLiteral(exp, "orderedSetLiteral");
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		super.visitQuery(exp);
		QueryExpressionVisitor visitor = new QueryExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.processWithVisitor(visitor);
		object = visitor.getObject();
		set = visitor.isSet();
		object_type_nav = visitor.isObject_type_nav();
	}

	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {
		super.visitSequenceLiteral(exp);
		visitCollectionLiteral(exp, "sequenceLiteral");
	}

	@Override
	public void visitSetLiteral(ExpSetLiteral exp) {
		super.visitSetLiteral(exp);
		visitCollectionLiteral(exp, "setLiteral");
	}

	@Override
	public void visitStdOp(ExpStdOp exp) {
		super.visitStdOp(exp);
		StandardOperationVisitor visitor = new StandardOperationVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.processWithVisitor(visitor);
		object = visitor.getObject();
		set = visitor.isSet();
		object_type_nav = visitor.isObject_type_nav();
	}

	@Override
	public void visitUndefined(ExpUndefined exp) {
		super.visitUndefined(exp);
		if (exp.type().isCollection(true)) {
			object = undefined_Set;
		} else {
			object = undefined;
		}
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		super.visitVariable(exp);
		visitVariableOperation(exp);
	}

	/**
	 * Handle a constant int value.
	 * 
	 * @param value
	 */
	protected void visitConstInteger(int value) {
		TypeLiterals integerType = model.typeFactory().integerType();
		integerType.addTypeLiteral("" + value);
		object = integerType.getTypeLiteral("" + value);
	}

	/**
	 * Visit every kind of collection literal.
	 * 
	 * @param exp
	 * @param literal
	 */
	private void visitCollectionLiteral(ExpCollectionLiteral exp, String literal) {
		List<Object> arguments = new ArrayList<Object>();

		DefaultExpressionVisitor visitor;
		for (org.tzi.use.uml.ocl.expr.Expression expression : exp.getElemExpr()) {
			visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
			expression.processWithVisitor(visitor);
			arguments.add(visitor.getObject());
		}

		invokeMethod(literal, arguments, false);
	}

	/**
	 * Visit every kind of variable operation.
	 * 
	 * @param exp
	 */
	private void visitVariableOperation(org.tzi.use.uml.ocl.expr.Expression exp) {
		VariableOperationVisitor visitor = new VariableOperationVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.processWithVisitor(visitor);
		object = visitor.getObject();
		set = visitor.isSet();
		object_type_nav = visitor.isObject_type_nav();
	}

	/**
	 * Visit every kind of type operation.
	 * 
	 * @param opName
	 * @param sourceExpression
	 * @param targetType
	 */
	private void visitTypeOperation(String opName, org.tzi.use.uml.ocl.expr.Expression sourceExpression, Type targetType) {
		List<Object> arguments = new ArrayList<Object>();

		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		sourceExpression.processWithVisitor(visitor);
		arguments.add(visitor.getObject());
		set = visitor.isSet();
		object_type_nav = visitor.isObject_type_nav();

		TypeConverter typeConverter = new TypeConverter(model);
		Expression typeExpression = typeConverter.convertToExpression(targetType);
		if (typeExpression != null) {
			arguments.add(typeExpression);
		} else {
			throw new TransformationException("No support for " + targetType + " as target type of " + opName + ".");
		}

		invokeMethod(opName, arguments, set);
	}

	/**
	 * Invokes the method to transform the operation with the given name.
	 * 
	 * @param opName
	 * @param arguments
	 * @param setOperation
	 */
	protected void invokeMethod(String opName, List<Object> arguments, boolean setOperation) {
		OCLMethodInvoker invoker = new OCLMethodInvoker();
		invoker.invoke(opName, arguments, setOperation, object_type_nav);
		object = invoker.getObject();
		set = invoker.isSet();
	}
}
