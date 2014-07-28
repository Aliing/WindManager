/**
 * @filename			OEMCustomer.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				Beijing
 *
 * Copyright (c) 2006-2010 Aerohive Co., Ltd.
 * All right reserved.
 */
package com.ah.be.common;

import java.util.HashMap;
import java.util.Map;

import com.ah.bo.hiveap.HiveAp;

/**
 * The class represents OEM customer
 */
public class OEMCustomer {
	
	private String blackboxFlag;

	private String companyName;

	private String companyFullName;

	private String companyAbbreviation;

	private String companyAddress;

	private String homePage;

	private String defaultHMPassword;

	private String defaultAPPassword;

	private String defaultAccessConsolePassword;

	private String SNMPContact;

	private String[] MACOUI;

	private String supportMail;
	
	private String supportPhoneNumberUS;
	private String supportPhoneNumber;

	private String salesMail;

	private String ordersMail;

	private String helpLink;

	private String registerUrl;

	private String accessPonitName;

	private String accessPonitNameBR;

	private String accessPonitNameCVG;

	private String nmsName;

	private String nmsNameAbbreviation;

	private String accessPointOS;

	private String wirelessUnitName;

	private String nmsCopyright;

	private Map<String, String> apSeries;

	private Map<String, String> hmModelNumber;

	private String apLowestVersion;

	private boolean expressModeEnable;

	private String defaultLsUrl;

	private String dnsSuffix;
	
	private String tellMeMoreIDM;

	/**
	 * Key of HiveManager Appliance 1U in the model number map.
	 */
	public final static String HM_MODEL_APP_1U_KEY = "APP_1U";

	/**
	 * Key of HiveManager Appliance 2U in the model number map.
	 */
	public final static String HM_MODEL_APP_2U_KEY = "APP_2U";

	/**
	 * Key of HiveManager VmWare 1U in the model number map.
	 */
	public final static String HM_MODEL_VM_1U_KEY = "VM_1U";

	/**
	 * Key of HiveManager VmWare 2U in the model number map.
	 */
	public final static String HM_MODEL_VM_2U_KEY = "VM_2U";

	public OEMCustomer() {

	}

	public void addApSeries(String ap, String oem) {
		if(this.getApSeries() == null) {
			this.setApSeries(new HashMap<String, String>());
		}

		this.getApSeries().put(ap, oem);
	}

	public void addHMModelNumber(String model, String oem) {
		if(this.getHmModelNumber() == null) {
			this.setHmModelNumber(new HashMap<String, String>());
		}

		this.getHmModelNumber().put(model, oem);
	}

	/**
	 * getter of companyName
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}


	/**
	 * setter of companyName
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	/**
	 * getter of companyAddress
	 * @return the companyAddress
	 */
	public String getCompanyAddress() {
		return companyAddress;
	}


