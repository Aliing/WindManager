package com.ah.bo.hiveap;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "DEVICE_RESETCONFIG")
@org.hibernate.annotations.Table(appliesTo = "DEVICE_RESETCONFIG", indexes = {
		@Index(name = "DEVICE_RESETCONFIG_SERIALNUMBER_OWNER", columnNames = { "OWNER", "SERIALNUMBER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DeviceResetConfig implements HmBo {

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
	
	@Column(length = 14, nullable = false, unique = true)
	private String serialNumber;

	private long timestamp = System.currentTimeMillis();

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
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
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof DeviceResetConfig
				&& (null == id ? super.equals(other) : id.equals(((DeviceResetConfig) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	@Override
	public DeviceResetConfig clone() {
		try {
			return (DeviceResetConfig) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	@Override
	public String getLabel() {
		return this.getSerialNumber();
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
