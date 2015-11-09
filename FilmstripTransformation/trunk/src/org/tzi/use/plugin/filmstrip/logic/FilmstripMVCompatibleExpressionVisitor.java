package org.tzi.use.plugin.filmstrip.logic;

import java.util.Collections;

import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.util.StringUtil;

public class FilmstripMVCompatibleExpressionVisitor extends
		FilmstripExpressionVisitor {

	public FilmstripMVCompatibleExpressionVisitor(MModel model, MClass src,
			ExpressionType type, MModelConnector mc, VarDeclList varDefs) {
		super(model, src, type, mc, varDefs);
	}
	
	@Override
	public void visitAllInstances(ExpAllInstances exp) {
		/*
		 * Transform <Class>.allInstances() into
		 * self.snapshot.<Class.asRolename()>
		 */
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
			MAssociation assoc = model.getAssociation(FilmstripModelConstants.makeSnapshotClsAssocName(((MClass) selfDecl.type()).name()));
			
			MAssociationEnd sourceEnd = assoc.associationEndsAt((MClass) selfDecl.type()).iterator().next();
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
		} catch (ExpInvalidException e) {
			throw new TransformationException("ExpAllInstances: Unable to navigate from snapshot to objects", e);
		}
		
		if(type == ExpressionType.POSTCONDITION && exp.isPre()){
			nav = FilmstripUtil.handlePredSucc(nav, false, knownVariables);
		}
		
		copyExpressionDetails(exp, nav);
		elements.push(nav);
	}
}
