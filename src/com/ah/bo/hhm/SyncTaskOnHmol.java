package com.ah.bo.hhm;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "sync_task_on_hmol")
@org.hibernate.annotations.Table(appliesTo = "sync_task_on_hmol", indexes = { @Index(name = "sctk_idx1", columnNames = { "vhmName",
		"createTime" }) })
public class SyncTaskOnHmol implements HmBo {

	private static final long	serialVersionUID		= 1L;

	public static final short	SYNC_FOR_CREATE_VHMUSER	= 1;
	public static final short	SYNC_FOR_MODIFY_VHMUSER	= 2;
	public static final short	SYNC_FOR_REMOVE_VHMUSER	= 3;

	@Id
	@GeneratedValue
	private Long				id;

	@Version
	private Timestamp				version;

	@Transient
	private boolean				selected				= false;

	private long				createTime;

	/*
	 * 1- sync for create VHM user 2- sync for modify VHM user info 3- sync for remove VHM user 4-
	 * sync for modify VHM user password
	 */
	private short				syncType;

	private String				vhmName;

	private String				vhmUsername;

	private long				syncTime;

	private int					syncTimes;

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public short getSyncType() {
		return syncType;
	}

	public void setSyncType(short syncType) {
		this.syncType = syncType;
	}

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public String getVhmUsername() {
		return vhmUsername;
	}

	public void setVhmUsername(String vhmUsername) {
		this.vhmUsername = vhmUsername;
	}

	public long getSyncTime() {
		return syncTime;
	}

	public void setSyncTime(long syncTime) {
		this.syncTime = syncTime;
	}

	public int getSyncTimes() {
		return syncTimes;
	}

	public void setSyncTimes(int syncTimes) {
		this.syncTimes = syncTimes;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Transient
	public String getSyncTypeDesc() {
		String msg;
		switch (syncType) {
		case SYNC_FOR_CREATE_VHMUSER:
			msg = "Create VHM User";
			break;
		case SYNC_FOR_MODIFY_VHMUSER:
			msg = "Modify VHM User";
			break;
		case SYNC_FOR_REMOVE_VHMUSER:
			msg = "Remove VHM User";
			break;
		default:
			msg = "Unknown sync type";
			break;
		}
		return msg;
	}

	@Transient
	public String getSyncTaskDatas() {
		String dataString = this.getSyncTypeDesc() + ", Task Create Time:"
				+ AhDateTimeUtil.getFormatDateTime(this.getCreateTime(), "MM-dd-yyyy HH:mm:ss");
		dataString = dataString + ", Domain Name:" + this.vhmName;
		dataString = dataString + ", User Name:" + this.vhmUsername;
		return dataString;
	}

}