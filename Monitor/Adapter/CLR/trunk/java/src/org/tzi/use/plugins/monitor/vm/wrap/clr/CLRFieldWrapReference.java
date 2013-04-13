package org.tzi.use.plugins.monitor.vm.wrap.clr;

public class CLRFieldWrapReference extends CLRFieldWrapBase {
	private final long reference;
	
	public CLRFieldWrapReference(long token, long reference) {
		super(token);
		this.reference = reference;
	}

	public long getReference() {
		return reference;
	}

	@Override
	public String toString() {
		return "CLRFieldWrapReference [reference=" + reference + "]";
	}

}
