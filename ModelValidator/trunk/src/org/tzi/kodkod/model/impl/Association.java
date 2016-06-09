package org.tzi.kodkod.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.ConstraintHelper;
import org.tzi.kodkod.helper.PrintHelper;
import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.config.impl.AssociationConfigurator;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.visitor.Visitor;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Implementation of IAssociation.
 * 
 * @author Hendrik Reitmann
 */
public class Association extends ModelElement implements IAssociation {

	private static final Logger LOG = Logger.getLogger(Association.class);

	protected int arity = 0;
	protected IAssociationClass associationClass;
	protected List<IAssociationEnd> associationEnds;
	protected IConfigurator<IAssociation> configurator;

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
		Formula formula = Formula.and(typeDefinitions(), multiplicityDefinitions(), cycleFreenessDefinitions());
		return formula.and(configurator.constraints(this));
	}

	protected Formula cycleFreenessDefinitions() {
		if(associationClass != null || !isBinaryAssociation()){
			return Formula.TRUE;
		}
		
		IAssociationEnd aggregateEnd = null;
		IAssociationEnd otherEnd = null;
		for(IAssociationEnd ae : associationEnds){
			if(ae.aggregationKind() != IAssociationEnd.REGULAR){
				aggregateEnd = ae;
			} else {
				otherEnd = ae;
			}
		}
		
		if(aggregateEnd == null || otherEnd == null){
			return Formula.TRUE;
		}
		
		if (aggregateEnd.associatedClass().equals(otherEnd.associatedClass())
				|| aggregateEnd.associatedClass().allParents().contains(otherEnd.associatedClass())
				|| aggregateEnd.associatedClass().allChildren().contains(otherEnd.associatedClass())) {
			// construct simple constraint
			Relation startRelation = aggregateEnd.associatedClass().inheritanceOrRegularRelation();
			
			// startRelation->forAll( s | s->closure( relation )->excludes( s ))
			// all c : one startRelation { c \notin c->closure( c' | c'.relation ) }
			
			Variable y = Variable.unary("y");
			Variable cls = Variable.unary("cls");
			Expression closureExp = cls.join(relation());
			
			Expression generalClosure = y.in(closureExp).comprehension(cls.oneOf(startRelation).and(y.oneOf(startRelation))).closure();
			Variable start = Variable.unary("start");
			Formula forAllExp = start.in(start.join(generalClosure)).not();
			
			return forAllExp.forAll( start.oneOf(startRelation) );
		}
		//TODO non-reflexive case(s)
		
		return Formula.TRUE;
	}

	/**
	 * Creates the formula for the type definition constraint.
	 */
	protected Formula typeDefinitions() {
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

			expression = ConstraintHelper.univLeftN(relation(), i);
			for (int j = 0; j < temporary.size() - i - 1; j++) { // helper.univ_r
				expression = expression.join(Expression.UNIV);
			}

			if (associationEnd.multiplicity() != null && associationEnd.multiplicity().isZeroOne() && isBinaryAssociation()
					|| (associationEnd.equals(associationClass) && isBinaryAssociation())) {
				formula = expression.in(associationEnd.associatedClass().inheritanceOrRegularRelation().union(
						model.typeFactory().undefinedType().relation()));
			} else {
				formula = expression.in(associationEnd.associatedClass().inheritanceOrRegularRelation());
			}

			formulas.add(formula);
			LOG.debug("Type of " + name() + ": " + PrintHelper.prettyKodkod(formula));
		}
		return Formula.and(formulas);
	}
	
	/**
	 * Creates the formula for the multiplicity constraints.
	 */
	protected Formula multiplicityDefinitions() {
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
							formula = lowerFormula.and(upperFormula);
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
			Expression product = associatedClass.inheritanceOrRegularRelation();

			for (int j = 1; j < associationEnds.size(); j++) {
				associatedClass = associationEnds.get(j).associatedClass();
				product = product.product(associatedClass.inheritanceOrRegularRelation());
			}

			formula2 = formula2.and(linkedObjects.in(product));
		}

		formula2 = formula2.forAll(variableDeclarations);
		LOG.debug("Mult for association class " + name() + ": " + PrintHelper.prettyKodkod(formula2));

		return formula1.and(formula2);
	}

	/**
	 * Creates the formula for an association end with multiplicity 0..1.
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
	 */
	private Decls createVariableDeclaration(List<Variable> variables, IAssociationEnd associationEnd) {
		Decls variableDeclaration = null;
		for (int j = 0; j < associationEnds.size(); j++) {
			if (associationEnds.get(j) != associationEnd) {
				variables.add(Variable.unary("v" + (j + 1)));
				IClass associatedClass = associationEnds.get(j).associatedClass();
				if (variables.size() == 1) {
					variableDeclaration = variables.get(0).oneOf(associatedClass.inheritanceOrRegularRelation());
				} else {
					variableDeclaration = variableDeclaration.and(variables.get(variables.size() - 1).oneOf(
							associatedClass.inheritanceOrRegularRelation()));
				}
			}
		}
		return variableDeclaration;
	}

	/**
	 * Returns an expression with the linked objects of an association end.
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
		return Collections.unmodifiableList(associationEnds);
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
	public boolean isAssociationClass() {
		return associationClass != null;
	}
	
	@Override
	public boolean isBinaryAssociation() {
		return associationEnds.size() == 2;
	}

	@Override
	public void setConfigurator(IConfigurator<IAssociation> configurator) {
		this.configurator = configurator;
	}

	@Override
	public IConfigurator<IAssociation> getConfigurator() {
		return configurator;
	}

	@Override
	public void resetConfigurator() {
		configurator = new AssociationConfigurator();
	}
}
