/**
 *@filename		DhcpServerOptionsCustom.java
 *@version
 *@author		Fiona
 *@createtime	2008-10-9 AM 10:07:26
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.ah.be.common.NmsUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
@MappedSuperclass
public class DhcpServerOptionsCustom implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	private short number;
	
	public static final int[] CUSTOM_OPTION_LIMIT = new int[]{1, 3, 6, 7, 15, 26, 42, 44, 51, 58, 59, 69, 70};
	
	public static final short CUSTOM_TYPE_INTEGER = 1;
	
	public static final short CUSTOM_TYYPE_IP = 2;
	
	public static final short CUSTOM_TYYPE_STRING = 3;
	
	public static final short CUSTOM_TYYPE_HEX = 4;
	
	public static EnumItem[] ENUM_CUSTOM_TYYPE = MgrUtil.enumItems(
		"enum.dhcp.server.option.custom.type.", new int[] { CUSTOM_TYPE_INTEGER, CUSTOM_TYYPE_IP, CUSTOM_TYYPE_STRING, CUSTOM_TYYPE_HEX });
	
	private short type = CUSTOM_TYPE_INTEGER;
	
	@Column(length = 256)
	private String value;

	public short getNumber()
	{
		return number;
	}

	public void setNumber(short number)
	{
		this.number = number;
	}

	public short getType()
	{
		return type;
	}

	public void setType(short type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}
	
	public String getValue(String deviceVersion)
	{
		String result = value;
		if(NmsUtil.compareSoftwareVersion(deviceVersion, "6.1.6.0") < 0){
			if(type == CUSTOM_TYYPE_HEX && value != null && value.length() > 32){
				result = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
			}
		}
		
		return result;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Transient
	public String getStrType() {
		return MgrUtil.getEnumString("enum.dhcp.server.option.custom.type."
			+ type);
	}

}
