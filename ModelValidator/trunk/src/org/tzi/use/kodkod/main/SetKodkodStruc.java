package org.tzi.use.kodkod.main;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.Solution.Outcome;
import kodkod.engine.satlab.SATFactory;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import kodkod.util.nodes.PrettyPrinter;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MSystem;

/**
 * Main Class to combine part formulas and bounds of classes, associations, 
 * associationclasses and invariants
 * relations and bounds for enumerations, integer and boolean
 * are created in this class and can be used from other classes
 * by set methods
 * Kodkod settings (Solver) are made in this class
 * Kodkod model is solved in this class
 * @author  Torsten Humann
 */
public class SetKodkodStruc{
	
	private final MainWindow curMainWindow;
	private final MSystem curSystem;
	private final HashMap<String, UMLClass> classes;
	private final HashMap<String, UMLAssociation> associations;
	private final HashMap<String, UMLAssociationClass> assClasses;
	private final ArrayList<OCLInvar> invariants;
	private final PrintWriter myLogWriter;
	
	private HashMap<String, Relation> allAttRelations = new HashMap<String, Relation>();
	private HashMap<String, Relation> eNumRelations = new HashMap<String, Relation>();
	// JW: for every enum value a Relation
	private HashMap<String, Relation> eNumRelationValues = new HashMap<String, Relation>();
	private Relation idRelation;
	private Relation boolRelation;
	private Relation boolRelationTrue;
	private Relation boolRelationFalse;
	private Relation undefinedRelation;
	private Relation undefinedSetRelation;
	
	private ArrayList<String> noTrans = new ArrayList<String>();
	private ArrayList<String> reas = new ArrayList<String>();
	
	/* ***********************ASSL-Relations********************* */
	private HashMap<String, GenericRelation> genericRelations;
	private List<Formula> additionalFormulas;
	private ArrayList<String> possibleAttributes;
	// ASSL-Translation Object, when its set, it ASSL-Bounds will be used
	private AsslTranslation asslTranslation;
	private Solution lastSolution;
	
	//builds Kodkod main formula and bounds and solves Kodkod model
	//starts creating systemstate if solution was succesfull
	//if independency of all all invariants is tested the systemstate is
	//not created
	//Kodkod settings
	public SetKodkodStruc(MainWindow curMainWin, MSystem curSys, HashMap<String, UMLClass> cla, HashMap<String, UMLAssociation> ass, HashMap<String, UMLAssociationClass> assCla, ArrayList<OCLInvar> inv, PrintWriter myLW){
		curMainWindow = curMainWin;
		curSystem = curSys;
		classes = cla;
		associations = ass;
		assClasses = assCla;
		invariants = inv;
		myLogWriter = myLW;
		for(int i = 0; i < curSystem.model().enumTypes().size(); i++){
			EnumType enType = (EnumType) curSystem.model().enumTypes().toArray()[i];
			eNumRelations.put(enType.name(), Relation.unary(enType.name()));
			// JW: adding for every enum-type a relation
			Iterator<?> it = enType.literals();
			while (it.hasNext() ) {
	            String lit = (String) it.next();
	            String key = enType.name() + "_#" + lit;
	            eNumRelationValues.put(key, Relation.unary(key));
			}
		}
		idRelation = Relation.unary("---ids");
		boolRelation = Relation.unary("---boolean");
		boolRelationTrue = Relation.unary("---boolean_true");
		boolRelationFalse = Relation.unary("---boolean_false");
		undefinedRelation = Relation.unary("---Undefined");
		undefinedSetRelation = Relation.unary("---Undefined_Set");
		genericRelations = new HashMap<String, GenericRelation>();
		additionalFormulas = new ArrayList<Formula>();
		possibleAttributes = new ArrayList<String>();
	}
	
