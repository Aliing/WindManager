package com.ah.be.ls.data;

import com.ah.bo.admin.UserRegInfoForLs;

public class QueryLicenseInfo {
	private byte                m_data_type;
	
	// change to subscription end date from cairo (4.0r1)
	private String				subEndDate;

	private String				supportEndDate;

	private int					numberOfAp;
	
	// add from congo (5.0r1)
	private int					numberOfCvg = 0;
	
	private String              cvgSubEndDate;
	// add from congo end

	private int					numberOfVhm;

	private int				    licensetype;

	private int					numberOfEvalValidDays;

	private boolean				userManagerLicenseExistFlag;

	private String 			    UserManagerLicense;
	
	private UserRegInfoForLs    userRegInfo;
	
	//public static final int    License_Type_Permanent  = 3;
	public static final int    License_Type_Evaluation = 1;
	
	public void setDataType(byte dataType)
	{
		m_data_type = dataType;
	}
	
	public byte getDataType()
	{
		return m_data_type;
	}	

	public String getSubEndDate()
	{
		return subEndDate;
	}

	public void setSubEndDate(String subEndDate)
	{
		this.subEndDate = subEndDate;
	}

	public String getSupportEndDate() {
		return supportEndDate;
	}

	public void setSupportEndDate(String supportEndDate) {
		this.supportEndDate = supportEndDate;
	}

	public int getNumberOfAp() {
		return numberOfAp;
	}

	public void setNumberOfAp(int numberOfAp) {
		this.numberOfAp = numberOfAp;
	}

	public int getNumberOfVhm() {
		return numberOfVhm;
	}

	public void setNumberOfVhm(int numberOfVhm) {
		this.numberOfVhm = numberOfVhm;
	}

	public UserRegInfoForLs getUserRegInfo()
	{
		return userRegInfo;
	}

	public void setUserRegInfo(UserRegInfoForLs userRegInfo)
	{
		this.userRegInfo = userRegInfo;
	}

	public int getLicenseType() {
		return licensetype;
	}

	public void setLicenseType(int iLicenseType) {
		licensetype = iLicenseType;
	}

	public int getNumberOfEvalValidDays() {
		return numberOfEvalValidDays;
	}

	public void setNumberOfEvalValidDays(int numberOfEvalValidDays) {
		this.numberOfEvalValidDays = numberOfEvalValidDays;
	}

	public boolean isUserManagerLicenseExistFlag() {
		return userManagerLicenseExistFlag;
	}

	public void setUserManagerLicenseExistFlag(boolean userManagerLicenseExistFlag) {
		this.userManagerLicenseExistFlag = userManagerLicenseExistFlag;
	}

	public String getManageLicense() {
		return UserManagerLicense;
	}

	public void setManageLicense(String strManageLicense) {
		this.UserManagerLicense = strManageLicense;
	}

	public int getNumberOfCvg()
	{
		return numberOfCvg;
	}

	public void setNumberOfCvg(int numberOfCvg)
	{
		this.numberOfCvg = numberOfCvg;
	}

	public String getCvgSubEndDate()
	{
		return cvgSubEndDate;
	}

	public void setCvgSubEndDate(String cvgSubEndDate)
	{
		this.cvgSubEndDate = cvgSubEndDate;
	}

}
