package com.ah.bo.useraccess;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Entity
@Table(name = "MGMT_SERVICE_SNMP")
@org.hibernate.annotations.Table(appliesTo = "MGMT_SERVICE_SNMP", indexes = {
		@Index(name = "MGMT_SERVICE_SNMP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MgmtServiceSnmp implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String mgmtName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean enableSnmp;
	
	private boolean enableCapwap = true;

	private boolean defaultFlag;

	private String contact;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "mgmtName", "description", "enableSnmp",
				"defaultFlag", "contact", "enableCapwap", "owner"};
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MGMT_SERVICE_SNMP_INFO", joinColumns = @JoinColumn(name = "MGMT_SERVICE_SNMP_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<MgmtServiceSnmpInfo> snmpInfo = new ArrayList<MgmtServiceSnmpInfo>();

	public List<MgmtServiceSnmpInfo> getSnmpInfo() {
		return snmpInfo;
	}

	public void setSnmpInfo(List<MgmtServiceSnmpInfo> snmpInfo) {
		this.snmpInfo = snmpInfo;
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

	public boolean getEnableSnmp() {
		return enableSnmp;
	}

	public void setEnableSnmp(boolean enableSnmp) {
		this.enableSnmp = enableSnmp;
	}

	public boolean getEnableCapwap() {
		return enableCapwap;
	}

	public void setEnableCapwap(boolean enableCapwap) {
		this.enableCapwap = enableCapwap;
	}
	
	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	// @Transient
	// private String[] communitiesString;
	//
	// @Transient
	// private String[] ipAddressesSnmp;
	//
	// @Transient
	// private String[] versionsSnmp;
	//
	// @Transient
	// private String[] operationsSnmp;

	@Transient
	private String snmpRadio = "server";

	// public String[] getCommunitiesString() {
	// return communitiesString;
	// }
	//
	// public void setCommunitiesString(String[] communitiesString) {
	// this.communitiesString = communitiesString;
	// }
	//
	// public String[] getIpAddressesSnmp() {
	// return ipAddressesSnmp;
	// }
	//
	// public void setIpAddressesSnmp(String[] ipAddressesSnmp) {
	// this.ipAddressesSnmp = ipAddressesSnmp;
	// }
	//
	// public String[] getOperationsSnmp() {
	// return operationsSnmp;
	// }
	//
	// public void setOperationsSnmp(String[] operationsSnmp) {
	// this.operationsSnmp = operationsSnmp;
	// }

	public String getSnmpRadio() {
		return snmpRadio;
	}

	public void setSnmpRadio(String snmpRadio) {
		this.snmpRadio = snmpRadio;
	}

	// public String[] getVersionsSnmp() {
	// return versionsSnmp;
	// }
	//
	// public void setVersionsSnmp(String[] versionsSnmp) {
	// this.versionsSnmp = versionsSnmp;
	// }

	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Transient
	public String getEnableCapwapValue() {
		return enableCapwap ? "Enabled" : "Disabled";
	}

	@Transient
	public String getEnableSnmpValue() {
		return enableSnmp ? "Enabled" : "Disabled";
	}
	
	@Transient
	public Map<String, String> getIpAddressValue() {
		Map<String, String> ipAddress = new HashMap<String, String>();

		if (snmpInfo == null || snmpInfo.size() <= 0) {
			ipAddress.put("none", MgrUtil
				.getUserMessage("config.optionsTransfer.none"));
			return ipAddress;
		}

		for (MgmtServiceSnmpInfo snmp : snmpInfo) {
			if (snmp != null && snmp.getIpAddress() != null) {
				ipAddress.put(snmp.getIpAddress().getAddressName(), 
						snmp.getIpAddress().getAddressName());
			}
		}

		return ipAddress;
	}

	@Override
	public MgmtServiceSnmp clone() {
		try {
			return (MgmtServiceSnmp) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}