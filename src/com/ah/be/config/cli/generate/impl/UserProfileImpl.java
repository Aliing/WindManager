package com.ah.be.config.cli.generate.impl;

import com.ah.be.config.cli.generate.CLIConfig;
import com.ah.be.config.cli.generate.AbstractAutoAdaptiveCLIGenerate;

@CLIConfig()
@Deprecated
public class UserProfileImpl extends AbstractAutoAdaptiveCLIGenerate {
	
//	private String upName;
	public UserProfileImpl() {}
	
	@Override
	public void init() {
//		upName = "up_123";
	}
	/*
	@CLIConfig(USER_PROFILE_ATTRIBUTE)*//** user-profile <string> attribute <string> *//*
	public CLIGenResult getUserProfileAttrs(){
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add(new Object[]{upName, 5});
		cliRes.add(new Object[]{upName, "10 - 20"});
		return cliRes;
	}

	*//** user-profile <string> vlan-id <number> *//*
	@CLIConfig(value=USER_PROFILE_VLAN_ID, version=">=6.1.1.0")
	public CLIGenResult getUPVlan(){
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add(new Object[]{upName, 1});
		return cliRes;
	}
	
	*//** user-profile <string> cac airtime-percentage <number> [{true:share-time|false:}] *//*
	@CLIConfig(USER_PROFILE_CAC_AIRTIME_PERCENTAGE)
	public CLIGenResult getAirtime(){
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add(new Object[]{upName, 100, false});
		return cliRes;
	}

	@CLIConfig
	public CLIGenResult getUserProfileAirtimeCLIs() throws CLIGenerateException{
		CLIGenResult cliRes = new CLIGenResult();
		*//** user-profile <string> qos-policy <string> *//*
		cliRes.add(USER_PROFILE_QOS_POLICY, new Object[]{upName, "q_name"} );
		*//** user-profile <string> mobility-policy <string> *//*
		cliRes.add(USER_PROFILE_MOBILITY_POLICY, new Object[]{upName, "mp_name"} );
		return cliRes;
	}
	
	@CLIConfig
	public CLIGenResult getTestClis() throws CLIGenerateException{
		CLIGenResult cliRes = new CLIGenResult();
		cliRes.add("111111");
		cliRes.add("222222");
		return cliRes;
	}
	*/
}