package com.ah.bo.hiveap;

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

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.wlan.SsidProfile;

@Entity
@Table(name = "WIFICLIENT_PREFERRED_SSID")
@org.hibernate.annotations.Table(appliesTo = "WIFICLIENT_PREFERRED_SSID", indexes = {
		@Index(name = "PREFERRED_SSID_OWNER", columnNames = { "OWNER" })
		})
public class WifiClientPreferredSsid implements HmBo {
	
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
	private String ssid;

	private int encryption;

	private int authentication;

	private int mgmtKey;
	
	@Column(length = 64)
	private String keyValue;
	
	private int keyType = 0;
	
	private int accessMode = SsidProfile.ACCESS_MODE_OPEN;
	
	@Column(length = 256)
	private String comment;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getLabel() {
		return ssid;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
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

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getEncryption() {
		return encryption;
	}

	public void setEncryption(int encryption) {
		this.encryption = encryption;
	}

	public int getAuthentication() {
		return authentication;
	}

	public void setAuthentication(int authentication) {
		this.authentication = authentication;
	}

	public int getMgmtKey() {
		return mgmtKey;
	}

	public void setMgmtKey(int mgmtKey) {
		this.mgmtKey = mgmtKey;
	}
	
	public String getAccessModeString(){
		switch (accessMode){
			case SsidProfile.ACCESS_MODE_WPA:
				return "WPA/WPA2 PSK (Personal)";
			case SsidProfile.ACCESS_MODE_PSK:
				return "Private PSK";
			case SsidProfile.ACCESS_MODE_8021X:
				return "WPA/WPA2 802.1X (Enterprise)";
			case SsidProfile.ACCESS_MODE_WEP:
				return "WEP";
			case SsidProfile.ACCESS_MODE_OPEN:
				return "Open";
			default:
				return "Unknown";
		}
	}

	public int getAccessMode() {
		return accessMode;
	}

	public void setAccessMode(int accessMode) {
		this.accessMode = accessMode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	public int getKeyType() {
		return keyType;
	}

	public void setKeyType(int keyType) {
		this.keyType = keyType;
	}
	
    @Override
    public WifiClientPreferredSsid clone() {
       try {
           return (WifiClientPreferredSsid) super.clone();
       } catch (CloneNotSupportedException e) {
           return null;
       }
    }

}
