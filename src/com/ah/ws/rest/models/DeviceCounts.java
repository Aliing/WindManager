package com.ah.ws.rest.models;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DeviceCounts {
	private long apCounts;

	private long brCounts;

	private long srCounts;

	private long cvgCounts;

	public long getApCounts() {
		return apCounts;
	}

	public void setApCounts(long apCounts) {
		this.apCounts = apCounts;
	}

	public long getBrCounts() {
		return brCounts;
	}

	public void setBrCounts(long brCounts) {
		this.brCounts = brCounts;
	}

	public long getSrCounts() {
		return srCounts;
	}

	public void setSrCounts(long srCounts) {
		this.srCounts = srCounts;
	}

	public long getCvgCounts() {
		return cvgCounts;
	}

	public void setCvgCounts(long cvgCounts) {
		this.cvgCounts = cvgCounts;
	}
}
