/**
 *@filename		Vlan.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-21 PM 07:16:52
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.useraccess.UserProfile;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "VLAN")
@org.hibernate.annotations.Table(appliesTo = "VLAN", indexes = {
		@Index(name = "VLAN_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Vlan implements HmBo {

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
	private String vlanName;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "VLAN_ITEM", joinColumns = @JoinColumn(name = "VLAN_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SingleTableItem> items = new ArrayList<SingleTableItem>();

	private boolean defaultFlag;

	@OneToMany(mappedBy = "vlan")
	private Set<UserProfile> userProfile = new HashSet<UserProfile>();

	public Set<UserProfile> getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(Set<UserProfile> userProfile) {
		this.userProfile = userProfile;
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
		return vlanName;
	}

	public String getVlanName() {
		return vlanName;
	}

	public void setVlanName(String vlanName) {
		this.vlanName = vlanName;
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

	public List<SingleTableItem> getItems() {
		return items;
	}

	public void setItems(List<SingleTableItem> items) {
		this.items = items;
	}

	@Transient
	public int getVlanCount() {
		return items.size();
	}

	@Transient
	public String[] getVlanList() {
		String[] vlans = new String[items.size()];
		int i = 0;
		for (SingleTableItem item : items) {
			vlans[i++] = item.getVlanId() + "/"
					+ MgrUtil.getEnumString("enum.ipAddress." + item.getType());
		}
		return vlans;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Vlan
				&& (null == id ? super.equals(other) : id.equals(((Vlan) other).getId()));
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	
	@Override
	public Vlan clone() {
		try {
			return (Vlan) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}