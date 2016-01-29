package org.tzi.use.kodkod.plugin;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * A utility class that removes USE specification file comments from a
 * {@linkplain Reader}. Line comments (--) as well as block comments.
 * 
 * @author Frank Hilken
 */
public class USECommentFilterReader extends FilterReader {

	private static final int COPYBUFFER_SIZE = 8192;
	private static final int ESTIMATED_INPUTFILE_LENGTH = 2000;
	
	private final String buffer;
	private int offset = 0;
	
	public USECommentFilterReader(Reader in) throws IOException {
		super(in);
		
		StringBuilder str = new StringBuilder(ESTIMATED_INPUTFILE_LENGTH);
		char[] buf = new char[COPYBUFFER_SIZE];
		
		int length;
		do {
			length = in.read(buf);
			if(length > 0){
				str.append(buf, 0, length);
			}
		} while( length > 0 );
		
		buffer = removeUSEComments(str.toString());
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		
		if (off < 0 || off > cbuf.length || len < 0
				|| (off + len) > cbuf.length || (off + len) < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		if(len == 0){
			return 0;
		}
		
		int copyLength = Math.min(len, buffer.length() - offset);
		if(copyLength == 0){
			return -1;
		}
		
		buffer.getChars(offset, offset+copyLength, cbuf, off);
		offset += copyLength;
		
		return copyLength;
	}
	
	private String removeUSEComments(String text){
		// removes multi line comments
		String x = text.replaceAll("\\/\\*([\\s\\S]*?)\\*\\/", "");
		// removes the preceding newline and the line comment, leaving only a newline
		return x.replaceAll("(?:\r\n|\r|\n)--([^\r\n]*)", "");
	}
	
}