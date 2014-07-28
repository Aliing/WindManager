package com.ah.be.config.create.source;

import com.ah.xml.be.config.LanEthxModeValue;

public interface LanPortInt {
	
	public static enum LanType{
		eth0("eth0"), lan1("eth1"), lan2("eth2"), lan3("eth3"), lan4("eth4");
		
		private String value;
		
		LanType(){
			
		}
		
		LanType(String value){
			this.value = value;
		}
		
		public String getValue(){
			return this.value;
		}
	}

	public boolean isConfigLanProfile();
	
	public boolean isConfigLanPort(LanType lanType);
	
	public boolean isLanInterShutdown(LanType lanType);
	
	public LanEthxModeValue getLanInterMode(LanType lanType);
	
	public int getLanInterSize(LanType lanType);
	
	public int getLanInterVlan(LanType lanType, int index);
	
	public boolean isConfigVlanCheck();
	
	public boolean isVlanCheck();
}
