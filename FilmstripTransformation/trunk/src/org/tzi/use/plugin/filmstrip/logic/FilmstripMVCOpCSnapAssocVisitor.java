package org.tzi.use.plugin.filmstrip.logic;

import org.tzi.use.api.UseApiException;
import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.plugin.filmstrip.logic.FilmstripExpressionVisitor.ExpressionType;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MGeneralization;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.util.StringUtil;

/**
 * transformation of application to filmstrip model using C@Snap Association
 * 
 * @author Nisha desai
 * 
 */

public class FilmstripMVCOpCSnapAssocVisitor extends FilmstripMMVisitor {

	public static final String NAME = "Model validator compatible transformation with binary associations";
	
	private FilmstripMVCOpCSnapAssocVisitor(String modelName) {
		super(modelName);
	}
	
	public static MModel transformModel(MModel model, String modelName){
		FilmstripMMVisitor visitor = new FilmstripMVCOpCSnapAssocVisitor(modelName);
		model.processWithVisitor(visitor);
		return visitor.getModel();
	}
	
	@Override
	protected Expression visitExpression(Expression expr, ExpressionType type,
			MClass src, VarDeclList varDefs) {
		FilmstripMVCompatibleExpressionVisitor visitor = new FilmstripMVCompatibleExpressionVisitor(
				model.getModel(), src, type, mc, varDefs);
		expr.processWithVisitor(visitor);
		return visitor.getResultExpression();
	}
	
	@Override
	protected void initModel() {
		try {
			filmstripElements();
			
		} catch (UseApiException e) {
			throw new TransformationException("Error initialising the model", e);
		}
	}
	
	@Override
	public void visitGeneralization(MGeneralization e) {
		throw new TransformationException("Model validator compatible transformation does not support generalization");
	}
	
	@Override
	public void visitClass(MClass e) {
		MClass newClass;
		try {
			newClass = model.createClass(e.name(), e.isAbstract());
			
			model.createAssociation(
					FilmstripModelConstants.makeSnapshotClsAssocName(e.name()),
					e.name(),
					e.nameAsRolename(),
					"*",
					MAggregationKind.NONE,
					FilmstripModelConstants.SNAPSHOT_CLASSNAME,
					FilmstripModelConstants.SNAPSHOT_ROLENAME,
					"1",
					MAggregationKind.NONE);
			
			model.createAssociation(
					FilmstripModelConstants.makeClsOrdableAssocName(e.name()),
					e.name(),
					FilmstripModelConstants.PRED_ROLENAME,
					"0..1",
					MAggregationKind.AGGREGATION,
					e.name(),
					FilmstripModelConstants.SUCC_ROLENAME,
					"0..1",
					MAggregationKind.NONE);
		}
		catch (Exception ex) {
			throw new TransformationException("Error transforming class "
					+ StringUtil.inQuotes(e.name()), ex);
		}

		newClass.setPositionInModel(e.getPositionInModel());
		copyAnnotations(e, newClass);
		createOpCClass(e);
		createClassInv(newClass);
	}
	
	protected void filmstripElements() throws UseApiException {
		// classes
		model.createClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME, false);
		model.createClass(FilmstripModelConstants.OPC_CLASSNAME, true);
		
		// associations	
		model.createAssociation(FilmstripModelConstants.SNAPSHOT_OPC, 
				new String[]{
						FilmstripModelConstants.SNAPSHOT_CLASSNAME,
						FilmstripModelConstants.OPC_CLASSNAME
				}, new String[]{
						FilmstripModelConstants.SNAP_ROLENAME,
						FilmstripModelConstants.OPC_ROLENAME
				}, new String[]{
						"1",
						"0..1"
				}, new int[]{
						MAggregationKind.NONE,
						MAggregationKind.NONE
				}, new boolean[]{
						false,
						false
				}, new String[][][]{}); 
		
		model.createAssociation(FilmstripModelConstants.FILMSTRIP_ASSOCNAME, 
				new String[]{
						FilmstripModelConstants.SNAPSHOT_CLASSNAME,
						FilmstripModelConstants.SNAPSHOT_CLASSNAME
				}, new String[]{
						FilmstripModelConstants.SUCC_ROLENAME,
						FilmstripModelConstants.PRED_ROLENAME
				}, new String[]{
						"0..1",
						"0..1"
				}, new int[]{
						MAggregationKind.NONE,
						MAggregationKind.NONE
				}, new boolean[]{
						false,
						false
				}, new String[][][]{});
		
		// operations
		model.createQueryOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.PRED_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.OpCSnapSNAPSHOT_PRED_OP);
		model.createQueryOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SUCC_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.OpCSnapSNAPSHOT_SUCC_OP);
		
		model.createQueryOperation(FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.PRED_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.OpCSnapOPC_PRED_OP );
		model.createQueryOperation(FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.SUCC_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.OpCSnapOPC_SUCC_OP );
		
		// constraints
		model.createInvariant(
				FilmstripModelConstants.SUCC_OPC_EXISTS_NAME,
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SUCC_OPC_EXISTS,
				false);
		
		model.createInvariant(
				FilmstripModelConstants.SNAPSHOT_INV_CYCLEFREE_NAME,
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_INV_CYCLEFREE,
				false);
		
		model.createInvariant(
				FilmstripModelConstants.SNAPSHOT_INV_ONEFILMSTRIP_NAME,
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_INV_ONEFILMSTRIP,
				false);
	}
	
}
