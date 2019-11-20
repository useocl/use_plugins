package org.tzi.use.plugin.filmstrip;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.TupleType.Part;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;
import org.tzi.use.util.StringUtil;

public final class FilmstripModelConstants {

	public static final String SNAPSHOT_CLASSNAME = "Snapshot";
	public static final String FILMSTRIP_ASSOCNAME = "Filmstrip";
	
	public static final String SNAPSHOTITEM_CLASSNAME = "SnapshotItem";
	public static final String ORDERABLE_ASSOCNAME = "PredSucc";
	public static final String SNAPSHOTELEMENT_ASSOCNAME = "SnapElement";
	public static final String SUCC_ROLENAME = "succ";
	public static final String PRED_ROLENAME = "pred";
	
	public static final String OPC_CLASSNAME = "OperationCall";
	public static final String OPC_ABBREVIATION = "OpC";
	public static final String OPC_SELF_VARNAME = "aSelf";
	public static final String OPC_RETURNVALUE_VARNAME = "result";
	
	//element for filmstrip binary association 
	
	public static final String SNAPSHOT_OPC = "SnapshotOpC";
	public static final String SNAP_ROLENAME = "snapshot";
	public static final String OPC_ROLENAME = "opc";
	
	public static final String OpCSnapSNAPSHOT_PRED_OP = "self." + PRED_ROLENAME;
	public static final String OpCSnapSNAPSHOT_SUCC_OP = "self." + SUCC_ROLENAME;
	public static final String OpCSnapOPC_PRED_OP = "self." + SNAP_ROLENAME;
	public static final String OpCSnapOPC_SUCC_OP = "self." + SNAP_ROLENAME + "." + SUCC_ROLENAME;

	public static final String SUCC_OPC_EXISTS_NAME = "succOpCExists";
	public static final String SUCC_OPC_EXISTS = "(not self. " + SUCC_ROLENAME + ".oclIsUndefined() implies (not self."+OPC_ROLENAME+".oclIsUndefined()))";
	/////////////
	public static final String SNAPSHOT_ROLENAME = FilmstripModelConstants.makeRoleName(SNAPSHOT_CLASSNAME);
	
	public static final String SNAPSHOT_PRED_OP = "if self." + PRED_ROLENAME
			+ "->size() = 1 then self." + PRED_ROLENAME
			+ "->any( true ) else oclUndefined(" + SNAPSHOT_CLASSNAME
			+ ") endif";
	public static final String SNAPSHOT_SUCC_OP = "if self." + SUCC_ROLENAME
			+ "->size() = 1 then self." + SUCC_ROLENAME
			+ "->any( true ) else oclUndefined(" + SNAPSHOT_CLASSNAME
			+ ") endif";
	
	// constraints
	public static final String OPC_INV_ASSOCCLASSBEHAVIOR_NAME = "assocClassBehavior";
	public static final String OPC_INV_ASSOCCLASSBEHAVIOR = 
			"self." + PRED_ROLENAME + "->size() = 1 and "
			+ "self." + SUCC_ROLENAME + "->size() = 1 and "
			+ OPC_CLASSNAME + ".allInstances()->forAll( op | "
			+ "(self." + PRED_ROLENAME + " = op." + PRED_ROLENAME + " and "
			+ "self." + SUCC_ROLENAME + " = op." + SUCC_ROLENAME + ") implies "
			+ "self = op)";
	
	public static final String SNAPSHOT_INV_CYCLEFREE_NAME = "cycleFree";
	public static final String SNAPSHOT_INV_CYCLEFREE = "Set{ self. " + SUCC_ROLENAME + "() }->closure( s | s."
			+ SUCC_ROLENAME + "() )->excludes( self )";
	
	public static final String SNAPSHOT_INV_ONEFILMSTRIP_NAME = "oneFilmstrip";
	public static final String SNAPSHOT_INV_ONEFILMSTRIP = 
			"(" + SNAPSHOT_CLASSNAME + ".allInstances()->select( s | s."
			+ PRED_ROLENAME + "().oclIsUndefined() )->size() = 1 and "
			+ SNAPSHOT_CLASSNAME + ".allInstances()->select( s | s."
			+ SUCC_ROLENAME + "().oclIsUndefined() )->size() = 1)";
	
	public static final String OPC_INV_SELFDEFINED_NAME = "aSelfDefined";
	public static final String OPC_INV_SELFDEFINED = "self." + OPC_SELF_VARNAME + ".isDefined";
	
