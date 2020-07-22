package org.tzi.use.gui.plugins;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.config.Options;
import org.tzi.use.gui.plugins.classdiagram.OutputClassDiagram;
import org.tzi.use.gui.plugins.data.MMConstants;
import org.tzi.use.gui.plugins.data.TAttribute;
import org.tzi.use.gui.plugins.data.TConstants;
import org.tzi.use.gui.plugins.data.TLink;
import org.tzi.use.gui.plugins.data.TObject;
import org.tzi.use.gui.plugins.data.TStatus;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.gui.views.diagrams.DiagramOptions;
import org.tzi.use.gui.views.diagrams.DiagramView;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MMPrintVisitor;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemState;

public class Utilities {
	public static String trim(String input) {
		if (input == null) {
			return null;
		}
		if (input.length() == 2 && input.startsWith("'") && input.endsWith("'")) {
			return null;
		}
		if (input.length() >= 2 && input.startsWith("'") && input.endsWith("'")) {
			return input.substring(1, input.length() - 1);
		}
		return input;
	}

	// private static int counter = 0;
	private static UseModelApi associationApi = new UseModelApi("AssocModel");

	public static MAssociation getMAssociation(String linkObjectName) {
		if (associationApi.getModel().classes().isEmpty()) {
			try {
				associationApi.createClass(MMConstants.CLS_OBJECT_NAME, false);
			} catch (UseApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		MAssociation existingAsso = associationApi.getAssociation(linkObjectName);
		if (existingAsso != null) {
			return existingAsso;
		}

		String associationName = linkObjectName;
		
		if (associationName.contains("CompLink")) {
			
			String role1Name = MMConstants.CLS_COMPOSITION_ATTR_FIRSTR + linkObjectName;
			String role2Name = MMConstants.CLS_COMPOSITION_ATTR_SECONDR + linkObjectName;
			try {
				return associationApi.createAssociation(associationName, MMConstants.CLS_OBJECT_NAME, role1Name, "*", 0,
						MMConstants.CLS_OBJECT_NAME, role2Name, "*", 2);
			} catch (UseApiException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		}
			else if (associationName.contains("AggLink")) {
				
				String role1Name = MMConstants.CLS_AGGREGATION_ATTR_FIRSTR + linkObjectName;
				String role2Name = MMConstants.CLS_AGGREGATION_ATTR_SECONDR + linkObjectName;
				try {
					return associationApi.createAssociation(associationName, MMConstants.CLS_OBJECT_NAME, role1Name, "*", 0,
							MMConstants.CLS_OBJECT_NAME, role2Name, "*", 1);
				} catch (UseApiException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return null;
				}	
			
		} else {
			
			String role1Name = MMConstants.CLS_LINK_ATTR_FIRSTR + linkObjectName;
			String role2Name = MMConstants.CLS_LINK_ATTR_SECONDR + linkObjectName;
			try {
				return associationApi.createAssociation(associationName, MMConstants.CLS_OBJECT_NAME, role1Name, "*", 0,
						MMConstants.CLS_OBJECT_NAME, role2Name, "*", 0);
			} catch (UseApiException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}

		}
		

	
	}

	public static TStatus getCurrentStatus(MSystemState state, MObject mObject, List<MObject> slots) {
		TObject tObject = getTObject(false, state, mObject, slots);
		return tObject.getCurrentStatus();
	}

	public static TObject createNewTObject(MSystemState state, MObject mObject, List<MObject> slots) {
		return getTObject(true, state, mObject, slots);
	}

	private static TObject getTObject(boolean increaseObjectID, MSystemState state, MObject mObject,
			List<MObject> slots) {
		MObjectState objState = mObject.state(state);
		String identity = trim(objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_IDENT).toString());
		String className = trim(objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_CLASSN).toString());
		String superclassName = trim(objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_SUPERCLASSN).toString());

		TObject tObject;
		if (increaseObjectID) {
			tObject = new TObject(identity, className,superclassName);
		} else {
			tObject = TObject.getNewObjectWithoutIncreasingID();
			tObject.setIdentityName(identity);
			tObject.setClassName(className);
			tObject.setsuperClassName(superclassName);
		}

		List<TAttribute> attributes = new LinkedList<TAttribute>();
		if (slots != null) {
			for (MObject slot : slots) {
				MObjectState slotState = slot.state(state);
				attributes.add(Utilities.getTransformationAttribute(slotState));
			}
		}
		tObject.addAll(attributes);

		return tObject;
	}

