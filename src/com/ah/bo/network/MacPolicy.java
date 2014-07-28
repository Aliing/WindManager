/**
 *@filename		MacPolicy.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-27 AM 09:41:02
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.useraccess.UserProfile;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "MAC_POLICY")
@org.hibernate.annotations.Table(appliesTo = "MAC_POLICY", indexes = {
		@Index(name = "MAC_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MacPolicy implements HmBo {

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
	private String policyName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MAC_POLICY_RULE", joinColumns = @JoinColumn(name = "MAC_POLICY_ID"))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<MacPolicyRule> rules = new ArrayList<MacPolicyRule>();

	@OneToMany(mappedBy = "macPolicyFrom")
	private Set<UserProfile> userProfileFrom = new HashSet<UserProfile>();

	@OneToMany(mappedBy = "macPolicyTo")
	private Set<UserProfile> userProfileTo = new HashSet<UserProfile>();

	public Set<UserProfile> getUserProfileFrom() {
		return userProfileFrom;
	}

	public void setUserProfileFrom(Set<UserProfile> userProfileFrom) {
		this.userProfileFrom = userProfileFrom;
	}

	public Set<UserProfile> getUserProfileTo() {
		return userProfileTo;
	}

	public void setUserProfileTo(Set<UserProfile> userProfileTo) {
		this.userProfileTo = userProfileTo;
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
		return policyName;
	}

	public List<MacPolicyRule> getRules() {
		return rules;
	}

	public void setRules(List<MacPolicyRule> rules) {
		this.rules = rules;
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
	
	@Override
	public MacPolicy clone() {
		try {
			return (MacPolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}