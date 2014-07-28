/**
 *@filename		MacPolicyRule.java
 *@version
 *@author		Fiona
 *@createtime	2007-9-27 AM 09:43:47
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
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

/**
 * @author Fiona
 * @version V1.0.0.0
 */
@Embeddable
public class MacPolicyRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Range(min = 1)
	private short ruleId;

	private short filterAction = IpPolicyRule.POLICY_ACTION_DENY;

	public static final short	MAC_POLICY_MASK_ONE		= 1;

	public static final short	MAC_POLICY_MASK_TWO		= 2;

	public static final short	MAC_POLICY_MASK_THREE	= 3;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SOURCE_MAC_ID")
	private MacOrOui			sourceMac;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DESTINATION_MAC_ID")
	private MacOrOui			destinationMac;
	
	private short actionLog = IpPolicyRule.POLICY_LOGGING_OFF;

	@Transient
	public String getFilterActionString()
	{
		return MacFilter.getFilterActionString(filterAction);
	}

	public short getFilterAction()
	{
		return filterAction;
	}

	public void setFilterAction(short filterAction)
	{
		this.filterAction = filterAction;
	}

	public short getRuleId()
	{
		return ruleId;
	}

	public void setRuleId(short ruleId)
	{
		this.ruleId = ruleId;
	}

	public MacOrOui getSourceMac()
	{
		return sourceMac;
	}

	public void setSourceMac(MacOrOui sourceMac)
	{
		this.sourceMac = sourceMac;
	}

	public MacOrOui getDestinationMac()
	{
		return destinationMac;
	}

	public void setDestinationMac(MacOrOui destinationMac)
	{
		this.destinationMac = destinationMac;
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

	public short getActionLog()
	{
		return actionLog;
	}

	public void setActionLog(short actionLog)
	{
		this.actionLog = actionLog;
	}

}