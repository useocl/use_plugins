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
 * transformation of application to filmstrip model using SnapCSnap Aggregation
 * 
 * @author Nisha desai
 * 
 */

public class FilmstripMVCSnapCSnapAggVisitor extends FilmstripMMVisitor {

	public static final String NAME = "Model validator compatible transformation using SnapCSnap Agg";
	
	private FilmstripMVCSnapCSnapAggVisitor(String modelName) {
		super(modelName);
	}
	
	public static MModel transformModel(MModel model, String modelName){
		FilmstripMMVisitor visitor = new FilmstripMVCSnapCSnapAggVisitor(modelName);
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
						FilmstripModelConstants.PRED_ROLENAME,
						FilmstripModelConstants.OPC_PRED_ROLENAME
				}, new String[]{
						"1",
						"0..1"
				}, new int[]{
						MAggregationKind.NONE,
						MAggregationKind.AGGREGATION
				}, new boolean[]{
						false,
						false
				}, new String[][][]{}); 
		
		model.createAssociation(FilmstripModelConstants.OPC_SNAPSHOT, 
				new String[]{
						FilmstripModelConstants.OPC_CLASSNAME,
						FilmstripModelConstants.SNAPSHOT_CLASSNAME
				}, new String[]{
						FilmstripModelConstants.OPC_SUCC_ROLENAME,
						FilmstripModelConstants.SUCC_ROLENAME
				}, new String[]{
						"0..1",
						"1"
				}, new int[]{
						MAggregationKind.NONE,
						MAggregationKind.AGGREGATION
				}, new boolean[]{
						false,
						false
				}, new String[][][]{});
		
		// operations
		model.createQueryOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.PRED_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SnapCSnapSNAPSHOT_PRED_OP);
		model.createQueryOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SUCC_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SnapCSnapSNAPSHOT_SUCC_OP);
		
		model.createQueryOperation(FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.PRED_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SnapCSnapOPC_PRED_OP );
		model.createQueryOperation(FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.SUCC_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SnapCSnapOPC_SUCC_OP );
		
		
		// constraints
		
		model.createInvariant(
				FilmstripModelConstants.SNAPSHOT_INV_ONEFILMSTRIP_NAME,
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_INV_ONEFILMSTRIP,
				false);
	}
	
}
