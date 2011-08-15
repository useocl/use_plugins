package org.tzi.use.plugins.xmihandler.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UMLPackage.Literals;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;

import sun.awt.VerticalBagLayout;

@SuppressWarnings("serial")
public class XMIHandlerControlView extends JDialog implements StateChangeListener, ChangeListener{

	private Session session;
	
	public XMIHandlerControlView(MainWindow parent, Session session) {
		super(parent, "XMIHandler Control");
		this.session = session;
		session.addChangeListener(this);
		initGUI();
		testEMF(session.system().model());
	}
	
	private void testEMF(MModel model) {
	// Create a resource set.
	  ResourceSet resourceSet = new ResourceSetImpl();
	  
	  UMLPackage.eINSTANCE.getName();
	  
	  resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	  
	  resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
 
	  URI uri = URI.createURI("jar:file:/home/stalker/uni/Masterarbeit/xmihandler_repo/lib/org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar!/");
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries").appendSegment(""));
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri.appendSegment("metamodels").appendSegment(""));
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles").appendSegment(""));
	  

	  // Get the URI of the model file.
	  URI fileURI = URI.createFileURI(new File("mylibrary.uml").getAbsolutePath());
	  System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + fileURI.path());

	  // Create a resource for this file.
	  Resource resource = resourceSet.createResource(fileURI);

	  Model umlMetamodel = (Model) EcoreUtil.getObjectByType(resourceSet.getResource(URI.createURI( UMLResource.UML_METAMODEL_URI), true ).getContents(), Literals.PACKAGE);
    System.out.println( "umlMetamodel = " + umlMetamodel );

	   final Model sampleModel = UMLFactory.eINSTANCE.createModel();
	   sampleModel.setName( "Sample Model" );

	   final Profile sampleProfile = UMLFactory.eINSTANCE.createProfile();
	   sampleProfile.setName( "Sample Profile" );	  
	  
	  for (MClass mClass : model.classes())
	  {
      System.out.println ("Class: " + mClass.name());	    
	    for (MAttribute mAttribute : mClass.attributes())
	    {
	      System.out.println (" " + mAttribute.name());	      
	    }
      for (MAssociation mAssociation : mClass.associations())
      {
        System.out.println (" " + mAssociation.name());       
      }	    

	  }
	  
	   resource.getContents().add( sampleModel );
	   resource.getContents().add( sampleProfile );
	   
	   final Stereotype ejbStereo = sampleProfile.createOwnedStereotype( "EJB" );
	   extendMetaclass( umlMetamodel, sampleProfile, UMLPackage.Literals.CLASS.getName(), ejbStereo );

	   sampleProfile.define();

	   final org.eclipse.uml2.uml.Package samplePackage = sampleModel.createNestedPackage( "sample" );
	   samplePackage.applyProfile( sampleProfile );

	   final org.eclipse.uml2.uml.Class sampleClass = samplePackage.createOwnedClass( "TimeEntry", false );
	   sampleClass.applyStereotype( ejbStereo );	   
	  
	  // Add the book and writer objects to the contents.
	  //resource.getContents().add(book);
	  // Save the contents of the resource to the file system.
	  try
	  {
	    resource.save(Collections.EMPTY_MAP);
	  }
	  catch (IOException e) {}	  
	}
	
	 private static void extendMetaclass( final Model umlMetamodel,
       final Profile profile,
       final String name,
       final Stereotype stereotype )
{
	   // The isRequired argument must be false, otherwise all classes will inherit the stereotype
	   //  by default
	   stereotype.createExtension( referenceMetaclass( umlMetamodel, profile, name ), false );
}

private static org.eclipse.uml2.uml.Class referenceMetaclass( final Model umlMetamodel,
                                final Profile profile,
                                final String name )
{
  final org.eclipse.uml2.uml.Class metaclass = (org.eclipse.uml2.uml.Class) umlMetamodel.getOwnedType( name );
  if ( !profile.getReferencedMetaclasses().contains( metaclass ) )
  {
    profile.createMetaclassReference( metaclass );
  }
  return metaclass;
}	
	
	private void initGUI() {
		JPanel backPanel = new JPanel(new VerticalBagLayout());
		this.getContentPane().add(backPanel);
		this.pack();
    this.setSize(350, 250);		
		this.setMinimumSize(this.getSize());
	}

	@Override
	public void stateChanged(StateChangeEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
	}
}
