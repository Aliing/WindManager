package com.ah.be.ts.hiveap;

public abstract class AbstractDebugNotification extends AbstractDebug implements DebugNotification {

	private static final long serialVersionUID = 1L;

    //***************************************************************
    // Variables
    //***************************************************************

	/** Description */
	protected String description;

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(DebugNotification other) {
		long diff = timstamp - other.getTimstamp();

		if (diff > 0) {
			diff = 1;
		} else if (diff < 0) {
			diff = -1;
		}

		return (int) diff;
	}

}