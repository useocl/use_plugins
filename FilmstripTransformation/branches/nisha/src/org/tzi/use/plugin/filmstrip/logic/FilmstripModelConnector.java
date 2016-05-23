package org.tzi.use.plugin.filmstrip.logic;

import java.util.Map;

import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MNavigableElement;
import org.tzi.use.uml.mm.MOperation;
import org.tzi.use.uml.ocl.type.BagType;
import org.tzi.use.uml.ocl.type.BasicType;
import org.tzi.use.uml.ocl.type.CollectionType;
import org.tzi.use.uml.ocl.type.EnumType;
import org.tzi.use.uml.ocl.type.OrderedSetType;
import org.tzi.use.uml.ocl.type.SequenceType;
import org.tzi.use.uml.ocl.type.SetType;
import org.tzi.use.uml.ocl.type.TupleType;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.type.TypeFactory;
import org.tzi.use.util.StringUtil;

public class FilmstripModelConnector implements MModelConnector {
	
	private MModel model;
	
	public FilmstripModelConnector(MModel targetModel){
		model = targetModel;
	}
	
	/**
	 * Returns the class of the new model that corresponds to the given class.
	 * This methods works regardless of the model the given class comes from.
	 * 
	 * @param cls class to find counterpart of
	 * @return counterpart of given class
	 */
	@Override
	public MClass mapClass(MClass cls){
		MClass c = model.getClass(cls.name());
		
		if(c == null){
			throw new TransformationException(
					"Could not find corresponding class for "
							+ StringUtil.inQuotes(cls.name()) + " in model.");
		}
		
		return c;
	}
	
	@Override
	public MAssociationClass mapAssociationClass(MAssociationClass cls) {
		MAssociationClass c = model.getAssociationClass(cls.name());
		
		if(c == null){
			throw new TransformationException(
					"Could not find corresponding association class for "
							+ StringUtil.inQuotes(cls.name()) + " in model.");
		}
		
		return c;
	}
	
	@Override
	public MAttribute mapAttribute(MAttribute attr) {
		MAttribute a = mapClass(attr.owner()).attribute(attr.name(), false);
		
		if(a == null){
			throw new TransformationException(
					"Could not find corresponding attribute for "
							+ StringUtil.inQuotes(attr.name()) + " of class "
							+ StringUtil.inQuotes(attr.owner())
							+ " in new model.");
		}
		
		return a;
	}
	
	@Override
	public MOperation mapOperation(MOperation operation) {
		MOperation op = mapClass(operation.cls()).operation(operation.name(), false);
		
		if(op == null){
			throw new TransformationException(
					"Could not find corresponding operation for "
							+ StringUtil.inQuotes(operation.name())
							+ " of class "
							+ StringUtil.inQuotes(operation.cls().name())
							+ " in new model.");
		}
		
		return op;
	}
	
	@Override
	public MAssociation mapAssociation(MAssociation assoc) {
		MAssociation a = model.getAssociation(assoc.name());
		
		if(a == null){
			throw new TransformationException("Could not find corresponding association for " + StringUtil.inQuotes(assoc.name()) + " in model.");
		}
		
		return a;
	}
	
	@Override
	public MNavigableElement mapNavigableElement(MNavigableElement source) {
		if(source instanceof MAssociationClass){
			return (MAssociationClass) mapClass(source.cls());
		}
		else if(source instanceof MAssociationEnd){
			MAssociationEnd assocEnd = (MAssociationEnd) source;
			MAssociation assoc = model.getAssociation(assocEnd.association().name());
			if(assoc == null){
				throw new TransformationException("Association "
						+ StringUtil.inQuotes(assocEnd.association().name())
						+ " not found in new model.");
			}
			MAssociationEnd res = assoc.getAssociationEnd(mapClass(source.cls()), assocEnd.name());
			if(res != null){
				return res;
			}
			throw new TransformationException("Association end "
					+ StringUtil.inQuotes(assocEnd.name())
					+ " of association "
					+ StringUtil.inQuotes(assocEnd.association().name())
					+ " not found in new model.");
		}
		throw new TransformationException("Encountered unknown model element for MNavigableElement.");
	}
	
	@Override
	public MAssociationEnd mapAssociationEnd(MAssociationEnd end) {
		MAssociation assoc = model.getAssociation(end.association().name());
		if(assoc != null){
			for(MAssociationEnd newEnd : assoc.associationEnds()){
				if(newEnd.name().equals(end.name())){
					return newEnd;
				}
			}
		}
		throw new TransformationException(
				"Could not find corresponding association end for "
						+ StringUtil.inQuotes(end.name()) + " of association "
						+ StringUtil.inQuotes(end.association().name())
						+ " in model.");
	}
	
	@Override
	public Type mapType(Type t){
		if(t.isTypeOfVoidType()){
			return TypeFactory.mkVoidType();
		}
		else if(t instanceof BasicType){
			// basictypes are immutable
			return t;
		}
		else if(t.isTypeOfOclAny()){
			return TypeFactory.mkOclAny();
		}
		else if(t.isTypeOfEnum()){
			EnumType et = (EnumType) t;
			return TypeFactory.mkEnum(et.name(), et.getLiterals());
		}
		else if(t.isTypeOfClass()){
			return mapClass((MClass) t);
		}
		else if(t.isTypeOfBag()){
			return TypeFactory.mkBag(mapType(((BagType) t).elemType()));
		}
		else if(t.isTypeOfSequence()){
			return TypeFactory.mkSequence(mapType(((SequenceType) t).elemType()));
		}
		else if(t.isTypeOfSet()){
			return TypeFactory.mkSet(mapType(((SetType) t).elemType()));
		}
		else if(t.isTypeOfOrderedSet()){
			return TypeFactory.mkOrderedSet(mapType(((OrderedSetType) t).elemType()));
		}
		else if(t.isTypeOfCollection()){
			return TypeFactory.mkCollection(mapType(((CollectionType) t).elemType()));
		}
		else if(t.isTypeOfTupleType()){
			TupleType tt = (TupleType) t;
			
			TupleType.Part[] parts = new TupleType.Part[tt.getParts().size()];
			int i = 0;
			for (Map.Entry<String, TupleType.Part> entry : tt.getParts().entrySet()) {
				parts[i] = new TupleType.Part(entry.getValue().getPosition(), entry.getValue().name(), mapType(entry.getValue().type()));
				i++;
			}
			
			return TypeFactory.mkTuple(parts);
		}
		else {
			throw new TransformationException("Unkown Type " + StringUtil.inQuotes(t.toString()) + ".");
		}
	}
	
}
