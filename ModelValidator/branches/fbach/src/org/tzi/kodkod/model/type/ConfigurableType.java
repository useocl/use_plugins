package org.tzi.kodkod.model.type;

import java.util.List;

import org.tzi.kodkod.model.config.ITypeConfigurator;
import org.tzi.kodkod.model.config.impl.TypeConfigurator;
import org.tzi.kodkod.model.iface.IConfigurableElement;

import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * Abstract base class for the configurable types.
 * 
 * @author Hendrik Reitmann
 * 
 */
public abstract class ConfigurableType extends TypeLiterals implements IConfigurableElement {

	protected ITypeConfigurator<ConfigurableType> configurator;

	ConfigurableType(String name) {
		super(name);
		resetConfigurator();
	}

	@Override
	public List<Object> atoms() {
		return createAtomList();
	}

	@Override
	public TupleSet lowerBound(TupleFactory tupleFactory) {
		return configurator.lowerBound(this, 1, tupleFactory);
	}

	@Override
	public TupleSet upperBound(TupleFactory tupleFactory) {
		return configurator.upperBound(this, 1, tupleFactory);
	}

	/**
	 * Sets a configurator for the type.
	 * 
	 * @param configurator
	 */
	public void setConfigurator(ITypeConfigurator<ConfigurableType> configurator) {
		this.configurator = configurator;
	}

	/**
	 * Returns the configurator of the type.
	 * 
	 * @return
	 */
	public ITypeConfigurator<ConfigurableType> getConfigurator() {
		return configurator;
	}

	@Override
	public void resetConfigurator() {
		configurator = new TypeConfigurator();
	}
}
