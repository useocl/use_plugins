package org.tzi.use.kodkod.solution;

import java.util.Map;

import kodkod.instance.Tuple;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObjectState;

/**
 * Strategy for the creation of attributes.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AttributeStrategy extends ElementStrategy {

	private String attributeName;

	public AttributeStrategy(UseSystemApi sApi, Map<String, MObjectState> objectStates, String attributeName) {
		super(sApi, objectStates);
		this.attributeName = attributeName;
	}
	
	@Override
	public void createElement(Tuple currentTuple) throws UseApiException {
		//TODO collection typed attributes are not supported 100%
		
		MObjectState mObjectState = objectStates.get(currentTuple.atom(0));

		MAttribute mAttribute = findAttribute(mObjectState);

		if (mAttribute != null && !mAttribute.isDerived()) {
			Object atom = currentTuple.atom(1);
			Type attributeType = mAttribute.type();

			ValueCreator valueCreator = new ValueCreator(mModel, objectStates, mAttribute, mObjectState);
			Value newVal = valueCreator.create(attributeType, atom);

			if (newVal != null) {
				systemApi.setAttributeValueEx(mObjectState.object(), mAttribute, newVal);
			}
		}
	}

	private MAttribute findAttribute(MObjectState mObjectState) {
		if (mObjectState != null) {
			for (MAttribute mAttribute : mObjectState.attributeValueMap().keySet()) {
				if (mAttribute.name().equals(attributeName)) {
					return mAttribute;
				}
			}
		}
		return null;
	}
}
