package org.tzi.use.kodkod.solution;

import java.util.Map;

import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;

import kodkod.instance.Tuple;

/**
 * Strategy for the creation of objects.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ObjectStrategy extends ElementStrategy {

	private String className;
	private MClass mClass;

	public ObjectStrategy(MSystemState mSystemState, MModel mModel, Map<String, MObjectState> objectStates, String className) {
		super(mSystemState, mModel, objectStates);
		this.className = className;
	}

	@Override
	public boolean canDo() {
		if (mModel.getClass(className) != null) {
			mClass = mModel.getClass(className);
		} else {
			mClass = mModel.getAssociationClass(className);
		}

		if (className != null) {
			return true;
		}

		return false;
	}

	@Override
	public void createElement(Tuple currentTuple) throws MSystemException {
		String className = mClass.name();
		String atom = (String) currentTuple.atom(0);
		String objectName = atom.replaceFirst(className + "_", "");

		MObject mObject;
		if (!mSystemState.hasObjectWithName(objectName)) {
			mObject = mSystemState.createObject(mClass, objectName);
		} else {
			mObject = mSystemState.objectByName(objectName);
		}

		MObjectState mObjectState = mObject.state(mSystemState);
		objectStates.put(atom, mObjectState);

	}
}
