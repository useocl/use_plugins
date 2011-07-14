package org.tzi.use.kodkod.main;

import org.tzi.use.uml.mm.MClassInvariant;

/**
 * icludes the invariant and a flag how the invariant is
 * translated (positive=normal, negative or deactivated)
 * @author  Torsten Humann
 */
public class OCLInvar{
	
	public static enum Flag {
		p, n, d
	}
	
	private final MClassInvariant mInv;
	private Flag f;
	
	public OCLInvar(MClassInvariant mClaInv){
		mInv = mClaInv;
		f = Flag.p;
	}
	
	public MClassInvariant getMClassInvariant(){
		return mInv;
	}
	
	public String getName(){
		return mInv.name();
	}
	
	public void setFlag(Flag fla){
		f = fla;
	}
	
	public Flag getFlag(){
		return f;
	}
		
}