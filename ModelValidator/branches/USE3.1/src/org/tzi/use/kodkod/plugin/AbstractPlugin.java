package org.tzi.use.kodkod.plugin;

import java.io.PrintWriter;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.tzi.kodkod.helper.LogMessages;
import org.tzi.kodkod.model.config.impl.ModelConfigurator;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.gen.model.GModel;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.kodkod.UseLogAppender;
import org.tzi.use.kodkod.transform.InvariantTransformator;
import org.tzi.use.kodkod.transform.ObjectDiagramExtractor;
import org.tzi.use.main.Session;
import org.tzi.use.uml.mm.MClassInvariant;
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

	protected MModel mModel;
	protected MSystem mSystem;

	protected void initialize(Session session) {
		mSystem = session.system();
		mModel = mSystem.model();
	}

	protected void initialize(Session session, MainWindow mainWindow) {
		initialize(session);
		UseLogAppender.initialize(mainWindow.logWriter());
	}

	/**
	 * Extracts the object diagram.
	 */
	protected void objDiagramExtraction(PrintWriter out) {
		ObjectDiagramExtractor extractor = new ObjectDiagramExtractor(mSystem);
		extractor.enrichModel(model(out));
	}

	protected IModel model(PrintWriter out) {
		return PluginModelFactory.INSTANCE.getModel(mModel, out);
	}

	/**
	 * Transforms the invariants of the generator and enrich the model.
	 */
	protected void enrichModelWithLoadedInvariants(PrintWriter out) {
		try {
			ModelConfigurator configurator = (ModelConfigurator) model(out).getConfigurator();
			configurator.clear();
			
			GModel gModel=null;
			try {
				gModel = mSystem.generator().gModel();
			} catch (NoSuchMethodError e) {
				out.println(LogMessages.noSuchMethodError + ". " + e);
			}
			
			Collection<MClassInvariant> loadedClassInvariants = gModel.loadedClassInvariants();

			if (loadedClassInvariants.size() > 0) {
				out.println(LogMessages.enrichWithLoadedInvariants);

				InvariantTransformator invariantTransformator = new InvariantTransformator(model(out).modelFactory(), model(out).typeFactory());

				for (MClassInvariant loadedInvariant : loadedClassInvariants) {
					if (!configurator.getInvariants().containsValue(loadedInvariant.name())) {

						IInvariant invariant = invariantTransformator.transform(model(out), loadedInvariant);
						configurator.addInvariant(invariant);
					}
				}
			}
		} catch (Exception e) {
			out.println(LogMessages.errorWithLoadedInvariants);
			if (LOG.isDebugEnabled()) {
				e.printStackTrace();
			}
		}
	}
}
