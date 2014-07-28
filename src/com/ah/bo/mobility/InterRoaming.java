package com.ah.bo.mobility;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "INTER_ROAMING")
@org.hibernate.annotations.Table(appliesTo = "INTER_ROAMING", indexes = {
		@Index(name = "INTER_ROAMING_OWNER", columnNames = { "OWNER" })
		})
public class InterRoaming implements HmBo {

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
	private String roamingName;

	private boolean enabledL3Setting;

	@Range(min = 5, max = 360000)
	private int keepAliveInterval = 10;

	@Range(min = 2, max = 1000)
	private int keepAliveAgeout = 5;

	@Range(min = 10, max = 36000)
	private int updateInterval = 60;

	@Range(min = 1, max = 1000)
	private int updateAgeout = 60;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Override
	public Long getId() {
		return this.id;
	}

	public String getRoamingName() {
		return roamingName;
	}

	public void setRoamingName(String roamingName) {
		this.roamingName = roamingName;
	}

	public boolean isEnabledL3Setting() {
		return enabledL3Setting;
	}

	public boolean getEnabledL3Setting() {
		return enabledL3Setting;
	}

	public void setEnabledL3Setting(boolean enabledL3Setting) {
		this.enabledL3Setting = enabledL3Setting;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public int getKeepAliveAgeout() {
		return keepAliveAgeout;
	}

	public void setKeepAliveAgeout(int keepAliveAgeout) {
		this.keepAliveAgeout = keepAliveAgeout;
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}

	public int getUpdateAgeout() {
		return updateAgeout;
	}

	public void setUpdateAgeout(int updateAgeout) {
		this.updateAgeout = updateAgeout;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		return this.roamingName;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}