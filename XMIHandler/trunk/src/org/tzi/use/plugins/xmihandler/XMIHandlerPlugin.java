package org.tzi.use.plugins.xmihandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.uml2.common.edit.domain.UML2AdapterFactoryEditingDomain;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.AssociationClass;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Signal;
import org.eclipse.uml2.uml.StructuredClassifier;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.edit.providers.UMLItemProviderAdapterFactory;
import org.eclipse.uml2.uml.edit.providers.UMLReflectiveItemProviderAdapterFactory;
import org.eclipse.uml2.uml.edit.providers.UMLResourceItemProviderAdapterFactory;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLExtendedMetaData;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resource.XMI212UMLResource;
import org.eclipse.uml2.uml.resource.XMI2UMLExtendedMetaData;
import org.eclipse.uml2.uml.resource.XMI2UMLResource;
import org.eclipse.uml2.uml.util.UMLSwitch;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.IPluginRuntime;
import org.tzi.use.runtime.impl.Plugin;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MGeneralization;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.util.Log;

@SuppressWarnings("unused")
public class XMIHandlerPlugin extends Plugin {

  private static String PLUGIN_NAME = "XMIHandler";

  private static ResourceSet resourceSet = new ResourceSetImpl();

  private static ModelFactory modelFactory = new ModelFactory();

  public static XMIHandlerPlugin getXMIHandlerPluginInstance() {
    return (XMIHandlerPlugin) pluginInstance;
  }

  @Override
  public String getName() {
    return PLUGIN_NAME;
  }

  @Override
  protected void doRun(IPluginRuntime pluginRuntime) {
    initialize();
  }

  private static void out(String output) {
    System.out.println(output);
  }

  private static void err(String error) {
    System.err.println(error);
  }

  /**********************************************************************************************
   ** export helper methods **
   **********************************************************************************************/

  private static Model createModel(String name) {
    Model model = UMLFactory.eINSTANCE.createModel();
    model.setName(name);

    out("Model '" + model.getQualifiedName() + "' created.");

    return model;
  }

  private static org.eclipse.uml2.uml.Package createPackage(
      org.eclipse.uml2.uml.Package nestingPackage, String name) {
    org.eclipse.uml2.uml.Package package_ = nestingPackage
        .createNestedPackage(name);

    out("Package '" + package_.getQualifiedName() + "' created.");

    return package_;
  }

  private static PrimitiveType createPrimitiveType(
      org.eclipse.uml2.uml.Package package_, String name) {
    PrimitiveType primitiveType = (PrimitiveType) package_
        .createOwnedPrimitiveType(name);

    out("Primitive type '" + primitiveType.getQualifiedName() + "' created.");

    return primitiveType;
  }

  private static Enumeration createEnumeration(
      org.eclipse.uml2.uml.Package package_, String name) {
    Enumeration enumeration = (Enumeration) package_
        .createOwnedEnumeration(name);

    out("Enumeration '" + enumeration.getQualifiedName() + "' created.");

    return enumeration;
  }

  private static EnumerationLiteral createEnumerationLiteral(
      Enumeration enumeration, String name) {
    EnumerationLiteral enumerationLiteral = enumeration
        .createOwnedLiteral(name);

    out("Enumeration literal '" + enumerationLiteral.getQualifiedName()
        + "' created.");

    return enumerationLiteral;
  }

  private static org.eclipse.uml2.uml.Class createClass(
      org.eclipse.uml2.uml.Package package_, String name, boolean isAbstract) {
    org.eclipse.uml2.uml.Class class_ = package_.createOwnedClass(name,
        isAbstract);

    out("Class '" + class_.getQualifiedName() + "' created.");

    return class_;
  }

  private static Generalization createGeneralization(
      Classifier specificClassifier, Classifier generalClassifier) {
    Generalization generalization = specificClassifier
        .createGeneralization(generalClassifier);

    out("Generalization " + specificClassifier.getQualifiedName() + " ->> "
        + generalClassifier.getQualifiedName() + " created.");

    return generalization;
  }

  private static Property createAttribute(org.eclipse.uml2.uml.Class class_,
      String name, Type type, boolean isUnique, boolean isOrdered,
      int lowerBound, int upperBound) {

    Property attribute = class_.createOwnedAttribute(name, type, lowerBound,
        upperBound);

    attribute.setIsUnique(isUnique);
    attribute.setIsOrdered(isOrdered);

    out("Attribute " + attribute.getQualifiedName() + " : "
        + type.getQualifiedName() + " created");

    return attribute;
  }

  private static Association createAssociation(Model umlModel, String name) {
    Association association = (Association) umlModel.createOwnedType(name,
        UMLPackage.Literals.ASSOCIATION);
    out("Association " + name + " created.");
    return association;
  }