	public void runKodkod(boolean indepen, int solv, boolean useResult){
		
		Formula form = null;
		Formula umlForm = null;
		
		KodkodUniverse kUni = new KodkodUniverse(curSystem.state());
		
		OCLFormulas oclFor = new OCLFormulas(this);
		
		SetStateStruc sss = new SetStateStruc(curMainWindow, curSystem, this.isUseASSLbounds());
		
		lastSolution = null;
		
		for(int i = 0; i < classes.size(); i++){
			UMLClass tmpClass = (UMLClass) classes.values().toArray()[i];
			tmpClass.usingAsslBounds(this.isUseASSLbounds());
			if(tmpClass.getFormula(this) != null){
				if(form == null){
					form = tmpClass.getFormula(this);
				}else{
					form = form.and(tmpClass.getFormula(this));
				}
			}
			allAttRelations.putAll(tmpClass.getAttRelations());
		}
		
		for(int i = 0; i < associations.size(); i++){
			UMLAssociation tmpAss = (UMLAssociation) associations.values().toArray()[i];
			tmpAss.usingAsslBounds(this.isUseASSLbounds());
			if(form == null){
				form = tmpAss.getFormula(this);
			}else{
				form = form.and(tmpAss.getFormula(this));
			}
		}
		
		for(int i = 0; i < assClasses.size(); i++){
			UMLAssociationClass tmpAssCla = (UMLAssociationClass) assClasses.values().toArray()[i];
			tmpAssCla.usingAsslBounds(this.isUseASSLbounds());
			if(form == null){
				form = tmpAssCla.getFormula(this);
			}else{
				form = form.and(tmpAssCla.getFormula(this));
			}
			allAttRelations.putAll(tmpAssCla.getAttRelations());
		}
		
		if(form == null){
			form = Formula.TRUE;
		}
		
		//save UML formula for independency check of all invariants
		//-> only formula for invariants must be set new for every invariant
		if(indepen){
			umlForm = form;
			for(int i = 0; i < invariants.size(); i++){
				invariants.get(i).setFlag(OCLInvar.Flag.p);
			}
		}
		
		form = oclFor.buildOCLForm(form, invariants);
		
		// add additional Formulas from ASSL
		for(int i = 0; i < additionalFormulas.size(); i++) {
			if(form == null){
				form = additionalFormulas.get(i);
			}else{
				form = form.and(additionalFormulas.get(i));
			}
		}
		
		//output of invariants that cannot be translated
		if(noTrans.size()>0){
			curMainWindow.logWriter().println("cannot translate the following invariants:");
			if(indepen){
				for(int i = 0; i < noTrans.size(); i++){
					curMainWindow.logWriter().println(noTrans.get(i));
				}
				curMainWindow.logWriter().println("independency is checked without these invariants");
			}else{
				for(int i = 0; i < noTrans.size(); i++){
					curMainWindow.logWriter().println(noTrans.get(i) + " (reason: " + reas.get(i) + ")");
				}
				curMainWindow.logWriter().println("invariants are deactivated");
			}
		}
		
		final Universe uni;
		if(this.isUseASSLbounds()) {
			uni = kUni.createAsslUniverse(getAllENums(), classes, assClasses, asslTranslation);
		} else {
			uni = kUni.createUniverse(classes, assClasses, getAllENums(), genericRelations, getMaxIntValue());
		}

		if(uni != null){
			final TupleFactory tFa = uni.factory();
			final Solver satSolver = new Solver();
			Bounds bou = new Bounds(uni);
			
			//default = 5 not enough
			satSolver.options().setBitwidth(10);
			
			switch(solv){
			case 0:
				satSolver.options().setSolver(SATFactory.DefaultSAT4J);
				curMainWindow.logWriter().println("using DefaultSAT4J");
				break;
			case 1:
				satSolver.options().setSolver(SATFactory.LightSAT4J);
				curMainWindow.logWriter().println("using LightSAT4J");
				break;
			case 2:
				satSolver.options().setSolver(SATFactory.MiniSat);
				curMainWindow.logWriter().println("using MiniSat");
				break;
			case 3:
				satSolver.options().setSolver(SATFactory.MiniSatProver);
				curMainWindow.logWriter().println("using MiniSatProver");
				break;
			case 4:
				satSolver.options().setSolver(SATFactory.ZChaff);
				curMainWindow.logWriter().println("using ZChaff");
				break;
			case 5:
				satSolver.options().setSolver(SATFactory.ZChaffMincost);
				curMainWindow.logWriter().println("using ZChaffMincost");
				break;
			default:
				satSolver.options().setSolver(SATFactory.DefaultSAT4J);
				curMainWindow.logWriter().println("using DefaultSAT4J");
				break;
			}

			for(int i = 0; i < classes.size(); i++){
				UMLClass tmpClass = (UMLClass) classes.values().toArray()[i];
				if(!this.isUseASSLbounds()) {
					bou = tmpClass.getClassBounds(bou, tFa, this, possibleAttributes);
				} else {
					bou = tmpClass.getClassBoundsAssl(bou, tFa, this, asslTranslation);
				}
			}
			
			for(int i = 0; i < assClasses.size(); i++){
				UMLAssociationClass tmpUMLAss = (UMLAssociationClass) assClasses.values().toArray()[i];
				if(!this.isUseASSLbounds()) {
					bou = tmpUMLAss.getClassBounds(bou, tFa, this);
				} else {
					bou = tmpUMLAss.getClassBoundsAssl(bou, tFa, this, asslTranslation);
				}
			}
			
			//extra loop so that the bounds of all classes and associationclasses
			//are set and can be used for cartproduct of upper bound of associations
			for(int i = 0; i < assClasses.size(); i++){
				UMLAssociationClass tmpUMLAss = (UMLAssociationClass) assClasses.values().toArray()[i];
				if(!this.isUseASSLbounds()) {
					bou = tmpUMLAss.getAssociationBounds(bou, tFa, this);
				} else {
					bou = tmpUMLAss.getAssociationBoundsAssl(bou, tFa, this, asslTranslation);
				}
			}
				
			for(int i = 0; i < associations.size(); i++){
				UMLAssociation tmpAss = (UMLAssociation) associations.values().toArray()[i];
				if(!this.isUseASSLbounds()) {
					bou = tmpAss.getAssociationBounds(bou, tFa, this);
				} else {
					bou = tmpAss.getAssociationBoundsAssl(bou, tFa, this, asslTranslation);
				}
			}
			
			// add generic relations to bound
			Set<String> keys = genericRelations.keySet();
			Iterator<String> genericRelationIterator = keys.iterator();
			while(genericRelationIterator.hasNext()) {
				String key = genericRelationIterator.next();
				genericRelations.get(key).getGenericBounds(bou, tFa, this);
			}
			
			bou = createBoolBounds(bou, tFa);
			if(!this.isUseASSLbounds()) {
				bou = createIDBound(bou, tFa);
			} else {
				bou = createASSLIDBound(bou, tFa);
			}
			bou = createENumBounds(bou, tFa);
			if(indepen){
				boolean changeCheck = false;
				for(int i = 0; i < invariants.size(); i++){
					//invariants that cannot be translated are not check for independency
					if(!noTrans.contains(invariants.get(i).getName())){
						for(int j = 0; j < invariants.size(); j++){
							if(noTrans.contains(invariants.get(j).getName())){
								//invariants that cannot be translated are deactivated
								invariants.get(j).setFlag(OCLInvar.Flag.d);
							}else{
								if(j==i){
									invariants.get(j).setFlag(OCLInvar.Flag.n);
								}else{
									invariants.get(j).setFlag(OCLInvar.Flag.p);
								}	
							}
						}
						form = oclFor.buildOCLForm(umlForm, invariants);
						//trying to solve the Kodkod model
						Solution sol = null;
						if(changeCheck == false){
							try{
								changeCheck = true;
								sol = satSolver.solve(form, bou);
							}catch (UnsatisfiedLinkError e){
								curMainWindow.logWriter().println("cannot find solver");
								curMainWindow.logWriter().println("using SAT4J");
								satSolver.options().setSolver(SATFactory.DefaultSAT4J);
								sol = satSolver.solve(form, bou);
							}catch (NoClassDefFoundError e){
								curMainWindow.logWriter().println("cannot find solver");
								curMainWindow.logWriter().println("using SAT4J");
								satSolver.options().setSolver(SATFactory.DefaultSAT4J);
								sol = satSolver.solve(form, bou);
							}
						}else{
							sol = satSolver.solve(form, bou);
						}
						
						if(sol.outcome().equals(Outcome.TRIVIALLY_SATISFIABLE) || sol.outcome().equals(Outcome.SATISFIABLE)){
							curMainWindow.logWriter().println(invariants.get(i).getName() + " : independency true");
						}else{
							curMainWindow.logWriter().println(invariants.get(i).getName() + " : independency false");
						}
					}
				}
			}else{
				myLogWriter.println(PrettyPrinter.print(form, 2));
				
				Solution sol = null;
				//trying to solve the Kodkod model
				try{
					sol = satSolver.solve(form, bou);
				}catch (UnsatisfiedLinkError e){
					curMainWindow.logWriter().println("cannot find solver");
					curMainWindow.logWriter().println("using SAT4J");
					satSolver.options().setSolver(SATFactory.DefaultSAT4J);
					sol = satSolver.solve(form, bou);
				}catch (NoClassDefFoundError e){
					curMainWindow.logWriter().println("cannot find solver");
					curMainWindow.logWriter().println("using SAT4J");
					satSolver.options().setSolver(SATFactory.DefaultSAT4J);
					sol = satSolver.solve(form, bou);
				}
				
				if(sol.outcome().equals(Outcome.TRIVIALLY_SATISFIABLE) || sol.outcome().equals(Outcome.SATISFIABLE)){
					lastSolution = sol;
					if(useResult) {
						myLogWriter.println(sol.instance());
						sss.addObjectInState(sol);
					}
				}
				if(useResult) {
					sss.kodkodOutput(sol);
				}
			}
		}else{
			myLogWriter.println(PrettyPrinter.print(form, 2));
			curMainWindow.logWriter().println("cannot run kodkod");
			curMainWindow.logWriter().println("upper bounds for all classes and associationclasses = 0");
			boolean checkState = curSystem.state().checkStructure(curMainWindow.logWriter());
			if(checkState){
				curMainWindow.logWriter().println("ok");
			}else{
				curMainWindow.logWriter().println("found errors");	
			}
		}
	}
	
