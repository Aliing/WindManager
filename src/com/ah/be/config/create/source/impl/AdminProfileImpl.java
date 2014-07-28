package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.AdminProfileInt;
import com.ah.be.config.util.AhConfigUtil;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.IpFilter;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.wlan.Cwp;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 * 
 */
@SuppressWarnings("static-access")
public class AdminProfileImpl implements AdminProfileInt {

	private final HiveAp hiveAp;
	private IpFilter manageIpFilter;
	private Set<IpAddress> ipAddress;
	private final HmStartConfig hmStart;

	public AdminProfileImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;

		manageIpFilter = hiveAp.getConfigTemplate().getIpFilter();
		if (manageIpFilter != null) {
			ipAddress = manageIpFilter.getIpAddress();
		}
		hmStart = MgrUtil.getQueryEntity().findBoByAttribute(HmStartConfig.class, "owner", hiveAp.getOwner());
	}

	public String getUpdateTime() {
		List<Object> adminTime = new ArrayList<Object>();
		adminTime.add(hiveAp);
		if (ipAddress != null) {
			adminTime.addAll(ipAddress);
		}
		adminTime.add(manageIpFilter);
		adminTime.add(hiveAp.getConfigTemplate().getMgmtServiceOption());
		return CLICommonFunc.getLastUpdateTime(adminTime);
	}
	
	public String getHiveApGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.configuration");
	}
	
	public String getHiveApName(){
		return hiveAp.getHostName();
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public boolean isConfigureAdmin() {
		return this.isConfigureReaderOnly() || this.isConfigureRootAdmin()
				|| this.isConfigureManageIp();
	}

	public boolean isConfigureReaderOnly() {
		return (hiveAp.getCfgReadOnlyUser() != null && !"".equals(hiveAp
				.getCfgReadOnlyUser()));
//				|| (hiveAp.getReadOnlyUser() != null && !"".equals(hiveAp
//						.getReadOnlyUser()));
	}

	public boolean isConfigureRootAdmin() {
//		return (hiveAp.getCfgAdminUser() != null && !"".equals(hiveAp
//				.getCfgAdminUser()))
//				|| (hiveAp.getAdminUser() != null && !"".equals(hiveAp
//						.getAdminUser()));
		return true;
	}

	public boolean isConfigureManageIp() {
		return manageIpFilter != null
				&& manageIpFilter.getIpAddress().size() > 0;
	}

	public int getReaderOnlySize() {
		return 1;
	}

	public String getReaderOnlyUser(int index) {
//		if (hiveAp.getCfgReadOnlyUser() != null
//				&& !"".equals(hiveAp.getCfgReadOnlyUser())) {
//			return hiveAp.getCfgReadOnlyUser();
//		} else {
//			return hiveAp.getReadOnlyUser();
//		}
		return hiveAp.getCfgReadOnlyUser();
	}

	public String getReaderOnlyPassword(int index) {
		String passWord = hiveAp.getCfgReadOnlyPassword();
//		if (hiveAp.getCfgReadOnlyPassword() != null
//				&& !"".equals(hiveAp.getCfgReadOnlyPassword())) {
//			passWord = hiveAp.getCfgReadOnlyPassword();
//		} else {
//			passWord = hiveAp.getReadOnlyPassword();
//		}
		return AhConfigUtil.hiveApUserPwdEncrypt(passWord);
	}

	public String getRootAdminUser() {
		String userName;
		if (hiveAp.getCfgAdminUser() != null&& !"".equals(hiveAp.getCfgAdminUser())) {
			userName = hiveAp.getCfgAdminUser();
		}else if(hiveAp.getAdminUser() != null && !"".equals(hiveAp.getAdminUser())){
			userName = hiveAp.getAdminUser();
		}else {
			userName = "admin";
		}
		return userName;
	}

	public String getRootAdminPassword() {
		String passWord = null;
		if (hiveAp.getCfgPassword() != null && !"".equals(hiveAp.getCfgPassword())) {
			passWord = hiveAp.getCfgPassword();
		}else if(hmStart.getHiveApPassword() != null && !"".equals(hmStart.getHiveApPassword())) {
			passWord = hmStart.getHiveApPassword();
		}else if(hiveAp.getAdminPassword() != null && !"".equals(hiveAp.getAdminPassword())){
			passWord = hiveAp.getAdminPassword();
		}
		return AhConfigUtil.hiveApUserPwdEncrypt(passWord);
	}
	
	public static String getRootAdminPassword(HiveAp hiveAp){
		String passWord = null;
		HmStartConfig hmStartS = MgrUtil.getQueryEntity().findBoByAttribute(HmStartConfig.class, "owner", hiveAp.getOwner());
		if (hiveAp.getCfgPassword() != null && !"".equals(hiveAp.getCfgPassword())) {
			passWord = hiveAp.getCfgPassword();
		}else if(hmStartS.getHiveApPassword() != null && !"".equals(hmStartS.getHiveApPassword())) {
			passWord = hmStartS.getHiveApPassword();
		}else if(hiveAp.getAdminPassword() != null && !"".equals(hiveAp.getAdminPassword())){
			passWord = hiveAp.getAdminPassword();
		}
		return passWord;
	}

	public int getManageIpSize() {
		return ipAddress.size();
	}
	
	

	public String getManageIpAndMask(int index) throws CreateXMLException {
		IpAddress ipAddressObj = (IpAddress) ipAddress.toArray()[index];
		SingleTableItem ipObj = CLICommonFunc
				.getIpAddress(ipAddressObj, hiveAp);
		return CLICommonFunc.countIpAndMask(ipObj.getIpAddress(), ipObj.getNetmask()) + "/"
				+ CLICommonFunc.turnNetMaskToNum(ipObj.getNetmask());
	}
	
	public boolean isConfigAuthTypeLocal(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().getUserAuth() == EnumConstUtil.ADMIN_USER_AUTHENTICATION_LOCAL;
	}
	
	public boolean isConfigAuthTypeBoth(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().getUserAuth() == EnumConstUtil.ADMIN_USER_AUTHENTICATION_BOTH;
	}
	
	public boolean isConfigAuthTypeRadius(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().getUserAuth() == EnumConstUtil.ADMIN_USER_AUTHENTICATION_RADIUS;
	}

	public boolean isConfigAdminAuth() {
		return hiveAp.getConfigTemplate().getMgmtServiceOption() != null;
	}
	
	public boolean isAuthTypePap(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().getRadiusAuthType() == Cwp.AUTH_METHOD_PAP;
	}
	
	public boolean isAuthTypeChap(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().getRadiusAuthType() == Cwp.AUTH_METHOD_CHAP;
	}
	
	public boolean isAuthTypeMschapv2(){
		return hiveAp.getConfigTemplate().getMgmtServiceOption().getRadiusAuthType() == Cwp.AUTH_METHOD_MSCHAPV2;
	}

}