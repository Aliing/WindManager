/**
 *@filename		FirewallPolicy.java
 *@version
 *@author		Fiona
 *@createtime	2011-6-15 PM 03:24:30
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "FIREWALL_POLICY")
@org.hibernate.annotations.Table(appliesTo = "FIREWALL_POLICY", indexes = {
		@Index(name = "FIREWALL_POLICY_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class FirewallPolicy implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String policyName;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String description;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "FIREWALL_POLICY_RULE", joinColumns = @JoinColumn(name = "FIREWALL_POLICY_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<FirewallPolicyRule> rules = new ArrayList<FirewallPolicyRule>();
	
	public static final short FIREWALL_POLICY_TYPE_ANY = 1;
	
	public static final short FIREWALL_POLICY_TYPE_IPNET = 2;
	
	public static final short FIREWALL_POLICY_TYPE_IPRANGE = 3;
	
	public static final short FIREWALL_POLICY_TYPE_NETOBJ = 4;
	
	public static final short FIREWALL_POLICY_TYPE_UPOBJ = 5;
	
	public static final short FIREWALL_POLICY_TYPE_VPN = 6;
	
	public static final short FIREWALL_POLICY_TYPE_WILDCARD = 7;
	
	public static final short FIREWALL_POLICY_TYPE_HOST = 8;
	
	public static final short FIREWALL_POLICY_TYPE_CLINET = 9;

	public static EnumItem[] ENUM_FIREWALL_POLICY_TYPE1 = MgrUtil.enumItems(
			"enum.config.security.firewall.policy.type.", new int[] { FIREWALL_POLICY_TYPE_ANY,
				FIREWALL_POLICY_TYPE_IPNET, FIREWALL_POLICY_TYPE_IPRANGE, FIREWALL_POLICY_TYPE_NETOBJ, FIREWALL_POLICY_TYPE_UPOBJ,
				FIREWALL_POLICY_TYPE_VPN, FIREWALL_POLICY_TYPE_WILDCARD });
	
	public static EnumItem[] ENUM_FIREWALL_POLICY_TYPE2 = MgrUtil.enumItems(
		"enum.config.security.firewall.policy.type.", new int[] { FIREWALL_POLICY_TYPE_ANY,
			FIREWALL_POLICY_TYPE_IPNET, FIREWALL_POLICY_TYPE_IPRANGE, FIREWALL_POLICY_TYPE_NETOBJ,
			FIREWALL_POLICY_TYPE_VPN, FIREWALL_POLICY_TYPE_WILDCARD, FIREWALL_POLICY_TYPE_HOST });
	
	private short defRuleAction = IpPolicyRule.POLICY_ACTION_PERMIT;
	
	private short defRuleLog = FirewallPolicyRule.POLICY_LOGGING_OFF;

	public List<FirewallPolicyRule> getRules()
	{
		return rules;
	}

	public short getDefRuleAction()
	{
		return defRuleAction;
	}

	public void setDefRuleAction(short defRuleAction)
	{
		this.defRuleAction = defRuleAction;
	}

	public short getDefRuleLog()
	{
		return defRuleLog;
	}

	public void setDefRuleLog(short defRuleLog)
	{
		this.defRuleLog = defRuleLog;
	}

	public void setRules(List<FirewallPolicyRule> rules)
	{
		this.rules = rules;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return policyName;
	}
	
	@Transient
	private boolean addRuleInTop;
	
	@Transient
	private FirewallPolicyRule singleRule;
	
	public FirewallPolicyRule getSingleRule()
	{
		return singleRule;
	}

	public void setSingleRule(FirewallPolicyRule singleRule)
	{
		this.singleRule = singleRule;
	}

	public boolean isAddRuleInTop()
	{
		return addRuleInTop;
	}

	public void setAddRuleInTop(boolean addRuleInTop)
	{
		this.addRuleInTop = addRuleInTop;
	}

	@Override
	public FirewallPolicy clone() {
		try {
			return (FirewallPolicy) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}