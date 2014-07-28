/**
 *@filename		DomainObject.java
 *@version
 *@author		Fiona
 *@createtime	2011-2-25 AM 10:32:00
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
import java.util.regex.Pattern;

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

import org.apache.commons.lang.StringUtils;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "DOMAIN_OBJECT")
@org.hibernate.annotations.Table(appliesTo = "DOMAIN_OBJECT", indexes = {
		@Index(name = "DOMAIN_OBJECT_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class DomainObject implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String objName;
	
	private boolean autoGenerateFlag;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "DOMAIN_NAME_ITEM", joinColumns = @JoinColumn(name = "DOMAIN_OBJECT_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<DomainNameItem> items = new ArrayList<DomainNameItem>();
	
	private Short objType = CLASSIFICATION_POLICY;
	
    public static final short CLASSIFICATION_POLICY  = 1;
	
	public static final short VPN_TUNNEL = 2;
	
	public static final short WEB_SECURITY = 3;
	
	public static EnumItem[] ENUM_DOMAIN_OBJECT_TYPE = MgrUtil.enumItems(
			"enum.domain.object.type.", new int[] {CLASSIFICATION_POLICY,
					VPN_TUNNEL, WEB_SECURITY });

	public String getObjName()
	{
		return objName;
	}

	public void setObjName(String objName)
	{
		this.objName = objName;
	}

	public List<DomainNameItem> getItems()
	{
		return items;
	}

	public void setItems(List<DomainNameItem> items)
	{
		this.items = items;
	}
	
	public boolean isAutoGenerateFlag() {
		return autoGenerateFlag;
	}

	public void setAutoGenerateFlag(boolean autoGenerateFlag) {
		this.autoGenerateFlag = autoGenerateFlag;
	}

	@Transient
	public String[] getNameList() {
		String[] names = new String[items.size()];
		int i = 0;
		for (DomainNameItem item : items) {
			names[i++] = item.getDomainName();
		}
		return names;
	}

	@Override
	public boolean equals(Object domainObject) {
		if (!(domainObject instanceof DomainObject)) {
			return false;
		}
		return null == id ? super.equals(domainObject) : id.equals(((DomainObject) domainObject).getId());
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
		return objName;
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
	public DomainObject clone() {
		try {
			return (DomainObject) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Short getObjType() {
		return objType;
	}

	public void setObjType(Short objType) {
		this.objType = objType;
	}
	
	@Transient
	public String getObjTypeShow() {
		switch (objType) {
		case 1:
			return "Client Classification";
		case 2:
			return "VPN Tunnel";
		case 3:
			return "Web Security";
		default:
			return "Client Classification";
		}
	}
	
	@Transient
    public boolean isGenerated() {
        if (StringUtils.isNotBlank(this.objName))
            return Pattern.matches("^DNS_\\d+_\\d{17}$", this.objName);
        return false;
    }
}