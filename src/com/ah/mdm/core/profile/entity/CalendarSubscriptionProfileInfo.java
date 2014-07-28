package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_calsub")
public class CalendarSubscriptionProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	calendarId;

	private String	accountDescription;
	private String	accountHostName;
	private String	accountUsername;
	private String	accountPassword;
	private boolean	accountUseSSL;

	public CalendarSubscriptionProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_SUB_CAL);
	}

	public String getAccountDescription()
	{
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription)
	{
		this.accountDescription = accountDescription;
	}

	public String getAccountHostName()
	{
		return accountHostName;
	}

	public void setAccountHostName(String accountHostName)
	{
		this.accountHostName = accountHostName;
	}

	public String getAccountUsername()
	{
		return accountUsername;
	}

	public void setAccountUsername(String accountUsername)
	{
		this.accountUsername = accountUsername;
	}

	public String getAccountPassword()
	{
		return accountPassword;
	}

	public void setAccountPassword(String accountPassword)
	{
		this.accountPassword = accountPassword;
	}

	public boolean isAccountUseSSL()
	{
		return accountUseSSL;
	}

	public void setAccountUseSSL(boolean accountUseSSL)
	{
		this.accountUseSSL = accountUseSSL;
	}

	public long getCalendarId()
	{
		return calendarId;
	}

	public void setCalendarId(long calendarId)
	{
		this.calendarId = calendarId;
	}

}
