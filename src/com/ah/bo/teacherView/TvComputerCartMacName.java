/**
 *@filename		SingleTableItem.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-17 PM 03:33:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.teacherView;

import java.io.Serializable;

import javax.persistence.Embeddable;


/**
 * @author		fisher
 * @version		V1.0.0.0 
 */
@Embeddable
public class TvComputerCartMacName implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	
	private String	stuMac;

	private String	stuName;

	/**
	 * @return the stuMac
	 */
	public String getStuMac() {
		return stuMac;
	}

	/**
	 * @param stuMac the stuMac to set
	 */
	public void setStuMac(String stuMac) {
		this.stuMac = stuMac;
	}

	/**
	 * @return the stuName
	 */
	public String getStuName() {
		return stuName;
	}

	/**
	 * @param stuName the stuName to set
	 */
	public void setStuName(String stuName) {
		this.stuName = stuName;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof TvComputerCartMacName)) {
			return false;
		}

		final TvComputerCartMacName cart = (TvComputerCartMacName) o;

		return stuMac != null ? stuMac.equals(cart.stuMac)
				: cart.stuMac == null;
	}

	public int hashCode() {
		int result;
		result = (stuMac != null ? stuMac.hashCode() : 0);
		return result;
	}
}
