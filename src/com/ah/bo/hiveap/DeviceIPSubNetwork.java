package com.ah.bo.hiveap;

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
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "DEVICE_INTERFACE_IPSUBNETWORK")
@org.hibernate.annotations.Table(appliesTo = "DEVICE_INTERFACE_IPSUBNETWORK", indexes = {
		@Index(name = "DEVICE_INTERFACE_IP_SUB_NETWORK_OWNER", columnNames = { "OWNER" })
		})
public class DeviceIPSubNetwork implements HmBo {

	private static final long serialVersionUID = 1592120113946307971L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;
	
	private String ipSubNetwork;
	
	@Transient
	private boolean selected;
	
	@Override
	public HmDomain getOwner() {
		return this.owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		return this.ipSubNetwork;
	}

	@Override
	public Long getId() {
		return this.id;
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

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getIpSubNetwork() {
		return ipSubNetwork;
	}

	public void setIpSubNetwork(String ipSubNetwork) {
		this.ipSubNetwork = ipSubNetwork;
	}

}