  private static Association createAssociation(String name,
      org.eclipse.uml2.uml.Class type1, boolean end1IsNavigable,
      boolean end1IsOrdered, AggregationKind end1Aggregation, String end1Name,
      int end1LowerBound, int end1UpperBound, org.eclipse.uml2.uml.Class type2,
      boolean end2IsNavigable, boolean end2IsOrdered,
      AggregationKind end2Aggregation, String end2Name, int end2LowerBound,
      int end2UpperBound) {

    Association association = type1.createAssociation(end1IsNavigable,
        end1Aggregation, end1Name, end1LowerBound, end1UpperBound, type2,
        end2IsNavigable, end2Aggregation, end2Name, end2LowerBound,
        end2UpperBound);

    association.setName(name);

    for (Property prop : association.getMemberEnds()) {
      if (prop.getName().equals(end1Name)) {
        prop.setIsOrdered(end1IsOrdered);
      }
      if (prop.getName().equals(end2Name)) {
        prop.setIsOrdered(end2IsOrdered);
      }
    }

    out("Association " + name + " created.");

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


    out("Association end " + name + ", isNavigable: " + isNavigable
        + ", isOrdered: " + isOrdered + ", aggregationKind: " + aggregationKind
        + ", lower: " + lower + ", upper: " + upper + " created.");

    return associationEnd;
  }

  private void createEnumerations(Model umlModel, MModel useModel) {
    for (EnumType enumType : useModel.enumTypes()) {
      Enumeration enumeration = createEnumeration(umlModel, enumType
          .shortName());
      for (String literal : enumType.getLiterals()) {
        createEnumerationLiteral(enumeration, literal);
      }
    }
  }

  private void createClasses(Model umlModel, MModel useModel) {
    for (MClass useClass : useModel.classes()) {
      createClass(umlModel, useClass.name(), useClass.isAbstract());
    }
  }

