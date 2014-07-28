package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class VhmOperation extends AbstractElement {

	private static final long	serialVersionUID			= 1L;

	private static final Tracer	log							= new Tracer(VhmOperation.class
																	.getSimpleName());

	/**
	 * VHM Operation Types
	 */
	public static final byte	VHM_OPER_TYPE_CREATE		= 1;

	public static final byte	VHM_OPER_TYPE_REMOVE		= 2;

	public static final byte	VHM_OPER_TYPE_STATUSCHANGE	= 3;

	public static final byte	VHM_OPER_TYPE_BACKUP		= 4;

	public static final byte	VHM_OPER_TYPE_RESTORE		= 5;

	public static final byte	VHM_OPER_TYPE_MODIFY		= 6;

	public static final byte	VHM_OPER_TYPE_SEND_CREDENT	= 7;

	/**
	 * VHM Status Types
	 */
	public static final byte	VHM_STATUS_NORMAL			= 0;

	public static final byte	VHM_STATUS_RESTORE			= 1;

	public static final byte	VHM_STATUS_DISABLE			= 2;

	public static final byte	VHM_STATUS_BACKUP			= 3;

	/**
	 * GM Light Capability
	 */
	public static final byte	GM_LIGHT_ENABLED			= 1;

	public static final byte	GM_LIGHT_DISABLED			= 0;

	/**
	 * User Account Type
	 */
	public static final short	USER_TYPE_EVAL				= 1;

	public static final short	USER_TYPE_PLAN_EVAL			= 2;

	public static final short	USER_TYPE_REGULAR			= 3;

	/* VHM Name */
	private String				vhmName						= "";

	/* Operation Type */
	private byte				operType;

	/* Number of HiveAPs */
	private int					apNum;

	/* VHM Status */
	private byte				status;

	/* GM Light */
	private byte				gmLight;

	/* DNS URL */
	private String				dnsUrl						= "";

	/* User Account Type */
	private short				userAccountType				= USER_TYPE_EVAL;

	/* User Name */
	private String				userName					= "";

	/* User Full Name */
	private String				userFullName				= "";

	/* Email Address */
	private String				emailAddr					= "";

	/* SSH Server */
	private String				sshServer					= "127.0.0.1";

	/* SSH Server Port */
	private int					sshPort						= 22;

	/* SSH Server User Name */
	private String				sshUserName					= "";

	/* SSH Server Password */
	private String				sshPwd						= "";

	/* SSH Path */
	private String				sshPath						= "";

	/* Backup/Restore File Name */
	private String				fileName					= "";

	/* Notify Flag */
	private boolean				notifyFlag					= true;

	/* VHM-ID */
	private String				vhmId						= "";

	/* Valid Days */
	private int					validDays					= -1;

	private String				ccEmailAddr					= "";

	private boolean				enableEnterprise			= false;

	public String getCcEmailAddr() {
		return ccEmailAddr;
	}

	public void setCcEmailAddr(String ccEmailAddr) {
		this.ccEmailAddr = ccEmailAddr;
	}

	public boolean isEnableEnterprise() {
		return enableEnterprise;
	}

	public void setEnableEnterprise(boolean enableEnterprise) {
		this.enableEnterprise = enableEnterprise;
	}

	public int getValidDays() {
		return validDays;
	}

	public void setValidDays(int validDays) {
		this.validDays = validDays;
	}

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

	public boolean isNotifyFlag() {
		return notifyFlag;
	}

	public void setNotifyFlag(boolean notifyFlag) {
		this.notifyFlag = notifyFlag;
	}

	public VhmOperation() {
		super();
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		if (vhmName != null) {
			this.vhmName = vhmName;
		}
	}

	public byte getOperType() {
		return operType;
	}

	public void setOperType(byte operType) {
		this.operType = operType;
	}

	public int getApNum() {
		return apNum;
	}

	public void setApNum(int apNum) {
		this.apNum = apNum;
	}

	public byte getStatus() {
		return status;
	}

	public byte getGmLight() {
		return gmLight;
	}

	public void setGmLight(byte gmLight) {
		this.gmLight = gmLight;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getDnsUrl() {
		return dnsUrl;
	}

	public void setDnsUrl(String dnsUrl) {
		if (dnsUrl != null) {
			this.dnsUrl = dnsUrl;
		}
	}

	public short getUserAccountType() {
		return userAccountType;
	}

	public void setUserAccountType(short userAccountType) {
		this.userAccountType = userAccountType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		if (userName != null) {
			this.userName = userName;
		}
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

	public String getSshServer() {
		return sshServer;
	}

	public void setSshServer(String sshServer) {
		if (sshServer != null) {
			this.sshServer = sshServer;
		}
	}

	public int getSshPort() {
		return sshPort;
	}

	public void setSshPort(int sshPort) {
		this.sshPort = sshPort;
	}

	public String getSshUserName() {
		return sshUserName;
	}

	public void setSshUserName(String sshUserName) {
		if (sshUserName != null) {
			this.sshUserName = sshUserName;
		}
	}

	public String getSshPwd() {
		return sshPwd;
	}

	public void setSshPwd(String sshPwd) {
		if (sshPwd != null) {
			this.sshPwd = sshPwd;
		}
	}

	public String getSshPath() {
		return sshPath;
	}

	public void setSshPath(String sshPath) {
		if (sshPath != null) {
			this.sshPath = sshPath;
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		if (fileName != null) {
			this.fileName = fileName;
		}
	}

	@Override
	public short getElemType() {
		return VHM_OPERATION;
	}

	@Override
	public String getElemName() {
		return "VHM Operation";
	}

	/*-
	 * VHM Operation
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        VHM Name Length        |          VHM Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * | Operation Type|               Number of HiveAPs
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   Num of HiveAPs|     Status    |    GM Light   | DNS URL Length
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   DNS URL Length|                  DNS URL ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |       User Account Type       |        User Name Length       |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                         User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |     User Full Name Length     |       User Full Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Email Address Length     |       Email Address ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                     SSH Server IP Address                     |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                        SSH Server Port                        |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  SSH Server User Name Length  |    SSH Server User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |   SSH Server Password Length  |    SSH Server Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        SSH Path Length        |          SSH Path ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Backup/Restore File Name Length|  Backup/Restore File Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * Length > 35
	 *
	 * Operation Type:
	 *
	 * 1 - Create VHM
	 * 2 - Remove VHM
	 * 3 - Disable VHM
	 * 4 - Backup VHM
	 * 5 - Restore VHM
	 *
	 * Status:
	 *
	 * 0 - Normal
	 * 1 - Restore
	 * 2 - Disable
	 * 3 - Backup
	 *
	 * GM Light:
	 *
	 * 0 - Disable Guest Manager Light
	 * 1 - Enable Guest Manager Light
	 *
	 * User Account Type:
	 *
	 * 1 - Evaluation
	 * 2 - Plan Evaluation
	 * 3 - Regular
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

			/* VHM Name */
			log.debug("encode", "VHM Name: " + vhmName);
			byte[] toBytes = vhmName.getBytes("iso-8859-1");
			int vhmNameLen = toBytes.length;
			bb.putShort((short) vhmNameLen);
			bb.put(toBytes);

			/* Operation Type */
			log.debug("encode", "Operation Type: " + operType);
			bb.put(operType);

			/* Number of HiveAPs */
			log.debug("encode", "Number of HiveAPs: " + apNum);
			bb.putInt(apNum);

			/* Status */
			log.debug("encode", "Status: " + status);
			bb.put(status);

			/* GM Light */
			log.debug("encode", "GM Light: " + gmLight);
			bb.put(gmLight);

			/* DNS URL */
			log.debug("encode", "DNS URL: " + dnsUrl);
			toBytes = dnsUrl.getBytes("iso-8859-1");
			int dnsUrlLen = toBytes.length;
			bb.putShort((short) dnsUrlLen);
			bb.put(toBytes);

			/* User Account Type */
			log.debug("encode", "User Account Type: " + userAccountType);
			bb.putShort(userAccountType);

			/* User Name */
			log.debug("encode", "User Name: " + userName);
			toBytes = userName.getBytes("iso-8859-1");
			int userNameLen = toBytes.length;
			bb.putShort((short) userNameLen);
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

			/* SSH Server IP */
			// log.debug("encode", "SSH Server: " + sshServer);
			// int i_ip = new Long(AhEncoder.ip2Long(sshServer)).intValue();
			bb.putInt(0);

			/* SSH Server Port */
			log.debug("encode", "SSH Server Port: " + sshPort);
			bb.putInt(sshPort);

			/* SSH Server User Name */
			log.debug("encode", "SSH Server User Name: " + sshUserName);
			toBytes = sshUserName.getBytes("iso-8859-1");
			int sshUserNameLen = toBytes.length;
			bb.putShort((short) sshUserNameLen);
			bb.put(toBytes);

			/* SSH Server Password */
			log.debug("encode", "SSH Server Password: " + sshPwd);
			toBytes = sshPwd.getBytes("iso-8859-1");
			int sshPwdLen = toBytes.length;
			bb.putShort((short) sshPwdLen);
			bb.put(toBytes);

			/* SSH Path */
			log.debug("encode", "SSH Path: " + sshPath);
			toBytes = sshPath.getBytes("iso-8859-1");
			int sshPathLen = toBytes.length;
			bb.putShort((short) sshPathLen);
			bb.put(toBytes);

			/* File Name */
			log.debug("encode", "File Name: " + fileName);
			toBytes = fileName.getBytes("iso-8859-1");
			int fileNameLen = toBytes.length;
			bb.putShort((short) fileNameLen);
			bb.put(toBytes);

			/* Notify Flag */
			if (notifyFlag) {
				bb.put((byte) 0x01);
			} else {
				bb.put((byte) 0x00);
			}

			int len = -1;
			/* VHM-ID */
			toBytes = vhmId.getBytes("iso-8859-1");
			len = toBytes.length;
			bb.putShort((short) len);
			bb.put(toBytes);

			/* Valid Days */
			bb.putInt(validDays);

			Tool.putString(bb, sshServer);
			Tool.putString(bb, ccEmailAddr);

			if (enableEnterprise) {
				bb.put((byte) 0x01);
			} else {
				bb.put((byte) 0x00);
			}

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
	 * VHM Operation
	 *
	 * 0                   1                   2                   3
	 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        VHM Name Length        |          VHM Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * | Operation Type|               Number of HiveAPs
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   Num of HiveAPs|     Status    |    GM Light   | DNS URL Length
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *   DNS URL Length|                  DNS URL ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |       User Account Type       |        User Name Length       |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                         User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |     User Full Name Length     |       User Full Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Email Address Length     |       Email Address ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                     SSH Server IP Address                     |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |                        SSH Server Port                        |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  SSH Server User Name Length  |    SSH Server User Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |   SSH Server Password Length  |    SSH Server Password ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |        SSH Path Length        |          SSH Path ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |Backup/Restore File Name Length|  Backup/Restore File Name ...
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *
	 * Length > 35
	 *
	 * Operation Type:
	 *
	 * 1 - Create VHM
	 * 2 - Remove VHM
	 * 3 - Disable VHM
	 * 4 - Backup VHM
	 * 5 - Restore VHM
	 *
	 * Status:
	 *
	 * 0 - Normal
	 * 1 - Restore
	 * 2 - Disable
	 * 3 - Backup
	 *
	 * GM Light:
	 *
	 * 0 - Disable Guest Manager Light
	 * 1 - Enable Guest Manager Light
	 *
	 * User Account Type:
	 *
	 * 1 - Evaluation
	 * 2 - Plan Evaluation
	 * 3 - Regular
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* VHM Name */
			short vhmNameLen = bb.getShort();
			vhmName = AhDecoder.bytes2String(bb, vhmNameLen);
			log.debug("decode", "VHM Name[len/value]: " + vhmNameLen + "/" + vhmName);

			/* Operation Type */
			operType = bb.get();
			log.debug("decode", "Operation Type: " + operType);

			/* Number of HiveAPs */
			apNum = bb.getInt();
			log.debug("decode", "Number of HiveAPs: " + apNum);

			/* Status */
			status = bb.get();
			log.debug("decode", "Status: " + status);

			/* GM Light */
			gmLight = bb.get();
			log.debug("decode", "GM Light: " + gmLight);

			/* DNS URL */
			short dnsUrlLen = bb.getShort();
			dnsUrl = AhDecoder.bytes2String(bb, dnsUrlLen);
			log.debug("decode", "DNS URL[len/value]: " + dnsUrlLen + "/" + dnsUrl);

			/* User Account Type */
			userAccountType = bb.getShort();
			log.debug("decode", " User Account Type: " + userAccountType);

			/* User Name */
			short userNameLen = bb.getShort();
			userName = AhDecoder.bytes2String(bb, userNameLen);
			log.debug("decode", "User Name[len/value]: " + userNameLen + "/" + userName);

			/* User Full Name */
			short userFullNameLen = bb.getShort();
			userFullName = AhDecoder.bytes2String(bb, userFullNameLen);
			log.debug("decode", "User Full Name[len/value]: " + userFullNameLen + "/"
					+ userFullName);

			/* Email Address */
			short emailAddrLen = bb.getShort();
			emailAddr = AhDecoder.bytes2String(bb, emailAddrLen);
			log.debug("decode", "Email Address[len/value]: " + emailAddrLen + "/" + emailAddr);

			/* SSH Server */
			bb.getInt();
			// sshServer = AhDecoder.long2Ip(ipv4);
			// log.debug("decode", "SSH Server IP[int/string]: " + ipv4 + "/" + sshServer);

			/* SSH Server Port */
			sshPort = bb.getInt();
			log.debug("decode", "SSH Server Port: " + sshPort);

			/* SSH Server User Name */
			short sshUserNameLen = bb.getShort();
			sshUserName = AhDecoder.bytes2String(bb, sshUserNameLen);
			log.debug("decode", "SSH Server User Name[len/value]: " + sshUserNameLen + "/"
					+ sshUserName);

			/* SSH Server Password */
			short sshPwdLen = bb.getShort();
			sshPwd = AhDecoder.bytes2String(bb, sshPwdLen);
			log.debug("decode", "SSH Server Password[len/value]: " + sshPwdLen + "/" + sshPwd);

			/* SSH Path */
			short sshPathLen = bb.getShort();
			sshPath = AhDecoder.bytes2String(bb, sshPathLen);
			log.debug("decode", "SSH Path[len/value]: " + sshPathLen + "/" + sshPath);

			/* File Name */
			short fileNameLen = bb.getShort();
			fileName = AhDecoder.bytes2String(bb, fileNameLen);
			log.debug("decode", "File Name[len/value]: " + fileNameLen + "/" + fileName);

			/* Notify Flag */
			byte flag = bb.get();
			if (flag == 0x00) {
				notifyFlag = false;
			} else {
				notifyFlag = true;
			}

			/* VHM-ID */
			short len2 = bb.getShort();
			vhmId = AhDecoder.bytes2String(bb, len2);

			/* Valid Days */
			validDays = bb.getInt();

			sshServer = Tool.getString(bb);
			ccEmailAddr = Tool.getString(bb);

			byte a = bb.get();
			if (a == 0x01) {
				enableEnterprise = true;
			} else {
				enableEnterprise = false;
			}

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