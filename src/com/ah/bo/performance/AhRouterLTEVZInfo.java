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
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_ROUTER_LTE_VZ_INFO")
@org.hibernate.annotations.Table(appliesTo = "HM_ROUTER_LTE_VZ_INFO", indexes = {
		@Index(name = "idx_router_lte_vz_info_owner", columnNames = { "OWNER" }),
		@Index(name = "idx_router_lte_vz_info_mac", columnNames = { "MAC" }) })
public class AhRouterLTEVZInfo implements HmBo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 12, nullable = false)
	private String mac;

	private byte networkMode;

	private byte connectStatus;

	private short rssi;

	private short rsrq;

	private short rsrp;

	private byte bars;

	private byte modemFlag;

	private String interfaceName;

	private String firmwareVersion;

	private String manufacture;

	private String hardwareID;

	private String simIccid;

	private String imei;

	private String carrier;

	private String cellID;

	private String systemMode;

	private String simStatus;

	private String modemMode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		// TODO Auto-generated method stub
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		// TODO Auto-generated method stub
		this.owner = owner;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub

	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public byte getNetworkMode() {
		return networkMode;
	}

	public void setNetworkMode(byte networkMode) {
		this.networkMode = networkMode;
	}

	public byte getConnectStatus() {
		return connectStatus;
	}

	public void setConnectStatus(byte connectStatus) {
		this.connectStatus = connectStatus;
	}

	public short getRssi() {
		return rssi;
	}
	
	@Transient
	public int getShowRssiValue() {
		return -rssi;
	}

	public void setRssi(short rssi) {
		this.rssi = rssi;
	}

	public short getRsrq() {
		return rsrq;
	}

	public void setRsrq(short rsrq) {
		this.rsrq = rsrq;
	}

	public short getRsrp() {
		return rsrp;
	}

	public void setRsrp(short rsrp) {
		this.rsrp = rsrp;
	}

	public byte getBars() {
		return bars;
	}

	public void setBars(byte bars) {
		this.bars = bars;
	}

	public byte getModemFlag() {
		return modemFlag;
	}

	public void setModemFlag(byte modemFlag) {
		this.modemFlag = modemFlag;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getManufacture() {
		return manufacture;
	}

	public void setManufacture(String manufacture) {
		this.manufacture = manufacture;
	}

	public String getHardwareID() {
		return hardwareID;
	}

	public void setHardwareID(String hardwareID) {
		this.hardwareID = hardwareID;
	}

	public String getSimIccid() {
		return simIccid;
	}

	public void setSimIccid(String simIccid) {
		this.simIccid = simIccid;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCellID() {
		return cellID;
	}

	public void setCellID(String cellID) {
		this.cellID = cellID;
	}

	public String getSystemMode() {
		if(systemMode.trim().equalsIgnoreCase("lte")){
			return "4G LTE";
		}
		return systemMode;
	}

	public void setSystemMode(String systemMode) {
		this.systemMode = systemMode;
	}

	public String getSimStatus() {
		simStatus = simStatus.toLowerCase().trim();
		if ("uninitialized".equals(simStatus)) {
			simStatus = "Not Activated";
		} else if ("ready".equals(simStatus)) {
			simStatus = "Activated";
		}else if("failed".equals(simStatus)){
			simStatus="Failed";
		}else if("locked".equals(simStatus)){
			simStatus="Locked";
		}
		return simStatus;
	}

	public void setSimStatus(String simStatus) {
		this.simStatus = simStatus;
	}

	public String getModemMode() {
		return modemMode;
	}

	public void setModemMode(String modemMode) {
		this.modemMode = modemMode;
	}

	@Transient
	public String getNetworkModeType() {
		if(this.networkMode==0){
			return "";
		}
		return MgrUtil.getEnumString("enum.usb.medem.cellular.mode."
				+ this.networkMode);
	}

	@Transient
	public String getBarsStatus() {
		String barsImage = "";
		switch (bars) {
		case 1:
			barsImage = LTE_VZ_MODEM_BARS1_image;
			break;
		case 2:
			barsImage = LTE_VZ_MODEM_BARS2_image;
			break;
		case 3:
			barsImage = LTE_VZ_MODEM_BARS3_image;
			break;
		case 4:
			barsImage = LTE_VZ_MODEM_BARS4_image;
			break;
		default:
			barsImage = LTE_VZ_MODEM_BARS0_image;
		}
		return barsImage;
	}

	public static final byte LTE_VZ_NETWORK_MODE_AUTO = 1;
	public static final byte LTE_VZ_NETWORK_MODE_2G = 2;
	public static final byte LTE_VZ_NETWORK_MODE_3G = 3;
	public static final byte LTE_VZ_NETWORK_MODE_4G = 4;
	public static final byte LTE_VZ_CONNECT_STATUS_DISCONNECT = 0;
	public static final byte LTE_VZ_CONNECT_STATUS_CONNECT = 1;
	public static final byte LTE_VZ_MODEM_EMBEDDED = 0;
	public static final byte LTE_VZ_MODEM_EXTERNAL = 1;

	private static final String LTE_VZ_MODEM_BARS0_image = "/images/monitor/bars_0.png";
	private static final String LTE_VZ_MODEM_BARS1_image = "/images/monitor/bars_1.png";
	private static final String LTE_VZ_MODEM_BARS2_image = "/images/monitor/bars_2.png";
	private static final String LTE_VZ_MODEM_BARS3_image = "/images/monitor/bars_3.png";
	private static final String LTE_VZ_MODEM_BARS4_image = "/images/monitor/bars_4.png";

}
