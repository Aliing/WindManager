package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.VlanReserveInt;
import com.ah.bo.hiveap.HiveAp;

public class VlanReserveImpl implements VlanReserveInt {
	
	private HiveAp hiveAp;

	public VlanReserveImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
	}
	
	public int getVlanReserve() {
		return getVlanReserve(hiveAp);
	}
	
	public static int getVlanReserve(HiveAp hiveAp){
		return hiveAp.getResrvedVlans();
	}

}
