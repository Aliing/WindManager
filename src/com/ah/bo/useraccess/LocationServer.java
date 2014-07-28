/**
 *@filename		LocationServer.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-12 PM 03:02:08
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.useraccess;

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
import org.hibernate.validator.constraints.Range;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.network.IpAddress;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "LOCATION_SERVER")
@org.hibernate.annotations.Table(appliesTo = "LOCATION_SERVER", indexes = {
		@Index(name = "LOCATION_SERVER_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class LocationServer implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long				id;

	@Version
	private Timestamp			version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String				name;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String				description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IPADDRESS_ID")
	private IpAddress			serverIP;

	private boolean				enableServer			= true;

	private boolean				enableTag				= true;

	private boolean				enableStation			= true;

	private boolean				enableRogue				= true;

	@Range(min = 1, max = 100000)
	private int					tagThreshold			= 1000;

	@Range(min = 1, max = 100000)
	private int					stationThreshold		= 200;

	@Range(min = 1, max = 100000)
	private int					rogueThreshold			= 50;

	public static final byte	SERVICETYPE_AEROHIVE	= 1;
	public static final byte	SERVICETYPE_AEROSCOUT	= 2;
	public static final byte	SERVICETYPE_EKAHAU	= 3;

	private byte				serviceType				= SERVICETYPE_AEROHIVE;

	private int					rssiChangeThreshold		= 3;

	private int					locationReportInterval	= 60;

	private int					rssiValidPeriod			= 60;

	private int					rssiHoldCount			= 0;

	private int					reportSuppressCount		= 0;
	
	@Range(min = 1, max = 65535)
	private int					ekahauPort=1;
	
	private String 				ekahauMac = "01188E000000";
	
	@Range(min = 1, max = 100000)
	private int 				ekahauTagThreshold=1000;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain			owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	public String getLabel() {
		return name;
	}

	@Transient
	public String getStrLocationServer() {
		return enableServer ? "Enabled" : "Disabled";
	}

	@Transient
	public String getStrTag() {
		return enableTag ? "Enabled" : "Disabled";
	}

	@Transient
	public String getStrStation() {
		return enableStation ? "Enabled" : "Disabled";
	}

	@Transient
	public String getStrRogue() {
		return enableRogue ? "Enabled" : "Disabled";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IpAddress getServerIP() {
		return serverIP;
	}

	public void setServerIP(IpAddress serverIP) {
		this.serverIP = serverIP;
	}

	public boolean isEnableServer() {
		return enableServer;
	}

	public void setEnableServer(boolean enableServer) {
		this.enableServer = enableServer;
	}

	public boolean isEnableTag() {
		return enableTag;
	}

	public void setEnableTag(boolean enableTag) {
		this.enableTag = enableTag;
	}

	public boolean isEnableStation() {
		return enableStation;
	}

	public void setEnableStation(boolean enableStation) {
		this.enableStation = enableStation;
	}

	public boolean isEnableRogue() {
		return enableRogue;
	}

	public void setEnableRogue(boolean enableRogue) {
		this.enableRogue = enableRogue;
	}

	public int getTagThreshold() {
		return tagThreshold;
	}

	public void setTagThreshold(int tagThreshold) {
		this.tagThreshold = tagThreshold;
	}

	public int getStationThreshold() {
		return stationThreshold;
	}

	public void setStationThreshold(int stationThreshold) {
		this.stationThreshold = stationThreshold;
	}

	public int getRogueThreshold() {
		return rogueThreshold;
	}

	public void setRogueThreshold(int rogueThreshold) {
		this.rogueThreshold = rogueThreshold;
	}

	public int getLocationReportInterval() {
		return locationReportInterval;
	}

	public void setLocationReportInterval(int locationReportInterval) {
		this.locationReportInterval = locationReportInterval;
	}

	public int getRssiChangeThreshold() {
		return rssiChangeThreshold;
	}

	public void setRssiChangeThreshold(int rssiChangeThreshold) {
		this.rssiChangeThreshold = rssiChangeThreshold;
	}

	public int getRssiValidPeriod() {
		return rssiValidPeriod;
	}

	public void setRssiValidPeriod(int rssiValidPeriod) {
		this.rssiValidPeriod = rssiValidPeriod;
	}

	public byte getServiceType() {
		return serviceType;
	}

	public void setServiceType(byte serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceType4Show() {
		switch (serviceType) {
		case SERVICETYPE_EKAHAU: return "Ekahau";
		case SERVICETYPE_AEROSCOUT: return "Aeroscout";
		default: return NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank();
		}
	}

	public int getReportSuppressCount() {
		return reportSuppressCount;
	}

	public void setReportSuppressCount(int reportSuppressCount) {
		this.reportSuppressCount = reportSuppressCount;
	}

	public int getRssiHoldCount() {
		return rssiHoldCount;
	}

	public void setRssiHoldCount(int rssiHoldCount) {
		this.rssiHoldCount = rssiHoldCount;
	}
	
	@Override
	public LocationServer clone() {
		try {
			return (LocationServer) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
	@Transient
	private String ipInputValue;

	public String getIpInputValue() {
		return ipInputValue;
	}

	public void setIpInputValue(String ipInputValue) {
		this.ipInputValue = ipInputValue;
	}

	public int getEkahauPort() {
		return ekahauPort;
	}

	public void setEkahauPort(int ekahauPort) {
		this.ekahauPort = ekahauPort;
	}

//	public String getEkahauIpAddress() {
//		return ekahauIpAddress;
//	}
//
//	public void setEkahauIpAddress(String ekahauIpAddress) {
//		this.ekahauIpAddress = ekahauIpAddress;
//	}
//
//	public String getEkahauDomain() {
//		return ekahauDomain;
//	}
//
//	public void setEkahauDomain(String ekahauDomain) {
//		this.ekahauDomain = ekahauDomain;
//	}
//
//	public byte getEkahauServerType() {
//		return ekahauServerType;
//	}

//	public void setEkahauServerType(byte ekahauServerType) {
//		this.ekahauServerType = ekahauServerType;
//	}

	public String getEkahauMac() {
		return ekahauMac;
	}

	public void setEkahauMac(String ekahauMac) {
		this.ekahauMac = ekahauMac;
	}
	
	public String getServerIpName4Show(){
		if (enableServer){
			if (serviceType==SERVICETYPE_AEROHIVE){
				return " ";
			} else {
				return serverIP.getAddressName();
//			} else if (serviceType==SERVICETYPE_AEROSCOUT){
//				return serverIP.getAddressName();
//			} else {
//				if (ekahauServerType==EKAHAU_SERVERTYPE_IP){
//					return ekahauIpAddress;
//				} else {
//					return ekahauDomain;
//				}
			}
		}
		return " ";
	}

	public int getEkahauTagThreshold() {
		return ekahauTagThreshold;
	}

	public void setEkahauTagThreshold(int ekahauTagThreshold) {
		this.ekahauTagThreshold = ekahauTagThreshold;
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