/**
 * @filename			DownloadManager.java
 * @version
 * @author				Jonathan
 * @since				2009.4.30
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.ls.util.ChecksumTool;
import com.ah.bo.admin.DownloadImageInfo;
import com.ah.bo.admin.HmDomain;

import com.ah.util.FileTool;
import com.ah.util.MgrUtil;

import com.ah.util.Tracer;

public class DownloadManager {
	private static final Tracer log = new Tracer(DownloadManager.class.getSimpleName());
	// private static final Logger log = Logger.getLogger("DownloadManager");

	public static final String HM_Image_Dir = AhDirTools.getHiveManagerImageDir();
	public static final String HiveOS_Image_Dir = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);

	/**
	 * Query HM download info
	 * 
	 * @return
	 */
	public static DownloadImageInfo getHMDownloadInfo() {
		String[] list = FileTool.listFilenames(HM_Image_Dir, ".*.download");

		for (String file : list) {
			DownloadImageInfo image = (DownloadImageInfo) FileTool.readObject(HM_Image_Dir + "/"
					+ file);
			image.setDownloadRate(image.getDownloadRateByFile());
			return image;
		}

		return null;
	}

	/**
	 * Query HiveOS download info list
	 * 
	 * @return
	 */
	public static List<DownloadImageInfo> getHiveOSDownloadList() {
		List<DownloadImageInfo> downloads = new ArrayList<DownloadImageInfo>();
		String[] list = FileTool.listFilenames(HiveOS_Image_Dir, ".*.download");

		for (String file : list) {
			DownloadImageInfo image = (DownloadImageInfo) FileTool.readObject(HiveOS_Image_Dir
					+ "/" + file);
			if (image == null) {
				continue;
			}
			image.setDownloadRate(image.getDownloadRateByFile());
			downloads.add(image);
		}

		return downloads;
	}

	/**
	 * Download HiveManager software image file from LS
	 * 
	 * @param version
	 * @param reqImageUid
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public static DownloadImageInfo downloadHMSoftware(String version, int reqImageUid)
			throws Exception {
		short productType = NmsUtil.isHostedHMApplication() ? DownloadImageInfo.PRODUCT_TYPE_HM_ONLINE
				: DownloadImageInfo.PRODUCT_TYPE_HIVEMANAGER;
		String hardwareTarget = BeOperateHMCentOSImpl.getHmModel();

		int currentUid = 0;
		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();

		if (versionInfo != null) {
			currentUid = versionInfo.getImageUid();
		}

		DownloadImageInfo image = DownloadProcess.queryFileInfo(productType, version,
				hardwareTarget, true, currentUid, reqImageUid);

		Thread ab = new DowloadManagerThread(image, true);
		ab.setName("downloadHMSoftware");
		ab.start();

		return image;
	}

	/**
	 * Download HiveOS software image file from LS
	 * 
	 * @param hardwareTarget
	 * @throws Exception
	 * @throws InterruptedException
	 */
	public static DownloadImageInfo downloadHiveApSoftware(String hardwareTarget) throws Exception {
		BeVersionInfo hmVersion = NmsUtil.getVersionInfo();
		// String version = NmsUtil.getHiveOSVersion(hmVersion);
		String version = hmVersion.getMainVersion() + "r" + hmVersion.getSubVersion();

		return downloadHiveApSoftware(version, hardwareTarget);
	}

	/**
	 * Multiple download HiveOS software image file from LS
	 * 
	 * @param hardwareTargetList
	 * @return
	 * @throws Exception
	 */
	public static List<DownloadImageInfo> downloadHiveApSoftware__unuse__(
			Set<String> hardwareTargetList) {
		List<DownloadImageInfo> list = new ArrayList<DownloadImageInfo>();
		BeVersionInfo hmVersion = NmsUtil.getVersionInfo();
		String version = hmVersion.getMainVersion() + "r" + hmVersion.getSubVersion();

		for (String hardwareTarget : hardwareTargetList) {
			DownloadImageInfo image;
			try {
				image = downloadHiveApSoftware(version, hardwareTarget);
				list.add(image);
			} catch (Exception e) {
				log.warn("downloadHiveApSoftware", "download HiveOS:" + hardwareTarget + " failed"
						+ e.getMessage());
			}
		}

		return list;
	}

	public static List<DownloadImageInfo> downloadHiveApSoftware(
			Map<String, Integer> hardTargetUidList) {
		List<DownloadImageInfo> list = new ArrayList<DownloadImageInfo>();
		BeVersionInfo hmVersion = NmsUtil.getVersionInfo();
		String version = hmVersion.getMainVersion() + "r" + hmVersion.getSubVersion();

		Set<String> keys = hardTargetUidList.keySet();
		for (String hardwareTarget : keys) {
			DownloadImageInfo image;
			try {
				image = downloadHiveApSoftware(version, hardwareTarget, hardTargetUidList
						.get(hardwareTarget));
				list.add(image);
			} catch (Exception e) {
				log.warn("downloadHiveApSoftware", "download HiveOS:" + hardwareTarget + " failed"
						+ e.getMessage());
			}
		}

		return list;
	}

	private static DownloadImageInfo downloadHiveApSoftware(String version, String hardwareTarget,
			int currentUid) throws Exception {
		DownloadImageInfo image = DownloadProcess
				.queryFileInfo(DownloadImageInfo.PRODUCT_TYPE_HIVEOS, version, hardwareTarget,
						true, currentUid, 0);

		Thread ab = new DowloadManagerThread(image, true);
		ab.setName("downloadHiveApSoftware");
		ab.start();

		return image;
	}

	private static DownloadImageInfo downloadHiveApSoftware(String version, String hardwareTarget)
			throws Exception {
		return downloadHiveApSoftware(version, hardwareTarget, 0);
	}

	static class DowloadManagerThread extends Thread {
		private DownloadImageInfo image;
		private boolean isNewDownload = false;
		private static final long RECONNECT_TIME = 60 * 1000;

		public DowloadManagerThread(DownloadImageInfo image, boolean isNewDownload) {
			this.image = image;
			this.isNewDownload = isNewDownload;
		}

		public void run() {
			try {
				while (true) {
					Thread proc = new DowloadProcessThread(image, isNewDownload);
					proc.setName("DownloadProcessThread");
					proc.start();

					while (true) {
						if (proc.getState() == Thread.State.TERMINATED) {
							break;
						}
						Thread.sleep(2 * 1000);
					}

					if (image.getDownloadStatus() == DownloadImageInfo.DOWNLOAD_STATUS_CONNECT_ERROR) {
						log.warn("DowloadManagerThread.run",
								"download:download connect error, wait for reconnect...");
						Thread.sleep(RECONNECT_TIME);
						isNewDownload = false;
						continue;
					} else {
						log.info("download:download end, status is "
								+ image.getDownloadStatus()
								+ "-"
								+ MgrUtil.getEnumString("enum.admin.software.download.status."
										+ image.getDownloadStatus()));
						;
						break;
					}
				}
			} catch (Exception e) {
				log.error("DowloadManagerThread.run ", "catch exception", e);
			}

		}
	}

	static class DowloadProcessThread extends Thread {
		private static final Tracer log = new Tracer("DowloadProcessThread");
		private DownloadImageInfo image = null;
		private boolean isNewDownload = false;

		public DowloadProcessThread(DownloadImageInfo image, boolean isNewDownload)
				throws Exception {
			this.image = image;
			this.isNewDownload = isNewDownload;
		}

		public void run() {
			try {
				DownloadProcess.downloadFile(image, isNewDownload);
			} catch (Exception e) {
				log.error("run ", "download exception " + e.getMessage());
			}
		}
	}

	/**
	 * remove all download status when HM upgrade
	 * 
	 * @throws Exception
	 */
	public static void removeDownloadTask() throws Exception {
		removeStatusFiles(HM_Image_Dir);
		removeStatusFiles(HiveOS_Image_Dir);
	}

	private static void removeStatusFiles(String dir) throws Exception {
		String[] list = FileTool.listFilenames(dir, ".*.download");
		for (String file : list) {
			FileTool.removeFile(dir + "/" + file);
		}
	}

	/**
	 * Run download check when HM start
	 * 
	 * @throws Exception
	 */
	public static void restartDownloadTask() throws Exception {
		check(HM_Image_Dir);
		check(HiveOS_Image_Dir);
	}

	private static void check(String dir) throws Exception {
		String[] list = FileTool.listFilenames(dir, ".*.download");

		for (String file : list) {
			DownloadImageInfo image = (DownloadImageInfo) FileTool.readObject(dir + "/" + file);
			String fileFullName = dir + "/" + image.getImageName();

			if (image.getDownloadStatus() == DownloadImageInfo.DOWNLOAD_STATUS_FINISHED
					&& image.getImageChecksumResult() == DownloadImageInfo.CHECKSUM_NONE) {
				log.info("find not checksum, process...");
				if (ChecksumTool.checksum(fileFullName, image.getImageChecksum())) {
					FileTool.removeFile(fileFullName + ".download");
				} else {
					image.setImageChecksumResult(DownloadImageInfo.CHECKSUM_NOTPASS);
				}
			} else if (image.getDownloadStatus() == DownloadImageInfo.DOWNLOAD_STATUS_DOWNLOADING
					|| image.getDownloadStatus() == DownloadImageInfo.DOWNLOAD_STATUS_CONNECT_ERROR) {
				log.info("find downloading, process...");

				DownloadImageInfo image2 = DownloadProcess.queryFileInfo(image.getProductType(),
						image.getImageVersion(), image.getHardwareTarget(), false, 0, image
								.getImageUid());

				if (!image.getImageChecksum().equalsIgnoreCase(image2.getImageChecksum())) {
					log.warn("now the checksum is not match, terminate the downloading:" + file);
					FileTool.removeFile(fileFullName + ".download");
					continue;
				}
				Thread ab = new DowloadManagerThread(image2, false);
				ab.setName("downloadprocess");
				ab.start();
			}

		}
	}
}