	public UMLClass getUMLClass(String nam){
		if(classes.containsKey(nam)){
			return classes.get(nam);
		}else{
			return assClasses.get(nam);
		}
	}
	
	public Relation getClassRelation(String nam){
		if(classes.containsKey(nam)){
			return classes.get(nam).getClaRelation();
		}else{
			return assClasses.get(nam).getClaRelation();
		}
	}
	
	public boolean checkAssociationClass(String nam){
		return assClasses.containsKey(nam);
	}
	
	public Relation getAssRelation(String nam){
		return associations.get(nam).getAssRelation();
	}
	
	public UMLAssociation getAssociation(String nam){
		return associations.get(nam);
	}
	
	public Relation getAssClaRelation(String nam){
		return assClasses.get(nam).getAssRelation();
	}
	
	public UMLAssociationClass getAssociationClass(String nam){
		return assClasses.get(nam);
	}
	
	public Relation getAttRelation(String nam){
		return allAttRelations.get(nam);
	}
	
	public Relation getENumRelation(String nam){
		return eNumRelations.get(nam);
	}
	
	// JW: get Value Relation
	public Relation getENumValueRelation(String nam){
		return eNumRelationValues.get(nam);
	}
	
	public Relation getIDRelation(){
		return idRelation;
	}
	
