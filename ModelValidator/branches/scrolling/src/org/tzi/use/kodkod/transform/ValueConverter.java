package org.tzi.use.kodkod.transform;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.type.TypeConstants;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.EnumValue;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;

/**
 * Convert a value of the use model in a representing value for the model
 * validator.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class ValueConverter {

	private static final Logger LOG = Logger.getLogger(ValueConverter.class);

	/**
	 * Converts the given value.
	 * 
	 * @param value
	 * @return
	 */
	public Set<String> convert(Value value) {
		Set<String> values = new HashSet<String>();

		if (value.isUndefined()) {
			values.add(TypeConstants.UNDEFINED);
		} else if (value instanceof StringValue) {
			values.add(((StringValue) value).value().replaceAll("'", ""));
		} else if (value.isBoolean() || value.isInteger() || value.isReal()) {
			values.add(value.toString());
		} else if (value instanceof EnumValue) {
			values.add(value.toString().replaceFirst("::", "_"));
		} else if (value.isObject()) {
			ObjectValue objectValue = (ObjectValue) value;
			MObject mObject = objectValue.value();
			values.add(mObject.cls().name() + "_" + mObject.name());
		} else if (value.isCollection()) {
			values.addAll(convertCollection(value));
		}

		return values;
	}

	private Set<String> convertCollection(Value value) {
		CollectionValue collectionValue = (CollectionValue) value;

		Set<String> values = new HashSet<String>();

		for (Value val : collectionValue.collection()) {
			if (val.isCollection()) {
				LOG.error(LogMessages.valueConversionNestedCollections);
			} else {
				values.addAll(convert(val));
			}
		}

		return values;
	}
}
