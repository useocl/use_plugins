package org.tzi.use.kodkod.transform.ocl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.impl.DerivedAssociation;
import org.tzi.kodkod.model.impl.DerivedAttribute;
import org.tzi.kodkod.model.type.ObjectType;
import org.tzi.kodkod.model.type.TypeLiterals;
import org.tzi.use.kodkod.transform.TransformationException;
import org.tzi.use.kodkod.transform.TypeConverter;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.expr.ExpAny;
import org.tzi.use.uml.ocl.expr.ExpAttrOp;
import org.tzi.use.uml.ocl.expr.ExpNavigation;
import org.tzi.use.uml.ocl.expr.ExpNavigationClassifierSource;
import org.tzi.use.uml.ocl.expr.ExpObjOp;
import org.tzi.use.uml.ocl.expr.ExpVariable;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.Type.VoidHandling;
import org.tzi.use.util.StringUtil;

import kodkod.ast.Node;
import kodkod.ast.Variable;

/**
 * Extension of DefaultExpressionVisitor to visit the variable operations of an
 * expression.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class VariableOperationVisitor extends DefaultExpressionVisitor {

	private IClass attributeClass;

	public VariableOperationVisitor(IModel model, Map<String, Node> variables, Map<String, IClass> variableClasses,
			Map<String, Variable> replaceVariables, List<String> collectionVariables) {
		super(model, variables, variableClasses, replaceVariables, collectionVariables);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <b>Note:</b> This method is only reachable from inside the
	 * {@code VariableOperationVisitor}. Other {@code any} expressions are
	 * handled by {@link QueryExpressionVisitor#visitAny(ExpAny)}.
	 */
	@Override
	public void visitAny(ExpAny exp) {
		super.visitAny(exp);
		Type type = exp.getVariableDeclarations().varDecl(0).type();
		if(type.isTypeOfClass()){
			attributeClass = model.getClass(((MClass)type).name());
		}
	}
	
	@Override
	public void visitAttrOp(ExpAttrOp exp) {
		exp.objExp().processWithVisitor(this);
		
		if(attributeClass == null){
			// try to find the source type
			org.tzi.kodkod.model.type.Type sourceType = new TypeConverter(model).convert(exp.objExp().type());
			if(sourceType instanceof ObjectType){
				attributeClass = ((ObjectType) sourceType).clazz();
			} else {
				throw new TransformationException("Cannot determine type of source expression for " + StringUtil.inQuotes(exp.toString()) + ".");
			}
		}
		IAttribute attribute = attributeClass.getAttribute(exp.attr().name());
		
		if (attribute != null) {
			if(attribute instanceof DerivedAttribute){
				handleDerivedAttribute(exp, (DerivedAttribute) attribute);
			} else {
				handleRegularAttribute(exp, attribute);
			}
			
			// update current type
			if(attribute.type().isObjectType()){
				org.tzi.kodkod.model.type.ObjectType t = (org.tzi.kodkod.model.type.ObjectType) attribute.type();
				attributeClass = t.clazz();
			}
			else {
				attributeClass = null;
			}
		} else {
			throw new TransformationException("Cannot find attribute " + exp.attr().name() + ".");
		}
	}

	private void handleDerivedAttribute(ExpAttrOp exp, DerivedAttribute attribute) {
		//TODO recursion detector
		Node oldSelf = variables.get("self");
		boolean oldSet = collectionVariables.contains("self");
		
		variables.put("self", (Node) object);
		if(set){
			collectionVariables.add("self");
		}
		attribute.derivedExpression().processWithVisitor(this);
		
		if(oldSelf != null){
			variables.put("self", oldSelf);
		} else {
			variables.remove("self");
		}
		if(!oldSet){
			collectionVariables.remove("self");
		}
	}

	private void handleRegularAttribute(ExpAttrOp exp, IAttribute attribute) {
		set = attribute.type().isSet();
		sourceType = exp.type();

		List<Object> arguments = new ArrayList<Object>();
		arguments.add(object);
		arguments.add(attribute.relation());
		arguments.add(set);

		invokeMethod("access", arguments, false);
	}

	@Override
	public void visitNavigation(ExpNavigation exp) {
		exp.getObjectExpression().processWithVisitor(this);
		
		MNavigableElement source = exp.getSource();
		MNavigableElement destination = exp.getDestination();
		MAssociation mAssociation = source.association();

		IAssociation association = model.getAssociation(mAssociation.name());

		if (association != null) {
			if(association instanceof DerivedAssociation){
				handleDerivedAssociationNavigation(exp, association, source, destination);
			} else {
				handleRegularAssociationNavigation(exp, association, source, destination);
			}
		} else {
			throw new TransformationException("Cannot find association " + StringUtil.inQuotes(mAssociation.name()) + ".");
		}
	}
	
	private void handleDerivedAssociationNavigation(ExpNavigation exp, IAssociation association,
			MNavigableElement source, MNavigableElement destination) {
		// TODO implement derived associations
		throw new TransformationException("Derived association ends are not supported yet.");
	}

	private void handleRegularAssociationNavigation(ExpNavigation exp, IAssociation association, MNavigableElement source,
			MNavigableElement destination) {
		boolean isAssociationClass = association.associationClass() != null;

		int fromRole = findAssociationEndIndex(source, association, false);
		int toRole = findAssociationEndIndex(destination, association, true);

		if (fromRole == -1) {
			fromRole = associationClassEnd(source, association, fromRole, true);
		} else if (toRole == -1) {
			toRole = associationClassEnd(destination, association, fromRole, false);
		}

		object_type_nav = !set;
		sourceType = exp.type();

		if (fromRole == -1 || toRole == -1 || fromRole == toRole) {
			throw new TransformationException("Cannot find correct associationEnd indexes for navigation from " + source.nameAsRolename()
					+ " to " + destination.nameAsRolename() + ".");
		}

		List<Object> arguments = new ArrayList<Object>();
		arguments.add(object);
		arguments.add(association.relation());
		arguments.add(fromRole);
		arguments.add(toRole);
		arguments.add(isAssociationClass);
		arguments.add(object_type_nav);

		invokeMethod("navigation", arguments, false);
	}

	@Override
	public void visitNavigationClassifierSource(ExpNavigationClassifierSource exp) {
		
		exp.getObjectExpression().processWithVisitor(this);
		
		List<Object> arguments = new ArrayList<Object>();
		
		Type t = exp.getObjectExpression().type();
		
		if(t.isKindOfAssociation(VoidHandling.EXCLUDE_VOID)){
			IAssociation assoc = model.getAssociation(((MAssociation) t).name());
			boolean isAssociationClass = assoc.associationClass() != null;
			
			int toRole = findAssociationEndIndex(exp.getDestination(), assoc, true);
			
			arguments.add(object);
			arguments.add(assoc.relation());
			arguments.add(toRole);
			arguments.add(isAssociationClass);
			
			invokeMethod("navigationClassifier", arguments, false);
		}
		else {
			throw new TransformationException("Cannot handle navigation on type " + StringUtil.inQuotes(t) + ".");
		}
	}

	/**
	 * Handle the navigation to an association class end.
	 * 
	 * @param source
	 * @param association
	 * @param fromIndex
	 * @param fromRole
	 * @return
	 */
	private int associationClassEnd(MNavigableElement source, IAssociation association, int fromIndex, boolean fromRole) {
		if (source.nameAsRolename().toLowerCase().equals(association.name().toLowerCase())) {
			if (fromRole) {
				set = false;
			} else {
				if (!association.isBinaryAssociation()) {
					set = true;
				} else {
					int toIndex = fromIndex == 1 ? 1 : 0;
					IAssociationEnd associationEnd = association.associationEnds().get(toIndex);
					set = !associationEnd.multiplicity().isObjectTypeEnd();
				}
				attributeClass = association.associationClass();
			}
			return 0;
		}
		return -1;
	}

	/**
	 * Search the index of the association end.
	 * 
	 * @param source
	 * @param association
	 * @param toRole
	 * @return
	 */
	private int findAssociationEndIndex(MNavigableElement source, IAssociation association, boolean toRole) {
		List<IAssociationEnd> associationEnds = association.associationEnds();
		IAssociationEnd associationEnd;
		for (int i = 0; i < associationEnds.size(); i++) {
			associationEnd = associationEnds.get(i);
			if (associationEnd.name().equals(source.nameAsRolename())) {
				if (toRole) {
					if (!association.isBinaryAssociation()) {
						set = true;
					} else {
						set = !associationEnd.multiplicity().isObjectTypeEnd();
					}
					attributeClass = associationEnd.associatedClass();
				}

				return i + 1;
			}
		}

		return -1;
	}

	@Override
	public void visitVariable(ExpVariable exp) {
		/*
		 * if (variables.containsKey(exp.getVarname())) { object =
		 * variables.get(exp.getVarname()); if
		 * (collectionVariables.contains(exp.getVarname())) { set = true; }
		 * getAttributeClass(exp.getVarname()); } else if
		 * (replaceVariables.containsKey(exp.getVarname())) { object =
		 * replaceVariables.get(exp.getVarname()).remove(0);
		 * getAttributeClass(exp.getVarname()); } else if
		 * (exp.type().isObjectType()) { IClass clazz =
		 * model.getClass(exp.type().shortName()); TypeLiterals type =
		 * clazz.objectType(); type.addTypeLiteral(exp.getVarname()); object =
		 * type.getTypeLiteral(exp.getVarname()); attributeClass = clazz; } else
		 * { throw new TransformationException("No variable " + exp.getVarname()
		 * + "."); }
		 */

		/*
		 * approach due to the description in the method createVariables in the
		 * class QueryExpressionVisitor. For this replacement it is necessary to
		 * use check the replaceVariables before the normal variables.
		 */
		sourceType = exp.type();
		
		if (replaceVariables.containsKey(exp.getVarname())) {
			object = replaceVariables.get(exp.getVarname());
			getAttributeClass(replaceVariables.get(exp.getVarname()).name());

		} else if (variables.containsKey(exp.getVarname())) {
			object = variables.get(exp.getVarname());
			if (collectionVariables.contains(exp.getVarname())) {
				set = true;
			}
			getAttributeClass(exp.getVarname());
		} else if (exp.type().isTypeOfClass()) {
			IClass clazz = model.getClass(exp.type().shortName());
			TypeLiterals type = clazz.objectType();
			type.addTypeLiteral(exp.getVarname());
			object = type.getTypeLiteral(exp.getVarname());
			attributeClass = clazz;
		} else {
			throw new TransformationException("No variable " + exp.getVarname() + ".");
		}
	}

	@Override
	public void visitObjOp(ExpObjOp exp) {
		super.visitObjOp(exp);
		MOperation operation = exp.getOperation();
		if (operation.hasResultType() && operation.resultType().isTypeOfClass()) {
			attributeClass = model.getClass(((MClass) operation.resultType()).name());
		}
	}

	private void getAttributeClass(String varName) {
		if (variableClasses.containsKey(varName))
			attributeClass = variableClasses.get(varName);
	}

	public IClass getAttributeClass() {
		return attributeClass;
	}
}