	public Relation getBoolRelation(){
		return boolRelation;
	}
	
	public Relation getBoolRelationTrue(){
		return boolRelationTrue;
	}
	
	public Relation getBoolRelationFalse(){
		return boolRelationFalse;
	}
	
	public Relation getUndefinedRelation() {
		return undefinedRelation;
	}
	
	public Relation getUndefinedSetRelation() {
		return undefinedSetRelation;
	}
	
	public Bounds createBoolBounds(Bounds bou, TupleFactory tFa){
		TupleSet tsB = tFa.noneOf(1);
		tsB.add(tFa.tuple("@true"));
		tsB.add(tFa.tuple("@false"));
		bou.boundExactly(boolRelation, tsB);
		
		TupleSet tsBT = tFa.noneOf(1);
		tsBT.add(tFa.tuple("@true"));
		bou.boundExactly(boolRelationTrue, tsBT);
		
		TupleSet tsBF = tFa.noneOf(1);
		tsBF.add(tFa.tuple("@false"));
		bou.boundExactly(boolRelationFalse, tsBF);
		
		TupleSet tsU = tFa.noneOf(1);
		tsU.add(tFa.tuple("Undefined"));
		bou.boundExactly(undefinedRelation, tsU);
		
		TupleSet tsUS = tFa.noneOf(1);
		tsUS.add(tFa.tuple("Undefined_Set"));
		bou.boundExactly(undefinedSetRelation, tsUS);
		
		return bou;
	}
	
	//returns the max possible value for integer attributes
	public int getMaxIntValue(){
		int ret = 1;
		int tmp = 0;
		
		for(int i = 0; i < classes.size(); i++){
			UMLClassImpl tmpUML = (UMLClassImpl) classes.values().toArray()[i];
			for(int j = 0; j < tmpUML.getUMLAttributeNames().size(); j++){
				if(tmpUML.getUMLAttributeNames().get(j).isInteger()){
					tmp = tmpUML.getUMLAttributeNames().get(j).getMaxValue();
					if(tmp > ret){
						ret = tmp;
					}
				}
			}
		}
		for(int i = 0; i < assClasses.size(); i++){
			UMLAssociationClassImpl tmpUML = (UMLAssociationClassImpl) assClasses.values().toArray()[i];
			for(int j = 0; j < tmpUML.getUMLAttributeNames().size(); j++){
				if(tmpUML.getUMLAttributeNames().get(j).isInteger()){
					tmp = tmpUML.getUMLAttributeNames().get(j).getMaxValue();
					if(tmp > ret){
						ret = tmp;
					}
				}
			}
		}
		
		return ret;
	}
	
