package org.tzi.use.gui.plugins.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.tzi.use.uml.mm.MMultiplicity;

public class TGeneralization {
	static int currentAssocID = TConstants.MIN_ID_ASSOCIATION;
	
	private final int id;
	
	private TClass firstEndClass;
	private TClass secondEndClass;

	public TGeneralization(TClass firstEndClass, TClass secondEndClass) {
		id = currentAssocID++;
		if (id > TConstants.MAX_ID_ASSOCIATION) {
			// TODO error output or exception
			System.out.println("Too many associations");
		}

		this.firstEndClass = firstEndClass;
		this.secondEndClass = secondEndClass;
	}

	@Override
	public String toString() {
		return "(ID: " + id + ")";
	}

	public int getID() {
		return id;
	}

	public TClass getFirstEndClass() {
		return firstEndClass;
	}

	public TClass getSecondEndClass() {
		return secondEndClass;
	}

	public boolean connenctedTo(TClass cls) {
		if (cls == null) {
			return false;
		}
		if (cls == firstEndClass) {
			return true;
		}
		if (cls == secondEndClass) {
			return true;
		}
		return false;
	}

	public TStatus getCurrentStatus() {
		return TStatus.COMPLETE;
	}
}