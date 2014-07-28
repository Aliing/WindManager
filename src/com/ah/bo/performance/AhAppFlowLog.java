package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.be.performance.appreport.AhReportCollectData;
import com.ah.bo.admin.HmDomain;


@Entity
@Table(name = "hm_repo_app_data_all")
@org.hibernate.annotations.Table(appliesTo = "hm_repo_app_data_all", indexes = {
		@Index(name = "IDX_REPO_APP_DATA_ALL_TIMESTAMP", columnNames ={"TIMESTAMP", "OWNER", "APMAC"})
	    })

public class AhAppFlowLog implements AhReportCollectData {
	
	private static final long serialVersionUID = 1L;	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	
	private int appCode;
	
	private long bytes;
	
	private long packets;
	
	@Column(name = "owner")
	private long ownerId;
	
	private long timeStamp;
	
	@Column(length = 20)
	private String apMac;
	
	@Transient
	private long startTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public int getAppCode() {
		return appCode;
	}

	public void setAppCode(int appCode) {
		this.appCode = appCode;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public long getPackets() {
		return packets;
	}

	public void setPackets(long packets) {
		this.packets = packets;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		
	}

	@Override
	public HmDomain getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		// TODO Auto-generated method stub
		
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
}