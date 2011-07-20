package org.tzi.use.plugins.monitor;

public class ProgressArgs {
	private String description;
	private int end;
	private int current;
	private boolean cancel;
	
	public ProgressArgs(String description, int end) {
		this(description, 0, end);
	}
	
	public ProgressArgs(String description, int current, int end) {
		this.description = description;
		this.current = current;
		this.end = end;
		this.cancel = false;
	}

	/**
	 * @return the cancel
	 */
	public boolean isCancel() {
		return cancel;
	}

	/**
	 * @param cancel the cancel to set
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return the current
	 */
	public int getCurrent() {
		return current;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	/**
	 * @param current the current to set
	 */
	public void setCurrent(int current) {
		this.current = current;
	}
}
