package org.tzi.use.plugin.filmstrip.gui;

import org.tzi.use.plugin.filmstrip.logic.TransformationInputException.ModelElements;
import org.tzi.use.uml.mm.MAssociation;
import org.tzi.use.uml.mm.MAssociationEnd;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MClassInvariant;
import org.tzi.use.util.StringUtil;

public class ErrorFormatter {
	
	private ErrorFormatter(){
	}

	public static String formatInputElementErrors(ModelElements elems) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		if(elems.getClasses().size() > 0){
			if(!first){
				sb.append(StringUtil.NEWLINE + StringUtil.NEWLINE);
			}
			first = false;
			sb.append("Classes:" + StringUtil.NEWLINE);
			sb.append(StringUtil.fmtSeq(elems.getClasses(), StringUtil.NEWLINE,
					new StringUtil.IElementFormatter<MClass>() {
				@Override
				public String format(MClass element) {
					return "* " + StringUtil.inQuotes(element.name());
				}
			}));
		}
		
		if(elems.getAssociations().size() > 0){
			if(!first){
				sb.append(StringUtil.NEWLINE + StringUtil.NEWLINE);
			}
			first = false;
			sb.append("Associations:" + StringUtil.NEWLINE);
			sb.append(StringUtil.fmtSeq(elems.getAssociations(), StringUtil.NEWLINE,
					new StringUtil.IElementFormatter<MAssociation>(){
				@Override
				public String format(MAssociation element) {
					return "* " + StringUtil.inQuotes(element.name());
				}
			}));
		}
		
		if(elems.getRoles().size() > 0){
			if(!first){
				sb.append(StringUtil.NEWLINE + StringUtil.NEWLINE);
			}
			first = false;
			sb.append("Roles:" + StringUtil.NEWLINE);
			sb.append(StringUtil.fmtSeq(elems.getRoles(), StringUtil.NEWLINE,
					new StringUtil.IElementFormatter<MAssociationEnd>(){
				@Override
				public String format(MAssociationEnd element) {
					return "* "
							+ StringUtil.inQuotes(element.name())
							+ " of association "
							+ StringUtil.inQuotes(element.association()
									.name()) + " at class "
							+ StringUtil.inQuotes(element.cls().name());
				}
			}));
		}
		
		if(elems.getClassInvariants().size() > 0){
			if(!first){
				sb.append(StringUtil.NEWLINE + StringUtil.NEWLINE);
			}
			first = false;
			sb.append("ClassInvariants:" + StringUtil.NEWLINE);
			sb.append(StringUtil.fmtSeq(elems.getClassInvariants(),
					StringUtil.NEWLINE,
					new StringUtil.IElementFormatter<MClassInvariant>() {
				@Override
				public String format(MClassInvariant element) {
					return "* " + StringUtil.inQuotes(element.name())
							+ " of class "
							+ StringUtil.inQuotes(element.cls().name());
				}
			}));
		}
		
		return sb.toString();
	}
	
}
