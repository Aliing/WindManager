package com.ah.bo.port;

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
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.ServiceFilter;
import com.ah.bo.network.Vlan;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusAssignment;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "PORT_ACCESS_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "PORT_ACCESS_PROFILE", 
    indexes = { @Index(name = "PORT_ACCESS_PROFILE_OWNER", columnNames = { "OWNER" }) })
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PortAccessProfile implements HmBo {

    private static final long serialVersionUID = -3514093305958892646L;

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

    public static final short PORT_TYPE_NONE = -1;
    public static final short PORT_TYPE_PHONEDATA = 1;
    public static final short PORT_TYPE_AP = 2;
    public static final short PORT_TYPE_MONITOR = 3;
    public static final short PORT_TYPE_ACCESS = 4;
    public static final short PORT_TYPE_8021Q = 5;
    public static final short PORT_TYPE_WAN = 6;
    private short portType = PORT_TYPE_ACCESS;
    
    private String portDescription;
    
    private boolean shutDownPorts; 

    /**
     * field for filter out the access profile, because the behavior on switch is different from router. 
     */
    public static final short ACCESS_POINT = 1;
    public static final short BRANCH_ROUTER = 2;
    public static final short CHESAPEAKE = 3;
    private short product = CHESAPEAKE;
    
    //****************** Authentication Settings *********************
    private boolean enabled8021X;
    private boolean first8021X = true;
    private int interval8021X = 30;
    private boolean enabledSameVlan;
    
    //Enable IDM for per Port Type
    private boolean enabledIDM;
    
    private boolean enabledMAC;
    private int authProtocol = Cwp.AUTH_METHOD_PAP;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RADIUS_SERVICE_ASSIGN_ID")
    private RadiusAssignment radiusAssignment;
    
    private boolean enabledApAuth;
    
    private boolean enabledCWP;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CWP_ID")
    private Cwp cwp;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERPROFILE_DEF_ID")
    private UserProfile defUserProfile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERPROFILE_SELFREG_ID")
    private UserProfile selfRegUserProfile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERPROFILE_GUEST_ID")
    private UserProfile guestUserProfile; // AirWatch Non-Compliance
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VOICE_VLAN_ID")
    private Vlan voiceVlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DATA_VLAN_ID")
    private Vlan dataVlan;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ACCESS_AUTHOK_USERPROFILE", joinColumns = { @JoinColumn(name = "ACCESS_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserProfile> authOkUserProfile = new HashSet<UserProfile>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ACCESS_AUTHOK_DATA_USERPROFILE", joinColumns = { @JoinColumn(name = "ACCESS_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserProfile> authOkDataUserProfile = new HashSet<UserProfile>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ACCESS_AUTHFAIL_USERPROFILE", joinColumns = { @JoinColumn(name = "ACCESS_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_PROFILE_ID") })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserProfile> authFailUserProfile = new HashSet<UserProfile>();
    
    //****************** 802.1X user profiles reassignment based on authentication *********************
    public static final short DENY_ACTION_BAN = 1;
    public static final short DENY_ACTION_BAN_FOREVER = 2;
    public static final short DENY_ACTION_DISCONNECT = 3;
    private short denyAction=DENY_ACTION_DISCONNECT;
    
    public static final int DEFAULT_ACTION_TIME = 60;
    @Range(min=1, max=100000000)
    private long actionTime=DEFAULT_ACTION_TIME;
    
    private boolean chkUserOnly;
    private boolean chkDeauthenticate;
    private boolean enableOsDection;
    
    //****************** add radius user groups and radius attribute mapping *********************
    private boolean enableAssignUserProfile = false;
    private int assignUserProfileAttributeId;
    private int assignUserProfileVenderId;

    public static final short USERPROFILE_ATTRIBUTE_SPECIFIED = 1;
    public static final short USERPROFILE_ATTRIBUTE_CUSTOMER = 2;
    
    private short userProfileAttributeType = USERPROFILE_ATTRIBUTE_SPECIFIED;
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
    @JoinTable(name = "ACCESS_RADIUS_USER_GROUP", joinColumns = { @JoinColumn(name = "ACCESS_PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_GROUP_ID") })
    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private Set<LocalUserGroup> radiusUserGroups = new HashSet<LocalUserGroup>();
    
    //****************** QOS Settings *********************
	public static final short QOS_CLASSIFICATION_MODE_TRUSTED = 0;
	public static final short QOS_CLASSIFICATION_MODE_UNTRUSTED = 1;
	private short qosClassificationMode = QOS_CLASSIFICATION_MODE_UNTRUSTED;
	public static final short QOS_CLASSIFICATION_TRUST_DSCP = 0;
	public static final short QOS_CLASSIFICATION_TRUST_8021P = 1;
    private short qosClassificationTrustMode = QOS_CLASSIFICATION_TRUST_DSCP;
    private boolean enableTrustedProiority;
    private short trustedPriority = 2;
    private short untrustedPriority = 2;
    private boolean enableQosMark;
    private short qosMarkMode = QOS_CLASSIFICATION_TRUST_DSCP;
	
	private boolean enableEthLimitDownloadBandwidth = false;
	private boolean enableEthLimitUploadBandwidth = false;
	private short ethLimitDownloadRate=100;
	private short ethLimitUploadRate=100;
	private boolean enableUSBLimitDownloadBandwidth = false;
	private boolean enableUSBLimitUploadBandwidth = false;
	private short usbLimitDownloadRate=100;
	private short usbLimitUploadRate=100;
    
	//****************** MDM Settings *********************
    // for router access only
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CONFIGMDM_ID")
    private ConfigTemplateMdm configtempleMdm; 
    private boolean enableMDM =false;

	//****************** Optional Settings *********************
    private boolean enabledClientReport; // for chesapeake access only

	// for router access only
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICE_FILTER_ID")
    private ServiceFilter serviceFilter;
    // for router access only
    public static final int AUTH_SEQUENCE_MAC_LAN_CWP = 1;
    public static final int AUTH_SEQUENCE_MAC_CWP_LAN = 2;
    public static final int AUTH_SEQUENCE_LAN_MAC_CWP = 3;
    public static final int AUTH_SEQUENCE_LAN_CWP_MAC = 4;
    public static final int AUTH_SEQUENCE_CWP_MAC_LAN = 5;
    public static final int AUTH_SEQUENCE_CWP_LAN_MAC = 6;
    private int authSequence = AUTH_SEQUENCE_MAC_LAN_CWP;
    
    //****************** VLAN Settings *********************
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NATIVE_VLAN_ID")
    private Vlan nativeVlan;
    @Column(length = 19400)
    private String allowedVlan;

    /*----------Transient field--------------*/
    @Transient
    private boolean selected;
    @Transient
    private boolean enabledPrimaryAuth;
    @Transient
    private boolean enableSecondaryAuth;
    @Transient
    private int primaryAuth;
    
    @Transient
    public String getPortTypeName() {
        return PORT_TYPE_NONE == this.portType ? "N/A" : MgrUtil
                .getEnumString("enum.portConfig.port.type." + this.portType);
    }
    
    @Transient
    public String getProductTypeName(){
        return CHESAPEAKE == this.product ? MgrUtil
                .getEnumString("enum.portConfig.product.type." + CHESAPEAKE)
                : MgrUtil.getEnumString("enum.portConfig.product.type." + BRANCH_ROUTER);
    }
    
    @Transient
    public String getPortClassName() {
        String className = "basic-port";
        switch (this.portType) {
        case PORT_TYPE_PHONEDATA:
            className = "ip-port";
            break;
        case PORT_TYPE_AP:
            className = "ap-port";
            break;
        case PORT_TYPE_MONITOR:
            className = "monitor-port";
            break;
        case PORT_TYPE_ACCESS:
            className = "access-port";
            break;
        case PORT_TYPE_8021Q:
            className = "t8021q-port"; // avoid use number start
            break;
        case PORT_TYPE_WAN:
            className = "wan-port";
            break;
        default:
            break;
        }
        return className;
    }
    @Transient
    public String getNameSubstr() {
        if (name==null) {
            return "";
        }
        if (name.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
            return name.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
        }
        
        return name;
    }
    @Transient
    public String getAllowedVlanSubstr() {
        if (allowedVlan==null) {
            return "";
        }
        if (allowedVlan.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
            return allowedVlan.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
        }
        
        return allowedVlan;
    }
    @Transient
    public boolean isIDMAuthEnabled() {
        if(enabledIDM) {
            if(portType == PORT_TYPE_ACCESS) {
                if (this.enabledCWP) {
                    if(null == cwp || (null != cwp
                        && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                                || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH
                                /*|| cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA*/))) {
                        return true;
                    }
                }
                if (this.enabled8021X) {
                    return true;
                }
            }
        }
        return false;
    }
    @Transient
    public boolean isRadiusAuthEnable() {
        if(isIDMAuthEnabled()) {
            return false;
        }
        if (this.enabledCWP && null != cwp
                && (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL 
                        || cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
            return true;
        } else if (this.enabled8021X || this.enabledMAC) {
            return true;
        } else {
            return false;
        }
    }
    
    @Transient
    public void setChkUserOnlyDefaults() {
        this.chkUserOnly = false;
        this.chkDeauthenticate = false;
        this.denyAction = DENY_ACTION_DISCONNECT;
        this.actionTime = DEFAULT_ACTION_TIME;
    }
    
    @Transient
    public String getMonitorPortStyle(){
        return portType == PORT_TYPE_MONITOR ? "none" :"";
    }
    
    @Transient
    public String getQosSettingsContentStyle(){
    	return portType == PORT_TYPE_MONITOR ? "none" :"";
    }
    
    @Transient
    public boolean isExistsAuthMode(short deviceType){
    	//device type switch mode not support cwp.
    	return (portType == PortAccessProfile.PORT_TYPE_PHONEDATA || portType == PortAccessProfile.PORT_TYPE_ACCESS) 
    			&& (isEnabled8021X() || isEnabledMAC() || (deviceType != HiveAp.Device_TYPE_SWITCH && isEnabledCWP()) );
    }
    
    @Transient
    public String getShutDownPortsStyle(){
    	return shutDownPorts ? "none" :"";
    }
    
    @Transient
    public boolean isRenameCWPType() {
        if (this.enabledIDM
                && (portType == PORT_TYPE_ACCESS && !enabledMAC)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Transient
    public String getSuffixIDMType() {
        String typeName = "";
        if(this.enabledIDM) {
            typeName = "(User Name/Password)";
        }
        return typeName;
    }

    /*-----------Override Object methods--------------*/
    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder()
                .append(this.id, ((PortAccessProfile) obj).id)
                .append(this.name, ((PortAccessProfile) obj).name)
                .append(this.portType, ((PortAccessProfile) obj).portType).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.id).append(this.name).append(this.portType)
                .toHashCode();
    }

    @Override
    public PortAccessProfile clone() {
        try {
            return (PortAccessProfile) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder().append(
                "{id:" + this.id + ", name:" + this.name +", portType:"+ this.portType + "}").toString();
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

    // =======Getter&Setter==========//
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public short getPortType() {
        return portType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPortType(short portType) {
        this.portType = portType;
    }
    
    public boolean isEnabled8021X() {
        return enabled8021X;
    }
    public boolean isFirst8021X() {
        return first8021X;
    }
    public int getInterval8021X() {
        return interval8021X;
    }
    public boolean isEnabledMAC() {
        return enabledMAC;
    }
    public boolean isEnabledCWP() {
        return enabledCWP;
    }
    public boolean isEnabledApAuth() {
        return enabledApAuth;
    }
    public void setEnabled8021X(boolean enabled8021x) {
        enabled8021X = enabled8021x;
    }
    public void setFirst8021X(boolean first8021x) {
        first8021X = first8021x;
    }
    public void setInterval8021X(int interval8021x) {
        interval8021X = interval8021x;
    }
    public void setEnabledMAC(boolean enabledMAC) {
        this.enabledMAC = enabledMAC;
    }
    public void setEnabledCWP(boolean enabledCWP) {
        this.enabledCWP = enabledCWP;
    }
    public void setEnabledApAuth(boolean enabledApAuth) {
        this.enabledApAuth = enabledApAuth;
    }
    public boolean isEnableEthLimitDownloadBandwidth() {
		return enableEthLimitDownloadBandwidth;
	}

	public boolean isEnableEthLimitUploadBandwidth() {
		return enableEthLimitUploadBandwidth;
	}

	public short getEthLimitDownloadRate() {
		return ethLimitDownloadRate;
	}

	public short getEthLimitUploadRate() {
		return ethLimitUploadRate;
	}

	public boolean isEnableUSBLimitDownloadBandwidth() {
		return enableUSBLimitDownloadBandwidth;
	}

	public boolean isEnableUSBLimitUploadBandwidth() {
		return enableUSBLimitUploadBandwidth;
	}

	public short getUsbLimitDownloadRate() {
		return usbLimitDownloadRate;
	}

	public short getUsbLimitUploadRate() {
		return usbLimitUploadRate;
	}

	public void setEnableEthLimitDownloadBandwidth(
			boolean enableEthLimitDownloadBandwidth) {
		this.enableEthLimitDownloadBandwidth = enableEthLimitDownloadBandwidth;
	}

	public void setEnableEthLimitUploadBandwidth(
			boolean enableEthLimitUploadBandwidth) {
		this.enableEthLimitUploadBandwidth = enableEthLimitUploadBandwidth;
	}

	public void setEthLimitDownloadRate(short ethLimitDownloadRate) {
		this.ethLimitDownloadRate = ethLimitDownloadRate;
	}

	public void setEthLimitUploadRate(short ethLimitUploadRate) {
		this.ethLimitUploadRate = ethLimitUploadRate;
	}

	public void setEnableUSBLimitDownloadBandwidth(
			boolean enableUSBLimitDownloadBandwidth) {
		this.enableUSBLimitDownloadBandwidth = enableUSBLimitDownloadBandwidth;
	}

	public void setEnableUSBLimitUploadBandwidth(
			boolean enableUSBLimitUploadBandwidth) {
		this.enableUSBLimitUploadBandwidth = enableUSBLimitUploadBandwidth;
	}

	public void setUsbLimitDownloadRate(short usbLimitDownloadRate) {
		this.usbLimitDownloadRate = usbLimitDownloadRate;
	}

	public void setUsbLimitUploadRate(short usbLimitUploadRate) {
		this.usbLimitUploadRate = usbLimitUploadRate;
	}

	public short getUntrustedPriority() {
		return untrustedPriority;
	}
	public short getQosClassificationMode() {
		return qosClassificationMode;
	}
	public boolean isEnableTrustedProiority() {
		return enableTrustedProiority;
	}
	public short getTrustedPriority() {
		return trustedPriority;
	}
	public boolean isEnableQosMark() {
		return enableQosMark;
	}
	public short getQosMarkMode() {
		return qosMarkMode;
	}
	public void setQosClassificationMode(short qosClassificationMode) {
		this.qosClassificationMode = qosClassificationMode;
	}
	public void setEnableTrustedProiority(boolean enableTrustedProiority) {
		this.enableTrustedProiority = enableTrustedProiority;
	}
	public void setTrustedPriority(short trustedPriority) {
		this.trustedPriority = trustedPriority;
	}
	public void setEnableQosMark(boolean enableQosMark) {
		this.enableQosMark = enableQosMark;
	}
	public void setQosMarkMode(short qosMarkMode) {
		this.qosMarkMode = qosMarkMode;
	}
	public void setUntrustedPriority(short untrustedPriority) {
		this.untrustedPriority = untrustedPriority;
	}
	public short getQosClassificationTrustMode() {
		return qosClassificationTrustMode;
	}
	public void setQosClassificationTrustMode(short qosClassificationTrustMode) {
		this.qosClassificationTrustMode = qosClassificationTrustMode;
	}
    public boolean isEnabledSameVlan() {
        return enabledSameVlan;
    }
    public void setEnabledSameVlan(boolean enabledSameVlan) {
        this.enabledSameVlan = enabledSameVlan;
    }
    public int getAuthProtocol() {
        return authProtocol;
    }
    public RadiusAssignment getRadiusAssignment() {
        return radiusAssignment;
    }
    public Cwp getCwp() {
        return cwp;
    }
    public Vlan getNativeVlan() {
        return nativeVlan;
    }
    public String getAllowedVlan() {
        return allowedVlan;
    }
    public void setAuthProtocol(int authProtocol) {
        this.authProtocol = authProtocol;
    }
    public void setRadiusAssignment(RadiusAssignment radiusAssignment) {
        this.radiusAssignment = radiusAssignment;
    }
    public void setCwp(Cwp cwp) {
        this.cwp = cwp;
    }
    public void setNativeVlan(Vlan nativeVlan) {
        this.nativeVlan = nativeVlan;
    }
    public void setAllowedVlan(String allowedVlan) {
        this.allowedVlan = allowedVlan;
    }
    public UserProfile getDefUserProfile() {
        return defUserProfile;
    }
    public Set<UserProfile> getAuthOkUserProfile() {
        return authOkUserProfile;
    }
    public Set<UserProfile> getAuthFailUserProfile() {
        return authFailUserProfile;
    }
    public void setDefUserProfile(UserProfile defUserProfile) {
        this.defUserProfile = defUserProfile;
    }
    public void setAuthOkUserProfile(Set<UserProfile> authOkUserProfile) {
        this.authOkUserProfile = authOkUserProfile;
    }
    public void setAuthFailUserProfile(Set<UserProfile> authFailUserProfile) {
        this.authFailUserProfile = authFailUserProfile;
    }
    public UserProfile getSelfRegUserProfile() {
        return selfRegUserProfile;
    }
    public void setSelfRegUserProfile(UserProfile selfRegUserProfile) {
        this.selfRegUserProfile = selfRegUserProfile;
    }
    public short getDenyAction() {
        return denyAction;
    }
    public long getActionTime() {
        return actionTime;
    }
    public boolean isChkUserOnly() {
        return chkUserOnly;
    }
    public boolean isChkDeauthenticate() {
        return chkDeauthenticate;
    }
    public boolean isEnableOsDection() {
        return enableOsDection;
    }
    public boolean isEnableAssignUserProfile() {
        return enableAssignUserProfile;
    }
    public int getAssignUserProfileAttributeId() {
        return assignUserProfileAttributeId;
    }
    public int getAssignUserProfileVenderId() {
        return assignUserProfileVenderId;
    }
    public Set<LocalUserGroup> getRadiusUserGroups() {
        return radiusUserGroups;
    }
    public void setDenyAction(short denyAction) {
        this.denyAction = denyAction;
    }
    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }
    public void setChkUserOnly(boolean chkUserOnly) {
        this.chkUserOnly = chkUserOnly;
    }
    public void setChkDeauthenticate(boolean chkDeauthenticate) {
        this.chkDeauthenticate = chkDeauthenticate;
    }
    public void setEnableOsDection(boolean enableOsDection) {
        this.enableOsDection = enableOsDection;
    }
    public void setEnableAssignUserProfile(boolean enableAssignUserProfile) {
        this.enableAssignUserProfile = enableAssignUserProfile;
    }
    public void setAssignUserProfileAttributeId(int assignUserProfileAttributeId) {
        this.assignUserProfileAttributeId = assignUserProfileAttributeId;
    }
    public void setAssignUserProfileVenderId(int assignUserProfileVenderId) {
        this.assignUserProfileVenderId = assignUserProfileVenderId;
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
    public ServiceFilter getServiceFilter() {
        return serviceFilter;
    }
    public void setServiceFilter(ServiceFilter serviceFilter) {
        this.serviceFilter = serviceFilter;
    }
    public Set<UserProfile> getAuthOkDataUserProfile() {
        return authOkDataUserProfile;
    }
    public void setAuthOkDataUserProfile(Set<UserProfile> authOkDataUserProfile) {
        this.authOkDataUserProfile = authOkDataUserProfile;
    }
    public short getProduct() {
        return product;
    }
    public void setProduct(short product) {
        this.product = product;
    }
    public int getAuthSequence() {
        return authSequence;
    }
    public void setAuthSequence(int authSequence) {
        this.authSequence = authSequence;
    }
    
    public boolean isEnabledPrimaryAuth() {
        return enabledPrimaryAuth;
    }
    public boolean isEnableSecondaryAuth() {
        return enableSecondaryAuth;
    }
    public int getPrimaryAuth() {
        return primaryAuth;
    }
    public void setEnabledPrimaryAuth(boolean enabledPrimaryAuth) {
        this.enabledPrimaryAuth = enabledPrimaryAuth;
    }
    public void setEnableSecondaryAuth(boolean enableSecondaryAuth) {
        this.enableSecondaryAuth = enableSecondaryAuth;
    }
    public void setPrimaryAuth(int primaryAuth) {
        this.primaryAuth = primaryAuth;
    }
    public boolean isEnableMDM() {
        return enableMDM;
    }
    public void setEnableMDM(boolean enableMDM) {
        this.enableMDM = enableMDM;
    }
    public ConfigTemplateMdm getConfigtempleMdm() {
        return configtempleMdm;
    }
    public void setConfigtempleMdm(ConfigTemplateMdm configtempleMdm) {
        this.configtempleMdm = configtempleMdm;
    }
    public boolean isEnabledClientReport() {
        return enabledClientReport;
    }
    public void setEnabledClientReport(boolean enabledClientReport) {
        this.enabledClientReport = enabledClientReport;
    }
    public Vlan getVoiceVlan() {
        return voiceVlan;
    }
    public void setVoiceVlan(Vlan voiceVlan) {
        this.voiceVlan = voiceVlan;
    }
    public Vlan getDataVlan() {
        return dataVlan;
    }
    public void setDataVlan(Vlan dataVlan) {
        this.dataVlan = dataVlan;
    }

	public String getPortDescription() {
		return portDescription;
	}

	public void setPortDescription(String portDescription) {
		this.portDescription = portDescription;
	}

	public boolean isShutDownPorts() {
		return shutDownPorts;
	}

	public void setShutDownPorts(boolean shutDownPorts) {
		this.shutDownPorts = shutDownPorts;
	}

    public UserProfile getGuestUserProfile() {
        return guestUserProfile;
    }

    public void setGuestUserProfile(UserProfile guestUserProfile) {
        this.guestUserProfile = guestUserProfile;
    }

    public boolean isEnabledIDM() {
        return enabledIDM;
    }

    public void setEnabledIDM(boolean enabledIDM) {
        this.enabledIDM = enabledIDM;
    }
	
}
