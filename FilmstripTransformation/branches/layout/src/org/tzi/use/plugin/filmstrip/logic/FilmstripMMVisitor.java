package org.tzi.use.plugin.filmstrip.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.plugin.filmstrip.logic.FilmstripExpressionVisitor.ExpressionType;
import org.tzi.use.uml.mm.Annotatable;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MElementAnnotation;
import org.tzi.use.uml.mm.MGeneralization;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MMVisitor;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.mm.MPrePostCondition;
import org.tzi.use.uml.mm.ModelFactory;
import org.tzi.use.uml.mm.commonbehavior.communications.MSignal;
import org.tzi.use.uml.ocl.expr.ExpAllInstances;
import org.tzi.use.uml.ocl.expr.ExpForAll;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.util.StringUtil;

//TODO add position in model for new model elements
public class FilmstripMMVisitor implements MMVisitor {
	
	public static final String NAME = "Maximum compatible transformation";
	
	protected UseModelApi model;
	protected MModelConnector mc;
	
	protected Stack<Annotatable> annotatables = new Stack<Annotatable>();
	protected final ModelFactory mFactory = new ModelFactory();
	
	protected FilmstripMMVisitor(String modelName){
		model = new UseModelApi(modelName);
		mc = new FilmstripModelConnector(model.getModel());
	}
	
	public static MModel transformModel(MModel model, String modelName){
		FilmstripMMVisitor visitor = new FilmstripMMVisitor(modelName);
		model.processWithVisitor(visitor);
		return visitor.getModel();
	}
	
	protected MModel getModel(){
		return model.getModel();
	}
	
	protected void initModel(){
		try {
			createFilmstripElements();
			
			// additional elements for the SnapshotItem interface
			// classes
			model.createClass(FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME, true);
			
			// associations
			model.createAssociation(FilmstripModelConstants.ORDERABLE_ASSOCNAME,
					FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME,
					FilmstripModelConstants.PRED_ROLENAME,
					"0..1",
					MAggregationKind.AGGREGATION,
					FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME,
					FilmstripModelConstants.SUCC_ROLENAME,
					"0..1",
					MAggregationKind.NONE);
			
			model.createAssociation(FilmstripModelConstants.SNAPSHOTELEMENT_ASSOCNAME,
					FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME,
					FilmstripModelConstants.makeRoleName(FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME),
					"*",
					MAggregationKind.NONE,
					FilmstripModelConstants.SNAPSHOT_CLASSNAME,
					FilmstripModelConstants.makeRoleName(FilmstripModelConstants.SNAPSHOT_CLASSNAME),
					"1",
					MAggregationKind.NONE);
		} catch (UseApiException e) {
			throw new TransformationException("Error initialising the model", e);
		}
	}
	
	/**
	 * Helper to visit expressions.
	 * 
	 * @param expr
	 * @return
	 */
	protected Expression visitExpression(Expression expr, ExpressionType type, MClass src, VarDeclList varDefs){
		FilmstripExpressionVisitor visitor = new FilmstripExpressionVisitor(
				model.getModel(), src, type, mc, varDefs);
		expr.processWithVisitor(visitor);
		return visitor.getResultExpression();
	}
	
	/**
	 * Creates an operation call class iff the class has operations requiring one.
	 * 
	 * @param cls
	 */
	protected void createOpCClass(MClass cls){
		for(MOperation op : cls.operations()){
			if(!op.preConditions().isEmpty() || !op.postConditions().isEmpty()){
				String opcName = FilmstripModelConstants.makeOpCName(cls.name());
				
				try {
					model.createClass(opcName, true);
					model.createAttribute(opcName,
							FilmstripModelConstants.OPC_SELF_VARNAME, cls.toString());
					model.createGeneralization(opcName,
							FilmstripModelConstants.OPC_CLASSNAME);
					model.createInvariant(
							FilmstripModelConstants.OPC_INV_SELFDEFINED_NAME,
							opcName,
							FilmstripModelConstants.OPC_INV_SELFDEFINED,
							false);
					model.createInvariant(
							FilmstripModelConstants.OPC_INV_SELFINPRED_NAME,
							opcName,
							FilmstripModelConstants.OPC_INV_SELFINPRED,
							false);
				}
				catch (UseApiException ex) {
					throw new TransformationException(
							"Error creating OpC class for "
									+ StringUtil.inQuotes(cls.name()), ex);
				}
				return;
			}
		}
	}
	
