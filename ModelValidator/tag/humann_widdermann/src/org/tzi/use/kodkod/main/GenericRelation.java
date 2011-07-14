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

// $Id$

package org.tzi.use.kodkod.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.tzi.use.uml.ocl.value.StringValue;
import org.tzi.use.uml.ocl.value.Value;

import kodkod.ast.Relation;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;

/**
 * relation for assl usage
 * @author JuRi
 *
 */
public class GenericRelation {
	//Counter for unique name
	private static int i = 0;
	
	private Relation relation;
	private String name;
	private Collection<Value> values;
	private boolean newUniverseItems;
	
	public GenericRelation(boolean newUniverseItems){
		this.name = "__GenericRelation_"+i;
		this.relation = Relation.unary(this.name);
		this.newUniverseItems = newUniverseItems;
		i++;
	}
	
	public GenericRelation(boolean newUniverseItems, String name){
		this.name = name;
		this.relation = Relation.unary(this.name);
		this.newUniverseItems = newUniverseItems;
	}
	
	public void setBoundValues(Collection<Value> values) {
		this.values = values;
	}
	
	public Bounds getGenericBounds(Bounds bou, TupleFactory tFa, SetKodkodStruc skks) {
		TupleSet tsGenericExactly = tFa.noneOf(1);
		
		if(this.values != null) {
			Iterator<Value> iterator = this.values.iterator();
			while(iterator.hasNext()) {
				Value val = iterator.next();
				if(val.getClass().equals(StringValue.class)){
					StringValue stringVal = (StringValue) val;
					tsGenericExactly.add(tFa.tuple(stringVal.value()));
				}
			}
			
			bou.boundExactly(this.relation, tsGenericExactly);
		}
		return bou;
	}
	
	public ArrayList<String> getValuesAsList() {
		ArrayList<String> stringList = new ArrayList<String>();
		if(this.values != null) {
			Iterator<Value> iterator = this.values.iterator();
			while(iterator.hasNext()) {
				Value val = iterator.next();
				if(val.getClass().equals(StringValue.class)){
					StringValue stringVal = (StringValue) val;
					stringList.add(stringVal.value());
				}
			}
		}
		return stringList;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Relation getRelation(){
		return this.relation;
	}
	
	@Override
	public String toString(){
		return "Generic-Relation: Name - " + this.name + " - values - " + values;
	}
	
	public boolean containsNewAtoms() {
		return newUniverseItems;
	}
}
