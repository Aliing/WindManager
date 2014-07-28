package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_exchange")
public class ExchangeProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	exchangeId;

	private String	emailAddress;
	private String	host;
	private boolean	ssl;
	private String	userName;
	private String	password;
	private byte[]	certificate;
	private String	certificateUrl;
	private String	certificateUrlHid;
	private String	certificatePassword;
	private boolean	preventMove;
	private boolean	preventAppSheet;
	private String	payloadCertificateUUID;
	private boolean	smimeEnabled;
	private String	smimeSigningCertificateUUID;
	private String	smimeEncryptionCertificateUUID;
	private int		mailNumberOfPastDaysToSync;
	private boolean	disableMailRecentsSyncing;

	public String getCertificateUrlHid() {
		return certificateUrl;
	}

	public void setCertificateUrlHid(String certificateUrlHid) {
		this.certificateUrlHid = certificateUrlHid;
	}

	public long getExchangeId()
	{
		return exchangeId;
	}

	public void setExchangeId(long exchangeId)
	{
		this.exchangeId = exchangeId;
	}

	public ExchangeProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_EXCHANGE_IOS);
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public boolean isSsl()
	{
		return ssl;
	}

	public void setSsl(boolean ssl)
	{
		this.ssl = ssl;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public byte[] getCertificate()
	{
		return certificate;
	}

	public void setCertificate(byte[] certificate)
	{
		this.certificate = certificate;
	}

	public String getCertificateUrl()
	{
		return certificateUrl;
	}

	public void setCertificateUrl(String certificateName)
	{
		this.certificateUrl = certificateName;
	}

	public String getCertificatePassword()
	{
		return certificatePassword;
	}

	public void setCertificatePassword(String certificatePassword)
	{
		this.certificatePassword = certificatePassword;
	}

	public boolean isPreventMove()
	{
		return preventMove;
	}

	public void setPreventMove(boolean preventMove)
	{
		this.preventMove = preventMove;
	}

	public boolean isPreventAppSheet()
	{
		return preventAppSheet;
	}

	public void setPreventAppSheet(boolean preventAppSheet)
	{
		this.preventAppSheet = preventAppSheet;
	}

	public String getPayloadCertificateUUID()
	{
		return payloadCertificateUUID;
	}

	public void setPayloadCertificateUUID(String payloadCertificateUUID)
	{
		this.payloadCertificateUUID = payloadCertificateUUID;
	}

	public boolean isSmimeEnabled()
	{
		return smimeEnabled;
	}

	public void setSmimeEnabled(boolean smimeEnabled)
	{
		this.smimeEnabled = smimeEnabled;
	}

	public String getSmimeSigningCertificateUUID()
	{
		return smimeSigningCertificateUUID;
	}

	public void setSmimeSigningCertificateUUID(String smimeSigningCertificateUUID)
	{
		this.smimeSigningCertificateUUID = smimeSigningCertificateUUID;
	}

	public String getSmimeEncryptionCertificateUUID()
	{
		return smimeEncryptionCertificateUUID;
	}

	public void setSmimeEncryptionCertificateUUID(String smimeEncryptionCertificateUUID)
	{
		this.smimeEncryptionCertificateUUID = smimeEncryptionCertificateUUID;
	}

	public int getMailNumberOfPastDaysToSync()
	{
		return mailNumberOfPastDaysToSync;
	}

	public void setMailNumberOfPastDaysToSync(int mailNumberOfPastDaysToSync)
	{
		this.mailNumberOfPastDaysToSync = mailNumberOfPastDaysToSync;
	}

	public boolean isDisableMailRecentsSyncing()
	{
		return disableMailRecentsSyncing;
	}

	public void setDisableMailRecentsSyncing(boolean disableMailRecentsSyncing)
	{
		this.disableMailRecentsSyncing = disableMailRecentsSyncing;
	}
}
