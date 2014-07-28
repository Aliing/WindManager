package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.VlanGroupProfileInt;
import com.ah.xml.be.config.VlanGroupObj;
/**
 * @author llchen
 * @version 2012-10-10 9:36:43 AM
 */

public class CreateVlanGroupTree {
	
	private VlanGroupProfileInt vlanGroupProfileImpl;
	private GenerateXMLDebug oDebug;
	private VlanGroupObj vlanGroupObj;
	
	public CreateVlanGroupTree(VlanGroupProfileInt vlanGroupProfileImpl, GenerateXMLDebug oDebug){
		this.vlanGroupProfileImpl = vlanGroupProfileImpl;
		this.oDebug = oDebug;
	}
	
	public VlanGroupObj getVlanGroupObj(){
		return this.vlanGroupObj;
	}
	
	public void generate(){
		vlanGroupObj = new VlanGroupObj();
		generateBonjourGatewayLevel_1();
		
	}
	
	private void generateBonjourGatewayLevel_1(){	
		/**element: <vlan-group>.<cr> */
		
		oDebug.debug("/configuration/interface/mgt0/ip-helper",
				"address", GenerateXMLDebug.CONFIG_ELEMENT,
				vlanGroupProfileImpl.getVlanGroupGuiName(), vlanGroupProfileImpl.getVlanGroupName());
		for(int i=0;i<vlanGroupProfileImpl.getVlanSize();i++){
			
			vlanGroupObj.setName(vlanGroupProfileImpl.getVlansGroupName(i));
			
			vlanGroupObj.getCr().add(CLICommonFunc.createAhNameActValueQuoteProhibited(vlanGroupProfileImpl.getValn(i),
			CLICommonFunc.getYesDefault(),CLICommonFunc.getYesDefault()));
			
			
		}
	}
	
	
}
