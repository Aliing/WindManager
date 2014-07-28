package com.ah.bo.hiveap;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class AutoProvisionDeviceInterface implements AhInterface {

	private static final long serialVersionUID = 1L;

	private short adminState;

	//below 5 items used for interface setting
	private short interfacePort;

	private short interfaceRole = AhInterface.ETHX_DEVICE_INTERFACE_ROLE_LAN;

	private short interfaceTransmissionType;

	private short interfaceSpeed;

	private short pseState = AhInterface.ETH_PSE_8023af;

	private String interfaceDownstreamBandwidth;
	
	private boolean pseEnabled = true;

	private String psePriority = AhInterface.ETH_PSE_PRIORITY_ETH1;
	private boolean enableNat=true;

	public AutoProvisionDeviceInterface() {

	}

	public AutoProvisionDeviceInterface(short port) {
		this.interfacePort = port;
	}

	public short getOperationMode(){
		return -1;
	}

	public void setOperationMode(short operationMode){

	}

	public short getAdminState(){
		return this.adminState;
	}

	public void setAdminState(short adminState){
		this.adminState = adminState;
	}

	public short getInterfacePort() {
		return interfacePort;
	}

	public void setInterfacePort(short interfacePort) {
		this.interfacePort = interfacePort;
	}

	public short getInterfaceRole() {
		return interfaceRole;
	}

	public void setInterfaceRole(short interfaceRole) {
		this.interfaceRole = interfaceRole;
	}

	public short getInterfaceTransmissionType() {
		return interfaceTransmissionType;
	}

	public void setInterfaceTransmissionType(short interfaceTransmissionType) {
		this.interfaceTransmissionType = interfaceTransmissionType;
	}

	public short getInterfaceSpeed() {
		return interfaceSpeed;
	}

	public void setInterfaceSpeed(short interfaceSpeed) {
		this.interfaceSpeed = interfaceSpeed;
	}

	public String getInterfaceDownstreamBandwidth() {
		return interfaceDownstreamBandwidth;
	}

	public void setInterfaceDownstreamBandwidth(String interfaceDownstreamBandwidth) {
		this.interfaceDownstreamBandwidth = interfaceDownstreamBandwidth;
	}

	public short getPseState() {
		return pseState;
	}

	public void setPseState(short pseState) {
		this.pseState = pseState;
	}

	public boolean isPseEnabled() {
		return pseEnabled;
	}

	public void setPseEnabled(boolean pseEnabled) {
		this.pseEnabled = pseEnabled;
	}

	public String getPsePriority() {
		return psePriority;
	}

	public void setPsePriority(String psePriority) {
		this.psePriority = psePriority;
	}
	
	@Transient
	public boolean getDisabledPseState() {
		return pseEnabled==false;
	}

	public boolean isEnableNat() {
		return enableNat;
	}

	public void setEnableNat(boolean enableNat) {
		this.enableNat = enableNat;
	}

}
