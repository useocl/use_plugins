package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.HashMap;

import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpBagLiteral;
import org.tzi.use.uml.ocl.expr.ExpCollect;
import org.tzi.use.uml.ocl.expr.ExpCollectionLiteral;
import org.tzi.use.uml.ocl.expr.ExpConstBoolean;
import org.tzi.use.uml.ocl.expr.ExpConstEnum;
import org.tzi.use.uml.ocl.expr.ExpConstInteger;
import org.tzi.use.uml.ocl.expr.ExpConstReal;
import org.tzi.use.uml.ocl.expr.ExpConstString;
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
import org.tzi.use.uml.ocl.expr.ExpObjAsSet;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpOne;
import org.tzi.use.uml.ocl.expr.ExpQuery;
import org.tzi.use.uml.ocl.expr.ExpReject;
import org.tzi.use.uml.ocl.expr.ExpSelect;
import org.tzi.use.uml.ocl.expr.ExpSequenceLiteral;
import org.tzi.use.uml.ocl.expr.ExpSetLiteral;
import org.tzi.use.uml.ocl.expr.ExpSortedBy;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral;
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpUndefined;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.ExpressionWithValue;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

/**
 * creates Kodkod formula for all invariants
 * @author Torsten Humann
 * 
 */
public class OCLFormulas {
	
	private static enum Exp {
		ALLINSTANCES,
		ANY,
		ASTYPE,
		ATTROP,
		BAGLITERAL,
		COLLECT,
		COLLECTIONLITERAL,
		CONSTBOOLEAN,
		CONSTENUM,
		CONSTINTEGER,
		CONSTREAL,
		CONSTSTRING,
		EMPTYCOLLECTION,
		EXISTS,
		FORALL,
		IF,
		ISKINDOF,
		ISTYPEOF,
		ISUNIQUE,
		ITERATE,
		LET,
		NAVIGATION,
		OBJASSET,
		OBJOP,
		ONE,
		QUERY,
		REJECT,
		WITHVALUE,
		SELECT,
		SEQUENCELITERAL,
		SETLITERAL,
		SORTEDBY,
		STDOP,
		TUPLELITERAL,
		TUPLESELECTOP,
		UNDEFINED,
		VARIABLE,
		DEFAULT
	}
	
	private SetKodkodStruc skks;
	
	//needed variables to save an expression, intexpression or
	//variable with its notation in the USE model
	private HashMap<String, Expression> exps = new HashMap<String, Expression>();
	private HashMap<String, IntExpression> intExps = new HashMap<String, IntExpression>();
	private HashMap<String, Variable> vars = new HashMap<String, Variable>();
	//unsolve and negExp if invariant cannot be translated
	//with expression that was the reason
	//variables are set in methods of this class
	private Boolean unsolve = false;
	private org.tzi.use.uml.ocl.expr.Expression negExp = null;
	
	public OCLFormulas(SetKodkodStruc setKod){
		skks = setKod;
	}
	
	//building of formula starts here
	//formula includes flag of invariant (positive, negative
	//or deactivated)
	public final Formula buildOCLForm(Formula form, ArrayList<OCLInvar> oclInv){
		for(int i = 0; i < oclInv.size(); i++){
			if(!oclInv.get(i).getFlag().equals(OCLInvar.Flag.d)){
				unsolve = false;
				negExp = null;
				exps.clear();
				intExps.clear();
				vars.clear();
				Formula newForm = Formula.TRUE;
				MClassInvariant claInv = oclInv.get(i).getMClassInvariant();
				switch(getExpressionType(claInv.expandedExpression())){
				case FORALL:
					newForm = expForAllForm(claInv);
					break;
				case DEFAULT:
					if(unsolve==false){
						unsolve = true;
						negExp = claInv.expandedExpression();
					}
					break;
				default:
					if(unsolve==false){
						unsolve = true;
						negExp = claInv.expandedExpression();
					}		
				}
				
				if(unsolve){
					skks.addNoTrans(claInv, negExp);
					form = form.and(Formula.TRUE);
				}else{
					switch(oclInv.get(i).getFlag()){
					case p:
						form = form.and(newForm);
						break;
					case n:
						form = form.and(newForm.not());
						break;
					case d:
						form = form.and(Formula.TRUE);
						break;
					}
				}
			}else{
				form = form.and(Formula.TRUE);
			}
		
		}
		
		return form;
	}
	
