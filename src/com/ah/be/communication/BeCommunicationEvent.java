package com.ah.be.communication;

import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

/**
 * communciation event base class
 *@filename		BeCommunicationEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-10 02:32:43
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCommunicationEvent extends BeBaseEvent {

	private static final long	serialVersionUID	= 1L;

	/**
	 * see defination in {@link BeCommunicationConstant}
	 */
	protected int				msgType;

	/**
	 * Serial number of response message is the same as request message
	 */
	protected int				serialNum;

	/**
	 * sequence number of trap event same with value in request message
	 */
	protected int				sequenceNum;

	// response result, value see BeCommunicationConstant
	protected byte				result				= BeCommunicationConstant.RESULTTYPE_SUCCESS;

	private int					timeout;

	/**
	 * AP reference
	 */
	protected HiveAp			ap;

	protected SimpleHiveAp		simpleHiveAp;

	protected String			apMac				= "";

	/**
	 * byte[] for packet
	 */
	private byte[]				packet;

	// unit is milli-seconds
	private long				messageTimeStamp;

	private String				messageTimeZone;
	
	// the default initial capacity for buffer
//	protected final int			BUFFER_CAPACITY		= 5000;
	protected final int			BUFFER_CAPACITY		= 1024 * 128; // fixed batch VHM move issue (CFD-553), change to 128k in Hollywood by xtong

	public BeCommunicationEvent() {
		setModuleId(BaseModule.ModuleID_Communication);
		setEventType(BeEventConst.COMMUNICATIONEVENTTYPE);
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		// override in sub instance
		return null;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		// override in sub instance
	}

	/**
	 * for multi-thread
	 */
	private boolean	isParsed	= false;

	public void setParsed(boolean isParsed) {
		this.isParsed = isParsed;
	}

	/**
	 * parse packet message to event data, you should set packet field value first.
	 * 
	 * @throws BeCommunicationDecodeException -
	 */
	public synchronized void parsePacket() throws BeCommunicationDecodeException {
		if (isParsed) {
			return;
		}

		this.parsePacket(packet);

		isParsed = true;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int elapseTime() {
		return --timeout;
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int type) {
		this.msgType = type;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}

	// support hash
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BeCommunicationEvent))
			return false;

		final BeCommunicationEvent event = (BeCommunicationEvent) o;

		return !(msgType != event.msgType || serialNum != event.serialNum);
	}

	public int hashCode() {
		int result = msgType;
		result = 29 * result + serialNum;
		return result;
	}

	@Deprecated
	public HiveAp getAp() {
		if (ap == null && apMac != null && apMac.trim().length() > 0) {
			ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apMac);
		}
		
		return ap;
	}
	
	public HiveAp getApNoQuery() {
		return ap;
	}

	public void setAp(HiveAp ap) {
		this.ap = ap;
		
		if (ap != null) {
			apMac = ap.getMacAddress();
		}
	}

	public byte[] getPacket() {
		return packet;
	}

	public void setPacket(byte[] packet) {
		this.packet = packet;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apSerialNum) {
		this.apMac = apSerialNum;
	}

	public SimpleHiveAp getSimpleHiveAp() {
		if (null == simpleHiveAp && apMac != null) {
			simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
		}
		
		return simpleHiveAp;
	}
	
	/**
	 * add this api for just return simple hiveap field value.
	 *
	 *@param 
	 *
	 *@return
	 */
	protected SimpleHiveAp getSimpleHiveApNoQuery() {
		return simpleHiveAp;
	}

	public void setSimpleHiveAp(SimpleHiveAp simpleHiveAp) {
		this.simpleHiveAp = simpleHiveAp;
		
		if (simpleHiveAp != null) {
			apMac = simpleHiveAp.getMacAddress();
		}
	}

	public int getSequenceNum() {
		return sequenceNum;
	}

	public void setSequenceNum(int sequenceNum) {
		this.sequenceNum = sequenceNum;
	}

	public long getMessageTimeStamp() {
		return messageTimeStamp;
	}

	public void setMessageTimeStamp(long messageTimeStamp) {
		this.messageTimeStamp = messageTimeStamp;
	}

	public String parseTimeZoneFromAP(byte timeZone) {
		if (timeZone == 0) {
			if (apMac == null || apMac.length() == 0) {
				// get local time zone
				return HmBeOsUtil.getServerTimeZone();
			}

			// get domain time zone
			simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				return HmBeOsUtil.getServerTimeZone();
			}

			HmDomain domain = CacheMgmt.getInstance()
					.getCacheDomainById(simpleHiveAp.getDomainId());
			if (domain == null) {
				return HmBeOsUtil.getServerTimeZone();
			}

			return domain.getTimeZoneString();
		} else {
			timeZone = (byte) (timeZone - 13);
			if (timeZone == 0) {
				return "GMT";
			}

			return "GMT"
					+ (timeZone > 0 ? "+" + String.valueOf(timeZone) : String.valueOf(timeZone));
		}
	}

	public String getMessageTimeZone() {
		return messageTimeZone;
	}

	public void setMessageTimeZone(String messageTimeZone) {
		this.messageTimeZone = messageTimeZone;
	}
}