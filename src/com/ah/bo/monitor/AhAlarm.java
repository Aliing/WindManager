package com.ah.bo.monitor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

/*
 * @author Chris Scheers
 */

@Entity
@Table(name = "AH_ALARM")
@org.hibernate.annotations.Table(appliesTo = "AH_ALARM", indexes = {
		@Index(name = "IDX_ALARM_OWNER", columnNames = { "OWNER" }),
		@Index(name = "AH_ALARM_AP_ID", columnNames = { "APID" }),
		@Index(name = "IDX_ALARM_TYPE_SUBTYPE", columnNames = { "ALARMTYPE", "ALARMSUBTYPE" })
		})
public class AhAlarm implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apId;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = 64)
	private String				objectName;

	private int					code;

	private String				trapDesc;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "time", column = @Column(name = "TRAP_TIME", nullable = false)),
			@AttributeOverride(name = "timeZone", column = @Column(name = "TRAP_TIME_ZONE")) })
	private HmTimeStamp			trapTimeStamp								= HmTimeStamp.ZERO_TIMESTAMP;

	public static final short	AH_SEVERITY_UNDETERMINED					= 1;

	// public static final short AH_SEVERITY_INFO = 2;

	public static final short	AH_SEVERITY_MINOR							= 3;

	public static final short	AH_SEVERITY_MAJOR							= 4;

	public static final short	AH_SEVERITY_CRITICAL						= 5;

	public static EnumItem[]	ENUM_AH_SEVERITY							= MgrUtil
																					.enumItems(
																							"enum.severity.",
																							new int[] {
			AH_SEVERITY_CRITICAL, AH_SEVERITY_MAJOR, AH_SEVERITY_MINOR, // AH_SEVERITY_INFO,
			AH_SEVERITY_UNDETERMINED														});

	/*
	 * AhProbableCause
	 */
	public static final short	AH_PROBABLE_CAUSE_CLEAR						= 0;

	public static final short	AH_PROBABLE_CAUSE_UNKNOWN					= 1;

	public static final short	AH_PROBABLE_CAUSE_FLASH_FAILURE				= 2;

	public static final short	AH_PROBABLE_CAUSE_FAN_FAILURE				= 3;

	public static final short	AH_PROBABLE_CAUSE_POWER_SUPPLY_FAILURE		= 4;

	public static final short	AH_PROBABLE_CAUSE_SOFTWARE_UPGRADE_FAILURE	= 5;

	public static final short	AH_PROBABLE_CAUSE_RADIO_FAILURE				= 6;

	private short				severity;

	private short				alarmType;																		// 1:snmp-radio
	// ;
	// 2:snmp-config
	// ; 3:
	// capwap

	private short				alarmSubType;
	
	@Column(nullable = true)
	private int					tag1 = 0;
	
	@Column(nullable = true)
	private int					tag2 = 0;
	
	private String				tag3 = "";
	
	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "time", column = @Column(name = "MODIFY_TIME", nullable = false)),
			@AttributeOverride(name = "timeZone", column = @Column(name = "MODIFY_TIME_ZONE")) })
	private HmTimeStamp			modifyTimeStamp								= HmTimeStamp.ZERO_TIMESTAMP;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "time", column = @Column(name = "CLEAR_TIME", nullable = false)),
			@AttributeOverride(name = "timeZone", column = @Column(name = "CLEAR_TIME_ZONE")) })
	private HmTimeStamp			clearTimeStamp								= HmTimeStamp.ZERO_TIMESTAMP;

	public String getApId() {
		return apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getTrapDesc() {
		return trapDesc;
	}

	/**
	 * This function is just for display at GUI.
	 * 
	 * @return -
	 * @author Joseph Chen
	 */
	public String getTrapDescString() {
		return trapDesc.replaceAll("\n", "<br />");
	}

	public void setTrapDesc(String trapDesc) {
		this.trapDesc = trapDesc;
	}

	public String getTrapTimeStringFromBE() {
	    if (trapTimeStamp == null || trapTimeStamp.getTime() <= 0) {
	        return "-";
        }
        return AhDateTimeUtil.getFormattedDateTime(getTrapTimeStamp());
	}
	
	public String getTrapTimeString() {
		if (trapTimeStamp == null || trapTimeStamp.getTime() <= 0) {
			return "-";
		}
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getFormattedDateTime(getTrapTimeStamp(), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getFormattedDateTime(getTrapTimeStamp());
		}
	}

	@Transient
	public String getTrapTimeExcel() {
		if (trapTimeStamp == null || trapTimeStamp.getTime() <= 0) {
			return "-";
		}

		return MgrUtil.getExcelDateTimeString(new Date(trapTimeStamp.getTime()), TimeZone
				.getTimeZone(trapTimeStamp.getTimeZone()));
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
	public String getLabel() {
		return apName;
	}

	public short getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(short alarmType) {
		this.alarmType = alarmType;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	public short getSeverity() {
		return severity;
	}

	public String getSeverityString() {
		if (severity < AH_SEVERITY_UNDETERMINED || severity > AH_SEVERITY_CRITICAL) {
			return "";
		} else {
			return MgrUtil.getEnumString("enum.severity." + severity);
		}
	}

	public void setSeverity(short severity) {
		this.severity = severity;
	}

	public String getSeverityColor() {
		String retColor;
		switch (severity) {
		case 1:
			retColor = "0x33FF33";
			break;
		case 3:
			retColor = "0xFFFF33";
			break;
		case 4:
			retColor = "0xFF9933";
			break;
		case 5:
			retColor = "0xEE3424";
			break;
		default:
			retColor = "0xFFFFFF";
		}

		return retColor;
	}

	@Transient
	public String[] getFieldValues() {
		return new String[] { "id", "apid", "apname", "code", "objectname", "trapdesc",
				"traptime", "alarmtype", "modifytime", "probablecause", "severity", "clearTime" };
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public String getClearTimeStringFromBE() {
	    if (clearTimeStamp == null || clearTimeStamp.getTime() <= 0) {
	        return "-";
	    }
	    return AhDateTimeUtil.getFormattedDateTime(getClearTimeStamp());
	}
	
	public String getClearTimeString() {
		if (clearTimeStamp == null || clearTimeStamp.getTime() <= 0) {
			return "-";
		}
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getFormattedDateTime(getClearTimeStamp(), loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getFormattedDateTime(getClearTimeStamp());
		}
	}

	/**
	 * getter of trapTimeStamp
	 * 
	 * @return the trapTimeStamp
	 */
	public HmTimeStamp getTrapTimeStamp() {
		return trapTimeStamp;
	}

	/**
	 * setter of trapTimeStamp
	 * 
	 * @param trapTimeStamp
	 *            the trapTimeStamp to set
	 */
	public void setTrapTimeStamp(HmTimeStamp trapTimeStamp) {
		if (trapTimeStamp == null) {
			this.trapTimeStamp = HmTimeStamp.ZERO_TIMESTAMP;
		} else {
			this.trapTimeStamp = trapTimeStamp;
		}
	}

	/**
	 * getter of modifyTimeStamp
	 * 
	 * @return the modifyTimeStamp
	 */
	public HmTimeStamp getModifyTimeStamp() {
		return modifyTimeStamp;
	}

	/**
	 * setter of modifyTimeStamp
	 * 
	 * @param modifyTimeStamp
	 *            the modifyTimeStamp to set
	 */
	public void setModifyTimeStamp(HmTimeStamp modifyTimeStamp) {
		if (modifyTimeStamp == null) {
			this.modifyTimeStamp = HmTimeStamp.ZERO_TIMESTAMP;
		} else {
			this.modifyTimeStamp = modifyTimeStamp;
		}
	}

	/**
	 * getter of clearTimeStamp
	 * 
	 * @return the clearTimeStamp
	 */
	public HmTimeStamp getClearTimeStamp() {
		return clearTimeStamp;
	}

	/**
	 * setter of clearTimeStamp
	 * 
	 * @param clearTimeStamp
	 *            the clearTimeStamp to set
	 */
	public void setClearTimeStamp(HmTimeStamp clearTimeStamp) {
		if (clearTimeStamp == null) {
			this.clearTimeStamp = HmTimeStamp.ZERO_TIMESTAMP;
		} else {
			this.clearTimeStamp = clearTimeStamp;
		}
	}

	public short getAlarmSubType() {
		return alarmSubType;
	}

	public void setAlarmSubType(short alarmSubType) {
		this.alarmSubType = alarmSubType;
	}

	public int getTag1() {
		return tag1;
	}

	public void setTag1(int tag1) {
		this.tag1 = tag1;
	}

	public int getTag2() {
		return tag2;
	}

	public void setTag2(int tag2) {
		this.tag2 = tag2;
	}

	public String getTag3() {
		return tag3;
	}

	public void setTag3(String tag3) {
		this.tag3 = tag3;
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}