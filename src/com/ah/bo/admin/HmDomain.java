package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.LicenseOperationTool;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "HM_DOMAIN")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class HmDomain implements HmBo {

	private static final long	serialVersionUID		= 1L;

	private static final Tracer	log						= new Tracer(HmDomain.class.getSimpleName());

	public static final String	HOME_DOMAIN				= "home";

	public static final String	GLOBAL_DOMAIN			= "global";

	public static final String 	PRODUCT_ID_VHM 			= "HMOL-Aerohive";
	// the value of runstatus
	public static final int		DOMAIN_DEFAULT_STATUS	= 0;

	public static final int		DOMAIN_RESTORE_STATUS	= 1;

	public static final int		DOMAIN_DISABLE_STATUS	= 2;

	public static final int		DOMAIN_BACKUP_STATUS	= 3;

	public static final int		DOMAIN_UPDATE_STATUS	= 4;

	public static final int		DOMAIN_DELETING_STATUS	= 5;

	public static final int		DOMAIN_UNKNOWN_STATUS	= 100;

	@Id
	@GeneratedValue
	private Long				id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false, unique = true)
	private String				domainName;

	@Min(0)
	private int					maxApNum;

	public static final int MAX_SIMULATE_HIVEAP_DEFAULT = 10;

	public static final int MAX_SIMULATE_CLIENT_PERAP_DEFAULT = 100;

	private int                 maxSimuAp = MAX_SIMULATE_HIVEAP_DEFAULT;

	private int                 maxSimuClient = MAX_SIMULATE_CLIENT_PERAP_DEFAULT;

	// add the status for domain
	private int					runStatus				= DOMAIN_DEFAULT_STATUS;

	private String				timeZone				= TimeZone.getDefault().getID();

	private boolean				supportGM;

	@Column(length = 64)
	private String				comment;

	private boolean				supportFullMode			= true;

	@Index(name = "HM_DOMAIN_VHM_ID")
	@Column(length = 10)
	private String				vhmID;

	/**
	 * it will contain SE/sales/VAR email address
	 */
	private String				bccEmail;

	/**
	 * the user this vhm belongs to
	 */
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "USER_ID", nullable = true)
//	private HmUser ownerUser;
	private String partnerId;

	public static final short ACCESS_MODE_TECH_OP_PARTNER_DENY = 0;
	public static final short ACCESS_MODE_TECH_OP_PARTNER_24H_R = 1;
	public static final short ACCESS_MODE_TECH_OP_PARTNER_24H_RW = 2;
	public static final short ACCESS_MODE_TECH_OP_PARTNER_R = 3;
	public static final short ACCESS_MODE_TECH_OP_PARTNER_RW = 4;
	
	private short accessMode = ACCESS_MODE_TECH_OP_PARTNER_RW;
	
	private Long authorizationEndDate = -1L;
	
	private boolean accessChanged=true;
	
	private int authorizedTime=-1;

	public short getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(short accessMode) {
		this.accessMode = accessMode;
	}

	public Long getAuthorizationEndDate() {
		return authorizationEndDate;
	}

	public void setAuthorizationEndDate(Long authorizationEndDate) {
		this.authorizationEndDate = authorizationEndDate;
	}
	
	public boolean isAccessChanged() {
		return accessChanged;
	}

	public void setAccessChanged(boolean accessChanged) {
		this.accessChanged = accessChanged;
	}
	
	public int getAuthorizedTime() {
		return authorizedTime;
	}

	public void setAuthorizedTime(int authorizedTime) {
		this.authorizedTime = authorizedTime;
	}

	public String getVhmID() {
		return vhmID;
	}

	public void setVhmID(String vhmID) {
		this.vhmID = vhmID;
	}

	/*
	 * The Max HiveAP number support for 'home' domain is calculate by license support minus other
	 * domain HiveAP count, not stored in DB.
	 */
	public int getMaxApNum() {
		if (HOME_DOMAIN.equals(domainName)) {
			// home domain max number not persistent in DB.
			if (NmsUtil.isHostedHMApplication()) {
				maxApNum = BeLicenseModule.AH_LICENSE_EXCESS_SUPPORT_AP_COUNT;
			} else {
				int licenseApNum = null == HmBeLicenseUtil.getLicenseInfo() ? 0 : HmBeLicenseUtil
						.getLicenseInfo().getHiveAps();
				maxApNum = licenseApNum - BoMgmt.getDomainMgmt().getNonHomeDomainAPNum();
			}
		} else if (NmsUtil.isHostedHMApplication()) {
			LicenseInfo lsInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domainName);
			if (null != lsInfo) {
				maxApNum = lsInfo.getHiveAps();
			} else {
				DomainOrderKeyInfo domOrder = QueryUtil.findBoByAttribute(DomainOrderKeyInfo.class, "domainName", domainName);
				if (null != domOrder) {
					int[] orderInfo = domOrder.getOrderInfo();
					maxApNum = orderInfo[1];
				}
			}
		}

		return maxApNum;
	}

	/*
	 * This function return value that the domain supports the max number HiveAP. the only
	 * difference from home to other domain is that: MaxApSupportNum = maxApNum + 5;
	 *
	 */
	public int getMaxApSupportNum() {
		if (HOME_DOMAIN.equals(domainName) && !NmsUtil.isHostedHMApplication()) {
			return getMaxApNum() + BeLicenseModule.AH_LICENSE_EXCESS_SUPPORT_AP_COUNT;
		} else {
			return getMaxApNum();
		}
	}

	public int getMaxCvgSupportNum() {
		int totalNum = null == HmBeLicenseUtil.getLicenseInfo() ? BeLicenseModule.AH_LICENSE_EXCESS_SUPPORT_AP_COUNT : HmBeLicenseUtil
			.getLicenseInfo().getCvgNumber();

		if (NmsUtil.isHostedHMApplication() && !HOME_DOMAIN.equals(domainName)) {
			LicenseInfo lsInfo = HmBeLicenseUtil.VHM_ORDERKEY_INFO.get(domainName);
			if (null != lsInfo) {
				return lsInfo.getCvgNumber();
			} else {
				DomainOrderKeyInfo domOrder = QueryUtil.findBoByAttribute(DomainOrderKeyInfo.class, "domainName", domainName);
				if (null != domOrder) {
					int[] orderInfo = domOrder.getOrderInfo();
					return orderInfo[4];
				}
			}
		}
		return totalNum;
	}

	public String getRunStatusShow() {
		switch (runStatus) {
		case DOMAIN_DEFAULT_STATUS:
			return "Normal";
		case DOMAIN_RESTORE_STATUS:
			return "Restore";
		case DOMAIN_DISABLE_STATUS:
			return "Disabled";
		case DOMAIN_BACKUP_STATUS:
			return "Backup";
		case DOMAIN_UPDATE_STATUS:
			return "Update";
		default:
			return "Unknown";
		}
	}

	public void setMaxApNum(int maxApNum) {
		this.maxApNum = maxApNum;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Override
	public String getLabel() {
		return domainName;
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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Transient
	public boolean isHomeDomain() {
		return HmDomain.HOME_DOMAIN.equals(domainName);
	}

	@Transient
	private int	managedApNum, managedSimApNum;

	public int getManagedApNum() {
		return managedApNum;
	}

	public void setManagedApNum(int managedApNum) {
		this.managedApNum = managedApNum;
	}

	public int getManagedSimApNum() {
		return managedSimApNum;
	}

	public void setManagedSimApNum(int managedSimApNum) {
		this.managedSimApNum = managedSimApNum;
	}

	@Transient
	public int computeManagedApNum() {
		List<Short> notInList = new ArrayList<>();
		notInList.add(HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
		managedApNum = (int) QueryUtil.findRowCount(HiveAp.class,
				new FilterParams("manageStatus = :s1 and simulated = :s2 and owner.id = :s3 AND hiveApModel not in :s4", new Object[] {
						HiveAp.STATUS_MANAGED, false, id, notInList}));

		log.info("computeManagedApNum", "HmDomain: " + domainName + "; Number of managed AP: "
				+ managedApNum);

		return managedApNum;
	}

	@Transient
	public boolean isManagedApNumFull() {
		//if (HmBeLicenseUtil.getLicenseInfo() == null) {
			//return true;
		//}

		LicenseInfo licenseInfo = LicenseOperationTool.getOrderKeyInfoFromDatabase(domainName);

		if (!HOME_DOMAIN.equals(domainName) && NmsUtil.isHostedHMApplication()) {
			HmBeLicenseUtil.VHM_ORDERKEY_INFO.put(domainName, licenseInfo);
		}

		int maxManagedApNum = getMaxApNum();

		// Additional 5 HiveAPs for home domain only.
		if (HOME_DOMAIN.equals(domainName)) {
			maxManagedApNum += BeLicenseModule.AH_LICENSE_EXCESS_SUPPORT_AP_COUNT;
		}

		log.info("isManagedApNumFull", "Maximum managed AP number: " + maxManagedApNum
				+ "; Current managed AP number: " + managedApNum);

		return managedApNum >= maxManagedApNum;
	}

	@Transient
	public int computeManagedSimApNum() {
		managedSimApNum = (int) QueryUtil.findRowCount(HiveAp.class,
				new FilterParams("manageStatus = :s1 and simulated = :s2 and owner.id = :s3", new Object[] {
						HiveAp.STATUS_MANAGED, true, id }));

		log.info("computeManagedSimApNum", "HmDomain: " + domainName + "; Number of managed simulated AP: "
				+ managedSimApNum);

		return managedSimApNum;
	}

	@Transient
	public boolean isManagedSimApNumFull() {
		log.info("isManagedSimApNumFull", "Maximum managed simulated AP number: " + maxSimuAp
				+ "; Current managed simulated AP number: " + managedSimApNum);

		return managedSimApNum >= maxSimuAp;
	}

	public void setRunStatus(int runStatus) {
		this.runStatus = runStatus;
	}

	public int getRunStatus() {
		return this.runStatus;
	}

	@Transient
	public boolean isRunning() {
		return runStatus == DOMAIN_DEFAULT_STATUS;
	}

	@Transient
	public boolean isRestoring() {
		return runStatus == DOMAIN_RESTORE_STATUS;
	}

	@Transient
	public boolean isDisabled() {
		return runStatus == DOMAIN_DISABLE_STATUS;
	}

	@Transient
	public String getDomainNameESC() {
		return domainName == null ? "" : domainName.replace("\\", "\\\\")
				.replace(" ", "&nbsp;").replace("\"", "\\\"").replace("'",
						"\\\'");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof HmDomain)) {
			return false;
		}

		final HmDomain domain = (HmDomain) o;

		return domainName != null ? domainName.equals(domain.domainName)
				: domain.domainName == null;
	}

	@Override
	public int hashCode() {
		int result;
		result = (domainName != null ? domainName.hashCode() : 0);
		return result;
	}

	@Transient
	private int	clientRefreshInterval;

	@Transient
	private int	monitoringId;

	@Transient
	private int	monitoringConfigId;

	@Transient
	private int	rfPlanningId;

	@Transient
	private int	userMngAdminId;

	@Transient
	private int	userMngOperatorId;

	@Transient
	private int	teacherId;

	@Transient
	private String defaultUserGroupStyle = "none"; // by default;

	public int getClientRefreshInterval() {
		return clientRefreshInterval;
	}

	public void setClientRefreshInterval(int clientRefreshInterval) {
		this.clientRefreshInterval = clientRefreshInterval;
	}

	public String getTimeZoneString() {
		return timeZone;
	}

	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone(timeZone);
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public boolean isSupportGM() {
		return supportGM;
	}

	public String getGMCapability() {
		if (supportGM) {
			return "Enabled";
		} else {
			return "Disabled";
		}
	}

	public void setSupportGM(boolean supportGM) {
		this.supportGM = supportGM;
	}

	@Transient
	public HmStartConfig getStartConfig() {
		if (null != id) {
			List<HmStartConfig> list = QueryUtil.executeQuery(HmStartConfig.class, null, null, id);
			return list.isEmpty() ? null : list.get(0);
		}
		return null;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isSupportFullMode() {
		return supportFullMode;
	}

	public void setSupportFullMode(boolean supportFullMode) {
		this.supportFullMode = supportFullMode;
	}

	public String getBccEmail() {
		return bccEmail;
	}

	public void setBccEmail(String ccEmail) {
		this.bccEmail = ccEmail;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	@Override
	public String toString() {
		return "Name: " + domainName + "; Max AP Count: " + maxApNum + "; Status: " + runStatus + "; GM Supported: " + supportGM + "; Full Mode Supported: " + supportFullMode;
	}

	@Transient
	public int getMaxApInDb() {
		return maxApNum;
	}

	public int getMonitoringId() {
		return monitoringId;
	}

	public void setMonitoringId(int monitoringId) {
		this.monitoringId = monitoringId;
	}

	public int getMonitoringConfigId() {
		return monitoringConfigId;
	}

	public void setMonitoringConfigId(int monitoringConfigId) {
		this.monitoringConfigId = monitoringConfigId;
	}

	public int getRfPlanningId() {
		return rfPlanningId;
	}

	public void setRfPlanningId(int rfPlanningId) {
		this.rfPlanningId = rfPlanningId;
	}

	public int getUserMngAdminId() {
		return userMngAdminId;
	}

	public void setUserMngAdminId(int userMngAdminId) {
		this.userMngAdminId = userMngAdminId;
	}

	public int getUserMngOperatorId() {
		return userMngOperatorId;
	}

	public void setUserMngOperatorId(int userMngOperatorId) {
		this.userMngOperatorId = userMngOperatorId;
	}

	public int getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(int teacherId) {
		this.teacherId = teacherId;
	}

	public int getMaxSimuAp()
	{
		return maxSimuAp;
	}

	public void setMaxSimuAp(int maxSimuAp)
	{
		this.maxSimuAp = maxSimuAp;
	}

	public int getMaxSimuClient()
	{
		return maxSimuClient;
	}

	public void setMaxSimuClient(int maxSimuClient)
	{
		this.maxSimuClient = maxSimuClient;
	}

	public String getDefaultUserGroupStyle() {
		return defaultUserGroupStyle;
	}

	public void setDefaultUserGroupStyle(String defaultUserGroupStyle) {
		this.defaultUserGroupStyle = defaultUserGroupStyle;
	}
	public String getInstanceId() {
		return vhmID != null && vhmID.trim().length() > 0 ? vhmID : BeLicenseModule.HIVEMANAGER_SYSTEM_ID; 
	}
	public String getAcmInstanceId(){
		String customer = "";
		try{
			customer = LicenseOperationTool.getCustomerIdFromRemote(getInstanceId());
		}catch(Exception e){
			log.info("unable to get the customer id");
		}
		if(customer == null || customer.equals("")){
			return getInstanceId();
		}
		return customer + "@" + getInstanceId();
	}

}