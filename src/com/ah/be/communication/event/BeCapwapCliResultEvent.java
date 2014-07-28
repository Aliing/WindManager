package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhCompressByte;
import com.ah.util.coder.AhDecoder;

/**
 *  CLI RESULT message element type, modified from BeCapwapFileDownloadResultEvent
 *@filename		BeCapwapCliResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-23 02:05:50
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCapwapCliResultEvent extends BeAPWTPEvent {

	private static final long serialVersionUID = 1L;

	// private int cliSerialNum;
	private byte cliResult;
	private String resultMsg;

	private int errorCode;

	public BeCapwapCliResultEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(getWtpMsgData());

			SimpleHiveAp cacheAp = CacheMgmt.getInstance().getSimpleHiveAp(
					apMac);
			if (NmsUtil.compareSoftwareVersion("3.0.2.0", cacheAp.getSoftVer()) < 0) {
				byte compressFlag = buf.get();
				buf.getInt();// int originalLen =

				if (compressFlag == BeCommunicationConstant.COMPRESS) {
					byte[] bytes_cli = Arrays.copyOfRange(buf.array(), 5, buf
							.array().length);
					byte[] unCompress = AhCompressByte.uncompress(bytes_cli);
					ByteBuffer newBuf = ByteBuffer.wrap(unCompress);
					sequenceNum = newBuf.getInt();
					cliResult = newBuf.get();
					int cliLen = newBuf.getInt();
					boolean hasErrorCode = NmsUtil.compareSoftwareVersion(
							"3.5.0.0", cacheAp.getSoftVer()) <= 0;
					if (hasErrorCode) {
						errorCode = newBuf.getInt();
					}
					resultMsg = AhDecoder.bytes2String(newBuf,
							hasErrorCode ? cliLen - 4 : cliLen);
				} else if (compressFlag == BeCommunicationConstant.NOTCOMPRESS) {
					sequenceNum = buf.getInt();
					cliResult = buf.get();
					int cliLen = buf.getInt();
					boolean hasErrorCode = NmsUtil.compareSoftwareVersion(
							"3.5.0.0", cacheAp.getSoftVer()) <= 0;
					if (hasErrorCode) {
						errorCode = buf.getInt();
					}
					resultMsg = AhDecoder.bytes2String(buf,
							hasErrorCode ? cliLen - 4 : cliLen);
				} else {
					throw new BeCommunicationDecodeException(
							"BeCapwapCliResultEvent.parsePacket() invalid compress flag");
				}
			} else {
				sequenceNum = buf.getInt();
				cliResult = buf.get();
				short errorCliLen = buf.getShort();
				resultMsg = AhDecoder.bytes2String(buf, AhDecoder
						.short2int(errorCliLen));
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeCapwapCliResultEvent.parsePacket() catch exception", e);
		}
	}

	public void setResultMsg(String errorCli) {
		this.resultMsg = errorCli;
	}

	public byte getCliResult() {
		return cliResult;
	}

	public void setCliResult(byte downloadResult) {
		this.cliResult = downloadResult;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public boolean isCliSuccessful() {
		return cliResult == BeCommunicationConstant.CLIRESULT_SUCCESS;
	}

	public String getCliSucceedMessage() {
		if (isCliSuccessful()) {
			return resultMsg;
		}
		return null;
	}

	public String getErrorCli() {
		if (!isCliSuccessful()) {
			if (null != resultMsg && resultMsg.endsWith("\n")) {
				resultMsg = resultMsg.substring(0, resultMsg.length() - 1);
			}
			return resultMsg;
		}
		return null;
	}

	public String getHiveOSErrorMessage() {
		if (errorCode > 0) {
			return NmsUtil.getHiveosErrorMessage(errorCode);
		} else {
			return "";
		}
	}
	
//	public boolean isFailedContinue(short hiveApModel){
//		/**
//		 * The Aerohive device was unable to upgrade image to flash memory 
//		 * because the new image has the same version as the one in flash. 
//		 * You may need to get a different image and try again.
//		 */
//		String str_code = AhDecoder.bytes2hex(
//				AhDecoder.toByteArray(errorCode, 4), true).toLowerCase();
//		
//		if(!"0x00030041".equals(str_code)){
//			return false;
//		}
//		DeviceInfo dInfo = NmsUtil.getDeviceInfo(hiveApModel);
//		if(dInfo != null && dInfo.getIntegerValue(DeviceInfo.SPT_DEVICE_IMAGE_COUNTS) < 2){
//			//only one image platform
//			return true;
//		}else{
//			return false;
//		}
//	}

}