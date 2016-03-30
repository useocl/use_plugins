package org.tzi.use.plugin.filmstrip.logic.addon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.plugin.filmstrip.FilmstripModelConstants;
import org.tzi.use.plugin.filmstrip.logic.FilmstripUtil;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpInvalidException;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpStdOp;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.expr.Expression;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.expr.VarDeclList;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.sys.soil.MAttributeAssignmentStatement;
import org.tzi.use.uml.sys.soil.MBlockStatement;
import org.tzi.use.uml.sys.soil.MConditionalExecutionStatement;
import org.tzi.use.uml.sys.soil.MEmptyStatement;
import org.tzi.use.uml.sys.soil.MIterationStatement;
import org.tzi.use.uml.sys.soil.MLinkInsertionStatement;
import org.tzi.use.uml.sys.soil.MNewLinkObjectStatement;
import org.tzi.use.uml.sys.soil.MNewObjectStatement;
import org.tzi.use.uml.sys.soil.MRValue;
import org.tzi.use.uml.sys.soil.MRValueExpression;
import org.tzi.use.uml.sys.soil.MRValueNewLinkObject;
import org.tzi.use.uml.sys.soil.MRValueNewObject;
import org.tzi.use.uml.sys.soil.MSequenceStatement;
import org.tzi.use.uml.sys.soil.MStatement;
import org.tzi.use.uml.sys.soil.MVariableAssignmentStatement;

public class CopyOperation {
	
	private static interface ObjectCreator {
		public MStatement makeNewObjectStatement(MClass c);
	}
	
	private final MClass SNAPSHOT;
	private final UseModelApi modelApi;
	private final MModel model;
	
