package com.ah.be.config.create.source.impl.baseImpl;

import com.ah.be.config.create.source.PseProfileInt;
import com.ah.xml.be.config.PsePowerManagementTypeValue;
import com.ah.xml.be.config.PseProfilePriorityValue;

public class PseProfileBaseImpl implements PseProfileInt {

	public String getWlanGuiName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWlanName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPseGuiName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConfigPse() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMaxPowerSource() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getPseProfileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPseProfileName(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public PseProfilePriorityValue getPseProfilePriority(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPseProfilePowerMode(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPseProfileThresholdPower(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isEnablePse() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEnablePriority() {
		// TODO Auto-generated method stub
		return false;
	}

	public PsePowerManagementTypeValue getManagementType() {
		// TODO Auto-generated method stub
		return null;
	}

	public short getPowerGuardBand() {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getMaxPowerBudget() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String getRestartValue() {
		return null;
	}

	@Override
	public boolean isEnableLegacy() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean isEnablePriorityLldp() {
        // TODO Auto-generated method stub
        return false;
    }

}
