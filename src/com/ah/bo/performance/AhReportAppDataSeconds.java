package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.be.performance.appreport.AhReportCollectData;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_REPO_APP_DATA")
@org.hibernate.annotations.Table(appliesTo = "HM_REPO_APP_DATA", indexes = {
//	@Index(name = "IDX_HM_REPO_APP_DATA_TIMESTAMP", columnNames = {"TIMESTAMP", "APMAC"}),
//	@Index(name = "IDX_HM_REPO_APP_DATA_OWNER", columnNames = {"OWNER"})
	@Index(name = "IDX_HM_REPO_APP_DATA_TIMESTAMP", columnNames = {"OWNER", "APMAC", "TIMESTAMP" }),
	@Index(name = "IDX_HM_REPO_APP_DATA_APPLICATION", columnNames = { "APPLICATION" }),
	@Index(name = "IDX_HM_REPO_APP_DATA_USERNAME", columnNames = { "USERNAME" })	
	})
public class AhReportAppDataSeconds implements AhReportCollectData {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "owner")
	private long ownerId;
	
	private String apmac;
	
	private long timestamp ;

	@Column(length = 20)
	private String clientmac;
	
	private long bytes;
	
	private short application;
	
	@Column(length = 32)
	private String ssid;
	
	private String username;
	
	private long seconds;
	
	private short appSeconds;
	
	@Column(length = 32)
	private String userProfileName;
	
//	@Column(length = 64)
//	private String osname;
//	
//	@Column(length = 128, name="app_hostname")
//	private String hostname;
//	 
//	private long vlan;

//	@Column(nullable = true)
//	private short port;   //  1 for radio 0, 2 for radio 1, 0, -1, ... for eth0, eth1, ...

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public short getAppSeconds() {
		return appSeconds;
	}

	public void setAppSeconds(short appSeconds) {
		this.appSeconds = appSeconds;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public String getApmac() {
		return apmac;
	}

	public void setApmac(String apmac) {
		this.apmac = apmac;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getClientmac() {
		return clientmac;
	}

	public void setClientmac(String clientmac) {
		this.clientmac = clientmac;
	}

	public long getBytes() {
		return bytes;
	}

	public void setBytes(long bytes) {
		this.bytes = bytes;
	}

	public short getApplication() {
		return application;
	}

	public void setApplication(short application) {
		this.application = application;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getSeconds() {
		return seconds;
	}

	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}

	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
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

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		
	}

}
