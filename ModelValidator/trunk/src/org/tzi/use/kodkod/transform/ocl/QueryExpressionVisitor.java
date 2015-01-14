package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.kodkod.transform.TransformationException;
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
import org.tzi.use.uml.ocl.type.ObjectType;

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

		if (sourceType.isCollection(true)) {
			if (((CollectionType) sourceType).elemType().isObjectType()) {
				ObjectType type = (ObjectType) ((CollectionType) sourceType).elemType();
				IClass clazz = model.getClass(type.cls().name());
				Relation relation = clazz.relation();
				if (clazz.existsInheritance()) {
					relation = clazz.inheritanceRelation();
				}
				arguments.add(1, relation);
			} else {
				throw new TransformationException(LogMessages.closureObjectMessage);
			}
		} else {
			throw new TransformationException(LogMessages.closureCollectionMessage);
		}

		invokeMethod(expClosure.name(), arguments, true);
	}

	@Override
	public void visitCollect(ExpCollect exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitCollectNested(ExpCollectNested exp) {
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

		List<String> replacedVariables = createVariables(exp.getVariableDeclarations());

		DefaultExpressionVisitor visitor2 = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getQueryExpression().processWithVisitor(visitor2);
		arguments.add(1, visitor2.getObject());

		replacedVariables.addAll(createVariables(exp.getVariableDeclarations()));

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

	@Override
	public void visitQuery(ExpQuery exp) {
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getRangeExpression().processWithVisitor(visitor);
		sourceType = exp.getRangeExpression().type();
		arguments.add(0, visitor.getObject());

		List<String> replacedVariables = createVariables(exp.getVariableDeclarations());

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
	private List<String> createVariables(VarDeclList varDeclList) {
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
				varName = UUID.randomUUID().toString();
				// varName = varDecl.name()+"_"+(int) (Math.random() * (100 - 2)
				// + 2);
				Variable v = createKodkodVariable(varName);
				replaceVariables.put(varDecl.name(), v);
				replaced.add(varDecl.name());
			} else {
				varName = varDecl.name();
				variables.put(varName, createKodkodVariable(varName));

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
		if (varDecl.type().isObjectType()) {
			variableClasses.put(name, model.getClass(varDecl.type().shortName()));
		}
	}

	private Variable createKodkodVariable(String name) {
		Variable var = Variable.unary(name);
		arguments.add(var);
		return var;
	}
}
