package org.tzi.use.modelvalidator.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kodkod.ast.Relation;

import org.tzi.use.modelvalidator.configuration.ClassConfiguration;

public class ModelValidator {

	List<ClassConfiguration> classConfigurations;

	List<Relation> classRelations = new ArrayList<Relation>();
	List<Object> atoms = new ArrayList<Object>();

	public ModelValidator(List<ClassConfiguration> classConfigurations) {
		this.classConfigurations = classConfigurations;
	}

	public void translateUML() {
		createClassRelations();
		createObjectAtoms();
		System.out.println(classRelations);
		System.out.println(atoms);
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
	 min number erhöhen bei concrete mandatory additional
	}
}
