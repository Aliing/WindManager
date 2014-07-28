package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.HostnameProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2008-1-10 16:09:26
 */

public class HostnameProfileImpl implements HostnameProfileInt {
	
	private HiveAp hiveAp;

	public HostnameProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.configuration");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public String getUpdatTime(){
		List<Object> listObj = new ArrayList<Object>();
		listObj.add(hiveAp);
		return CLICommonFunc.getLastUpdateTime(listObj);
	}
	
	public String getHostName(){
		return hiveAp.getHostName();
	}
	
	public boolean isConfigHostName(){
		String macAddr = hiveAp.getMacAddress().toLowerCase();
		if(hiveAp.getDownloadInfo().isOemHm()){
			return !("BB-"+macAddr.substring(macAddr.length()-6)).equals(hiveAp.getHostName());
		}else{
			return !("AH-"+macAddr.substring(macAddr.length()-6)).equals(hiveAp.getHostName());
		}
	}
	
	public static void main(String[] args){
		String aaa="001977002F10";
		System.out.println(aaa.substring(aaa.length()-6));
	}
	
	
}