	/**
	 * setter of companyAddress
	 * @param companyAddress the companyAddress to set
	 */
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}


	/**
	 * getter of homePage
	 * @return the homePage
	 */
	public String getHomePage() {
		return homePage;
	}


	/**
	 * setter of homePage
	 * @param homePage the homePage to set
	 */
	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}


	/**
	 * getter of defaultHMPassword
	 * @return the defaultHMPassword
	 */
	public String getDefaultHMPassword() {
		return defaultHMPassword;
	}


	/**
	 * setter of defaultHMPassword
	 * @param defaultHMPassword the defaultHMPassword to set
	 */
	public void setDefaultHMPassword(String defaultHMPassword) {
		this.defaultHMPassword = defaultHMPassword;
	}


	/**
	 * getter of defaultAPPassword
	 * @return the defaultAPPassword
	 */
	public String getDefaultAPPassword() {
		return defaultAPPassword;
	}


	/**
	 * setter of defaultAPPassword
	 * @param defaultAPPassword the defaultAPPassword to set
	 */
	public void setDefaultAPPassword(String defaultAPPassword) {
		this.defaultAPPassword = defaultAPPassword;
	}


	/**
	 * getter of sNMPContact
	 * @return the sNMPContact
	 */
	public String getSNMPContact() {
		return SNMPContact;
	}


	/**
	 * setter of sNMPContact
	 * @param contact the sNMPContact to set
	 */
	public void setSNMPContact(String contact) {
		SNMPContact = contact;
	}


	/**
	 * getter of mACOUI
	 * @return the mACOUI
	 */
	public String[] getMACOUI() {
		return MACOUI;
	}


	/**
	 * setter of mACOUI
	 * @param macoui the mACOUI to set
	 */
	public void setMACOUI(String[] macoui) {
		MACOUI = macoui;
	}


	/**
	 * getter of supportMail
	 * @return the supportMail
	 */
	public String getSupportMail() {
		return supportMail;
	}


	/**
	 * setter of supportMail
	 * @param supportMail the supportMail to set
	 */
	public void setSupportMail(String supportMail) {
		this.supportMail = supportMail;
	}


	/**
	 * getter of helpLink
	 * @return the helpLink
	 */
	public String getHelpLink() {
		return helpLink;
	}


	/**
	 * setter of helpLink
	 * @param helpLink the helpLink to set
	 */
	public void setHelpLink(String helpLink) {
		this.helpLink = helpLink;
	}


	/**
	 * getter of registerUrl
	 * @return the registerUrl
	 */
	public String getRegisterUrl() {
		return registerUrl;
	}

	/**
	 * setter of registerUrl
	 * @param registerUrl the registerUrl to set
	 */
	public void setRegisterUrl(String registerUrl) {
		this.registerUrl = registerUrl;
	}


	/**
	 * getter of salesMail
	 * @return the salesMail
	 */
	public String getSalesMail() {
		return salesMail;
	}

	/**
	 * setter of salesMail
	 * @param salesMail the salesMail to set
	 */
	public void setSalesMail(String salesMail) {
		this.salesMail = salesMail;
	}

	/**
	 * getter of ordersMail
	 * @return the ordersMail
	 */
	public String getOrdersMail() {
		return ordersMail;
	}

	/**
	 * setter of ordersMail
	 * @param ordersMail the ordersMail to set
	 */
	public void setOrdersMail(String ordersMail) {
		this.ordersMail = ordersMail;
	}

	/**
	 * getter of companyFullName
	 * @return the companyFullName
	 */
	public String getCompanyFullName() {
		return companyFullName;
	}

	/**
	 * setter of companyFullName
	 * @param companyFullName the companyFullName to set
	 */
	public void setCompanyFullName(String companyFullName) {
		this.companyFullName = companyFullName;
	}

	/**
	 * getter of companyAbbreviation
	 * @return the companyAbbreviation
	 */
	public String getCompanyAbbreviation() {
		return companyAbbreviation;
	}

	/**
	 * setter of companyAbbreviation
	 * @param companyAbbreviation the companyAbbreviation to set
	 */
	public void setCompanyAbbreviation(String companyAbbreviation) {
		this.companyAbbreviation = companyAbbreviation;
	}

	/**
	 * getter of accessPonitName
	 * @return the accessPonitName
	 */
	public String getAccessPonitName() {
		return accessPonitName;
	}

	public String getAccessPonitName(short deviceType){
		if(deviceType == HiveAp.Device_TYPE_HIVEAP){
			return this.accessPonitName;
		}else if(deviceType == HiveAp.Device_TYPE_BRANCH_ROUTER){
			return this.accessPonitNameBR;
		}else if(deviceType == HiveAp.Device_TYPE_VPN_GATEWAY){
			return this.accessPonitNameCVG;
		}else if(deviceType == HiveAp.Device_TYPE_VPN_BR){
			return this.accessPonitNameCVG;
		}else{
			return this.accessPonitName;
		}
	}

	/**
	 * setter of accessPonitName
	 * @param accessPonitName the accessPonitName to set
	 */
	public void setAccessPonitName(String accessPonitName) {
		this.accessPonitName = accessPonitName;
	}

	public String getAccessPonitNameBR() {
		return accessPonitNameBR;
	}

	public void setAccessPonitNameBR(String accessPonitNameBR) {
		this.accessPonitNameBR = accessPonitNameBR;
	}

	public String getAccessPonitNameCVG() {
		return accessPonitNameCVG;
	}

	public void setAccessPonitNameCVG(String accessPonitNameCVG) {
		this.accessPonitNameCVG = accessPonitNameCVG;
	}

	/**
	 * getter of nmsName
	 * @return the nmsName
	 */
	public String getNmsName() {
		return nmsName;
	}

	/**
	 * setter of nmsName
	 * @param nmsName the nmsName to set
	 */
	public void setNmsName(String nmsName) {
		this.nmsName = nmsName;
	}

	/**
	 * getter of nmsNameAbbreviation
	 * @return the nmsNameAbbreviation
	 */
	public String getNmsNameAbbreviation() {
		return nmsNameAbbreviation;
	}

	/**
	 * setter of nmsNameAbbreviation
	 * @param nmsNameAbbreviation the nmsNameAbbreviation to set
	 */
	public void setNmsNameAbbreviation(String nmsNameAbbreviation) {
		this.nmsNameAbbreviation = nmsNameAbbreviation;
	}

	/**
	 * getter of accessPointOS
	 * @return the accessPointOS
	 */
	public String getAccessPointOS() {
		return accessPointOS;
	}

	/**
	 * setter of accessPointOS
	 * @param accessPointOS the accessPointOS to set
	 */
	public void setAccessPointOS(String accessPointOS) {
		this.accessPointOS = accessPointOS;
	}

	/**
	 * getter of wirelessUnitName
	 * @return the wirelessUnitName
	 */
	public String getWirelessUnitName() {
		return wirelessUnitName;
	}

	/**
	 * setter of wirelessUnitName
	 * @param wirelessUnitName the wirelessUnitName to set
	 */
	public void setWirelessUnitName(String wirelessUnitName) {
		this.wirelessUnitName = wirelessUnitName;
	}

	/**
	 * getter of nmsCopyright
	 * @return the nmsCopyright
	 */
	public String getNmsCopyright() {
		return nmsCopyright;
	}

	/**
	 * setter of nmsCopyright
	 * @param nmsCopyright the nmsCopyright to set
	 */
	public void setNmsCopyright(String nmsCopyright) {
		this.nmsCopyright = nmsCopyright;
	}

	public String getCompanyNameWithoutBlank() {
		if(this.companyName == null) {
			return null;
		}

		String[] names = this.companyName.split(" ");
		StringBuffer name = new StringBuffer();

		for(String aName : names) {
			name.append(aName);
		}

		return name.toString();

	}


	/**
	 * getter of defaultAccessConsolePassword
	 * @return the defaultAccessConsolePassword
	 */
	public String getDefaultAccessConsolePassword() {
		return defaultAccessConsolePassword;
	}

	/**
	 * setter of defaultAccessConsolePassword
	 * @param defaultAccessConsolePassword the defaultAccessConsolePassword to set
	 */
	public void setDefaultAccessConsolePassword(String defaultAccessConsolePassword) {
		this.defaultAccessConsolePassword = defaultAccessConsolePassword;
	}

	/**
	 * getter of apSeries
	 * @return the apSeries
	 */
	public Map<String, String> getApSeries() {
		return apSeries;
	}

	/**
	 * setter of apSeries
	 * @param apSeries the apSeries to set
	 */
	public void setApSeries(Map<String, String> apSeries) {
		this.apSeries = apSeries;
	}

	/**
	 * getter of hmModelNumber
	 * @return the hmModelNumber
	 */
	public Map<String, String> getHmModelNumber() {
		return hmModelNumber;
	}

	/**
	 * setter of hmModelNumber
	 * @param hmModelNumber the hmModelNumber to set
	 */
	public void setHmModelNumber(Map<String, String> hmModelNumber) {
		this.hmModelNumber = hmModelNumber;
	}

	/**
	 * getter of apLowestVersion
	 * @return the apLowestVersion
	 */
	public String getApLowestVersion() {
		return apLowestVersion;
	}

	/**
	 * setter of apLowestVersion
	 * @param apLowestVersion the apLowestVersion to set
	 */
	public void setApLowestVersion(String apLowestVersion) {
		this.apLowestVersion = apLowestVersion;
	}

	/**
	 * @param args
	 * @author Joseph Chen
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the expressModeEnable
	 */
	public boolean getExpressModeEnable() {
		return expressModeEnable;
	}

	/**
	 * @param expressModeEnable the expressModeEnable to set
	 */
	public void setExpressModeEnable(boolean expressModeEnable) {
		this.expressModeEnable = expressModeEnable;
	}

	public String getDefaultLsUrl()
	{
		return defaultLsUrl;
	}

	public void setDefaultLsUrl(String defaultLsUrl)
	{
		this.defaultLsUrl = defaultLsUrl;
	}

	public String getDnsSuffix() {
		return dnsSuffix;
	}

	public void setDnsSuffix(String dnsSuffix) {
		this.dnsSuffix = dnsSuffix;
	}

    public String getTellMeMoreIDM() {
        return tellMeMoreIDM;
    }

    public void setTellMeMoreIDM(String tellMeMoreIDM) {
        this.tellMeMoreIDM = tellMeMoreIDM;
    }

    public String getSupportPhoneNumberUS() {
        return supportPhoneNumberUS;
    }

    public String getSupportPhoneNumber() {
        return supportPhoneNumber;
    }

    public void setSupportPhoneNumberUS(String supportPhoneNumberUS) {
        this.supportPhoneNumberUS = supportPhoneNumberUS;
    }

    public void setSupportPhoneNumber(String supportPhoneNumber) {
        this.supportPhoneNumber = supportPhoneNumber;
    }

	public String getBlackboxFlag() {
		return blackboxFlag;
	}

	public void setBlackboxFlag(String blackboxFlag) {
		this.blackboxFlag = blackboxFlag;
	}
}
