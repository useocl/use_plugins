package org.tzi.use.plugin.otc;

final class OTCModel {
	static String getModel() {
		StringBuilder b = new StringBuilder();
		
		b.append("model PartialObjectDia");					b.append("\n");
															b.append("\n");
		b.append("class Object");							b.append("\n");
		b.append("attributes");								b.append("\n");
		b.append("  identity:String init:''");				b.append("\n");
		b.append("  className:String init:''");				b.append("\n");
		b.append("end");									b.append("\n");
															b.append("\n");
		b.append("class Attribute");						b.append("\n");
		b.append("attributes");								b.append("\n");
		b.append("  attributeName:String init:''");			b.append("\n");
		b.append("  attributeValue:OclAny init:''");		b.append("\n");
		b.append("end");									b.append("\n");
															b.append("\n");
		b.append("class Link");								b.append("\n");
		b.append("attributes");								b.append("\n");
		b.append("  linkName:String init:''");				b.append("\n");
		b.append("  sRoleName:String init:''");				b.append("\n");
		b.append("  wRoleName:String init:''");				b.append("\n");
		b.append("end");									b.append("\n");
															b.append("\n");
		b.append("association Object_Attribute between");	b.append("\n");
		b.append("  Object[1]  role object");				b.append("\n");
		b.append("  Attribute[0..*] role attribute");		b.append("\n");
		b.append("end");									b.append("\n");
															b.append("\n");
		b.append("association SLink_SObject between");		b.append("\n");
		b.append("  Link[0..*] role sLink");				b.append("\n");
		b.append("  Object[1]  role sObject");				b.append("\n");
		b.append("end");									b.append("\n");
															b.append("\n");
		b.append("association WLink_WObject between");		b.append("\n");
		b.append("  Link[0..*] role wLink");				b.append("\n");
		b.append("  Object[1]  role wObject");				b.append("\n");
		b.append("end");									b.append("\n");
		
		return b.toString();
	}
}
