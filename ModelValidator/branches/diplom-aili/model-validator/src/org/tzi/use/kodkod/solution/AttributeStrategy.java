package org.tzi.use.kodkod.solution;

import java.util.Map;

import kodkod.instance.Tuple;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemState;

/**
 * Strategy for the creation of attributes.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class AttributeStrategy extends ElementStrategy {

	private String attributeName;

	public AttributeStrategy(MSystemState mSystemState, MModel mModel, Map<String, MObjectState> objectStates, String attributeName) {
		super(mSystemState, mModel, objectStates);
		this.attributeName = attributeName;
	}

	@Override
	public void createElement(Tuple currentTuple) {
		MObjectState mObjectState = objectStates.get(currentTuple.atom(0));

		MAttribute mAttribute = findAttribute(mObjectState);

		if (mAttribute != null) {
			Object atom = currentTuple.atom(1);
			Type attributeType = mAttribute.type();

			ValueCreator valueCreator = new ValueCreator(mModel, objectStates, mAttribute, mObjectState);
			Value newVal = valueCreator.create(attributeType, atom);

			if (newVal != null) {
				mObjectState.setAttributeValue(mAttribute, newVal);
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
