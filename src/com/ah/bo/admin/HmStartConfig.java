package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.useraccess.MgmtServiceOption;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_START_CONFIG")
public class HmStartConfig implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final String EXPRESS_MODE_DEFAULT_NTP_SERVER = "ntp1.aerohive.com";
	
	public static final String EXPRESS_MODE_DEFAULT_DNS_SERVER1 = "dns1.aerohive.com";
	
	public static final String EXPRESS_MODE_DEFAULT_DNS_SERVER2 = "dns2.aerohive.com";
	
	public static final String EXPRESS_MODE_DEFAULT_DNS_IP1 = "208.67.222.222";
	
	public static final String EXPRESS_MODE_DEFAULT_DNS_IP2 = "208.67.220.220";
	
	public static final String EXPRESS_MODE_DEFAULT_DNS_IP1_DEPRECATED = "168.143.87.77";
	
	public static final String EXPRESS_MODE_DEFAULT_DNS_IP2_DEPRECATED = "209.128.124.9";
	
	public static final String EXPRESS_MODE_DEFAULT_NTP_IP1 = "206.80.44.205";
	
	public static final String EXPRESS_MODE_DEFAULT_NTP_IP2 = "206.80.44.206";

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	public static final short HM_MODE_EASY = 1;
	public static final short HM_MODE_FULL = 2;
	public static EnumItem[] HM_MODE = MgrUtil.enumItems("enum.hm.start.mode.",
			new int[] { HM_MODE_EASY, HM_MODE_FULL });
	private short modeType = HM_MODE_EASY;

	private String hiveApPassword;

	private String networkName = NmsUtil.getOEMCustomer().getCompanyNameWithoutBlank();
	
	private boolean useAccessConsole;
	
	private short ledBrightness = MgmtServiceOption.SYSTEM_LED_BRIGHT;
	
	private boolean adminUserLogin;
	
	private boolean enableAutoDiscovery = true;
	
	public boolean isEnableAutoDiscovery() {
		return enableAutoDiscovery;
	}

	public void setEnableAutoDiscovery(boolean enableAutoDiscovery) {
		this.enableAutoDiscovery = enableAutoDiscovery;
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
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public short getModeType() {
		return modeType;
	}

	public void setModeType(short modeType) {
		this.modeType = modeType;
	}

	public String getHiveApPassword() {
		return hiveApPassword;
	}

	public void setHiveApPassword(String hiveApPassword) {
		this.hiveApPassword = hiveApPassword;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	@Override
	public String getLabel() {
		return NmsUtil.getOEMCustomer().getNmsNameAbbreviation() + 
			" Start Config";
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

	public boolean isUseAccessConsole()
	{
		return useAccessConsole;
	}

	public void setUseAccessConsole(boolean useAccessConsole)
	{
		this.useAccessConsole = useAccessConsole;
	}

	public short getLedBrightness()
	{
		return ledBrightness;
	}

	public void setLedBrightness(short ledBrightness)
	{
		this.ledBrightness = ledBrightness;
	}

	public boolean isAdminUserLogin() {
		return adminUserLogin;
	}

	public void setAdminUserLogin(boolean adminUserLogin) {
		this.adminUserLogin = adminUserLogin;
	}
	
	@Transient
	private String asciiKey;

	public String getAsciiKey()
	{
		return asciiKey;
	}

	public void setAsciiKey(String asciiKey)
	{
		this.asciiKey = asciiKey;
	}

	@Transient
	private String oldHiveApPassword;

	public String getOldHiveApPassword() {
		return oldHiveApPassword;
	}

	public void setOldHiveApPassword(String oldHiveApPassword) {
		this.oldHiveApPassword = oldHiveApPassword;
	}
	
	@Transient
	private String quickStartPwd;

	public String getQuickStartPwd()
	{
		return quickStartPwd;
	}

	public void setQuickStartPwd(String quickStartPwd)
	{
		this.quickStartPwd = quickStartPwd;
	}
	
	//when upgrade,the idm quick start networkpolicy create panel pop up just once
	private boolean idmPolicyCreatePanelPopUpFlag;

	public boolean isIdmPolicyCreatePanelPopUpFlag() {
		return idmPolicyCreatePanelPopUpFlag;
	}

	public void setIdmPolicyCreatePanelPopUpFlag(
			boolean idmPolicyCreatePanelPopUpFlag) {
		this.idmPolicyCreatePanelPopUpFlag = idmPolicyCreatePanelPopUpFlag;
	}
	
}