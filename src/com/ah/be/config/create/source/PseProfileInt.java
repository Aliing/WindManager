package com.ah.be.config.create.source;

import com.ah.xml.be.config.PsePowerManagementTypeValue;
import com.ah.xml.be.config.PseProfilePriorityValue;

/**
 * @author llchen
 * @version 2011-12-29 9:36:43 AM
 */

public interface PseProfileInt {

	public String getWlanGuiName();
	
	public String getWlanName();
	
	public String getPseGuiName();

	public boolean isConfigPse();
	
	public int getMaxPowerSource();
	
	public String getRestartValue();
	
	public int getPseProfileSize();
	
	public String getPseProfileName(int index);
	
	public PseProfilePriorityValue getPseProfilePriority(int index);
	
	public String getPseProfilePowerMode(int index);
	
	public int getPseProfileThresholdPower(int index);
	
	//public boolean isEnablePse();
	
	public boolean isEnablePriority();
	
	public PsePowerManagementTypeValue getManagementType();
	
	public short getPowerGuardBand();
	
	public short getMaxPowerBudget();
	
	public boolean isEnableLegacy();
	
	public boolean isEnablePriorityLldp();

}