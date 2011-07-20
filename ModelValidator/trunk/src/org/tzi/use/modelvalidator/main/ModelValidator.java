package org.tzi.use.modelvalidator.main;

import java.util.ArrayList;
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
				atoms.add(concreteMandatoryObject);
			}
			for (int i = classConfiguration.getConcreteMandatoryObjects()
					.size(); i < classConfiguration.getMaximumNumberOfObjects(); i++) {
				
			}
		}
		// for (int i = 0; i < person_specific_o.size(); i++) {
		// atoms.add("Object_" + person_specific_o.get(i));
		// }
		// for (int i = person_specific_o.size(); i < person_max_o; i++) {
		// atoms.add("Object_person" + (i + 1));
		// }
	}
}
