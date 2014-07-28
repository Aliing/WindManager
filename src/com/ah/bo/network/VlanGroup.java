/**
 *@filename		VlanGroup.java
 *@version
 *@author		Wenping
 *@createtime	2012-8-30 PM 07:16:52
 *Copyright (c) 2006-2012 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

/**
 * @author Wenping
 * @version V1.0.0.0
 */
@Entity
@Table(name = "VLAN_GROUP")
@org.hibernate.annotations.Table(appliesTo = "VLAN_GROUP", indexes = {
		@Index(name = "VLAN_GROUP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class VlanGroup implements HmBo {

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
	private String vlanGroupName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;
	
	private String vlans;
	
	private boolean defaultFlag;

	public String getVlanGroupName() {
		return vlanGroupName;
	}

	public void setVlanGroupName(String vlanGroupName) {
		this.vlanGroupName = vlanGroupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVlans() {
		return vlans;
	}

	public void setVlans(String vlans) {
		this.vlans = vlans;
	}

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
	public String getLabel() {
		return vlanGroupName;
	}

	public boolean getDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
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
	public VlanGroup clone() {
		try {
			return (VlanGroup) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof VlanGroup
				&& (null == id ? super.equals(other) : id
						.equals(((VlanGroup) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	@Transient
	public boolean getVlanGroupIsAny(){
		if(vlanGroupName == null || vlanGroupName.isEmpty() || MgrUtil.getUserMessage("config.ipPolicy.any").equals(vlanGroupName)){
			return true;
		} else {
			return false;
		}
	}
}