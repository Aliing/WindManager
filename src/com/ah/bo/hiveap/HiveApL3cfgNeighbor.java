package com.ah.bo.hiveap;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class HiveApL3cfgNeighbor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private short neighborType;
	
	private String neighborMac;

	@Transient
	private String hostname;
	
	@Transient
	private String ipAddress;
	
	@Transient
	private String netMask;
	
	@Transient
	private boolean isDhcp;
	
	@Transient
	private String cfgIpAddress;
	
	@Transient
	private String cfgNetMask;

	public static final short NEIGHBOR_TYPE_INCLUDED = 1;
	public static final short NEIGHBOR_TYPE_EXCLUDED = 2;

	public static EnumItem[] NEIGHBOR_TYPE = MgrUtil.enumItems(
			"enum.l3Neighbor.", new int[] { NEIGHBOR_TYPE_INCLUDED,
					NEIGHBOR_TYPE_EXCLUDED });

	public short getNeighborType() {
		return neighborType;
	}

	public void setNeighborType(short neighborType) {
		this.neighborType = neighborType;
	}

	public String getNeighborMac() {
		return neighborMac;
	}

	public void setNeighborMac(String neighborMac) {
		this.neighborMac = neighborMac;
	}

	public String getHostname() {
		if (null == hostname) {
			initHostnameIpAddress();
		}
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddress() {
		if (null == hostname) {
			initHostnameIpAddress();
		}
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNetMask() {
		if (null == hostname) {
			initHostnameIpAddress();
		}
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

	public boolean isDhcp() {
		if (null == hostname) {
			initHostnameIpAddress();
		}
		return isDhcp;
	}

	public void setDhcp(boolean isDhcp) {
		this.isDhcp = isDhcp;
	}

	public String getCfgIpAddress() {
		if (null == hostname) {
			initHostnameIpAddress();
		}
		return cfgIpAddress;
	}

	public void setCfgIpAddress(String cfgIpAddress) {
		this.cfgIpAddress = cfgIpAddress;
	}

	public String getCfgNetMask() {
		if (null == hostname) {
			initHostnameIpAddress();
		}
		return cfgNetMask;
	}

	public void setCfgNetMask(String cfgNetMask) {
		this.cfgNetMask = cfgNetMask;
	}

	private void initHostnameIpAddress() {
		if (null != neighborMac) {
			List<?> list = QueryUtil.executeQuery(
					"select hostName, dhcp, ipAddress, netmask, cfgIpAddress, cfgNetmask from "
							+ HiveAp.class.getSimpleName(), null,
					new FilterParams("macAddress", neighborMac));
			if (!list.isEmpty()) {
				Object[] values = (Object[]) list.get(0);
				String hostname = (String) values[0];
				Boolean isDhcp = (Boolean) values[1];
				String ipAddress = (String) values[2];
				String netMask = (String) values[3];
				String cfgIpAddress = (String) values[4];
				String cfgNetMask = (String) values[5];

				this.hostname = hostname;
				this.ipAddress = ipAddress;
				this.netMask = netMask;
				this.isDhcp = isDhcp;
				this.cfgIpAddress = cfgIpAddress;
				this.cfgNetMask = cfgNetMask;
			}
		}
	}

}