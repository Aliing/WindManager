package com.ah.bo.lan;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.MgrUtil;

@Embeddable
public class LanInterfacesMode implements Serializable, Cloneable {
	
	private static final long serialVersionUID = -499289911473619588L;
	
	////-------mode--------//
	private boolean eth0On = true;
	
	private boolean eth1On;
	
	private boolean eth2On;
	
	private boolean eth3On;
	
	private boolean eth4On;
	////-------mode--------//
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[the interface mode is: " + 
				"ETH0: "+(this.isEth0On()?"Enable":"Disable") +
				", ETH1: "+(this.isEth1On()?"Enable":"Disable") +
				", ETH2: "+(this.isEth2On()?"Enable":"Disable") +
				", ETH3: "+(this.isEth3On()?"Enable":"Disable") +
				", ETH4: "+(this.isEth4On()?"Enable":"Disable") + "]");
		return builder.toString();
	}
	
	////-------Transient--------//
	/**
	 * TA1120: Avoid user to select two LAN profiles with overlapping LAN ports:
	 * <br> check the LANs ports is overlapping
	 * @param interfaceMode
	 * @return return the error message when overlapping with other LAN profile; 
	 * <br>else return empty message
	 */
	@Transient
	public String getOverlappingPortMsg(LanInterfacesMode interfaceMode) {
		StringBuilder sBuilder = new StringBuilder();
		if(this.isEth1On() && interfaceMode.isEth1On()) {
			sBuilder.append(MgrUtil.getUserMessage("error.assignLAN.overlapping", "ETH1"));
			sBuilder.append("<br>");
		}
		if(this.isEth2On() && interfaceMode.isEth2On()) {
			sBuilder.append(MgrUtil.getUserMessage("error.assignLAN.overlapping", "ETH2"));
			sBuilder.append("<br>");
		}
		if(this.isEth3On() && interfaceMode.isEth3On()) {
			sBuilder.append(MgrUtil.getUserMessage("error.assignLAN.overlapping", "ETH3"));
			sBuilder.append("<br>");
		}
		if(this.isEth4On() && interfaceMode.isEth4On()) {
			sBuilder.append(MgrUtil.getUserMessage("error.assignLAN.overlapping", "ETH4"));
			sBuilder.append("<br>");
		}
		return sBuilder.toString();
	}
	
	@Transient
	public boolean isAssignedPorts() {
		return this.eth1On || this.eth2On || this.eth3On || this.eth4On;
	}
	
	////-------Getter&Setter--------//
	public boolean isEth0On() {
		return eth0On;
	}

	public void setEth0On(boolean eth0On) {
		this.eth0On = eth0On;
	}

	public boolean isEth1On() {
		return eth1On;
	}

	public void setEth1On(boolean eth1On) {
		this.eth1On = eth1On;
	}

	public boolean isEth2On() {
		return eth2On;
	}

	public void setEth2On(boolean eth2On) {
		this.eth2On = eth2On;
	}

	public boolean isEth3On() {
		return eth3On;
	}

	public void setEth3On(boolean eth3On) {
		this.eth3On = eth3On;
	}

	public boolean isEth4On() {
		return eth4On;
	}

	public void setEth4On(boolean eth4On) {
		this.eth4On = eth4On;
	}
	////-------Getter&Setter--------//
}
