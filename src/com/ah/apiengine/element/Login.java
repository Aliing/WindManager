package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class Login extends AbstractElement {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(Login.class.getSimpleName());

	/* API-Engine Super User Name */
	private String engineUserName = "";

	/* API-Engine Super User Password */
	private String enginePassword = "";

	/* API-Client Super User Name */
	private String clientUserName = "";

	/* API-Client Super User Password */
	private String clientPassword = "";

	public Login() {
		super();
	}

	public String getEngineUserName() {
		return engineUserName;
	}

	public void setEngineUserName(String engineUserName) {
		if (engineUserName != null) {
			this.engineUserName = engineUserName;
		}
	}

	public String getEnginePassword() {
		return enginePassword;
	}

	public void setEnginePassword(String enginePassword) {
		if (enginePassword != null) {
			this.enginePassword = enginePassword;
		}
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public void setClientUserName(String clientUserName) {
		if (clientUserName != null) {
			this.clientUserName = clientUserName;
		}
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public void setClientPassword(String clientPassword) {
		if (clientPassword != null) {
			this.clientPassword = clientPassword;
		}
	}

	@Override
	public short getElemType() {
		return LOGIN;
	}

	@Override
	public String getElemName() {
		return "Login";
	}

	/*-
	 * Login
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  API-Engine User Name Length  |    API-Engine User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |API-Engine User Password Length|  API-Engine User Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  API-Client User Name Length  |    API-Client User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |API-Client User Password Length|  API-Client User Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * Length: > 8
	 */
	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.debug("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.debug("encode", "Start Position: " + startPos);

			/* API-Engine User Name */
			log.debug("encode", "API-Engine User Name: " + engineUserName);
			byte[] toBytes = engineUserName.getBytes("iso-8859-1");
			int engineUserNameLen = toBytes.length;
			bb.putShort((short) engineUserNameLen);
			bb.put(toBytes);

			/* API-Engine User Password */
			log.debug("encode", "API-Engine Password: " + enginePassword);
			toBytes = enginePassword.getBytes("iso-8859-1");
			int engineUserPwdLen = toBytes.length;
			bb.putShort((short) engineUserPwdLen);
			bb.put(toBytes);

			/* API-Client User Name */
			log.debug("encode", "API-Client User Name: " + clientUserName);
			toBytes = clientUserName.getBytes("iso-8859-1");
			int clientUserNameLen = toBytes.length;
			bb.putShort((short) clientUserNameLen);
			bb.put(toBytes);

			/* API-Client User Password */
			log.debug("encode", "API-Client Password: " + clientPassword);
			toBytes = clientPassword.getBytes("iso-8859-1");
			int clientUserPwdLen = toBytes.length;
			bb.putShort((short) clientUserPwdLen);
			bb.put(toBytes);

			// End Position
			int endPos = bb.position();
			log.debug("encode", "End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.debug("encode", "Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	/*-
	 * Login
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  API-Engine User Name Length  |    API-Engine User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |API-Engine User Password Length|    API-Engine User Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  API-Client User Name Length  |    API-Client User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |API-Client User Password Length|    API-Client User Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * Length: > 8
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* API-Engine Super User Name */
			short engineUserNameLen = bb.getShort();
			engineUserName = AhDecoder.bytes2String(bb, engineUserNameLen);
			log.debug("decode", "API-Engine Super User Name[len/value]: " + engineUserNameLen + "/" + engineUserName);

			/* API-Engine Super User Password */
			short enginePwdLen = bb.getShort();
			enginePassword = AhDecoder.bytes2String(bb, enginePwdLen);
			log.debug("decode", "API-Engine Super User Password[len/value]: " + enginePwdLen + "/" + enginePassword);

			/* API-Client Super User Name */
			short clientUserNameLen = bb.getShort();
			clientUserName = AhDecoder.bytes2String(bb, clientUserNameLen);
			log.debug("decode", "API-Client Super User Name[len/value]: " + clientUserNameLen + "/" + clientUserName);

			/* API-Client Super User Password */
			short clientPwdLen = bb.getShort();
			clientPassword = AhDecoder.bytes2String(bb, clientPwdLen);
			log.debug("decode", "API-Client Super User Password[len/value]: " + clientPwdLen + "/" + clientPassword);
			
			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException("Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

}