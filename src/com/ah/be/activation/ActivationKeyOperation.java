/**
 *@filename		ActivationKeyOperation.java
 *@version
 *@author		Fiona
 *@createtime	Apr 9, 2009 11:04:05 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.activation;

import java.util.List;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;

import com.ah.be.hiveap.HiveAPInfoFromeDatabase;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.license.LicenseOperationTool;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.be.ls.data.PacketAPClientInfoData;
import com.ah.be.ls.data.PacketActQueryData;
import com.ah.be.ls.data.PacketApInfoData;
import com.ah.be.ls.data.PacketInvalidActResponseData;
import com.ah.be.ls.data.PacketNewVersionFlagQueryData;
import com.ah.be.ls.data.PacketNewVersionFlagResponseData;
import com.ah.be.ls.data.PacketValidActResponseData;
import com.ah.be.ls.returndata.ActResponseData;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.ActivationKeyInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.LicenseServerSetting;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class ActivationKeyOperation {
	
	/**
	 * Send query and deal with the receive information.
	 *
	 * @param arg_Info -
	 * @param ifRetry if retry to send if failed
	 * @param arg_NewAck the new activation key
	 * @return String : "" if send successfully otherwise is error message
	 */
	public static String sendQueryAndReceive (ActivationKeyInfo arg_Info, boolean ifRetry, String arg_NewAck) {
		String errorMes = "";
		if (null != arg_Info) {
			/*
			 * send
			 */
			PacketActQueryData oSendata = new PacketActQueryData();
			ActResponseData oRecvData = new ActResponseData();
			
			// set activation key
			if (ifRetry) {
				oSendata.setActKey(arg_Info.getActivationKey());
			} else {
				oSendata.setActKey(arg_NewAck);
			}
			
			// set HiveManager IP
			oSendata.setHMIP(HmBeOsUtil.getHiveManagerIPAddr());
			
			// set HiveManager system id
			oSendata.setSystemId(arg_Info.getSystemId());
			
			// send successfully
			if(ClientSenderCenter.sendActQuery(oSendata, oRecvData)) {
				
				// valid response
				arg_Info.setStartRetryTimer(false);
				arg_Info.setHasRetryTime((byte)0);
				
				if(oRecvData.getResponseType() == CommConst.Valid_Response) {
					PacketValidActResponseData oData = oRecvData.getValidResponseData();
					
					// set new query period
					arg_Info.setQueryPeriod(oData.getPeriod());
					
					// set query retry time
					arg_Info.setQueryRetryTime(oData.getRetryTimes());
				
					// set query retry interval
					arg_Info.setQueryInterval(oData.getInterval());
					
					arg_Info.setActivateSuccess(true);
					
					if (!ifRetry) {
						arg_Info.setActivationKey(arg_NewAck);
					}
					arg_Info.initTheUsedHours();
					
				// invalid response
				} else {
					if (ifRetry) {
						arg_Info.setActivateSuccess(false);
						if (HM_License.getInstance().isVirtualMachineSystem()) {
							HmBeActivationUtil.ACTIVATION_KEY_VALID = false;
						}
					}
					PacketInvalidActResponseData oData = oRecvData.getInvalidResponse();
					errorMes = oData.getDesc();
				}
			// send failed
			} else {
				// start retry timer
				if (ifRetry) {
					if (arg_Info.getHasRetryTime() < arg_Info.getQueryRetryTime()) {
						arg_Info.setHasRetryTime((byte)(arg_Info.getHasRetryTime()+1));
						arg_Info.setStartRetryTimer(true);
						arg_Info.initTheUsedHours();
					} else {
						arg_Info.setStartRetryTimer(false);
						arg_Info.setHasRetryTime((byte)0);
						arg_Info.setActivateSuccess(false);
						if (HM_License.getInstance().isVirtualMachineSystem()) {
							HmBeActivationUtil.ACTIVATION_KEY_VALID = false;
						}
					}
				}
				errorMes = MgrUtil.getUserMessage("error.licenseActivationKeyFailed.nework.disconnect");
			}
		}
		return errorMes;
	}
	
	/**
	 * Send collection information of hivemanager and ap to license server.
	 */
	public static void sendCollectionInfo () {
		sendInfoToLicenseServer(true);
	}
	
	private static void sendInfoToLicenseServer(boolean collect) {
		if (NmsUtil.isHostedHMApplication()) {
//			if (collect) {
//				doSendCollectionInfo(HmBeLicenseUtil.getLicenseInfo(), HmDomain.HOME_DOMAIN);
//			} else {
//				doSendClientInfo(HmBeLicenseUtil.getLicenseInfo(), HmDomain.HOME_DOMAIN);
//			}
			// only production user need send the information
			if (NmsUtil.isProduction()) {
				// send all vhm information to license server
				List<?> allNames = QueryUtil.executeQuery("SELECT domainName FROM " + HmDomain.class.getSimpleName(), new SortParams("id"),
					new FilterParams("runStatus = :s1 AND domainName != :s2 AND domainName != :s3", 
						new Object[]{HmDomain.DOMAIN_DEFAULT_STATUS, HmDomain.HOME_DOMAIN, HmDomain.GLOBAL_DOMAIN}));

				for (Object obj : allNames) {
					String domName = (String)obj;
					if (collect) {
						doSendCollectionInfo(LicenseOperationTool.getOrderKeyInfoFromDatabase(domName, false), domName);
					} else {
						doSendClientInfo(LicenseOperationTool.getOrderKeyInfoFromDatabase(domName, false), domName);
					}
				}
			}
		} else {
			if (collect) {
				doSendCollectionInfo(HmBeLicenseUtil.getLicenseInfo(), null);
			} else {
				doSendClientInfo(HmBeLicenseUtil.getLicenseInfo(), null);
			}
		}
	}
	
	public static void doSendCollectionInfo(LicenseInfo lsInfo, String domName) {
		if (!BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(lsInfo.getLicenseType())) {
			/*
			 * send
			 */
			PacketApInfoData oSendata = new PacketApInfoData();
			
			/*
			 * get the flag of if use activation key
			 */
			if (lsInfo.isUseActiveCheck()) {
				oSendata.setNeedActKeyFlag(true);
				
				// set activation key
				oSendata.setActKey(HmBeActivationUtil.getActivationKey());
			} else {
				oSendata.setNeedActKeyFlag(!(null == lsInfo.getOrderKey() || "".equals(lsInfo.getOrderKey())));
				
				// set order key
				oSendata.setActKey(lsInfo.getOrderKey());
			}
			// set HiveManager IP
			oSendata.setHMIP(HmBeOsUtil.getHiveManagerIPAddr());
			
			// set HiveManager external version
			BeVersionInfo version = NmsUtil.getVersionInfo();
			if (null == version) {
				DebugUtil.licenseDebugError("sendCollectionInfo : Get system external version failed.");
				return;
			} else {
				oSendata.setViewVersion(version.getMainVersion()+"r"+version.getSubVersion());
			}
			
			// set HiveManager hardware target
			oSendata.setProType("1U".equalsIgnoreCase(BeOperateHMCentOSImpl.getHmModel()) ? CommConst.Product_Type_1U_HM : 
				CommConst.Product_Type_2U_HM);
			
			// set HiveManager system id
			oSendata.setSystemId(lsInfo.getSystemId());

			// mesh ap number
			oSendata.setMeshApcount(HiveAPInfoFromeDatabase.getHiveAPNumber(HiveAp.HIVEAP_TYPE_MP, domName));
			
			// portal ap number
			oSendata.setPortApcount(HiveAPInfoFromeDatabase.getHiveAPNumber(HiveAp.HIVEAP_TYPE_PORTAL, domName));
			
			// hiveap version and count
			oSendata.setApVersionList(HiveAPInfoFromeDatabase.getHiveAPVersion(domName));
			
			// send failed
			if(!ClientSenderCenter.sendApInfo(oSendata)) {
				
				DebugUtil.licenseDebugError("sendCollectionInfo : Send hiveap and hivemanage information to license server failed. Domain name is "+domName);
			}
		}
	}
	
	/**
	 * Send mac client to license server.
	 */
	public static void sendClientMacInfo () {
		sendInfoToLicenseServer(false);
	}
	
	public static void doSendClientInfo(LicenseInfo lsInfo, String domName) {
		if (!BeLicenseModule.LICENSE_TYPE_NO_ORDERKEY.equals(lsInfo.getLicenseType())) {
			/*
			 * send
			 */
			PacketAPClientInfoData oSendata = new PacketAPClientInfoData();
			
			/*
			 * get the flag of if use activation key
			 */
			if (lsInfo.isUseActiveCheck()) {
				oSendata.setNeedActKeyFlag(true);
				
				// set activation key
				oSendata.setActKey(HmBeActivationUtil.getActivationKey());
			} else {
				oSendata.setNeedActKeyFlag(!(null == lsInfo.getOrderKey() || "".equals(lsInfo.getOrderKey())));
				
				// set order key
				oSendata.setActKey(lsInfo.getOrderKey());
			}
			
			// set HiveManager system id
			oSendata.setSystemId(lsInfo.getSystemId());
			
			// client mac and count
			oSendata.setApClientList(HiveAPInfoFromeDatabase.getActiveClientsMacInfo(domName));
			
			// send failed
			if(!ClientSenderCenter.SendApClientInfo(oSendata)) {
				
				DebugUtil.licenseDebugError("sendClientMacInfo : Send active client mac information to license server failed. Domain name is "+domName);
			}
		}
	}
	
	/**
	 * Send hivemanager current version to license server and get the flag of exist new version.
	 *
	 * @param lsInfo -
	 */
	public static void getNewVersionFlag (LicenseServerSetting lsInfo) {
		/*
		 * send data
		 */
		PacketNewVersionFlagQueryData oSendata = new PacketNewVersionFlagQueryData();
		
		/*
		 * get the flag of if use activation key
		 */
		LicenseInfo orderInfo = HmBeLicenseUtil.getLicenseInfo();
		if (null == orderInfo) {
			oSendata.setNeedActKeyFlag(false);
			oSendata.setNeedOrderkey(false);
		} else {
		if (orderInfo.isUseActiveCheck()) {
			oSendata.setNeedActKeyFlag(true);
			
			// set activation key
			oSendata.setActKey(HmBeActivationUtil.getActivationKey());
		} else {
			oSendata.setNeedActKeyFlag(false);
			
			oSendata.setNeedOrderkey(!(null == orderInfo.getOrderKey() || "".equals(orderInfo.getOrderKey())));
			
			// set order key
			oSendata.setOrderkey(orderInfo.getOrderKey());
		}
		}
		// set uid
		oSendata.setUid(NmsUtil.getVersionInfo().getImageUid());
		
		// set HiveManager internal version
		String version = NmsUtil.getInnerVersion();
		if (null == version) {
			DebugUtil.licenseDebugError("sendCollectionInfo : Get system inner version failed.");
			return;
		} else {
			oSendata.setInnerVersion(version);
		}
		
		// set HiveManager hardware target
		oSendata.setProType("1U".equalsIgnoreCase(BeOperateHMCentOSImpl.getHmModel()) ? CommConst.Product_Type_1U_HM : 
			CommConst.Product_Type_2U_HM);
		
		// set HiveManager system id
		oSendata.setSystemId(null == orderInfo ? HM_License.getInstance().get_system_id() : orderInfo.getSystemId());
		
		//HM type
		oSendata.setHmType(NmsUtil.isHostedHMApplication() ? CommConst.PRODUCT_TYPE_HM_ONLINE : CommConst.PRODUCT_TYPE_HIVEMANAGER);
		
		oSendata.setUpdateLimited(CommConst.HM_Update_Limit);
		
		PacketNewVersionFlagResponseData oRecvData = new PacketNewVersionFlagResponseData();
		
		// send successfully
		if(ClientSenderCenter.sendNewVersionFlagQuery(oSendata, oRecvData)) {			
			lsInfo.setAvailableSoftToUpdate(CommConst.ACT_RES_NEWVERSION_TRUE == oRecvData.getNewVersionFlag());
			
		// send failed
		} else {
			DebugUtil.licenseDebugError("getNewVersionFlag : Get new version flag from license server failed.");
		}			
	}

}