/**
 *@filename		DownloadSoftwareAction.java
 *@version
 *@author		Fiona
 *@createtime	Apr 17, 2009 1:43:32 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ui.actions.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.ls.ClientSenderCenter;
import com.ah.be.ls.DownloadManager;
import com.ah.be.ls.data.PacketVersion2QueryData;
import com.ah.be.ls.data.PacketVersion2ResponseData;
import com.ah.be.ls.data.VersionInfoData;
import com.ah.be.ls.returndata.VersionInfoResponseData_2;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.DownloadImageInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class DownloadSoftwareAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(DownloadSoftwareAction.class
															.getSimpleName());

	private String				imageId				= "";

	public static boolean		downloadSuccess;

	private String				errorMes			= "";

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("download".equals(operation) || "refresh".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("v", getHmFileList());
				jsonObject.put("error", errorMes);
				jsonObject.put("flag", downloadSuccess);
				return "json";
			} else {
				if (getLicenseValidFlag()) {
					imageList = getQueryVersionList();
				} else {
					errorMes = MgrUtil.getUserMessage("error.admin.download.software.license.invalid");
				}
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_UPDATE_SOFTWARE);
	}

	/**
	 * Get image file list base on different operation
	 * 
	 * @return List<JSONObject>
	 * @throws JSONException -
	 */
	public List<JSONObject> getHmFileList() throws JSONException {
		List<JSONObject> listInfo = new ArrayList<JSONObject>();
		Collection<DownloadImageInfo> images;

		// get downloading information from map
		Map<String, DownloadImageInfo> mapValues = HmBeActivationUtil.SOFTWARE_IMAGE_LIST_FROM_LS;
		DownloadImageInfo downloadInfo;

		// download the selected image version
		if ("download".equals(operation)) {
			try {
				downloadInfo = DownloadManager.downloadHMSoftware(imageId, mapValues.get(imageId).getImageUid());
				images = getImageFileDetail(mapValues, downloadInfo);
			} catch (Exception ex) {
				log.error("Download new version failed", ex);
				errorMes = "Download new version failed : " + ex.getMessage();
				images = HmBeActivationUtil.SOFTWARE_IMAGE_LIST_FROM_LS.values();
			}
			// show the refresh page
		} else {
			// check if there is undownloaded file
			downloadInfo = DownloadManager.getHMDownloadInfo();
			images = getImageFileDetail(mapValues, downloadInfo);
		}
		if (null != images) {
			for (DownloadImageInfo item : images) {
				listInfo.add(getJsonObjectFromImageInfo(item));
			}
		}
		return listInfo;
	}

	private JSONObject getJsonObjectFromImageInfo(DownloadImageInfo arg_Image) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("fileVersion", arg_Image.getImageVersion());
		jsonObj.put("fileUid", arg_Image.getImageUid());
		jsonObj.put("fileSize", arg_Image.getFileSizeByByte());
		jsonObj.put("status", arg_Image.getStatusString());
		jsonObj.put("rate", arg_Image.getRateString());
		jsonObj.put("time", arg_Image.getTimeString());
		jsonObj.put("action", arg_Image.getActionTypeString());
		return jsonObj;
	}

	/**
	 * Get image list from session or query to license server
	 * 
	 * @return Collection<DownloadImageInfo>
	 */
	private Collection<DownloadImageInfo> getQueryVersionList() {
		Map<String, DownloadImageInfo> mapValues = HmBeActivationUtil.SOFTWARE_IMAGE_LIST_FROM_LS;
		if (!mapValues.isEmpty()) {
			boolean ifUseSession = false;
			for (DownloadImageInfo detail : mapValues.values()) {
				if (detail.getDownloadStatus() != DownloadImageInfo.DOWNLOAD_STATUS_NONE) {
					ifUseSession = true;
					break;
				}
			}
			if (ifUseSession) {
				return mapValues.values();
			}
		}
		List<DownloadImageInfo> details = new ArrayList<DownloadImageInfo>();
		mapValues.clear();
		DownloadImageInfo detail;

		/*
		 * set the send data
		 */
		PacketVersion2QueryData oSendata = new PacketVersion2QueryData();
		oSendata.setHmType(NmsUtil.isHostedHMApplication() ? CommConst.PRODUCT_TYPE_HM_ONLINE
				: CommConst.PRODUCT_TYPE_HIVEMANAGER);
		oSendata
				.setProType("1U".equalsIgnoreCase(BeOperateHMCentOSImpl.getHmModel()) ? CommConst.Product_Type_1U_HM
						: CommConst.Product_Type_2U_HM);
		oSendata.setInnerVersion(NmsUtil.getInnerVersion());
		oSendata.setUpdateLimited(CommConst.HM_Update_Limit);
		
		LicenseInfo lsInfo = HmBeLicenseUtil.getLicenseInfo();
		if (null == lsInfo) {
			// set system id
			oSendata.setSystemId(HM_License.getInstance().get_system_id());
			
			// set order key flag
			oSendata.setIsOrderkey(false);
		} else {
			// set system id
			oSendata.setSystemId(lsInfo.getSystemId());
			
			// set order key flag
			boolean useOrder = lsInfo.isUseActiveCheck();
			if (useOrder) {
				oSendata.setOrderkey(HmBeActivationUtil.getActivationKey());
			} else {
				oSendata.setOrderkey(lsInfo.getOrderKey());
				useOrder = !(null == lsInfo.getOrderKey() || "".equals(lsInfo.getOrderKey()));
			}
			oSendata.setIsOrderkey(useOrder);
		}
		
		// set uid
		oSendata.setUid(versionInfo.getImageUid());

		// mapValues.clear();
		// DownloadImageInfo detail = new DownloadImageInfo();
		// detail.setImageVersion("3.1r2");
		// detail.setImageSize(123456);
		// detail.setProductType(DownloadImageInfo.PRODUCT_TYPE_HIVEMANAGER);
		// detail.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NONE);
		// detail.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_NONE);
		// details.add(detail);
		// mapValues.put("1", detail);
		// detail = new DownloadImageInfo();
		// detail.setImageVersion("3.2r2");
		// detail.setImageSize(45678);
		// detail.setProductType(DownloadImageInfo.PRODUCT_TYPE_HIVEMANAGER);
		// detail.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NONE);
		// detail.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_NONE);
		// details.add(detail);
		// mapValues.put("2", detail);
		// detail = new DownloadImageInfo();
		// detail.setImageVersion("3.3r2");
		// detail.setImageSize(56789);
		// detail.setProductType(DownloadImageInfo.PRODUCT_TYPE_HIVEMANAGER);
		// detail.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NONE);
		// detail.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_NONE);
		// details.add(detail);
		// mapValues.put("3", detail);
		// return details;

		VersionInfoResponseData_2 oRecvData = new VersionInfoResponseData_2();

		// send query successfully
		if (ClientSenderCenter.sendVersionInfoQuery_2(oSendata, oRecvData)) {
			// get valid response
			if (oRecvData.getResponseType() == CommConst.Valid_Response) {
				PacketVersion2ResponseData oData = oRecvData.getValidResponse();
				List<VersionInfoData> oList = oData.getVersionList();

				// check if there is new version list
				if (oData.getCount() > 0) {
					if (oList == null) {
						log.info("getQueryVersionList", "new version list is null");
						errorMes = MgrUtil
								.getUserMessage("error.admin.download.software.no.version");
					} else {
						log
								.info("getQueryVersionList", "Get new version count is: "
										+ oList.size());
						for (VersionInfoData versionInfo : oList) {
							detail = new DownloadImageInfo();
							detail.setImageVersion(versionInfo.getVersion());
							detail.setImageSize(versionInfo.getFileSize());
							detail.setProductType(oSendata.getHmType());
							detail.setImageUid(versionInfo.getUid());
							detail.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NONE);
							detail.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_NONE);
							details.add(detail);
							mapValues.put(detail.getImageVersion(), detail);
						}
					}
				} else {
					errorMes = MgrUtil.getUserMessage("error.admin.download.software.no.version");
				}
			} else {
				errorMes = oRecvData.getInvalidResponse().getErrInfo();
			}
		} else {
			errorMes = MgrUtil.getUserMessage("error.admin.query.license.server.failed",
					"Get new version");
		}
		return details;
	}

	/**
	 * Get image file list when downloading
	 * 
	 * @param arg_Value : the list in session
	 * @param arg_Select : the downloading image
	 * @return Collection<DownloadImageInfo>
	 */
	private Collection<DownloadImageInfo> getImageFileDetail(
			Map<String, DownloadImageInfo> arg_Value, DownloadImageInfo arg_Select) {
		Collection<DownloadImageInfo> details = null;
		if (!arg_Value.isEmpty()) {
			details = arg_Value.values();
		}
		if (null != details && !details.isEmpty()) {
			for (String key : arg_Value.keySet()) {
				// has finished download
				if (null == arg_Select) {
					if (DownloadImageInfo.DOWNLOAD_STATUS_DOWNLOADING == arg_Value.get(key).getDownloadStatus()) {
						arg_Value.get(key).setImageChecksumResult(DownloadImageInfo.CHECKSUM_PASS);
						arg_Value.get(key).setDownloadEndTime(new Date(System.currentTimeMillis()));
						arg_Value.get(key).setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_FINISHED);
						downloadSuccess = true;
					}
				} else {
					// the item is downloading
					if (arg_Select.getImageVersion().equals(key)) {
						downloadSuccess = false;
						arg_Value.get(key).setDownloadRate(arg_Select.getDownloadRateByFile());
						arg_Value.get(key).setImageChecksumResult(DownloadImageInfo.CHECKSUM_NOTPASS);
						arg_Value.get(key).setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_DOWNLOADING);
					// other image cannot be download
					} else {
						arg_Value.get(key).setImageChecksumResult(DownloadImageInfo.CHECKSUM_NOTPASS);
					}
				}
			}
		}
		return details;
	}

	private Collection<DownloadImageInfo>	imageList	= new ArrayList<DownloadImageInfo>();

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getErrorMes() {
		return errorMes;
	}

	public void setErrorMes(String errorMes) {
		this.errorMes = errorMes;
	}

	public Collection<DownloadImageInfo> getImageList() {
		return imageList;
	}

	public void setImageList(Collection<DownloadImageInfo> imageList) {
		this.imageList = imageList;
	}
	
	public boolean getIsDownloading() {
		return null != DownloadManager.getHMDownloadInfo();
	}
	
	public boolean getLicenseValidFlag() {
		if (!NmsUtil.isHostedHMApplication()) {
			LicenseInfo licenseInfo = HmBeLicenseUtil.getLicenseInfo();
			if (!"".equals(licenseInfo.getOrderKey()) && licenseInfo.getLicenseType().equals(BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM)) {
				List<OrderHistoryInfo> historyInfo = QueryUtil.executeQuery(OrderHistoryInfo.class, new SortParams("supportEndDate", false), new FilterParams("domainName = :s1 AND (licenseType = :s2 OR licenseType = :s3) AND statusFlag = :s4", 
					new Object[]{HmDomain.HOME_DOMAIN, BeLicenseModule.LICENSE_TYPE_PERMANENT_NUM, BeLicenseModule.LICENSE_TYPE_RENEW_NUM,
					OrderHistoryInfo.ENTITLE_KEY_STATUS_NORMAL}));
				return !historyInfo.isEmpty();
			} else {
				return HmBeLicenseUtil.HIVEMANAGER_LICENSE_VALID == HmBeLicenseUtil.LICENSE_VALID;
			}
		}
		return true;
	}

}