package org.tzi.use.plugins.xmihandler.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.ModelImpl;
import org.eclipse.uml2.uml.internal.impl.NamedElementImpl;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;
import org.eclipse.uml2.uml.internal.impl.PackageImportImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;
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
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.sys.MSystem;

//@SuppressWarnings("unused")
public class XMIImporter {

	/**********************************************************************************************
	 ** import helper methods **
	 **********************************************************************************************/

	private static void createEnumerations(MModel useModel,
			EList<Element> allResourceElements) throws Exception {
		for (Element elem : allResourceElements) {
			if (elem instanceof Enumeration) {
				Enumeration enumeration = (Enumeration) elem;
				if (useModel.enumType(enumeration.getName()) == null) {
					List<String> literals = new ArrayList<String>();
					for (EnumerationLiteral literal : enumeration.getOwnedLiterals()) {
						literals.add(literal.getName());
					}
					useModel.addEnumType(TypeFactory.mkEnum(enumeration.getName(),
							literals));
				}
			}
		}
	}

	private static void createClasses(MModel useModel,
			EList<Element> allResourceElements) throws Exception {
		for (Element elem : allResourceElements) {
			if (elem instanceof org.eclipse.uml2.uml.Class) {
				org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;
				if (useModel.getClass(umlClass.getName()) == null) {
					MClass useClass = Utils.getModelFactory().createClass(
							umlClass.getName(), umlClass.isAbstract());
					useModel.addClass(useClass);
				}
			}
		}
	}

	private static Type convertType(MModel useModel, TypedElement prop) {
		boolean isSet = false;
		boolean isOrderedSet = false;
		boolean isBag = false;

		Type convertedType = null;

		if (prop != null) {
			if (prop.getType() instanceof PrimitiveTypeImpl) {
				if (prop instanceof MultiplicityElement) {
					if (((MultiplicityElement)prop).getUpper() == LiteralUnlimitedNatural.UNLIMITED) {
						if (((MultiplicityElement)prop).isUnique()) {
							if (((MultiplicityElement)prop).isOrdered()) {
								isOrderedSet = true;
							} else {
								isSet = true;
							}
						} else {
							isBag = true;
						}
					}
				}

				if (prop.getType().getName().equals("String")) {
					if (isSet) {
						convertedType = TypeFactory.mkSet(TypeFactory.mkString());
					} else if (isOrderedSet) {
						convertedType = TypeFactory.mkOrderedSet(TypeFactory.mkString());
					} else if (isBag) {
						convertedType = TypeFactory.mkBag(TypeFactory.mkString());
					} else {
						convertedType = TypeFactory.mkString();
					}
				} else if (prop.getType().getName().equals("Integer")) {
					if (isSet) {
						convertedType = TypeFactory.mkSet(TypeFactory.mkInteger());
					} else if (isOrderedSet) {
						convertedType = TypeFactory.mkOrderedSet(TypeFactory.mkInteger());
					} else if (isBag) {
						convertedType = TypeFactory.mkBag(TypeFactory.mkInteger());
					} else {
						convertedType = TypeFactory.mkInteger();
					}
				} else if (prop.getType().getName().equals("Boolean")) {
					if (isSet) {
						convertedType = TypeFactory.mkSet(TypeFactory.mkBoolean());
					} else if (isOrderedSet) {
						convertedType = TypeFactory.mkOrderedSet(TypeFactory.mkBoolean());
					} else if (isBag) {
						convertedType = TypeFactory.mkBag(TypeFactory.mkBoolean());
					} else {
						convertedType = TypeFactory.mkBoolean();
					}
				} else if (prop.getType().getName().equals("Real")) {
					if (isSet) {
						convertedType = TypeFactory.mkSet(TypeFactory.mkReal());
					} else if (isOrderedSet) {
						convertedType = TypeFactory.mkOrderedSet(TypeFactory.mkReal());
					} else if (isBag) {
						convertedType = TypeFactory.mkBag(TypeFactory.mkReal());
					} else {
						convertedType = TypeFactory.mkReal();
					}
				}
			} else if (prop.getType() instanceof ClassImpl) {
				convertedType = useModel.getClass(prop.getType().getName());
			} else if (prop.getType() instanceof EnumerationImpl) {
				convertedType = useModel.enumType(prop.getType().getName());
			}
		}

		return convertedType;
	}

