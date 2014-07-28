package com.ah.bo.network;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "AIR_SCREEN_RULE_GROUP")
@org.hibernate.annotations.Table(appliesTo = "AIR_SCREEN_RULE_GROUP", indexes = {
		@Index(name = "AIR_SCREEN_RULE_GROUP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AirScreenRuleGroup implements HmBo {

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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "AIR_SCREEN_GROUP_RULE", joinColumns = { @JoinColumn(name = "GROUP_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<AirScreenRule> rules = new HashSet<AirScreenRule>();

	@Override
	public Long getId() {
		return id;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return profileName;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public Set<AirScreenRule> getRules() {
		return rules;
	}

	public void setRules(Set<AirScreenRule> rules) {
		this.rules = rules;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public AirScreenRuleGroup clone() {
		try {
			return (AirScreenRuleGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}