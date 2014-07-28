package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;

public class ServerCSRAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private String				commonName			= null;
	private String				orgName				= null;
	private String				orgUnit				= null;
	private String				localName			= null;
	private String				stateName			= null;
	private String				countryCode			= null;
	private String				email				= null;
	private String				altEmail;
	public String getAltEmail() {
		return altEmail;
	}

	public void setAltEmail(String altEmail) {
		this.altEmail = altEmail;
	}

	public String getAltDNS() {
		return altDNS;
	}

	public void setAltDNS(String altDNS) {
		this.altDNS = altDNS;
	}

	public String getAltIP() {
		return altIP;
	}

	public void setAltIP(String altIP) {
		this.altIP = altIP;
	}

	private String altDNS;
	private String altIP;
	// show "1024"
	private String				keySize				= "1";
	private String				password			= null;
	private String				fileName			= "server";

	private static EnumItem[]	enumKeySize			= getEnums_KeySize();

	private static EnumItem[] getEnums_KeySize() {
		String[] keySizeArray = new String[] { "512", "1024", "2048" };
		EnumItem[] enumItems = new EnumItem[keySizeArray.length];
		for (int i = 0; i < keySizeArray.length; i++) {
			EnumItem item = new EnumItem(i, keySizeArray[i]);
			enumItems[i] = item;
		}

		return enumItems;
	}

	// constant define
	private final String	HANDLEMODE_EXPORTCSR	= "exportCSR";

	private final String	HANDLEMODE_SIGNCSR		= "signCSR";
	
	private final String	HANDLEMODE_CLIENT_SIGNCSR = "clientCSR";

	// enum value : {"exportCSR","signCSR"}
	private String			handleMode				= HANDLEMODE_EXPORTCSR;

	private String			validity				= "365";

	private boolean			disabledExport			= false;

	private boolean			disabledSign			= true;

	private String			inputPath;

	private boolean			combineFile				= false;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("create".equals(operation)) {
				try {

					// check file name
					if (fileName.equals("Default_key") || fileName.equals("Default_CA")) {
						addActionError(HmBeResUtil.getString("serverCSR.create.error") + " "
								+ fileName + " is a reserved name.");

						return SUCCESS;
					}

					boolean isSucc = createCSR();
					if (isSucc) {
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.csr"));

						// save file name
						MgrUtil.setSessionAttribute("serverCSRFileName", fileName);

						return "create";
					} else {
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.csr"));
						addActionError(HmBeResUtil.getString("serverCSR.create.error"));
					}
				} catch (Exception e) {
					addActionError(HmBeResUtil.getString("serverCSR.create.error") + " "
							+ e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.csr"));
				}

				return SUCCESS;
			} else if ("cancel".equals(operation)) {
				cancelOperation();
				return SUCCESS;
			} else if ("ok".equals(operation)) {
				fileName = (String) MgrUtil.getSessionAttribute("serverCSRFileName");
				// inputPath = "WEB-INF" + File.separator + "downloads"
				// + File.separator + "aerohiveca" + File.separator
				// + fileName + ".csr";

				inputPath = AhDirTools.getCertificateDir(getDomain().getDomainName()) + fileName
						+ ".csr";

				// check file exist
				File file = new File(inputPath);
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(HmBeResUtil.getString("serverCSR.fileNotExist"));
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.find.created.csr"));
					return "create";
				}

				if (HANDLEMODE_EXPORTCSR.equals(handleMode)) {
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.download.csr"));
					return "download";
				}

				try {
					if (HANDLEMODE_SIGNCSR.equals(handleMode)) {
						boolean isSucc = signCSRFile();

						if (!isSucc) {
							addActionError(HmBeResUtil.getString("serverCSR.sign.error"));
							generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.sign.csr"));
							return "create";
						} else {
							addActionMessage(HmBeResUtil.getString("serverCSR.sign.success"));
							generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.sign.csr"));
						}
					}else if(HANDLEMODE_CLIENT_SIGNCSR.equals(handleMode)){
						String customerId = "";
						if(NmsUtil.isHostedHMApplication()){
							customerId = getUserContext().getCustomerId();
						}else{
							List<?> cusList = QueryUtil.executeQuery("select customerId from "+CloudAuthCustomer.class.getSimpleName(), null, new FilterParams("owner.domainName",HmDomain.HOME_DOMAIN));
						    if(!cusList.isEmpty()){
						    	customerId = (String)cusList.get(0);
						    }
						}
						boolean isSucc = new CertificateGenSV().signCsrByClientCA(getDomain().getDomainName(),customerId,getDomain().getInstanceId(),fileName);
						if (!isSucc) {
							addActionError(HmBeResUtil.getString("serverCSR.sign.error"));
							generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.sign.csr"));
							return "create";
						} else {
							addActionMessage(HmBeResUtil.getString("serverCSR.sign.success"));
							generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.sign.csr"));
						}
					}
				} catch (Exception e) {
					addActionError(HmBeResUtil.getString("serverCSR.sign.error") + " "
							+ e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.sign.csr"));
				}

				return SUCCESS;
			} else {
				/**
				 * init field
				 */
				if (handleMode.equals(HANDLEMODE_EXPORTCSR)) {
					disabledExport = false;
					disabledSign = true;
				} else {
					disabledExport = true;
					disabledSign = false;
				}

				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SERVERCSR);
	}

	private boolean createCSR() throws Exception {
		String keySizeStr = "1024";
		for (EnumItem item : enumKeySize) {
			if (item.getKey() == Integer.valueOf(keySize)) {
				keySizeStr = item.getValue();
			}
		}

		// create CSR file
		BeRootCADTO dto = new BeRootCADTO();
		dto.setCommName(commonName);
		dto.setCountryCode(countryCode);
		dto.setEmailAddress(email == null ? "" : email);
		dto.setAltName(getSubjectAltName());
		dto.setKeySize(keySizeStr);
		dto.setLocalityName(localName);
		dto.setOrgName(orgName);
		dto.setOrgUnit(orgUnit);
		dto.setPassword(password == null ? "" : password.trim());
		dto.setStateName(stateName);
		// dto.setValidity(validity);
		dto.setFileName(fileName);
		dto.setDomainName(getDomain().getDomainName());

		boolean isSuccess = HmBeAdminUtil.createServerCSR(dto);

		if (isSuccess) {
			MgrUtil.setSessionAttribute("serverCSRDto", dto);
		}

		return isSuccess;
	}
	
	private String getSubjectAltName()
	{
		String name = "";
		
		if (altEmail != null && altEmail.length() > 0) {
			name = "email:" + altEmail + ",";
		}
		
		if (altDNS != null && altDNS.length() > 0) {
			name = name + "DNS:" + altDNS + ",";
		}
		
		if (altIP != null && altIP.length() > 0) {
			name = name + "IP:" + altIP + ",";
		}
		
		if (name.length() > 0) {
			name = name.substring(0,name.length() -1);
		}
		
		return name;
	}

	public String getLocalFileName() {
		return fileName + ".csr";
	}

	public InputStream getInputStream() throws Exception {
		// return ServletActionContext.getServletContext().getResourceAsStream(inputPath);

		inputPath = AhDirTools.getCertificateDir(getDomain().getDomainName()) + fileName + ".csr";

		return new FileInputStream(inputPath);
	}

	private boolean signCSRFile() throws Exception {
		BeRootCADTO dto = (BeRootCADTO) MgrUtil.getSessionAttribute("serverCSRDto");
		dto.setValidity(validity);
		dto.setDomainName(getDomain().getDomainName());

		return HmBeAdminUtil.signServerCsr(dto, combineFile);
	}

	private void cancelOperation() {
		commonName = null;
		orgName = null;
		orgUnit = null;
		localName = null;
		stateName = null;
		countryCode = null;
		email = null;
		altEmail = null;
		altDNS = null;
		altIP = null;
		keySize = null;
		password = null;
		fileName = "server";
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKeySize() {
		return keySize;
	}

	public void setKeySize(String keySize) {
		this.keySize = keySize;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(String orgUnit) {
		this.orgUnit = orgUnit;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public static EnumItem[] getEnumKeySize() {
		return enumKeySize;
	}

	public static void setEnumKeySize(EnumItem[] enumKeySize) {
		ServerCSRAction.enumKeySize = enumKeySize;
	}

	public int getCommonNameLength() {
		return 64;
	}

	public int getOrgNameLength() {
		return 64;
	}

	public int getOrgUnitLength() {
		return 64;
	}

	public int getLocalNameLength() {
		return 64;
	}

	public int getStateNameLength() {
		return 64;
	}

	public int getCountryCodeLength() {
		return 2;
	}

	public int getEmailAddrLength() {
		return 64;
	}

	public int getPasswordLength() {
		return 20;
	}

	public int getFileNameLength() {
		return 20;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getHandleMode() {
		return handleMode;
	}

	public void setHandleMode(String handleMode) {
		this.handleMode = handleMode;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public boolean isDisabledExport() {
		return disabledExport;
	}

	public void setDisabledExport(boolean disabledExport) {
		this.disabledExport = disabledExport;
	}

	public boolean isDisabledSign() {
		return disabledSign;
	}

	public void setDisabledSign(boolean disabledSign) {
		this.disabledSign = disabledSign;
	}

	public boolean isCombineFile() {
		return combineFile;
	}

	public void setCombineFile(boolean combineFile) {
		this.combineFile = combineFile;
	}

	public TextItem[] getServerCSRRadio() {
		return new TextItem[] {new TextItem("signCSR",
				HmBeResUtil.getString("serverCSR.sign.radio")),
				new TextItem("clientCSR",HmBeResUtil.getString("serverCSR.client.sign.radio"))};
	}

}