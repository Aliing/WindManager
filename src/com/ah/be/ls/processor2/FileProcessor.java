package com.ah.be.ls.processor2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.license.LicenseInfo;
import com.ah.be.ls.data2.ErrorResponse;
import com.ah.be.ls.data2.FileTxObject;
import com.ah.be.ls.data2.Header;
import com.ah.be.ls.data2.TxObject;
import com.ah.be.ls.util.CommConst;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.NameValuePair;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;
import com.ah.util.http.HttpFileTransferUtil;

public class FileProcessor {

	private static final Tracer log = new Tracer(FileProcessor.class.getSimpleName());

	private FileTxObject requestObj;

	public FileTxObject getRequestObj() {
		return requestObj;
	}

	public void setRequestObj(FileTxObject requestObj) {
		this.requestObj = requestObj;
	}

	private TxObject responseObj;

	private int fileType;

	public void run() throws Exception {
		// generate file
		String filename = "/tmp/abcd_" + fileType + ".bin";
		File file = new File(filename);
		OutputStream out = new FileOutputStream(file);
		requestObj.write(out);

		String systemId = "";
		LicenseInfo licenseInfo = HmBeLicenseUtil.getLicenseInfo();
		if (licenseInfo != null && null != licenseInfo.getSystemId()) {
			systemId = licenseInfo.getSystemId();
		}

		NameValuePair nv0 = new NameValuePair("fileType", String.valueOf(fileType));
		NameValuePair nv1 = new NameValuePair("systemId", systemId);

		HttpCommunication hc = new HttpCommunication(getUploadURL());
		initHttpProxySetting(hc);
		initHostAuthentication(hc);

		byte[] recvBytesx = hc.sendFileToLS(new NameValuePair[] { nv0, nv1 }, "bin", file);
		if (null != recvBytesx && recvBytesx.length > 0) {
			Header recvHeader = new Header(); 
			byte[] recvBytes = PacketUtil.split(ByteBuffer.wrap(recvBytesx), recvHeader);
			parsePacket(recvHeader.getType(), recvBytes);
		}
		log.info("FileProcessor,delete file: " + filename);
		file.delete();
	}

	private void parsePacket(byte packetType, byte[] input) {
		ByteBuffer buf = ByteBuffer.wrap(input);
		switch (packetType) {
		case CommConst.PacketType_Error_Response:
			responseObj = new ErrorResponse();
			break;
		default:
			break;
		}
		if (responseObj != null) {
			responseObj.unpack(buf);
		}
	}

	public TxObject getResponseTxObject() {
		return responseObj;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public int getFileType() {
		return fileType;
	}

	private static String getUploadURL() {
		LicenseServerSetting lsStr = HmBeActivationUtil.getLicenseServerInfo();
		return "https://" + lsStr.getLserverUrl() + "/uploadServer";
	}

	private static void initHostAuthentication(HttpCommunication hc) {
		hc.setTargetNeedAuth(true);
		hc.setTargetUsername(HttpFileTransferUtil.LS_HTTPS_FILE_UPLOAD_USER);
		hc.setTargetPassword(HttpFileTransferUtil.LS_HTTPS_FILE_UPLOAD_PASSWORD);
	}

	private static void initHttpProxySetting(HttpCommunication hc) {
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
	}

}