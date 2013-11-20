package org.tzi.use.kodkod.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;

import kodkod.instance.Tuple;

/**
 * Strategy for the creation of links for an assocation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AssociationStrategy extends ElementStrategy {

	private String associationName;
	private MAssociation mAssociation;

	public AssociationStrategy(MSystemState mSystemState, MModel mModel, Map<String, MObjectState> objectStates, String associationName) {
		super(mSystemState, mModel, objectStates);
		this.associationName = associationName;
	}

	@Override
	public boolean canDo() {
		mAssociation = mModel.getAssociation(associationName);
		if (mAssociation != null) {
			return true;
		}
		return false;
	}

	@Override
	public void createElement(Tuple currentTuple) throws MSystemException {
		List<MObject> tupleObjects = new ArrayList<MObject>();
		Object atom;
		for (int i = 0; i < currentTuple.arity(); i++) {
			atom=currentTuple.atom(i);
			if(atom != TypeConstants.UNDEFINED){
				tupleObjects.add(objectStates.get(currentTuple.atom(i)).object());
			}
			else{
				return;
			}
		}

		if(!mSystemState.hasLinkBetweenObjects(mAssociation, tupleObjects, null)){
			mSystemState.createLink(mAssociation, tupleObjects, null);
		}
	}

}
