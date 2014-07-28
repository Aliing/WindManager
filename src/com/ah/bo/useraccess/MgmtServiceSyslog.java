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
@Table(name = "MGMT_SERVICE_SYSLOG")
@org.hibernate.annotations.Table(appliesTo = "MGMT_SERVICE_SYSLOG", indexes = {
		@Index(name = "MGMT_SERVICE_SYS_LOG_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MgmtServiceSyslog implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String mgmtName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean internalServer = true;	
	
	private short facility = 10;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "serverName", "mgmtName", "description",
				"facility","internalServer", "owner"};
	}

	public enum EnumFacility {
		Auth, Authpriv, Security, User, Local0, Local1, Local2, Local3, Local4, Local5, Local6, Local7;
		public String getKey() {
			return name();
		}

		public String getValue() {
			return MgrUtil.getUserMessage(name());
		}
	}

	public enum EnumSeverity {
		Emergency, Alert, Critical, Error, Warning, Notification, Info, Debug;
		public String getKey() {
			return name();
		}

		public String getValue() {
			return MgrUtil.getUserMessage(name());
		}
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MGMT_SERVICE_SYSLOG_INFO", joinColumns = @JoinColumn(name = "MGMT_SERVICE_SYSLOG_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<MgmtServiceSyslogInfo> syslogInfo = new ArrayList<MgmtServiceSyslogInfo>();

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

	public short getFacility() {
		return facility;
	}

	public void setFacility(short facility) {
		this.facility = facility;
	}

	public List<MgmtServiceSyslogInfo> getSyslogInfo() {
		return syslogInfo;
	}

	public void setSyslogInfo(List<MgmtServiceSyslogInfo> syslogInfo) {
		this.syslogInfo = syslogInfo;
	}

	// @Transient
	// private String[] descriptionsSyslog;
	// @Transient
	// private String[] ipAddressesSyslog;
	// @Transient
	// private String[] severitiesSyslog;
	@Transient
	private String syslogRadio = "server";

	// public String[] getDescriptionsSyslog() {
	// return descriptionsSyslog;
	// }
	//
	// public void setDescriptionsSyslog(String[] descriptionsSyslog) {
	// this.descriptionsSyslog = descriptionsSyslog;
	// }
	//
	// public String[] getIpAddressesSyslog() {
	// return ipAddressesSyslog;
	// }
	//
	// public void setIpAddressesSyslog(String[] ipAddressesSyslog) {
	// this.ipAddressesSyslog = ipAddressesSyslog;
	// }
	//
	// public String[] getSeveritiesSyslog() {
	// return severitiesSyslog;
	// }
	//
	// public void setSeveritiesSyslog(String[] severitiesSyslog) {
	// this.severitiesSyslog = severitiesSyslog;
	// }

	public String getSyslogRadio() {
		return syslogRadio;
	}

	public void setSyslogRadio(String syslogRadio) {
		this.syslogRadio = syslogRadio;
	}

	@Transient
	public Map<String, String> getIpAddressValue() {
		Map<String, String> ipAddress = new HashMap<String, String>();

		if (syslogInfo == null || syslogInfo.size() <= 0) {
			ipAddress.put("none", MgrUtil
				.getUserMessage("config.optionsTransfer.none"));
			return ipAddress;
		}

		for (MgmtServiceSyslogInfo syslog : syslogInfo) {
			if (syslog != null && syslog.getIpAddress() != null) {
				ipAddress.put(syslog.getIpAddress().getAddressName(), 
						syslog.getIpAddress().getAddressName());
			}
		}

		return ipAddress;
	}

	@Transient
	public String getFacilityValue() {
		String name = "";
		switch (facility) {
		case 0:
			name = "Auth";
			break;
		case 1:
			name = "Authpriv";
			break;
		case 2:
			name = "Security";
			break;
		case 3:
			name = "User";
			break;
		case 4:
			name = "Local0";
			break;
		case 5:
			name = "Local1";
			break;
		case 6:
			name = "Local2";
			break;
		case 7:
			name = "Local3";
			break;
		case 8:
			name = "Local4";
			break;
		case 9:
			name = "Local5";
			break;
		case 10:
			name = "Local6";
			break;
		case 11:
			name = "Local7";
			break;
		default:
			break;
		}
		return name;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public MgmtServiceSyslog clone() {
		try {
			return (MgmtServiceSyslog) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean getInternalServer() {
		return internalServer;
	}

	public void setInternalServer(boolean internalServer) {
		this.internalServer = internalServer;
	}

}