	//creates first forAll formula to translate the context of invariant
	private Formula expForAllForm(MClassInvariant claInv){
		
		final Expression bodyExpression = getExpIsTypeOf(claInv);;
		Formula bodyFormula = null;
		
		vars.put(claInv.var(), Variable.unary(claInv.var()));
		
		bodyFormula = getKodkodFormula(claInv.bodyExpression());
		if(bodyFormula.equals(Formula.TRUE)){
			bodyFormula = null;
		}
		
		if(bodyFormula!=null && bodyExpression!=null){
			return getKodkodFormula(claInv.bodyExpression()).forAll(vars.get(claInv.var()).oneOf(bodyExpression));
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = claInv.expandedExpression();
			}
			return Formula.TRUE;
		}
	}
	
	//creates Kodkod formula for OCL expression forAll
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	private Formula forAllForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula bodyFormula = null;
		Expression forAllExpression = null;
		Decls varExpression = null;
		
		ExpForAll eFA = (ExpForAll) expr;
		
		//saves variable for using it later by OCL notation in other methods
		for(int i = 0; i < eFA.getVariableDeclarations().size(); i++){
			vars.put(eFA.getVariableDeclarations().varDecl(i).name(), Variable.unary(eFA.getVariableDeclarations().varDecl(i).name()));
		}
		
		forAllExpression = getKodkodExpression(eFA.getRangeExpression());
		bodyFormula = getKodkodFormula(eFA.getQueryExpression());
		if(bodyFormula.equals(Formula.TRUE)){
			bodyFormula = null;
		}
		
		if(forAllExpression != null && bodyFormula != null){
			for(int i = 0; i < eFA.getVariableDeclarations().size(); i++){
				if(i == 0){
					varExpression = vars.get(eFA.getVariableDeclarations().varDecl(i).name()).oneOf(forAllExpression);
				}else{
					varExpression = varExpression.and(vars.get(eFA.getVariableDeclarations().varDecl(i).name()).oneOf(forAllExpression));
				}
			}
			return bodyFormula.forAll(varExpression);
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return Formula.TRUE;
		}
		
	}
	
	//creates Kodkod formula for OCL expression exists
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	private Formula existsForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula bodyFormula = null;
		Expression existsExpression = null;
		Decls varExpression = null;
		
		ExpExists eE = (ExpExists) expr;
		
		//saves variable for using it later by OCL notation in other methods
		for(int i = 0; i < eE.getVariableDeclarations().size(); i++){
			vars.put(eE.getVariableDeclarations().varDecl(i).name(), Variable.unary(eE.getVariableDeclarations().varDecl(i).name()));
		}
		
		existsExpression = getKodkodExpression(eE.getRangeExpression());
		bodyFormula = getKodkodFormula(eE.getQueryExpression());
		if(bodyFormula.equals(Formula.TRUE)){
			bodyFormula = null;
		}
		
		if(existsExpression != null && bodyFormula != null){
			for(int i = 0; i < eE.getVariableDeclarations().size(); i++){
				if(i == 0){
					varExpression = vars.get(eE.getVariableDeclarations().varDecl(i).name()).oneOf(existsExpression);
				}else{
					varExpression = varExpression.and(vars.get(eE.getVariableDeclarations().varDecl(i).name()).oneOf(existsExpression));
				}
			}
			return bodyFormula.forSome(varExpression);
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return Formula.TRUE;
		}
		
	}
	
	//creates Kodkod formula for OCL expression isTypeOf
	//using needed variable by notation in OCL expression
	//and relation from SetKodkodStruc object
	private Formula isTypeOfForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		ExpIsTypeOf eITO = (ExpIsTypeOf) expr;
		
		String tarTyp = eITO.toString().substring(eITO.toString().indexOf(".oclIsTypeOf(") + 13);
		tarTyp = tarTyp.substring(0, tarTyp.indexOf(")"));
		
		String varName = eITO.toString().substring(0, eITO.toString().indexOf(".oclIsTypeOf("));
		
		if(vars.get(varName)!=null && skks.getClassRelation(tarTyp)!=null){
			return vars.get(varName).in(skks.getClassRelation(tarTyp));
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return Formula.TRUE;
		}
		
	}
	
	//creates Kodkod formula for OCL expressions or, xor, and,
	//not, implies, excludes, excludesAll, includes, includesAll,
	//isDefined, isEmpty, notEmpty, =, <>, >, >=, <, <=
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	//looks if parts are integer, boolean or sets for
	//to get the right formula parts and expressions
	private Formula stdOpForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		boolean intExp = false;
		
		ExpStdOp eSO = (ExpStdOp) expr;
		
		Formula eSOForms[] = new Formula[eSO.args().length];
		Formula stdOpForm = Formula.TRUE;
		Expression eSOExps[] = new Expression[eSO.args().length];
		IntExpression eSOIntExps[] = new IntExpression[eSO.args().length];
		
		for(int i = 0; i < eSO.args().length; i++){
			eSOExps[i] = getKodkodExpression(eSO.args()[i], false);
			eSOIntExps[i] = getKodkodIntExpression(eSO.args()[i], false);
			eSOForms[i] = getKodkodFormula(eSO.args()[i], false);
			if(eSOForms[i].equals(Formula.TRUE)){
				eSOForms[i] = null;
			}
			//special cases if ocl expression is a variable, stdOperation,
			//attribute type or if-expression because of difference
			//in types (integer, boolean or set)
			switch(getExpressionType(eSO.args()[i])){
			case VARIABLE:
				eSOForms[i] = null;
				ExpVariable eV = (ExpVariable) eSO.args()[i];
				if(checkIntExpr(eSO.opname()) && eV.type().isInteger()){
					eSOExps[i] = null;
					eSOIntExps[i] = intExps.get(getExpVariableName(eSO.args()[i]));
				}else{
					if(vars.containsKey(getExpVariableName(eSO.args()[i]))){
						eSOExps[i] = vars.get(getExpVariableName(eSO.args()[i]));
					}else{
						eSOExps[i] = exps.get(getExpVariableName(eSO.args()[i]));
					}
					eSOIntExps[i] = null;
				}
				break;
			case STDOP:
				ExpStdOp eeSO = (ExpStdOp) eSO.args()[i];
				if(checkStdExp(eeSO.opname())){
					if(checkMathExpr(eeSO.opname())){
						if(eeSO.args()[0].type().isInteger()){
							eSOForms[i] = null;
							eSOExps[i] = null;
							eSOIntExps[i] = getIntExpStdOp(eSO.args()[i]);
						}else{
							if(eeSO.opname().equals("-") && !eeSO.args()[0].type().isReal()){
								eSOForms[i] = null;
								eSOExps[i] = getExpStdOp(eSO.args()[i]);
								eSOIntExps[i] = null;
							}
						}
					}else if(checkIntExpr(eSO.opname())){
						eSOForms[i] = null;
						eSOExps[i] = null;
						if(getExpStdOp(eSO.args()[i])!=null){
							eSOIntExps[i] = getExpStdOp(eSO.args()[i]).count();
						}else{
							eSOIntExps[i] = null;
						}
					}else{
						eSOForms[i] = null;
						eSOExps[i] = getExpStdOp(eSO.args()[i]);
						eSOIntExps[i] = null;
					}
				}else{
					eSOForms[i] = stdOpForm(eSO.args()[i]);
					eSOExps[i] = null;
					eSOIntExps[i] = null;
				}
				break;
			case ATTROP:
				eSOForms[i] = null;
				ExpAttrOp eAO = (ExpAttrOp) eSO.args()[i];
				if(checkIntExpr(eSO.opname()) && eAO.type().isInteger()){
					eSOExps[i] = null;
					if(getExpAttrOp(eSO.args()[i])!=null){
						eSOIntExps[i] = getExpAttrOp(eSO.args()[i]).count();
					}else{
						eSOIntExps[i] = null;
					}
				}else{
					eSOExps[i] = getExpAttrOp(eSO.args()[i]);
					eSOIntExps[i] = null;
				}
				break;
			case IF:
				eSOForms[i] = null;
				ExpIf eI = (ExpIf) eSO.args()[i];
				if(eI.type().isInteger()){
					eSOExps[i] = null;
					eSOIntExps[i] = getIntExpIf(eSO.args()[i]);
				}else{
					eSOExps[i] = getExpIf(eSO.args()[i]);
					eSOIntExps[i] = null;
				}
				break;
			case DEFAULT:
				break;
			}
			if(eSOIntExps[i] != null){
				intExp = true;
			}
		}
		
		//choosing the right translation rule by opname
		if(eSO.opname().equals("or")){
			if((eSOForms[0]==null && eSOExps[0]==null) || (eSOForms[1]==null && eSOExps[1]==null)){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				if(eSOForms[0] == null){
					if(eSOForms[1] == null){
						stdOpForm = eSOExps[0].eq(skks.getBoolRelationTrue()).or(eSOExps[1].eq(skks.getBoolRelationTrue()));
					}else{
						stdOpForm = eSOExps[0].eq(skks.getBoolRelationTrue()).or(eSOForms[1]);
					}
				}else{
					if(eSOForms[1] == null){
						stdOpForm = eSOForms[0].or(eSOExps[1].eq(skks.getBoolRelationTrue()));
					}else{
						stdOpForm = eSOForms[0].or(eSOForms[1]);
					}
				}
			}
		}else if(eSO.opname().equals("xor")){
			if((eSOForms[0]==null && eSOExps[0]==null) || (eSOForms[1]==null && eSOExps[1]==null)){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				if(eSOForms[0] == null){
					if(eSOForms[1] == null){
						stdOpForm = (eSOExps[0].eq(skks.getBoolRelationTrue()).or(eSOExps[1].eq(skks.getBoolRelationTrue()))).and((eSOExps[0].eq(skks.getBoolRelationTrue()).not().or(eSOExps[1].eq(skks.getBoolRelationTrue()).not())));
					}else{
						stdOpForm = (eSOExps[0].eq(skks.getBoolRelationTrue()).or(eSOForms[1])).and((eSOExps[0].eq(skks.getBoolRelationTrue()).not().or(eSOForms[1].not())));
					}
				}else{
					if(eSOForms[1] == null){
						stdOpForm = (eSOForms[0].or(eSOExps[1].eq(skks.getBoolRelationTrue()))).and((eSOForms[0].not().or(eSOExps[1].eq(skks.getBoolRelationTrue()).not())));
					}else{
						stdOpForm = (eSOForms[0].or(eSOForms[1])).and((eSOForms[0].not().or(eSOForms[1].not())));
					}
				}
			}
		}else if(eSO.opname().equals("and")){
			if((eSOForms[0]==null && eSOExps[0]==null) || (eSOForms[1]==null && eSOExps[1]==null)){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				if(eSOForms[0] == null){
					if(eSOForms[1] == null){
						stdOpForm = eSOExps[0].eq(skks.getBoolRelationTrue()).and(eSOExps[1].eq(skks.getBoolRelationTrue()));
					}else{
						stdOpForm = eSOExps[0].eq(skks.getBoolRelationTrue()).and(eSOForms[1]);
					}
				}else{
					if(eSOForms[1] == null){
						stdOpForm = eSOForms[0].and(eSOExps[1].eq(skks.getBoolRelationTrue()));
					}else{
						stdOpForm = eSOForms[0].and(eSOForms[1]);
					}
				}
			}
		}else if(eSO.opname().equals("implies")){
			if((eSOForms[0]==null && eSOExps[0]==null) || (eSOForms[1]==null && eSOExps[1]==null)){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				if(eSOForms[0] == null){
					if(eSOForms[1] == null){
						stdOpForm = eSOExps[0].eq(skks.getBoolRelationFalse()).not().implies(eSOExps[1].eq(skks.getBoolRelationTrue()));
					}else{
						stdOpForm = eSOExps[0].eq(skks.getBoolRelationFalse()).not().implies(eSOForms[1]);
					}
				}else{
					if(eSOForms[1] == null){
						stdOpForm = eSOForms[0].implies(eSOExps[1].eq(skks.getBoolRelationTrue()));
					}else{
						stdOpForm = eSOForms[0].implies(eSOForms[1]);
					}
				}
			}
		}else if(eSO.opname().equals("not")){
			if(eSOForms[0]==null && eSOExps[0]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				if(eSOForms[0] == null){
					stdOpForm = eSOExps[0].eq(skks.getBoolRelationFalse());
				}else{
					stdOpForm = eSOForms[0].not();
				}
			}
		}else if(eSO.opname().equals("excludes")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpForm = eSOExps[1].in(eSOExps[0]).not();
			}
		}else if(eSO.opname().equals("excludesAll")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpForm = eSOExps[0].intersection(eSOExps[1]).no();
			}
		}else if(eSO.opname().equals("includes") || eSO.opname().equals("includesAll")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpForm = eSOExps[1].in(eSOExps[0]);
			}
		}else if(eSO.opname().equals("isDefined")){
			if(eSOExps[0]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpForm = eSOExps[0].some();
			}
		}else if(eSO.opname().equals("isEmpty")){
			if(eSOExps[0]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpForm = eSOExps[0].no();
			}
		}else if(eSO.opname().equals("notEmpty")){
			if(eSOExps[0]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpForm = eSOExps[0].some();
			}
		}else if(eSO.opname().equals("=")){
			if(intExp){
				if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
					stdOpForm = eSOIntExps[0].eq(eSOIntExps[1]);
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}else{
				if(eSOExps[0]!=null && eSOExps[1]!=null){
					if(eSO.args()[0].type().isString() || eSO.args()[0].type().isReal()){
						if(unsolve==false){
							unsolve = true;
							negExp = expr;
						}
					}else{
						stdOpForm = eSOExps[0].eq(eSOExps[1]);
					}
				}else if(eSOExps[0]!=null && eSOForms[1]!=null){
					stdOpForm = eSOExps[0].eq(skks.getBoolRelationTrue()).iff(eSOForms[1]);
				}else if(eSOForms[0]!=null && eSOExps[1]!=null){
					stdOpForm = eSOForms[0].iff(eSOExps[1].eq(skks.getBoolRelationTrue()));
				}else if(eSOForms[0]!=null && eSOForms[1]!=null){
					stdOpForm = eSOForms[0].iff(eSOForms[1]);
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}
		}else if(eSO.opname().equals("<>")){
			if(intExp){
				if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
					stdOpForm = eSOIntExps[0].eq(eSOIntExps[1]).not();
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}else{
				if(eSOExps[0]!=null && eSOExps[1]!=null){
					if(eSO.args()[0].type().isString() || eSO.args()[0].type().isReal()){
						if(unsolve==false){
							unsolve = true;
							negExp = expr;
						}
					}else{
						stdOpForm = eSOExps[0].eq(eSOExps[1]).not();
					}
				}else if(eSOExps[0]!=null && eSOForms[1]!=null){
					stdOpForm = eSOExps[0].eq(skks.getBoolRelationTrue()).iff(eSOForms[1]).not();
				}else if(eSOForms[0]!=null && eSOExps[1]!=null){
					stdOpForm = eSOForms[0].iff(eSOExps[1].eq(skks.getBoolRelationTrue())).not();
				}else if(eSOForms[0]!=null && eSOForms[1]!=null){
					stdOpForm = eSOForms[0].iff(eSOForms[1]).not();
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}
		}else if(eSO.opname().equals(">")){
			if(intExp){
				if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
					stdOpForm = eSOIntExps[0].gt(eSOIntExps[1]);
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}
		}else if(eSO.opname().equals("<")){
			if(intExp){
				if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
					stdOpForm = eSOIntExps[0].lt(eSOIntExps[1]);
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}
		}else if(eSO.opname().equals(">=")){
			if(intExp){
				if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
					stdOpForm = eSOIntExps[0].gte(eSOIntExps[1]);
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}
		}else if(eSO.opname().equals("<=")){
			if(intExp){
				if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
					stdOpForm = eSOIntExps[0].lte(eSOIntExps[1]);
				}else{
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}
			}
		}
		
		return stdOpForm;
		
	}
	
	//creates Kodkod formula for OCL expressions let
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	//saves let expression for using it later by OCL notation in other methods
	private Formula letForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		boolean intExp = false;
		
		Expression letExpression = null;
		IntExpression letIntExpression = null;
		
		ExpLet eL = (ExpLet) expr;
		
		letExpression = getKodkodExpression(eL.getVarExpression(), false);
		
		//special cases if ocl expression is of type integer
		//because Kodkod needs intExpression
		switch (getExpressionType(eL.getVarExpression())){
		case STDOP:
			ExpStdOp opNameCheck = (ExpStdOp) eL.getVarExpression();
			intExp = checkIntExpr(opNameCheck.opname());
			if(intExp){
				if(getExpStdOp(eL.getVarExpression())!=null){
					letIntExpression = getExpStdOp(eL.getVarExpression()).count();
				}
			}else{
				letExpression = getExpStdOp(eL.getVarExpression());
			}
			break;
		case IF:
			ExpIf eI = (ExpIf) eL.getVarExpression();
			intExp = eI.type().isInteger();
			if(intExp){
				letIntExpression = getIntExpIf(eL.getVarExpression());
			}else{
				letExpression = getExpIf(eL.getVarExpression());
			}
			break;
		case CONSTINTEGER:
			intExp = true;
			letIntExpression = getIntExpConstInteger(eL.getVarExpression());
			break;
		}
		
		//saves expression or intExpreesion for using it later by OCL notation in other methods 
		if(intExp){
			intExps.put(eL.getVarname(), letIntExpression);
		}else{
			exps.put(eL.getVarname(), letExpression);
		}
		
		return getKodkodFormula(eL.getInExpression());
			
	}
	
	//creates Kodkod formula for OCL expression one
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	private Formula oneForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula bodyFormula = null;
		Expression oneExpression = null;
		Decls varExpression = null;
		
		ExpOne eO = (ExpOne) expr;
		
		//saves variable for using it later by OCL notation in other methods
		for(int i = 0; i < eO.getVariableDeclarations().size(); i++){
			vars.put(eO.getVariableDeclarations().varDecl(i).name(), Variable.unary(eO.getVariableDeclarations().varDecl(i).name()));
		}
		
		oneExpression = getKodkodExpression(eO.getRangeExpression());
		bodyFormula = getKodkodFormula(eO.getQueryExpression());
		
		if(oneExpression != null && bodyFormula != null){
			for(int i = 0; i < eO.getVariableDeclarations().size(); i++){
				if(i == 0){
					varExpression = vars.get(eO.getVariableDeclarations().varDecl(i).name()).oneOf(oneExpression);
				}else{
					varExpression = varExpression.and(vars.get(eO.getVariableDeclarations().varDecl(i).name()).oneOf(oneExpression));
				}
			}
			return bodyFormula.comprehension(varExpression).one();
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return Formula.TRUE;
		}
		
	}
	
	//creates Kodkod formula for attribute expressions in OCL
	//parts of OCL expression call corresponding methods
	//to get needed expressions
	private Formula attrOpForm(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Expression objExp = null;
		
		ExpAttrOp eAO = (ExpAttrOp) expr;
		
		switch(getExpressionType(eAO.objExp())){
		case VARIABLE:
			if(vars.containsKey(getExpVariableName(eAO.objExp()))){
				objExp = vars.get(getExpVariableName(eAO.objExp()));
			}else{
				objExp = exps.get(getExpVariableName(eAO.objExp()));
			}
			break;
		case DEFAULT:
			break;
		}
		
		if(eAO.attr().type().isBoolean() && objExp!=null){
			return objExp.join(skks.getAttRelation(eAO.attr().owner().name() + "_-" + eAO.attr().name())).eq(skks.getBoolRelationTrue());
		}else{
			return Formula.TRUE;
		}
	}
	
	//creates Kodkod expression for OCL expression isTypeOf
	//returns relation of class
	private Expression getExpIsTypeOf(MClassInvariant claInv){
		
		return skks.getClassRelation(claInv.cls().name());
		
	}
	
	//creates Kodkod expression for OCL navigation
	//finds start of navigation and creates Kodkod expression
	//parts of OCL expression call corresponding methods
	//to get needed expressions
	//calls method for navigation with given start
	private Expression getExpNavigation(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Expression var = null;
		
		ExpNavigation eN = (ExpNavigation) expr;
		
		switch(getExpressionType(eN.getObjectExpression())){
		case VARIABLE:
			if(eN.getObjectExpression().toString().equals("self")){
				if(unsolve==false){
					unsolve = true;
					negExp = eN.getObjectExpression();
				}
			}else{
				if(vars.containsKey(getExpVariableName(eN.getObjectExpression()))){
					var = vars.get(getExpVariableName(eN.getObjectExpression()));
				}else{
					var = exps.get(getExpVariableName(eN.getObjectExpression()));
				}
			}
			break;
		case NAVIGATION:
			var = getExpNavigation(eN.getObjectExpression());
			break;
		case DEFAULT:
			if(unsolve==false){
				unsolve = true;
				negExp = eN.getObjectExpression();
			}
			break;
		}
		if(var!=null){
			return getExpNavigation(expr, var);
		}else{
			return null;
		}
		
	}
	
	//creates Kodkod expression for OCL navigation
	//start of navigation is given
	//sets joins as needed for translation rule
	private Expression getExpNavigation(org.tzi.use.uml.ocl.expr.Expression expr, Expression var){
		
		int sourPos = -1;
		int destPos = -1;
		
		ExpNavigation eN = (ExpNavigation) expr;
		
		Expression ex;
		Relation tmpRel;
		
		for(int i = 0; i < eN.getDestination().association().associationEnds().size(); i++){
			MAssociationEnd mAssEnd = (MAssociationEnd) eN.getSource().association().associationEnds().toArray()[i];
			if(mAssEnd.nameAsRolename().equals(eN.getSource().nameAsRolename())){
				sourPos = i;
			}
			if(mAssEnd.nameAsRolename().equals(eN.getDestination().nameAsRolename())){
				destPos = i;
			}
		}
		
		if(skks.checkAssociationClass(eN.getDestination().association().name())){
			sourPos++;
			destPos++;
			tmpRel = skks.getAssClaRelation(eN.getSource().association().name());
		}else{
			tmpRel = skks.getAssRelation(eN.getSource().association().name());
		}
		ex = tmpRel;
		for(int i = 0; i < destPos; i++){
			if(i == sourPos){
				ex = var.join(ex);
			}else{
				ex = Expression.UNIV.join(ex);
			}
		}
		for(int i = tmpRel.arity() - 1; i > destPos; i--){
			if(i == sourPos){
				ex = ex.join(var);
			}else{
				ex = ex.join(Expression.UNIV);
			}
		}
		return ex;
	}
	
	//creates Kodkod expression for OCL expression select
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	private Expression getExpSelect(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula bodyFormula = null;
		Expression selectExpression = null;
		Decls varExpression = null;
		
		ExpSelect eS = (ExpSelect) expr;
		
		//saves variable for using it later by OCL notation in other methods
		for(int i = 0; i < eS.getVariableDeclarations().size(); i++){
			vars.put(eS.getVariableDeclarations().varDecl(i).name(), Variable.unary(eS.getVariableDeclarations().varDecl(i).name()));
		}
		
		selectExpression = getKodkodExpression(eS.getRangeExpression());
		bodyFormula = getKodkodFormula(eS.getQueryExpression());
		if(bodyFormula.equals(Formula.TRUE)){
			bodyFormula = null;
		}
		
		if(selectExpression != null && bodyFormula != null){
			for(int i = 0; i < eS.getVariableDeclarations().size(); i++){
				if(i == 0){
					varExpression = vars.get(eS.getVariableDeclarations().varDecl(i).name()).oneOf(selectExpression);
				}else{
					varExpression = varExpression.and(vars.get(eS.getVariableDeclarations().varDecl(i).name()).oneOf(selectExpression));
				}
			}
			return bodyFormula.comprehension(varExpression);
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	//creates Kodkod expression for OCL expression reject
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	private Expression getExpReject(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula bodyFormula = null;
		Expression rejectExpression = null;
		Decls varExpression = null;
		
		ExpReject eR = (ExpReject) expr;
		
		//saves variable for using it later by OCL notation in other methods
		for(int i = 0; i < eR.getVariableDeclarations().size(); i++){
			vars.put(eR.getVariableDeclarations().varDecl(i).name(), Variable.unary(eR.getVariableDeclarations().varDecl(i).name()));
		}
		
		rejectExpression = getKodkodExpression(eR.getRangeExpression());
		bodyFormula = getKodkodFormula(eR.getQueryExpression());
		
		if(rejectExpression != null && bodyFormula != null){
			for(int i = 0; i < eR.getVariableDeclarations().size(); i++){
				if(i == 0){
					varExpression = vars.get(eR.getVariableDeclarations().varDecl(i).name()).oneOf(rejectExpression);
				}else{
					varExpression = varExpression.and(vars.get(eR.getVariableDeclarations().varDecl(i).name()).oneOf(rejectExpression));
				}
			}
			return bodyFormula.not().comprehension(varExpression);
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	//creates Kodkod expression for OCL expression of collection type
	//parts of OCL expression call corresponding methods
	//to get needed formula parts and expressions
	//only possible if queryExpression is navigation or asType
	private Expression getExpCollect(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Expression bodyExpression = null;
		Expression collectExpression = null;
		
		ExpCollect eC = (ExpCollect) expr;
		
		collectExpression = getKodkodExpression(eC.getRangeExpression(), false);
		
		switch(getExpressionType(eC.getQueryExpression())){
		case ASTYPE:
			bodyExpression = getExpAsType(eC.getQueryExpression());
			bodyExpression = collectExpression;
			break;
		case NAVIGATION:
			bodyExpression = getExpNavigation(eC.getQueryExpression(), collectExpression);
			break;
		case DEFAULT:
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			break;
		}
		return bodyExpression;
		
	}
	
	//creates Kodkod expression for OCL expression AsType
	//returns relation of class or associationclass
	private Expression getExpAsType(org.tzi.use.uml.ocl.expr.Expression expr){
		
		ExpAsType eAT = (ExpAsType) expr;
		
		String tarTyp = eAT.toString().substring(eAT.toString().indexOf(".oclAsType(") + 11);
		tarTyp = tarTyp.substring(0, tarTyp.indexOf(")"));
		
		return skks.getClassRelation(tarTyp);
		
	}
	
	//creates Kodkod expression for OCL expression flatten, union
	//intersection, size, asSet, including, excluding, symmetricDifference,
	//and difference
	//parts of OCL expression call corresponding methods
	//to get needed expressions
	private Expression getExpStdOp(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Expression stdOpExpr = null;
		
		ExpStdOp eSO = (ExpStdOp) expr;
		
		Expression eSOExps[] = new Expression[eSO.args().length];
		for(int i = 0; i < eSO.args().length; i++){
			eSOExps[i] = getKodkodExpression(eSO.args()[i], false);
		}
		
		//choosing the right translation rule by opname
		if(eSO.opname().equals("flatten")){
			stdOpExpr = eSOExps[0];
		}else if(eSO.opname().equals("union")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0].union(eSOExps[1]);
			}
		}else if(eSO.opname().equals("intersection")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0].intersection(eSOExps[1]);
			}
		}else if(eSO.opname().equals("size")){
			if(eSOExps[0]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0];
			}
		}else if(eSO.opname().equals("asSet")){
			stdOpExpr = eSOExps[0];
		}else if(eSO.opname().equals("including")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0].union(eSOExps[1]);
			}
		}else if(eSO.opname().equals("excluding")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0].difference(eSOExps[1]);
			}
		}else if(eSO.opname().equals("symmetricDifference")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0].difference(eSOExps[1]).union(eSOExps[1].difference(eSOExps[0]));
			}
		}else if(eSO.opname().equals("-")){
			if(eSOExps[0]==null || eSOExps[1]==null){
				if(unsolve==false){
					unsolve = true;
					negExp = expr;
				}
			}else{
				stdOpExpr = eSOExps[0].difference(eSOExps[1]);
			}
		}
		
		return stdOpExpr;
		
	}
	
	//creates Kodkod expression for attribute expression in OCL
	//parts of OCL expression call corresponding methods
	//to get needed expressions
	private Expression getExpAttrOp(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Expression objExp = null;
		
		ExpAttrOp eAO = (ExpAttrOp) expr;
		
		objExp = getKodkodExpression(eAO.objExp());
		
		if(objExp!=null){
			return objExp.join(skks.getAttRelation(eAO.attr().owner().name() + "_-" + eAO.attr().name()));
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	//creates Kodkod expression for constant of type boolean
	//returns boolrelationtrue if constant is true boolrelationfalse
	//if constant is false
	private Expression getExpConstBoolean(org.tzi.use.uml.ocl.expr.Expression expr){
		
		ExpConstBoolean eCB = (ExpConstBoolean) expr;
		
		if(eCB.value()==true){
			return skks.getBoolRelationTrue();
		}else{
			return skks.getBoolRelationFalse();
		}
		
	}
	
	// JW: creates Kodkod expression for constant of a enum typ
	private Expression getExpConstEnum(org.tzi.use.uml.ocl.expr.Expression expr){
		ExpConstEnum eCE = (ExpConstEnum) expr;
		return skks.getENumValueRelation(eCE.type() + "_#" + eCE.value());
	}
	
	//creates Kodkod expression for OCL expression if
	//parts of OCL expression call corresponding methods
	//to get needed expressions
	//if expression creates Kodkod expression and cannot create
	//Kodkod formula so it is not possible at all if expressions
	//in OCL
	private Expression getExpIf(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula ifForm = Formula.TRUE;
		Expression thenExpr = null;
		Expression elseExpr = null;
		
		ExpIf eI = (ExpIf) expr;
		
		ifForm = getKodkodFormula(eI.getCondition());
		thenExpr = getKodkodExpression(eI.getThenExpression());
		elseExpr = getKodkodExpression(eI.getElseExpression());
		
		if(ifForm!=null && thenExpr!=null && elseExpr!=null){
			return ifForm.thenElse(thenExpr, elseExpr);
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	//creates Kodkod expression for OCL expression allInstances
	//returns relation of class or associationclass
	private Expression getExpAllInstances(org.tzi.use.uml.ocl.expr.Expression expr){
		
		ExpAllInstances eAA = (ExpAllInstances) expr;
		
		return skks.getClassRelation(eAA.toString().substring(0, (eAA.toString().length() - 13)));
		
	}
	
	//creates Kodkod intExpression for OCL integer operator
	//+, -, *, /
	//parts of OCL expression call corresponding methods
	//to get needed intExpressions
	//intExpression is used because it is an integer operator
	private IntExpression getIntExpStdOp(org.tzi.use.uml.ocl.expr.Expression expr){
		
		IntExpression stdOpExpr = null;
		
		ExpStdOp eSO = (ExpStdOp) expr;
		
		IntExpression eSOIntExps[] = new IntExpression[eSO.args().length];
		
		for(int i = 0; i < eSO.args().length; i++){
			eSOIntExps[i] = getKodkodIntExpression(eSO.args()[i], false);
			switch(getExpressionType(eSO.args()[i])){
			case VARIABLE:
				eSOIntExps[i] = intExps.get(getExpVariableName(eSO.args()[i]));
				break;
			case STDOP:
				if(getExpStdOp(eSO.args()[i])!=null){
					eSOIntExps[i] = getExpStdOp(eSO.args()[i]).count();
				}else{
					eSOIntExps[i] = null;
				}
				break;
			case ATTROP:
				if(getExpAttrOp(eSO.args()[i])!=null){
					eSOIntExps[i] = getExpAttrOp(eSO.args()[i]).count();
				}else{
					eSOIntExps[i] = null;
				}
				break;
			case IF:
				eSOIntExps[i] = getIntExpIf(eSO.args()[i]);
				break;
			case DEFAULT:
				break;
			}
		}
		
		//choosing the right translation rule by opname
		if(eSOIntExps[0]!=null && eSOIntExps[1]!=null){
			if(eSO.opname().equals("+")){
				if(eSOIntExps[0]==null || eSOIntExps[1]==null){
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}else{
					stdOpExpr = eSOIntExps[0].plus(eSOIntExps[1]);
				}
			}else if(eSO.opname().equals("-")){
				if(eSOIntExps[0]==null || eSOIntExps[1]==null){
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}else{
					stdOpExpr = eSOIntExps[0].minus(eSOIntExps[1]);
				}
			}else if(eSO.opname().equals("*")){
				if(eSOIntExps[0]==null || eSOIntExps[1]==null){
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}else{
					stdOpExpr = eSOIntExps[0].multiply(eSOIntExps[1]);
				}
			}else if(eSO.opname().equals("/")){
				if(eSOIntExps[0]==null || eSOIntExps[1]==null){
					if(unsolve==false){
						unsolve = true;
						negExp = expr;
					}
				}else{
					stdOpExpr = eSOIntExps[0].divide(eSOIntExps[1]);
				}
			}
		}
		
		return stdOpExpr;
	}
	
	//creates Kodkod inExpression for OCL expression if
	//parts of OCL expression call corresponding methods
	//to get needed intExpressions
	//same use as if expression but intExpressions in then
	//and else part
	private IntExpression getIntExpIf(org.tzi.use.uml.ocl.expr.Expression expr){
		
		Formula ifForm = Formula.TRUE;
		IntExpression thenExpr = null;
		IntExpression elseExpr = null;
		
		ExpIf eI = (ExpIf) expr;
		
		ifForm = getKodkodFormula(eI.getCondition());
		
		thenExpr = getKodkodIntExpression(eI.getThenExpression());
		
		switch(getExpressionType(eI.getThenExpression())){
		case STDOP:
			if(getExpStdOp(eI.getThenExpression())!=null){
				thenExpr = getExpStdOp(eI.getThenExpression()).count();
			}
			break;
		case DEFAULT:
			break;
		}
		
		elseExpr = getKodkodIntExpression(eI.getElseExpression());
		switch(getExpressionType(eI.getElseExpression())){
		case STDOP:
			if(getExpStdOp(eI.getElseExpression())!=null){
				elseExpr = getExpStdOp(eI.getElseExpression()).count();
			}
			break;
		case DEFAULT:
			break;
		}
		
		if(ifForm!=null && thenExpr!=null && elseExpr!=null){
			return ifForm.thenElse(thenExpr, elseExpr);
		}else{
			if(unsolve==false){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	//creates Kodkod intExpression for constant of type integer
	//intExpression is set by IntConstant in Kodkod
	private IntExpression getIntExpConstInteger(org.tzi.use.uml.ocl.expr.Expression expr){
		
		ExpConstInteger eCI = (ExpConstInteger) expr;
		
		return IntConstant.constant(eCI.value());
		
	}
	
	//returns the name of a variable
	private String getExpVariableName(org.tzi.use.uml.ocl.expr.Expression expr){
		
		ExpVariable eV = (ExpVariable) expr;
		
		return eV.getVarname();
		
	}
	
	private Formula getKodkodFormula(org.tzi.use.uml.ocl.expr.Expression expr){
		return getKodkodFormula(expr, true);
	}
	
	//calls the needed methods to get the needed Kodkod formula
	//if check is true the unsolve variable is set (not always possible
	//because of special cases in stdOp)
	private Formula getKodkodFormula(org.tzi.use.uml.ocl.expr.Expression expr, boolean check){
		
		switch(getExpressionType(expr)){
		case FORALL:
			return forAllForm(expr);
		case STDOP:
			return stdOpForm(expr);
		case LET:
			return letForm(expr);
		case EXISTS:
			return existsForm(expr);
		case ONE:
			return oneForm(expr);
		case ISTYPEOF:
			return isTypeOfForm(expr);
		case ATTROP:
			return attrOpForm(expr);
		case DEFAULT:
			if(unsolve==false && check==true){
				unsolve = true;
				negExp = expr;
			}
			return Formula.TRUE;
		default:
			if(unsolve==false && check==true){
				unsolve = true;
				negExp = expr;
			}
			return Formula.TRUE;
		}
		
	}
	
	private Expression getKodkodExpression(org.tzi.use.uml.ocl.expr.Expression expr){
		return getKodkodExpression(expr, true);
	}
	
	//calls the needed methods to get the needed Kodkod expression
	//if check is true the unsolve variable is set (not always possible
	//because of special cases in stdOp)
	private Expression getKodkodExpression(org.tzi.use.uml.ocl.expr.Expression expr, boolean check){
		
		switch(getExpressionType(expr)){
		case NAVIGATION:
			return getExpNavigation(expr);
		case SELECT:
			return getExpSelect(expr);
		case COLLECT:
			return getExpCollect(expr);
		case ASTYPE:
			return getExpAsType(expr);
		case STDOP:
			return getExpStdOp(expr);
		case ATTROP:
			return getExpAttrOp(expr);
		case IF:
			return getExpIf(expr);
		case REJECT:
			return getExpReject(expr);
		case ALLINSTANCES:
			return getExpAllInstances(expr);
		case CONSTBOOLEAN:
			return getExpConstBoolean(expr);
		// JW: adding EnumConstant
		case CONSTENUM:
			return getExpConstEnum(expr);
		case VARIABLE:
			if(vars.containsKey(getExpVariableName(expr))){
				return vars.get(getExpVariableName(expr));
			}else{
				return exps.get(getExpVariableName(expr));
			}
		case DEFAULT:
			if(unsolve==false && check==true){
				unsolve = true;
				negExp = expr;
			}
			return null;
		default:
			if(unsolve==false && check==true){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	private IntExpression getKodkodIntExpression(org.tzi.use.uml.ocl.expr.Expression expr){
		return getKodkodIntExpression(expr, true);
	}
	
	//calls the needed methods to get the needed Kodkod intExpression
	//if check is true the unsolve variable is set (not always possible
	//because of special cases in stdOp)
	private IntExpression getKodkodIntExpression(org.tzi.use.uml.ocl.expr.Expression expr, boolean check){
		
		switch(getExpressionType(expr)){
		case IF:
			return getIntExpIf(expr);
		case CONSTINTEGER:
			return getIntExpConstInteger(expr);
		case DEFAULT:
			if(unsolve==false && check==true){
				unsolve = true;
				negExp = expr;
			}
			return null;
		default:
			if(unsolve==false && check==true){
				unsolve = true;
				negExp = expr;
			}
			return null;
		}
		
	}
	
	//gives the type of the USE Expression object
	//types which cannot be translated give a default value
	private Exp getExpressionType(org.tzi.use.uml.ocl.expr.Expression expr){
		if(expr instanceof ExpAllInstances){
			return Exp.ALLINSTANCES;
		}
		
		if(expr instanceof ExpAny){
		}
		
		if(expr instanceof ExpAsType){
			return Exp.ASTYPE;
		}
		
		if(expr instanceof ExpAttrOp){
			return Exp.ATTROP;
		}
		
		if(expr instanceof ExpBagLiteral){
		}
		
		if(expr instanceof ExpCollect){
			return Exp.COLLECT;
		}
		
		if(expr instanceof ExpCollectionLiteral){
		}
		
		if(expr instanceof ExpConstBoolean){
			return Exp.CONSTBOOLEAN;
		}
		
		if(expr instanceof ExpConstEnum){
			return Exp.CONSTENUM;
		}
		
		if(expr instanceof ExpConstInteger){
			return Exp.CONSTINTEGER;
		}
		
		if(expr instanceof ExpConstReal){
		}
		
		if(expr instanceof ExpConstString){
		}
		
		if(expr instanceof ExpEmptyCollection){
		}
		
		if(expr instanceof ExpExists){
			return Exp.EXISTS;
		}
		
		if(expr instanceof ExpForAll){
			return Exp.FORALL;
		}
		
		if(expr instanceof ExpIf){
			return Exp.IF;
		}
		
		if(expr instanceof ExpIsKindOf){
		}
		
		if(expr instanceof ExpIsTypeOf){
			return Exp.ISTYPEOF;
		}
		
		if(expr instanceof ExpIsUnique){
		}
		
		if(expr instanceof ExpIterate){
		}
		
		if(expr instanceof ExpLet){
			return Exp.LET;
		}
		
		if(expr instanceof ExpNavigation){
			return Exp.NAVIGATION;
		}
		
		if(expr instanceof ExpObjAsSet){
		}
		
		if(expr instanceof ExpObjOp){
		}
		
		if(expr instanceof ExpOne){
			return Exp.ONE;
		}
		
		if(expr instanceof ExpQuery){
		}
		
		if(expr instanceof ExpReject){
			return Exp.REJECT;
		}
		
		if(expr instanceof ExpressionWithValue){
		}
		
		if(expr instanceof ExpSelect){
			return Exp.SELECT;
		}
		
		if(expr instanceof ExpSequenceLiteral){
		}
		
		if(expr instanceof ExpSetLiteral){
		}
		
		if(expr instanceof ExpSortedBy){
		}
		
		if(expr instanceof ExpStdOp){
			return Exp.STDOP;
		}
		
		if(expr instanceof ExpTupleLiteral){
		}
		
		if(expr instanceof ExpTupleSelectOp){
		}
		
		if(expr instanceof ExpUndefined){
		}
		
		if(expr instanceof ExpVariable){
			return Exp.VARIABLE;
		}
		
		return Exp.DEFAULT;
		
	}
	
	//checks if the opname of stdOp is an integer expression
	private boolean checkIntExpr(String name){
		
		if(name.equals("=")){
			return true;
		}
		
		if(name.equals("<>")){
			return true;
		}
		
		if(name.equals(">")){
			return true;
		}
		
		if(name.equals("<")){
			return true;
		}
		
		if(name.equals(">=")){
			return true;
		}
		
		if(name.equals("<=")){
			return true;
		}
		
		if(name.equals("size")){
			return true;
		}
		
		return false;
		
	}
	
	//checks if the opname is translated by Kodkod expression (intExpression)
	//or Kodkod formula
	private boolean checkStdExp(String name){
		
		if(name.equals("intersection")){
			return true;
		}
		
		if(name.equals("flatten")){
			return true;
		}
		
		if(name.equals("union")){
			return true;
		}
		
		if(name.equals("size")){
			return true;
		}
		
		if(name.equals("isSet")){
			return true;
		}
		
		if(name.equals("including")){
			return true;
		}
		
		if(name.equals("excluding")){
			return true;
		}
		
		if(name.equals("symmetricDifference")){
			return true;
		}
		
		if(name.equals("-")){
			return true;
		}
		
		if(name.equals("+")){
			return true;
		}
		
		if(name.equals("*")){
			return true;
		}
		
		if(name.equals("/")){
			return true;
		}
		
		return false;
		
	}
	
	//checks if the opname is an integer operator
	private boolean checkMathExpr(String name){
		
		if(name.equals("+")){
			return true;
		}
		
		if(name.equals("-")){
			return true;
		}
		
		if(name.equals("*")){
			return true;
		}
		
		if(name.equals("/")){
			return true;
		}
		
		return false;
		
	}
	
}