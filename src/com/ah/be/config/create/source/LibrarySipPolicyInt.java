package com.ah.be.config.create.source;

public interface LibrarySipPolicyInt {

	public String getIpPolicyGuiName();
	
	public String getIpPolicyName();
	
	public String getDefaultGroupName();
	
	public int getLibrarySipPolicySize();
	
	public int getLibrarySipPolicyId(int index);
	
	public String getBeforeValue(int index);
	
	public boolean isConfigBefore(int index);
	
	public int getPolicyBeforeIdValue(int index);
	
	public String getFieldValue(int index);
	
	public boolean isConfigContains(int index);
	
	public boolean isConfigDiffersFrom(int index);
	
	public boolean isConfigMatches(int index);
	
	public boolean isConfigOccursAfter(int index);
	
	public boolean isConfigOccursBefore(int index);
	
	public boolean isConfigStartsWith(int index);
	
	public String getLibrarySipPolicyValue(int index);
	
	public String getLibrarySipPolicyGroup(int index);
	
	public boolean isConfigEqual(int index);
	
	public boolean isConfigGreaterThan(int index);
	
	public boolean isConfigLessThan(int index);
	
	public String getUserGroupAction(int index);
	
	public String getUserGroupMessage(int index);
	
	public String getDefUserGroupAction();
	
	public String getDefUserGroupMessage();
}
