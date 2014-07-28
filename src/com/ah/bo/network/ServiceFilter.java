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

/*
 * @author Fisher
 */

@Entity
@Table(name = "SERVICE_FILTER")
@org.hibernate.annotations.Table(appliesTo = "SERVICE_FILTER", indexes = {
		@Index(name = "SERVICE_FILTER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class ServiceFilter implements HmBo {

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

	@Column(length = 64, nullable = false)
	private String filterName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean enableSSH = true;

	private boolean enableTelnet;

	private boolean enablePing = true;

	private boolean enableSNMP;

	private boolean defaultFlag;
	
	private boolean interTraffic = true;

	@Transient
	public boolean isDefaultValues() {
		/*
		 * this function is used for check the profile is with the default
		 * values.
		 * Note: the function need to updated while default value changed!
		 */
		return getEnableSSH() && !getEnableTelnet() && getEnablePing()
				&& !getEnableSNMP() && getInterTraffic();
	}

	@Version
	private Timestamp version;

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getEnableSSH() {
		return enableSSH;
	}

	public void setEnableSSH(boolean enableSSH) {
		this.enableSSH = enableSSH;
	}

	public boolean getEnableTelnet() {
		return enableTelnet;
	}

	public void setEnableTelnet(boolean enableTelnet) {
		this.enableTelnet = enableTelnet;
	}

	public boolean getEnablePing() {
		return enablePing;
	}

	public void setEnablePing(boolean enablePing) {
		this.enablePing = enablePing;
	}

	public boolean getEnableSNMP() {
		return enableSNMP;
	}

	public void setEnableSNMP(boolean enableSNMP) {
		this.enableSNMP = enableSNMP;
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
		if (!(other instanceof ServiceFilter)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((ServiceFilter) other).getId());
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

	public boolean getInterTraffic() {
		return interTraffic;
	}

	public void setInterTraffic(boolean interTraffic) {
		this.interTraffic = interTraffic;
	}

    @Override
    public ServiceFilter clone() {
       try {
           return (ServiceFilter) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

}