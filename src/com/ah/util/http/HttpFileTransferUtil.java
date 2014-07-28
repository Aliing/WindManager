package com.ah.util.http;

import java.io.File;
import java.util.List;

import org.apache.http.HttpResponse;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;

public class HttpFileTransferUtil {

	public static final String	LS_HTTPS_FILE_UPLOAD_USER = "upload";
	
	public static final String	LS_HTTPS_FILE_UPLOAD_PASSWORD = "a1r2h3v4";
	
	public static final String	LS_FILE_UPLOAD_FILT_TYPE	= "FileType";
	
	public static final int		LS_FILE_UPLOAD_FILE_TYPE_DATA_COLLECTION = 1;
	
	/**
	 * upload files to ls
	 * @param fileNames -
	 * @param urlParam -
	 * @param delete -
	 * @throws Exception -
	 */
	public static void uploadFilesToLS(String[] fileNames, String urlParam, boolean delete) throws Exception{
		
		HttpCommunication hc = uploadSettings(urlParam);
		
		for (String fileName : fileNames) {
			File file = new File(fileName);
			HttpResponse response = hc.uploadFile(null, file);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new Exception("Fail to upload file "+fileName+", statuscode="+
						response.getStatusLine().getStatusCode()+",message="
						+response.getStatusLine().getReasonPhrase());
			} else if(delete) {
				file.delete();
			}
		}
	}
	
	/**
	 * upload file to ls
	 * @param fileName -
	 * @param urlParam -
	 * @throws Exception -
	 */
	public static void uploadFileToLS(String fileName,String urlParam) throws Exception{
		
		HttpCommunication hc = uploadSettings(urlParam);
		
		File file = new File(fileName);
		HttpResponse response = hc.uploadFile(null, file);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("Fail to upload file "+fileName+", statuscode="+
					response.getStatusLine().getStatusCode()+",message="
					+response.getStatusLine().getReasonPhrase());
		}
	}
	
	/**
	 * get upload settings
	 * @param urlParam -
	 * @return -
	 * @throws Exception -
	 */
	private static HttpCommunication uploadSettings(String urlParam)
			throws Exception {
		//get license server url
		LicenseServerSetting lsStr = HmBeActivationUtil.getLicenseServerInfo();
		if(lsStr == null)
			throw new Exception("No license server info");
		String url = "https://" + lsStr.getLserverUrl() + "/upload";
		if(urlParam != null)
			url = url + "?" + urlParam;
		
		HttpCommunication hc = new HttpCommunication(url);
		
		//set proxy information
		HMServicesSettings setting = null;
		List<HMServicesSettings> bos = QueryUtil.executeQuery(HMServicesSettings.class, null, null,
				BoMgmt.getDomainMgmt().getHomeDomain().getId());
		if (!bos.isEmpty()) {
			setting = bos.get(0);
		}

		boolean enableProxyFlag = false;
		if (setting != null) {
			enableProxyFlag = setting.isEnableProxy();
		}

		if (enableProxyFlag) {
			hc.setEnableProxyFlag(true);
			hc.setProxyHost(setting.getProxyServer());
			hc.setProxyPort(setting.getProxyPort());
			hc.setProxyUsername(setting.getProxyUserName());
			hc.setProxyPassword(setting.getProxyPassword());
		}
		
		//set auth information
		hc.setTargetNeedAuth(true);
		hc.setTargetUsername(LS_HTTPS_FILE_UPLOAD_USER);
		hc.setTargetPassword(LS_HTTPS_FILE_UPLOAD_PASSWORD);
		return hc;
	}
	
	/**
	 * update data collection file to LS
	 * @param fileName -
	 * @param apMac -
	 * @throws Exception -
	 */
	public static void uploadDataCollectionFileToLS(String fileName,String apMac) throws Exception {
		String urlParam = LS_FILE_UPLOAD_FILT_TYPE+"="+LS_FILE_UPLOAD_FILE_TYPE_DATA_COLLECTION;
		uploadFileToLS(fileName, urlParam);
	}

}