	public static TAttribute getTransformationAttribute(MObjectState slotState) {
		Value attr = slotState.attributeValue(MMConstants.CLS_SLOT_ATTR_ATTR);
		String tAttrName = trim(attr.toString());

		Value val = slotState.attributeValue(MMConstants.CLS_SLOT_ATTR_VAL);
		String tValue = trim(val.toString());

		TAttribute.Type tType;
		if (tValue == null || val.type().isTypeOfVoidType()) {
			// check for void type first
			// empty String should be handled like void
			tType = TAttribute.Type.VOID;
		} else if (val.type().isTypeOfBoolean()) {
			tType = TAttribute.Type.BOOLEAN;
		} else if (val.type().isTypeOfInteger()) {
			tType = TAttribute.Type.INTEGER;
		} else if (val.type().isTypeOfReal()) {
			tType = TAttribute.Type.REAL;
		} else if (val.type().isTypeOfString()) {
			tType = TAttribute.Type.STRING;
		} else {
			tType = TAttribute.Type.ANY;
			// TODO error ausgabe
			System.out.println("Not a valid input type: " + val.type());
		}
		
		return new TAttribute(tAttrName, tValue, tType);
	}

	public static TLink getTLinkWithoutId(MObjectState linkObjState) {
		TLink tLink = TLink.getNewLinkWithoutIncreasingID();
		tLink.setLinkName(trim(linkObjState.attributeValue(MMConstants.CLS_LINK_ATTR_ASSOC).toString()));
		tLink.setFirstEndRoleName(trim(linkObjState.attributeValue(MMConstants.CLS_LINK_ATTR_FIRSTR).toString()));
		tLink.setSecondEndRoleName(trim(linkObjState.attributeValue(MMConstants.CLS_LINK_ATTR_SECONDR).toString()));

		return tLink;
	}
	
	public static TLink getTCompWithoutId(MObjectState linkObjState) {
		TLink tLink = TLink.getNewLinkWithoutIncreasingID();
		tLink.setLinkName(trim(linkObjState.attributeValue(MMConstants.CLS_COMPOSITION_ATTR_COMP).toString()));
		tLink.setFirstEndRoleName(trim(linkObjState.attributeValue(MMConstants.CLS_COMPOSITION_ATTR_FIRSTR).toString()));
		tLink.setSecondEndRoleName(trim(linkObjState.attributeValue(MMConstants.CLS_COMPOSITION_ATTR_SECONDR).toString()));

		return tLink;
	}
	
	public static TLink getTAggrWithoutId(MObjectState linkObjState) {
		TLink tLink = TLink.getNewLinkWithoutIncreasingID();
		tLink.setLinkName(trim(linkObjState.attributeValue(MMConstants.CLS_AGGREGATION_ATTR_AGGR).toString()));
		tLink.setFirstEndRoleName(trim(linkObjState.attributeValue(MMConstants.CLS_AGGREGATION_ATTR_FIRSTR).toString()));
		tLink.setSecondEndRoleName(trim(linkObjState.attributeValue(MMConstants.CLS_AGGREGATION_ATTR_SECONDR).toString()));

		return tLink;
	}

	public static TLink createNewTLink(MObjectState linkObjState, TObject o1, TObject o2,int kind) {
		String associationName = trim(linkObjState.attributeValue(MMConstants.CLS_LINK_ATTR_ASSOC).toString());
		String blackRole = trim(linkObjState.attributeValue(MMConstants.CLS_LINK_ATTR_FIRSTR).toString());
		String whiteRole = trim(linkObjState.attributeValue(MMConstants.CLS_LINK_ATTR_SECONDR).toString());
		kind = 0;
		return new TLink(associationName, o1, o2, blackRole, whiteRole,kind);
	}
	
	public static TLink createNewTLinkComp(MObjectState linkObjState, TObject o1, TObject o2, int kind) {
		String associationName = trim(linkObjState.attributeValue(MMConstants.CLS_COMPOSITION_ATTR_COMP).toString());
		String blackRole = trim(linkObjState.attributeValue(MMConstants.CLS_COMPOSITION_ATTR_FIRSTR).toString());
		String whiteRole = trim(linkObjState.attributeValue(MMConstants.CLS_COMPOSITION_ATTR_SECONDR).toString());
		kind = 2;
		return new TLink(associationName, o1, o2, blackRole, whiteRole, kind);
	}
	
	public static TLink createNewTLinkAggr(MObjectState linkObjState, TObject o1, TObject o2, int kind) {
		String associationName = trim(linkObjState.attributeValue(MMConstants.CLS_AGGREGATION_ATTR_AGGR).toString());
		String blackRole = trim(linkObjState.attributeValue(MMConstants.CLS_AGGREGATION_ATTR_FIRSTR).toString());
		String whiteRole = trim(linkObjState.attributeValue(MMConstants.CLS_AGGREGATION_ATTR_SECONDR).toString());
		kind = 1;
		return new TLink(associationName, o1, o2, blackRole, whiteRole, kind);
	}


