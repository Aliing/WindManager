/**
 *@filename		LocalUserGroup.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-25 PM 02:53:41
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.useraccess;

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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.validator.constraints.Range;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.wlan.Scheduler;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Entity
@Table(name = "LOCAL_USER_GROUP")
@org.hibernate.annotations.Table(appliesTo = "LOCAL_USER_GROUP", indexes = {
		@Index(name = "LOCAL_USER_GROUP_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class LocalUserGroup implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final int MAX_COUNT_AP_USERGROUPPERAP=512;

	// for the users have no user group before 3.2r2
	public static final String DEFAULT_GROUP_NAME_FOR_RESTORE = "default_radius_user_group";
	
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
	private String groupName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description="";

	private int userProfileId = -1;
	private int vlanId = -1;
	private int reauthTime = 1800;
	
	// fnr add for private psk
	public static final int USERGROUP_USERTYPE_RADIUS = 1;
	public static final int USERGROUP_USERTYPE_AUTOPSK = 2;
	public static final int USERGROUP_USERTYPE_MANUALLYPSK = 3;
	private int userType = USERGROUP_USERTYPE_RADIUS;
	
	public static final int USERGROUP_CREDENTIAL_FLASH = 1;
	public static final int USERGROUP_CREDENTIAL_DRAM = 2;
	private int credentialType = USERGROUP_CREDENTIAL_FLASH;
	
	private String userNamePrefix="";
	private String pskSecret="";
	private String pskLocation="";
	
	@Range(min = 8, max = 63)
	private int pskLength=8;
	
	public static final int PSK_METHOD_PASSWORD_ONLY = 1;
	public static final int PSK_METHOD_PASSWORD_USERNAME = 2;
	public static EnumItem[] ENUM_PSK_METHOD = MgrUtil
			.enumItems("enum.psk.method.", new int[] { PSK_METHOD_PASSWORD_ONLY,
					PSK_METHOD_PASSWORD_USERNAME });
	private int pskGenerateMethod=PSK_METHOD_PASSWORD_ONLY;
	
	private String concatenateString="#";
	
	public static final int VALIDTYME_TYPE_ALWAYS = 0;
	public static final int VALIDTYME_TYPE_ONCE = 1;
	public static final int VALIDTYME_TYPE_SCHEDULE = 2;
	public static EnumItem[] ENUM_VALIDTIME_TYPE = MgrUtil
			.enumItems("enum.validTime.type.", new int[] { VALIDTYME_TYPE_ALWAYS, VALIDTYME_TYPE_ONCE,
					VALIDTYME_TYPE_SCHEDULE });
	private int validTimeType=VALIDTYME_TYPE_ALWAYS;
	
	private String timeZoneStr = "America/Los_Angeles";
	
	private Date startTime;
	
	private Date expiredTime;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCHEDULE_ID")
	private Scheduler schedule;
	
	@Range(min = 1, max = 9999)
	private int bulkNumber=1;
	
	@Range(min = 1, max = 9999)
	private int indexRange=10;
	
	@Range(min = 0, max = 365)
	private int intervalDay=0;
	private int intervalHour=0;
	private int intervalMin=0;
	
	private boolean blnBulkType;
	
	private boolean blnCharDigits=true;
	private boolean blnCharLetters=true;
	private boolean blnCharSpecial=true;

	public static final int PSKFORMAT_COMBO_OR = 0;
	public static final int PSKFORMAT_COMBO_AND = 1;
	public static final int PSKFORMAT_COMBO_NOCOMBO = 2;
	private int personPskCombo = PSKFORMAT_COMBO_AND;
	
	private boolean voiceDevice = false;
	
	public static EnumItem[] ENUM_PSKFORMAT_COMBO_TYPE = MgrUtil
	.enumItems("enum.pskformat.combo.type.", new int[] { PSKFORMAT_COMBO_AND, PSKFORMAT_COMBO_OR,
			PSKFORMAT_COMBO_NOCOMBO });

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
		return groupName;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public String getGroupNameSubstr() {
		if (groupName==null) {
			return "";
		}
		if (groupName.length()> BaseAction.DISPLAY_LENGTH_IN_GUI_OK) {
			return groupName.substring(0, BaseAction.DISPLAY_LENGTH_IN_GUI) + "...";
		}
		
		return groupName;
	}

	public void setGroupName(String groupName) {
		if (groupName!=null){
			this.groupName = groupName.trim();
		} else {
			this.groupName = groupName;
		}
	}

	public int getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(int userProfileId) {
		this.userProfileId = userProfileId;
	}

	public int getVlanId() {
		return vlanId;
	}

	public void setVlanId(int vlanId) {
		this.vlanId = vlanId;
	}

	public int getReauthTime() {
		return reauthTime;
	}

	public void setReauthTime(int reauthTime) {
		this.reauthTime = reauthTime;
	}

	@Transient
	public String getValue() {
		return groupName;
	}

	@Transient
	public String getStrUserProfileId() {
		return userProfileId == -1 ? "" : String.valueOf(userProfileId);
	}

	@Transient
	public String getStrVlanId() {
		return vlanId == -1 ? "" : String.valueOf(vlanId);
	}

	@Transient
	public String getStrReauthTime() {
		return reauthTime == -1 ? "" : String.valueOf(reauthTime);
	}

	public int getUserType() {
		return userType;
	}
	
	public String getStrUserType(){
		if (userType == USERGROUP_USERTYPE_RADIUS) {
			return "RADIUS";
		} else if (userType == USERGROUP_USERTYPE_AUTOPSK){
			return "Private PSK-Auto";
		} else {
			return "Private PSK-Manual";
		}
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public int getCredentialType() {
		return credentialType;
	}

	public void setCredentialType(int credentialType) {
		this.credentialType = credentialType;
	}

	public String getUserNamePrefix() {
		return userNamePrefix;
	}

	public void setUserNamePrefix(String userNamePrefix) {
		if (userNamePrefix!=null){
			this.userNamePrefix = userNamePrefix.trim();
		} else {
			this.userNamePrefix = userNamePrefix;
		}
	}

	public String getPskSecret() {
		return pskSecret;
	}

	public void setPskSecret(String pskSecret) {
		this.pskSecret = pskSecret;
	}

	public String getPskLocation() {
		return pskLocation;
	}

	public void setPskLocation(String pskLocation) {
		if (pskLocation!=null){
			this.pskLocation = pskLocation.trim();
		} else {
			this.pskLocation = pskLocation;
		}
	}

	public int getPskLength() {
		return pskLength;
	}

	public void setPskLength(int pskLength) {
		this.pskLength = pskLength;
	}

	public int getValidTimeType() {
		return validTimeType;
	}

	public void setValidTimeType(int validTimeType) {
		this.validTimeType = validTimeType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public Scheduler getSchedule() {
		return schedule;
	}

	public void setSchedule(Scheduler schedule) {
		this.schedule = schedule;
	}

	public boolean getBlnCharDigits() {
		return blnCharDigits;
	}

	public void setBlnCharDigits(boolean blnCharDigits) {
		this.blnCharDigits = blnCharDigits;
	}

	public boolean getBlnCharLetters() {
		return blnCharLetters;
	}

	public void setBlnCharLetters(boolean blnCharLetters) {
		this.blnCharLetters = blnCharLetters;
	}

	public boolean getBlnCharSpecial() {
		return blnCharSpecial;
	}

	public void setBlnCharSpecial(boolean blnCharSpecial) {
		this.blnCharSpecial = blnCharSpecial;
	}

	public int getPersonPskCombo() {
		return personPskCombo;
	}

	public void setPersonPskCombo(int personPskCombo) {
		this.personPskCombo = personPskCombo;
	}
	
	public int getPskGenerateMethod() {
		return pskGenerateMethod;
	}

	public void setPskGenerateMethod(int pskGenerateMethod) {
		this.pskGenerateMethod = pskGenerateMethod;
	}

	public String getConcatenateString() {
		return concatenateString;
	}

	public void setConcatenateString(String concatenateString) {
		if (concatenateString!=null){
			this.concatenateString = concatenateString.trim();
		} else {
			this.concatenateString = concatenateString;
		}
	}
	
	public long getCurrentTimeOffset(Calendar nowCalendar){
		long offset =nowCalendar.get(Calendar.ZONE_OFFSET)/1000 + nowCalendar.get(Calendar.DST_OFFSET)/1000 -  getTimeZoneOffSet(nowCalendar);
		return offset *1000;
	}
	
	public long getBulkUserCount(){
		if (userType==USERGROUP_USERTYPE_AUTOPSK && validTimeType ==VALIDTYME_TYPE_SCHEDULE && blnBulkType) {
			Calendar aa = Calendar.getInstance();
			aa.setTimeInMillis(startTime.getTime());
			long startoffset = getCurrentTimeOffset(aa);
			aa.setTimeInMillis(expiredTime.getTime());
			long endoffset = getCurrentTimeOffset(aa);
			
			long dayLightSavingBeginandEndtime = LocalUser.getDayLightSaveing(startTime.getTime() + startoffset,
					expiredTime.getTime() + endoffset, getGroupTimeZone());
			
			long intervalBulk = intervalDay*24 * 60 * 60 * 1000L + intervalHour * 60 * 60 *1000L + intervalMin *60 *1000L;
			long allTime = expiredTime.getTime() + endoffset - startTime.getTime() - startoffset  - dayLightSavingBeginandEndtime;
			long bulktimes = (allTime%intervalBulk==0)? (allTime/intervalBulk):(allTime/intervalBulk +1);
			return indexRange * (bulkNumber>bulktimes? bulktimes: bulkNumber);
		}
		return 0;
	}

	public  long getTimeZoneOffSet(Calendar cale){
		try{
			TimeZone tz = TimeZone.getTimeZone(timeZoneStr);
			Calendar retCale = Calendar.getInstance(tz);
			retCale.clear(Calendar.MILLISECOND);
			retCale.set(Calendar.YEAR,cale.get(Calendar.YEAR));
			retCale.set(Calendar.MONTH,cale.get(Calendar.MONTH));
			retCale.set(Calendar.DAY_OF_MONTH,cale.get(Calendar.DAY_OF_MONTH));
			retCale.set(Calendar.HOUR_OF_DAY,cale.get(Calendar.HOUR_OF_DAY));
			retCale.set(Calendar.MINUTE,cale.get(Calendar.MINUTE));
			retCale.set(Calendar.SECOND,cale.get(Calendar.SECOND));
			return retCale.get(Calendar.ZONE_OFFSET)/1000 + retCale.get(Calendar.DST_OFFSET)/1000;
		} catch (Exception e) {
			DebugUtil .commonDebugWarn( "Local User get PSK convert time zone catch IOException: ", e);
			return 0;
		}
	}
	
	public TimeZone getGroupTimeZone(){
		try{
			return TimeZone.getTimeZone(timeZoneStr);
			//return (tz.getRawOffset() + tz.getDSTSavings())/1000;
		} catch (Exception e) {
			DebugUtil .commonDebugWarn( "Local User get PSK convert time zone catch IOException: ", e);
			return TimeZone.getTimeZone("America/Los_Angeles");
		}
	}

	/**
	 * @return the bulkNumber
	 */
	public int getBulkNumber() {
		return bulkNumber;
	}

	/**
	 * @param bulkNumber the bulkNumber to set
	 */
	public void setBulkNumber(int bulkNumber) {
		this.bulkNumber = bulkNumber;
	}

	/**
	 * @return the intervalDay
	 */
	public int getIntervalDay() {
		return intervalDay;
	}

	/**
	 * @param intervalDay the intervalDay to set
	 */
	public void setIntervalDay(int intervalDay) {
		this.intervalDay = intervalDay;
	}

	/**
	 * @return the intervalHour
	 */
	public int getIntervalHour() {
		return intervalHour;
	}

	/**
	 * @param intervalHour the intervalHour to set
	 */
	public void setIntervalHour(int intervalHour) {
		this.intervalHour = intervalHour;
	}

	/**
	 * @return the intervalMin
	 */
	public int getIntervalMin() {
		return intervalMin;
	}

	/**
	 * @param intervalMin the intervalMin to set
	 */
	public void setIntervalMin(int intervalMin) {
		this.intervalMin = intervalMin;
	}

	/**
	 * @return the blnBulkType
	 */
	public boolean isBlnBulkType() {
		return blnBulkType;
	}

	/**
	 * @param blnBulkType the blnBulkType to set
	 */
	public void setBlnBulkType(boolean blnBulkType) {
		this.blnBulkType = blnBulkType;
	}

	/**
	 * @return the indexRange
	 */
	public int getIndexRange() {
		return indexRange;
	}

	/**
	 * @param indexRange the indexRange to set
	 */
	public void setIndexRange(int indexRange) {
		this.indexRange = indexRange;
	}

	public String getTimeZoneStr()
	{
		return timeZoneStr;
	}

	public void setTimeZoneStr(String timeZoneStr)
	{
		this.timeZoneStr = timeZoneStr;
	}
	
	@Transient
	private int	timezone = HmBeOsUtil.getServerTimeZoneIndex(null);

	public int getTimezone()
	{
		return timezone;
	}

	public void setTimezone(int timezone)
	{
		this.timezone = timezone;
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

	public boolean isVoiceDevice() {
		return voiceDevice;
	}

	public void setVoiceDevice(boolean voiceDevice) {
		this.voiceDevice = voiceDevice;
	}
}