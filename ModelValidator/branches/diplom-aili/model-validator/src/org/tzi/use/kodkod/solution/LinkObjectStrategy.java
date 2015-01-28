package org.tzi.use.kodkod.solution;

import java.util.Map;

import kodkod.instance.Tuple;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.sys.MLinkObject;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;

/**
 * Strategy for the creation of links of an association class.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class LinkObjectStrategy extends ElementStrategy {

	private MAssociationClass mAssociationClass;

	public LinkObjectStrategy(UseSystemApi sApi, Map<String, MObjectState> objectStates,
			MAssociationClass mAssociationClass) {
		super(sApi, objectStates);
		this.mAssociationClass = mAssociationClass;
	}

	@Override
	public void createElement(Tuple currentTuple) throws UseApiException {
		String objectNameAtom = (String) currentTuple.atom(0);
		String objectName = objectNameAtom.replaceFirst(mAssociationClass.name() + "_", "");

		MObject[] tupleObjects = new MObject[currentTuple.arity()-1];
		for (int i = 1; i < currentTuple.arity(); i++) {
			tupleObjects[i-1] = objectStates.get(currentTuple.atom(i)).object();
		}

		MLinkObject mLinkObject;
		try {
			mLinkObject = (MLinkObject) systemApi.getObjectSafe(objectName);
		}
		catch(UseApiException ex){
			mLinkObject = systemApi.createLinkObjectEx(mAssociationClass, objectName, tupleObjects);
		}

		objectStates.put(objectNameAtom, mLinkObject.state(systemApi.getSystem().state()));
	}

}
