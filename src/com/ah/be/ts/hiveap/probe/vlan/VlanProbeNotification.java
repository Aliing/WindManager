package com.ah.be.ts.hiveap.probe.vlan;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.ts.hiveap.AbstractDebugNotification;
import com.ah.be.ts.hiveap.DebugNotification;

public class VlanProbeNotification extends AbstractDebugNotification {

	private static final long serialVersionUID = 1L;

	//***************************************************************
    // Variables
    //***************************************************************

	private int vlan;

	private int netmask;

	private String ipAddress;

	private String gateway;

	private String dns;
	
	public VlanProbeNotification(String hiveApMac) {
		super.hiveApMac = hiveApMac;
	}

	public VlanProbeNotification(String hiveApMac, int cookieId) {
		this(hiveApMac);
		super.cookieId = cookieId;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public int getVlan() {
		return vlan;
	}

	public void setVlan(int vlan) {
		this.vlan = vlan;
	}

	public int getNetmask() {
		return netmask;
	}

	public void setNetmask(int netmask) {
		this.netmask = netmask;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	@Override
	public short getCapwapType() {
		return BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_VLANPROBE;
	}

	@Override
	public Category getCategory() {
		return Category.VLAN_PROBE;
	}

	@Override
	public int compareTo(DebugNotification other) {
		// Sort results by VLAN instead of default sorting property log message time.
		return other instanceof VlanProbeNotification ? vlan - ((VlanProbeNotification) other).getVlan() : super.compareTo(other);
	}

	@Override
	public String toString() {
		return "VLAN Probe Notification - AP: " + hiveApMac + "; Cookie: " + cookieId;
	}

}