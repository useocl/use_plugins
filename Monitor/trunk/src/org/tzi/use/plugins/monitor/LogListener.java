package org.tzi.use.plugins.monitor;

import java.util.logging.Level;

/**
 * A listener interface for log messages.
 * 
 * @author Lars Hamann
 *
 */
public interface LogListener {
	/**
	 * Called if a new message is logged.
	 * @param level The importance of the message
	 * @param message The message itself
	 */
	void newLogMessage(Object source, Level level, String message);
}
