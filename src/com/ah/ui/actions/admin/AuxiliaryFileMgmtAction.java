/**
 *@filename		AuxiliaryFileMgmtAction.java
 *@version
 *@author		Fiona
 *@createtime	2008-7-22 PM 02:43:57
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeFileInfo;
import com.ah.be.admin.cidClients.CidClientsUtil;
import com.ah.be.admin.cidClients.CidDeviceEBO;
import com.ah.be.admin.cidClients.CidDevicesResponseEBO;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.os.BeNoPermissionException;
import com.ah.be.os.FileManager;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.rest.ahmdm.client.ResponseFromMDMImpl;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.enrolledclient.tools.MacAddressUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.ActionContext;

/**
 * @author Fiona
 * @version V1.0.0.0
 */
public class AuxiliaryFileMgmtAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log						= new Tracer(AuxiliaryFileMgmtAction.class
																.getSimpleName());

	public static final String	HM_AUXILIARY_FILE_TYPE	= "AuxiliaryFileType";

	public static final int CID_CLIENTS_CHECKBOX = 0;
	public static final int CID_CLIENTS_MAC_ADDRESS = 1;
	public static final int CID_CLIENTS_IMEI = 2;
	
	// selected file name
	private String				selectFile;

	private List<BeFileInfo>	fileList;

	private String				inputPath;

	private String				titleName				= "";

	private String				selectFileName			= "";

	private int					fileTypeFlag;

	private String				resultStr				= "";

	private String				filePath				= "";

	private File				upload;

	private String				uploadFileName;
	
	private boolean             autoRefresh;
	
	private long rcount;
	
	private int pcount = 0;
	
	public int rct = 0;
	public int pct = 0;
	
	public long getRowCount(){
		return rcount;
	}
	@Override
	public int getPageCount(){
		return pcount;
	}
	
	public void setRowCount(int rc){
		this.rcount = (long)rc;
	}
	
	public void setPageCount(int pc){
		this.pcount = pc;
	}
	@Override
	public int getPageIndex(){
		int pageCount = getPageCount();
		if (pageIndex > pageCount || pageIndex < 1) {
			// Go to last page
			pageIndex = pageCount;
		}
		return pageIndex < 1 ? 1 : pageIndex;
	
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}
	
	public boolean getEnablePageAutoRefreshSetting(){
		return autoRefresh;
	}

	public String execute() throws Exception {
		String forward = globalForward();

		if (forward != null) {
			return forward;
		}

		try {
			// mib file and radius, macoui dictionary all appear here
			if (L2_FEATURE_MIB_FILE.equals(operation)
					|| L2_FEATURE_RADIUS_DICTIONARY.equals(operation)
					|| L2_FEATURE_MACOUI_DICTIONARY.equals(operation)
					|| L2_FEATURE_CID_CLIENTS.equals(operation)) {
				MgrUtil.setSessionAttribute(HM_AUXILIARY_FILE_TYPE, operation);
			}

			titleName = (String) MgrUtil.getSessionAttribute(HM_AUXILIARY_FILE_TYPE);
			setSelectedL2Feature(titleName);
			
			// reset the read write permission
			resetPermission();
			
			AccessControl.checkUserAccess(userContext, getSelectedL2FeatureKey(), CrudOperation.READ);

			// mib file type, file path and dowload result string
			if (L2_FEATURE_MIB_FILE.equals(titleName)) {
				fileTypeFlag = HmBeAdminUtil.AH_HM_MIB_FILE_TYPE;
			//	filePath = BeAdminCentOSTools.AH_NMS_MIB_ROOT;
				filePath = AhDirTools.getMibDir();
				resultStr = MgrUtil.getUserMessage("hm.audit.log.download.mib.file") + selectFile;

				// radius dictionary file type, file path and dowload result
				// string
			} else if (L2_FEATURE_RADIUS_DICTIONARY.equals(titleName)) {
				fileTypeFlag = HmBeAdminUtil.AH_HM_RADIUS_DICTIONARY_TYPE;
			//	filePath = BeAdminCentOSTools.AH_NMS_RADIUS_DICT_ROOT;
				filePath = AhDirTools.getRadiusDictionaryDir();
				selectFile = "dictionary";
				selectFileName = "dictionary";
				resultStr = MgrUtil.getUserMessage("hm.audit.log.download.radius.dictionary") + selectFile;

				// mac oui dictionary file type, file path and dowload result
				// string
			} else if (L2_FEATURE_MACOUI_DICTIONARY.equals(titleName)) {
				fileTypeFlag = HmBeAdminUtil.AH_HM_MACOUI_DICTIONARY_TYPE;
			//	filePath = BeAdminCentOSTools.AH_NMS_MACOUI_DICT_ROOT;
				filePath = AhDirTools.getMacOuiDictionaryDir();
				selectFile = "ouiDictionary.txt";
				selectFileName = "ouiDictionary.txt";
				resultStr = MgrUtil.getUserMessage("hm.audit.log.download.macoui.dictionary") + selectFile;
			}else if(L2_FEATURE_CID_CLIENTS.equals(titleName)){
				autoRefresh = false;
				fileTypeFlag = HmBeAdminUtil.AH_HM_CID_CLIENT_FILE_TYPE;
				//	filePath = BeAdminCentOSTools.AH_NMS_MACOUI_DICT_ROOT;
				filePath = AhDirTools.getCidClientsDir();
				selectFile = "cidClients.txt";
				selectFileName = "cidClients.txt";
				resultStr = MgrUtil.getUserMessage("hm.audit.log.download.cidClients.file") + selectFile;
			}

			// get the mib files or radius dictionaries
			prepareFileList();

			if ("download".equals(operation)) {
				if (!checkFileExistUnderDirectoryAllowed(selectFile, fileTypeFlag)) {
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, resultStr);
					return titleName;
				}
				inputPath = filePath + selectFile;
				File file = new File(inputPath);

				// check file exist
				if (!file.exists()) {

					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, resultStr);
					return titleName;
				}

				generateAuditLog(HmAuditLog.STATUS_SUCCESS, resultStr);
				return "download";
			} else if ("showDetails".equals(operation)) {
				return "detail";
			} else if ("upload".equals(operation)) {
				return uploadMacOrOuiDict();
			} else if ("initCaptureResult".equals(operation)) {
				fileTypeFlag = HmBeAdminUtil.AH_HM_CAPTURE_RESULT_FILETYPE;

				try {
					prepareFileList();
				} catch (Exception e) {
					// directory maybe not exist
					log.error("execute", "initCaptureResult catch exception",e);
				}

				jsonArray = new JSONArray();
				if (fileList != null)
				{
					for (BeFileInfo file : fileList) {
						jsonObject = new JSONObject();
						jsonObject.put("fileName", file.getFileName());
						jsonObject.put("fileSize", file.getFileSize());
						jsonObject.put("createTime", file.getCreateTime());
						jsonArray.put(jsonObject);
					}
				}

				return "json";
			} else if ("checkDownloadCapture".equals(operation)) {
				if (selectFile == null || selectFile.trim().length() == 0) {
					jsonObject = new JSONObject();
					jsonObject.put("message", MgrUtil.getUserMessage("at.least.select.a.item.message"));
					return "json";
				}

			//	inputPath = BeAdminCentOSTools.AH_NMS_CAPTURERESULT_DIR + File.separator + selectFile;
				inputPath = AhDirTools.getDumpDir() + selectFile;
				File file = new File(inputPath);

				// check file exist
				if (!file.exists()) {
					jsonObject = new JSONObject();
					jsonObject.put("message",
							MgrUtil.getUserMessage("select.file.not.exist.message"));
					return "json";
				}

				jsonObject = new JSONObject();
				return "json";
			} else if ("downloadCapture".equals(operation)) {
			//	inputPath = BeAdminCentOSTools.AH_NMS_CAPTURERESULT_DIR + File.separator + selectFile;
				if (checkFileExistUnderDirectoryAllowed(selectFile, HmBeAdminUtil.AH_HM_CAPTURE_RESULT_FILETYPE)) {
					inputPath = AhDirTools.getDumpDir() + selectFile;

					return "download";
				}
				return titleName;
			} else if ("deleteCapture".equals(operation)) {
				if (selectFile == null || selectFile.trim().length() == 0) {
					jsonObject = new JSONObject();
					jsonObject.put("message", MgrUtil.getUserMessage("at.least.select.a.item.message"));
					return "json";
				}

			//	inputPath = BeAdminCentOSTools.AH_NMS_CAPTURERESULT_DIR + File.separator + selectFile;
				if (!checkFileExistUnderDirectoryAllowed(selectFile, HmBeAdminUtil.AH_HM_CAPTURE_RESULT_FILETYPE)) {
					jsonObject = new JSONObject();
					jsonObject.put("message", MgrUtil.getUserMessage("select.file.not.exist.message"));
					return "json";
				}
				inputPath = AhDirTools.getDumpDir() + selectFile;
				File file = new File(inputPath);

				// check file exist
				if (!file.exists()) {
					jsonObject = new JSONObject();
					jsonObject.put("message",
							MgrUtil.getUserMessage("select.file.not.exist.message"));
					return "json";
				}

				boolean isSuccess = HmBeOsUtil.deletefile(inputPath);
				if (!isSuccess) {
					jsonObject = new JSONObject();
					jsonObject.put("message", MgrUtil.getUserMessage("delete.item.failure.message"));

					return "json";
				} else {
					fileTypeFlag = HmBeAdminUtil.AH_HM_CAPTURE_RESULT_FILETYPE;
					prepareFileList();

					jsonArray = new JSONArray();
					if (fileList != null)
					{
						for (BeFileInfo fileInfo : fileList) {
							jsonObject = new JSONObject();
							jsonObject.put("fileName", fileInfo.getFileName());
							jsonObject.put("fileSize", fileInfo.getFileSize());
							jsonObject.put("createTime", fileInfo.getCreateTime());
							jsonArray.put(jsonObject);
						}
					}

					return "json";
				}
			} else if("cidClients".equals(operation)){
				setInitSelectedColumns();
				preparePage();
				return "cidClients";
			}else if ("cidClientsUpload".equals(operation)){
				// check file name
				if (upload.canExecute() || !uploadFileName.endsWith(".txt")) {
					addActionError(MgrUtil.getUserMessage("error.file.upload.fail.format", uploadFileName));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Upload CID client file(" + uploadFileName + ")");
					return "cidClients";
				}
				List<CidDeviceEBO> cids = new ArrayList<CidDeviceEBO>();
				//boolean isSuccess = uploadCidClients(cids);
				boolean isSuccess = uploadMacVerify(cids);
				if (true == isSuccess){
					boolean isUpdate = addCidClients(cids);
					if(true == isUpdate){
						addActionMessage(MgrUtil.getUserMessage("info.fileUploaded", uploadFileName));
					}else{
						addActionError(MgrUtil.getUserMessage("error.file.upload.fail",uploadFileName));
					}
				}else{
					addActionError(MgrUtil.getUserMessage("error.file.upload.fail.format",uploadFileName));
				}
				setInitSelectedColumns();
				preparePage();
				return "cidClients";
			}else if("cidClientsDownload".equals(operation)){
				if (!checkFileExistUnderDirectoryAllowed(selectFile, HmBeAdminUtil.AH_HM_CID_CLIENT_FILE_TYPE)) {
					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, "Download CID client file");
					return "cidClients";
				}
				inputPath = filePath + selectFile;
				genCidClientsFile(inputPath);
				File file = new File(inputPath);

				// check file exist
				if (!file.exists()) {

					// commonly, logic should not come here
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, resultStr);
					return "cidClients";
				}

				generateAuditLog(HmAuditLog.STATUS_SUCCESS, resultStr);
				return "download";
			}else if("pollCidClientsList".equals(operation)){
				try{
					super.removeSessionAttributes();
					enableSorting();
					enablePaging();
					jsonArray = new JSONArray(getUpdates());
					return "json";
				}catch(Exception e){
					return "json";
				}
			}else if("refreshFromCache".equals(operation)){
				try{
					setInitSelectedColumns();
					setSelectedL2Feature(L2_FEATURE_CID_CLIENTS);
					enableSorting();
					enablePaging();
					Integer tn = (Integer)MgrUtil
					    .getSessionAttribute(boClass.getSimpleName() + "TotalNumber");
			        Integer tp = (Integer)MgrUtil
					    .getSessionAttribute(boClass.getSimpleName() + "TotalPages");
					
					@SuppressWarnings("unchecked")
					List<CidDeviceEBO> tem = (List<CidDeviceEBO>)MgrUtil
							.getSessionAttribute(boClass.getSimpleName() + "Page");
					if((tem != null) && (tn != null) && (tp != null)){
						setPageCount(tp);
						setRowCount(tn);
						page = tem;
					}
				    return "cidClients";
				}catch(Exception e){
					return "cidClients";
				}
			}else if(OPERATION_SORT.equals(operation)){
				updateSortParams();
				setInitSelectedColumns();
				preparePage();
				return "cidClients";
			}else {
				baseCustomizationOperationJson();
				if (jsonObject != null && jsonObject.length() > 0) {
					return "json";
				}
				if(true == pagingOperation()){
					setInitSelectedColumns();
					if(pageIndex == -1){
						enablePaging();
						CidDevicesResponseEBO cids = prepareCidClientsList(String.valueOf(paging.getPageSize()),"0");
						pageIndex = Integer.parseInt(cids.getTotalPages());
					}
					if("gotoPage".equals(operation)){
						int pc = (Integer)MgrUtil.getSessionAttribute(boClass.getSimpleName() + "TotalPages");
						pageIndex = pageIndex > pc ? pc : pageIndex;
					}
					preparePage();
					return "cidClients";
				}
				return titleName;
			}
		} catch (Exception e) {
			addActionError(e.getMessage());
			return titleName;
		}
	}

	/**
	 * Get the file information
	 */
	public void prepareFileList() {
		fileList = HmBeAdminUtil.getFileInfoList(fileTypeFlag);
	}

	// struts download support -- begin
	public String getLocalFileName() {
		return selectFile;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(inputPath);
	}

	// struts download support -- end

	public String getSelectFile() {
		return selectFile;
	}

	public void setSelectFile(String selectFile) {
		this.selectFile = selectFile;
	}

	public List<BeFileInfo> getFileList() {
		return fileList;
	}

	public String getTitleName() {
		return titleName;
	}

	/**
	 * Show all the file detail in another jsp.
	 * 
	 * @return String
	 * @throws IllegalArgumentException -
	 * @throws IOException -
	 * @throws BeNoPermissionException -
	 */
	public String getFileDetail() throws IllegalArgumentException, IOException,
			BeNoPermissionException {
		// only can show the file under this directory
		if (checkFileExistUnderDirectoryAllowed(selectFileName, fileTypeFlag)) {
			String[] detail = HmBeOsUtil.readFile(filePath + selectFileName);
			StringBuffer strBuf = new StringBuffer("");

			if (null != detail) {
				for (String line : detail) {
					strBuf.append(line).append("\n");
				}
			}
			return strBuf.toString();
		}
		return "";
	}
	
	/*
	 * Check the select file if exist under the directory which allowed access
	 */
	private boolean checkFileExistUnderDirectoryAllowed(String fileName, int fileType) {
		List<BeFileInfo> fileNames = HmBeAdminUtil.getFileInfoList(fileType);
		if (null != fileNames && !fileNames.isEmpty()) {
			boolean fileExist = false;
			for (BeFileInfo fileInfo : fileNames) {
				if (fileName.equals(fileInfo.getFileName())) {
					fileExist = true;
					break;
				}
			}
			return fileExist;
		}
		return false;
	}

	public String getSelectFileName() {
		return selectFileName;
	}

	public void setSelectFileName(String selectFileName) {
		this.selectFileName = selectFileName;
	}
	
	public String getWriteDisabled4Upload(){
		if (!getUserContext().getDomain().isHomeDomain()) {
			return "disabled";
		}

		return getWriteDisabled();
	}

	/**
	 * Upload the mac our dictionary file to appoint location.
	 * 
	 * @return result
	 */
	private String uploadMacOrOuiDict() {
		// for log message
		String result = MgrUtil.getUserMessage("hm.audit.log.upload.macoui.dictionary");

		try {
			// check file name
			if (upload.canExecute() || !uploadFileName.endsWith(".txt")) {
				addActionError(MgrUtil.getUserMessage("error.file.upload.fail.format", uploadFileName));
				generateAuditLog(HmAuditLog.STATUS_FAILURE, result + "(" + uploadFileName + ")");
				return titleName;
			}
			FileOutputStream fos = new FileOutputStream(BeAdminCentOSTools.AH_NMS_MACOUI_DICT_FILE);
			FileInputStream fis = new FileInputStream(upload);

			/*
			 * read the message to the output file
			 */
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			fis.close();

			/*
			 * update the mac oui dictionary values in memary
			 */
			FileManager fileMg = FileManager.getInstance();

			try {
				AhConstantUtil.macOuiDict.clear();
				// get all the message from the mac oui dictionary
				String[] allLines = fileMg.readFile(upload.getPath());

				for (String line : allLines) {
					line = line.trim();
					// the mac oui line contains this string
					if (line.contains("(hex)")) {
						// get the mac oui
						String key = (line.substring(0, 8)).replace("-", "");
						// get the company name
						String value = line.substring(line.indexOf("(hex)") + 5).trim();
						AhConstantUtil.macOuiDict.put(key, value);
					}
				}
			} catch (Exception ex) {
				addActionError(ex.getMessage());
			}

			addActionMessage(MgrUtil.getUserMessage("info.fileUploaded", uploadFileName));
			generateAuditLog(HmAuditLog.STATUS_SUCCESS, result + "(" + uploadFileName + ")");
			
		} catch (Exception e) {
			AhAppContainer.HmBe.setSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_HIVEAPS,
					result + " : " + e.getMessage());
			addActionError(MgrUtil.getUserMessage("error.file.upload.fail", uploadFileName));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, result + "(" + uploadFileName + ")");
		}
		return titleName;
	}
	
	protected void setInitSelectedColumns() {
		List<HmTableColumn> tableColumn = new ArrayList<HmTableColumn>();
        tableColumn.add(new HmTableColumn(CID_CLIENTS_CHECKBOX));
        tableColumn.add(new HmTableColumn(CID_CLIENTS_MAC_ADDRESS));
        tableColumn.add(new HmTableColumn(CID_CLIENTS_IMEI));
		selectedColumns = tableColumn;
	}
	
	@Override
	public void enableSorting(){
		String sessionKey = boClass.getSimpleName() + "Sorting";
		sortParams = (SortParams) MgrUtil.getSessionAttribute(sessionKey);
		if (sortParams == null) {
			sortParams = new SortParams("macAddress");

			MgrUtil.setSessionAttribute(sessionKey, sortParams);
		}
		// So every sort tag doesn't need to specify a session key
		ActionContext.getContext().put(PAGE_SORTING, sortParams);
	}
	@Override
	public void prepare() throws Exception{
		super.prepare();
		setDataSource(CidDeviceEBO.class);
		setSelectedL2Feature(L2_FEATURE_CID_CLIENTS);
		//keyColumnId = ENROLLED_CLIENT_STATUS;
		this.tableId = HmTableColumn.TABLE_ADMIN_AUXILIARY_CID_CLIENTS;
	} 
	
	public void preparePage() throws Exception {
		super.removeSessionAttributes();
		setSelectedL2Feature(L2_FEATURE_CID_CLIENTS);
		enablePaging();
		CidDevicesResponseEBO cids = prepareCidClientsList(String.valueOf(paging.getPageSize())
				, String.valueOf(pageIndex-1));
		if(null != cids){
			formatMac(cids.getCidList());
			page = cids.getCidList();
			this.pcount = Integer.parseInt(cids.getTotalPages());
			this.rcount = Integer.parseInt(cids.getTotalNumber());
			MgrUtil
			.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer((int)rcount));
			MgrUtil
			.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer((int)pcount));
			MgrUtil
			.setSessionAttribute(boClass.getSimpleName() + "Page",page);
		}else{
			page = new ArrayList<CidDeviceEBO>();
			MgrUtil
			.setSessionAttribute(boClass.getSimpleName() + "Page",page);
			MgrUtil
			.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(0));
			MgrUtil
			.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(0));
		}
	}
	
