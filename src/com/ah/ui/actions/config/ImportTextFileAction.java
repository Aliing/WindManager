/**
 *@filename		ImportTextFileAction.java
 *@version
 *@author		Wpliang
 *@createtime	2012-07-31 PM 03:39:50
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
package com.ah.ui.actions.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.os.BeOsLayerModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.OsVersion;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;

/**
 * @author Wpliang
 * @version V1.0.0.0
 */
public class ImportTextFileAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static final String OS_STR = "OS=";
	public static final String END_STR = "END";
	public static final String VERSION_STR = "Version=";
	// /HiveManager/downloads/fingerprints
	public static final String OS_VERSION_FILE_PATH = AhDirTools.getOsDetectionDir();
	public static final String OS_VERSION_FILE_NAME = "os_dhcp_fingerprints.txt";
	public static final String OS_VERSION_FILE_NAME_BAK = "os_dhcp_fingerprints_bak.txt";
	public static final String OS_VERSION_FILE_NAME_DEF = "os_dhcp_fingerprints_default.txt";
	public static final String OS_VERSION_FILE_NAME_CHG = "os_dhcp_fingerprints_changes.xml";
	public static final String OS_VERSION_FILE_NAME_TAR = "os_dhcp_fingerprints.tar.gz";
	public static final int[] KEYCODES_OS_VERSION_NAME = new int[] { 32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,
		51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,
		70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,
		89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,
		106,107,108,109,110,111,112,113,114,115,116,117,118,119,
		120,121,122,123,124,125,126 };

	private File upload;

	private String uploadContentType;

	private String uploadFileName;

	private String strListForward;

