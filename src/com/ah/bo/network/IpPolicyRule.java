/**
 *@filename		IpPolicyRule.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-19 AM 10:04:43
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;


/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Embeddable
public class IpPolicyRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Range(min = 1)
	private short ruleId;

	private short filterAction = POLICY_ACTION_DENY;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_IP_ID", nullable = true)
	private IpAddress sourceIp;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DESTINATION_IP_ID", nullable = true)
	private IpAddress desctinationIp;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "NETWORK_SERVICE_ID", nullable = true)
	private NetworkService networkService;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMAPP_SERVICE_ID", nullable = true)
	private CustomApplication customApp;
	
	private short serviceType;
	/**
	 * IP policy rule definite custom service type: 0
	 */
	public static final short RULE_NETWORKSERVICE_TYPE = 0;
	/**
	 * IP policy rule definite custom service type: 1
	 */
	public static final short RULE_CUSTOMSERVICE_TYPE = 1;
	
	public static final short POLICY_ACTION_PERMIT = 1;
	
	public static final short POLICY_ACTION_DENY = 2;
	
	public static final short POLICY_ACTION_TRAFFIC_DROP = 3;
	
	public static final short POLICY_ACTION_NAT = 4;
	
	public static EnumItem[] ENUM_IP_POLICY_ACTION = MgrUtil.enumItems(
		"enum.ipPolicyAction.", new int[] { POLICY_ACTION_PERMIT,
			POLICY_ACTION_DENY, POLICY_ACTION_TRAFFIC_DROP, POLICY_ACTION_NAT });
	
	public static EnumItem[] ENUM_MAC_POLICY_ACTION = MgrUtil.enumItems(
			"enum.ipPolicyAction.", new int[] { POLICY_ACTION_PERMIT,
				POLICY_ACTION_DENY });
	
	public static EnumItem[] L7_ENUM_IP_POLICY_ACTION = MgrUtil.enumItems(
			"enum.ipPolicyAction.", new int[] { POLICY_ACTION_PERMIT,
				POLICY_ACTION_DENY });
	
	public static final short POLICY_LOGGING_OFF = 1;
	
	public static final short POLICY_LOGGING_INITIATE = 2;
	
	public static final short POLICY_LOGGING_TERMINATE = 3;
	
	public static final short POLICY_LOGGING_BOTH = 4;
	
	public static final short POLICY_LOGGING_DROP = 5;

	public static EnumItem[] ENUM_POLICY_LOGGING_DENY = MgrUtil.enumItems(
			"enum.policyLogDeny.", new int[] { POLICY_LOGGING_OFF,
				POLICY_LOGGING_DROP });
	
	public static EnumItem[] ENUM_POLICY_LOGGING_PERMIT = MgrUtil.enumItems(
		"enum.policyLogPermit.", new int[] { POLICY_LOGGING_OFF,
			POLICY_LOGGING_INITIATE, POLICY_LOGGING_TERMINATE, POLICY_LOGGING_BOTH });
	
	public static EnumItem[] ENUM_POLICY_TRAFFIC_LOGGING_DROP = MgrUtil.enumItems(
		"enum.policyLogTrafficDrop.", new int[] { POLICY_LOGGING_OFF,
			POLICY_LOGGING_INITIATE, POLICY_LOGGING_TERMINATE, POLICY_LOGGING_DROP });
	
	public static EnumItem[] ENUM_POLICY_TRAFFIC_LOGGING_ALL = MgrUtil.enumItems(
			"enum.policyLogAll.", new int[] { POLICY_LOGGING_OFF,
				POLICY_LOGGING_INITIATE, POLICY_LOGGING_TERMINATE, POLICY_LOGGING_BOTH, POLICY_LOGGING_DROP });
	
	private short actionLog = POLICY_LOGGING_OFF;

	public short getActionLog()
	{
		return actionLog;
	}

	public void setActionLog(short actionLog)
	{
		this.actionLog = actionLog;
	}

	@Transient
	public String getFilterActionString() {
		return MacFilter.getFilterActionString(filterAction);
	}

	public IpAddress getDesctinationIp() {
		return desctinationIp;
	}

	public void setDesctinationIp(IpAddress desctinationIp) {
		this.desctinationIp = desctinationIp;
	}

	public short getFilterAction() {
		return filterAction;
	}

	public void setFilterAction(short filterAction) {
		this.filterAction = filterAction;
	}

	public NetworkService getNetworkService() {
		return networkService;
	}

	public void setNetworkService(NetworkService networkService) {
		this.networkService = networkService;
	}

	public IpAddress getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(IpAddress sourceIp) {
		this.sourceIp = sourceIp;
	}

	public short getRuleId() {
		return ruleId;
	}

	public void setRuleId(short ruleId) {
		this.ruleId = ruleId;
	}
	
	@Transient
	private int reorder;

	public int getReorder() {
		return reorder;
	}

	public void setReorder(int reorder) {
		this.reorder = reorder;
	}
	
	@Transient
	public String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}

	public CustomApplication getCustomApp() {
		return customApp;
	}

	public void setCustomApp(CustomApplication customApp) {
		this.customApp = customApp;
	}

	public short getServiceType() {
		return serviceType;
	}

	public void setServiceType(short serviceType) {
		this.serviceType = serviceType;
	}
	
	

}