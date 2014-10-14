package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.tzi.kodkod.helper.ExpressionHelper;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.kodkod.transform.OperationStack;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.ObjectType;

/**
 * Extension of DefaultExpressionVisitor to visit the operations of an
 * expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class OperationExpressionVisitor extends DefaultExpressionVisitor {

	private Map<String, Node> opVariables;
	private List<String> opCollectionVariables;
	private Map<String, IClass> opVariableClasses;
	private Map<String, Variable> opReplaceVariables;

	public OperationExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, Variable> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);

		opVariables = new HashMap<String, Node>(variables);
		opCollectionVariables = new ArrayList<String>(collectionVariables);
		opVariableClasses = new HashMap<String, IClass>(variableClasses);
		opReplaceVariables = new HashMap<String, Variable>(replaceVariables);

		opVariables.remove("self");
		opCollectionVariables.remove("self");
		opVariableClasses.remove("self");
		opReplaceVariables.remove("self");
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		MOperation operation = exp.getOperation();

		org.tzi.use.uml.ocl.expr.Expression[] arguments = exp.getArguments();
		visitOperationVariable(arguments[0]);
		
		if (!OperationStack.INSTANCE.contains(exp.getOperation())) {
			OperationStack.INSTANCE.push(operation);
			
			visitParams(operation, arguments);

			Node self = opVariables.get("self");
			Expression expression = getAsExpression(self);

			Map<MClass, MOperation> overiddenOperations = getOverriddenOperations(operation.cls(), operation.name());

			DefaultExpressionVisitor mainVisitor = visitOperation(operation);
			if (overiddenOperations.isEmpty()) {
				object = mainVisitor.getObject();
			} else {
				Iterator<MClass> iterator = overiddenOperations.keySet().iterator();
				object = handleOveriddenOperation(iterator, overiddenOperations, expression, getAsExpression(mainVisitor.getObject()));
			}

			
			set = exp.getOperation().resultType().isCollection(true);
			object_type_nav = mainVisitor.isObject_type_nav();

			object = expression.in(undefined).thenElse(undefined, getAsExpression(object));

			opVariables.remove("self");
			opVariableClasses.remove("self");

			//variables.putAll(opVariables);
			//variableClasses.putAll(opVariableClasses);

			OperationStack.INSTANCE.pop();
		} else {
			throw new TransformationException("Operation " + operation.name() + " is recursive!");
		}

	}

	private Expression handleOveriddenOperation(Iterator<MClass> iterator, Map<MClass, MOperation> overiddenOperations, Expression selfExpression,
			Expression object) {
		if (!iterator.hasNext()) {
			return object;
		} else {
			MClass cls = iterator.next();
			
			IClass baseVariableClass = opVariableClasses.remove("self");
			opVariableClasses.put("self", model.getClass(cls.name()));
			
			Formula inFormula = selfExpression.in(model.getClass(cls.name()).relation());
			Expression expression = getAsExpression(visitOperation(overiddenOperations.get(cls)).getObject());
			
			opVariableClasses.put("self", baseVariableClass);
			
			return inFormula.thenElse(expression, handleOveriddenOperation(iterator, overiddenOperations, selfExpression, object));
		}
	}

	private Expression getAsExpression(Object object) {
		if (object instanceof Expression) {
			return (Expression) object;
		}
		return ExpressionHelper.boolean_formula2expr((Formula) object, model.typeFactory());
	}

	private Map<MClass, MOperation> getOverriddenOperations(MClass cls, String operationName) {
		Map<MClass, MOperation> operations = new HashMap<MClass, MOperation>();

		for (MClass child : cls.children()) {
			MOperation operation = child.operation(operationName, false);
			if (operation != null) {
				operations.put(child, operation);
			}
			operations.putAll(getOverriddenOperations(child, operationName));
		}

		return operations;
	}

	/**
	 * Visit the operation.
	 * 
	 * @param operation
	 * @return
	 */

	private DefaultExpressionVisitor visitOperation(MOperation operation) {
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, opVariables, opVariableClasses, opReplaceVariables,
				opCollectionVariables);
		operation.expression().processWithVisitor(visitor);

		return visitor;
	}

	/**
	 * Visit the parameters of an operation.
	 * 
	 * @param operation
	 * @param arguments
	 */

	private void visitParams(MOperation operation, org.tzi.use.uml.ocl.expr.Expression[] arguments) {
		DefaultExpressionVisitor visitor;
		VarDeclList params = operation.paramList();
		
		if (params.size() > 0) {
			VarDecl currentParam;
			for (int i = 0; i < params.size(); i++) {
				visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
				arguments[i + 1].processWithVisitor(visitor);

				currentParam = params.varDecl(i);
				
				opVariables.put(currentParam.name(), (Node) visitor.getObject());
				
				if(currentParam.type().isObjectType()){
					ObjectType type = (ObjectType) currentParam.type();
					opVariableClasses.put(currentParam.name(),model.getClass(type.cls().name()));
				}

				if (currentParam.type().isCollection(true)) {
					opCollectionVariables.add(currentParam.name());
				}
			}
		}
	}

	/**
	 * Visit the variable on which the operation is called.
	 * 
	 * @param arguments
	 */

	private void visitOperationVariable(org.tzi.use.uml.ocl.expr.Expression operationVariable) {
		VariableOperationVisitor variableVisitor = new VariableOperationVisitor(model, variables, variableClasses, replaceVariables,
				collectionVariables);
		operationVariable.processWithVisitor(variableVisitor);
		
		opVariables.put("self", (Node) variableVisitor.getObject());

		IClass clazz = variableVisitor.getAttributeClass();

		opVariableClasses.put("self", clazz);
		if (variableVisitor.isSet()) {
			opCollectionVariables.add("self");
		}
	}

}
