/**
 *@filename		ActiveDirectoryOrLdapInfo.java
 *@version
 *@author		Fiona
 *@createtime	2008-1-15 AM 11:01:03
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Embeddable
public class ActiveDirectoryOrLdapInfo implements Serializable
{
	private static final long		serialVersionUID	= 1L;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DIRECTORY_OR_LDAP_ID", nullable = false)
	private ActiveDirectoryOrOpenLdap	directoryOrLdap;

	private short					serverPriority		= RadiusServer.RADIUS_PRIORITY_PRIMARY;

	public short getServerPriority()
	{
		return serverPriority;
	}

	public void setServerPriority(short serverPriority)
	{
		this.serverPriority = serverPriority;
	}

	public ActiveDirectoryOrOpenLdap getDirectoryOrLdap()
	{
		return directoryOrLdap;
	}

	public void setDirectoryOrLdap(ActiveDirectoryOrOpenLdap directoryOrLdap)
	{
		this.directoryOrLdap = directoryOrLdap;
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
}
