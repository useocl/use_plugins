package org.tzi.use.modelvalidator.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

import org.tzi.use.modelvalidator.configuration.ClassConfiguration;

public class ModelValidator {

	List<ClassConfiguration> classConfigurations;

	List<Relation> classRelations = new ArrayList<Relation>();
	List<Object> atoms = new ArrayList<Object>();
	
	Bounds bounds;

	public ModelValidator(List<ClassConfiguration> classConfigurations) {
		this.classConfigurations = classConfigurations;
	}

	public void translateUML() {
		createClassRelations();
		createObjectAtoms();

		final Universe universe = new Universe(atoms);
		final TupleFactory factory = universe.factory();
		bounds = new Bounds(universe);

		createClassBounds(bounds, factory);
	}

	public Solution startSearch() {
		
		final Solver solver = new Solver();
		
		solver.options().setSolver(SATFactory.MiniSat);

		// UML constraints
		final Formula uml = Formula.TRUE;

		// OCL constraints
		final Formula ocl = Formula.TRUE;

		// configuration constraints
		final Formula configuration = Formula.TRUE;

		final Formula show = Formula.and(uml, ocl, configuration);
		return solver.solve(show, bounds);
		
	}

	private void createClassRelations() {
		for (ClassConfiguration classConfiguration : classConfigurations) {
			classRelations.add(Relation.unary("Class_"
					+ classConfiguration.getCls().name()));
		}
	}

	private void createObjectAtoms() {
		for (ClassConfiguration classConfiguration : classConfigurations) {
			for (String concreteMandatoryObject : classConfiguration
					.getConcreteMandatoryObjects()) {
				atoms.add("Object_" + concreteMandatoryObject);
			}
			Iterator<String> concreteOptionalObjectIter = classConfiguration
					.getConcreteOptionalObjects().iterator();
			for (int i = classConfiguration.getConcreteMandatoryObjects()
					.size(); i < classConfiguration.getMaximumNumberOfObjects(); i++) {
				if (concreteOptionalObjectIter.hasNext()) {
					atoms.add("Object_" + concreteOptionalObjectIter.next());
				} else {
					atoms.add("Object_"
							+ classConfiguration.getCls().name().toLowerCase()
							+ (i + 1));
				}
			}
		}
	}

	private void createClassBounds(Bounds b, TupleFactory f) {
		for (ClassConfiguration classConfiguration : classConfigurations) {
			final TupleSet lowerBound = f.noneOf(1);

			for (String concreteMandatoryObject : classConfiguration
					.getConcreteMandatoryObjects()) {
				lowerBound.add(f.tuple("Object_" + concreteMandatoryObject));
			}

			Iterator<String> concreteOptionalObjectIter = classConfiguration
					.getConcreteOptionalObjects().iterator();
			for (int i = classConfiguration.getConcreteMandatoryObjects()
					.size(); i < classConfiguration.getMinimumNumberOfObjects(); i++) {
				if (concreteOptionalObjectIter.hasNext()) {
					lowerBound.add(f.tuple("Object_"
							+ concreteOptionalObjectIter.next()));
				} else {
					lowerBound.add(f.tuple("Object_"
							+ classConfiguration.getCls().name().toLowerCase()
							+ (i + 1)));
				}
			}

			final TupleSet upperBound = f.noneOf(1);
			upperBound.addAll(lowerBound);

			for (int i = lowerBound.size(); i < classConfiguration
					.getMaximumNumberOfObjects(); i++) {
				if (concreteOptionalObjectIter.hasNext()) {
					upperBound.add(f.tuple("Object_"
							+ concreteOptionalObjectIter.next()));
				} else {
					upperBound.add(f.tuple("Object_"
							+ classConfiguration.getCls().name().toLowerCase()
							+ (i + 1)));
				}
			}

			Relation classRelation = null;
			for (Relation relation : classRelations) {
				if (relation.name().equals(
						"Class_" + classConfiguration.getCls().name())) {
					classRelation = relation;
				}
			}

			b.bound(classRelation, lowerBound, upperBound);

		}
	}

}