	private static void createAttributes(MModel useModel,
			EList<Element> allResourceElements) throws Exception {
		for (Element elem : allResourceElements) {

			if (elem instanceof org.eclipse.uml2.uml.Class) {

				org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;

				MClass useClass = useModel.getClass(umlClass.getName());

				for (Property prop : umlClass.getAttributes()) {

					MAttribute attr = null;

					String propName = (prop.getName() == null || prop.getName().isEmpty()) ?
							prop.getType().getName()
							: prop.getName();

							attr = Utils.getModelFactory().createAttribute(propName, convertType(useModel, prop));

							if (attr != null) {
								useClass.addAttribute(attr);
							}
				}
			}
		}
	}

	private static void createOperations(MModel useModel,
			EList<Element> allResourceElements) throws Exception {
		for (Element elem : allResourceElements) {

			if (elem instanceof org.eclipse.uml2.uml.Class) {

				org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;

				MClass useClass = useModel.getClass(umlClass.getName());

				for (Operation operation : umlClass.getOperations()) {
					EList<Parameter> parameters = operation.getOwnedParameters();
					Type returnType = null;
					VarDeclList varDeclList = null;
					for (Parameter p : parameters) {
						if (p.getDirection() != ParameterDirectionKind.RETURN_LITERAL) {
							VarDecl varDecl = new VarDecl(p.getName(), convertType(useModel, p));
							if (varDeclList == null) {
								varDeclList = new VarDeclList(varDecl);
							} else {
								varDeclList.add(varDecl);
							}
						}
					}
					if (varDeclList == null) {
						varDeclList = new VarDeclList(true);
					}
					returnType = convertType(useModel, operation.getReturnResult());
					MOperation useOperation = Utils.getModelFactory().
							createOperation(operation.getName(), varDeclList,
									returnType);
					useClass.addOperation(useOperation);
				}
			}
		}
	}

	private static void createGeneralizations(MModel useModel,
			EList<Element> allResourceElements) {
		DirectedGraph<MClassifier, MGeneralization> genGraph = useModel.generalizationGraph();
		for (Element elem : allResourceElements) {
			if (elem instanceof org.eclipse.uml2.uml.Class) {
				org.eclipse.uml2.uml.Class childClass = (org.eclipse.uml2.uml.Class) elem;
				for (Generalization gen : childClass.getGeneralizations()) {
					for (Element el : gen.getTargets()) {
						org.eclipse.uml2.uml.Class parentClass = (org.eclipse.uml2.uml.Class) el;
						genGraph.addEdge(Utils.getModelFactory().createGeneralization(
								useModel.getClass(childClass.getName()),
								useModel.getClass(parentClass.getName())));
					}
				}
			}
		}
	}

	private static void createAssociations(MModel useModel,
			EList<Element> allResourceElements) throws Exception {

		for (Element elem : allResourceElements) {

			if (elem instanceof Association) {
				Association theAssoc = (Association) elem;

				ArrayList<MAssociationEnd> assocEnds = new ArrayList<MAssociationEnd>();
				List<VarDecl> emptyQualifiers = Collections.emptyList();

				for (Property assocEnd : theAssoc.getMemberEnds()) {

					if (useModel.getClass(assocEnd.getType().getName()) == null) {
						break;
					}

					MClass assocEndClass = useModel
							.getClass(assocEnd.getType().getName());

					MMultiplicity m1 = Utils.getModelFactory().createMultiplicity();

					m1.addRange(assocEnd.getLower(), assocEnd.getUpper());

					int assocEndAggregationKind = MAggregationKind.NONE;
					switch (assocEnd.getAggregation()) {
					case COMPOSITE_LITERAL:
						assocEndAggregationKind = MAggregationKind.COMPOSITION;
						break;
					case SHARED_LITERAL:
						assocEndAggregationKind = MAggregationKind.AGGREGATION;
						break;
					}

					MAssociationEnd assocLeftEnd = Utils
							.getModelFactory()
							.createAssociationEnd(
									assocEndClass,
									(assocEnd.getName() == null || assocEnd.getName().isEmpty()) ? Utils.getXmiId(assocEnd)
											: assocEnd.getName(), m1, assocEndAggregationKind,
											assocEnd.isOrdered(), emptyQualifiers);

					assocEnds.add(assocLeftEnd);

				}

				if (assocEnds.size() < 2) {
					continue;
				}

				try {
					MAssociation assoc = Utils.getModelFactory().createAssociation(
							theAssoc.getName());
					for (MAssociationEnd end : assocEnds) {
						assoc.addAssociationEnd(end);
					}
					useModel.addAssociation(assoc);
				} catch (MInvalidModelException e) {
				}

			}
		}
	}

