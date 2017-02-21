package org.tzi.use.kodkod.transform.enrich;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.type.ConfigurableType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.use.kodkod.transform.ValueConverter;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.ocl.value.CollectionValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MLink;
import org.tzi.use.uml.sys.MLinkObject;
import org.tzi.use.uml.sys.MObject;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.MSystemState;

/**
 * Class to extract an object diagram and enrich the model of the model
 * validator.
 * 
 * @author Hendrik Reitmann
 */
public class ObjectDiagramModelEnricher implements ModelEnricher{

	private static final Logger LOG = Logger.getLogger(ObjectDiagramModelEnricher.class);

	/**
	 * Enrich the given model with the information of the object diagram.
	 */
	@Override
	public void enrichModel(MSystem mSystem,IModel model) {
		try {
			MSystemState systemState = mSystem.state();
			extractObjects(model, systemState);
			extractLinks(model, systemState);
			LOG.info(LogMessages.objDiagramExtractionSuccessful);			
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.error(LogMessages.objDiagramExtractionError);
			} else {
				LOG.error(LogMessages.objDiagramExtractionError + " " + e);
			}
		}
	}

	/**
	 * Extract the links of an object diagram.
	 * 
	 * @param model
	 * @param systemState
	 */
	private void extractLinks(IModel model, MSystemState systemState) {
		for (MLink link : systemState.allLinks()) {
			if(link.association().isDerived()){
				continue;
			}
			List<MObject> linkedObjects = link.linkedObjects();
			int index = 0;

			String[] specificValue;
			if (link instanceof MLinkObject) {
				specificValue = new String[linkedObjects.size() + 1];

				MLinkObject mLinkObject = (MLinkObject) link;
				specificValue[0] = mLinkObject.cls().name() + "_" + mLinkObject.name();

				index = 1;
			} else {
				specificValue = new String[linkedObjects.size()];
			}

			for (MObject linkedObject : linkedObjects) {
				specificValue[index] = linkedObject.cls().name() + "_" + linkedObject.name();
				index++;
			}

			IAssociation association = model.getAssociation(link.association().name());
			association.getConfigurator().addSpecificValue(specificValue);
		}
	}

	/**
	 * Extract all object of the object diagram.
	 */
	private void extractObjects(IModel model, MSystemState systemState) {
		IClass clazz;
		String clazzName;

		for (MObject mObject : systemState.allObjects()) {
			clazzName = mObject.cls().name();
			clazz = model.getClass(clazzName);
			clazz.getConfigurator().addSpecificValue(new String[] { mObject.name() });
			clazz.objectType().addValue(new String[] { mObject.name() });

			extractAttributeValues(model, mObject.state(systemState), clazz, mObject);
		}
	}

	/**
	 * Extraction of the attribute values.
	 */
	private void extractAttributeValues(IModel model, MObjectState objectState, IClass clazz, MObject mObject) {
		TypeFactory typeFactory = model.typeFactory();
		IAttribute attribute;

		for (MAttribute mAttribute : mObject.cls().allAttributes()) {
			if(mAttribute.isDerived()){
				continue;
			}
			attribute = clazz.getAttribute(mAttribute.name());
			Value value = objectState.attributeValue(mAttribute);
			if (!value.isUndefined()) {

				ValueConverter valueConverter = new ValueConverter();
				Set<String> values = valueConverter.convert(value);

				Type type = getType(typeFactory, value);

				for (String stringValue : values) {
					setAttributeValue(mObject.cls().name() + "_" + mObject.name(), attribute, type, stringValue);
					addValueToType(type, stringValue);
				}
			}
		}
	}

	/**
	 * Sets the value for the given attribute.
	 */
	private void setAttributeValue(String name, IAttribute attribute, Type type, String value) {
		if (type != null) {
			attribute.getConfigurator().addSpecificValue(new String[] { name, value });
		}
	}

	/**
	 * Add a specific value to the type definition.
	 */
	private void addValueToType(Type type, String value) {
		if (type != null && type instanceof ConfigurableType) {
			((ConfigurableType) type).getConfigurator().addSpecificValue(new String[] { value });
		}
	}

	private Type getType(TypeFactory typeFactory, Value value) {
		String typeName;
		if (value.isCollection()) {
			typeName = ((CollectionValue) value).elemType().shortName();
		} else {
			typeName = value.type().shortName();
		}
		//TODO attributes can also reference other classes and enums
		Type type = typeFactory.buildInType(typeName);
		return type;
	}

}
