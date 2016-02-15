package org.tzi.use.kodkod.transform.ocl;

import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpBagLiteral;
import org.tzi.use.uml.ocl.expr.ExpClosure;
import org.tzi.use.uml.ocl.expr.ExpCollect;
import org.tzi.use.uml.ocl.expr.ExpCollectNested;
import org.tzi.use.uml.ocl.expr.ExpConstBoolean;
import org.tzi.use.uml.ocl.expr.ExpConstEnum;
import org.tzi.use.uml.ocl.expr.ExpConstInteger;
import org.tzi.use.uml.ocl.expr.ExpConstReal;
import org.tzi.use.uml.ocl.expr.ExpConstString;
import org.tzi.use.uml.ocl.expr.ExpConstUnlimitedNatural;
import org.tzi.use.uml.ocl.expr.ExpEmptyCollection;
import org.tzi.use.uml.ocl.expr.ExpExists;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpIf;
import org.tzi.use.uml.ocl.expr.ExpIsKindOf;
import org.tzi.use.uml.ocl.expr.ExpIsTypeOf;
import org.tzi.use.uml.ocl.expr.ExpIsUnique;
import org.tzi.use.uml.ocl.expr.ExpIterate;
import org.tzi.use.uml.ocl.expr.ExpLet;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpNavigationClassifierSource;
import org.tzi.use.uml.ocl.expr.ExpObjAsSet;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpObjRef;
import org.tzi.use.uml.ocl.expr.ExpObjectByUseId;
import org.tzi.use.uml.ocl.expr.ExpOclInState;
import org.tzi.use.uml.ocl.expr.ExpOne;
import org.tzi.use.uml.ocl.expr.ExpOrderedSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpRange;
import org.tzi.use.uml.ocl.expr.ExpReject;
import org.tzi.use.uml.ocl.expr.ExpSelect;
import org.tzi.use.uml.ocl.expr.ExpSelectByKind;
import org.tzi.use.uml.ocl.expr.ExpSelectByType;
import org.tzi.use.uml.ocl.expr.ExpSequenceLiteral;
import org.tzi.use.uml.ocl.expr.ExpSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpSortedBy;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral.Part;
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionVisitor;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;

public class ExpressionTraversalVisitor implements ExpressionVisitor {

	@Override
	public void visitAllInstances(ExpAllInstances exp) {
	}

	@Override
	public void visitAny(ExpAny exp) {
		visitQuery(exp);
	}

	@Override
	public void visitAsType(ExpAsType exp) {
		exp.getSourceExpr().processWithVisitor(this);
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		for (Expression expression : exp.getElemExpr()) {
			expression.processWithVisitor(this);
		}
	}

	@Override
	public void visitCollect(ExpCollect exp) {
		visitQuery(exp);
	}

	@Override
	public void visitCollectNested(ExpCollectNested exp) {
		visitQuery(exp);
	}

	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
	}

	@Override
	public void visitConstString(ExpConstString exp) {
	}

	@Override
	public void visitEmptyCollection(ExpEmptyCollection exp) {
	}

	@Override
	public void visitExists(ExpExists exp) {
		visitQuery(exp);
	}

	@Override
	public void visitForAll(ExpForAll exp) {
		visitQuery(exp);
	}

	@Override
	public void visitIf(ExpIf exp) {
		exp.getCondition().processWithVisitor(this);
		exp.getThenExpression().processWithVisitor(this);
		exp.getElseExpression().processWithVisitor(this);
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		exp.getSourceExpr().processWithVisitor(this);
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		exp.getSourceExpr().processWithVisitor(this);
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		visitQuery(exp);
	}

	@Override
	public void visitIterate(ExpIterate exp) {
		visitQuery(exp);
	}

	@Override
	public void visitLet(ExpLet exp) {
		exp.getVarExpression().processWithVisitor(this);
		exp.getInExpression().processWithVisitor(this);
	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		exp.getObjectExpression().processWithVisitor(this);
	}

	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {
		exp.getObjectExpression().processWithVisitor(this);
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		for (Expression expression : exp.getArguments()) {
			expression.processWithVisitor(this);
		}
	}

	@Override
	public void visitObjRef(ExpObjRef exp) {
	}

	@Override
	public void visitOne(ExpOne exp) {
		visitQuery(exp);
	}

	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
		for (Expression expression : exp.getElemExpr()) {
			expression.processWithVisitor(this);
		}
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		exp.getRangeExpression().processWithVisitor(this);
		exp.getQueryExpression().processWithVisitor(this);
	}

	@Override
	public void visitReject(ExpReject exp) {
		visitQuery(exp);
	}

	@Override
	public void visitWithValue(ExpressionWithValue exp) {
	}

	@Override
	public void visitSelect(ExpSelect exp) {
		visitQuery(exp);
	}

	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {
		for (Expression expression : exp.getElemExpr()) {
			expression.processWithVisitor(this);
		}
	}

	@Override
	public void visitSetLiteral(ExpSetLiteral exp) {
		for (Expression expression : exp.getElemExpr()) {
			expression.processWithVisitor(this);
		}
	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {
		visitQuery(exp);
	}

	@Override
	public void visitStdOp(ExpStdOp exp) {
		for (Expression expression : exp.args()) {
			expression.processWithVisitor(this);
		}
	}

	@Override
	public void visitTupleLiteral(ExpTupleLiteral exp) {
		for (Part part : exp.getParts()) {
			part.getExpression().processWithVisitor(this);
		}
	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {
		exp.getTupleExp().processWithVisitor(this);
	}

	@Override
	public void visitUndefined(ExpUndefined exp) {
	}

	@Override
	public void visitVariable(ExpVariable exp) {
	}

	@Override
	public void visitClosure(ExpClosure exp) {
		visitQuery(exp);
	}

	@Override
	public void visitOclInState(ExpOclInState exp) {
		exp.getSourceExpr().processWithVisitor(this);
	}

	@Override
	public void visitVarDeclList(VarDeclList varDeclList) {
	}

	@Override
	public void visitVarDecl(VarDecl varDecl) {
	}

	@Override
	public void visitObjectByUseId(ExpObjectByUseId exp) {
		exp.getIdExpression().processWithVisitor(this);
	}

	@Override
	public void visitConstUnlimitedNatural(ExpConstUnlimitedNatural exp) {
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind exp) {
		exp.getSourceExpression().processWithVisitor(this);
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType exp) {
		exp.getSourceExpression().processWithVisitor(this);
	}

	@Override
	public void visitRange(ExpRange exp) {
		exp.getStart().processWithVisitor(this);
		exp.getEnd().processWithVisitor(this);
	}

	@Override
	public void visitNavigationClassifierSource(
			ExpNavigationClassifierSource exp) {
		exp.getObjectExpression().processWithVisitor(this);
	}

}
