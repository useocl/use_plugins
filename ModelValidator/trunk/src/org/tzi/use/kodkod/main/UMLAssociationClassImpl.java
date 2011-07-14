package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;

/**
 * creates the relations for an associationclass, creates the formula, creates
 * the bounds attribute relations, formulas and bounds for attributes of the
 * associated associationclass are created too max and min number of objects
 * (links) of the associationclass are represented by variable lowerBound and
 * upperBound
 * 
 * @author Torsten Humann
 */
public class UMLAssociationClassImpl implements UMLAssociationClass {

	private MSystemState curState;
	private final MModel curModel;
	private final MAssociationClass mAssClass;
	private int lowerBound;
	private int upperBound;
	private ArrayList<UMLAttributeNames> boundNames;
	private Relation assClaRelation;
	private Relation claRelation;
	private Relation parRelation;
	private HashMap<String, Relation> attRelations = new HashMap<String, Relation>();
	private HashMap<String, Relation> priRelations = new HashMap<String, Relation>();
	
	private boolean usingAsslBounds = false;

	public UMLAssociationClassImpl(MSystem curSys, MAssociationClass mAssCla) {
		curState = curSys.state();
		curModel = curSys.model();
		mAssClass = mAssCla;
		lowerBound = curSys.state().objectsOfClass(mAssCla).size();
		upperBound = curSys.state().objectsOfClass(mAssCla).size();
		boundNames = new ArrayList<UMLAttributeNames>();
		assClaRelation = Relation.nary(mAssClass.name(), mAssClass
				.associationEnds().toArray().length + 1);
		claRelation = Relation.unary("-" + mAssClass.name());
		parRelation = Relation.unary("--" + mAssClass.name());
		for (int i = 0; i < mAssClass.allAttributes().size(); i++) {
			MAttribute att = (MAttribute) mAssClass.allAttributes().get(i);
			attRelations.put(mAssClass.name() + "_-" + att.name(),
					Relation.nary(mAssClass.name() + "_-" + att.name(), 2));
			if (att.type().isReal() || att.type().isString()) {
				priRelations.put(
						mAssClass.name() + "_-" + att.name() + "-"
								+ att.type().toString(),
						Relation.unary(mAssClass.name() + "_-" + att.name()
								+ "-" + att.type().toString()));
			}
			boundNames.add(new UMLAttributeNames(att.name(), att.type()));
		}
	}

