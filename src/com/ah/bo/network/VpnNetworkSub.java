/**
 *@filename		VpnNetworkSub.java
 *@version
 *@author		fisher
 *@createtime	2008-9-12 PM 01:35:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.util.MgrUtil;

/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Embeddable
public class VpnNetworkSub implements Serializable{
	
	private static final long	serialVersionUID	= 1L;
	
	private int key;
	
	private String ipNetwork;
	
	private int ipBranches;
	
	private String localIpNetwork;
	
	private boolean subnetClassification;
	
	private boolean reserveClassification;
	
	private boolean enablePortForwarding;
	
	/**
	 * uniqueSubnetworkForEachBranches=true && enableNat=false  : UC-1
	 * uniqueSubnetworkForEachBranches=true && enableNat=true	: UC-2
	 * uniqueSubnetworkForEachBranches=false && enableNat=true  : UC-3
	 */
	private boolean uniqueSubnetworkForEachBranches = true;
	
	public static final byte DEFAULT_GATEWAY_FIRST_IP = 0;
	
	public static final byte DEFAULT_GATEWAY_LAST_IP  = 1;
	
	private byte defaultGateway = DEFAULT_GATEWAY_FIRST_IP;
	
	/*-----------------------DNAT settings---------------------*/
	private boolean enableNat;
	
	/*---------------DHCP settings-------------------------*/
	private boolean enableDhcp; // For 'Internal Use'
	
	private int leftEnd;
	
	private int rightEnd;
	
	private String ntpServerIp;
	
	private boolean overrideDNSService = false;
	
	private boolean enableArpCheck = true;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPN_DNS_ID")
	private DnsServiceProfile dnsService;
	
	public static final int DEFAULT_LEASETIME = 86400;
	@Range(min = 60, max = 86400000)
	private int leaseTime=DEFAULT_LEASETIME;
	
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String domainName;
	/*---------------DHCP settings-------------------------*/
	
	@Transient
	public String getEnableDhcpStr(){
		if (enableDhcp){
			return "Enabled";
		}
		return "Disabled";
	}
	@Transient
	public String getLeaseTimeStr(){
		if (!isEnableDhcp()){
			return "";
		}
		return String.valueOf(leaseTime);
	}
	
	public String getIpNetwork() {
		return ipNetwork;
	}

	public void setIpNetwork(String ipNetwork) {
		this.ipNetwork = MgrUtil.getStartIpAddressValue(ipNetwork);
	}

	public int getIpBranches() {
		return ipBranches;
	}

	public void setIpBranches(int ipBranches) {
		this.ipBranches = ipBranches;
	}

	public long getIpBranchesCount(){
		String [] networkMask  = ipNetwork.split("/");
		long netCount= ((long)(Math.pow(2,32-Integer.parseInt(networkMask[1]))/ipBranches)) -3;
	    if (netCount<0) {
	    	netCount=0;
	    }
	    return netCount;
	}
	
	public long getRangeSize(){
		return getIpBranchesCount()-leftEnd-rightEnd;
		
	}
	
	public int getLeftEnd() {
		return leftEnd;
	}

	public void setLeftEnd(int leftEnd) {
		this.leftEnd = leftEnd;
	}

	public int getRightEnd() {
		return rightEnd;
	}

	public void setRightEnd(int rightEnd) {
		this.rightEnd = rightEnd;
	}

	public boolean isSubnetClassification() {
		return subnetClassification;
	}

	public void setSubnetClassification(boolean subnetClassification) {
		this.subnetClassification = subnetClassification;
	}

	public boolean isReserveClassification() {
		return reserveClassification;
	}

	public void setReserveClassification(boolean reserveClassification) {
		this.reserveClassification = reserveClassification;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public boolean isEnableDhcp() {
		return enableDhcp;
	}

	public void setEnableDhcp(boolean enableDhcp) {
		this.enableDhcp = enableDhcp;
	}

	public String getNtpServerIp() {
		return ntpServerIp;
	}

	public void setNtpServerIp(String ntpServerIp) {
		this.ntpServerIp = ntpServerIp;
	}

	public int getLeaseTime() {
		return leaseTime;
	}

	public void setLeaseTime(int leaseTime) {
		this.leaseTime = leaseTime;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public boolean isUniqueSubnetworkForEachBranches() {
		return uniqueSubnetworkForEachBranches;
	}
	
	public void setUniqueSubnetworkForEachBranches(
			boolean uniqueSubnetworkForEachBranches) {
		this.uniqueSubnetworkForEachBranches = uniqueSubnetworkForEachBranches;
	}
	
	public boolean isEnableNat() {
		return enableNat;
	}
	
	public void setEnableNat(boolean enableNat) {
		this.enableNat = enableNat;
	}
	
	public String getLocalIpNetwork() {
		return localIpNetwork;
	}
	
	public void setLocalIpNetwork(String localIpNetwork) {
		this.localIpNetwork = MgrUtil.getStartIpAddressValue(localIpNetwork);
	}
	
	public byte getDefaultGateway() {
		return defaultGateway;
	}
	
	public void setDefaultGateway(byte defaultGateway) {
		this.defaultGateway = defaultGateway;
	}
	
	public boolean isEnablePortForwarding() {
		return enablePortForwarding;
	}
	
	public void setEnablePortForwarding(boolean enablePortForwarding) {
		this.enablePortForwarding = enablePortForwarding;
	}
	
	public boolean isOverrideDNSService() {
		return overrideDNSService;
	}
	
	public void setOverrideDNSService(boolean overrideDNSService) {
		this.overrideDNSService = overrideDNSService;
	}
	
	public DnsServiceProfile getDnsService() {
		return dnsService;
	}
	
	public void setDnsService(DnsServiceProfile dnsService) {
		this.dnsService = dnsService;
	}
	public boolean isEnableArpCheck() {
		return enableArpCheck;
	}
	public void setEnableArpCheck(boolean enableArpCheck) {
		this.enableArpCheck = enableArpCheck;
	}
}