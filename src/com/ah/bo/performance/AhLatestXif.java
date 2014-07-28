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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_LATESTXIF")
@org.hibernate.annotations.Table(appliesTo = "HM_LATESTXIF", indexes = {
		@Index(name = "LATEST_XIF_OWNER", columnNames = { "OWNER" }),
		@Index(name = "LATEST_XIF_AP_MAC", columnNames = { "APMAC" })
		})
public class AhLatestXif implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 64, nullable = false)
	private String apName;

	@Column(nullable = false)
	private int ifIndex;

	@Column(nullable = false)
	private String apMac;

//	private Date statTime;

	@Column(length = 14)
	private String apSerialNumber;

	private byte ifPromiscuous;

	private byte ifType;

	private byte ifMode;

	@Column(length = 32)
	private String ifName = "";

	@Column(length = 32)
	private String ssidName = "";

	private byte ifConfMode;
	
	public static final byte INTERFACE_STATUS_UP = 1;
	public static final byte INTERFACE_STATUS_DOWN = 2;

	private byte ifAdminStatus;

	private byte ifOperStatus;

	private HmTimeStamp	timeStamp = HmTimeStamp.CURRENT_TIMESTAMP;

	private String				bssid = "";

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
		return "AhLatestXif";
	}

	// For multi page selection
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

	@Transient
	public String getIfTypeString() {
		switch (ifType) {
		case AhXIf.IFTYPE_PHYSICAL:
		case AhXIf.IFTYPE_VIRTURAL:
			return MgrUtil.getEnumString("enum.mib.interface.type." + ifType);
		default:
			return "Unknown";
		}
	}

	@Transient
	public String getIfModeString() {
		switch (ifMode) {
		case AhXIf.IFMODE_NOTUSED:
		case AhXIf.IFMODE_ACCESS:
		case AhXIf.IFMODE_BACKHAUL:
		case AhXIf.IFMODE_BRIDGE:
		case AhXIf.IFMODE_DUAL:
		case AhXIf.IFMODE_SENSOR:
		case AhXIf.IFMODE_WAN_CLIENT:
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
		case AhXIf.IFMODE_ACCESS:
		case AhXIf.IFMODE_BACKHAUL:
		case AhXIf.IFMODE_BRIDGE:
		case AhXIf.IFMODE_DUAL:
		case AhXIf.IFMODE_SENSOR:
		case AhXIf.IFMODE_WAN_CLIENT:
			return MgrUtil.getEnumString("enum.mib.interface.mode."
					+ ifConfMode);
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
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

//	public Date getStatTime() {
//		return statTime;
//	}
//
//	public void setStatTime(Date statTime) {
//		this.statTime = statTime;
//	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Transient
	private int channel;

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "AP Node Id: " + apMac + "; If Name: " + ifName + "; If Type: " + ifType + "; If Mode: " + ifMode +  "; BSSID: " + bssid + "; SSID: " + ssidName;
	}

}