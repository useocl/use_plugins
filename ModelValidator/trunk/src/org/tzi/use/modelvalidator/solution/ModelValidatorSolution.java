package org.tzi.use.modelvalidator.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

import org.tzi.use.modelvalidator.configuration.ClassConfiguration;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;

public class ModelValidatorSolution {

	private Map<MClass, List<String>> classObjectsMap = new HashMap<MClass, List<String>>();

	public ModelValidatorSolution(Solution solution,
			List<ClassConfiguration> classConfigurations) {
		for (ClassConfiguration classConfiguration : classConfigurations) {
			MClass cls = classConfiguration.getCls();

			Relation clsRelation = null;
			for (Relation relation : solution.instance().relations()) {
				if (relation.name().equals("Class_" + cls.name())) {
					clsRelation = relation;
				}
			}

			TupleSet objectTuples = solution.instance().tuples(clsRelation);

			List<String> objectNames = new ArrayList<String>();
			for (Tuple objectTuple : objectTuples) {
				objectNames.add(objectTuple.toString());
			}

			classObjectsMap.put(cls, objectNames);

		}
	}

	public void setSnapshot(MSystem system) {
		for (MClass cls : classObjectsMap.keySet()) {
			for (String objectName : classObjectsMap.get(cls)) {
				try {
					String validObjectName = objectName.replaceAll(
							Pattern.quote("[Object_"), "").replaceAll(
							Pattern.quote("]"), "");
					if(!system.state().allObjectNames().contains(validObjectName)) {
						system.evaluateStatement(new MNewObjectStatement(cls,
							validObjectName));
					}
				} catch (MSystemException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
