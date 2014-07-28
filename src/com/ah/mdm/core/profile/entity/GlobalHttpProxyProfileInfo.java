package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_global_http_proxy")
public class GlobalHttpProxyProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	proxyId;

	private String	proxyType;
	private String	proxyServer;
	private String	proxyServerPort;
	private String	proxyUsername;
	private String	proxyPassword;
	private String	proxyPACURL;

	public GlobalHttpProxyProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_GLOBAL_HTTP_PROXY);
	}

	public String getProxyType()
	{
		return proxyType;
	}

	public void setProxyType(String proxyType)
	{
		this.proxyType = proxyType;
	}

	public String getProxyServer()
	{
		return proxyServer;
	}

	public void setProxyServer(String proxyServer)
	{
		this.proxyServer = proxyServer;
	}

	public String getProxyServerPort()
	{
		return proxyServerPort;
	}

	public void setProxyServerPort(String proxyServerPort)
	{
		this.proxyServerPort = proxyServerPort;
	}

	public String getProxyUsername()
	{
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername)
	{
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword()
	{
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword)
	{
		this.proxyPassword = proxyPassword;
	}

	public String getProxyPACURL()
	{
		return proxyPACURL;
	}

	public void setProxyPACURL(String proxyPACURL)
	{
		this.proxyPACURL = proxyPACURL;
	}

	public long getProxyId()
	{
		return proxyId;
	}

	public void setProxyId(long proxyId)
	{
		this.proxyId = proxyId;
	}
}