//	private static final Tracer log = new Tracer(ImportTextFileAction.class.getSimpleName());
	
	public String returnAllValue(String defaultRet, String strEx) {
		if (getLastExConfigGuide() != null) {
			return strEx;
		}
		return defaultRet;
	}

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			// get the domain
			// thisDomain = findBoById(HmDomain.class, domainId);

			if ("importFiles".equals(operation)) {
				try {
					AccessControl.checkUserAccess(getUserContext(),
							getSelectedL2FeatureKey(), CrudOperation.CREATE);
				} catch (HmException ex) {
					MgrUtil.setSessionAttribute("errorMessage",
							MgrUtil.getUserMessage(ex));
					setUpdateContext(true);
					return strListForward;
				}
				return returnAllValue(SUCCESS, "successEx");
				// return SUCCESS;
			} else if ("cancel".equals(operation)) {
				setUpdateContext(true);
				return strListForward;
			} else if ("import".equals(operation)) {
				saveFile();
				return returnAllValue(SUCCESS, "successEx");
			} else if ("importExceedLimit".equals(operation)) {
				addActionError(MgrUtil.getUserMessage("import.text.file.exceedLimit"));
				
				return returnAllValue(SUCCESS, "successEx");
			} else {
				return returnAllValue(SUCCESS, "successEx");
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
			return ERROR;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		strListForward = getLstForward();
		if ("osObject".equals(strListForward)) {
			setSelectedL2Feature(L2_FEATURE_OS_OBJECT);
		}
		if ("hiveApNew".equals(strListForward)) {
			// setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
			String listTypeFromSession = (String) MgrUtil
					.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
			if ("managedVPNGateways".equals(listTypeFromSession)) {
				setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
			} else if ("managedRouters".equals(listTypeFromSession)) {
				setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
			} else if ("managedDeviceAPs".equals(listTypeFromSession)) {
				setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
			} else if ("managedHiveAps".equals(listTypeFromSession)) {
				setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
			} else {
				setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
			}
		}

	}

	private void saveFile() {
		FileOutputStream fos;
		FileInputStream fis;

		/*
		 * create the direct which saved the file if it does not exist
		 */
		try {
			HmBeOsUtil.getFileAndSubdirectoryNames(OS_VERSION_FILE_PATH,
					BeOsLayerModule.ONLYFILE, false);
		} catch (Exception e) {
			HmBeOsUtil.createDirectory(OS_VERSION_FILE_PATH);
		}
		String oldName = getUploadFileName();
		try {
			if (null != oldName) {
				if (null != upload && !"".equals(oldName)) {
					// the file format is txt
					if (!oldName.endsWith(".txt")) {
						addActionError(MgrUtil.getUserMessage(
								"error.formatInvalid", "Text File"));
						return;
					}
					// the file cannot be empty
					if (upload.length() == 0) {
						addActionError(MgrUtil
								.getUserMessage("error.licenseFailed.file.invalid"));
						return;
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
					fos = new FileOutputStream(OS_VERSION_FILE_PATH + OS_VERSION_FILE_NAME_BAK);

					while ((len = fis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					fis.close();
				} else {
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
				}
			} else {
				addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
			}

			// check file content
			List<String> lines = NmsUtil.readFileByLines(OS_VERSION_FILE_PATH + OS_VERSION_FILE_NAME_BAK);
			if (!checkFileContent(lines)) {
				if(getActionErrors().isEmpty()) {
					addActionError(MgrUtil.getUserMessage("error.formatInvalid",
					"Text File"));
				}
				return;
			}
			copyFile(OS_VERSION_FILE_PATH + OS_VERSION_FILE_NAME_BAK, OS_VERSION_FILE_PATH + OS_VERSION_FILE_NAME);

			// update OsVesion table
			HmDomain gDomain = QueryUtil.findBoByAttribute(HmDomain.class,
					"domainName", HmDomain.GLOBAL_DOMAIN);
			List<OsVersion> osVersions = getOsVersions(lines);
			QueryUtil.removeBos(OsVersion.class, new FilterParams(
					"owner.domainName", HmDomain.GLOBAL_DOMAIN));
			for (OsVersion osVersion : osVersions) {
				OsVersion osVer = new OsVersion();
				osVer.setOption55(osVersion.getOption55());
				osVer.setOsVersion(osVersion.getOsVersion());
				osVer.setOwner(gDomain);
				QueryUtil.createBo(osVer);
			}
			
			//reflash osinfo and clients
			CacheMgmt.getInstance().initClientOsInfoCache();
//			Collection<SimpleHiveAp> apList = CacheMgmt.getInstance().getManagedApList();
//			HmBePerformUtil.syncRequestActiveClients(apList);
			
			//compress file
			String strCmd = "";
			StringBuffer strCmdBuf = new StringBuffer();
			strCmdBuf.append("tar zcvf ");
			strCmdBuf.append(OS_VERSION_FILE_PATH + OS_VERSION_FILE_NAME_TAR);
			strCmdBuf.append(" -C ");
			strCmdBuf.append(OS_VERSION_FILE_PATH);
			strCmdBuf.append(" " + OS_VERSION_FILE_NAME);
			strCmd = strCmdBuf.toString();
			boolean compressResult = BeAdminCentOSTools.exeSysCmd(strCmd);
			if(!compressResult){
				addActionError(MgrUtil.getUserMessage("error.file.upload.fail",
						uploadFileName));
				return;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("error.file.upload.fail",
					uploadFileName));
			return;
		}

		addActionMessage(MgrUtil.getUserMessage("import.text.file.success",
				getText("config.textfile.title.osObject")));
	}

	public void copyFile(String oldPath, String newPath) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		String version = NmsUtil.getOSOptionFileVersion(newPath);
		try {
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				fis = new FileInputStream(oldPath);
				fos = new FileOutputStream(newPath);
				BigDecimal b1 = new BigDecimal(version);
				BigDecimal b2 = new BigDecimal("0.1");
				float fVer = b1.add(b2).floatValue();
				String versionStr = VERSION_STR + String.valueOf(fVer) + "\r\n";
				byte[] versionByte = versionStr.getBytes();
				fos.write(versionByte);
				byte[] buffer = new byte[1444];
				while ((byteread = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, byteread);
				}
				fis.close();
				fos.close();
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		}

	}

	private boolean checkFileContent(List<String> lines) {
		boolean result = true;
		boolean isOS = false;
		List<String> osNameList = new ArrayList<String>();
		List<String> option55List = new ArrayList<String>();
		if (lines == null || lines.size() == 0) {
			return false;
		}

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line == null || "".equals(line)) {
				return false;
			}

			if (!isOS) {
				if (!line.startsWith(OS_STR)) {
					return false;
				} else {
					String osName = line.substring(line.indexOf(OS_STR)
							+ OS_STR.length());
					osNameList.add(osName);
					isOS = true;
					continue;
				}
			}

			if (END_STR.equals(line)) {
				isOS = false;
			}

			if (isOS) {
				String option55 = line;
				option55List.add(option55);
			}

			if(i == lines.size()-1 && !END_STR.equals(line) ){
				return false;
			}
		}

		for (String osName : osNameList) {
			if (osName == null || "".equals(osName)) {
				return false;
			}
			String osNameTrim = osName.trim();
			if (osNameTrim.length() > 32) {
				return false;
			}

			 if(!checkNameChar(osNameTrim)){
			 return false;
			 }
			 
			 //fix bug 19081
//			 long count = QueryUtil.findRowCount(OsVersion.class, new FilterParams("osVersion=:s1 and owner.domainName!=:s2",new Object[]{osName,HmDomain.GLOBAL_DOMAIN}));
//			 if(count > 0){
//				 addActionError(MgrUtil.getUserMessage("import.text.file.osversion.name.duplicate",
//							new String[]{osName,uploadFileName}));
//				 return false;
//			 }
		}

		for (String option55Name : option55List) {
			if (option55Name == null || "".equals(option55Name)) {
				return false;
			}
			String option55NameTrim = option55Name.trim();
			if (option55NameTrim.length() > 256) {
				return false;
			}
			String regex = "(\\d+,)*\\d+"; // like 1,15,3,6,44,46,47,31,33,249,43
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(option55NameTrim);
			if (!m.matches()) {
				return false;
			}

			String[] option55Names = option55NameTrim.split(",");
			String regex_value = "[1-9]\\d{0,1}$|^1\\d{2}$|^2[0-4]\\d{1}$|^25[0-5]"; // 1-255
			p = Pattern.compile(regex_value);
			for (String name : option55Names) {
				m = p.matcher(name);
				if (!m.matches()) {
					return false;
				}
			}

			int count = Collections.frequency(option55List, option55Name);
			if (count > 1) {
				return false;
			}
		}

		return result;
	}

	public static List<String> getOsNameList(List<String> lines) {
		List<String> osNameList = new ArrayList<String>();
		for (String line : lines) {
			if (line.startsWith(OS_STR)) {
				String osName = line.substring(line.indexOf(OS_STR)
						+ OS_STR.length());
				osNameList.add(osName.trim());
			}
		}

		// remove duplicate
		HashSet<String> h = new HashSet<String>(osNameList);
		osNameList.clear();
		osNameList.addAll(h);

		return osNameList;
	}
	
	
	public static List<OsVersion> getOsVersions(List<String> lines){
		List<OsVersion> osVersions = new ArrayList<OsVersion>();
		OsVersion osVersion = null;
		boolean isOS = false;
		String osName = "";
		for (String line : lines) {
			if (line.startsWith(OS_STR)) {
				osName = line.substring(line.indexOf(OS_STR)
						+ OS_STR.length());
				isOS = true;
				continue;
			}
			if (END_STR.equals(line)) {
				isOS = false;
			}

			if (isOS) {
				osVersion = new OsVersion();
				osVersion.setOsVersion(osName.trim());
				String option55 = line;
				osVersion.setOption55(option55.trim());
				osVersions.add(osVersion);	
			}
		}
		
		return osVersions;
	}
	

	public static boolean checkNameChar(String name) {
		boolean result = true;
		for (char c : name.toCharArray()) {
			int asciiCode = (int) c;
			if (containChar(KEYCODES_OS_VERSION_NAME, asciiCode) < 0) {
				return false;
			}
		}

		return result;
	}

	private static int containChar(int[] range, int keycode) {
		for (int i = 0; i < range.length; i++) {
			if (range[i] == keycode) {
				return i;
			}
		}
		return -1;
	}

	public String getTitleStr() {
		strListForward = getLstForward();
		if ("osObject".equals(strListForward)) {
			return getSelectedL2Feature().getDescription() + " > "
			+ getText("config.textfile.title.osObject");
		}

		return getSelectedL2Feature().getDescription() + " > "
				+ getText("config.textfile.import");
	}

	private String noticeInfo = "";

	/**
	 * The format of one line value.
	 * 
	 * @return String
	 */
	public String getNoticeInfo() {
		if ("osObject".equals(strListForward)) {
			noticeInfo = getText("config.textfile.message1.osObject");
			noticeInfo += getText("config.textfile.message2.osObject");
			noticeInfo += getText("config.textfile.message3.osObject");
		}

		return noticeInfo;
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

	@Override
	public Collection<HmBo> load(HmBo bo) {
		return null;
	}

}