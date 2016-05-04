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
	private IModelFactory modelFactory;

	private PluginModelFactory() {
		setModelFactory(new SimpleFactory());
	}

	/**
	 * Returns the representing model for the given use model.
	 */
	public IModel getModel(MModel mModel) {
		if (reTransform) {
			reTransform = false;

			TypeFactory tf = new PrimitiveTypeFactory();
			registerDefaultOperationGroups(tf);
			ModelTransformator transformator = new ModelTransformator(modelFactory, tf);
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
	 * Registers the default operation groups with the different translation
	 * methods.
	 */
	private void registerDefaultOperationGroups(TypeFactory tf) {
		OCLGroupRegistry registry = OCLGroupRegistry.INSTANCE;
		registry.unregisterAll();
		registry.registerOperationGroup(new VariableOperationGroup(tf));
		registry.registerOperationGroup(new IntegerOperationGroup(tf));
		registry.registerOperationGroup(new BooleanOperationGroup(tf));
		registry.registerOperationGroup(new ClassOperationGroup(tf));
		registry.registerOperationGroup(new AnyOperationGroup(tf, true));
		registry.registerOperationGroup(new ConditionalOperationGroup(tf));
		registry.registerOperationGroup(new SetOperationGroup(tf));
		registry.registerOperationGroup(new CollectionConstructorGroup(tf));
	}
	
	public void registerForSession(Session s){
		if(session.get() == null || s != session.get()){
			// session has changed since last register
			if(session.get() != null){
				session.get().removeChangeListener(this);
			}
			s.addChangeListener(this);
			session = new WeakReference<Session>(s);
		}
		if(system.get() == null || s.system() != system.get()){
			// system has changed since last register
			if(system.get() != null){
				system.get().getEventBus().unregister(this); 
			}
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
