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

@Entity
@Table(name = "OS_OBJECT")
@org.hibernate.annotations.Table(appliesTo = "OS_OBJECT", indexes = {
		@Index(name = "OS_OBJECT_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class OsObject implements HmBo {

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
	private String osName;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "OS_OBJECT_VERSION", joinColumns = @JoinColumn(name = "OS_OBJECT_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<OsObjectVersion> items = new ArrayList<OsObjectVersion>();
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "OS_OBJECT_VERSION_DHCP", joinColumns = @JoinColumn(name = "OS_OBJECT_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<OsObjectVersion> dhcpItems = new ArrayList<OsObjectVersion>();
	
	private boolean defaultFlag;
	
	public boolean isDefaultFlag()
	{
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag)
	{
		this.defaultFlag = defaultFlag;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}
	
	public List<OsObjectVersion> getItems() {
		return items;
	}

	public void setItems(List<OsObjectVersion> items) {
		this.items = items;
	}
	
	public List<OsObjectVersion> getDhcpItems() {
		return dhcpItems;
	}

	public void setDhcpItems(List<OsObjectVersion> dhcpItems) {
		this.dhcpItems = dhcpItems;
	}

	@Transient
	public String[] getVersionList() {
		List<String> list = new ArrayList<String>();
		if(dhcpItems.size()>0){
			list.add("OS Versions for DHCP options");
			for (OsObjectVersion dhcpItem : dhcpItems) {
				list.add("|--" +dhcpItem.getOsVersion());
			}
		}
		if(items.size()>0){
			list.add("OS Versions for HTTP User-Agent");
			for (OsObjectVersion item : items) {
				list.add("|--" +item.getOsVersion());
			}
		}
		String[] versions = new String[list.size()];
		for(int i=0;i<list.size();i++){
			versions[i]=list.get(i);
		}
		
		return versions;
	}

	@Override
	public boolean equals(Object osObject) {
		if (!(osObject instanceof OsObject)) {
			return false;
		}
		return null == id ? super.equals(osObject) : id.equals(((OsObject) osObject).getId());
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
		return osName;
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
	public OsObject clone() {
		try {
			return (OsObject) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}