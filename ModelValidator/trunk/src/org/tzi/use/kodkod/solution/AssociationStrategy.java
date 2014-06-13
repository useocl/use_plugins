package org.tzi.use.kodkod.solution;

import java.util.Map;

import kodkod.instance.Tuple;

import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;

/**
 * Strategy for the creation of links for an assocation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AssociationStrategy extends ElementStrategy {

	private String associationName;
	private MAssociation mAssociation;

	public AssociationStrategy(UseSystemApi sApi, Map<String, MObjectState> objectStates, String associationName) {
		super(sApi, objectStates);
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
	public void createElement(Tuple currentTuple) throws UseApiException {
		MObject[] tupleObjects = new MObject[currentTuple.arity()];
		Object atom;
		for (int i = 0; i < currentTuple.arity(); i++) {
			atom = currentTuple.atom(i);
			if(atom != TypeConstants.UNDEFINED){
				tupleObjects[i] = objectStates.get(currentTuple.atom(i)).object();
			}
			else{
				return;
			}
		}
		
		systemApi.createLinkEx(mAssociation, tupleObjects);

//		if(!mSystemState.hasLinkBetweenObjects(mAssociation, tupleObjects, null)){
//			mSystemState.createLink(mAssociation, tupleObjects, null);
//		}
	}

}
