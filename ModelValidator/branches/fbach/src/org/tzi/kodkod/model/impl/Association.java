package org.tzi.kodkod.model.impl;

import java.util.ArrayList;
import java.util.List;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.ConstraintHelper;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.config.impl.AssociationConfigurator;
import org.tzi.kodkod.model.config.impl.Configurator;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Implementation of IAssociation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class Association extends ModelElement implements IAssociation {

	private static final Logger LOG = Logger.getLogger(Association.class);

	private int arity = 0;
	private IAssociationClass associationClass;
	private List<IAssociationEnd> associationEnds;
	private Configurator<IAssociation> configurator;

	Association(IModel model, String name) {
		super(model, name);
		associationEnds = new ArrayList<IAssociationEnd>();
	}

	@Override
	public Relation relation() {
		if (relation == null) {
			arity += associationEnds.size();
			String name = name();
			if (associationClass != null) {
				name += "_assoc";
			}
			relation = Relation.nary(name, arity);
		}
		return relation;
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return configurator.lowerBound(this, arity, tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return configurator.upperBound(this, arity, tupleFactory);
	}

	@Override
	public Formula constraints() {
		Formula formula = Formula.and(typeDefinitions(), multiplicityDefinitions());
		return formula.and(configurator.constraints(this));
	}

	/**
	 * Creates the formula for the type definition constraint.
	 * 
	 * @return
	 */
	private Formula typeDefinitions() {
		ConstraintHelper helper = new ConstraintHelper();
		List<Formula> formulas = new ArrayList<Formula>();

		ArrayList<IAssociationEnd> temporary = new ArrayList<IAssociationEnd>();
		if (associationClass != null) {
			temporary.add(associationClass);
		}
		temporary.addAll(associationEnds);

		Formula formula;
		Expression expression;
		IAssociationEnd associationEnd;

		for (int i = 0; i < temporary.size(); i++) {
			associationEnd = temporary.get(i);

			expression = helper.univ_l(relation(), i);
			for (int j = 0; j < temporary.size() - i - 1; j++) {
				expression = expression.join(Expression.UNIV);
			}

			if (associationEnd.multiplicity() != null && associationEnd.multiplicity().isZeroOne() && isBinaryAssociation()
					|| (associationEnd.equals(associationClass) && isBinaryAssociation())) {
				formula = expression.in(getAssociatedClassRelation(associationEnd.associatedClass()).union(
						model.typeFactory().undefinedType().relation()));
			} else {
				formula = expression.in(getAssociatedClassRelation(associationEnd.associatedClass()));
			}

			formulas.add(formula);
			LOG.debug("Type of " + name() + ": " + PrintHelper.prettyKodkod(formula));
		}
		return Formula.and(formulas);
	}

	/**
	 * Creates the formula for the multiplicity constraints.
	 * 
	 * @return
	 */
	public Formula multiplicityDefinitions() {
		List<Formula> formulas = new ArrayList<Formula>();
		List<Variable> variables;
		Multiplicity multiplicity;

		for (int index = 0; index < associationEnds.size(); index++) {

			variables = new ArrayList<Variable>(relation().arity() - 1);
			multiplicity = associationEnds.get(index).multiplicity();

			if (!multiplicity.isZeroMany()) {

				Decls variableDeclarations = createVariableDeclaration(variables, associationEnds.get(index));

				Expression linkedObjects = createLinkedObjectsExpression(variables, index, false);

				Formula formula = null;
				if (multiplicity.isZeroOne()) {
					formula = zeroOneMultiplicity(variables, index, linkedObjects);
				} else {
					for (Range range : multiplicity.getRanges()) {
						Formula lowerFormula = Formula.TRUE;
						if (range.getLower() > 0) {
							lowerFormula = linkedObjects.count().gte(IntConstant.constant(range.getLower()));
							if (associationClass != null) {
								Relation undefined = model.typeFactory().undefinedType().relation();
								lowerFormula = lowerFormula.and(undefined.in(linkedObjects.join(Expression.UNIV)).not());
							}
						}

						Formula upperFormula = Formula.TRUE;
						if (range.getUpper() != Multiplicity.MANY) {
							upperFormula = linkedObjects.count().lte(IntConstant.constant(range.getUpper()));
						}

						if (formula == null) {
							formula = (lowerFormula.and(upperFormula));
						} else {
							formula = formula.or(lowerFormula.and(upperFormula));
						}
					}
				}
				formula = formula.forAll(variableDeclarations);

				formulas.add(formula);
				LOG.debug("Mult for " + name() + ": " + PrintHelper.prettyKodkod(formula));
			}
		}

		if (associationClass != null) {
			formulas.add(associationClassMultiplicityDefinitions());
		}

		return Formula.and(formulas);
	}

	/**
	 * Creates the formula for the multiplicity constraint for an association
	 * class.
	 * 
	 * @return
	 */
	private Formula associationClassMultiplicityDefinitions() {
		List<Variable> variables = new ArrayList<Variable>(relation().arity() - 1);
		Decls variableDeclarations = createVariableDeclaration(variables, null);
		Expression linkedObjects = relation();
		for (int i = variables.size() - 1; i >= 0; i--) {
			linkedObjects = linkedObjects.join(variables.get(i));
		}

		Formula formula1 = linkedObjects.lone().forAll(variableDeclarations);
		LOG.debug("Mult for association class " + name() + ": " + PrintHelper.prettyKodkod(formula1));

		Variable acVariable = Variable.unary("ac");
		variableDeclarations = acVariable.oneOf(associationClass.relation());
		linkedObjects = acVariable.join(relation());
		Formula formula2 = linkedObjects.one();

		if (isBinaryAssociation()) {
			IClass associatedClass = associationEnds.get(0).associatedClass();
			Expression product = getAssociatedClassRelation(associatedClass);

			for (int j = 1; j < associationEnds.size(); j++) {
				associatedClass = associationEnds.get(j).associatedClass();
				product = product.product(getAssociatedClassRelation(associatedClass));
			}

			formula2 = formula2.and(linkedObjects.in(product));
		}

		formula2 = formula2.forAll(variableDeclarations);
		LOG.debug("Mult for association class " + name() + ": " + PrintHelper.prettyKodkod(formula2));

		return formula1.and(formula2);
	}

	/**
	 * Creates the formula for an association end with multiplicity 0..1.
	 * 
	 * @param variables
	 * @param index
	 * @param linkedObjects
	 * @return
	 */
	private Formula zeroOneMultiplicity(List<Variable> variables, int index, Expression linkedObjects) {
		Formula formula;
		Expression objects;
		if (associationClass == null) {
			objects = linkedObjects;
		} else {
			objects = createLinkedObjectsExpression(variables, index, true);
		}
		
		if (isBinaryAssociation()) {
			formula = objects.one();
		} else {
			formula = objects.lone();
		}
		
		return formula;
	}

	/**
	 * Creates the variable declarations for the multiplicity formulas.
	 * 
	 * @param variables
	 * @param associationEnd
	 * @return
	 */
	private Decls createVariableDeclaration(List<Variable> variables, IAssociationEnd associationEnd) {
		Decls variableDeclaration = null;
		for (int j = 0; j < associationEnds.size(); j++) {
			if (associationEnds.get(j) != associationEnd) {
				variables.add(Variable.unary("v" + (j + 1)));
				IClass associatedClass = associationEnds.get(j).associatedClass();
				if (variables.size() == 1) {
					variableDeclaration = variables.get(0).oneOf(getAssociatedClassRelation(associatedClass));
				} else {
					variableDeclaration = variableDeclaration.and(variables.get(variables.size() - 1).oneOf(
							getAssociatedClassRelation(associatedClass)));
				}
			}
		}
		return variableDeclaration;
	}

	/**
	 * Returns an expression with the linked objects of an association end.
	 * 
	 * @param variables
	 * @param currentEndIndex
	 * @param univJoinRelation
	 * @return
	 */
	private Expression createLinkedObjectsExpression(List<Variable> variables, int currentEndIndex, boolean univJoinRelation) {
		Expression linkedObjects = null;
		for (int j = 0; j < currentEndIndex; j++) {
			if (j == 0) {
				if (!univJoinRelation)
					linkedObjects = variables.get(j).join(relation());
				else
					linkedObjects = variables.get(j).join(Expression.UNIV.join(relation()));
			} else {
				linkedObjects = variables.get(j).join(linkedObjects);
			}
		}
		for (int j = associationEnds.size() - 1; j > currentEndIndex; j--) {
			if (j == associationEnds.size() - 1 && currentEndIndex == 0) {
				linkedObjects = relation().join(variables.get(j - 1));
			} else {
				linkedObjects = linkedObjects.join(variables.get(j - 1));
			}
		}
		return linkedObjects;
	}

	/**
	 * Returns the relation of the given associated class.
	 * 
	 * @param associatedClass
	 * @return
	 */
	private Relation getAssociatedClassRelation(IClass associatedClass) {
		if (associatedClass.existsInheritance()) {
			return associatedClass.inheritanceRelation();
		} else {
			return associatedClass.relation();
		}
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visitAssociation(this);
	}

	@Override
	public void addAssociationEnd(IAssociationEnd associationEnd) {
		associationEnds.add(associationEnd);
	}

	@Override
	public List<IAssociationEnd> associationEnds() {
		return associationEnds;
	}

	@Override
	public void setAssociationClass(IAssociationClass associationClass) {
		if (this.associationClass == null) {
			arity++;
		}
		this.associationClass = associationClass;
	}

	@Override
	public IAssociationClass associationClass() {
		return associationClass;
	}

	@Override
	public boolean isBinaryAssociation() {
		return associationEnds.size() == 2;
	}

	@Override
	public void setConfigurator(Configurator<IAssociation> configurator) {
		this.configurator = configurator;
	}

	@Override
	public Configurator<IAssociation> getConfigurator() {
		return configurator;
	}

	@Override
	public void resetConfigurator() {
		configurator = new AssociationConfigurator();
	}
}
