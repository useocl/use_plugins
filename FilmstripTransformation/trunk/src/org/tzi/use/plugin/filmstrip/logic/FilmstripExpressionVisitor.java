package org.tzi.use.plugin.filmstrip.logic;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.ListIterator;
import java.util.Stack;

import org.tzi.use.parser.SemanticException;
import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.mm.statemachines.MState;
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
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
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
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.ExpressionVisitor;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.expr.VarInitializer;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.util.StringUtil;

public class FilmstripExpressionVisitor implements ExpressionVisitor {

	protected final MModel model;
	protected final MClass src;
	protected final ExpressionType type;
	protected final MModelConnector mc;
	
	protected final Stack<Expression> elements = new Stack<Expression>();
	protected final VarDeclList selfVariables;
	protected final Stack<VarDeclList> knownVariables = new Stack<VarDeclList>();
	
	public enum ExpressionType {
		CLASSINVARIANT, PRECONDITION, POSTCONDITION, OPERATION, SOIL
	}
	
	public FilmstripExpressionVisitor(MModel model, MClass src,
			ExpressionType type, MModelConnector mc, VarDeclList varDefs) {
		this.model = model;
		this.src = src;
		this.type = type;
		this.mc = mc;
		
		switch (type) {
		case CLASSINVARIANT:
			selfVariables = varDefs;
			break;
		case OPERATION:
			knownVariables.push(varDefs);
		case SOIL:
			selfVariables = new VarDeclList(new VarDecl("self", src));
			break;
		case PRECONDITION:
		case POSTCONDITION:
			selfVariables = new VarDeclList(new VarDecl("self", src));
			break;
		default:
			throw new TransformationException("Unkown expression type "
					+ StringUtil.inQuotes(type));
		}
	}
	
	/**
	 * Can be called multiple times.
	 * 
	 * @return
	 */
	public Expression getResultExpression() {
		try {
			return elements.peek();
		}
		catch(EmptyStackException ex){
			throw new TransformationException("No result expression present", ex);
		}
	}
	
	private Expression processSubExpression(Expression expr, VarDeclList vars) {
		knownVariables.push(vars);
		Expression res = processSubExpression(expr);
		knownVariables.pop();
		return res;
	}
	
	private Expression processSubExpression(Expression expr, VarDecl var) {
		return processSubExpression(expr, new VarDeclList(var));
	}
	
	private Expression processSubExpression(Expression expr) {
		int elemSize = elements.size();
		expr.processWithVisitor(this);
		if(elemSize+1 != elements.size()){
			throw new TransformationException(
					"A subexpression killed the expression stack!\n"
							+ expr.toString());
		}
		return elements.pop();
	}
	
	private Expression[] processSubExpressionArray(Expression[] exps){
		Expression[] newExps = new Expression[exps.length];
		for(int i = 0; i < exps.length; i++){
			newExps[i] = processSubExpression(exps[i]);
		}
		return newExps;
	}
	
	private VarDecl processVarDecl(VarDecl vd){
		return new VarDecl(vd.name(), mc.mapType(vd.type()));
	}
	
	private VarDeclList processVarDeclList(VarDeclList varList){
		VarDeclList list = new VarDeclList(varList.allHaveSameType());
		for (VarDecl varDecl : varList) {
			list.add(processVarDecl(varDecl));
		}
		return list;
	}
	
	private VarInitializer processVarInitializer(
			VarInitializer accuInitializer, Expression exp) {
		try {
			return new VarInitializer(accuInitializer.name(),
					mc.mapType(accuInitializer.type()),
					exp);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("VarInitializer", ex);
		}
	}
	
	private MState processState(MState state) {
		return state;
	}
	
	protected void copyExpressionDetails(Expression from, Expression to){
		to.setIsPre(false);
		to.setSourcePosition(from.getSourcePosition());
	}
	
	private VarDecl getSelfDef(String name){
		if (selfVariables == null){
			return null;
		}
		
		for(int i = 0; i < selfVariables.size(); i++){
			if(selfVariables.varDecl(i).name().equals(name)){
				return selfVariables.varDecl(i);
			}
		}
		return null;
	}
	
