package com.ah.be.ls;

import java.util.List;

import org.dom4j.Document;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.hiveap.HiveApInfoForLs;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.data.CommErrInfo;
import com.ah.be.ls.data.HttpsServerInfo;
import com.ah.be.ls.data.OrderkeyErrData;
import com.ah.be.ls.data.OrderkeyQueryData;
import com.ah.be.ls.data.PacketAPClientInfoData;
import com.ah.be.ls.data.PacketActQueryData;
import com.ah.be.ls.data.PacketApInfoData;
import com.ah.be.ls.data.PacketInvalidActResponseData;
import com.ah.be.ls.data.PacketNewVersionFlagQueryData;
import com.ah.be.ls.data.PacketNewVersionFlagResponseData;
import com.ah.be.ls.data.PacketTrapData;
import com.ah.be.ls.data.PacketValidActResponseData;
import com.ah.be.ls.data.PacketVersion2QueryData;
import com.ah.be.ls.data.PacketVersion2ResponseData;
import com.ah.be.ls.data.PacketVersionInfoQueryData;
import com.ah.be.ls.data.PacketVersionInfoResponseData;
import com.ah.be.ls.data.QueryApInfo;
import com.ah.be.ls.data.QueryLicenseInfo;
import com.ah.be.ls.data.VmVerifyInfo;
import com.ah.be.ls.data2.ApConnectStatRequest;
import com.ah.be.ls.data2.ApUsageStatRequest;
import com.ah.be.ls.data2.ErrorResponse;
import com.ah.be.ls.data2.RequestTxObjectSample;
import com.ah.be.ls.data2.ResponseTxObjectSample;
import com.ah.be.ls.processor.ActQueryProcessor;
import com.ah.be.ls.processor.ApClientInfoProcessor;
import com.ah.be.ls.processor.ApInfoProcessor;
import com.ah.be.ls.processor.NewVersionFlagQueryProcessor;
import com.ah.be.ls.processor.OrderkeyErrProcessor;
import com.ah.be.ls.processor.OrderkeyQueryProcessor;
import com.ah.be.ls.processor.TrapInformProcessor;
import com.ah.be.ls.processor.UserRegInfoProcessor;
import com.ah.be.ls.processor.VersionInfoQuery2Processor;
import com.ah.be.ls.processor.VersionInfoQueryProcessor;
import com.ah.be.ls.processor.VmVerifyProcessor;
import com.ah.be.ls.processor2.FileProcessor;
import com.ah.be.ls.processor2.TxProcessor;
import com.ah.be.ls.returndata.ActResponseData;
import com.ah.be.ls.returndata.VersionInfoResponseData;
import com.ah.be.ls.returndata.VersionInfoResponseData_2;
import com.ah.be.ls.sample.ObjectSample;
import com.ah.be.ls.sample.ResponseObjectSample;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;

public class ClientSenderCenter {

//	private static Log log = LogFactory.getLog("commonlog.ClientSenderCenter");
    private static final Tracer log = new Tracer(ClientSenderCenter.class.getSimpleName());

	public static boolean sendActQuery(PacketActQueryData oSendata, ActResponseData oRecvData) {
		ActQueryProcessor oProcessor = new ActQueryProcessor();

		oProcessor.init_send_data(oSendata);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		if (CommConst.Act_Response_Data_Type == oProcessor.get_response_type()) {
			oRecvData.setResponseType(CommConst.Valid_Response);
			oRecvData.setValidResponseData((PacketValidActResponseData) oProcessor.get_response());
		} else {
			oRecvData.setResponseType(CommConst.Invalid_Response);
			oRecvData.setInvalidResponseData((PacketInvalidActResponseData) oProcessor
					.get_response());
		}

		return true;
	}

	public static boolean sendVersionInfoQuery(PacketVersionInfoQueryData oSendata,
			VersionInfoResponseData oRecvData) {
		VersionInfoQueryProcessor oProcessor = new VersionInfoQueryProcessor();

		oProcessor.init_send_data(oSendata);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		if (CommConst.Download_Response_Data_Type == oProcessor.get_response_type()) {
			oRecvData.setResponseType(CommConst.Valid_Response);
			oRecvData.setValidResponse((PacketVersionInfoResponseData) oProcessor.get_response());
		} else {
			oRecvData.setResponseType(CommConst.Invalid_Response);
		}

		return true;
	}

	public static boolean sendVersionInfoQuery_2(PacketVersion2QueryData oSendata,
			VersionInfoResponseData_2 oRecvData) {
		VersionInfoQuery2Processor oProcessor = new VersionInfoQuery2Processor();

		oProcessor.init_send_data(oSendata);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		if (CommConst.Version_List_Response_Data_Type == oProcessor.get_response_type()) {
			oRecvData.setResponseType(CommConst.Valid_Response);
			oRecvData.setVaildResponse((PacketVersion2ResponseData) oProcessor.get_response());
		} else {
			oRecvData.setResponseType(CommConst.Invalid_Response);
			oRecvData.setInvalidResponse((CommErrInfo) oProcessor.get_response());
		}

		return true;
	}

