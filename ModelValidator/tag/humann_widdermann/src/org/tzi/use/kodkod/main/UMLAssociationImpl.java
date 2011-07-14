package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import kodkod.ast.Decls;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

import org.tzi.use.kodkod.assl.AsslTranslation;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;

/**
 * creates the relations for an association, creates the formula,
 * creates the bounds
 * max and min number of links of the association are represented
 * by variable lowerBound and upperBound
 * @author  Torsten Humann
 */
public class UMLAssociationImpl implements UMLAssociation{
	
	private MSystemState curState;
	private final MModel curModel;
	private final MAssociation mAssociation;
	private int lowerBound;
	private int upperBound;
	private Relation assRelation;
	private boolean isUsingAsslBound;
	
	public UMLAssociationImpl(MSystem curSys, MAssociation mAss){
		curState = curSys.state();
		curModel = curSys.model();
		mAssociation = mAss;
		lowerBound = curSys.state().linksOfAssociation(mAss).size();
		upperBound = curSys.state().linksOfAssociation(mAss).size();
		assRelation = Relation.nary(mAssociation.name(), mAssociation.associationEnds().toArray().length);
		isUsingAsslBound = false;
	}
	
	public void setLowerBound(int lb){
		lowerBound = lb;
	}
	
	public int getLowerBound(){
		return lowerBound;
	}
	
	public void setUpperBound(int ub){
		upperBound = ub;
	}
	
	public int getUpperBound(){
		return upperBound;
	}
	
	public MAssociation getMAssociation(){
		return mAssociation;
	}
	
	public String getName(){
		return mAssociation.name();
	}
	
	public Relation getAssRelation(){
		return assRelation;
	}
	
	//builds the Kodkod formula for the associated association
	public Formula getFormula(SetKodkodStruc skks){
		
		Formula form = null;
		
		int ind = 0;
		
		Expression expHead = null;
		Expression expBody = null;
		Expression expVar = null;
		Decls varDecl = null;
		Formula tmpForm = null;
		boolean hasUndefinedValue = false;
		
		Variable v[] = new Variable[assRelation.arity() - 1];
		
		expHead = assRelation;
		Expression tmpExpression = assRelation; //TODO hier weitermachen... in der SChleife undefined reinbekommen
		for(int i = 0; i < assRelation.arity() - 1; i++){
			expHead = expHead.join(Expression.UNIV);
			expBody = Expression.UNIV.join(assRelation);
			for(int j = 0; j < i; j++){
				expBody = Expression.UNIV.join(expBody);
			}
			for(int j = i; j < assRelation.arity() - 2; j++){
				expBody = expBody.join(Expression.UNIV);
			}
			MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[i + 1];
			if(i == 0){
				// Undefinded beim 0..1 mit hinzufügen
				if(assEnd.multiplicity().toString().equals("0..1") && mAssociation.associationEnds().size() == 2) {
					hasUndefinedValue = true;
					tmpExpression = tmpExpression.join(Expression.UNIV.difference(skks.getUndefinedRelation()));
					tmpForm = expBody.in(skks.getClassRelation(assEnd.cls().name()).union(skks.getUndefinedRelation()));
				} else {
					tmpExpression = tmpExpression.join(Expression.UNIV);
					tmpForm = expBody.in(skks.getClassRelation(assEnd.cls().name()));
				}
			}else{
				// Undefinded beim 0..1 mit hinzufügen
				if(assEnd.multiplicity().toString().equals("0..1") && mAssociation.associationEnds().size() == 2) {
					hasUndefinedValue = true;
					tmpForm = tmpForm.and(expBody.in(skks.getClassRelation(assEnd.cls().name()).union(skks.getUndefinedRelation())));
				} else {
					tmpExpression = tmpExpression.join(Expression.UNIV);
					tmpForm = tmpForm.and(expBody.in(skks.getClassRelation(assEnd.cls().name())));
				}
			}
			if(i == assRelation.arity() - 2){
				assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[0];
				// Undefinded beim 0..1 mit hinzufügen
				if(assEnd.multiplicity().toString().equals("0..1") && mAssociation.associationEnds().size() == 2) {
					hasUndefinedValue = true;
					tmpExpression = tmpExpression.difference(skks.getUndefinedRelation());
					tmpForm = expHead.in(skks.getClassRelation(assEnd.cls().name()).union(skks.getUndefinedRelation())).and(tmpForm);
				} else {
					//tmpExpression = tmpExpression.join(Expression.UNIV);
				    tmpForm = expHead.in(skks.getClassRelation(assEnd.cls().name())).and(tmpForm);
				}
			}
		}
		form = tmpForm;
		for(int i = 0; i < mAssociation.associationEnds().toArray().length; i++){
			if(getAssociationHasUnlimited((MAssociationEnd) mAssociation.associationEnds().toArray()[i])/* getAssociatedClassMin(i) != 0 || getAssociatedClassMax(i) != -1*/){
				ind = 0;
				for(int j = 0; j < mAssociation.associationEnds().toArray().length; j++){
					if(j != i){
						v[ind] = Variable.unary("v" + (j + 1));
						MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[j];
						if(ind == 0){
							varDecl = v[ind].oneOf(skks.getClassRelation(assEnd.cls().name()));
						}else{
							varDecl = varDecl.and(v[ind].oneOf(skks.getClassRelation(assEnd.cls().name())));
						}
						ind++;
					}
				}
				for(int j = 0; j < i; j++){
					if(j == 0){
						expVar = v[j].join(assRelation);
					}else{
						expVar = v[j].join(expVar);
					}
				}
				for(int j = mAssociation.associationEnds().toArray().length - 1; j > i; j--){
					if(j == mAssociation.associationEnds().toArray().length - 1 && i == 0){
						expVar = assRelation.join(v[j - 1]);
					}else{
						expVar = expVar.join(v[j - 1]);
					}
				}
			}
			Formula minMax = getAssociationFormulaMinMax((MAssociationEnd) mAssociation.associationEnds().toArray()[i], expVar, varDecl);
			if(minMax != null){
				form = form.and(minMax);				
			}
//			if(getAssociatedClassMin(i) != 0){
//				form = form.and(expVar.count().gte(IntConstant.constant(getAssociatedClassMin(i))).forAll(varDecl));
//			}
//			if(getAssociatedClassMax(i) != -1){
//				form = form.and(expVar.count().lte(IntConstant.constant(getAssociatedClassMax(i))).forAll(varDecl));
//			}
		}
		
		if(!isUsingAsslBound){
			//Handling for Undefined
			if(hasUndefinedValue) {
				form = form.and(tmpExpression.count().gte(IntConstant.constant(lowerBound)));
			} else {
				form = form.and(assRelation.count().gte(IntConstant.constant(lowerBound)));			
			}
			// Ignore if upperBound = -1
			if(upperBound > -1){
				if(hasUndefinedValue) {
					form = form.and(tmpExpression.count().lte(IntConstant.constant(upperBound)));
				} else {			
					form = form.and(assRelation.count().lte(IntConstant.constant(upperBound)));
				}
			}
		}
		return form;
	}
	
