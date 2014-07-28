package com.ah.be.admin.cidClients;

import java.io.Serializable;
import java.sql.Timestamp;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Cid")
public class CidDeviceEBO implements HmBo,Serializable{
	
	private static final long serialVersionUID = -5575087165366925184L;
	
	@XStreamAsAttribute
	private String macAddress;
	
	@XStreamAsAttribute
	private String imei;
	
	public CidDeviceEBO(){
		
	}
	
	public CidDeviceEBO(String macAddress,String imei){
		this.macAddress = macAddress;
		this.imei = imei;
	}
	
	public String getMacAddress(){
		return this.macAddress;
	}
	
	public void setMacAddress(String macAddress){
		this.macAddress = macAddress;
	}
	
	public String getImei(){
		return this.imei;
	}
	
	public void setImei(String imei){
		this.imei = imei;
	}

	@Override
	public HmDomain getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		
	}

}
