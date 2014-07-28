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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "VLAN_DHCP_SERVER",uniqueConstraints = { @UniqueConstraint(columnNames = {
		"OWNER", "PROFILENAME" }) })
@org.hibernate.annotations.Table(appliesTo = "VLAN_DHCP_SERVER", indexes = {
		@Index(name = "VLAN_DHCP_SERVER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class VlanDhcpServer implements HmBo {

	private static final long serialVersionUID = 1L;

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
	private String profileName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	@Range(min=1, max=4094)
	private int interVlan = 1;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String interfaceIp;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String interfaceNet;
	
	private boolean enablePing = true;
	
	/*
	 * DHCP Server options
	 */
	public static EnumItem[] ENUM_INTERFACE_FOR_OPTION = MgrUtil.enumItems(
		"enum.interface.dhcp.server.", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 });
	
	private short dhcpMgt = 0;
	
	public static final short ENABLE_DHCP_SERVER = 1;
	
	public static final short ENABLE_DHCP_RELAY = 2;
	
	private short typeFlag = ENABLE_DHCP_SERVER;
	
	private boolean authoritative = true;
	
	private boolean enableArp = true;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String defaultGateway;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String dnsServer1;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String dnsServer2;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String dnsServer3;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String domainName;
	
	private String leaseTime = "86400";
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String dhcpNetmask;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String pop3;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String smtp;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String wins1;
	
	// add wins2 from 3.2r2
	@Column(length = IP_ADDRESS_LENGTH)
	private String wins2;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String ntpServer1;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String ntpServer2;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String logsrv;
	
	private String mtu; 
	
	// remove hivemanage server from 3.2r2
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "IPADDRESS_ID")
//	private IpAddress hiveManagerServer;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DHCP_SERVER_CUSTOM", joinColumns = @JoinColumn(name = "VLAN_DHCP_SERVER_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DhcpServerOptionsCustom> customs = new ArrayList<DhcpServerOptionsCustom>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DHCP_SERVER_IPPOOL", joinColumns = @JoinColumn(name = "VLAN_DHCP_SERVER_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DhcpServerIpPool> ipPools = new ArrayList<DhcpServerIpPool>();
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String ipHelper1;
	
	@Column(length = IP_ADDRESS_LENGTH)
	private String ipHelper2;
	
	// add from 3.4r3
	private boolean natSupport;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return profileName;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getInterVlan()
	{
		return interVlan;
	}

	public void setInterVlan(int interVlan)
	{
		this.interVlan = interVlan;
	}

	public boolean isEnablePing()
	{
		return enablePing;
	}

	public void setEnablePing(boolean enablePing)
	{
		this.enablePing = enablePing;
	}

	public short getDhcpMgt()
	{
		return dhcpMgt;
	}

	public void setDhcpMgt(short dhcpMgt)
	{
		this.dhcpMgt = dhcpMgt;
	}

	public short getTypeFlag()
	{
		return typeFlag;
	}

	public void setTypeFlag(short typeFlag)
	{
		this.typeFlag = typeFlag;
	}

	public boolean isAuthoritative()
	{
		return authoritative;
	}

	public void setAuthoritative(boolean authoritative)
	{
		this.authoritative = authoritative;
	}

	public String getDefaultGateway()
	{
		return defaultGateway;
	}

	public void setDefaultGateway(String defaultGateway)
	{
		this.defaultGateway = defaultGateway;
	}

	public String getDnsServer1()
	{
		return dnsServer1;
	}

	public void setDnsServer1(String dnsServer1)
	{
		this.dnsServer1 = dnsServer1;
	}

	public String getDnsServer2()
	{
		return dnsServer2;
	}

	public void setDnsServer2(String dnsServer2)
	{
		this.dnsServer2 = dnsServer2;
	}

	public String getDnsServer3()
	{
		return dnsServer3;
	}

	public void setDnsServer3(String dnsServer3)
	{
		this.dnsServer3 = dnsServer3;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	public String getLeaseTime()
	{
		return leaseTime;
	}

	public void setLeaseTime(String leaseTime)
	{
		this.leaseTime = leaseTime;
	}

	public String getPop3()
	{
		return pop3;
	}

	public void setPop3(String pop3)
	{
		this.pop3 = pop3;
	}

	public String getSmtp()
	{
		return smtp;
	}

	public void setSmtp(String smtp)
	{
		this.smtp = smtp;
	}

	public String getNtpServer1()
	{
		return ntpServer1;
	}

	public void setNtpServer1(String ntpServer1)
	{
		this.ntpServer1 = ntpServer1;
	}

	public String getNtpServer2()
	{
		return ntpServer2;
	}

	public void setNtpServer2(String ntpServer2)
	{
		this.ntpServer2 = ntpServer2;
	}

	public String getLogsrv()
	{
		return logsrv;
	}

	public String getMtu()
	{
		return mtu;
	}

	public void setMtu(String mtu)
	{
		this.mtu = mtu;
	}

	public void setLogsrv(String logsrv)
	{
		this.logsrv = logsrv;
	}

	public List<DhcpServerOptionsCustom> getCustoms()
	{
		return customs;
	}

	public void setCustoms(List<DhcpServerOptionsCustom> customs)
	{
		this.customs = customs;
	}

	public List<DhcpServerIpPool> getIpPools()
	{
		return ipPools;
	}

	public void setIpPools(List<DhcpServerIpPool> ipPools)
	{
		this.ipPools = ipPools;
	}

	public String getIpHelper1()
	{
		return ipHelper1;
	}

	public void setIpHelper1(String ipHelper1)
	{
		this.ipHelper1 = ipHelper1;
	}

	public String getIpHelper2()
	{
		return ipHelper2;
	}

	public void setIpHelper2(String ipHelper2)
	{
		this.ipHelper2 = ipHelper2;
	}

	public String getDhcpNetmask()
	{
		return dhcpNetmask;
	}

	public String getInterfaceIp()
	{
		return interfaceIp;
	}

	public void setInterfaceIp(String interfaceIp)
	{
		this.interfaceIp = interfaceIp;
	}

	public String getInterfaceNet()
	{
		return interfaceNet;
	}

	public void setInterfaceNet(String interfaceNet)
	{
		this.interfaceNet = interfaceNet;
	}

	public void setDhcpNetmask(String dhcpNetmask)
	{
		this.dhcpNetmask = dhcpNetmask;
	}
	
	@Transient
	public String getStrDhcpMgt() {
		return MgrUtil.getEnumString("enum.interface.dhcp.server."
			+ dhcpMgt);
	}

	@Transient
	public String getInterfaceInfo() {
		if (!"".equals(interfaceIp)) {
			return interfaceIp + "/" + interfaceNet;
		}
		return "";
	}

	public boolean isEnableArp()
	{
		return enableArp;
	}

	public void setEnableArp(boolean enableArp)
	{
		this.enableArp = enableArp;
	}

	public String getWins1() {
		return wins1;
	}

	public void setWins1(String wins1) {
		this.wins1 = wins1;
	}

	public String getWins2() {
		return wins2;
	}

	public void setWins2(String wins2) {
		this.wins2 = wins2;
	}
	
	@Override
	public VlanDhcpServer clone() {
		try {
			return (VlanDhcpServer) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isNatSupport()
	{
		return natSupport;
	}

	public void setNatSupport(boolean natSupport)
	{
		this.natSupport = natSupport;
	}
	
	@Transient
	public String serverOptionDisplayStyle = "none";// by default
	@Transient
	public String customOptionDisplayStyle = "none";// by default
	@Transient
	public String advancedDisplayStyle = "none";// by default

	public String getServerOptionDisplayStyle() {
		return serverOptionDisplayStyle;
	}

	public void setServerOptionDisplayStyle(String serverOptionDisplayStyle) {
		this.serverOptionDisplayStyle = serverOptionDisplayStyle;
	}

	public String getCustomOptionDisplayStyle() {
		return customOptionDisplayStyle;
	}

	public void setCustomOptionDisplayStyle(String customOptionDisplayStyle) {
		this.customOptionDisplayStyle = customOptionDisplayStyle;
	}

	public String getAdvancedDisplayStyle() {
		return advancedDisplayStyle;
	}

	public void setAdvancedDisplayStyle(String advancedDisplayStyle) {
		this.advancedDisplayStyle = advancedDisplayStyle;
	}

}