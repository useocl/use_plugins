package org.tzi.use.plugin.filmstrip;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.TupleType.Part;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.util.StringUtil;

public final class FilmstripModelConstants {

	public static final String SNAPSHOT_CLASSNAME = "Snapshot";
	public static final String FILMSTRIP_ASSOCNAME = "Filmstrip";
	
	public static final String ORDERABLE_CLASSNAME = "SnapshotItem";
	public static final String ORDERABLE_ASSOCNAME = "PredSucc";
	public static final String SNAPSHOTELEMENT_ASSOCNAME = "SnapElement";
	public static final String SUCC_ROLENAME = "succ";
	public static final String PRED_ROLENAME = "pred";
	
	public static final String OPC_CLASSNAME = "OpC";
	public static final String OPC_SELF_VARNAME = "aSelf";
	public static final String OPC_RETURNVALUE_VARNAME = "retVal";
	
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
	public static final String SNAPSHOT_INV_CYCLEFREE = "Set{ self }->closure( s | s."
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
			+ "." + makeRoleName(SNAPSHOT_CLASSNAME) + " = self."
			+ PRED_ROLENAME + "()";
	
	public static final String CLASS_INV_VALIDLINKING_NAME = "validSnapshotLinking";
	public static final String CLASS_INV_VALIDLINKING = "self." + SUCC_ROLENAME
			+ ".isDefined implies " + "self." + SUCC_ROLENAME + "."
			+ makeRoleName(SNAPSHOT_CLASSNAME) + " = self."
			+ makeRoleName(SNAPSHOT_CLASSNAME) + "." + SUCC_ROLENAME + "()";
	
	private FilmstripModelConstants(){
	}
	
	public static String makeOpCName(String className){
		return String.format("%sOpC", className);
	}
	
	public static String makeOpCName(String className, String opName){
		return String.format("%s_%sOpC", opName, className);
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
		return String.format("%s%s", makeRoleName(SNAPSHOT_CLASSNAME), cls);
	}

	public static String makeClsOrdableAssocName(String cls) {
		return String.format("%s%s", FilmstripModelConstants.ORDERABLE_ASSOCNAME, cls);
	}

	public static String makeClsPredRolename(String cls) {
		return String.format("%s%s", FilmstripModelConstants.PRED_ROLENAME, cls);
	}
	
	public static String makeClsSuccRolename(String cls) {
		return String.format("%s%s", FilmstripModelConstants.SUCC_ROLENAME, cls);
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
		if(t.isTrueObjectType()){
			return "((not " + varName + ".oclIsUndefined()) implies " + varName
					+ "." + makeRoleName(SNAPSHOT_CLASSNAME) + " = self."
					+ (inPred ? PRED_ROLENAME : SUCC_ROLENAME) + "())";
		}
		else if(t.isCollection(true)){
			CollectionType collType = (CollectionType) t;
			String tmp = makeParamDefinedInv("c", collType.elemType(), inPred);
			if(!tmp.equals("true")){
				return varName + "->forAll( c | " + tmp + " )";
			}
		}
		else if(t.isTupleType(true)){
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
	
}
