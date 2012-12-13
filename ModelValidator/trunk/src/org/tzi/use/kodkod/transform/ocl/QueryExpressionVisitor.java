package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kodkod.ast.Node;
import kodkod.ast.Variable;

import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.uml.ocl.expr.ExpAny;
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

/**
 * Extension of DefaultExpressionVisitor to visit the queries of an expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class QueryExpressionVisitor extends DefaultExpressionVisitor {

	private List<Object> arguments;

	public QueryExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, List<Variable>> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);
		arguments = new ArrayList<Object>();
	}

	@Override
	public void visitAny(ExpAny exp) {
		visitQueryAndInvoke(exp);
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
		createReplaceVariables(exp.getVariableDeclarations(), 2);

		visitQuery(exp);

		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getQueryExpression().processWithVisitor(visitor);
		arguments.add(2, visitor.getObject());

		invokeMethod(exp.name(), arguments, true);
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

	private void visitQueryAndInvoke(ExpQuery exp) {
		createVariables(exp.getVariableDeclarations());

		visitQuery(exp);

		invokeMethod(exp.name(), arguments, true);
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getRangeExpression().processWithVisitor(visitor);

		arguments.add(0, visitor.getObject());

		visitor = new DefaultExpressionVisitor(model, variables, variableClasses, replaceVariables, collectionVariables);
		exp.getQueryExpression().processWithVisitor(visitor);

		arguments.add(1, visitor.getObject());
	}

	/**
	 * Create the variables used in the query.
	 * 
	 * @param varDeclList
	 */
	private void createVariables(VarDeclList varDeclList) {
		VarDecl varDecl;
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

			boolean replace = false;
			String varName;
			if (variables.containsKey(varDecl.name())) {
				varName = UUID.randomUUID().toString();
				replace = true;
			} else {
				varName = varDecl.name();

			}
			variables.put(varName, createKodkodVariable(varName));

			objectVariable(varDecl, varName);

			if (replace) {
				List<Variable> variable = new ArrayList<Variable>();
				variable.add((Variable) variables.get(varName));
				replaceVariables.put(varDecl.name(), variable);
			}
		}
	}

	/**
	 * Create replace variables for the isUnique operation.
	 * 
	 * @param varDeclList
	 * @param variableCount
	 */
	private void createReplaceVariables(VarDeclList varDeclList, int variableCount) {
		if (varDeclList.size() == 1) {
			VarDecl varDecl = varDeclList.varDecl(0);

			List<Variable> variables = new ArrayList<Variable>();
			for (int i = 1; i <= variableCount; i++) {
				variables.add(createKodkodVariable(varDecl.name() + "" + i));
			}

			replaceVariables.put(varDecl.name(), variables);

			objectVariable(varDecl, varDecl.name());
		}
	}

	/**
	 * Stores the class of an object variable for futher use.
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
