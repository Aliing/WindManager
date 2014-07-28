package com.ah.be.communication.event;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.ah.be.app.DebugUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.config.create.cli.HmCliSpecialHandling;
import com.ah.util.coder.AhCompressByte;
import com.ah.util.coder.AhDecoder;

/**
 * cli request/response event
 *@filename		BeCliEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2007-11-23 10:42:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 */
public class BeCliEvent extends BeCommunicationEvent {

	private static final long	serialVersionUID		= 1L;

	// // cliSerialNum created by communication module
	// private int cliSerialNum = 0;

	private String[]			clis;

	private static final int	MAXLENGTH_PRECOMPRESS	= 30 * 1024;

	private static final int	MAXLENGTH_POSTCOMPRESS	= 60 * 1024;

	public static final byte	CLITYPE_TIMECONSUMING	= 1;

	public static final byte	CLITYPE_CONFIGURATION	= 2;

	public static final byte	CLITYPE_NORMAL			= 3;

	public static final byte	CLITYPE_ENFORCE			= 4;

	/**
	 * for cli request control, default type is normal.
	 */
	private byte				cliType					= CLITYPE_NORMAL;

	/**
	 * for transaction cli request control.
	 */
	private int					transactionCode			= -1;

	public BeCliEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_CLIREQ;
	}

	/**
	 * Construct method
	 * 
	 * @param clis -
	 * @param cliSerialNum -
	 */
	public BeCliEvent(String[] clis, int sequenceNum) {
		this();
		this.clis = clis;
		this.sequenceNum = sequenceNum;
	}

	/**
	 * build event data to packet message
	 * 
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		try {
			if (simpleHiveAp == null && ap == null) {
				throw new BeCommunicationEncodeException("AP is a necessary field!");
			}

			if (clis == null || clis.length == 0) {
				throw new BeCommunicationEncodeException("cli(s) is(are) required.");
			}

			if (sequenceNum <= 0) {
				throw new BeCommunicationEncodeException("sequenceNum is required.");
			}

			byte[] cliData = getCliData(simpleHiveAp != null ? simpleHiveAp
					.getSoftVer() : ap.getSoftVer());

			/**
			 * AP identifier 's length = 6 +1 + apSerialNum.length()<br>
			 * cli 's length = 6+ ....<br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int cliLen = 6 + cliData.length;
			int bufLength = apIdentifierLen + cliLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CLI);
			buf.putInt(cliLen - 6);
			buf.put(cliData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException("BeCliEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * get cli data
	 * 
	 * @param
	 * @return
	 */
	private byte[] getCliData(String apVer) {
		String s = "";
		for (String cli : clis) {
			s += cli;
		}
		byte[] clisArray;
		try {
			clisArray = s.getBytes("iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			clisArray = s.getBytes();
		}

		if (NmsUtil.compareSoftwareVersion("3.0.2.0", apVer) < 0) {
			// add compress support from 3.0r3
			int originalLen = clisArray.length + 4;
			ByteBuffer _buf = ByteBuffer.allocate(4 + clisArray.length);
			_buf.putInt(sequenceNum);
			_buf.put(clisArray);
			byte[] _clisArray = _buf.array();

			byte flag_compress = BeCommunicationConstant.NOTCOMPRESS;
			if (clisArray.length > AhCompressByte.THRESHOLD) {
				flag_compress = BeCommunicationConstant.COMPRESS;
				int origalLen = _clisArray.length;

				_clisArray = AhCompressByte.compress(_clisArray);

				int resultLen = _clisArray.length;
				DebugUtil.commonDebugInfo("BeCliEvent.getCliData(): compress data array for ap("
						+ apMac + "), original data length is " + origalLen
						+ ", result data length is " + resultLen);
			}

			ByteBuffer buf = ByteBuffer.allocate(_clisArray.length + 5);
			buf.put(flag_compress);
			buf.putInt(originalLen);
			// buf.putInt(cliSerialNum);
			// buf.put(clisArray);
			buf.put(_clisArray);

			// if message length exceed limit, report failure when send request
			if ((flag_compress == BeCommunicationConstant.NOTCOMPRESS && _clisArray.length >= MAXLENGTH_PRECOMPRESS)
					|| (flag_compress == BeCommunicationConstant.COMPRESS && (5 + _clisArray.length) >= MAXLENGTH_POSTCOMPRESS)) {
				result = BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT;
			}

			return buf.array();
		} else {
			ByteBuffer buf = ByteBuffer.allocate(s.length() + 4);
			buf.putInt(sequenceNum);
			buf.put(clisArray);

			if (buf.capacity() >= MAXLENGTH_PRECOMPRESS) {
				result = BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT;
			}

			return buf.array();
		}
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			ByteBuffer buf = ByteBuffer.wrap(data);
			while (buf.hasRemaining()) {
				short msgType = buf.getShort();
				int msgLen = buf.getInt();

				if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER) {
					byte macLen = buf.get();
					apMac = AhDecoder.bytes2String(buf, AhDecoder.byte2int(macLen)).toUpperCase();
				} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_RESULTDESCRIPTOR) {
					// check length valid
					if (msgLen != 1) {
						throw new BeCommunicationDecodeException(
								"Invalid messge length in BeScriptConfigEvent");
					}

					result = buf.get();
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeCliEvent, type value = " + msgType);
					// DebugUtil
					// .commonDebugWarn("Invalid messge type in BeCliEvent,
					// type value = "
					// + msgType);
					//
					// return;
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException("BeCliEvent.parsePacket() catch exception", e);
		}
	}

	public String[] getClis() {
		return clis;
	}

	public void setClis(String[] clis) {
		this.clis = clis;
		if(NmsUtil.isHMForOEM() && this.clis != null && this.clis.length > 0){
			String cliStr = "";
			for(int i=0; i<this.clis.length; i++){
				cliStr += this.clis[i];
				if(!cliStr.endsWith("\n")){
					cliStr += "\n";
				}
			}
			if(cliStr != null && !"".equals(cliStr)){
				String[][] newSTR = {
						{"hive", "cluster"},
						{"aerohive", "Black-Box"},
						{"hivemanager", "SmartPath-EMS"},
						{"hiveui", "clusterui"}
				};
				cliStr = HmCliSpecialHandling.getOEMClis(cliStr, newSTR);
				String[] resClis = cliStr.split("\n");
				for(int j=0; j<resClis.length; j++){
					if(!resClis[j].endsWith("\n")){
						resClis[j] += "\n";
					}
				}
				this.clis = resClis;
			}
		}
	}

	public byte getCliType() {
		return cliType;
	}

	public void setCliType(byte cliType) {
		this.cliType = cliType;
	}

	public boolean isTimeConsuming() {
		return cliType == CLITYPE_TIMECONSUMING;
	}

	public boolean isConfiguration() {
		return cliType == CLITYPE_CONFIGURATION;
	}

	public boolean isNormal() {
		return cliType == CLITYPE_NORMAL;
	}

	public boolean isEnforce() {
		return cliType == CLITYPE_ENFORCE;
	}

	public int getTransactionCode() {
		return transactionCode;
	}

	public boolean isTrasactionConfigurationCli() {
		return transactionCode >= 0;
	}

	public void setTransactionCode(int transactionCode) {
		this.transactionCode = transactionCode;
	}
}