/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2012 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.tzi.use.uml.ocl.expr;

import java.io.StringWriter;

/**
 * Work in progress for a HTML output of an expression with colors, etc.
 * @author Lars Hamann
 *
 */
public class GenerateHTMLExpressionVisitor implements ExpressionVisitor {

	private final StringWriter buffer;
	
	/**
	 * @param sw
	 */
	public GenerateHTMLExpressionVisitor(StringWriter sw) {
		this.buffer = sw;
	}

	// helper
	private void visiQueryOperation(ExpQuery exp) {
		exp.getRangeExpression().processWithVisitor(this);
		writeSpan("operation", exp.name());
		writeSpan("operator", "(");
		exp.getVariableDeclarations().processWithVisitor(this);
		writeSpan("operator", "|");
		exp.getQueryExpression().processWithVisitor(this);
	}
	
	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		writeSpan("type", exp.getSourceType().toString());
		writeSpan("allInstances", ".allInstances()");
	}

	@Override
	public void visitAny(ExpAny exp) {
		visiQueryOperation(exp);
	}

	@Override
	public void visitAsType(ExpAsType exp) {
		exp.getSourceExpr().processWithVisitor(this);
		buffer.write('.');
		writeSpan("operation", "oclAsType");
		writeSpan("operator", "(");
		writeSpan("type", exp.type().toString());
		writeSpan("operator", ")");
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		exp.objExp().processWithVisitor(this);
		buffer.write('.');
		writeSpan("attribute", exp.attr().name());
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		writeSpan("collectionLiteral", "Bag");
		writeSpan("operator", "{");
		
		for (Expression e : exp.getElemExpr()) {
			e.processWithVisitor(this);
		}
		
		writeSpan("operator", "}");
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
		writeSpan("literal", String.valueOf(exp.value()));
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
		writeSpan("literal", exp.value());
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
		writeSpan("literal", String.valueOf(exp.value()));
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
		writeSpan("literal", String.valueOf(exp.value()));
	}

	@Override
	public void visitConstString(ExpConstString exp) {
		writeSpan("literal", exp.value());
	}

	@Override
	public void visitEmptyCollection(ExpEmptyCollection exp) {
		writeSpan("operation", "oclEmpty");
		writeSpan("operator", "(");
		writeSpan("type", exp.type().toString());
		writeSpan("operator", ")");
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
		writeSpan("keyword", "if");
		exp.getCondition().processWithVisitor(this);
		writeSpan("keyword", "then");
		exp.getThenExpression().processWithVisitor(this);
		writeSpan("keyword", "else");
		exp.getElseExpression().processWithVisitor(this);
		writeSpan("keyword", "endif");
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		exp.getSourceExpr().processWithVisitor(this);

		if (exp.getSourceExpr().type().isCollection(true))
        	buffer.write("->");
        else
        	buffer.write(".");
		
		writeSpan("operation", "oclIsTypeOf");
		writeSpan("operator", "(");
		writeSpan("type", exp.type().toString());
		writeSpan("operator", ")");
	}

	
	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		visitQuery(exp);
	}

	@Override
	public void visitIterate(ExpIterate exp) {
		
	}

	@Override
	public void visitLet(ExpLet exp) {

	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		exp.getObjectExpression().processWithVisitor(this);
		buffer.write('.');
		buffer.write(exp.getDestination().nameAsRolename());
	}

	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {

	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		exp.getArguments()[0].processWithVisitor(this);
		buffer.write('.');
		writeSpan("operation", exp.getOperation().name());
		writeSpan("operator", "(");
		for (int i = 1; i < exp.getArguments().length; ++i) {
			if (i > 1)
				buffer.write(", ");
			exp.getArguments()[i].processWithVisitor(this);
		}
		writeSpan("operator", ")");
	}

	@Override
	public void visitObjRef(ExpObjRef exp) {
		writeSpan("objname", exp.toString());
	}

	@Override
	public void visitOne(ExpOne exp) {
		visitQuery(exp);
	}

	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {

	}

	@Override
	public void visitQuery(ExpQuery exp) {

	}

	@Override
	public void visitReject(ExpReject exp) {

	}

	@Override
	public void visitWithValue(ExpressionWithValue exp) {

	}

	@Override
	public void visitSelect(ExpSelect exp) {

	}

	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {

	}

	@Override
	public void visitSetLiteral(ExpSetLiteral exp) {

	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {

	}

	@Override
	public void visitStdOp(ExpStdOp exp) {

	}

	@Override
	public void visitTupleLiteral(ExpTupleLiteral exp) {

	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {

	}

	@Override
	public void visitUndefined(ExpUndefined exp) {

	}

	@Override
	public void visitVariable(ExpVariable exp) {

	}

	@Override
	public void visitClosure(ExpClosure expClosure) {

	}

	@Override
	public void visitOclInState(ExpOclInState expOclInState) {

	}
	
	@Override
	public void visitVarDeclList(VarDeclList varDeclList) {
		for (int i = 0; i < varDeclList.size(); ++i) {
			if (i > 0)
				buffer.write(", ");
			
			varDeclList.varDecl(i).processWithVisitor(this);
		}
	}
	
	@Override
	public void visitVarDecl(VarDecl varDecl) {
		writeSpan("var", varDecl.name());
		buffer.write(":");
		writeSpan("type", varDecl.type().toString());
	}

	
	private void writeSpan(String cls, String content) {
		buffer.write("<span class='");
		buffer.write(cls);
		buffer.write("'>");
		buffer.write(content);
		buffer.write("</span>");
	}
	
	public static String CSS = 
			"<style type='text/css'>" +
			"*.type {font-weight:bold}" +
			"*.literal {font-style:italic;}" + 
			"</style>";

		@Override
	public void visitObjectByUseId(ExpObjectByUseId expObjectByUseId) {
		buffer.write(expObjectByUseId.getSourceType().cls().name());
		buffer.write("(");
		expObjectByUseId.processWithVisitor(this);
		buffer.write(")");
	}

	@Override
	public void visitConstUnlimitedNatural(
			ExpConstUnlimitedNatural expressionConstUnlimitedNatural) {
		buffer.write("*");
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind expSelectByKind) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType expSelectByType) {
		// TODO Auto-generated method stub
		
	}
}