//	private boolean uploadCidClients(List<CidDeviceEBO> cids ){
//		String macPt = "([0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2})|" +
//				"([0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2})|" +
//				"([0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2})";
//        String imeiPt = "[0-9]{15}";
//		try{
//			FileManager fileMg = FileManager.getInstance();
//				String[] allLines = fileMg.readFile(upload.getPath());
//				for (String line : allLines) {
//					line = line.replaceAll(" ", "");
//						if (line.contains(",")) {
//							String[] str = line.split(",");
//							if(str.length == 1){
//								String t1 = str[0];
//								if((t1.length() == 12) || (t1.length() == 17)){
//									Pattern p = Pattern.compile(macPt);
//									Matcher m = p.matcher(t1);
//									if(m.find()){
//										cids.add(new CidDeviceEBO(m.group(),""));
//									}else {
//										return false;
//									}
//								}else{
//									return false;
//								}
//							}else if (str.length == 2){
//								String tem = str[0];
//								if((tem.length() == 12) || (tem.length() == 17) || ("".equals(tem))){
//									String macTemp = "";
//									Pattern p ;
//									Matcher m ;
//									if("".equals(tem)){
//										macTemp="";
//									}else{
//										p = Pattern.compile(macPt);
//										m = p.matcher(tem);
//										if(m.find()){
//											 macTemp = MacAddressUtil.removeDelimiter(m.group());
//										}else{
//											return false;
//									    }
//									}
//									String imeiTemp = str[1];
//									if(imeiTemp.length() == 15){
//										p = Pattern.compile(imeiPt);
//										m = p.matcher(imeiTemp);
//										if(m.find()){
//											String temp = m.group();
//										    cids.add(new CidDeviceEBO(macTemp,temp));
//										}else{
//											return false;
//										}
//									}else{
//										return false;
//									}
//								}else{
//									return false;
//								}
//							}else{
//								return false;
//							}
//					    }else{
//						    return false;
//					    }
//				}
//			return true;
//		}catch(Exception e){
//			return false;
//		}
//	}
	
	private boolean uploadMacVerify(List<CidDeviceEBO> cids){
		String macPt17 = "([0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2}:[0-9A-Fa-f]{2})|" +
				"([0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2})";
		String macPt12 = "([0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2}[0-9A-Fa-f]{2})|" ;
		try{
			FileManager fileMg = FileManager.getInstance();
			String[] allLines = fileMg.readFile(upload.getPath());
			for(String line : allLines){
				line = line.replaceAll(" ", "");
				if((line.length() == 12)){
					Pattern p = Pattern.compile(macPt12);
					Matcher m = p.matcher(line);
					if(m.find()){
						cids.add(new CidDeviceEBO(m.group(),""));
					}else{
						return false;
					}
				}else if((line.length() == 17)){
					Pattern p = Pattern.compile(macPt17);
					Matcher m = p.matcher(line);
					if(m.find()){
						cids.add(new CidDeviceEBO(m.group(),""));
					}else{
						return false;
					}
				}else{
					return false;
				}
			}
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	private CidDevicesResponseEBO prepareCidClientsList(String pageSize,String pageIndex) throws Exception{
		try{
			String direction = "";
			enableSorting();
			if(sortParams.isAscending()){
				direction = CidClientsUtil.CID_CLIENTS_ASC;
			}else{
				direction = CidClientsUtil.CID_CLIENTS_DESC;
			}
			String cidReq = CidClientsUtil.transObjectToXML(getDomain().getInstanceId()
					, pageSize,pageIndex,direction,sortParams.getOrderBy());
			ResponseModel resModel = new ResponseFromMDMImpl().sendInfoToMDM(
					CidClientsUtil.getUrl() + CidClientsUtil.CID_CLIENTS_QUERY, cidReq);
			CidDevicesResponseEBO cidRes = CidClientsUtil.transResponseToObject(resModel);
			return cidRes;
		}catch(Exception e){
			log.error("CID CLIENTS : prepareCidClientsList()");
			return null;
		}
	}
	
	private List<CidDeviceEBO> getTotalCidClients(String size){
		try{
			String direction = "";
			enableSorting();
			if(sortParams.isAscending()){
				direction = CidClientsUtil.CID_CLIENTS_ASC;
			}else{
				direction = CidClientsUtil.CID_CLIENTS_DESC;
			}
			String cidReq = CidClientsUtil.transObjectToXML(getDomain().getInstanceId()
					, size,"0",direction,sortParams.getOrderBy());
			ResponseModel resModel = new ResponseFromMDMImpl().sendInfoToMDM(
					CidClientsUtil.getUrl() + CidClientsUtil.CID_CLIENTS_QUERY, cidReq);
			CidDevicesResponseEBO cidRes = CidClientsUtil.transResponseToObject(resModel);
			return cidRes.getCidList();
		}catch(Exception e){
			log.error("CID CLIENTS : getTotalCidClients()");
			return null;
		}
	}

	private boolean addCidClients(List<CidDeviceEBO> cids){
		try{
			String cidSend = CidClientsUtil.transAddedCidDevicesToXML(getDomain().getInstanceId(),cids);
			HttpClient client = new HttpClient();
			ResponseModel resModel= HttpToolkit.doPostXML(CidClientsUtil.getUrl() + CidClientsUtil.CID_CLIENTS_ADD
					,cidSend,client);
			if(null != resModel){
				if(resModel.getResponseCode() == HttpStatus.SC_OK){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}catch(Exception e){
			log.error(AuxiliaryFileMgmtAction.class.getSimpleName() + "addCidClients()" + "failed to add the cid clients");
			return false;
		}
	}
	
	private String readString(List<CidDeviceEBO> cids){
		String content = "";
		for(int i= 0;i < cids.size();i++){
			content = content.concat(cids.get(i).getMacAddress() + "\r\n");
		}
		return content;
	}
	
	private static void formatMac(List<CidDeviceEBO> cids){
		if(null != cids){
			for(int i=0;i < cids.size();i++){
				CidDeviceEBO temp = cids.get(i);
				if(temp.getMacAddress().replaceAll(" ", "").length() == 12){
					temp.setMacAddress(MacAddressUtil.addDelimiter(temp.getMacAddress(), 2, ":"));
				}
			}
		}
	}
	
	public void genCidClientsFile(String file) throws IOException{
		List<CidDeviceEBO> cidList = getTotalCidClients(String.valueOf(MgrUtil.getSessionAttribute(boClass.getSimpleName() + "TotalNumber")));
		if(cidList != null){
			String content = readString(cidList);
			CidClientsUtil.writeFile(file, content);
		}else{
			CidClientsUtil.writeFile(file, " ");
		}
	}
	
	private Collection<JSONObject> refreshFromSession(List<CidDeviceEBO> cidClientList) throws Exception{
		Collection<JSONObject> updates = new Vector<JSONObject>();
		JSONObject update = new JSONObject();
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Page",cidClientList);
		update.put("id", -1);
		updates.add(update);
		return updates;
	}
	
	@SuppressWarnings("unchecked")
	private Collection<JSONObject> getUpdates()throws Exception{
		Collection<JSONObject> updates = new Vector<JSONObject>();
		
		List<CidDeviceEBO> oldCidClientList = (List<CidDeviceEBO>)MgrUtil
				.getSessionAttribute(boClass.getSimpleName() + "Page");
		int oldRows = (Integer)MgrUtil
				.getSessionAttribute(boClass.getSimpleName() + "TotalNumber");
		CidDevicesResponseEBO cidClientRes = prepareCidClientsList(String.valueOf(paging.getPageSize())
				, String.valueOf(pageIndex-1));
		formatMac(cidClientRes.getCidList());
		List<CidDeviceEBO> newCidClientList = cidClientRes.getCidList();
		
		rct = Integer.parseInt(cidClientRes.getTotalNumber());
		pct = Integer.parseInt(cidClientRes.getTotalPages());
	
		if((oldCidClientList == null) || (rct != oldRows)){
            MgrUtil
				.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
		    MgrUtil
				.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
			return refreshFromSession(newCidClientList);
		}
		if (oldCidClientList.size() != newCidClientList.size()) {
			// full refresh
			MgrUtil
			    .setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
	        MgrUtil
			    .setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
			return refreshFromSession(newCidClientList);
		}
		for (int i = 0; i < oldCidClientList.size(); i++) {
			CidDeviceEBO client = oldCidClientList.get(i);
			CidDeviceEBO newClient = newCidClientList.get(i);
				if ((!client.getMacAddress().equals(newClient.getMacAddress()))
					||(!client.getImei().equals(newClient.getImei()))
						) {
					// full refresh
					MgrUtil
					.setSessionAttribute(boClass.getSimpleName() + "TotalNumber",new Integer(rct));
			        MgrUtil
					.setSessionAttribute(boClass.getSimpleName() + "TotalPages",new Integer(pct));
					return refreshFromSession(newCidClientList);
				}
			
		}

		oldCidClientList = newCidClientList;
		return updates;
	}
}