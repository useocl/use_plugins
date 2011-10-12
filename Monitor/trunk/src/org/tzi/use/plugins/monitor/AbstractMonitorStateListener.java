package org.tzi.use.plugins.monitor;

/**
 * Abstract class for IMonitorStateListeners that only
 * need particular informations. 
 * @author Lars Hamann
 *
 */
public class AbstractMonitorStateListener implements MonitorStateListener {

	@Override
	public void monitorStarted(Monitor source) {}

	@Override
	public void monitorPaused(Monitor source) {}

	@Override
	public void monitorResumed(Monitor source) {}

	@Override
	public void monitorEnded(Monitor source) {}

	@Override
	public void monitorStateChanged(Monitor source) {}

}
