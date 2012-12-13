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
import org.tzi.use.kodkod.transform.OperationRecursionDetector;
import org.tzi.use.kodkod.transform.OperationRecursionDetector.OperationRecursionException;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.VarDecl;

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
	private Map<String, List<Variable>> opReplaceVariables;

	public OperationExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, List<Variable>> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);

		opVariables = new HashMap<String, Node>();
		opCollectionVariables = new ArrayList<String>();
		opVariableClasses = new HashMap<String, IClass>();
		opReplaceVariables = new HashMap<String, List<Variable>>();
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		MOperation operation = exp.getOperation();
		try {
			OperationRecursionDetector.INSTANCE.addOperation(operation);
		} catch (OperationRecursionException e) {
			throw new TransformationException("Operation " + operation.name() + " is recursive!", e);
		}

		org.tzi.use.uml.ocl.expr.Expression[] arguments = exp.getArguments();
		
		visitOperationVariable(arguments[0]);
		visitParams(operation, arguments);
		
		Node self = opVariables.get("self");
		Expression expression = getAsExpression(self);
		
		Map<MClass,MOperation> overiddenOperations = overriddenOperations(operation.cls(),operation.name());
		
		DefaultExpressionVisitor mainVisitor=visitOperation(operation);
		if(overiddenOperations.isEmpty()){
			object = mainVisitor.getObject();
		}
		else{
			Iterator<MClass> iterator = overiddenOperations.keySet().iterator();
			object = test(iterator,overiddenOperations,expression,getAsExpression(mainVisitor.getObject()));
		}
		
		set = mainVisitor.isSet();
		object_type_nav = mainVisitor.isObject_type_nav();

		opVariables.remove("self");
		opVariableClasses.remove("self");
		
		variables.putAll(opVariables);
		variableClasses.putAll(opVariableClasses);
		
		OperationRecursionDetector.INSTANCE.finishOperation();
	}

	private Expression test(Iterator<MClass> iterator, Map<MClass, MOperation> overiddenOperations, Expression selfExpression,Expression object) {
		if(!iterator.hasNext()){
			return object;
		}
		else{
			MClass cls = iterator.next();
			Formula inFormula = selfExpression.in(model.getClass(cls.name()).relation());
			Expression expression = getAsExpression(visitOperation(overiddenOperations.get(cls)).getObject());
			return inFormula.thenElse(expression, test(iterator, overiddenOperations, selfExpression, object));
		}
	}

	private Expression getAsExpression(Object object) {
		if(object instanceof Expression){
			return (Expression) object;
		}
		return ExpressionHelper.boolean_formula2expr((Formula) object, model.typeFactory());
	}

	private Map<MClass, MOperation> overriddenOperations(MClass cls,String operationName){
		Map<MClass,MOperation> operations = new HashMap<MClass,MOperation>();		

		for(MClass child : cls.children()){
			MOperation operation = child.operation(operationName, false);
			if(operation!=null){
				operations.put(child, operation);
			}
			operations.putAll(overriddenOperations(child, operationName));
		}
		
		return operations;
	}
	
	
	/**
	 * Visit the operation.
	 * 
	 * @param operation
	 * @return 
	 */
	
	protected DefaultExpressionVisitor visitOperation(MOperation operation) {
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
	
	protected void visitParams(MOperation operation, org.tzi.use.uml.ocl.expr.Expression[] arguments) {
		DefaultExpressionVisitor visitor;
		List<VarDecl> params = operation.allParams();
		if (params.size() > 0) {

			VarDecl currentParam;
			for (int i = 0; i < params.size(); i++) {
				visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
				arguments[i + 1].processWithVisitor(visitor);

				currentParam = params.get(i);
				opVariables.put(currentParam.name(), (Node) visitor.getObject());
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
	
	protected void visitOperationVariable(org.tzi.use.uml.ocl.expr.Expression operationVariable) {
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
