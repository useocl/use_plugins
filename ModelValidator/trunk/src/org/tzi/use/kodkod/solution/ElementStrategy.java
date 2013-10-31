package org.tzi.use.kodkod.solution;

import java.util.Map;

import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MObjectState;
import org.tzi.use.uml.sys.MSystemException;
import org.tzi.use.uml.sys.MSystemState;

import kodkod.instance.Tuple;

/**
 * Abstract strategy for the creation of the different elemens.
 * 
 * @author Hendrik Reitmann
 *
 */
public abstract class ElementStrategy {

	protected MModel mModel;
	protected MSystemState mSystemState;
	protected Map<String, MObjectState> objectStates;
	
	public ElementStrategy(MSystemState mSystemState,MModel mModel, Map<String, MObjectState> objectStates){
		this.mSystemState=mSystemState;
		this.mModel=mModel;
		this.objectStates=objectStates;
	}

	public boolean canDo(){
		return true;
	}
	
	public abstract void createElement(Tuple currentTuple) throws MSystemException;
}
