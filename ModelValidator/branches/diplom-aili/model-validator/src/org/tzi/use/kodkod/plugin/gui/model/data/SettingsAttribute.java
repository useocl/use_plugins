package org.tzi.use.kodkod.plugin.gui.model.data;

import org.tzi.use.uml.mm.MAttribute;

public class SettingsAttribute extends Settings {

	private final MAttribute attribute;
	private Bounds collectionSize = new Bounds();

	public SettingsAttribute(MAttribute attribute) {
		super();
		this.attribute = attribute;
	}

	public MAttribute getAttribute() {
		return attribute;
	}

	public Bounds getCollectionSize() {
		return collectionSize;
	}

}
