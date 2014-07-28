/**
 * Search the LDAP tree info event
 */
package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;

/**
 * @author cchen
 *
 */
@SuppressWarnings("serial")
public class BeLDAPTreeInfoEvent extends BeCapwapClientEvent {
	// 1:search sub directory 2:search attributes
	public static final byte		SEARCH_TYPE_SUBDIRECTORY    = 1;
	public static final byte		SEARCH_TYPE_ATTRIBUTES		= 2;
	
	// 1: AD 2: OD 3:LDAP
	public static final byte		SERVER_TYPE_AD				= 1;
	public static final byte		SERVER_TYPE_OD				= 2;
	public static final byte		SERVER_TYPE_LDAP			= 3;
	
	private String 			server = "";
	
	private byte 			serverType = SERVER_TYPE_AD;
	
	private String 			baseDn = "";
	
	private String 			bindDn = "";
	
	private String 			passwd = "";
	
	private byte 			searchType	 = SEARCH_TYPE_SUBDIRECTORY;
	
	private byte 			maxSubDirectory	 = 0;	// 0 is no limit
	
	private String			attribute = "";			// only valid when type is search attribute
	
	/**
	 * build event data to packet message
	 * @see com.ah.be.communication.BeCommunicationEvent#buildPacket()
	 */
	@Override
	public byte[] buildPacket() throws BeCommunicationEncodeException {
		if (apMac == null) {
			throw new BeCommunicationEncodeException("ApMac is a necessary field!");
		}

		if (sequenceNum <= 0) {
			throw new BeCommunicationEncodeException("sequenceNum is a necessary field!");
		}
		
		try {
			/**
			 * AP identifier 's length = 6 + 1 + apMac.length()<br>
			 * query's length = 6 + 11 + requestData.length
			 */
			byte[] requestData = getRequestData();
			
			int apIdentifierLen = 6 + 1 + apMac.length();
			int queryLen = 6 + 11 + requestData.length;
			ByteBuffer buf = ByteBuffer.allocate(apIdentifierLen + queryLen);
			
			// set value
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_APIDENTIFIER);
			buf.putInt(1 + apMac.length());
			buf.put((byte) apMac.length());
			buf.put(apMac.getBytes());
			buf.putShort(BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTQUERY);
			buf.putInt(11 + requestData.length);
			buf.putShort(queryType);
			buf.putInt(sequenceNum);
			buf.put(flag);
			buf.putInt(requestData.length);
			
			// set event data
			buf.put(requestData);
			setPacket(buf.array());
			return buf.array();
		} catch (Exception e) {
			throw new BeCommunicationEncodeException("BeLDAPTreeInfoEvent.buildPacket() catch exception", e);
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

		buf.put((byte)server.length());
		buf.put(server.getBytes());
		buf.put(serverType);
		
		buf.put((byte)baseDn.length());
		buf.put(baseDn.getBytes());
		
		buf.put((byte)bindDn.length());
		buf.put(bindDn.getBytes());
		
		buf.put((byte)passwd.length());
		buf.put(passwd.getBytes());
		
		buf.put(searchType);
		buf.putInt(maxSubDirectory);
		
		buf.putShort((short)attribute.length());
		buf.put(attribute.getBytes());
		
		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	public BeLDAPTreeInfoEvent() {
		super();
		queryType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getBaseDn() {
		return baseDn;
	}

	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}

	public String getBindDn() {
		return bindDn;
	}

	public void setBindDn(String bindDn) {
		this.bindDn = bindDn;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public byte getSearchType() {
		return searchType;
	}

	public void setSearchType(byte searchType) {
		this.searchType = searchType;
	}

	public byte getMaxSubDirectory() {
		return maxSubDirectory;
	}

	public void setMaxSubDirectory(byte maxSubDirectory) {
		this.maxSubDirectory = maxSubDirectory;
	}

	public byte getServerType() {
		return serverType;
	}

	public void setServerType(byte serverType) {
		this.serverType = serverType;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

}
