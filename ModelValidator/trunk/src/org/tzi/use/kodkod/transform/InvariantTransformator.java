package org.tzi.use.kodkod.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.ExpressionHelper;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelFactory;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.use.kodkod.transform.ocl.DefaultExpressionVisitor;
import org.tzi.use.uml.mm.MClassInvariant;

/**
 * Class to transform invariants of the use model in invariants of the model of
 * the model validator.
 * 
 * @author Hendrik
 */
public class InvariantTransformator {

	private static final Logger LOG = Logger.getLogger(InvariantTransformator.class);

	private final IModelFactory factory;
	private final TypeFactory typeFactory;

	public InvariantTransformator(final IModelFactory factory, final TypeFactory typeFactory) {
		this.factory = factory;
		this.typeFactory = typeFactory;
	}

	/**
	 * Transformation of a single invariant.
	 */
	public IInvariant transform(IModel model, MClassInvariant mClassInvariant) throws TransformationException {
		LOG.debug(mClassInvariant.toString() + ": " + mClassInvariant.bodyExpression().toString());

		IClass invariantClass = model.getClass(mClassInvariant.cls().name());

		Map<String, Node> variables = new TreeMap<String, Node>();
		Map<String, IClass> variableClasses = new TreeMap<String, IClass>();
		Map<String, Node> contextVariables = new TreeMap<String, Node>();

		String[] singleVars = mClassInvariant.var().split(",");
		for (String var : singleVars) {
			variables.put(var.trim(), Variable.unary(var.trim()));
			variableClasses.put(var.trim(), invariantClass);
			contextVariables.put(var.trim(), variables.get(var.trim()));
		}

		DefaultExpressionVisitor visitor = new DefaultExpressionVisitor(model, variables, variableClasses, new HashMap<String, Variable>(),
				new ArrayList<String>());
		mClassInvariant.bodyExpression().processWithVisitor(visitor);

		IInvariant invariant = createInvariant(mClassInvariant.toString(), invariantClass, contextVariables.values(), visitor.getObject());
		
		return invariant;
	}

	/**
	 * Transforms a single invariant and add the resulting invariant to the
	 * class of the model validator.
	 */
	public IInvariant transformAndAdd(IModel model, MClassInvariant mClassInvariant) throws TransformationException {
		IInvariant invariant = transform(model, mClassInvariant);
		IClass invariantClass = model.getClass(mClassInvariant.cls().name());
		invariantClass.addInvariant(invariant);
		return invariant;
	}

	/**
	 * Creates a invariant for the model of the model validator.
	 */
	private IInvariant createInvariant(String name, IClass invariantClass, Collection<Node> variables, Object transform) {
		IInvariant invariant = factory.createInvariant(name, invariantClass);

		if (transform != null) {
			Formula formula;
			if (transform instanceof Expression) {
				formula = ExpressionHelper.boolean_expr2formula((Expression) transform, typeFactory);
			} else {
				formula = (Formula) transform;
			}

			Relation relation;
			if (!invariantClass.existsInheritance()) {
				relation = invariantClass.relation();
			} else {
				relation = invariantClass.inheritanceRelation();
			}

			Decls variableDeclaration = null;
			for (Node node : variables) {
				if (node instanceof Variable) {
					if (variableDeclaration == null) {
						variableDeclaration = ((Variable) node).oneOf(relation);
					} else {
						variableDeclaration = variableDeclaration.and(((Variable) node).oneOf(relation));
					}
				}
			}

			formula = formula.forAll(variableDeclaration);

			invariant.setFormula(formula);
		}
		return invariant;
	}

	/**
	 * Transform all invariants and add the resulting invariants to the classes
	 * of the model validator.
	 */
	public void transformAndAdd(IModel model, Collection<MClassInvariant> classInvariants) {
		for (MClassInvariant inv : classInvariants) {
			try {
				IInvariant invariant = transformAndAdd(model, inv);
				debugOut(invariant);

			} catch (Exception exception) {
				printTransformationError(inv, exception);
			}
		}
		LOG.info(LogMessages.invTransformSuccessful);
	}

	/**
	 * Transformation of all given invariants.
	 */
	public List<IInvariant> transform(IModel model, Collection<MClassInvariant> classInvariants) {
		List<IInvariant> invariants = new ArrayList<IInvariant>();

		for (MClassInvariant inv : classInvariants) {
			try {
				IInvariant invariant = transform(model, inv);
				invariants.add(invariant);
				debugOut(invariant);

			} catch (TransformationException exception) {
				printTransformationError(inv, exception);
			}
		}
		LOG.info(LogMessages.invTransformSuccessful);
		return invariants;
	}

	private void debugOut(IInvariant invariant) {
		LOG.debug(LogMessages.invTransformSuccessful(invariant.toString()));
		LOG.debug(PrintHelper.prettyKodkod(invariant.formula()));
	}

	private void printTransformationError(MClassInvariant inv, Exception exception) {
		if (LOG.isDebugEnabled()) {
			LOG.error(LogMessages.invTransformError(inv.toString()), exception);
		} else {
			LOG.error(LogMessages.invTransformError(inv.toString()) + " " + exception.getMessage());
		}
	}
}
