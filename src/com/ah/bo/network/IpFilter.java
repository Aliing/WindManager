package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/*
 * @author Fisher
 */

@Entity
@Table(name = "IP_FILTER")
@org.hibernate.annotations.Table(appliesTo = "IP_FILTER", indexes = {
		@Index(name = "IP_FILTER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class IpFilter implements HmBo {

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

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String filterName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@Version
	private Timestamp version;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "IP_FILTER_IP_ADDRESS", joinColumns = { @JoinColumn(name = "IP_FILTER_ID") }, inverseJoinColumns = { @JoinColumn(name = "IP_ADDRESS_ID") })
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private Set<IpAddress> ipAddress = new HashSet<IpAddress>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<IpAddress> getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(Set<IpAddress> ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IpFilter)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((IpFilter) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
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
		return filterName;
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
    public IpFilter clone() {
       try {
           return (IpFilter) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

}