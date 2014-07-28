/**
 *@filename		IpAddress.java
 *@version
 *@author		Fiona
 *@createtime	2007-8-29 PM 03:16:58
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
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
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
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "IP_ADDRESS")
@org.hibernate.annotations.Table(appliesTo = "IP_ADDRESS", indexes = {
		@Index(name = "IP_ADDRESS_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class IpAddress implements HmBo {

	private static final long serialVersionUID = 1L;

	public static final String NETMASK_OF_SINGLE_IP = "255.255.255.255";
	
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
	private String addressName;

	public static final short TYPE_IP_ADDRESS = 1;
	
	public static final short TYPE_HOST_NAME = 2;
	
	public static final short TYPE_IP_NETWORK = 3;
	
	public static final short TYPE_IP_WILDCARD = 4;
	
	public static final short TYPE_IP_RANGE = 5;
	
	public static final short TYPE_WEB_PAGE = 6;
	
	private short typeFlag = TYPE_IP_ADDRESS;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "IP_ADDRESS_ITEM", joinColumns = @JoinColumn(name = "IP_ADDRESS_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<SingleTableItem> items = new ArrayList<SingleTableItem>();

	private boolean defaultFlag;

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

	public String getAddressName() {
		return addressName;
	}

	@Transient
	public String getValue() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
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
		return addressName;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IpAddress)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((IpAddress) other).getId());
	}

	@Transient
	public String[] getIpAddressList() {
		String[] ips = new String[items.size()];
		int i = 0;
		for (SingleTableItem item : items) {
			String ipentry = item.getIpAddress();
			if (TYPE_IP_RANGE == typeFlag) {
				ipentry += (" - "+item.getNetmask());
			}
			ips[i++] = ipentry + "/"
					+ MgrUtil.getEnumString("enum.ipAddress." + item.getType());
		}
		return ips;
	}

	@Transient
	public int getIpAddressCount() {
		return items.size();
	}

	@Transient
	public String getStringType() {
		switch (typeFlag) {
			case TYPE_IP_ADDRESS:
				return "IP Address";
			case TYPE_HOST_NAME:
				return "Host Name";
			case TYPE_IP_NETWORK:
				return "Network";
			case TYPE_IP_WILDCARD:
				return "Wildcard";
			case TYPE_IP_RANGE:
				return "IP Range";
			case TYPE_WEB_PAGE:
				return "Web Page";
			default:
				return "UnKnown";
		}
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}

	public boolean isDefaultFlag() {
		return addressName.equals("10.0.0.0") || addressName.equals("172.16.0.0") || addressName.equals("192.168.0.0") || defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	public List<SingleTableItem> getItems() {
		return items;
	}

	public void setItems(List<SingleTableItem> items) {
		this.items = items;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public short getTypeFlag()
	{
		return typeFlag;
	}

	public void setTypeFlag(short typeFlag)
	{
		this.typeFlag = typeFlag;
	}
	
	@Override
	public IpAddress clone() {
		try {
			return (IpAddress) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}