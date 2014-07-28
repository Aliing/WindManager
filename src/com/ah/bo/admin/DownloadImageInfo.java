package com.ah.bo.admin;

import java.io.File;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ah.be.ls.DownloadManager;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class DownloadImageInfo implements Serializable {

	private static final long	serialVersionUID				= 1L;

	private String				imageVersion;

	public static final short	PRODUCT_TYPE_HIVEMANAGER		= 1;
	public static final short	PRODUCT_TYPE_HIVEOS				= 2;
	public static final short	PRODUCT_TYPE_GUESTMANAGER		= 3;
	public static final short	PRODUCT_TYPE_HM_ONLINE			= 4;

	public static EnumItem[]	PRODUCT_TYPE					= MgrUtil
																		.enumItems(
																				"enum.admin.software.product.type.",
																				new int[] { PRODUCT_TYPE_HIVEMANAGER,
			PRODUCT_TYPE_HIVEOS,
			PRODUCT_TYPE_GUESTMANAGER,
			PRODUCT_TYPE_HM_ONLINE												});

	private short				productType;

	private String				hardwareTarget;

	private String				imageName;

	private String				imagePath;

	private long				imageSize;

	private String				imageChecksum;

	public static final short	DOWNLOAD_STATUS_NONE			= 0;
	public static final short	DOWNLOAD_STATUS_DOWNLOADING		= 1;
	public static final short	DOWNLOAD_STATUS_FINISHED		= 2;
	public static final short	DOWNLOAD_STATUS_PAUSE			= 3;
	public static final short	DOWNLOAD_STATUS_CONNECT_ERROR	= 4;

	public static EnumItem[]	DOWNLOAD_STATUS					= MgrUtil
																		.enumItems(
																				"enum.admin.software.download.status.",
																				new int[] { DOWNLOAD_STATUS_NONE,
			DOWNLOAD_STATUS_DOWNLOADING,
			DOWNLOAD_STATUS_FINISHED,
			DOWNLOAD_STATUS_PAUSE,
			DOWNLOAD_STATUS_CONNECT_ERROR										});

	private short				downloadStatus					= DOWNLOAD_STATUS_NONE;

	public static final short	CHECKSUM_NONE					= 0;
	public static final short	CHECKSUM_PASS					= 1;
	public static final short	CHECKSUM_NOTPASS				= 2;

	public static EnumItem[]	CHECKSUM_RESULT					= MgrUtil
																		.enumItems(
																				"enum.admin.software.checksum.result.",
																				new int[] { CHECKSUM_NONE,
			CHECKSUM_PASS,
			CHECKSUM_NOTPASS													});

	private short				imageChecksumResult				= CHECKSUM_NONE;

	private Date				downloadStartTime;

	private Date				downloadEndTime;

	private String				annotation;
	
	private int                 imageUid;

	public int getImageUid()
	{
		return imageUid;
	}

	public void setImageUid(int imageUid)
	{
		this.imageUid = imageUid;
	}

	public String getImageVersion() {
		return imageVersion;
	}

	public void setImageVersion(String imageVersion) {
		this.imageVersion = imageVersion;
	}

	public short getProductType() {
		return productType;
	}

	public void setProductType(short productType) {
		this.productType = productType;
	}

	public String getHardwareTarget() {
		return hardwareTarget;
	}

	public void setHardwareTarget(String hardwareTarget) {
		this.hardwareTarget = hardwareTarget;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}

	public String getImageChecksum() {
		return imageChecksum;
	}

	public void setImageChecksum(String imageChecksum) {
		this.imageChecksum = imageChecksum;
	}

	public short getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(short downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public short getImageChecksumResult() {
		return imageChecksumResult;
	}

	public void setImageChecksumResult(short imageChecksumResult) {
		this.imageChecksumResult = imageChecksumResult;
	}

	public Date getDownloadStartTime() {
		return downloadStartTime;
	}

	public void setDownloadStartTime(Date downloadStartTime) {
		this.downloadStartTime = downloadStartTime;
	}

	public Date getDownloadEndTime() {
		return downloadEndTime;
	}

	public void setDownloadEndTime(Date downloadEndTime) {
		this.downloadEndTime = downloadEndTime;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getActionTypeString() {
		String str = "";
		switch (imageChecksumResult) {
		case CHECKSUM_NONE:
			str = "<a class='actionType' href='javascript:requestDownload(\"" + imageVersion
					+ "\");'>Download</a>";
			break;
		}
		return str;
	}

	public String getImageTypeString() {
		return MgrUtil.getEnumString("enum.admin.software.product.type." + productType);
	}

	public String getStatusString() {
		String color = "";
		switch (downloadStatus) {
		case DOWNLOAD_STATUS_NONE:
			color = "black";
			break;
		case DOWNLOAD_STATUS_DOWNLOADING:
		case DOWNLOAD_STATUS_FINISHED:
			color = "blue";
			break;
		}
		if ("".equals(color)) {
			return "";
		} else {
			return "<span class='currentState'><font color='" + color + "'>"
					+ MgrUtil.getEnumString("download.software.status." + downloadStatus)
					+ "</font></span>";
		}
	}

	public String getRateString() {
		if (downloadStatus == DOWNLOAD_STATUS_DOWNLOADING) {
			return "<div class='a0'><div class='a1' style='width:" + (int) (downloadRate * 100)
					+ "px'>" + (int) (downloadRate * 100) + "%</div></div>";
		} else {
			return "";
		}
	}

	public String getTimeString() {
		if (downloadStatus == DOWNLOAD_STATUS_FINISHED) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return formatter.format(downloadEndTime);
		} else {
			return "";
		}
	}

	public float getDownloadRateByFile() {
		String fileName = null;
		if (this.productType == PRODUCT_TYPE_HIVEMANAGER
				|| this.productType == PRODUCT_TYPE_HM_ONLINE) {
			fileName = DownloadManager.HM_Image_Dir + "/" + this.imageName;
		} else if (this.productType == PRODUCT_TYPE_HIVEOS) {
			fileName = DownloadManager.HiveOS_Image_Dir + "/" + this.imageName;
		}
		File file = new File(fileName);
		if (file.exists()) {
			long currentLen = file.length();
			return (float) currentLen / imageSize;
		} else {
			return 0;
		}
	}

	public String getFileSizeByByte() {
		NumberFormat nbf = NumberFormat.getInstance();
		nbf.setMinimumFractionDigits(2);
		nbf.setMaximumFractionDigits(2);
		return nbf.format(imageSize / 1024);
	}

	private float	downloadRate	= 0;

	public long getImageSize() {
		return imageSize;
	}

	public void setImageSize(long imageSize) {
		this.imageSize = imageSize;
	}

	public float getDownloadRate() {
		return downloadRate;
	}

	public void setDownloadRate(float downloadRate) {
		this.downloadRate = downloadRate;
	}

}
