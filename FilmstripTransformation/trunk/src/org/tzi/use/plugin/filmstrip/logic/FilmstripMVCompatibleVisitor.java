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

public class FilmstripMVCompatibleVisitor extends FilmstripMMVisitor {

	public static final String NAME = "Model validator compatible transformation with ternary association";
	
	private FilmstripMVCompatibleVisitor(String modelName) {
		super(modelName);
	}
	
	public static MModel transformModel(MModel model, String modelName){
		FilmstripMMVisitor visitor = new FilmstripMVCompatibleVisitor(modelName);
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
			createFilmstripElements();
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
	
}
