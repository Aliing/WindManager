package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeShowCaptureStatusResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-2-10 10:35:56
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeShowCaptureStatusResultEvent extends BeCapwapClientResultEvent {

	private byte				wifiInterface;

	public static final byte	CAPTURING_NOINPROGRESS	= 0;

	public static final byte	CAPTURING_INPROGRESS	= 1;

	private byte				capturing;

	private int					totalFramesCaptured;

	private int					rxFramesCaptured;

	private int					txFramesCaptured;

	private String				saveFileName;

	private String				lastResultFileName;

	public BeShowCaptureStatusResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWCAPTURESTATUS;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			wifiInterface = buf.get();
			capturing = (buf.get() == CAPTURING_NOINPROGRESS) ? CAPTURING_NOINPROGRESS
					: CAPTURING_INPROGRESS;
			totalFramesCaptured =buf.getInt();
			txFramesCaptured = buf.getInt();
			rxFramesCaptured = buf.getInt();
			byte len = buf.get();
			saveFileName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
			len = buf.get();
			lastResultFileName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(len));
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeShowCaptureStatusResultEvent.parsePacket() catch exception", e);
		}
	}

	public byte getCapturing() {
		return capturing;
	}

	public void setCapturing(byte capturing) {
		this.capturing = capturing;
	}

	public String getLastResultFileName() {
		return lastResultFileName;
	}

	public void setLastResultFileName(String lastResultFileName) {
		this.lastResultFileName = lastResultFileName;
	}

	public int getRxFramesCaptured() {
		return rxFramesCaptured;
	}

	public void setRxFramesCaptured(int rxFramesCaptured) {
		this.rxFramesCaptured = rxFramesCaptured;
	}

	public String getSaveFileName() {
		return saveFileName;
	}

	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	public int getTotalFramesCaptured() {
		return totalFramesCaptured;
	}

	public void setTotalFramesCaptured(int totalFramesCaptured) {
		this.totalFramesCaptured = totalFramesCaptured;
	}

	public int getTxFramesCaptured() {
		return txFramesCaptured;
	}

	public void setTxFramesCaptured(int txFramesCaptured) {
		this.txFramesCaptured = txFramesCaptured;
	}

	public byte getWifiInterface() {
		return wifiInterface;
	}

	public void setWifiInterface(byte wifiInterface) {
		this.wifiInterface = wifiInterface;
	}
}
