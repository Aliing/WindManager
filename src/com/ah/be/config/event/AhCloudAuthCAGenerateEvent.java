package com.ah.be.config.event;

import com.ah.be.cloudauth.result.HmCloudAuthCertResult;
import com.ah.be.event.BeEventConst;
import com.ah.bo.hiveap.HiveAp;

public class AhCloudAuthCAGenerateEvent extends AhConfigEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HmCloudAuthCertResult result;

	private String[] clis;
	
	protected int seqNum;

	public AhCloudAuthCAGenerateEvent() {
		super.setEventType(BeEventConst.AH_CLOUD_AUTH_UPDATE_EVENT);
	}

	public AhCloudAuthCAGenerateEvent(HiveAp hiveAp) {
		this();
		super.hiveAp = hiveAp;
	}
	
	public String[] getClis() {
		return clis;
	}

	public void setClis(String[] clis) {
		this.clis = clis;
	}
	
	public HmCloudAuthCertResult getResult() {
		return result;
	}

	public void setResult(HmCloudAuthCertResult result) {
		this.result = result;
	}
	
	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

}
