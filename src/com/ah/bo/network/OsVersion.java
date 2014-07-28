package com.ah.bo.network;

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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "OS_VERSION")
@org.hibernate.annotations.Table(appliesTo = "OS_VERSION", indexes = {
		@Index(name = "OS_VERSION_OWNER", columnNames = { "OWNER" })
		})
public class OsVersion implements HmBo  {
	private static final long	serialVersionUID	= 1L;
	
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;
	
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String	osVersion;
	
	private String option55;
	
	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getOption55() {
		return option55;
	}

	public void setOption55(String option55) {
		this.option55 = option55;
	}
	
	@Override
	public boolean equals(Object osVersion) {
		if (!(osVersion instanceof OsVersion)) {
			return false;
		}
		return null == id ? super.equals(osVersion) : id.equals(((OsVersion) osVersion).getId());
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
		return osVersion;
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
	public OsVersion clone() {
		try {
			return (OsVersion) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