	public static final String OPC_INV_SELFINPRED_NAME = "aSelfInPred";
	public static final String OPC_INV_SELFINPRED = "self." + OPC_SELF_VARNAME
			+ "." + SNAPSHOT_ROLENAME + " = self."
			+ PRED_ROLENAME + "()";
	
	public static final String CLASS_INV_VALIDLINKING_NAME = "validSnapshotLinking";
	public static final String CLASS_INV_VALIDLINKING = "self." + SUCC_ROLENAME
			+ ".isDefined implies " + "self." + SUCC_ROLENAME + "."
			+ SNAPSHOT_ROLENAME + " = self."
			+ SNAPSHOT_ROLENAME + "." + SUCC_ROLENAME + "()";
	
	private FilmstripModelConstants(){
	}
	
	public static String makeOpCName(String className){
		return String.format("%s%s", className, FilmstripModelConstants.OPC_ABBREVIATION);
	}
	
	public static String makeOpCName(String className, String opName){
		return String.format("%s_%s%s", opName, className, FilmstripModelConstants.OPC_ABBREVIATION);
	}
	
	public static String makeRoleName(String className){
		if(className.isEmpty()){
			return "";
		}
		else {
			return Character.toLowerCase(className.charAt(0)) + className.substring(1);
		}
	}
	
	public static String makeInvName(String invName, boolean preCond){
		return String.format("%s_%s", (preCond?"pre":"post"), invName);
	}
	
	public static String makeSnapshotClsAssocName(String cls){
		return String.format("%s%s", SNAPSHOT_CLASSNAME, cls);
	}
	
	public static String makeSnapshotClsRoleName(String cls){
		return String.format("%s%s", SNAPSHOT_ROLENAME, cls);
	}

	public static String makeClsOrdableAssocName(String cls) {
		return String.format("%s%s", FilmstripModelConstants.ORDERABLE_ASSOCNAME, cls);
	}

	public static String makeClsPredRolename(String cls) {
		return String.format("%s%s", FilmstripModelConstants.PRED_ROLENAME, cls);
	}
	
	public static String makeClsSuccRolename(String cls) {
		return String.format("%s%s", SUCC_ROLENAME, cls);
	}
	
	public static String makeParamDefinedInvName(MAttribute param, boolean inPred){
		String predSucc = inPred ? PRED_ROLENAME : SUCC_ROLENAME;
		return String.format("param%sIn%s",
				Character.toUpperCase(param.name().charAt(0))
						+ param.name().substring(1),
				Character.toUpperCase(predSucc.charAt(0))
						+ predSucc.substring(1));
	}
	
	public static String makeParamDefinedInv(String varName, Type t, boolean inPred){
		if(t.isTypeOfClass()){
			return "((not " + varName + ".oclIsUndefined()) implies " + varName
					+ "." + SNAPSHOT_ROLENAME + " = self."
					+ (inPred ? PRED_ROLENAME : SUCC_ROLENAME) + "())";
		}
		else if(t.isKindOfCollection(VoidHandling.EXCLUDE_VOID)){
			CollectionType collType = (CollectionType) t;
			String tmp = makeParamDefinedInv("c", collType.elemType(), inPred);
			if(!tmp.equals("true")){
				return varName + "->forAll( c | " + tmp + " )";
			}
		}
		else if(t.isTypeOfTupleType()){
			TupleType tupleType = (TupleType) t;
			
			ArrayList<String> parts = new ArrayList<String>(tupleType.getParts().size());
			for(Entry<String, Part> part : tupleType.getParts().entrySet()){
				String tmp = makeParamDefinedInv(varName + "." + part.getKey(), part.getValue().type(), inPred);
				if(!tmp.equals("true")){
					parts.add(tmp);
				}
			}
			
			if(parts.size() > 0){
				return StringUtil.fmtSeq(parts, " and ");
			}
		}
		return "true";
	}
	
	public static String makeValidLinkingInvName(String assoc){
		return String.format("validLinking%s", assoc);
	}
	
	public static String makeValidLinkingInvPart(String endName, boolean isCollection){
		if(isCollection){
			return String.format("self.%s->forAll( c | c.%s = self.%s )", endName,
					SNAPSHOT_ROLENAME,
					SNAPSHOT_ROLENAME);
		}
		else {
			return String.format("(self.%s.isDefined implies self.%s = self.%s.%s)", endName,
					SNAPSHOT_ROLENAME, endName,
					SNAPSHOT_ROLENAME);
		}
	}
	
}
