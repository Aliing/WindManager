/**
 *@filename		TX11aOr11gRateSetting.java
 *@version
 *@author		Fiona
 *@createtime	2008-5-6 PM 04:57:00
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.wlan;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class TX11aOr11gRateSetting implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final short RATE_SET_TYPE_BASIC = 0;

	public static final short RATE_SET_TYPE_OPT = 1;

	public static final short RATE_SET_TYPE_NEI = 2;
	
	public static EnumItem[] ENUM_RATE_SET_TYPE = MgrUtil.enumItems("enum.rateSetType.",
			new int[] { RATE_SET_TYPE_BASIC, RATE_SET_TYPE_OPT,
						RATE_SET_TYPE_NEI });
	
	public static EnumItem[] ENUM_RATE_SET_TYPE_11N = MgrUtil.enumItems("enum.rateSetType.",
			new int[] { RATE_SET_TYPE_OPT,
						RATE_SET_TYPE_NEI });
	
	@Transient
	private GRateType gRateType = null;
	
	@Transient
	private ARateType aRateType = null;
	
	@Transient
	private NRateType nRateType = null;
	
	// the 11g rate(Mbps)
	public enum GRateType {
		one, two, five, six, nine, eleven, twelve, eighteen, twenty_four, thirty_six, forty_eight, fifty_four
	}
	
	// the 11a rate(Mbps)
	public enum ARateType {
		six, nine, twelve, eighteen, twenty_four, thirty_six, forty_eight, fifty_four
	}
	
	public enum NRateType {
		zero, one, two, three, four, five, six, seven,eight,nine,ten,eleven,twelve,thirteen,fourteen,fifteen,
		sixteen,seventeen,eighteen,nineteen, twenty, twenty_one,twenty_two, twenty_three
	}
	
	private short rateSet = RATE_SET_TYPE_BASIC;

	public GRateType getGRateType() {
		return gRateType;
	}

	public void setGRateType(GRateType rateType) {
		gRateType = rateType;
	}

	public ARateType getARateType() {
		return aRateType;
	}

	public void setARateType(ARateType rateType) {
		aRateType = rateType;
	}

	public short getRateSet() {
		return rateSet;
	}

	public void setRateSet(short rateSet) {
		this.rateSet = rateSet;
	}
	
	public String getkey() {
		if (null != aRateType) {
			return aRateType.name();
		}
		if (null != gRateType) {
			return gRateType.name();
		}
		return nRateType.name();
	}
	
	public String getValue() {
		if (null != aRateType) {
			return MgrUtil.getEnumString("enum.radioTransmitRateA.withBlank."
					+ (aRateType.ordinal()+2));
		}
		if (null != gRateType) {
			return MgrUtil.getEnumString("enum.radioTransmitRateBG.withBlank."
					+ (gRateType.ordinal()+2));
		}

		return MgrUtil.getEnumString("enum.radioTransmitRateNA."
				+ (nRateType.ordinal()+10));

	}

	public NRateType getNRateType() {
		return nRateType;
	}

	public void setNRateType(NRateType rateType) {
		nRateType = rateType;
	}
	
}
