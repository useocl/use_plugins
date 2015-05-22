package org.tzi.kodkod.model.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import kodkod.ast.Formula;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.IConfigurator;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.iface.IAssociation;
import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelFactory;
import org.tzi.kodkod.model.type.EnumType;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.model.visitor.ResetVisitor;
import org.tzi.kodkod.model.visitor.Visitor;

/**
 * Implementation of IModel.
 * 
 * @author Hendrik Reitmann
 * 
 */
public final class Model implements IModel {
	
	private static final Logger LOG = Logger.getLogger(Model.class);
	
	private final String name;
    private Map<String, EnumType> enums;
    private Map<String, IClass> classes;
    private Map<String, IAssociation> associations;
    private IModelFactory modelFactory;
    private TypeFactory typeFactory;
    private IConfigurator<IModel> configurator;

    Model(String name, IModelFactory modelFactory,TypeFactory typeFactory){
    	this.name=name;
    	this.modelFactory=modelFactory;
    	this.typeFactory=typeFactory;
    	enums=new TreeMap<String, EnumType>();
    	classes=new TreeMap<String, IClass>();
    	associations=new TreeMap<String, IAssociation>();
    	resetConfigurator();
    }

	@Override
	public String name() {
		return name;
	}
	
	@Override
	public EnumType getEnumType(String name) {
		return enums.get(name);
	}

	@Override
	public void addEnumType(EnumType enumType) {
		enums.put(enumType.name(), enumType);
	}

	@Override
	public void addClass(IClass clazz) {
		classes.put(clazz.name(), clazz);
	}

	@Override
	public IClass getClass(String name) {
		return classes.get(name);
	}

	@Override
	public void addAssociation(IAssociation association) {
		associations.put(association.name(), association);
	}

	@Override
	public IAssociation getAssociation(String name) {
		return associations.get(name);
	}

	@Override
	public Collection<EnumType> enumTypes() {
		return enums.values();
	}

	@Override
	public Collection<IClass> classes() {
		return classes.values();
	}

	@Override
	public Collection<IAssociation> associations() {
		return associations.values();
	}

	@Override
	public Collection<IInvariant> classInvariants() {
		LinkedList<IInvariant> invariants = new LinkedList<IInvariant>();
		for(IClass cls : classes()){
			invariants.addAll(cls.invariants());
		}
		return invariants;
	}
	
	@Override
	public TypeFactory typeFactory() {
		return typeFactory;
	}
	
	@Override
	public Formula constraints() {
		return configurator.constraints(this);
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitModel(this);
	}

	@Override
	public void reset() {
		accept(new ResetVisitor());
		LOG.info(LogMessages.modelResetSuccessful);
	}

	@Override
	public void setConfigurator(IConfigurator<IModel> configurator) {
		this.configurator = configurator;
	}

	@Override
	public IConfigurator<IModel> getConfigurator() {
		return configurator;
	}
	
	@Override
	public void resetConfigurator() {
		configurator = new ModelConfigurator(this);
	}

	@Override
	public IModelFactory modelFactory() {
		return modelFactory;
	}
}
