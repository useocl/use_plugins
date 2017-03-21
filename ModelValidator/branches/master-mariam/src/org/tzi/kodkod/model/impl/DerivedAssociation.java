package org.tzi.kodkod.model.impl;

import org.tzi.kodkod.model.iface.IModel;

import kodkod.ast.Formula;

/**
 * @author Frank Hilken
 */
public class DerivedAssociation extends Association {

	DerivedAssociation(IModel model, String name) {
		super(model, name);
	}

	@Override
	public Formula constraints() {
		return Formula.and(multiplicityDefinitions(), cycleFreenessDefinitions());
	}
	
	@Override
	protected Formula multiplicityDefinitions() {
		// we can ignore association classes
		
//		DerivedAssociationEnd derivedRole = null;
//		int derIdx = -1;
//		
//		List<IAssociationEnd> aEnds = associationEnds();
//		for (int i = 0; i < aEnds.size(); i++) {
//			IAssociationEnd aEnd = aEnds.get(i);
//			if(aEnd instanceof DerivedAssociationEnd){
//				derivedRole = (DerivedAssociationEnd) aEnd;
//				derIdx = i;
//				break;
//			}
//		}
//		if(derivedRole == null){
//			return Formula.TRUE;
//		}
//		
//		Map<String, Node> variables = new TreeMap<String, Node>();
//		Map<String, IClass> variableClasses = new TreeMap<String, IClass>();
//		
//		for (int j = 0; j < associationEnds().size(); j++) {
//			IAssociationEnd aEnd = associationEnds().get(j);
//
//			if(aEnd.multiplicity().isZeroMany()){
//				continue;
//			}
//			
//			// \forAll  ends \in (aEnds - aEnd) | #(derExpr( ends )).conformsTo( multiplicity )
//			
//			for (Parameter parameter : derivedRole.derivedParameters()) {
//				// make a var for this param
//				// add var to decls
//				// add var to variables for DEV
//				if(aEnd instanceof DerivedAssociationEnd){
//					
//				} else {
//				
//				}
//			}
//		}
//		
//		new DefaultExpressionVisitor(model, variables, variableClasses, new HashMap<String, Variable>(), new ArrayList<String>());
		
		//TODO multiplicities
		return Formula.TRUE;
	}
	
	@Override
	protected Formula cycleFreenessDefinitions() {
		//TODO cycle-freeness
		return Formula.TRUE;
	}
	
}
