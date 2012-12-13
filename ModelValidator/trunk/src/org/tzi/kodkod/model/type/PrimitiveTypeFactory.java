package org.tzi.kodkod.model.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;

/**
 * Implementation of TypeFactory.
 * 
 * @author Hendrik Reitmann
 */
public class PrimitiveTypeFactory extends TypeFactory {

	private static final Logger LOG = Logger.getLogger(PrimitiveTypeFactory.class);

	@Override
	protected Map<String, Type> createBuildInTypes() {
		Map<String, Type> buildInTypes = new HashMap<String, Type>();
		buildInTypes.put(TypeConstants.UNDEFINED, new UndefinedType());
		buildInTypes.put(TypeConstants.UNDEFINED_SET, new UndefinedSetType());
		buildInTypes.put(TypeConstants.ANY, new AnyType());

		IntegerType integerType = new IntegerType();

		buildInTypes.put(TypeConstants.INTEGER, integerType);
		buildInTypes.put(TypeConstants.REAL, new RealType());
		buildInTypes.put(TypeConstants.STRING, new StringType(integerType));
		buildInTypes.put(TypeConstants.BOOLEAN, new BooleanType());
		return buildInTypes;
	}

	@Override
	public TypeLiterals enumType(String name, List<String> literals) {
		return new EnumType(name, literals);
	}

	@Override
	public Type setType(Type elemType) {
		return new SetType(elemType);
	}

	@Override
	public Type bagType(Type elemType) {
		LOG.warn(LogMessages.unsupportedCollectionWarning("bags"));
		return setType(elemType);
	}

	@Override
	public Type orderedSetType(Type elemType) {
		LOG.warn(LogMessages.unsupportedCollectionWarning("orderedSets"));
		return setType(elemType);
	}

	@Override
	public Type sequenceType(Type elemType) {
		LOG.warn(LogMessages.unsupportedCollectionWarning("sequences"));
		return setType(elemType);
	}
}
