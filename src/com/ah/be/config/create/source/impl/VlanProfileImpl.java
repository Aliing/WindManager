package com.ah.be.config.create.source.impl;

import com.ah.be.config.create.source.VlanProfileInt;

public class VlanProfileImpl implements VlanProfileInt {
	
	private int vlan;

	public VlanProfileImpl(int vlan) {
		this.vlan = vlan;
	}
	
	public String getVlanName() {
		return String.valueOf(vlan);
	}
	
	public int getVlanId(){
		return this.vlan;
	}

}
