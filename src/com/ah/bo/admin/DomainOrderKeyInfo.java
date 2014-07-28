/**
 *@filename		DomainOrderKeyInfo.java
 *@version
 *@author		Fiona
 *@createtime	Mar 12, 2010 2:28:52 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.admin;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.be.common.AerohiveEncryptTool;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseOperationTool;
import com.ah.bo.HmBo;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
@Entity
@Table(name = "DOMAIN_ORDER_KEY_INFO")
@org.hibernate.annotations.Table(appliesTo = "DOMAIN_ORDER_KEY_INFO", indexes = {
		@Index(name = "DOMAIN_ORDER_KEY_INFO_DOMAINNAME", columnNames = { "DOMAINNAME" })
	})
public class DomainOrderKeyInfo implements HmBo {

	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_ORDER_KEY = "DEFAULT-ORDER-KEY";

	@Id
	@GeneratedValue
	private Long id;

	private String hoursUsed;
	
	private long createTime = 0;
	
	/*
	 * For HA begin
	 */
	private String systemId;
	
	/*
	 * For HA end
	 */
	private String domainName;
	
	private String orderKey;
	
	// encrypt order key information
	// 00(type)+0000000(ap number)+0000(vhm number)+000(evalu days)+"home"
	// 00(type)+0000000(ap number)+0000(vhm number)+000(evalu days)+00000(cvg number)+"home"
	private String licenseStr;
	
	// from congo fcs we change the design of evaluation entitlement key
	private short evalKeyChange = 0;

	public short getEvalKeyChange() {
		return evalKeyChange;
	}

	public void setEvalKeyChange(short evalKeyChange) {
		this.evalKeyChange = evalKeyChange;
	}

	// ------------implement interface
	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getLabel() {
		return "Domain Entitlement Key";
	}

	@Override
	public HmDomain getOwner() {
		return null;
	}

	@Override
	public void setOwner(HmDomain owner) {
		//this.owner = owner;
	}

	@Version
	private Timestamp version;

	@Override
	public Timestamp getVersion() {
		return version;
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
	public void setId(Long id)
	{
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getHoursUsed() {
		return hoursUsed;
	}

	public void setHoursUsed(String hoursUsed) {
		this.hoursUsed = hoursUsed;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@Transient
	public int getHoursUsedInt() {
		if (HmDomain.HOME_DOMAIN.equals(domainName)) {
			if (null == hoursUsed || "".equals(hoursUsed)) {
				return 0;
			} else {
				return LicenseOperationTool.getDecryptedHours(hoursUsed, domainName);
			}
		} else if (NmsUtil.isPlanner()) {
			return 0;
		} else {
			return (int)((System.currentTimeMillis()-createTime)/(1000l*60l*60l));
		}
	}
	
	@Transient
	public String getEncriptString(int descript) {
		return LicenseOperationTool.getEncryptedHours(descript, domainName);
	}

	public String getLicenseStr() {
		return licenseStr;
	}

	public void setLicenseStr(String licenseStr) {
		this.licenseStr = licenseStr;
	}
	
	@Transient
	public void setEncryptLicense(String type, int apNum, int vhmNum, int evalDays, int cvgNum) {
		AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(domainName);
		setLicenseStr(encryptTool.encrypt(type+getChangedStr(apNum, 7)+getChangedStr(vhmNum, 4)+getChangedStr(evalDays, 3)+getChangedStr(cvgNum, 5)+domainName));
	}
	
	@Transient
	private String getChangedStr(int number, int len) {
		String result = String.valueOf(number);
		while (result.length() < len) {
			result = "0" + result;
		}
		return result;
	}
	
	@Transient
	public int[] getOrderInfo() {
		int[] result = new int[5];
		AerohiveEncryptTool encryptTool = new AerohiveEncryptTool(domainName);
		String totalStr = encryptTool.decrypt(licenseStr);
		if (null != totalStr && (totalStr.length() == (16+domainName.length())
			|| totalStr.length() == (21+domainName.length()))) {
			try {
				// type
				result[0] = Integer.valueOf(totalStr.substring(0, 2));
				// ap number
				result[1] = Integer.valueOf(totalStr.substring(2, 9));
				// vhm number
				result[2] = Integer.valueOf(totalStr.substring(9, 13));
				// evaluation days
				result[3] = Integer.valueOf(totalStr.substring(13, 16));
				
				if (totalStr.length() == (21+domainName.length())) {
					result[4] = Integer.valueOf(totalStr.substring(16, 21));
				} else if (result[0] == 0) {
					result[4] = HmDomain.HOME_DOMAIN.equals(domainName) ? 
						BeLicenseModule.AH_LICENSE_NO_ENTITLEMENT_KEY_CVG_COUNT : BeLicenseModule.AH_LICENSE_NO_ENTITLEMENT_KEY_CVG_COUNT_VHM;
				} else {
					result[4] = 0;
				}
			} catch (NumberFormatException nfe) {
				return result;
			}
		}
		return result;
	}

	public String getOrderKey() {
		return orderKey;
	}

	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}

	public String getSystemId()
	{
		return systemId;
	}

	public void setSystemId(String systemId)
	{
		this.systemId = systemId;
	}
	
}