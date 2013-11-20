package org.tzi.use.kodkod.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MLinkObject;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;

import kodkod.instance.Tuple;

/**
 * Strategy for the creation of links of an association class.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AssociationClassStrategy extends ElementStrategy {

	private MAssociationClass mAssociationClass;

	public AssociationClassStrategy(MSystemState mSystemState, MModel mModel, Map<String, MObjectState> objectStates,
			MAssociationClass mAssociationClass) {
		super(mSystemState, mModel, objectStates);
		this.mAssociationClass = mAssociationClass;
	}

	@Override
	public void createElement(Tuple currentTuple) throws MSystemException {
		String objectNameAtom = (String) currentTuple.atom(0);
		String objectName = objectNameAtom.replaceFirst(mAssociationClass.name() + "_", "");

		List<MObject> tupleObjects = new ArrayList<MObject>();
		for (int i = 1; i < currentTuple.arity(); i++) {
			tupleObjects.add(objectStates.get(currentTuple.atom(i)).object());
		}

		MLinkObject mLinkObject = null;
		if (!mSystemState.hasObjectWithName(objectName)) {
			mLinkObject = mSystemState.createLinkObject(mAssociationClass, objectName, tupleObjects, null);
		} else {
			mLinkObject = (MLinkObject) mSystemState.objectByName(objectName);
		}

		objectStates.put(objectNameAtom, mLinkObject.state(mSystemState));
	}

}
