package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_app_lock")
public class AppLockProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	appLockId;

	private String	appIdentifier;

	public AppLockProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_APP_LOCK);
	}

	public String getAppIdentifier()
	{
		return appIdentifier;
	}

	public void setAppIdentifier(String appIdentifier)
	{
		this.appIdentifier = appIdentifier;
	}

	public long getAppLockId()
	{
		return appLockId;
	}

	public void setAppLockId(long appLockId)
	{
		this.appLockId = appLockId;
	}
}
