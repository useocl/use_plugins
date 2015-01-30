package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kodkod.ast.Expression;
import kodkod.ast.IntConstant;
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodModelValidatorConfiguration;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.EnumType;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.kodkod.model.type.TypeLiterals;
import org.tzi.kodkod.ocl.OCLMethodInvoker;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.kodkod.transform.TypeConverter;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor.OclTransformationContext.CollectionUndefinedBehavior;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor.OclTransformationContext.UndefinedBehavior;
import org.tzi.use.uml.mm.MClass;
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
import org.tzi.use.uml.ocl.expr.ExpRange;
import org.tzi.use.uml.ocl.expr.ExpSequenceLiteral;
import org.tzi.use.uml.ocl.expr.ExpSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;
import org.tzi.use.util.StringUtil;

/**
 * Default visitor implementation for the transform use expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class DefaultExpressionVisitor extends SimpleExpressionVisitor {

	private static final Logger LOG = Logger.getLogger(DefaultExpressionVisitor.class);
	
	protected final IModel model;
	
	protected final Map<String, Node> variables;
	protected final List<String> collectionVariables;
	protected final Map<String, IClass> variableClasses;
	protected final Map<String, Variable> replaceVariables;
	
	protected final Relation undefined;
	protected final Relation undefined_Set;

	protected final Stack<OclTransformationContext> stack;
	
	public static class OclTransformationContext {
		public enum UndefinedBehavior {
			IS_UNDEFINED,
			CAN_BE_UNDEFINED,
			CANNOT_BE_UNDEFINED
		}
		public enum CollectionUndefinedBehavior {
			CONTAINS_UNDEFINED,
			CAN_CONTAIN_UNDEFINED,
			CANNOT_CONTAIN_UNDEFINED
		}
		
		//TODO make Node instead of Object
		public final Object object;
		public boolean set = false;
		public boolean object_type_nav = false;
		
		public final UndefinedBehavior undefined;
		public final CollectionUndefinedBehavior containsUndefined;
		
		public OclTransformationContext(Object object, UndefinedBehavior undefined, CollectionUndefinedBehavior containsUndefined) {
			this.object = object;
			this.undefined = undefined;
			this.containsUndefined = containsUndefined;
		}

		public OclTransformationContext(Object object) {
			this(object, UndefinedBehavior.CAN_BE_UNDEFINED, CollectionUndefinedBehavior.CAN_CONTAIN_UNDEFINED);
		}
	}

	public DefaultExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, Variable> replaceVariables, List<String> collectionVariables, Stack<OclTransformationContext> contextStack) {
		this.model = model;
		this.variables = variables;
		this.variableClasses = variableClasses;
		this.replaceVariables = replaceVariables;
		this.collectionVariables = collectionVariables;

		undefined = model.typeFactory().undefinedType().relation();
		undefined_Set = model.typeFactory().undefinedSetType().relation();
		
		stack = contextStack;
		
		//TODO should not be required
//		set = false;
//		object_type_nav = false;
	}

	/**
	 * Returns the transformation result.
	 * 
	 * @return
	 */
	public Object getObject() {
		return stack.peek().object;
	}

	/**
	 * Returns true if the result object represents a set.
	 * 
	 * @return
	 */
	public boolean isSet() {
		return stack.peek().set;
	}
	
	/**
	 * Returns <code>true</code>, if the result object is a navigation to an object end.
	 * 
	 * @return
	 */
	public boolean isObject_type_nav() {
		return stack.peek().object_type_nav;
	}

	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		super.visitAllInstances(exp);
		IClass clazz = model.getClass(exp.getSourceType().name());

		List<Object> arguments = new ArrayList<Object>();
		if (!clazz.existsInheritance()) {
			arguments.add(clazz.relation());
		} else {
			arguments.add(clazz.inheritanceRelation());
		}
		invokeMethod("allInstances", arguments, false, false);
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
		OclTransformationContext ctx = processVariableOperation(exp);
		stack.push(ctx);
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		super.visitBagLiteral(exp);
		LOG.warn(LogMessages.unsupportedCollectionWarning("bags"));
		visitCollectionLiteral(exp, "bagLiteral");
	}

	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
		super.visitConstBoolean(exp);
		Map<String, Expression> typeLiterals = model.typeFactory().booleanType().typeLiterals();
		OclTransformationContext ctx;
		if (exp.value()) {
			ctx = new OclTransformationContext(
					typeLiterals.get(TypeConstants.BOOLEAN_TRUE),
					UndefinedBehavior.CANNOT_BE_UNDEFINED,
					CollectionUndefinedBehavior.CANNOT_CONTAIN_UNDEFINED);
		} else {
			ctx = new OclTransformationContext(
					typeLiterals.get(TypeConstants.BOOLEAN_FALSE),
					UndefinedBehavior.CANNOT_BE_UNDEFINED,
					CollectionUndefinedBehavior.CANNOT_CONTAIN_UNDEFINED);
		}
		stack.push(ctx);
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
		super.visitConstEnum(exp);
		EnumType enumType = model.getEnumType(exp.type().shortName());
		
		if (enumType == null) {
			throw new TransformationException("Cannot find enum " + StringUtil.inQuotes(exp.type().shortName()) + ".");
		}
		
		enumType.addTypeLiteral(exp.value());
		OclTransformationContext ctx = new OclTransformationContext(
				enumType.getTypeLiteral(exp.value()),
				UndefinedBehavior.CANNOT_BE_UNDEFINED,
				CollectionUndefinedBehavior.CANNOT_CONTAIN_UNDEFINED);
		stack.push(ctx);
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
		super.visitConstInteger(exp);
		addIntegerRelation(exp.value());
		
		OclTransformationContext ctx = new OclTransformationContext(
				IntConstant.constant(exp.value()),
				UndefinedBehavior.CANNOT_BE_UNDEFINED,
				CollectionUndefinedBehavior.CANNOT_CONTAIN_UNDEFINED);
		stack.push(ctx);
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
		super.visitConstReal(exp);
		Double value = new Double(exp.value());
		LOG.warn(LogMessages.constRealWarning(value));
		addIntegerRelation(value.intValue());

		OclTransformationContext ctx = new OclTransformationContext(
				IntConstant.constant(value.intValue()),
				UndefinedBehavior.CANNOT_BE_UNDEFINED,
				CollectionUndefinedBehavior.CANNOT_CONTAIN_UNDEFINED);
		stack.push(ctx);
	}

	@Override
	public void visitConstString(ExpConstString exp) {
		super.visitConstString(exp);
		TypeLiterals stringType = model.typeFactory().stringType();
		stringType.addTypeLiteral(exp.value());
		
		OclTransformationContext ctx = new OclTransformationContext(
				stringType.getTypeLiteral(exp.value()),
				UndefinedBehavior.CANNOT_BE_UNDEFINED,
				CollectionUndefinedBehavior.CANNOT_CONTAIN_UNDEFINED);
		stack.push(ctx);
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		super.visitObjOp(exp);
		//TODO
		OperationExpressionVisitor visitor = new OperationExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables, stack);
		exp.processWithVisitor(visitor);
		
		OclTransformationContext ctx = new OclTransformationContext(visitor.getObject());
		ctx.set = visitor.isSet();
		ctx.object_type_nav = visitor.isObject_type_nav();
		stack.push(ctx);
	}

	@Override
	public void visitLet(ExpLet exp) {
		super.visitLet(exp);
		OclTransformationContext varCtx = processSubExpression(exp.getVarExpression());
		Node varExpression = (Node) varCtx.object;

		variables.put(exp.getVarname(), varExpression);
		if(exp.getVarType().isTypeOfClass()){
			variableClasses.put(exp.getVarname(), model.getClass(((MClass)exp.getVarType()).name()));
		}
		if (varCtx.set) {
			collectionVariables.add(exp.getVarname());
		}

		OclTransformationContext inCtx = processSubExpression(exp.getInExpression());

		variables.remove(exp.getVarname());
		if(exp.getVarType().isTypeOfClass()){
			variableClasses.remove(exp.getVarname());
		}
		if (varCtx.set) {
			collectionVariables.remove(exp.getVarname());
		}
		
		OclTransformationContext ctx = new OclTransformationContext(inCtx.object, inCtx.undefined, inCtx.containsUndefined);
		ctx.set = inCtx.set;
		ctx.object_type_nav = inCtx.object_type_nav;
		stack.push(ctx);
	}

	@Override
	public void visitIf(ExpIf exp) {
		super.visitIf(exp);

		List<Object> arguments = new ArrayList<Object>();

		OclTransformationContext condCtx = processSubExpression(exp.getCondition());
		arguments.add(condCtx.object);

		OclTransformationContext thenCtx = processSubExpression(exp.getThenExpression());
		arguments.add(thenCtx.object);

		OclTransformationContext elseCtx = processSubExpression(exp.getElseExpression());
		arguments.add(elseCtx.object);

		invokeMethod("if_then_else", arguments, false, false);
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
		OclTransformationContext ctx = processVariableOperation(exp);
		stack.push(ctx);
	}

	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {
		super.visitObjAsSet(exp);
		OclTransformationContext ctx = processVariableOperation(exp.getObjectExpression());
		ctx.set = true;
		stack.push(ctx);
	}

	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
		super.visitOrderedSetLiteral(exp);
		LOG.warn(LogMessages.unsupportedCollectionWarning("orderedSets"));
		visitCollectionLiteral(exp, "orderedSetLiteral");
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		super.visitQuery(exp);
		
		//TODO
		QueryExpressionVisitor visitor = new QueryExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables, stack);
		exp.processWithVisitor(visitor);
		
		OclTransformationContext ctx = new OclTransformationContext(visitor.getObject());
		ctx.set = visitor.isSet();
		ctx.object_type_nav = visitor.isObject_type_nav();
		stack.push(ctx);
	}

	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {
		super.visitSequenceLiteral(exp);
		LOG.warn(LogMessages.unsupportedCollectionWarning("sequences"));
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
		
		//TODO
		StandardOperationVisitor visitor = new StandardOperationVisitor(model, variables, variableClasses, replaceVariables, collectionVariables, stack);
		exp.processWithVisitor(visitor);
		
		OclTransformationContext ctx = new OclTransformationContext(visitor.getObject());
		ctx.set = visitor.isSet();
		ctx.object_type_nav = visitor.isObject_type_nav();
		stack.push(ctx);
	}

	@Override
	public void visitUndefined(ExpUndefined exp) {
		super.visitUndefined(exp);
		OclTransformationContext ctx;
		if (exp.type().isKindOfCollection(VoidHandling.EXCLUDE_VOID)) {
			ctx = new OclTransformationContext(undefined_Set,
					UndefinedBehavior.IS_UNDEFINED,
					CollectionUndefinedBehavior.CAN_CONTAIN_UNDEFINED);
		} else {
			ctx = new OclTransformationContext(undefined,
					UndefinedBehavior.IS_UNDEFINED,
					CollectionUndefinedBehavior.CAN_CONTAIN_UNDEFINED);
		}
		stack.push(ctx);
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		super.visitVariable(exp);
		OclTransformationContext ctx = processVariableOperation(exp);
		stack.push(ctx);
	}

	@Override
	public void visitRange(ExpRange exp) {
		super.visitRange(exp);
		
		org.tzi.use.uml.ocl.expr.Expression[] expToVisit = new org.tzi.use.uml.ocl.expr.Expression[]{
				exp.getStart(),
				exp.getEnd()
		};
		
		List<Object> args = new ArrayList<Object>(2);
		for(org.tzi.use.uml.ocl.expr.Expression e : expToVisit){
			OclTransformationContext ctx = processSubExpression(e);
			args.add(ctx.object);
		}
		
		invokeMethod("mkSetRange", args, false, false);
	}
	
	/**
	 * Adds a constant int value to the integer relation bounds. This is
	 * required if the integer value is eventually transformed into a relation.
	 * 
	 * @param value
	 */
	protected void addIntegerRelation(int value) {
		TypeLiterals integerType = model.typeFactory().integerType();
		
		int bitwidth = KodkodModelValidatorConfiguration.INSTANCE.bitwidth();
		int requiredBitwidth = ((int) Math.ceil(Math.log(Math.abs(value))/Math.log(2))) +1;
		if(requiredBitwidth > bitwidth){
			LOG.error("Model contains number " + StringUtil.inQuotes(value) + " which is too big for configured bitwidth. Required bitwidth: " + requiredBitwidth + " or greater.");
		}
		
		integerType.addTypeLiteral("" + value);
	}

	protected OclTransformationContext processSubExpression(org.tzi.use.uml.ocl.expr.Expression expr) {
		//TODO replace new Visitor with usage of self
		int size = stack.size();
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables, stack);
		expr.processWithVisitor(visitor);
		if(stack.size() != size+1){
			throw new TransformationException("A subexpression killed the transformation stack.\n" + expr.toString());
		}
		return stack.pop();
	}
	
	/**
	 * Visit every kind of collection literal.
	 * 
	 * @param exp
	 * @param literal
	 */
	private void visitCollectionLiteral(ExpCollectionLiteral exp, String literal) {
		List<Object> arguments = new ArrayList<Object>();

		for (org.tzi.use.uml.ocl.expr.Expression expression : exp.getElemExpr()) {
			OclTransformationContext ctx = processSubExpression(expression);
			arguments.add(ctx.object);
		}

		invokeMethod(literal, arguments, false, false);
	}

	/**
	 * Visit every kind of variable operation.
	 */
	private OclTransformationContext processVariableOperation(org.tzi.use.uml.ocl.expr.Expression exp) {
		VariableOperationVisitor visitor = new VariableOperationVisitor(model, variables, variableClasses, replaceVariables, collectionVariables, stack);
		exp.processWithVisitor(visitor);
		
		OclTransformationContext ctx = new OclTransformationContext(visitor.getObject());
		ctx.set = visitor.isSet();
		ctx.object_type_nav = visitor.isObject_type_nav();
		return ctx;
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

		OclTransformationContext res = processSubExpression(sourceExpression);
		arguments.add(res.object);

		TypeConverter typeConverter = new TypeConverter(model);
		Expression typeExpression = typeConverter.convertToExpression(targetType);
		if (typeExpression != null) {
			arguments.add(typeExpression);
		} else {
			throw new TransformationException("No support for " + targetType + " as target type of " + opName + ".");
		}

		invokeMethod(opName, arguments, res.set, res.object_type_nav);
	}

	/**
	 * Invokes the method to transform the operation with the given name.
	 * 
	 * @param opName
	 * @param arguments
	 * @param setOperation
	 */
	protected void invokeMethod(String opName, List<Object> arguments, boolean setOperation, boolean objectTypeNav) {
		OCLMethodInvoker invoker = new OCLMethodInvoker();
		invoker.invoke(opName, arguments, setOperation, objectTypeNav);
		
		OclTransformationContext ctx = new OclTransformationContext(invoker.getObject());
		ctx.set = invoker.isSet();
		ctx.object_type_nav = objectTypeNav; //FIXME might not be correct
		stack.push(ctx);
	}
}