	public int getAsslMaxIntValue() {
		int ret = 1;
		
		return ret;
	}
	
	public Bounds createIDBound(Bounds bou, TupleFactory tFa){
		TupleSet tsID = tFa.noneOf(1);
		for(int i = 0; i < getMaxIntValue(); i++){
			tsID.add(tFa.tuple("@id" + i));
		}
		bou.bound(idRelation, tsID);
		
		return bou;
	}
	
	public Bounds createASSLIDBound(Bounds bou, TupleFactory tFa){
		TupleSet tsID = tFa.noneOf(1);
		for(int i = 0; i < asslTranslation.getMaxInteger(); i++){
			tsID.add(tFa.tuple("@id" + i));
		}
		bou.bound(idRelation, tsID);
		
		return bou;
	}
	
	public Bounds createENumBounds(Bounds bou, TupleFactory tFa){
		for(int i = 0; i < curSystem.model().enumTypes().size(); i++){
			TupleSet eNum = tFa.noneOf(1);
			EnumType enType = (EnumType) curSystem.model().enumTypes().toArray()[i];
			Relation eNumRel = eNumRelations.get(enType.name());
			Iterator<?> it = enType.literals();
			while (it.hasNext() ) {
	            String lit = (String) it.next();
	            String value = enType.name() + "_#" + lit;
	            eNum.add(tFa.tuple(value));
	            // Add EnumValues to thier own Relation
	            Relation eNumValueRel = eNumRelationValues.get(value);
	            TupleSet eNumValue = tFa.noneOf(1);
	            eNumValue.add(tFa.tuple(value));
	            bou.boundExactly(eNumValueRel, eNumValue);	            
	        }
			bou.boundExactly(eNumRel, eNum);
		}
		return bou;
	}
	
	public ArrayList<String> getAllENums(){
		ArrayList<String> ret = new ArrayList<String>();
		for(int i = 0; i < curSystem.model().enumTypes().size(); i++){
			EnumType enType = (EnumType) curSystem.model().enumTypes().toArray()[i];
			Iterator<?> it = enType.literals();
			while (it.hasNext() ) {
	            String lit = (String) it.next();
	            ret.add(enType.name() + "_#" + lit);
			}
		}
		return ret;
	}
	
	//sets the problems of translating the OCL invariants
	public void addNoTrans(MClassInvariant mci, org.tzi.use.uml.ocl.expr.Expression negExp){
		noTrans.add(mci.name());
		reas.add(negExp.toString());
	}
	
	public static ArrayList<ArrayList<String>> cartProduct(ArrayList<ArrayList<String>> sets) {
		ArrayList<ArrayList<String>> tmpCartProd = new ArrayList<ArrayList<String>>();
		ArrayList<String> cartProdElem = new ArrayList<String>();
		int cartSize = 1;
		//loop to get cardinality of Cartesian product
		for(int i = 0; i < sets.size(); i++){
			cartSize = cartSize * sets.get(i).size();
		}
		//loop to create all elements of Cartesian product
	    for(int i = 0; i < cartSize; i++){
	    	int div = 1;
	    	cartProdElem = new ArrayList<String>();
	    	//loop that collects one element of each class
	    	for(int j = 0; j < sets.size(); j ++){
	    		cartProdElem.add(sets.get(j).get((i / div) % sets.get(j).size()));
	    		div = div * sets.get(j).size();
			}
	    	tmpCartProd.add(cartProdElem);
	    }
	    return tmpCartProd;
	}
	
	public void addGenericRelation(GenericRelation relation) {
		genericRelations.put(relation.getName(), relation);
	}
	
	public void setAdditionalFormulas(List<Formula> additionalFormulas) {
		this.additionalFormulas = additionalFormulas;
	}
	
	public void setPossibleAttributesList(ArrayList<String> possibleAttributes) {
		this.possibleAttributes = possibleAttributes;
	}

	/**
	 * @param useASSLbounds the useASSLbounds to set
	 */
	public void setASSLTranslation(AsslTranslation asslTranslation) {
		this.asslTranslation = asslTranslation;
	}

	/**
	 * @return the useASSLbounds
	 */
	public boolean isUseASSLbounds() {
		return asslTranslation != null;
	}
	
	public boolean hasASolution() {
		return lastSolution != null;
	}
	
	public void useSolution() {
		if(hasASolution()) {
			SetStateStruc sss = new SetStateStruc(curMainWindow, curSystem, this.isUseASSLbounds());
			myLogWriter.println(lastSolution.instance());
			sss.addObjectInState(lastSolution);
			sss.kodkodOutput(lastSolution);
		}
	}
}