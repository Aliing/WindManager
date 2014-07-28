package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_email")
public class EmailProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	emailId;

	private String	accountDescription;
	private String	accountName;
	//private String	accountType							= "EmailTypeIMAP";
	private String	accountType;
	private String	address;
//	private String	incomingMailServerAuthentication	= "EmailAuthPassword";
	private String	incomingMailServerAuthentication;
	private String	incomingMailServerIMAPPathPrefix;
	private String	incomingMailServerHostName;
	private int		incomingMailServerPortNumber;
//	private boolean	incomingMailServerUseSSL			= true;
	private boolean	incomingMailServerUseSSL;
	private String	incomingMailServerUsername;
	private String	incomingPassword;
	private String	outgoingPassword;
	private boolean	outgoingPasswordSameAsIncomingPassword;
	private String	outgoingMailServerAuthentication;
	private String	outgoingMailServerHostName;
	private int		outgoingMailServerPortNumber;
//	private boolean	outgoingMailServerUseSSL			= true;
	private boolean	outgoingMailServerUseSSL;
	private String	outgoingMailServerUsername;
	private boolean	preventMove;
	private boolean	preventAppSheet;
	private boolean	smimeEnabled;
	private String	smimeSigningCertificateUUID;
	private String	smimeEncryptionCertificateUUID;
	private boolean	disableMailRecentsSyncing;

	public long getEmailId()
	{
		return emailId;
	}

	public void setEmailId(long emailId)
	{
		this.emailId = emailId;
	}

	public EmailProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_EMAIL);
	}

	public String getAccountDescription()
	{
		return accountDescription;
	}

	public void setAccountDescription(String accountDescription)
	{
		this.accountDescription = accountDescription;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getIncomingMailServerAuthentication()
	{
		return incomingMailServerAuthentication;
	}

	public void setIncomingMailServerAuthentication(String incomingMailServerAuthentication)
	{
		this.incomingMailServerAuthentication = incomingMailServerAuthentication;
	}

	public String getIncomingMailServerIMAPPathPrefix()
	{
		return incomingMailServerIMAPPathPrefix;
	}

	public void setIncomingMailServerIMAPPathPrefix(String incomingMailServerIMAPPathPrefix)
	{
		this.incomingMailServerIMAPPathPrefix = incomingMailServerIMAPPathPrefix;
	}

	public String getIncomingMailServerHostName()
	{
		return incomingMailServerHostName;
	}

	public void setIncomingMailServerHostName(String incomingMailServerHostName)
	{
		this.incomingMailServerHostName = incomingMailServerHostName;
	}

	public int getIncomingMailServerPortNumber()
	{
		return incomingMailServerPortNumber;
	}

	public void setIncomingMailServerPortNumber(int incomingMailServerPortNumber)
	{
		this.incomingMailServerPortNumber = incomingMailServerPortNumber;
	}

	public boolean isIncomingMailServerUseSSL()
	{
		return incomingMailServerUseSSL;
	}

	public void setIncomingMailServerUseSSL(boolean incomingMailServerUseSSL)
	{
		this.incomingMailServerUseSSL = incomingMailServerUseSSL;
	}

	public String getIncomingMailServerUsername()
	{
		return incomingMailServerUsername;
	}

	public void setIncomingMailServerUsername(String incomingMailServerUsername)
	{
		this.incomingMailServerUsername = incomingMailServerUsername;
	}

	public String getIncomingPassword()
	{
		return incomingPassword;
	}

	public void setIncomingPassword(String incomingPassword)
	{
		this.incomingPassword = incomingPassword;
	}

	public String getOutgoingPassword()
	{
		return outgoingPassword;
	}

	public void setOutgoingPassword(String outgoingPassword)
	{
		this.outgoingPassword = outgoingPassword;
	}

	public boolean isOutgoingPasswordSameAsIncomingPassword()
	{
		return outgoingPasswordSameAsIncomingPassword;
	}

	public void setOutgoingPasswordSameAsIncomingPassword(boolean outgoingPasswordSameAsIncomingPassword)
	{
		this.outgoingPasswordSameAsIncomingPassword = outgoingPasswordSameAsIncomingPassword;
	}

	public String getOutgoingMailServerAuthentication()
	{
		return outgoingMailServerAuthentication;
	}

	public void setOutgoingMailServerAuthentication(String outgoingMailServerAuthentication)
	{
		this.outgoingMailServerAuthentication = outgoingMailServerAuthentication;
	}

	public String getOutgoingMailServerHostName()
	{
		return outgoingMailServerHostName;
	}

	public void setOutgoingMailServerHostName(String outgoingMailServerHostName)
	{
		this.outgoingMailServerHostName = outgoingMailServerHostName;
	}

	public int getOutgoingMailServerPortNumber()
	{
		return outgoingMailServerPortNumber;
	}

	public void setOutgoingMailServerPortNumber(int outgoingMailServerPortNumber)
	{
		this.outgoingMailServerPortNumber = outgoingMailServerPortNumber;
	}

	public boolean isOutgoingMailServerUseSSL()
	{
		return outgoingMailServerUseSSL;
	}

	public void setOutgoingMailServerUseSSL(boolean outgoingMailServerUseSSL)
	{
		this.outgoingMailServerUseSSL = outgoingMailServerUseSSL;
	}

	public String getOutgoingMailServerUsername()
	{
		return outgoingMailServerUsername;
	}

	public void setOutgoingMailServerUsername(String outgoingMailServerUsername)
	{
		this.outgoingMailServerUsername = outgoingMailServerUsername;
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

	public boolean isDisableMailRecentsSyncing()
	{
		return disableMailRecentsSyncing;
	}

	public void setDisableMailRecentsSyncing(boolean disableMailRecentsSyncing)
	{
		this.disableMailRecentsSyncing = disableMailRecentsSyncing;
	}
}
