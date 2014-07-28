package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "AP_CONNECT_HISTORY_INFO")
@org.hibernate.annotations.Table(appliesTo = "AP_CONNECT_HISTORY_INFO", indexes = {
		@Index(name = "AP_CONNECT_HISTORY_INFO_OWNER", columnNames = { "OWNER" }),
		@Index(name = "AP_CONNECT_HISTORY_INFO_TIME", columnNames = { "trapTime" })
		})
public class APConnectHistoryInfo implements HmBo {
	
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long				id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain			owner;

	@Version
	private Timestamp				version;

	// above id,owner and version is general attributes

	public static final short	TRAP_CONNECT		= 1;
	public static final short	TRAP_DISCONNECT		= 2;

	private String				apId;

	private String				apName;

	private long				trapTime;

	private short				trapType			= TRAP_DISCONNECT;

	private String				trapMessage;

	private Long				mapId;
	
	@Transient
	private TimeZone tz;
	
	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

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

	public long getTrapTime() {
		return trapTime;
	}

	public void setTrapTime(long trapTime) {
		this.trapTime = trapTime;
	}

	public short getTrapType() {
		return trapType;
	}

	public void setTrapType(short trapType) {
		this.trapType = trapType;
	}
	
	public String getTrapTypeString(){
		if (trapType==TRAP_DISCONNECT) {
			return "Connection Down";
		}
		return "Connection Up";
	}
	
	public String getTrapTimeString(){
		return AhDateTimeUtil.getSpecifyDateTimeReport(trapTime, tz);
	}

	public String getTrapMessage() {
		return trapMessage;
	}

	public void setTrapMessage(String trapMessage) {
		this.trapMessage = trapMessage;
	}

	public Long getMapId() {
		return mapId;
	}

	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}

	// below is general methods
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

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}
	
}