  private void createAttributes(Model umlModel, MModel useModel) {
    for (MClass useClass : useModel.classes()) {

      org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) umlModel
          .getOwnedType(useClass.name());

      for (MAttribute useAttribute : useClass.attributes()) {

        String typeName = useAttribute.type().shortName();
        int lowerBound = 1;
        int upperBound = 1;
        boolean isUnique = true;
        boolean isOrdered = false;

        if (useAttribute.type().isSet() || useAttribute.type().isBag()) {
          typeName = ((CollectionType) useAttribute.type()).elemType()
              .shortName();
          upperBound = LiteralUnlimitedNatural.UNLIMITED;
          if (useAttribute.type().isBag()) {
            isUnique = false;
          }
        }

        if (useAttribute.type().isOrderedSet()) {
          isOrdered = true;
        }

        Type theType = umlModel.getOwnedType(typeName);

        if (theType == null) {
          theType = createPrimitiveType(umlModel, typeName);
        }

        createAttribute(umlClass, useAttribute.name(), theType, isUnique,
            isOrdered, lowerBound, upperBound);

      }

    }
  }

  private void createGeneralizations(Model umlModel, MModel useModel) {
    DirectedGraph<MClass, MGeneralization> genGraph = useModel
        .generalizationGraph();

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

  private boolean isBinary(MAssociation association) {
    return association.associationEnds().size() == 2;
  }

  private void createAssociations(Model umlModel, MModel useModel) {

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

  private ArrayList<Integer> parseMultiplicity(String multiplicity) {
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

  public void exportToXMI(File file, MModel useModel) {
    // Get the URI of the model file.
    URI fileURI = URI.createFileURI(file.getAbsolutePath());

    out(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + fileURI.path());

    // Create a resource for this file.
    Resource resource = getResource(fileURI);

    final Model umlModel = createModel(useModel.name());

    createEnumerations(umlModel, useModel);

    createClasses(umlModel, useModel);

    createAttributes(umlModel, useModel);

    createGeneralizations(umlModel, useModel);

    createAssociations(umlModel, useModel);

    resource.getContents().add(umlModel);

    // Save the contents of the resource to the file system.
    try {
      resource.save(Collections.EMPTY_MAP);
    } catch (IOException e) {
    }

    out("Exported: " + umlModel.getName());

  }

  /**********************************************************************************************
   ** import helper methods **
   **********************************************************************************************/

  private void createEnumerations(MModel useModel, EList<Element> allResourceElements) {
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

  private void createClasses(MModel useModel, EList<Element> allResourceElements) {
    for (Element elem : allResourceElements) {
      if (elem instanceof org.eclipse.uml2.uml.Class) {
        org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;
        if (useModel.getClass(umlClass.getName()) == null) {
          MClass useClass = modelFactory.createClass(umlClass.getName(), umlClass
              .isAbstract());
          try {
            useModel.addClass(useClass);
          } catch (MInvalidModelException e) {
            e.printStackTrace();
          }          
        }
      }
    }
  }

  private void createAttributes(MModel useModel, EList<Element> allResourceElements) {
    for (Element elem : allResourceElements) {

      if (elem instanceof org.eclipse.uml2.uml.Class) {

        org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) elem;

        MClass useClass = useModel.getClass(umlClass.getName());

        for (Property prop : umlClass.getAllAttributes()) {

          MAttribute attr = null;
          
          String propName = (prop.getName() == null || prop.getName().isEmpty()) ? 
              prop.getType().getName() : prop.getName();
          
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
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkString()));
              } else if (isOrderedSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkString()));
              } else if (isBag) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkString()));
              } else {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkString());
              }
            } else if (prop.getType().getName().equals("Integer")) {
              if (isSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkInteger()));
              } else if (isOrderedSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkInteger()));
              } else if (isBag) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkInteger()));
              } else {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkInteger());
              }
            } else if (prop.getType().getName().equals("Boolean")) {
              if (isSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkBoolean()));
              } else if (isOrderedSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkBoolean()));
              } else if (isBag) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkBoolean()));
              } else {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkBoolean());
              }
            } else if (prop.getType().getName().equals("Real")) {
              if (isSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkSet(TypeFactory.mkReal()));
              } else if (isOrderedSet) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkOrderedSet(TypeFactory.mkReal()));
              } else if (isBag) {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkBag(TypeFactory.mkReal()));
              } else {
                attr = modelFactory.createAttribute(propName, TypeFactory
                    .mkReal());
              }
            }
          } else if (prop.getType() instanceof ClassImpl) {
            attr = modelFactory.createAttribute(propName, TypeFactory
                .mkObjectType(useModel.getClass(prop.getType().getName())));
          } else if (prop.getType() instanceof EnumerationImpl) {
            attr = modelFactory.createAttribute(propName, useModel
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

  private void createGeneralizations(MModel useModel, EList<Element> allResourceElements) {
    DirectedGraph<MClass, MGeneralization> genGraph = useModel
        .generalizationGraph();
    for (Element elem : allResourceElements) {
      if (elem instanceof org.eclipse.uml2.uml.Class) {
        org.eclipse.uml2.uml.Class childClass = (org.eclipse.uml2.uml.Class) elem;
        for (Generalization gen : childClass.getGeneralizations()) {
          for (Element el : gen.getTargets()) {
            org.eclipse.uml2.uml.Class parentClass = (org.eclipse.uml2.uml.Class) el;
            genGraph.addEdge(modelFactory.createGeneralization(useModel
                .getClass(childClass.getName()), useModel.getClass(parentClass
                .getName())));
          }
        }
      }
    }
  }

  private void createAssociations(MModel useModel, EList<Element> allResourceElements) {

    for (Element elem : allResourceElements) {

      if (elem instanceof Association) {
        Association theAssoc = (Association) elem;
        
        ArrayList<MAssociationEnd> assocEnds = new ArrayList<MAssociationEnd> ();
        List<VarDecl> emptyQualifiers = Collections.emptyList();
        
        for (Property assocEnd : theAssoc.getMemberEnds()) {
          
          if (useModel.getClass(assocEnd.getType().getName()) == null) {
            break;
          }

          MClass assocEndClass = useModel.getClass(assocEnd.getType().getName());

          MMultiplicity m1 = modelFactory.createMultiplicity();

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

          MAssociationEnd assocLeftEnd = modelFactory.createAssociationEnd(
              assocEndClass,
              (assocEnd.getName() == null || assocEnd.getName().isEmpty()) ? 
                  assocEnd.getType().getName() : assocEnd.getName(),
              m1, assocEndAggregationKind,
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
        
        MAssociation assoc = modelFactory.createAssociation(assocName);
        
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
  
  private Model findModel(Resource resource, String modelName) {
    for (Object obj : EcoreUtil.getObjectsByType(resource.getContents(), UMLPackage.Literals.ELEMENT)) {
      org.eclipse.uml2.uml.Element elem = (org.eclipse.uml2.uml.Element) obj;
      if (elem instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl && 
          ((org.eclipse.uml2.uml.internal.impl.ModelImpl)elem).getName().equalsIgnoreCase(modelName)) {
        return (org.eclipse.uml2.uml.internal.impl.ModelImpl) elem;
      }
      
      Model model = findModelRecursive(elem.getOwnedElements(), modelName);
      if (model != null) {
        return model;
      }
    }
    return null;
  }
  
  private Model findModelRecursive (EList<Element> list, String modelName) {
    for (Element elem : list) {
      if (elem instanceof org.eclipse.uml2.uml.internal.impl.ModelImpl && 
          ((org.eclipse.uml2.uml.internal.impl.ModelImpl)elem).getName().equalsIgnoreCase(modelName)) {
        return (org.eclipse.uml2.uml.internal.impl.ModelImpl) elem;
      }
      Model model = findModelRecursive(elem.getOwnedElements(), modelName);
      if (model != null) {
        return model;
      }      
    }
    return null;
  }
  
  private EList<Element> agregateElements(Resource resource) {
    EList<Element> list = new BasicEList<Element>();    
    for (Object obj : EcoreUtil.getObjectsByType(resource.getContents(), UMLPackage.Literals.ELEMENT)) {
      org.eclipse.uml2.uml.Element elem = (org.eclipse.uml2.uml.Element) obj;
      list.add(elem);
      list.addAll(agregateElementsRecursive(elem.getOwnedElements()));
    }
    
    return list;
  }
  
  private EList<Element> agregateElementsRecursive(EList<Element> elements) {
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

  public void importFromXMI(File file, Session session) {
    // Get the URI of the model file.
    URI fileURI = URI.createFileURI(file.getAbsolutePath());
    out(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + fileURI.path());

    // Create a resource for this file.
    Resource resource = getResource(fileURI);

    try {
      resource.load(Collections.EMPTY_MAP);
    } catch (IOException e) {
    }
    
    Model umlModel = findModel(resource, file.getName().replaceFirst("[.][^.]+$", ""));
    
    EList<Element> allResourceElements = agregateElements(resource);
    
    out ("Sukaaaaaaaaaa: " + allResourceElements.size());
    
    if (umlModel == null) {
      Log.error("Import is impossible, bad model");
      return;
    }

    MModel useModel = modelFactory.createModel(umlModel.getName());

    MSystem system = new MSystem(useModel);

    createEnumerations(useModel, allResourceElements);

    createClasses(useModel, allResourceElements);

    createAttributes(useModel, allResourceElements);

    createGeneralizations(useModel, allResourceElements);

    createAssociations(useModel, allResourceElements);

    session.setSystem(system);

    out("Imported: " + umlModel.getName());

  }

  private void initialize() {

    // String path = System.getProperty("eUML.resources");
    String path = "lib/xmihandlerjars/org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar";

    Map<String, Object> extensionToFactoryMap = resourceSet
        .getResourceFactoryRegistry().getExtensionToFactoryMap();
    Map<URI, URI> uriMap = resourceSet.getURIConverter().getURIMap();

    try {
      FileInputStream in = new FileInputStream(path);
      in.close();
    } catch (IOException e) {
      throw (new RuntimeException(e));
    }

    path = path.replace('\\', '/');

    URI uri = URI.createURI("jar:file:" + path + "!/");
    Log.debug("eUML.resource URI --> " + uri);

    Registry packageRegistry = resourceSet.getPackageRegistry();
    packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    // for other xmi files with further namespaces:
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_1_NS_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_1_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_NS_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_1_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_2_2_NS_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_NS_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put(XMI212UMLResource.UML_METAMODEL_URI,
        UMLPackage.eINSTANCE);
    // eclipse namespaces:
    packageRegistry.put(UML212UMLResource.UML_METAMODEL_NS_URI,
        UMLPackage.eINSTANCE);
    packageRegistry.put("http://www.eclipse.org/uml2/3.0.0/UML",
        UMLPackage.eINSTANCE);
    packageRegistry.put("http://www.eclipse.org/uml2/2.0.0/UML",
        UMLPackage.eINSTANCE);    

    // For the .uml files in the eclipse jar files, we need this:
    extensionToFactoryMap.put(UMLResource.FILE_EXTENSION,
        UMLResource.Factory.INSTANCE);
    uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment(
        "libraries").appendSegment(""));
    uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri
        .appendSegment("metamodels").appendSegment(""));
    uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment(
        "profiles").appendSegment(""));

    extensionToFactoryMap.put(UML22UMLResource.FILE_EXTENSION,
        UML22UMLResource.Factory.INSTANCE);
    extensionToFactoryMap.put(XMI2UMLResource.FILE_EXTENSION,
        XMI2UMLResource.Factory.INSTANCE);

    uriMap.putAll(UML22UMLExtendedMetaData.getURIMap());
    uriMap.putAll(XMI2UMLExtendedMetaData.getURIMap());
  }

  private Resource getResource(URI uri) {
    if (!"xmi".equals(uri.fileExtension())
        && !"uml".equals(uri.fileExtension())) {
      // Make sure we have a recognized file extension
      uri = uri.appendFileExtension("xmi");
    }

    Resource r = resourceSet.createResource(uri);

    if (r == null) {
      throw new NullPointerException("Failed to create resource for URI " + uri);
    }

    return r;
  }
  
}
