package org.tzi.use.kodkod.transform.enrich;

import org.tzi.kodkod.model.iface.IModel;
import org.tzi.use.uml.sys.MSystem;

public interface ModelEnricher {

	public void enrichModel(MSystem mSystem,IModel model);
}
