package org.tzi.use.plugins.xmihandler;

import java.io.File;
import java.io.IOException;
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
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MGeneralization;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.Log;


//@SuppressWarnings("unused")
public class XMIImporter {

  /**********************************************************************************************
   ** import helper methods **
   **********************************************************************************************/

  private static void createEnumerations(MModel useModel,
      EList<Element> allResourceElements) {
    for (Element elem : allResourceElements) {
      if (elem instanceof org.eclipse.uml2.uml.Enumeration) {
        org.eclipse.uml2.uml.Enumeration enumeration = (org.eclipse.uml2.uml.Enumeration) elem;
        if (useModel.enumType(enumeration.getName()) == null) {
          List<String> literals = new ArrayList<String>();
          for (EnumerationLiteral literal : enumeration.getOwnedLiterals()) {
            literals.add(literal.getName());
          }
          try {
            useModel.addEnumType(TypeFactory.mkEnum(enumeration.getName(),
                literals));
          } catch (MInvalidModelException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private static void createClasses(MModel useModel, EList<Element> allResourceElements) {
    for (Element elem : allResourceElements) {
      if (elem instanceof org.eclipse.uml2.uml.Class) {
        org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;
        if (useModel.getClass(umlClass.getName()) == null) {
          MClass useClass = Utils.getModelFactory().createClass(umlClass.getName(),
              umlClass.isAbstract());
          try {
            useModel.addClass(useClass);
          } catch (MInvalidModelException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private static void createAttributes(MModel useModel,
      EList<Element> allResourceElements) {
    for (Element elem : allResourceElements) {

      if (elem instanceof org.eclipse.uml2.uml.Class) {

        org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;

        MClass useClass = useModel.getClass(umlClass.getName());

        for (Property prop : umlClass.getAllAttributes()) {

          MAttribute attr = null;

          String propName = (prop.getName() == null || prop.getName().isEmpty()) ? prop
              .getType().getName()
              : prop.getName();

          if (prop.getType() instanceof PrimitiveTypeImpl) {
            boolean isSet = false;
            boolean isOrderedSet = false;
            boolean isBag = false;

            if (prop.getUpper() == LiteralUnlimitedNatural.UNLIMITED) {
              if (prop.isUnique()) {
                if (prop.isOrdered()) {
                  isOrderedSet = true;
                } else {
                  isSet = true;
                }
              } else {
                isBag = true;
              }
            }

            if (prop.getType().getName().equals("String")) {
              if (isSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkString()));
              } else if (isOrderedSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkString()));
              } else if (isBag) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkString()));
              } else {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkString());
              }
            } else if (prop.getType().getName().equals("Integer")) {
              if (isSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkInteger()));
              } else if (isOrderedSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkInteger()));
              } else if (isBag) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkInteger()));
              } else {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkInteger());
              }
            } else if (prop.getType().getName().equals("Boolean")) {
              if (isSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkBoolean()));
              } else if (isOrderedSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkBoolean()));
              } else if (isBag) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkBoolean()));
              } else {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkBoolean());
              }
            } else if (prop.getType().getName().equals("Real")) {
              if (isSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkReal()));
              } else if (isOrderedSet) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkReal()));
              } else if (isBag) {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkReal()));
              } else {
                attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                    .mkReal());
              }
            }
          } else if (prop.getType() instanceof ClassImpl) {
            attr = Utils.getModelFactory().createAttribute(propName, TypeFactory
                .mkObjectType(useModel.getClass(prop.getType().getName())));
          } else if (prop.getType() instanceof EnumerationImpl) {
            attr = Utils.getModelFactory().createAttribute(propName, useModel
                .enumType(prop.getType().getName()));
          }

          if (attr != null) {
            try {
              useClass.addAttribute(attr);
            } catch (MInvalidModelException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  private static void createGeneralizations(MModel useModel,
      EList<Element> allResourceElements) {
    DirectedGraph<MClass, MGeneralization> genGraph = useModel
        .generalizationGraph();
    for (Element elem : allResourceElements) {
      if (elem instanceof org.eclipse.uml2.uml.Class) {
        org.eclipse.uml2.uml.Class childClass = (org.eclipse.uml2.uml.Class) elem;
        for (Generalization gen : childClass.getGeneralizations()) {
          for (Element el : gen.getTargets()) {
            org.eclipse.uml2.uml.Class parentClass = (org.eclipse.uml2.uml.Class) el;
            genGraph.addEdge(Utils.getModelFactory().createGeneralization(useModel
                .getClass(childClass.getName()), useModel.getClass(parentClass
                .getName())));
          }
        }
      }
    }
  }

  private static void createAssociations(MModel useModel,
      EList<Element> allResourceElements) {

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

          MAssociationEnd assocLeftEnd = Utils.getModelFactory().createAssociationEnd(
              assocEndClass, (assocEnd.getName() == null || assocEnd.getName()
                  .isEmpty()) ? assocEnd.getType().getName() : assocEnd
                  .getName(), m1, assocEndAggregationKind,
              assocEnd.isOrdered(), emptyQualifiers);

          assocEnds.add(assocLeftEnd);

        }

        if (assocEnds.size() < 2) {
          continue;
        }

        String assocName = theAssoc.getName();

        if (assocName == null || assocName.isEmpty()) {
          for (int i = 0; i < theAssoc.getMemberEnds().size(); i++) {
            assocName += theAssoc.getMemberEnds().get(i).getType().getName();
            if (i < theAssoc.getMemberEnds().size() - 1) {
              assocName += "_";
            }
          }
        }

        MAssociation assoc = Utils.getModelFactory().createAssociation(assocName);

        for (MAssociationEnd end : assocEnds) {
          try {
            assoc.addAssociationEnd(end);
          } catch (MInvalidModelException e) {
            e.printStackTrace();
          }
        }

        try {
          useModel.addAssociation(assoc);
        } catch (MInvalidModelException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        }

      }
    }
  }

  private static Model findModel(Resource resource, String modelName) {
    for (Object obj : EcoreUtil.getObjectsByType(resource.getContents(),
        UMLPackage.Literals.ELEMENT)) {
      org.eclipse.uml2.uml.Element elem = (org.eclipse.uml2.uml.Element) obj;
      if (elem instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl
          && ((org.eclipse.uml2.uml.internal.impl.ModelImpl) elem).getName()
              .equalsIgnoreCase(modelName)) {
        return (org.eclipse.uml2.uml.internal.impl.ModelImpl) elem;
      }

      Model model = findModelRecursive(elem.getOwnedElements(), modelName);
      if (model != null) {
        return model;
      }
    }
    return null;
  }

  private static Model findModelRecursive(EList<Element> list, String modelName) {
    for (Element elem : list) {
      if (elem instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl
          && ((org.eclipse.uml2.uml.internal.impl.ModelImpl) elem).getName()
              .equalsIgnoreCase(modelName)) {
        return (org.eclipse.uml2.uml.internal.impl.ModelImpl) elem;
      }
      Model model = findModelRecursive(elem.getOwnedElements(), modelName);
      if (model != null) {
        return model;
      }
    }
    return null;
  }

  private static EList<Element> agregateElements(Resource resource) {
    EList<Element> list = new BasicEList<Element>();
    for (Object obj : EcoreUtil.getObjectsByType(resource.getContents(),
        UMLPackage.Literals.ELEMENT)) {
      org.eclipse.uml2.uml.Element elem = (org.eclipse.uml2.uml.Element) obj;
      list.add(elem);
      list.addAll(agregateElementsRecursive(elem.getOwnedElements()));
    }

    return list;
  }

  private static EList<Element> agregateElementsRecursive(EList<Element> elements) {
    EList<Element> list = new BasicEList<Element>();

    for (Element elem : elements) {
      if (elem instanceof org.eclipse.uml2.uml.Package) {
        list.addAll(agregateElementsRecursive(elem.getOwnedElements()));
      } else if (elem instanceof org.eclipse.uml2.uml.Model) {
        list.addAll(agregateElementsRecursive(elem.getOwnedElements()));
      } else {
        list.add(elem);
      }
    }

    return list;
  }

  /**********************************************************************************************
   ** xmi import **
   **********************************************************************************************/

  public static void importFromXMI(File file, Session session) {
    // Get the URI of the model file.
    URI fileURI = URI.createFileURI(file.getAbsolutePath());
    Utils.out(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + fileURI.path());

    // Create a resource for this file.
    Resource resource = Utils.getResource(fileURI);

    try {
      resource.load(Collections.EMPTY_MAP);
    } catch (IOException e) {
    }

    Model umlModel = findModel(resource, file.getName().replaceFirst(
        "[.][^.]+$", ""));

    EList<Element> allResourceElements = agregateElements(resource);

    if (umlModel == null) {
      Log.error("Import is impossible, bad model");
      return;
    }

    MModel useModel = Utils.getModelFactory().createModel(umlModel.getName());

    MSystem system = new MSystem(useModel);

    createEnumerations(useModel, allResourceElements);

    createClasses(useModel, allResourceElements);

    createAttributes(useModel, allResourceElements);

    createGeneralizations(useModel, allResourceElements);

    createAssociations(useModel, allResourceElements);

    session.setSystem(system);

    Utils.out("Imported: " + umlModel.getName());

  }

}
