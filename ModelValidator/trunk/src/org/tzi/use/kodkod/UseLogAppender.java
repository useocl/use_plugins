package org.tzi.use.kodkod;

import java.io.PrintWriter;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Log4J appender use
 * 
 * @author Hendrik Reitmann
 * 
 */
public class UseLogAppender extends WriterAppender {

	private static PrintWriter printWriter;

	public static void initialize(PrintWriter printWriter) {
		UseLogAppender.printWriter = printWriter;
	}

	@Override
	public void append(LoggingEvent event) {
		if (printWriter != null) {
			final String message = this.layout.format(event);
			printWriter.print(message);
		}
	}
}
