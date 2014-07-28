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
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.useraccess.MgmtServiceIPTrack;

@Entity
@Table(name = "ROUTING_POLICY")
@org.hibernate.annotations.Table(appliesTo = "ROUTING_POLICY", indexes = {
		@Index(name = "ROUTING_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class RoutingPolicy implements HmBo {

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
	private String policyName;
	
	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	public static final short POLICYRULE_SPLIT = 1;
	public static final short POLICYRULE_ALL = 2;
	public static final short POLICYRULE_CUSTOM = 3;
	
	private short policyRuleType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IP_TRACK_ID")
	private MgmtServiceIPTrack ipTrackForCheck;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXCEPTIONLIST_ID")
	private DomainObject domainObjectForDesList;
	
	private boolean enableIpTrackForCheck;
	
	private boolean enableDomainObjectForDesList;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "ROUTING_POLICY_RULE", joinColumns = @JoinColumn(name = "ROUTING_POLICY_RULE_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<RoutingPolicyRule> routingPolicyRuleList = new ArrayList<RoutingPolicyRule>();
	
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
		return policyName;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public short getPolicyRuleType() {
		return policyRuleType;
	}

	public void setPolicyRuleType(short policyRuleType) {
		this.policyRuleType = policyRuleType;
	}

	public MgmtServiceIPTrack getIpTrackForCheck() {
		return ipTrackForCheck;
	}

	public void setIpTrackForCheck(MgmtServiceIPTrack ipTrackForCheck) {
		this.ipTrackForCheck = ipTrackForCheck;
	}

	public DomainObject getDomainObjectForDesList() {
		return domainObjectForDesList;
	}

	public void setDomainObjectForDesList(DomainObject domainObjectForDesList) {
		this.domainObjectForDesList = domainObjectForDesList;
	}

	public boolean isEnableIpTrackForCheck() {
		return enableIpTrackForCheck;
	}

	public void setEnableIpTrackForCheck(boolean enableIpTrackForCheck) {
		this.enableIpTrackForCheck = enableIpTrackForCheck;
	}

	public boolean isEnableDomainObjectForDesList() {
		return enableDomainObjectForDesList;
	}

	public void setEnableDomainObjectForDesList(boolean enableDomainObjectForDesList) {
		this.enableDomainObjectForDesList = enableDomainObjectForDesList;
	}

	public List<RoutingPolicyRule> getRoutingPolicyRuleList() {
		return routingPolicyRuleList;
	}

	public void setRoutingPolicyRuleList(
			List<RoutingPolicyRule> routingPolicyRuleList) {
		this.routingPolicyRuleList = routingPolicyRuleList;
	}
	
	@Override
	public RoutingPolicy clone() {
		try {
			return (RoutingPolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private String editDomType;
	public static final String EDIT_DOMAIN_OBJECT_CUSTOM = "custom";
	public static final String EDIT_DOMAIN_OBJECT_ALL = "all";

	public String getEditDomType() {
		return editDomType;
	}

	public void setEditDomType(String editDomType) {
		this.editDomType = editDomType;
	}
	
}
