package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeTroubleShootingResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-11 10:53:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeTroubleShootingResultEvent extends BeCapwapClientResultEvent {

	private String				macAddress;

	private String				bssid;

	/**
	 * unit is ms
	 */
	private long				timeStamp;

	public static final int		STAGE_80211			= 0;

	public static final int		STAGE_RADIUS		= 1;

	public static final int		STAGE_AUTH			= 2;

	public static final int		STAGE_DHCP			= 3;

	private int					stage;

	public static final short	STAGERESULT_SUCCESS	= 1;

	public static final short	STAGERESULT_FAILURE	= 0;

	private boolean				stageResult;

	private String				description;

	private short				completingStep;

	private short				totalStep;

	private long				sequenceNumber4TroubleShoot;

	public static final int		LEVEL_BASIC			= 0;

	public static final int		LEVEL_INFO			= 1;

	public static final int		LEVEL_DETAIL		= 2;

	/**
	 * level of message
	 */
	private int					level;

	public BeTroubleShootingResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			sequenceNumber4TroubleShoot = AhDecoder.int2long(buf.getInt());
			macAddress = AhDecoder.bytes2hex(buf, 6).toUpperCase();
			bssid = AhDecoder.bytes2hex(buf, 6).toUpperCase();
			timeStamp = AhDecoder.int2long(buf.getInt()) * 1000;
			stage = buf.getInt();
			completingStep = buf.getShort();
			totalStep = buf.getShort();
			stageResult = (buf.getShort() == STAGERESULT_SUCCESS);
			short len = buf.getShort();
			description = AhDecoder.bytes2String(buf, AhDecoder.short2int(len));
			if (buf.hasRemaining()) {
				level = buf.getInt();
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeTroubleShootingResultEvent.parsePacket() catch exception", e);
		}
	}

	public short getCompletingStep() {
		return completingStep;
	}

	public void setCompletingStep(short completingStep) {
		this.completingStep = completingStep;
	}

	public boolean isStageResult() {
		return stageResult;
	}

	public void setStageResult(boolean stageResult) {
		this.stageResult = stageResult;
	}

	public short getTotalStep() {
		return totalStep;
	}

	public void setTotalStep(short totalStep) {
		this.totalStep = totalStep;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public long getSequenceNumber4TroubleShoot() {
		return sequenceNumber4TroubleShoot;
	}

	public void setSequenceNumber4TroubleShoot(long sequenceNumber4TroubleShoot) {
		this.sequenceNumber4TroubleShoot = sequenceNumber4TroubleShoot;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
