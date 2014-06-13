/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2010 Mark Richters, University of Bremen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.tzi.use.uml.mm.commonbehavior.communications;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.tzi.use.uml.mm.MAttribute;
import org.tzi.use.uml.mm.MInvalidModelException;
import org.tzi.use.uml.mm.MMVisitor;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.MModelElementImpl;
import org.tzi.use.uml.ocl.type.MessageType;
import org.tzi.use.util.StringUtil;

/**
 * Meta class for signals
 * UML-SS 2.4.1 p. 465 
 * @author Lars Hamann
 */
public class MSignalImpl extends MModelElementImpl implements MSignal {

	private boolean isAbstract;
	
	/**
	 * To get access to the inheritance hierarchy.
	 */
	private MModel model;

	private int position;
	
	/**
	 * UML 2.4.1 p. 466
	 * The attributes owned by the signal. (Subsets Classifier::attribute, Namespace::ownedMember). 
	 * This association end is ordered.
	 */
	private Map<String,MAttribute> ownedAttribute = new HashMap<>();
	
	/**
	 * Constructs a new signal with the given <code>name</code>.
	 * @param name
	 */
	public MSignalImpl(String name, boolean isAbstract) {
		super(name);
		this.isAbstract = isAbstract;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	/**
	 * @return the model
	 */
	public MModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(MModel model) {
		this.model = model;
	}

	public void addAttribute(MAttribute attr) throws MInvalidModelException {		
		for (MSignal signal : this.generalizationHierachie(true)) {
			if (signal.getAttribute(attr.name()) != null) {
				throw new MInvalidModelException("An attribute with name "
						+ StringUtil.inQuotes(attr.name())
						+ " is already defined in signal "
						+ StringUtil.inQuotes(signal.name()) + ".");
			}
		}
		
		this.ownedAttribute.put(attr.name(), attr);
	}
	
	@Override
	public Set<MAttribute> getAttributes() {
		return new HashSet<>(this.ownedAttribute.values());
	}
	
	@Override
	public MAttribute getAttribute(String name) {
		return this.ownedAttribute.get(name);
	}
	
	@Override
	public Set<MSignal> parents() {
		return model.generalizationGraph().targetNodeSet(MSignal.class, this);
	}

	@Override
	public Set<MSignal> allParents() {
		return model.generalizationGraph().targetNodeClosureSet(MSignal.class, this);
	}

	@Override
	public Iterable<MSignal> generalizationHierachie(final boolean includeThis) {
		return new Iterable<MSignal>() {
			@SuppressWarnings({ "rawtypes", "unchecked" }) // Signals only inherit from other signals
			@Override
			public Iterator<MSignal> iterator() {
				return (Iterator)model.generalizationGraph().targetNodeClosureSetIterator(MSignalImpl.this, includeThis);
			}
		};
	}

	@Override
	public Iterable<MSignal> specializationHierachie(final boolean includeThis) {
		return new Iterable<MSignal>() {
			@SuppressWarnings({ "rawtypes", "unchecked" }) // Signals only inherit from other signals
			@Override
			public Iterator<MSignal> iterator() {
				return (Iterator)model.generalizationGraph().sourceNodeClosureSetIterator(MSignalImpl.this, includeThis);
			}
		};
	}

	@Override
	public Set<MSignal> allChildren() {
		return model.generalizationGraph().sourceNodeClosureSet(MSignal.class, this);
	}

	@Override
	public Set<MSignal> children() {
		return model.generalizationGraph().sourceNodeSet(MSignal.class, this);
	}

	@Override
	public void processWithVisitor(MMVisitor v) {
		v.visitSignal(this);
	}

	@Override
	public int getPositionInModel() {
		return position;
	}

	@Override
	public void setPositionInModel(int line) {
		this.position = line;		
	}

	@Override
	public MessageType getType() {
		return new MessageType(this);
	}

	@Override
	public Set<MAttribute> getAllAttributes() {
		Set<MAttribute> attrs = new HashSet<MAttribute>(getAttributes());
		for (MSignal parent : generalizationHierachie(false)) {
			attrs.addAll(parent.getAttributes());
		}
		
		return attrs;
	}
}
