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
@Table(name = "DEVICE_POLICY")
@org.hibernate.annotations.Table(appliesTo = "DEVICE_POLICY", indexes = {
		@Index(name = "DEVICE_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DevicePolicy implements HmBo{

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
	private String policyName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	private boolean enableOui = true;
	private boolean enableOs = true;
	private boolean enableDomain = true;
	private boolean enableSingleCheck;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DEVICE_POLICY_RULE", joinColumns = @JoinColumn(name = "DEVICE_POLICY_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DevicePolicyRule> rules = new ArrayList<DevicePolicyRule>();

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

	public boolean isEnableOui() {
		return enableOui;
	}

	public void setEnableOui(boolean enableOui) {
		this.enableOui = enableOui;
	}

	public boolean isEnableOs() {
		return enableOs;
	}

	public void setEnableOs(boolean enableOs) {
		this.enableOs = enableOs;
	}

	public boolean isEnableDomain() {
		return enableDomain;
	}

	public void setEnableDomain(boolean enableDomain) {
		this.enableDomain = enableDomain;
	}

	public boolean isEnableSingleCheck() {
		return enableSingleCheck;
	}

	public void setEnableSingleCheck(boolean enableSingleCheck) {
		this.enableSingleCheck = enableSingleCheck;
	}

	public List<DevicePolicyRule> getRules() {
		return rules;
	}

	public void setRules(List<DevicePolicyRule> rules) {
		this.rules = rules;
	}
	
	@Override
	public String getLabel() {
		return policyName;
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
	public DevicePolicy clone() {
		try {
			return (DevicePolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}