	/**
	 * Creates class invariants for the application model classes.
	 * 
	 * @param cls
	 */
	protected void createClassInv(MClass cls) {
		try {
			// valid pred succ linking
			model.createInvariant(
					FilmstripModelConstants.CLASS_INV_VALIDLINKING_NAME,
					cls.name(),
					FilmstripModelConstants.CLASS_INV_VALIDLINKING,
					false);
		} catch (UseApiException ex) {
			throw new TransformationException(
					"Error adding invariant "
							+ StringUtil
									.inQuotes(cls.name() + "::" + FilmstripModelConstants.CLASS_INV_VALIDLINKING_NAME),
					ex);
		}
	}
	
	/**
	 * Creates invariants for associations from the application model.
	 * 
	 * @param assoc
	 */
	protected void createAssocInv(MAssociation assoc) {
		String sourceClass = null;
		
		String[] ends = new String[assoc.associationEnds().size()-1];
		boolean isTernary = ends.length > 1;
		int i = 0;
		for(MAssociationEnd end : assoc.associationEnds()){
			if(sourceClass == null){
				// first end is invariant holder
				sourceClass = end.cls().name();
				continue;
			}
			
			ends[i++] = FilmstripModelConstants.makeValidLinkingInvPart(end.name(),
					isTernary || end.multiplicity().isCollection());
		}
		
		String inv = StringUtil.fmtSeq(ends, " and ");
		String invName = FilmstripModelConstants.makeValidLinkingInvName(assoc.name());
		try {
			model.createInvariant(
					invName,
					sourceClass,
					inv,
					false);
		} catch (UseApiException ex) {
			throw new TransformationException("Error adding invariant "
					+ StringUtil.inQuotes(sourceClass  + "::" + invName), ex);
		}
	}
	
	/**
	 * Creates invariants for operation call parameters.
	 * 
	 * @param attr
	 * @param inPred
	 */
	protected void createParamInv(MAttribute attr, boolean inPred) {
		String inv = FilmstripModelConstants.makeParamDefinedInv(
				"self." + attr.name(), attr.type(), inPred);
		if(inv.equals("true")){
			return;
		}
		
		String invName = FilmstripModelConstants.makeParamDefinedInvName(attr, inPred);
		
		try {
			model.createInvariant(
					invName,
					attr.owner().name(),
					inv,
					false);
		} catch (UseApiException ex) {
			throw new TransformationException("Error adding invariant "
					+ StringUtil.inQuotes(invName), ex);
		}
	}
	
	/**
	 * Copies over association specifics like subsets, redefines, derived and union.
	 * 
	 * @param from
	 * @param to
	 */
	protected void copyAssociationDetails(MAssociation from, MAssociation to){
		for(MAssociationEnd end : from.associationEnds()){
			MAssociationEnd newEnd = to.getAssociationEnd(mc.mapClass(end.cls()), end.name());

			copyAnnotations(end, newEnd);
			
			for(MAssociationEnd sEnd : end.getSubsettedEnds()){
				MAssociationEnd tmp = mc.mapAssociationEnd(sEnd);
				newEnd.addSubsettedEnd(tmp);
				tmp.addSubsettingEnd(newEnd);
			}
			for(MAssociationEnd rEnd : end.getRedefinedEnds()){
				MAssociationEnd tmp = mc.mapAssociationEnd(rEnd);
				newEnd.addRedefinedEnd(tmp);
				tmp.addRedefiningEnd(newEnd);
			}
			
			if(end.isUnion()){
				newEnd.setUnion(true);
			}
			if(end.isDerived()){
				VarDeclList l = new VarDeclList(end.getDeriveParamter().allHaveSameType());
				for(VarDecl vd : end.getDeriveParamter()){
					l.add(new VarDecl(vd.name(), mc.mapType(vd.type())));
				}
				newEnd.setDeriveExpression(l,
						visitExpression(end.getDeriveExpression(),
								ExpressionType.OPERATION, newEnd.cls(), l));
				newEnd.setDerived(true);
			}
		}
	}
	
