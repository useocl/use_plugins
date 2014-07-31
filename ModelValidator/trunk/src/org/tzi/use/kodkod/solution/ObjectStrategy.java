package org.tzi.use.kodkod.solution;

import java.util.Map;

import kodkod.instance.Tuple;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;

/**
 * Strategy for the creation of objects.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ObjectStrategy extends ElementStrategy {

	private String className;
	private MClass mClass;

	public ObjectStrategy(UseSystemApi systemApi, Map<String, MObjectState> objectStates, String className) {
		super(systemApi, objectStates);
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
	public void createElement(Tuple currentTuple) throws UseApiException {
		String className = mClass.name();
		String atom = (String) currentTuple.atom(0);
		String objectName = atom.replaceFirst(className + "_", "");

		MObject mObject;
		try {
			//TODO is this a wrong implementation for ADE mode, but system state is reset anyways? Artifact?
			mObject = systemApi.getObjectSafe(objectName);
		}
		catch(UseApiException ex){
			mObject = systemApi.createObject(className, objectName);
		}

		MObjectState mObjectState = mObject.state(systemApi.getSystem().state());
		objectStates.put(atom, mObjectState);
	}
}
