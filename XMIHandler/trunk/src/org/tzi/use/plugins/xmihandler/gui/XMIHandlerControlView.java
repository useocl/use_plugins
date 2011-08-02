package org.tzi.use.plugins.xmihandler.gui;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
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
		testEMF();
	}
	
	private void testEMF() {
	// Create a resource set.
	  ResourceSet resourceSet = new ResourceSetImpl();

	  // Register the default resource factory -- only needed for stand-alone!
	  resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
	    Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

	  // Get the URI of the model file.
	  URI fileURI = URI.createFileURI(new File("mylibrary.xmi").getAbsolutePath());
	  System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + fileURI.path());

	  // Create a resource for this file.
	  Resource resource = resourceSet.createResource(fileURI);
	  
	  EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
	  EClass purchaseOrderClass = ecoreFactory.createEClass();
	  purchaseOrderClass.setName("PurchaseOrder");
	  EAttribute shipTo = ecoreFactory.createEAttribute();
	  shipTo.setName("shipTo");
	  shipTo.setEType(EcorePackage.Literals.ESTRING);
	  purchaseOrderClass.getEStructuralFeatures().add(shipTo);
	  
	  resource.getContents().add(purchaseOrderClass);
	  
	  // Add the book and writer objects to the contents.
	  //resource.getContents().add(book);
	  // Save the contents of the resource to the file system.
	  try
	  {
	    resource.save(Collections.EMPTY_MAP);
	  }
	  catch (IOException e) {}	  
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
