package org.tzi.use.gui.plugins.data;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class TAttribute {
	private String name;
	private String value;
	private Set<Type> types;

	public enum Type {
		INTEGER, REAL, BOOLEAN, STRING, VOID, ANY;

		@Override
		public String toString() {
			switch (this) {
			case INTEGER:
				return "Integer";
			case REAL:
				return "Real";
			case BOOLEAN:
				return "Boolean";
			case STRING:
				return "String";
			case VOID:
				return "OclVoid";
			case ANY:
				return "OclAny";
			default:
				throw new IllegalArgumentException();
			}
		}
	}
	
	public TAttribute(String name, String value) {
		this.name = name;
		this.value = value;
		this.types = null;
	}

	public TAttribute(String name, String value, Type type) {
		this.name = name;
		this.value = value;
		setSingleType(type);
		// setValueAndType(value);
	}

	public TAttribute(String name, Type type) {
		this.name = name;
		setSingleType(type);
	}

	@Override
	public String toString() {
		return "(Name: " + name + ", Value: " + value + ", Types: " + types.toString() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TAttribute)) {
			return false;
		}
		TAttribute otherAttribute = (TAttribute) obj;

		if (name == null && otherAttribute.name != null) {
			return false;
		}
		if (name != null && !name.equals(otherAttribute.name)) {
			return false;
		}

		if (value == null && otherAttribute.value != null) {
			return false;
		}
		if (value != null && !value.equals(otherAttribute.value)) {
			return false;
		}

		if (types == null && otherAttribute.types != null) {
			return false;
		}
		if (types != null && !types.equals(otherAttribute.types)) {
			return false;
		}

		return true;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
		types = null;
	}

	public Type getSingleType() {
		if (types == null || types.isEmpty()) {
			return Type.VOID;
		}
		if (types.size() == 1) {
			Type[] typeArray = types.toArray(new Type[1]);
			return typeArray[0];
		}
		return Type.ANY;
	}

	private void setSingleType(Type type) {
		types = new HashSet<Type>();
		types.add(type);
	}

	public Set<Type> getAllTypes() {
		return types;
	}

	public void setAllTypes(Set<Type> allTypes) {
		types = allTypes;
	}

	public String getDisplayTextForClass() {
		String nameText = name;
		if (nameText == null) {
			nameText = TConstants.PLACEHOLDER;
		}

		LinkedList<Type> typeList = new LinkedList<Type>(types);
		Type firstType = typeList.pop();
		if (firstType == null || firstType == Type.VOID) {
			return nameText;
		} else if (typeList.isEmpty()) {
			return nameText + " : " + firstType.toString();
		} else {
			String typeText = TConstants.CONFLICT_MARKER + " " + firstType.toString();
			for (Type type : typeList) {
				typeText += (", " + type);
			}
			return nameText + " : " + typeText;
		}
	}

	public TStatus getCurrentStatus() {
		if (types.size() > 1) {
			return TStatus.CONFLICT;
		}
		// name is needed for both objects and classes
		if (name == null) {
			return TStatus.MISSING;
		}
		return TStatus.COMPLETE;
	}
}