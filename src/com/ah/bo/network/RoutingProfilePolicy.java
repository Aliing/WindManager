package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "ROUTING_PROFILE_POLICY")
@org.hibernate.annotations.Table(appliesTo = "ROUTING_PROFILE_POLICY", indexes = {
		@Index(name = "ROUTING_PROFILE_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RoutingProfilePolicy implements HmBo {

	private static final long serialVersionUID = 1L;
	
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
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String profileName;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	@ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "POSITION")
	@CollectionTable(name = "ROUTING_PROFILE_POLICY_RULE", joinColumns = @JoinColumn(name = "ROUTING_PROFILE_POLICY_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RoutingProfilePolicyRule> routingProfilePolicyRuleList = new ArrayList<RoutingProfilePolicyRule>();
	
	private short profileType;
	
	public static final short POLICYRULE_SPLIT = 1;
	public static final short POLICYRULE_ALL = 2;
	public static final short POLICYRULE_CUSTOM = 3;
	
	public short getProfileType() {
		return profileType;
	}

	public void setProfileType(short profileType) {
		this.profileType = profileType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isSelected() {
		return selected;
	}

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

	public List<RoutingProfilePolicyRule> getRoutingProfilePolicyRuleList() {
		return routingProfilePolicyRuleList;
	}

	public void setRoutingProfilePolicyRuleList(
			List<RoutingProfilePolicyRule> routingProfilePolicyRuleList) {
		this.routingProfilePolicyRuleList = routingProfilePolicyRuleList;
	}

	@Override
	public RoutingProfilePolicy clone() {
		try {
			return (RoutingProfilePolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Set<String> getActiveWans() {
		Set<String> result = new HashSet<>();
		
		if (routingProfilePolicyRuleList != null) {
			for (RoutingProfilePolicyRule rule : routingProfilePolicyRuleList) {
				if (rule.getOut1() != null)
					result.add(rule.getOut1());
				
				if (rule.getOut2() != null)
					result.add(rule.getOut2());
			}
		}
		
		return result;
	}
}
