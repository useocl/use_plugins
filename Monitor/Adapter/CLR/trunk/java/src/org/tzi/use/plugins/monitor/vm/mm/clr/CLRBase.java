package org.tzi.use.plugins.monitor.vm.mm.clr;

import org.tzi.use.monitor.adapter.clr.CLRAdapter;

/**
 * Base class for CLR meta-model elements.
 * @author <a href="mailto:dhonsel@informatik.uni-bremen.de">Daniel Honsel</a>
 */
public abstract class CLRBase {
	protected CLRAdapter adapter;
	protected long id;
	
	public CLRBase(CLRAdapter adapter, long id) {
		this.adapter = adapter;
		this.id = id;
	}
	
	public CLRAdapter getAdapter() {
		return this.adapter;
	}
	
	public Long getId() {
		return this.id;
	}
	
	public long getIdCLR() {
		return this.id;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CLRBase))
			return false;
		CLRBase other = (CLRBase) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}