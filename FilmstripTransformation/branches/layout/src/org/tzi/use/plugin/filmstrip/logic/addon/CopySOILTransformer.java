package org.tzi.use.plugin.filmstrip.logic.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.plugin.filmstrip.logic.FilmstripUtil;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MBlockStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;
import org.tzi.use.uml.sys.soil.MObjectOperationCallStatement;
import org.tzi.use.uml.sys.soil.MRValue;
import org.tzi.use.uml.sys.soil.MRValueExpression;
import org.tzi.use.uml.sys.soil.MRValueNewObject;
import org.tzi.use.uml.sys.soil.MRValueOperationCall;
import org.tzi.use.uml.sys.soil.MSequenceStatement;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.uml.sys.soil.MVariableAssignmentStatement;

public class CopySOILTransformer {
	
	private final MModel model;
	
	/**TODO
	 * - constant Strings into final fields
	 * - exception handling
	 */
	
	public CopySOILTransformer(MModel model) {
		this.model = model;
	}
	
	public void transformSoilOperations(){
		for(MClass cls : model.classes()){
			for(MOperation op : cls.operations()){
				if(!op.hasStatement() || FilmstripUtil.isFilmstripClass(op.cls())){
					continue;
				}
				
				MStatement stmt = transformSoilOperationStatement(op.getStatement(), op);
				op.setStatement(stmt);
			}
		}
	}
	
	private MStatement transformSoilOperationStatement(MStatement stmt, MOperation op){
		List<VarDecl> vars = new ArrayList<VarDecl>();
		
		if(stmt instanceof MBlockStatement){
			vars.addAll(((MBlockStatement) stmt).getVariableDeclarations());
		}
		
		MStatement[] prefix = createCopyPrefix(op, vars);
		MStatement[] main = transformSoilOperationBody(op.getStatement());
		MStatement[] suffix = createCopySuffix(op);
		
		LinkedList<MStatement> stmts = new LinkedList<MStatement>();
		
		for (int i = 0; i < prefix.length; i++) {
			stmts.add(prefix[i]);
		}
		for (int i = 0; i < main.length; i++) {
			stmts.add(main[i]);
		}
		for (int i = 0; i < suffix.length; i++) {
			stmts.add(suffix[i]);
		}
		
		return new MBlockStatement(vars, new MSequenceStatement(stmts));
	}
	
	private MStatement[] transformSoilOperationBody(MStatement stmt){
		if(stmt.isEmptyStatement()){
			return new MStatement[0];
		}
		else if(stmt instanceof MBlockStatement){
			return transformSoilOperationBody(((MBlockStatement) stmt).getBody());
		}
		else if(stmt instanceof MSequenceStatement){
			MStatement[] arr = new MStatement[((MSequenceStatement) stmt).getNumStatements()];
			return ((MSequenceStatement) stmt).getStatements().toArray(arr);
		}
		else {
			return new MStatement[]{ stmt };
		}
	}
	
	private MStatement[] createCopyPrefix(MOperation op, List<VarDecl> vars){
		ArrayList<MStatement> stmtList = new ArrayList<MStatement>(10 + op.paramList().size());
		
		MClass snapshot = model.getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
		MClass opCls = model.getClass(FilmstripModelConstants.makeOpCName(op.cls().name(), op.name()));
		MAssociation filmstrip = model.getAssociation(FilmstripModelConstants.FILMSTRIP_ASSOCNAME);
		
		//TODO ensure unique names
		vars.add(new VarDecl("s", snapshot));
		vars.add(new VarDecl("op", opCls));
		
		MNavigableElement src = snapshot.navigableEnd(op.cls().nameAsRolename());
		MNavigableElement dst = op.cls().navigableEnd(FilmstripModelConstants.makeRoleName(FilmstripModelConstants.SNAPSHOT_CLASSNAME));
		Expression selfSnapshot;
		try {
			selfSnapshot = new ExpNavigation(new ExpVariable("self", op.cls()), src, dst, Collections.<Expression>emptyList());
		} catch (ExpInvalidException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			throw new UnsupportedOperationException("BAAAAAAD", ex);
		}
		
		// s := self.snapshot.copy()
		stmtList.add(new MVariableAssignmentStatement("s", new MRValueOperationCall(new MObjectOperationCallStatement(selfSnapshot, snapshot.operation("copy", false), new Expression[0]))));
		
		// op := new *_*OpC
		stmtList.add(new MVariableAssignmentStatement("op", new MRValueNewObject(new MNewObjectStatement(opCls, (String) null))));
		// op.aSelf := self
		stmtList.add(new MAttributeAssignmentStatement(new ExpVariable("op", opCls), opCls.attribute(FilmstripModelConstants.OPC_SELF_VARNAME, true), new ExpVariable("self", op.cls())));
		// other parameters
		for(VarDecl var : op.paramList()){
			stmtList.add(new MAttributeAssignmentStatement(new ExpVariable("op", opCls), opCls.attribute(var.name(), false), new ExpVariable(var.name(), var.type())));
		}
		// insert (self.snapshot, s, op)
		List<MRValue> participants = new ArrayList<MRValue>();
		participants.add(new MRValueExpression(selfSnapshot));
		participants.add(new MRValueExpression(new ExpVariable("s", snapshot)));
		participants.add(new MRValueExpression(new ExpVariable("op", opCls)));
		stmtList.add(new MLinkInsertionStatement(filmstrip, participants, Collections.<List<MRValue>>emptyList()));
		
		// self := self.succ
		stmtList.add(new MVariableAssignmentStatement("self", new MRValueExpression(FilmstripUtil.handlePredSucc(new ExpVariable("self", op.cls()), false, new Stack<VarDeclList>()))));
		// redefine parameters if necessary
		for(VarDecl var : op.paramList()){
			Expression expr = new ExpVariable(var.name(), var.type());
			Expression handled = FilmstripUtil.handlePredSucc(expr, false, new Stack<VarDeclList>());
			if(handled != expr){
				stmtList.add(new MVariableAssignmentStatement(var.name(), new MRValueExpression(handled)));
			}
		}

		MStatement[] ret = new MStatement[stmtList.size()];
		return stmtList.toArray(ret);
	}
	
	private MStatement[] createCopySuffix(MOperation op) {
		MClass opCls = model.getClass(FilmstripModelConstants.makeOpCName(op.cls().name(), op.name()));
		
		// retVal := result
		if(op.hasResultType()){
			Expression obj = new ExpVariable("op", opCls);
			MAttribute attr = opCls.attribute(FilmstripModelConstants.OPC_RETURNVALUE_VARNAME, false);
			return new MStatement[]{ new MAttributeAssignmentStatement(obj, attr, new ExpVariable("result", op.resultType())) };
		}
		else {
			return new MStatement[0];
		}
	}
	
}