	public static boolean sendTrap(PacketTrapData oSendData) {
		TrapInformProcessor oProcessor = new TrapInformProcessor();

		oProcessor.init_send_data(oSendData);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		return true;
	}

	public static boolean sendNewVersionFlagQuery(PacketNewVersionFlagQueryData oSendData,
			PacketNewVersionFlagResponseData oRecvData) {
		NewVersionFlagQueryProcessor oProcessor = new NewVersionFlagQueryProcessor();

		oProcessor.init_send_data(oSendData);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		PacketNewVersionFlagResponseData oTmp = (PacketNewVersionFlagResponseData) oProcessor
				.get_response();

		oRecvData.setDataType(oTmp.getDataType());
		oRecvData.setNewVersionFlag(oTmp.getNewVersionFlag());

		return true;
	}

	public static boolean sendApInfo(PacketApInfoData oSendData) {
		ApInfoProcessor oProcessor = new ApInfoProcessor();

		oProcessor.init_send_data(oSendData);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		return true;
	}

	public static boolean SendApClientInfo(PacketAPClientInfoData oSendData) {
		ApClientInfoProcessor oProcessor = new ApClientInfoProcessor();

		oProcessor.init_send_data(oSendData);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		return true;
	}

	private static boolean getUploadHttpsInfo(HttpsServerInfo oInfo) {	
		LicenseServerSetting lsStr = HmBeActivationUtil.getLicenseServerInfo();
		oInfo.setHost(lsStr.getLserverUrl());
		oInfo.setPort(LicenseServerSetting.DEFAULT_LICENSE_SERVER_PORT);
		oInfo.setQuery("/uploadserver");

		return true;
	}

	public static boolean uploadStaFile() {
		HttpsServerInfo oHttpsInfo = new HttpsServerInfo();

		if (!getUploadHttpsInfo(oHttpsInfo)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "get https info error");
			return false;
		}

