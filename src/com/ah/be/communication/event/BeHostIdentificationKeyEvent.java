package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.Map;

import com.ah.be.app.DebugUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.util.coder.AhCompressByte;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeHostIdentificationKeyEvent extends BeCommunicationEvent {

	public static final byte	KEYTYPE_PUBLICKEY	= 0;

	public static final byte	KEYTYPE_PRIVATEKEY	= 1;

	/**
	 * key: key type<br>
	 * value: key info
	 */
	private Map<Byte, String>	keyMap				= null;

	private short attemptCount = 0;

	public void setAttemptCount(short attemptCount){
		this.attemptCount = attemptCount;
	}

	public short getAttemptCount(){
		return attemptCount;
	}

	public BeHostIdentificationKeyEvent() {
		super();
		msgType = BeCommunicationConstant.MESSAGETYPE_HOSTIDENTIFICATIONKEYREQ;
	}

	/**
	 * build event data to packet message
	 *
	 * @return BeCommunicationMessageData
	 * @throws BeCommunicationEncodeException -
	 */
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		if (keyMap == null) {
			throw new BeCommunicationEncodeException("keyMap is a necessary field!");
		}

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}

		try {
			byte[] keyData = getKeyData();

			/**
			 * AP identifier 's length = 6 +1 + apSerialNum.length()<br>
			 * host identification key 's length = 6+ ....<br>
			 */
			int apIdentifierLen = 7 + apMac.length();
			int keyDataLen = 6 + keyData.length;
			int bufLength = apIdentifierLen + keyDataLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_HOSTIDENTIFICATIONKEY);
			buf.putInt(keyDataLen - 6);
			buf.put(keyData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeHostIdentificationKeyEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * get key byte array
	 *
	 * @param
	 *
	 * @return
	 */
	private byte[] getKeyData() {
		int keyInfoLen = 0;
		for (Byte keyType : keyMap.keySet()) {
			String key = keyMap.get(keyType);
			keyInfoLen += 5 + key.length();
		}

		ByteBuffer keyInfoBuf = ByteBuffer.allocate(keyInfoLen);
		for (Byte keyType : keyMap.keySet()) {
			String key = keyMap.get(keyType);
			keyInfoBuf.put(keyType);
			keyInfoBuf.putInt(key.length());
			keyInfoBuf.put(key.getBytes());
		}

		int originalLen = keyInfoLen + 4;
		ByteBuffer _buf = ByteBuffer.allocate(originalLen);
		_buf.putInt(sequenceNum);
		_buf.put(keyInfoBuf.array());
		byte[] _array = _buf.array();

		byte flag_compress = BeCommunicationConstant.NOTCOMPRESS;
		if (_array.length > AhCompressByte.THRESHOLD) {
			flag_compress = BeCommunicationConstant.COMPRESS;
			int preLen = _array.length;

			_array = AhCompressByte.compress(_array);

			int postLen = _array.length;
			DebugUtil
					.commonDebugInfo("BeHostIdentificationKeyEvent.getKeyData(): compress data array for ap("
							+ apMac
							+ "), original data length is "
							+ preLen
							+ ", result data length is " + postLen);
		}

		ByteBuffer buf = ByteBuffer.allocate(_array.length + 5);
		buf.put(flag_compress);
		buf.putInt(originalLen);
		buf.put(_array);

		return buf.array();
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
								"Invalid messge length in BeHostIdentificationKeyEvent");
					}

					result = buf.get();
				} else {
					throw new BeCommunicationDecodeException(
							"Invalid messge element type in BeHostIdentificationKeyEvent, type value = "
									+ msgType);
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeHostIdentificationKeyEvent.parsePacket() catch exception", e);
		}
	}

	public Map<Byte, String> getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(Map<Byte, String> keyMap) {
		this.keyMap = keyMap;
	}
}
