package com.ah.be.config.create.source.common;

import java.util.ArrayList;
import java.util.List;

public enum AcmOsObject {

	UNKNOW(-1,"Unknow",""),
	iOS(0, "iPod/iPhone/iPad", "17.0.0.0/8"), 
	MACOS(1, "MacOS", "17.0.0.0/8"), 
	SYMBIAN(2, "Symbian", ""), 
	BLACKBERRY(3, "Blackberry", ""), 
	ANDROID(4,"Android", "android.clients.google.com;schemas.google.com;www.android.com;mtalk.google.com"), 
	CHROME(5, "Chrome", "www3.l.google.com;clients.l.google.com;clients2.googleusercontent.com;chrome.google.com;" +
			"talk.google.com;accounts.google.com;www.googleapis.com;apis.google.com;www.gstatic.com;ssl.gstatic.com"),
	WINDOWSPHONE(6, "WindowsPhone", "");
	
	private int value;
	
	private String name;
	
	private String ipAddress;
	
	private AcmOsObject(int value, String name, String ipAddress){
		this.value = value;
		this.name = name;
		this.ipAddress = ipAddress;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getIpAddress(){
		return this.ipAddress;
	}
	
	public static String getIpAddressByValue(int value){
		for(AcmOsObject os : AcmOsObject.values()){
			if(os.getValue() == value)
				return os.getIpAddress();
		}
		return AcmOsObject.UNKNOW.getIpAddress();
	}
	
	public static List<String> getIpAddressByValue(int[] values){
		List<String> ipAddress = new ArrayList<String>();
		if(null != values && values.length > 0){
			for(int value : values){
				ipAddress.add(getIpAddressByValue(value));
			}
		}
		return ipAddress;
	}
}
