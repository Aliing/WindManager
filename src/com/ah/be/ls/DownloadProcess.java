package com.ah.be.ls;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.image.ImageManager;
import com.ah.be.hiveap.ImageInfo;
import com.ah.be.license.HM_License;
import com.ah.be.license.LicenseInfo;
import com.ah.be.ls.action.Packet;
import com.ah.be.ls.data.PacketDownloadRequest;
import com.ah.be.ls.data.PacketDownloadResponse;
import com.ah.be.ls.data.PacketHeadData;
import com.ah.be.ls.util.ChecksumTool;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;
import com.ah.bo.admin.DownloadImageInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.FileTool;
import com.ah.util.Tracer;

public class DownloadProcess extends Thread {
	
	private static final Tracer	log	= new Tracer("DownloadProcess");

	private static void initRequestValue(PacketDownloadRequest req) {
		String orderKey = null;	
		LicenseInfo lsInfo = HmBeLicenseUtil.getLicenseInfo();
		if (null == lsInfo) {
			// set system id
			req.setSystemId(HM_License.getInstance().get_system_id());
		} else {
			// set system id
			req.setSystemId(lsInfo.getSystemId());
			
			// set order key
			if (lsInfo.isUseActiveCheck()) {
				orderKey = HmBeActivationUtil.getActivationKey();
			} else {
				orderKey = lsInfo.getOrderKey();
			}
		}

		req.setValidateCode(orderKey);
		req.setRandomCode(CommTool.getRandInt());
		String ip = HmBeOsUtil.getHiveManagerIPAddr();
		log.info("ip=" + ip);
		req.setStrIp(ip);
	}

	public static void downloadFile(DownloadImageInfo image, boolean isNewDownload)
			throws Exception {
		short productType = image.getProductType();
		String imageDir;
		if (productType == DownloadImageInfo.PRODUCT_TYPE_HIVEMANAGER
				|| productType == DownloadImageInfo.PRODUCT_TYPE_HM_ONLINE) {
			imageDir = AhDirTools.getHiveManagerImageDir();
		} else if (productType == DownloadImageInfo.PRODUCT_TYPE_HIVEOS) {
			imageDir = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
		} else {
			throw new Exception("Must specify the image product type");
		}

		String imageFileFullName;
		String imageInfoFullName;
		imageFileFullName = imageDir + "/" + image.getImageName();
		imageInfoFullName = imageDir + "/" + image.getImageName() + ".download";

		try {
			PacketDownloadRequest req = new PacketDownloadRequest();

			initRequestValue(req);

			req.setDataType(CommConst.Data_Type_File_Download_Request);

			req.setHardwareTarget(image.getHardwareTarget());
			req.setReqDownloadVersion(image.getImageVersion());
			req.setProductType((byte) image.getProductType());
			req.setCurrentUid(0);
			req.setReqImageUid(image.getImageUid());

			image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_DOWNLOADING);
			FileTool.save(image, imageInfoFullName);

			if (isNewDownload) {
				req.setReqDownloadBeginPos(0);
				req.setReqDownloadSize((int) image.getImageSize());
			} else {
				// calculate file break point
				int brokeFileLen = FileTool.getFileLength(imageFileFullName);
				if (brokeFileLen == image.getImageSize()) {
					log.warning("downloadFile ", "the file [" + image.getImageName()
							+ "] has been download completely");
					image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_FINISHED);

					if (ChecksumTool.checksum(imageFileFullName, image.getImageChecksum())) {
						FileTool.removeFile(imageInfoFullName);
					} else {
						image.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NOTPASS);
						FileTool.save(image, imageInfoFullName);
					}
					return;
				}

