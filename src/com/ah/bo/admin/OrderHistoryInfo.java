package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.app.HmBeOsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "order_history_info")
public class OrderHistoryInfo implements HmBo {

	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue
	private Long				id;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH, nullable = false)
	private String				orderKey;

	private long				activeTime;

	private String				licenseType;

	private int					numberOfAps;

	private int					numberOfVhms;

	private int					numberOfEvalValidDays;

	private long				supportEndDate;

	private String				domainName;
	
	public static final short ENTITLE_KEY_STATUS_NORMAL = 1;
	
	public static final short ENTITLE_KEY_STATUS_DISABLE = 2;
	
	public static final short ENTITLE_KEY_STATUS_OVERDUE = 3;
	
	private short               statusFlag = ENTITLE_KEY_STATUS_NORMAL;
	
	private boolean             sendEmail;
	
	// add from cairo (4.0r1)
	private long              	subEndDate;
	
	// add from congo (5.0r1)
	private int					numberOfCvgs;
	
	// add from congo (5.0r1)
	private long              	cvgSubEndDate;
	
	private short               cvgStatusFlag = ENTITLE_KEY_STATUS_NORMAL;

	public boolean isSendEmail()
	{
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail)
	{
		this.sendEmail = sendEmail;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public long getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(long activeTime) {
		this.activeTime = activeTime;
	}

	public int getNumberOfAps() {
		return numberOfAps;
	}

	public void setNumberOfAps(int numberOfAps) {
		this.numberOfAps = numberOfAps;
	}

	public int getNumberOfVhms() {
		return numberOfVhms;
	}

	public void setNumberOfVhms(int numberOfVhms) {
		this.numberOfVhms = numberOfVhms;
	}

	public int getNumberOfEvalValidDays() {
		return numberOfEvalValidDays;
	}

	public void setNumberOfEvalValidDays(int numberOfEvalValidDays) {
		this.numberOfEvalValidDays = numberOfEvalValidDays;
	}

	public long getSupportEndDate() {
		return supportEndDate;
	}

	public void setSupportEndDate(long supportEndDate) {
		this.supportEndDate = supportEndDate;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Version
	private Timestamp	version;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
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
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "Entitlement Key:" + orderKey;
	}

	@Transient
	public String getActiveTimeStr() {
		return AhDateTimeUtil.getDateStrFromLong(activeTime, AhDateTimeUtil.DEFAULT_FORMATTER,
				HmDomain.HOME_DOMAIN.equals(domainName) ? HmBeOsUtil.getServerTimeZone()
						: getTimeZoneOfVhm());
	}

	@Transient
	public String getSupportEndTimeStr() {
		if (supportEndDate > 0) {
			return AhDateTimeUtil.getDateStrFromLongNoTimeZone(supportEndDate);
		} else {
			return "N/A";
		}
	}
	
	@Transient
	public String getSubEndTimeStr() {
		if (subEndDate > 0) {
			return AhDateTimeUtil.getDateStrFromLongNoTimeZone(subEndDate);
		} else {
			return "N/A";
		}
	}
	
	@Transient
	public String getCvgSubEndTimeStr() {
		if (cvgSubEndDate > 0) {
			return AhDateTimeUtil.getDateStrFromLongNoTimeZone(cvgSubEndDate);
		} else {
			return "N/A";
		}
	}

	@Transient
	private String getTimeZoneOfVhm() {
		HmDomain hmDom = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", domainName);
		if (null != hmDom) {
			return hmDom.getTimeZoneString();
		}
		return HmBeOsUtil.getServerTimeZone();
	}

	@Transient
	public String getLicenseTypeStr() {
		String licenseT = "Invalid";
		if (BeLicenseModule.LICENSE_TYPE_EVALUATION_NUM.equals(licenseType)) {
			licenseT = "Evaluation";
		} else if (BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licenseType)) {
			licenseT = HmDomain.HOME_DOMAIN.equals(domainName) ? "Permanent" : "Subscription";
		} else if (BeLicenseModule.LICENSE_TYPE_RENEW_NUM.equals(licenseType)) {
			licenseT = HmDomain.HOME_DOMAIN.equals(domainName) ? "Permanent Renew" : 
				(numberOfAps>0? "Renewed Subscription":"Renewed Support");
		}
		return licenseT;
	}

	public String getLicenseType() {
		return licenseType;
	}

	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean getIsPermanentLicense() {
		return BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM.equals(licenseType)
				|| BeLicenseModule.LICENSE_TYPE_RENEW_NUM.equals(licenseType);
	}

	public short getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(short statusFlag) {
		this.statusFlag = statusFlag;
	}

	public long getSubEndDate()
	{
		return subEndDate;
	}

	public void setSubEndDate(long subEndDate)
	{
		this.subEndDate = subEndDate;
	}

	public int getNumberOfCvgs()
	{
		return numberOfCvgs;
	}

	public void setNumberOfCvgs(int numberOfCvgs)
	{
		this.numberOfCvgs = numberOfCvgs;
	}

	public long getCvgSubEndDate()
	{
		return cvgSubEndDate;
	}

	public void setCvgSubEndDate(long cvgSubEndDate)
	{
		this.cvgSubEndDate = cvgSubEndDate;
	}

	public short getCvgStatusFlag()
	{
		return cvgStatusFlag;
	}

	public void setCvgStatusFlag(short cvgStatusFlag)
	{
		this.cvgStatusFlag = cvgStatusFlag;
	}
	
	@Transient
	public String getDeviceStatusStr() {
		switch (statusFlag) {
			case ENTITLE_KEY_STATUS_NORMAL:
				return "Normal";
			case ENTITLE_KEY_STATUS_DISABLE:
				return "Invalid";
			case ENTITLE_KEY_STATUS_OVERDUE:
				return "Expired";
		}
		return "";
	}
	
	@Transient
	public String getCvgStatusStr() {
		switch (cvgStatusFlag) {
			case ENTITLE_KEY_STATUS_NORMAL:
				return "Normal";
			case ENTITLE_KEY_STATUS_DISABLE:
				return "Invalid";
			case ENTITLE_KEY_STATUS_OVERDUE:
				return "Expired";
		}
		return "";
	}

}