package org.tzi.use.kodkod.plugin;

import java.lang.ref.WeakReference;

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
import org.tzi.use.main.ChangeEvent;
import org.tzi.use.main.ChangeListener;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MSystem;
import org.tzi.use.uml.sys.events.ClassInvariantsLoadedEvent;
import org.tzi.use.uml.sys.events.ClassInvariantsUnloadedEvent;

import com.google.common.eventbus.Subscribe;

/**
 * Singleton to encapsulate the model transformation.
 * 
 * @author Hendrik Reitmann
 * 
 */
public enum PluginModelFactory implements ChangeListener {

	INSTANCE;

	private WeakReference<Session> session = new WeakReference<Session>(null);
	private WeakReference<MSystem> system = new WeakReference<MSystem>(null);
	private boolean reTransform = true;

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
		if (reTransform) {
			reTransform = false;

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
	
	public void registerForSession(Session s){
		if(session.get() == null || s != session.get()){
			// session has changed since last register
			s.addChangeListener(this);
			session = new WeakReference<Session>(s);
		}
		if(system.get() == null || s.system() != system.get()){
			// system has changed since last register
			s.system().getEventBus().register(this);
			system = new WeakReference<MSystem>(s.system());
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if(((Session) e.getSource()).hasSystem() && ((Session) e.getSource()).system() != system.get()){
			reTransform = true;
		}
	}
	
	@Subscribe
	public void onClassInvariantLoaded(ClassInvariantsLoadedEvent ev){
		reTransform = true;
	}
	
	@Subscribe
	public void onClassInvariantUnloaded(ClassInvariantsUnloadedEvent ev){
		reTransform = true;
	}
	
}
