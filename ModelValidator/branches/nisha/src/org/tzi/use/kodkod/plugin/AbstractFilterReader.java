package org.tzi.use.kodkod.plugin;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public abstract class AbstractFilterReader extends FilterReader {

	private static final int COPYBUFFER_SIZE = 8192;
	private static final int DEFAULT_INPUTFILE_LENGTH = 2000;
	
	private final String buffer;
	private int readOffset = 0;
	
	public AbstractFilterReader(Reader in, int initialBufferLength) throws IOException {
		super(in);
		
		StringBuilder str = new StringBuilder(initialBufferLength);
		char[] buf = new char[COPYBUFFER_SIZE];
		
		int length;
		do {
			length = in.read(buf);
			if(length > 0){
				str.append(buf, 0, length);
			}
		} while( length > 0 );
		
		buffer = manipulateInput(str.toString());
	}
	
	public AbstractFilterReader(Reader in) throws IOException {
		this(in, DEFAULT_INPUTFILE_LENGTH);
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
		
		int copyLength = Math.min(len, buffer.length() - readOffset);
		if(copyLength == 0){
			return -1;
		}
		
		buffer.getChars(readOffset, readOffset+copyLength, cbuf, off);
		readOffset += copyLength;
		
		return copyLength;
	}
	
	abstract protected String manipulateInput(String text);
}
