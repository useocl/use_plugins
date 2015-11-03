package org.tzi.use.plugin.filmstrip.logic;

import java.util.Collections;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Stack;

import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpAsType;
import org.tzi.use.uml.ocl.expr.ExpCollectNested;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpTupleLiteral;
import org.tzi.use.uml.ocl.expr.ExpTupleSelectOp;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;

public class FilmstripUtil {
	
	private FilmstripUtil() {
	}
	
	public static Expression handlePredSucc(Expression range, boolean goPred, Stack<VarDeclList> scope) {
		if(range.type().isTypeOfClass()){
			MClass cls = (MClass) range.type();
			if(FilmstripModelConstants.SNAPSHOT_CLASSNAME.equals(cls.name())){
				MOperation op = cls.operation( goPred ? FilmstripModelConstants.PRED_ROLENAME : FilmstripModelConstants.SUCC_ROLENAME, true);
				try {
					return new ExpObjOp(op, new Expression[]{ range });
				} catch (ExpInvalidException e) {
					throw new TransformationException("handlePredSucc", e);
				}
			}
			else {
				MNavigableElement succ = cls.navigableEnd(FilmstripModelConstants.SUCC_ROLENAME);
				MNavigableElement pred = cls.navigableEnd(FilmstripModelConstants.PRED_ROLENAME);
				
				try {
					if(goPred){
						return new ExpNavigation(range, succ, pred, Collections.<Expression>emptyList());
					}
					else {
						return new ExpNavigation(range, pred, succ, Collections.<Expression>emptyList());
					}
				} catch (ExpInvalidException e) {
					throw new TransformationException("handlePredSucc", e);
				}
			}
		}
		else if(range.type().isKindOfCollection(VoidHandling.EXCLUDE_VOID)){
			CollectionType collType = (CollectionType) range.type();
			
			VarDecl uniqueVar = makeUniqueVar(collType.elemType(), scope);
			Expression e = new ExpVariable(uniqueVar.name(), uniqueVar.type());
			scope.push(new VarDeclList(uniqueVar));
			Expression handled = handlePredSucc(e, goPred, scope);
			scope.pop();
			
			if(e == handled){
				// no handling required, simplify result
				return range;
			}
			
			try {
				handled = new ExpCollectNested(uniqueVar, range, handled);
				
				if(!collType.equals(handled.type())){
					if(collType.isKindOfSet(VoidHandling.EXCLUDE_VOID)){
						handled = ExpStdOp.create("asSet", new Expression[]{ handled });
					}
					else if(collType.isKindOfOrderedSet(VoidHandling.EXCLUDE_VOID)){
						handled = ExpStdOp.create("asOrderedSet", new Expression[]{ handled });
					}
					else {
						// fallback. should never appear
						handled = new ExpAsType(handled, collType);
					}
				}
				
				return handled; 
			} catch (ExpInvalidException ex) {
				throw new TransformationException("Error navigating on CollectionType", ex);
			}
		}
		else if(range.type().isTypeOfTupleType()){
			TupleType tt = (TupleType) range.type();
			
			ExpTupleLiteral.Part[] parts = new ExpTupleLiteral.Part[tt.getParts().size()];
			int i = 0;
			boolean changed = false;
			for(Entry<String, TupleType.Part> part : tt.getParts().entrySet()){
				
				Expression op = new ExpTupleSelectOp(part.getValue(), range);
				Expression handled = handlePredSucc(op, goPred, scope);
				if(op != handled){
					changed = true;
				}
				
				parts[i] = new ExpTupleLiteral.Part(part.getKey(), handled);
				i++;
			}
			
			if(changed){
				return new ExpTupleLiteral(parts);
			}
			else {
				// no changes applied, result gets simplified
				return range;
			}
		}
		else {
			return range;
		}
	}

	private static VarDecl makeUniqueVar(Type elemType, Stack<VarDeclList> scope) {
		char ident = elemType.toString().toLowerCase().charAt(0);
		String varName;
		int i = 1;
		do {
			varName = ident + String.valueOf(i++);
		}
		while(isVarNameTaken(varName, scope));
		
		return new VarDecl(varName, elemType);
	}
	
	/**
	 * Returns whether a {@code varName} is taken in a certain {@code scope}.
	 * 
	 * @param varName variable name to check
	 * @param scope scope to check against
	 * @return true, if varName exists
	 */
	private static boolean isVarNameTaken(String varName, Stack<VarDeclList> scope) {
		for (ListIterator<VarDeclList> it = scope
				.listIterator(scope.size()); it.hasPrevious();) {
			VarDeclList l = it.previous();
			for(int i = 0; i < l.size(); i++){
				if(l.varDecl(i).name().equals(varName)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Identifies whether the class is a class added by the filmstrip transformation or not.
	 * @param c class to check
	 * @return true if the class was added by the filmstrip transformation
	 */
	public static boolean isFilmstripClass(MClass c){
		return c.name().equals(FilmstripModelConstants.SNAPSHOT_CLASSNAME)
				|| c.name().equals(FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME)
				|| c.name().endsWith(FilmstripModelConstants.OPC_ABBREVIATION);
	}
	
	/**
	 * Identifies whether the association is an association added by the
	 * filmstrip transformation or not.
	 * 
	 * @param as association to check
	 * @return true if the association was added by the filmstrip transformation
	 */
	public static boolean isFilmstripAssoc(MAssociation as) {
		return as.name().equals(FilmstripModelConstants.FILMSTRIP_ASSOCNAME)
				|| as.name().equals(FilmstripModelConstants.SNAPSHOTELEMENT_ASSOCNAME)
				|| as.name().startsWith(FilmstripModelConstants.ORDERABLE_ASSOCNAME)
				|| as.name().startsWith(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
	}
	
}
