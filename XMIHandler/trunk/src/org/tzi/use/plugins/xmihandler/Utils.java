package org.tzi.use.plugins.xmihandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLExtendedMetaData;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resource.XMI212UMLResource;
import org.eclipse.uml2.uml.resource.XMI2UMLExtendedMetaData;
import org.eclipse.uml2.uml.resource.XMI2UMLResource;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.util.Log;

public class Utils {

  private static ResourceSet resourceSet = null;

  private static ModelFactory modelFactory = null;

  public static void out(String output) {
    System.out.println(output);
  }

  public static void err(String error) {
    System.err.println(error);
  }

  public static ModelFactory getModelFactory() {
    if (modelFactory == null) {
      modelFactory = new ModelFactory();
    }
    return modelFactory;
  }

  private static ResourceSet getResourceSet() {

    if (resourceSet == null) {
      resourceSet = new ResourceSetImpl();

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
      uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri
          .appendSegment("libraries").appendSegment(""));
      uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri
          .appendSegment("metamodels").appendSegment(""));
      uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri
          .appendSegment("profiles").appendSegment(""));

      extensionToFactoryMap.put(UML22UMLResource.FILE_EXTENSION,
          UML22UMLResource.Factory.INSTANCE);
      extensionToFactoryMap.put(XMI2UMLResource.FILE_EXTENSION,
          XMI2UMLResource.Factory.INSTANCE);

      uriMap.putAll(UML22UMLExtendedMetaData.getURIMap());
      uriMap.putAll(XMI2UMLExtendedMetaData.getURIMap());
    }
    
    return resourceSet;

  }

  public static Resource getResource(URI uri) {

    if (!"xmi".equals(uri.fileExtension())
        && !"uml".equals(uri.fileExtension())) {
      // Make sure we have a recognized file extension
      uri = uri.appendFileExtension("xmi");
    }
    
    ResourceSet rs = getResourceSet();
    
    if (rs == null) {
      throw new NullPointerException("Failed to create resource set");      
    }

    Resource r = rs.createResource(uri);

    if (r == null) {
      throw new NullPointerException("Failed to create resource for URI " + uri);
    }

    return r;
  }

}
