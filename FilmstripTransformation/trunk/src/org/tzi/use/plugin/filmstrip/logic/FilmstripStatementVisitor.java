package org.tzi.use.plugin.filmstrip.logic;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.tzi.use.plugin.filmstrip.logic.FilmstripExpressionVisitor.ExpressionType;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.sys.ppcHandling.PPCHandler;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MBlockStatement;
import org.tzi.use.uml.sys.soil.MConditionalExecutionStatement;
import org.tzi.use.uml.sys.soil.MEmptyStatement;
import org.tzi.use.uml.sys.soil.MEnterOperationStatement;
import org.tzi.use.uml.sys.soil.MExitOperationStatement;
import org.tzi.use.uml.sys.soil.MIterationStatement;
import org.tzi.use.uml.sys.soil.MLibraryOperationCallStatement;
import org.tzi.use.uml.sys.soil.MLinkDeletionStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;
import org.tzi.use.uml.sys.soil.MNewLinkObjectStatement;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;
import org.tzi.use.uml.sys.soil.MObjectDestructionStatement;
import org.tzi.use.uml.sys.soil.MObjectOperationCallStatement;
import org.tzi.use.uml.sys.soil.MObjectRestorationStatement;
import org.tzi.use.uml.sys.soil.MOperationCallInverseStatement;
import org.tzi.use.uml.sys.soil.MOperationCallStatement;
import org.tzi.use.uml.sys.soil.MRValue;
import org.tzi.use.uml.sys.soil.MRValueExpression;
import org.tzi.use.uml.sys.soil.MRValueNewLinkObject;
import org.tzi.use.uml.sys.soil.MRValueNewObject;
import org.tzi.use.uml.sys.soil.MRValueOperationCall;
import org.tzi.use.uml.sys.soil.MSequenceStatement;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.uml.sys.soil.MStatementVisitor;
import org.tzi.use.uml.sys.soil.MVariableAssignmentStatement;
import org.tzi.use.uml.sys.soil.MVariableDestructionStatement;
import org.tzi.use.uml.sys.soil.MWhileStatement;
import org.tzi.use.uml.sys.soil.library.LibraryOperation;

public class FilmstripStatementVisitor implements MStatementVisitor {
	
	private final MModel model;
	private final MClass src;
	private final MModelConnector mc;
	
	private final Stack<MStatement> elements = new Stack<MStatement>();
	//TODO predefined vars/parameters
	
	public FilmstripStatementVisitor(MModel model, MClass src, MModelConnector mc) {
		this.model = model;
		this.src = src;
		this.mc = mc;
	}
	
	public MStatement getResultStatement() {
		try {
			return elements.peek();
		}
		catch(EmptyStackException ex){
			throw new TransformationException("No result statement present", ex);
		}
	}
	
	private MStatement processSubStatement(MStatement body) throws Exception {
		int elemSize = elements.size();
		body.processWithVisitor(this);
		if(elemSize+1 != elements.size()){
			throw new TransformationException(
					"A sub-statement killed the statement stack!\n"
							+ body.toString());
		}
		return elements.pop();
	}
	
	private Expression processExpression(Expression expr) throws Exception {
		FilmstripExpressionVisitor fv = new FilmstripExpressionVisitor(model, src, ExpressionType.SOIL, mc, null);
		expr.processWithVisitor(fv);
		return fv.getResultExpression();
	}
	
	private Expression[] processExpressionArray(Expression[] exps) throws Exception {
		Expression[] newExps = new Expression[exps.length];
		for(int i = 0; i < exps.length; i++){
			newExps[i] = processExpression(exps[i]);
		}
		return newExps;
	}
	
	private MRValue processMRValue(MRValue rValue) throws Exception {
		if(rValue instanceof MRValueExpression){
			MRValueExpression rVal = (MRValueExpression) rValue;
			return new MRValueExpression(processExpression(rVal.getExpression()));
		}
		else if(rValue instanceof MRValueNewLinkObject){
			MNewLinkObjectStatement stmt = (MNewLinkObjectStatement) processSubStatement(((MRValueNewLinkObject) rValue)
					.getNewLinkObjectStatement());
			return new MRValueNewLinkObject(stmt);
		}
		else if(rValue instanceof MRValueNewObject){
			MNewObjectStatement stmt = (MNewObjectStatement) processSubStatement(((MRValueNewObject) rValue)
					.getNewObjectStatement());
			return new MRValueNewObject(stmt);
		}
		else if(rValue instanceof MRValueOperationCall){
			MOperationCallStatement stmt = (MOperationCallStatement) processSubStatement(((MRValueOperationCall) rValue)
					.getOperationCallStatement());
			return new MRValueOperationCall(stmt);
		}
		else {
			throw new TransformationException("Unknown subclass of MRValue.");
		}
	}
	
	private List<MRValue> processMRValueList(List<MRValue> participants) throws Exception {
		List<MRValue> ret = new ArrayList<MRValue>();
		for(MRValue val : participants){
			ret.add(processMRValue(val));
		}
		return ret;
	}
	
	private List<VarDecl> processVarDeclList(List<VarDecl> variableDeclarations) {
		List<VarDecl> ret = new ArrayList<VarDecl>();
		for(VarDecl var : variableDeclarations){
			ret.add(new VarDecl(var.name(), mc.mapType(var.type())));
		}
		return ret;
	}
	
	@Override
	public void visit(MAttributeAssignmentStatement s) throws Exception {
		Expression expr = processExpression(s.getObject());
		MAttribute attr = mc.mapAttribute(s.getAttribute());
		MRValue val = processMRValue(s.getRValue());
		
		elements.push(new MAttributeAssignmentStatement(expr, attr, val));
	}

