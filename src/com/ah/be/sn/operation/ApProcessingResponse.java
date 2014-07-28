package com.ah.be.sn.operation;

public abstract class ApProcessingResponse {

	private Ap ap;

	public Ap getAp() {
		return ap;
	}

	public void setAp(Ap ap) {
		this.ap = ap;
	}

	private Status status;

	public Status getStatus() {
		if (status == null) {
			status = new Status();
		}
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}