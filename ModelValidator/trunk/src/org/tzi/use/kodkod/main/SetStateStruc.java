package org.tzi.use.kodkod.main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kodkod.engine.Solution;
import kodkod.engine.Solution.Outcome;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.parser.ocl.OCLCompiler;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.type.ObjectType;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;
import org.tzi.use.uml.sys.soil.MNewLinkObjectStatement;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;
import org.tzi.use.uml.sys.soil.MRValue;
import org.tzi.use.uml.sys.soil.MRValueExpression;

/**
 * setting the system state represented by the Kodkod solution 
 * @author  Torsten Humann
 */
public class SetStateStruc{
	
	private MainWindow curMainWindow;
	private MSystem curSystem;
	private HashMap<String, String> nameChange = new HashMap<String, String>();
	private boolean useASSL;
	
	public SetStateStruc(MainWindow curMainWin, MSystem curSys, boolean useASSL){
		curMainWindow = curMainWin;
		curSystem = curSys;
		this.useASSL = useASSL;
	}
	
	//atoms of relations of solution are checked and the represented objects, links
	//and values of attributes are created and set
	//nameChange is used to get explicit names of objects (if an object with the same
	//name already exists but is represented by another atom)
	public void addObjectInState(Solution sol){
		
		String kodkodName = new String();
		String objName = new String();
		String claName = new String();
		String relName = new String();
		String attName = new String();
		String tmpObjName = new String();
		
		if(useASSL){ 
			curSystem.reset();
		}
		
		int atIndex;
		int index;
		int neuIndex;
		
		HashMap<String, Integer> intValue = new HashMap<String, Integer>();
		
		VarBindings varBindings = curSystem.varBindings();
		
		for(int i = 0; i < sol.instance().relations().size(); i++){
			//only atoms that represent objects of the class itself (and no subclass) are
			//created from atoms of the relation of a class
			if(curSystem.model().getClass(sol.instance().relations().toArray()[i].toString()) != null && curSystem.model().getAssociation(sol.instance().relations().toArray()[i].toString()) == null){
				MClass cla = curSystem.model().getClass(sol.instance().relations().toArray()[i].toString());
				TupleSet tsOutput = (TupleSet) sol.instance().relationTuples().values().toArray()[i];
				List<String> names = new ArrayList<String>();
				for(int j = 0; j < tsOutput.size(); j++){
					Tuple tOutput = (Tuple) tsOutput.toArray()[j];
					objName = tOutput.atom(0).toString();
					kodkodName = objName;
					atIndex = objName.indexOf('@');
					if(objName.length() < sol.instance().relations().toArray()[i].toString().length()){
						claName = objName;
					}else{
						claName = objName.substring(0, 1).toLowerCase().concat(objName.substring(1)).substring(0, sol.instance().relations().toArray()[i].toString().length());
					}
					relName = sol.instance().relations().toArray()[i].toString().substring(0, 1).toLowerCase().concat(sol.instance().relations().toArray()[i].toString().substring(1));
					if(claName.equals(relName) && atIndex == relName.length()){
						tmpObjName = objName.substring((sol.instance().relations().toArray()[i].toString().length() + 1));
						index = tmpObjName.indexOf("_");
						if(index != -1){
							objName = tmpObjName.substring(index + 1);
							nameChange.put(kodkodName, objName);
						}else{
							objName = objName.replaceFirst("@", "");
							if(varBindings.getValue(objName) != null){
								neuIndex = 0;
								tmpObjName = objName;
								do{
									neuIndex++;
									objName = tmpObjName + "_" + neuIndex;
								}while(varBindings.getValue(objName) != null);
							}
							names.add(objName);
							nameChange.put(kodkodName, objName);
						}
					}
				}
				
				try{
					//TODO Objekte erstellen
					// Über alle Namen iterieren
					for(int j = 0; j < names.size(); j++) {
						// create object
						MNewObjectStatement clsCmd = new MNewObjectStatement(cla, names.get(j));
						curSystem.evaluateStatement(clsCmd);
					}
//					MCmdCreateObjects clsCmd = new MCmdCreateObjects(curSystem.state(), names, TypeFactory.mkObjectType(cla));
//					curSystem.executeCmd(clsCmd);
				}catch(MSystemException e){
					System.out.println(e);
				}
			}
			//only atoms that represent objects of the class itself (and no subclass) are
			//created from atoms of the relation of an associationclass
			//the represented links are created too
			if(curSystem.model().getAssociationClass(sol.instance().relations().toArray()[i].toString()) != null){
				MAssociationClass assCla = curSystem.model().getAssociationClass(sol.instance().relations().toArray()[i].toString());
				TupleSet tsOutput = (TupleSet) sol.instance().relationTuples().values().toArray()[i];
				for(int j = 0; j < tsOutput.size(); j++){
					Tuple tOutput = (Tuple) tsOutput.toArray()[j];	
					objName = tOutput.atom(0).toString();
					kodkodName = objName;
					atIndex = objName.indexOf('@');
					if(objName.length() < sol.instance().relations().toArray()[i].toString().length()){
						claName = objName;
					}else{
						claName = objName.substring(0, 1).toLowerCase().concat(objName.substring(1)).substring(0, sol.instance().relations().toArray()[i].toString().length());
					}
					relName = sol.instance().relations().toArray()[i].toString().substring(0, 1).toLowerCase().concat(sol.instance().relations().toArray()[i].toString().substring(1));
					if(claName.equals(relName) && atIndex == relName.length()){
						tmpObjName = objName.substring((sol.instance().relations().toArray()[i].toString().length() + 1));
						index = tmpObjName.indexOf("_");
						if(index != -1){
							objName = tmpObjName.substring(index + 1);
							nameChange.put(kodkodName, objName);
						}else{
							//TODO List type ändern
							List<MRValue> links = new ArrayList<MRValue>();
							//List<String> links = new ArrayList<String>();
							objName = objName.replaceFirst("@", "");
							if(varBindings.getValue(objName) != null){
								neuIndex = 0;
								tmpObjName = objName;
								do{
									neuIndex++;
									objName = tmpObjName + "_" + neuIndex;
								}while(varBindings.getValue(objName) != null);
							}
							nameChange.put(kodkodName, objName);
							for(int k = 1; k < tOutput.arity(); k++){
								ObjectType type = curSystem.state().objectByName(tOutput.atom(k).toString().replace("@", "")).type();
								ObjectValue value = new ObjectValue(type, curSystem.state().objectByName(tOutput.atom(k).toString().replace("@", "")));
								links.add(new MRValueExpression(value));
								//links.add(nameChange.get(tOutpgut.atom(k).toString()));
							}
							try{
								// TODO CreateInsertObjects
								// Ueber alle Links iterieren
								MNewLinkObjectStatement assClaCmd = new MNewLinkObjectStatement(assCla, links, objName);
								curSystem.evaluateStatement(assClaCmd);
								
								//MCmdCreateInsertObjects assClaCmd = new MCmdCreateInsertObjects(curSystem.state(), objName, assCla, links);
								//curSystem.executeCmd(assClaCmd);
							}catch (MSystemException e){
								System.out.println(e);
							}
						}
					}
				}
			}
			
			//creates the represented links of associations
			if(curSystem.model().getAssociation(sol.instance().relations().toArray()[i].toString()) != null && curSystem.model().getClass(sol.instance().relations().toArray()[i].toString()) == null){
				MAssociation ass = curSystem.model().getAssociation(sol.instance().relations().toArray()[i].toString());
				TupleSet tsOutput = (TupleSet) sol.instance().relationTuples().values().toArray()[i];
				for(int j = 0; j < tsOutput.size(); j++){
					Tuple tOutput = (Tuple) tsOutput.toArray()[j];
					MObject[] obj = new MObject[tOutput.arity()];
					Expression[] expr = new Expression[tOutput.arity()];
					boolean hasNoUndefinedValue = true;
					for(int k = 0; k < tOutput.arity(); k++){
						objName = nameChange.get(tOutput.atom(k).toString());
						if(objName != null) {
							expr[k] = OCLCompiler.compileExpression(curSystem.model(), objName, "<input>", new PrintWriter(System.err), varBindings);
							obj[k] = curSystem.state().objectByName(objName);
						} else {
							hasNoUndefinedValue = false;
						}
					}
					if(!curSystem.state().hasLinkBetweenObjects(ass, obj) && hasNoUndefinedValue){
						try {
							MLinkInsertionStatement assCmd = new MLinkInsertionStatement(ass, obj, Collections.<List<Value>>emptyList());
							curSystem.evaluateStatement(assCmd);
//							MCmdInsertLink assCmd = new MCmdInsertLink(curSystem.state(), expr, ass);
//							curSystem.executeCmd(assCmd);
						} catch (MSystemException e) {
							System.out.println(e);
						}
					}
				}
			}
		}
		
		//sets the values of attributes represented by relations
		//extra loop so that all objects exist and the attributes can be set
		for(int i = 0; i < sol.instance().relations().size(); i++){
			if(sol.instance().relations().toArray()[i].toString().contains("_-")){
				index = sol.instance().relations().toArray()[i].toString().indexOf("_-");
				if(!sol.instance().relations().toArray()[i].toString().substring(index + 2).contains("-")){
					MAttribute att;
					if(sol.instance().relations().toArray()[i].toString().substring(0, 1).equals("-")){
						att = curSystem.model().getClass(sol.instance().relations().toArray()[i].toString().substring(1, sol.instance().relations().toArray()[i].toString().indexOf("_-"))).attribute(sol.instance().relations().toArray()[i].toString().substring(sol.instance().relations().toArray()[i].toString().indexOf("_-") + 2), true);
					}else{
						att = curSystem.model().getClass(sol.instance().relations().toArray()[i].toString().substring(0, sol.instance().relations().toArray()[i].toString().indexOf("_-"))).attribute(sol.instance().relations().toArray()[i].toString().substring(sol.instance().relations().toArray()[i].toString().indexOf("_-") + 2), true);
					}
					TupleSet tsOutput = (TupleSet) sol.instance().relationTuples().values().toArray()[i];
					//if type of attribute is class or associationclass
					if(att.type().isObjectType()){
						for(int j = 0; j < tsOutput.size(); j++){
							Tuple tOutput = (Tuple) tsOutput.toArray()[j];
							objName = nameChange.get(tOutput.atom(0).toString());
							if(!tOutput.atom(tOutput.arity() - 1).toString().equals("Undefined")) {
								attName = nameChange.get(tOutput.atom(tOutput.arity() - 1).toString());
							} else {
								attName = "Undefined";
							}
							Expression objExp = OCLCompiler.compileExpression(curSystem.model(), objName, "<input>", new PrintWriter(System.err), varBindings);
							Expression attExp = OCLCompiler.compileExpression(curSystem.model(), attName, "<input>", new PrintWriter(System.err), varBindings);
							//ExpAttrOp expAO = new ExpAttrOp(att, objExp);
							try{
								//TODO Set Attributes 
								MAttributeAssignmentStatement attCmd = new MAttributeAssignmentStatement(objExp, att, attExp);
								curSystem.evaluateStatement(attCmd);
//								MCmdSetAttribute attCmd = new MCmdSetAttribute(curSystem.state(), expAO, attExp);
//								curSystem.executeCmd(attCmd);
							}catch(MSystemException e){
								System.out.println(e);
							}
						}
					//if type of attribute is an enumeration
					}else if(att.type().isEnum()){
						for(int j = 0; j < tsOutput.size(); j++){
							Tuple tOutput = (Tuple) tsOutput.toArray()[j];
							objName = nameChange.get(tOutput.atom(0).toString());
							attName = tOutput.atom(tOutput.arity() - 1).toString().substring(tOutput.atom(tOutput.arity() - 1).toString().indexOf("_") + 1);
							if(!attName.equals("Undefined")){
								Expression objExp = OCLCompiler.compileExpression(curSystem.model(), objName, "<input>", new PrintWriter(System.err), varBindings);
								Expression attExp = OCLCompiler.compileExpression(curSystem.model(), attName, "<input>", new PrintWriter(System.err), varBindings);
								//ExpAttrOp expAO = new ExpAttrOp(att, objExp);
								try{
									//TODO Set Attributes 
									MAttributeAssignmentStatement attCmd = new MAttributeAssignmentStatement(objExp, att, attExp);
									curSystem.evaluateStatement(attCmd);
//									MCmdSetAttribute attCmd = new MCmdSetAttribute(curSystem.state(), expAO, attExp);
//									curSystem.executeCmd(attCmd);
								}catch(MSystemException e){
									System.out.println(e);
								}
							}
						}
					//if type of attribute is integer (number of pairs in relation)
					}else if(att.type().isInteger()){
						intValue.clear();
						for(int j = 0; j < tsOutput.size(); j++){
							Tuple tOutput = (Tuple) tsOutput.toArray()[j];
							if(!intValue.containsKey(tOutput.atom(0).toString())){
								intValue.put(tOutput.atom(0).toString(), 1);
							}else{
								intValue.put(tOutput.atom(0).toString(), intValue.get(tOutput.atom(0).toString()) + 1);
							}
						}
						for(int j = 0; j < intValue.size(); j++){
							objName = nameChange.get(intValue.keySet().toArray()[j]);
							Integer value = intValue.get(intValue.keySet().toArray()[j]);
							attName = value.toString();
							// check for undefined
							if(value.intValue() == 1) {
								Tuple tOutput = (Tuple) tsOutput.toArray()[0];
								if(tOutput.atom(tOutput.arity()-1).equals("Undefined")) {
									attName = "Undefined";
								}
							}
							Expression objExp = OCLCompiler.compileExpression(curSystem.model(), objName, "<input>", new PrintWriter(System.err), varBindings);
							Expression attExp = OCLCompiler.compileExpression(curSystem.model(), attName, "<input>", new PrintWriter(System.err), varBindings);
							//ExpAttrOp expAO = new ExpAttrOp(att, objExp);
							try{
								//TODO Set Attributes 
								MAttributeAssignmentStatement attCmd = new MAttributeAssignmentStatement(objExp, att, attExp);
								curSystem.evaluateStatement(attCmd);
//								MCmdSetAttribute attCmd = new MCmdSetAttribute(curSystem.state(), expAO, attExp);
//								curSystem.executeCmd(attCmd);
							}catch(MSystemException e){
								System.out.println(e);
							}
						}
					//if type of attribute is boolean
					}else if(att.type().isBoolean()){
						for(int j = 0; j < tsOutput.size(); j++){
							Tuple tOutput = (Tuple) tsOutput.toArray()[j];
							objName = nameChange.get(tOutput.atom(0).toString());
							if(!tOutput.atom(tOutput.arity() - 1).toString().equals("Undefined")) {
								attName = tOutput.atom(tOutput.arity() - 1).toString().substring(1);
							} else {
								attName = "Undefined";
							}
							Expression objExp = OCLCompiler.compileExpression(curSystem.model(), objName, "<input>", new PrintWriter(System.err), varBindings);
							Expression attExp = OCLCompiler.compileExpression(curSystem.model(), attName, "<input>", new PrintWriter(System.err), varBindings);
							//ExpAttrOp expAO = new ExpAttrOp(att, objExp);
							try{
								//TODO Set Attributes 
								MAttributeAssignmentStatement attCmd = new MAttributeAssignmentStatement(objExp, att, attExp);
								curSystem.evaluateStatement(attCmd);
//								MCmdSetAttribute attCmd = new MCmdSetAttribute(curSystem.state(), expAO, attExp);
//								curSystem.executeCmd(attCmd);
							}catch(MSystemException e){
								System.out.println(e);
							}
						}
					//if type of attribute is String or Real	
					}else{
						for(int j = 0; j < tsOutput.size(); j++){
							Tuple tOutput = (Tuple) tsOutput.toArray()[j];
							objName = nameChange.get(tOutput.atom(0).toString());
							String className = curSystem.state().objectByName(objName).cls().name();
							attName = tOutput.atom(tOutput.arity() - 1).toString();
							if(!attName.equals("Undefined"))
								attName = tOutput.atom(tOutput.arity() - 1).toString().substring(className.length() + att.name().length() + 1);
							attName = attName.substring(attName.indexOf("_") + 1);
							
							if(!attName.equals("Undefined")){
								Expression objExp = OCLCompiler.compileExpression(curSystem.model(), objName, "<input>", new PrintWriter(System.err), varBindings);
								Expression attExp = OCLCompiler.compileExpression(curSystem.model(), attName, "<input>", new PrintWriter(System.err), varBindings);
								//ExpAttrOp expAO = new ExpAttrOp(att, objExp);
								try{
									//TODO Set Attributes 
									MAttributeAssignmentStatement attCmd = new MAttributeAssignmentStatement(objExp, att, attExp);
									curSystem.evaluateStatement(attCmd);
//									MCmdSetAttribute attCmd = new MCmdSetAttribute(curSystem.state(), expAO, attExp);
//									curSystem.executeCmd(attCmd);
								}catch(MSystemException e){
									System.out.println(e);
								}
							}
						}
					}	
				}
			}
		}
	}
	
	//prints Solving informations to the logWriter
	public void kodkodOutput(Solution sol){
		curMainWindow.logWriter().println("kodkod outcome: " + sol.outcome().toString());
		curMainWindow.logWriter().println("translation time to CNF: " + sol.stats().translationTime() + " ms");
		curMainWindow.logWriter().println("solving time of CNF: " + sol.stats().solvingTime() + " ms");
		curMainWindow.logWriter().println("needed clauses: " + sol.stats().clauses());
		curMainWindow.logWriter().println("used primary variables: " + sol.stats().primaryVariables());
		curMainWindow.logWriter().println("needed variables: " + sol.stats().variables());
		if(sol.outcome().equals(Outcome.TRIVIALLY_UNSATISFIABLE) || sol.outcome().equals(Outcome.UNSATISFIABLE)){
			curMainWindow.logWriter().println("proof: " + sol.proof());
		}else{
			boolean checkState = curSystem.state().checkStructure(curMainWindow.logWriter());
			if(checkState){
				curMainWindow.logWriter().println("ok");
			}else{
				curMainWindow.logWriter().println("found errors");	
			}
		}
		curMainWindow.logWriter().flush();
	}
	
}