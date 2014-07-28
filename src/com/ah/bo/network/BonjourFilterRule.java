/**
 *@filename		BonjourFilterRule.java
 *@version
 *@author		WenPing
 *@createtime	2012-08-31 PM 03:25:16
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
 * @author		WenPing
 * @version		V1.0.0.0 
 */
@Embeddable
public class BonjourFilterRule implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	@Range(min = 1)
	private short ruleId;
	
	private String metric;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FROM_VLAN_GROUP_ID")
	private VlanGroup fromVlanGroup;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TO_VLAN_GROUP_ID")
	private VlanGroup toVlanGroup;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "BONJOUR_SERVICE_ID")
	private BonjourService bonjourService;
	
	private String realmName;
	
	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public short getRuleId()
	{
		return ruleId;
	}

	public void setRuleId(short ruleId)
	{
		this.ruleId = ruleId;
	}
	
	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public VlanGroup getFromVlanGroup() {
		return fromVlanGroup;
	}

	public void setFromVlanGroup(VlanGroup fromVlanGroup) {
		this.fromVlanGroup = fromVlanGroup;
	}

	public VlanGroup getToVlanGroup() {
		return toVlanGroup;
	}

	public void setToVlanGroup(VlanGroup toVlanGroup) {
		this.toVlanGroup = toVlanGroup;
	}

	public BonjourService getBonjourService() {
		return bonjourService;
	}

	public void setBonjourService(BonjourService bonjourService) {
		this.bonjourService = bonjourService;
	}

	@Transient
	private int reorder;

	public int getReorder() {
		return reorder;
	}

	public void setReorder(int reorder) {
		this.reorder = reorder;
	}

}
