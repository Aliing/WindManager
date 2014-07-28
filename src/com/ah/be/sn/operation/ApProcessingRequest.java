package com.ah.be.sn.operation;

public abstract class ApProcessingRequest {

	private Ap ap;

	public Ap getAp() {
		return ap;
	}

	public void setAp(Ap ap) {
		this.ap = ap;
	}

	private RemoteSystem remoteSystem;

	public RemoteSystem getSystem() {
		return remoteSystem;
	}

	public void setSystem(RemoteSystem remoteSystem) {
		this.remoteSystem = remoteSystem;
	}

}