	//sets the bounds of relations of the association
	public Bounds getAssociationBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks){
		
		ArrayList<String> associationAtomsLower = new ArrayList<String>();
		
		TupleSet tsAssociationLower = tFa.noneOf(assRelation.arity());
		TupleSet tsAssociationUpper = tFa.noneOf(assRelation.arity());
		TupleSet tmpTS = null;
		
		Relation tmpRelation;	
		
		//lower Bounds
		for(int i = 0; i < curState.linksOfAssociation(curModel.getAssociation(mAssociation.name())).links().size(); i++){
			MLink curLink = (MLink) curState.linksOfAssociation(curModel.getAssociation(mAssociation.name())).links().toArray()[i];
			associationAtomsLower.clear();
			for(int j = 0; j < mAssociation.associationEnds().toArray().length; j++){
				MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[j];
				for(int k = 0; k < getAssociatedClassAndSubclassName(j).size(); k++){
					UMLClass tmpUMLCla = skks.getUMLClass(getAssociatedClassAndSubclassName(j).get(k));
					for(int l = 0; l < tmpUMLCla.getObjectCount(); l++){
						MObject obj = (MObject) curState.objectsOfClass(tmpUMLCla.getMClass()).toArray()[l];
						if(obj.name().equals(curLink.linkEnd(assEnd).object().name())){
							associationAtomsLower.add(tmpUMLCla.getBoundName() + (l + 1) + "_" + curLink.linkEnd(assEnd).object().name());
							k = getAssociatedClassAndSubclassName(j).size();
						}
					}
				}
			}
			tsAssociationLower.add(tFa.tuple(associationAtomsLower));
		}
		
		//upper Bounds
		for(int i = 0; i < mAssociation.associationEnds().toArray().length; i++){
			MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[i];
			tmpRelation = skks.getClassRelation(assEnd.cls().name());
			if(i == 0){
				// Special handling for 0..1
				if(assEnd.multiplicity().toString().equals("0..1") && mAssociation.associationEnds().toArray().length == 2) {
					tmpTS = bou.upperBound(tmpRelation);
					final TupleSet union_undefined = tFa.noneOf(1);
					union_undefined.addAll(tmpTS);
					union_undefined.add(tFa.tuple("Undefined"));
					tmpTS = union_undefined;
				} else {
					tmpTS = bou.upperBound(tmpRelation);
				}
			}else{
				// Special handling for 0..1
				if(assEnd.multiplicity().toString().equals("0..1") && mAssociation.associationEnds().toArray().length == 2) {
					TupleSet tmpTS2 = bou.upperBound(tmpRelation);
					final TupleSet union_undefined = tFa.noneOf(1);
					union_undefined.addAll(tmpTS2);
					union_undefined.add(tFa.tuple("Undefined"));
					tmpTS = tmpTS.product(union_undefined);
				} else {
					tmpTS = tmpTS.product(bou.upperBound(tmpRelation));
				}
			}
		}
		tsAssociationUpper = tmpTS;
		bou.bound(assRelation, tsAssociationLower, tsAssociationUpper);
		
		return bou;
	}
	
	public int getLinkCount(){
		return curState.linksOfAssociation(mAssociation).size();
	}
	
	public ArrayList<String> getAssociatedClassAndSubclassName(int ind){
		ArrayList<String> ret = new ArrayList<String>();
		MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[ind];
		ret.add(assEnd.cls().name());
		for(int i = 0; i < assEnd.cls().allChildren().size(); i++){
			MClass chi = (MClass) assEnd.cls().allChildren().toArray()[i];
			ret.add(chi.name());
		}
		return ret;
	}
	
	/**
	 * Function replaced by getAssociationFormulaMinMax()
	 * 
	 */
	@Deprecated
	public int getAssociatedClassMin(int ind){
		MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[ind];
		if(assEnd.multiplicity().toString().charAt(0) == '*'){
			return 0;
		}else{
			if(assEnd.multiplicity().toString().length() == 1){
				return Integer.parseInt(assEnd.multiplicity().toString().substring(0));
			}else{
				return Integer.parseInt(assEnd.multiplicity().toString().substring(0, 1));
			}
		}
	}
	
	/**
	 * Returns the Formula of the multiplcities
	 * @author Juergen Widdermann
	 * @param expVar 
	 * @param varDecl 
	 */
	public Formula getAssociationFormulaMinMax(MAssociationEnd assEnd, Expression expVar, Decls varDecl){
		//collect all multiplicities
		String[] multiplicities = assEnd.multiplicity().toString().split(",");
		Formula result = null;
		for(String multiplicity:multiplicities) {
			Formula subformula = null;
			//Get parts of the multiplicity
			//multiplicityParts[0] is the minima
			//multiplicityParts[1] is the maxima, could be empty
			String[] multiplicityParts = multiplicity.split("\\.\\.");
			// minima exists
			if(multiplicityParts.length >= 1) {
				//Creating formular for Association min
				if(!multiplicityParts[0].equals("*")) {
					//* Multiplicity has no constraint
					if(multiplicityParts.length >= 2) {
						if(multiplicityParts[0].equals("1") && multiplicityParts[1].equals("*")){
							//Special handling for 1..* -> some elements
							subformula = expVar.some();
						}else if(multiplicityParts[0].equals("0") && multiplicityParts[1].equals("1")){
							//Special handling for 0..1 -> one Element; (including undefined value)
							subformula = expVar.one();
						}else {
							//Only add minima if its greater 0
							if(Integer.parseInt(multiplicityParts[0]) > 0) {
								subformula = expVar.count().gte(IntConstant.constant(Integer.parseInt(multiplicityParts[0])));
							}
							//Creating formular for Association max
							if(!multiplicityParts[1].equals("*")) {
								if(subformula != null) {
									subformula = subformula.and(expVar.count().lte(IntConstant.constant(Integer.parseInt(multiplicityParts[1]))));
								} else {
									subformula = expVar.count().lte(IntConstant.constant(Integer.parseInt(multiplicityParts[1])));
								}
							}
						}
					} else {
						// Only One Number, then Multiplicitynumber has to be Equal
						if(multiplicityParts[0].equals("1")) {
							subformula = expVar.one();
						} else {
							subformula = expVar.count().eq(IntConstant.constant(Integer.parseInt(multiplicityParts[0])));
						}
					}
				}
			}

			//Only Add subformula if it exists
			if(subformula != null) {
				if(result == null) {
					result = subformula;
				} else {
					result = result.or(subformula);
				}
			}
		}
		if(result != null) {
			result = result.forAll(varDecl);
		}
		return result;
	}
	
	/**
	 * Returns the Formula of the multiplcities
	 * @author Juergen Widdermann
	 * @param expVar 
	 * @param varDecl 
	 */
	public boolean getAssociationHasUnlimited(MAssociationEnd assEnd){
		//collect all multiplicities
		String[] multiplicities = assEnd.multiplicity().toString().split(",");
		for(String multiplicity:multiplicities) {
			//Get parts of the multiplicity
			//multiplicityParts[0] is the minima
			//multiplicityParts[1] is the maxima, could be empty
			String[] multiplicityParts = multiplicity.split("\\.\\.");
			// minima exists
			if(multiplicityParts.length >= 1) {
				//Creating formular for Association min
				if(!multiplicityParts[0].equals("*")) {
					//* Multiplicity has no constraint
					return true;
				}
			}
		}
		return false;
	}
	
	@Deprecated
	/**
	 * Function replaced by getAssociationFormulaMinMax()
	 */
	public int getAssociatedClassMax(int ind){
		MAssociationEnd assEnd = (MAssociationEnd) mAssociation.associationEnds().toArray()[ind];
		if(assEnd.multiplicity().toString().length() == 1){
			if(assEnd.multiplicity().toString().charAt(0) == '*'){
				return -1;
			}else{
				return Integer.parseInt(assEnd.multiplicity().toString().substring(0));
			}
		}else{
			if(assEnd.multiplicity().toString().charAt(3) == '*'){
				return -1;
			}else{
				return Integer.parseInt(assEnd.multiplicity().toString().substring(3));
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.kodkod.main.UMLAssociation#getAssociationBoundsAssl(kodkod.instance.Bounds, kodkod.instance.TupleFactory, org.tzi.use.kodkod.main.SetKodkodStruc, java.util.ArrayList)
	 */
	@Override
	public Bounds getAssociationBoundsAssl(Bounds bou, TupleFactory tFa,
			SetKodkodStruc setKodkodStruc, AsslTranslation asslTranslation) {
		
		ArrayList<Vector<MObject>> asslAssociationList = asslTranslation.getAssociationBoundUpper().get(getName());
		ArrayList<Vector<MObject>> asslAssociationListExact = asslTranslation.getAssociationBoundLower().get(getName());
		TupleSet tsAssociationLower = tFa.noneOf(assRelation.arity());
		TupleSet tsAssociationUpper = tFa.noneOf(assRelation.arity());
		
		// 
		
		// Exact Bound is in Lower
		if(asslAssociationListExact != null) {
			for(Vector<MObject> association : asslAssociationListExact) {
				Vector<String> ass = new Vector<String>();
				for(MObject asso : association){
					ass.add(asslTranslation.getKodKodObjectName(asso));
				}
				tsAssociationLower.add(tFa.tuple(ass));
			}
		}
		
		// Try Association are in Upper
		tsAssociationUpper.addAll(tsAssociationLower);
		if(asslAssociationList != null) {
			for(Vector<MObject> association : asslAssociationList) {
				Vector<String> ass = new Vector<String>();
				for(MObject asso : association){
					ass.add(asslTranslation.getKodKodObjectName(asso));
				}
				tsAssociationUpper.add(tFa.tuple(ass));
			}
		}
		
		// Add Undefined if necessairy
		List<MAssociationEnd> ends = mAssociation.associationEnds();
		if(ends != null && ends.size() == 2) {
			MAssociationEnd end1 = ends.get(0);
			MAssociationEnd end2 = ends.get(1);
			Relation tmpRel = setKodkodStruc.getClassRelation(end1.cls().name());
			TupleSet undefined = tFa.noneOf(1);
			undefined.add(tFa.tuple("Undefined"));
			if(end1.multiplicity().toString().equals("0..1") || end2.multiplicity().toString().equals("0..1") && tmpRel != null) {
				TupleSet tsObjects = bou.upperBound(tmpRel);
				tsAssociationUpper.addAll(tsObjects.product(undefined));
			}
			tmpRel = setKodkodStruc.getClassRelation(end2.cls().name());
			if(end1.multiplicity().toString().equals("0..1") || end2.multiplicity().toString().equals("0..1") && tmpRel != null) {
				TupleSet tsObjects = bou.upperBound(tmpRel);
				tsAssociationUpper.addAll(undefined.product(tsObjects));
			}
		}
		
		if(tsAssociationLower.size() > 0) {
			bou.bound(assRelation, tsAssociationLower, tsAssociationUpper);
		} else {
			bou.bound(assRelation, tsAssociationUpper);
		}
		
		return bou;
	}

	/* (non-Javadoc)
	 * @see org.tzi.use.kodkod.main.UMLAssociation#usingAsslBounds(boolean)
	 */
	@Override
	public void usingAsslBounds(boolean isUsingAsslBound) {
		// TODO Auto-generated method stub
		this.isUsingAsslBound = isUsingAsslBound; 
	}
	
}