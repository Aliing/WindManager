package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.ApReportData;
import com.ah.bo.admin.HmDomain;


@Entity
@Table(name = "hm_appdata_seconds")
@org.hibernate.annotations.Table(appliesTo = "hm_appdata_seconds", indexes = {
		@Index(name = "IDX_APPDATASECONDS_TIMESTAMP", columnNames ={"timestamp","apMac", "application"}),
		@Index(name = "IDX_APPDATASECONDS_OWNER", columnNames = {"OWNER"})
})

public class AhAppDataSeconds implements ApReportData {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private Long id;	
	
	@Column(name = "owner")
	private long ownerId;
	
	private short radioType;
	
	private long timeStamp;
	@Column(length = 20)
	private String apMac;

	@Column(length = 20)
	private String clientMac;
	private int packetsUpLoad;
	private int packetsDownLoad;
	private long bytesDownLoad;
	private long bytesUpLoad;
	private boolean passThrough;
	private short application;
	private short seconds;
	private short appSeconds;
	private short interface4Client;
	private short peerInterface;
	@Column(length = 32)
	private String clientOsType;
	
	@Column(length = 64)
	private String osName;
	
	@Column(length = 128)
	private String userName;
	private int userProfile;
	
	@Column(length = 32)
	private String userProfileName;
	
	private int vLan;
	@Column(length = 32)
	private String ssid;
	@Column(length = 128)
	private String hostName;
	private long[] extensionBigInt;
	private long[] extensionTimeStamp;
	private String[] extensionText;
	private byte[] extensionByteArray;
	
	public String getUserProfileName() {
		return userProfileName;
	}

	public void setUserProfileName(String userProfileName) {
		this.userProfileName = userProfileName;
	}

	public short getAppSeconds() {
		return appSeconds;
	}

	public void setAppSeconds(short appSeconds) {
		this.appSeconds = appSeconds;
	}

	@Override
	public Long getId() {
		return this.id;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public Timestamp getVersion() {
		return null;
	}
	@Override
	public void setVersion(Timestamp version) {
	}
	

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public String getLabel() {
		return "appdata_hour";
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

	public String getClientMac() {
		return clientMac;
	}
	
	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}
	
	public int getPacketsUpLoad() {
		return packetsUpLoad;
	}
	
	public void setPacketsUpLoad(int packetsUpLoad) {
		this.packetsUpLoad = packetsUpLoad;
	}
	
	public int getPacketsDownLoad() {
		return packetsDownLoad;
	}
	
	public void setPacketsDownLoad(int packetsDownLoad) {
		this.packetsDownLoad = packetsDownLoad;
	}
	
	public long getBytesDownLoad() {
		return bytesDownLoad;
	}
	
	public void setBytesDownLoad(long bytesDownLoad) {
		this.bytesDownLoad = bytesDownLoad;
	}
	
	public long getBytesUpLoad() {
		return bytesUpLoad;
	}
	
	public void setBytesUpLoad(long bytesUpLoad) {
		this.bytesUpLoad = bytesUpLoad;
	}
	
	public boolean getPassThrough() {
		return passThrough;
	}
	
	public void setPassThrough(boolean passThrough) {
		this.passThrough = passThrough;
	}
	
	public short getApplication() {
		return application;
	}
	
	public void setApplication(short application) {
		this.application = application;
	}
	
	public short getSeconds() {
		return seconds;
	}
	
	public void setSeconds(short seconds) {
		this.seconds = seconds;
	}
	
	public short getInterface4Client() {
		return interface4Client;
	}
	
	public void setInterface4Client(short interface4Client) {
		this.interface4Client = interface4Client;
	}
	
	public short getPeerInterface() {
		return peerInterface;
	}
	
	public void setPeerInterface(short peerInterface) {
		this.peerInterface = peerInterface;
	}
	
	public String getClientOsType() {
		return clientOsType;
	}
	
	public void setClientOsType(String clientOsType) {
		this.clientOsType = clientOsType;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public int getUserProfile() {
		return userProfile;
	}
	
	public void setUserProfile(int userProfile) {
		this.userProfile = userProfile;
	}
	
	public int getVLan() {
		return vLan;
	}
	
	public void setVLan(int vLan) {
		this.vLan = vLan;
	}
	
	public long[] getExtensionBigInt() {
		return extensionBigInt;
	}
	
	public void setExtensionBigInt(long[] extensionBigInt) {
		this.extensionBigInt = extensionBigInt;
	}
	
	public long[] getExtensionTimeStamp() {
		return extensionTimeStamp;
	}
	
	public void setExtensionTimeStamp(long[] extensionTimeStamp) {
		this.extensionTimeStamp = extensionTimeStamp;
	}
	
	public String[] getExtensionText() {
		return extensionText;
	}
	
	public void setExtensionText(String[] extensionText) {
		this.extensionText = extensionText;
	}
	
	public byte[] getExtensinByteArray() {
		return extensionByteArray;
	}
	
	public void setExtensionByteArray(byte[] extensionByteArray) {
		this.extensionByteArray = extensionByteArray;
	}
	
	public String getOsName() {
		return osName;
	}
	
	public void setOsName(String osName) {
		this.osName = osName;
	}
	
	public String getSsid() {
		return ssid;
	}
	
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {	
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {	
	}

	public short getRadioType() {
		return radioType;
	}

	public void setRadioType(short radioType) {
		this.radioType = radioType;
	}

	 
}