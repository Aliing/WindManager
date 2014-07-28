package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class UserOperation extends AbstractElement {

	private static final long	serialVersionUID				= 1L;

	private static final Tracer	log								= new Tracer(UserOperation.class
																		.getSimpleName());

	public static final byte	USER_OPER_TYPE_CREATE			= 1;

	public static final byte	USER_OPER_TYPE_REMOVE			= 2;

	public static final byte	USER_OPER_TYPE_REINIT_PASSWORD	= 3;

	public static final byte	USER_OPER_TYPE_SEND_CREDENT		= 4;

	public static final byte	USER_OPER_TYPE_SEND_NEWURL		= 5;

	public static final byte	USER_GROUP_ATTR_MONITOR			= 0;

	public static final byte	USER_GROUP_ATTR_ADMIN			= 1;

	public static final byte	USER_GROUP_ATTR_CONFIG			= 2;

	/* User Name */
	private String				userName						= "";

	/* Operation Type */
	private byte				operType;

	/* VHM Name */
	private String				vhmName							= "";

	/* Password */
	private String				password						= "";

	/* User Group Attribute */
	private byte				userGroupAttr;

	/* User Full Name */
	private String				userFullName					= "";

	/* Email Address */
	private String				emailAddr						= "";

	/* Login URL */
	private String				loginUrl						= "";

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public UserOperation() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		if (userName != null) {
			this.userName = userName;
		}
	}

	public byte getOperType() {
		return operType;
	}

	public void setOperType(byte operType) {
		this.operType = operType;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		if (vhmName != null) {
			this.vhmName = vhmName;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		if (password != null) {
			this.password = password;
		}
	}

	public byte getUserGroupAttr() {
		return userGroupAttr;
	}

	public void setUserGroupAttr(byte userGroupAttr) {
		this.userGroupAttr = userGroupAttr;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		if (userFullName != null) {
			this.userFullName = userFullName;
		}
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		if (emailAddr != null) {
			this.emailAddr = emailAddr;
		}
	}

	@Override
	public short getElemType() {
		return USER_OPERATION;
	}

	@Override
	public String getElemName() {
		return "User Operation";
	}

	/*-
	 * User Operation
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        User Name Length       |         User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        VHM Name Length        |         VHM Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Operation Type |User Group Attr|     User Password Length      |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                        User Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |     User Full Name Length     |       User Full Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Email Address Length     |       Email Address ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * Length > 12
	 *
	 * Operation Type :
	 *
	 * 1 - create user
	 * 2 - remove user
	 * 3 - change password
	 *
	 * User Group Attribute:
	 *
	 * 0 - monitor
	 * 1 - admin
	 * 2 - config
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

			/* User Name */
			log.debug("encode", "User Name: " + userName);
			byte[] toBytes = userName.getBytes("iso-8859-1");
			int userNameLen = toBytes.length;
			bb.putShort((short) userNameLen);
			bb.put(toBytes);

			/* VHM Name */
			log.debug("encode", "VHM Name: " + vhmName);
			toBytes = vhmName.getBytes("iso-8859-1");
			int vhmNameLen = toBytes.length;
			bb.putShort((short) vhmNameLen);
			bb.put(toBytes);

			/* Operation Type */
			log.debug("encode", "Operation Type: " + operType);
			bb.put(operType);

			/* User Group Attribute */
			log.debug("encode", "User Group Attribute: " + userGroupAttr);
			bb.put(userGroupAttr);

			/* Password */
			log.debug("encode", "Password: " + password);
			toBytes = password.getBytes("iso-8859-1");
			int pwdLen = toBytes.length;
			bb.putShort((short) pwdLen);
			bb.put(toBytes);

			/* User Full Name */
			log.debug("encode", "User Full Name: " + userFullName);
			toBytes = userFullName.getBytes("iso-8859-1");
			int userFullNameLen = toBytes.length;
			bb.putShort((short) userFullNameLen);
			bb.put(toBytes);

			/* Email Address */
			log.debug("encode", "Email Address: " + emailAddr);
			toBytes = emailAddr.getBytes("iso-8859-1");
			int emailAddrLen = toBytes.length;
			bb.putShort((short) emailAddrLen);
			bb.put(toBytes);

			/* Login URL */
			toBytes = loginUrl.getBytes("iso-8859-1");
			int loginUrl = toBytes.length;
			bb.putShort((short) loginUrl);
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
	 * User Operation
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        User Name Length       |         User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        VHM Name Length        |         VHM Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Operation Type |User Group Attr|     User Password Length      |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                        User Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |     User Full Name Length     |       User Full Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Email Address Length     |       Email Address ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * Length > 12
	 *
	 * Operation Type :
	 *
	 * 1 - create user
	 * 2 - remove user
	 * 3 - change password
	 *
	 * User Group Attribute:
	 *
	 * 0 - monitor
	 * 1 - admin
	 * 2 - config
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* User Name */
			short userNameLen = bb.getShort();
			userName = AhDecoder.bytes2String(bb, userNameLen);
			log.debug("decode", "User Name[len/value]: " + userNameLen + "/" + userName);

			/* VHM Name */
			short vhmNameLen = bb.getShort();
			vhmName = AhDecoder.bytes2String(bb, vhmNameLen);
			log.debug("decode", "VHM Name[len/value]: " + vhmNameLen + "/" + vhmName);

			/* Operation Type */
			operType = bb.get();
			log.debug("decode", "Operation Type: " + operType);

			/* User Group Attribute */
			userGroupAttr = bb.get();
			log.debug("decode", "User Group Attribute: " + userGroupAttr);

			/* Password */
			short pwdLen = bb.getShort();
			password = AhDecoder.bytes2String(bb, pwdLen);
			log.debug("decode", "Password[len/value]: " + pwdLen + "/" + password);

			/* User Name */
			short userFullNameLen = bb.getShort();
			userFullName = AhDecoder.bytes2String(bb, userFullNameLen);
			log.debug("decode", "User Full Name[len/value]: " + userFullNameLen + "/"
					+ userFullName);

			/* Email Address */
			short emailAddrLen = bb.getShort();
			emailAddr = AhDecoder.bytes2String(bb, emailAddrLen);
			log.debug("decode", "Email Address[len/value]: " + emailAddrLen + "/" + emailAddr);

			/* Login URL */
			short urlLen = bb.getShort();
			loginUrl = AhDecoder.bytes2String(bb, urlLen);

			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException(
					"Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

}