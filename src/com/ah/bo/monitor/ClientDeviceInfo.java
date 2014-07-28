
/*
id: bigSerial, not null primary key
MAC: varchar(20), not null, indexed
owner : BigInt, not null, indexed
HostName: VarChar(128)
OS_type: VarChar(32)
update_at: bigint, not null
 **/


package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "client_device_info")
@org.hibernate.annotations.Table(appliesTo = "client_device_info", indexes = {
		@Index(name = "idx_client_device_info_mac", columnNames = {"MAC"}),
		@Index(name = "idx_client_device_info_owner", columnNames = {"owner"})
	    })
public class ClientDeviceInfo  implements HmBo{
	
	private static final long serialVersionUID = 7352464627445775552L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(length = 20, nullable = false)
	private String MAC ;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	@Column(length = 128)
	private String hostName ;
	
	@Column(length = 32)
	private String  OS_type ;
	@Column(nullable = false)
	private Long update_at ;
	
	@Column(length = 128)
	private String userName;
	
	@Column(length = 128)
	private String profileName;
	
	@Column(length = 128)
	private String ssid;
	
	private int vlan;
	
	private int radioType;
	
	private String option55;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public int getVlan() {
		return vlan;
	}
	public void setVlan(int vlan) {
		this.vlan = vlan;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public int getRadioType() {
		return radioType;
	}
	public void setRadioType(int radioType) {
		this.radioType = radioType;
	}
	public String getMAC() {
		return MAC;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getOS_type() {
		return OS_type;
	}
	public void setOS_type(String oS_type) {
		OS_type = oS_type;
	}
	
	public Long getUpdate_at() {
		return update_at;
	}
	public void setUpdate_at(Long update_at) {
		this.update_at = update_at;
	}
	public void setMAC(String mAC) {
		MAC = mAC;
	}
	@Override
	public String getLabel() {
		return null;
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
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public String getOption55() {
		return option55;
	}
	public void setOption55(String option55) {
		this.option55 = option55;
	}

}


