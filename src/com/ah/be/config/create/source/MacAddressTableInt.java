package com.ah.be.config.create.source;

import java.util.Set;

import com.ah.bo.network.Vlan;


public interface MacAddressTableInt {
	
	public boolean isConfigFDB();
	
	public int getIdleTimeout();
		
	public int getMacAddressStaticSize();
	
	public String getStaticMacAddress(int index);
	
	public int getStaticVlanId(int index);
	
	public String getStaticInterface(int index);
	
	public int getSelectVlanSize();
	
	public int getSelectVlanId(int index);
	
	public boolean isEnableMacLearnForAllVlans();
	
	public boolean isEnableNotification();
	
	public int getNotificationInterval();
	
}