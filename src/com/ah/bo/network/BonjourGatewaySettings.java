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

@Entity
@Table(name = "BONJOUR_GATEWAY_SETTINGS")
@org.hibernate.annotations.Table(appliesTo = "BONJOUR_GATEWAY_SETTINGS", indexes = {
		@Index(name = "BONJOUR_GATEWAY_SETTINGS_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class BonjourGatewaySettings implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String bonjourGwName;
	
	private String description;
	
	private String vlans="1-4094";
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "BONJOUR_ACTIVE_SERVICE", joinColumns = @JoinColumn(name = "BONJOUR_GATEWAY_SETTINGS_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<BonjourActiveService> bonjourActiveServices = new ArrayList<BonjourActiveService>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "BONJOUR_FILTER_RULE", joinColumns = @JoinColumn(name = "BONJOUR_FILTER_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<BonjourFilterRule> rules = new ArrayList<BonjourFilterRule>();
	
	@Transient
	private BonjourServiceTreeNode bonjourServiceTreeNode =new BonjourServiceTreeNode();
	
	public BonjourServiceTreeNode getBonjourServiceTreeNode() {
		return bonjourServiceTreeNode;
	}

	public void setBonjourServiceTreeNode(
			BonjourServiceTreeNode bonjourServiceTreeNode) {
		this.bonjourServiceTreeNode = bonjourServiceTreeNode;
	}

	public List<BonjourActiveService> getBonjourActiveServices() {
		return bonjourActiveServices;
	}
	
	@Transient
	private String editInfo;
	@Transient
	private String[] metrics;
	@Transient
	private String[] fromVlanGroups;
	@Transient
	private String[] toVlanGroups;
	@Transient
	private String[] ruleIds;
	@Transient
	private String[] serviceNames;
	@Transient
	private String[] serviceTypes;
	@Transient
	private String[] realms;
		
	public String[] getMetrics() {
		return metrics;
	}
	public void setMetrics(String[] metrics) {
		this.metrics = metrics;
	}

	public String[] getFromVlanGroups() {
		return fromVlanGroups;
	}

	public void setFromVlanGroups(String[] fromVlanGroups) {
		this.fromVlanGroups = fromVlanGroups;
	}

	public String getEditInfo() {
		return editInfo;
	}

	public void setEditInfo(String editInfo) {
		this.editInfo = editInfo;
	}
	
	public String[] getToVlanGroups() {
		return toVlanGroups;
	}

	public String[] getRuleIds() {
		return ruleIds;
	}

	public String[] getServiceNames() {
		return serviceNames;
	}

	public String[] getServiceTypes() {
		return serviceTypes;
	}

	public String[] getRealms() {
		return realms;
	}

	public void setToVlanGroups(String[] toVlanGroups) {
		this.toVlanGroups = toVlanGroups;
	}

	public void setRuleIds(String[] ruleIds) {
		this.ruleIds = ruleIds;
	}

	public void setServiceNames(String[] serviceNames) {
		this.serviceNames = serviceNames;
	}

	public void setServiceTypes(String[] serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public void setRealms(String[] realms) {
		this.realms = realms;
	}

	@Transient
	private BonjourFilterRule singleRule;
	
	public BonjourFilterRule getSingleRule()
	{
		return singleRule;
	}

	public void setSingleRule(BonjourFilterRule singleRule)
	{
		this.singleRule = singleRule;
	}
	
	@Transient
	private String selectedServiceIDs;

	public String getSelectedServiceIDs() {
		return selectedServiceIDs;
	}

	public void setSelectedServiceIDs(String selectedServiceIDs) {
		this.selectedServiceIDs = selectedServiceIDs;
	}

	public void setBonjourActiveServices(
			List<BonjourActiveService> bonjourActiveServices) {
		this.bonjourActiveServices = bonjourActiveServices;
	}

	public List<BonjourFilterRule> getRules() {
		return rules;
	}

	public void setRules(List<BonjourFilterRule> rules) {
		this.rules = rules;
	}

	private boolean defaultFlag;
	
	public boolean isDefaultFlag()
	{
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag)
	{
		this.defaultFlag = defaultFlag;
	}

	public String getOsName() {
		return bonjourGwName;
	}

	public void setOsName(String osName) {
		this.bonjourGwName = osName;
	}
	
	public String getBonjourGwName() {
		return bonjourGwName;
	}

	public void setBonjourGwName(String bonjourGwName) {
		this.bonjourGwName = bonjourGwName;
	}
	
	public String getDescription() {
		return description;
	}

	public String getVlans() {
		return vlans;
	}

	public void setVlans(String vlans) {
		this.vlans = vlans;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object osObject) {
		if (!(osObject instanceof BonjourGatewaySettings)) {
			return false;
		}
		return null == id ? super.equals(osObject) : id.equals(((BonjourGatewaySettings) osObject).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
	public String getLabel() {
		return bonjourGwName;
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
	public BonjourGatewaySettings clone() {
		try {
			return (BonjourGatewaySettings) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}