	private static EList<Element> agregateElements(Resource resource) {
		EList<Element> list = new BasicEList<Element>();
		for (Object obj : EcoreUtil.getObjectsByType(resource.getContents(),
				UMLPackage.Literals.ELEMENT)) {
			Element elem = (Element) obj;
			list.addAll(agregateElementsRecursive(elem));
		}

		return list;
	}

	private static EList<Element> agregateElementsRecursive(Element parentElem) {

		EList<Element> list = new BasicEList<Element>();

		list.add(parentElem);

		for (Element elem : parentElem.getOwnedElements()) {
			if (elem instanceof PackageImpl
					|| elem instanceof ModelImpl) {
				list.addAll(agregateElementsRecursive(elem));
			} if (elem instanceof PackageImportImpl) {
				list.addAll(agregateElementsRecursive(((PackageImportImpl) elem).getImportedPackage()));
			} else {
				list.add(elem);
			}
		}

		return list;
	}

	private static void addModelNamePrefixes(EList<Element> allResourceElements) {
		for (Element elem : allResourceElements) {
			Model ownerModel = getOwnerModel(elem);
			if (!(elem instanceof ModelImpl)
					&& elem instanceof NamedElementImpl
					&& !((NamedElement) elem).getName().trim().isEmpty()
					&& !(ownerModel == null) && !ownerModel.getName().trim().isEmpty()) {
				((NamedElement) elem).setName(ownerModel.getName() + "::"
						+ ((NamedElement) elem).getName());
			}
		}
	}

	private static void fixMissingElementNames(EList<Element> allResourceElements) {
		for (Element elem : allResourceElements) {
			if ((elem instanceof NamedElementImpl) &&
					((((NamedElementImpl)elem).getName() == null) ||
							((NamedElementImpl)elem).getName().trim().isEmpty())) {
				((NamedElementImpl)elem).setName(Utils.getXmiId(elem));
			}
		}
	}

	private static Model getOwnerModel(Element elem) {
		Element ownerElem = elem.getOwner();
		while (!((ownerElem instanceof ModelImpl) || (ownerElem == null))) {
			ownerElem = ownerElem.getOwner();
		}
		return (ModelImpl) ownerElem;
	}

	private static Model getUmlModel(EList<Element> allResourceElements,
			String modelName) throws Exception {

		EList<Model> modelList = new BasicEList<Model>();

		for (Element elem : allResourceElements) {
			if (elem instanceof ModelImpl) {
				modelList.add((ModelImpl) elem);
			}
		}

		if (modelList.isEmpty()) {
			throw new Exception ("No valid model found");
		}

		if (modelList.size() > 1) {
			//addModelNamePrefixes(allResourceElements);
		}

		for (Model m : modelList) {
			if (m.getName().equalsIgnoreCase(modelName)) {
				return m;
			}
		}

		return modelList.get(0);
	}

	private static String getModelStatsStr(MModel useModel) {
		String stats = "";

		int classCnt = useModel.classes().size();
		int assocCnt = useModel.associations().size();

		stats = "(" + classCnt + " " + (classCnt == 1 ? "class" : "classes") + ", " +
				assocCnt + " " + (assocCnt == 1 ? "association" : "associations") + ")";

		return stats;
	}

	/**********************************************************************************************
	 ** xmi import **
	 **********************************************************************************************/

	public static void importFromXMI(File file, Session session)
			throws Exception {

		// Get the URI of the model file.
		URI fileURI = URI.createFileURI(file.getAbsolutePath());

		// Create a resource for this file.
		Resource resource = Utils.getResource(fileURI);

		Utils.out("Importing from file: " + resource.getURI().path());

		resource.load(null);

		EList<Element> allResourceElements = agregateElements(resource);

		fixMissingElementNames(allResourceElements);

		Model umlModel = getUmlModel(allResourceElements, file.getName().replaceFirst("\\.[^.]+$", ""));

		MModel useModel = Utils.getModelFactory().createModel(umlModel.getName());

		createEnumerations(useModel, allResourceElements);

		createClasses(useModel, allResourceElements);

		createAttributes(useModel, allResourceElements);

		createOperations(useModel, allResourceElements);

		createGeneralizations(useModel, allResourceElements);

		createAssociations(useModel, allResourceElements);

		session.setSystem(new MSystem(useModel));

		Utils.out("Imported: " + umlModel.getName() + " " + getModelStatsStr(useModel));

	}

}
