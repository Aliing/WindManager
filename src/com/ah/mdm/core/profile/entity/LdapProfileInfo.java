package com.ah.mdm.core.profile.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

//import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "t_profile_ldap")
public class LdapProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long							ldapId;

	private String							accountDescription;
	private String							accountHostName;
	private boolean							accountUseSSL;
	private String							accountUserName;
	private String							accountPassword;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ProfileLdapSearchSetting.class)
	@JoinColumn(name = "ldapId")
	private List<ProfileLdapSearchSetting>	searchSettings	= new ArrayList<ProfileLdapSearchSetting>();

	public long getLdapId()
	{
		return ldapId;
	}

	public void setLdapId(long ldapId)
	{
		this.ldapId = ldapId;
	}

	public LdapProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_LDAP);
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

	public boolean isAccountUseSSL()
	{
		return accountUseSSL;
	}

	public void setAccountUseSSL(boolean accountUseSSL)
	{
		this.accountUseSSL = accountUseSSL;
	}

	public String getAccountUserName()
	{
		return accountUserName;
	}

	public void setAccountUserName(String accountUserName)
	{
		this.accountUserName = accountUserName;
	}

	public String getAccountPassword()
	{
		return accountPassword;
	}

	public void setAccountPassword(String accountPassword)
	{
		this.accountPassword = accountPassword;
	}

	public List<ProfileLdapSearchSetting> getSearchSettings()
	{
		return searchSettings;
	}

	public void setSearchSettings(List<ProfileLdapSearchSetting> searchSettings)
	{
		this.searchSettings = searchSettings;
	}

}
