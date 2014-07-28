package com.ah.bo.useraccess;

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
@Table(name = "MGMT_SERVICE_DNS")
@org.hibernate.annotations.Table(appliesTo = "MGMT_SERVICE_DNS", indexes = {
		@Index(name = "MGMT_SERVICE_DNS_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MgmtServiceDns implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 32)
	private String domainName;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String mgmtName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MGMT_SERVICE_DNS_INFO", joinColumns = @JoinColumn(name = "MGMT_SERVICE_DNS_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<MgmtServiceDnsInfo> dnsInfo = new ArrayList<MgmtServiceDnsInfo>();

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "domainName", "mgmtName", "description", "owner"};
	}

	public List<MgmtServiceDnsInfo> getDnsInfo() {
		return dnsInfo;
	}

	public void setDnsInfo(List<MgmtServiceDnsInfo> dnsInfo) {
		this.dnsInfo = dnsInfo;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return this.mgmtName;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMgmtName() {
		return mgmtName;
	}

	public void setMgmtName(String mgmtName) {
		this.mgmtName = mgmtName;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String[] getIpAddressValue() {
		String[] ips = new String[dnsInfo.size()];
		int i = 0;
		for (MgmtServiceDnsInfo dns : dnsInfo) {
			if (dns != null) {
				String serverName = dns.getServerName();
				if (null != serverName && !"".equals(serverName)) {
					ips[i++] = serverName;
				} else if (null != dns.getIpAddress()) {
					ips[i++] = dns.getIpAddress().getAddressName();
				}
			}
		}
		return ips;
	}

	@Override
	public MgmtServiceDns clone() {
		try {
			return (MgmtServiceDns) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	private boolean defaultFlag;
	
	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}
	
	@Transient
	private boolean parentIframeOpenFlg;
	@Transient
	private String parentDomID = "";
	@Transient
	private String contentShowType = "subdrawer";
	
	public String getContentShowType() {
		return contentShowType;
	}

	public void setContentShowType(String contentShowType) {
		this.contentShowType = contentShowType;
	}
	
	public boolean isParentIframeOpenFlg() {
		return parentIframeOpenFlg;
	}

	public void setParentIframeOpenFlg(boolean parentIframeOpenFlg) {
		this.parentIframeOpenFlg = parentIframeOpenFlg;
	}

	public String getParentDomID() {
		return parentDomID;
	}

	public void setParentDomID(String parentDomID) {
		this.parentDomID = parentDomID;
	}

}