	/**
	 * Returns the most recently declared {@link VarDecl} of the variable with
	 * the given {@code name}.
	 * 
	 * @param name name of the declared variable
	 * @return most recent definition of the variable, null if none exist
	 */
	private VarDecl getVarDef(String name){
		for (ListIterator<VarDeclList> it = knownVariables
				.listIterator(knownVariables.size()); it.hasPrevious();) {
			VarDeclList l = it.previous();
			for(int i = 0; i < l.size(); i++){
				if(l.varDecl(i).name().equals(name)){
					return l.varDecl(i);
				}
			}
		}
		return null;
	}
	
	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		//TODO incompatible with anything other than Class.allInstances (e.g. Association.allInstances)
		MClass expType = (MClass) mc.mapType(exp.getSourceType());
		
		if(selfVariables.size() < 1){
			throw new TransformationException("No self variable found for ExpAllInstances");
		}
		VarDecl selfDecl = selfVariables.varDecl(0);
		Expression self = new ExpVariable(selfDecl.name(), selfDecl.type());
		
		MClass snapshotClass = model.getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
		Expression snapshot;
		
		switch (type) {
		case CLASSINVARIANT:
		case OPERATION:
		case SOIL:
			// navigate from self to snapshot, self = random object in correct snapshot
			MClass snapElement = model.getClass(FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME);
			MAssociation assoc = model.getAssociation(FilmstripModelConstants.SNAPSHOTELEMENT_ASSOCNAME);
			
			MAssociationEnd sourceEnd = assoc.associationEndsAt(snapElement).iterator().next();
			MAssociationEnd destEnd = assoc.associationEndsAt(snapshotClass).iterator().next();
			
			try {
				snapshot = new ExpNavigation(self, sourceEnd, destEnd, Collections.<Expression>emptyList());
			} catch (ExpInvalidException e1) {
				throw new TransformationException("ExpAllInstances: Unable to navigate to snapshot class", e1);
			}
			break;
		case PRECONDITION:
		case POSTCONDITION:
			// self is the operation call object
			String opName = (type == ExpressionType.POSTCONDITION && !exp.isPre()) ? "succ" : "pred";
			MOperation op = ((MClass) selfDecl.type()).operation(opName, true);
			
			try {
				snapshot = new ExpObjOp(op, new Expression[]{ self });
			} catch (ExpInvalidException e) {
				throw new TransformationException("ExpAllInstances: Unable to call " + StringUtil.inQuotes(opName)
						+ " on variable " + StringUtil.inQuotes(selfDecl.name())
						+ " of type " + StringUtil.inQuotes(selfDecl.type()), e);
			}
			break;
		default:
			throw new TransformationException("ExpAllInstances: Unkown expression type " + StringUtil.inQuotes(type));
		}
		
		// navigate from snapshot to expType
		MAssociation assoc2 = model.getAssociation(FilmstripModelConstants.makeSnapshotClsAssocName(expType.name()));
		
		MAssociationEnd sourceEnd2 = assoc2.associationEndsAt(snapshotClass).iterator().next();
		MAssociationEnd destEnd2 = assoc2.associationEndsAt(expType).iterator().next();
		
		Expression nav;
		try {
			nav = new ExpNavigation(snapshot, sourceEnd2, destEnd2, Collections.<Expression>emptyList());
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpAllInstances: Unable to navigate from snapshot to objects", ex);
		}
		
		if(type == ExpressionType.POSTCONDITION && exp.isPre()){
			nav = FilmstripUtil.handlePredSucc(nav, false, knownVariables);
		}
		
