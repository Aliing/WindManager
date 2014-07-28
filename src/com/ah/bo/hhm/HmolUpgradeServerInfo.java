/**
 *@filename		HmolUpgradeServerInfo.java
 *@version
 *@author		wpliang
 *All right reserved.
 */
package com.ah.bo.hhm;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/**
 * @author		Liang WenPing
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "HMOL_UPGRADE_SERVER_INFO")
@IdClass(ServerInfoKey.class)
public class HmolUpgradeServerInfo implements HmBo {

	private static final long serialVersionUID = 1L;
	
	@Id
	private String vhmId;
	
	@Id
	private String versionName;
	
	private String serverAddress;
	
	private String serverDomainName;

	public String getVhmId() {
		return vhmId;
	}

	public void setVhmId(String vhmId) {
		this.vhmId = vhmId;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getServerDomainName() {
		return serverDomainName;
	}

	public void setServerDomainName(String serverDomainName) {
		this.serverDomainName = serverDomainName;
	}

	/* ================= Override methods ======================= */
	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public Long getId() {
		return null;
	}

	@Override
	public void setId(Long id) {
	}

	@Version
	private Timestamp	version;

	public Timestamp getVersion() {
		return version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

}