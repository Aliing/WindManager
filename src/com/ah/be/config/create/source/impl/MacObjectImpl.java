package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.MacObjectInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.MacOrOui;

public class MacObjectImpl implements MacObjectInt {
	
	private MacOrOui macOrOui;
	private HiveAp hiveAp;

	public MacObjectImpl(MacOrOui macOrOui, HiveAp hiveAp){
		this.macOrOui = macOrOui;
		this.hiveAp = hiveAp;
	}
	
	public String getMacObjectName(){
		return this.macOrOui.getMacOrOuiName();
	}
	
	public int getMacObjectSize(){
		if(macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_RANGE){
			if(macOrOui.getItems() == null){
				return 0;
			}
			return macOrOui.getItems().size();
		}else if(macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI ||
				macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS){
			return 1;
		}else{
			return 0;
		}
	}
	
	public String getMacRange(int index) throws CreateXMLException{
		String from = null;
		String to = null;
		if(macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_RANGE){
			from = macOrOui.getItems().get(index).getMacRangeFrom();
			to = macOrOui.getItems().get(index).getMacRangeTo();
			return CLICommonFunc.transFormMacAddrOrOui(from) + " - " + CLICommonFunc.transFormMacAddrOrOui(to);
		}else if(macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_OUI){
			String macAddr = CLICommonFunc.getMacAddressOrOui(macOrOui, this.hiveAp).getMacEntry();
			from = macAddr + "000000";
			to = macAddr + "ffffff";
		}else if(macOrOui.getTypeFlag() == MacOrOui.TYPE_MAC_ADDRESS){
			String macAddr = CLICommonFunc.getMacAddressOrOui(macOrOui, this.hiveAp).getMacEntry();
			from = macAddr;
			to = macAddr;
		}
		return CLICommonFunc.transFormMacAddrOrOui(from) + " - " + CLICommonFunc.transFormMacAddrOrOui(to);
		
	}
}
