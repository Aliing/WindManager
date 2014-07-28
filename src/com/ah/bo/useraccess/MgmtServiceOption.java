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
import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.wlan.Cwp;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * modification history
 * 
 * add 'disable console port' and 'temperature alarm threshold'
 * joseph chen, 04/08/2008
 * 
 * add 'enable smart poe'
 * joseph chen, 05/26/2008
 * 
 * add 'disable trap over capwap'
 * jospeh chen, 07/18/2008
 * 
 * add ICSA Log and Drop
 * Fiona Feng, 09/11/2008
 * 
 * add system led
 * Fiona Feng, 02/10/2009
 * 
 * add radius auth type
 * Fiona Feng, 06/03/2009
 * 
 * add ppsk auto save gap interval
 * zhang jie, 09/03/2011
 * 
 * add os detection 
 * liang wenping,03/08/2012
 * 
 */

@Entity
@Table(name = "MGMT_SERVICE_OPTION")
@org.hibernate.annotations.Table(appliesTo = "MGMT_SERVICE_OPTION", indexes = {
		@Index(name = "MGMT_SERVICE_OPTION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class MgmtServiceOption implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String mgmtName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	private boolean disableResetButton;

	private boolean disableProxyArp;

	private boolean disableSsid;

	private boolean disableConsolePort;
	
	private boolean disableCallAdmissionControl = true;
	
	private boolean enableSmartPoe = true;
	
	private boolean enablePCIData;
	
	private short macAuthDelimiter = RadiusAssignment.RADIUS_MACAUTHDELIMITER_COLON;
	
	private short macAuthStyle = RadiusAssignment.RADIUS_MACAUTHSTYLE_NO;
	
	private short macAuthCase = RadiusAssignment.RADIUS_MACAUTHCASE_LOWER;
	
	private short userAuth = EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL;
	
	private boolean enableForwardMaxMac;
	
	private boolean enableForwardMaxIp;
	
	private boolean logDroppedPackets;
	
	private boolean logFirstPackets;
	
	private boolean dropFragmentedIpPackets;
	
	private boolean dropNonMgtTraffic;
	
	public static final short MULTICAST_BLOCK = 1;
	public static final short MULTICAST_ALLOW = 2;
	
	private short multicastselect=MULTICAST_ALLOW;
	
	public void setMulticastselect(short multicastselect) {
		this.multicastselect = multicastselect;
	}

	public short getMulticastselect() {
		return multicastselect;
	}
	
	public static final short DEFAULT_FORWARD_MAX_MAC = 0;
	
	public static final short DEFAULT_FORWARD_MAX_IP = 0;
	
	// it will be 0 if disableForwardMaxMac == false
	@Range(min = 0, max = 8000)
	private short forwardMaxMac = DEFAULT_FORWARD_MAX_MAC;
	
	// it will be 0 if disableForwardMaxIp == false
	@Range(min = 0, max = 8000)
	private short forwardMaxIp = DEFAULT_FORWARD_MAX_IP;

	@Range(min = 50, max = 80)
	private short tempAlarmThreshold = 75; // unit: celsius
	
	@Range(min = 1000, max = 3000)
	private short fansUnderSpeedAlarmThreshold = 2000;

	public static final short DEFAULT_AIRTIME_PER_SECOND = 500;
	
	@Range(min = 100, max = 1000)
	private short airtimePerSecond = DEFAULT_AIRTIME_PER_SECOND;

	public static final byte DEFAULT_ROAMING_GUARANTEED_AIRTIME = 20;
	
	@Range(min = 0, max = 100)
	private short roamingGuaranteedAirtime = DEFAULT_ROAMING_GUARANTEED_AIRTIME;
	
	// add radius auth type from 3.4r1
	private int radiusAuthType = Cwp.AUTH_METHOD_PAP;

	// add radius server from 3.2r1
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RADIUS_SERVICE_ASSIGN_ID")
	private RadiusAssignment radiusServer;
	
	// add system led brightness from 3.2r2
	public static final short SYSTEM_LED_BRIGHT = 1;
	
	public static final short SYSTEM_LED_SOFT = 2;
	
	public static final short SYSTEM_LED_DIM = 3;
	
	public static final short SYSTEM_LED_OFF = 4;
	
	public static EnumItem[] ENUM_SYSTEM_LED_BRIGHTNESS = MgrUtil.enumItems(
			"enum.system.led.brightness.", new int[] { SYSTEM_LED_BRIGHT,
					SYSTEM_LED_SOFT, SYSTEM_LED_DIM, SYSTEM_LED_OFF });
	
	private short systemLedBrightness = SYSTEM_LED_BRIGHT;
	
	private boolean enableTcpMss = true;
	
	private int tcpMssThreshold;
	
	private boolean enableIcmpRedirect;
	
	private int ppskAutoSaveInt = 600;

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "mgmtName", "description",
				"disableResetButton", "disableProxyArp", "disableSsid",
				"disableConsolePort", "userAuth", "radius_service_assign_id",
				"tempAlarmThreshold", "enableSmartPoe", "macAuthDelimiter",
				"macAuthStyle", "macAuthCase", "disableCallAdmissionControl",
				"enableForwardMaxMac", "enableForwardMaxIp", "forwardMaxMac",
				"forwardMaxIp", "airtimePerSecond", "roamingGuaranteedAirtime",
				"logDroppedPackets", "logFirstPackets",
				"dropFragmentedIpPackets", "dropNonMgtTraffic",
				"systemLedBrightness", "radiusAuthType", "enablePCIData",
				"enableTcpMss", "tcpMssThreshold", "enableIcmpRedirect",
				"enablePMTUD","monitorMSS","thresholdForAllTCP","thresholdThroughVPNTunnel", 
				"owner", "ppskAutoSaveInt","multicastselect","enableOsdetection","osDetectionMethod",
				"enableSyncVlanId","fansUnderSpeedAlarmThreshold","enableCaptureDataByCWP"};
	}

	public String getMgmtName() {
		return mgmtName;
	}

	public void setMgmtName(String mgmtName) {
		this.mgmtName = mgmtName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getDisableResetButton() {
		return disableResetButton;
	}

	public void setDisableResetButton(boolean disableResetButton) {
		this.disableResetButton = disableResetButton;
	}

	public boolean getDisableProxyArp() {
		return disableProxyArp;
	}

	public void setDisableProxyArp(boolean disableProxyArp) {
		this.disableProxyArp = disableProxyArp;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return mgmtName;
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

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MULTICAST_FORWARDING", joinColumns = @JoinColumn(name = "MGMT_SERVICE_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<MulticastForwarding> multipleVlan = new ArrayList<MulticastForwarding>();

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
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean getDisableSsid() {
		return disableSsid;
	}

	public void setDisableSsid(boolean disableSsid) {
		this.disableSsid = disableSsid;
	}

	public boolean getDisableConsolePort() {
		return disableConsolePort;
	}

	public void setDisableConsolePort(boolean disableConsolePort) {
		this.disableConsolePort = disableConsolePort;
	}
	
	public boolean getDisableCallAdmissionControl() {
		return this.disableCallAdmissionControl;
	}
	
	public void setDisableCallAdmissionControl(boolean disableCallAdmissionControl) {
		this.disableCallAdmissionControl = disableCallAdmissionControl;
	}
	
	public boolean getEnableSmartPoe() {
		return enableSmartPoe;
	}
	
	public void setEnableSmartPoe(boolean enableSmartPoe) {
		this.enableSmartPoe = enableSmartPoe;
	}
	
	@Transient
	public String getDisableProxyArpValue() {
		return disableProxyArp ? "Disabled" : "Enabled";
	}

	@Transient
	public String getDisableResetButtonValue() {
		return disableResetButton ? "Disabled" : "Enabled";
	}

	@Transient
	public String getDisableSsidValue() {
		return disableSsid ? "Disabled" : "Enabled";
	}

	@Transient
	public String getDisableViaRadiusValue() {
		if (this.userAuth >= 0)
			return MgrUtil.getEnumString("enum.userAuth.type." + userAuth);
		return "";
	}

	@Transient
	public String getDisableConsolePortValue() {
		return disableConsolePort ? "Disabled" : "Enabled";
	}
	
	@Transient
	public String getDisableCallAdmissionControlValue() {
		return disableCallAdmissionControl ? "Disabled" : "Enabled";
	}
	
	@Transient
	public String getEnableSmartPoeValue() {
		return enableSmartPoe ? "Enabled" : "Disabled";
	}
	
	@Transient
	public String getSystemLed() {
		return MgrUtil.getEnumString("enum.system.led.brightness." + systemLedBrightness);
	}
	
	public short getUserAuth() {
		return userAuth;
	}

	public void setUserAuth(short userAuth) {
		this.userAuth = userAuth;
	}
	
	public short getTempAlarmThreshold() {
		return tempAlarmThreshold;
	}
	
	public void setTempAlarmThreshold(short tempAlarmThreshold) {
		this.tempAlarmThreshold = tempAlarmThreshold;
	}

	public short getMacAuthDelimiter()
	{
		return macAuthDelimiter;
	}

	public void setMacAuthDelimiter(short macAuthDelimiter)
	{
		this.macAuthDelimiter = macAuthDelimiter;
	}

	public short getMacAuthStyle()
	{
		return macAuthStyle;
	}

	public void setMacAuthStyle(short macAuthStyle)
	{
		this.macAuthStyle = macAuthStyle;
	}

	public short getMacAuthCase()
	{
		return macAuthCase;
	}

	public void setMacAuthCase(short macAuthCase)
	{
		this.macAuthCase = macAuthCase;
	}

	/**
	 * getter of forwardMacMac
	 * @return the forwardMacMac
	 */
	public short getForwardMaxMac() {
		return forwardMaxMac;
	}

	/**
	 * setter of forwardMacMac
	 * @param forwardMacMac the forwardMacMac to set
	 */
	public void setForwardMaxMac(short forwardMacMac) {
		this.forwardMaxMac = forwardMacMac;
	}

	/**
	 * getter of forwardMacIp
	 * @return the forwardMacIp
	 */
	public short getForwardMaxIp() {
		return forwardMaxIp;
	}

	/**
	 * setter of forwardMacIp
	 * @param forwardMacIp the forwardMacIp to set
	 */
	public void setForwardMaxIp(short forwardMacIp) {
		this.forwardMaxIp = forwardMacIp;
	}

	/**
	 * getter of airtimePerSecond
	 * @return the airtimePerSecond
	 */
	public short getAirtimePerSecond() {
		return airtimePerSecond;
	}

	/**
	 * setter of airtimePerSecond
	 * @param airtimePerSecond the airtimePerSecond to set
	 */
	public void setAirtimePerSecond(short airtimePerSecond) {
		this.airtimePerSecond = airtimePerSecond;
	}

	/**
	 * getter of roamingGuaranteedAirtime
	 * @return the roamingGuaranteedAirtime
	 */
	public short getRoamingGuaranteedAirtime() {
		return roamingGuaranteedAirtime;
	}

	/**
	 * setter of roamingGuaranteedAirtime
	 * @param roamingGuaranteedAirtime the roamingGuaranteedAirtime to set
	 */
	public void setRoamingGuaranteedAirtime(short roamingGuaranteedAirtime) {
		this.roamingGuaranteedAirtime = roamingGuaranteedAirtime;
	}

	public boolean isLogDroppedPackets()
	{
		return logDroppedPackets;
	}

	public void setLogDroppedPackets(boolean logDroppedPackets)
	{
		this.logDroppedPackets = logDroppedPackets;
	}

	public boolean isLogFirstPackets()
	{
		return logFirstPackets;
	}

	public void setLogFirstPackets(boolean logFirstPackets)
	{
		this.logFirstPackets = logFirstPackets;
	}

	public boolean isDropFragmentedIpPackets()
	{
		return dropFragmentedIpPackets;
	}

	public void setDropFragmentedIpPackets(boolean dropFragmentedIpPackets)
	{
		this.dropFragmentedIpPackets = dropFragmentedIpPackets;
	}

	public boolean isDropNonMgtTraffic()
	{
		return dropNonMgtTraffic;
	}

	public void setDropNonMgtTraffic(boolean dropNonMgtTraffic)
	{
		this.dropNonMgtTraffic = dropNonMgtTraffic;
	}

	public boolean isEnableForwardMaxMac()
	{
		return enableForwardMaxMac;
	}

	public void setEnableForwardMaxMac(boolean enableForwardMaxMac)
	{
		this.enableForwardMaxMac = enableForwardMaxMac;
	}

	public boolean isEnableForwardMaxIp()
	{
		return enableForwardMaxIp;
	}

	public void setEnableForwardMaxIp(boolean enableForwardMaxIp)
	{
		this.enableForwardMaxIp = enableForwardMaxIp;
	}

	public RadiusAssignment getRadiusServer()
	{
		return radiusServer;
	}

	public void setRadiusServer(RadiusAssignment radiusServer)
	{
		this.radiusServer = radiusServer;
	}

	public short getSystemLedBrightness() {
		return systemLedBrightness;
	}

	public void setSystemLedBrightness(short systemLedBrightness) {
		this.systemLedBrightness = systemLedBrightness;
	}

	public boolean isEnableTcpMss() {
		return enableTcpMss;
	}

	public void setEnableTcpMss(boolean enableTcpMss) {
		this.enableTcpMss = enableTcpMss;
	}

	public int getTcpMssThreshold() {
		return tcpMssThreshold;
	}

	public void setTcpMssThreshold(int tcpMssThreshold) {
		this.tcpMssThreshold = tcpMssThreshold;
	}

	public boolean isEnableIcmpRedirect() {
		return enableIcmpRedirect;
	}

	public void setEnableIcmpRedirect(boolean enableIcmpRedirect) {
		this.enableIcmpRedirect = enableIcmpRedirect;
	}

	public int getRadiusAuthType() {
		return radiusAuthType;
	}

	public void setRadiusAuthType(int radiusAuthType) {
		this.radiusAuthType = radiusAuthType;
	}
	
	@Override
	public MgmtServiceOption clone() {
		try {
			return (MgmtServiceOption) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean isEnablePCIData() {
		return enablePCIData;
	}

	public void setEnablePCIData(boolean enablePCIData) {
		this.enablePCIData = enablePCIData;
	}
	
	public int getPpskAutoSaveInt(){
		return this.ppskAutoSaveInt;
	}
	
	public void setPpskAutoSaveInt(int ppskAutoSaveInt){
		this.ppskAutoSaveInt = ppskAutoSaveInt;
	}

	public void setMultipleVlan(List<MulticastForwarding> multipleVlan) {
		this.multipleVlan = multipleVlan;
	}

	public List<MulticastForwarding> getMultipleVlan() {
		return multipleVlan;
	}
	
	//added for PMTUD start
	private boolean enablePMTUD = true;
	
	private boolean monitorMSS = true;
	
	private int thresholdForAllTCP = 0;
	
	private int thresholdThroughVPNTunnel = 0;
	
	//added for PMTUD end
	
	public boolean isEnablePMTUD() {
		return enablePMTUD;
	}

	public void setEnablePMTUD(boolean enablePMTUD) {
		this.enablePMTUD = enablePMTUD;
	}

	public boolean isMonitorMSS() {
		return monitorMSS;
	}

	public void setMonitorMSS(boolean monitorMSS) {
		this.monitorMSS = monitorMSS;
	}

	public int getThresholdForAllTCP() {
		return thresholdForAllTCP;
	}

	public void setThresholdForAllTCP(int thresholdForAllTCP) {
		this.thresholdForAllTCP = thresholdForAllTCP;
	}

	public int getThresholdThroughVPNTunnel() {
		return thresholdThroughVPNTunnel;
	}

	public void setThresholdThroughVPNTunnel(int thresholdThroughVPNTunnel) {
		this.thresholdThroughVPNTunnel = thresholdThroughVPNTunnel;
	}

	public short getFansUnderSpeedAlarmThreshold() {
		return fansUnderSpeedAlarmThreshold;
	}

	public void setFansUnderSpeedAlarmThreshold(short fansUnderSpeedAlarmThreshold) {
		this.fansUnderSpeedAlarmThreshold = fansUnderSpeedAlarmThreshold;
	}

	// add OS detection start
	private boolean enableOsdetection = true;
	public static final short OS_DETECTION_METHOD_DHCP = 1;
	public static final short OS_DETECTION_METHOD_HTTP = 2;
	public static final short OS_DETECTION_METHOD_BOTH = 3;
	private short osDetectionMethod = OS_DETECTION_METHOD_DHCP;

	public boolean isEnableOsdetection() {
		return enableOsdetection;
	}

	public void setEnableOsdetection(boolean enableOsdetection) {
		this.enableOsdetection = enableOsdetection;
	}

	public short getOsDetectionMethod() {
		return osDetectionMethod;
	}

	public void setOsDetectionMethod(short osDetectionMethod) {
		this.osDetectionMethod = osDetectionMethod;
	}
	// add OS detection end
	
	// Enable Sync VLAN id in MAC session from original AP to remote AP
	private boolean enableSyncVlanId = false;

	public boolean isEnableSyncVlanId() {
		return enableSyncVlanId;
	}

	public void setEnableSyncVlanId(boolean enableSyncVlanId) {
		this.enableSyncVlanId = enableSyncVlanId;
	}
	// Enable Sync VLAN id in MAC session from original AP to remote AP
	// Enable Capture data input within CWP
	private boolean enableCaptureDataByCWP = false;

	public boolean isEnableCaptureDataByCWP() {
		return enableCaptureDataByCWP;
	}

	public void setEnableCaptureDataByCWP(boolean enableCaptureDataByCWP) {
		this.enableCaptureDataByCWP = enableCaptureDataByCWP;
	}
	// Enable Capture data input within CWP
}