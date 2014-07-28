package com.ah.mdm.core.profile.entity;

import java.util.UUID;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractProfileInfo
{
	private String	uuid;
	private String	identifier;
	private String	type;
	private int		version;
	private String	displayName;	// optional
	private String	description;	// optional
	private String	organization;	// optional
	private short   userProfileAttributeValue = 1;  // optional
	
	

	public AbstractProfileInfo(String type)
	{
		this.uuid = UUID.randomUUID().toString();
		this.type = type;
		this.version = 1;// default value
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String uuid)
	{
		this.uuid = uuid;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public int getVersion()
	{
		return version;
	}

	public void setVersion(int version)
	{
		this.version = version;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	
	public short getUserProfileAttributeValue() {
		return userProfileAttributeValue;
	}

	public void setUserProfileAttributeValue(short userProfileAttributeValue) {
		this.userProfileAttributeValue = userProfileAttributeValue;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getOrganization()
	{
		return organization;
	}

	public void setOrganization(String organization)
	{
		this.organization = organization;
	}

}
