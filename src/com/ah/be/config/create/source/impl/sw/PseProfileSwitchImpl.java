package com.ah.be.config.create.source.impl.sw;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.impl.baseImpl.PseProfileBaseImpl;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.PseProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.port.PortPseProfile;
import com.ah.xml.be.config.PsePowerManagementTypeValue;
import com.ah.xml.be.config.PseProfilePriorityValue;
 

public class PseProfileSwitchImpl extends PseProfileBaseImpl {
	
	private PortGroupProfile portGroup;
	
	private HiveAp hiveAp;
		
	private List<PseProfile> pseProfileList = new ArrayList<PseProfile>();
	
	public PseProfileSwitchImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
		this.portGroup = hiveAp.getPortGroup();
		if (portGroup != null && portGroup.getPortPseProfiles() != null) {
			for (PortPseProfile profile : portGroup.getPortPseProfiles()) {
				if (profile.getPseProfile() != null) {
					pseProfileList.add(profile.getPseProfile());
				}
			}
 		}
	}
	
	public boolean isConfigPse() {
		return hiveAp.isEnableSwitchPse();
	}

	public int getMaxPowerSource() {
		return hiveAp.getMaxpowerBudget();
	}
	
	public String getRestartValue() {
		//priority enable power-type dynamic guard-band 10" or "no priority enable power-type power-management-type static guard-band 10"
	    StringBuffer sb = new StringBuffer();
	    if (this.isEnablePriority()) {
	    	sb.append("priority enable");
	    } else {
	    	sb.append("no priority enable");
	    }
	    if (PsePowerManagementTypeValue.STATIC == getManagementType()) {
	    	sb.append(" power-type static guard-band " + getPowerGuardBand());
	    } else {
	    	sb.append(" power-type dynamic guard-band " + getPowerGuardBand());
	    }
	    if (this.isEnableLegacy()) {
	    	sb.append(" legacy enable");
	    } else {
	    	sb.append(" no legacy enable");
	    }
	    return sb.toString();
	}
	
	public int getPseProfileSize() {
		return pseProfileList.size();
	}
	
	public String getPseProfileName(int index) {
		return pseProfileList.get(index).getName();
	}
	
	public PseProfilePriorityValue getPseProfilePriority(int index) {
		short priority = pseProfileList.get(index).getPriority();
		if (PseProfile.PRIORITY_CRITICAL == priority) {
			return PseProfilePriorityValue.CRITICAL;
		} else if (PseProfile.PRIORITY_HIGH == priority) {
			return PseProfilePriorityValue.HIGH;
		} else {
			return PseProfilePriorityValue.LOW;
		}
		 
	}
	
	public String getPseProfilePowerMode(int index) {
		if (AhInterface.PSE_PDTYPE_8023AF == pseProfileList.get(index).getPowerMode()) {
			return "802.3af";
		} else {
			return "802.3at";
		}
	}
	
	public int getPseProfileThresholdPower(int index) {
		int psePower = pseProfileList.get(index).getThresholdPower();
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.1.6.0") >= 0){
			return psePower;
		}
		
		int maxPower;
		if(AhInterface.PSE_PDTYPE_8023AF == pseProfileList.get(index).getPowerMode()){
			maxPower = PseProfile.THRESHOLD_POWER_AF;
		}else{
			maxPower = PseProfile.THRESHOLD_POWER_AT;
		}
		
		return Math.min(maxPower, psePower);
	}
	
	public boolean isEnablePse() {
		return hiveAp.isEnableSwitchPse();
	}
	
	public boolean isEnablePriority() {
		return hiveAp.isEnableSwitchPriority();
	}
	
	public PsePowerManagementTypeValue getManagementType() {
		if (HiveAp.MANAGERMENT_TYPE_STATIC == hiveAp.getManagementType()) {
			return PsePowerManagementTypeValue.STATIC;
		} else {
		    return PsePowerManagementTypeValue.DYNAMIC;
		}
	}
	
	public short getPowerGuardBand() {
		return hiveAp.getPowerGuardBand();
	}
	
	public short getMaxPowerBudget() {
		return hiveAp.getMaxpowerBudget();
	}
	
	public boolean isEnableLegacy() {
		return hiveAp.isEnablePoeLegacy();
	}
	
    public boolean isEnablePriorityLldp() {
        return hiveAp.isEnablePoeLldp();
    }

}
