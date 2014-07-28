package com.ah.be.communication.mo;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.devices.impl.Device;

public class SimulateHiveAP {

	private short	simulateNumber;

	private String	macAddress		= NmsUtil.getOEMCustomer().getMACOUI()[0];

	private short	code;

	private String	ipAddress		= "0.0.0.0";

	private String	netmask			= "0.0.0.0";

	private String	gateway			= "0.0.0.0";

	private int		countryCode;

	private int		regionCode;

	private byte	ipType			= -1;

	private byte	apType			= -1;

	private String	wtpName			= "";

	private String	sn				= "";

	private String	softVer			= "";

	private String	displaySoftVer	= NmsUtil.getOEMCustomer().getAccessPointOS();

	private String	location		= "";

	private String	productName		= "";

	private String	vhmName			= "";

	private String	clientInfo		= "";

	private short	apModel;

	private byte	timezone;

	private short	wifi0Channel;

	private short	wifi0Power;

	private short	wifi1Channel;

	private short	wifi1Power;

	public SimulateHiveAP() {
	}

	public SimulateHiveAP(short simulateNumber,byte	timezone,int countryCode,
			short apModel,String vhmName,String location,short wifi0Channel,
			short wifi0Power,short wifi1Channel,short wifi1Power){
		this.simulateNumber = simulateNumber;
		this.timezone = (byte)(timezone + 13);
		this.countryCode = countryCode;
		this.apModel = apModel;
		this.productName = getFullProductName(apModel);
		this.vhmName = vhmName;
		this.location = location;
		this.wifi0Channel = wifi0Channel;
		this.wifi0Power = wifi0Power;
		this.wifi1Channel = wifi1Channel;
		this.wifi1Power = wifi1Power;
	}

	public byte getTimezone() {
		return timezone;
	}

	public void setTimezone(byte timezone) {
		this.timezone = timezone;
	}

	public short getWifi0Channel() {
		return wifi0Channel;
	}

	public void setWifi0Channel(short wifi0Channel) {
		this.wifi0Channel = wifi0Channel;
	}

	public short getWifi0Power() {
		return wifi0Power;
	}

	public void setWifi0Power(short wifi0Power) {
		this.wifi0Power = wifi0Power;
	}

	public short getWifi1Channel() {
		return wifi1Channel;
	}

	public void setWifi1Channel(short wifi1Channel) {
		this.wifi1Channel = wifi1Channel;
	}

	public short getWifi1Power() {
		return wifi1Power;
	}

	public void setWifi1Power(short wifi1Power) {
		this.wifi1Power = wifi1Power;
	}

	public byte getApType() {
		return apType;
	}

	public void setApType(byte apType) {
		this.apType = apType;
	}

	public String getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}

	public short getCode() {
		return code;
	}

	public void setCode(short code) {
		this.code = code;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public String getDisplaySoftVer() {
		return displaySoftVer;
	}

	public void setDisplaySoftVer(String displaySoftVer) {
//		this.displaySoftVer = displaySoftVer;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public byte getIpType() {
		return ipType;
	}

	public void setIpType(byte ipType) {
		this.ipType = ipType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(int regionCode) {
		this.regionCode = regionCode;
	}

	public short getSimulateNumber() {
		return simulateNumber;
	}

	public void setSimulateNumber(short simulateNumber) {
		this.simulateNumber = simulateNumber;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getSoftVer() {
		return softVer;
	}

	public void setSoftVer(String softVer) {
		this.softVer = softVer;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public String getWtpName() {
		return wtpName;
	}

	public void setWtpName(String wtpName) {
		this.wtpName = wtpName;
	}

	public short getDataLength() {
		String[] versions = AhConstantUtil.getEnumValues(Device.SUPPORTED_HIVEOS_VERSIONS, apModel);
		if (versions != null && versions.length > 0) {
			softVer = versions[0];
		} else {
			BeVersionInfo version = ((BeVersionInfo)BaseAction.getSessionVersionInfo());
			softVer = version.getMainVersion() + "." + version.getSubVersion() + ".0";
		}
		if (displaySoftVer.indexOf("Release") < 0) {
			displaySoftVer += " "
					+ MgrUtil.getHiveOSDisplayVersion(softVer)
					+ " Release";
		}

		short len = 0;
		len += 2; // simulate number
		len += 1; // mac len
		len += macAddress != null ? macAddress.length() : 0;
		len += 2; // code
		len += 4; // ip address
		len += 4; // net mask
		len += 4; // gateway
		len += 4; // country code
		len += 4; // region code
		len += 1; // ip type
		len += 1; // ap type
		len += 1; // wtp name len
		len += wtpName != null ? wtpName.length() : 0;
		len += 1; // sn len
		len += sn != null ? sn.length() : 0;
		len += 1; // software version len
		len += softVer != null ? softVer.length() : 0;
		len += 1; // display software version len
		len += displaySoftVer != null ? displaySoftVer.length() : 0;
		len += 2; // location len
		len += location != null ? location.length() : 0;
		len += 1; // product name len
		len += productName != null ? productName.length() : 0;
		len += 1; // vhm name len
		len += vhmName != null ? vhmName.length() : 0;
		len += 2; // client info len
		len += clientInfo != null ? clientInfo.length() : 0;
		len += 2; // ap model
		len += 1; // time zone
		len += 2; // wifi0 channel
		len += 2; // wifi0 power
		len += 2; // wifi1 channel
		len += 2; // wifi1 power

		return len;
	}

	public short getApModel() {
		return apModel;
	}

	public void setApModel(short apModel) {
		this.apModel = apModel;
	}

	public static String getFullProductName(short hiveApModel) {
		return AhConstantUtil.getHiveApProductNameByModel(hiveApModel);
	}
}
