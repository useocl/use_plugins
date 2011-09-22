package org.tzi.use.plugins.xmihandler.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MMultiplicity;
import org.tzi.use.uml.sys.StateChangeEvent;
import org.tzi.use.uml.sys.StateChangeListener;

import sun.awt.VerticalBagLayout;

@SuppressWarnings("serial")
public class XMIHandlerView extends JDialog implements StateChangeListener, ChangeListener{

	private Session session;
	private static File fileToProcess = null;
	
  private JTextArea fileNameTextArea;
  private ButtonGroup buttonGroup;
  private JPanel selectPanel;
  private JButton selectBtn;
  private JPanel processPanel;
  private JRadioButton importRadioBtn;
  private JRadioButton exportRadioBtn;
  private JButton processBtn;  
	
	public XMIHandlerView(MainWindow parent, Session theSession) {
		super(parent, "XMIHandler");
		this.session = theSession;
		session.addChangeListener(this);
		initGUI();
	}
	
	public static void main (String[] args) {
	  new XMIHandlerView(null, null);
//	  testXMIExport ();
//    testXMIImport ();	  
	}
	
	private static void exportXMI(File file, MModel model) {
	// Create a resource set.
	  ResourceSet resourceSet = new ResourceSetImpl();
	  
	  UMLPackage.eINSTANCE.getName();
	  
	  resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	  
	  resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, UMLResource.Factory.INSTANCE);
 
