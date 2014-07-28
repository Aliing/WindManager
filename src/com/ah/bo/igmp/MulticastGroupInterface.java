package com.ah.bo.igmp;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
@Entity
@Table(name = "MULTICAST_GROUP_INTERFACE")
@org.hibernate.annotations.Table(appliesTo = "MULTICAST_GROUP_INTERFACE", indexes = {
		@Index(name = "MULTICAST_GROUP_INTERFACE_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MulticastGroupInterface implements HmBo {

	private static final long serialVersionUID = -5553785668197993106L;

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
	
	
	private Short interfaceType;
	private Integer interfacePort;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "multicastGroupId")
	private MulticastGroup multicastGroup;
	
	
	public MulticastGroup getMulticastGroup() {
		return multicastGroup;
	}

	public void setMulticastGroup(MulticastGroup multicastGroup) {
		this.multicastGroup = multicastGroup;
	}

	public Short getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(Short interfaceType) {
		this.interfaceType = interfaceType;
	}

	public Integer getInterfacePort() {
		return interfacePort;
	}

	public void setInterfacePort(Integer interfacePort) {
		this.interfacePort = interfacePort;
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
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof MulticastGroupInterface)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((MulticastGroupInterface) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	@Override
	public MulticastGroupInterface clone() {
		try {
			return (MulticastGroupInterface) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
