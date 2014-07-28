package com.ah.be.config.create.source.impl.brAndCVG;

import com.ah.be.config.create.source.impl.InterfaceProfileImpl;
import com.ah.be.config.create.source.impl.branchRouter.InterfaceBRImpl;
import com.ah.be.config.create.source.impl.cvg.InterfaceCVGImpl;
import com.ah.bo.hiveap.HiveAp;

public class InterfaceBRAndCVGImpl extends InterfaceBRImpl {
	
	private InterfaceProfileImpl cvgImpl;

	public InterfaceBRAndCVGImpl(HiveAp hiveAp, boolean view) throws Exception{
		super(hiveAp, view);
		cvgImpl = new InterfaceCVGImpl(hiveAp, view);
	}
	
	public boolean isEnableEthDhcp(InterType type){
		if(type == InterType.eth0){
			return cvgImpl.isEnableEthDhcp(type);
		}else{
			return super.isEnableEthDhcp(type);
		}
	}
	
	public boolean isConfigEthIp(InterType type){
		if(type == InterType.eth0){
			return cvgImpl.isConfigEthIp(type);
		}else{
			return super.isConfigEthIp(type);
		}
	}
	
	public String getEthIp(InterType type){
		if(type == InterType.eth0){
			return cvgImpl.getEthIp(type);
		}else{
			return super.getEthIp(type);
		}
	}
	
	public boolean isEnableWanNat(InterType type){
		return cvgImpl.isEnableWanNat(type);
	}
	
}
