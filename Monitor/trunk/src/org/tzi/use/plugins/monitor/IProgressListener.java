package org.tzi.use.plugins.monitor;

public interface IProgressListener {
	void progressStart(ProgressArgs args);
	void progress(ProgressArgs args);
	void progressEnd();
}
