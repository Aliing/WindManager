package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_wifi")
public class WifiProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	wifiId;

	private String	ssidStr;
	private boolean	hiddenNetwork;
	private boolean	autoJoin;
	private String	encryptionType;				// WEP, WPA, Any, and None
	private boolean	enterpriseUsed	= false;
	private String	password;
	private String	proxyType		= "None";		// None Manual or Auto
	// If the ProxyType field is set to Manual
	private String	proxyServer;
	private int		proxyServerPort;
	private String	proxyUsername;
	private String	proxyPassword;
	// If the ProxyType field is set to Auto
	private String	proxyPACURL;

	// EAPClientConfiguration
	// Enterprise Settings
	// Protocols
	private String	acceptEAPTypes;				// 13 = TLS 17 = LEAP 21 =
													// TTLS 25 = PEAP 43
													// = EAP-FAST 18=EAP-SIM,
													// format:
													// 13,17,21,25,43
	private boolean	eAPFASTProvisionPAC;
	private boolean	eAPFASTProvisionPACAnonymously;
	private boolean	eAPFASTUsePAC;
	private String	tTLSInnerAuthentication;		// PAP,CHAP,MSCHAP OR
													// MACHAPv2
	// Authentication
	private String	username;
	private boolean	oneTimeUserPassword;
	private String	userPassword;
	private String	outerIdentity;
	// Trust
	private String	certificateNames;				// divide use "," // divide
													// use ","
	private String	payloadCertificateAnchorUUID;	// divide use ","
	private String	tLSTrustedServerNames;			// divide use ","
	private String	payloadCertificateUUID;
	private String	certificateName;

	public WifiProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_WIFI);
	}

	public long getWifiId()
	{
		return wifiId;
	}

	public void setWifiId(long wifiId)
	{
		this.wifiId = wifiId;
	}

	public String getSsidStr()
	{
		return ssidStr;
	}

	public void setSsidStr(String ssidStr)
	{
		this.ssidStr = ssidStr;
	}

	public boolean isHiddenNetwork()
	{
		return hiddenNetwork;
	}

	public void setHiddenNetwork(boolean hiddenNetwork)
	{
		this.hiddenNetwork = hiddenNetwork;
	}

	public boolean isAutoJoin()
	{
		return autoJoin;
	}

	public void setAutoJoin(boolean autoJoin)
	{
		this.autoJoin = autoJoin;
	}

	public String getEncryptionType()
	{
		return encryptionType;
	}

	public void setEncryptionType(String encryptionType)
	{
		this.encryptionType = encryptionType;
	}

	public boolean isEnterpriseUsed()
	{
		return enterpriseUsed;
	}

	public void setEnterpriseUsed(boolean enterpriseUsed)
	{
		this.enterpriseUsed = enterpriseUsed;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getProxyType()
	{
		return proxyType;
	}

	public void setProxyType(String proxyType)
	{
		this.proxyType = proxyType;
	}

	public String getProxyServer()
	{
		return proxyServer;
	}

	public void setProxyServer(String proxyServer)
	{
		this.proxyServer = proxyServer;
	}

	public int getProxyServerPort()
	{
		return proxyServerPort;
	}

	public void setProxyServerPort(int proxyServerPort)
	{
		this.proxyServerPort = proxyServerPort;
	}

	public String getProxyUsername()
	{
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername)
	{
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword()
	{
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword)
	{
		this.proxyPassword = proxyPassword;
	}

	public String getProxyPACURL()
	{
		return proxyPACURL;
	}

	public void setProxyPACURL(String proxyPACURL)
	{
		this.proxyPACURL = proxyPACURL;
	}

	public String getAcceptEAPTypes()
	{
		return acceptEAPTypes;
	}

	public void setAcceptEAPTypes(String acceptEAPTypes)
	{
		this.acceptEAPTypes = acceptEAPTypes;
	}

	public boolean iseAPFASTProvisionPAC()
	{
		return eAPFASTProvisionPAC;
	}

	public void seteAPFASTProvisionPAC(boolean eAPFASTProvisionPAC)
	{
		this.eAPFASTProvisionPAC = eAPFASTProvisionPAC;
	}

	public boolean iseAPFASTProvisionPACAnonymously()
	{
		return eAPFASTProvisionPACAnonymously;
	}

	public void seteAPFASTProvisionPACAnonymously(boolean eAPFASTProvisionPACAnonymously)
	{
		this.eAPFASTProvisionPACAnonymously = eAPFASTProvisionPACAnonymously;
	}

	public boolean iseAPFASTUsePAC()
	{
		return eAPFASTUsePAC;
	}

	public void seteAPFASTUsePAC(boolean eAPFASTUsePAC)
	{
		this.eAPFASTUsePAC = eAPFASTUsePAC;
	}

	public String gettTLSInnerAuthentication()
	{
		return tTLSInnerAuthentication;
	}

	public void settTLSInnerAuthentication(String tTLSInnerAuthentication)
	{
		this.tTLSInnerAuthentication = tTLSInnerAuthentication;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public boolean isOneTimeUserPassword()
	{
		return oneTimeUserPassword;
	}

	public void setOneTimeUserPassword(boolean oneTimeUserPassword)
	{
		this.oneTimeUserPassword = oneTimeUserPassword;
	}

	public String getUserPassword()
	{
		return userPassword;
	}

	public void setUserPassword(String userPassword)
	{
		this.userPassword = userPassword;
	}

	public String getOuterIdentity()
	{
		return outerIdentity;
	}

	public void setOuterIdentity(String outerIdentity)
	{
		this.outerIdentity = outerIdentity;
	}

	public String getCertificateNames()
	{
		return certificateNames;
	}

	public void setCertificateNames(String certificateNames)
	{
		this.certificateNames = certificateNames;
	}

	public String getPayloadCertificateAnchorUUID()
	{
		return payloadCertificateAnchorUUID;
	}

	public void setPayloadCertificateAnchorUUID(String payloadCertificateAnchorUUID)
	{
		this.payloadCertificateAnchorUUID = payloadCertificateAnchorUUID;
	}

	public String gettLSTrustedServerNames()
	{
		return tLSTrustedServerNames;
	}

	public void settLSTrustedServerNames(String tLSTrustedServerNames)
	{
		this.tLSTrustedServerNames = tLSTrustedServerNames;
	}

	public String getPayloadCertificateUUID()
	{
		return payloadCertificateUUID;
	}

	public void setPayloadCertificateUUID(String payloadCertificateUUID)
	{
		this.payloadCertificateUUID = payloadCertificateUUID;
	}

	public String getCertificateName()
	{
		return certificateName;
	}

	public void setCertificateName(String certificateName)
	{
		this.certificateName = certificateName;
	}
}
