package com.ah.mdm.core.profile.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_profile_vpn")
public class VpnProfileInfo extends AbstractProfileInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long	vpnId;

	// PayloadDisplayName
	private String	userDefinedName;
	private boolean	overridePrimary;
	private String	vpnType;											// "L2TP",
																		// "PPTP","IPSec","VPN"
	private String	subVpnType;										// it is
																		// useful
																		// while
																		// the
																		// VPN
																		// type
																		// is
																		// "VPN"
	private String	proxiesType						= "None";			// "None","Manual","Automatic"

	// For Proxy manual
	private int		httpEnable;
	private String	httpProxy;
	private int		httpPort;
	private String	httpProxyUsername;
	private String	httpProxyPassword;
	// For Proxy automatic
	// private String proxyAutoConfigEnable;
	private String	proxyAutoConfigURLString;

	// PPP
	private String	authType						= "Password";		// Password
																		// or
																		// SecurID
	// AuthName
	private String	authName;
	private String	authPassword;
	private boolean	tokenCard;
	// CommRemoteAddress
	private String	commRemoteAddress;
	private String	authEAPPlugins					= "EAP-RSA";
	private String	authProtocol					= "EAP";

	// PPTP
	private boolean	ccpMPPE40Enabled;
	private boolean	ccpMPPE128Enabled;
	private boolean	ccpEnabled;
	private String	encryptionLevel;									// none,automatic,maximumBit

	// IPSec
	private String	remoteAddress;
	private String	authenticationMethod			= "SharedSecret";	// Certificate
	private String	xAuthName;
	private String	xAuthPassword;
	private int		xAuthEnabled					= 1;

	private String	localIdentifier;
	private String	sharedSecret;
	private String	payloadCertificateUUID;
	// ipsec machine authentication
	private boolean	promptForVPNPIN					= true;
	private boolean	onDemandEnable;

	// IPSec
	private boolean	promptForPassword;
	private boolean	hybridAuth;

	private String	onDemandMatchDomainsAlwaysHost	= "";
	private String	onDemandMatchDomainsNeverHost	= "";
	private String	onDemandMatchDomainsOnRetryHost	= "";
	private String	certificateUrl;

	public VpnProfileInfo()
	{
		super(ProfilePayloadType.PAYLOAD_TYPE_VPN);
	}

	public String getCertificateUrl()
	{
		return certificateUrl;
	}

	public void setCertificateUrl(String certificateName)
	{
		this.certificateUrl = certificateName;
	}

	public long getVpnId()
	{
		return vpnId;
	}

	public void setVpnId(long vpnId)
	{
		this.vpnId = vpnId;
	}

	public String getUserDefinedName()
	{
		return userDefinedName;
	}

	public void setUserDefinedName(String userDefinedName)
	{
		this.userDefinedName = userDefinedName;
	}

	public boolean isOverridePrimary()
	{
		return overridePrimary;
	}

	public void setOverridePrimary(boolean overridePrimary)
	{
		this.overridePrimary = overridePrimary;
	}

	public String getVpnType()
	{
		return vpnType;
	}

	public void setVpnType(String vpnType)
	{
		this.vpnType = vpnType;
	}

	public String getSubVpnType()
	{
		return subVpnType;
	}

	public void setSubVpnType(String subVpnType)
	{
		this.subVpnType = subVpnType;
	}

	public String getProxiesType()
	{
		return proxiesType;
	}

	public void setProxiesType(String proxiesType)
	{
		this.proxiesType = proxiesType;
	}

	public int getHttpEnable()
	{
		return httpEnable;
	}

	public void setHttpEnable(int httpEnable)
	{
		this.httpEnable = httpEnable;
	}

	public String getHttpProxy()
	{
		return httpProxy;
	}

	public void setHttpProxy(String httpProxy)
	{
		this.httpProxy = httpProxy;
	}

	public int getHttpPort()
	{
		return httpPort;
	}

	public void setHttpPort(int httpPort)
	{
		this.httpPort = httpPort;
	}

	public String getHttpProxyUsername()
	{
		return httpProxyUsername;
	}

	public void setHttpProxyUsername(String httpProxyUsername)
	{
		this.httpProxyUsername = httpProxyUsername;
	}

	public String getHttpProxyPassword()
	{
		return httpProxyPassword;
	}

	public void setHttpProxyPassword(String httpProxyPassword)
	{
		this.httpProxyPassword = httpProxyPassword;
	}

	public String getProxyAutoConfigURLString()
	{
		return proxyAutoConfigURLString;
	}

	public void setProxyAutoConfigURLString(String proxyAutoConfigURLString)
	{
		this.proxyAutoConfigURLString = proxyAutoConfigURLString;
	}

	public String getAuthType()
	{
		return authType;
	}

	public void setAuthType(String authType)
	{
		this.authType = authType;
	}

	public String getAuthName()
	{
		return authName;
	}

	public void setAuthName(String authName)
	{
		this.authName = authName;
	}

	public String getAuthPassword()
	{
		return authPassword;
	}

	public void setAuthPassword(String authPassword)
	{
		this.authPassword = authPassword;
	}

	public boolean isTokenCard()
	{
		return tokenCard;
	}

	public void setTokenCard(boolean tokenCard)
	{
		this.tokenCard = tokenCard;
	}

	public String getCommRemoteAddress()
	{
		return commRemoteAddress;
	}

	public void setCommRemoteAddress(String commRemoteAddress)
	{
		this.commRemoteAddress = commRemoteAddress;
	}

	public String getAuthEAPPlugins()
	{
		return authEAPPlugins;
	}

	public void setAuthEAPPlugins(String authEAPPlugins)
	{
		this.authEAPPlugins = authEAPPlugins;
	}

	public String getAuthProtocol()
	{
		return authProtocol;
	}

	public void setAuthProtocol(String authProtocol)
	{
		this.authProtocol = authProtocol;
	}

	public String getRemoteAddress()
	{
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress)
	{
		this.remoteAddress = remoteAddress;
	}

	public String getAuthenticationMethod()
	{
		return authenticationMethod;
	}

	public void setAuthenticationMethod(String authenticationMethod)
	{
		this.authenticationMethod = authenticationMethod;
	}

	public String getxAuthName()
	{
		return xAuthName;
	}

	public void setxAuthName(String xAuthName)
	{
		this.xAuthName = xAuthName;
	}

	public int getxAuthEnabled()
	{
		return xAuthEnabled;
	}

	public void setxAuthEnabled(int xAuthEnabled)
	{
		this.xAuthEnabled = xAuthEnabled;
	}

	public String getLocalIdentifier()
	{
		return localIdentifier;
	}

	public void setLocalIdentifier(String localIdentifier)
	{
		this.localIdentifier = localIdentifier;
	}

	public String getPayloadCertificateUUID()
	{
		return payloadCertificateUUID;
	}

	public void setPayloadCertificateUUID(String payloadCertificateUUID)
	{
		this.payloadCertificateUUID = payloadCertificateUUID;
	}

	public boolean isPromptForVPNPIN()
	{
		return promptForVPNPIN;
	}

	public void setPromptForVPNPIN(boolean promptForVPNPIN)
	{
		this.promptForVPNPIN = promptForVPNPIN;
	}

	public boolean isPromptForPassword()
	{
		return promptForPassword;
	}

	public void setPromptForPassword(boolean promptForPassword)
	{
		this.promptForPassword = promptForPassword;
	}

	public boolean isHybridAuth()
	{
		return hybridAuth;
	}

	public void setHybridAuth(boolean hybridAuth)
	{
		this.hybridAuth = hybridAuth;
	}

	public boolean isOnDemandEnable()
	{
		return onDemandEnable;
	}

	public void setOnDemandEnable(boolean onDemandEnable)
	{
		this.onDemandEnable = onDemandEnable;
	}

	public String getOnDemandMatchDomainsAlwaysHost()
	{
		return onDemandMatchDomainsAlwaysHost;
	}

	public void setOnDemandMatchDomainsAlwaysHost(String onDemandMatchDomainsAlwaysHost)
	{
		this.onDemandMatchDomainsAlwaysHost = onDemandMatchDomainsAlwaysHost;
	}

	public String getOnDemandMatchDomainsNeverHost()
	{
		return onDemandMatchDomainsNeverHost;
	}

	public void setOnDemandMatchDomainsNeverHost(String onDemandMatchDomainsNeverHost)
	{
		this.onDemandMatchDomainsNeverHost = onDemandMatchDomainsNeverHost;
	}

	public String getOnDemandMatchDomainsOnRetryHost()
	{
		return onDemandMatchDomainsOnRetryHost;
	}

	public void setOnDemandMatchDomainsOnRetryHost(String onDemandMatchDomainsOnRetryHost)
	{
		this.onDemandMatchDomainsOnRetryHost = onDemandMatchDomainsOnRetryHost;
	}

	public String getSharedSecret()
	{
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret)
	{
		this.sharedSecret = sharedSecret;
	}

	public String getEncryptionLevel()
	{
		return encryptionLevel;
	}

	public void setEncryptionLevel(String encryptionLevel)
	{
		this.encryptionLevel = encryptionLevel;
	}

	public boolean isCcpMPPE40Enabled()
	{
		return ccpMPPE40Enabled;
	}

	public void setCcpMPPE40Enabled(boolean ccpMPPE40Enabled)
	{
		this.ccpMPPE40Enabled = ccpMPPE40Enabled;
	}

	public boolean isCcpMPPE128Enabled()
	{
		return ccpMPPE128Enabled;
	}

	public void setCcpMPPE128Enabled(boolean ccpMPPE128Enabled)
	{
		this.ccpMPPE128Enabled = ccpMPPE128Enabled;
	}

	public boolean isCcpEnabled()
	{
		return ccpEnabled;
	}

	public void setCcpEnabled(boolean ccpEnabled)
	{
		this.ccpEnabled = ccpEnabled;
	}

	public String getxAuthPassword()
	{
		return xAuthPassword;
	}

	public void setxAuthPassword(String xAuthPassword)
	{
		this.xAuthPassword = xAuthPassword;
	}

}
