package org.tzi.use.kodkod.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IAssociationClass;
import org.tzi.kodkod.model.iface.IAssociationEnd;
import org.tzi.kodkod.model.iface.IAttribute;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelFactory;
import org.tzi.kodkod.model.impl.DerivedAssociationEnd;
import org.tzi.kodkod.model.impl.DerivedAssociationEnd.Parameter;
import org.tzi.kodkod.model.type.ObjectType;
import org.tzi.kodkod.model.type.Type;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.use.graph.DirectedGraph;
import org.tzi.use.uml.mm.MAggregationKind;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.uml.mm.MGeneralization;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.ocl.expr.VarDecl;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.util.StringUtil;
import org.tzi.use.util.uml.sorting.UseFileOrderComparator;

/**
 * Class to transform the use model in a model of the model validator.
 * 
 * @author Hendrik Reitmann
 * @author Frank Hilken
 */
public class ModelTransformator {

	private static final Logger LOG = Logger.getLogger(ModelTransformator.class);

	private final IModelFactory factory;
	private final TypeFactory typeFactory;

	public ModelTransformator(final IModelFactory factory, final TypeFactory typeFactory) {
		this.factory = factory;
		this.typeFactory = typeFactory;
	}

	public IModel transform(MModel mModel) {
		long startTime = System.currentTimeMillis();

		LOG.info(LogMessages.startModelTransform(mModel.name()));

		IModel model = factory.createModel(mModel.name(), factory, typeFactory);

		try {
			transformEnums(model, mModel.enumTypes());

			List<MAssociationClass> associationClasses = new ArrayList<MAssociationClass>(mModel.getAssociationClassesOnly());

			List<MClass> classes = new ArrayList<MClass>(mModel.classes());
			classes.removeAll(associationClasses);

			List<MAssociation> simpleAssociations = new ArrayList<MAssociation>(mModel.associations());
			simpleAssociations.removeAll(associationClasses);

			Collections.sort(associationClasses, new UseFileOrderComparator());
			Collections.sort(classes, new UseFileOrderComparator());
			Collections.sort(simpleAssociations, new UseFileOrderComparator());
			
			transformClasses(model, classes);

			transformAssociationClasses(model, associationClasses);

			transformGeneralization(model, mModel.generalizationGraph());

			transformAttributes(model, classes);

			transformSimpleAssociations(model, simpleAssociations);

			InvariantTransformator invariantTransformator = new InvariantTransformator(factory, typeFactory);
			List<MClassInvariant> classInvariants = new ArrayList<MClassInvariant>(mModel.classInvariants());
			Collections.sort(classInvariants, new UseFileOrderComparator());
			
			invariantTransformator.transformAndAdd(model, classInvariants);

			LOG.info(LogMessages.modelTransformSuccessful);
			LOG.info(LogMessages.modelTransformTime((System.currentTimeMillis() - startTime)));

		} catch (Exception exception) {
			if (LOG.isDebugEnabled()) {
				LOG.error(LogMessages.modelTransformError, exception);
			} else {
				LOG.error(LogMessages.modelTransformError);
			}
		}

		return model;
	}

	private void transformEnums(IModel model, Set<EnumType> enumTypes) {
		List<String> literals;
		for (EnumType enumType : enumTypes) {
			literals = new ArrayList<String>();
			for (String literal : enumType.getLiterals()) {
				literals.add(literal);
			}
			model.addEnumType((org.tzi.kodkod.model.type.EnumType) typeFactory.enumType(enumType.name(), literals));
		}
	}

	private void transformClasses(IModel model, List<MClass> mClasses) {
		for (MClass mClass : mClasses) {
			if (!mClass.name().startsWith("$")) {
				IClass kClass = factory.createClass(model, mClass.name(), mClass.isAbstract());
				model.addClass(kClass);
			} else {
				LOG.error(LogMessages.className$Error);
			}
		}
	}

	private void transformAttributes(IModel model, List<MClass> mClasses) {
		for (MClass mClass : mClasses) {
			IClass kClass = model.getClass(mClass.name());
			transformClassAttributes(model, kClass, mClass.attributes());
		}
	}

