package com.ah.bo.mobility;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.UserProfile;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "TUNNEL_SETTING")
@org.hibernate.annotations.Table(appliesTo = "TUNNEL_SETTING", indexes = {
		@Index(name = "TUNNEL_SETTING_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class TunnelSetting implements HmBo {

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
	private String tunnelName;

	@Range(min = 10, max = 600)
	private int unroamingInterval = 60;

	@Range(min = 0, max = 2147483647)
	private int unroamingAgeout = 0;

	public static final short TUNNELSETTING_DYNAMIC_TUNNELING = 1;

	// removed from 3.2r1
	public static final short TUNNELSETTING_TUNNELING_TO = 2;

	// removed from 3.2r1
	public static final short TUNNELSETTING_TUNNELING_FROM = 3;

	// added from 3.2r1
	public static final short TUNNELSETTING_STATIC_TUNNELING = 2;

	private int enableType = TUNNELSETTING_DYNAMIC_TUNNELING;

	// remove this field from 3.4r1
	//private boolean roamingEnable;

	public static final short TUNNELSETTING_TUNNELTYPE_IPADDRESS = 1;

	public static final short TUNNELSETTING_TUNNELTYPE_RANGEIP = 2;

	private int tunnelToType = TUNNELSETTING_TUNNELTYPE_IPADDRESS;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IP_ADDRESS_ID")
	private IpAddress ipAddress;

	private String ipRangeStart;

	private String ipRangeEnd;

	private String password;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "TUNNEL_SETTING_IP_ADDRESS", joinColumns = { @JoinColumn(name = "TUNNEL_SETTING_ID") }, inverseJoinColumns = { @JoinColumn(name = "IP_ADDRESS_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<IpAddress> ipAddressList = new HashSet<IpAddress>();

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@OneToMany(mappedBy = "tunnelSetting")
	private Set<UserProfile> userProfile = new HashSet<UserProfile>();

	public Set<UserProfile> getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(Set<UserProfile> userProfile) {
		this.userProfile = userProfile;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return this.tunnelName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getTunnelName() {
		return tunnelName;
	}

	public void setTunnelName(String tunnelName) {
		this.tunnelName = tunnelName;
	}

	public int getEnableType() {
		return enableType;
	}

	public void setEnableType(int enableType) {
		this.enableType = enableType;
	}

//	public boolean isRoamingEnable() {
//		return roamingEnable;
//	}
//
//	public boolean getRoamingEnable() {
//		return roamingEnable;
//	}
//
//	public void setRoamingEnable(boolean roamingEnable) {
//		this.roamingEnable = roamingEnable;
//	}

	public int getTunnelToType() {
		return tunnelToType;
	}

	public void setTunnelToType(int tunnelToType) {
		this.tunnelToType = tunnelToType;
	}

	public IpAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(IpAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getIpRangeStart() {
		return ipRangeStart;
	}

	public void setIpRangeStart(String ipRangeStart) {
		this.ipRangeStart = ipRangeStart;
	}

	public String getIpRangeEnd() {
		return ipRangeEnd;
	}

	public void setIpRangeEnd(String ipRangeEnd) {
		this.ipRangeEnd = ipRangeEnd;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUnroamingInterval() {
		return unroamingInterval;
	}

	public void setUnroamingInterval(int unroamingInterval) {
		this.unroamingInterval = unroamingInterval;
	}

	public int getUnroamingAgeout() {
		return unroamingAgeout;
	}

	public void setUnroamingAgeout(int unroamingAgeout) {
		this.unroamingAgeout = unroamingAgeout;
	}

	@Transient
	public String getEnableTypeString() {
		return getEnableTypeString(enableType);
	}

	public Set<IpAddress> getIpAddressList()
	{
		return ipAddressList;
	}

	public void setIpAddressList(Set<IpAddress> ipAddressList)
	{
		this.ipAddressList = ipAddressList;
	}

	@Transient
	public static String getEnableTypeString(int enableType) {
		return MgrUtil.getEnumString("enum.tunnel.enableType." + enableType);
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
	public TunnelSetting clone() {
		try {
			return (TunnelSetting) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private String ipInputValue;

	public String getIpInputValue()
	{
		return ipInputValue;
	}

	public void setIpInputValue(String ipInputValue)
	{
		this.ipInputValue = ipInputValue;
	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	@Transient
	private String contentShowType = "subdrawer";
	
	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}

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

}