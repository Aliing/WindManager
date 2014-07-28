package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.OsObjectInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.OsObject;
import com.ah.bo.network.OsObjectVersion;

public class OsObjectImpl implements OsObjectInt {
	
	private OsObject osObject;
	
	private HiveAp hiveAp;
	
	private List<String> osObjectlist = new ArrayList<String>();

	public OsObjectImpl(OsObject osObject,HiveAp hiveAp){
		this.osObject = osObject;
		this.hiveAp = hiveAp;
		this.loadOsObjectlist();
	}
	
	//load osObject list
	private void loadOsObjectlist(){
		if(osObject.getItems() != null && hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100){
			for(OsObjectVersion osVersion: osObject.getItems()){
				osObjectlist.add(osVersion.getOsVersion());
			}
		}
		
		if(osObject.getDhcpItems() != null && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "5.1.1.0") >= 0){
			for(OsObjectVersion osVersion: osObject.getDhcpItems()){
				osObjectlist.add(osVersion.getOsVersion());
			}
		}
	}
	
	public boolean isConfigOsObject(){
		return osObjectlist.size() > 0;
	}
	
	public String getOsObjectName(){
		return osObject.getOsName();
	}
	
	public int getOsVersionSize(){
		return osObjectlist.size();
	}
	
	public String getOsVersion(int index){
		return osObjectlist.get(index);
	}
}
