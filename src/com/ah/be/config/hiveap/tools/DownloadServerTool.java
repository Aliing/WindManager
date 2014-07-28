package com.ah.be.config.hiveap.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.os.FileManager;
import com.ah.util.NameValuePair;
import com.ah.util.http.HttpCommunication;

public class DownloadServerTool implements Serializable {

	private static final long serialVersionUID = 1L;

	private static DownloadServerTool instance;

	private DownloadServerTool() {
	}

	public static DownloadServerTool getInstance() {
		if (instance == null) {
			instance = new DownloadServerTool();
		}
		return instance;
	}

	public void simulateUploadCfg(String macAddress, String domainName,
			UpdateObject upObj) {
		new Thread(new ConfigSimulator(macAddress, domainName, upObj)).start();
	}

	public static void main(String... args) {
		String macAddress = "123456789012";
		String domainName = "zjie";
		UpdateObject upObj = new UpdateObject();
		String[] clis = new String[] { "save config \"https://localhost:8443/ds\"" };
		upObj.setClis(clis);
		upObj.setUpdateType(UpdateParameters.AH_DOWNLOAD_DS_CONFIG);

		DownloadServerTool.getInstance().simulateUploadCfg(macAddress,
				domainName, upObj);
	}

	public static class ConfigSimulator implements Runnable {

		private UpdateObject upObj;

		private String macAddress;

		private String domainName;

		public ConfigSimulator(String macAddress, String domainName,
				UpdateObject upObj) {
			this.macAddress = macAddress;
			this.upObj = upObj;
			this.domainName = domainName;
		}

		@Override
		public void run() {
			try {
				if (upObj == null) {
					return;
				}
				List<String> dsUrls = getUrl(upObj);
				for (String url : dsUrls) {
					String contents = sendDSRequest(url);
					if (contents == null) {
						return;
					}
					String filePath = AhDirTools
							.getSimulateConfigDir(domainName);
					String fileName = getFileName();
					filePath += fileName;
					FileManager.getInstance().createFile(contents, filePath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String sendDSRequest(String url) {
			try {
				HttpCommunication httpCommunication = new HttpCommunication(url);
				HttpEntity responseEntity = httpCommunication
						.sendRequestByGet(new ArrayList<NameValuePair>());
				String dsRest = EntityUtils.toString(responseEntity);
				String errorMsg = checkResult(dsRest);
				if(errorMsg == null){
					sendResultEvent(true, null);
				}else{
					sendResultEvent(false, errorMsg);
				}
				return dsRest;
			} catch (Exception e) {
				sendResultEvent(false, e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		private List<String> getUrl(UpdateObject upObj) {
			String[] clis = upObj.getClis();
			List<String> urlList = new ArrayList<String>();
			for (String rs : clis) {
				int index1 = rs.indexOf("\"");
				int index2 = rs.indexOf("\"", index1 + 1);
				if(index1 < 0 || index2 < 0){
					continue;
				}
				urlList.add(rs.substring(index1 + 1, index2));
			}
			return urlList;
		}

		private String getFileName() {
			if (upObj == null) {
				return null;
			}
			String typeStr;
			if (upObj.getUpdateType() == UpdateParameters.AH_DOWNLOAD_DS_CONFIG) {
				typeStr = "full_config";
			} else if (upObj.getUpdateType() == UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG) {
				typeStr = "user_config";
			} else {
				typeStr = "audit_config";
			}
			return this.macAddress + "_" + typeStr + ".ds";
		}

		private void sendResultEvent(boolean success, String errorMsg) {
			BeCapwapCliResultEvent event = new BeCapwapCliResultEvent();
			event.setApMac(this.macAddress);
			event.setSequenceNum(upObj.getSequenceNum());
			if (success) {
				event.setCliResult(BeCommunicationConstant.CLIRESULT_SUCCESS);
			} else {
				event.setCliResult(BeCommunicationConstant.CLIRESULT_FAIL);
				event.setResultMsg(errorMsg);
			}
			HmBeConfigUtil.getUpdateManager().dealCliFinishEvent(event);
		}
		
		private String checkResult(String contents){
			if(contents == null){
				return "Upload result is null.";
			}
			contents = contents.trim();
			if (upObj.getUpdateType() == UpdateParameters.AH_DOWNLOAD_DS_CONFIG) {
				return contents.contains("config version") ? null : "Upload config failed.";
			} else if (upObj.getUpdateType() == UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG) {
				if(contents.contains("user") || contents.isEmpty()){
					return null;
				}else{
					return "Upload user config failed.";
				}
			} else {
				if(contents.startsWith("0")){
					return null;
				}else{
					return contents.substring(2, contents.indexOf("\n"));
				}
			}
		}

	}
}
