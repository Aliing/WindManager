package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.be.cloudauth.IDMConfig;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "DOWNLOAD_INFO")
@org.hibernate.annotations.Table(appliesTo = "DOWNLOAD_INFO", indexes = {
		@Index(name = "downloadInfo_device_mac", columnNames = { "macAddress" })
		})
public class DownloadInfo implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length = 12, nullable = false)
	private String macAddress;

	private boolean view;

	/*** NmsUtil.isEcwpDefault(); */
	private boolean ecwpDefault;

	/*** NmsUtil.isEcwpDepaul(); */
	private boolean ecwpDepaul;

	/*** NmsUtil.isEcwpNnu(); */
	private boolean ecwpNnu;

	/*** * NmsUtil.isHostedHMApplication(); */
	private boolean HHMApp;

	/*** HmBeOsUtil.getHiveManagerIPAddr(); */
	private String hmIpAddress;
	
	/** NmsUtil.isVhmEnableIdm(hiveAp.getOwner().getId()) */
	private boolean enableIdm;

	/***
	 * new
	 * HmCloudAuthCertMgmtImpl().getRadSecConfig(this.hiveAp.getOwner().getId
	 * ());
	 */
	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "idmGatewayServer", column = @Column(name = "CLOUD_AUTH_SERVER")),
			@AttributeOverride(name = "idmCertAPI", column = @Column(name = "CLOUD_AUTH_CERT_SERVER")),
			@AttributeOverride(name = "idmCustomerAPI", column = @Column(name = "CLOUD_AUTH_WEB_SERVER")),
			@AttributeOverride(name = "idmRadSecTLSPort", column = @Column(name = "TLS_PORT")),
			@AttributeOverride(name = "idmSelfRegDeviceGuestAPI", column = @Column(name = "SELF_REG_API")),
			@AttributeOverride(name = "idmSelfRegDeviceCRL", column = @Column(name = "SELF_REG_CRL"))
	})
	private IDMConfig idmRadSecConfig;
	
	/** NmsUtil.isHMForOEM() */
	private boolean oemHm = false;
	
	/** HmBeOsUtil.getTimeZoneOffSet(MgmtServiceTime.timeZoneStr); */
	private String timeZoneOffSet;
	
	/** HmBeOsUtil.getTimeZoneWholeStr(MgmtServiceTime.timeZoneStr); */
	private String timeZoneString;
	
	/** 
	 * dayLightTime = new AhDayLightSavingUtil(hiveAp.getDownloadInfo().getTimeZoneString());
			return dayLightTime.isUseDayLightSaving();
	 * */
	private boolean useDayLightSaving;
	
	/**dayLightTime.getStartDLSdate() + " "
				+ dayLightTime.getStartDLStime() + " "
				+ dayLightTime.getEndDLSdate() + " "
				+ dayLightTime.getEndDLStime();*/
	private String dayLightTime;
	
	/** ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, ConfigUtil.KEY_URL_ROOT_PATH) */
	private String mdmURLPath;
	
	/** getOwner().getInstanceId(); */
	private String vhmInstanceId;
	
	/** use for cloud cwp start ***************************************************************/
	private String rootURL;
	private String customerId;
	private String APIKey;
	private String APINonce;
	private int serviceId;
	/** use for cloud cwp end ****************************************************************/
	
	public static final short DS_DOWNLOAD_RESULT_SUCCESS = 0;
	private short errorCode = DS_DOWNLOAD_RESULT_SUCCESS;
	
	@Column(length=409600)
	private String errorMessage;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@Version
	private Timestamp version;
	
	/** Transient column start */
	@Transient
	private String runningConfig = "";
	
	@Transient
	private String runningUsers = "";
	
	@Transient
	private boolean fromap=true;
	/** Transient column end */

	public boolean isView() {
		return view;
	}

	public void setView(boolean view) {
		this.view = view;
	}

	public boolean isEcwpDefault() {
		return ecwpDefault;
	}

	public void setEcwpDefault(boolean ecwpDefault) {
		this.ecwpDefault = ecwpDefault;
	}

	public boolean isEcwpDepaul() {
		return ecwpDepaul;
	}

	public void setEcwpDepaul(boolean ecwpDepaul) {
		this.ecwpDepaul = ecwpDepaul;
	}

	public boolean isEcwpNnu() {
		return ecwpNnu;
	}

	public void setEcwpNnu(boolean ecwpNnu) {
		this.ecwpNnu = ecwpNnu;
	}

	public boolean isHHMApp() {
		return HHMApp;
	}

	public void setHHMApp(boolean hHMApp) {
		HHMApp = hHMApp;
	}

	public String getRunningConfig() {
		return runningConfig;
	}

	private boolean isExistsSnmpCli(String clis) {
		String regex = "(snmp) (reader) (version) (any|v1|v2c|v3) (community)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(clis);
		return matcher.find();
	}

	public void setRunningConfig(String runningConfig) {
		this.runningConfig = runningConfig;
		if(this.runningConfig != null){
			// Fix the SNMP issue in the HM3.5r4 release.
			if (this.runningConfig
					.contains("no snmp reader version any community hivecommunity")) {
				this.runningConfig = runningConfig.replace(
						"no snmp reader version any community hivecommunity", "");
			} else if (isExistsSnmpCli(runningConfig)) {
				this.runningConfig += this.runningConfig.endsWith("\n") ? "snmp reader version any community hivecommunity\n"
						: "\nsnmp reader version any community hivecommunity\n";
			}
		}
	}

	public String getRunningUsers() {
		return runningUsers;
	}

	public void setRunningUsers(String runningUsers) {
		this.runningUsers = runningUsers;
	}

	public String getHmIpAddress() {
		return hmIpAddress;
	}

	public void setHmIpAddress(String hmIpAddress) {
		this.hmIpAddress = hmIpAddress;
	}

	public IDMConfig getIdmRadSecConfig() {
		return idmRadSecConfig;
	}

	public void setIdmRadSecConfig(IDMConfig idmRadSecConfig) {
		this.idmRadSecConfig = idmRadSecConfig;
	}
	
	public boolean isFromap() {
		return fromap;
	}

	public void setFromap(boolean fromap) {
		this.fromap = fromap;
	}

	public boolean isEnableIdm() {
		return enableIdm;
	}

	public void setEnableIdm(boolean enableIdm) {
		this.enableIdm = enableIdm;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public boolean isOemHm() {
		return oemHm;
	}

	public void setOemHm(boolean oemHm) {
		this.oemHm = oemHm;
	}

	public String getTimeZoneOffSet() {
		return timeZoneOffSet;
	}

	public void setTimeZoneOffSet(String timeZoneOffSet) {
		this.timeZoneOffSet = timeZoneOffSet;
	}

	public String getTimeZoneString() {
		return timeZoneString;
	}

	public void setTimeZoneString(String timeZoneString) {
		this.timeZoneString = timeZoneString;
	}

	public boolean isUseDayLightSaving() {
		return useDayLightSaving;
	}

	public void setUseDayLightSaving(boolean useDayLightSaving) {
		this.useDayLightSaving = useDayLightSaving;
	}

	public String getDayLightTime() {
		return dayLightTime;
	}

	public void setDayLightTime(String dayLightTime) {
		this.dayLightTime = dayLightTime;
	}

	public short getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(short errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getMdmURLPath() {
		return mdmURLPath;
	}

	public void setMdmURLPath(String mdmURLPath) {
		this.mdmURLPath = mdmURLPath;
	}

	public String getVhmInstanceId() {
		return vhmInstanceId;
	}

	public void setVhmInstanceId(String vhmInstanceId) {
		this.vhmInstanceId = vhmInstanceId;
	}

	public String getRootURL() {
		return rootURL;
	}

	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAPIKey() {
		return APIKey;
	}

	public void setAPIKey(String aPIKey) {
		APIKey = aPIKey;
	}

	public String getAPINonce() {
		return APINonce;
	}

	public void setAPINonce(String aPINonce) {
		APINonce = aPINonce;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
}
