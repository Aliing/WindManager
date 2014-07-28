package com.ah.be.config.create.source.impl.branchRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.LanPortInt;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.xml.be.config.LanEthxModeValue;

public class LanPortImpl implements LanPortInt {
	
	private HiveAp hiveAp;
	
	private Map<LanType, PortAccessProfile> portProfileMap = new HashMap<LanType, PortAccessProfile>();
	
	private Map<LanType, List<Integer>> lanVlanMap = new HashMap<LanType, List<Integer>>();
	
	public LanPortImpl(HiveAp hiveAp) throws CreateXMLException{
		this.hiveAp = hiveAp;
		initLan();
	}
	
	private void initLan() throws CreateXMLException{
		if(this.hiveAp == null || hiveAp.getPortGroup() == null || hiveAp.getPortGroup().getBasicProfiles() == null){
			return;
		}
		//TODO Port Template Profiles
		for(PortBasicProfile baseProfile : hiveAp.getPortGroup().getBasicProfiles()){
			if(baseProfile.getETHs() == null){
				continue;
			}
			for(int i=0; i<baseProfile.getETHs().length; i++){
				if("1".equals(baseProfile.getETHs()[i])){
					portProfileMap.put(LanPortInt.LanType.lan1, baseProfile.getAccessProfile());
				}else if("2".equals(baseProfile.getETHs()[i])){
					portProfileMap.put(LanPortInt.LanType.lan2, baseProfile.getAccessProfile());
				}else if("3".equals(baseProfile.getETHs()[i])){
					portProfileMap.put(LanPortInt.LanType.lan3, baseProfile.getAccessProfile());
				}else if("4".equals(baseProfile.getETHs()[i])){
					portProfileMap.put(LanPortInt.LanType.lan4, baseProfile.getAccessProfile());
				}
			}
		}
		
		for(LanType lanType : portProfileMap.keySet()){
			PortAccessProfile accessProfile = portProfileMap.get(lanType);
			
			if(accessProfile == null){
				continue;
			}
			if(lanVlanMap.get(lanType) == null){
				lanVlanMap.put(lanType, new ArrayList<Integer>());
			}
			
			if(accessProfile.getPortType() == PortAccessProfile.PORT_TYPE_8021Q){
				if(accessProfile.getNativeVlan() != null) {
					lanVlanMap.get(lanType).add(CLICommonFunc.getVlan(accessProfile.getNativeVlan(), hiveAp).getVlanId());
				}
				if(accessProfile.getAllowedVlan() != null && !"".equals(accessProfile.getAllowedVlan()) 
						&& !accessProfile.getAllowedVlan().toLowerCase().equals("all")) {
					boolean[] vlans = new boolean[5000];
					CLICommonFunc.mergeRange(vlans, accessProfile.getAllowedVlan());
					for(int i=0; i<vlans.length; i++){
						if(vlans[i]){
							lanVlanMap.get(lanType).add(i);
						}
					}
				}else{
					if(accessProfile.getDefUserProfile() != null){
						int vlan = InterfaceBRImpl.getUserProfileVlan(accessProfile.getDefUserProfile(), hiveAp);
						if(vlan > 0){
							lanVlanMap.get(lanType).add(vlan);
						}
					}
					if(accessProfile.getSelfRegUserProfile() != null){
						int vlan = InterfaceBRImpl.getUserProfileVlan(accessProfile.getSelfRegUserProfile(), hiveAp);
						if(vlan > 0){
							lanVlanMap.get(lanType).add(vlan);
						}
					}
				}
			}
		}
	}

	public boolean isConfigLanProfile() {
		return true;
	}

	public boolean isConfigLanPort(LanType lanType) {
		return portProfileMap.get(lanType) != null && !isLanInterShutdown(lanType);
	}

	public boolean isLanInterShutdown(LanType lanType) {
		boolean isShutdown = false;
		if(lanType == LanType.lan1){
			isShutdown =  this.hiveAp.getEth1Interface().getAdminState() == AhInterface.ADMIN_STATE_DOWM;
		}else if(lanType == LanType.lan2){
			isShutdown =  this.hiveAp.getEth2Interface().getAdminState() == AhInterface.ADMIN_STATE_DOWM;
		}else if(lanType == LanType.lan3){
			isShutdown =  this.hiveAp.getEth3Interface().getAdminState() == AhInterface.ADMIN_STATE_DOWM;
		}else if(lanType == LanType.lan4){
			isShutdown =  this.hiveAp.getEth4Interface().getAdminState() == AhInterface.ADMIN_STATE_DOWM;
		}
		
		if(!isShutdown){
			isShutdown = portProfileMap.get(lanType) == null;
		}
		
		return isShutdown;
	}

	public LanEthxModeValue getLanInterMode(LanType lanType) {
		if(portProfileMap.get(lanType).getPortType() == PortAccessProfile.PORT_TYPE_8021Q){
			return LanEthxModeValue.TRUNK;
		}else{
			return LanEthxModeValue.PORT_BASED;
		}
	}

	public int getLanInterSize(LanType lanType) {
		return lanVlanMap.get(lanType) == null? 0 : lanVlanMap.get(lanType).size();
	}

	public int getLanInterVlan(LanType lanType, int index) {
		return lanVlanMap.get(lanType).get(index);
	}
	
	public boolean isConfigVlanCheck(){
		return !portProfileMap.isEmpty();
	}
	
	public boolean isVlanCheck(){
		for(PortAccessProfile lanProfile : portProfileMap.values()){
			if(lanProfile.getPortType() != PortAccessProfile.PORT_TYPE_8021Q){
				return false;
			}
		}
		return true;
	}
}
