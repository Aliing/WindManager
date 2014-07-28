package com.ah.be.config.create;

import com.ah.be.config.create.source.VlanProfileInt;
import com.ah.xml.be.config.AhNameActValue;

public class CreateVlanTree {
	
	private AhNameActValue vlanObj;

	public CreateVlanTree(VlanProfileInt vlanImpl){
		vlanObj = CLICommonFunc.createAhNameActValue(vlanImpl.getVlanName(), CLICommonFunc.getYesDefault());
	}
	
	public AhNameActValue getVlanObj(){
		return this.vlanObj;
	}
}
