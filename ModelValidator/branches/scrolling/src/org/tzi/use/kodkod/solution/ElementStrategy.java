package org.tzi.use.kodkod.solution;

import java.util.Map;

import kodkod.instance.Tuple;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.uml.mm.MModel;
import org.tzi.use.uml.sys.MObjectState;

/**
 * Abstract strategy for the creation of the different elemens.
 * 
 * @author Hendrik Reitmann
 *
 */
public abstract class ElementStrategy {

	protected UseSystemApi systemApi;
	protected MModel mModel;
	protected Map<String, MObjectState> objectStates;
	
	public ElementStrategy(UseSystemApi systemApi, Map<String, MObjectState> objectStates){
		this.systemApi = systemApi;
		mModel = systemApi.getSystem().model();
		this.objectStates = objectStates;
	}

	public boolean canDo(){
		return true;
	}
	
	public abstract void createElement(Tuple currentTuple) throws UseApiException;
}
