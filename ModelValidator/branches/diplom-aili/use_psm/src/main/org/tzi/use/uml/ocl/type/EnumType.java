/*
 * USE - UML based specification environment
 * Copyright (C) 1999-2004 Mark Richters, University of Bremen
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

// $Id$

package org.tzi.use.uml.ocl.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tzi.use.uml.mm.Annotatable;
import org.tzi.use.uml.mm.MClassifier;
import org.tzi.use.uml.mm.MElementAnnotation;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.mm.UseFileLocatable;
import org.tzi.use.util.collections.CollectionUtil;

import com.google.common.collect.Iterators;

/**
 * An enumeration type.
 *
 * @version     $ProjectVersion: 0.393 $
 * @author  Mark Richters
 */
public final class EnumType extends Type implements Annotatable, MClassifier, UseFileLocatable {
    //TODO: Use delegation for Annotatable?
	private String fName;
    
    /**
     * list of enumeration literals
     */
    private ArrayList<String> fLiterals;
    
    /**
     * for fast access
     */
    private HashSet<String> fLiteralSet;
    
    /**
     * Possible annotations of this model element.
     */
    private Map<String, MElementAnnotation> annotations = Collections.emptyMap();
    
    private MModel fModel;

	private int positionInModel;
    
    /**
     * Constructs an enumeration type with name and list of literals
     * (String objects). The list of literals is checked for
     * duplicates.
     */
    protected EnumType(MModel model, String name, List<String> literals) {
        fModel = model;
    	fName = name;
        fLiterals = new ArrayList<String>(literals);
        fLiteralSet = new HashSet<String>(fLiterals.size());
        
        Iterator<String> it = fLiterals.iterator();
        while (it.hasNext() ) {
            String lit = it.next();
            if (! fLiteralSet.add(lit) )
                throw new IllegalArgumentException("duplicate literal `" +  lit + "'");
        }
    }
    
    /**
     * Returns the set of all direct parent classes (without this
     * class).
     *
     * @return Set(MClass) 
     */
    public Set<MClassifier> parents() {
        return CollectionUtil.downCastUnsafe(fModel.generalizationGraph().targetNodeSet(this));
    }

    /**
     * Returns the set of all parent classes (without this
     * class). This is the transitive closure of the generalization
     * relation.
     *
     * @return Set(MClass) 
     */
    public Set<MClassifier> allParents() {
    	return CollectionUtil.downCastUnsafe(fModel.generalizationGraph().targetNodeClosureSet(this));
    }

    /**
     * Returns the set of all child classes (without this class). This
     * is the transitive closure of the generalization relation.
     *
     * @return Set(MClass) 
     */
    public Set<MClassifier> allChildren() {
    	return CollectionUtil.downCastUnsafe(fModel.generalizationGraph().sourceNodeClosureSet(this));
    }

    /**
     * Returns the set of all direct child classes (without this
     * class).
     *
     * @return Set(MClass) 
     */
    public Set<MClassifier> children() {
    	return CollectionUtil.downCastUnsafe(fModel.generalizationGraph().sourceNodeSet(this));
    }
    
    public boolean isEnum() {
    	return true;
    }
    
    /** 
     * Returns the name of the enumeration type.
     */
    public String name() {
        return fName;
    }

    /** 
     * Returns an iterator over the literals.
     */
    public Iterator<String> literals() {
        return fLiterals.iterator();
    }

    /**
     * Returns an unmodifiable list of literals for the enumeration
     * @return
     */
    public List<String> getLiterals() {
    	return Collections.unmodifiableList(fLiterals);
    }
    
    /** 
     * Returns true if this enumeration type contains the given literal.
     */
    public boolean contains(String lit) {
        return fLiteralSet.contains(lit);
    }

    /** 
     * Returns true if this type is a subtype of <code>t</code>. 
     */
    public boolean isSubtypeOf(Type t) {
        return equals(t) || t.isTrueOclAny();
    }

    /** 
     * Returns the set of all supertypes (including this type).
     */
    public Set<Type> allSupertypes() {
        Set<Type> res = new HashSet<Type>(2);
        res.add(TypeFactory.mkOclAny());
        res.add(this);
        return res;
    }

    @Override
    public boolean isAnnotatable() {
    	return true;
    }
    
    @Override
    public Map<String, MElementAnnotation> getAllAnnotations() {
    	return this.annotations;
    }
    
    @Override
    public boolean isAnnotated() {
    	return !this.annotations.isEmpty();
    }
    
    @Override
    public MElementAnnotation getAnnotation(String name) {
    	if (this.annotations.containsKey(name)) {
    		return this.annotations.get(name);
    	} else {
    		return null;
    	}
    }
    
    @Override
    public String getAnnotationValue(String annotationName, String attributeName) {
    	MElementAnnotation ann = getAnnotation(annotationName);
    	
    	if (ann == null) return "";
    	
    	String value = ann.getAnnotationValue(attributeName); 
    	return (value == null ? "" : value);
    }
    
    @Override
    public void addAnnotation(MElementAnnotation annotation) {
    	this.annotations = CollectionUtil.initAsHashMap(this.annotations);
    	this.annotations.put(annotation.getName(), annotation);
    }
    
    /**
     * Returns true if the passed type is equal.
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this ) return true;
        if (obj.getClass().equals(getClass())) 
            return fName.equals(((EnumType) obj).fName);
        return false;
    }

    public int hashCode() {
        return fName.hashCode();
    }
    
    /** 
     * Return complete printable type name, e.g. 'Set(Bag(Integer))'. 
     */
    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(fName);
    }

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public Iterable<? extends MClassifier> generalizationHierachie(boolean includeThis) {
		// We don't support generalization of enumerations, yet
		return new Iterable<MClassifier>() {
			@Override
			public Iterator<MClassifier> iterator() {
				return Iterators.emptyIterator();
			}
		};
	}
	
	public Iterable<? extends MClassifier> specializationHierachie(boolean includeThis) {
		// We don't support generalization of enumerations, yet
		return new Iterable<MClassifier>() {
			@Override
			public Iterator<MClassifier> iterator() {
				return Iterators.emptyIterator();
			}
		};
	}

	public void setPositionInModel(int pos){
		positionInModel = pos;
	}
	
	@Override
	public int getPositionInModel() {
		return positionInModel;
	}
}
