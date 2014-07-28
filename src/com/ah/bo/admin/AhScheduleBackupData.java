/**
 *@filename		AhScheduleBackup.java
 *@version
 *@author		Xiaolanbao
 *@createtime	2007-10-9 10:39:13 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

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

import com.ah.bo.HmBo;
import com.ah.util.EnumConstUtil;

/**
 * @author Xiaolanbao
 * @version V2.2.0.0
 */

@Entity
@Table(name = "SCHEDULE_BACKUP")
public class AhScheduleBackupData implements HmBo {

	private static final long serialVersionUID = 1L;

	private static final short	MAX_SCP_PARA_LENGTH	= 512;

	@Id
	@GeneratedValue
	private Long				id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain			owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp			version;

	@Transient
	private boolean				selected;

	// backupcontent enum value definition
	// 0: full backup 1: not include events/alarms
	public static final short	BACKUPCONTENT_FULLBACKUP	= 0;

	public static final short	BACKUPCONTENT_PARTLYBACKUP	= 1;

	private short				backupContent;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String				startDate;

	private short				startHour;

	private short				startMinute;

	private boolean				endDateFlag;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String				endDate;

	private short				endHour;

	private short				endMinute;

	private boolean				rescurFlag;

	private int					interval;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String				scpIpAdd;

	private int					scpPort;

	@Column(length = MAX_SCP_PARA_LENGTH)
	private String				scpFilePath;

	@Column(length = MAX_SCP_PARA_LENGTH)
	private String				scpUsr;

	@Column(length = MAX_SCP_PARA_LENGTH)
	private String				scpPsd;

	private boolean				liveFlag;

	private short				protocol					= EnumConstUtil.RESTORE_PROTOCOL_SCP;

	private String               backupType;
	public String getBackupType() {
		return backupType;
	}

	public void setBackupType(String backupType) {
		this.backupType = backupType;
	}

	public short getProtocol() {
		return protocol;
	}

	public void setProtocol(short protocol) {
		this.protocol = protocol;
	}

	public short getBackupContent() {
		return this.backupContent;
	}

	public void setBackupContent(short sContent) {
		this.backupContent = sContent;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String strDate) {
		this.startDate = strDate;
	}

	public short getStartHour() {
		return this.startHour;
	}

	public void setStartHour(short sHour) {
		this.startHour = sHour;
	}

	public short getStartMinute() {
		return this.startMinute;
	}

	public void setStartMinute(short sMinute) {
		this.startMinute = sMinute;
	}

	public boolean getEndDateFlag() {
		return this.endDateFlag;
	}

	public void setEndDateFlag(boolean bFlag) {
		this.endDateFlag = bFlag;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String strDate) {
		this.endDate = strDate;
	}

	public short getEndHour() {
		return this.endHour;
	}

	public void setEndHour(short sHour) {
		this.endHour = sHour;
	}

	public short getEndMinute() {
		return this.endMinute;
	}

	public void setEndMinute(short sMinute) {
		this.endMinute = sMinute;
	}

	public boolean getRescurFlag() {
		return this.rescurFlag;
	}

	public void setRescurFlag(boolean bFlag) {
		this.rescurFlag = bFlag;
	}

	public int getInterval() {
		return this.interval;
	}

	public void setInterval(int iInterval) {
		this.interval = iInterval;
	}

	public String getScpIpAdd() {
		return this.scpIpAdd;
	}

	public void setScpIpAdd(String strScpIpAdd) {
		this.scpIpAdd = strScpIpAdd;
	}

	public int getScpPort() {
		return this.scpPort;
	}

	public void setScpPort(int iPort) {
		this.scpPort = iPort;
	}

	public String getScpFilePath() {
		return this.scpFilePath;
	}

	public void setScpFilePath(String strFilePath) {
		this.scpFilePath = strFilePath;
	}

	public String getScpUsr() {
		return this.scpUsr;
	}

	public void setScpUsr(String strScpUsr) {
		this.scpUsr = strScpUsr;
	}

	public String getScpPsd() {
		return this.scpPsd;
	}

	public void setScpPsd(String strPsd) {
		this.scpPsd = strPsd;
	}

	public boolean getLiveFlag() {
		return this.liveFlag;
	}

	public void setLiveFlag(boolean bFlag) {
		this.liveFlag = bFlag;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "schedule_backup";
	}

	@Override
	public Timestamp getVersion() {
		return this.version;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
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

}