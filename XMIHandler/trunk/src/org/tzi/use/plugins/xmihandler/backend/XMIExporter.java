package org.tzi.use.plugins.xmihandler.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.main.Session;
import org.tzi.use.plugins.xmihandler.utils.Utils;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.uml.mm.MGeneralization;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;

public class XMIExporter {

	/**********************************************************************************************
	 ** export helper methods **
	 **********************************************************************************************/

	private static Model createModel(String name) {
		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);

		return model;
	}

	private static Type getOrCreateType(
			org.eclipse.uml2.uml.Package package_, String name) {

		Type theType = package_.getOwnedType(name);

		if (theType == null) {
			theType = package_.createOwnedPrimitiveType(name);
		}

		return theType;
	}

	private static Enumeration createEnumeration(
			org.eclipse.uml2.uml.Package package_, String name) {
		Enumeration enumeration = package_.createOwnedEnumeration(name);

		return enumeration;
	}

	private static EnumerationLiteral createEnumerationLiteral(
			Enumeration enumeration, String name) {
		EnumerationLiteral enumerationLiteral = enumeration
				.createOwnedLiteral(name);

		return enumerationLiteral;
	}

	private static org.eclipse.uml2.uml.Class createClass(
			org.eclipse.uml2.uml.Package package_, String name, boolean isAbstract) {
		org.eclipse.uml2.uml.Class class_ = package_.createOwnedClass(name,
				isAbstract);

		return class_;
	}

	private static Generalization createGeneralization(
			Classifier specificClassifier, Classifier generalClassifier) {
		Generalization generalization = specificClassifier
				.createGeneralization(generalClassifier);

		return generalization;
	}

	private static Property createAttribute(org.eclipse.uml2.uml.Class class_,
			String name, Type type, boolean isUnique, boolean isOrdered,
			int lowerBound, int upperBound) {

		Property attribute = class_.createOwnedAttribute(name, type, lowerBound,
				upperBound);

		attribute.setIsUnique(isUnique);
		attribute.setIsOrdered(isOrdered);

		return attribute;
	}

	private static Association createAssociation(Model umlModel, String name) {
		Association association = (Association) umlModel.createOwnedType(name,
				UMLPackage.Literals.ASSOCIATION);
		return association;
	}

	private static Property createAssociationEnd(Association association,
			Type type, boolean isNavigable, boolean isOrdered,
			AggregationKind aggregationKind, String name, int lower, int upper) {

		Property associationEnd = isNavigable ? association
				.createNavigableOwnedEnd(name, type) : association.createOwnedEnd(name,
						type);

				associationEnd.setName(name);
				associationEnd.setType(type);
				associationEnd.setIsNavigable(isNavigable);
				associationEnd.setIsOrdered(isOrdered);
				associationEnd.setAggregation(aggregationKind);
				associationEnd.setLower(lower);
				associationEnd.setUpper(upper);

				return associationEnd;
	}

	private static void createEnumerations(Model umlModel, MModel useModel) {
		for (EnumType enumType : useModel.enumTypes()) {
			Enumeration enumeration = createEnumeration(umlModel, enumType
					.shortName());
			for (String literal : enumType.getLiterals()) {
				createEnumerationLiteral(enumeration, literal);
			}
		}
	}

	private static void createClasses(Model umlModel, MModel useModel) {
		for (MClass useClass : useModel.classes()) {
			createClass(umlModel, useClass.name(), useClass.isAbstract());
		}
	}

	private static void createAttributes(Model umlModel, MModel useModel) {
		for (MClass useClass : useModel.classes()) {

			org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) umlModel
					.getOwnedType(useClass.name());

			for (MAttribute useAttribute : useClass.attributes()) {

				String typeName = useAttribute.type().shortName();
				int lowerBound = 1;
				int upperBound = 1;
				boolean isUnique = true;
				boolean isOrdered = false;

				if (useAttribute.type().isKindOfSet(VoidHandling.EXCLUDE_VOID) || useAttribute.type().isKindOfBag(VoidHandling.EXCLUDE_VOID)) {
					typeName = ((CollectionType) useAttribute.type()).elemType().shortName();
					upperBound = LiteralUnlimitedNatural.UNLIMITED;
					if (useAttribute.type().isKindOfBag(VoidHandling.EXCLUDE_VOID)) {
						isUnique = false;
					}
				}

				if (useAttribute.type().isKindOfOrderedSet(VoidHandling.EXCLUDE_VOID)) {
					isOrdered = true;
				}

				Type theType = getOrCreateType(umlModel, typeName);

				createAttribute(umlClass, useAttribute.name(), theType, isUnique,
						isOrdered, lowerBound, upperBound);

			}

		}
	}

	private static void createOperations(Model umlModel, MModel useModel) {
		for (MClass useClass : useModel.classes()) {

			org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) umlModel
					.getOwnedType(useClass.name());

			for (MOperation useOperation : useClass.operations()) {

				VarDeclList varDeclList = useOperation.paramList();

				EList<String> parameterNames = new BasicEList<String>();
				EList<Type> parameterTypes = new BasicEList<Type>();

				for (int i = 0; i < varDeclList.size(); i++) {
					VarDecl varDecl = varDeclList.varDecl(i);
					parameterNames.add(varDecl.name());
					parameterTypes.add(getOrCreateType(umlModel, varDecl.type().shortName()));
				}

				Operation operation = null;

				if (useOperation.resultType() != null) {
					Type returnType = getOrCreateType(umlModel, useOperation.resultType().shortName());
					operation = umlClass.createOwnedOperation(useOperation.name(), parameterNames, parameterTypes, returnType);
				} else {
					operation = umlClass.createOwnedOperation(useOperation.name(), parameterNames, parameterTypes);
				}

				if (operation != null) {
					Constraint constr = operation.createBodyCondition(null);
					LiteralString valueSpec = (LiteralString) constr.createSpecification(null, null, UMLPackage.Literals.LITERAL_STRING);
					valueSpec.setValue(useOperation.expression().toString());
				}
			}
		}
	}

	private static void createGeneralizations(Model umlModel, MModel useModel) {
		DirectedGraph<MClassifier, MGeneralization> genGraph = useModel.generalizationGraph();

		for (MClass useClass : useModel.classes()) {

			org.eclipse.uml2.uml.Class parentClass = (org.eclipse.uml2.uml.Class) umlModel
					.getOwnedType(useClass.name());

			Set<MGeneralization> genEdges = genGraph.allEdges(useClass);

			for (MGeneralization gen : genEdges) {

				org.eclipse.uml2.uml.Class childClass = (org.eclipse.uml2.uml.Class) umlModel
						.getOwnedType(gen.target().name());

				if (!parentClass.getName().equals(childClass.getName())) {
					createGeneralization(parentClass, childClass);
				}

			}
		}
	}

	private static void createAssociations(Model umlModel, MModel useModel) {

		for (MAssociation mAssociation : useModel.associations()) {

			Association umlAssoc = createAssociation(umlModel, mAssociation.name());

			for (MAssociationEnd useAssocEnd : mAssociation.associationEnds()) {

				org.eclipse.uml2.uml.Class umlAssocEndClass = (org.eclipse.uml2.uml.Class) umlModel
						.getOwnedMember(useAssocEnd.cls().name());

				AggregationKind umlAssocEndAggregationKind = AggregationKind.NONE_LITERAL;
				switch (useAssocEnd.aggregationKind()) {
				case MAggregationKind.COMPOSITION:
					umlAssocEndAggregationKind = AggregationKind.COMPOSITE_LITERAL;
					break;
				case MAggregationKind.AGGREGATION:
					umlAssocEndAggregationKind = AggregationKind.SHARED_LITERAL;
					break;
				}

				int umlAssocEndLower = 1;
				int umlAssocEndUpper = 1;

				ArrayList<Integer> umlAssocEndMultiplicity = parseMultiplicity(useAssocEnd
						.multiplicity().toString());

				if (umlAssocEndMultiplicity != null) {
					umlAssocEndLower = umlAssocEndMultiplicity.get(0);
					umlAssocEndUpper = umlAssocEndMultiplicity.get(1);
				}

				createAssociationEnd(umlAssoc, umlAssocEndClass, useAssocEnd
						.isNavigable(), useAssocEnd.isOrdered(),
						umlAssocEndAggregationKind, useAssocEnd.nameAsRolename(),
						umlAssocEndLower, umlAssocEndUpper);
			}

		}
	}

	private static ArrayList<Integer> parseMultiplicity(String multiplicity) {
		String[] splitted = multiplicity.split("\\.\\.");
		if (splitted.length < 1 || splitted.length > 2) {
			return null;
		}

		ArrayList<Integer> res = new ArrayList<Integer>();

		if (splitted.length == 1) {
			if (splitted[0].equals("*")) {
				res.add(0);
			} else {
				res.add(Integer.valueOf(splitted[0]));
			}

			res.add(splitted[0].equals("*") ? LiteralUnlimitedNatural.UNLIMITED
					: Integer.valueOf(splitted[0]));
		} else if (splitted.length == 2) {
			res.add(Integer.valueOf(splitted[0]));
			res.add(splitted[1].equals("*") ? LiteralUnlimitedNatural.UNLIMITED
					: Integer.valueOf(splitted[1]));
		}

		return res;
	}

	/**********************************************************************************************
	 ** xmi export **
	 **********************************************************************************************/

	public static void exportToXMI(File file, Session session)
			throws Exception {

		// Get the URI of the model file.
		URI fileURI = URI.createFileURI(file.getAbsolutePath());

		// Create a resource for this file.
		Resource resource = Utils.getResource(fileURI);

		Utils.out("Exporting to file: " + resource.getURI().path());

		final MModel useModel = session.system().model();

		final Model umlModel = createModel(useModel.name());

		createEnumerations(umlModel, useModel);

		createClasses(umlModel, useModel);

		createAttributes(umlModel, useModel);

		//createOperations(umlModel, useModel);

		createGeneralizations(umlModel, useModel);

		createAssociations(umlModel, useModel);

		resource.getContents().add(umlModel);

		// Save the contents of the resource to the file system.
		resource.save(Collections.EMPTY_MAP);

		Utils.out("Exported: " + umlModel.getName());

	}

}
