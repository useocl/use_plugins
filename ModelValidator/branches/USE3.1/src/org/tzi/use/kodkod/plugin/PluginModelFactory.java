package org.tzi.use.kodkod.plugin;

import java.io.File;

import org.tzi.kodkod.model.iface.IModel;
import org.tzi.kodkod.model.iface.IModelFactory;
import org.tzi.kodkod.model.impl.SimpleFactory;
import org.tzi.kodkod.model.type.PrimitiveTypeFactory;
import org.tzi.kodkod.model.type.TypeFactory;
import org.tzi.kodkod.ocl.OCLGroupRegistry;
import org.tzi.kodkod.ocl.operation.AnyOperationGroup;
import org.tzi.kodkod.ocl.operation.BooleanOperationGroup;
import org.tzi.kodkod.ocl.operation.ClassOperationGroup;
import org.tzi.kodkod.ocl.operation.CollectionConstructorGroup;
import org.tzi.kodkod.ocl.operation.ConditionalOperationGroup;
import org.tzi.kodkod.ocl.operation.IntegerOperationGroup;
import org.tzi.kodkod.ocl.operation.SetOperationGroup;
import org.tzi.kodkod.ocl.operation.VariableOperationGroup;
import org.tzi.use.kodkod.transform.ModelTransformator;
import org.tzi.use.uml.mm.MModel;

/**
 * Singleton to encapsulate the model transformation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum PluginModelFactory {

	INSTANCE;

	private String modelStatistics = "";
	private long lastModified = 0;

	private IModel model;
	private TypeFactory typeFactory;
	private IModelFactory modelFactory;

	private PluginModelFactory() {
		setModelFactory(new SimpleFactory());
		setTypeFactory(new PrimitiveTypeFactory());
		registerDefaultOperationGroups();
	}

	/**
	 * Returns the representing model for the given use model.
	 * 
	 * @param mModel
	 * @return
	 */
	public IModel getModel(final MModel mModel) {
		File file = new File(mModel.filename());

		if (!modelStatistics.equals(mModel.getStats()) || lastModified != file.lastModified()) {
			modelStatistics = mModel.getStats();
			lastModified = file.lastModified();

			ModelTransformator transformator = new ModelTransformator(modelFactory, typeFactory);
			model = transformator.transform(mModel);
		}

		return model;
	}

	/**
	 * Sets the model factory.
	 * 
	 * @param modelFactory
	 */
	public void setModelFactory(IModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	/**
	 * Sets the type factory
	 * 
	 * @param typeFactory
	 */
	public void setTypeFactory(TypeFactory typeFactory) {
		this.typeFactory = typeFactory;
	}

	/**
	 * Registers the default operation groups with the different translation
	 * methods.
	 */
	public void registerDefaultOperationGroups() {
		OCLGroupRegistry registry = OCLGroupRegistry.INSTANCE;
		registry.registerOperationGroup(new VariableOperationGroup(typeFactory));
		registry.registerOperationGroup(new IntegerOperationGroup(typeFactory));
		registry.registerOperationGroup(new BooleanOperationGroup(typeFactory));
		registry.registerOperationGroup(new ClassOperationGroup(typeFactory));
		registry.registerOperationGroup(new AnyOperationGroup(typeFactory, true));
		registry.registerOperationGroup(new ConditionalOperationGroup(typeFactory));
		registry.registerOperationGroup(new SetOperationGroup(typeFactory));
		registry.registerOperationGroup(new CollectionConstructorGroup(typeFactory));
	}
}
