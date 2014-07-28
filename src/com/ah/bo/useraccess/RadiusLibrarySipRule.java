/**
 *@filename		RadiusLibrarySipRule.java
 *@version
 *@author		Fiona
 *@createtime	2010-10-13 06:55:57
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.useraccess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.HmBoBase;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class RadiusLibrarySipRule implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	@Range(min = 1)
	private short ruleId;
	
	@Column(length = 2)
	private String field;
	
	public static final short SIP_OPERATOR_MATCH = 1;
	
	public static final short SIP_OPERATOR_CONTAIN = 2;
	
	public static final short SIP_OPERATOR_DIFFER = 3;
	
	public static final short SIP_OPERATOR_START = 4;
	
	public static final short SIP_OPERATOR_OCCUR_AFTER = 5;
	
	public static final short SIP_OPERATOR_OCCUR_BEFORE = 6;
	
	public static final short SIP_OPERATOR_EQUAL = 7;
	
	public static final short SIP_OPERATOR_GREATER_THAN = 8;
	
	public static final short SIP_OPERATOR_LESS_THAN = 9;
	
	public static EnumItem[] ENUM_SIP_OPERATOR = MgrUtil.enumItems(
		"enum.config.radius.library.sip.operator.", new int[] { SIP_OPERATOR_MATCH, SIP_OPERATOR_CONTAIN,
			SIP_OPERATOR_DIFFER, SIP_OPERATOR_START, SIP_OPERATOR_OCCUR_AFTER, SIP_OPERATOR_OCCUR_BEFORE,
			SIP_OPERATOR_EQUAL, SIP_OPERATOR_GREATER_THAN, SIP_OPERATOR_LESS_THAN});
	
	private short operator = SIP_OPERATOR_MATCH;
	
	@Column(length = HmBoBase.DEFAULT_STRING_LENGTH)
	private String valueStr;
	
	public static final String[] BL_FIELD_VALID_PATRON = new String[] {
		"", "Y", "N"
	};
	
	public static final String[] BH_FIELD_CURRENCY_TYPE = new String[]{
		"",
		"USD",
		"AED", 
		"AFN", 
		"ALL", 
		"AMD", 
		"ANG",
		"AOA",
		"ARS",
		"AUD", 
		"AWG", 
		"AZN",
		"BAM", 
		"BBD", 
		"BDT", 
		"BGN", 
		"BHD", 
		"BIF", 
		"BMD", 
		"BND", 
		"BOB", 
		"BRL", 
		"BSD", 
		"BTN", 
		"BWP", 
		"BYR", 
		"BZD",
		"CAD", 
		"CDF", 
		"CHF", 
		"CLP", 
		"CNY",
		"COP", 
		"CRC", 
		"CUP", 
		"CVE",
		"CYP", 
		"CZK", 
		"DJF", 
		"DKK", 
		"DOP",
		"DZD", 
		"EEK", 
		"EGP", 
		"ERN", 
		"ETB", 
		"EUR", 
		"FJD",
		"FKP", 
		"GBP", 
		"GEL", 
		"GGP", 
		"GHS", 
		"GIP", 
		"GMD", 
		"GNF", 
		"GTQ", 
		"GYD", 
		"HKD", 
		"HNL", 
		"HRK", 
		"HTG", 
		"HUF", 
		"IDR", 
		"ILS", 
		"IMP",
		"INR", 
		"IQD", 
		"IRR", 
		"ISK", 
		"JEP", 
		"JMD", 
		"JOD", 
		"JPY",
		"KES", 
		"KGS", 
		"KHR", 
		"KMF",
		"KPW", 
		"KRW",
		"KWD", 
		"KYD", 
		"KZT",
		"LAK", 
		"LBP", 
		"LKR", 
		"LRD", 
		"LSL", 
		"LTL", 
		"LVL", 
		"LYD", 
		"MAD", 
		"MDL", 
		"MGA", 
		"MKD", 
		"MMK", 
		"MNT", 
		"MOP", 
		"MRO", 
		"MTL", 
		"MUR", 
		"MVR", 
		"MWK", 
		"MXN", 
		"MYR", 
		"MZN", 
		"NAD", 
		"NGN", 
		"NIO", 
		"NOK", 
		"NPR", 
		"NZD", 
		"OMR", 
		"PAB", 
		"PEN", 
		"PGK", 
		"PHP", 
		"PKR", 
		"PLN",
		"PYG", 
		"QAR", 
		"RON", 
		"RSD", 
		"RUB", 
		"RWF", 
		"SAR", 
		"SBD", 
		"SCR", 
		"SDG", 
		"SEK", 
		"SGD", 
		"SHP", 
		"SLL",
		"SOS", 
		"SPL", 
		"SRD", 
		"STD", 
		"SVC", 
		"SYP", 
		"SZL", 
		"THB", 
		"TJS", 
		"TMM", 
		"TND", 
		"TOP", 
		"TRY", 
		"TTD",
		"TVD", 
		"TWD", 
		"TZS", 
		"UAH", 
		"UGX",  
		"UYU", 
		"UZS", 
		"VEB", 
		"VEF",
		"VND",
		"VUV", 
		"WST", 
		"XAF",
		"XAG",
		"XAU", 
		"XCD", 
		"XDR",
		"XOF", 
		"XPD", 
		"XPF",
		"XPT", 
		"YER", 
		"ZAR", 
		"ZMK", 
		"ZWD"
	};
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_GROUP_ID", nullable = false)
	private LocalUserGroup userGroup;
	
	public static final short SIP_RULE_ACTION_PERMIT = 1;
	
	public static final short SIP_RULE_ACTION_DENY = 2;
	
	public static final short SIP_RULE_ACTION_RESTRICTED = 3;
	
	public static EnumItem[] ENUM_SIP_RULE_ACTION = MgrUtil.enumItems(
		"enum.config.radius.library.sip.action.", new int[] { SIP_RULE_ACTION_PERMIT,
			SIP_RULE_ACTION_DENY, SIP_RULE_ACTION_RESTRICTED});
	
	private short action = SIP_RULE_ACTION_PERMIT;
	
	private String message;

	public short getRuleId()
	{
		return ruleId;
	}

	public void setRuleId(short ruleId)
	{
		this.ruleId = ruleId;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public short getOperator()
	{
		return operator;
	}

	public void setOperator(short operator)
	{
		this.operator = operator;
	}

	public String getValueStr()
	{
		return valueStr;
	}

	public void setValueStr(String valueStr)
	{
		this.valueStr = valueStr;
	}

	public LocalUserGroup getUserGroup()
	{
		return userGroup;
	}

	public void setUserGroup(LocalUserGroup userGroup)
	{
		this.userGroup = userGroup;
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
	public String getOperStr() {
		return MgrUtil.getEnumString("enum.config.radius.library.sip.operator."+operator);
	}
	
	@Transient
	public String getActionStr() {
		return MgrUtil.getEnumString("enum.config.radius.library.sip.action."+action);
	}
	
	@Transient
	public String getRuleDescription() {
		return "  "+MgrUtil.getUserMessage("config.radius.library.sip.policy.rule.description", 
			new String[]{getOperStr(), valueStr, userGroup.getGroupName(), getActionStr().toLowerCase(), getMessageStr()});
	}
	
	@Transient
	public String getMessageStr() {
		if (null == message || "".equals(message)) {
			return "";
		} else {
			String[] words = message.split(" ");
			if (words.length > 5) {
				StringBuffer result = new StringBuffer();
				for (int i = 0; i < 5; i++) {
					result.append(words[i]+" ");
				}
				result.append("...");
				return result.toString();
			} else {
				return message;
			}
		}
	}

	public short getAction()
	{
		return action;
	}

	public void setAction(short action)
	{
		this.action = action;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}
