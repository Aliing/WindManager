package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

public interface BonjourGatewayInt {

	public boolean isEnableBonjourGateway();
	
	public int getBonjourGatewayFiletrRulesize();
	
	public String getBonjourGatewayFiletrRuleName(int index);
	
	public String getBonjourGatewayFiletrRuleId(int index);
	
	public String getWlanName();
	
	public String getBonjourGatewayGuiName();
	
	public boolean isConfigBonjouGateway();
	
	public int getBonjourVlanSize();
	
	public String getBonjourValn(int index);
	
	public boolean isConfigPriority();
	
	public int getPriority() throws CreateXMLException;
	
	public boolean isConfigBefore(int index);
	
	public String getBonjourBeforeIdValue(int index);
	
	public String getBeforeValue(int index);
	
	public int getReverseIndex(int index);
	
	public String getFromGroupName(int index);
	
	public String getToGroupName(int index);
	
	public boolean isConfigMetricValue(int index);
	
	public String getMetricValue(int index);
	
//	public boolean isConfigRuleAction();
	
//	public boolean isCOnfigRuleActionPermit(int index);
//	
//	public boolean isCOnfigRuleActionDeny(int index);
	
	public String getRealmName();
}