	public static String getObjectDisplayNameFromSlot(MObjectState slotState) {
		Value attributeName = slotState.attributeValue(MMConstants.CLS_SLOT_ATTR_ATTR);
		Value attributeValue = slotState.attributeValue(MMConstants.CLS_SLOT_ATTR_VAL);
		String displayName = trim(attributeName.toString());
		if (displayName == null) {
			displayName = TConstants.OPTIONAL_MARKER;
		}
		String displayValue = attributeValue.toString();
		if (displayValue == null || displayValue.equals("''")) {
			return displayName;
		}

		return displayName + "=" + displayValue;
	}

	public static String getMainDisplayFromObject(MObjectState objState) {
		Value identity = objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_IDENT);
		Value className = objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_CLASSN);
		Value superClassName = objState.attributeValue(MMConstants.CLS_OBJECT_ATTR_SUPERCLASSN);
		
		String postfix = "";
		System.out.println(superClassName.toString().length());
		if (superClassName != null && superClassName.toString().length() != 2) {
			
			postfix = " < " + trim(superClassName.toString());
		}
				 
		String displayIdentity = trim(identity.toString());
		String displayClassName = trim(className.toString());
		if (displayClassName != null) {
			displayClassName += postfix;
		}
		if (displayIdentity == null && displayClassName == null) {
			return " : " + TConstants.OPTIONAL_MARKER;
		}
		if (displayIdentity != null && displayClassName == null) {
			return displayIdentity + " : " + TConstants.OPTIONAL_MARKER;
		}
		if (displayIdentity == null && displayClassName != null) {
			return " : " + displayClassName;
		}
		return displayIdentity + " : " + displayClassName;
	}

	public static void addShowHideOptions(JPopupMenu popupMenu, DiagramView diagramView,
			DiagramOptions diagramOptions) {
		final JCheckBoxMenuItem cbAttrValues = new JCheckBoxMenuItem("Show attributes");
		cbAttrValues.setState(diagramOptions.isShowAttributes());
		cbAttrValues.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				diagramOptions.setShowAttributes(ev.getStateChange() == ItemEvent.SELECTED);
				diagramView.invalidateContent(true);
			}
		});
		popupMenu.add(cbAttrValues);

		final JCheckBoxMenuItem cbAssocNames = new JCheckBoxMenuItem("Show association names");
		cbAssocNames.setState(diagramOptions.isShowAssocNames());
		cbAssocNames.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				diagramOptions.setShowAssocNames(ev.getStateChange() == ItemEvent.SELECTED);
				diagramView.invalidateContent(true);
			}
		});
		popupMenu.add(cbAssocNames);

		final JCheckBoxMenuItem cbRolenames = new JCheckBoxMenuItem("Show role names");
		cbRolenames.setState(diagramOptions.isShowRolenames());
		cbRolenames.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ev) {
				diagramOptions.setShowRolenames(ev.getStateChange() == ItemEvent.SELECTED);
				diagramView.invalidateContent(true);
			}
		});
		popupMenu.add(cbRolenames);

		if (diagramView instanceof OutputClassDiagram) {
			final JCheckBoxMenuItem cbMultiplicities = new JCheckBoxMenuItem("Show multiplicities");
			cbMultiplicities.setState(diagramOptions.isShowMutliplicities());
			cbMultiplicities.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent ev) {
					diagramOptions.setShowMutliplicities(ev.getStateChange() == ItemEvent.SELECTED);
					diagramView.invalidateContent(true);
				}
			});
			popupMenu.add(cbMultiplicities);
		}
	}

	public static void exportUSEModel(Component parent, UseModelApi modelApi) {
		final JFileChooser fc = new JFileChooser(Options.getLastDirectory().toFile());
		ExtFileFilter filter = new ExtFileFilter("use", "USE specifications");
		fc.setFileFilter(filter);
		fc.setDialogTitle("Save specification");

		int returnVal = fc.showSaveDialog(parent);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getCurrentDirectory().toString();
			String filename = fc.getSelectedFile().getName();
			if (!filename.endsWith(".use")) {
				filename += ".use";
			}
			File file = new File(path, filename);

			try {
				PrintWriter printWriter = new PrintWriter(file);
				MMPrintVisitor v = new MMPrintVisitor(printWriter);
				v.visitModel(modelApi.getModel());
				printWriter.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private final static String apostrophe = "'";
	private final static String empty = "";

	public static String setApostrophes(String s) {
		if (s == null) {
			s = empty;
		}
		s = s.replace(apostrophe, empty);
		return apostrophe + s + apostrophe;
	}

	public static boolean equalsWithoutApostrophes(String s1, String s2) {
		return setApostrophes(s1).equals(setApostrophes(s2));
	}
}