/**
 *@filename		HiveApFileAction.java
 *@version
 *@author		Fiona
 *@createtime	2007-10-22 PM 01:29:49
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.actions.hiveap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.hiveap.ImageInfo;
import com.ah.be.hiveap.ImageManager;
import com.ah.be.hiveap.L7SignatureMng;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.protocol.ssh.scp.AhScpMgmt;
import com.ah.be.protocol.ssh.scp.AhScpMgmtImpl;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.hiveap.HiveApAutoProvision;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.hiveap.LSevenSignatures;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.wlan.Cwp;
import com.ah.ui.actions.BaseAction;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.NameValuePair;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class HiveApFileAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HiveApFileAction.class
			.getSimpleName());

	public static final String	HM_HIVEAP_FILE_TYPE	= "hmHiveApFileType";

	public static final String	HM_CWP_DIRECTORY_NAME = "hmCwpDirectoryName";

	public static final String FILE_TYPE_L7_SIGNATURE = "l7signature";

	private String fileType = "image";

	private String selectType = "local";

	private File upload;

	private String uploadContentType;

	private String uploadFileName;

	private String directoryName;

	private String ipAddress;

	private String port = "22";

	private String filePath;

	private String scpUser;

	private String scpPass;

	private String[] imageFiles;

	private String cwpDir;

	private String[] cwpDirs;

	private String[] cwpFiles;

	private String[] pageResources;

	private String[] signatureFiles;

	private String domainName = "";

	//private String homeDomain = "("+HmDomain.HOME_DOMAIN+")";

	private String whereDirect = "directoryName = :s1";

	private String whereCwpDirect = "cwpName != :s1 AND directoryName = :s2";

	private String whereCwpFile = "(webPageSource = " + Cwp.WEB_PAGE_SOURCE_IMPORT + "AND directoryName = " + cwpDir +
								" AND (webPageName = :s1 OR resultPageName = :s2)) OR (webPageSource = " + Cwp.WEB_PAGE_SOURCE_AUTOGENERATE +
								"AND (headImage = :s3 OR footImage = :s4 OR backgroundImage = :s5 OR successFootImage = :s6 OR " +
								"successHeadImage = :s7 OR successBackgroundImage = :s8))";

	private String whereCwpCus = "webPageSource = " + Cwp.WEB_PAGE_SOURCE_AUTOGENERATE + "AND (headImage = :s1 OR footImage = :s2 OR " +
								"backgroundImage = :s3 OR successFootImage = :s4 OR successHeadImage = :s5 OR successBackgroundImage = :s6)";

	public static final int CWP_RESOURCE_MAX_SIZE = 1024; //unit: KByte

	private static final String DEFAULT_CWP_KEY_FILE_PATH =  AhDirTools.getHmRoot() +
																"resources" + File.separator +
																"cwp" + File.separator;
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			// get the domain name for select
			if ("".equals(domainName)) {
				domainName = QueryUtil.findBoById(HmDomain.class, domainId).getDomainName();
			}

			// get from different page
			if ("newImageFile".equals(operation) || "newCwpFile".equals(operation) || "newCwpCustomFile".equals(operation)
					|| "newCwpDirectory".equals(operation)||"newL7Signature".equals(operation)) {
				if ("newImageFile".equals(operation)) {
					fileType = "image";
					if (!isOEMSystem()) {
						selectType = "license";
					}
				} else if ("newCwpFile".equals(operation)) {
					fileType = "cwpFile";
					MgrUtil.setSessionAttribute(HM_CWP_DIRECTORY_NAME, cwpDir);
				} else if ("newCwpCustomFile".equals(operation)) {
					fileType = "cwpPageCustom";
				} else if ("newCwpDirectory".equals(operation)) {
					fileType = "cwpDirectory";
				} else if ("newL7Signature".equals(operation)) {
					fileType = FILE_TYPE_L7_SIGNATURE;
				}
				MgrUtil.setSessionAttribute(HM_HIVEAP_FILE_TYPE, fileType);
			} else {
				fileType = (String)MgrUtil.getSessionAttribute(HM_HIVEAP_FILE_TYPE);
				if (fileType.equals("cwpFile")) {
					cwpDir = (String)MgrUtil.getSessionAttribute(HM_CWP_DIRECTORY_NAME);
				}
				// upload file
				if ("addFiles".equals(operation)) {
					return saveFile();
				} else if ("removeFiles".equals(operation)) {
					return removeFile();

				// create directory for Captive Web Page
				} else if ("newDir".equals(operation)) {
					return newDirectory();

				// view files under the selected directory of Captive Web Page
//				} else if ("viewFile".equals(operation)) {
//					jsonObject = new JSONObject();
//					jsonObject.put("id", "cwpfiles");
//					jsonObject.put("v", getCwpFiles(cwpName));
//					return "json";
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HIVEAP_FILE);
	}

	/**
	 * Upload the file to appoint location.
	 *
	 *@return result
	 */
	private String saveFile() {
		FileOutputStream fos = null;
		FileInputStream fis = null;
		String strPath = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);

		// for log message
		String result = "Upload "+ NmsUtil.getOEMCustomer().getAccessPointOS() + " image";

		// the file is Captive Web Page
		if ("cwpFile".equals(fileType)) {
			strPath = AhDirTools.getCwpWebPageDir(domainName, cwpDir);
			result = "Upload CWP web page";
		}else if ("cwpPageCustom".equals(fileType)) {
			strPath = AhDirTools.getPageResourcesDir(domainName);
			result = "Upload CWP web page resource";
		}else if(FILE_TYPE_L7_SIGNATURE.equals(fileType)){
			strPath = AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN);
			result = "Upload L7 signature file";
		}

		/*
		 * create the direct which saved the file if it does not exist
		 */
		try {
			HmBeOsUtil.getFileAndSubdirectoryNames(strPath,
					BeOsLayerModule.ONLYFILE, false);
		} catch (Exception e) {
			HmBeOsUtil.createDirectory(strPath);
		}

		String oldName = "";
		AhScpMgmt fileTranser = null;

		try {
			/*
			 * upload the file from local host
			 */
			if ("local".equals(selectType)) {
				oldName = getUploadFileName();
				if (null != upload && !"".equals(oldName)) {

					/*
					 * check the format of the file
					 */
					// Linklater security of upload execute file
					if (upload.canExecute()) {
						addActionError(MgrUtil.getUserMessage("error.formatInvalid", oldName));
						return ERROR;
					}
					if (!checkFileFormat(oldName)) {
						return ERROR;
					}

					/*
					 * read the message to the output file
					 */
					fis = new FileInputStream(upload);
					byte[] buffer = new byte[2048];
					int len;

					/*
					 * generate the stream of file output
					 */
					fos = new FileOutputStream(strPath + oldName);

					boolean firstRun = true;
					boolean parseImageHeadError = false;
					HiveApImageInfo imgInfo = null;
					while ((len = fis.read(buffer)) > 0) {
						if ("image".equals(fileType) && oldName.endsWith(".hm")) {
							// the first read buffer
							if (firstRun) {
								String lineStr = new String (buffer, "UTF8");
								// check script and xml format
								imgInfo = getImageInfoHead(lineStr);
								if (null == imgInfo) {
									addActionError(MgrUtil.getUserMessage("error.licenseFailed.file.invalid"));
									HmBeOsUtil.deletefile(strPath + oldName);
									parseImageHeadError = true;
									break;
								}
								firstRun = false;
							}
						}
						fos.write(buffer, 0, len);
					}
					if(parseImageHeadError){
						return ERROR;
					}

					if ("image".equals(fileType)) {
						if (!generateImageFileInfo(strPath, oldName, HiveApImageInfo.SOURCE_TYPE_LOCAL)) {
							return ERROR;
						}

						// update image to download server
						saveImageDS(new File(strPath + oldName));

						// run the script in the file
						if (oldName.endsWith(".hm")) {
							if (!checkVersionExist(imgInfo, strPath, oldName)) {
								return ERROR;
							}
						}
					} else if(FILE_TYPE_L7_SIGNATURE.equals(fileType)){
						// parse signature meta info
						try {
							new L7SignatureMng().l7SaveOne(oldName);
							/*- for testing
							LSevenSignatures l = new LSevenSignatures();
							l.setFileName(oldName+(int)(Math.random()*10));
							l.setAhVersion("1.0.0."+(int)(Math.random()*10));
							l.setDateReleased("01022013");
							l.setOwner(BoMgmt.getDomainMgmt().getHomeDomain());
							l.setPackageType((short)((Math.random()*2)+1));
							l.setPlatformId((short)((Math.random()*4)+1));
							l.setVendorVersion((int)(Math.random()*10) + ".2.2.2");
							QueryUtil.createBo(l);*/
						} catch (Exception e) {
							//remove file from disk
							HmBeOsUtil.deletefile(strPath + oldName);
							throw e;
						}
					}
				} else {
					addActionError(MgrUtil
							.getUserMessage("error.fileNotExist"));
					return ERROR;
				}

			/*
			 * upload the file from remote server
			 */
			} else if ("remote".equals(selectType)) {
				File oldFile = new File(filePath);
				oldName = oldFile.getName();

				/*
				 * check the format of the file
				 */
				// Linklater security of upload execute file
				if (oldFile.canExecute()) {
					addActionError(MgrUtil.getUserMessage("error.formatInvalid", oldName));
					return ERROR;
				}
				if (!checkFileFormat(oldName)) {
					return ERROR;
				}

				// get the file by scp
				fileTranser = new AhScpMgmtImpl(ipAddress, Integer.valueOf(port), scpUser, scpPass);
				fileTranser.scpGet(filePath, strPath);

				if (!generateImageFileInfo(strPath, oldName, HiveApImageInfo.SOURCE_TYPE_SCP)) {
					return ERROR;
				}

				if ("image".equals(fileType) && oldName.endsWith(".hm")) {
					fis = new FileInputStream(strPath + oldName);
					byte[] buffer = new byte[2048];
					boolean firstRun = true;
					boolean parseImageHeadError = false;
					HiveApImageInfo imgInfo = null;
					while (fis.read(buffer) > 0 && firstRun) {
						// the first read buffer
						String lineStr = new String (buffer, "UTF8");
						// check the script and xml format
						imgInfo = getImageInfoHead(lineStr);
						if (null == imgInfo) {
							addActionError(MgrUtil.getUserMessage("error.licenseFailed.file.invalid"));
							HmBeOsUtil.deletefile(strPath + oldName);
							parseImageHeadError = true;
							break;
						}
						firstRun = false;
					}
					if(parseImageHeadError){
						return ERROR;
					}

					saveImageDS(new File(strPath + oldName));
					// run the script in the file
					if (!checkVersionExist(imgInfo, strPath, oldName)) {
						return ERROR;
					}
				}
			/*
			 * download from license server
			 */
			} else {
//				Map<String, Integer> hardwareList = getHardwareList();
//				if (null == hardwareList || downloadInfo.size() > 0) {
//					addActionMessage(MgrUtil.getUserMessage("license.server.available.software.update.hiveos"));
//					return ERROR;
//				}
//				downloadInfo = DownloadManager.downloadHiveApSoftware(hardwareList);
//				if (downloadInfo.size() == 0) {
//					addActionMessage(MgrUtil.getUserMessage("license.server.available.software.update.hiveos"));
//					return ERROR;
//				}
//				for (DownloadImageInfo image : downloadInfo) {
//					oldName += image.getImageName()+" ";
//				}
//				oldName = oldName.substring(0, oldName.length()-1);
//				while (DownloadManager.getHiveOSDownloadList().size() > 0) {
//					Thread.sleep(1000);
//				}
				AhAppContainer.getBeConfigModule().getImageSynupLS().downloadImageManual();
			}
			if("license".equals(selectType)){
				addActionMessage(MgrUtil.getUserMessage("info.ls.fileUploaded"));
			}else{
				addActionMessage(MgrUtil.getUserMessage("info.fileUploaded", oldName));
			}
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, result + " ("
					+ oldName + ")");
			return SUCCESS;
		} catch (HmException e) {
			AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_HIVEAPS,
					result + " : " + MgrUtil.getUserMessage(e));
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, result + " (" + oldName
					+ ")");
			return ERROR;
		} catch (Exception e) {
			AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
					HmSystemLog.FEATURE_HIVEAPS, result + " : "
							+ e.getMessage());
			addActionError(MgrUtil.getUserMessage("error.file.upload.fail", oldName));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, result + " ("
					+ oldName + ")");
			return ERROR;
		} finally {
			if (fileTranser != null) {
				fileTranser.close();
			}
			if(null != fos){
				IOUtils.closeQuietly(fos);
			}
			if(null != fis){
				IOUtils.closeQuietly(fis);
			}
		}
	}

	private boolean generateImageFileInfo(String strPath, String oldName, byte uploadType)
			throws FileNotFoundException {
		// validate image file format
		ImageInfo imageFileInfo = NmsUtil.getImageInfoFromFile(strPath + oldName);
		if (imageFileInfo == null) {
			HmBeOsUtil.deletefile(strPath + oldName);
			addActionError(MgrUtil.getUserMessage("error.image.formatInvalid", oldName));
			return false;
		}

		String version = imageFileInfo.getReversion();
		String[] versions = version.split("\\.");

		if (version == null || version.length() < 7 || versions.length < 4) {
			HmBeOsUtil.deletefile(strPath + oldName);
			addActionError(MgrUtil.getUserMessage(
					"error.image.versionFormatInvalid",
					new String[] { oldName,
							imageFileInfo.getReversion() }));
			return false;
		}

		String versionStr = versions[0] + "." + versions[1] + "r" + versions[2];
		if (NmsUtil.compareSoftwareVersion(NmsUtil.getHMCurrentVersion(), imageFileInfo.getReversion()) < 0) {
			HmBeOsUtil.deletefile(strPath + oldName);
			addActionError(MgrUtil.getUserMessage(
					"error.image.versionInvalid",
					new String[] { oldName, versionStr }));

			return false;
		}

		if (imageFileInfo.getTargetName() == null) {
			HmBeOsUtil.deletefile(strPath + oldName);
			addActionError(MgrUtil.getUserMessage("error.image.productNameInvalid", oldName));

			return false;
		}

		if (!addImageInfo(oldName, uploadType)) {
			HmBeOsUtil.deletefile(strPath + oldName);
			addActionError(MgrUtil.getUserMessage("error.image.addImageInfo", oldName));

			return false;
		}

		return true;
	}

	/**
	 * Check the format of the uploaded file
	 *
	 * @param fileName -
	 * @return boolean
	 */
	private boolean checkFileFormat(String fileName) {
		if (fileName.contains(" ")) {
			addActionError(MgrUtil.getUserMessage(
					"error.hiveAPFile.contain.blank", fileName));
			return false;
		}
		// fix Bug 16893
		String specialChar = "[]{}();$&|\\<>#%^` \"~'";
		if(containSpecialChar(fileName,specialChar)){
			addActionError(MgrUtil.getUserMessage(
					"error.invalidCharacters", specialChar));
			return false;
		}
		
		// Linklater security of upload execute file
		if (fileName.endsWith(".jsp")) {
			addActionError(MgrUtil.getUserMessage("error.formatInvalid", fileName));
			return false;
		}

		if ("cwpFile".equals(fileType) || "cwpPageCustom".equals(fileType)) {
			if (fileName.length() > 32) {
				addActionError(MgrUtil.getUserMessage(
							"error.hiveAPFile.name.length", new String[]{fileName, "32"}));
				return false;
			}
			if (upload.length() > CWP_RESOURCE_MAX_SIZE * 1024) {
				addActionError(MgrUtil.getUserMessage(
						"error.file.upload.fail.size", fileName));
				return false;
			}
			if ("cwpFile".equals(fileType)) {
				if (ifTheSizeMoreThanLimit(upload.length(), fileName)) {
					return false;
				}
			}
			if (fileName.endsWith(".img") || fileName.endsWith(".img.S")
					|| fileName.endsWith(".pem")) {
				addActionError(MgrUtil.getUserMessage(
						"error.formatInvalid", fileName));
				return false;
			}
		} else if ("image".equals(fileType)) {
			if ((!fileName.endsWith(".img") && !fileName.endsWith(".img.S"))
				|| fileName.length() < 5) {
				addActionError(MgrUtil.getUserMessage("error.formatInvalid", fileName));
				return false;
			}
//			if(fileName.contains("&")){
//				addActionError(MgrUtil.getUserMessage("error.invalidCharacters", new String[]{fileName, "&"}));
//			}
			if (fileName.length() > 64) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAPFile.name.length", new String[]{fileName, "64"}));
				return false;
			}
			List<TextItem> images = getAvailableImageFiles();
			for (TextItem imageName : images) {
				if (imageName.getKey().equalsIgnoreCase(fileName)) {
					addActionError(MgrUtil.getUserMessage(
							"error.hiveAPFile.upload.fail.exist", fileName));
					return false;
				}
			}
		} else if(FILE_TYPE_L7_SIGNATURE.equals(fileType)){
			if(!fileName.endsWith(".tar.gz")){
				addActionError(MgrUtil.getUserMessage("error.formatInvalid", fileName));
				return false;
			}
			if (fileName.length() > 64) {
				addActionError(MgrUtil.getUserMessage(
						"error.hiveAPFile.name.length", new String[]{fileName, "64"}));
				return false;
			}
		}
		return true;
	}

	private boolean containSpecialChar(String fileName,String specialChar){
		boolean result = false;
		if(fileName == null){
			return result;
		}
		for(int i =0;i<specialChar.length();i++){
			if(fileName.contains(String.valueOf(specialChar.charAt(i)))){
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean checkImageInfo(String imageFile) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process new_process = rt.exec("chmod u+x "+imageFile);
			if (new_process.waitFor() != 0) {
				return false;
			}
			new_process = rt.exec(imageFile);
			if (new_process.waitFor() != 0) {
				return false;
			}
			HmBeOsUtil.deletefile(imageFile.substring(0, imageFile.length()-3)+".xml");
			return true;
		} catch (Exception ex) {
			log.error("checkImageInfo : ", ex.getMessage());
			return false;
		}
	}

	private boolean addImageInfo(String imageName, byte uploadType) {

		//remove image with same name.
		try{
			QueryUtil.removeBos(HiveApImageInfo.class, new FilterParams("imageName", imageName));
		} catch (Exception ex) {
			log.error("Image "+imageName+"has been existed, remove failed.", ex);
			return false;
		}

		try {
			HiveApImageInfo imgInfo = new HiveApImageInfo();
			imgInfo.setImageName(imageName);
			imgInfo.setImageUid(0);
			imgInfo.setSourceType(uploadType);

			com.ah.be.config.image.ImageManager.updateHiveApImageInfo(imgInfo);
			QueryUtil.createBo(imgInfo);
		} catch (Exception e) {
			log.error("exception occured while add image info(GUI).", e);
			return false;
		}

		return true;
	}

	private boolean checkVersionExist(HiveApImageInfo imgInfo, String imgPath, String oldName) {
		if (!checkImageInfo(imgPath + oldName)) {
			addActionError(MgrUtil.getUserMessage("error.licenseFailed.file.invalid"));
			try {
				HmBeOsUtil.deletefile(imgPath + oldName);
			} catch (Exception ex) {
				log.error("checkVersionExist(delete file) : ", ex.getMessage());
			}
			return false;
		}
		imgInfo.setImageName(oldName.substring(0, oldName.length()-3));
		String where = "productName = :s1 AND majorVersion = :s2 AND minorVersion = :s3 AND relVersion = :s4 AND patchVersion = :s5";
		Object[] values = new Object[5];
		values[0] = imgInfo.getProductName();
		values[1] = imgInfo.getMajorVersion();
		values[2] = imgInfo.getMinorVersion();
		values[3] = imgInfo.getRelVersion();
		values[4] = imgInfo.getPatchVersion();
		// get the object with the same version from database
		List<HiveApImageInfo> allImage = QueryUtil.executeQuery(HiveApImageInfo.class, null, new FilterParams(where, values));
		if (!allImage.isEmpty()) {
			for (HiveApImageInfo info : allImage) {
				try {
					QueryUtil.removeBoBase(info);
					HmBeOsUtil.deletefile(imgPath + info.getImageName());
					// remove the .hm file
					if ((new File(imgPath + info.getImageName() + ".hm")).exists()) {
						HmBeOsUtil.deletefile(imgPath + info.getImageName() + ".hm");
					}
				} catch (Exception ex) {
					log.error("checkVersionExist(remove bo) : ", ex.getMessage());
				}
			}
		}
		// create the new object
		try {
			imgInfo.setOwner(QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN));
			QueryUtil.createBo(imgInfo);
		} catch (Exception ex) {
			log.error("checkVersionExist(create bo) : ", ex.getMessage());
		}
		return true;
	}

	/**
	 * Get the image product name and version from the header xml string
	 *
	 *@param lineStr -
	 *@return HiveApImageInfo
	 */
	private HiveApImageInfo getImageInfoHead(String lineStr) {
		try {
			if (!lineStr.startsWith("#!/bin/bash") || !lineStr.contains("<Image-Header") || !lineStr.contains("</Image-Header>")
					|| !lineStr.contains("<Firmware>") || !lineStr.contains("</Firmware>")) {
				return null;
			}
			HiveApImageInfo info = new HiveApImageInfo();
			SAXReader reader = new SAXReader();
			String docName = lineStr.substring(lineStr.indexOf("<Firmware>"), lineStr.indexOf("</Firmware>")
					+ "</Firmware>".length());
			Document doc = reader.read(new StringReader(docName));
			Element roota = doc.getRootElement();
			Iterator<?> iter = roota.elementIterator();
			Element foo;
			while(iter.hasNext()){
				foo = (Element) iter.next();
				// get product name
				if(foo.getName().equalsIgnoreCase("Product")){
					info.setProductName(foo.getStringValue());
				// get image version
				} else if(foo.getName().equalsIgnoreCase("Version")){
					iter = foo.elementIterator();
					while (iter.hasNext()) {
						foo = (Element) iter.next();
						if (foo.getName().equalsIgnoreCase("External")) {
							iter = foo.elementIterator();
							while (iter.hasNext()) {
								foo = (Element) iter.next();
								// get major version
								if(foo.getName().equalsIgnoreCase("Major")){
									info.setMajorVersion(foo.getStringValue());

								// get minor version
								} else if(foo.getName().equalsIgnoreCase("Minor")){
									info.setMinorVersion(foo.getStringValue());

								// get release version
								} else if(foo.getName().equalsIgnoreCase("Release")){
									info.setRelVersion(foo.getStringValue());

								// get patch string
								} else if(foo.getName().equalsIgnoreCase("Patch")){
									try {
										info.setImageUid(Integer.parseInt(foo.getStringValue()));
									} catch (NumberFormatException nfe) {
										info.setImageUid(0);
									}
								}
							}
						}
					}
				}
			}
			String regex = "^\\d+\\.+\\d+r\\d+\\w*$";
			// check the product name and version format
			if ("".equals(info.getProductName()) || !Pattern.matches(regex, info.getImageVersion().trim())) {
				return null;
			}
			com.ah.be.config.image.ImageManager.updateHiveApImageInfo(info);
			return info;
		} catch (Exception ex) {
			log.error("checkImageInfo : ", ex.getMessage());
			return null;
		}
	}

	/**
	 * Remove the file from appoint location.
	 *
	 * @return result
	 * @throws Exception -
	 */
	private String removeFile() throws Exception {
		String strPath = AhDirTools.getImageDir(HmDomain.HOME_DOMAIN);
		StringBuffer resultTrue = new StringBuffer();
		StringBuffer resultFalse = new StringBuffer();
		String fileResultType;
		int sucNum = 0;
		int failNum = 0;
		List<String> inUsedFileNames = new ArrayList<String>();
		if ("cwpDirectory".equals(fileType)) {
			fileResultType = MgrUtil.getUserMessage("hm.audit.log.cwp.web.page.directory");
			String checkResult = ifCwpOrKeyFileIsUsed(whereDirect, cwpDirs, 1);
			if (!"".equals(checkResult)) {
				addActionError(checkResult);
				return SUCCESS;
			}
			for (String dir : cwpDirs) {
				// error from url direct
				if (dir.startsWith("../")) {
					continue;
				}
				// remove CWP directory
				try {
					if (HmBeOsUtil.deleteDirectory(AhDirTools.getCwpWebDir(domainName) + dir)) {
						resultTrue.append("(").append(dir).append("),");
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, fileResultType
							+ " ("+dir+") "+MgrUtil.getUserMessage("hm.audit.log.was.removed"));
						sucNum++;
					} else {
						resultFalse.append("(").append(dir).append("),");
						generateAuditLog(HmAuditLog.STATUS_FAILURE, fileResultType
							+ " ("+dir+") "+MgrUtil.getUserMessage("hm.audit.log.was.removed"));
						failNum++;
					}
				} catch (Exception e) {
					AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_HIVEAPS, fileResultType +
							" : " + e.getMessage());
					resultFalse.append("(").append(dir).append("),");
					generateAuditLog(HmAuditLog.STATUS_FAILURE, fileResultType
						+ " ("+dir+") "+MgrUtil.getUserMessage("hm.audit.log.was.removed"));
					failNum++;
				}

			}
		/*
		 * remove image or cwp page file or cwp page resources
		 */
		} else {
			fileResultType = NmsUtil.getOEMCustomer().getAccessPointOS() + " image";
			String[] delFiles = imageFiles;

			if ("cwpPageCustom".equals(fileType)) {
				fileResultType = "CWP web page resource";
				strPath = AhDirTools.getPageResourcesDir(domainName);
				delFiles = pageResources;
			}

			if ("cwpFile".equals(fileType)) {
				fileResultType = "CWP web page";
				strPath = AhDirTools.getCwpWebPageDir(domainName, cwpDir);
				delFiles = cwpFiles;
			}

			if(FILE_TYPE_L7_SIGNATURE.equals(fileType)){
				fileResultType = "L7 signature file";
				strPath = AhDirTools.getL7SignatureDir(HmDomain.HOME_DOMAIN);
				delFiles = signatureFiles;
			}

			String checkResult = "";

			if ("cwpPageCustom".equals(fileType)) {
				checkResult = ifCwpOrKeyFileIsUsed(whereCwpCus, delFiles, 6);
			}

			if ("cwpFile".equals(fileType)) {
				checkResult = ifCwpOrKeyFileIsUsed(whereCwpFile, delFiles, 8);
			}

			if (!"".equals(checkResult)) {
				addActionError(checkResult);
				return SUCCESS;
			}

			for (String name : delFiles) {
				// error from url direct
				if (name.startsWith("../")) {
					continue;
				}
				try {
					// check image which is used by auto provision, for bug 16870
					if ("image".equals(fileType)) {
						List<?> objs = QueryUtil.executeQuery(HiveApAutoProvision.class, null, new FilterParams("imageName", name));
						if (objs != null
								&& !objs.isEmpty()) {
							inUsedFileNames.add(name);
							continue;
						}

						//remove image on Download server
						removeImageDS(name);
					}
					if (!HmBeOsUtil.isFileExist(strPath + name) || HmBeOsUtil.deletefile(strPath + name)) {
						// remove the database record
						if ("image".equals(fileType)) {
							QueryUtil.bulkRemoveBos(HiveApImageInfo.class, new FilterParams("imageName", name));
							// remove the .hm file
							if ((new File(strPath + name + ".hm")).exists()) {
								HmBeOsUtil.deletefile(strPath + name + ".hm");
							}
						}else if(FILE_TYPE_L7_SIGNATURE.equals(fileType)){
							QueryUtil.bulkRemoveBos(LSevenSignatures.class, new FilterParams("fileName", name));
						}
						resultTrue.append("(").append(name).append("),");
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, fileResultType
							+ " ("+name+") "+MgrUtil.getUserMessage("hm.audit.log.was.removed"));
						sucNum++;
					} else {
						resultFalse.append("(").append(name).append("),");
						generateAuditLog(HmAuditLog.STATUS_FAILURE, fileResultType
							+ " ("+name+") "+MgrUtil.getUserMessage("hm.audit.log.was.removed"));
						failNum++;
					}
				} catch (Exception e) {
					AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
							HmSystemLog.FEATURE_HIVEAPS, fileResultType +
							" : " + e.getMessage());
					resultFalse.append("(").append(name).append("),");
					generateAuditLog(HmAuditLog.STATUS_FAILURE, fileResultType
						+ " ("+name+") "+MgrUtil.getUserMessage("hm.audit.log.was.removed"));
					failNum++;
				}
			}
		}

		String result = "";
		if (!"".equals(resultTrue.toString())) {
			result = MgrUtil.getUserMessage("error.hiveAPFile.remove.message", new String[]{fileResultType+(sucNum>1?" were":" was"),
					resultTrue.toString().substring(0, resultTrue.toString().length()-1)});
		}
		if (!"".equals(resultFalse.toString())) {
			result = result + MgrUtil.getUserMessage("error.hiveAPFile.remove.message", new String[]{fileResultType+(failNum>1?" were not":" was not"),
					resultFalse.toString().substring(0, resultFalse.toString().length()-1)});
		}
		addActionMessage(result);

		if (inUsedFileNames != null
				&& !inUsedFileNames.isEmpty()) {
			String result1 = "";
			result1 += "These images can not be removed because they are still used in auto provision profile:";
			for (String nameTmp : inUsedFileNames) {
				result1 += "<br>" + nameTmp;
			}
			addActionError(result1);
		}
		return SUCCESS;
	}

	/**
	 * Check if cwp or key file is used by CWP.
	 *
	 * @param arg_Where : the select sql;
	 * @param arg_File : the file names;
	 * @param arg_Count : the param number
	 * @return String : "" is true.
	 */
	private String ifCwpOrKeyFileIsUsed(String arg_Where, Object[] arg_File, int arg_Count) {
		int i = 0;
		StringBuffer result = new StringBuffer();
		String resultString = "";
		if (null != arg_File) {
			Object[] values;
			for (Object name : arg_File) {
				String fileName = (String)name;
				values = new Object[arg_Count];
				for(int j=0; j<values.length; j++) {
					values[j] = fileName;
				}
				List<?> boIds = QueryUtil.executeQuery("select id from "
					+ (Cwp.class).getSimpleName(), null,
					new FilterParams(arg_Where, values), domainId);
				if (!boIds.isEmpty()) {
					result.append("(").append(fileName).append(") ");
					i ++;
				}
			}
		}
		if (!"".equals(result.toString())) {
			String beVerb = i > 1 ? "are" : "is";
			resultString = MgrUtil.getUserMessage("error.hiveAPFile.usedByProfiles", new String[]{result.toString()+ beVerb, "Captive Web Portals"});
		}
		return resultString;
	}

	/**
	 * Create directory for Captive Web Page.
	 *
	 *@return result
	 */
	private String newDirectory() {
		if (null != directoryName && !"".equals(directoryName)) {
			// don't allow 20 chars : 32space 34" 35# 36$ 37% 38& 39' 40( 41) 42* 47/ 59; 60< 62> 63? 92\ 94^ 96` 124| 126~
			String invalidChar = " \"#$%&'()*/;<>?\\^`|~";
			if (directoryName.startsWith("../")) {
				addActionError(MgrUtil.getUserMessage("error.formatInvalid", "Directory Name"));
				return ERROR;
			}
			for (int i = 0; i < invalidChar.length(); i++) {
				if (directoryName.indexOf(invalidChar.charAt(i)) > -1) {
					addActionError(MgrUtil.getUserMessage("error.invalidCharacters.directory.name", String.valueOf(invalidChar.charAt(i))));
					return ERROR;
				}
			}
			for (String name : getAvailableCwpDirs()) {
				if (name.equals(directoryName)) {
					addActionError(MgrUtil.getUserMessage("error.objectExists",
							directoryName));
					return ERROR;
				}
			}
			try {
				if (HmBeOsUtil.createDirectory(AhDirTools.getCwpWebDir(domainName) + directoryName)) {
					addActionMessage(MgrUtil.getUserMessage(
							"info.cwp.directory", directoryName));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS,MgrUtil.getUserMessage("hm.audit.log.create.cwp.web.page.directory")+
							" (" + directoryName + ")");
				}
			} catch (Exception e) {
				AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR,
						HmSystemLog.FEATURE_HIVEAPS, "Create CWP web page directory : "
								+ e.getMessage());
				generateAuditLog(HmAuditLog.STATUS_FAILURE, fileType+MgrUtil.getUserMessage("hm.audit.log.create.cwp.web.page.directory")
						+ " (" + directoryName + ")");
				addActionError(MgrUtil.getUserMessage("error.cwp.directory",
						directoryName));
				return ERROR;
			}
		}
		directoryName = "";
		return SUCCESS;
	}

	public List<String> getAvailableDefaultKey() {
		List<String> key = new ArrayList<String>();
		for (int i = 0; i < 16; i++) {
			key.add(String.valueOf(i) + ".pem");
		}
		return key;
	}

	/**
	 * Get all the available image files in one domain.
	 * @return List<TextItem>
	 */
	private List<TextItem> getAvailableImageFiles() {
		return ImageManager.getAllImageFiles();
	}

	public List<HiveApImageInfo> getAvailableHiveosImages(){
		SortParams sp = new SortParams("imageName");
		sp.setPrimaryOrderBy("majorVersion");
		sp.setPrimaryAscending(false);
		return QueryUtil.executeQuery(HiveApImageInfo.class, sp, null);
	}

	public List<LSevenSignatures> getAvailableL7Signatures() {
		SortParams sp = new SortParams("fileName");
		sp.setPrimaryOrderBy("ahVersion");
		sp.setPrimaryAscending(false);
		return QueryUtil.executeQuery(LSevenSignatures.class, sp, null);
	}

	/**
	 * get all page resoruces in a domain
	 *
	 * @param domainName -
	 * @return -
	 * @author Joseph Chen
	 */
	public static List<String> getAllPageResources(String domainName) {
		List<String> pageResources = null;
		try {
			pageResources = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getPageResourcesDir(domainName),
					BeOsLayerModule.ONLYFILE, false);
		} catch (Exception e) {
		}

		if (null == pageResources) {
			pageResources = new ArrayList<String>();
			pageResources.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}

		return pageResources;
	}

	/**
	 * get available page resources for CWP page customization
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public List<String> getAvailablePageResources() {
		return getAllPageResources(domainName);
	}

	/**
	 * Get all the cwp directory names in this domain
	 * @param arg_Name - domain name
	 * @return List<String>
	 */
	public static List<String> getAllCwpDirs(String arg_Name) {
		List<String> cwpDirs = null;
		try {
			cwpDirs = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpWebDir(arg_Name),
					BeOsLayerModule.ONLYDIRECTORY, false);
		} catch (Exception e) {
		}
		if (null == cwpDirs) {
			cwpDirs = new ArrayList<String>();
			cwpDirs.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return cwpDirs;
	}

	/**
	 * Get all available cwp files.
	 *
	 * @return -
	 */
	public List<String> getAvailableCwpFiles() {
		List<String> cwpFile;
		if (null == cwpDir) {
			cwpFile = new ArrayList<String>();
			cwpFile.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		} else {
			cwpFile = getCwpFiles(cwpDir);
		}
		return cwpFile;
	}

	/**
	 * Get all the directory names in one or all domains
	 * @return List<String>
	 */
	public List<String> getAvailableCwpDirs() {
		return getAllCwpDirs(domainName);
	}

	/**
	 * Get all the cwp files in this directory and this domain
	 * @param arg_Name - domain name
	 * @param str_Dir - directory name
	 * @return List<String>
	 */
	public static List<String> getAllCwpFiles(String arg_Name, String str_Dir) {
		List<String> cwpFile = null;
		try {
			cwpFile = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpWebDir(arg_Name)
							+ str_Dir, BeOsLayerModule.ONLYFILE, false);
		} catch (Exception e) {
		}
		if (null == cwpFile) {
			cwpFile = new ArrayList<String>();
			cwpFile.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return cwpFile;
	}

	/**
	 * Get all the cwp files in this directory.
	 * @param str_Dir - directory name
	 * @return List<String>
	 */
	public List<String> getCwpFiles(String str_Dir) {
		List<String> cwpFile = null;
		try {
			if (null != str_Dir) {
				cwpFile = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpWebDir(domainName)+ str_Dir, BeOsLayerModule.ONLYFILE, false);
			}
		} catch (Exception e) {
		}
		if (null == cwpFile) {
			cwpFile = new ArrayList<String>();
			cwpFile.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return cwpFile;
	}

	/**
	 * Get all the key file in this domain
	 * @param arg_Name - domainName
	 * @return List<String>
	 */
	public static List<String> getAllKeyFiles(String arg_Name) {
		List<String> keyFiles = null;
		try {
			keyFiles = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpServerKeyDir(arg_Name),
					BeOsLayerModule.ONLYFILE, false);
		} catch (Exception e) {

		}

		if (null == keyFiles) {
			keyFiles = new ArrayList<String>();
		}

		/*
		 * check and add the default key file
		 * the default key file will always be 0.pem
		 * joseph_chen, 08-11-12
		 */
		String defaultKeyFile = "0.pem";
		String sourceKeyFile = "default.pem";

		if(!keyFiles.contains(defaultKeyFile)) {
			/*
			 * copy the default file
			 */
			try {
				HmBeOsUtil.copyFile(DEFAULT_CWP_KEY_FILE_PATH + sourceKeyFile,
						AhDirTools.getCwpServerKeyDir(arg_Name) + defaultKeyFile);
			} catch (Exception excetion) {
				keyFiles.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
				return keyFiles;
			}

			keyFiles.add(defaultKeyFile);
		}

		return keyFiles;
	}

	/**
	 * Get all the available key file in one or all domains
	 * @return List<String>
	 */
	public List<String> getAvailableKeyFiles() {
		List<String> keyFiles = null;
		try {
			// in all domains
			if (getShowDomain()) {
				List<String> keyFile;
				// get all the dommain names
				List<String> names = getAllDomainNames();
				for (String name : names) {
					keyFile = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpServerKeyDir(name),
							BeOsLayerModule.ONLYFILE, false);
					if (keyFile != null) {
						for (String file : keyFile) {
							if (null == keyFiles) {
								keyFiles = new ArrayList<String>();
							}
							// generate the new file name
							keyFiles.add(file + "(" + name + ")");
						}
					}
				}
			} else {
				keyFiles = HmBeOsUtil.getFileAndSubdirectoryNames(AhDirTools.getCwpServerKeyDir(domainName),
						BeOsLayerModule.ONLYFILE, false);
			}
		} catch (Exception e) {
		}
		if (null == keyFiles) {
			keyFiles = new ArrayList<String>();
			keyFiles.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return keyFiles;
	}

	/**
	 * Get all the domain name of this HiveManager
	 * @return List<String>
	 */
	public List<String> getAllDomainNames() {
		List<String> domainNames = new ArrayList<String>();
		List<?> boNames = QueryUtil.executeQuery(
				"select domainName from " + HmDomain.class.getSimpleName(), new SortParams("id"), null);
		for (Object obj : boNames) {
			if (!HmDomain.GLOBAL_DOMAIN.equals(obj.toString())) {
				domainNames.add((String) obj);
			}
		}
		return domainNames;
	}

	/**
	 * delete CWP directory by CWP profile name
	 * @param name : the name of the given CWP profile name
	 * @return boolean : if delete successfully
	 * @author Joseph Chen
	 */
	public boolean deleteCwpDirectory(String name) {
		// error url direct
		if (name.startsWith("../")) {
			return false;
		}
		String checkResult = ifCwpOrKeyFileIsUsed(whereCwpDirect, new String[]{name}, 2);

		if (!"".equals(checkResult)) {
			return false;
		}
		String action = "Delete CWP web page directory";

		userContext = getSessionUserContext();

		// remove CWP directory
		try {
			if (HmBeOsUtil.deleteDirectory(AhDirTools.getCwpWebDir(domainName) + name)) {
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, action + " ("+ name +")");
				return true;
			} else {
				generateAuditLog(HmAuditLog.STATUS_FAILURE, action + " ("+ name +")");
				return false;
			}
		} catch (Exception exception) {
			generateAuditLog(HmAuditLog.STATUS_FAILURE, action + " ("+ name +")");
		}

		return false;
	}

	/**
	 * Check the total file size of one cwp directory.
	 * @param arg_Size : the upload file size;
	 * @param arg_Name : the upload file name
	 * @return boolean : false the size is ok, true the size is more.
	 */
	private boolean ifTheSizeMoreThanLimit(long arg_Size, String arg_Name) {
		try {
			if (null != cwpDir) {
				List<File> allFile = HmBeOsUtil.getFilesFromFolder(new File(AhDirTools.getCwpWebDir(domainName)+ cwpDir), false);
				if (null != allFile) {
					for (File dirc : allFile) {
						arg_Size += dirc.length();
					}
					if (arg_Size > CWP_RESOURCE_MAX_SIZE * 1024) {
						addActionError(MgrUtil.getUserMessage(
							"error.file.upload.fail.directory.size", arg_Name));
						return true;
					}
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	public String getHideCwp() {
		return "cwpFile".equals(fileType) ? "" : "none";
	}

	public String getHideCwpPageCustom() {
		return "cwpPageCustom".equals(fileType) ? "" : "none";
	}

	public String getHideUploadFile() {
		return "cwpDirectory".equals(fileType) ? "none" : "";
	}

	public boolean getL7SignaturePage() {
		return FILE_TYPE_L7_SIGNATURE.equals(fileType);
	}

	public boolean getImagePage() {
		return "image".equals(fileType);
	}

	public boolean getDisableLocal() {
		return !"local".equals(selectType);
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadContentType() {
		return uploadContentType;
	}

	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setImageFiles(String[] imageFiles) {
		this.imageFiles = imageFiles;
	}

	public void setPageResources(String[] pageResources) {
		this.pageResources = pageResources;
	}

	public void setSignatureFiles(String[] signatureFiles) {
		this.signatureFiles = signatureFiles;
	}

	public void setCwpDir(String cwpDir) {
		this.cwpDir = cwpDir;
	}

	public void setCwpFiles(String[] cwpFiles) {
		this.cwpFiles = cwpFiles;
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
	}

	public String getSelectType() {
		return selectType;
	}

	public void setSelectType(String selectType) {
		this.selectType = selectType;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void setScpUser(String scpUser) {
		this.scpUser = scpUser;
	}

	public void setScpPass(String scpPass) {
		this.scpPass = scpPass;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getPort() {
		return port;
	}

	public String getFilePath() {
		return filePath;
	}

	public String getScpUser() {
		return scpUser;
	}

//	protected JSONObject jsonObject = null;
//
//	public String getJSONString() {
//		return jsonObject.toString();
//	}

	public String getCwpDir() {
		return cwpDir;
	}

//	private String cwpName;
//
//	public String getViewFile() {
//		return "viewFile";
//	}
//
//	public String getCwpName()
//	{
//		return cwpName;
//	}
//
//	public void setCwpName(String cwpName)
//	{
//		this.cwpName = cwpName;
//	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getScpPass()
	{
		return scpPass;
	}

	public void setCwpDirs(String[] cwpDirs) {
		this.cwpDirs = cwpDirs;
	}

	protected JSONArray jsonArray = null;
	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	public String getCwpWebResourcePath() {
		return "/hm/domains/" + domainName + "/CwpPageResources/";
	}

	/*
	 * for image download from license server
	 */
	//private List<DownloadImageInfo> downloadInfo = DownloadManager.getHiveOSDownloadList();

	public Map<String, Integer> getHardwareList() {
		return ImageManager.getHardwaresMatchCurrentHMVersion();
	}

	public String getShowScpServer() {
		return "remote".equals(selectType) ? "" : "none";
	}

	public String getHiveAPImageVersion() {
		return NmsUtil.getOEMCustomer().getAccessPointOS() +" latest images from update server";
	}

	private void saveImageDS(File image) throws Exception{
		if(!MgrUtil.isEnableDownloadServer()){
			return;
		}

		HttpCommunication httpCommunication = new HttpCommunication(getDsUrl());
		httpCommunication.sendFileToLS(new NameValuePair[] {new NameValuePair("dsOperation", "addImage")}, "upload", image);
	}

	private void removeImageDS(String imageName) throws Exception{
		if(!MgrUtil.isEnableDownloadServer()){
			return;
		}

		HttpCommunication httpCommunication = new HttpCommunication(getDsUrl());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("dsOperation", "removeImage"));
		params.add(new NameValuePair("uploadFileName", imageName));
		httpCommunication.sendRequestByGet(params);
	}

	private String getDsUrl(){
		return "https://" + MgrUtil.getDownloadServerHost() + "/ds/imagemanage.action";
	}

}