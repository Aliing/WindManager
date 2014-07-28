package com.ah.mdm.core.profile.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

//import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "t_profile_deviceconfiguration")
public class ConfigurationProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long									profileId;
	private String									customerId;
	private boolean									hasRemovalPasscode;
	private boolean									isEncrypted;
	private boolean									isManaged					= true;
	private boolean									removalDisallowed;
	private boolean									scope;
	private Date									removalDate;																						// date
	private float									durationUntilRemoval;
	private Date									createDate;
	private long									creatorId;
	private Date									updateDate;

	@Transient
	private List<AbstractProfileInfo>				profileInfoItems			= new ArrayList<AbstractProfileInfo>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ApnProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<ApnProfileInfo>					apnProfileInfos				= new ArrayList<ApnProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = AppLockProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<AppLockProfileInfo>				appLockProfileInfos			= new ArrayList<AppLockProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = CalDavProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<CalDavProfileInfo>					calDavProfileInfos			= new ArrayList<CalDavProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = CalendarSubscriptionProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<CalendarSubscriptionProfileInfo>	calendarSubscriptionProfileInfos			= new ArrayList<CalendarSubscriptionProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = CardDavProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<CardDavProfileInfo>				cardDavProfileInfos			= new ArrayList<CardDavProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = CredentialsProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<CredentialsProfileInfo>			credentialsProfileInfos		= new ArrayList<CredentialsProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = EmailProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<EmailProfileInfo>					emailProfileInfos			= new ArrayList<EmailProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ExchangeProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<ExchangeProfileInfo>				exchangeProfileInfos		= new ArrayList<ExchangeProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = GlobalHttpProxyProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<GlobalHttpProxyProfileInfo>		globalHttpProxyProfileInfos	= new ArrayList<GlobalHttpProxyProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = IdentificationProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<IdentificationProfileInfo>			identificationProfileInfos	= new ArrayList<IdentificationProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = LdapProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<LdapProfileInfo>					ldapProfileInfos			= new ArrayList<LdapProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = PasscodeProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<PasscodeProfileInfo>				passcodeProfileInfos		= new ArrayList<PasscodeProfileInfo>();
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "removalPasscodeId", unique = true)
	private RemovalPasscodeProfileInfo				removalPasscodeProfileInfo;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = RestrictionsProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<RestrictionsProfileInfo>			restrictionsProfileInfos		= new ArrayList<RestrictionsProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = ScepProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<ScepProfileInfo>					scepProfileInfos			= new ArrayList<ScepProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = VpnProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<VpnProfileInfo>					vpnProfileInfos				= new ArrayList<VpnProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = WebClipProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<WebClipProfileInfo>				webClipProfileInfos			= new ArrayList<WebClipProfileInfo>();
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = WifiProfileInfo.class)
	@JoinColumn(name = "profileId")
	private List<WifiProfileInfo>					wifiProfileInfos			= new ArrayList<WifiProfileInfo>();
	@JoinColumn(name = "profileId")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = MdmProfileInfo.class)
	private List<MdmProfileInfo>					mdmProfileInfos				= new ArrayList<MdmProfileInfo>();

	public ConfigurationProfileInfo(String type)
	{
		super(type);
	}

	public ConfigurationProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_DEVICE_CONFIGURATION);
	}

	public long getProfileId()
	{
		return profileId;
	}

	public void setProfileId(long profileId)
	{
		this.profileId = profileId;
	}

	public String getCustomerId()
	{
		return customerId;
	}

	public void setCustomerId(String customerId)
	{
		this.customerId = customerId;
	}

	public boolean isHasRemovalPasscode()
	{
		return hasRemovalPasscode;
	}

	public void setHasRemovalPasscode(boolean hasRemovalPasscode)
	{
		this.hasRemovalPasscode = hasRemovalPasscode;
	}

	public boolean isEncrypted()
	{
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted)
	{
		this.isEncrypted = isEncrypted;
	}

	public boolean isManaged()
	{
		return isManaged;
	}

	public void setManaged(boolean isManaged)
	{
		this.isManaged = isManaged;
	}

	public boolean isRemovalDisallowed()
	{
		return removalDisallowed;
	}

	public void setRemovalDisallowed(boolean removalDisallowed)
	{
		this.removalDisallowed = removalDisallowed;
	}

	public boolean isScope()
	{
		return scope;
	}

	public void setScope(boolean scope)
	{
		this.scope = scope;
	}

	public Date getRemovalDate()
	{
		if (removalDate == null)
		{
			return null;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(removalDate);
		return date.getTime();
	}

	public void setRemovalDate(Date removalDateParam)
	{
		if (null != removalDateParam)
		{
			Calendar date = Calendar.getInstance();
			date.setTime(removalDateParam);
			this.removalDate = date.getTime();
		}
	}

	public float getDurationUntilRemoval()
	{
		return durationUntilRemoval;
	}

	public void setDurationUntilRemoval(float durationUntilRemoval)
	{
		this.durationUntilRemoval = durationUntilRemoval;
	}

	public Date getCreateDate()
	{
		if (createDate == null)
		{
			return null;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(createDate);
		return date.getTime();
	}

	public void setCreateDate(Date createDateParam)
	{
		if (null != createDateParam)
		{
			Calendar date = Calendar.getInstance();
			date.setTime(createDateParam);
			this.createDate = date.getTime();
		}
	}

	public long getCreatorId()
	{
		return creatorId;
	}

	public void setCreatorId(long creatorId)
	{
		this.creatorId = creatorId;
	}

	public Date getUpdateDate()
	{
		if (updateDate == null)
		{
			return null;
		}
		Calendar date = Calendar.getInstance();
		date.setTime(updateDate);
		return date.getTime();
	}

	public void setUpdateDate(Date updateDateParam)
	{
		if (null != updateDateParam)
		{
			Calendar date = Calendar.getInstance();
			date.setTime(updateDateParam);
			this.updateDate = date.getTime();
		}
	}

	public List<AbstractProfileInfo> getProfileInfoItems()
	{
		return profileInfoItems;
	}

	public void setProfileInfoItems(List<AbstractProfileInfo> profileInfoItems)
	{
		this.profileInfoItems = profileInfoItems;
	}

	public List<ApnProfileInfo> getApnProfileInfos()
	{
		return apnProfileInfos;
	}

	public void setApnProfileInfos(List<ApnProfileInfo> apnProfileInfos)
	{
		this.apnProfileInfos = apnProfileInfos;
	}

	public List<AppLockProfileInfo> getAppLockProfileInfos()
	{
		return appLockProfileInfos;
	}

	public void setAppLockProfileInfos(List<AppLockProfileInfo> appLockProfileInfos)
	{
		this.appLockProfileInfos = appLockProfileInfos;
	}

	public List<CalDavProfileInfo> getCalDavProfileInfos()
	{
		return calDavProfileInfos;
	}

	public void setCalDavProfileInfos(List<CalDavProfileInfo> calDavProfileInfos)
	{
		this.calDavProfileInfos = calDavProfileInfos;
	}

	public List<CalendarSubscriptionProfileInfo> getCalendarSubscriptionProfileInfos()
	{
		return calendarSubscriptionProfileInfos;
	}

	public void setCalSubProfileInfos(List<CalendarSubscriptionProfileInfo> calendarSubscriptionProfileInfos)
	{
		this.calendarSubscriptionProfileInfos = calendarSubscriptionProfileInfos;
	}

	public List<CardDavProfileInfo> getCardDavProfileInfos()
	{
		return cardDavProfileInfos;
	}

	public void setCardDavProfileInfos(List<CardDavProfileInfo> cardDavProfileInfos)
	{
		this.cardDavProfileInfos = cardDavProfileInfos;
	}

	public List<CredentialsProfileInfo> getCredentialsProfileInfos()
	{
		return credentialsProfileInfos;
	}

	public void setCredentialsProfileInfos(List<CredentialsProfileInfo> credentialsProfileInfos)
	{
		this.credentialsProfileInfos = credentialsProfileInfos;
	}

	public List<EmailProfileInfo> getEmailProfileInfos()
	{
		return emailProfileInfos;
	}

	public void setEmailProfileInfos(List<EmailProfileInfo> emailProfileInfos)
	{
		this.emailProfileInfos = emailProfileInfos;
	}

	public List<ExchangeProfileInfo> getExchangeProfileInfos()
	{
		return exchangeProfileInfos;
	}

	public void setExchangeProfileInfos(List<ExchangeProfileInfo> exchangeProfileInfos)
	{
		this.exchangeProfileInfos = exchangeProfileInfos;
	}

	public List<GlobalHttpProxyProfileInfo> getGlobalHttpProxyProfileInfos()
	{
		return globalHttpProxyProfileInfos;
	}

	public void setGlobalHttpProxyProfileInfos(List<GlobalHttpProxyProfileInfo> globalHttpProxyProfileInfos)
	{
		this.globalHttpProxyProfileInfos = globalHttpProxyProfileInfos;
	}

	public List<IdentificationProfileInfo> getIdentificationProfileInfos()
	{
		return identificationProfileInfos;
	}

	public void setIdentificationProfileInfos(List<IdentificationProfileInfo> identificationProfileInfos)
	{
		this.identificationProfileInfos = identificationProfileInfos;
	}

	public List<LdapProfileInfo> getLdapProfileInfos()
	{
		return ldapProfileInfos;
	}

	public void setLdapProfileInfos(List<LdapProfileInfo> ldapProfileInfos)
	{
		this.ldapProfileInfos = ldapProfileInfos;
	}

	public List<PasscodeProfileInfo> getPasscodeProfileInfos()
	{
		return passcodeProfileInfos;
	}

	public void setPasscodeProfileInfos(List<PasscodeProfileInfo> passcodeProfileInfos)
	{
		this.passcodeProfileInfos = passcodeProfileInfos;
	}

	public RemovalPasscodeProfileInfo getRemovalPasscodeProfileInfo()
	{
		return removalPasscodeProfileInfo;
	}

	public void setRemovalPasscodeProfileInfo(RemovalPasscodeProfileInfo removalPasscodeProfileInfo)
	{
		this.removalPasscodeProfileInfo = removalPasscodeProfileInfo;
	}

	public List<RestrictionsProfileInfo> getRestrictionsProfileInfos()
	{
		return restrictionsProfileInfos;
	}

	public void setRestrictionsProfileInfos(List<RestrictionsProfileInfo> restrictionsProfileInfos)
	{
		this.restrictionsProfileInfos = restrictionsProfileInfos;
	}

	public List<ScepProfileInfo> getScepProfileInfos()
	{
		return scepProfileInfos;
	}

	public void setScepProfileInfos(List<ScepProfileInfo> scepProfileInfos)
	{
		this.scepProfileInfos = scepProfileInfos;
	}

	public List<VpnProfileInfo> getVpnProfileInfos()
	{
		return vpnProfileInfos;
	}

	public void setVpnProfileInfos(List<VpnProfileInfo> vpnProfileInfos)
	{
		this.vpnProfileInfos = vpnProfileInfos;
	}

	public List<WebClipProfileInfo> getWebClipProfileInfos()
	{
		return webClipProfileInfos;
	}

	public void setWebClipProfileInfos(List<WebClipProfileInfo> webClipProfileInfos)
	{
		this.webClipProfileInfos = webClipProfileInfos;
	}

	public List<WifiProfileInfo> getWifiProfileInfos()
	{
		return wifiProfileInfos;
	}

	public void setWifiProfileInfos(List<WifiProfileInfo> wifiProfileInfos)
	{
		this.wifiProfileInfos = wifiProfileInfos;
	}

	public List<MdmProfileInfo> getMdmProfileInfos()
	{
		return mdmProfileInfos;
	}

	public void setMdmProfileInfos(List<MdmProfileInfo> mdmProfileInfos)
	{
		this.mdmProfileInfos = mdmProfileInfos;
	}

}
