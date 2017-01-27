package org.tzi.use.kodkod.plugin;

import java.io.IOException;
import java.io.Reader;

/**
 * A utility class that removes USE specification file comments from a
 * {@linkplain Reader}. Line comments (--) as well as block comments.
 * 
 * @author Frank Hilken
 */
public class USECommentFilterReader extends AbstractFilterReader {

	public USECommentFilterReader(Reader in) throws IOException {
		super(in);
	}

	@Override
	protected String manipulateInput(String text) {
		// removes multi line comments
		String x = text.replaceAll("\\/\\*([\\s\\S]*?)\\*\\/", "");
		// removes the preceding newline and the line comment, leaving only a newline
		return x.replaceAll("(\r\n|\r|\n)?--([^\r\n]*)", "");
	}
	
}