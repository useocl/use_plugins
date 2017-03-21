package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpClosure;
import org.tzi.use.uml.ocl.expr.ExpCollect;
import org.tzi.use.uml.ocl.expr.ExpCollectNested;
import org.tzi.use.uml.ocl.expr.ExpExists;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpIsUnique;
import org.tzi.use.uml.ocl.expr.ExpOne;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpReject;
import org.tzi.use.uml.ocl.expr.ExpSelect;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;

import kodkod.ast.Expression;
import kodkod.ast.Node;
import kodkod.ast.Variable;

/**
 * Extension of DefaultExpressionVisitor to visit the queries of an expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class QueryExpressionVisitor extends DefaultExpressionVisitor {

	private List<Object> arguments;

	public QueryExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, Variable> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);
		arguments = new ArrayList<Object>();
	}

	@Override
	public void visitAny(ExpAny exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitClosure(ExpClosure expClosure) {
		visitQuery(expClosure);

		if (sourceType.isKindOfCollection(VoidHandling.EXCLUDE_VOID)) {
			if (((CollectionType) sourceType).elemType().isTypeOfClass()) {
				MClass type = (MClass) ((CollectionType) sourceType).elemType();
				IClass clazz = model.getClass(type.name());
				arguments.add(1, clazz.inheritanceOrRegularRelation());
			} else {
				throw new TransformationException(LogMessages.closureObjectMessage);
			}
		} else {
			throw new TransformationException(LogMessages.closureCollectionMessage);
		}

		invokeMethod(expClosure.name(), arguments, true);
	}

	private void collectTypeCheck(ExpQuery exp){
		if(exp.getRangeExpression().type().isKindOfBag(VoidHandling.EXCLUDE_VOID)
				|| exp.getRangeExpression().type().isKindOfSet(VoidHandling.EXCLUDE_VOID)){
			LOG.warn("Collect operation `" + exp.toString() + "' results in unsupported type `Bag'. It will be interpreted as `Set'.");
		}
		else if(exp.getRangeExpression().type().isKindOfOrderedSet(VoidHandling.EXCLUDE_VOID)
				|| exp.getRangeExpression().type().isKindOfSequence(VoidHandling.EXCLUDE_VOID)){
			LOG.warn("Collect operation `" + exp.toString() + "' results in unsupported type `Sequence'. It will be interpreted as `Set'.");
		}
	}
	
	@Override
	public void visitCollect(ExpCollect exp) {
		collectTypeCheck(exp);
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitCollectNested(ExpCollectNested exp) {
		collectTypeCheck(exp);
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitForAll(ExpForAll exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitExists(ExpExists exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getRangeExpression().processWithVisitor(visitor);
		arguments.add(0, visitor.getObject());

		List<String> replacedVariables = createVariables(exp.getVariableDeclarations(), ((Expression) visitor.getObject()).arity());

		DefaultExpressionVisitor visitor2 = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getQueryExpression().processWithVisitor(visitor2);
		arguments.add(1, visitor2.getObject());

		replacedVariables.addAll(createVariables(exp.getVariableDeclarations(), ((Expression) visitor.getObject()).arity()));

		visitor2 = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getQueryExpression().processWithVisitor(visitor2);
		arguments.add(2, visitor2.getObject());

		invokeMethod(exp.name(), arguments, true);

		removeReplaceVariable(replacedVariables);
	}

	@Override
	public void visitOne(ExpOne exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitReject(ExpReject exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitSelect(ExpSelect exp) {
		visitQueryAndInvoke(exp);
	}
	
	public void visitQuery(ExpQuery exp) {
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getRangeExpression().processWithVisitor(visitor);
		sourceType = exp.getRangeExpression().type();
		arguments.add(0, visitor.getObject());

		List<String> replacedVariables = createVariables(exp.getVariableDeclarations(), ((Expression) visitor.getObject()).arity());

		DefaultExpressionVisitor visitor2 = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getQueryExpression().processWithVisitor(visitor2);
		arguments.add(1, visitor2.getObject());
		
		removeReplaceVariable(replacedVariables);
	}
	
	private void visitQueryAndInvoke(ExpQuery exp) {
		visitQuery(exp);

		invokeMethod(exp.name(), arguments, true);
	}

	private void removeReplaceVariable(List<String> replacedVariables) {
		for (String varName : replacedVariables) {
			replaceVariables.remove(varName);
		}
	}

	/**
	 * Create the variables used in the query.
	 * 
	 * @param varDeclList
	 */
	private List<String> createVariables(VarDeclList varDeclList, int arity) {
		VarDecl varDecl;
		List<String> replaced = new ArrayList<String>();

		for (int i = 0; i < varDeclList.size(); i++) {
			varDecl = varDeclList.varDecl(i);

			/*
			 * Two variables with the same name in different contexts (e.g.
			 * Set{1,2,3,4}->select(e| e=1)->select(e| e=1)->notEmpty) causes
			 * evaluation failures. So 'Set{1,2,3,4}->select(e| e=1)->select(e|
			 * e=1)->notEmpty' is false and 'Set{1,2,3,4}->select(e|
			 * e=1)->select(f| f=1)->notEmpty' is true. To correct this, one of
			 * the variables is replaced by uuid, which is used instead of the
			 * real variable name.
			 */

			String varName;
			if (variables.containsKey(varDecl.name())) {
				Expression otherVar = (Expression) variables.get(varDecl.name());
				varName = UUID.randomUUID().toString();
				Variable v = createKodkodVariable(varName, otherVar.arity());
				replaceVariables.put(varDecl.name(), v);
				replaced.add(varDecl.name());
			} else {
				varName = varDecl.name();
				variables.put(varName, createKodkodVariable(varName, arity));
			}

			objectVariable(varDecl, varName);
		}
		return replaced;
	}

	/**
	 * Stores the class of an object variable for further use.
	 * 
	 * @param varDecl
	 */
	private void objectVariable(VarDecl varDecl, String name) {
		if (varDecl.type().isTypeOfClass()) {
			variableClasses.put(name, model.getClass(varDecl.type().shortName()));
		}
	}

	private Variable createKodkodVariable(String name, int arity) {
		Variable var = Variable.nary(name, arity);
		arguments.add(var);
		return var;
	}
}
