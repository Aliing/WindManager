package com.ah.be.config.create.bootstrap;

import com.ah.be.config.create.CreateXMLException;

public interface BootstrapInt {
	
	public String getApVersion();

	public String getHiveApHostName();

	public String getHiveId();

	public boolean isConfigureHiveProfile();

	public int getHiveApNativeVlanId() throws CreateXMLException;

	public int getHiveApMgtVlanId() throws CreateXMLException;

	public boolean isConfigureMgtVlan();

	public boolean isConfigureHivePassword();
	
//	public boolean isConfigCwp() throws CreateXMLException;

	public String getHivePassword();
	
	public boolean isConfigSnmp();

	public String getSnmpLocation();

	public String getRootAdminUser();

	public String getRootAdminPassword();

	public boolean isConfigureRootAdmin();

	public boolean isConfigureRootPassWord();
	
	public boolean isEnableCwpDtls();
	
	public boolean isConfigCwpDtlsBootPassPhrase();
	
	public String getCwpDtlsBootPassPhrase();
	
	public int getCwpHeartbeatInterval();
	
	public int getCwpServerPort();
	
//	public boolean isConfigHiveNativeVlan() throws CreateXMLException;
	
//	public boolean isConfigMgtNativeVlan() throws CreateXMLException;
	
//	public int getMgtNativeVlan();
	
	public int getCwpDeadInterval();
	
	public boolean isConfigCwpServerName();
	
	public boolean isConfigCwpServerPrimary();
	
	public boolean isConfigCwpServerSecond();

	public String getCwpServerName();
	
	public String getCwpServerNameSecond();
	
	public String getVhmName();
	
	public boolean isConfigBootParam();
	
	public String getNetdumpServer();
	
	public boolean isEnableNetdump();

}