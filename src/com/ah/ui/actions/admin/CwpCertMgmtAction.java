package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

@SuppressWarnings("serial")
public class CwpCertMgmtAction extends BaseAction implements QueryBo {

	private static final Tracer	log						= new Tracer(CwpCertMgmtAction.class
																.getSimpleName());

	private final String		CREATECERTWAY_CONCATE	= "concatenate";

	private final String		CREATECERTWAY_NEW		= "new";

	private String				createCertWay			= CREATECERTWAY_CONCATE;

	private String				commonName;

	private String				orgName;

	private String				orgUnit;

	private String				localName;

	private String				stateName;

	private String				countryCode;

	private String				email;

	private String				validity				= "365";

	private int					keySize					= KEYSIZE_1024;

	private String				certName_concate;

	private String				certName_new;

	private String				password;

	private String				certificateFile;

	private String				privateKeyFile;

	private String				description_concate;

	private String				description_new;

	// imported cert/key file name
	private String				fileName;

	// for download
	// selected ca file name, should with suffix
	private String				certFileName;

	private String				inputPath;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.cwpCertificate"))) {
					return getLstForward();
				}

				// check max index (0-15)
				int nextIndex = getNextIndex();
				if (nextIndex > CWPINDEX_MAXVALUE) {
					addActionMessage(MgrUtil.getUserMessage("message.certificate.is.more"));
					setTableColumns();
					return prepareBoList();
				}

				setSessionDataSource(new CwpCertificate());
				return getInputResult();
			} else if ("create".equals(operation)) {

				boolean isConcate = createCertWay.equals(CREATECERTWAY_CONCATE);
				// check cert,key file matching
				if (isConcate) {

					boolean isMatching = false;
					String errMessage = null;
					try {
						isMatching = HmBeAdminUtil
								.checkForCwp(
										getDomain().getDomainName(),
										certificateFile,
										(privateKeyFile == null || privateKeyFile.length() == 0) ? certificateFile
												: privateKeyFile, password);
					} catch (Exception e) {
						log.error("execute", "check cwp cert and key match catch exception", e);
						errMessage = e.getMessage();
					}

					if (!isMatching) {
						addActionError(errMessage);
						return getInputResult();
					}
				}
				
				// can't contain blank
				String cnameString = isConcate ? certName_concate : certName_new;
				if (cnameString.indexOf(" ") > 0) {
					addActionError(MgrUtil.getUserMessage("action.error.certificate.file.name.contain.blank"));
					return getErrorInputResult();
				}

				boolean isSucc = createCert();
				if (!isSucc) {
					addActionError(MgrUtil.getUserMessage("action.error.create.certificate.file"));
					return getInputResult();
				}

				CwpCertificate cert = new CwpCertificate();
				cert.setCertName(isConcate ? certName_concate : certName_new);
				cert.setDescription(isConcate ? description_concate : description_new);
				cert.setEncrypted(createCertWay.equals(CREATECERTWAY_CONCATE) && password != null
						&& password.trim().length() > 0);
				cert.setIndex(getNextIndex());
				cert.setSrcCertName((createCertWay.equals(CREATECERTWAY_CONCATE) ? certificateFile
						: cert.getCertName())
						+ ".pem");
				cert.setSrcKeyName((createCertWay.equals(CREATECERTWAY_CONCATE) ? privateKeyFile
						: cert.getCertName())
						+ ".pem");
				dataSource = cert;

				setTableColumns();

				return createBo();
			} else if (("create" + getLstForward()).equals(operation)) {
				boolean isConcate = createCertWay.equals(CREATECERTWAY_CONCATE);
				// check cert,key file matching
				if (isConcate) {

					boolean isMatching = false;
					String errMessage = null;
					try {
						isMatching = HmBeAdminUtil
								.checkForCwp(
										getDomain().getDomainName(),
										certificateFile,
										(privateKeyFile == null || privateKeyFile.length() == 0) ? certificateFile
												: privateKeyFile, password);
					} catch (Exception e) {
						log.error("execute", "check cwp cert and key match catch exception", e);
						errMessage = e.getMessage();
					}

					if (!isMatching) {
						addActionErrorMsg(errMessage);
						return getErrorInputResult();
					}
				}
				
				// can't contain blank
				String cnameString = isConcate ? certName_concate : certName_new;
				if (cnameString.indexOf(" ") > 0) {
					addActionError(MgrUtil.getUserMessage("action.error.certificate.file.name.contain.blank"));
					return getErrorInputResult();
				}

				boolean isSucc = createCert();
				if (!isSucc) {
					addActionErrorMsg("Fail to create certificate file!");
					return getErrorInputResult();
				}

				CwpCertificate cert = new CwpCertificate();
				cert.setCertName(isConcate ? certName_concate : certName_new);
				cert.setDescription(isConcate ? description_concate : description_new);
				cert.setEncrypted(createCertWay.equals(CREATECERTWAY_CONCATE) && password != null
						&& password.trim().length() > 0);
				cert.setIndex(getNextIndex());
				cert.setSrcCertName((createCertWay.equals(CREATECERTWAY_CONCATE) ? certificateFile
						: cert.getCertName())
						+ ".pem");
				cert.setSrcKeyName((createCertWay.equals(CREATECERTWAY_CONCATE) ? privateKeyFile
						: cert.getCertName())
						+ ".pem");
				dataSource = cert;

				id = createBo(dataSource);
				setUpdateContext(true);
				
				if(isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("succ", true);
					jsonObject.put("parentDomID", getParentDomID());
					jsonObject.put("addedId", id);
					jsonObject.put("addedName", ((CwpCertificate)(dataSource)).getCertName());
					return "json";
				} else {
					return getLstForward();
				}

			} else if ("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				addLstTitle(getText("config.title.cwpCertificate.edit") + " '"
						+ getDisplayName() + "'");
				
				if (dataSource == null) {
					return prepareBoList();
				} else {
					MgrUtil.setSessionAttribute("cache_CertName", getDataSource().getCertName());

					 return getEditResult();
				}
			} else if ("update".equals(operation)) {

				// check certName
				if (!checkCertName(getDataSource().getCertName())) {
					addActionError(MgrUtil.getUserMessage("action.error.object.update.item.exist",getDataSource().getCertName()));
					return getEditResult();
				}
				
				// can't contain blank
				if (getDataSource().getCertName().indexOf(" ") > 0) {
					addActionError(MgrUtil.getUserMessage("action.error.certificate.file.name.contain.blank"));
					return getEditResult();
				}

				if (!checkIndexValid(getDataSource().getIndex())) {
					addActionError(MgrUtil.getUserMessage("action.error.object.update.same.index.exist"));
					return getEditResult();
				}

				// rename file name
				String cacheCertName = (String) MgrUtil.getSessionAttribute("cache_CertName");
				boolean isSucc = true;
				String message = "";
				if (!cacheCertName.equals(getDataSource().getCertName())) {
					try {
						String certDir = AhDirTools.getCwpServerKeyDir(getDataSource().getOwner()
								.getDomainName());
						HmBeOsUtil.moveFile(certDir + cacheCertName + ".pem", certDir
								+ getDataSource().getCertName() + ".pem");
					} catch (Exception e) {
						log.error("execute", "rename cert name catch exception", e);
						isSucc = false;
						message = e.getMessage();
					}
				}

				if (!isSucc) {
					addActionError(MgrUtil.getUserMessage("action.error.rename.certificate") + message);
					return getEditResult();
				}

				setTableColumns();

				return updateBo();
			} else if (("update" + getLstForward()).equals(operation)) {
				// check certName
				if (!checkCertName(getDataSource().getCertName())) {
					addActionErrorMsg("The object cannot be updated because the item '" + getDataSource().getCertName()
							+ "' already exists.");
					return getErrorEditResult();
				}
				
				// can't contain blank
				if (getDataSource().getCertName().indexOf(" ") > 0) {
					addActionError(MgrUtil.getUserMessage("action.error.certificate.file.name.contain.blank"));
					return getEditResult();
				}

				if (!checkIndexValid(getDataSource().getIndex())) {
					addActionErrorMsg("The object cannot be updated because same index already exists.");
					return getErrorEditResult();
				}

				// rename file name
				String cacheCertName = (String) MgrUtil.getSessionAttribute("cache_CertName");
				boolean isSucc = true;
				String message = "";
				if (!cacheCertName.equals(getDataSource().getCertName())) {
					try {
						String certDir = AhDirTools.getCwpServerKeyDir(getDataSource().getOwner()
								.getDomainName());
						HmBeOsUtil.moveFile(certDir + cacheCertName + ".pem", certDir
								+ getDataSource().getCertName() + ".pem");
					} catch (Exception e) {
						log.error("execute", "rename cert name catch exception", e);
						isSucc = false;
						message = e.getMessage();
					}
				}

				if (!isSucc) {
					addActionErrorMsg("Fail to rename certificate: " + message);
					return getErrorEditResult();
				}

				updateBo(dataSource);
				setUpdateContext(true);
				if(isJsonMode() && !isParentIframeOpenFlg()) {
					jsonObject = new JSONObject();
					jsonObject.put("succ", true);
					return "json";
				} else {
					return getLstForward();
				}
			}else if ("importCert".equals(operation)) {
				clearErrorsAndMessages();
				// addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward("cwpCertMgmt");
				MgrUtil.setSessionAttribute("cwpCertMgmt_installType", "cert");
				MgrUtil.setSessionAttribute("createCert_certName", certName_concate);
				MgrUtil.setSessionAttribute("createCert_description", description_concate);
				MgrUtil.setSessionAttribute("certificateFile_install", certificateFile);
				MgrUtil.setSessionAttribute("privateKey_install", privateKeyFile);

				return "newFile";
			} else if ("importKey".equals(operation)) {
				clearErrorsAndMessages();
				// addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward("cwpCertMgmt");
				MgrUtil.setSessionAttribute("cwpCertMgmt_installType", "key");
				MgrUtil.setSessionAttribute("createCert_certName", certName_concate);
				MgrUtil.setSessionAttribute("createCert_description", description_concate);
				MgrUtil.setSessionAttribute("certificateFile_install", certificateFile);
				MgrUtil.setSessionAttribute("privateKey_install", privateKeyFile);

				return "newFile";
			} else if ("continue".equals(operation)) {
				String installType = (String) MgrUtil
						.getSessionAttribute("cwpCertMgmt_installType");
				if (installType.equals("cert")) {
					if (fileName != null && fileName.length() > 0) {
						certificateFile = fileName;
					} else {
						certificateFile = (String) MgrUtil
								.getSessionAttribute("certificateFile_install");
					}
					MgrUtil.removeSessionAttribute("certificateFile_install");

					privateKeyFile = (String) MgrUtil.getSessionAttribute("privateKey_install");
					MgrUtil.removeSessionAttribute("privateKey_install");
				} else {
					if (fileName != null && fileName.length() > 0) {
						privateKeyFile = fileName;
					} else {
						privateKeyFile = (String) MgrUtil.getSessionAttribute("privateKey_install");
					}
					MgrUtil.removeSessionAttribute("privateKey_install");

					certificateFile = (String) MgrUtil
							.getSessionAttribute("certificateFile_install");
					MgrUtil.removeSessionAttribute("certificateFile_install");
				}

				certName_concate = (String) MgrUtil.getSessionAttribute("createCert_certName");
				description_concate = (String) MgrUtil
						.getSessionAttribute("createCert_description");

				removeLstTitle();
				removeLstForward();

				return getInputResult();
			} else if ("export".equals(operation)) {

				CwpCertificate selectedBo = getSelectCert();
				if (selectedBo == null) {
					addActionError(HmBeResUtil.getString("certMgmt.findCertFile.error"));
					setTableColumns();
					return prepareBoList();
				}

				certFileName = selectedBo.getCertName();

				// check file exist
				inputPath = AhDirTools.getCwpServerKeyDir(selectedBo.getOwner().getDomainName())
						+ certFileName + ".pem";

				File file = new File(inputPath);
				if (!file.exists()) {
					// commonly, logic should not come here
					addActionError(HmBeResUtil.getString("certMgmt.findCertFile.error"));
					setTableColumns();
					return prepareBoList();
				}

				return "download";
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("remove".equals(operation)) {

				List<CwpCertificate> certList = getSelectCertList();
				if (certList == null) {
					addActionError(HmBeResUtil.getString("certMgmt.findCertFile.error"));
					setTableColumns();
					return prepareBoList();
				}

				boolean isSucc = baseOperation();

				if (isSucc) {
					for (CwpCertificate certificate : certList) {
						try {
							inputPath = AhDirTools.getCwpServerKeyDir(certificate.getOwner()
									.getDomainName())
									+ certificate.getCertName() + ".pem";
							HmBeOsUtil.deletefile(inputPath);
						} catch (Exception e) {
							log.error("execute", "delete cwp cert(" + inputPath
									+ ") catch exception", e);
						}
					}
				}

				setTableColumns();
				return prepareBoList();

			} else {
				setTableColumns();

				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CWPCERTMGMT);
		setDataSource(CwpCertificate.class);
		keyColumnId = COLUMN_CERTNAME;
		tableId = HmTableColumn.TABLE_CONFIGURATION_CWP_CERTIFICATE;
	}

	public CwpCertificate getDataSource() {
		return (CwpCertificate) dataSource;
	}

	/**
	 * create new cert file
	 * 
	 * @return -
	 */
	private boolean createCert() {
		try {
			if (createCertWay.equals(CREATECERTWAY_CONCATE)) {
				
				if (privateKeyFile == null || privateKeyFile.length() == 0)	{
					privateKeyFile = certificateFile;
				}
				
				return HmBeAdminUtil.mergeForCwp(getDomain().getDomainName(), certName_concate,
						certificateFile, privateKeyFile, password);
			} else if (createCertWay.equals(CREATECERTWAY_NEW)) {
				//
				String keySizeStr = CLICommonFunc.getEnumItemValue(getEnumKeySize(), keySize);

				BeRootCADTO dto = new BeRootCADTO();
				dto.setFileName(certName_new);
				dto.setCommName(commonName);
				dto.setCountryCode(countryCode);
				dto.setEmailAddress(email == null ? "" : email);
				dto.setKeySize(keySizeStr);
				dto.setLocalityName(localName);
				dto.setOrgName(orgName);
				dto.setOrgUnit(orgUnit);
				dto.setPassword(password);
				dto.setStateName(stateName);
				dto.setValidity(validity);
				dto.setDomainName(getDomain().getDomainName());

				return HmBeAdminUtil.createDomainCWP(getDomain().getDomainName(), dto);
			}
		} catch (Exception e) {
			log.error("createCert", "create cert failed.", e);

			return false;
		}

		return true;
	}

	public static final int	CWPINDEX_MAXVALUE	= 15;

	/**
	 * get cert file index which for device
	 * 
	 * @return -
	 */
	private int getNextIndex() {
		int index = 0;
		List<?> indexList = QueryUtil.executeQuery("select index from "
				+ CwpCertificate.class.getSimpleName(), null, null, getDomainId());

		if (indexList == null || indexList.size() == 0) {
			return 0;
		}

		for (; index < Integer.MAX_VALUE; index++) {
			if (!indexList.contains(index)) {
				break;
			}
		}

		return index;
	}

	/**
	 * check whether exists duplicate file name
	 * 
	 * @param certName -
	 * @return -
	 */
	private boolean checkCertName(String certName) {
		List<?> nameList = QueryUtil.executeQuery("select certName from " + CwpCertificate.class.getSimpleName(), null,
				new FilterParams("id != :s1", new Object[] { dataSource.getId() }), getDomainId());
		for (Object obj : nameList) {
			String name = (String) obj;
			if (certName.equalsIgnoreCase(name)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * check whether exists duplicate index
	 * 
	 * @param index -
	 * @return -
	 */
	private boolean checkIndexValid(int index) {
		List<?> indexList = QueryUtil.executeQuery("select index from " + CwpCertificate.class.getSimpleName(), null,
				new FilterParams("id != :s1", new Object[] { dataSource.getId() }), getDomainId());
		for (Object obj : indexList) {
			Integer int_ = (Integer) obj;
			if (int_ == index) {
				return false;
			}
		}

		return true;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_CERTNAME		= 1;

	public static final int	COLUMN_SRCCERTNAME	= 2;

	public static final int	COLUMN_SRCKEYNAME	= 3;

	public static final int	COLUMN_ENCRYPTED	= 4;

	public static final int	COLUMN_DECRIPTION	= 5;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_CERTNAME:
			code = "config.cwpCert.certName";
			break;
		case COLUMN_SRCCERTNAME:
			code = "config.cwpCert.cert";
			break;
		case COLUMN_SRCKEYNAME:
			code = "config.cwpCert.privateKey";
			break;
		case COLUMN_ENCRYPTED:
			code = "config.cwpCert.encrypted";
			break;
		case COLUMN_DECRIPTION:
			code = "config.cwpCert.description";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();

		columns.add(new HmTableColumn(COLUMN_CERTNAME));
		columns.add(new HmTableColumn(COLUMN_SRCCERTNAME));
		columns.add(new HmTableColumn(COLUMN_SRCKEYNAME));
		columns.add(new HmTableColumn(COLUMN_ENCRYPTED));
		columns.add(new HmTableColumn(COLUMN_DECRIPTION));

		return columns;
	}

	public static final int	KEYSIZE_512		= 1;

	public static final int	KEYSIZE_1024	= 2;

	public static final int	KEYSIZE_2048	= 3;

	public static EnumItem[] getEnumKeySize() {
		return MgrUtil.enumItems("enum.cert.keySize.", new int[] { KEYSIZE_512, KEYSIZE_1024,
				KEYSIZE_2048 });
	}

	public List<String> getAvailableCaFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile || listFile.size() == 0) {
			listFile = new ArrayList<String>();
			listFile.add("");
		}
		return listFile;
	}

	public List<String> getAvailableKeyFile() {
		List<String> listFile = HmBeAdminUtil.getCAFileList(getDomain().getDomainName());
		if (null == listFile) {
			listFile = new ArrayList<String>();
		}
		listFile.add(0, "");
		return listFile;
	}

	private List<CwpCertificate> getSelectCertList() {

		try {
			List<CwpCertificate> certList = new ArrayList<CwpCertificate>(getAllSelectedIds()
					.size());
			for (Long id : getAllSelectedIds()) {
				certList.add((CwpCertificate) findBoById(boClass, id, this));
			}

			return certList;

		} catch (Exception e) {
			log.error("getSelectCertList", "catch exception", e);
			return null;
		}
	}

	// struts download support
	private CwpCertificate getSelectCert() {

		try {
			CwpCertificate cwpCert = null;
			for (Long id : getAllSelectedIds()) {
				cwpCert = (CwpCertificate) findBoById(boClass, id, this);
			}

			return cwpCert;

		} catch (Exception e) {
			log.error("getSelectCert", "failed to find cwp cert bo which id is "
					+ getSelectedId(), e);
			return null;
		}
	}

	public String getLocalFileName() {
		return certFileName + ".pem";
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(inputPath);
	}

	// struts download support -- end

	public String getDisplayName() {
		return getDataSource().getCertName().replace("\\", "\\\\").replace("'", "\\'");
	}

	public String getCreateCertWay() {
		return createCertWay;
	}

	public void setCreateCertWay(String createCertWay) {
		this.createCertWay = createCertWay;
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

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getCertificateFile() {
		return certificateFile;
	}

	public void setCertificateFile(String certificateFile) {
		this.certificateFile = certificateFile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		bo.getOwner().getDomainName();
		return null;
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return getDataSource().getDefaultFlag() ? "disabled" : "";
		}
		return "disabled";
	}

	public String getCertName_concate() {
		return certName_concate;
	}

	public void setCertName_concate(String certName_concate) {
		this.certName_concate = certName_concate;
	}

	public String getCertName_new() {
		return certName_new;
	}

	public void setCertName_new(String certName_new) {
		this.certName_new = certName_new;
	}

	public String getDescription_concate() {
		return description_concate;
	}

	public void setDescription_concate(String description_concate) {
		this.description_concate = description_concate;
	}

	public String getDescription_new() {
		return description_new;
	}

	public void setDescription_new(String description_new) {
		this.description_new = description_new;
	}

	private String getInputResult() {
		if (this.getLastExConfigGuide() != null){
			 return "inputEx";
		} else {
			return isJsonMode()? "inputJson" : INPUT;
		}
	}
	
	private String getErrorInputResult() {
		if (this.getLastExConfigGuide() != null) {
			return "inputEx";
		} else {
			return isJsonMode() ? (isParentIframeOpenFlg() ? "inputJson" : "json") : INPUT;
		}
	}
	
	private String getEditResult() {
		if (this.getLastExConfigGuide() != null){
			return "editEx";
		} else {
			return isJsonMode()? "editJson" : "edit";
		}
	}
	
	private String getErrorEditResult() {
		if (this.getLastExConfigGuide() != null) {
			return "editEx";
		} else {
			return isJsonMode() ? (isParentIframeOpenFlg() ? "editJson" : "json") : INPUT;
		}
	}

}