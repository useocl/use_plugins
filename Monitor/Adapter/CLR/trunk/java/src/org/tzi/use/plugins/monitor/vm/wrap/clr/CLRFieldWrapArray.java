package org.tzi.use.plugins.monitor.vm.wrap.clr;

import java.util.HashSet;
import java.util.Set;

public class CLRFieldWrapArray extends CLRFieldWrapBase {
	private Set<Long> references;
	
	public CLRFieldWrapArray(long token) {
		super(token);
		references = new HashSet<Long>();
	}
	
	public void addReference(long ref)
	{
		references.add(ref);
	}

	public Set<Long> getReferences() {
		return references;
	}
	
}
