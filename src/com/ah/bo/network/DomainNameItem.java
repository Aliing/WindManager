/**
 *@filename		DomainNameItem.java
 *@version
 *@author		Fiona
 *@createtime	2011-2-25 AM 10:32:46
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

import com.ah.bo.HmBo;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Embeddable
public class DomainNameItem implements Serializable, Cloneable {
	private static final long	serialVersionUID	= 1L;
	
	@Column(length = HmBo.DEFAULT_STRING_LENGTH)
	private String	domainName;

	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String	description;

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(String domainName)
	{
		this.domainName = domainName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
