package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_apn")
public class ProfileApn
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	apnId;
	private String	apn;
	private String	username;
	private String	password;
	private String	proxy;
	private int		proxyPort;

	public long getApnId()
	{
		return apnId;
	}

	public void setApnId(long apnId)
	{
		this.apnId = apnId;
	}

	public String getApn()
	{
		return apn;
	}

	public void setApn(String apn)
	{
		this.apn = apn;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getProxy()
	{
		return proxy;
	}

	public void setProxy(String proxy)
	{
		this.proxy = proxy;
	}

	public int getProxyPort()
	{
		return proxyPort;
	}

	public void setProxyPort(int proxyPort)
	{
		this.proxyPort = proxyPort;
	}
}
