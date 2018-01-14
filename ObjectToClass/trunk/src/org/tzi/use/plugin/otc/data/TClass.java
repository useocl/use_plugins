package org.tzi.use.plugin.otc.data;

import java.util.LinkedList;
import java.util.List;

public class TClass {
	static int currentClassID = TConstants.MIN_ID_CLASS;
	private final int id;
	private String className;
	private List<TAttribute> attributes;

	public TClass(String className) {
		id = currentClassID++;
		if (id > TConstants.MAX_ID_CLASS) {
			// TODO error output or exception
			System.out.println("Too many classes");
		}

		attributes = new LinkedList<TAttribute>();
		this.className = className;
	}

	@Override
	public String toString() {
		return "(ID: " + id + ", ClassName: " + className + ", Attributes: " + getAttributes().toString() + ")";
	}

	public int getID() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<TAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<TAttribute> a) {
		attributes = a;
	}

	public void addAll(List<TAttribute> as) {
		attributes.addAll(as);
	}

	public TStatus getCurrentStatus() {
		TStatus status;
		// initialize with the help of className
		if (className == null) {
			status = TStatus.MISSING;
		} else {
			status = TStatus.COMPLETE;
		}
		for (TAttribute a : getAttributes()) {
			TStatus attributeStatus = a.getCurrentStatus();
			if (attributeStatus == TStatus.CONFLICT) {
				// if a single attribute has a conflict, immediately return
				// conflict
				return TStatus.CONFLICT;
			} else if (attributeStatus == TStatus.MISSING) {
				// if any attribute has the missing status, set status
				// accordingly
				status = TStatus.MISSING;
			}
		}
		return status;
	}
}