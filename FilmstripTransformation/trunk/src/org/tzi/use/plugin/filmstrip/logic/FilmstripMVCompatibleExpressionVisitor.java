package org.tzi.use.plugin.filmstrip.logic;

import java.util.Collections;

import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
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
		
		switch (type) {
		case CLASSINVARIANT:
		case OPERATION:
		case SOIL:
			// no changes required
			break;
		case PRECONDITION:
		case POSTCONDITION:
			self = new ExpVariable(FilmstripModelConstants.OPC_SELF_VARNAME, expType);
			if(type == ExpressionType.POSTCONDITION && !exp.isPre()){
				self = FilmstripUtil.handlePredSucc(self, false, knownVariables);
			}
			break;
		default:
			throw new TransformationException("Unkown expression type " + StringUtil.inQuotes(type));
		}
		
		// navigate to snapshot and back to the class
		MClass snapshot = model.getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
		MAssociation assoc = model.getAssociation(FilmstripModelConstants
				.makeSnapshotClsAssocName(expType.name()));
		
		MAssociationEnd sourceEnd = assoc.associationEndsAt(expType).iterator().next();
		MAssociationEnd destEnd = assoc.associationEndsAt(snapshot).iterator().next();
		Expression nav = self;
		try {
			if(type != ExpressionType.PRECONDITION && type != ExpressionType.POSTCONDITION){
				nav = new ExpNavigation(nav, sourceEnd, destEnd,
						Collections.<Expression>emptyList());
			}
			nav = new ExpNavigation(nav, destEnd,
					sourceEnd, Collections.<Expression>emptyList());
		} catch (ExpInvalidException ex) {
			throw new TransformationException("ExpAllInstances", ex);
		}
		
		copyExpressionDetails(exp, nav);
		elements.push(nav);
	}
}
