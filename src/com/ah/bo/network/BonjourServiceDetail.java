package com.ah.bo.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.MgrUtil;


@Embeddable
public class BonjourServiceDetail implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private boolean shared;
	private String type;
	private String name;
	private String ip4;
	private String ip6;
	private int port;
	private String host;
	private short vlan;
	@Column(length = 2048)
	private String text;
	@Column(length = 12, nullable = false)
	private String macAddress;
	private String action = "";
	private String vlanGroupName="";
	private String shareRomoteBdd="";
	
	public static final String SEPARATOR_CHAR = "<%_%>"; 
	
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp4() {
		return ip4;
	}
	public void setIp4(String ip4) {
		this.ip4 = ip4;
	}
	public String getIp6() {
		return ip6;
	}
	public void setIp6(String ip6) {
		this.ip6 = ip6;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public short getVlan() {
		return vlan;
	}
	public void setVlan(short vlan) {
		this.vlan = vlan;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getVlanGroupName() {
		return vlanGroupName;
	}
	public void setVlanGroupName(String vlanGroupName) {
		this.vlanGroupName = vlanGroupName;
	}
	
	public String getShareRomoteBdd() {
		return shareRomoteBdd;
	}
	public void setShareRomoteBdd(String shareRomoteBdd) {
		this.shareRomoteBdd = shareRomoteBdd;
	}
	
	@Transient
	private List<VlanGroup> vlanGroups = new ArrayList<VlanGroup>();
	
	public List<VlanGroup> getVlanGroups() {
		return vlanGroups;
	}
	public void setVlanGroups(List<VlanGroup> vlanGroups) {
		this.vlanGroups = vlanGroups;
	}
	
	@Transient
	public String getIp4Str(){
		return ip4+":"+port;
	}
	@Transient
	public String vlanGroupRange;

	public String getVlanGroupRange() {
		return vlanGroupRange;
	}
	public void setVlanGroupRange(String vlanGroupRange) {
		this.vlanGroupRange = vlanGroupRange;
	}
	@Transient
	public boolean getVlanGroupExist(){
		if(vlanGroupName == null || vlanGroupName.isEmpty() || MgrUtil.getUserMessage("config.ipPolicy.any").equals(vlanGroupName)){
			return false;
		} else {
			return true;
		}
	}
	
}
