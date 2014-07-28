package com.ah.bo.lan;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.DosPrevention;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.Scheduler;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "LAN_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "LAN_PROFILE", indexes = {
		@Index(name = "LAN_PROFILE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class LanProfile implements HmBo {

	private static final long serialVersionUID = 4652351534480672645L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;
	
	@Column(nullable = false)
	private String name;
	
	private String description;
	
	// Interfaces Mode
	@Embedded
	@AttributeOverrides({ 
		@AttributeOverride(column = @Column(name="ETH0_ON"), name = "eth0On"),
		@AttributeOverride(column = @Column(name="ETH1_ON"), name = "eth1On"),
		@AttributeOverride(column = @Column(name="ETH2_ON"), name = "eth2On"),
		@AttributeOverride(column = @Column(name="ETH3_ON"), name = "eth3On"),
		@AttributeOverride(column = @Column(name="ETH4_ON"), name = "eth4On")})
	private LanInterfacesMode lanInterfacesMode = new LanInterfacesMode();
	
	// Access Mode(enable for Trunk)
	private boolean enabled8021Q;
	
	////-------Access Security-------//
	// Enable 802.1X
	private boolean enabled8021X;
	
	// Enable Captive Web Portal
	private boolean cwpSelectEnabled;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERPROFILE_DEFAULT_ID")
	private UserProfile userProfileDefault;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CWP_ID")
	private Cwp cwp;
	
	// Enable MAC Authentication
	private boolean macAuthEnabled;
	
	private int authProtocol = Cwp.AUTH_METHOD_PAP;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RADIUS_SERVICE_ASSIGN_ID")
	private RadiusAssignment radiusAssignment;
	////-------Access Security--------//
	
	////-------DoS Prevention and Filters--------//
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_DOS_ID")
	private DosPrevention ipDos;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICE_FILTER_ID")
	private ServiceFilter serviceFilter;
	////-------DoS Prevention and Filters--------//
	
	////-------Advanced--------//
	// Configuration Settings
	public static final int AUTH_SEQUENCE_MAC_LAN_CWP = 1;
	public static final int AUTH_SEQUENCE_MAC_CWP_LAN = 2;
	public static final int AUTH_SEQUENCE_LAN_MAC_CWP = 3;
	public static final int AUTH_SEQUENCE_LAN_CWP_MAC = 4;
	public static final int AUTH_SEQUENCE_CWP_MAC_LAN = 5;
	public static final int AUTH_SEQUENCE_CWP_LAN_MAC = 6;
	public static EnumItem[] ENUM_AUTH_SEQUENCE = MgrUtil.enumItems("enum.lan.auth.sequence.",
			new int[] { AUTH_SEQUENCE_MAC_LAN_CWP, AUTH_SEQUENCE_MAC_CWP_LAN,
					AUTH_SEQUENCE_LAN_MAC_CWP, AUTH_SEQUENCE_LAN_CWP_MAC,
					AUTH_SEQUENCE_CWP_MAC_LAN, AUTH_SEQUENCE_CWP_LAN_MAC });
	
	private int authSequence = AUTH_SEQUENCE_MAC_LAN_CWP;
	////-------Advanced--------//
	
	// Scheduler
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "LAN_PROFILE_SCHEDULER", joinColumns = { @JoinColumn(name = "LAN_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEDULER_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<Scheduler> schedulers = new HashSet<Scheduler>();

	////-------Networks--------//
	// Native(Untagged)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NATIVE_NETWORK_ID")
	private VpnNetwork nativeNetwork;
	// Regular(Tagged)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "LAN_PROFILE_REGULAR_NETWORKS", joinColumns = { @JoinColumn(name = "LAN_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "NETWORKS_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<VpnNetwork> regularNetworks = new HashSet<VpnNetwork>();
	////-------Networks--------//
	
	/// ------- VLANS ---------//
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NATIVE_VLAN_ID")
	private Vlan nativeVlan;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "LAN_PROFILE_REGULAR_VLAN", joinColumns = { @JoinColumn(name = "LAN_PROFILE_ID")} , inverseJoinColumns = { @JoinColumn(name = "VLAN_ID")})
	private Set<Vlan> regularVlans = new HashSet<Vlan>();
	/// ------- VLANS ---------//
	
	/**
	 * This set of local user groups is used for RADIUS attribute mapping.
	 * When RADIUS attribute mapping is enabled in LAN, local user groups have to be configured.
	 * 
	 * This is added in Dakar release.
	 * 
	 * Jianliang Chen
	 * 2012-03-30
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "LAN_RADIUS_USER_GROUP", joinColumns = { @JoinColumn(name = "LAN_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_GROUP_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<LocalUserGroup> radiusUserGroups = new HashSet<LocalUserGroup>();
	
	// Transient field
	@Transient
	private boolean selected;
	
	@Transient
	public String getAccessModeValue() {
		if(this.enabled8021Q) {
			return "Open";
		} else {
		    if(this.enabled8021X) {
		        return "802.1X";
		    }
			return "Open";
		}
	}
	@Transient
    public boolean isRadiusAuthEnable() {
        if (this.cwpSelectEnabled && null != cwp
                && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL 
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
            return true;
        } else if (!this.enabled8021Q && this.enabled8021X) {
            return true;
        } else {
            return this.macAuthEnabled;
        }
    }
	
	/*-----------Override Object methods--------------*/
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.id, ((LanProfile) obj).id)
				.append(this.name, ((LanProfile) obj).name).isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).append(this.name).toHashCode();
	}
	@Override
	public LanProfile clone() {
		try {
			return (LanProfile) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	@Override
	public String toString() {
		return new StringBuilder().append(
				"{id:" + this.id + ", name:" + this.name + " ports: " + this.lanInterfacesMode
						+ "}").toString();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return this.name;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	//=======Getter&Setter==========//
	public String getName() {
		return name;
	}
	
	public String getNameSubstr() {
		if (name==null) {
			return "";
		}
		if (name.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return name.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LanInterfacesMode getLanInterfacesMode() {
		return lanInterfacesMode;
	}

	public void setLanInterfacesMode(LanInterfacesMode lanInterfacesMode) {
		this.lanInterfacesMode = lanInterfacesMode;
	}

	public boolean isCwpSelectEnabled() {
		return cwpSelectEnabled;
	}

	public void setCwpSelectEnabled(boolean cwpSelectEnabled) {
		this.cwpSelectEnabled = cwpSelectEnabled;
	}

	public UserProfile getUserProfileDefault() {
		return userProfileDefault;
	}

	public void setUserProfileDefault(UserProfile userProfileDefault) {
		this.userProfileDefault = userProfileDefault;
	}

	public Cwp getCwp() {
		return cwp;
	}

	public void setCwp(Cwp cwp) {
		this.cwp = cwp;
	}

	public boolean isMacAuthEnabled() {
		return macAuthEnabled;
	}

	public void setMacAuthEnabled(boolean macAuthEnabled) {
		this.macAuthEnabled = macAuthEnabled;
	}

	public DosPrevention getIpDos() {
		return ipDos;
	}

	public void setIpDos(DosPrevention ipDos) {
		this.ipDos = ipDos;
	}

	public ServiceFilter getServiceFilter() {
		return serviceFilter;
	}

	public void setServiceFilter(ServiceFilter serviceFilter) {
		this.serviceFilter = serviceFilter;
	}

	public int getAuthSequence() {
		return authSequence;
	}

	public void setAuthSequence(int authSequence) {
		this.authSequence = authSequence;
	}

	public Set<Scheduler> getSchedulers() {
		return schedulers;
	}

	public void setSchedulers(Set<Scheduler> schedulers) {
		this.schedulers = schedulers;
	}

	public RadiusAssignment getRadiusAssignment() {
		return radiusAssignment;
	}

	public void setRadiusAssignment(RadiusAssignment radiusAssignment) {
		this.radiusAssignment = radiusAssignment;
	}
	
	public boolean isEnabled8021Q() {
		return enabled8021Q;
	}
	public void setEnabled8021Q(boolean enabled8021Q) {
		this.enabled8021Q = enabled8021Q;
	}

	public VpnNetwork getNativeNetwork() {
		return nativeNetwork;
	}

	public void setNativeNetwork(VpnNetwork nativeNetwork) {
		this.nativeNetwork = nativeNetwork;
	}

	public Set<VpnNetwork> getRegularNetworks() {
		return regularNetworks;
	}

	public void setRegularNetworks(Set<VpnNetwork> regularNetworks) {
		this.regularNetworks = regularNetworks;
	}
	public int getAuthProtocol() {
		return authProtocol;
	}
	public void setAuthProtocol(int authProtocol) {
		this.authProtocol = authProtocol;
	}
	public boolean isEnabled8021X() {
        return enabled8021X;
    }
    public void setEnabled8021X(boolean enabled8021x) {
        enabled8021X = enabled8021x;
    }
    
//    @Transient
 // TODO for remove network object in user profile
//	public Set<VpnNetwork> getVpnNetworks() {
//		Set<VpnNetwork> resultSet = new HashSet<VpnNetwork>();
//		if (enabled8021Q) {
//			resultSet.add(nativeNetwork);
//			resultSet.addAll(regularNetworks);
//		} else {
//			resultSet.add(userProfileDefault.getNetworkObj());
//		}
//		return resultSet;
//	}
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERPROFILE_SELFREG_ID")
	private UserProfile userProfileSelfReg;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "LAN_PROFILE_USER_PROFILE", joinColumns = { @JoinColumn(name = "LAN_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<UserProfile> radiusUserProfile = new HashSet<UserProfile>();
	
	public static final short		DENY_ACTION_BAN				= 1;
	public static final short		DENY_ACTION_BAN_FOREVER		= 2;
	public static final short		DENY_ACTION_DISCONNECT		= 3;
	public static EnumItem[] DENY_ACTION = MgrUtil.enumItems(
			"enum.denyAction.", new int[] { DENY_ACTION_BAN, DENY_ACTION_BAN_FOREVER,
					DENY_ACTION_DISCONNECT });

	private short denyAction=DENY_ACTION_DISCONNECT;
	
	public static final int DEFAULT_ACTION_TIME = 60;
	@Range(min=1, max=100000000)
	private long actionTime=DEFAULT_ACTION_TIME;
	
	private boolean chkUserOnly;
	
	private boolean chkDeauthenticate;
	
	private boolean enableOsDection;

	public UserProfile getUserProfileSelfReg() {
		return userProfileSelfReg;
	}
	public void setUserProfileSelfReg(UserProfile userProfileSelfReg) {
		this.userProfileSelfReg = userProfileSelfReg;
	}
	public Set<UserProfile> getRadiusUserProfile() {
		return radiusUserProfile;
	}
	public void setRadiusUserProfile(Set<UserProfile> radiusUserProfile) {
		this.radiusUserProfile = radiusUserProfile;
	}
	public short getDenyAction() {
		return denyAction;
	}
	public void setDenyAction(short denyAction) {
		this.denyAction = denyAction;
	}
	public long getActionTime() {
		return actionTime;
	}
	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}
	public boolean isChkUserOnly() {
		return chkUserOnly;
	}
	public void setChkUserOnly(boolean chkUserOnly) {
		this.chkUserOnly = chkUserOnly;
	}
	public boolean isChkDeauthenticate() {
		return chkDeauthenticate;
	}
	public void setChkDeauthenticate(boolean chkDeauthenticate) {
		this.chkDeauthenticate = chkDeauthenticate;
	}

	@Transient
	public void setChkUserOnlyDefaults() {
		this.chkUserOnly = false;
		this.chkDeauthenticate = false;
		this.denyAction = DENY_ACTION_DISCONNECT;
		this.actionTime = DEFAULT_ACTION_TIME;
	}
	
	private boolean enableAssignUserProfile = false;
	
	private int assignUserProfileAttributeId;
	
	private int assignUserProfileVenderId;
	
	public static final short USERPROFILE_ATTRIBUTE_SPECIFIED = 1;
	
	public static final short USERPROFILE_ATTRIBUTE_CUSTOMER = 2;
	
	private short userProfileAttributeType = USERPROFILE_ATTRIBUTE_SPECIFIED;

	public boolean isEnableAssignUserProfile() {
		return enableAssignUserProfile;
	}

	public void setEnableAssignUserProfile(boolean enableAssignUserProfile) {
		this.enableAssignUserProfile = enableAssignUserProfile;
	}

	public int getAssignUserProfileAttributeId() {
		return assignUserProfileAttributeId;
	}

	public void setAssignUserProfileAttributeId(int assignUserProfileAttributeId) {
		this.assignUserProfileAttributeId = assignUserProfileAttributeId;
	}

	public int getAssignUserProfileVenderId() {
		return assignUserProfileVenderId;
	}

	public void setAssignUserProfileVenderId(int assignUserProfileVenderId) {
		this.assignUserProfileVenderId = assignUserProfileVenderId;
	}
	
	public Set<LocalUserGroup> getRadiusUserGroups() {
		return this.radiusUserGroups;
	}

	public void setRadiusUserGroups(Set<LocalUserGroup> radiusUserGroups) {
		this.radiusUserGroups = radiusUserGroups;
	}
	public short getUserProfileAttributeType() {
		return userProfileAttributeType;
	}
	public void setUserProfileAttributeType(short userProfileAttributeType) {
		this.userProfileAttributeType = userProfileAttributeType;
	}
    public Vlan getNativeVlan() {
        return nativeVlan;
    }
    public void setNativeVlan(Vlan nativeVlan) {
        this.nativeVlan = nativeVlan;
    }
    public Set<Vlan> getRegularVlans() {
        return regularVlans;
    }
    public void setRegularVlans(Set<Vlan> regularVlans) {
        this.regularVlans = regularVlans;
    }
	public boolean isEnableOsDection() {
		return enableOsDection;
	}
	public void setEnableOsDection(boolean enableOsDection) {
		this.enableOsDection = enableOsDection;
	}
}