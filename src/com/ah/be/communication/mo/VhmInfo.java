package com.ah.be.communication.mo;

public class VhmInfo {
	/* VHM Type */
	public static final short	VHM_TYPE_EVAL		= 1;
	public static final short	VHM_TYPE_PLAN_EVAL	= 2;
	public static final short	VHM_TYPE_REGULAR	= 3;

	/* VHM-ID */
	private String				vhmId;

	/* VHM Name */
	private String				vhmName;

	/* User Account Type */
	private short				vhmType				= VHM_TYPE_EVAL;

	/* Number of HiveAPs */
	private int					maxApNum;
	
	/* Max Number of Simulate HiveAPs, add from 4.0r1 beta2 */
	private int                 maxSimuApNum;
	
	/* Max Number of Simulate Clients per AP, add from 4.0r1 beta2 */
	private int                 maxSimuClientNum;

	/* VHM admin Name */
	private String				vhmAdminName;

	/* VHM admin Full Name */
	private String				vhmAdminFullName;

	/* VHM admin email Address */
	private String				vhmAdminEmailAddress;

	private String				vhmAdminClearPassword;

	/* GM Light Status */
	private boolean				gmLightEnable;

	/* Valid Days */
	private int					validDays			= -1;

	private boolean				needNotifyFlag;

	private String				ccEmailAddress;

	private boolean				enterpriseEnableFlag;

	private boolean				statusEnable;

	private String				url;

	private String				orderKey;

	private String				ownerUserName;

	private String				hhmName;
	
	private short 				accessMode;
	
	private Long 				authorizationEndDate;
	
	private int 				authorizedTime;

	public int getAuthorizedTime() {
		return authorizedTime;
	}

	public void setAuthorizedTime(int authorizedTime) {
		this.authorizedTime = authorizedTime;
	}

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

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public short getVhmType() {
		return vhmType;
	}

	public void setVhmType(short vhmType) {
		this.vhmType = vhmType;
	}

	public int getMaxApNum() {
		return maxApNum;
	}

	public void setMaxApNum(int maxApNum) {
		this.maxApNum = maxApNum;
	}

	public String getVhmAdminName() {
		return vhmAdminName;
	}

	public void setVhmAdminName(String vhmAdminName) {
		this.vhmAdminName = vhmAdminName;
	}

	public String getVhmAdminFullName() {
		return vhmAdminFullName;
	}

	public void setVhmAdminFullName(String vhmAdminFullName) {
		this.vhmAdminFullName = vhmAdminFullName;
	}

	public String getVhmAdminEmailAddress() {
		return vhmAdminEmailAddress;
	}

	public void setVhmAdminEmailAddress(String vhmAdminEmailAddress) {
		this.vhmAdminEmailAddress = vhmAdminEmailAddress;
	}

	public String getVhmAdminClearPassword() {
		return vhmAdminClearPassword;
	}

	public void setVhmAdminClearPassword(String vhmAdminClearPassword) {
		this.vhmAdminClearPassword = vhmAdminClearPassword;
	}

	public boolean isGmLightEnable() {
		return gmLightEnable;
	}

	public void setGmLightEnable(boolean gmLightEnable) {
		this.gmLightEnable = gmLightEnable;
	}

	public int getValidDays() {
		return validDays;
	}

	public void setValidDays(int validDays) {
		this.validDays = validDays;
	}

	public boolean isNeedNotifyFlag() {
		return needNotifyFlag;
	}

	public void setNeedNotifyFlag(boolean needNotifyFlag) {
		this.needNotifyFlag = needNotifyFlag;
	}

	public String getCcEmailAddress() {
		return ccEmailAddress;
	}

	public void setCcEmailAddress(String ccEmailAddress) {
		this.ccEmailAddress = ccEmailAddress;
	}

	public boolean isEnterpriseEnableFlag() {
		return enterpriseEnableFlag;
	}

	public void setEnterpriseEnableFlag(boolean enterpriseEnableFlag) {
		this.enterpriseEnableFlag = enterpriseEnableFlag;
	}

	public boolean isStatusEnable() {
		return statusEnable;
	}

	public void setStatusEnable(boolean statusEnable) {
		this.statusEnable = statusEnable;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public String getOwnerUserName() {
		return ownerUserName;
	}

	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	public String getHhmName() {
		return hhmName;
	}

	public void setHhmName(String hhmName) {
		this.hhmName = hhmName;
	}

	public int getMaxSimuApNum()
	{
		return maxSimuApNum;
	}

	public void setMaxSimuApNum(int maxSimuApNum)
	{
		this.maxSimuApNum = maxSimuApNum;
	}

	public int getMaxSimuClientNum()
	{
		return maxSimuClientNum;
	}

	public void setMaxSimuClientNum(int maxSimuClientNum)
	{
		this.maxSimuClientNum = maxSimuClientNum;
	}
	
	@Override
	// just for debug modify vhm
	public String toString() {
		return "VhmInfo [vhmId=" + vhmId + ", vhmName=" + vhmName
				+ ", vhmType=" + vhmType + ", maxApNum="
				+ maxApNum + ", maxSimuApNum="
				+ maxSimuApNum + ", maxSimuClientNum=" + maxSimuClientNum
				+ ", vhmAdminName=" + vhmAdminName + ", vhmAdminFullName="
				+ vhmAdminFullName + ", vhmAdminEmailAddress="
				+ vhmAdminEmailAddress + ", gmLightEnable=" + gmLightEnable
				+ ", validDays=" + validDays + ", needNotifyFlag="
				+ needNotifyFlag + ", enterpriseEnableFlag="
				+ enterpriseEnableFlag + ", statusEnable=" + statusEnable
				+ ", url=" + url + ", orderKey="
				+ orderKey + ", ownerUserName="
				+ ownerUserName + ", hhmName=" + hhmName
				+ ", ccEmailAddress=" + ccEmailAddress + "]";
	}

}
