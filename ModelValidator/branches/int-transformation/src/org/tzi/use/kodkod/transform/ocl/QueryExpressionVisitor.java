package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

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
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;

/**
 * Extension of DefaultExpressionVisitor to visit the queries of an expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class QueryExpressionVisitor extends DefaultExpressionVisitor {

	private List<Object> arguments;
	
	//FIXME get rid of this field variable by refactoring methods
	private boolean object_type_nav;

	public QueryExpressionVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, Variable> replaceVariables, List<String> collectionVariables, Stack<OclTransformationContext> contextStack) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables, contextStack);
		arguments = new ArrayList<Object>();
		object_type_nav = false;
	}

	@Override
	public void visitAny(ExpAny exp) {
		visitQueryAndInvoke(exp);
	}

	@Override
	public void visitClosure(ExpClosure expClosure) {
		visitQuery(expClosure);

		Type sourceType = expClosure.getRangeExpression().type();
		if (sourceType.isKindOfCollection(VoidHandling.EXCLUDE_VOID)) {
			if (((CollectionType) sourceType).elemType().isTypeOfClass()) {
				MClass type = (MClass) ((CollectionType) sourceType).elemType();
				IClass clazz = model.getClass(type.name());
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

		invokeMethod(expClosure.name(), arguments, true, false);
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
		OclTransformationContext rangeCtx = processSubExpression(exp.getRangeExpression());
		arguments.add(0, rangeCtx.object);

		List<String> replacedVariables = createVariables(exp.getVariableDeclarations());

		OclTransformationContext queryCtx = processSubExpression(exp.getQueryExpression());
		arguments.add(1, queryCtx.object);

		/*
		 * We go through the same expression again but rename the variable
		 * first. This way we have the same expression twice with different
		 * variable names to represent the expression isUnique using a forAll
		 * expression with two variables:
		 * 
		 * src->isUnique( a | expr(a) ) becomes
		 * src->forAll( a, b | a <> b implies expr(a) <> expr(b) )
		 */
		replacedVariables.addAll(createVariables(exp.getVariableDeclarations()));

		OclTransformationContext queryCtx2 = processSubExpression(exp.getQueryExpression());
		arguments.add(2, queryCtx2.object);

		invokeMethod(exp.name(), arguments, true, rangeCtx.object_type_nav);

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
		OclTransformationContext rangeCtx = processSubExpression(exp.getRangeExpression());
		arguments.add(0, rangeCtx.object);
		object_type_nav = rangeCtx.object_type_nav;

		List<String> replacedVariables = createVariables(exp.getVariableDeclarations());

		OclTransformationContext queryCty = processSubExpression(exp.getQueryExpression());
		arguments.add(1, queryCty.object);

		removeReplaceVariable(replacedVariables);
	}

	private void visitQueryAndInvoke(ExpQuery exp) {
		visitQuery(exp);
		invokeMethod(exp.name(), arguments, true, object_type_nav);
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
			 * the variables is replaced by UUID, which is used instead of the
			 * real variable name.
			 */

			String varName;
			if (variables.containsKey(varDecl.name())) {
				varName = UUID.randomUUID().toString();
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
		if (varDecl.type().isTypeOfClass()) {
			variableClasses.put(name, model.getClass(varDecl.type().shortName()));
		}
	}

	private Variable createKodkodVariable(String name) {
		Variable var = Variable.unary(name);
		arguments.add(var);
		return var;
	}
}