	  URI uri = URI.createURI("jar:file:/home/stalker/uni/Masterarbeit/xmihandler_repo/lib/org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar!/");
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries").appendSegment(""));
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri.appendSegment("metamodels").appendSegment(""));
	  resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles").appendSegment(""));
	  
	  // Get the URI of the model file.
	  URI fileURI = URI.createFileURI(file.getAbsolutePath());

	  // Create a resource for this file.
	  Resource resource = resourceSet.createResource(fileURI);
	  
    final Model theModel = UMLFactory.eINSTANCE.createModel();
    theModel.setName(model.name());
    
    final org.eclipse.uml2.uml.Class sampleClass = UMLFactory.eINSTANCE.createClass();
    sampleClass.setName("Test");

    for (MClass mClass : model.classes())                                                                                                                                                       
    {                                                                                                                                                                                           
      org.eclipse.uml2.uml.Class theClass = theModel.createOwnedClass(mClass.name(), mClass.isAbstract());      
      for (MAttribute mAttribute : mClass.attributes())                                                                                                                                         
      {                                                                                                                                                                                         
        System.out.println (" " + mAttribute.name());
        PrimitiveType theType = (PrimitiveType)theModel.getOwnedType(mAttribute.type().shortName());         
        if (theType == null) {
          theType = theModel.createOwnedPrimitiveType(mAttribute.type().shortName());
        }

        theClass.createOwnedAttribute(mAttribute.name(), theType);
      }
      
    }
    
    for (MAssociation mAssociation : model.associations())                                                                                                                                   
    {
          MAssociationEnd leftEnd = mAssociation.associationEnds().get(0);
          MAssociationEnd rightEnd = mAssociation.associationEnds().get(1);          
          org.eclipse.uml2.uml.Class leftEndClass = (org.eclipse.uml2.uml.Class)theModel.getOwnedMember(leftEnd.cls().name());          
          org.eclipse.uml2.uml.Class rightEndClass = (org.eclipse.uml2.uml.Class)theModel.getOwnedMember(rightEnd.cls().name());
          
          AggregationKind leftEndAggregationKind = AggregationKind.NONE_LITERAL;
          switch (leftEnd.aggregationKind()) {
            case MAggregationKind.COMPOSITION: leftEndAggregationKind = AggregationKind.COMPOSITE_LITERAL; break;
            case MAggregationKind.AGGREGATION: leftEndAggregationKind = AggregationKind.SHARED_LITERAL; break;           
          }
          
          AggregationKind rightEndAggregationKind = AggregationKind.NONE_LITERAL;
          switch (rightEnd.aggregationKind()) {
            case MAggregationKind.COMPOSITION: rightEndAggregationKind = AggregationKind.COMPOSITE_LITERAL; break;
            case MAggregationKind.AGGREGATION: rightEndAggregationKind = AggregationKind.SHARED_LITERAL; break;           
          }
          
          int leftEndLower = 0;
          int leftEndUpper = 0;
          
          if (leftEnd.multiplicity().toString().equals(MMultiplicity.ONE.toString())) {
            leftEndLower = 1;
            leftEndUpper = 1;
          } else if (leftEnd.multiplicity().toString().equals(MMultiplicity.ONE_MANY.toString())) {
            leftEndLower = 1;
            leftEndUpper = LiteralUnlimitedNatural.UNLIMITED;            
          } else if (leftEnd.multiplicity().toString().equals(MMultiplicity.ZERO_MANY.toString())) {
            leftEndLower = 0;
            leftEndUpper = LiteralUnlimitedNatural.UNLIMITED;            
          } else if (leftEnd.multiplicity().toString().equals(MMultiplicity.ZERO_ONE.toString())) {
            leftEndLower = 0;
            leftEndUpper = 1;            
          }
          
          int rightEndLower = 0;
          int rightEndUpper = 0;
          
          if (rightEnd.multiplicity().toString().equals(MMultiplicity.ONE.toString())) {
            rightEndLower = 1;
            rightEndUpper = 1;
          } else if (rightEnd.multiplicity().toString().equals(MMultiplicity.ONE_MANY.toString())) {
            rightEndLower = 1;
            rightEndUpper = LiteralUnlimitedNatural.UNLIMITED;            
          } else if (rightEnd.multiplicity().toString().equals(MMultiplicity.ZERO_MANY.toString())) {
            rightEndLower = 0;
            rightEndUpper = LiteralUnlimitedNatural.UNLIMITED;            
          } else if (rightEnd.multiplicity().toString().equals(MMultiplicity.ZERO_ONE.toString())) {
            rightEndLower = 0;
            rightEndUpper = 1;            
          }          
          
        org.eclipse.uml2.uml.Association assoc = 
        leftEndClass.createAssociation(rightEnd.isNavigable(), leftEndAggregationKind, rightEnd.name(), leftEndLower, leftEndUpper, 
          rightEndClass, leftEnd.isNavigable(), rightEndAggregationKind, leftEnd.name(), rightEndLower, rightEndUpper);
        
        assoc.setName(mAssociation.name());
    }

	  resource.getContents().add(theModel);
	  
	  // Save the contents of the resource to the file system.
	  try
	  {
	    resource.save(Collections.EMPTY_MAP);
	  }
	  catch (IOException e) {} 
	}
	
  private static void importXMI(File file, MModel model) {
    // Create a resource set.
    ResourceSet resourceSet = new ResourceSetImpl();
    
    UMLPackage.eINSTANCE.getName();
    
    resourceSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
    
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, UMLResource.Factory.INSTANCE);
 
    URI uri = URI.createURI("jar:file:/home/stalker/uni/Masterarbeit/xmihandler_repo/lib/org.eclipse.uml2.uml.resources_3.0.0.v200906011111.jar!/");
    resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries").appendSegment(""));
    resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri.appendSegment("metamodels").appendSegment(""));
    resourceSet.getURIConverter().getURIMap().put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles").appendSegment(""));
    
    // Get the URI of the model file.
    URI fileURI = URI.createFileURI(file.getAbsolutePath());
    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>" + fileURI.path());

    // Create a resource for this file.
    Resource resource = resourceSet.getResource(fileURI, true);
    try
    {
      resource.load(Collections.EMPTY_MAP);
    }
    catch (IOException e) {}    

    final Model companyModel = (Model)resource.getContents().get(0);
    System.out.println (companyModel.getName());
        
    for (Type type : companyModel.getOwnedTypes())
    {
      System.out.println (type.getQualifiedName());
      
      if (type instanceof org.eclipse.uml2.uml.Class)
      {
        for (Property prop : ((org.eclipse.uml2.uml.Class) type).getAllAttributes())
        {
          System.out.println (prop.getQualifiedName());
        }
      }
      if (type instanceof Association)
      {
        for (Property t : ((Association)type).getAllAttributes())
        {
          System.out.println ("---------" + t.getLowerValue().stringValue());
          System.out.println ("---------" + t.getUpperValue().stringValue());
        }
      }
    }
    
  }
	
	private void initGUI() {
		JPanel backPanel = new JPanel(new VerticalBagLayout());
		this.getContentPane().add(backPanel);
		
	  fileNameTextArea = new JTextArea();
	  buttonGroup = new ButtonGroup();
	  selectPanel = new JPanel();
	  selectBtn = new JButton();
	  processPanel = new JPanel();
	  importRadioBtn = new JRadioButton();
	  exportRadioBtn = new JRadioButton();
	  processBtn = new JButton();
		
    selectPanel.setName("selectPanel"); 
    selectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    fileNameTextArea.setText("No file selected"); 
    fileNameTextArea.setName("fileNameTextArea");
    selectPanel.add(fileNameTextArea);
    fileNameTextArea.setRows(5);
    fileNameTextArea.setColumns(25);
    fileNameTextArea.setEditable(false);
    fileNameTextArea.setLineWrap(true);
    
    selectBtn.setText("Select File"); 
    selectBtn.setName("selectBtn"); 
    selectPanel.add(selectBtn);
    
    selectBtn.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("XMI Files", "xmi"));
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileToProcess = fc.getSelectedFile();
            fileNameTextArea.setText(fileToProcess.getAbsolutePath());
            //This is where a real application would open the file.
        } else {
        }
      }
    });    

    backPanel.add(selectPanel);

    processPanel.setName("processPanel"); 
    processPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    buttonGroup.add(importRadioBtn);
    importRadioBtn.setText("Import"); 
    importRadioBtn.setName("importRadioBtn"); 
    processPanel.add(importRadioBtn);

    buttonGroup.add(exportRadioBtn);
    exportRadioBtn.setSelected(true);
    exportRadioBtn.setText("Export"); 
    exportRadioBtn.setName("exportRadioBtn"); 
    processPanel.add(exportRadioBtn);

    processBtn.setText("Process"); 
    processBtn.setName("processBtn"); 
    processPanel.add(processBtn);
    
    processBtn.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fileToProcess == null)
        {
          JOptionPane.showMessageDialog(null, "Please select an output xmi file first", "No output file", JOptionPane.WARNING_MESSAGE);
          return;
        }
        
        if (exportRadioBtn.isSelected())
        {
          exportXMI(fileToProcess, session.system().model());          
        } else if (importRadioBtn.isSelected()) {
          importXMI(fileToProcess, session.system().model());          
        }
      }
    });    

    backPanel.add(processPanel);		

		this.pack();
	
		this.setMinimumSize(this.getPreferredSize());
		this.setVisible(true);
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
