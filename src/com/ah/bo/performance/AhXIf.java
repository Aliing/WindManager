package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_XIF",uniqueConstraints = { @UniqueConstraint( columnNames = { "statTimeStamp", "apName", "ifIndex" } ) })
@org.hibernate.annotations.Table(appliesTo = "HM_XIF", indexes = {
		@Index(name = "XIF_OWNER", columnNames = { "OWNER" })
		})
public class AhXIf implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private XIfPK				xifpk;

	private String				apMac;

	@Column(length = 14)
	private String				apSerialNumber;

	public static final byte	IFPROMISCUOUS_TRUE	= 1;

	public static final byte	IFPROMISCUOUS_FALSE	= 2;

	private byte				ifPromiscuous;

	public static final byte	IFTYPE_PHYSICAL		= 0;

	public static final byte	IFTYPE_VIRTURAL		= 1;

	public static EnumItem[]	IF_TYPE				= MgrUtil.enumItems("enum.mib.interface.type.",
															new int[] { IFTYPE_PHYSICAL,
			IFTYPE_VIRTURAL								});

	private byte				ifType;

	public static final byte	IFMODE_NOTUSED		= 0;

	public static final byte	IFMODE_ACCESS		= 1;

	public static final byte	IFMODE_BACKHAUL		= 2;

	public static final byte	IFMODE_BRIDGE		= 3;

	public static final byte	IFMODE_DUAL			= 4;
	
	public static final byte	IFMODE_SENSOR		= 5;
	
	public static final byte	IFMODE_WAN_CLIENT	= 6;

	public static EnumItem[]	IF_MODE				= MgrUtil.enumItems("enum.mib.interface.mode.",
															new int[] { IFMODE_NOTUSED,
			IFMODE_ACCESS, IFMODE_BACKHAUL, IFMODE_BRIDGE, IFMODE_DUAL, IFMODE_SENSOR, IFMODE_WAN_CLIENT});

	private byte				ifMode;

	@Column(length = 32)
	private String				ifName				= "";

	@Column(length = 32)
	private String				ssidName			= "";

	private byte				ifConfMode;

	public static final byte	IFADMINSTATUS_UP	= 1;

	public static final byte	IFADMINSTATUS_DOWN	= 2;

	private byte				ifAdminStatus;

	public static final byte	IFOPERSTATUS_UP		= 1;

	public static final byte	IFOPERSTATUS_DOWN	= 2;

	private byte				ifOperStatus;

	private String				bssid				= "";

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public byte getIfAdminStatus() {
		return ifAdminStatus;
	}

	public void setIfAdminStatus(byte ifAdminStatus) {
		this.ifAdminStatus = ifAdminStatus;
	}

	public byte getIfMode() {
		return ifMode;
	}

	public void setIfMode(byte ifMode) {
		this.ifMode = ifMode;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public byte getIfPromiscuous() {
		return ifPromiscuous;
	}

	public void setIfPromiscuous(byte ifPromiscuous) {
		this.ifPromiscuous = ifPromiscuous;
	}

	public byte getIfType() {
		return ifType;
	}

	public void setIfType(byte ifType) {
		this.ifType = ifType;
	}

	public String getSsidName() {
		return ssidName;
	}

	public void setSsidName(String name) {
		ssidName = name;
	}

	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "";
	}

	// For multi page selection
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

	/*
	 * statistic objects have no owner or version,because it created by system, it can't be updated
	 * and 'statTime' keep track of when create it.
	 */
	/**
	 * modify mark: add owner field for VHM
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApSerialNumber() {
		return apSerialNumber;
	}

	public void setApSerialNumber(String apSerialNumber) {
		this.apSerialNumber = apSerialNumber;
	}

	public XIfPK getXifpk() {
		return xifpk;
	}

	public void setXifpk(XIfPK xifpk) {
		this.xifpk = xifpk;
	}

	@Transient
	public String getIfTypeString() {
		switch (ifType) {
		case IFTYPE_PHYSICAL:
		case IFTYPE_VIRTURAL:
			return MgrUtil.getEnumString("enum.mib.interface.type." + ifType);
		default:
			return "Unknown";
		}
	}

	@Transient
	public String getIfModeString() {
		switch (ifMode) {
		case IFMODE_NOTUSED:
		case IFMODE_ACCESS:
		case IFMODE_BACKHAUL:
		case IFMODE_BRIDGE:
		case IFMODE_DUAL:
		case IFMODE_SENSOR:
		case IFMODE_WAN_CLIENT:
			return MgrUtil.getEnumString("enum.mib.interface.mode." + ifMode);
		default:
			return "Unknown";
		}
	}

	public byte getIfConfMode() {
		return ifConfMode;
	}

	public void setIfConfMode(byte ifConfMode) {
		this.ifConfMode = ifConfMode;
	}

	@Transient
	public String getIfConfModeString() {
		switch (ifConfMode) {
		case IFMODE_ACCESS:
		case IFMODE_BACKHAUL:
		case IFMODE_BRIDGE:
		case IFMODE_DUAL:
		case IFMODE_SENSOR:
		case IFMODE_WAN_CLIENT:
			return MgrUtil.getEnumString("enum.mib.interface.mode." + ifConfMode);
		default:
			return "Unknown";
		}
	}

	public byte getIfOperStatus() {
		return ifOperStatus;
	}

	public void setIfOperStatus(byte ifOperStatus) {
		this.ifOperStatus = ifOperStatus;
	}

	@Override
	public void setId(Long id) {
		this.id=id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

}