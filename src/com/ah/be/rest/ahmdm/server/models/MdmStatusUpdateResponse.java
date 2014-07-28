package com.ah.be.rest.ahmdm.server.models;

import com.ah.be.rest.server.models.BaseModel;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("content")
public class MdmStatusUpdateResponse extends BaseModel {
	public final static int RESULT_SUCCESS = 0;
	public final static int RESULT_AP_NOT_FOUND = 1;
	public final static int RESULT_AP_DISCONNECTED = 2;
	public final static int RESULT_OTHER_FAILURE = 3;
	
	@XStreamAlias("APMacAddress")
	private String apMacAddress;
	@XStreamAlias("ErrorCode")
	private int resultCode;
	
	public String getApMacAddress() {
		return apMacAddress;
	}
	public void setApMacAddress(String apMacAddress) {
		this.apMacAddress = apMacAddress;
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

}
