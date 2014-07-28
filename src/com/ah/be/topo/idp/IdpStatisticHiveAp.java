package com.ah.be.topo.idp;


public class IdpStatisticHiveAp {
	private String nodeId;
	// CAPWAP request returned value for result event used.
	private int sequenceNum = 0;//HmBeCommunicationUtil.getSequenceNumber();
	// Indicate whether can deal with the reported IDP message.
	private boolean opened;
	// Indicate whether can deal with the reported mitigation message.
	private boolean mitigationOpened;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public boolean isMitigationOpened() {
		return mitigationOpened;
	}

	public void setMitigationOpened(boolean mitigationOpened) {
		this.mitigationOpened = mitigationOpened;
	}

}
