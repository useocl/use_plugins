package org.tzi.kodkod.helper;

import java.util.ArrayList;
import java.util.List;

import org.tzi.kodkod.model.iface.IClass;
import org.tzi.kodkod.model.iface.IInvariant;
import org.tzi.kodkod.model.iface.IModel;

public class InvariantHelper {

	/**
	 * Returns all invariants of the model.
	 * 
	 * @param model
	 * @return
	 */
	public static List<IInvariant> getAllInvariants(IModel model) {
		List<IInvariant> invariants = new ArrayList<IInvariant>();
		for (IClass clazz : model.classes()) {
			invariants.addAll(clazz.invariants());
		}
		return invariants;
	}
}
