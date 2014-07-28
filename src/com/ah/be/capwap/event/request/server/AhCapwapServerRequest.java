/**
 *@filename		AhCapwapServerRequest.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event.request.server;

// aerohive import
import com.ah.be.capwap.AhCapwapEncodeException;
import com.ah.be.capwap.event.AhCapwapEvent;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public abstract class AhCapwapServerRequest extends AhCapwapEvent {

	private static final long	serialVersionUID	= 1L;

	public static final int AH_CAPWAP_REQUEST_SUCCESS = 0;
	public static final int AH_CAPWAP_REQUEST_TIMEOUT = 1;
	public static final int AH_CAPWAP_REQUEST_DISCONNECT = 2;

	protected byte[] reqPacket;
	protected int reqRet;
	protected int tranCount = MAXIMUN_TRANSMIT_COUNT;
	protected int retranInterval = RETRANSMIT_INTERVAL;
	protected int timer;
	protected int retranCount;

	protected AhCapwapServerRequest() {
		super();
	}

	protected AhCapwapServerRequest(int type) {
		super(type);
	}

	protected AhCapwapServerRequest(String serialNum) {
		super(serialNum);
	}

	protected AhCapwapServerRequest(int type, String serialNum) {
		super(type, serialNum);
	}

	protected AhCapwapServerRequest(int type, String serialNum, byte[] reqPacket) {
		this(type, serialNum);
		this.reqPacket = reqPacket;
	}

	public byte[] getReqPacket() {
		return reqPacket;
	}

	public void setReqPacket(byte[] reqPacket) {
		this.reqPacket = reqPacket;
	}

	public int getReqRet() {
		return reqRet;
	}

	public void setReqRet(int reqRet) {
		this.reqRet = reqRet;
	}

	public int getTranCount() {
		return tranCount;
	}

	public void setTranCount(int tranCount) {
		this.tranCount = tranCount;
	}

	public int getRetranInterval() {
		return retranInterval;
	}

	public void setRetranInterval(int retranInterval) {
		this.retranInterval = retranInterval;
	}

	private void initTimer() {
		timer = retranInterval;
	}

	public void startTimer() {
		initTimer();
	}

	/**
	 * Decrease timer for capwap request to judge whether the request needs to be retransmited or not.
	 * <p>
	 * @param decValue  Decrease value for timer.
	 * @return  Indicate whether the request needs to be retransmited or not.
	 */
	public synchronized boolean decreaseTimer(int decValue) {
		timer -= decValue;

		if (timer <= 0) {
			retranCount++;
			initTimer();

			return true;
		} else {
			return false;
		}
	}

	public boolean isTimeout() {
		return retranCount >= tranCount;
	}

	public abstract byte[] buildPacket() throws AhCapwapEncodeException;

	public abstract String getRequestName();

}