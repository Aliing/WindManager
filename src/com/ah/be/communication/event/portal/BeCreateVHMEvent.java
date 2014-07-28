package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeCreateVHMEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-1-25 03:38:51
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeCreateVHMEvent extends BePortalHMPayloadEvent {

	private VhmInfo	vhmInfo;

	public BeCreateVHMEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_CREATE;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		return null;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		vhmInfo = new VhmInfo();
		vhmInfo.setVhmId(AhDecoder.getString(buf));
		vhmInfo.setVhmName(AhDecoder.getString(buf));
		vhmInfo.setVhmType(buf.getShort());
		vhmInfo.setMaxApNum(buf.getInt());
		vhmInfo.setVhmAdminName(AhDecoder.getString(buf));
		vhmInfo.setVhmAdminFullName(AhDecoder.getString(buf));
		vhmInfo.setVhmAdminEmailAddress(AhDecoder.getString(buf));
		vhmInfo.setGmLightEnable(buf.get() == ENABLE);
		vhmInfo.setValidDays(buf.getInt());
		vhmInfo.setNeedNotifyFlag(buf.get() == ENABLE);
		vhmInfo.setStatusEnable(buf.get() == ENABLE);
		vhmInfo.setCcEmailAddress(AhDecoder.getString(buf));
		vhmInfo.setEnterpriseEnableFlag(buf.get() == ENABLE);
		vhmInfo.setUrl(AhDecoder.getString(buf));
		vhmInfo.setVhmAdminClearPassword(AhDecoder.getString(buf));
		vhmInfo.setOrderKey(AhDecoder.getString(buf));
		vhmInfo.setOwnerUserName(AhDecoder.getString(buf));
		vhmInfo.setHhmName(AhDecoder.getString(buf));
		vhmInfo.setMaxSimuApNum(buf.getInt());
		vhmInfo.setMaxSimuClientNum(buf.getInt());
		vhmInfo.setAccessMode(buf.getShort());
		vhmInfo.setAuthorizationEndDate(buf.getLong());
		vhmInfo.setAuthorizedTime(buf.getShort());

		return null;
	}

	public VhmInfo getVhmInfo() {
		return vhmInfo;
	}

	public void setVhmInfo(VhmInfo vhmInfo) {
		this.vhmInfo = vhmInfo;
	}
}
