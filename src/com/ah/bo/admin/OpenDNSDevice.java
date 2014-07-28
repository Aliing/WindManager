package com.ah.bo.admin;

import java.sql.Timestamp;

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

@Entity
@Table(name = "OPENDNS_DEVICE")
@org.hibernate.annotations.Table(appliesTo = "OPENDNS_DEVICE", indexes = {
		@Index(name = "OPENDNS_DEVICE_OWNER", columnNames = { "OWNER" })
		})
public class OpenDNSDevice implements HmBo {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long	id;
	
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
	
	private String deviceLabel;
	
	private String deviceId;
	
	private String deviceKey;
	
	private boolean defaultDevice = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OPENDNS_ACCOUNT_ID")
	private OpenDNSAccount openDNSAccount;
	
	public String getDeviceLabel() {
		return deviceLabel;
	}

	public void setDeviceLabel(String deviceLabel) {
		this.deviceLabel = deviceLabel;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceKey() {
		return deviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		this.deviceKey = deviceKey;
	}

	@Override
	public String getLabel() {
		return deviceLabel;
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
		return this.version;
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

	public boolean isDefaultDevice() {
		return defaultDevice;
	}

	public void setDefaultDevice(boolean defaultDevice) {
		this.defaultDevice = defaultDevice;
	}

	public OpenDNSAccount getOpenDNSAccount() {
		return openDNSAccount;
	}

	public void setOpenDNSAccount(OpenDNSAccount openDNSAccount) {
		this.openDNSAccount = openDNSAccount;
	}

}
