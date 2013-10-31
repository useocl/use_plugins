package org.tzi.use.kodkod.solution;

import java.util.Map;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.type.BagType;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.OrderedSetType;
import org.tzi.use.uml.ocl.type.SequenceType;
import org.tzi.use.uml.ocl.type.SetType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.BagValue;
import org.tzi.use.uml.ocl.value.BooleanValue;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.IntegerValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.OrderedSetValue;
import org.tzi.use.uml.ocl.value.RealValue;
import org.tzi.use.uml.ocl.value.SequenceValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.UndefinedValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;

/**
 * Class to create the representing values in use for the atoms of the solution.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ValueCreator {

	private static final Logger LOG = Logger.getLogger(ValueCreator.class);

	private MModel mModel;
	private MAttribute mAttribute;
	private Map<String, MObjectState> objectStates;
	private MObjectState mObjectState;

	public ValueCreator(MModel mModel, Map<String, MObjectState> objectStates, MAttribute mAttribute, MObjectState mObjectState) {
		this.mModel = mModel;
		this.mAttribute = mAttribute;
		this.objectStates = objectStates;
		this.mObjectState = mObjectState;
	}

	public Value create(Type attributeType, Object atom) {
		if (attributeType.isCollection(false)) {
			return createCollectionValue(atom, attributeType);
		} else {
			return createValue(attributeType, atom);
		}
	}

	private Value createValue(Type attributeType, Object atom) {
		if (atom.equals(TypeConstants.UNDEFINED)) {
			return createUndefinedValue();
		} else if (attributeType.isInteger()) {
			return createIntegerValue(atom);
		} else if (attributeType.isBoolean()) {
			return createBooleanValue(atom);
		} else if (attributeType.isString()) {
			return createStringValue(atom);
		} else if (attributeType.isReal()) {
			return createRealValue(atom);
		} else if (attributeType.isObjectType()) {
			return createObjectValue(atom);
		} else if (attributeType.isEnum()) {
			return createEnumValue(atom);
		}

		return null;
	}

	private Value createCollectionValue(Object atom, Type attributeType) {
		if (atom.equals(TypeConstants.UNDEFINED_SET)) {
			return createUndefinedValue();
		} else if (attributeType.isSet()) {
			return createSetValue(atom, (SetType) attributeType);
		} else if (attributeType.isBag()) {
			return createBagValue(atom, (BagType) attributeType);
		} else if (attributeType.isSequence()) {
			return createSequenceValue(atom, (SequenceType) attributeType);
		} else if (attributeType.isOrderedSet()) {
			return createOrderedSetValue(atom, (OrderedSetType) attributeType);
		}

		return null;
	}

	private Value createUndefinedValue() {
		return UndefinedValue.instance;
	}

	private Value createEnumValue(Object atom) {
		String[] valueSplit = ((String) atom).split("_");

		EnumType enumType = mModel.enumType(valueSplit[0]);
		if (enumType != null) {
			return new EnumValue(enumType, valueSplit[1]);
		} else {
			LOG.error(LogMessages.noEnumTypeError(valueSplit[0]));
			return null;
		}
	}

	private Value createObjectValue(Object atom) {
		MObjectState o = objectStates.get(atom);
		MObject mObject = o.object();
		return new ObjectValue(mObject.type(), mObject);
	}

	private Value createRealValue(Object atom) {
		String value = (String) atom;
		value = value.split("_", 2)[1];
		return new RealValue(Double.parseDouble(value));
	}

	private Value createStringValue(Object atom) {
		String value = (String) atom;
		value = value.split("_", 2)[1];
		return new StringValue(value);
	}

	private Value createIntegerValue(Object atom) {
		Integer value = (Integer) atom;
		return IntegerValue.valueOf(value);
	}

	private BooleanValue createBooleanValue(Object atom) {
		return BooleanValue.get(new Boolean((String) atom));
	}

	private Value createSetValue(Object atom, SetType attributeType) {
		SetValue setValue;
		if (mObjectState.attributeValue(mAttribute).isUndefined()) {
			setValue = new SetValue(attributeType.elemType());
		} else {
			setValue = (SetValue) mObjectState.attributeValue(mAttribute);
		}
		Value val = createValue(attributeType.elemType(), atom);
		setValue = setValue.including(val);

		return setValue;
	}

	private Value createSequenceValue(Object atom, SequenceType attributeType) {
		SequenceValue sequenceValue;
		if (mObjectState.attributeValue(mAttribute).isUndefined()) {
			sequenceValue = new SequenceValue(attributeType.elemType());
		} else {
			sequenceValue = (SequenceValue) mObjectState.attributeValue(mAttribute);
		}
		Value val = createValue(attributeType.elemType(), atom);
		sequenceValue = sequenceValue.append(val);

		return sequenceValue;
	}

	private Value createOrderedSetValue(Object atom, OrderedSetType attributeType) {
		OrderedSetValue orderedSetValue;
		if (mObjectState.attributeValue(mAttribute).isUndefined()) {
			orderedSetValue = new OrderedSetValue(attributeType.elemType());
		} else {
			orderedSetValue = (OrderedSetValue) mObjectState.attributeValue(mAttribute);
		}
		Value val = createValue(attributeType.elemType(), atom);
		orderedSetValue = orderedSetValue.append(val);

		return orderedSetValue;
	}

	private Value createBagValue(Object atom, BagType attributeType) {
		BagValue bagValue;
		if (mObjectState.attributeValue(mAttribute).isUndefined()) {
			bagValue = new BagValue(attributeType.elemType());
		} else {
			bagValue = (BagValue) mObjectState.attributeValue(mAttribute);
		}
		Value val = createValue(attributeType.elemType(), atom);
		bagValue = bagValue.including(val);

		return bagValue;
	}
}