	public void setLowerBound(int lb) {
		lowerBound = lb;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setUpperBound(int ub) {
		upperBound = ub;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public MClass getMClass() {
		return mAssClass;
	}

	public MAssociation getMAssociation() {
		return mAssClass;
	}

	public MAssociationClass getMAssociationClass() {
		return mAssClass;
	}

	public String getName() {
		return mAssClass.name();
	}

	public String getTableName() {
		if (mAssClass.isAbstract()) {
			return mAssClass.name() + " (abstract)";
		} else {
			return mAssClass.name();
		}
	}

	public Relation getClaRelation() {
		return claRelation;
	}

	public Relation getAssRelation() {
		return assClaRelation;
	}

	public HashMap<String, Relation> getAttRelations() {
		return attRelations;
	}

	// builds the Kodkod formula for the associated associationclass
	public Formula getFormula(SetKodkodStruc skks) {

		Formula form = null;

		int ind = 0;

		Expression exp = null;
		Expression expHead = null;
		Expression expBody = null;
		Expression expVar = null;
		Relation tmpAttRelation;
		Relation tmpPriRelation;
		Decls varDecl = null;
		Formula tmpForm = null;
		Variable v[] = new Variable[assClaRelation.arity() - 2];
		Variable s[] = new Variable[assClaRelation.arity()];
		Variable p = Variable.unary("p");

		// children are in the associationclass relation
		if (mAssClass.children().size() > 0) {
			exp = parRelation;
			for (int i = 0; i < mAssClass.children().size(); i++) {
				MClass child = (MClass) mAssClass.children().toArray()[i];
				exp = exp.union(skks.getClassRelation(child.name()));
			}
			form = exp.eq(claRelation);
		}

		for (int i = 0; i < mAssClass.allParents().size(); i++) {
			MClass parent = (MClass) mAssClass.allParents().toArray()[i];
			if (form == null) {
				form = claRelation.in(skks.getClassRelation(parent.name()));
			}
			form = form
					.and(claRelation.in(skks.getClassRelation(parent.name())));
		}

		// formula part for attributes of associationclass
		for (int i = 0; i < mAssClass.allAttributes().size(); i++) {
			MAttribute att = (MAttribute) mAssClass.allAttributes().get(i);
			tmpAttRelation = attRelations.get(mAssClass.name() + "_-"
					+ att.name());
			if (att.type().isInteger()) {
				tmpPriRelation = skks.getIDRelation();
			} else if (att.type().isBoolean()) {
				tmpPriRelation = skks.getBoolRelation();
			} else if (att.type().isEnum()) {
				tmpPriRelation = skks.getENumRelation(att.type().toString());
			} else if (att.type().isObjectType()) {
				tmpPriRelation = skks.getClassRelation(att.type().toString());
			} else {
				tmpPriRelation = priRelations.get(mAssClass.name() + "_-"
						+ att.name() + "-" + att.type().toString());
			}
			if (form == null) {
				form = tmpAttRelation.join(Expression.UNIV).in(claRelation);
			} else {
				form = form.and(tmpAttRelation.join(Expression.UNIV).in(
						claRelation));
			}
			if(this.usingAsslBounds) {
				form = form.and(Expression.UNIV.join(tmpAttRelation).in(
						tmpPriRelation.union(skks.getUndefinedRelation())));
			} else {
				form = form.and(Expression.UNIV.join(tmpAttRelation).in(
					tmpPriRelation));
			}
			if (att.type().isInteger()) {
				form = form.and(p.join(tmpAttRelation).count()
						.gte(IntConstant.constant(getMinIntValue(att.name())))
						.forAll(p.oneOf(claRelation)));
				form = form.and(p.join(tmpAttRelation).count()
						.lte(IntConstant.constant(getMaxIntValue(att.name())))
						.forAll(p.oneOf(claRelation)));
			} else {
				form = form.and(p.join(tmpAttRelation).one()
						.forAll(p.oneOf(claRelation)));
			}
		}

		// formula part for association part of associationclass
		expHead = assClaRelation;
		for (int i = 0; i < assClaRelation.arity() - 1; i++) {
			expHead = expHead.join(Expression.UNIV);
			expBody = Expression.UNIV.join(assClaRelation);
			for (int j = 0; j < i; j++) {
				expBody = Expression.UNIV.join(expBody);
			}
			for (int j = i; j < assClaRelation.arity() - 2; j++) {
				expBody = expBody.join(Expression.UNIV);
			}
			MAssociationEnd assEnd = (MAssociationEnd) mAssClass
					.associationEnds().toArray()[i];
			if (i == 0) {
				tmpForm = expBody
						.in(skks.getClassRelation(assEnd.cls().name()));
			} else {
				tmpForm = tmpForm.and(expBody.in(skks.getClassRelation(assEnd
						.cls().name())));
			}
			if (i == assClaRelation.arity() - 2) {
				tmpForm = expHead.in(claRelation).and(tmpForm);
			}
		}
		if (form == null) {
			form = tmpForm;
		} else {
			form = form.and(tmpForm);
		}
		for (int i = 0; i < mAssClass.associationEnds().toArray().length + 1; i++) {
			if (i == 0) {
				// part formula that for every atom of claRelation exists one
				// tuple of assClaRelation
				s[0] = Variable.unary("s1");
				form = form.and(s[0].join(assClaRelation).one()
						.forAll(s[0].oneOf(claRelation)));
				// part formula that relation part of tuple is unique
				for (int j = 1; j < assClaRelation.arity(); j++) {
					s[j] = Variable.unary("s" + (j + 1));
					if (j == 1) {
						MAssociationEnd assEnd = (MAssociationEnd) mAssClass
								.associationEnds().toArray()[0];
						varDecl = s[1].oneOf(skks.getClassRelation(assEnd.cls()
								.name()));
					} else {
						MAssociationEnd assEnd = (MAssociationEnd) mAssClass
								.associationEnds().toArray()[j - 1];
						varDecl = varDecl.and(s[j].oneOf(skks
								.getClassRelation(assEnd.cls().name())));
					}
				}
				for (int j = assClaRelation.arity() - 1; j > 0; j--) {
					if (j == assClaRelation.arity() - 1) {
						expVar = assClaRelation.join(s[j]);
					} else {
						expVar = expVar.join(s[j]);
					}
				}
				form = form.and(expVar.lone().forAll(varDecl));
			} else {
				if (getAssociatedClassMin(i - 1) != 0
						|| getAssociatedClassMax(i - 1) != -1) {
					ind = 0;
					for (int j = 0; j < mAssClass.associationEnds().toArray().length; j++) {
						if (j != (i - 1)) {
							v[ind] = Variable.unary("v" + (j + 1));
							MAssociationEnd assEnd = (MAssociationEnd) mAssClass
									.associationEnds().toArray()[j];
							if (ind == 0) {
								varDecl = v[ind].oneOf(skks
										.getClassRelation(assEnd.cls().name()));
							} else {
								varDecl = varDecl
										.and(v[ind].oneOf(skks
												.getClassRelation(assEnd.cls()
														.name())));
							}
							ind++;
						}
					}
					expVar = Expression.UNIV.join(assClaRelation);
					for (int j = 0; j < (i - 1); j++) {
						expVar = v[j].join(expVar);
					}
					for (int j = mAssClass.associationEnds().toArray().length; j > i; j--) {
						expVar = expVar.join(v[j - 2]);
					}
					if (getAssociatedClassMin(i - 1) != 0) {
						form = form
								.and(expVar
										.count()
										.gte(IntConstant
												.constant(getAssociatedClassMin(i - 1)))
										.forAll(varDecl));
					}
					if (getAssociatedClassMax(i - 1) != -1) {
						form = form
								.and(expVar
										.count()
										.lte(IntConstant
												.constant(getAssociatedClassMax(i - 1)))
										.forAll(varDecl));
					}
				}
			}
		}
		return form;
	}

	// sets the bounds of relations of the class part of the associationclass
	// (with all attributes)
	public Bounds getClassBounds(Bounds bou, TupleFactory tFa,
			SetKodkodStruc skks) {

		ArrayList<String> attributeNames = new ArrayList<String>();
		ArrayList<String> attributeObjects = new ArrayList<String>();
		ArrayList<String[]> attributesLower = new ArrayList<String[]>();
		ArrayList<ArrayList<String>> attributesUpper = new ArrayList<ArrayList<String>>();

		int val = 0;

		TupleSet tsClassLower = tFa.noneOf(1);
		TupleSet tsClassUpper = tFa.noneOf(1);
		TupleSet tsParClaLower = tFa.noneOf(1);
		TupleSet tsParClaUpper = tFa.noneOf(1);
		TupleSet tsOnlClaLower = tFa.noneOf(1);
		TupleSet tsOnlClaUpper = tFa.noneOf(1);
		TupleSet tsAttributeLower = tFa.noneOf(2);
		TupleSet tsAttributeUpper = tFa.noneOf(2);
		TupleSet tsPrimitive = tFa.noneOf(1);

		Relation tmpRelation;

		if (bou.lowerBound(claRelation) != null) {
			tsClassLower.addAll(bou.lowerBound(claRelation));
		}
		if (bou.upperBound(claRelation) != null) {
			tsClassUpper.addAll(bou.upperBound(claRelation));
		}
		for (int i = 0; i < lowerBound; i++) {
			if (i < curState.objectsOfClass(mAssClass).size()) {
				MObject obj = (MObject) curState.objectsOfClass(mAssClass)
						.toArray()[i];
				tsClassLower.add(tFa.tuple(getBoundName() + (i + 1) + "_"
						+ obj.name()));
				tsOnlClaLower.add(tFa.tuple(getBoundName() + (i + 1) + "_"
						+ obj.name()));
			} else {
				tsClassLower.add(tFa.tuple(getBoundName() + (i + 1)));
				tsOnlClaLower.add(tFa.tuple(getBoundName() + (i + 1)));
			}
		}
		tsClassUpper.addAll(tsClassLower);
		tsOnlClaUpper.addAll(tsOnlClaLower);
		for (int i = lowerBound; i < upperBound; i++) {
			tsClassUpper.add(tFa.tuple(getBoundName() + (i + 1)));
			tsOnlClaUpper.add(tFa.tuple(getBoundName() + (i + 1)));
		}
		if (tsClassLower.size() == 0) {
			bou.bound(claRelation, tsClassUpper);
		} else {
			bou.bound(claRelation, tsClassLower, tsClassUpper);
		}
		if (mAssClass.children().size() > 0) {
			if (tsOnlClaLower.size() == 0) {
				bou.bound(parRelation, tsOnlClaUpper);
			} else {
				bou.bound(parRelation, tsOnlClaLower, tsOnlClaUpper);
			}
		}

		// sets bounds in bound of parent classes too
		for (int i = 0; i < mAssClass.allParents().size(); i++) {
			MClass parent = (MClass) mAssClass.allParents().toArray()[i];
			tsParClaLower.clear();
			tsParClaUpper.clear();
			tmpRelation = skks.getClassRelation(parent.name());
			if (bou.lowerBound(tmpRelation) != null) {
				tsParClaLower.addAll(bou.lowerBound(tmpRelation));
			}
			if (bou.upperBound(tmpRelation) != null) {
				tsParClaUpper.addAll(bou.upperBound(tmpRelation));
			}
			tsParClaLower.addAll(tsClassLower);
			tsParClaUpper.addAll(tsClassUpper);
			if (tsParClaLower.size() == 0) {
				bou.bound(tmpRelation, tsParClaUpper);
			} else {
				bou.bound(tmpRelation, tsParClaLower, tsParClaUpper);
			}
		}

		// sets the bounds for attributes
		for (int i = 0; i < mAssClass.allAttributes().size(); i++) {
			tsAttributeLower.clear();
			tsAttributeUpper.clear();
			tsPrimitive.clear();
			MAttribute att = (MAttribute) mAssClass.allAttributes().get(i);
			// attribute type is enumeration
			if (att.type().isEnum()) {
				attributeNames.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for (int j = 0; j < upperBound; j++) {
					if (j < curState.objectsOfClass(mAssClass).size()) {
						MObject obj = (MObject) curState.objectsOfClass(
								mAssClass).toArray()[j];
						EnumType enType = (EnumType) att.type();
						attributeNames.add(getBoundName() + (j + 1) + "_"
								+ obj.name());
						if (!obj.state(curState).attributeValue(att).toString()
								.equals("Undefined")) {
							tsAttributeLower.add(tFa.tuple(getBoundName()
									+ (j + 1) + "_" + obj.name(),
									enType.name()
											+ "_"
											+ obj.state(curState)
													.attributeValue(att)
													.toString()));
						}
					} else {
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				attributesUpper.add(attributeNames);
				EnumType enType = (EnumType) att.type();
				Iterator<?> it = enType.literals();
				while (it.hasNext()) {
					String lit = (String) it.next();
					attributeObjects.add(enType.name() + "_#" + lit);
				}
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for (int j = 0; j < attributesUpper.size(); j++) {
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
				// attribute type is integer (number of pairs represent the
				// value of attribute)
			} else if (att.type().isInteger()) {
				for (int j = 0; j < upperBound; j++) {
					if (j < curState.objectsOfClass(mAssClass).size()) {
						MObject obj = (MObject) curState.objectsOfClass(
								mAssClass).toArray()[j];
						if (obj.state(curState).attributeValue(att).toString()
								.equals("Undefined")) {
							val = 1;
						} else {
							val = new Integer(obj.state(curState)
									.attributeValue(att).toString()).intValue();
						}
						if (val > skks.getMaxIntValue()) {
							val = skks.getMaxIntValue();
						}
						for (int k = 0; k < val; k++) {
							tsAttributeLower.add(tFa.tuple(getBoundName()
									+ (j + 1) + "_" + obj.name(), "@id" + k));
							tsAttributeUpper.add(tFa.tuple(getBoundName()
									+ (j + 1) + "_" + obj.name(), "@id" + k));
						}
					} else {
						for (int k = 0; k < getMaxIntValue(att.name()); k++) {
							tsAttributeUpper.add(tFa.tuple(getBoundName()
									+ (j + 1), "@id" + k));
						}
					}
				}
				// attribute type is boolean
			} else if (att.type().isBoolean()) {
				attributeNames.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for (int j = 0; j < upperBound; j++) {
					if (j < curState.objectsOfClass(mAssClass).size()) {
						MObject obj = (MObject) curState.objectsOfClass(
								mAssClass).toArray()[j];
						if (!obj.state(curState).attributeValue(att).toString()
								.equals("@Undefined")) {
							tsAttributeLower.add(tFa.tuple(getBoundName()
									+ (j + 1) + "_" + obj.name(), "@"
									+ obj.state(curState).attributeValue(att)
											.toString()));
							tsAttributeUpper.add(tFa.tuple(getBoundName()
									+ (j + 1) + "_" + obj.name(), "@"
									+ obj.state(curState).attributeValue(att)
											.toString()));
						} else {
							attributeNames.add(getBoundName() + (j + 1) + "_"
									+ obj.name());
						}
					} else {
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				attributeObjects.add("@true");
				attributeObjects.add("@false");
				attributesUpper.add(attributeNames);
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for (int j = 0; j < attributesUpper.size(); j++) {
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
				// attribute type is String or Real
			} else if (att.type().isString() || att.type().isReal()) {
				attributeNames.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for (int j = 0; j < upperBound; j++) {
					if (j < curState.objectsOfClass(mAssClass).size()) {
						MObject obj = (MObject) curState.objectsOfClass(
								mAssClass).toArray()[j];
						attributeNames.add(getBoundName() + (j + 1) + "_"
								+ obj.name());
						if (!obj.state(curState).attributeValue(att).toString()
								.equals("Undefined")) {
							tsPrimitive.add(tFa.tuple(getAttributeBoundName(i)
									+ (j + 1)
									+ "_"
									+ obj.state(curState).attributeValue(att)
											.toString()));
							tsAttributeLower.add(tFa.tuple(getBoundName()
									+ (j + 1) + "_" + obj.name(),
									getAttributeBoundName(i)
											+ (j + 1)
											+ "_"
											+ obj.state(curState)
													.attributeValue(att)
													.toString()));
							attributeObjects.add(getAttributeBoundName(i)
									+ (j + 1)
									+ "_"
									+ obj.state(curState).attributeValue(att)
											.toString());
						}
					} else {
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				for (int j = 0; j < getBoundNames(att.name()).size(); j++) {
					if (att.type().isString()) {
						tsPrimitive.add(tFa.tuple(getAttributeBoundName(i)
								+ (j + 1 + curState.objectsOfClass(mAssClass)
										.size()) + "_'"
								+ getBoundNames(att.name()).get(j) + "'"));
						attributeObjects.add(getAttributeBoundName(i)
								+ (j + 1 + curState.objectsOfClass(mAssClass)
										.size()) + "_'"
								+ getBoundNames(att.name()).get(j) + "'");
					} else {
						tsPrimitive.add(tFa.tuple(getAttributeBoundName(i)
								+ (j + 1 + curState.objectsOfClass(mAssClass)
										.size()) + "_"
								+ getBoundNames(att.name()).get(j)));
						attributeObjects.add(getAttributeBoundName(i)
								+ (j + 1 + curState.objectsOfClass(mAssClass)
										.size()) + "_"
								+ getBoundNames(att.name()).get(j));
					}
				}
				attributesUpper.add(attributeNames);
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for (int j = 0; j < attributesUpper.size(); j++) {
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
				// attribute type is class or associationclass
			} else if (att.type().isObjectType()) {
				attributeNames.clear();
				attributesLower.clear();
				attributeObjects.clear();
				attributesUpper.clear();
				for (int j = 0; j < upperBound; j++) {
					if (j < curState.objectsOfClass(mAssClass).size()) {
						MObject obj = (MObject) curState.objectsOfClass(
								mAssClass).toArray()[j];
						attributeNames.add(getBoundName() + (j + 1) + "_"
								+ obj.name());
						attributesLower.add(new String[] {
								getBoundName() + (j + 1) + "_" + obj.name(),
								obj.state(curState).attributeValue(att)
										.toString() });
					} else {
						attributeNames.add(getBoundName() + (j + 1));
					}
				}
				UMLClass tmpUMLCla = skks.getUMLClass(att.type().toString());
				for (int j = 0; j < tmpUMLCla.getUpperBound(); j++) {
					if (j < tmpUMLCla.getObjectCount()) {
						MObject obj = (MObject) curState.objectsOfClass(
								tmpUMLCla.getMClass()).toArray()[j];
						for (int k = 0; k < attributesLower.size(); k++) {
							if (attributesLower.get(k)[1].substring(1).equals(
									obj.name())) {
								tsAttributeLower.add(tFa.tuple(
										attributesLower.get(k)[0],
										tmpUMLCla.getBoundName() + (j + 1)
												+ "_" + obj.name()));
							}
						}
						attributeObjects.add(tmpUMLCla.getBoundName() + (j + 1)
								+ "_" + obj.name());
					} else {
						attributeObjects
								.add(tmpUMLCla.getBoundName() + (j + 1));
					}
				}
				attributesUpper.add(attributeNames);
				attributesUpper.add(attributeObjects);
				attributesUpper = SetKodkodStruc.cartProduct(attributesUpper);
				for (int j = 0; j < attributesUpper.size(); j++) {
					tsAttributeUpper.add(tFa.tuple(attributesUpper.get(j)));
				}
			}
			if (lowerBound == 0) {
				bou.bound(
						attRelations.get(mAssClass.name() + "_-" + att.name()),
						tsAttributeUpper);
			} else {
				bou.bound(
						attRelations.get(mAssClass.name() + "_-" + att.name()),
						tsAttributeLower, tsAttributeUpper);
			}
			if (att.type().isString() || att.type().isReal()) {
				bou.bound(
						priRelations.get(mAssClass.name() + "_-" + att.name()
								+ "-" + att.type().toString()), tsPrimitive);
			}
		}
		return bou;
	}

	// sets the bounds of relations of the association part of the
	// associationclass
	public Bounds getAssociationBounds(Bounds bou, TupleFactory tFa,
			SetKodkodStruc skks) {

		ArrayList<String> associationClassAtomsLower = new ArrayList<String>();

		TupleSet tsAssociationClassLower = tFa.noneOf(assClaRelation.arity());
		TupleSet tsAssociationClassUpper = tFa.noneOf(assClaRelation.arity());
		TupleSet tmpTS = null;

		Relation tmpRelation;

		// lower Bonds
		for (int i = 0; i < curState
				.linksOfAssociation(curModel.getAssociation(mAssClass.name()))
				.links().size(); i++) {
			MLink curLink = (MLink) curState
					.linksOfAssociation(
							curModel.getAssociation(mAssClass.name())).links()
					.toArray()[i];
			associationClassAtomsLower.clear();
			MObject obj1 = (MObject) curState.objectsOfClass(mAssClass)
					.toArray()[i];
			associationClassAtomsLower.add(getBoundName() + (i + 1) + "_"
					+ obj1.name());
			for (int j = 0; j < mAssClass.associationEnds().toArray().length; j++) {
				MAssociationEnd assEnd = (MAssociationEnd) mAssClass
						.associationEnds().toArray()[j];
				for (int k = 0; k < getAssociatedClassAndSubclassName(j).size(); k++) {
					UMLClass tmpUMLCla = skks
							.getUMLClass(getAssociatedClassAndSubclassName(j)
									.get(k));
					for (int l = 0; l < tmpUMLCla.getObjectCount(); l++) {
						MObject obj2 = (MObject) curState.objectsOfClass(
								tmpUMLCla.getMClass()).toArray()[l];
						if (obj2.name().equals(
								curLink.linkEnd(assEnd).object().name())) {
							associationClassAtomsLower.add(tmpUMLCla
									.getBoundName()
									+ (l + 1)
									+ "_"
									+ curLink.linkEnd(assEnd).object().name());
							k = getAssociatedClassAndSubclassName(j).size();
						}
					}
				}
			}
			tsAssociationClassLower.add(tFa.tuple(associationClassAtomsLower));
		}

		// upper Bounds
		for (int i = 0; i < mAssClass.associationEnds().toArray().length; i++) {
			MAssociationEnd assEnd = (MAssociationEnd) mAssClass
					.associationEnds().toArray()[i];
			tmpRelation = skks.getClassRelation(assEnd.cls().name());
			if (i == 0) {
				tmpTS = bou.upperBound(claRelation).product(
						bou.upperBound(tmpRelation));
			} else {
				tmpTS = tmpTS.product(bou.upperBound(tmpRelation));
			}
		}
		tsAssociationClassUpper = tmpTS;

		bou.bound(assClaRelation, tsAssociationClassLower,
				tsAssociationClassUpper);

		return bou;
	}

	// creating a unique name for the atom
	public String getBoundName() {
		return mAssClass.name().substring(0, 1).toLowerCase()
				.concat(mAssClass.name().substring(1))
				+ "@";
	}

	public int getObjectCount() {
		return curState.objectsOfClass(mAssClass).size();
	}

	public int getLinkCount() {
		return curState.linksOfAssociation(mAssClass).size();
	}

	// creating a unique name for a value of an attribute
	public String getAttributeBoundName(int ind) {
		MAttribute att = (MAttribute) mAssClass.allAttributes().get(ind);
		return (mAssClass.name() + "_" + att.name()).substring(0, 1)
				.toLowerCase()
				+ (mAssClass.name() + "_" + att.name()).substring(1);
	}

	// sets the possible values of an attribute
	public void setBoundNames(String attName, ArrayList<String> bn) {
		for (int i = 0; i < boundNames.size(); i++) {
			if (boundNames.get(i).getAttributeName().equals(attName)) {
				boundNames.get(i).setBoundNames(bn);
			}
		}
	}

	// get the possible values of an attribute
	public ArrayList<String> getBoundNames(String attName) {
		for (int i = 0; i < boundNames.size(); i++) {
			if (boundNames.get(i).getAttributeName().equals(attName)) {
				return boundNames.get(i).getBoundNames();
			}
		}
		return null;
	}

	public void setUMLAttributeNames(ArrayList<UMLAttributeNames> an) {
		boundNames.clear();
		boundNames = an;
	}

	public ArrayList<UMLAttributeNames> getUMLAttributeNames() {
		return boundNames;
	}

	public boolean isAttributeEditable(String attName) {
		for (int i = 0; i < boundNames.size(); i++) {
			if (boundNames.get(i).getAttributeName().equals(attName)) {
				return boundNames.get(i).isEditable();
			}
		}
		return false;
	}

	public int getMaxIntValue(String attName) {
		for (int i = 0; i < boundNames.size(); i++) {
			if (boundNames.get(i).getAttributeName().equals(attName)) {
				return boundNames.get(i).getMaxValue();
			}
		}
		return 0;
	}

	public int getMinIntValue(String attName) {
		for (int i = 0; i < boundNames.size(); i++) {
			if (boundNames.get(i).getAttributeName().equals(attName)) {
				return boundNames.get(i).getMinValue();
			}
		}
		return 0;
	}

	public ArrayList<String> getAssociatedClassAndSubclassName(int ind) {
		ArrayList<String> ret = new ArrayList<String>();
		MAssociationEnd assEnd = (MAssociationEnd) mAssClass.associationEnds()
				.toArray()[ind];
		ret.add(assEnd.cls().name());
		for (int i = 0; i < assEnd.cls().allChildren().size(); i++) {
			MClass chi = (MClass) assEnd.cls().allChildren().toArray()[i];
			ret.add(chi.name());
		}
		return ret;
	}

	public int getAssociatedClassMin(int ind) {
		MAssociationEnd assEnd = (MAssociationEnd) mAssClass.associationEnds()
				.toArray()[ind];
		if (assEnd.multiplicity().toString().charAt(0) == '*') {
			return 0;
		} else {
			if (assEnd.multiplicity().toString().length() == 1) {
				return Integer.parseInt(assEnd.multiplicity().toString()
						.substring(0));
			} else {
				return Integer.parseInt(assEnd.multiplicity().toString()
						.substring(0, 1));
			}
		}
	}

	public int getAssociatedClassMax(int ind) {
		MAssociationEnd assEnd = (MAssociationEnd) mAssClass.associationEnds()
				.toArray()[ind];
		if (assEnd.multiplicity().toString().length() == 1) {
			if (assEnd.multiplicity().toString().charAt(0) == '*') {
				return -1;
			} else {
				return Integer.parseInt(assEnd.multiplicity().toString()
						.substring(0));
			}
		} else {
			if (assEnd.multiplicity().toString().charAt(3) == '*') {
				return -1;
			} else {
				return Integer.parseInt(assEnd.multiplicity().toString()
						.substring(3));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tzi.use.kodkod.main.UMLClass#getPossibleObjects()
	 */
	@Override
	public ArrayList<String> getPossibleObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tzi.use.kodkod.main.UMLClass#getClassBounds(kodkod.instance.Bounds,
	 * kodkod.instance.TupleFactory, org.tzi.use.kodkod.main.SetKodkodStruc,
	 * java.util.ArrayList)
	 */
	@Override
	public Bounds getClassBounds(Bounds bou, TupleFactory tFa,
			SetKodkodStruc skks, ArrayList<String> possibleAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	// sets the bounds of relations of the class (with all attributes)
	@SuppressWarnings("unchecked")
	public Bounds getClassBoundsAssl(Bounds bou, TupleFactory tFa,
			SetKodkodStruc skks, AsslTranslation asslTranslation) {
		
		ArrayList<String> asslObjects = asslTranslation.getAssociationClassUpperObjectBound().get(getName());
		ArrayList<MObject> asslObjectsExact = asslTranslation.getAssociationClassLowerObjectBound().get(getName());
		HashMap<MObject, HashMap<String, ArrayList<String>>> asslObjectAttributeMapping = asslTranslation.getObjectAttributeUpperMapping().get(getName());
		HashMap<MObject, HashMap<String, String>> asslObjectAttributeLowerMapping = asslTranslation.getObjectAttributeLowerMapping().get(getName());

		ArrayList<String> attributeNames = new ArrayList<String>();
		ArrayList<String> attributeObjects = new ArrayList<String>();
		ArrayList<ArrayList<String>> attributesUpper = new ArrayList<ArrayList<String>>();

		int val = 0;

		TupleSet tsClassLower = tFa.noneOf(1);
		TupleSet tsClassUpper = tFa.noneOf(1);
		TupleSet tsParClaExact = tFa.noneOf(1);
		TupleSet tsOnlClaLower = tFa.noneOf(1);
		TupleSet tsOnlClaUpper = tFa.noneOf(1);
		TupleSet tsAttributeLower = tFa.noneOf(2);
		TupleSet tsAttributeUpper = tFa.noneOf(2);
		TupleSet tsPrimitive = tFa.noneOf(1);

		Relation tmpRelation;

		if (bou.lowerBound(claRelation) != null) {
			tsClassLower.addAll(bou.lowerBound(claRelation));
		}
		
		if (asslObjectsExact != null) {
			for (MObject asslObject : asslObjectsExact) {
				String asslObjectName = asslTranslation.getKodKodObjectName(asslObject);
				tsClassLower.add(tFa.tuple(asslObjectName));
				tsOnlClaLower.add(tFa.tuple(asslObjectName));
			}
		}
		// tsClassExact.addAll(tFa.setOf(asslObjects));
		// tsOnlClaExact.addAll(tFa.setOf(asslObjects));

		tsClassUpper.addAll(tsClassLower);
		tsOnlClaUpper.addAll(tsOnlClaLower);
		
		if (asslObjects != null) {
			int i = 1;
			int offset = asslTranslation.getClassObjectCount(getName());
			for (String asslObject : asslObjects) {
				tsClassUpper.add(tFa.tuple(asslObject+"@"+(offset+i)));
				tsOnlClaUpper.add(tFa.tuple(asslObject+"@"+(offset+i)));
				attributeObjects.add(asslObject+"@"+(offset+i));
				i++;
			}
		}

		bou.bound(claRelation, tsClassLower, tsClassUpper);

		if (mAssClass.children().size() > 0) {
			bou.bound(parRelation, tsOnlClaLower, tsOnlClaUpper);
		}

		// sets bounds in bound of parent classes too
		for (int i = 0; i < mAssClass.allParents().size(); i++) {
			MClass parent = (MClass) mAssClass.allParents().toArray()[i];
			tsParClaExact.clear();
			tmpRelation = skks.getClassRelation(parent.name());
			if (bou.upperBound(tmpRelation) != null) {
				tsParClaExact.addAll(bou.upperBound(tmpRelation));
			}
			tsParClaExact.addAll(tsClassUpper);
			bou.boundExactly(tmpRelation, tsParClaExact);
		}
		
		ArrayList<Object> allObjects = new ArrayList<Object>();
		if(attributeObjects != null) {
			allObjects.addAll((ArrayList<Object>) attributeObjects.clone());
		}
		if(asslObjectsExact != null){
			allObjects.addAll((ArrayList<Object>) asslObjectsExact.clone());
		}
		
		//sets the bounds for attributes
		for(int i = 0; i < mAssClass.allAttributes().size(); i++){
			tsAttributeLower.clear();
			tsAttributeUpper.clear();
			tsPrimitive.clear();
			MAttribute att = (MAttribute) mAssClass.allAttributes().get(i);
			
			// When no Objects, dont try to fill them with attributes
			if(allObjects != null) {
				//attribute type is enumeration
				if(att.type().isEnum()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();
					EnumType enType = (EnumType) att.type();
					
					int y = 1;
					
					// add assl Attributes
					for(Object asslObject : allObjects) {
						String objectName = null;
						if(asslObject instanceof String) {
							objectName = (String)asslObject;
						}
						if(asslObject instanceof MObject) {
							objectName = asslTranslation.getKodKodObjectName((MObject)asslObject);
						}
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = enType.name() + "_#" + asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(objectName, value));
							tsAttributeUpper.add(tFa.tuple(objectName, value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								String value = enType.name() + "_#" + objectAttributeValue;
								tsPrimitive.add(tFa.tuple(value));
								tsAttributeUpper.add(tFa.tuple(objectName, value));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(objectName, objName));
						}
						y++;
					}
				//attribute type is integer (number of pairs represent the value of attribute)
				}else if(att.type().isInteger()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();
					
					int y = 1;
					
					for(Object asslObject : allObjects) {
						String objectName = null;
						if(asslObject instanceof String) {
							objectName = (String)asslObject;
						}
						if(asslObject instanceof MObject) {
							objectName = asslTranslation.getKodKodObjectName((MObject)asslObject);
						}
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							val = (new Integer(asslObjectAttributeLowerMapping.get(asslObject).get(att.name()))).intValue();
							for(int k = 0; k < val; k++){
								tsPrimitive.add(tFa.tuple("@id"+k));
								tsAttributeLower.add(tFa.tuple(objectName, "@id"+k));
								tsAttributeUpper.add(tFa.tuple(objectName, "@id"+k));
							}
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								val = (new Integer(objectAttributeValue)).intValue();
								for(int k = 0; k < val; k++){
									tsPrimitive.add(tFa.tuple("@id"+k));
									tsAttributeUpper.add(tFa.tuple(objectName, "@id"+k));
								}
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(objectName, objName));
						}
						y++;
					}
					tsAttributeUpper.addAll(tsAttributeLower);
				//attribute type is boolean
				} else if(att.type().isBoolean()){
					attributeNames.clear();
					attributeObjects.clear();
					
					int y = 1;
					
					// add assl Attributes
					for(Object asslObject : allObjects) {
						String objectName = null;
						if(asslObject instanceof String) {
							objectName = (String)asslObject;
						}
						if(asslObject instanceof MObject) {
							objectName = asslTranslation.getKodKodObjectName((MObject)asslObject);
						}
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = "@" + asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(objectName, value));
							tsAttributeUpper.add(tFa.tuple(objectName, value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								String value = "@" + objectAttributeValue;
								tsPrimitive.add(tFa.tuple(value));
								tsAttributeUpper.add(tFa.tuple(objectName, value));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(objectName, objName));
						}
						y++;
					}
				//attribute type is String or Real
				} else  if(att.type().isString() || att.type().isReal()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();
					
					int y = 1;
					
					// add assl Attributes
					for(Object asslObject : allObjects) {
						String objectName = null;
						if(asslObject instanceof String) {
							objectName = (String)asslObject;
						}
						if(asslObject instanceof MObject) {
							objectName = asslTranslation.getKodKodObjectName((MObject)asslObject);
						}
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = getAttributeBoundName(i) + 1 + "_" + asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(objectName, value));
							tsAttributeUpper.add(tFa.tuple(objectName, value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							int k = 0;
							for(String objectAttributeValue: objectAttributeValues) {
								String value = getAttributeBoundName(i) + (k + 1) + "_" + objectAttributeValue;
								tsPrimitive.add(tFa.tuple(value));
								tsAttributeUpper.add(tFa.tuple(objectName, value));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(objectName, objName));
						}
						y++;
					}
				//attribute type is class or associationclass
				} else if(att.type().isObjectType()){
					attributeNames.clear();
					attributeObjects.clear();
					attributesUpper.clear();

					int y = 1;
					
					// add assl Attributes
					for(Object asslObject : allObjects) {
						String objectName = null;
						if(asslObject instanceof String) {
							objectName = (String)asslObject;
						}
						if(asslObject instanceof MObject) {
							objectName = asslTranslation.getKodKodObjectName((MObject)asslObject);
						}
						if(asslObjectAttributeLowerMapping != null
								&& asslObjectAttributeLowerMapping.get(asslObject) != null
								&& asslObjectAttributeLowerMapping.get(asslObject).get(att.name()) != null) {
							String value = asslObjectAttributeLowerMapping.get(asslObject).get(att.name());
							tsPrimitive.add(tFa.tuple(value));
							tsAttributeLower.add(tFa.tuple(objectName, value));
							tsAttributeUpper.add(tFa.tuple(objectName, value));
						} else if(asslObjectAttributeMapping != null 
								&& asslObjectAttributeMapping.get(asslObject) != null
								&& asslObjectAttributeMapping.get(asslObject).get(att.name()) != null) {
							ArrayList<String> objectAttributeValues = asslObjectAttributeMapping.get(asslObject).get(att.name());
							for(String objectAttributeValue: objectAttributeValues) {
								tsPrimitive.add(tFa.tuple(objectAttributeValue));
								tsAttributeUpper.add(tFa.tuple(objectName, objectAttributeValue));
							}
						} else {
							// Add Undefined
							String objName = "Undefined";
							tsAttributeUpper.add(tFa.tuple(objectName, objName));
						}
						y++;
					}
				}
			}
			if(tsAttributeLower.size() == 0){
				bou.bound(attRelations.get(mAssClass.name() + "_-" + att.name()), tsAttributeUpper);
			}else{
				bou.bound(attRelations.get(mAssClass.name() + "_-" + att.name()), tsAttributeLower, tsAttributeUpper);
			}
			if(att.type().isString() || att.type().isReal()){
				bou.bound(priRelations.get(mAssClass.name() + "_-" + att.name() + "-" + att.type().toString()), tsPrimitive);
			}
		}
		return bou;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.tzi.use.kodkod.main.UMLAssociation#getAssociationBoundsAssl(kodkod
	 * .instance.Bounds, kodkod.instance.TupleFactory,
	 * org.tzi.use.kodkod.main.SetKodkodStruc, java.util.ArrayList)
	 */
	@Override
	public Bounds getAssociationBoundsAssl(Bounds bou, TupleFactory tFa,
			SetKodkodStruc setKodkodStruc, AsslTranslation asslTranslation) {
		
		ArrayList<Vector<Object>> asslAssociationList = asslTranslation.getAssociationClassBoundUpper().get(getName());
		ArrayList<Vector<MObject>> asslAssociationListExact = asslTranslation.getAssociationClassBoundLower().get(getName());

		TupleSet tsAssociationClassLower = tFa.noneOf(assClaRelation.arity());
		TupleSet tsAssociationClassUpper = tFa.noneOf(assClaRelation.arity());

		// Exact Bound is in Lower
		if (asslAssociationListExact != null) {
			for (Vector<MObject> association : asslAssociationListExact) {
				Vector<String> ass = new Vector<String>();
				for(MObject associationPart : association){
					ass.add(asslTranslation.getKodKodObjectName(associationPart));
				}
				tsAssociationClassLower.add(tFa.tuple(ass));
			}
		}

		tsAssociationClassUpper.addAll(tsAssociationClassLower);
		if (asslAssociationList != null) {
			int i = 1;
			int offset = asslTranslation.getClassObjectCount(getName());
			for (Vector<Object> asslAssociation : asslAssociationList) {
				Vector<String> ass = new Vector<String>();
				for(Object obj : asslAssociation) {
					if(obj instanceof String) {
						ass.add(((String)obj)+"@"+(i+offset));
					}
					if(obj instanceof MObject) {
						ass.add(asslTranslation.getKodKodObjectName((MObject)obj));
					}
				}
				tsAssociationClassUpper.add(tFa.tuple(ass));
				i++;
			}
		}

		// tsAssociationClassUpper = tmpTS;

		if (tsAssociationClassLower.size() > 0) {
			bou.bound(assClaRelation, tsAssociationClassLower,
					tsAssociationClassUpper);
		} else {
			bou.bound(assClaRelation, tsAssociationClassUpper);
		}
		return bou;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tzi.use.kodkod.main.UMLAssociation#usingAsslBounds(boolean)
	 */
	@Override
	public void usingAsslBounds(boolean usingAsslBounds) {
		// TODO Auto-generated method stub
		this.usingAsslBounds = usingAsslBounds;
	}
}