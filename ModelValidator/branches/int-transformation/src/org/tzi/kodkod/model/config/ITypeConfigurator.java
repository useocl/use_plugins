package org.tzi.kodkod.model.config;

import java.util.List;
import java.util.Set;

import org.tzi.kodkod.model.type.ConfigurableType;

/**
 * Instances of the type ITypeConfigurator represent configurators for instances
 * of ConfigurableType.
 * 
 * @author Hendrik Reitmann
 * 
 */
public interface ITypeConfigurator<M extends ConfigurableType> extends IConfigurator<M> {

	/**
	 * Returns the atoms for this type.
	 * 
	 * @param m
	 * @param literals
	 * @return
	 */
	public Set<Object> atoms(M m, List<Object> literals);

}
