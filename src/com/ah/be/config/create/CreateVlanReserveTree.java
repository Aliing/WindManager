package com.ah.be.config.create;

import com.ah.be.config.create.source.VlanReserveInt;
import com.ah.xml.be.config.AhIntAct;

public class CreateVlanReserveTree {
	
	private VlanReserveInt reserveImpl;
	
	private AhIntAct vlanReserveObj;

	public CreateVlanReserveTree(VlanReserveInt reserveImpl){
		this.reserveImpl = reserveImpl;
	}
	
	public AhIntAct getVlanReserve(){
		return vlanReserveObj;
	}
	
	public void generate(){
		vlanReserveObj = CLICommonFunc.createAhIntActObj(
				reserveImpl.getVlanReserve(), CLICommonFunc.getYesDefault());
	}
}
