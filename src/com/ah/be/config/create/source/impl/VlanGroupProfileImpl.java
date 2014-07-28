package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.VlanGroupProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.VlanGroup;
import com.ah.util.MgrUtil;
/**
 * @author llchen
 * @version 2012-10-10 9:36:43 AM
 */
public class VlanGroupProfileImpl implements VlanGroupProfileInt {
	
	private HiveAp hiveAp;
	private VlanGroup vlanGroup;
	private List<String> vlans = new ArrayList<String>();
	public VlanGroupProfileImpl(VlanGroup vlanGroup,HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		this.vlanGroup = vlanGroup;
		this.loadValns();
	}
	
	private void loadValns(){
		List<String> list = CLICommonFunc.mergeRangeList(vlanGroup.getVlans());
		for(String vlan : list){
			if(vlan.indexOf("-")>0){
				vlan = vlan.replaceAll("\\s*\\-\\s*"," ");
			}
			vlans.add(vlan);
		}
	}
	
	public String getVlanGroupGuiName() {
		return MgrUtil.getUserMessage("config.upload.debug.vlanGroup");
	}

	public String getVlanGroupName() {
		return hiveAp.getConfigTemplate().getConfigName();
	}
	
	public String getVlansGroupName(int index) {
		return vlanGroup.getVlanGroupName();
	}
	
	public int getVlanSize() {
		return vlans.size();
	}
	
	public String getValn(int index) {
		return vlans.get(index);
	}

}
