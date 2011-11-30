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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.uml2.common.edit.domain.UML2AdapterFactoryEditingDomain;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
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

  private AdapterFactoryEditingDomain editingDomain;

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
    initializeEditingDomain();
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

  private static Association createAssociation(Type type1,
      boolean end1IsNavigable, AggregationKind end1Aggregation,
      String end1Name, int end1LowerBound, int end1UpperBound, Type type2,
      boolean end2IsNavigable, AggregationKind end2Aggregation,
      String end2Name, int end2LowerBound, int end2UpperBound) {

    Association association = type1.createAssociation(end1IsNavigable,
        end1Aggregation, end1Name, end1LowerBound, end1UpperBound, type2,
        end2IsNavigable, end2Aggregation, end2Name, end2LowerBound,
        end2UpperBound);

    out("Association " + association.getName() + " created.");

    return association;
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

  private void createAssociations(Model umlModel, MModel useModel) {
    for (MAssociation mAssociation : useModel.associations()) {

    }
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

    // for (MAssociation mAssociation : useModel.associations()) {
    // MAssociationEnd leftEnd = mAssociation.associationEnds().get(0);
    // MAssociationEnd rightEnd = mAssociation.associationEnds().get(1);
    // org.eclipse.uml2.uml.Class leftEndClass = (org.eclipse.uml2.uml.Class)
    // umlModel
    // .getOwnedMember(leftEnd.cls().name());
    // org.eclipse.uml2.uml.Class rightEndClass = (org.eclipse.uml2.uml.Class)
    // umlModel
    // .getOwnedMember(rightEnd.cls().name());
    //
    // AggregationKind leftEndAggregationKind = AggregationKind.NONE_LITERAL;
    // switch (leftEnd.aggregationKind()) {
    // case MAggregationKind.COMPOSITION:
    // leftEndAggregationKind = AggregationKind.COMPOSITE_LITERAL;
    // break;
    // case MAggregationKind.AGGREGATION:
    // leftEndAggregationKind = AggregationKind.SHARED_LITERAL;
    // break;
    // }
    //
    // AggregationKind rightEndAggregationKind = AggregationKind.NONE_LITERAL;
    // switch (rightEnd.aggregationKind()) {
    // case MAggregationKind.COMPOSITION:
    // rightEndAggregationKind = AggregationKind.COMPOSITE_LITERAL;
    // break;
    // case MAggregationKind.AGGREGATION:
    // rightEndAggregationKind = AggregationKind.SHARED_LITERAL;
    // break;
    // }
    //
    // int leftEndLower = 0;
    // int leftEndUpper = 0;
    //
    // if (leftEnd.multiplicity().toString()
    // .equals(MMultiplicity.ONE.toString())) {
    // leftEndLower = 1;
    // leftEndUpper = 1;
    // } else if (leftEnd.multiplicity().toString().equals(
    // MMultiplicity.ONE_MANY.toString())) {
    // leftEndLower = 1;
    // leftEndUpper = LiteralUnlimitedNatural.UNLIMITED;
    // } else if (leftEnd.multiplicity().toString().equals(
    // MMultiplicity.ZERO_MANY.toString())) {
    // leftEndLower = 0;
    // leftEndUpper = LiteralUnlimitedNatural.UNLIMITED;
    // } else if (leftEnd.multiplicity().toString().equals(
    // MMultiplicity.ZERO_ONE.toString())) {
    // leftEndLower = 0;
    // leftEndUpper = 1;
    // }
    //
    // int rightEndLower = 0;
    // int rightEndUpper = 0;
    //
    // if (rightEnd.multiplicity().toString().equals(
    // MMultiplicity.ONE.toString())) {
    // rightEndLower = 1;
    // rightEndUpper = 1;
    // } else if (rightEnd.multiplicity().toString().equals(
    // MMultiplicity.ONE_MANY.toString())) {
    // rightEndLower = 1;
    // rightEndUpper = LiteralUnlimitedNatural.UNLIMITED;
    // } else if (rightEnd.multiplicity().toString().equals(
    // MMultiplicity.ZERO_MANY.toString())) {
    // rightEndLower = 0;
    // rightEndUpper = LiteralUnlimitedNatural.UNLIMITED;
    // } else if (rightEnd.multiplicity().toString().equals(
    // MMultiplicity.ZERO_ONE.toString())) {
    // rightEndLower = 0;
    // rightEndUpper = 1;
    // }
    //
    // org.eclipse.uml2.uml.Association assoc = leftEndClass.createAssociation(
    // leftEnd.isNavigable(), leftEndAggregationKind, leftEnd.name(),
    // leftEndLower, leftEndUpper, rightEndClass, rightEnd.isNavigable(),
    // rightEndAggregationKind, rightEnd.name(), rightEndLower,
    // rightEndUpper);
    //
    // assoc.setName(mAssociation.name());
    // }

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

  private void createEnumerations(MModel useModel, Model umlModel) {

  }

  private void createClasses(MModel useModel, Model umlModel) {
    for (Type type : umlModel.getOwnedTypes()) {
      if (type instanceof org.eclipse.uml2.uml.Class) {
        org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) type;
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

  private void createAttributes(MModel useModel, Model umlModel) {
    for (Type type : umlModel.getOwnedTypes()) {

      if (type instanceof org.eclipse.uml2.uml.Class) {
        
        org.eclipse.uml2.uml.Class umlClass = (org.eclipse.uml2.uml.Class) type;
        
        MClass useClass = useModel.getClass(umlClass.getName());
        
        for (Property prop : umlClass.getAllAttributes()) {
          
          out (prop.getType().toString());          

          if (prop.getType() instanceof PrimitiveTypeImpl) {
            
            MAttribute attr = null;
            boolean isSet = false;
            boolean isBag = false;
            
            if (prop.getUpper() == LiteralUnlimitedNatural.UNLIMITED) {
              if (prop.isUnique()) {
                isSet = true;
              } else {
                isBag = true;
              }
            }

            if (prop.getType().getName().equals("String")) {
              if (isSet) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkSet(TypeFactory.mkString()));                
              } else if (isBag) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkBag(TypeFactory.mkString()));                
              } else {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkString());                
              }
            } else if (prop.getType().getName().equals("Integer")) {
              if (isSet) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkSet(TypeFactory.mkInteger()));                
              } else if (isBag) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkBag(TypeFactory.mkInteger()));                
              } else {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkInteger());                
              }
            } else if (prop.getType().getName().equals("Boolean")) {
              if (isSet) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkSet(TypeFactory.mkBoolean()));                
              } else if (isBag) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkBag(TypeFactory.mkBoolean()));                
              } else {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkBoolean());                
              }
            } else if (prop.getType().getName().equals("Real")) {
              if (isSet) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkSet(TypeFactory.mkReal()));                
              } else if (isBag) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkBag(TypeFactory.mkReal()));                
              } else {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkReal());                
              }
            } else if (prop.getType().getName().equals("Date")) {
              if (isSet) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkSet(TypeFactory.mkDate()));                
              } else if (isBag) {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkBag(TypeFactory.mkDate()));                
              } else {
                attr = modelFactory.createAttribute(prop.getName(), TypeFactory
                    .mkDate());                
              }              
            }

            if (attr != null) {
              try {
                 useClass.addAttribute(attr);
              } catch (MInvalidModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          } else if (prop.getType() instanceof ClassImpl) {
            
          } else if (prop.getType() instanceof EnumerationImpl) {
            
          }
        }
      }
    }
  }

  private void createGeneralizations(MModel useModel, Model umlModel) {

  }

  private void createAssociations(MModel useModel, Model umlModel) {

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

    Model umlModel = (Model) EcoreUtil.getObjectByType(resource.getContents(),
        UMLPackage.Literals.MODEL);

    if (umlModel == null) {
      Log.error("Import is impossible, bad model");
      return;
    }

    MModel useModel = modelFactory.createModel(umlModel.getName());

    MSystem system = new MSystem(useModel);

    createEnumerations(useModel, umlModel);

    createClasses(useModel, umlModel);
    
    createAttributes(useModel, umlModel);

    createGeneralizations(useModel, umlModel);

    createAssociations(useModel, umlModel);

    {

      // if (type instanceof Association) {
      // Association theAssoc = (Association) type;
      //
      // List<VarDecl> emptyQualifiers = Collections.emptyList();
      //
      // MAssociation assoc =
      // modelFactory.createAssociation(theAssoc.getName());
      //
      // Property leftEnd = theAssoc.getAllAttributes().get(0);
      // Property rightEnd = theAssoc.getAllAttributes().get(1);
      //
      // MClass leftEndClass = model.getClass(leftEnd.getClass_().getName());
      // MClass rightEndClass = model.getClass(rightEnd.getClass_().getName());
      //
      // MMultiplicity m1 = modelFactory.createMultiplicity();
      // MMultiplicity m2 = modelFactory.createMultiplicity();
      //
      // m1.addRange(leftEnd.getLower(), leftEnd.getUpper());
      // m2.addRange(rightEnd.getLower(), rightEnd.getUpper());
      //
      // int leftEndAggregationKind = MAggregationKind.NONE;
      // switch (leftEnd.getAggregation()) {
      // case COMPOSITE_LITERAL:
      // leftEndAggregationKind = MAggregationKind.COMPOSITION;
      // break;
      // case SHARED_LITERAL:
      // leftEndAggregationKind = MAggregationKind.AGGREGATION;
      // break;
      // }
      //
      // int rightEndAggregationKind = MAggregationKind.NONE;
      // switch (rightEnd.getAggregation()) {
      // case COMPOSITE_LITERAL:
      // rightEndAggregationKind = MAggregationKind.COMPOSITION;
      // break;
      // case SHARED_LITERAL:
      // rightEndAggregationKind = MAggregationKind.AGGREGATION;
      // break;
      // }
      //
      // MAssociationEnd assocLeftEnd = modelFactory.createAssociationEnd(
      // leftEndClass, leftEndClass.name(), m1, leftEndAggregationKind,
      // leftEnd.isOrdered(), emptyQualifiers);
      //
      // MAssociationEnd assocRightEnd = modelFactory.createAssociationEnd(
      // rightEndClass, rightEndClass.name(), m2, rightEndAggregationKind,
      // rightEnd.isOrdered(), emptyQualifiers);
      //
      // try {
      // assoc.addAssociationEnd(assocLeftEnd);
      // assoc.addAssociationEnd(assocRightEnd);
      // model.addAssociation(assoc);
      // } catch (MInvalidModelException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
      //
      // }
    }

    session.setSystem(system);

    out("Imported: " + umlModel.getName());

  }

  private void initializeEditingDomain() {

    // String path = System.getProperty("eUML.resources");
    String path = "lib/xmihandlerjars/org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar";

    BasicCommandStack commandStack = new BasicCommandStack() {

      @Override
      protected void handleError(Exception exception) {
        super.handleError(exception);
        throw new RuntimeException(exception);
      }

    };

    List<AdapterFactory> factories = new ArrayList<AdapterFactory>();
    factories.add(new UMLResourceItemProviderAdapterFactory());
    factories.add(new UMLItemProviderAdapterFactory());
    factories.add(new EcoreItemProviderAdapterFactory());
    factories.add(new UMLReflectiveItemProviderAdapterFactory());
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(
        factories);

    editingDomain = new UML2AdapterFactoryEditingDomain(adapterFactory,
        commandStack);

    ResourceSet resourceSet = editingDomain.getResourceSet();
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
    // These lines were one cause for issue 5915: (Were they needed?)
    // TODO: Review - tfm
    // if (Character.isLetter(path.charAt(0))) {
    // path = '/' + path;
    // }
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

    Resource r = editingDomain.getResourceSet().createResource(uri);

    if (r == null) {
      throw new NullPointerException("Failed to create resource for URI " + uri);
    }

    return r;
  }

}
