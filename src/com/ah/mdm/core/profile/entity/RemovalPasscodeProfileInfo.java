package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_removal_passcode")
public class RemovalPasscodeProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	removalPasscodeId;

	private String	removalPassword;

	public RemovalPasscodeProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_REMOVAL_PASSWORD);
	}

	public String getRemovalPassword()
	{
		return removalPassword;
	}

	public void setRemovalPassword(String removalPassword)
	{
		this.removalPassword = removalPassword;
	}

	public long getRemovalPasscodeId()
	{
		return removalPasscodeId;
	}

	public void setRemovalPasscodeId(long removalPasscodeId)
	{
		this.removalPasscodeId = removalPasscodeId;
	}
}
