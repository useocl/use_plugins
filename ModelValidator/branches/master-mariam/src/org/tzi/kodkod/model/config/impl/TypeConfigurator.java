package org.tzi.kodkod.model.config.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.tzi.kodkod.model.config.ITypeConfigurator;
import org.tzi.kodkod.model.type.ConfigurableType;

/**
 * Default type configurator.
 * 
 * @author Hendrik Reitmann
 * 
 */
public class TypeConfigurator extends Configurator<ConfigurableType> implements ITypeConfigurator<ConfigurableType> {

	public TypeConfigurator() {
		super();
	}

	@Override
	public Set<Object> atoms(ConfigurableType type, List<Object> literals) {
		return new LinkedHashSet<Object>();
	}

	/**
	 * Returns all specific values of a type.
	 * 
	 * @return
	 */
	protected Collection<String[]> allValues() {
		return specificValues;
	}
}
