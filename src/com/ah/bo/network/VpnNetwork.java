/**
 *@filename		AccessConsole.java
 *@version
 *@author		Fiona
 *@createtime	2008-9-12 PM 01:35:37
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.jclark.xml.tok.Buffer;

/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "VPN_NETWORK")
@org.hibernate.annotations.Table(appliesTo = "VPN_NETWORK", indexes = {
		@Index(name = "VPN_NETWORK_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class VpnNetwork implements HmBo {
	
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String networkName;
	
	public static final int VPN_NETWORK_TYPE_INTERNAL=1;
	public static final int VPN_NETWORK_TYPE_GUEST=2;
	public static final int VPN_NETWORK_TYPE_MANAGERMENT=3;
	private int networkType=VPN_NETWORK_TYPE_INTERNAL;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "VLAN_ID")
//	private Vlan vlan;
	
	public static final int VPN_NETWORK_WEBSECURITY_NONE=0;
	public static final int VPN_NETWORK_WEBSECURITY_WEBSENSE=1;
	public static final int VPN_NETWORK_WEBSECURITY_BARRACUDA=2;
	private int webSecurity=VPN_NETWORK_WEBSECURITY_NONE;
	
	public static int NETWORK_WEBSECURITY_FAIL_DENY=1;
	public static int NETWORK_WEBSECURITY_FAIL_PERMIT=2;
	private int failConnectionOption;

	
	public static EnumItem[] ENUM_VPN_NETWORK_WEBSECURITY = MgrUtil.enumItems("enum.vpn.network.websecurity.",
			new int[] {VPN_NETWORK_WEBSECURITY_NONE,VPN_NETWORK_WEBSECURITY_BARRACUDA,VPN_NETWORK_WEBSECURITY_WEBSENSE});
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPN_DNS_ID")
	private DnsServiceProfile vpnDnsService;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	
	/*------Network Type: VPN_NETWORK_TYPE_GUEST - SUbNetworks: s----------*/
	private final static String DEFAULT_IPADDRESS_SPACE = "192.168.83.0/24";
	private String ipAddressSpace = DEFAULT_IPADDRESS_SPACE;
	private int guestLeftReserved;
	private int guestRightReserved;
	/*------Network Type: VPN_NETWORK_TYPE_GUEST - SUbNetworks: e----------*/
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_NETWORK_PORT_FORWARDING", joinColumns = @JoinColumn(name = "VPN_NETWORK_ID", nullable = false))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<PortForwarding> portForwardings = new ArrayList<PortForwarding>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_NETWORK_SUBITEM", joinColumns = @JoinColumn(name = "VPN_NETWORK_SUB_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<VpnNetworkSub> subItems = new ArrayList<VpnNetworkSub>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_NETWORK_SUBNETCLASS", joinColumns = @JoinColumn(name = "VPN_NETWORK_SUBNETCLASS_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SingleTableItem> subNetwokClass = new ArrayList<SingleTableItem>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_NETWORK_IP_RESERVE_ITEM", joinColumns = @JoinColumn(name = "VPN_NETWORK_RESERVECLASS_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SingleTableItem> reserveClass = new ArrayList<SingleTableItem>();
	
	@OneToMany(mappedBy = "vpnNetwork", fetch = FetchType.LAZY)
	private List<SubNetworkResource> subNetworkRes = new ArrayList<SubNetworkResource>();

	private boolean enableDhcp;
	
	private boolean enableArpCheck = true;
	
	private String ntpServerIp;
	
	@Range(min = 60, max = 86400000)
	private int leaseTime=86400;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String domainName;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_NETWORK_CUSTOM", joinColumns = @JoinColumn(name = "VPN_NETWORK_CUSTOM_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DhcpServerOptionsCustom> customOptions = new ArrayList<DhcpServerOptionsCustom>();

	private boolean defaultFlag;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VPN_NETWORK_SUBNET_CUSTOMS", joinColumns = @JoinColumn(name = "VPN_NETWORK_SUBNET_CUSTOM_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SubnetworkDHCPCustom> subnetworkDHCPCustoms = new ArrayList<SubnetworkDHCPCustom>();
	
	@Transient
	public String customOptionDisplayStyle = "none";// by default
	
//	@Transient
//	private List<SingleTableItem> tmpSubNetwokClass = new ArrayList<SingleTableItem>();
//	@Transient
//	private List<SingleTableItem> tmpReserveClass = new ArrayList<SingleTableItem>();
	@Transient
	private List<SubnetworkDHCPCustom> tmpSubNetworkDHCPCustoms = new ArrayList<SubnetworkDHCPCustom>();
	
	@Transient
	private List<PortForwarding> tmpPortForwardings = new ArrayList<PortForwarding>();


	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return networkName;
	}

	@Transient
	boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	@Override
	public VpnNetwork clone() {
		try {
			return (VpnNetwork) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public String getNetworkName() {
		return networkName;
	}
	
	public String getNetworkNameSubstr() {
		if (networkName==null) {
			return "";
		}
		if (networkName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return networkName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public int getNetworkType() {
		return networkType;
	}

	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}

//	public Vlan getVlan() {
//		return vlan;
//	}
//
//	public void setVlan(Vlan vlan) {
//		this.vlan = vlan;
//	}

	public int getWebSecurity() {
		return webSecurity;
	}

	public void setWebSecurity(int webSecurity) {
		this.webSecurity = webSecurity;
	}
	
	public String getWebSecurityString(){
		if (webSecurity<=0){
			if(NmsUtil.isOpenDNSEnabled(getOwner())){
				return MgrUtil.getEnumString("enum.vpn.network.websecurity.3");
			}
			return  MgrUtil.getEnumString("enum.vpn.network.websecurity.0");
		}else{
			StringBuffer buffer = new StringBuffer();
			if(NmsUtil.isOpenDNSEnabled(getOwner())){
				buffer.append(MgrUtil.getEnumString("enum.vpn.network.websecurity.3"));
				buffer.append(",");
			}
			
			buffer.append(MgrUtil.getEnumString("enum.vpn.network.websecurity." + webSecurity));
			return buffer.toString();
		}
	}
	
	public List<PortForwarding> getTmpPortForwardings() {
		return tmpPortForwardings;
	}

	public void setTmpPortForwardings(List<PortForwarding> tmpPortForwardings) {
		this.tmpPortForwardings = tmpPortForwardings;
	}
	
	@Transient
	public String getNetworkTypeString() {
		if (networkType == VPN_NETWORK_TYPE_INTERNAL) {
			return MgrUtil.getUserMessage("config.vpn.network.internalUse");
		} else if (networkType == VPN_NETWORK_TYPE_GUEST) {
			return MgrUtil.getUserMessage("config.vpn.network.guestUse");
		} else if (networkType == VPN_NETWORK_TYPE_MANAGERMENT){
		    return MgrUtil.getUserMessage("config.vpn.network.management");
		}
		return "";
	}
	
//	@Transient
//	public String getNetworkVlanString() {
//		if (vlan != null) {
//			return getNetworkName() + " (" + vlan.getVlanName() + ")";
//		} else {
//			return getNetworkName();
//		}
//	}

	public DnsServiceProfile getVpnDnsService() {
		return vpnDnsService;
	}

	public void setVpnDnsService(DnsServiceProfile vpnDnsService) {
		this.vpnDnsService = vpnDnsService;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnableDhcp() {
		return enableDhcp;
	}
	
	public String getEnableDhcpString(){
		if (enableDhcp){
			return "Enabled";
		}
		return "Disabled";
	}
	
	public String getDhcpAllString(){
		if (networkType==VPN_NETWORK_TYPE_GUEST) {
			if (enableDhcp){
				return "Enabled";
			}
			return "Disabled";
		} else {
			if (subItems==null || subItems.isEmpty()) {
				return "Disabled";
			}
			int dhcpCount = 0;
			for(VpnNetworkSub vs: subItems){
				if (vs.isEnableDhcp()) {
					dhcpCount ++;
				}
			}
			if (dhcpCount==0) {
				return "Disabled";
			}
			if (dhcpCount==subItems.size()) {
				return "Enabled";
			}
			return "Partial";
		}
	}

	public void setEnableDhcp(boolean enableDhcp) {
		this.enableDhcp = enableDhcp;
		if(!enableDhcp) {
			getCustomOptions().clear();
		}
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
	
	public String getLeaseTimeString(){
		if (!isEnableDhcp()){
			return "";
		}
		return String.valueOf(leaseTime);
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<DhcpServerOptionsCustom> getCustomOptions() {
		return customOptions;
	}

	public void setCustomOptions(List<DhcpServerOptionsCustom> customOptions) {
		this.customOptions = customOptions;
	}

	public List<SingleTableItem> getSubNetwokClass() {
        return subNetwokClass;
    }

    public void setSubNetwokClass(List<SingleTableItem> subNetwokClass) {
        this.subNetwokClass = subNetwokClass;
    }

    public List<SingleTableItem> getReserveClass() {
		return reserveClass;
	}

	public void setReserveClass(List<SingleTableItem> reserveClass) {
		this.reserveClass = reserveClass;
	}

	public List<VpnNetworkSub> getSubItems() {
		return subItems;
	}

	public void setSubItems(List<VpnNetworkSub> subItems) {
		this.subItems = subItems;
	}

	public List<PortForwarding> getPortForwardings() {
		return portForwardings;
	}

	public void setPortForwardings(List<PortForwarding> portForwardings) {
		this.portForwardings = portForwardings;
	}
	
	public String reserveItems2String(int key, List<SingleTableItem> items,long domainId) {
		StringBuilder sbf = new StringBuilder();
		boolean notfirstRow = false;
		for (SingleTableItem iterable_element : items) {
			if (iterable_element.getKey() == key) {
				if (notfirstRow) {
					sbf.append("<br/>");
				}
				String ipAddress = iterable_element.getIpAddress();
				boolean isExistIp = StringUtils.isNotBlank(ipAddress);
                switch (iterable_element.getType()) {
				case SingleTableItem.TYPE_MAP:
					if (null != iterable_element.getLocation()) {
						if(isExistIp) sbf.append(ipAddress).append(":");
						 sbf.append(" Topology Node: ");
						sbf.append(iterable_element.getLocation().getMapName());
					}
					break;
				case SingleTableItem.TYPE_HIVEAPNAME:
				    if(isExistIp) sbf.append(ipAddress).append(":");
				    sbf.append(" Device Name: ");
					sbf.append(iterable_element.getTypeName());
					break;
				case SingleTableItem.TYPE_CLASSIFIER:
				    if(isExistIp) sbf.append(ipAddress).append(":");
				    sbf.append(" Custom Tags: ");
					sbf.append(iterable_element.getClassifierName(domainId));
					break;
				default:
					break;
				}
				notfirstRow = true;
			}
		}
		return sbf.toString();
	}

	public String getCustomOptionDisplayStyle() {
		return customOptionDisplayStyle;
	}

	public void setCustomOptionDisplayStyle(String customOptionDisplayStyle) {
		this.customOptionDisplayStyle = customOptionDisplayStyle;
	}

//	public List<SingleTableItem> getTmpSubNetwokClass() {
//        return tmpSubNetwokClass;
//    }
//
//    public void setTmpSubNetwokClass(List<SingleTableItem> tmpSubNetwokClass) {
//        this.tmpSubNetwokClass = tmpSubNetwokClass;
//    }
//
//    public List<SingleTableItem> getTmpReserveClass() {
//		return tmpReserveClass;
//	}
//
//	public void setTmpReserveClass(List<SingleTableItem> tmpReserveClass) {
//		this.tmpReserveClass = tmpReserveClass;
//	}

	public int getFailConnectionOption() {
		return failConnectionOption;
	}

	public void setFailConnectionOption(int failConnectionOption) {
		this.failConnectionOption = failConnectionOption;
	}

	public String getIpAddressSpace() {
		return ipAddressSpace;
	}

	public void setIpAddressSpace(String ipAddressSpace) {
		this.ipAddressSpace = MgrUtil.getStartIpAddressValue(ipAddressSpace);
	}
	
	public List<SubNetworkResource> getSubNetworkRes() {
		return subNetworkRes;
	}

	public void setSubNetworkRes(List<SubNetworkResource> subNetworkRes) {
		this.subNetworkRes = subNetworkRes;
	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	
	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public boolean equalsSubNetwok(Object obj){
		VpnNetwork vpnNetwork;
		if(obj instanceof VpnNetwork){
			vpnNetwork = (VpnNetwork)obj;
		}else{
			return false;
		}
		
		if(this.networkType != vpnNetwork.getNetworkType()){
			return false;
		}else if(this.networkType == VPN_NETWORK_TYPE_GUEST){
//			return this.ipAddressSpace != null && this.ipAddressSpace.equals(vpnNetwork.getIpAddressSpace());
			return true;
		}
		
		if(this.subItems != null && vpnNetwork.getSubItems() != null){
			for(VpnNetworkSub item1 : this.subItems){
				boolean find = false;
				for(VpnNetworkSub item2 : vpnNetwork.getSubItems()){
					if(item1.getIpNetwork().equals(item2.getIpNetwork())  
							&& item1.getLocalIpNetwork().equals(
									item2.getLocalIpNetwork()) &&
							item1.getIpBranches() == item2.getIpBranches() && 
							item1.getLeftEnd() == item2.getLeftEnd() && 
							item1.getRightEnd() == item2.getRightEnd()
							&& item1.getDefaultGateway() == item2.getDefaultGateway()){
						find = true;
					}
				}
				if(!find){
					return false;
				}
			}
			
			for(VpnNetworkSub item1 : vpnNetwork.getSubItems()){
				boolean find = false;
				for(VpnNetworkSub item2 : this.subItems){
					if(item1.getIpNetwork().equals(item2.getIpNetwork()) 
							&& item1.getLocalIpNetwork().equals(
									item2.getLocalIpNetwork()) && 
							item1.getIpBranches() == item2.getIpBranches() && 
							item1.getLeftEnd() == item2.getLeftEnd() && 
							item1.getRightEnd() == item2.getRightEnd()
							&& item1.getDefaultGateway() == item2.getDefaultGateway()){
						find = true;
					}
				}
				if(!find){
					return false;
				}
			}
		}else if(this.subItems == null && vpnNetwork.getSubNetwokClass() == null){
			return true;
		}else{
			return false;
		}
		
		return true;
	}
	
	public boolean equalsReserveClass(Object obj){
		VpnNetwork vpnNetwork;
		if(obj instanceof VpnNetwork){
			vpnNetwork = (VpnNetwork)obj;
		}else{
			return false;
		}
		
		if(this.networkType == VPN_NETWORK_TYPE_GUEST){
			return false;
		}
		
		if(this.reserveClass != null && vpnNetwork.getReserveClass() != null){
			for(SingleTableItem item1 : this.reserveClass){
				boolean find = false;
				for(SingleTableItem item2 : vpnNetwork.getReserveClass()){
					if(item1.equals(item2)){
						find = true;
						break;
					}
				}
				if(!find){
					return false;
				}
			}
			
			for(SingleTableItem item1 : vpnNetwork.getReserveClass()){
				boolean find = false;
				for(SingleTableItem item2 : this.reserveClass){
					if(item1.equals(item2)){
						find = true;
						break;
					}
				}
				if(!find){
					return false;
				}
			}
		}else if(this.reserveClass == null && vpnNetwork.getReserveClass() == null){
			return true;
		}else{
			return false;
		}
		
		return true;
	}
	
	@Transient
	private boolean blnMgtNetwork;
	
	public boolean isBlnMgtNetwork() {
		return blnMgtNetwork;
	}

	public void setBlnMgtNetwork(boolean blnMgtNetwork) {
		this.blnMgtNetwork = blnMgtNetwork;
	}

	public int getGuestLeftReserved() {
		return guestLeftReserved;
	}

	public void setGuestLeftReserved(int guestLeftReserved) {
		this.guestLeftReserved = guestLeftReserved;
	}

	public int getGuestRightReserved() {
		return guestRightReserved;
	}

	public void setGuestRightReserved(int guestRightReserved) {
		this.guestRightReserved = guestRightReserved;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof VpnNetwork)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((VpnNetwork) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	public List<SubnetworkDHCPCustom> getSubnetworkDHCPCustoms() {
		return subnetworkDHCPCustoms;
	}

	public void setSubnetworkDHCPCustoms(List<SubnetworkDHCPCustom> subnetworkDHCPCustoms) {
		this.subnetworkDHCPCustoms = subnetworkDHCPCustoms;
	}

	public List<SubnetworkDHCPCustom> getTmpSubNetworkDHCPCustoms() {
		return tmpSubNetworkDHCPCustoms;
	}

	public void setTmpSubNetworkDHCPCustoms(List<SubnetworkDHCPCustom> tmpSubNetworkDHCPCustoms) {
		this.tmpSubNetworkDHCPCustoms = tmpSubNetworkDHCPCustoms;
	}

	@Transient
    private boolean referenced = false;
	/**
	 * Field of determine whether the current edit object is referenced by other data.<br>
	 * Default as false;
	 */
    public boolean isReferenced() {
        return referenced;
    }
    public void setReferenced(boolean referenced) {
        this.referenced = referenced;
    }

	public boolean isEnableArpCheck() {
		return enableArpCheck;
	}

	public void setEnableArpCheck(boolean enableArpCheck) {
		this.enableArpCheck = enableArpCheck;
	}
	
}