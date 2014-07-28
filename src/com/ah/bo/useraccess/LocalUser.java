/**
 *@filename		LocalUser.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-26 PM 09:47:21
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.useraccess;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "LOCAL_USER")
@org.hibernate.annotations.Table(appliesTo = "LOCAL_USER", indexes = { @Index(name = "LOCAL_USER_OWNER", columnNames = { "OWNER" }) })
public class LocalUser implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

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

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String userName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description = "";

	// public static final int PASSWORD_TYPE_ASCII = 1;
	// public static final int PASSWORD_TYPE_HEX = 2;
	// public static EnumItem[] ENUM_PASSWORD_TYPE = MgrUtil
	// .enumItems("enum.password.type.", new int[] { PASSWORD_TYPE_ASCII,
	// PASSWORD_TYPE_HEX });
	// private int passwordType=PASSWORD_TYPE_ASCII;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String localUserPassword;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GROUP_ID")
	private LocalUserGroup localUserGroup;

	// private boolean saveMemory;

	private int userType = LocalUserGroup.USERGROUP_USERTYPE_RADIUS;

	@Column(length = 128)
	private String mailAddress = "";

	private boolean revoked;

	public static final int MAX_COUNT_AP30_LOCALUSER = 9999;
	public static final int MAX_COUNT_AP30_USERPERSSID = 9999;
	public static final int MAX_COUNT_AP30_USERCOUNT_PERAP = 9999;
	public static final int MAX_COUNT_AP30_USERPERAP = 80000;

	public static final int MAX_COUNT_AP10_LOCALUSER = 4095;
	public static final int MAX_COUNT_AP10_USERPERSSID = 4095;
	public static final int MAX_COUNT_AP10_USERCOUNT_PERAP = 4095;
	public static final int MAX_COUNT_AP10_USERPERAP = 40000;

	public static final int MAX_COUNT_BR100_USERPERBR = 128;

	/*
	 * the following fields are for GML status, visitorCompany, sponsor,
	 * ssidName visitor name
	 */
	public static final int STATUS_FREE = 1;

	public static final int STATUS_ALLOCATED = 2;

	public static final int STATUS_REVOKED = 3;

	public static final int STATUS_EXPIRED = 4;
	
	public static final int STATUS_PARTIAL_REVOKED = 5;

	public static final EnumItem[] ENUM_STATUS = MgrUtil.enumItems(
			"enum.user.status.", new int[] { STATUS_FREE, STATUS_ALLOCATED,
					STATUS_REVOKED, STATUS_EXPIRED, STATUS_PARTIAL_REVOKED});

	private int status = STATUS_FREE;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = true)
	private String visitorCompany;

	@Column(length = DEFAULT_SPNSOR_LENGTH, nullable = true)
	private String sponsor;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = true)
	private String ssidName;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = true)
	private String visitorName;

	@Column(length = 128, nullable = true)
	private String oldPPSK;

	/*
	 * this field is only for user of USERGROUP_USERTYPE_RADIUS
	 */
	private boolean activated;

	private boolean defaultFlag;

	/**
	 * getter of status
	 * 
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * setter of status
	 * 
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * getter of visitorCompany
	 * 
	 * @return the visitorCompany
	 */
	public String getVisitorCompany() {
		return visitorCompany;
	}

	/**
	 * setter of visitorCompany
	 * 
	 * @param visitorCompany
	 *            the visitorCompany to set
	 */
	public void setVisitorCompany(String visitorCompany) {
		this.visitorCompany = visitorCompany;
	}

	/**
	 * getter of sponsor
	 * 
	 * @return the sponsor
	 */
	public String getSponsor() {
		return sponsor;
	}

	/**
	 * setter of sponsor
	 * 
	 * @param sponsor
	 *            the sponsor to set
	 */
	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	/**
	 * getter of ssidName
	 * 
	 * @return the ssidName
	 */
	public String getSsidName() {
		return ssidName;
	}

	/**
	 * setter of ssidName
	 * 
	 * @param ssidName
	 *            the ssidName to set
	 */
	public void setSsidName(String ssidName) {
		this.ssidName = ssidName;
	}

	/**
	 * getter of visitorName
	 * 
	 * @return the visitorName
	 */
	public String getVisitorName() {
		return visitorName;
	}

	/**
	 * setter of visitorName
	 * 
	 * @param visitorName
	 *            the visitorName to set
	 */
	public void setVisitorName(String visitorName) {
		this.visitorName = visitorName;
	}

	/**
	 * getter of activated
	 * 
	 * @return the activated
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * setter of activated
	 * 
	 * @param activated
	 *            the activated to set
	 */
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getOldPPSK() {
		return oldPPSK;
	}

	public void setOldPPSK(String oldPPSK) {
		this.oldPPSK = oldPPSK;
	}

	public String getActivationValue() {
		return this.activated ? "Activated" : "Not Activated";
	}

	public String getComment() {
		return this.description;
	}

	public void setComment(String comment) {
		this.description = comment;
	}

	public boolean getRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
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
	private boolean selected;

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
		return userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		if (userName != null) {
			this.userName = userName.trim();
		} else {
			this.userName = userName;
		}
	}

	public String getLocalUserPassword() {
		return localUserPassword;
	}

	public void setLocalUserPassword(String localUserPassword) {
		this.localUserPassword = localUserPassword;
	}

	public LocalUserGroup getLocalUserGroup() {
		return localUserGroup;
	}

	public void setLocalUserGroup(LocalUserGroup localUserGroup) {
		this.localUserGroup = localUserGroup;
	}

	@Transient
	public String getUserGroupName() {
		return null == localUserGroup ? "" : localUserGroup.getGroupName();
	}

	// @Transient
	// public String getSaveInMemory() {
	// return saveMemory ? "Yes" : "No";
	// }

	@Transient
	public String getValue() {
		return userName;
	}

	// public boolean isSaveMemory() {
	// return saveMemory;
	// }
	//
	// public void setSaveMemory(boolean saveMemory) {
	// this.saveMemory = saveMemory;
	// }

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public int getUserType() {
		return userType;
	}

	public String getStrUserType() {
		if (userType == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return "RADIUS";
		} else if (userType == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			return "Private PSK-Auto";
		} else {
			return "Private PSK-Manual";
		}
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public String getStrPskString() {
		if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return "";
		}
		if (MgrUtil.getSessionAttribute("SHOWORHIDEALLPSK") == null
				|| !(Boolean) MgrUtil.getSessionAttribute("SHOWORHIDEALLPSK")) {
			return "********";
		}
		return getStrPsk();
	}

	public String getStrPsk() {
		if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			if (getLocalUserGroup().getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_ONLY) {
				return dcdauthPuGenPskAuto();
			} else {
				return getUserName()
						+ getLocalUserGroup().getConcatenateString()
						+ dcdauthPuGenPskAuto();
			}
		} else if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			if (getLocalUserGroup().getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_ONLY) {
				return localUserPassword;
			} else {
				return getUserName()
						+ getLocalUserGroup().getConcatenateString()
						+ getLocalUserPassword();
			}
		} else {
			return "";
		}
	}

	public String getStrPasswordDigest() {
		String allDigest[];
		if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			allDigest = getPasswdAndPSkDigest(dcdauthPuGenPskAuto());
			return allDigest[0];
		} else if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			allDigest = getPasswdAndPSkDigest(localUserPassword);
			return allDigest[0];
		} else {
			return "";
		}
	}

	public String getStrPSKDigest() {
		String allDigest[];
		if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK) {
			allDigest = getPasswdAndPSkDigest(dcdauthPuGenPskAuto());
			return allDigest[1];
		} else if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_MANUALLYPSK) {
			allDigest = getPasswdAndPSkDigest(localUserPassword);
			return allDigest[1];
		} else {
			return "";
		}
	}

	// public int getPasswordType() {
	// return passwordType;
	// }
	//
	// public void setPasswordType(int passwordType) {
	// this.passwordType = passwordType;
	// }
	// public String getScheduleTimeString(){
	// if (getLocalUserGroup().getSchedule()!=null) {
	// return getLocalUserGroup().getSchedule().getAllTimeString();
	// }
	// return "";
	// }

	public String getExpiredTimeString() {
		if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return "";
		}
		TimeZone tz= TimeZone.getTimeZone(BaseAction.getSessionUserContext().getTimeZone());
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
		}
		Calendar aa = Calendar.getInstance();

		if (getLocalUserGroup().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_ONCE) {
			if (getLocalUserGroup().getExpiredTime() == null) {
				return "";
			}
			aa.setTimeInMillis(getLocalUserGroup().getExpiredTime().getTime());
			if (getLocalUserGroup().getExpiredTime().getTime()
					+ getCurrentTimeOffset(aa) < System.currentTimeMillis()) {
				return "Expired";
			} else {
				aa.setTimeInMillis(getLocalUserGroup().getExpiredTime().getTime());
				return AhDateTimeUtil.getSpecifyDateTime(getLocalUserGroup().getExpiredTime().getTime() + getCurrentTimeOffset(aa), tz, loginUser != null ? loginUser : owner);
			}

		} else if (getLocalUserGroup().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) {
			if (getLocalUserGroup().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK
					&& getLocalUserGroup().isBlnBulkType()) {

				long needAddTime;
				Date startTime = getLocalUserGroup().getStartTime();
				Date expireTime = getLocalUserGroup().getExpiredTime();
				long stepTime = getLocalUserGroup().getIntervalDay() * 24 * 60
						* 60 * 1000L + getLocalUserGroup().getIntervalHour()
						* 60 * 60 * 1000 + getLocalUserGroup().getIntervalMin()
						* 60 * 1000;

				aa.setTimeInMillis(startTime.getTime());
				long startoffset = getCurrentTimeOffset(aa);
				aa.setTimeInMillis(expireTime.getTime());
				long endoffset = getCurrentTimeOffset(aa);
				long dayLightSavingBeginandEndtime = getDayLightSaveing(
						startTime.getTime() + startoffset, expireTime.getTime()
								+ endoffset, getLocalUserGroup()
								.getGroupTimeZone());

				long onePeriod = expireTime.getTime() + endoffset
						- startTime.getTime() - startoffset
						- dayLightSavingBeginandEndtime;
				long stepCount = (onePeriod % stepTime == 0) ? (onePeriod / stepTime)
						: (onePeriod / stepTime + 1);

				aa.setTimeInMillis(System.currentTimeMillis());
				long currentoffset = getCurrentTimeOffset(aa);
				long dayLightSaving = getDayLightSaveing(startTime.getTime()
						+ startoffset, System.currentTimeMillis()
						+ currentoffset, getLocalUserGroup().getGroupTimeZone());

				if (getLocalUserGroup().getIndexRange() == getLocalUserGroup()
						.getBulkUserCount()) {
					long totalStepCount = (System.currentTimeMillis() - (startTime
							.getTime() + startoffset + dayLightSaving))
							/ stepTime;
					if (totalStepCount < stepCount) {
						needAddTime = 0;
					} else {
						needAddTime = totalStepCount * stepTime;
					}
				} else {
					int userNameIndex = Integer.parseInt(getUserName()
							.substring(getUserName().length() - 4,
									getUserName().length()));
					int userIndexCycle = (userNameIndex - 1)
							/ getLocalUserGroup().getIndexRange();
					long cycleNubmer = (System.currentTimeMillis() - (startTime
							.getTime() + startoffset + dayLightSaving + stepTime
							* userIndexCycle))
							/ (stepCount * stepTime);
					if (cycleNubmer < 0)
						cycleNubmer = 0;
					needAddTime = cycleNubmer * stepCount * stepTime + stepTime
							* userIndexCycle;
				}

				long totalNeedAddEndTime = getLocalUserGroup().getBulkNumber()
						* stepTime;

				if (needAddTime < 0) {
					needAddTime = 0;
				}
				if (totalNeedAddEndTime <= needAddTime) {
					return "Expired";
				} else {
					aa.setTimeInMillis(expireTime.getTime() + needAddTime);
					long displayTimeoffset = getCurrentTimeOffset(aa);
					dayLightSaving = getDayLightSaveing(expireTime.getTime()
							+ endoffset, expireTime.getTime() + needAddTime
							+ displayTimeoffset, aa.getTimeZone());

					if (expireTime.getTime() + needAddTime
							+ getCurrentTimeOffset(aa) - dayLightSaving < System
								.currentTimeMillis()) {
						return "Expired";
					}

					return AhDateTimeUtil.getSpecifyDateTime(expireTime.getTime() + needAddTime + getCurrentTimeOffset(aa) + dayLightSaving, tz, loginUser != null ? loginUser : owner);
				}
			} else {
				Calendar[] startEndTime = getLocalUserGroup().getSchedule()
						.getStartAndEndTime(
								getLocalUserGroup().getGroupTimeZone());
				if (startEndTime == null) {
					return "Expired";
				} else {
					aa.setTimeInMillis(startEndTime[1].getTimeInMillis());
					return AhDateTimeUtil.getSpecifyDateTime(startEndTime[1].getTimeInMillis() + getCurrentTimeOffset(aa), tz, loginUser != null ? loginUser : owner);
				}
			}
		}
		return "";
	}

	public String getStartTimeString() {
		if (getUserType() == LocalUserGroup.USERGROUP_USERTYPE_RADIUS) {
			return "";
		}
		TimeZone tz= TimeZone.getTimeZone(BaseAction.getSessionUserContext().getTimeZone());
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() !=null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
		}
		Calendar aa = Calendar.getInstance();

		if (getLocalUserGroup().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_ONCE) {
			if (getLocalUserGroup().getStartTime() == null) {
				return "";
			}
			if (getLocalUserGroup().getExpiredTime() == null) {
				aa.setTimeInMillis(getLocalUserGroup().getStartTime().getTime());
				return AhDateTimeUtil.getSpecifyDateTime(getLocalUserGroup().getStartTime().getTime() + getCurrentTimeOffset(aa), tz, loginUser != null ? loginUser : owner);
			}
			aa.setTimeInMillis(getLocalUserGroup().getExpiredTime().getTime());
			if (getLocalUserGroup().getExpiredTime().getTime()
					+ getCurrentTimeOffset(aa) < System.currentTimeMillis()) {
				return "Expired";
			} else {
				aa.setTimeInMillis(getLocalUserGroup().getStartTime().getTime());
				return AhDateTimeUtil.getSpecifyDateTime(getLocalUserGroup().getStartTime().getTime() + getCurrentTimeOffset(aa), tz, loginUser != null ? loginUser : owner);
			}
		} else if (getLocalUserGroup().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) {
			if (getLocalUserGroup().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK
					&& getLocalUserGroup().isBlnBulkType()) {
				long needAddTime;
				Date startTime = getLocalUserGroup().getStartTime();
				Date expireTime = getLocalUserGroup().getExpiredTime();

				long stepTime = getLocalUserGroup().getIntervalDay() * 24 * 60
						* 60 * 1000L + getLocalUserGroup().getIntervalHour()
						* 60 * 60 * 1000 + getLocalUserGroup().getIntervalMin()
						* 60 * 1000;
				aa.setTimeInMillis(startTime.getTime());
				long startoffset = getCurrentTimeOffset(aa);
				aa.setTimeInMillis(expireTime.getTime());
				long endoffset = getCurrentTimeOffset(aa);
				long dayLightSavingBeginandEndtime = getDayLightSaveing(
						startTime.getTime() + startoffset, expireTime.getTime()
								+ endoffset, getLocalUserGroup()
								.getGroupTimeZone());

				long onePeriod = expireTime.getTime() + endoffset
						- startTime.getTime() - startoffset
						- dayLightSavingBeginandEndtime;
				long stepCount = (onePeriod % stepTime == 0) ? (onePeriod / stepTime)
						: (onePeriod / stepTime + 1);

				aa.setTimeInMillis(System.currentTimeMillis());
				long currentoffset = getCurrentTimeOffset(aa);
				long dayLightSaving = getDayLightSaveing(startTime.getTime()
						+ startoffset, System.currentTimeMillis()
						+ currentoffset, getLocalUserGroup().getGroupTimeZone());

				if (getLocalUserGroup().getIndexRange() == getLocalUserGroup()
						.getBulkUserCount()) {
					long totalStepCount = (System.currentTimeMillis() - (startTime
							.getTime() + startoffset + dayLightSaving))
							/ stepTime;
					if (totalStepCount < stepCount) {
						needAddTime = 0;
					} else {
						needAddTime = totalStepCount * stepTime;
					}
				} else {
					int userNameIndex = Integer.parseInt(getUserName()
							.substring(getUserName().length() - 4,
									getUserName().length()));
					int userIndexCycle = (userNameIndex - 1)
							/ getLocalUserGroup().getIndexRange();

					long cycleNubmer = (System.currentTimeMillis() - (startTime
							.getTime() + startoffset + dayLightSaving + stepTime
							* userIndexCycle))
							/ (stepCount * stepTime);
					if (cycleNubmer < 0)
						cycleNubmer = 0;
					needAddTime = cycleNubmer * stepCount * stepTime + stepTime
							* userIndexCycle;
				}

				long totalNeedAddEndTime = getLocalUserGroup().getBulkNumber()
						* stepTime;
				if (needAddTime < 0) {
					needAddTime = 0;
				}
				if (totalNeedAddEndTime <= needAddTime) {
					return "Expired";
				} else {
					aa.setTimeInMillis(expireTime.getTime() + needAddTime);
					long displayTimeoffset = getCurrentTimeOffset(aa);
					long dayLightSavingEndtime = getDayLightSaveing(
							expireTime.getTime() + endoffset,
							expireTime.getTime() + needAddTime
									+ displayTimeoffset, aa.getTimeZone());

					if (expireTime.getTime() + needAddTime
							+ getCurrentTimeOffset(aa) + dayLightSavingEndtime < System
								.currentTimeMillis()) {
						return "Expired";
					}
					aa.setTimeInMillis(startTime.getTime() + needAddTime);
					displayTimeoffset = getCurrentTimeOffset(aa);
					dayLightSaving = getDayLightSaveing(startTime.getTime()
							+ startoffset, startTime.getTime() + needAddTime
							+ displayTimeoffset, aa.getTimeZone());

					return AhDateTimeUtil.getSpecifyDateTime(startTime.getTime() + needAddTime + getCurrentTimeOffset(aa) + dayLightSaving, tz, loginUser != null ? loginUser : owner);
				}
			} else {
				Calendar[] startEndTime = getLocalUserGroup().getSchedule()
						.getStartAndEndTime(
								getLocalUserGroup().getGroupTimeZone());
				if (startEndTime == null) {
					return "Expired";
				} else {
					aa.setTimeInMillis(startEndTime[0].getTimeInMillis());
					return AhDateTimeUtil.getSpecifyDateTime(startEndTime[0].getTimeInMillis() + getCurrentTimeOffset(aa), tz, loginUser != null ? loginUser : owner);
				}
			}
		}
		return "";
	}

	public long getCurrentTimeOffset(Calendar nowCalendar) {
		long offset = nowCalendar.get(Calendar.ZONE_OFFSET) / 1000
				+ nowCalendar.get(Calendar.DST_OFFSET) / 1000
				- getLocalUserGroup().getTimeZoneOffSet(nowCalendar);
		return offset * 1000;
	}

	private static final int PU_CHAR_PATTERN_LETTERS = 0x08;
	private static final int PU_CHAR_PATTERN_DIGITS = 0x10;
	private static final int PU_CHAR_PATTERN_SPECIAL_CHARS = 0x20;
	private static final int MAX_ASCII_PSK_LEN = 63;

	public static long getDayLightSaveing(long configtime, long nowtime,
			TimeZone timeZone) {
		Calendar bb = Calendar.getInstance(timeZone);
		bb.setTimeInMillis(configtime);
		int first = bb.get(Calendar.DST_OFFSET);

		bb.setTimeInMillis(nowtime);
		int second = bb.get(Calendar.DST_OFFSET);
		return first - second;
	}

	// @Transient
	public String dcdauthPuGenPskAuto() {
		byte[] byteRet;
		byte[] sha1Out;
		byte[] psk = new byte[80];

		String prefix = getLocalUserGroup().getUserNamePrefix();
		String user = getUserName().substring(getUserName().length() - 4);
		int index = Integer.parseInt(user);
		Date startTime = getLocalUserGroup().getStartTime();
		Date expireTime = getLocalUserGroup().getExpiredTime();
		String secret = getLocalUserGroup().getPskSecret();
		String location = getLocalUserGroup().getPskLocation();
		int comboPattern = getLocalUserGroup().getPersonPskCombo();
		int charPattern = 0;
		int autoGenLen = getLocalUserGroup().getPskLength();

		if (getLocalUserGroup().getBlnCharLetters())
			charPattern |= PU_CHAR_PATTERN_LETTERS;
		if (getLocalUserGroup().getBlnCharDigits())
			charPattern |= PU_CHAR_PATTERN_DIGITS;
		if (getLocalUserGroup().getBlnCharSpecial())
			charPattern |= PU_CHAR_PATTERN_SPECIAL_CHARS;

		long intStartTime = 0;
		long intExpireTime = 0xffffffffL;

		if (getLocalUserGroup().getUserType() == LocalUserGroup.USERGROUP_USERTYPE_AUTOPSK
				&& getLocalUserGroup().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE
				&& getLocalUserGroup().isBlnBulkType()) {

			long stepTime = getLocalUserGroup().getIntervalDay() * 24 * 60 * 60
					* 1000L + getLocalUserGroup().getIntervalHour() * 60 * 60
					* 1000 + getLocalUserGroup().getIntervalMin() * 60 * 1000;

			Calendar tmp = Calendar.getInstance();
			long currentoffset = getCurrentTimeOffset(tmp);
			tmp.setTimeInMillis(startTime.getTime());
			long startoffset = getCurrentTimeOffset(tmp);
			tmp.setTimeInMillis(expireTime.getTime());
			long endoffset = getCurrentTimeOffset(tmp);

			long dayLightSaving = getDayLightSaveing(startTime.getTime()
					+ startoffset, System.currentTimeMillis() + currentoffset,
					getLocalUserGroup().getGroupTimeZone());

			if (getLocalUserGroup().getIndexRange() == getLocalUserGroup()
					.getBulkUserCount()) {
				Calendar aa = Calendar.getInstance();
				long totalStepCount = (System.currentTimeMillis() - (startTime
						.getTime() + startoffset + dayLightSaving))
						/ stepTime;
				if (totalStepCount < 0) {
					totalStepCount = 0;
				}
				if (startTime != null) {
					aa = Calendar.getInstance();
					aa.setTime(startTime);
					intStartTime = (aa.getTimeInMillis() + totalStepCount
							* stepTime)
							/ 1000 + getCurrentTimeOffset(aa) / 1000;
				}
				if (expireTime != null) {
					aa = Calendar.getInstance();
					aa.setTime(expireTime);
					intExpireTime = (aa.getTimeInMillis() + totalStepCount
							* stepTime)
							/ 1000 + getCurrentTimeOffset(aa) / 1000;
				}
			} else {
				Calendar aa = Calendar.getInstance();
				int userNameIndex = Integer.parseInt(getUserName().substring(
						getUserName().length() - 4, getUserName().length()));
				int userIndexCycle = (userNameIndex - 1)
						/ getLocalUserGroup().getIndexRange();

				long dayLightSavingBeginandEndtime = getDayLightSaveing(
						startTime.getTime() + startoffset, expireTime.getTime()
								+ endoffset, getLocalUserGroup()
								.getGroupTimeZone());
				long onePeriod = expireTime.getTime() + endoffset
						- startTime.getTime() - startoffset
						- dayLightSavingBeginandEndtime;

				long stepCount = (onePeriod % stepTime == 0) ? (onePeriod / stepTime)
						: (onePeriod / stepTime + 1);
				long cycleNubmer = (System.currentTimeMillis() - (startTime
						.getTime() + startoffset + dayLightSaving + stepTime
						* userIndexCycle))
						/ (stepCount * stepTime);
				if (cycleNubmer < 0)
					cycleNubmer = 0;
				long needAddTime = cycleNubmer * stepCount * stepTime
						+ stepTime * userIndexCycle;
				if (needAddTime < 0) {
					needAddTime = 0;
				}
				if (startTime != null) {
					aa = Calendar.getInstance();
					aa.setTime(startTime);
					intStartTime = (aa.getTimeInMillis() + needAddTime) / 1000
							+ getCurrentTimeOffset(aa) / 1000;
				}
				if (expireTime != null) {
					aa = Calendar.getInstance();
					aa.setTime(expireTime);
					intExpireTime = (aa.getTimeInMillis() + needAddTime) / 1000
							+ getCurrentTimeOffset(aa) / 1000;
				}
			}

		} else if (getLocalUserGroup().getValidTimeType() == LocalUserGroup.VALIDTYME_TYPE_SCHEDULE) {
			Calendar[] startEndTime = getLocalUserGroup().getSchedule()
					.getStartAndEndTime(getLocalUserGroup().getGroupTimeZone());
			if (startEndTime != null) {
				intStartTime = startEndTime[0].getTimeInMillis() / 1000
						+ getCurrentTimeOffset(startEndTime[0]) / 1000;
				intExpireTime = startEndTime[1].getTimeInMillis() / 1000
						+ getCurrentTimeOffset(startEndTime[1]) / 1000;
			}
		} else {
			if (startTime != null) {
				Calendar aa = Calendar.getInstance();
				aa.setTime(startTime);
				intStartTime = aa.getTimeInMillis() / 1000
						+ getCurrentTimeOffset(aa) / 1000;
			}
			if (expireTime != null) {
				Calendar aa = Calendar.getInstance();
				aa.setTime(expireTime);
				intExpireTime = aa.getTimeInMillis() / 1000
						+ getCurrentTimeOffset(aa) / 1000;
			}
		}

		StringBuilder bufTemp = new StringBuilder();
		bufTemp.append(prefix).append("\n").append(index).append("\n")
				.append(intStartTime).append("\n").append(intExpireTime)
				.append("\n").append(secret).append("\n").append(location)
				.append("\n").append(autoGenLen);

		MessageDigest digest;

		try {
			sha1Out = new byte[20];
			for (int m = 0; m < 20; m++) {
				sha1Out[m] = 0;
			}
			digest = MessageDigest.getInstance("SHA-1");
			for (int i = 0; i * 20 < autoGenLen; i++) {
				digest.update(bufTemp.toString().getBytes());
				digest.update(sha1Out);
				sha1Out = digest.digest();
				if (sha1Out == null || sha1Out.length != 20)
					return null;
				System.arraycopy(sha1Out, 0, psk, i * 20, 20);
			}
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		authPGenCharPattern(psk, comboPattern, charPattern, MAX_ASCII_PSK_LEN);

		byteRet = new byte[autoGenLen];
		System.arraycopy(psk, 0, byteRet, 0, autoGenLen);

		// System.out.println(String.valueOf(byteRet));
		return new String(byteRet);
	}

	private int authPGenCharPattern(byte[] psk, int comboPattern,
			int charPattern, int length) {
		int ret = -1;
		switch (comboPattern) {
		case LocalUserGroup.PSKFORMAT_COMBO_OR:
			ret = dcdauthPuGenPskComboOr(psk, charPattern, length);
			break;
		case LocalUserGroup.PSKFORMAT_COMBO_AND:
			ret = dcdauthPuGenPskComboAnd(psk, charPattern, length);
			break;
		case LocalUserGroup.PSKFORMAT_COMBO_NOCOMBO:
			ret = dcdauthPuGenPskComboNo(psk, charPattern, length);
			break;
		default:
			break;
		}
		return ret;
	}

	private int dcdauthPuGenPskComboOr(byte[] psk, int charPattern, int length) {
		String str;
		switch (charPattern) {
		case PU_CHAR_PATTERN_LETTERS:
			str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
			break;
		case PU_CHAR_PATTERN_DIGITS:
			str = "0123456789";
			break;
		case PU_CHAR_PATTERN_SPECIAL_CHARS:
			str = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			break;
		case PU_CHAR_PATTERN_LETTERS + PU_CHAR_PATTERN_DIGITS:
			str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
			break;
		case PU_CHAR_PATTERN_LETTERS + PU_CHAR_PATTERN_SPECIAL_CHARS:
			str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			break;
		case PU_CHAR_PATTERN_DIGITS + PU_CHAR_PATTERN_SPECIAL_CHARS:
			str = "0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			break;
		case PU_CHAR_PATTERN_LETTERS + +PU_CHAR_PATTERN_DIGITS
				+ PU_CHAR_PATTERN_SPECIAL_CHARS:
			str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
			break;
		default:
			return -1;
		}
		byte[] buf = str.getBytes();
		int bufLen = buf.length;
		int pskValue;
		for (int i = 0; i < length; i++) {
			pskValue = (psk[i] >= 0) ? psk[i] : (psk[i] + 256);
			psk[i] = buf[pskValue % bufLen];
		}
		return 0;
	}

	private int dcdauthPuGenPskComboAnd(byte[] psk, int charPattern, int length) {
		String[] strArray = new String[3];
		byte[][] byteArray = new byte[3][];
		int byteArrayLength = 0;

		if (0 != (charPattern & PU_CHAR_PATTERN_LETTERS))
			strArray[0] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		if (0 != (charPattern & PU_CHAR_PATTERN_DIGITS))
			strArray[1] = "0123456789";
		if (0 != (charPattern & PU_CHAR_PATTERN_SPECIAL_CHARS))
			strArray[2] = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		if (null == strArray[0] && null == strArray[1] && null == strArray[2])
			return -1;

		if (null != strArray[0]) {
			byteArray[byteArrayLength] = strArray[0].getBytes();
			byteArrayLength++;
		}
		if (null != strArray[1]) {
			byteArray[byteArrayLength] = strArray[1].getBytes();
			byteArrayLength++;
		}
		if (null != strArray[2]) {
			byteArray[byteArrayLength] = strArray[2].getBytes();
			byteArrayLength++;
		}
		int pskValue;
		for (int i = 0; i < length; i++) {
			pskValue = (psk[i] >= 0) ? psk[i] : (psk[i] + 256);
			int j = (i / 3) % byteArrayLength;
			psk[i] = byteArray[j][pskValue % byteArray[j].length];
		}
		return 0;
	}

	private int dcdauthPuGenPskComboNo(byte[] psk, int charPattern, int length) {
		String str;

		if (0 != (charPattern & PU_CHAR_PATTERN_LETTERS))
			str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		else if (0 != (charPattern & PU_CHAR_PATTERN_DIGITS))
			str = "0123456789";
		else if (0 != (charPattern & PU_CHAR_PATTERN_SPECIAL_CHARS))
			str = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		else
			return -1;
		byte[] buf = str.getBytes();
		int bufLen = buf.length;
		int pskValue;
		for (int i = 0; i < length; i++) {
			pskValue = (psk[i] >= 0) ? psk[i] : (psk[i] + 256);
			psk[i] = buf[pskValue % bufLen];
		}
		return 0;
	}

	@Transient
	final byte NUM_OF_PRINTABLE_CHARS = 94;
	@Transient
	final byte THE_FIRST_PRINTABLE_CHAR = '!';

	/*
	 * digest generator
	 */
	private String digestGenerator(String password) {
		MessageDigest digest;
		byte[] tmp_buf;
		try {
			// tmp_buf = new byte[20];
			digest = MessageDigest.getInstance("SHA-1");
			digest.update(password.getBytes());
			tmp_buf = digest.digest();
			if (tmp_buf == null || tmp_buf.length != 20)
				return null;
			byte[] out_buf = new byte[5];
			int int_temp;
			for (int i = 0; i < 5; i++) {
				out_buf[i] = (byte) (tmp_buf[i] ^ tmp_buf[i + 5]
						^ tmp_buf[i + 10] ^ tmp_buf[i + 15]);
				int_temp = (out_buf[i] >= 0) ? out_buf[i] : (256 + out_buf[i]);
				out_buf[i] = (byte) (int_temp % NUM_OF_PRINTABLE_CHARS);
				out_buf[i] += THE_FIRST_PRINTABLE_CHAR;
			}
			return new String(out_buf);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/*
	 * get passwd-digest and PSK-digest
	 */
	public String[] getPasswdAndPSkDigest(String password) {
		String[] str_Return = new String[2];
		// get passwd-digest
		String digest = digestGenerator(password);
		str_Return[0] = digest;
		// get PSK-digest
		if (localUserGroup.getPskGenerateMethod() == LocalUserGroup.PSK_METHOD_PASSWORD_USERNAME) {
			StringBuilder strBuf = new StringBuilder();
			strBuf.append(getUserName())
					.append(getLocalUserGroup().getConcatenateString())
					.append(password);
			digest = digestGenerator(strBuf.toString());
		}
		str_Return[1] = digest;
		return str_Return;
	}

	public String getUserStatusString() {
		for (EnumItem status : ENUM_STATUS) {
			if (status.getKey() == this.status)
				return status.getValue();
		}

		return "-";
	}

	/**
	 * @return the defaultFlag
	 */
	public boolean isDefaultFlag() {
		return defaultFlag;
	}

	/**
	 * @param defaultFlag
	 *            the defaultFlag to set
	 */
	public void setDefaultFlag(boolean defaultFlag) {
		this.defaultFlag = defaultFlag;
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}