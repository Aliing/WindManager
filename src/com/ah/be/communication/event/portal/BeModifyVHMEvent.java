package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.app.DebugUtil;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * 
 *@filename		BeModifyVHMEvent.java
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
public class BeModifyVHMEvent extends BePortalHMPayloadEvent {

	private VhmInfo	vhmInfo;

	public BeModifyVHMEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_MODIFY;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (vhmInfo == null) {
			throw new BeCommunicationEncodeException("vhmInfo is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, vhmInfo.getVhmId());

		AhEncoder.putString(buf, vhmInfo.getVhmName());

		buf.putShort(vhmInfo.getVhmType());
		buf.putInt(vhmInfo.getMaxApNum());

		AhEncoder.putString(buf, vhmInfo.getVhmAdminName());

		AhEncoder.putString(buf, vhmInfo.getVhmAdminFullName());

		AhEncoder.putString(buf, vhmInfo.getVhmAdminEmailAddress());

		buf.put(vhmInfo.isGmLightEnable() ? ENABLE : DISABLE);
		buf.putInt(vhmInfo.getValidDays());
		buf.put(vhmInfo.isNeedNotifyFlag() ? ENABLE : DISABLE);
		buf.put(vhmInfo.isStatusEnable() ? ENABLE : DISABLE);

		AhEncoder.putString(buf, vhmInfo.getCcEmailAddress());

		buf.put(vhmInfo.isEnterpriseEnableFlag() ? ENABLE : DISABLE);

		AhEncoder.putString(buf, vhmInfo.getUrl());

		AhEncoder.putString(buf, vhmInfo.getVhmAdminClearPassword());
		AhEncoder.putString(buf, vhmInfo.getOrderKey());
		AhEncoder.putString(buf, vhmInfo.getOwnerUserName());
		AhEncoder.putString(buf, vhmInfo.getHhmName());

		buf.putInt(vhmInfo.getMaxSimuApNum());
		buf.putInt(vhmInfo.getMaxSimuClientNum());

		buf.putShort(vhmInfo.getAccessMode());
		buf.putLong(vhmInfo.getAuthorizationEndDate());

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
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
		
		DebugUtil.sgeDebugInfo("BeModifyVHMEvent : " + vhmInfo.toString());

		return null;
	}

	public VhmInfo getVhmInfo() {
		return vhmInfo;
	}

	public void setVhmInfo(VhmInfo vhmInfo) {
		this.vhmInfo = vhmInfo;
	}
}
