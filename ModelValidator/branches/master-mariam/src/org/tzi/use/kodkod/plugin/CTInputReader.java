package org.tzi.use.kodkod.plugin;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tzi.use.util.StringUtil;

public class CTInputReader extends AbstractFilterReader {

	public CTInputReader(Reader in) throws IOException {
		super(in);
	}

	/**
	 * Manipulates the input to give one line of CT name and one line of CT
	 * expression repeatedly.
	 */
	@Override
	protected String manipulateInput(String text) {
		text = text.replaceAll("(\r\n|\r|\n)", "");
		StringBuilder sb = new StringBuilder();
		
		//TODO crashes!!! Pattern compilation exception @ < in second option
		String[] splits = text.split("@\\}>--|--<\\{@|~~~");
		
		final Pattern ctPattern = Pattern.compile("^\\s*(?:\\[\\s*([\\w\\d_]+)\\s*\\])?(.*)$", Pattern.CASE_INSENSITIVE);
		for (String ct : splits) {
			Matcher match = ctPattern.matcher(ct);
			if(match.find()){
				String name = match.group(1);
				String expr = match.group(2);
				
				sb.append(name == null ? "" : name);
				sb.append(StringUtil.NEWLINE);
				sb.append(expr);
				sb.append(StringUtil.NEWLINE);
			}
		}
		
		return sb.toString();
	}

}
