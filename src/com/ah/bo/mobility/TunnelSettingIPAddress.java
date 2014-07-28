package com.ah.bo.mobility;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.network.IpAddress;

@Embeddable
@SuppressWarnings("serial")
public class TunnelSettingIPAddress implements Serializable {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "IP_ADDRESS_ID")
	private IpAddress ipAddress;

	private String password;
	
	@Transient
	private long tunnelSettingId;

	public IpAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(IpAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof TunnelSettingIPAddress)) {
			return false;
		} else if (null != ipAddress && null != ipAddress.getId()) {
			return ipAddress.getId().equals(
					((TunnelSettingIPAddress) other).getIpAddress().getId());
		}
		return super.equals(other);
	}

	@Override
	public int hashCode() {
		if (null != ipAddress && null != ipAddress.getId()) {
			return ipAddress.getId().intValue();
		}
		return super.hashCode();
	}

	public long getTunnelSettingId() {
		return tunnelSettingId;
	}

	public void setTunnelSettingId(long tunnelSettingId) {
		this.tunnelSettingId = tunnelSettingId;
	}
}
