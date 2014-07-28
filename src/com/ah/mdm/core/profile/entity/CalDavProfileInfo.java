package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_caldav")
public class CalDavProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	claDavId;

	private String	accountDescription;
	private String	hostName;
	private String	username;
	private String	password;
	private boolean	useSSL;
	private int		port;
	private String	principalURL;

	public CalDavProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_CAL_DAV);
	}

	public String getAccountDescription()
	{
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription)
	{
		this.accountDescription = accountDescription;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
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

	public boolean isUseSSL()
	{
		return useSSL;
	}

	public void setUseSSL(boolean useSSL)
	{
		this.useSSL = useSSL;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public String getPrincipalURL()
	{
		return principalURL;
	}

	public void setPrincipalURL(String principalURL)
	{
		this.principalURL = principalURL;
	}

	public long getClaDavId()
	{
		return claDavId;
	}

	public void setClaDavId(long claDavId)
	{
		this.claDavId = claDavId;
	}

}
