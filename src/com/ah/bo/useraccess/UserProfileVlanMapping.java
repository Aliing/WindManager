package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryCertainBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Vlan;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.wlan.SsidProfile;

@Entity
@Table(name = "USER_PROFILE_VLAN_MAPPING")
@org.hibernate.annotations.Table(appliesTo = "USER_PROFILE_VLAN_MAPPING", indexes = {
		@Index(name = "USER_PROFILE_VLAN_MAPPING_OWNER", columnNames = { "OWNER" }),
		@Index(name = "USER_PROFILE_VLAN_MAPPING_UP_VLAN", columnNames = { "USERPROFILE_ID", "VLAN_ID" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class UserProfileVlanMapping implements HmBo {
	private static final long serialVersionUID = 1L;

	public UserProfileVlanMapping() {
	}
	public UserProfileVlanMapping(UserProfile userProfile, Vlan vlan, ConfigTemplate networkPolicy, HmDomain owner) {
		this.userProfile = userProfile;
		this.vlan = vlan;
		this.owner = owner;
		this.networkPolicy = networkPolicy;
	}
	
	public UserProfileVlanMapping assignMappingToSsid(SsidProfile ssidProfile) {
		this.setSsidProfile(ssidProfile);
		return this;
	}
	
	public UserProfileVlanMapping assignMappingToPortAccess(PortAccessProfile portAccessProfile) {
		this.setPortAccessProfile(portAccessProfile);
		return this;
	}
	
	public UserProfileVlanMapping assignMappingToProfile(HmBo profile) {
		if (profile instanceof SsidProfile) {
			return this.assignMappingToSsid((SsidProfile)profile);
		} else if (profile instanceof PortAccessProfile) {
			return this.assignMappingToPortAccess((PortAccessProfile)profile);
		}
		return this;
	}
	
	public static final String MAPPING_TYPE_ALL = "all";
	public static final String MAPPING_TYPE_SSID = "ssid";
	public static final String MAPPING_TYPE_PORT_ACCESS = "port-access";
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;
	
	@Transient
	private boolean selected;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERPROFILE_ID")
	private UserProfile userProfile;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VLAN_ID")
	private Vlan vlan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "config_template_id")
	private ConfigTemplate networkPolicy;
	
	@Transient
	private SsidProfile ssidProfile;
	
	@Transient
	private PortAccessProfile portAccessProfile;
	
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return this.getUserProfile().getUserProfileName() + "( " + this.getVlan().getVlanName() + " )";
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public UserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile) {
		this.userProfile = userProfile;
	}

	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}
	public ConfigTemplate getNetworkPolicy() {
		return networkPolicy;
	}
	public void setNetworkPolicy(ConfigTemplate networkPolicy) {
		this.networkPolicy = networkPolicy;
	}
	
	public SsidProfile getSsidProfile() {
		return ssidProfile;
	}
	public void setSsidProfile(SsidProfile ssidProfile) {
		this.ssidProfile = ssidProfile;
		this.checkMappingType(MAPPING_TYPE_SSID);
	}
	
	public PortAccessProfile getPortAccessProfile() {
		return portAccessProfile;
	}
	public void setPortAccessProfile(PortAccessProfile portAccessProfile) {
		this.portAccessProfile = portAccessProfile;
		this.checkMappingType(MAPPING_TYPE_PORT_ACCESS);
	}
	
	private void checkMappingType(String type) {
		switch(type) {
			case MAPPING_TYPE_SSID:
				if (this.ssidProfile != null) {
					this.portAccessProfile = null;
				}
				break;
			case MAPPING_TYPE_PORT_ACCESS:
				if (this.portAccessProfile != null) {
					this.ssidProfile = null;
				}
			default:
				break;
		}
	}
	
	@Override
	public String toString() {
		return "upId: " + this.getUserProfile().getId() + "(" + this.getUserProfile().getUserProfileName() + ")"
				+ ", vlanId: " + this.getVlan().getId() + "(" + this.getVlan().getVlanName() + ")"
				+ ", owner: " + this.getOwner().getId();
	}
	
	public static final QueryCertainBo<UserProfileVlanMapping> FULL_BO_QUERY =
			new QueryCertainBo<UserProfileVlanMapping>() {
				@Override
				public Collection<HmBo> loadBo(UserProfileVlanMapping upMapping) {
					if (upMapping.getUserProfile() != null) {
						upMapping.getUserProfile().getId();
						if (upMapping.getUserProfile().getAssignRules() != null) {
							upMapping.getUserProfile().getAssignRules().size();
						}
					}
					if (upMapping.getVlan() != null) {
						upMapping.getVlan().getId();
					}
					if (upMapping.getOwner() != null) {
						upMapping.getOwner().getId();
					}
					return null;
				}
			};
	
	public static final HmBo getRelativeProfile(String type, Long id) {
		return getRelativeProfile(type, id, null);
	}
	public static final HmBo getRelativeProfile(String type, Long id, QueryBo queryBo) {
		switch(type) {
			case MAPPING_TYPE_SSID:
				if (queryBo != null) {
					return QueryUtil.findBoById(SsidProfile.class, id, queryBo);
				} else {
					return QueryUtil.findBoById(SsidProfile.class, id);
				}
			case MAPPING_TYPE_PORT_ACCESS:
				if (queryBo != null) {
					return QueryUtil.findBoById(PortAccessProfile.class, id, queryBo);
				} else {
					return QueryUtil.findBoById(PortAccessProfile.class, id);
				}
			default:
				return null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.getVlan().getId(), ((UserProfileVlanMapping) obj).getVlan().getId())
				.append(this.getUserProfile().getId(), ((UserProfileVlanMapping) obj).getUserProfile().getId()).isEquals();
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getVlan().getId()).append(this.getUserProfile().getId()).toHashCode();
	}
}