	private void transformClassAttributes(IModel model, IClass clazz, List<MAttribute> attributes) {
		TypeConverter typeConverter = new TypeConverter(model);

		for (MAttribute mAttribute : attributes) {
			Type type = typeConverter.convert(mAttribute.type());
			if (type != null) {
				IAttribute attr;
				if(mAttribute.isDerived()){
					attr = factory.createDerivedAttribute(model, mAttribute.name(), type, clazz, mAttribute.getDeriveExpression());
				} else {
					attr = factory.createAttribute(model, mAttribute.name(), type, clazz);
				}
				clazz.addAttribute(attr);
			}
		}
	}

	private void transformSimpleAssociations(IModel model, List<MAssociation> mAssociations) {
		MultiplicityTransformator multTransformator = new MultiplicityTransformator();
		for (MAssociation mAssociation : mAssociations) {
			model.addAssociation(transformAssociation(multTransformator, model, mAssociation));
		}
	}

	private IAssociation transformAssociation(MultiplicityTransformator multTransformator, IModel model, MAssociation mAssociation) {
		IAssociation association;
		if(mAssociation.isDerived()){
			association = factory.createDerivedAssociation(model, mAssociation.name());
		} else {
			association = factory.createAssociation(model, mAssociation.name());
		}

		for (MAssociationEnd mAssociationEnd : mAssociation.associationEnds()) {
			IAssociationEnd end;
			if(mAssociationEnd.isDerived()){
				DerivedAssociationEnd derivedEnd = (DerivedAssociationEnd) factory.createDerivedAssociationEnd(mAssociationEnd.name(),
						multTransformator.transform(mAssociationEnd.multiplicity()),
						transformAggregationKind(mAssociationEnd.aggregationKind()),
						model.getClass(mAssociationEnd.cls().name()), mAssociationEnd.getDeriveExpression());
				
				TypeConverter tc = new TypeConverter(model);
				List<DerivedAssociationEnd.Parameter> params = new ArrayList<DerivedAssociationEnd.Parameter>();
				for (VarDecl varDecl : mAssociationEnd.getDeriveParamter()) {
					Type convertedType = tc.convert(varDecl.type());
					
					if(!convertedType.isObjectType()){
						// should not be possible in a valid UML/OCL model
						throw new TransformationException("Parameter type of derived association expression is not a class.");
					}
					
					params.add(new Parameter(varDecl.name(), ((ObjectType) convertedType).clazz()));
				}
				derivedEnd.setDerivedParameters(params);
				
				end = derivedEnd;
			} else {
				end = factory.createAssociationEnd(mAssociationEnd.name(),
						multTransformator.transform(mAssociationEnd.multiplicity()),
						transformAggregationKind(mAssociationEnd.aggregationKind()),
						model.getClass(mAssociationEnd.cls().name()));
			}
			
			association.addAssociationEnd(end);
			end.associatedClass().addAssociation(association);
		}
		
		return association;
	}

	private int transformAggregationKind(int useAggregationKind) {
		if(useAggregationKind == MAggregationKind.COMPOSITION){
			return IAssociationEnd.COMPOSITION;
		} else if(useAggregationKind == MAggregationKind.AGGREGATION){
			return IAssociationEnd.AGGREGATION;
		} else {
			return IAssociationEnd.REGULAR;
		}
	}

	private void transformAssociationClasses(IModel model, List<MAssociationClass> mAssociationClasses) {
		MultiplicityTransformator multTransformator = new MultiplicityTransformator();

		for (MAssociationClass mAssociationClass : mAssociationClasses) {
			if(mAssociationClass.isDerived()){
				LOG.error("Derived association classes are not supported. Ignoring " + StringUtil.inQuotes(mAssociationClass.name()) + ".");
				continue;
			}
			
			IAssociationClass associationClass = factory.createAssociationClass(model, mAssociationClass.name(), mAssociationClass.isAbstract());
			transformClassAttributes(model, associationClass, mAssociationClass.attributes());
			model.addClass(associationClass);

			IAssociation association = transformAssociation(multTransformator, model, mAssociationClass);
			association.setAssociationClass(associationClass);

			model.addAssociation(association);
		}
	}

	private void transformGeneralization(IModel model, DirectedGraph<MClassifier, MGeneralization> mGeneralizationGraph) {
		IClass source, target;

		Iterator<MGeneralization> iterator = mGeneralizationGraph.edgeIterator();
		while (iterator.hasNext()) {
			MGeneralization next = iterator.next();
			if (!(next.source() instanceof MClass))  continue;
			
			source = model.getClass(next.source().name());
			target = model.getClass(next.target().name());
			source.addParent(target);
			target.addChild(source);
		}
	}
}