	protected void createFilmstripElements() throws UseApiException {
		// classes
		model.createClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME, false);
		model.createClass(FilmstripModelConstants.OPC_CLASSNAME, true);
		
		// associations
		model.createAssociation(FilmstripModelConstants.FILMSTRIP_ASSOCNAME, 
			new String[]{
					FilmstripModelConstants.SNAPSHOT_CLASSNAME,
					FilmstripModelConstants.SNAPSHOT_CLASSNAME,
					FilmstripModelConstants.OPC_CLASSNAME
			}, new String[]{
					FilmstripModelConstants.PRED_ROLENAME,
					FilmstripModelConstants.SUCC_ROLENAME,
					FilmstripModelConstants.makeRoleName(FilmstripModelConstants.OPC_CLASSNAME)
			}, new String[]{
					"0..1",
					"0..1",
					"0..1"
			}, new int[]{
					MAggregationKind.NONE,
					MAggregationKind.NONE,
					MAggregationKind.NONE
			}, new boolean[]{
					false,
					false,
					false
			}, new String[0][][]);
		
		// operations
		model.createQueryOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.PRED_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_PRED_OP);
		model.createQueryOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SUCC_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_SUCC_OP);
		
		model.createQueryOperation(FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.PRED_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_PRED_OP);
		model.createQueryOperation(FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.SUCC_ROLENAME,
				new String[0][],
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_SUCC_OP);
		
		// constraints
		model.createInvariant(
				FilmstripModelConstants.OPC_INV_ASSOCCLASSBEHAVIOR_NAME,
				FilmstripModelConstants.OPC_CLASSNAME,
				FilmstripModelConstants.OPC_INV_ASSOCCLASSBEHAVIOR,
				false);
		
		model.createInvariant(
				FilmstripModelConstants.SNAPSHOT_INV_CYCLEFREE_NAME,
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_INV_CYCLEFREE,
				false);
		
		model.createInvariant(
				FilmstripModelConstants.SNAPSHOT_INV_ONEFILMSTRIP_NAME,
				FilmstripModelConstants.SNAPSHOT_CLASSNAME,
				FilmstripModelConstants.SNAPSHOT_INV_ONEFILMSTRIP,
				false);
	}
	
	@Override
	public void visitAnnotation(MElementAnnotation a) {
		Map<String, String> annoValues = new HashMap<String, String>();
		
		for(Map.Entry<String, String> value : a.getValues().entrySet()){
			annoValues.put(value.getKey(), value.getValue());
		}
		
		try {
			annotatables.pop().addAnnotation(new MElementAnnotation(a.getName(), annoValues));
		}
		catch(EmptyStackException ex){
			throw new TransformationException("Error handling annotation "
					+ StringUtil.inQuotes(a.getName()));
		}
	}
	
	protected void copyAnnotations(Annotatable from, Annotatable to){
		for (MElementAnnotation ann : from.getAllAnnotations().values()) {
			annotatables.push(to);
			ann.processWithVisitor(this);
		}
	}
	
	@Override
	public void visitAssociation(MAssociation e) {
		int numEntries = e.associationEnds().size();
		String[] assocEnds = new String[numEntries];
		String[] roleNames = new String[numEntries];
		String[] multiplicities = new String[numEntries];
		int[] aggregationKinds = new int[numEntries];
		boolean[] orderedInfo = new boolean[numEntries];
		String[][][] qualifier;
		if(e.hasQualifiedEnds()){
			qualifier = new String[numEntries][][];
			for (int i = 0; i < qualifier.length; i++) {
				qualifier[i] = new String[0][];
			}
		} else {
			qualifier = new String[0][][];
		}
		
		int idx = 0;
		for(MAssociationEnd assocEnd : e.associationEnds()){
			assocEnds[idx] = assocEnd.cls().name();
			roleNames[idx] = assocEnd.name();
			multiplicities[idx] = assocEnd.multiplicity().toString();
			aggregationKinds[idx] = assocEnd.aggregationKind();
			orderedInfo[idx] = assocEnd.isOrdered();
			
			if(assocEnd.hasQualifiers()){
				String[][] qualifiers = new String[assocEnd.getQualifiers().size()][2];
				int i = 0;
				for (Iterator<VarDecl> it = assocEnd.getQualifiers().iterator(); it
						.hasNext(); i++) {
					VarDecl var = it.next();
					qualifiers[i] = new String[]{
						var.name(),
						var.type().toString()
					};
				}
				qualifier[idx] = qualifiers;
			}
			
			idx++;
		}
		
		MAssociation newAssoc;
		try {
			newAssoc = model.createAssociation(e.name(), assocEnds, roleNames, multiplicities,
					aggregationKinds, orderedInfo, qualifier);
		} catch (UseApiException ex) {
			throw new TransformationException("Error adding association "
					+ StringUtil.inQuotes(e.name()), ex);
		}

		// redefines, subsets, union marks
		newAssoc.setPositionInModel(e.getPositionInModel());
		copyAssociationDetails(e, newAssoc);
		copyAnnotations(e, newAssoc);
		
		createAssocInv(newAssoc);
	}
	
	@Override
	public void visitAssociationClass(MAssociationClass e) {
		String[] assocEnds = new String[e.getAllOtherAssociationEnds().size()];
		String[] roleNames = new String[e.getAllOtherAssociationEnds().size()];
		String[] multiplicities = new String[e.getAllOtherAssociationEnds().size()];
		int[] aggregationKinds = new int[e.getAllOtherAssociationEnds().size()];
		
		int idx = 0;
		for(MAssociationEnd assocEnd : e.getAllOtherAssociationEnds()){
			assocEnds[idx] = assocEnd.cls().name();
			roleNames[idx] = assocEnd.name();
			multiplicities[idx] = assocEnd.multiplicity().toString();
			aggregationKinds[idx] = assocEnd.aggregationKind();
			idx++;
		}
		
		MAssociationClass newAssocClass;
		try {
			newAssocClass = model.createAssociationClass(e.name(), e.isAbstract(), assocEnds,
					roleNames, multiplicities, aggregationKinds);
			model.createGeneralization(e.name(), FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME);
		} catch (UseApiException ex) {
			throw new TransformationException("Error adding associationclass "
					+ StringUtil.inQuotes(e.name()), ex);
		}
		
		newAssocClass.setPositionInModel(e.getPositionInModel());
		copyAssociationDetails(e, newAssocClass);
		copyAnnotations(e, newAssocClass);
		
		createOpCClass(e);
		createClassInv(newAssocClass);
		createAssocInv(newAssocClass);
	}

	@Override
	public void visitAssociationEnd(MAssociationEnd e) {
		// handled by visitAssociation
	}

	@Override
	public void visitAttribute(MAttribute e) {
		MAttribute attribute;
		try {
			attribute = model.createAttribute(e.owner().name(), e.name(), e.type().toString());
			
			// derived and init expressions might be dependent on query operations and are added later
		} catch (UseApiException ex) {
			throw new TransformationException("Error adding attribute "
					+ StringUtil.inQuotes(e.name()) + " of class "
					+ StringUtil.inQuotes(e.owner().name()), ex);
		}
		
		attribute.setPositionInModel(e.getPositionInModel());
		copyAnnotations(e, attribute);
	}

	@Override
	public void visitClass(MClass e) {
		MClass newClass;
		try {
			newClass = model.createClass(e.name(), e.isAbstract());
			model.createGeneralization(e.name(), FilmstripModelConstants.SNAPSHOTITEM_CLASSNAME);
			
			MAssociation snapshotAssoc = model.createAssociation(
					FilmstripModelConstants.makeSnapshotClsAssocName(e.name()),
					e.name(),
					e.nameAsRolename(),
					"*",
					MAggregationKind.NONE,
					FilmstripModelConstants.SNAPSHOT_CLASSNAME,
					FilmstripModelConstants.makeSnapshotClsRoleName(e.name()),
					"1",
					MAggregationKind.NONE);
			
			// add redefines marks
			MAssociation snapshotBaseAssoc = model.getAssociation(FilmstripModelConstants.SNAPSHOTELEMENT_ASSOCNAME);
			snapshotAssoc.addRedefines(snapshotBaseAssoc);
			snapshotBaseAssoc.addRedefinedBy(snapshotAssoc);
			
			MAssociationEnd snapshotBaseEnd = snapshotBaseAssoc.getAssociationEnd(
					model.getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME),
					FilmstripModelConstants.SNAPSHOT_ROLENAME);
			for(MAssociationEnd snapshotEnd : snapshotAssoc.associationEnds()){
				if(snapshotEnd.name().startsWith(snapshotBaseEnd.name())){
					snapshotEnd.addRedefinedEnd(snapshotBaseEnd);
					snapshotBaseEnd.addRedefiningEnd(snapshotEnd);
					break;
				}
			}
			
			MAssociation orderableAssoc = model.createAssociation(
					FilmstripModelConstants.makeClsOrdableAssocName(e.name()),
					e.name(),
					FilmstripModelConstants.makeClsPredRolename(e.name()),
					"0..1",
					MAggregationKind.AGGREGATION,
					e.name(),
					FilmstripModelConstants.makeClsSuccRolename(e.name()),
					"0..1",
					MAggregationKind.NONE);
			
			// add redefines marks
			MAssociation orderableBaseAssoc = model.getAssociation(FilmstripModelConstants.ORDERABLE_ASSOCNAME);
			orderableAssoc.addRedefines(orderableBaseAssoc);
			orderableBaseAssoc.addRedefinedBy(orderableAssoc);
			
			for(MAssociationEnd end : orderableAssoc.associationEnds()){
				for(MAssociationEnd baseEnd : orderableBaseAssoc.associationEnds()){
					if(end.name().startsWith(baseEnd.name())){
						end.addRedefinedEnd(baseEnd);
						baseEnd.addRedefiningEnd(end);
						break;
					}
				}
			}
		}
		catch (UseApiException ex) {
			throw new TransformationException("Error transforming class "
					+ StringUtil.inQuotes(e.name()), ex);
		}

		newClass.setPositionInModel(e.getPositionInModel());
		copyAnnotations(e, newClass);
		createOpCClass(e);
		createClassInv(newClass);
		
		/*
		 * attributes are processed when all types (classes) are known
		 * invariants and operations are processed when all classes and
		 * associations are computed
		 * Statemachines are trimmed from the model
		 */
	}
	
	@Override
	public void visitClassInvariant(MClassInvariant e) {
		MClass newOwningClass = mc.mapClass(e.cls());
		
		Expression expToTransform = e.bodyExpression();
		
		VarDeclList varDefs = null;
		List<String> vars = null;
		if(e.hasVar()){
			varDefs = new VarDeclList(true);
			vars = new ArrayList<String>(1);
			VarDeclList secondaryVarDefs = new VarDeclList(true);
			
			boolean first = true;
			for(VarDecl var : e.vars()){
				if(first){
					varDefs.add(new VarDecl(var.name(), newOwningClass));
					vars.add(var.name());
				} else {
					secondaryVarDefs.add(new VarDecl(var.name(), newOwningClass));
				}
				first = false;
			}
			
			if(e.vars().size() > 1){
				// rewrite invariant with multiple variables
				try {
					Expression rangeExp = new ExpAllInstances(newOwningClass);
					expToTransform = new ExpForAll(secondaryVarDefs, rangeExp, expToTransform);
				} catch (ExpInvalidException e1) {
					throw new TransformationException("Unable to rewrite variables for invariant " + StringUtil.inQuotes(e.qualifiedName()), e1);
				}
			}
		}
		
		MClassInvariant newInv;
		try {
			newInv = mFactory.createClassInvariant(
					e.name(),
					vars,
					mc.mapClass(e.cls()),
					visitExpression(expToTransform,
							ExpressionType.CLASSINVARIANT,
							newOwningClass, varDefs),
					e.isExistential());
			
			model.getModel().addClassInvariant(newInv);
		}
		catch (ExpInvalidException | MInvalidModelException ex) {
			throw new TransformationException(
					"Error transforming class invariant "
							+ StringUtil.inQuotes(e.name()) + " of class "
							+ StringUtil.inQuotes(e.cls().name()), ex);
		}
		
		newInv.setPositionInModel(e.getPositionInModel());
		copyAnnotations(e, newInv);
	}

	@Override
	public void visitEnum(EnumType e){
		EnumType newEnumeration;
		try {
			newEnumeration = model.createEnumeration(e.name(), new ArrayList<String>(e.getLiterals()));
		} catch (UseApiException ex) {
			throw new TransformationException("Error transforming enumeration "
					+ StringUtil.inQuotes(e.name()), ex);
		}
		
		newEnumeration.setPositionInModel(e.getPositionInModel());
		copyAnnotations(e, newEnumeration);
	}
	
	@Override
	public void visitGeneralization(MGeneralization e) {
		MGeneralization generalization;
		try {
			generalization = model.createGeneralization(e.child().name(), e.parent().name());
		} catch (UseApiException ex) {
			throw new TransformationException(
					"Error transforming generalization between "
							+ StringUtil.inQuotes(e.parent().name()) + " and "
							+ StringUtil.inQuotes(e.child().name()), ex);
		}
		
		copyAnnotations(e, generalization);
	}

	@Override
	public void visitModel(MModel m) {
		initModel();
		
		for(EnumType e : m.enumTypes()){
			visitEnum(e);
		}
		
		Set<MAssociationClass> associationClasses = new HashSet<MAssociationClass>();
		for(MClass c : m.classes()){
			if(c instanceof MAssociationClass){
				associationClasses.add((MAssociationClass) c);
				continue;
			}
			c.processWithVisitor(this);
		}
		
		List<MAssociationClass> assocClsDependencies = makeDependencyAssocClasses(associationClasses);
		for(MClass cls : assocClsDependencies){
			cls.processWithVisitor(this);
		}

		// all classes are defined
		// process attributes
		for(MClass ca : m.classes()){
			for(MAttribute a : ca.attributes()){
				a.processWithVisitor(this);
			}
		}
		
		// associations might redefine or subset others
		List<MAssociation> assocsDependencies = makeDependencyAssocs(m.associations());
		for(MAssociation as : assocsDependencies){
			as.processWithVisitor(this);
		}
		
		for (Iterator<MGeneralization> iterator = m.generalizationGraph().edgeIterator(); iterator.hasNext();) {
			MGeneralization gen = iterator.next();
			gen.processWithVisitor(this);
		}
		
		// visit and define all operations
		for(MClass ca : m.classes()){
			for(MOperation op : ca.operations()){
				op.processWithVisitor(this);
			}
		}
		
		// visit operation bodies and attribute init and derive expressions, that might refer other operations
		for(MClass ca : m.classes()){
			for(MOperation op : ca.operations()){
				visitOperationBody(op);
			}
			for(MAttribute a : ca.attributes()){
				visitAttrInitDeriveExpr(a);
			}
		}
		
		for(MClassInvariant inv : m.modelClassInvariants()){
			inv.processWithVisitor(this);
		}
		
		for(MPrePostCondition cond : m.prePostConditions()){
			cond.processWithVisitor(this);
		}
		
		copyAnnotations(m, model.getModel());
	}

	private List<MAssociation> makeDependencyAssocs(
			Collection<MAssociation> associations) {
		List<MAssociation> res = new ArrayList<MAssociation>(associations.size());
		
		// collect data
		Map<MAssociation, Set<? extends MAssociation>> dependencies = new TreeMap<MAssociation, Set<? extends MAssociation>>();
		
		for(MAssociation elem : associations){
			dependencies.put(elem, elem.allParents());
		}
		
		// format data
		int count = 0;
		while(res.size() != associations.size()){
			count = res.size();
			for(Entry<MAssociation, Set<? extends MAssociation>> elem : dependencies.entrySet()){
				if(res.contains(elem)){
					continue;
				}
				boolean all = true;
				for(MAssociation assoc : elem.getValue()){
					if(!res.contains(assoc)){
						all = false;
						break;
					}
				}
				if(all){
					res.add(elem.getKey());
				}
			}
			
			if(count == res.size() && res.size() != associations.size()){
				// reflexive dependency, infinite loop
				throw new TransformationException("Could not resolve dependencies for Associations.");
			}
		}
		
		return res;
	}

	/**
	 * Sorts the association classes by association dependency. All association
	 * classes an association class might reference must be defined.
	 * 
	 * @param associationClasses
	 * @return ordered list of association classes
	 */
	private List<MAssociationClass> makeDependencyAssocClasses(Set<MAssociationClass> associationClasses) {
		List<MAssociationClass> res = new ArrayList<>(associationClasses.size());
		
		// collect data
		Map<MAssociationClass, Set<MAssociationClass>> dependencies = new TreeMap<MAssociationClass, Set<MAssociationClass>>();
		
		for(MAssociationClass elem : associationClasses){
			Set<MAssociationClass> deps = new TreeSet<MAssociationClass>();
			for(MClass e : elem.associatedClasses()){
				if(associationClasses.contains(e)){
					deps.add((MAssociationClass) e);
				}
			}
			
			dependencies.put(elem, deps);
		}
		
		// format data
		int count = 0;
		while(res.size() != associationClasses.size()){
			count = res.size();
			
			for(Entry<MAssociationClass, Set<MAssociationClass>> e : dependencies.entrySet()){
				if(res.contains(e.getKey())){
					continue;
				}
				boolean all = true;
				for(MAssociationClass aCls : e.getValue()){
					if(!res.contains(aCls)){
						all = false;
						break;
					}
				}
				if(all){
					res.add(e.getKey());
				}
			}
			
			if(count == res.size() && res.size() != associationClasses.size()){
				// reflexive dependency, infinite loop
				throw new TransformationException("Could not resolve dependencies for AssociationClasses.");
			}
		}
		
		return res;
	}

	@Override
	public void visitOperation(MOperation op) {
		VarDeclList paramList = op.paramList();
		String[][] params = new String[paramList.size()][2];
		
		for(int i = 0; i < paramList.size(); i++){
			VarDecl elem = paramList.varDecl(i);
			params[i][0] = elem.name();
			params[i][1] = elem.type().toString();
		}
		
		String resultType = op.hasResultType()?op.resultType().toString():null;
		MOperation newOp;
		try {
			newOp = model.createOperation(op.cls().name(), op.name(), params, resultType);
		} catch (UseApiException ex) {
			throw new TransformationException("Error creating operation "
					+ StringUtil.inQuotes(op.name()), ex);
		}
		
		newOp.setPositionInModel(op.getPositionInModel());
		copyAnnotations(op, newOp);
		
		if(!op.preConditions().isEmpty() || !op.postConditions().isEmpty()) {
			String opcName = FilmstripModelConstants.makeOpCName(op.cls().name(), op.name());
			try {
				model.createClass(opcName, false);
				model.createGeneralization(opcName, FilmstripModelConstants.makeOpCName(op.cls().name()));
				
				if(resultType != null){
					MAttribute res = model.createAttribute(opcName,
							FilmstripModelConstants.OPC_RETURNVALUE_VARNAME,
							resultType);
					createParamInv(res, false);
				}
				for(VarDecl var : op.paramList()){
					MAttribute param = model.createAttribute(opcName,
							var.name(), var.type().toString());
					createParamInv(param, true);
				}
			}
			catch(UseApiException ex){
				throw new TransformationException(
						"Error transforming OpC class for operation "
								+ StringUtil.inQuotes(op.name()), ex);
			}
		}
	}

	private void visitOperationBody(MOperation op){
		MOperation newOp = mc.mapOperation(op);
		
		if(op.hasBody()){
			if(op.hasExpression()){
				VarDeclList varDefs = new VarDeclList(false);
				for(VarDecl var : op.paramList()){
					varDefs.add(new VarDecl(var.name(), mc.mapType(var.type())));
				}
				if(op.resultType() != null){
					varDefs.add(new VarDecl("result", mc.mapType(op.resultType())));
				}
				
				try {
					newOp.setExpression(visitExpression(op.expression(),
							ExpressionType.OPERATION, mc.mapClass(op.cls()), varDefs));
				}
				catch (MInvalidModelException ex) {
					throw new TransformationException(
							"Error transforming expression for operation "
									+ StringUtil.inQuotes(op.name())
									+ " of class "
									+ StringUtil.inQuotes(op.cls().name()), ex);
				}
			}
			else if(op.hasStatement()){
				MStatement newOpStmt;
				try {
					FilmstripStatementVisitor fsv = new FilmstripStatementVisitor(model.getModel(), mc.mapClass(op.cls()), mc);
					op.getStatement().processWithVisitor(fsv);
					newOpStmt = fsv.getResultStatement();
				} catch (Exception ex) {
					throw new TransformationException(
							"Error transforming statement for operation "
									+ StringUtil.inQuotes(op.name())
									+ " of class "
									+ StringUtil.inQuotes(op.cls().name()), ex);
				}
				
				newOp.setStatement(newOpStmt);
			}
			else {
				throw new TransformationException(
						"Model contains an unsupported operation body. Class: "
								+ StringUtil.inQuotes(op.cls().name())
								+ "; Op: " + StringUtil.inQuotes(op.name()));
			}
		}
	}
	
	private void visitAttrInitDeriveExpr(MAttribute src) {
		MAttribute target = mc.mapAttribute(src);
		
		if(src.isDerived()){
			target.setDeriveExpression(visitExpression(src.getDeriveExpression(), ExpressionType.SOIL, target.owner(), null));
		}
		if(src.getInitExpression().isPresent()){
			target.setInitExpression(visitExpression(src.getInitExpression().get(), ExpressionType.SOIL, target.owner(), null));
		}
		
	}
	
	@Override
	public void visitPrePostCondition(MPrePostCondition e) {
		MClass cls = model.getClass(FilmstripModelConstants.makeOpCName(e.cls().name(), e.operation().name()));
		if(cls == null){
			throw new TransformationException("Could not find class "
					+ StringUtil.inQuotes(FilmstripModelConstants.makeOpCName(e
							.cls().name(), e.operation().name())));
		}
		
		MClassInvariant inv;
		try {
			ExpressionType expType = e.isPre()?ExpressionType.PRECONDITION:ExpressionType.POSTCONDITION;
			Expression newExpression = visitExpression(e.expression(), expType, cls, null);
			
			String invName = FilmstripModelConstants.makeInvName(e.name(), e.isPre());
			inv = mFactory.createClassInvariant(invName, null, cls, newExpression, false);
			model.getModel().addClassInvariant(inv);
		}
		catch (ExpInvalidException | MInvalidModelException ex) {
			throw new TransformationException(
					"Error transforming classinvariant for PrePostCondition "
							+ StringUtil.inQuotes(e.name()) + " of class "
							+ StringUtil.inQuotes(e.cls().name()), ex);
		}
		
		copyAnnotations(e, inv);
	}
	
	@Override
	public void visitSignal(MSignal mSignalImpl) {
		// trimmed from the model
	}
	
}
