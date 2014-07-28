package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeCAFileInfo;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.ActiveDirectoryOrOpenLdap;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.ui.actions.BaseAction;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class CertificatesAction extends BaseAction {

	private static final long			serialVersionUID	= 1L;

	private static final Tracer			log					= new Tracer(CertificatesAction.class
																	.getSimpleName());

	private static List<BeCAFileInfo>	fileList;

	// for download
	// selected ca file name, should with suffix
	private String						certFileName;

	private String						inputPath;

	private boolean						pfx2pem;

	private String						pfxPassword;

	private boolean						der2pem;

	private final String				DERTYPE_CERT		= "cert";

	private final String				DERTYPE_KEY			= "key";

	private String						derType				= DERTYPE_CERT;

	private String						derPassword;

	private List<String>				selectedCAName;
	
	private String importFileType;
	
	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			// check session token for CSRF attack
			if (!isCSRFTokenValida() && ("remove".equals(operation))) {
				generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("security.csrfattack") + getLastTitle());

				throw new HmException(MgrUtil.getUserMessage("error.security.invalidRequest"),						
						HmMessageCodes.SECURITY_REQUEST_INVALID, new String[] { "remove" });
			}
			
			if ("export".equals(operation)) {
				if (null == selectedCAName || selectedCAName.isEmpty()) {
					addActionError(MgrUtil.getUserMessage("action.error.no.selected.import"));
					return SUCCESS;
				}
				certFileName = selectedCAName.get(0);

				// check file exist
				inputPath = AhDirTools.getCertificateDir(getDomain().getDomainName())
						+ certFileName;

				File file = new File(inputPath);
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(HmBeResUtil.getString("certMgmt.findCertFile.error"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.download.certificate"));
					return SUCCESS;
				}

				generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.download.certificate"));
				return "download";
			} else if ("import".equals(operation)) {
				// goto import page
				return INPUT;
			} else if ("importFile".equals(operation)) {
				if (certificateFile == null) {
					if(isJsonMode() && !isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.fileNotExist"));
						return "json";
					}else{
						addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
						 return getReturnPathWithJsonMode(SUCCESS,"inputEx");
					}
				}

				// check file suffix
				if (!checkImportCertFile()) {
					
					if(isJsonMode() && !isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage(getActionErrors().toArray()[0].toString()));
						
						return "json";
					}else{
						return getReturnPathWithJsonMode(INPUT,"inputEx");
					}
				}
				
				if(isJsonMode() && !isParentIframeOpenFlg()){
					jsonObject = new JSONObject();
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", certificateFileFileName);
					jsonObject.put("newObjId", certificateFileFileName);
					
					try {
						importCertFiles();
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.import.certificate"));
					} catch (FileNotFoundException e) {
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.fileNotExist"));
						log.error("execute", "file not found!", e);
					} catch (Exception e) {
						jsonObject.put("errMsg", HmBeResUtil.getString("certMgmt.importCert.error"));
						log.error("execute", "Import certificate failed!", e);
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					try {
						importCertFiles();

						addActionMessage(HmBeResUtil.getString("certMgmt.importCert.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.import.certificate"));
					} catch (FileNotFoundException e) {
						addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
						log.error("execute", "file not found!", e);
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.import.certificate"));
					} catch (Exception e) {
						addActionError(HmBeResUtil.getString("certMgmt.importCert.error"));
						log.error("execute", "Import certificate failed!", e);
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.import.certificate"));
					}

					// refresh certificate file list
					prepareCertFileList();
					return getReturnPathWithJsonMode(SUCCESS,"inputEx");
				}
			} else if ("remove".equals(operation)) {
				removeCertificateFiles();

				prepareCertFileList();
				return SUCCESS;
			} else if (("importFile" + getLstForward()).equals(operation)) {
				if (certificateFile == null) {
					if(isJsonMode() && !isParentIframeOpenFlg()){
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.fileNotExist"));
						return "json";
					}else{
						addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
						return getReturnPathWithJsonMode(INPUT, "inputEx", "importCertificateJsonDlg");
					}
				}

				// check file suffix
				if (!checkImportCertFile()) {
					if (this.getLastExConfigGuide() != null){
						 return "inputEx";
					} else {
						if(isJsonMode() && !isParentIframeOpenFlg()){
							jsonObject = new JSONObject();
							jsonObject.put("resultStatus", false);
							jsonObject.put("errMsg", MgrUtil.getUserMessage(getActionErrors().toArray()[0].toString()));
							return "json";
						}
						return getReturnPathWithJsonMode(INPUT, "inputEx", "importCertificateJsonDlg");
					}
				}
				
				if(isJsonMode() && !isParentIframeOpenFlg()){
					jsonObject = new JSONObject();
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("newObjName", certificateFileFileName);
					jsonObject.put("newObjId", certificateFileFileName);
					
					try {
						importCertFiles();
					} catch (FileNotFoundException e) {
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.fileNotExist"));
						log.error("execute", "file not found!", e);
						jsonObject.put("resultStatus", false);
						return "json";
					} catch (Exception e) {
						jsonObject.put("errMsg", HmBeResUtil.getString("certMgmt.importCert.error"));
						log.error("execute", "Import certificate failed!", e);
						jsonObject.put("resultStatus", false);
						return "json";
					}
					jsonObject.put("resultStatus", true);
					return "json";
				}else{
					try {
						importCertFiles();
					} catch (FileNotFoundException e) {
						addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
						log.error("execute", "file not found!", e);
					} catch (Exception e) {
						addActionError(HmBeResUtil.getString("certMgmt.importCert.error"));
						log.error("execute", "Import certificate failed!", e);
					}
					setUpdateContext(true);
					String str =  getLstForward();
					return str;
				}
			} else if ("return".equals(operation)) {
				return SUCCESS;
			} else if (("return" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("radiusImport".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("admin.title.cafile.import"))) {
					setUpdateContext(true);
					return getLstForward();
				}

				return getReturnPathWithJsonMode(INPUT, "inputEx", "importCertificateJsonDlg");
			} else if ("managementImport".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("admin.title.cafile.import"))) {
					setUpdateContext(true);
					return getLstForward();
				}

				if (this.getLastExConfigGuide() != null){
					 return "inputEx";
				} else {
					 return getReturnPathWithJsonMode(INPUT,"inputEx");
				}
			} else {
				prepareCertFileList();

				return SUCCESS;
			}
		} catch (Exception e) {
			log.error("execute", MgrUtil.getUserMessage(e), e);
			addActionError(MgrUtil.getUserMessage(e));
			generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.certificate")
					+ " " + MgrUtil.getUserMessage(e));
			
			try {
				prepareCertFileList();
				return SUCCESS;
			} catch (Exception ne) {
				return prepareEmptyBoList();
			}
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CERTIFICATES);
	}

	public void prepareCertFileList() {
		fileList = new ArrayList<BeCAFileInfo>();

		if (getUserContext() == null) {
			// Need to redirect to login.
			return;
		}

		if (showAllCert()) {
			prepareAllDomainCAFileList();
		} else {
			fileList = HmBeAdminUtil.getCAFileInfoList(getDomain().getDomainName());
		}

		if (fileList == null || fileList.size() == 0) {
			fileList = new ArrayList<BeCAFileInfo>(0);
		}
	}
	
	private boolean showAllCert() {
		return null != getUserContext() && getUserContext().getSwitchDomain() == null && getUserContext().getDomain().isHomeDomain() && getUserContext().getUserGroup().isAdministrator();
	}

	private void prepareAllDomainCAFileList() {
		List<HmDomain> domainList = QueryUtil.executeQuery(HmDomain.class, null, new FilterParams(
				"domainName != :s1", new Object[]{HmDomain.GLOBAL_DOMAIN}));
		for (HmDomain domain : domainList) {
			List<BeCAFileInfo> domainFileList = HmBeAdminUtil.getCAFileInfoList(domain
					.getDomainName());
			if (domainFileList != null && domainFileList.size() > 0) {
				fileList.addAll(domainFileList);
			}
		}
	}

	private void removeCertificateFiles() throws Exception {
		for (String selectedName : selectedCAName) {
			// check default
			if (selectedName.equals("Default_key.pem")
					|| selectedName.equalsIgnoreCase("Default_CA.pem")
					|| selectedName.equalsIgnoreCase(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_CERT)
					|| selectedName.equalsIgnoreCase(BeAdminCentOSTools.AH_NMS_DEFAULT_SERVER_KEY)) {
				addActionError(MgrUtil.getUserMessage("action.error.remove.default.certificate",selectedName));
				continue;
			}

			// check whether certificate file being used
			String checkResult = ifCaOrKeyFileIsUsed(new String[] { selectedName });
			if (!"".equals(checkResult)) {
				addActionError(checkResult);
				continue;
			}

			boolean isSucc;
			String exceptionMessage = "";
			try {
				isSucc = HmBeAdminUtil.removeCAFile(selectedName, getDomain().getDomainName());
			} catch (Exception e) {
				isSucc = false;
				log.error("removeCertificateFiles", "catch exception when remove file "
						+ selectedName, e);
				exceptionMessage = e.getMessage();
			}

			if (isSucc) {
				// refresh file list
				addActionMessage(MgrUtil.getUserMessage("message.certificate.removed.success",selectedName));
				generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.remove.certificate"));
			} else {
				addActionError(MgrUtil.getUserMessage("action.error.unable.to.remove.certificate")+"'" + selectedName + "'. "
						+ exceptionMessage);
				generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.remove.certificate"));
			}
		}
	}

	// struts download support
	public String getLocalFileName() {
		return certFileName;
	}

	public InputStream getInputStream() throws Exception {
		inputPath = AhDirTools.getCertificateDir(getDomain().getDomainName()) + certFileName;

		return new FileInputStream(inputPath);

		// inputPath = "/WEB-INF/" + "downloads" + File.separator + "aerohiveca"
		// + File.separator + certFileName;

		// return ServletActionContext.getServletContext().getResourceAsStream(
		// inputPath);
	}

	// struts download support -- end

	private boolean checkImportCertFile() {
		// if (!certificateFileFileName.endsWith(".pem")) {
		// addActionError(MgrUtil.getUserMessage("error.formatInvalid", "Certificate file"));
		// return false;
		// }
		if (!verifyCertificateFileType(certificateFileFileName)) {
			addActionError(MgrUtil.getUserMessage("error.formatInvalid", "Certificate file"));
			return false;
		}

		// max file name length 32
		if (certificateFileFileName.length() > 32) {
			addActionError(MgrUtil.getUserMessage("action.error.certificate.file.name.length"));
			return false;
		}

		// can't contain blank
		if (certificateFileFileName.indexOf(" ") > 0) {
			addActionError(MgrUtil.getUserMessage("action.error.certificate.file.name.contain.blank"));
			return false;
		}

		if (getCertificateFile().length() == 0) {
			addActionError(MgrUtil.getUserMessage("action.error.import.file.is.empty"));
			return false;
		}

		return true;
	}

	/**
	 * Check if file is used by RADIUS on HiveAP or Open LDAP.
	 * 
	 * @param arg_File
	 *            names
	 * @return the file names are used
	 */
	private String ifCaOrKeyFileIsUsed(String[] arg_File) {
		int i = 0;
		StringBuilder result = new StringBuilder();
		String resultString = "";
		if (null != arg_File) {
			String where1 = "caCertFile = :s1 OR keyFile = :s2 OR serverFile = :s3";
			Object[] values = new Object[3];
			String where2 = "caCertFileO = :s1 OR clientFile = :s2 OR keyFileO = :s3";
			for (String name : arg_File) {
				values[0] = name;
				values[1] = name;
				values[2] = name;
				List<?> boIds1 = QueryUtil.executeQuery("select id from "
						+ (RadiusOnHiveap.class).getSimpleName(), null, new FilterParams(where1,
						values), null, getDomain().getId());
				if (boIds1.isEmpty()) {
					List<?> boIds2 = QueryUtil.executeQuery("select id from "
							+ (ActiveDirectoryOrOpenLdap.class).getSimpleName(), null,
							new FilterParams(where2, values), null, getDomain().getId());
					if (!boIds2.isEmpty()) {
						result.append("(").append(name).append(") ");
						i++;
					}
				} else {
					result.append("(").append(name).append(") ");
					i++;
				}
			}
		}
		if (!"".equals(result.toString())) {
			if (i > 1) {
				resultString = MgrUtil.getUserMessage("error.hiveAPFile.usedByProfiles",
						new String[] { result.toString() + "are",
								NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Settings or LDAP Settings" });
			} else {
				resultString = MgrUtil.getUserMessage("error.hiveAPFile.usedByProfiles",
						new String[] { result.toString() + "is",
								NmsUtil.getOEMCustomer().getAccessPonitName()+" AAA Server Settings or LDAP Settings" });
			}
		}
		return resultString;
	}

	// struts upload support
	private void importCertFiles() throws Exception {
		FileOutputStream fos = null;
		FileInputStream fis = null;

		try {
			// 1: upload file to server side
			fos = new FileOutputStream(AhDirTools.getCertificateDir(getDomain().getDomainName())
					+ getCertificateFileFileName());
			fis = new FileInputStream(getCertificateFile());
			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}

			// convert file format if needed
			String srcFileName = getCertificateFileFileName();
			String dstFileName = srcFileName.substring(0, srcFileName.indexOf(".")) + ".pem";
			if (pfx2pem) {
				boolean isSucc = HmBeAdminUtil.switchPfxToPem(srcFileName, dstFileName,
						(pfxPassword == null || pfxPassword.length() == 0) ? null : pfxPassword,
						getDomain().getDomainName());
				if (!isSucc) {
					throw new Exception(
							"Unable to convert certificate file format from pfx to pem.");
				}
			}

			if (der2pem) {
				boolean isSucc = false;
				if (derType.equals(DERTYPE_CERT)) {
					isSucc = HmBeAdminUtil.switchDERToPemCert(srcFileName, dstFileName, getDomain()
							.getDomainName());
				} else if (derType.equals(DERTYPE_KEY)) {
					isSucc = HmBeAdminUtil
							.switchDerToPemKey(srcFileName, dstFileName,
									(derPassword == null || derPassword.length() == 0) ? null
											: derPassword, getDomain().getDomainName());
				}
				if (!isSucc) {
					throw new Exception(
							"Unable to convert certificate file format from der to pem.");
				}
			}

		} finally {
			if (fos != null) {
				fos.close();
			}

			if (fis != null) {
				fis.close();
			}
		}
	}

	File			certificateFile;

	private String	certificateFileContentType;

	private String	certificateFileFileName;

	public void setCertificateFile(File certificateFile) {
		this.certificateFile = certificateFile;
	}

	public String getCertificateFileContentType() {
		return certificateFileContentType;
	}

	public void setCertificateFileContentType(String certificateFileContentType) {
		this.certificateFileContentType = certificateFileContentType;
	}

	public String getCertificateFileFileName() {
		return certificateFileFileName;
	}

	public void setCertificateFileFileName(String certificateFileFileName) {
		this.certificateFileFileName = certificateFileFileName;
	}

	public File getCertificateFile() {
		return certificateFile;
	}

	// struts upload support -- end

	public static List<BeCAFileInfo> getFileList() {
		return fileList;
	}

	public static void setFileList(List<BeCAFileInfo> fileList) {
		CertificatesAction.fileList = fileList;
	}

	public boolean isDer2pem() {
		return der2pem;
	}

	public void setDer2pem(boolean der2pem) {
		this.der2pem = der2pem;
	}

	public String getDerPassword() {
		return derPassword;
	}

	public void setDerPassword(String derPassword) {
		this.derPassword = derPassword;
	}

	public String getDerType() {
		return derType;
	}

	public void setDerType(String derType) {
		this.derType = derType;
	}

	public boolean isPfx2pem() {
		return pfx2pem;
	}

	public void setPfx2pem(boolean pfx2pem) {
		this.pfx2pem = pfx2pem;
	}

	public String getPfxPassword() {
		return pfxPassword;
	}

	public void setPfxPassword(String pfxPassword) {
		this.pfxPassword = pfxPassword;
	}

	public List<String> getSelectedCAName() {
		return selectedCAName;
	}

	public void setSelectedCAName(List<String> selectedCAName) {
		this.selectedCAName = selectedCAName;
	}

	public String getImportFileType() {
		return importFileType;
	}

	public void setImportFileType(String importFileType) {
		this.importFileType = importFileType;
	}

	/**
	 * check valid certificate file type
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean verifyCertificateFileType(String fileName) {
		
		Pattern p = Pattern.compile("^.*?\\.(P7B|P7C|SPC|P12|PFX|DER|CER|CRT|PEM)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher(fileName);
		if(matcher.find()) {
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		String fileName = "a.pembbbb.P7b.jpg.der";
		CertificatesAction.verifyCertificateFileType(fileName);
	}

}