	public CopyOperation(MModel model) {
		super();
		SNAPSHOT = model.getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME);
		this.model = model;
		modelApi = new UseModelApi(model);
	}
	
	/**TODO
	 * - constant Strings into final fields
	 * - exception handling
	 */
	
	public void createAndAddCopyOperation() throws UnsupportedOperationException, UseApiException {
		MStatement soilOp = createCopyOperation();
		
		MOperation copyOp = modelApi.createOperation(FilmstripModelConstants.SNAPSHOT_CLASSNAME, "copy", new String[0][], FilmstripModelConstants.SNAPSHOT_CLASSNAME);
		modelApi.createPrePostCondition(FilmstripModelConstants.SNAPSHOT_CLASSNAME, "copy", "uncopied", "self.succ().oclIsUndefined()", true);
		copyOp.setStatement(soilOp);
	}
	
	/**
	 * @return SOIL expression of the operation
	 */
	private MStatement createCopyOperation(){
		MSequenceStatement main = new MSequenceStatement();
		
		MBlockStatement ret = new MBlockStatement(Arrays.asList(
				new VarDecl("s", SNAPSHOT)
				), main);
		
		// new Snapshot
		main.appendStatement(new MVariableAssignmentStatement("s", new MRValueNewObject(new MNewObjectStatement(SNAPSHOT, (String) null))));
		
		// copy class objects
		Set<MAssociationClass> assocClasses = new TreeSet<MAssociationClass>();
		for(MClass c : applClasses(model.classes())){
			if(c instanceof MAssociationClass){
				assocClasses.add((MAssociationClass) c);
				continue;
			}
			main.appendStatement(copyClass(model, c, new ObjectCreator() {
				@Override
				public MStatement makeNewObjectStatement(MClass c) {
					return new MVariableAssignmentStatement("x", new MRValueNewObject(new MNewObjectStatement(c, (String) null)));
				}
			}));
		}
		
		// copy assoc class objects
		//TODO force order by dependency
		for(MClass c : assocClasses){
			main.appendStatement(copyClass(model, c, new ObjectCreator() {
				@Override
				public MStatement makeNewObjectStatement(MClass c) {
					MAssociationClass ac = (MAssociationClass) c;
					
					List<MRValue> participants = new ArrayList<MRValue>();
					for(MAssociationEnd end : ac.associationEnds()){
						try {
							participants.add(new MRValueExpression(new ExpNavigation(new ExpVariable("cl", c), ac, end, Collections.<Expression>emptyList())));
						} catch (ExpInvalidException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
					}
					return new MVariableAssignmentStatement("x", new MRValueNewLinkObject(new MNewLinkObjectStatement(ac, participants, Collections.<List<MRValue>>emptyList(), (String) null)));
				}
			}));
		}
		
		// copy attribute values
		for(MClass c : applClasses(model.classes())){
			if(c.attributes().size() == 0){
				continue;
			}
			main.appendStatement(copyAttributes(model, c));
		}
		
		// copy links
		Set<MAssociation> handledAssocs = new TreeSet<MAssociation>();
		for(MClass c : applClasses(model.classes())){
			MSequenceStatement seq = null;
			for(MAssociation as : c.associations()){
				if(handledAssocs.contains(as) || FilmstripUtil.isFilmstripAssoc(as)){
					continue;
				}
				if(as.associationEnds().size() > 2){
					throw new UnsupportedOperationException("Snapshot::copy() only supports binary associations.");
				}
				
				if(seq == null){
					seq = new MSequenceStatement();
				}
				
				MRValue self = new MRValueExpression(FilmstripUtil.handlePredSucc(new ExpVariable("cl", c), false, new Stack<VarDeclList>())); // "cl.succ"
				MAssociationEnd ownEnd;
				MAssociationEnd otherEnd;
				if(as.associationEndsAt(c).size() == 2){
					MAssociationEnd[] aends = new MAssociationEnd[2];
					aends = as.associationEnds().toArray(aends);
					
					ownEnd = aends[0];
					otherEnd = aends[1];
				}
				else {
					ownEnd = as.associationEndsAt(c).iterator().next();
					otherEnd = (MAssociationEnd) as.navigableEndsFrom(c).get(0);
				}
					
				Expression objects;
				Expression cond;
				MRValue other;
				try {
					objects = new ExpNavigation(new ExpVariable("cl", c), ownEnd, otherEnd, Collections.<Expression>emptyList());
				
					if(otherEnd.multiplicity().isCollection()){
						cond = ExpStdOp.create("isEmpty", new Expression[]{ objects });
						other = new MRValueExpression(FilmstripUtil.handlePredSucc(new ExpVariable("elem", ((CollectionType)objects.type()).elemType()),
								false, new Stack<VarDeclList>()));
					}
					else {
						cond = ExpStdOp.create("oclIsUndefined", new Expression[]{ objects });
						other = new MRValueExpression(FilmstripUtil.handlePredSucc(objects, false,
								new Stack<VarDeclList>()));
					}
					cond = ExpStdOp.create("not", new Expression[]{ cond });
				}
				catch (ExpInvalidException ex) {
					//TODO
					ex.printStackTrace();
					throw new UnsupportedOperationException("Something went wrong!", ex);
				}
				
				List<MRValue> participants = new ArrayList<MRValue>();
				for(MAssociationEnd end : as.associationEnds()){
					if(end.equals(ownEnd)){
						participants.add(self);
					}
					else {
						participants.add(other);
					}
				}
				MStatement then;
				MStatement insertion = new MLinkInsertionStatement(as, participants, Collections.<List<MRValue>>emptyList());
				
				if(otherEnd.multiplicity().isCollection()){
					then = new MIterationStatement("elem", objects, insertion);
				}
				else {
					then = insertion;
				}
				
				seq.appendStatement(new MConditionalExecutionStatement(cond, then, MEmptyStatement.getInstance()));
				
				handledAssocs.add(as);
			}
			
			if(seq != null){
				MAssociation assoc = model.getAssociation(FilmstripModelConstants.makeSnapshotClsAssocName(c.name()));
				Expression iterExp;
				try {
					iterExp = new ExpNavigation(
							new ExpVariable("self", SNAPSHOT),
							assoc.getAssociationEnd(SNAPSHOT, FilmstripModelConstants.makeSnapshotClsRoleName(c.name())),
							assoc.getAssociationEnd(c, c.nameAsRolename()), Collections.<Expression>emptyList());
					main.appendStatement(new MIterationStatement("cl", iterExp, seq));
				} catch (ExpInvalidException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
		
		// result := s
		main.appendStatement(new MVariableAssignmentStatement("result", new MRValueExpression(new ExpVariable("s", model.getClass(FilmstripModelConstants.SNAPSHOT_CLASSNAME)))));
		
		return ret;
	}
	
	private MStatement copyAttributes(MModel model, MClass c) {
		MSequenceStatement seq = new MSequenceStatement();
		
		for(MAttribute a : c.attributes()){
			seq.appendStatement(new MAttributeAssignmentStatement(
					new ExpVariable("cl", c), a, new MRValueExpression(
							FilmstripUtil.handlePredSucc(
									new ExpAttrOp(a, FilmstripUtil
											.handlePredSucc(new ExpVariable(
													"cl", c), true,
													new Stack<VarDeclList>())),
									false, new Stack<VarDeclList>()))));
		}
		
		MAssociation assoc = model.getAssociation(FilmstripModelConstants.makeSnapshotClsAssocName(c.name()));
		MStatement ret = null;
		try {
			Expression iterExp = new ExpNavigation(
					new ExpVariable("s", SNAPSHOT),
					assoc.getAssociationEnd(SNAPSHOT, FilmstripModelConstants.makeSnapshotClsRoleName(c.name())),
					assoc.getAssociationEnd(c, c.nameAsRolename()), Collections.<Expression>emptyList());
			ret = new MIterationStatement("cl", iterExp, seq);
		} catch (ExpInvalidException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return ret;
	}

	private MStatement copyClass(MModel model, MClass c, ObjectCreator oc){
		if(c.isAbstract()){
			return MEmptyStatement.getInstance();
		}
		
		MSequenceStatement seq = new MSequenceStatement();
		
		// x := new <class>
		seq.appendStatement(oc.makeNewObjectStatement(c));
		
		// insert PredSucc link
		List<MRValue> predSuccRoles = Arrays.<MRValue>asList(
				new MRValueExpression(new ExpVariable("cl", c)),
				new MRValueExpression(new ExpVariable("x", c)));
		seq.appendStatement(new MLinkInsertionStatement(model
				.getAssociation(FilmstripModelConstants
						.makeClsOrdableAssocName(c.name())), predSuccRoles, new ArrayList<List<MRValue>>()));
		
		// insert Snapshot link
		List<MRValue> snapshotRoles = Arrays.<MRValue>asList(
				new MRValueExpression(new ExpVariable("x", c)),
						new MRValueExpression(new ExpVariable(
								"s", SNAPSHOT)));
		seq.appendStatement(new MLinkInsertionStatement(model
				.getAssociation(FilmstripModelConstants
						.makeSnapshotClsAssocName(c.name())),
				snapshotRoles, new ArrayList<List<MRValue>>()));
		
		// block with var x and iteration
		MBlockStatement inB = new MBlockStatement(Arrays.asList(
				new VarDecl("x", c)
				), seq);
		
		MAssociation assoc = model.getAssociation(FilmstripModelConstants.makeSnapshotClsAssocName(c.name()));
		Expression iterExp;
		MStatement ret = null;
		try {
			iterExp = new ExpNavigation(
					new ExpVariable("self", SNAPSHOT),
					assoc.getAssociationEnd(SNAPSHOT, FilmstripModelConstants.makeSnapshotClsRoleName(c.name())),
					assoc.getAssociationEnd(c, c.nameAsRolename()), Collections.<Expression>emptyList());
			ret = new MIterationStatement("cl", iterExp, inB);
		} catch (ExpInvalidException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * Filters the classes added by the Filmstrip transformation.
	 * 
	 * @param classes all classes of the model
	 * @return classes of the application model only
	 */
	private Collection<MClass> applClasses(Collection<MClass> classes){
		Collection<MClass> ret = new ArrayList<MClass>();
		for(MClass e : classes){
			if(!FilmstripUtil.isFilmstripClass(e)){
				ret.add(e);
			}
		}
		return ret;
	}
	
}
