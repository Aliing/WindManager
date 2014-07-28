package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.util.datetime.AhDateTimeUtil;
@Entity
@Table(name = "MDM_PROFILES")
@org.hibernate.annotations.Table(appliesTo = "MDM_PROFILES", indexes = {
		@Index(name = "MDM_PROFILES_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)

public class MdmProfiles implements HmBo {
	
	private static final long serialVersionUID = 1L;
	public static final SimpleDateFormat   DISPLAY_DATE_FORMAT         = new SimpleDateFormat("MMM dd,yyyy HH:mm");

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String mdmProfilesName;
	
	private long createTime = 0;
	
	private long updateTime = 0;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;	
	
	@Transient
	private boolean selected;	
	
	@Range(min = 0, max = 4095)
	private short userProfileAttributeValue = 1;
	
	
	
	public short getUserProfileAttributeValue() {
		return userProfileAttributeValue;
	}

	public void setUserProfileAttributeValue(short userProfileAttributeValue) {
		this.userProfileAttributeValue = userProfileAttributeValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMdmProfilesName() {
		return mdmProfilesName;
	}

	public void setMdmProfilesName(String mdmProfilesName) {
		this.mdmProfilesName = mdmProfilesName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
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
	
	public String getCreateTimeForDisplay() {
		return createTime==0?"":AhDateTimeUtil.getDateStrFromLong(createTime, DISPLAY_DATE_FORMAT);
	}
	
	public String getUpdateTimeForDisplay() {
		return updateTime==0?"":AhDateTimeUtil.getDateStrFromLong(updateTime, DISPLAY_DATE_FORMAT);
	}
	
	@Override
	public String getLabel() {
		return this.mdmProfilesName;
	}
	
	@Override
	public MdmProfiles clone() {
		try {
			return (MdmProfiles) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
