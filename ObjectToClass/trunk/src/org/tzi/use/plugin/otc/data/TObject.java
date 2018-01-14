package org.tzi.use.plugin.otc.data;

import java.util.LinkedList;
import java.util.List;

public class TObject {
	static int currentObjectID = TConstants.MIN_ID_OBJECT;
	private final int id;
	private String identityName;
	private String className;
	private List<TAttribute> attributes;

	public TObject(String identityName, String className) {
		id = currentObjectID++;
		if (id > TConstants.MAX_ID_OBJECT) {
			// TODO error output or exception
			System.out.println("Too many objects");
		}

		attributes = new LinkedList<TAttribute>();
		this.identityName = identityName;
		this.className = className;
	}

	public static TObject getNewObjectWithoutIncreasingID() {
		return new TObject();
	}

	private TObject() {
		id = TConstants.DEFAULT_ID;
		attributes = new LinkedList<TAttribute>();
	}

	@Override
	public String toString() {
		return "(ID: " + id + ", Identity: " + identityName + ", ClassName: " + className + ", Attributes: "
				+ getAttributes().toString() + ")";
	}

	public int getID() {
		return id;
	}

	public String getIdentityName() {
		return identityName;
	}

	public String getClassName() {
		return className;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
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