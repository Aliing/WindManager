package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_ldap_search_setting")
public class ProfileLdapSearchSetting
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	searchSettingId;
	private String	description;
	private String	searchBase;
	private String	scope;

	public long getSearchSettingId()
	{
		return searchSettingId;
	}

	public void setSearchSettingId(long searchSettingId)
	{
		this.searchSettingId = searchSettingId;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getSearchBase()
	{
		return searchBase;
	}

	public void setSearchBase(String searchBase)
	{
		this.searchBase = searchBase;
	}

	public String getScope()
	{
		return scope;
	}

	public void setScope(String scope)
	{
		this.scope = scope;
	}
}
