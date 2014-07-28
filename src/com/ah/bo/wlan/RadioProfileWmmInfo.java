/**
 *@filename		RadioProfileWmmInfo.java
 *@version
 *@author		Fiona
 *@createtime	2009-03-10 PM 06:57:00
 *Copyright (c) 2006-2009 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 */
package com.ah.bo.wlan;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class RadioProfileWmmInfo implements Serializable {

	private static final long	serialVersionUID	= 1L;
	
	@Transient
	private AccessCategory acType = null;
	
	public enum AccessCategory {
		BG, BE, VI, VO
	}
	
	private boolean noAck = false;
	
	@Range(min=0, max=15)
	private short aifs;
	
	@Range(min=1, max=15)
	private short minimum;
	
	@Range(min=1, max=15)
	private short maximum;
	
	@Range(min=0, max=8192)
	private int txoplimit;
	
	public String getkey() {
		return acType.name();
	}

	public String getValue() {
		return MgrUtil.getEnumString("enum.radio.profile.wmm.ac."
			+ acType.name().toLowerCase());
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

	public AccessCategory getAcType() {
		return acType;
	}

	public void setAcType(AccessCategory acType) {
		this.acType = acType;
	}

	public boolean isNoAck() {
		return noAck;
	}

	public void setNoAck(boolean noAck) {
		this.noAck = noAck;
	}

	public short getAifs() {
		return aifs;
	}

	public void setAifs(short aifs) {
		this.aifs = aifs;
	}

	public short getMinimum() {
		return minimum;
	}

	public void setMinimum(short minimum) {
		this.minimum = minimum;
	}

	public short getMaximum() {
		return maximum;
	}

	public void setMaximum(short maximum) {
		this.maximum = maximum;
	}

	public int getTxoplimit() {
		return txoplimit;
	}

	public void setTxoplimit(int txoplimit) {
		this.txoplimit = txoplimit;
	}

}