package com.ah.be.config.create;
import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.PseProfileInt;
import com.ah.xml.be.config.AhEnumAct;
import com.ah.xml.be.config.AhEnumActValue;
import com.ah.xml.be.config.AhEnumShow;
import com.ah.xml.be.config.AhName;
import com.ah.xml.be.config.PseLegacy;
import com.ah.xml.be.config.PseObj;
import com.ah.xml.be.config.PsePowerManagementType;
import com.ah.xml.be.config.PsePriority;
import com.ah.xml.be.config.PsePriorityLldp;
import com.ah.xml.be.config.PseProfile;
import com.ah.xml.be.config.PseProfilePowerMode;
import com.ah.xml.be.config.PseProfilePriority;
import com.ah.xml.be.config.PseRestart;

public class CreatePseTree {
	private PseProfileInt pseImpl;
	private PseObj pseObj;
	private GenerateXMLDebug oDebug;
	
	//private List<Object> childList_1 = new ArrayList<Object>();
	//private List<Object> childList_2 = new ArrayList<Object>();

	public CreatePseTree(PseProfileInt pseImpl, GenerateXMLDebug oDebug) throws Exception {
		this.pseImpl = pseImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		if(this.pseImpl != null){
			pseObj = new PseObj();
			generatePseLevel_1();
		}
	}
	
	public PseObj getPseObj(){
		return this.pseObj;
	}
	
	private void generatePseLevel_1() throws Exception {		
		/** element: <pse>.<enable> */
		pseObj.setEnable(CLICommonFunc.getAhOnlyAct(pseImpl.isConfigPse()));
		if(pseImpl.isConfigPse()){
			pseObj.setMaxPowerSource(CLICommonFunc.createAhIntActObj(pseImpl.getMaxPowerSource(), CLICommonFunc.getYesDefault()));
			/** element: <pse>.<priority>.<enable> */
			if (pseImpl.isEnablePriority()) {
				//PsePriority priority = new PsePriority();
				//priority.setEnable(CLICommonFunc.getAhOnlyAct(true));
				//pseObj.setPriority(priority);
			}
			
			pseObj.setGuardBand(CLICommonFunc.createAhIntActObj(pseImpl.getPowerGuardBand(), true));
			
			/** element: <pse>.<power-management-type>.<options> */
			PsePowerManagementType pmt = new PsePowerManagementType();
			pmt.setValue(pseImpl.getManagementType());
			pmt.setOperation(AhEnumAct.YES);
			pseObj.setPowerManagementType(pmt);		
			
			/** element: <pse>.<legacy>.<enable> */
			PseLegacy legacy = new PseLegacy();
			legacy.setEnable(CLICommonFunc.getAhOnlyAct(pseImpl.isEnableLegacy()));
			pseObj.setLegacy(legacy);
			
			/** element: <pse>.<priority-lldp>.<enable> */
//			PsePriorityLldp priorityLldp = new PsePriorityLldp();
//			priorityLldp.setEnable(CLICommonFunc.getAhOnlyAct(pseImpl.isEnablePriorityLldp()));
//			pseObj.setPriorityLldp(priorityLldp);
			
			/** element: <pse>.<restart> */
			PseRestart restart = new PseRestart();
			restart.setOperation(AhEnumShow.YES_WITH_SHOW);
			restart.setCr("");
			AhName ahName = new AhName();
			ahName.setName(pseImpl.getRestartValue());
			restart.setAHDELTAASSISTANT(ahName);
			pseObj.setRestart(restart);
		}
		
		/** element: <pse>.<profile> */
		for (int i = 0; i < pseImpl.getPseProfileSize(); i++) {
			pseObj.getProfile().add(CreatePseProfile(i));
		}
	}
	
	private PseProfile CreatePseProfile(int index) {
		PseProfile profile = new PseProfile();
		profile.setCr("");
		profile.setName(pseImpl.getPseProfileName(index));
		profile.setOperation(AhEnumActValue.YES_WITH_VALUE);
		
		PseProfilePowerMode mode = new PseProfilePowerMode();
		mode.setOperation(AhEnumAct.YES);
		mode.setPowerLimit(CLICommonFunc.createAhIntActObj(pseImpl.getPseProfileThresholdPower(index), true));
		mode.setValue(pseImpl.getPseProfilePowerMode(index));
		profile.setPowerMode(mode);
		
		PseProfilePriority priority = new PseProfilePriority();
		priority.setOperation(AhEnumAct.YES);
		priority.setValue(pseImpl.getPseProfilePriority(index));
		profile.setPriority(priority);
		return profile;
	}
	
}