		copyExpressionDetails(exp, nav);
		elements.push(nav);
	}

	@Override
	public void visitBagLiteral(ExpBagLiteral exp) {
		Expression[] exps = processSubExpressionArray(exp.getElemExpr());
		
		ExpBagLiteral bagLiteralExp;
		try {
			bagLiteralExp = new ExpBagLiteral(exps);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpBagLiteral", ex);
		}
		
		copyExpressionDetails(exp, bagLiteralExp);
		elements.push(bagLiteralExp);
	}
	
	@Override
	public void visitConstBoolean(ExpConstBoolean exp) {
		ExpConstBoolean constBooleanExp = new ExpConstBoolean(exp.value());
		
		copyExpressionDetails(exp, constBooleanExp);
		elements.push(constBooleanExp);
	}

	@Override
	public void visitConstEnum(ExpConstEnum exp) {
		ExpConstEnum constEnumExp = new ExpConstEnum((EnumType) mc.mapType(exp.type()), exp.value());
		
		copyExpressionDetails(exp, constEnumExp);
		elements.push(constEnumExp);
	}

	@Override
	public void visitConstInteger(ExpConstInteger exp) {
		ExpConstInteger constIntegerExp = new ExpConstInteger(exp.value());
		
		copyExpressionDetails(exp, constIntegerExp);
		elements.push(constIntegerExp);
	}

	@Override
	public void visitConstReal(ExpConstReal exp) {
		ExpConstReal constRealExp = new ExpConstReal(exp.value());
		
		copyExpressionDetails(exp, constRealExp);
		elements.push(constRealExp);
	}

	@Override
	public void visitConstString(ExpConstString exp) {
		ExpConstString constStringExp = new ExpConstString(exp.value());
		
		copyExpressionDetails(exp, constStringExp);
		elements.push(constStringExp);
	}

	@Override
	public void visitConstUnlimitedNatural(
			ExpConstUnlimitedNatural expressionConstUnlimitedNatural) {
		ExpConstUnlimitedNatural constUnlimitedNaturalExp = new ExpConstUnlimitedNatural();
		
		copyExpressionDetails(expressionConstUnlimitedNatural, constUnlimitedNaturalExp);
		elements.push(constUnlimitedNaturalExp);
	}
	
	@Override
	public void visitEmptyCollection(ExpEmptyCollection exp) {
		ExpEmptyCollection emptyCollectionExp;
		try {
			emptyCollectionExp = new ExpEmptyCollection(mc.mapType(exp.type()));
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpEmptyCollection", ex);
		}
		
		copyExpressionDetails(exp, emptyCollectionExp);
		elements.push(emptyCollectionExp);
	}
	
	@Override
	public void visitIf(ExpIf exp) {
		Expression condition = processSubExpression(exp.getCondition());
		Expression thenExp = processSubExpression(exp.getThenExpression());
		Expression elseExp = processSubExpression(exp.getElseExpression());
		
		ExpIf ifExp;
		try {
			ifExp = new ExpIf(condition, thenExp, elseExp);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpIf", ex);
		}
		
		copyExpressionDetails(exp, ifExp);
		elements.push(ifExp);
	}
	
	@Override
	public void visitLet(ExpLet exp) {
		Expression varExp = processSubExpression(exp.getVarExpression());
		Type newType = mc.mapType(exp.getVarType());
		Expression inExp = processSubExpression(exp.getInExpression(),
				new VarDecl(exp.getVarname(), newType));
		
		ExpLet letExp;
		try {
			letExp = new ExpLet(exp.getVarname(), newType, varExp, inExp);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpLet", ex);
		}
		
		copyExpressionDetails(exp, letExp);
		elements.push(letExp);
	}
	
	@Override
	public void visitObjRef(ExpObjRef exp) {
		// cannot occur
	}
	
	@Override
	public void visitObjOp(ExpObjOp exp) {
		Expression[] exps = processSubExpressionArray(exp.getArguments());
		MOperation op = mc.mapClass(exp.getOperation().cls()).operation(exp.getOperation().name(), true);
		if(op == null){
			throw new TransformationException("Could not find operation "
					+ StringUtil.inQuotes(exp.getOperation().name())
					+ " in new model class "
					+ StringUtil.inQuotes(exp.getOperation().cls().name()));
		}
		
		if(exp.isPre()){
			exps[0] = FilmstripUtil.handlePredSucc(exps[0], true, knownVariables);
		}
		
		Expression objOpExp;
		try {
			objOpExp = new ExpObjOp(op, exps);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpObjOp", ex);
		}
		
		copyExpressionDetails(exp, objOpExp);
		if(exp.isPre()){
			objOpExp = FilmstripUtil.handlePredSucc(objOpExp, false, knownVariables);
		}
		
		elements.push(objOpExp);
	}
	
	@Override
	public void visitOrderedSetLiteral(ExpOrderedSetLiteral exp) {
		Expression[] exps = processSubExpressionArray(exp.getElemExpr());
		
		ExpOrderedSetLiteral orderedSetLiteralExp;
		try {
			orderedSetLiteralExp = new ExpOrderedSetLiteral(exps);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpOrderedSetLiteral", ex);
		}
		
		copyExpressionDetails(exp, orderedSetLiteralExp);
		elements.push(orderedSetLiteralExp);
	}
	
	@Override
	public void visitWithValue(ExpressionWithValue exp) {
		// cannot occur
	}
	
	@Override
	public void visitSequenceLiteral(ExpSequenceLiteral exp) {
		Expression[] exps = processSubExpressionArray(exp.getElemExpr());
		
		ExpSequenceLiteral sequenceLiteralExp;
		try {
			sequenceLiteralExp = new ExpSequenceLiteral(exps);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpSequenceLiteral", ex);
		}
		
		copyExpressionDetails(exp, sequenceLiteralExp);
		elements.push(sequenceLiteralExp);
	}

	@Override
	public void visitSetLiteral(ExpSetLiteral exp) {
		Expression[] exps = processSubExpressionArray(exp.getElemExpr());
		
		ExpSetLiteral setLiteralExp;
		try {
			setLiteralExp = new ExpSetLiteral(exps);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpSetLiteral", ex);
		}
		
		copyExpressionDetails(exp, setLiteralExp);
		elements.push(setLiteralExp);
	}
	
	@Override
	public void visitTupleLiteral(ExpTupleLiteral exp) {
		ExpTupleLiteral.Part[] parts = new ExpTupleLiteral.Part[exp.getParts().length];
		for(ExpTupleLiteral.Part p : exp.getParts()){
			p = new ExpTupleLiteral.Part(p.getName(),
					processSubExpression(p.getExpression()),
					mc.mapType(p.getType()));
		}
		
		ExpTupleLiteral tupleLiteralExp = new ExpTupleLiteral(parts);

		copyExpressionDetails(exp, tupleLiteralExp);
		elements.push(tupleLiteralExp);
	}
	
	@Override
	public void visitUndefined(ExpUndefined exp) {
		ExpUndefined undefinedExp = new ExpUndefined(mc.mapType(exp.type()));
		
		copyExpressionDetails(exp, undefinedExp);
		elements.push(undefinedExp);
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		Expression variableExp;
		switch (type) {
		case CLASSINVARIANT:
		case OPERATION:
			VarDecl def = getVarDef(exp.getVarname());
			if(def == null){
				def = getSelfDef(exp.getVarname());
			}
			if(def == null){
				throw new TransformationException("Unknown variable "
						+ StringUtil.inQuotes(exp.getVarname()) + " encountered");
			}
			variableExp = new ExpVariable(def.name(), def.type());
			break;
		case SOIL:
			variableExp = new ExpVariable(exp.getVarname(), mc.mapType(exp.type()));
			break;
		case PRECONDITION:
		case POSTCONDITION:
			VarDecl var = getVarDef(exp.getVarname());
			if(var != null){
				variableExp = new ExpVariable(var.name(), var.type());
			}
			else if((var = getSelfDef(exp.getVarname())) != null){
				MClass t = (MClass) var.type();
				MAttribute a = t.attribute(FilmstripModelConstants.OPC_SELF_VARNAME, true);
				variableExp = new ExpVariable(FilmstripModelConstants.OPC_SELF_VARNAME, a.type());
				
				if(type == ExpressionType.POSTCONDITION){
					variableExp = FilmstripUtil.handlePredSucc(variableExp, false, knownVariables);
				}
			}
			else {
				MAttribute aSelfAttr = src.attribute(FilmstripModelConstants.OPC_SELF_VARNAME, true);
				
				MAttribute attr = null;
				boolean aSelfVar = false;
				// handle special "result" var
				if(exp.getVarname().equals("result")){
					attr = src.attribute(
							FilmstripModelConstants.OPC_RETURNVALUE_VARNAME,
							false);
					aSelfVar = false;
				}
				else {
					// operation parameters
					attr = src.attribute(exp.getVarname(), false);
					
					if(attr == null){
						// properties of "self" (aSelf)
						attr = ((MClass) aSelfAttr.type()).attribute(exp.getVarname(), true);
						aSelfVar = true;
					}
				}
				
				if(attr == null){
					throw new TransformationException("Unknown variable "
							+ StringUtil.inQuotes(exp.getVarname()));
				}
				
				if(aSelfVar){
					variableExp = new ExpVariable(FilmstripModelConstants.OPC_SELF_VARNAME, aSelfAttr.type());
					if(type == ExpressionType.POSTCONDITION){
						variableExp = FilmstripUtil.handlePredSucc(variableExp, false, knownVariables);
					}
					variableExp = new ExpAttrOp(attr, variableExp);
				}
				else {
					variableExp = new ExpVariable(exp.getVarname(), attr.type());
					if(type == ExpressionType.POSTCONDITION){
						variableExp = FilmstripUtil.handlePredSucc(variableExp, false, knownVariables);
					}
				}
			}
			break;
		default:
			throw new TransformationException("Unkown expression type " + StringUtil.inQuotes(type));
		}
		
		copyExpressionDetails(exp, variableExp);
		elements.push(variableExp);
	}
	
	@Override
	public void visitObjectByUseId(ExpObjectByUseId expObjectByUseId) {
		Expression idExp = processSubExpression(expObjectByUseId.getIdExpression());
		
		ExpObjectByUseId objectByUseIdExp;
		try {
			objectByUseIdExp = new ExpObjectByUseId((MClass) mc.mapType(expObjectByUseId.getSourceType()), idExp);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpObjectByUseId", ex);
		}
		
		copyExpressionDetails(expObjectByUseId, objectByUseIdExp);
		elements.push(objectByUseIdExp);
	}
	
	@Override
	public void visitAny(ExpAny exp) {
		visitQuery(exp);
	}

	@Override
	public void visitAsType(ExpAsType exp) {
		Expression expr = processSubExpression(exp.getSourceExpr());
		ExpAsType asTypeExp;
		try {
			asTypeExp = new ExpAsType(expr, mc.mapType(exp.getTargetType()));
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpAsType", ex);
		}
		
		copyExpressionDetails(exp, asTypeExp);
		elements.push(asTypeExp);
	}

	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		MAttribute attr = mc.mapAttribute(exp.attr());
		Expression expr = processSubExpression(exp.objExp());
		if(exp.isPre()){
			expr = FilmstripUtil.handlePredSucc(expr, true, knownVariables);
		}
		
		Expression attrOpExp = new ExpAttrOp(attr, expr);
		if(exp.isPre()){
			attrOpExp = FilmstripUtil.handlePredSucc(attrOpExp, false, knownVariables);
		}
		
		copyExpressionDetails(exp, attrOpExp);
		elements.push(attrOpExp);
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
	public void visitExists(ExpExists exp) {
		visitQuery(exp);
	}

	@Override
	public void visitForAll(ExpForAll exp) {
		visitQuery(exp);
	}

	@Override
	public void visitIsKindOf(ExpIsKindOf exp) {
		Expression expr = processSubExpression(exp.getSourceExpr());
		
		ExpIsKindOf isKindOfExp;
		try {
			isKindOfExp = new ExpIsKindOf(expr, mc.mapType(exp.getTargetType()));
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpIsKindOf", ex);
		}
		
		copyExpressionDetails(exp, isKindOfExp);
		elements.push(isKindOfExp);
	}

	@Override
	public void visitIsTypeOf(ExpIsTypeOf exp) {
		Expression expr = processSubExpression(exp.getSourceExpr());
		
		ExpIsTypeOf isTypeOfExp;
		try {
			isTypeOfExp = new ExpIsTypeOf(expr, mc.mapType(exp.getTargetType()));
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpIsTypeOf", ex);
		}
		
		copyExpressionDetails(exp, isTypeOfExp);
		elements.push(isTypeOfExp);
	}

	@Override
	public void visitIsUnique(ExpIsUnique exp) {
		visitQuery(exp);
	}

	@Override
	public void visitIterate(ExpIterate exp) {
		VarDeclList vars = processVarDeclList(exp.getVariableDeclarations());
		VarDeclList varsWithResultVar = processVarDeclList(exp.getVariableDeclarations());
		VarInitializer initializer = processVarInitializer(exp.getAccuInitializer(),
				processSubExpression(exp.getAccuInitializer().initExpr()));
		varsWithResultVar.add(new VarDecl(initializer.name(), initializer.type()));
		Expression qryExp = processSubExpression(exp.getQueryExpression(), varsWithResultVar);
		Expression rangeExpr = processSubExpression(exp.getRangeExpression());
		
		ExpIterate iterateExp;
		try {
			iterateExp = new ExpIterate(vars, initializer, rangeExpr, qryExp);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpIterate", ex);
		}
		
		copyExpressionDetails(exp, iterateExp);
		elements.push(iterateExp);
	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		MNavigableElement srcElem = mc.mapNavigableElement(exp.getSource());
		MNavigableElement destElem = mc.mapNavigableElement(exp.getDestination());
		Expression objExpr = processSubExpression(exp.getObjectExpression());
		Expression[] qualifiers = processSubExpressionArray(exp.getQualifierExpression());
		if(exp.isPre()){
			objExpr = FilmstripUtil.handlePredSucc(objExpr, true, knownVariables);
		}
		
		Expression navigationExp;
		try {
			navigationExp = new ExpNavigation(objExpr, srcElem, destElem, Arrays.asList(qualifiers));
			if(exp.isPre()){
				navigationExp = FilmstripUtil.handlePredSucc(navigationExp, false, knownVariables);
			}
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpNavigation", ex);
		}
		
		copyExpressionDetails(exp, navigationExp);
		elements.push(navigationExp);
	}

	@Override
	public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
		MNavigableElement destElem = mc.mapNavigableElement(exp.getDestination());
		Type type = mc.mapType(exp.type());
		Expression objExpr = processSubExpression(exp.getObjectExpression());
		
		if(exp.isPre()){
			objExpr = FilmstripUtil.handlePredSucc(objExpr, true, knownVariables);
		}
		
		Expression navigationExp = new ExpNavigationClassifierSource(type, objExpr, destElem);
		
		if(exp.isPre()){
			navigationExp = FilmstripUtil.handlePredSucc(navigationExp, false, knownVariables);
		}
		
		copyExpressionDetails(exp, navigationExp);
		elements.push(navigationExp);
	}
	
	@Override
	public void visitObjAsSet(ExpObjAsSet exp) {
		Expression objExpr = processSubExpression(exp.getObjectExpression());
		
		ExpObjAsSet objAsSetExp = new ExpObjAsSet(objExpr);
		
		copyExpressionDetails(exp, objAsSetExp);
		elements.push(objAsSetExp);
	}

	@Override
	public void visitOne(ExpOne exp) {
		visitQuery(exp);
	}

	@Override
	public void visitQuery(ExpQuery exp) {
		visitQuery(exp, exp.getClass());
	}

	private void visitQuery(ExpQuery exp, Class<? extends ExpQuery> cls) {
		ExpQuery queryExp;
		try {
			Expression qryExpression;
			Expression rangeExpr = processSubExpression(exp.getRangeExpression());
			if(exp.getVariableDeclarations().size() > 1){
				VarDeclList vars = processVarDeclList(exp.getVariableDeclarations());
				qryExpression = processSubExpression(exp.getQueryExpression(), vars);
				
				// create class instance with multiple var declarations
				queryExp = cls.getConstructor(VarDeclList.class, Expression.class, Expression.class)
						.newInstance(vars, rangeExpr, qryExpression);
			}
			else {
				VarDecl var = processVarDecl(exp.getVariableDeclarations().varDecl(0));
				qryExpression = processSubExpression(exp.getQueryExpression(), new VarDeclList(var));
				
				// create class instance with one var declaration
				queryExp = cls.getConstructor(VarDecl.class, Expression.class, Expression.class)
						.newInstance(var, rangeExpr, qryExpression);
			}

		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new TransformationException("Could not visit query expression " + StringUtil.inQuotes(cls.getSimpleName()), e);
		}
		
		copyExpressionDetails(exp, queryExp);
		elements.push(queryExp);
	}

	@Override
	public void visitReject(ExpReject exp) {
		visitQuery(exp);
	}

	@Override
	public void visitSelect(ExpSelect exp) {
		visitQuery(exp);
	}

	@Override
	public void visitSortedBy(ExpSortedBy exp) {
		visitQuery(exp);
	}
	
	@Override
	public void visitStdOp(ExpStdOp exp) {
		if(exp.opname().equals("oclIsNew")){
			visitOclIsNew(exp);
			return;
		}
		
		Expression[] exps = processSubExpressionArray(exp.args());
		
		ExpStdOp stdOpExp;
		try {
			stdOpExp = ExpStdOp.create(exp.opname(), exps);
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpStdOp", ex);
		}
		
		copyExpressionDetails(exp, stdOpExp);
		elements.push(stdOpExp);
	}

	private void visitOclIsNew(ExpStdOp exp) {
		Expression srcExp = processSubExpression(exp.args()[0]);
		
		srcExp = FilmstripUtil.handlePredSucc(srcExp, true, knownVariables);
		
		ExpStdOp stdOpExp;
		try {
			stdOpExp = ExpStdOp.create("oclIsUndefined", new Expression[]{ srcExp });
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpStdOp", ex);
		}
		
		copyExpressionDetails(exp, stdOpExp);
		elements.push(stdOpExp);
	}

	@Override
	public void visitTupleSelectOp(ExpTupleSelectOp exp) {
		TupleType.Part part = new TupleType.Part(exp.getPart().getPosition(),
				exp.getPart().name(), mc.mapType(exp.getPart().type()));
		Expression tupleExp = processSubExpression(exp.getTupleExp());
		
		ExpTupleSelectOp tupleSelectOpExp = new ExpTupleSelectOp(part, tupleExp);
		
		copyExpressionDetails(exp, tupleSelectOpExp);
		elements.push(tupleSelectOpExp);
	}

	@Override
	public void visitClosure(ExpClosure exp) {
		visitQuery(exp);
	}

	@Override
	public void visitOclInState(ExpOclInState expOclInState) {
		Expression rangeExpr = processSubExpression(expOclInState.getSourceExpr());
		MState state = processState(expOclInState.getState());
		
		ExpOclInState oclInStateExp = new ExpOclInState(rangeExpr, state);
		
		copyExpressionDetails(expOclInState, oclInStateExp);
		elements.push(oclInStateExp);
	}

	@Override
	public void visitVarDeclList(VarDeclList varDeclList) {
		throw new TransformationException("Cannot visit VarDeclList directly");
	}

	@Override
	public void visitVarDecl(VarDecl varDecl) {
		throw new TransformationException("Cannot visit VarDecl directly");
	}

	@Override
	public void visitSelectByKind(ExpSelectByKind expSelectByKind) {
		ExpSelectByKind selectByKind;
		try {
			selectByKind = new ExpSelectByKind(
					processSubExpression(expSelectByKind.getSourceExpression()),
					mc.mapType(expSelectByKind.type()));
		} catch (SemanticException e) {
			throw new TransformationException("ExpSelectByKind", e);
		}
		
		copyExpressionDetails(expSelectByKind, selectByKind);
		elements.push(selectByKind);
	}

	@Override
	public void visitExpSelectByType(ExpSelectByType expSelectByType) {
		ExpSelectByKind selectByType;
		try {
			selectByType = new ExpSelectByType(
					processSubExpression(expSelectByType.getSourceExpression()),
					mc.mapType(expSelectByType.type()));
		} catch (SemanticException e) {
			throw new TransformationException("ExpSelectByType", e);
		}
		
		copyExpressionDetails(expSelectByType, selectByType);
		elements.push(selectByType);
	}

	@Override
	public void visitRange(ExpRange exp) {
		ExpRange rangeExp = new ExpRange(processSubExpression(exp.getStart()),
				processSubExpression(exp.getEnd()));
		
		copyExpressionDetails(exp, rangeExp);
		elements.push(rangeExp);
	}
	
}