		return Upload_client.uploadFile(oHttpsInfo.getHost(), oHttpsInfo.getPort(), oHttpsInfo
				.getQuery(), CommConst.Upload_Statistic_Info_Data_Type);
	}
	
	/**
	 * send orderKey to ls when orderKey invalid at hm and orderKey valid at ls
	 * 
	 * @param entitlementKey
	 * @return
	 * @author xcwang
	 */
	public static boolean sendEntitlementKey(String entitlementKey) throws Exception {
		
		OrderkeyErrData oSendData = new OrderkeyErrData();
		oSendData.setOrderKey(entitlementKey);
		
		OrderkeyErrProcessor oProcessor = new OrderkeyErrProcessor();
		oProcessor.init_send_data(oSendData);
		
		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			
			return false;
		}
		
		if (CommConst.Order_Key_Err_Response_Data_Type == oProcessor.get_response_type()) {
			return (Boolean) oProcessor.get_response();
		} else {
			throw new Exception(((CommErrInfo) oProcessor.get_response()).getErrInfo());
		}
	}
	
	/**
	 * initialize order key and query license info
	 * 
	 * @param hmType
	 * @param currentType, license type
	 * @param hmId
	 * @param currentAp, ap number
	 * @param orderKey
	 * @param currentVhm, vhm number
	 *            -
	 * @return QueryLicenseInfo
	 * @throws Exception
	 *             -
	 */
	public static QueryLicenseInfo initOrderKeyAndQueryLicenseInfoNew(int hmType, String hmId,
			String orderKey, String currentType, int currentAp, int currentVhm, int currentCvg) throws Exception {
		OrderkeyQueryData oSendData = new OrderkeyQueryData();
		oSendData.setHMType(hmType);
		oSendData.setOrderKey(orderKey);
		oSendData.setFulfillmentID(hmId);
		oSendData.setCurrentType(currentType);
		oSendData.setCurrentAp(currentAp);
		oSendData.setCurrentVhm(currentVhm);
		oSendData.setCurrentCvg(currentCvg);

		OrderkeyQueryProcessor oProcessor = new OrderkeyQueryProcessor();
		oProcessor.init_send_data(oSendData);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			
			LicenseServerSetting lsStr = HmBeActivationUtil.getLicenseServerInfo();

			throw new Exception(MgrUtil
					.getUserMessage("error.license.orderkey.from.license.server", lsStr.getLserverUrl()));
		}

		if (CommConst.Order_key_Response_Data_type == oProcessor.get_response_type()) {
			return (QueryLicenseInfo) oProcessor.get_response();
		} else {
			throw new Exception(((CommErrInfo) oProcessor.get_response()).getErrInfo());
		}
	}

	/**
	 * check VM-HM validation, only use for VM-HM
	 * 
	 * @param systemId
	 *            -
	 * @param currentOrderKey
	 *            -
	 * @return boolean
	 * @throws Exception
	 *             -
	 */
	public static boolean checkVmhmValidation(String systemId, String currentOrderKey)
			throws Exception {
		VmVerifyInfo oSendData = new VmVerifyInfo();
		oSendData.setOrderKey(currentOrderKey);
		oSendData.setSystemID(systemId);

		VmVerifyProcessor oProcessor = new VmVerifyProcessor();
		oProcessor.init_send_data(oSendData);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			
			LicenseServerSetting lsStr = HmBeActivationUtil.getLicenseServerInfo();

			throw new Exception(MgrUtil
					.getUserMessage("error.license.orderkey.from.license.server", lsStr.getLserverUrl()));
		}

		// get response
		CommErrInfo oResponse = (CommErrInfo) oProcessor.get_response();
		return oResponse.getErrCode() == VmVerifyInfo.VM_VERIFy_OK;
	}

	/**
	 * query HiveAPs info
	 * 
	 * @param hmolFlag
	 *            -
	 * @param hmId
	 *            -
	 * @return List<QueryApInfo>
	 * @throws Exception
	 *             -
	 */
	public static List<QueryApInfo> queryApInfo(boolean hmolFlag, String hmId) throws Exception {
		throw new Exception("the queryApInfo has not been code!");
	}

	/**
	 * A sample for communicating with LS
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ResponseObjectSample doSample(String desc, int number, List<String> names,
			boolean isOk, ObjectSample testo) throws Exception {
		RequestTxObjectSample requestObj = new RequestTxObjectSample();
		requestObj.setDesc(desc);
		requestObj.setNumber(number);
		requestObj.setNames(names);
		requestObj.setTesto(testo);

		TxProcessor tp = new TxProcessor();
		tp.setPacketType(CommConst.PacketType_Sample_Request);
		tp.setRequestTxObject(requestObj);

		tp.run();
		if (tp.getResponseTxObject() instanceof ErrorResponse) {
			throw new Exception(((ErrorResponse) tp.getResponseTxObject()).getMessage());
		}

		ResponseTxObjectSample responseObj = (ResponseTxObjectSample) tp.getResponseTxObject();

		ResponseObjectSample obj = new ResponseObjectSample();
		obj.setDesc(responseObj.getDesc());
		obj.setAmount(responseObj.getAmount());
		return obj;
	}

	public static void sendAPConnectStatInfo(List<HiveApInfoForLs> apConnectStatInfos)
			throws Exception {
		FileProcessor fp = new FileProcessor();
		fp.setFileType(CommConst.PacketType_ApConnectStat_UploadRequest);
		ApConnectStatRequest requestObj = new ApConnectStatRequest();
		requestObj.setInfos(apConnectStatInfos);
		fp.setRequestObj(requestObj);
		fp.run();
		if (fp.getResponseTxObject() instanceof ErrorResponse) {
			throw new Exception(((ErrorResponse) fp.getResponseTxObject()).getMessage());
		}
	}
	
	public static boolean sendUserRegisterInfo(UserRegInfoForLs userInfo)
		throws Exception {
		UserRegInfoProcessor oProcessor = new UserRegInfoProcessor();
		
		oProcessor.init_send_data(userInfo);

		if (!client.sPackProcesser(oProcessor)) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, "execte pack processor error");
			return false;
		}

		return true;
	}

	public static String testForConnectingToLS(boolean enableProxy, String proxyHost,
			int proxyPort, String proxyUsername, String proxyPassword) {
		String result = HmBeResUtil.getString("connecttest.tols.success");
		try {
			String lsHost = HmBeActivationUtil.getLicenseServerInfo().getLserverUrl();
			HttpCommunication hc = new HttpCommunication("https://" + lsHost + "/messageserver");
			if (enableProxy) {
				hc.setEnableProxyFlag(true);
				hc.setProxyHost(proxyHost);
				hc.setProxyPort(proxyPort);
				hc.setProxyUsername(proxyUsername);
				hc.setProxyPassword(proxyPassword);
			}
			if (!hc.testForConnecting()) {
				result = HmBeResUtil.getString("connecttest.tols.failed");
			}
		} catch (Exception e) {
			log.error("ClientSenderCenter","testForConnectingToLS", e);
			if (enableProxy && null != e && null != e.getMessage()) {
				if (e.getMessage().equals("no response") || e.getMessage().equals("response error")) {
					result = HmBeResUtil.getString("connecttest.tols.lsfailed");
				} else {
					result = HmBeResUtil.getString("connecttest.tols.proxyfailed", new String[] { e
							.getMessage() });
				}
			} else {
				result = HmBeResUtil.getString("connecttest.tols.failed");
			}
		}

		return result;
	}

	public static void sendApUsageStat(Document doc) throws Exception {
		FileProcessor fp = new FileProcessor();
		fp.setFileType(CommConst.PacketType_ApUsageStat_UploadRequest);
		ApUsageStatRequest requestObj = new ApUsageStatRequest();
		requestObj.setDoc(doc);
		fp.setRequestObj(requestObj);
		fp.run();
		if (fp.getResponseTxObject() instanceof ErrorResponse) {
			throw new Exception(((ErrorResponse) fp.getResponseTxObject()).getMessage());
		}
	}
}
