package org.tzi.use.kodkod.plugin;

import org.apache.log4j.Logger;
import org.tzi.kodkod.KodkodModelValidatorConfiguration;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.UseLogAppender;
import org.tzi.use.kodkod.transform.enrich.ModelEnricher;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MSystem;

/**
 * Abstract base class for the new commands.
 * 
 * @author Hendrik Reitmann
 * 
 */
public abstract class AbstractPlugin {

	protected static final Logger LOG = Logger.getLogger(AbstractPlugin.class);

	protected Session session;
	protected MModel mModel;
	protected MSystem mSystem;

	protected void initialize(Session session) {
		this.session = session;
		mSystem = session.system();
		mModel = mSystem.model();
		PluginModelFactory.INSTANCE.registerForSession(session);
	}

	protected void initialize(Session session, MainWindow mainWindow) {
		initialize(session);
		UseLogAppender.initialize(mainWindow.logWriter());
	}

	/**
	 * Enriches the model with a given object diagram (automatic diagram extraction).
	 */
	protected void enrichModel() {
		ModelEnricher enricher = KodkodModelValidatorConfiguration.INSTANCE.getModelEnricher();
		enricher.enrichModel(mSystem, model());
	}

	protected IModel model() {
		return PluginModelFactory.INSTANCE.getModel(mModel);
	}

}
