package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.IkeAuthMethodValue;
import com.ah.xml.be.config.IkeDhGroupValue;
import com.ah.xml.be.config.IkeHashValue;
import com.ah.xml.be.config.IkePfsGroupValue;
import com.ah.xml.be.config.VpnModeValue;

/**
 * @author zhang
 * @version 2009-4-28 10:13:15
 */

public interface VPNProfileInt {
	
	public boolean isVPNServer();
	
	public boolean isVPNClient();
	
	public boolean isConfigVpn();
	
	public String getVpnProfileGuiName();
	
	public String getVpnProfileName();
	
	public String getClientPoolName();
	
	public String getClientPoolIpRange();
	
	public String getClientPoolNetMask();
	
	public String getXauthClientListName();
	
	public int getXauthClientUserSize();
	
	public String getXauthClientUserName(int index);
	
	public String getXauthClientPassword(int index);
	
	public String getVPNServerName();
	
	public int getVPNClientIpsecSize();
	
	public int getVpnIpsecTunnelSize();
	
	public String getVPNClientIpsecName(int index);
	
	public String getPhase1Encryption();
	
	public IkeHashValue getPhase1Hash();
	
	public boolean isConfigPhase1Lifetime();
	
	public int getPhase1Lifetime();
	
	public IkeDhGroupValue getPhase1Dhgroup();
	
	public IkeAuthMethodValue getPhase1Auth();
	
	public String getPhase2Encryption();
	
	public IkeHashValue getPhase2Hash();
	
	public int getPhase2Lifetime();
	
	public IkePfsGroupValue getPhase2PfsGroup();
	
	public String getDNSServerAddress() throws CreateXMLException;
	
	public String getVpnClientGateWay(int index);
	
	public boolean isNatTraversalEnable();
	
	public String getIpSecTunnelName(int index);

	public String getClientUserName();
	
	public String getClientUserPassword();
	
	//tunnel policy
	
	public String getTunnlePolicyName();
	
	public String getIpsecTunnelName();

	public String getIpPoolName();
	
	public int getClientTunnelSize();
	
	public String getIpsecName(int index);
	
	public boolean isPrimary(int index);
	
	public int getIdleInterval();
	
	public int getRetry();
	
	public int getRetryInterval();
	
	public boolean isConfigLocalIkeId();
	
	public boolean isConfigPeerIkeId();
	
	public boolean isConfigIkeAddress();
	
	public boolean isConfigIkeAsn1dn();
	
	public boolean isConfigIkeFqdn();
	
	public boolean isConfigIkeUfqdn();
	
	public String getIkeAddressValue();
	
	public String getIkeAsn1dnValue();
	
	public String getIkeFqdnValue();
	
	public String getIkeUfqdnValue();
	
	public int getL3TunnelExceptionSize();
	
	public String getL3TunnelException(int index);
	
	public VpnModeValue getVpnMode();
	
	public int getNatPolicySize();
	
	public String getNaPolicyName(int index);
	
//	public String getGreGateway(int index);
}
