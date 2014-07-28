package com.ah.bo.hhm;

import java.sql.Timestamp;

import javax.persistence.Entity;
//import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "hm_updatesoftwareinfo")
public class HMUpdateSoftwareInfo implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long			id;

	private String			domainName;

	private String			ipAddress;

	private String			hmVersion;

	public static final int	STATUS_ACTIVE		= 1;

	public static final int	STATUS_STANDBY		= 2;

	public static final int	STATUS_NEEDCONFIRM	= 3;

	public static final int	STATUS_UPDATEDNS	= 4;

	private int				status = STATUS_ACTIVE;
	
	public static final boolean NEED_AP_SWITH   = true;
	
	public static final boolean NOT_NEED_AP_SWITCH = false;
	
	private boolean   apSwithStatus =  NOT_NEED_AP_SWITCH;     

	public boolean isApSwithStatus() {
		return apSwithStatus;
	}

	public void setApSwithStatus(boolean apSwithStatus) {
		this.apSwithStatus = apSwithStatus;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "HM Update Software Information";
	}

//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "OWNER", nullable = false)
//	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		//return owner;
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
//		this.owner = owner;
	}

	@Version
	private Timestamp	version;

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getHmVersion() {
		return hmVersion;
	}

	public void setHmVersion(String hmVersion) {
		this.hmVersion = hmVersion;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStatusShow()
	{
		switch (status) {
		case STATUS_ACTIVE:
			return "Active";

		case STATUS_STANDBY:
			return "Standby";
			
		case STATUS_NEEDCONFIRM:
			return "Need Confirm";
			
		case STATUS_UPDATEDNS:
			return "Need Update DNS";
			
		default:
			return "Unknown";
		}
	}

}