	@Override
	public void visit(MBlockStatement s) throws Exception {
		List<VarDecl> vars = processVarDeclList(s.getVariableDeclarations());
		MStatement stmt = processSubStatement(s.getBody());
		
		elements.push(new MBlockStatement(vars, stmt));
	}

	@Override
	public void visit(MConditionalExecutionStatement s) throws Exception {
		Expression expr = processExpression(s.getCondition());
		MStatement thenStmt = processSubStatement(s.getThenStatement());
		MStatement elseStmt = processSubStatement(s.getElseStatement());
		
		elements.push(new MConditionalExecutionStatement(expr, thenStmt, elseStmt));
	}

	@Override
	public void visit(MEmptyStatement s) throws Exception {
		elements.push(MEmptyStatement.getInstance());
	}

	@Override
	public void visit(MEnterOperationStatement s) throws Exception {
		Expression object = processExpression(s.getObject());
		MOperation operation = mc.mapOperation(s.getOperation());
		Expression[] arguments = processExpressionArray(s.getArguments());
		PPCHandler handler = s.getCustomPPCHandler();
		
		elements.push(new MEnterOperationStatement(object, operation, arguments, handler));
	}

	@Override
	public void visit(MExitOperationStatement s) throws Exception {
		Expression operationResult = processExpression(s.getOperationResult());
		PPCHandler handler = s.getCustomPPCHandler();
		
		elements.push(new MExitOperationStatement(operationResult, handler));
	}

	@Override
	public void visit(MIterationStatement s) throws Exception {
		Expression range = processExpression(s.getRange());
		MStatement body = processSubStatement(s.getBody());
		
		elements.push(new MIterationStatement(s.getVariableName(), range, body));
	}

	@Override
	public void visit(MLinkDeletionStatement s) throws Exception {
		MAssociation association = mc.mapAssociation(s.getAssociation());
		List<MRValue> participants = processMRValueList(s.getParticipants());
		List<List<MRValue>> qualifiers = new ArrayList<List<MRValue>>();
		for(List<MRValue> val : s.getQualifiers()){
			qualifiers.add(processMRValueList(val));
		}
		
		elements.push(new MLinkDeletionStatement(association, participants, qualifiers));
	}

	@Override
	public void visit(MLinkInsertionStatement s) throws Exception {
		MAssociation association = mc.mapAssociation(s.getAssociation());
		List<MRValue> participants = processMRValueList(s.getParticipants());
		List<List<MRValue>> qualifiers = new ArrayList<List<MRValue>>();
		for(List<MRValue> val : s.getQualifiers()){
			qualifiers.add(processMRValueList(val));
		}
		
		elements.push(new MLinkInsertionStatement(association, participants, qualifiers));
	}

	@Override
	public void visit(MNewLinkObjectStatement s) throws Exception {
		MAssociationClass associationClass = mc.mapAssociationClass(s.getAssociationClass());
		List<MRValue> participants = processMRValueList(s.getParticipants());
		List<List<MRValue>> qualifiers = new ArrayList<List<MRValue>>();
		for(List<MRValue> val : s.getQualifiers()){
			qualifiers.add(processMRValueList(val));
		}
		Expression objectName = (s.getObjectName() != null)?processExpression(s.getObjectName()):null;
		
		elements.push(new MNewLinkObjectStatement(associationClass, participants, qualifiers, objectName));
	}

	@Override
	public void visit(MNewObjectStatement s) throws Exception {
		MClass objectClass = mc.mapClass(s.getObjectClass());
		Expression objName = s.getObjectName();
		if(objName != null){
			objName = processExpression(objName);
		}
		
		elements.push(new MNewObjectStatement(objectClass, objName));
	}

	@Override
	public void visit(MObjectDestructionStatement s) throws Exception {
		Expression toDelete = processExpression(s.getToDelete());
		
		elements.push(new MObjectDestructionStatement(toDelete));
	}

	@Override
	public void visit(MObjectRestorationStatement s) throws Exception {
		// not part of a static model definition
	}

	@Override
	public void visit(MOperationCallInverseStatement s) throws Exception {
		// not part of a static model definition
	}

	@Override
	public void visit(MLibraryOperationCallStatement s) throws Exception {
		LibraryOperation operation = s.getOperation();
		Expression[] args = processExpressionArray(s.getArguments());
		
		elements.push(new MLibraryOperationCallStatement(operation, args));
	}

	@Override
	public void visit(MObjectOperationCallStatement s) throws Exception {
		Expression object = processExpression(s.getObject());
		MOperation operation = mc.mapOperation(s.getOperation());
		Expression[] arguments = processExpressionArray(s.getArguments());
		
		elements.push(new MObjectOperationCallStatement(object, operation, arguments));
	}

	@Override
	public void visit(MSequenceStatement s) throws Exception {
		MSequenceStatement stmt = new MSequenceStatement();
		
		for (MStatement subStmt : s.getStatements()) {
			stmt.appendStatement(processSubStatement(subStmt));
		}
		
		elements.push(stmt);
	}

	@Override
	public void visit(MVariableAssignmentStatement s) throws Exception {
		MRValue rValue = processMRValue(s.getValue());
		
		elements.push(new MVariableAssignmentStatement(s.getVariableName(), rValue));
	}

	@Override
	public void visit(MVariableDestructionStatement s) throws Exception {
		elements.push(new MVariableDestructionStatement(s.getVariableName()));
	}

	@Override
	public void visit(MWhileStatement s) throws Exception {
		Expression condition = processExpression(s.getCondition());
		MStatement body = processSubStatement(s.getBody());
		
		elements.push(new MWhileStatement(condition, body));
	}

}
