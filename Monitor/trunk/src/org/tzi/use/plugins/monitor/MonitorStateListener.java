package org.tzi.use.plugins.monitor;

/**
 * Interface for classes which need to be notified
 * when the monitor state changes. 
 * @author Lars Hamann
 *
 */
public interface MonitorStateListener {
	/**
	 * Called when monitoring is started
	 * @param source
	 */
	void monitorStarted(Monitor source);
	
	/**
	 * Called when monitoring is paused
	 * @param source
	 */
	void monitorPaused(Monitor source);
	
	/**
	 * Called when monitoring is resumed
	 * @param source
	 */
	void monitorResumed(Monitor source);
	
	/**
	 * Called when monitoring is ended
	 * @param source
	 */
	void monitorEnded(Monitor source);
	
	/**
	 * Called every time the monitoring state changes
	 * @param source
	 */
	void monitorStateChanged(Monitor source);
}
