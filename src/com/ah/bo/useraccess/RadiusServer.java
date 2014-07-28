/**
 *@filename		RadiusService.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-27 PM 05:11:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.HmBo;
import com.ah.bo.network.IpAddress;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Embeddable
@SuppressWarnings("serial")
public class RadiusServer implements Serializable
{
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IP_ADDRESS_ID", nullable = true)
	private IpAddress	ipAddress;

	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String	sharedSecret;
	
	public static final short RADIUS_SERVER_TYPE_BOTH = 1;
	
	public static final short RADIUS_SERVER_TYPE_AUTH = 2;
	
	public static final short RADIUS_SERVER_TYPE_ACCT = 3;
	
	public static EnumItem[] ENUM_RADIUS_TYPE = MgrUtil.enumItems(
		"enum.radius.server.type.", new int[] { RADIUS_SERVER_TYPE_BOTH,
			RADIUS_SERVER_TYPE_AUTH, RADIUS_SERVER_TYPE_ACCT });
	
	private short   serverType = RADIUS_SERVER_TYPE_BOTH;
	
	private int		authPort = 1812;
	
	private int		acctPort = 1813;
	
	public static final short RADIUS_PRIORITY_PRIMARY = 1;

	public static final short RADIUS_PRIORITY_BACKUP1 = 2;
	
	public static final short RADIUS_PRIORITY_BACKUP2 = 3;
	
	public static final short RADIUS_PRIORITY_BACKUP3 = 4;

	public static EnumItem[] ENUM_RADIUS_PRIORITY = MgrUtil.enumItems(
			"enum.radiusPriority.", new int[] { RADIUS_PRIORITY_PRIMARY,
				RADIUS_PRIORITY_BACKUP1, RADIUS_PRIORITY_BACKUP2, RADIUS_PRIORITY_BACKUP3 });
	
	private short	serverPriority;
	
	@Transient
	private boolean useSelfAsServer = false;

	public String getSharedSecret()
	{
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret)
	{
		this.sharedSecret = sharedSecret;
	}

	public int getAuthPort()
	{
		return authPort;
	}
	
	@Transient
	public String getAuthPortStr() {
		if (RADIUS_SERVER_TYPE_ACCT == serverType) {
			return "--";
		}
		return String.valueOf(authPort);
	}

	public void setAuthPort(int authPort)
	{
		this.authPort = authPort;
	}

	public int getAcctPort()
	{
		return acctPort;
	}

	public void setAcctPort(int acctPort)
	{
		this.acctPort = acctPort;
	}
	
	@Transient
	public String getAcctPortStr() {
		if (RADIUS_SERVER_TYPE_AUTH == serverType) {
			return "--";
		}
		return String.valueOf(acctPort);
	}

	public short getServerPriority()
	{
		return serverPriority;
	}

	public void setServerPriority(short serverPriority)
	{
		this.serverPriority = serverPriority;
	}
	
	@Transient
	public String getPriorityStr() {
		return MgrUtil.getEnumString("enum.radiusPriority."+serverPriority);
	}
	
	@Transient
	public String getTypeStr() {
		return MgrUtil.getEnumString("enum.radius.server.type."+serverType);
	}
	
	@Transient
	public String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}

	public IpAddress getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(IpAddress ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public short getServerType()
	{
		return serverType;
	}

	public void setServerType(short serverType)
	{
		this.serverType = serverType;
	}

	public boolean isUseSelfAsServer() {
		return useSelfAsServer;
	}

	public void setUseSelfAsServer(boolean useSelfAsServer) {
		this.useSelfAsServer = useSelfAsServer;
	}
	
}
