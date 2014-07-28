package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.util.coder.AhEncoder;

/**
 *
 *@filename		BeAAATestEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-3-29 03:23:07
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeAAATestEvent extends BeCapwapClientEvent {

	public static final byte	TESTTYPE_LDAP_SEARCH	= 1;
	public static final byte	TESTTYPE_NET_JOIN		= 2;
	public static final byte	TESTTYPE_NTLM_AUTH		= 3;
	public static final byte	TESTTYPE_RADIUS_TEST	= 4;
	public static final byte	TEST_TYPE_LIBSIP_TEST	= 5;
	public static final byte	TEST_TYPE_NETJOIN_TEST	= 6;// specify server net-join
	public static final byte	TEST_TYPE_NTLM_AUTH_TEST= 7;

	public static final byte	DOMAINTYPE_DEFAULT		= 0;
	public static final byte	DOMAINTYPE_PRIMARY		= 1;
	public static final byte	DOMAINTYPE_BACKUP1		= 2;
	public static final byte	DOMAINTYPE_BACKUP2		= 3;
	public static final byte	DOMAINTYPE_BACKUP3		= 4;

	public static final byte	ACCOUNTING_CHECK		= 1;
	public static final byte	ACCOUNTING_NOCHECK		= 0;

	private byte				testType;

	private String				userName = "";

	private String				password = "";

	private String				domain = "";

	private String				baseDN = "";

	private byte				domainType = DOMAINTYPE_DEFAULT;

	private byte				accounting = ACCOUNTING_NOCHECK;

	private String				server = "";

	private String				realm = "";

	// computer OU
	private String				cou = "";
	
	// client LDAP sals wrapping
	private String				ldapSaslWrapping = "";

	public BeAAATestEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST;
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

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}

		try {
			byte[] requestData = getRequestData();

			/**
			 * AP identifier 's length = 6 + 1 + apSerialNum.length()<br>
			 * query's length = 6 + 11 + requestData.length
			 */
			int apIdentifierLen = 7 + apMac.length();
			int queryLen = 17 + requestData.length;
			int bufLength = apIdentifierLen + queryLen;
			ByteBuffer buf = ByteBuffer.allocate(bufLength);
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(apIdentifierLen - 6);
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + requestData.length); // 2+4+1+4+reqestData.length
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(requestData.length); // data length
			buf.put(requestData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException(
					"BeAAATestEvent.buildPacket() catch exception", e);
		}
	}

	/**
	 * get request data
	 *
	 * @param
	 *
	 * @return
	 */
	private byte[] getRequestData() throws BeCommunicationEncodeException {

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.put(testType);

		buf.put((byte)userName.length());
		buf.put(userName.getBytes());

		buf.put((byte)password.length());
		buf.put(password.getBytes());

		buf.put((byte)domain.length());
		buf.put(domain.getBytes());

		buf.putShort((short)baseDN.length());
		buf.put(baseDN.getBytes());

		buf.put(domainType);
		buf.put(accounting);

		buf.put((byte)server.length());
		buf.put(server.getBytes());

		buf.put((byte)realm.length());
		buf.put(realm.getBytes());

		AhEncoder.putString(buf, cou);

		AhEncoder.putString(buf, ldapSaslWrapping);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	/**
	 * parse packet message to event data
	 *
	 * @param data -
	 * @throws BeCommunicationDecodeException -
	 */
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		super.parsePacket(data);
	}

	public byte getAccounting() {
		return accounting;
	}

	public void setAccounting(byte accounting) {
		this.accounting = accounting;
	}

	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public byte getDomainType() {
		return domainType;
	}

	public void setDomainType(byte domainType) {
		this.domainType = domainType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public byte getTestType() {
		return testType;
	}

	public void setTestType(byte testType) {
		this.testType = testType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getCou() {
		return cou;
	}

	public void setCou(String cou) {
		this.cou = cou;
	}

	public String getLdapSaslWrapping() {
		return ldapSaslWrapping;
	}

	public void setLdapSaslWrapping(String ldapSaslWrapping) {
		this.ldapSaslWrapping = ldapSaslWrapping;
	}
}