				req.setReqDownloadBeginPos(brokeFileLen);
				req.setReqDownloadSize((int) image.getImageSize() - req.getReqDownloadBeginPos());
			}

			// receive file content and write into local file

			sendRequestToLS(req, imageFileFullName, image.getImageChecksum());

			image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_FINISHED);
			image.setDownloadEndTime(new Date());

			// check file checksum
			if (ChecksumTool.checksum(imageFileFullName, image.getImageChecksum())) {
				FileTool.removeFile(imageInfoFullName);
			} else {
				// comment on 2009.7.7
				image.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NOTPASS);
				log.error("image checksum not pass!");

				FileTool.removeFile(imageInfoFullName);
				FileTool.removeFile(imageFileFullName);
				throw new Exception("Image checksum not pass");
			}

			// insert into DB:download_image_info when download hiveap image file
			if (image.getProductType() == DownloadImageInfo.PRODUCT_TYPE_HIVEOS) {
				generateApImageInfo(image, imageDir);
			}
		} catch (SocketTimeoutException ex) {
			image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_CONNECT_ERROR);
			FileTool.save(image, imageInfoFullName);
			log.error("downloadFile ", "download SocketTimeoutException: " + ex.getMessage());
		} catch (NoRouteToHostException ex) {
			image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_CONNECT_ERROR);
			FileTool.save(image, imageInfoFullName);
			log.error("downloadFile ", "download NoRouteToHostException: " + ex.getMessage());
		} catch (UnknownHostException ex) {
			image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_CONNECT_ERROR);
			FileTool.save(image, imageInfoFullName);
			log.error("downloadFile ", "download UnknownHostException: " + ex.getMessage());
		} catch (Exception ex) {
			FileTool.removeFile(imageInfoFullName);
			log.error("downloadFile ", "download exception", ex);
		}
	}

	private static void generateApImageInfo(DownloadImageInfo image, String imageDir)
			throws Exception {
		HiveApImageInfo imgInfo = new HiveApImageInfo();
		// set image name
		imgInfo.setImageName(image.getImageName());
		// set hardware target
		imgInfo.setProductName(image.getHardwareTarget());
		imgInfo.setImageUid(image.getImageUid());
		/*
		 * Set version information
		 */
		imgInfo.setImageVersion(image.getImageVersion());
//		BeVersionInfo allversion = NmsUtil.getVersionInfo();
//		String[] mainVer = allversion.getMainVersion().split("\\.");
//		imgInfo.setMajorVersion(mainVer[0]);
//		imgInfo.setMinorVersion(mainVer[1]);
//		imgInfo.setRelVersion(allversion.getSubVersion());
		imgInfo.setOwner(QueryUtil.findBoByAttribute(HmDomain.class, "domainName",
				HmDomain.HOME_DOMAIN));

		if (image.getProductType() == DownloadImageInfo.PRODUCT_TYPE_HIVEOS) {
			ImageInfo imageFileInfo = NmsUtil.getImageInfoFromFile(imageDir + image.getImageName());
			if (imageFileInfo != null) {
				imgInfo.setReleaseData(imageFileInfo.getDate());
				try {
					imgInfo.setImageSize(Long.parseLong(imageFileInfo.getSize()));
				} catch (Exception e) {
				}
				imgInfo.setSourceType(HiveApImageInfo.SOURCE_TYPE_LICENSESERVER);
			}
		}

		// remove same version ap image
		List<HiveApImageInfo> apImages = QueryUtil.executeQuery(HiveApImageInfo.class, null, new FilterParams(
				"productName=:s1 and majorVersion=:s2 and minorVersion=:s3 and relVersion=:s4",
				new Object[] { imgInfo.getProductName(),
						imgInfo.getMajorVersion(),
						imgInfo.getMinorVersion(),
						imgInfo.getRelVersion() }));
		if (!apImages.isEmpty()) {
			HiveApImageInfo info = apImages.get(0);
			try {
				QueryUtil.removeBoBase(info);
				HmBeOsUtil.deletefile(imageDir + info.getImageName());
				// remove the .hm file
				if ((new File(imageDir + info.getImageName() + ".hm")).exists()) {
					HmBeOsUtil.deletefile(imageDir + info.getImageName() + ".hm");
				}
			} catch (Exception ex) {
				log.error("checkVersionExist(remove bo) : ", ex.getMessage());
			}
		}

		ImageManager.updateHiveApImageInfo(imgInfo);
		QueryUtil.createBo(imgInfo);
	}

	public static DownloadImageInfo queryFileInfo(short productType, String imageVersion,
			String hardwareTarget, boolean isNewDownload, int currentUid, int reqUid)
			throws Exception {
		String imageDir;
		if (productType == DownloadImageInfo.PRODUCT_TYPE_HIVEMANAGER
				|| productType == DownloadImageInfo.PRODUCT_TYPE_HM_ONLINE) {
			imageDir = AhDirTools.getHiveManagerImageDir();
		} else if (productType == DownloadImageInfo.PRODUCT_TYPE_HIVEOS) {
			imageDir = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
		} else {
			throw new Exception("Must specify the image product type");
		}

		PacketDownloadRequest req = new PacketDownloadRequest();

		initRequestValue(req);

		req.setDataType(CommConst.Data_Type_File_Query_Request);
		req.setHardwareTarget(hardwareTarget);
		req.setReqDownloadVersion(imageVersion);
		req.setProductType((byte) productType);
		req.setCurrentUid(currentUid);
		req.setReqImageUid(reqUid);

		PacketDownloadResponse resp = sendRequestToLS(req, null, null);

		DownloadImageInfo image = new DownloadImageInfo();
		image.setProductType(productType);
		image.setHardwareTarget(hardwareTarget);
		image.setImageVersion(resp.getImageVersion());
		// device can download the latest image
		if (!imageVersion.equals(resp.getImageVersion()) && productType == DownloadImageInfo.PRODUCT_TYPE_HIVEOS) {
			if (!QueryUtil.executeQuery("select id from "+HiveApImageInfo.class.getSimpleName(), null, new FilterParams("imageName", resp.getFileName())).isEmpty()) {
				throw new Exception("The image file ("+resp.getFileName()+") exists in this HM.");
			}
		}
		image.setImageUid(resp.getImageUid());

		if (!isNewDownload) {
			if (!image.getImageName().equalsIgnoreCase(resp.getFileName())
					|| !(image.getImageChecksum().equals(resp.getFileChecksum()))
					|| (image.getImageSize() != resp.getFileSize())) {
				FileTool.removeFile(imageDir + "/" + image.getImageName());
			}
		}

		image.setImageName(resp.getFileName());
		image.setImageChecksum(resp.getFileChecksum());
		image.setImageSize(resp.getFileSize());
		image.setDownloadStartTime(new Date());
		image.setDownloadStatus(DownloadImageInfo.DOWNLOAD_STATUS_DOWNLOADING);

		return image;
	}

	public static PacketDownloadResponse sendRequestToLS(PacketDownloadRequest req,
			String imageFileFullName, String reqChecksum) throws Exception {
		String host;
		int port;
		String queryString = "/downloadserver";

		req.buildData();

		HttpsURLConnection conn;
		InputStream is;
		OutputStream os;

		byte[] tmpBuffer = new byte[CommConst.BUFFER_SIZE];

		// packet head data
		PacketHeadData sendHeadData = new PacketHeadData();
		sendHeadData.setType(CommConst.Packet_Type_Download_Request);
		sendHeadData.setProtocolVersion(CommConst.Protocol_Version);
		sendHeadData.setLength(req.getData().length);
		sendHeadData.setSecretFlag(CommConst.Secret_Flag_Yes);

		int packetLength = Packet.buildPacket(tmpBuffer, 0, sendHeadData, req.getData());
		byte[] packetBuffer = new byte[packetLength];
		System.arraycopy(tmpBuffer, 0, packetBuffer, 0, packetLength);

		// ssl connect
		LicenseServerSetting setting = HmBeActivationUtil.getLicenseServerInfo();
		host = setting.getLserverUrl();
		port = LicenseServerSetting.DEFAULT_LICENSE_SERVER_PORT;
		URL surl = new URL("https", host, port, queryString);

		CommTool.initHttps();

		// initConn
		// get stream
		conn = (HttpsURLConnection) surl.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setConnectTimeout(CommConst.TIME_OUT);
		conn.setReadTimeout(CommConst.TIME_OUT);

		os = conn.getOutputStream();
		os.write(packetBuffer);
		os.flush();
		os.close();

		log.info("process " + "http response code=" + conn.getResponseCode());

		// receive data
		is = conn.getInputStream();

		BufferedInputStream in = new BufferedInputStream(is);

		// receive packet head
		Arrays.fill(tmpBuffer, 0, tmpBuffer.length, (byte) 0);
		if (in.read(tmpBuffer, 0, CommConst.Packet_Head_Size) < 0) {
			log.error("read packet head failed!");
			throw new Exception("Communication failed!");
		}
		if (CommConst.Error_Response_Flag == tmpBuffer[0]) {
			log.error("packet type error! [" + tmpBuffer[0] + "]");
			throw new Exception("Communication failed!");
		}

		PacketHeadData recvHeadData = Packet.parsePacketHead(tmpBuffer);
		if (recvHeadData.getType() != sendHeadData.getType() + 1) {
			log.error("No this type packet! [" + recvHeadData.getType() + "]");
			throw new Exception("Communication failed!");
		}

		// receive packet data
		int len;
		Arrays.fill(tmpBuffer, 0, tmpBuffer.length, (byte) 0);
		len = in.read(tmpBuffer, 0, recvHeadData.getLength());
		if (len < 0) {
			log.error("read packet data failed!");
			throw new Exception("Communication failed!");
		}
		byte[] dataBuffer;
		if (recvHeadData.getSecretFlag() == CommConst.Secret_Flag_Yes) {
			dataBuffer = Packet.decryptData(tmpBuffer, len);
		} else {
			dataBuffer = new byte[len];
			System.arraycopy(tmpBuffer, 0, dataBuffer, 0, len);
		}

		PacketDownloadResponse resp = new PacketDownloadResponse();
		resp.setData(dataBuffer);
		resp.parseData();

		// receive file content
		if (resp.getDataType() == CommConst.Data_Type_File_Download_Success_Response) {
			if (!reqChecksum.equalsIgnoreCase(resp.getFileChecksum())) {
				in.close();
				is.close();
				conn.disconnect();
				throw new Exception("the response checksum not match request checksum!");
			}
			writeToFile(req.getReqDownloadBeginPos(), imageFileFullName, in);
		} else if (resp.getDataType() == CommConst.Data_Type_File_Query_Success_Response) {
			// nothing
		} else if (resp.getDataType() == CommConst.Data_Type_File_Query_Deny_Response) {
			in.close();
			is.close();
			conn.disconnect();
			throw new Exception("Query file information was denied! " + resp.getReason());
		} else if (resp.getDataType() == CommConst.Data_Type_File_Download_Deny_Response) {
			in.close();
			is.close();
			conn.disconnect();
			throw new Exception("Download file was denied!" + resp.getReason());
		}

		in.close();
		is.close();
		conn.disconnect();

		return resp;
	}

	private static void writeToFile(int downloadBeginPosition, String imageFileFullName,
			BufferedInputStream in) throws Exception {
		byte[] bb = new byte[1024];

		File fileOut = new File(imageFileFullName);
		long len3 = fileOut.length();

		int len2;
		int totalLen = 0;
		if (downloadBeginPosition == 0) {
			FileOutputStream out = new FileOutputStream(fileOut);
			log.info("new download [" + imageFileFullName + "] begin ...");
			while ((len2 = in.read(bb)) != -1) {
				out.write(bb, 0, len2);
				totalLen += len2;
			}
			log
					.info("new download [" + imageFileFullName + "] end! total download len="
							+ totalLen);
		} else {
			RandomAccessFile out2 = new RandomAccessFile(imageFileFullName, "rw");
			out2.seek(len3);

			log.info("breakpoint download [" + imageFileFullName + "] begin from " + len3 + " ...");
			while ((len2 = in.read(bb)) != -1) {
				out2.write(bb, 0, len2);
				totalLen += len2;
			}
			log.info("breakpoint download [" + imageFileFullName + "] end! total download len="
					+ totalLen);
		}
	}

}