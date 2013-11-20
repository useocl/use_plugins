package org.tzi.use.kodkod.transform;

import kodkod.ast.Expression;
import kodkod.ast.Relation;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.use.uml.ocl.type.CollectionType;

/**
 * Convert a type of the use model in a representing type of the model
 * validator.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class TypeConverter {

	private static final Logger LOG = Logger.getLogger(TypeConverter.class);

	private IModel model;
	private TypeFactory typeFactory;

	public TypeConverter(IModel model) {
		this.model = model;
		typeFactory = model.typeFactory();
	}

	/**
	 * Returns the kodkod expression for the given type.
	 * 
	 * @param type
	 * @return
	 */
	public Expression convertToExpression(org.tzi.use.uml.ocl.type.Type type) {
		Type t = convert(type);
		if (t.isSet()) {
			Expression typeExpression = t.expression();

			if (typeExpression != null) {
				Relation undefined = typeFactory.undefinedType().relation();
				Relation undefined_Set = typeFactory.undefinedSetType().relation();
				return typeExpression.union(undefined).union(undefined_Set);
			}
		}
		return t.expression();
	}

	/**
	 * Converts the given type to a type of the model validator.
	 * 
	 * @param type
	 * @return
	 */
	public Type convert(org.tzi.use.uml.ocl.type.Type type) {
		if (type.isVoidType()) {
			return typeFactory.undefinedType();
		} else if (type.isString() || type.isBoolean() || type.isInteger() || type.isReal()) {
			return typeFactory.buildInType(type.shortName());
		} else if (type.isEnum()) {
			return model.getEnumType(type.shortName());
		} else if (type.isObjectType()) {
			return typeFactory.objectType(model.getClass(type.shortName()));
		} else if (type.isTrueOclAny()) {
			return typeFactory.anyType();
		} else if (type.isCollection(true)) {
			return convertCollection(type);
		}

		LOG.error(LogMessages.typeConvertError(type.shortName()));

		return null;
	}

	private Type convertCollection(org.tzi.use.uml.ocl.type.Type type) {
		CollectionType collectionType = (CollectionType) type;
		Type elemType = convert(collectionType.elemType());

		if (elemType != null) {
			if (type.isSet()) {
				return typeFactory.setType(elemType);
			} else if (type.isBag()) {
				return typeFactory.bagType(elemType);
			} else if (type.isSequence()) {
				return typeFactory.sequenceType(elemType);
			} else if (type.isOrderedSet()) {
				return typeFactory.orderedSetType(elemType);
			}
		}

		return null;
	}
}
