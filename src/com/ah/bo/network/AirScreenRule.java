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
@Table(name = "AIR_SCREEN_RULE")
@org.hibernate.annotations.Table(appliesTo = "AIR_SCREEN_RULE", indexes = {
		@Index(name = "AIR_SCREEN_RULE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AirScreenRule implements HmBo {

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOURCE", nullable = true)
	private AirScreenSource source;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "AIR_SCREEN_RULE_BEHAVIOR", joinColumns = { @JoinColumn(name = "RULE_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<AirScreenBehavior> behaviors = new HashSet<AirScreenBehavior>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "AIR_SCREEN_RULE_ACTION", joinColumns = { @JoinColumn(name = "RULE_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<AirScreenAction> actions = new HashSet<AirScreenAction>();

	@Column(length = 64)
	private String comment;

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

	public AirScreenSource getSource() {
		return source;
	}

	public void setSource(AirScreenSource source) {
		this.source = source;
	}

	public Set<AirScreenBehavior> getBehaviors() {
		return behaviors;
	}

	public void setBehaviors(Set<AirScreenBehavior> behaviors) {
		this.behaviors = behaviors;
	}

	public Set<AirScreenAction> getActions() {
		return actions;
	}

	public void setActions(Set<AirScreenAction> actions) {
		this.actions = actions;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	public String getValue() {
		return profileName;
	}

	@Transient
	private AirScreenSource tempSource = new AirScreenSource();
	@Transient
	private AirScreenBehavior tempBehavior = new AirScreenBehavior();
	@Transient
	private AirScreenAction tempAction = new AirScreenAction();

	public AirScreenSource getTempSource() {
		return tempSource;
	}

	public void setTempSource(AirScreenSource tempSource) {
		this.tempSource = tempSource;
	}

	public AirScreenBehavior getTempBehavior() {
		return tempBehavior;
	}

	public void setTempBehavior(AirScreenBehavior tempBehavior) {
		this.tempBehavior = tempBehavior;
	}

	public AirScreenAction getTempAction() {
		return tempAction;
	}

	public void setTempAction(AirScreenAction tempAction) {
		this.tempAction = tempAction;
	}

	@Transient
	public String sourceCreationDisplayStyle = "none";// by default
	@Transient
	public String behaviorCreationDisplayStyle = "none";
	@Transient
	public String actionCreationDisplayStyle = "none";

	public String getSourceCreationDisplayStyle() {
		return sourceCreationDisplayStyle;
	}

	public void setSourceCreationDisplayStyle(String sourceCreationDisplayStyle) {
		this.sourceCreationDisplayStyle = sourceCreationDisplayStyle;
	}

	public String getBehaviorCreationDisplayStyle() {
		return behaviorCreationDisplayStyle;
	}

	public void setBehaviorCreationDisplayStyle(
			String behaviorCreationDisplayStyle) {
		this.behaviorCreationDisplayStyle = behaviorCreationDisplayStyle;
	}

	public String getActionCreationDisplayStyle() {
		return actionCreationDisplayStyle;
	}

	public void setActionCreationDisplayStyle(String actionCreationDisplayStyle) {
		this.actionCreationDisplayStyle = actionCreationDisplayStyle;
	}

	@Override
	public AirScreenRule clone() {
		try {
			return (AirScreenRule) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private String inputOuiText;

	public String getInputOuiText() {
		return inputOuiText;
	}

	public void setInputOuiText(String inputOuiText) {
		this.inputOuiText = inputOuiText;
	}
	
}