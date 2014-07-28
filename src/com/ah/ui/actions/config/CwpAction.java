package com.ah.ui.actions.config;

/*
 * @author Fisher
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.validator.constraints.Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeEventUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.db.configuration.ConfigurationChangedEvent;
import com.ah.be.os.BeNoPermissionException;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.os.FileManager;
import com.ah.be.resource.BeResModule_CWPImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.Vlan;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.CwpCertificate;
import com.ah.bo.wlan.CwpPageCustomization;
import com.ah.bo.wlan.CwpPageField;
import com.ah.bo.wlan.CwpPageFieldComparator;
import com.ah.bo.wlan.CwpPageMultiLanguageRes;
import com.ah.bo.wlan.SsidProfile;
import com.ah.bo.wlan.WalledGardenItem;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApFileAction;
import com.ah.util.CheckItem;
import com.ah.util.CreateObjectAuto;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.NetTool;
import com.ah.util.Tracer;
import com.ah.util.compress.tar.TarArchive;
import com.ah.util.io.UnicodeFormatter;

public class CwpAction extends BaseAction implements QueryBo {

	private static final int MAXIMUM_USE_POLICY_SIZE = 8000;

    private final static short STATE_ORIGINAL = 1;
	
	private final static short STATE_CURRENT = 2;
	
	private static final long serialVersionUID = 1L;
	
	private final static short RESOURCE_TYPE_HEAD_IMAGE = 1;
	
	private final static short RESOURCE_TYPE_FOOT_IMAGE = 2;
	
	private final static short RESOURCE_TYPE_USER_POLICY = 3;
	
	private final static short RESOURCE_TYPE_SUCCESS_HEAD_IMAGE = 4;
	
	private final static short RESOURCE_TYPE_SUCCESS_FOOT_IMAGE = 5;
	
	private final static short RESOURCE_TYPE_BACKGROUND_IMAGE = 6;
	
	private final static short RESOURCE_TYPE_SUCCESS_BACKGROUND_IMAGE = 7;
	
	private final static short RESOURCE_TYPE_FAILURE_HEAD_IMAGE = 8;
	
	private final static short RESOURCE_TYPE_FAILURE_FOOT_IMAGE = 9;
	
	private final static short RESOURCE_TYPE_FAILURE_BACKGROUND_IMAGE = 10;
	
	private final static short PAGE_INDEX = 1;
	
	private final static short PAGE_SUCCESS = 2;
	
	private final static short PAGE_USER_POLICY = 3;
	
	private final static short PAGE_AUTHENTICATION = 4;
	
	private final static short PAGE_REGISTRATION = 5;
	
	private final static short PAGE_BOTH = 6;
	
	private final static short PAGE_EULA = 7;
	
	private final static short PAGE_FAILURE = 8;
	
	private final static short PAGE_PPSK = 9;
	
	private final static String DEFAULT_IMAGE_DIRECTORY = "images";
	
	private final static String DEFAULT_RESORUCE_DIRECTORY = "resources";
	
	private final static String DEFAULT_RESORUCE_CWP_DIRECTORY = "cwp";
	
	public final static String DEFAULT_DOMAINS_DIRECTORY = "domains";
	
	private final static String DEFAULT_RESOURCE_PATH = AhDirTools.getHmRoot() + 
															DEFAULT_RESORUCE_DIRECTORY + File.separator + 
															DEFAULT_RESORUCE_CWP_DIRECTORY + File.separator;
	
	private final static String DEFAULT_IMAGE_PATH = AhDirTools.getHmRoot() + 
															DEFAULT_IMAGE_DIRECTORY + File.separator + 
															DEFAULT_RESORUCE_CWP_DIRECTORY + File.separator;

	private final static String HTML_TEMPLATE_AUTHENTICATION = "authentication.html";
	
	private final static String HTML_TEMPLATE_REGISTRATION = "registration.html";
	
	private final static String HTML_TEMPLATE_BOTH = "auth-reg.html";
	
	private final static String HTML_TEMPLATE_EULA = "eula.html";
	
	private final static String HTML_TEMPLATE_SUCCESS = "success.html";
	
	public final static String HTML_TEMPLATE_SUCCESS_OLD = "success-old.html";
	
	public final static String HTML_SUCCESS_OLD = "s-old.html";
	
	public final static String HTML_SUCCESS_NEW = "s-new.html";
	
	private final static String HTML_TEMPLATE_FAILURE = "failure.html";
	
	public final static String HTML_TEMPLATE_PPSK = "ppsk_index.html";
	
	public final static String HTML_TEMPLATE_PPSK_AUTH = "ppsk_auth_index.html";
	
	private final static String HTML_TEMPLATE_USER_POLICY = "use-policy.html";
	
	private final static String PAGE_ELEMENT_H1_TITLE = "h1Title";
	
	private final static String PAGE_ELEMENT_BACKGROUND = "background-image: url();";
	
	private final static String PAGE_ELEMENT_TILE = "tile-image";
	
	private final static String PAGE_ELEMENT_FOREGROUND = "color: ;";
	
	private final static String PAGE_ELEMENT_HEAD_IMAGE = "headImage";
	
	private final static String PAGE_ELEMENT_FOOT_IMAGE = "footImage";
	
	private final static String PAGE_ELEMENT_USER_POLICY = "userPolicy";
	
	private final static String PAGE_ELEMENT_NOTICE = "notice\">";
	
	private final static String PAGE_ELEMENT_SIP_OK = "ok\">";
	
	private final static String PAGE_ELEMENT_SIP_FINES = "fines\">";

	private final static String PAGE_ELEMENT_SIP_BLOCK = "sipDeny\">";
	
	private final static String PAGE_ELEMENT_PAUSE_TIME = "pause-time";
	
	private final static String PAGE_ELEMENT_TRANSFER_URL = "transfer-url";
	
	private final static String PAGE_ELEMENT_REGISTRATION_FIELDS = "registrationFields";
	
	private final static String PAGE_ELEMENT_PPSK_REG_FIELD_VISITING = "visitingField";
	
	private final static String AUTO_GENERATED_PAGE_AUTHENTICATION = "authentication.html";
	
	private final static String AUTO_GENERATED_PAGE_REGISTRATION = "registration.html";
	
	private final static String AUTO_GENERATED_PAGE_BOTH = "auth-reg.html";
	
	private final static String AUTO_GENERATED_PAGE_EULA = "eula.html";
	
	public final static String AUTO_GENERATED_PAGE_PPSK = "ppsk_index.cgi";
	
	private final static String AUTO_GENERATED_RESULT_PAGE = "success.html";
	
	private final static String AUTO_GENERATED_FAILURE_PAGE = "failure.html";
	
	private final static String AUTO_GENERATED_USER_POLICY = "use-policy.html";
	
	public final static String USE_POLICY_FILE_SUFFIX = ".usepolicy";
	
	public final static String DEFAULT_CWP_WEB_PAGES = "Default-CWP-Pages.tar.gz";
	
	private String domainName = "";

	private final static String PAGE_ELEMENT_REGISTRATION_COUNTRYLIST = "idmCountryList";
	private final static String PAGE_ELEMENT_REGISTRATION_COUNTRYLIST_SNIPPET = "countrycodeSnippet.html";
	private final static String DEFAULT_RESORUCE_IDM_DIRECTORY = "idm";
	
	private static final Tracer log = new Tracer(CwpAction.class
			.getSimpleName());
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_REGISTRATION_TYPE = 2;
	
	public static final int COLUMN_REGISTRATION_PERIOD = 3;
	
	public static final int COLUMN_AUTHENTICATION_METHOD = 4;
	
	public static final int COLUMN_SESSION_TIMER = 5;
	
	public static final int COLUMN_DEFAULT_NETWORK_SETTING = 6;
	
	public static final int COLUMN_ENABLE_HTTPS = 7;
	
	public static final int COLUMN_DESCRIPTION = 8;

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {			
			// get the domain name for select
			if ("".equals(domainName)) {
				domainName = findBoById(HmDomain.class, domainId).getDomainName();
			}
			
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.cwp"))) {
					return getLstForward();
				}
				setSessionDataSource(new Cwp());
				if ("userPolicy".equalsIgnoreCase(typeFromSsid)){
					getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_EULA);
					getDataSource().setSelfRegOnly(typeFromSsid);
				}
				if ("ppskECwp".equalsIgnoreCase(typeFromSsid)){
					getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_PPSK);
					getDataSource().setSelfRegOnly(typeFromSsid);
					getDataSource().setLoginDisplayStyle("");
				}
				getDataSource().setPageCustomization(new CwpPageCustomization(false, false));
				prepareForView();
				//initRadioValue();
				return getInputResult();
			} else if("createCwpFromSSID".equals(operation)) {
				
				setSessionDataSource(new Cwp());
				
				if(this.ssidId != null) {
				    if(bindTarget == CWP_BIND_TARGET_LAN) {
				        PortAccessProfile access = QueryUtil.findBoById(PortAccessProfile.class, ssidId);
				        if(null != access && access.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
				            idmSelfReg = access.isEnabledIDM();
				        }
				    } else if (bindTarget == CWP_BIND_TARGET_SSID) {
				        SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId);
				        if(ssid != null) {
				            int accessMode = ssid.getAccessMode();
				            getDataSource().setSsidAccessMode(accessMode);
				            idmSelfReg = ssid.isEnabledIDM();
				            
				            if(accessMode == SsidProfile.ACCESS_MODE_PSK) {
				                if(this.isPpskCwp()) {
				                    getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_PPSK);
				                } else {
				                    getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_EULA);
				                }
				                
				                this.setEnableRegistrationType("none");
				            } else if( accessMode == SsidProfile.ACCESS_MODE_8021X
				                    || (accessMode == SsidProfile.ACCESS_MODE_WEP
				                    && ssid.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP)) {
				                getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_EULA);
				                this.setEnableRegistrationType("none");
				            }
				        }
				    }
				}
				
				getDataSource().setPageCustomization(new CwpPageCustomization(idmSelfReg, getIDMPPSKSelfRegFlag()));
				prepareForView();
				
				return "cwpDrawer";
			} else if ("create".equals(operation)) {
				if (checkNameExists("cwpName", getDataSource().getCwpName())) {
					updateWebPageSettings();
					prepareForView();
					
					if(isJsonMode() &&  !isContentShownInSubDrawer()){
						jsonObject = new JSONObject();
						jsonObject.put("ok", false);
						jsonObject.put("msg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getCwpName()));
						return "json";
					}
					
					return getInputResult();
				}
				
				//set the flag that identify sef reg from idm
				if(idmSelfReg){
					if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH
					        || (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK
					        && getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG)){
						getDataSource().setIdmSelfReg(true);
					}else{
						getDataSource().setIdmSelfReg(false);
					}
				}else{
					getDataSource().setIdmSelfReg(false);
				}
				
				if (isNameImported(getDataSource().getCwpName())) {
					addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.nameImported",
							getDataSource().getCwpName()));
					updateWebPageSettings();
					prepareForView();
					
					if(isJsonMode() && !isContentShownInSubDrawer()){
						jsonObject = new JSONObject();
						jsonObject.put("ok", false);
						jsonObject.put("msg", MgrUtil.getUserMessage("error.config.cwp.page.customization.nameImported", getDataSource().getCwpName()));
						return "json";
					}
					return getInputResult();
				}
				
				prepareSubmitData();
				
				// check the cwp server key file
				if (getDataSource().getEnabledHttps() 
						&& getDataSource().getCertificate() != null) {
					if(!HmBeAdminUtil.verifycpskey(AhDirTools.getCwpServerKeyDir(domainName)  
								+ getDataSource().getCertificate().getCertName())) {
						addActionError(MgrUtil.getUserMessage("error.file.upload.fail.cwp.server.key.invalid",
							getDataSource().getCertificate().getCertName()));
						updateWebPageSettings();
						prepareForView();
						return getInputResult();
					}
				}
				
				if(!createCwpPage(getDataSource(), true)) {
					updateWebPageSettings();
					prepareForView();
					return getInputResult();
				}

				id=createBo(dataSource);
				
				if(isJsonMode()) {
					/*
					 * set cwp to SSID
					 */
					jsonObject = new JSONObject();
					
					if(bindTarget == CWP_BIND_TARGET_SSID) { // for SSID
						SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId, this);
						changeSSIDByCWP(ssid, (Cwp)dataSource);
					} else if(bindTarget == CWP_BIND_TARGET_LAN) { // for LAN
					    PortAccessProfile acc = QueryUtil.findBoById(PortAccessProfile.class, ssidId, this);
						changeAccessByCWP(acc, (Cwp)dataSource);
					} else {
						jsonObject.put("ok", true);
						jsonObject.put("parentDomID", getParentDomID());
						jsonObject.put("id", id);
						jsonObject.put("name", getDataSource().getCwpName());
					}
					
					return "json";
				} else {
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				if (this.getLastExConfigGuide()!=null
						&& null != dataSource 
						&& null != dataSource.getId()
						&& dataSource.getId().compareTo(id) == 0){
					// For the config-guided, if page redirect back to the current edit SSID get data from session  
					getSessionDataSource();
				} else {
					editBo(this); 
				}
					
				addLstTitle(getText("config.title.cwp.edit") + " '"
						+ getChangedCwpName() + "'");
				if (dataSource == null) {
					return prepareBoList();
				} else {
				    getDataSource().setPreviousRegType(getDataSource().getRegistrationType());
				    
	                if(this.ssidId != null) {
	                    if(bindTarget == CWP_BIND_TARGET_LAN) {
	                        PortAccessProfile access = QueryUtil.findBoById(PortAccessProfile.class, ssidId);
	                        if(null != access && access.getPortType() == PortAccessProfile.PORT_TYPE_ACCESS) {
	                            idmSelfReg = access.isEnabledIDM();
	                        }
	                    } else if (bindTarget == CWP_BIND_TARGET_SSID) {
	                        SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId);
	                        
	                        if(ssid != null) {
	                            int accessMode = ssid.getAccessMode();
	                            getDataSource().setSsidAccessMode(accessMode);
	                            idmSelfReg = ssid.isEnabledIDM();
	                            
	                            if(accessMode == SsidProfile.ACCESS_MODE_PSK) {
	                                if(this.isPpskCwp()) {
	                                    getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_PPSK);
	                                } else {
	                                    getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_EULA);
	                                }
	                                
	                                this.setEnableRegistrationType("none");
	                            } else if( accessMode == SsidProfile.ACCESS_MODE_8021X
	                                    || (accessMode == SsidProfile.ACCESS_MODE_WEP
	                                    && ssid.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP)) {
	                                getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_EULA);
	                                this.setEnableRegistrationType("none");
	                            }
	                        }
	                    }
					} else {
					    // from the navigation tree
					    idmSelfReg = getDataSource().isIdmSelfReg();
					}
					prepareForView();
					
					return getInputResult();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				Cwp profile = (Cwp) findBoById(boClass, cloneId, this);
				profile.setCwpName("");
				profile.setId(null);
				profile.setOwner(null);
				profile.setVersion(null);
				
				/*
				 * clone walled garden
				 */
				List<WalledGardenItem> items = new ArrayList<WalledGardenItem>();
				
				for(WalledGardenItem item : profile.getWalledGarden()) {
					items.add(item);
				}
				
				profile.setWalledGarden(items);
				
				setSessionDataSource(profile);
				prepareForView();
				return getInputResult();
			} else if ("update".equals(operation)) {
				if (dataSource != null) {
					prepareSubmitData();
					
					//set the flag that identify sef reg from idm
					if(idmSelfReg){
						if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH
						        || (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK
						        && getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG)){
							getDataSource().setIdmSelfReg(true);
						}else{
							getDataSource().setIdmSelfReg(false);
						}
					}else{
						getDataSource().setIdmSelfReg(false);
					}
					
					// check the cwp server key file
					if (getDataSource().getEnabledHttps() 
							&& getDataSource().getCertificate() != null) {
						if(!HmBeAdminUtil.verifycpskey(AhDirTools.getCwpServerKeyDir(domainName)  
								+ getDataSource().getCertificate().getCertName())) {
							addActionError(MgrUtil.getUserMessage("error.file.upload.fail.cwp.server.key.invalid",
								getDataSource().getCertificate().getCertName()));
							updateWebPageSettings();
							prepareForView();
							return getInputResult();
						}
					}
				}
				
				if(!createCwpPage(getDataSource(), true)) {
					updateWebPageSettings();
					prepareForView();
					return getInputResult();
				}
				
				String updateResult = updateBo();
				
				if(isJsonMode()) {
					jsonObject = new JSONObject();
					jsonObject.put("ok", true);
					return "json";
				} else {
					return updateResult;
				}
			} else if (("update"+ getLstForward()).equals(operation)) {
				if (dataSource != null) {
					// check the cwp server key file
					if (getDataSource().getEnabledHttps() 
							&& !HmBeAdminUtil.verifycpskey(AhDirTools.getCwpServerKeyDir(domainName) 
									+ getDataSource().getCertificate().getCertName())) {
						addActionError(MgrUtil.getUserMessage("error.file.upload.fail.cwp.server.key.invalid",
							getDataSource().getCertificate().getCertName()));
						updateWebPageSettings();
						prepareForView();
						return getInputResult();
					}
					prepareSubmitData();
				}
				if(!createCwpPage(getDataSource(), true)) {
					updateWebPageSettings();
					prepareForView();
					return getInputResult();
				}
				
				if(idmSelfReg){
					if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH
					        || (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK
					        && getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG)){
						getDataSource().setIdmSelfReg(true);
					}else{
						getDataSource().setIdmSelfReg(false);
					}
				}else{
					getDataSource().setIdmSelfReg(false);
				}
				updateBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("cwpName", getDataSource().getCwpName())) {
					updateWebPageSettings();
					prepareForView();
					return getInputResult();
				}
				
				if (isNameImported(getDataSource().getCwpName())) {
					addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.nameImported",
						getDataSource().getCwpName()));
					updateWebPageSettings();
					prepareForView();
					return getInputResult();
				}
				
				prepareSubmitData();
				
				// check the cwp server key file
				if (getDataSource().getEnabledHttps() 
						&& getDataSource().getCertificate() != null) {
					if(!HmBeAdminUtil.verifycpskey(AhDirTools.getCwpServerKeyDir(domainName)  
								+ getDataSource().getCertificate().getCertName())) {
						addActionError(MgrUtil.getUserMessage("error.file.upload.fail.cwp.server.key.invalid",
							getDataSource().getCertificate().getCertName()));
						updateWebPageSettings();
						prepareForView();
						return getInputResult();
					}
				}
				
				if(!createCwpPage(getDataSource(), true)) {
					updateWebPageSettings();
					prepareForView();
					return getInputResult();
				}
				
				//set the flag that identify sef reg from idm
				if(idmSelfReg){
					if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH){
						getDataSource().setIdmSelfReg(true);
					}else{
						getDataSource().setIdmSelfReg(false);
					}
				}else{
					getDataSource().setIdmSelfReg(false);
				}

				id = createBo(dataSource);
				setUpdateContext(true);
				return getLstForward();
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("viewFile".equals(operation)) {
				jsonArray = new JSONArray();
				if (name.contains("cwp&directory")) {
					String directoryName = name.substring(name.indexOf("cwp&directory")+"cwp&directory".length());
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", "directoryName");
					jsonObject.put("v", getAvailableCwpDirs());
					jsonArray.put(jsonObject);
					jsonObject = new JSONObject();
					jsonObject.put("id", "pageName");
					if (getText("config.optionsTransfer.none").equals(directoryName)) {
						jsonObject.put("v", getAvailableCwpFiles());
					} else {
						jsonObject.put("v", getCwpFiles(directoryName));
					}
					jsonArray.put(jsonObject);
				} else if ("cwp&resource".equals(name)) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", "backgroundImage");
					jsonObject.put("v", getAvailableBackgroundImages());
					jsonArray.put(jsonObject);
//					jsonObject = new JSONObject();
//					jsonObject.put("id", "headImage");
//					jsonObject.put("v", getAvailableHeadImages());
//					jsonArray.put(jsonObject);
					jsonObject = new JSONObject();
					jsonObject.put("id", "footImage");
					jsonObject.put("v", getAvailableFootImages());
					jsonArray.put(jsonObject);
					jsonObject = new JSONObject();
					jsonObject.put("id", "userPolicy");
					jsonObject.put("v", getAvailableUserPolicies());
					jsonArray.put(jsonObject);
				} else {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("id", "pageName");
					jsonObject.put("v", getCwpFiles(name));
					jsonArray.put(jsonObject);
				}
				return "json";
			} else if ("addKeyFile".equals(operation)
					|| "editKeyFile".equals(operation)
					|| "newVlanId".equals(operation)
					|| "editVlanId".equals(operation)
					|| "newWGIp".equals(operation)
					|| "editWGIp".equals(operation)
					|| "newSuccessExternalURL".equals(operation)
					|| "editSuccessExternalURL".equals(operation)
					|| "newFailureExternalURL".equals(operation)
					|| "editFailureExternalURL".equals(operation)) {
				
				clearErrorsAndMessages();
				
				if("newPrimaryIp".equals(operation)
						|| "editPrimaryIp".equals(operation)) {
					addLstForward("cwpPrimaryIp");
				} else if("newSecondaryIp".equals(operation)
						|| "editSecondaryIp".equals(operation)) {
					addLstForward("cwpSecondaryIp");
				} else if("newWGIp".equals(operation)
						|| "editWGIp".equals(operation)) {
					getDataSource().setTempService(serviceId);
					getDataSource().setTempProtocol(this.protocolNumber);
					getDataSource().setTempPort(this.port);
					getDataSource().setHideCreateItem("");
					getDataSource().setHideNewButton("none");
					addLstForward("cwpWGIp");
				} else if("newSuccessExternalURL".equals(operation)
						|| "editSuccessExternalURL".equals(operation)){
					addLstForward("cwpSuccessURL");
				} else if("newFailureExternalURL".equals(operation)
						|| "editFailureExternalURL".equals(operation)){
					addLstForward("cwpFailureURL");
				} else {
					addLstForward("newCwp");
				}
				
				getDataSource().setCurrentOperation(operation);
				prepareSubmitData();
				return operation;
			} else if ( operation != null &&
					((operation.contains("Image") && operation.contains("add"))
					|| "addUserPolicy".equals(operation))) {
				/*
				 * operation: addBackgroundImage, addHeadImage, addFootImage
				 *            addSuccessBackgroundImage, 
				 *            addSuccessHeadImage, 
				 *            addSuccessFootImage
				 */
				clearErrorsAndMessages();
				addLstForward("cwpPageCustom");
				prepareSubmitData();
				return "addPageResource";
			} else if ("previewIndexPage".equals(operation)
					|| "previewSuccessPage".equals(operation)
					|| "previewPpskPage".equals(operation)
					|| "previewFailurePage".equals(operation)) {
			    
			    previewLanguage = customizeLanguage;
			    
				clearErrorsAndMessages();
				setUpdateContext(true);
				submitCustomizationData();
//				prepareSubmitData();
				return operation;
			} else if("refreshMultiRes".equals(operation)){
			//	JSONObject jsonObject = new JSONObject();
				Integer language=Integer.parseInt(request.getParameter("languageId"));
				jsonObject=getRefreshMultiLanguageRes(language);
				return "json";
			} else if("saveMultiRes".equals(operation)){
				jsonObject = new JSONObject();
				jsonObject.put("ok", saveMultiLanguageRes());
				return "json";
			} else if("resetOneRes".equals(operation)){
			//	JSONObject jsonObject = new JSONObject();
				Integer language=Integer.parseInt(request.getParameter("languageId"));
				jsonObject=resetOneLanguageRes(language);
				return "json";
			}
			
			else if ("previewIndexPageMultiLan".equals(operation)
					|| "previewSuccessPageMultiLan".equals(operation)
					|| "previewPpskPageMultiLan".equals(operation)
					|| "previewFailurePageMultiLan".equals(operation)) {
				
				return removeMulitSuffix(operation);
			}
			else if ("customizePage".equals(operation)) {
			    jsonObject = new JSONObject();
			    
				clearErrorsAndMessages();
				setUpdateContext(true);
				
				if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA) {
				    // check the use policy text length
				    if(null != getDataSource() && null != getDataSource().getPageCustomization()) {
				        final Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes = getDataSource().getPageCustomization().getMultiLanguageRes();
                        for (Integer lanuage: multiLanguageRes.keySet()) {
				            CwpPageMultiLanguageRes res = multiLanguageRes.get(lanuage);
				            if(res.getUserPolicy().length() > MAXIMUM_USE_POLICY_SIZE) {
                                jsonObject.put("msg",
                                                MgrUtil.getUserMessage(
                                                        "error.config.cwp.page.customization.usepolicy.limitation",
                                                        Integer.toString(MAXIMUM_USE_POLICY_SIZE)) + " in "
                                                        +MgrUtil.getEnumString("enum.cwp.multilanguage."+Integer.toString(lanuage)));
				                return "json";
				            }
				        }
				    }
				}
				
				submitCustomizationData();
//				prepareSubmitData();
				
				setId(dataSource.getId());
				setTabId(getLstTabId());
				removeLstTabId();
				prepareForView();
				
				getDataSource().setPreviousRegType(getDataSource().getRegistrationType());
				
				jsonObject.put("ok", true);
				return "json";
			} else if ("openCustomizePage".equals(operation)) {
				clearErrorsAndMessages();
				setUpdateContext(true);
				prepareCustomizationData();
				return operation;
			} else if ("continue".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					prepareForView();
					
					if (getUpdateContext()) {
						removeLstTitle();
						removeLstForward();
						removeLstTabId();
						setUpdateContext(false);
					} 
					
					return getInputResult();
				}
			} else if ("previewReturn".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					setId(dataSource.getId());
					setTabId(getLstTabId());
					removeLstTabId();
					refreshFields = false;
					prepareCustomizationData();
					return "openCustomizePage";
				}
			} else if ("exportWebPages".equals(operation)) {
				prepareSubmitData();
				return operation;
			} else if ("addWGItem".equals(operation)) {
				addWalledGardenItem();	
//				prepareForView();
//				return getInputResult();
				return "json";
			} 
			/* CWP Multi Language support */
			else if("updateLanguageList".equals(operation)) {
				updateLanguageList();	
//				prepareForView();
//				return getInputResult();
				return "json";
			}
			else if ("removeWGItems".equals(operation)
					|| "removeWGItemsNone".equals(operation)) {
				if("removeWGItemsNone".equals(operation)) {
					getDataSource().setHideCreateItem("");
					getDataSource().setHideNewButton("none");
				} else {
					getDataSource().setHideCreateItem("none");
					getDataSource().setHideNewButton("");
				}
				
				removeWalledGardenItems();
//				prepareForView();
//				return getInputResult();
				return "json";
			} else if("cwpListDialog".equals(operation)) {
				
				return "cwpListDialog";
			} else if("setCwpToSSID".equals(operation)) {
				setCwpToSSID();
				return "json";
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

    private boolean getIDMPPSKSelfRegFlag() {
        if (bindTarget == CWP_BIND_TARGET_SSID && idmSelfReg
                && getDataSource().getSsidAccessMode() == SsidProfile.ACCESS_MODE_PSK
                && getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK
                && getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG) {
            return true;
        }
        return false;
    }

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CAPTIVE_PORTAL_WEB);
		setDataSource(Cwp.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_AUTHENTICATION_CWP;
	}	

	@Override
	public boolean baseOperation() throws Exception {
		/* first, call method in super class
		 * and then, remove created directory
		 */
		
		/*
		 * save the names of objects
		 * they will be deleted in super.baseOperation()
		 */
		Set<Long> selectedIds;
		List<String> names = null;
		
		if("remove".equals(operation)) {
			selectedIds = new HashSet<Long>();
			selectedIds.addAll(getAllSelectedIds());
			
			names = new ArrayList<String>();
			Cwp item;
			
			// get name from database
			for(Long id : selectedIds) {
				item = (Cwp) findBoById(boClass, id, this);
				
				if(item != null 
						&& item.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_IMPORT
						&& item.getSuccessPageSource() != Cwp.SUCCESS_PAGE_SOURCE_IMPORT
						&& item.getFailurePageSource() != Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
					names.add(item.getCwpName());
				}
				
				/*
				 * delete temp use policy file
				 */
				deleteUsePolicy(item);
			}
		}
		
		boolean result = super.baseOperation();
		
		if(result && "remove".equals(operation) && names != null) {
			HiveApFileAction fileAction = new HiveApFileAction();
			fileAction.setDomainName(domainName);
			
			for(String name : names) {
				fileAction.deleteCwpDirectory(name);
				//remove the new added .tar.gz file that new added from 6.1r4
				if(FileManager.getInstance().existsFile(AhDirTools.getCwpWebDir(domainName) + name + TarArchive.SUFFIX_TAR_ZIP)){
					FileManager.getInstance().deletefile(AhDirTools.getCwpWebDir(domainName) + name + TarArchive.SUFFIX_TAR_ZIP);
				}
			}
		}
		
		return result;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof Cwp) {
//			dataSource = bo;
			CwpPageCustomization pageCustom = ((Cwp)bo).getPageCustomization();
			
			if(pageCustom.getFields() != null) {
				pageCustom.getFields().size();
			}
			
			if(pageCustom.getMultiLanguageRes() != null) {
				pageCustom.getMultiLanguageRes().size();
			}
			
			if(((Cwp)bo).getCertificate() != null) {
				((Cwp)bo).getCertificate().getId();
			}
			
			if(((Cwp)bo).getVlan() != null) {
				((Cwp)bo).getVlan().getId();
			}
			
			if(((Cwp)bo).getWalledGarden() != null) {
				((Cwp)bo).getWalledGarden().size();
			}
			
			if(((Cwp)bo).getIpAddressSuccess() != null){
				((Cwp) bo).getIpAddressSuccess().getId();
			}
			
			if(((Cwp)bo).getIpAddressFailure() != null){
				((Cwp) bo).getIpAddressFailure().getId();
			}
		}
		if (bo instanceof IpAddress) {
            IpAddress ipAddress = (IpAddress) bo;
            if (ipAddress.getItems() != null)
                ipAddress.getItems().size();
        }
		
		if(bo instanceof PortAccessProfile) {
		    PortAccessProfile accProfile = (PortAccessProfile) bo;
			if(accProfile.getCwp() != null) {
			    accProfile.getCwp().getId();
			}
			if(accProfile.getRadiusUserGroups() != null) {
			    accProfile.getRadiusUserGroups().size();
			}
			if(!accProfile.getAuthOkUserProfile().isEmpty()) {
			    accProfile.getAuthOkUserProfile().size();
			}
			if(!accProfile.getAuthFailUserProfile().isEmpty()) {
			    accProfile.getAuthFailUserProfile().size();
			}
			if(!accProfile.getAuthOkDataUserProfile().isEmpty()) {
			    accProfile.getAuthOkDataUserProfile().size();
			}
		}
		if(bo instanceof SsidProfile) {
			SsidProfile ssidProfile = (SsidProfile) bo;
			if(ssidProfile.getRadiusUserProfile() != null) {
				ssidProfile.getRadiusUserProfile().size();
			}
			if(ssidProfile.getRadiusUserGroups() != null) {
				ssidProfile.getRadiusUserGroups().size();
			}
		}
		return null;
	}	

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	/**
	 * get the description of column by id
	 *
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.cwp.head.cwpName";
			break;
		case COLUMN_REGISTRATION_TYPE:
			code = "config.cwp.detail.registType";
			break;
		case COLUMN_REGISTRATION_PERIOD:
			code = "config.cwp.head.registrationPeriod";
			break;
		case COLUMN_AUTHENTICATION_METHOD:
			code = "config.cwp.detail.authMethod";
			break;
		case COLUMN_SESSION_TIMER:
			code = "config.cwp.enable.popup";
			break;
		case COLUMN_DEFAULT_NETWORK_SETTING:
			code = "config.cwp.head.useDefaultNetwork";
			break;
		case COLUMN_ENABLE_HTTPS:
			code = "config.cwp.head.enabledHttps";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.security.description";
			break;
		}
		
		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(8);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_REGISTRATION_TYPE));
		columns.add(new HmTableColumn(COLUMN_REGISTRATION_PERIOD));
		columns.add(new HmTableColumn(COLUMN_AUTHENTICATION_METHOD));
		columns.add(new HmTableColumn(COLUMN_SESSION_TIMER));
		columns.add(new HmTableColumn(COLUMN_DEFAULT_NETWORK_SETTING));
		columns.add(new HmTableColumn(COLUMN_ENABLE_HTTPS));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
		
	private void prepareSubmitData() throws Exception {
		Cwp dataSource = getDataSource();
		
		if (dataSource.getServerType() != Cwp.CWP_INTERNAL){
			dataSource.setDhcpMode(Cwp.MODE_BROADCAST);
			dataSource.setLeaseTime(10);
		} else {
			dataSource.setVlan(null);
			dataSource.setWalledGarden(null);
		}
		
		if (dataSource.getUseDefaultNetwork()){
			dataSource.setIpForAMode("");
			dataSource.setMaskForAMode("");
			dataSource.setIpForBGMode("");
			dataSource.setMaskForBGMode("");
			dataSource.setIpForEth0("");
			dataSource.setMaskForEth0("");
			dataSource.setIpForEth1("");
			dataSource.setMaskForEth1("");
		}
		
		if (!dataSource.getEnabledHttps()){
			dataSource.setCertificate(null);
			dataSource.setCertificateDN(false);
		}
		
		updateWebPageSettings();
		prepareLazyInfo();
		updatePPSKServerType();
		
		updateMultiLanguageSupport();
		
		if(isNnuStyle()) {
			if(dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
				dataSource.setDisableRoamingLogin(true);
				dataSource.setEnabledHTTP302(true);
			}
		}
	}
	
	private void updateWebPageSettings() {
		Cwp dataSource = getDataSource();
		
		updateLoginPageSettings();
		updateSuccessPageSettings();
		updateFailurePageSettings();
		
		/*
		 * depaul
		 */
		if(this.isDepaulStyle()) {
			if("reassociate".equals(reassociateRadioType)) {
				dataSource.setNeedReassociate(true);
			} else if("direct-access".equals(reassociateRadioType)) {
				dataSource.setNeedReassociate(false);
			}
		}
	}
	
	private void updateLoginPageSettings() {
		Cwp dataSource = getDataSource();
		
		if(dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL
				&& !dataSource.isPpskServer()) {
			return ;
		}
		
		/*
		 * login page
		 */
		if("customize".equals(webPageRadioType)) { // customize
			dataSource.setWebPageSource(Cwp.WEB_PAGE_SOURCE_AUTOGENERATE);
			//updateUsePolicy(dataSource);
		} else if("import".equals(webPageRadioType)) { // import
			dataSource.setWebPageSource(Cwp.WEB_PAGE_SOURCE_IMPORT);
		} 
	}
	
	private void updatePPSKServerType() {
		Cwp dataSource = getDataSource();
		
		if(getDataSource().getRegistrationType()
				== Cwp.REGISTRATION_TYPE_PPSK) {
		if("authentication".equals(this.ppskServerRadioType)) { // authentication
			dataSource.setPpskServerType(Cwp.PPSK_SERVER_TYPE_AUTH);
		} else if("registration".equals(this.ppskServerRadioType)) { // registration
			dataSource.setPpskServerType(Cwp.PPSK_SERVER_TYPE_REG);
		} 
		} else {
			dataSource.setPpskServerType(Cwp.PPSK_SERVER_DEFAULT_TYPE);
		}
	}
	
//	private void updateUsePolicy(Cwp cwp) {
//		/*
//		 * save content of use policy into a file
//		 * the file will be referred to in Cwp object
//		 * the file will be deleted if the cwp is deleted
//		 */
//		String fileName = cwp.getCwpName() + USE_POLICY_FILE_SUFFIX;
//		String fileDir = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy"
//				+ File.separator;
//		
//		if(!FileManager.getInstance().existsFile(fileDir)) {
//			FileManager.getInstance().createDirectory(fileDir);
//		}
//		
//		String fileContent = this.getUserPolicy(cwp);
//		
//		if(fileContent == null) {
//			return ;
//		}
//		
//		try {
//			FileManager.getInstance().writeFile(fileDir + fileName, fileContent, false);
//		} catch (IOException e) {
//			
//		}
//		
//		cwp.getPageCustomization().setUserPolicy(fileName);
//	}
	
	private void updateSuccessPageSettings() {
		Cwp dataSource = getDataSource();
		
		/*
		 * success page
		 */
		if(dataSource.isShowSuccessPage()) {
			if("customize".equals(successPageRadioType)) { // customize
				dataSource.setSuccessPageSource(Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE);
				
			} else if("import".equals(successPageRadioType)) { // import
				dataSource.setSuccessPageSource(Cwp.SUCCESS_PAGE_SOURCE_IMPORT);
				dataSource.setDirectoryName(this.successDirectoryName);
			}
		} 
		
		if("no-redirection".equals(this.successRedirection)) { // no redirection
			dataSource.setSuccessRedirection(Cwp.SUCCESS_REDIRECT_NO);
			dataSource.setSuccessExternalURL(null);
		} else if("original".equals(this.successRedirection)) { // original
			dataSource.setSuccessRedirection(Cwp.SUCCESS_REDIRECT_ORIGINAL);
			dataSource.setSuccessExternalURL(null);
		} else if("external".equals(this.successRedirection)) { // external
			dataSource.setSuccessRedirection(Cwp.SUCCESS_REDIRECT_EXTERNAL);
		}
	}
	
	private void updateFailurePageSettings() {
		Cwp dataSource = getDataSource();
		
		/*
		 * failure page
		 */
		if(dataSource.isShowFailurePage()) {
			if("customize".equals(failurePageRadioType)) { // customize
				dataSource.setFailurePageSource(Cwp.FAILURE_PAGE_SOURCE_CUSTOMIZE);
			} else if("import".equals(failurePageRadioType)) { // import
				dataSource.setFailurePageSource(Cwp.FAILURE_PAGE_SOURCE_IMPORT);
				dataSource.setDirectoryName(this.failureDirectoryName);
			}
		} 
		
		if("no-redirection".equals(this.failureRedirection)) { // no redirection
			dataSource.setFailureRedirection(Cwp.FAILURE_REDIRECT_NO);
			dataSource.setFailureExternalURL(null);
		} else if("login".equals(this.failureRedirection)) { // login
			dataSource.setFailureRedirection(Cwp.FAILURE_REDIRECT_LOGIN);
			dataSource.setFailureExternalURL(null);
		} else if("external".equals(this.failureRedirection)) { // external
			dataSource.setFailureRedirection(Cwp.FAILURE_REDIRECT_EXTERNAL);
		}
	}
	
	private void prepareLazyInfo() throws Exception {
		if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
			if(this.isCommonStyle()) {
				if(getDataSource().getPasswordEncryption() != Cwp.PASSWORD_ENCRYPTION_SHARED) {
					getDataSource().setSharedSecret(null);
				} else {
					getDataSource().setAuthMethod(Cwp.AUTH_METHOD_CHAP);
				}
			}
			
			if (vlanId != null && vlanId != -1) {
				getDataSource().setVlan(findBoById(Vlan.class, vlanId));
			} else {
				getDataSource().setVlan(CreateObjectAuto.createNewVlan(inputVlanValue, 
																		getDomain(), 
																		"For CWP : " + getDataSource().getCwpName()));
			}
			
			if(getDataSource().isPpskServer()) {
				getDataSource().setPasswordEncryption(Cwp.PASSWORD_ENCRYPTION_SHARED);
				getDataSource().setSharedSecret(null);
			}
		} else {
			if(getDataSource().getServerType() == Cwp.CWP_EXTERNAL) {
				if(getDataSource().isOverrideVlan()) {
					if (vlanId != null && vlanId != -1) {
						getDataSource().setVlan(findBoById(Vlan.class, vlanId));
					} else {
						getDataSource().setVlan(CreateObjectAuto.createNewVlan(inputVlanValue,
																			getDomain(), 
																			"For CWP : " + getDataSource().getCwpName()));
					}
				} else {
					getDataSource().setVlan(null);
				}
			} else {
				getDataSource().setOverrideVlan(false);
				getDataSource().setVlan(null);
			}
			
			getDataSource().setLoginURL(null);
			getDataSource().setPasswordEncryption(Cwp.PASSWORD_ENCRYPTION_SHARED);
			getDataSource().setSharedSecret(null);
		}
		
		if(this.certificate != null && this.certificate != -1) {
			getDataSource().setCertificate(findBoById(CwpCertificate.class,
					this.certificate));
			
			if(getDataSource().isCertificateDN()) {
				getDataSource().setServerDomainName(null);
			}
		} else {
			getDataSource().setCertificate(null);
		}
		
		if(getDataSource().getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_EXTERNAL){
			if(getDataSource().getExternalURLSuccessType() == Cwp.EXTERNAL_URL_TAGGED){
				if(this.successExternalURLId != null && this.successExternalURLId != -1){
					getDataSource().setIpAddressSuccess(QueryUtil.findBoById(IpAddress.class, successExternalURLId));
				}else{
					getDataSource().setIpAddressSuccess(null);
				}
				getDataSource().setSuccessExternalURL(null);
			}else{
				getDataSource().setIpAddressSuccess(null);
			}
		}else{
			getDataSource().setSuccessExternalURL(null);
			getDataSource().setIpAddressSuccess(null);
		}
		
		if(getDataSource().getFailureRedirection() == Cwp.FAILURE_REDIRECT_EXTERNAL){
			if(getDataSource().getExternalURLFailureType() == Cwp.EXTERNAL_URL_TAGGED){
				if(this.failureExternalURLId != null && this.failureExternalURLId != -1){
					getDataSource().setIpAddressFailure(QueryUtil.findBoById(IpAddress.class, failureExternalURLId));
				}else{
					getDataSource().setIpAddressFailure(null);
				}
				getDataSource().setFailureExternalURL(null);
			}else{
				getDataSource().setIpAddressFailure(null);
			}
		}else{
			getDataSource().setFailureExternalURL(null);
			getDataSource().setIpAddressFailure(null);
		}
	}
	
	public static String toHexString(int number) {
		String tempString = Integer.toHexString(number);
		
		if(tempString.length() == 1) {
			tempString = "0" + tempString;
		}
		
		return tempString;
	}

	@Override
	public Cwp getDataSource() {
		return (Cwp) dataSource;
	}

	/**
	 * this function is only used when restoring DB backup
	 *
	 * @param cwpDTO -
	 * @author Joseph Chen
	 */
	public void setCwpDataSource(Cwp cwpDTO) {
		dataSource = cwpDTO;
	}

	public EnumItem[] getEnumDhcpMode() {
		return Cwp.ENUM_DHCP_MODE;
	}
	
	public EnumItem[] getEnumAuthMethod() {
		return Cwp.ENUM_AUTH_METHOD;
	}

	public EnumItem[] getEnumRegistrationType() {
		EnumItem[] items;
		
		if(this.isFullMode()) {
			items = Cwp.ENUM_REGISTRATION_TYPE;
		} else {
			items = Cwp.ENUM_REGISTRATION_TYPE_EXPRESS;
		}
		
		if(NmsUtil.isHMForOEM()) { // OEM system
			/*
			 * remove ECWP
			 */
			EnumItem[] oemItems;
			if(this.isFullMode()){
				 oemItems = new EnumItem[items.length - 1];
			}else{
				 oemItems = new EnumItem[items.length];
			}
			
			int i = 0;
			
			for(EnumItem item : items) {
				if(item.getKey() != Cwp.REGISTRATION_TYPE_EXTERNAL) {
					oemItems[i++] = item;
				}
			}
			
			return oemItems;
		}
		
		if(this.bindTarget == CWP_BIND_TARGET_LAN
				|| (this.bindTarget == CWP_BIND_TARGET_SSID
						&& !this.ppskCwp && !this.wpaCwp)) {
		    
		    int accessMode = getDataSource().getSsidAccessMode();
		    if(this.isFullMode()){
    		    if(idmSelfReg) {
    		        if(this.bindTarget == CWP_BIND_TARGET_SSID && accessMode > 0) {
    		            // for IDM
    		            if((accessMode == SsidProfile.ACCESS_MODE_WPA)
    		                    || (accessMode == SsidProfile.ACCESS_MODE_WEP 
    		                    && getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA)) {
    		                return Cwp.ENUM_IDM_REGISTRATION_NOT_OPEN;
    		            } else if (accessMode == SsidProfile.ACCESS_MODE_OPEN) {
    		                return Cwp.ENUM_IDM_REGISTRATION_OPEN;
    		            }
    		        } else if(this.bindTarget == CWP_BIND_TARGET_LAN) {
    		            return Cwp.ENUM_IDM_REGISTRATION_WIRED;
    		        }
    		    }
		    }
			/*
			 * remove PPSK
			 */
			EnumItem[] oemItems;
			if(this.isFullMode()){
				 oemItems = new EnumItem[items.length - 1];
			}else{
				 oemItems = new EnumItem[items.length];
			}
			
			int i = 0;
			
			for(EnumItem item : items) {
				if(item.getKey() != Cwp.REGISTRATION_TYPE_PPSK) {
					oemItems[i++] = item;
				}
			}
			
			return oemItems;
		} else if(this.bindTarget == CWP_BIND_TARGET_SSID
						&& this.wpaCwp) {
			EnumItem[] oemItems;
			if(this.isFullMode()){
				 oemItems = new EnumItem[items.length - 4];
			}else{
				 oemItems = new EnumItem[items.length];
			}
			int i = 0;
			for(EnumItem item : items) {
				if(item.getKey() == Cwp.REGISTRATION_TYPE_AUTHENTICATED 
						|| item.getKey() == Cwp.REGISTRATION_TYPE_EXTERNAL ) {
					oemItems[i++] = item;
				}
			}
			return oemItems;
		}
		
		if(isEasyMode()) {
		    Object obj = MgrUtil.getSessionAttribute(SsidProfile.class.getSimpleName()+ "Source");
		    if(null != obj) {
		        SsidProfile ssid = (SsidProfile) obj;
		        final int accessMode = ssid.getAccessMode();
                if(ssid.isEnabledIDM()) {
                    if(accessMode == SsidProfile.ACCESS_MODE_WPA
                        || accessMode == SsidProfile.ACCESS_MODE_WEP) {
                        getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
                        return Cwp.ENUM_REGISTRATION_TYPE_AUTHONLY;
                    } else if(accessMode == SsidProfile.ACCESS_MODE_OPEN) {
                        return Cwp.ENUM_IDM_REGISTRATION_EXPRESS_OPEN;
                    } else if (accessMode == SsidProfile.ACCESS_MODE_8021X || accessMode == SsidProfile.ACCESS_MODE_PSK) {
                        getDataSource().setRegistrationType(Cwp.REGISTRATION_TYPE_EULA);
                        return Cwp.ENUM_REGISTRATION_TYPE_SELFONLY;
                    }
		        }
		    }
		}
		
		return items;
	}
	
	public Range getRegistrationPeriodRange() {
		return getAttributeRange("registrationPeriod");
	}

	public Range getLeaseTimeRange() {
		return getAttributeRange("leaseTime");
	}

	public Range getNumberFieldRange() {
		return getAttributeRange("numberField");
	}

	public Range getRequestFieldRange() {
		return getAttributeRange("requestField");
	}

	public int getCwpNameLength() {
		return getAttributeLength("cwpName");
	}

	public int getCommentLength() {
		return getAttributeLength("comment");
	}

	public int getSuccessNoticeLength() {
		return CwpPageCustomization.TEXT_NOTICE_LENGTH;
	}

	public String getHideNetwork() {
		if (!getDataSource().getUseDefaultNetwork()) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHideFile() {
		if (getDataSource().getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT) {
			return "";
		} else {
			return "none";
		}
	}

	public String getCustomizePage() {
		if (getDataSource().getWebPageSource() == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
			return "";
		} else {
			return "none";
		}
	}

	public String getCustomizeSuccessPageDisplay() {
		if (getDataSource().getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getImportSuccessPage() {
		if (getDataSource().getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_IMPORT) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getCustomizeFailurePageDisplay() {
		if (getDataSource().getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_CUSTOMIZE) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getImportFailurePage() {
		if (getDataSource().getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowInternal() {
		if (getDataSource().getServerType()==Cwp.CWP_INTERNAL) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowExternal() {
		if (getDataSource().getServerType()==Cwp.CWP_EXTERNAL) {
			return "";
		} else {
			return "none";
		}
	}

	public String getShowExternalCWP() {
		if (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
				return "";				
		} else {
			return "none";
		}
	}

	public String getShowInternalCWP() {
		if (getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL) {
			return "";
		} else {
				return "none";
			}
		}

	public String getShowInternalServer() {
		if (getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL
				&& getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_PPSK) {
			return "";
		} else {
				return "none";
			}
	}

	public String getHideKeyFileList() {
		if(getDataSource().getEnabledHttps()) {
			return ""; 
		} else {
			return "none";
		}
	}

	public String getChangedCwpName() {
		return getDataSource().getCwpName().replace("\\", "\\\\").replace("'",
				"\\'");
	}
	
	public List<String> getAvailableCwpDirs() {
		return HiveApFileAction.getAllCwpDirs(domainName);
	}
	
	public List<String> getAvailableCwpFiles()
	{
		List<String> cwpFile;
		if (null == getAvailableCwpDirs()) {
			cwpFile = new ArrayList<String>();
			cwpFile.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		} else if (null == getDataSource().getDirectoryName() || 
			"".equals(getDataSource().getDirectoryName())
			|| MgrUtil.getUserMessage("config.optionsTransfer.none").equals(getDataSource().getDirectoryName())) {
			cwpFile = getCwpFiles(getAvailableCwpDirs().get(0));
		} else {
			cwpFile = getCwpFiles(getDataSource().getDirectoryName());
		}
		return cwpFile;
	}	

	public List<String> getCwpFiles(String str_Dir) {
		return HiveApFileAction.getAllCwpFiles(domainName, str_Dir);
	}

	public List<String> getAvailableKeyFiles() {
		return HiveApFileAction.getAllKeyFiles(domainName);
	}

	private String name;

//	private JSONArray jsonArray = null;
	
	private String typeFromSsid;

//	public String getJSONString() {
//		return jsonArray.toString();
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getViewFile() {
		return "viewFile";
	}
	
	public List<String> getAvailableBackgroundImages() {
		List<String> backgroundImages = new ArrayList<String>();
		List<String> uploadedResources = getAvailablePageResources();
		
		backgroundImages.addAll(uploadedResources);
		
		// add default page resources
		if(!NmsUtil.isHMForOEM()){
			backgroundImages.add(CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_LIGHT);
			backgroundImages.add(CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_DARK);
		}
		backgroundImages.add(CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D);
		
		return backgroundImages; 
	}
	
	public List<String> getAvailableHeadImages() {
		List<String> headImages = new ArrayList<String>();
		List<String> uploadedResources = getAvailablePageResources();
		
		headImages.addAll(uploadedResources);
		
		// add default page resources
		headImages.add(CwpPageCustomization.DEFAULT_HEAD_IMAGE);
		
		return headImages; 
	}
	
	public List<String> getAvailableFootImages() {
		List<String> footImages = new ArrayList<String>();
		List<String> uploadedResources = getAvailablePageResources();
		
		footImages.addAll(uploadedResources);
		
		// add default page resources
		footImages.add(CwpPageCustomization.DEFAULT_FOOT_IMAGE);
		
		return footImages; 
	}
	
	public List<String> getAvailableUserPolicies() {
		List<String> userPolicies = new ArrayList<String>();
		List<String> uploadedPolicies = getAvailablePageResources();
		
		userPolicies.addAll(uploadedPolicies);
		
		// add default user policies
		userPolicies.add(CwpPageCustomization.DEFAULT_USER_POLICY);
		
		return userPolicies;
	}
	
	public List<String> getAvailableForegroundColor() {
		List<String> colors = new ArrayList<String>();
		
		for(int i=0; i<CwpPageCustomization.FOREGROUND_COLOR.length; i++) {
			colors.add((String)CwpPageCustomization.FOREGROUND_COLOR[i][0]);
		}
		
		return colors;
	}
	
	public String getShowOriginalAuthenticated() {
		return getShowAuthenticated(STATE_ORIGINAL);
	}
	
	public String getShowCurrentAuthenticated() {
		return getShowAuthenticated(STATE_CURRENT);
	}
	
	private String getShowAuthenticated(short state) {
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			return "none"; 
		}
		
		byte regType = bo.getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowAuthenticated() {
		byte regType = getDataSource().getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowAuthOrPPSK() {
		byte regType = getDataSource().getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			return "";
		} else if(regType == Cwp.REGISTRATION_TYPE_PPSK) {
			if(getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_AUTH) {
				return "";
			} else {
				return "none";
			}
		} else {
			return "none";
		}
	}
	
	public String getShowH1Title() {
		return NmsUtil.isHMForOEM() ? "none" : "";
	}
	
	public String getShowAuthenticateMethod() {
		byte regType = getDataSource().getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			return "";
		} else if(regType == Cwp.REGISTRATION_TYPE_EXTERNAL) {
				return "";
		} else {
			return "none";
		}
	}
	
	public String getShowPPSKServer() {
		if (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowPPSKServerReg() {
		if (getDataSource().isPpskServer()
				&& getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowUsePolicy() {
		if (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL
				|| getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK
				|| getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
			return "none";
		} else {
			return "";
		}
	}
	
	public String getShowFailurePageSection() {
		if (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA) {
			return "none";
		} else {
			return "";
		}
	}
	
	public String getShowOriginalRegistrated() {
		return getShowRegistered(STATE_ORIGINAL);
	}
	
	public String getShowCurrentRegistrated() {
		return getShowRegistered(STATE_CURRENT);
	}
	
	private String getShowRegistered(short state) {
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			return "none"; 
		}
		
		byte regType = bo.getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_REGISTERED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowRegistrated() {
		byte regType = getDataSource().getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_REGISTERED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowRegistrateCustom() {
		byte regType = getDataSource().getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_REGISTERED
				|| regType == Cwp.REGISTRATION_TYPE_BOTH) {
			
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowOriginalBoth() {
		return getShowBoth(STATE_ORIGINAL);
	}

	public String getShowCurrentBoth() {
		return getShowBoth(STATE_CURRENT);
	}

	public String getShowBothOrPPSK() {
		if(getDataSource().isPpskServer()) {
			if(getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_AUTH) {
			return "";
			}
			else {
				return "none";
			}
		} else {
			return getShowBoth(STATE_CURRENT);	
		}
	}
	
	private String getShowBoth(short state) {
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			return "none"; 
		}
		
		return bo.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH ? "" : "none";
	}
	
	public String getShowNotEula() {
		byte regType = getDataSource().getRegistrationType();
		
		if(regType == Cwp.REGISTRATION_TYPE_EULA) {
			return "none";
		} else {
			return "";
		}
	}
	
	public boolean getShowOriginalEula() {
		return getShowEula(STATE_ORIGINAL);
	}
	
	public boolean getShowCurrentEula() {
		return getShowEula(STATE_CURRENT);
	}
	
	private boolean getShowEula(short state) {
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			return false; 
		}
		
		byte regType = bo.getRegistrationType();

		return regType == Cwp.REGISTRATION_TYPE_EULA;
	}
	
	public EnumItem[] getFieldOrders() {
		if(getDataSource() == null
				|| getDataSource().getPageCustomization() == null
				|| getDataSource().getPageCustomization().getFields() == null) {
			return null;
		}
		
		int size = getDataSource().getPageCustomization().getFields().size();
		EnumItem[] items = new EnumItem[size];
		
		for(int i=0; i<size; i++) {
			items[i] = new EnumItem(i+1, String.valueOf(i+1));
		}
		
		return items;
	}
	
	/*
	 * default; customize; import;
	 */
	private String webPageRadioType;
	
	/*
	 * default; customize; import;
	 */
	private String successPageRadioType;
	
	/*
	 * default; customize; import;
	 */
	private String failurePageRadioType;
	
	/*
	 * authentication; self-registration
	 */
	private String ppskServerRadioType;
	
	
	/**
	 * getter of ppskServerRadioType
	 * @return the ppskServerRadioType
	 */
	public String getPpskServerRadioType() {
		byte type = getDataSource().getPpskServerType() ;
		String radioType;
		
		switch (type) {
		case Cwp.PPSK_SERVER_TYPE_AUTH:
			radioType = "authentication";
			break;
		case Cwp.PPSK_SERVER_TYPE_REG:
			radioType = "registration";
			break;
		default:
			radioType = "authentication";
			break;
		}
		
		return radioType;
	}

	/**
	 * setter of ppskServerRadioType
	 * @param ppskServerRadioType the ppskServerRadioType to set
	 */
	public void setPpskServerRadioType(String ppskServerRadioType) {
		this.ppskServerRadioType = ppskServerRadioType;
	}
	
	/**
	 * getter of webPageRadioType
	 * @return the webPageRadioType
	 */
	public String getWebPageRadioType() {
		byte source = getDataSource().getWebPageSource() ;
		String type = "";
		
		switch (source) {
		case Cwp.WEB_PAGE_SOURCE_AUTOGENERATE:
			type = "customize";
			break;
		case Cwp.WEB_PAGE_SOURCE_IMPORT:
			type = "import";
			break;
		default:
			break;
		}
		
		return type;
	}

	/**
	 * setter of webPageRadioType
	 * @param webPageRadioType the webPageRadioType to set
	 */
	public void setWebPageRadioType(String webPageRadioType) {
		this.webPageRadioType = webPageRadioType;
	}
	
	
	/*
	 * original;internal;external
	 */
	private String successRedirection;
	
	/**
	 * getter of successRedirection
	 * @return the successRedirection
	 */
	public String getSuccessRedirection() {
		byte redirection = getDataSource().getSuccessRedirection();
		String result = "";
		
		switch(redirection) {
		case Cwp.SUCCESS_REDIRECT_NO:
			result = "no-redirection";
			break;
		case Cwp.SUCCESS_REDIRECT_ORIGINAL:
			result = "original";
			break;
		case Cwp.SUCCESS_REDIRECT_EXTERNAL:
			result = "external";
			break;
		default:
			break;
		}
		
		return result;
	}

	/**
	 * setter of successRedirection
	 * @param successRedirection the successRedirection to set
	 */
	public void setSuccessRedirection(String successRedirection) {
		this.successRedirection = successRedirection;
	}

	/*
	 * original;internal;external
	 */
	private String failureRedirection;
	
	/**
	 * getter of failureRedirection
	 * @return the failureRedirection
	 */
	public String getFailureRedirection() {
		byte redirection = getDataSource().getFailureRedirection();
		String result = "";
		
		switch(redirection) {
		case Cwp.FAILURE_REDIRECT_NO:
			result = "no-redirection";
			break;
		case Cwp.FAILURE_REDIRECT_LOGIN:
			result = "login";
			break;
		case Cwp.FAILURE_REDIRECT_EXTERNAL:
			result = "external";
			break;
		default:
			break;
		}
		
		return result;
	}

	/**
	 * setter of failureRedirection
	 * @param failureRedirection the failureRedirection to set
	 */
	public void setFailureRedirection(String failureRedirection) {
		this.failureRedirection = failureRedirection;
	}

	private List<String> getAvailablePageResources()
	{
		/*
		 * notice
		 * 
		 * files with suffix ".usepolicy" is temporarily created
		 * those files should not be included.  
		 */
		List<String> tempList = HiveApFileAction.getAllPageResources(domainName);
		List<String> fileList = new ArrayList<String>();
		
		for(String file : tempList) {
			if(file.equals(MgrUtil.getUserMessage("config.optionsTransfer.none"))) {
				continue;
			}
			
			if(file.endsWith(USE_POLICY_FILE_SUFFIX)) {
				continue;
			}
			
			fileList.add(file);
		}
		
		return fileList;
	}
	
	private boolean refreshFields = true;
	
	private void prepareCustomizationData() {
		CwpPageCustomization pageCustomization = getDataSource().getPageCustomization();
        if (refreshFields
                && (null == getDataSource().getId() 
                || ((registType == Cwp.REGISTRATION_TYPE_BOTH 
                || registType == Cwp.REGISTRATION_TYPE_REGISTERED
                || (registType == Cwp.REGISTRATION_TYPE_PPSK && getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG)) 
                        && registType != getDataSource().getPreviousRegType()))) {
            // only refresh the fields for IDM enabled
            pageCustomization.initMultiLanguageFields(idmSelfReg && (registType == Cwp.REGISTRATION_TYPE_BOTH), idmSelfReg && (registType == Cwp.REGISTRATION_TYPE_PPSK));
        }
		
		/*
		 * parse R/G/B from HTML color #00FF00
		 */
		String rawColor = pageCustomization.getForegroundColor();
	
		if("Login".equals(this.customPage) || "PPSK".equals(this.customPage)) {
			this.backgroundImage = pageCustomization.getBackgroundImage();
			this.tileBackgroundImage = pageCustomization.getTileBackgroundImage();
			rawColor = pageCustomization.getForegroundColor();
			this.headerImage = pageCustomization.getHeadImage();
			this.footerImage = pageCustomization.getFootImage();
//			this.usePolicy = this.getUserPolicy(STATE_CURRENT);
			this.preparePageFields(getDataSource());
		} else if("Success".equals(this.customPage)) {
			this.backgroundImage = pageCustomization.getSuccessBackgroundImage();
			this.tileBackgroundImage = pageCustomization.getTileSuccessBackgroundImage();
			rawColor = pageCustomization.getSuccessForegroundColor();
			this.headerImage = pageCustomization.getSuccessHeadImage();
			this.footerImage = pageCustomization.getSuccessFootImage();
//			this.notice = getDataSource().getPageCustomization().getSuccessNotice();
//			this.librarySIPStatus = getDataSource().getPageCustomization().getSuccessLibrarySIPStatus();
//			this.librarySIPFines = getDataSource().getPageCustomization().getSuccessLibrarySIPFines();
		} else if("Failure".equals(this.customPage)) {
			this.backgroundImage = pageCustomization.getFailureBackgroundImage();
			this.tileBackgroundImage = pageCustomization.isTileFailureBackgroundImage();
			rawColor = pageCustomization.getFailureForegroundColor();
			this.headerImage = pageCustomization.getFailureHeadImage();
			this.footerImage = pageCustomization.getFailureFootImage();
//			this.librarySIPBlock = getDataSource().getPageCustomization().getFailureLibrarySIPFines();
		}
		
		this.setForegroundColorR(Short.parseShort(rawColor.substring(1, 3), 16));
		this.setForegroundColorG(Short.parseShort(rawColor.substring(3, 5), 16));
		this.setForegroundColorB(Short.parseShort(rawColor.substring(5), 16));
		
		/*
		 * PPSK server type
		 */
		updatePPSKServerType();
	}
	
	private void submitCustomizationData() {
		StringBuilder bufferColor = new StringBuilder("#");
			
		bufferColor.append(toHexString(this.getForegroundColorR()))
					.append(toHexString(this.getForegroundColorG()))
					.append(toHexString(this.getForegroundColorB()));
			
		if("Login".equals(this.customPage) || "PPSK".equals(this.customPage)) {
			getDataSource().getPageCustomization().setBackgroundImage(this.backgroundImage);
			getDataSource().getPageCustomization().setTileBackgroundImage(isTileBackgroundImage());
			this.getDataSource().getPageCustomization().setForegroundColor(bufferColor.toString());
			getDataSource().getPageCustomization().setHeadImage(this.headerImage);
			
			if(!NmsUtil.isHMForOEM()){
				getDataSource().getPageCustomization().setFootImage(this.footerImage);
			}else{
				getDataSource().getPageCustomization().setFootImage(null);
			}
			
			if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
					|| getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
				updatePageFields();
			}
			
			/*
			 * PPSK server type
			 */
			updatePPSKServerType();
			
			//saveUsePolicy();
		} else if("Success".equals(this.customPage)) {
			getDataSource().getPageCustomization().setSuccessBackgroundImage(this.backgroundImage);
			getDataSource().getPageCustomization().setTileSuccessBackgroundImage(isTileBackgroundImage());
			this.getDataSource().getPageCustomization().setSuccessForegroundColor(bufferColor.toString());
			getDataSource().getPageCustomization().setSuccessHeadImage(this.headerImage);
			
			if(!NmsUtil.isHMForOEM()){
				getDataSource().getPageCustomization().setSuccessFootImage(this.footerImage);
			}else{
				getDataSource().getPageCustomization().setSuccessFootImage(null);
			}
			
			if("General".equals(this.successMessageType)) {
				getDataSource().getPageCustomization().setSuccessLibrarySIP(false);
//				getDataSource().getPageCustomization().setSuccessNotice(this.notice);
//				getDataSource().getPageCustomization().setSuccessLibrarySIPStatus(CwpPageCustomization.DEFAULT_LIB_SIP_STATUS);
//				getDataSource().getPageCustomization().setSuccessLibrarySIPFines(CwpPageCustomization.DEFAULT_LIB_SIP_STATUS);
			} else {
				getDataSource().getPageCustomization().setSuccessLibrarySIP(true);
//				getDataSource().getPageCustomization().setSuccessNotice(CwpPageCustomization.DEFAULT_SUCCESS_NOTICE);
//				getDataSource().getPageCustomization().setSuccessLibrarySIPStatus(this.librarySIPStatus);
//				getDataSource().getPageCustomization().setSuccessLibrarySIPFines(this.librarySIPFines);
				
			}
		} else if("Failure".equals(this.customPage)) {
			getDataSource().getPageCustomization().setFailureBackgroundImage(this.backgroundImage);
			getDataSource().getPageCustomization().setTileFailureBackgroundImage(isTileBackgroundImage());
			this.getDataSource().getPageCustomization().setFailureForegroundColor(bufferColor.toString());
			getDataSource().getPageCustomization().setFailureHeadImage(this.headerImage);
			if(!NmsUtil.isHMForOEM()){
				getDataSource().getPageCustomization().setFailureFootImage(this.footerImage);
			}else{
				getDataSource().getPageCustomization().setFailureFootImage(null);
			}
//			getDataSource().getPageCustomization().setFailureLibrarySIPFines(this.librarySIPBlock);
			
			if("General".equals(this.failureMessageType)) {
				getDataSource().getPageCustomization().setFailureLibrarySIP(false);
//				getDataSource().getPageCustomization().setFailureLibrarySIPFines(CwpPageCustomization.DEFAULT_LIB_SIP_BLOCK);
			} else {
				getDataSource().getPageCustomization().setFailureLibrarySIP(true);
//				getDataSource().getPageCustomization().setFailureLibrarySIPFines(this.librarySIPBlock);
			}
		}
	}
	
//	private void saveUsePolicy() {
//		/*
//		 * save content of use policy into a file
//		 * the file will be referred to in Cwp object
//		 * the file will be deleted if the cwp is deleted
//		 */
//		String fileName = getDataSource().getCwpName() + USE_POLICY_FILE_SUFFIX;
//		String fileDir = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy"
//				+ File.separator;
//		
//		if(!FileManager.getInstance().existsFile(fileDir)) {
//			FileManager.getInstance().createDirectory(fileDir);
//		}
//		
//		try {
//			FileManager.getInstance().writeFile(fileDir + fileName, this.usePolicy, false);
//		} catch (IOException e) {
//			
//		}
//		
//		//getDataSource().getPageCustomization().setUserPolicy(fileName);
//	}
	
	private void deleteUsePolicy(Cwp cwp) {
		String fileName = cwp.getCwpName() + USE_POLICY_FILE_SUFFIX;
		String filePath = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy"
				+ File.separator + fileName;
		
		try {
			HmBeOsUtil.deletefile(filePath);
		} catch (IOException e) {
			
		}
	}
	
	private void prepareForView() {
		preparePageResources();
		if(!"update".equals(operation)) {
		    preparePageFields(getDataSource());
		}
		prepareDataList();
		//initRadioValue();
		initMultiLanguageSupportCheckBox();
		initLanguageList();
		preparePageMultiLanguageRes(getDataSource());
		
		this.serviceId = getDataSource().getTempService();
		this.protocolNumber = getDataSource().getTempProtocol();
		this.port = getDataSource().getTempPort();
		this.hideCreateItem = getDataSource().getHideCreateItem();
		this.hideNewButton = getDataSource().getHideNewButton();
	}
	
	private void initRadioValue(){
		initSuccessRedirection();
		initFailureRedirection();
		initSuccessPageSource();
		initFailurePageSource();
		initWebPageSource();
	}
	
	private void initSuccessRedirection(){
		byte redirection = getDataSource().getSuccessRedirection();
		switch(redirection) {
		case Cwp.SUCCESS_REDIRECT_NO:
			successRedirection = "no-redirection";
			break;
		case Cwp.SUCCESS_REDIRECT_ORIGINAL:
			successRedirection = "original";
			break;
		case Cwp.SUCCESS_REDIRECT_EXTERNAL:
			successRedirection = "external";
			break;
		default:
			successRedirection = "no-redirection";
			break;
		}
	}
	
	private void initFailureRedirection(){
		byte redirection = getDataSource().getFailureRedirection();
		
		switch(redirection) {
		case Cwp.FAILURE_REDIRECT_NO:
			failureRedirection = "no-redirection";
			break;
		case Cwp.FAILURE_REDIRECT_LOGIN:
			failureRedirection = "login";
			break;
		case Cwp.FAILURE_REDIRECT_EXTERNAL:
			failureRedirection = "external";
			break;
		default:
			failureRedirection = "no-redirection";
			break;
		}
	}
	
	private void initSuccessPageSource(){
		byte source = getDataSource().getSuccessPageSource() ;
		
		switch (source) {
		case Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE:
			successPageRadioType = "customize";
			break;
		case Cwp.SUCCESS_PAGE_SOURCE_IMPORT:
			successPageRadioType = "import";
			break;
		default:
			successPageRadioType = "customize";
			break;
		}
	}
	
	private void initFailurePageSource(){
		byte source = getDataSource().getFailurePageSource() ;
		
		switch (source) {
		case Cwp.FAILURE_PAGE_SOURCE_CUSTOMIZE:
			failurePageRadioType = "customize";
			break;
		case Cwp.FAILURE_PAGE_SOURCE_IMPORT:
			failurePageRadioType = "import";
			break;
		default:
			failurePageRadioType = "customize";
			break;
		}
	}
	
	private void initWebPageSource(){
		byte source = getDataSource().getWebPageSource() ;
		
		switch (source) {
		case Cwp.WEB_PAGE_SOURCE_AUTOGENERATE:
			webPageRadioType = "customize";
			break;
		case Cwp.WEB_PAGE_SOURCE_IMPORT:
			webPageRadioType = "import";
			break;
		default:
			webPageRadioType = "customize";
			break;
		}
	}
	
	private void preparePageResources() {
		Cwp dataSource = getDataSource();
		
		if (dataSource.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
			
		} else if (getDataSource().getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT) {
			
		}
		
		if(dataSource.getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_IMPORT) {
			this.successDirectoryName = dataSource.getDirectoryName();
		}
		
		if(dataSource.getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
			this.failureDirectoryName = dataSource.getDirectoryName();
		}
	}
	
	private void preparePageFields(Cwp cwp) {
		
	    final byte registrationType = cwp.getRegistrationType();
		CwpPageCustomization pageCustomization = cwp.getPageCustomization();
		Map<String, CwpPageField> fields = new LinkedHashMap<String, CwpPageField>();
		byte order = 1;
		
		for(String field : CwpPageField.FIELDS) {
			CwpPageField newField = pageCustomization.getPageField(field);
			
			if(newField == null) {
				newField = new CwpPageField();
				newField.setEnabled(true);
				
                if(isOpenSSIDIDMSelfReg(registrationType) && field.equals("Comment")) {
                    continue;
                } else if (!isOpenSSIDIDMSelfReg(registrationType) && field.equals("Representing")) {
                    continue;
                }
				
				if(field.equals("Phone") || field.equals("Comment") || field.equals("Representing")) {
					newField.setRequired(false);
				} else {
                    if((isOpenSSIDIDMSelfReg(registrationType) && (field.equals(CwpPageField.VISITING)))
                            || (isPPSKSSIDIDMSelfReg(registrationType, cwp.getPpskServerType()) && (field.equals(CwpPageField.VISITING)))){
						newField.setRequired(false);
					} else {
						newField.setRequired(true);
					}
				}
				
				if(field.equals(CwpPageField.FIRSTNAME)){
					newField.setLabelName("firstName");
				}else if(field.equals(CwpPageField.LASTNAME)){
					newField.setLabelName("lastName");
				}else if(field.equals(CwpPageField.EMAIL)){
					newField.setLabelName("email");
				}else if(field.equals(CwpPageField.PHONE)){
					newField.setLabelName("phone");
				}else if(field.equals(CwpPageField.VISITING)){
					newField.setLabelName("visiting");
				}else if(field.equals(CwpPageField.REPRESENTING)){
					newField.setLabelName("representing");
				}
				
				newField.setLabel(field);
				newField.setPlace(order++);
				newField.setField(field);
				newField.setFieldMark(field);
				setMultiLanguageFields(newField);
			}
			
			
			fields.put(field, newField);
		}
		
		pageCustomization.setFields(fields);
	}

    private boolean isOpenSSIDIDMSelfReg(final byte registrationType) {
        return idmSelfReg && registrationType == Cwp.REGISTRATION_TYPE_BOTH;
    }
    private boolean isPPSKSSIDIDMSelfReg(final byte registrationType, final byte ppskServerType) {
        return idmSelfReg && registrationType == Cwp.REGISTRATION_TYPE_PPSK && ppskServerType == Cwp.PPSK_SERVER_TYPE_REG;
    }
	
	private void setMultiLanguageFields(CwpPageField field){
		String searchWord="cwp.preview.ppsk.firstname_label";
		String mark=CwpPageField.FIRSTNAMEMARK;
		if(field.getField().equals(CwpPageField.FIRSTNAME)){
			searchWord="cwp.preview.ppsk.firstname_label";
			mark=CwpPageField.FIRSTNAMEMARK;
		}else if(field.getField().equals(CwpPageField.LASTNAME)){
			searchWord="cwp.preview.ppsk.lastname_label";
			mark=CwpPageField.LASTNAMEMARK;
		}else if(field.getField().equals(CwpPageField.EMAIL)){
		    searchWord="cwp.preview.ppsk.email_label";
			mark=CwpPageField.EMAILMARK;
		}else if(field.getField().equals(CwpPageField.PHONE)){
			searchWord="cwp.preview.ppsk.phone_label";
			mark=CwpPageField.PHONEMARK;
		}else if(field.getField().equals(CwpPageField.VISITING)){
			if(isOpenSSIDIDMSelfReg(getDataSource().getRegistrationType())){
				searchWord="cwp.preview.ppsk.idm.visiting_label";
			}else{
				searchWord="cwp.preview.ppsk.visiting_label";
			}
			mark=CwpPageField.VISITINGMARK;
		}else if(field.getField().equals(CwpPageField.COMMENT)){
			searchWord="cwp.preview.ppsk.reason_label";
			mark=CwpPageField.COMMENTMARK;
		}else if(field.getField().equals(CwpPageField.REPRESENTING)){
			searchWord="cwp.preview.ppsk.idm.reason_label";
			mark=CwpPageField.REPRESENTINGMARK;
		}
		
		field.setFieldMark(mark);
		field.setLabel(cwpResReader.getString(searchWord, getLocaleFromPreview(1)));
		field.setLabel2(cwpResReader.getString(searchWord, getLocaleFromPreview(2)));
		field.setLabel3(cwpResReader.getString(searchWord, getLocaleFromPreview(3)));
		field.setLabel4(cwpResReader.getString(searchWord, getLocaleFromPreview(4)));
		field.setLabel5(cwpResReader.getString(searchWord, getLocaleFromPreview(5)));
		field.setLabel6(cwpResReader.getString(searchWord, getLocaleFromPreview(6)));
		field.setLabel7(cwpResReader.getString(searchWord, getLocaleFromPreview(7)));
		field.setLabel8(cwpResReader.getString(searchWord, getLocaleFromPreview(8)));
		field.setLabel9(cwpResReader.getString(searchWord, getLocaleFromPreview(9)));
	
	}
	
	private void prepareDataList() {
		this.certificateList = getBoCheckItems("certName", 
				CwpCertificate.class,
				null,
				BaseAction.CHECK_ITEM_BEGIN_NO,
				BaseAction.CHECK_ITEM_END_NO);

		this.vlanIdList = getBoCheckItems("vlanName", 
				Vlan.class,
				null,
				BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);
		
		this.ipAddressIdList = getBoCheckItems("addressName", 
				IpAddress.class,
				new FilterParams("typeFlag = :s1", new Object[]{IpAddress.TYPE_WEB_PAGE}),
				BaseAction.CHECK_ITEM_BEGIN_BLANK,
				BaseAction.CHECK_ITEM_END_NO);

		this.ipList = getIpObjectsByIpNameAndNet();
		
		if(getDataSource().getCertificate() != null
				&& certificate == null) {
			certificate = getDataSource().getCertificate().getId();
		}
		
		if (getDataSource().getVlan() != null
				&& vlanId == null) {
			vlanId = getDataSource().getVlan().getId();
		}
		
		if(getDataSource().getIpAddressSuccess() != null
				&& successExternalURLId == null){
			successExternalURLId = getDataSource().getIpAddressSuccess().getId();
		}
		
		if(getDataSource().getIpAddressFailure() != null 
				&& failureExternalURLId == null){
			failureExternalURLId = getDataSource().getIpAddressFailure().getId();
		}
	}
	
	private void updatePageFields() {
	    if(isOpenSSIDIDMSelfReg(getDataSource().getRegistrationType())) {
	        // change the enabled fields and required feilds
	        enableds = ArrayUtils.add(enableds, 0, "0");
	        enableds = ArrayUtils.add(enableds, 1, "1");
	        requireds = ArrayUtils.add(requireds, 0, "0");
	        requireds = ArrayUtils.add(requireds, 1, "1");
	    }
	    
		int index = 0;
		
		for(CwpPageField field : getDataSource().getPageCustomization().getFields().values()) {
			boolean rawEnabled = false;
			
			/*
			 * strangely, if none of the fields are selected in GUI, the String array
			 * enableds will contain a item "false"
			 */
			if(enableds != null && !enableds[0].equals("false")) {
				for(int j=0; j<enableds.length; j++) {
					if(index == Integer.parseInt(enableds[j])) { // the row is enabled
						rawEnabled = true;
						
//						field.setLabel(labels[j]);
						field.setPlace(orders[j]);
						
						// get the value of required
						boolean fieldRequired = false;
						
						if(requireds != null && !requireds[0].equals("false")) {
							for (String required : requireds) {
								if (index == Integer.parseInt(required)) { // the field is required
									fieldRequired = true;
									break;
								}
							}							
						}
						
						field.setRequired(fieldRequired);
						
						break;
					}
				}
			}
			
			field.setEnabled(rawEnabled);
			if(!field.getEnabled()) {
			    // reset the required field
			    field.setRequired(false);
			}
				
			index++;
		}
	}
	
	/**
	 * this function is used in restoring WLAN configuration
	 *
	 * @param cwp -
	 * @author Joseph Chen
	 */
	public void updatePageFields(Cwp cwp) {
		if(cwp == null) {
			return ;
		}
		
		if(cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED) {
			/*
			 * clear the old page fields if existed
			 */
			if(cwp.getPageCustomization().getFields() != null
					&& cwp.getPageCustomization().getFields().size() > 0) {
				cwp.getPageCustomization().getFields();
			}
		} else {
			/*
			 * if there are not page fields, add them
			 */
			if(cwp.getPageCustomization().getFields() == null
					|| cwp.getPageCustomization().getFields().size() == 0) {
				preparePageFields(cwp);
			}
		}
	}
	
//	private String[] labels;
	
	private String[] enableds;
	
	private String[] requireds;
	
	private byte[] orders;

//	/**
//	 * getter of labels
//	 * @return the labels
//	 */
//	public String[] getLabels() {
//		return labels;
//	}
//
//	/**
//	 * setter of labels
//	 * @param labels the labels to set
//	 */
//	public void setLabels(String[] labels) {
//		this.labels = labels;
//	}

	/**
	 * getter of enableds
	 * @return the enableds
	 */
	public String[] getEnableds() {
		return enableds;
	}

	/**
	 * setter of enableds
	 * @param enableds the enableds to set
	 */
	public void setEnableds(String[] enableds) {
		this.enableds = enableds;
	}

	/**
	 * getter of requireds
	 * @return the requireds
	 */
	public String[] getRequireds() {
		return requireds;
	}

	/**
	 * setter of requireds
	 * @param requireds the requireds to set
	 */
	public void setRequireds(String[] requireds) {
		this.requireds = requireds;
	}

	/**
	 * getter of orders
	 * @return the orders
	 */
	public byte[] getOrders() {
		return orders;
	}

	/**
	 * setter of orders
	 * @param orders the orders to set
	 */
	public void setOrders(byte[] orders) {
		this.orders = orders;
	}

	public String getHideOriginalPage() {
		return getDataSource().getId() == null ? "none" : "";
	}
	
	public String getOriginalBackgroundImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_BACKGROUND_IMAGE, true);
	}
	
	public String getCurrentBackgroundImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_BACKGROUND_IMAGE, true);
	}
	
	public String getOriginalForegroundColor() {
		return getForegroundColorRGB(STATE_ORIGINAL, PAGE_INDEX);
	}
	
	public String getCurrentForegroundColor() {
		return getForegroundColorRGB(STATE_CURRENT, PAGE_INDEX);
	}
	
	public String getOriginalSuccessForegroundColor() {
		return getForegroundColorRGB(STATE_ORIGINAL, PAGE_SUCCESS);
	}
	
	public String getOriginalFailureForegroundColor() {
		return getForegroundColorRGB(STATE_ORIGINAL, PAGE_FAILURE);
	}
	
	public String getCurrentSuccessForegroundColor() {
		return getForegroundColorRGB(STATE_CURRENT, PAGE_SUCCESS);
	}
	
	public String getCurrentFailureForegroundColor() {
		return getForegroundColorRGB(STATE_CURRENT, PAGE_FAILURE);
	}
	
	public String getOriginalHeadImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_HEAD_IMAGE, true);
	}
	
	public String getCurrentHeadImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_HEAD_IMAGE, true);
	}
	
	public String getOriginalFootImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_FOOT_IMAGE, true);
	}
	
	public String getCurrentFootImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_FOOT_IMAGE, true);
	}
	
	public String getOriginalSuccessBackgroundImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_SUCCESS_BACKGROUND_IMAGE, true);
	}
	
	public String getOriginalFailureBackgroundImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_FAILURE_BACKGROUND_IMAGE, true);
	}
	
	public String getCurrentSuccessBackgroundImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_SUCCESS_BACKGROUND_IMAGE, true);
	}
	
	public String getCurrentFailureBackgroundImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_FAILURE_BACKGROUND_IMAGE, true);
	}
	
	public String getOriginalSuccessHeadImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_SUCCESS_HEAD_IMAGE, true);
	}
	
	public String getOriginalFailureHeadImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_FAILURE_HEAD_IMAGE, true);
	}
	
	public String getCurrentSuccessHeadImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_SUCCESS_HEAD_IMAGE, true);
	}
	
	public String getCurrentFailureHeadImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_FAILURE_HEAD_IMAGE, true);
	}
	
	public String getOriginalSuccessFootImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_SUCCESS_FOOT_IMAGE, true);
	}
	
	public String getOriginalFailureFootImage() {
		return getPageResource(STATE_ORIGINAL, RESOURCE_TYPE_FAILURE_FOOT_IMAGE, true);
	}
	
	public String getCurrentSuccessFootImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_SUCCESS_FOOT_IMAGE, true);
	}
	
	public String getCurrentFailureFootImage() {
		return getPageResource(STATE_CURRENT, RESOURCE_TYPE_FAILURE_FOOT_IMAGE, true);
	}
	
	public String getOriginalUserPolicy() {
		return getUserPolicy(STATE_ORIGINAL);
	}
	

	
	public List<CwpPageField> getCurrentEnabledPageFields() {
		//return getEnabledPageFields(STATE_CURRENT);
		List<CwpPageField> pageFields = new ArrayList<CwpPageField>();
		for(CwpPageField pageField : getEnabledPageFields(STATE_CURRENT)){
			if(pageField.getEnabled()){
				CwpPageField tem = copyPageField(pageField);
				signalAdd(tem);
				pageFields.add(tem);
			}
		}
		return pageFields;
	}
	
	private CwpPageField copyPageField(CwpPageField pageField){
		CwpPageField page = new CwpPageField();
		
		page.setEnabled(pageField.getEnabled());
		page.setField(pageField.getField());
		page.setFieldMark(pageField.getFieldMark());
		page.setLabel(pageField.getLabel());
		page.setLabel2(pageField.getLabel2());
		page.setLabel3(pageField.getLabel3());
		page.setLabel4(pageField.getLabel4());
		page.setLabel5(pageField.getLabel5());
		page.setLabel6(pageField.getLabel6());
		page.setLabel7(pageField.getLabel7());
		page.setLabel8(pageField.getLabel8());
		page.setLabel9(pageField.getLabel9());
		page.setLabelName(pageField.getLabelName());
		page.setPlace(pageField.getPlace());
		page.setRequired(pageField.getRequired());
		page.setRestoreId(pageField.getRestoreId());
		
		return page;
	}
	
	private void signalAdd(CwpPageField field){
		if(field != null){
			if(field.getRequired() && 
					!field.getLabel().contains("*")){
				field.setLabel(field.getLabel() + "*");
				field.setLabel2(field.getLabel2() + "*");
				field.setLabel3(field.getLabel3() + "*");
				field.setLabel4(field.getLabel4() + "*");
				field.setLabel5(field.getLabel5() + "*");
				field.setLabel6(field.getLabel6() + "*");
				field.setLabel7(field.getLabel7() + "*");
				field.setLabel8(field.getLabel8() + "*");
				field.setLabel9(field.getLabel9() + "*");
			}else if(!field.getRequired() &&
					        !field.getLabel().contains("*")){
				field.setLabel(field.getLabel());
				field.setLabel2(field.getLabel2());
				field.setLabel3(field.getLabel3());
				field.setLabel4(field.getLabel4());
				field.setLabel5(field.getLabel5());
				field.setLabel6(field.getLabel6());
				field.setLabel7(field.getLabel7());
				field.setLabel8(field.getLabel8());
				field.setLabel9(field.getLabel9());
			}else if(!field.getRequired() &&
					        field.getLabel().contains("*")){
				field.setLabel(field.getLabel().substring(0,field.getLabel().length()-1));
				field.setLabel2(field.getLabel2().substring(0,field.getLabel2().length()-1));
				field.setLabel3(field.getLabel3().substring(0,field.getLabel3().length()-1));
				field.setLabel4(field.getLabel4().substring(0,field.getLabel4().length()-1));
				field.setLabel5(field.getLabel5().substring(0,field.getLabel5().length()-1));
				field.setLabel6(field.getLabel6().substring(0,field.getLabel6().length()-1));
				field.setLabel7(field.getLabel7().substring(0,field.getLabel7().length()-1));
				field.setLabel8(field.getLabel8().substring(0,field.getLabel8().length()-1));
				field.setLabel9(field.getLabel9().substring(0,field.getLabel9().length()-1));
			}
			
		}
	}
	
	public List<CwpPageField> getOriginalEnabledPageFields() {
		return getEnabledPageFields(STATE_ORIGINAL);
	}

	public String getOriginalSuccessNotice() {
		if(getDataSource() == null
				|| getDataSource().getId() == null) {
			return null;
		}
		
		// get original BO from database
		Cwp originalBo = null;
		
		try {
			originalBo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
		} catch(Exception e) {
			
		}
		
		String successNotice = originalBo != null ? originalBo.getPageCustomization().getCwpPageMultiLanguageRes(1).getSuccessNotice() : null;
		return escapeCharacter(successNotice);
	}
	
	public String getCurrentSuccessNotice() {
		String successNotice = getDataSource() != null ? getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(1).getSuccessNotice() : null;
		return escapeCharacter(successNotice);
	}
	
	private String escapeCharacter(String source) {
		if(source == null) {
			return null;
		}
		
		// change the '\n' into '<br>'
		source = source.replace("\r\n", "<br>");
	
		// change '{', '}' into &123;, &125;
		source = source.replace("{", "&#123;");
		source = source.replace("}", "&#125;");
		source = source.replace("'", "&#180;");
		
		return source;
	}
	
	public String getDisplayName() {
		return getDataSource().getCwpName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}
	
	private String getUserPolicy(short state) {
		String filePath = getPageResource(state, RESOURCE_TYPE_USER_POLICY, false);
		
		if(filePath == null) {
			return null;
		}
		
		if(!(new File(filePath).exists())) {
			filePath = DEFAULT_RESOURCE_PATH + CwpPageCustomization.DEFAULT_USER_POLICY;
		}
		
		// get the content of user policy from file
		BufferedReader bufferReader;
		
		try {
			bufferReader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			bufferReader = null;
		}
		
		if(bufferReader == null) {
			return null;
		}
		
		String textLine;
		StringBuilder userPolicy = new StringBuilder();
		
		try {
			while((textLine = bufferReader.readLine()) != null) {
				userPolicy.append(textLine).append("\n");
			}
		} catch (IOException e) {
			
		}
		
		return userPolicy.toString();
	}
	
	private String getUserPolicy(Cwp cwp) {
		String filePath = getPageResource(cwp.getPageCustomization(), RESOURCE_TYPE_USER_POLICY, false);
		
		if(filePath == null) {
			return null;
		}
		
		// get the content of user policy from file
		BufferedReader bufferReader;
		
		try {
			bufferReader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			bufferReader = null;
		}
		
		if(bufferReader == null) {
			return null;
		}
		
		String textLine;
		StringBuilder userPolicy = new StringBuilder();
		
		try {
			while((textLine = bufferReader.readLine()) != null) {
				userPolicy.append(textLine).append("\n");
			}
		} catch (IOException e) {
			
		}
		
		return userPolicy.toString();
	}
	
	private String getUserPolicy(String fileName) {
		String filePath;
		
		if(isDefaultResource(fileName)) { // get from default ones
			filePath = DEFAULT_RESOURCE_PATH + fileName;					
		} else { // get from uploaded ones
			filePath = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy" + File.separator + fileName;
		}
		
		// get the content of user policy from file
		BufferedReader bufferReader;
		
		try {
			bufferReader = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			bufferReader = null;
		}
		
		if(bufferReader == null) {
			return null;
		}
		
		String textLine;
		StringBuilder userPolicy = new StringBuilder();
		
		try {
			while((textLine = bufferReader.readLine()) != null) {
				userPolicy.append(escapeCharacter(textLine)).append("\n");
			}
		} catch (IOException e) {
			
		}
		
		return userPolicy.toString();
	}
	
	public String getDefaultUsePolicy() {
		return cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(getCustomizeLanguage())).replaceAll("<br>","\\r\\n" );

//		String filePath = getPageResource(new CwpPageCustomization(), 
//							RESOURCE_TYPE_USER_POLICY, false);
//		
//		if(filePath == null) {
//			return null;
//		}
//		
//		// get the content of user policy from file
//		BufferedReader bufferReader;
//		
//		try {
//			bufferReader = new BufferedReader(new FileReader(new File(filePath)));
//		} catch (FileNotFoundException e) {
//			bufferReader = null;
//		}
//		
//		if(bufferReader == null) {
//			return null;
//		}
//		
//		String textLine;
//		StringBuilder userPolicy = new StringBuilder();
//		
//		try {
//			while((textLine = bufferReader.readLine()) != null) {
//				userPolicy.append(textLine).append("<br>");
//			}
//		} catch (IOException e) {
//			
//		}
//		
//		return userPolicy.toString();
	}
	
	private String getPageResource(CwpPageCustomization bo, int type, boolean isRelative) {
		/*
		 * the page resources may be images which will be used in preview page with HTML tag "img".
		 * in tag "img", if the url is null or empty(""), something wrong will happen.
		 * For example, in creating a CWP page, after previewing the creating page, HM will navigate 
		 * to list view(cwpPageCustomizationList.jsp), not to INPUT page(cwpPageCustomization.jsp).
		 * 
		 * the following value could be arbitrary
		 * joseph chen , 07/29/2008
		 */
		String nullResource = "/nullimage";
		
		if(bo == null) {
			return nullResource;
		}
		
		// get absolute path in HM
		String path;
		
	
		String fileName = null;
		
		switch(type) {
		case RESOURCE_TYPE_HEAD_IMAGE:
			fileName = bo.getHeadImage();
			break;
		case RESOURCE_TYPE_FOOT_IMAGE:
			fileName = bo.getFootImage();
			break;
		case RESOURCE_TYPE_USER_POLICY:
			fileName = bo.getUserPolicy();
			break;
		case RESOURCE_TYPE_SUCCESS_HEAD_IMAGE:
			fileName = bo.getSuccessHeadImage();
			break;
		case RESOURCE_TYPE_SUCCESS_FOOT_IMAGE:
			fileName = bo.getSuccessFootImage();
			break;
		case RESOURCE_TYPE_BACKGROUND_IMAGE:
			fileName = bo.getBackgroundImage();
			break;
		case RESOURCE_TYPE_SUCCESS_BACKGROUND_IMAGE:
			fileName = bo.getSuccessBackgroundImage();
			break;
		case RESOURCE_TYPE_FAILURE_HEAD_IMAGE:
			fileName = bo.getFailureHeadImage();
			break;
		case RESOURCE_TYPE_FAILURE_FOOT_IMAGE:
			fileName = bo.getFailureFootImage();
			break;
		case RESOURCE_TYPE_FAILURE_BACKGROUND_IMAGE:
			fileName = bo.getFailureBackgroundImage();
			break;
		
		default:
			break;
		}
		
		if(isDefaultResource(fileName)) { // get from default ones
			if(type == RESOURCE_TYPE_USER_POLICY) {
				path = DEFAULT_RESOURCE_PATH + fileName;					
			} else {
				path = DEFAULT_IMAGE_PATH + fileName;
			}
			
			if(isRelative) {
				if(type == RESOURCE_TYPE_USER_POLICY) {
					path = path.substring(path.indexOf(DEFAULT_RESORUCE_DIRECTORY) - 1);
				} else {
					path = path.substring(path.indexOf(DEFAULT_IMAGE_DIRECTORY) - 1);
				}
			}
		} else { // get from uploaded ones
			if(type == RESOURCE_TYPE_USER_POLICY) {
				path = AhDirTools.getPageResourcesDir(domainName) + "UsePolicy" + File.separator + fileName;
			} else {
				path = AhDirTools.getPageResourcesDir(domainName) + fileName;
			}
			
			if(isRelative) {
				path = path.substring(path.indexOf(DEFAULT_DOMAINS_DIRECTORY) - 1);
			}
		}
		
		path = path.replace("\\", "/");
		return path;
	}
	
	private String getPageResource(short state, int type, boolean isRelative) {
		// get original BO from database
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			/*
			 * could not return null here, 
			 * reason please see: 
			 * getPageResource(CwpPageCustomization bo, int type, boolean isRelative)
			 */
			return "/nullimage"; 
		}
		
		return getPageResource(bo.getPageCustomization(), type, isRelative);
	}
	
	private List<CwpPageField> getEnabledPageFields(short type) {
		Cwp dataSource = null;
		
		if(type == STATE_ORIGINAL) {
			if(getDataSource().getId() != null)
				try {
					dataSource = (Cwp) findBoById(boClass, getDataSource().getId(), this);
				} catch (Exception e) {
					
				}
		} else {
			dataSource = getDataSource();
		}
		
		if(dataSource == null 
				|| dataSource.getPageCustomization().getFields() == null) {
			return null;
		}
		
		List<CwpPageField> enabledFields = new ArrayList<CwpPageField>();
		
		for(CwpPageField field : 
			dataSource.getPageCustomization().getFields().values()) {
		    if(field.getEnabled()) {
		        if(isOpenSSIDIDMSelfReg(dataSource.getRegistrationType()) 
		                && !field.getLabel().toLowerCase().contains(CwpPageField.COMMENT.toLowerCase())){
		            enabledFields.add(field);
		        }else{
		            if(!field.getLabel().toLowerCase().contains(CwpPageField.REPRESENTING.toLowerCase())) {
		                enabledFields.add(field);
		            }
		        }
		    }
		}
		
		Collections.sort(enabledFields, new CwpPageFieldComparator());
		
		return enabledFields;
	}
	
	private List<CwpPageField> getEnabledPageFields(Cwp dataSource) {
		if(dataSource == null 
				|| dataSource.getPageCustomization().getFields() == null) {
			return null;
		}
		
		List<CwpPageField> enabledFields = new ArrayList<CwpPageField>();
		
		for(CwpPageField field : 
			dataSource.getPageCustomization().getFields().values()) {
			if(field.getEnabled()) {
				enabledFields.add(field);
			}
		}
		
		Collections.sort(enabledFields, new CwpPageFieldComparator());
		
		return enabledFields;
	}
	
	/**
	 * create cwp web page
	 * this function will be used in restoring backup data.
	 * and at the time of restoring, BE is not started up. so some
	 * functions of BE cound not be used to check the existence of 
	 * destination directory. a flag is set to indicate whether existence
	 * check will be done before delete the existed directory. 
	 * 
	 * @param dataSource -
	 * @param deleteWithCheck -
	 * @return -
	 * @author Joseph Chen
	 */
	public boolean createCwpPage(Cwp dataSource, boolean deleteWithCheck) {
		if(dataSource == null) {
			return true;
		}
		
		/*
		 * remove the origianl directory if exists, and create new one
		 */ 
		String newDirPath = AhDirTools.getCwpWebDir(domainName)
							+ dataSource.getCwpName()
							+ File.separator;
		
		String newDirZipPath = AhDirTools.getCwpWebDir(domainName)
							+ dataSource.getCwpName();
							
		
		try {
			if(FileManager.getInstance().existsFile(newDirPath)) {
				if(needDeleteDirectory(dataSource)) {
					if(deleteWithCheck) {
						/*
						 * check whether the directory is used by other CWP
						 */
						HiveApFileAction fileAction = new HiveApFileAction();
						fileAction.setDomainName(domainName);
						boolean deleteResult  = fileAction.deleteCwpDirectory(dataSource.getCwpName());
						
						if(!deleteResult) {
							addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.clearFailed"));
							return false;
						}else{
							if(FileManager.getInstance().existsFile(newDirZipPath + TarArchive.SUFFIX_TAR_ZIP)){
								FileManager.getInstance().deletefile(newDirZipPath + TarArchive.SUFFIX_TAR_ZIP);
							}
						}
					} else {
						/*
						 * remove the directory without check
						 */
						FileManager.getInstance().deleteDirectory(newDirPath);
						
						if(FileManager.getInstance().existsFile(newDirZipPath + TarArchive.SUFFIX_TAR_ZIP)){
							FileManager.getInstance().deletefile(newDirZipPath + TarArchive.SUFFIX_TAR_ZIP);
						}
					}
				}
			} 
			
			/*
			 * if none of the three pages is IMPORT, create directory
			 */
			if((dataSource.getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL 
							&& dataSource.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT)
					|| (dataSource.isShowSuccessPage() && 
							dataSource.getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_IMPORT)
					|| (dataSource.isShowFailurePage() &&
							dataSource.getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_IMPORT)) {
				
			} else {
				FileManager.getInstance().createDirectory(newDirPath);
			}
		} catch (Exception exception) {
			log.error("createPage", "failed to create the path - " + newDirPath, exception);
			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
			return false;
		}
		
		// set related parameters (directory/login page/ result page/failure page)
		setWebPages(dataSource);
		
		/*
		 * create pages
		 */
		if(dataSource.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_IMPORT) {
			boolean result = true;
			byte regType = dataSource.getRegistrationType();
			
			/*
			 * create login page
			 */
			switch(regType) {
			case Cwp.REGISTRATION_TYPE_AUTHENTICATED:
				result = createPage(dataSource, PAGE_AUTHENTICATION);
				break;
			case Cwp.REGISTRATION_TYPE_REGISTERED:
				result = createPage(dataSource, PAGE_REGISTRATION);
				break;
			case Cwp.REGISTRATION_TYPE_BOTH:
				result = createPage(dataSource, PAGE_BOTH);
				break;
			case Cwp.REGISTRATION_TYPE_EULA:
				result = createPage(dataSource, PAGE_EULA);
				break;
			case Cwp.REGISTRATION_TYPE_PPSK:
					result = createPage(dataSource, PAGE_PPSK);
				break;
			default:
				break;
			}
				
			if(!result) {
				return false;
			}
			
			
			if(regType != Cwp.REGISTRATION_TYPE_EXTERNAL) {
				if(regType != Cwp.REGISTRATION_TYPE_EULA) {
					if(!createPage(dataSource, PAGE_USER_POLICY)) {
						return false;
					}
				}	
			}
		}

		/*
		 * create success page
		 */
		if(dataSource.isShowSuccessPage()) {
			if(dataSource.getSuccessPageSource() != Cwp.SUCCESS_PAGE_SOURCE_IMPORT) {
				if(!createPage(dataSource, PAGE_SUCCESS)) {
					return false;
				}	
			}
		}
		
		/*
		 * create failure page
		 * 
		 * EULA type of registration doesn't need failure page, jchen, 2011-08-25
		 */	
		if(dataSource.isShowFailurePage() 
				&& dataSource.getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA) {
			/*
			 * use login page with error message as failure page
			 * joseph chen, 2011-08-12
			 */
			if(dataSource.isUseLoginAsFailure()) {
				copyLoginPageToFailure(dataSource);
			} else { // create independent failure page
				if(dataSource.getFailurePageSource() != Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
					if(!createPage(dataSource, PAGE_FAILURE)) {
						return false;
					}
				}
			}
		}
		
		/*
		 * create new & old success page
		 */
		CwpPageCustomization pagec = dataSource.getPageCustomization();
		CwpPageMultiLanguageRes multiRes = pagec.getCwpPageMultiLanguageRes(1);
		if(pagec != null){
			String oldTemPath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_SUCCESS_OLD;
			String newPath = AhDirTools.getCwpWebDir(domainName)
					+ dataSource.getCwpName()+ File.separator + HTML_TEMPLATE_SUCCESS;
			String desOldPath = AhDirTools.getCwpWebDir(domainName)
					+ dataSource.getCwpName()+ File.separator + HTML_SUCCESS_OLD;
			String desNewPath = AhDirTools.getCwpWebDir(domainName)
					+ dataSource.getCwpName()+ File.separator + HTML_SUCCESS_NEW;
			String[] htmlContent = null;
			try {
				htmlContent = FileManager.getInstance().readFile(oldTemPath);
				FileManager.getInstance().createFile("", desNewPath);
				FileManager.getInstance().copyFile(newPath, desNewPath);
				if(pagec.isSuccessLibrarySIP()) {
					/*
					 * Library SIP
					 */
					modifyField(htmlContent, PAGE_ELEMENT_SIP_OK, 
							multiRes.getSuccessLibrarySIPStatus().replace("\r\n", "<br>"));
					
					modifyField(htmlContent, PAGE_ELEMENT_SIP_FINES, 
							multiRes.getSuccessLibrarySIPFines().replace("\r\n", "<br>"));
				} else {
//					modifyField(htmlLines, PAGE_ELEMENT_NOTICE, 
//							pageCustomization.getSuccessNotice().replace("\r\n", "<br>"));
				}
				FileManager.getInstance().createFile(desOldPath, htmlContent);
			} catch (Exception exception) {
				log.error("failed to create page new/old success page");
			}
		}
		
		/*----create tar.gz file for the directory-----*/
		new TarArchive().createTarZip(newDirZipPath, newDirZipPath + TarArchive.SUFFIX_TAR);
		return true;
	}
	
	private void copyLoginPageToFailure(Cwp cwp) {
		/*
		 * get path of login page
		 */
		String fileDir = getCreatedDirectory(cwp);
		String loginPage = null;
		
		switch(cwp.getRegistrationType()) {
		case Cwp.REGISTRATION_TYPE_AUTHENTICATED:
			loginPage = AUTO_GENERATED_PAGE_AUTHENTICATION;
			break;
		case Cwp.REGISTRATION_TYPE_REGISTERED:
			loginPage = AUTO_GENERATED_PAGE_REGISTRATION;
			break;
		case  Cwp.REGISTRATION_TYPE_BOTH:
			loginPage = AUTO_GENERATED_PAGE_BOTH;
			break;
		case Cwp.REGISTRATION_TYPE_EULA:
			loginPage = AUTO_GENERATED_PAGE_EULA;
			break;
		case Cwp.REGISTRATION_TYPE_PPSK:
			loginPage = AUTO_GENERATED_PAGE_PPSK;
			break;
		default:
			break;
		}
		
		if(loginPage == null) {
			return ;
		}
		
		String failurePage;
		
		if(loginPage.endsWith(".cgi")) {
			failurePage = "failure.cgi";
			cwp.setFailurePageName(failurePage);
		} else {
			failurePage = AUTO_GENERATED_FAILURE_PAGE;
		}
		
		// create file
		try {
			FileManager.getInstance().copyFile(fileDir + loginPage, fileDir + failurePage);
		} catch (Exception exception) {
			log.error("failed to copy the login page<"
					+ loginPage + "> to failure page<" + fileDir + "failure.html>", exception);
		}
	}
	
	private boolean needDeleteDirectory(Cwp cwp) {
		if(cwp == null) {
			return false;
		}
		
		if(cwp.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT) {
			if(cwp.getCwpName().equals(cwp.getDirectoryName())) {
				return false;
			}
		}
		
		if(cwp.getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_IMPORT) {
			if(cwp.getCwpName().equals(this.successDirectoryName)) {
				return false;
			}
		}
		
		if(cwp.getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
			if(cwp.getCwpName().equals(this.failureDirectoryName)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean createPage(Cwp dataSource, short pageType) {
		if(dataSource == null) {
			return false;
		}
		
		CwpPageCustomization pageCustomization = dataSource.getPageCustomization();
		
		if(pageCustomization == null) {
			return false;
		}
		
		// get path of HTML template
		String templatePath = null;
		
		switch(pageType) {
		case PAGE_AUTHENTICATION:
				templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_AUTHENTICATION;
			break;
		case PAGE_REGISTRATION:
				templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_REGISTRATION;
			break;
		case PAGE_BOTH:
			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_BOTH;
			break;
		case PAGE_EULA:
			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_EULA;
			break;
		case PAGE_SUCCESS:
			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_SUCCESS;
			break;
		case PAGE_USER_POLICY:
			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_USER_POLICY;
			break;
		case PAGE_FAILURE:
			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_FAILURE;
			break;
		case PAGE_PPSK:
			if(dataSource.getPpskServerType() == Cwp.PPSK_SERVER_TYPE_AUTH)
				templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_PPSK_AUTH;
			else 
			templatePath = DEFAULT_RESOURCE_PATH + HTML_TEMPLATE_PPSK;
			
			break;
		default:
			break;
		}
		
		// copy resources files
		if(!copyPageResources(dataSource, pageType, getCreatedDirectory(dataSource), templatePath)) {
			return false;
		}
		
		// copy multi language js files
		if(!copyMultiLanguageJSFiles(dataSource, pageType, getCreatedDirectory(dataSource), templatePath)) {
			return false;
		}
		
		// read template into String lines
		String[] htmlLines = null;
		
		try {
			htmlLines = FileManager.getInstance().readFile(templatePath);
		} catch (Exception exception) {
			log.error("createPage", "failed to read the template file - " + templatePath, exception);
		}
		
		if(htmlLines == null) {
			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
			return false;
		}
		
		/*
		 * common page elements
		 */
		customizeCommonPage(htmlLines);
		
		/*
		 * customize pages
		 */
		if (pageType != PAGE_SUCCESS
				&& pageType != PAGE_FAILURE) {
			/*
			 * modify the common ones
			 * PAGE_AUTHENTICATION, PAGE_REGISTRATION, PAGE_BOTH, PAGE_USER_POLICY
			 */
			// body foreground
			customizeForeground(htmlLines, pageCustomization.getForegroundColor());
			// body background
			modifyField(htmlLines, PAGE_ELEMENT_BACKGROUND, pageCustomization.getBackgroundImage());
			// body background, tile
			modifyField(htmlLines, PAGE_ELEMENT_TILE, pageCustomization.getTileBackgroundString());
			// head image
//			modifyField(htmlLines, PAGE_ELEMENT_HEAD_IMAGE, pageCustomization.getHeadImage());
			// foot image
			modifyField(htmlLines, PAGE_ELEMENT_FOOT_IMAGE, pageCustomization.getFootImage());
		}
						
		if (pageType == PAGE_REGISTRATION 
				|| pageType == PAGE_BOTH) {
			// registration fields
			htmlLines = customizeRegistrationFields(htmlLines, dataSource);
			
			// Self-reg via IDM
			htmlLines = generateCountryCodeList(htmlLines, dataSource);
		} else if (pageType == PAGE_PPSK && dataSource.getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG) {
		    htmlLines = customizePPSKRegFields(htmlLines, dataSource);
		}
		
		/*
		 * SUCCESS PAGE
		 */
		if (pageType == PAGE_SUCCESS) {
			// body foreground
			customizeForeground(htmlLines, pageCustomization.getSuccessForegroundColor());
			// body background
			modifyField(htmlLines, PAGE_ELEMENT_BACKGROUND, pageCustomization.getSuccessBackgroundImage());
			// body background, tile
			modifyField(htmlLines, PAGE_ELEMENT_TILE, pageCustomization.getTileSuccessBackgroundString());
			// head image
//			modifyField(htmlLines, PAGE_ELEMENT_HEAD_IMAGE, pageCustomization.getSuccessHeadImage());
			// foot image
			modifyField(htmlLines, PAGE_ELEMENT_FOOT_IMAGE, pageCustomization.getSuccessFootImage());
			// notice
			if(pageCustomization.isSuccessLibrarySIP()) {
				/*
				 * Library SIP
				 */
//				modifyField(htmlLines, PAGE_ELEMENT_SIP_OK, 
//						pageCustomization.getSuccessLibrarySIPStatus().replace("\r\n", "<br>"));
//				
//				modifyField(htmlLines, PAGE_ELEMENT_SIP_FINES, 
//						pageCustomization.getSuccessLibrarySIPFines().replace("\r\n", "<br>"));
			} else {
//				modifyField(htmlLines, PAGE_ELEMENT_NOTICE, 
//						pageCustomization.getSuccessNotice().replace("\r\n", "<br>"));
			}
            if (dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH
                    && dataSource.isIdmSelfReg()) {
                replaceSuccIDMResource(htmlLines);
            }
		}
		
		/*
		 * FAILURE PAGE
		 */
		if (pageType == PAGE_FAILURE) {
			// body foreground
			customizeForeground(htmlLines, pageCustomization.getFailureForegroundColor());
			// body background
			modifyField(htmlLines, PAGE_ELEMENT_BACKGROUND, pageCustomization.getFailureBackgroundImage());
			// body background, tile
			modifyField(htmlLines, PAGE_ELEMENT_TILE, pageCustomization.getTileFailureBackgroundString());
			// head image
//			modifyField(htmlLines, PAGE_ELEMENT_HEAD_IMAGE, pageCustomization.getFailureHeadImage());
			// foot image
			modifyField(htmlLines, PAGE_ELEMENT_FOOT_IMAGE, pageCustomization.getFailureFootImage());
			
			// notice
			if(pageCustomization.isFailureLibrarySIP()) {
				/*
				 * Library SIP
				 */
//				modifyField(htmlLines, PAGE_ELEMENT_SIP_BLOCK, 
//						pageCustomization.getFailureLibrarySIPFines().replace("\r\n", "<br>"));
			}
		}
		
		if (pageType == PAGE_USER_POLICY
				|| pageType == PAGE_EULA) {
			// user policy
//			modifyField(htmlLines, PAGE_ELEMENT_USER_POLICY, pageCustomization.getUserPolicy());
		}
		
		// write lines into destination file
		String newFilePath = getCreatedDirectory(dataSource);
		
		switch(pageType) {
		case PAGE_AUTHENTICATION:
			newFilePath += AUTO_GENERATED_PAGE_AUTHENTICATION;
			break;
		case PAGE_REGISTRATION:
			newFilePath += AUTO_GENERATED_PAGE_REGISTRATION;
			break;
		case PAGE_BOTH:
			newFilePath += AUTO_GENERATED_PAGE_BOTH;
			break;
		case PAGE_SUCCESS:
			newFilePath += AUTO_GENERATED_RESULT_PAGE;
			break;
		case PAGE_FAILURE:
			newFilePath += AUTO_GENERATED_FAILURE_PAGE;
			break;
		case PAGE_USER_POLICY:
			newFilePath += AUTO_GENERATED_USER_POLICY;
			break;
		case PAGE_EULA:
			newFilePath += AUTO_GENERATED_PAGE_EULA;
			break;
		case PAGE_PPSK:
			newFilePath += AUTO_GENERATED_PAGE_PPSK;
			break;
		default:
			break;
		}
		
		// create file
		try {
			FileManager.getInstance().createFile(newFilePath, htmlLines);
		} catch (Exception exception) {
			log.error("createPage", "failed to write the HTML file - " + newFilePath, exception);
			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
			return false;
		}
		
		reWriteMultiLanguageJSFiles(dataSource, pageType, getCreatedDirectory(dataSource), templatePath);
		
		return true;
	}

	private String[] customizePPSKRegFields(String[] lines, Cwp dataSource) {
	       if(lines == null || dataSource == null ) {
	            return null;
	        }
	        
	        if(dataSource.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
	            return null;
	        }
	        
	        // store source String lines into a LinkedList
	        List<String> lineList = new LinkedList<String>();
	        
	        for(String line : lines) {
	            lineList.add(line);
	            if(line != null && line.contains(PAGE_ELEMENT_PPSK_REG_FIELD_VISITING)) {
	                List<CwpPageField> enabledFields = getEnabledPageFields(dataSource);
	                for (CwpPageField cwpPageField : enabledFields) {
	                    String label = cwpPageField.getLabel();
	                    if(StringUtils.isNotBlank(label) 
	                            && label.toLowerCase().contains(CwpPageField.VISITING.toLowerCase())) {
	                        if(cwpPageField.getRequired()) {
	                            lineList.add("<tr><td> <input type=\"text\" name=\"company\" id=\"field4\" maxlength=\"32\" placeholder=\"\" required/><br /></td></tr>");
	                        } else {
	                            lineList.add("<tr><td> <input type=\"text\" name=\"company\" id=\"field4\" maxlength=\"32\" placeholder=\"\"/><br /></td></tr>");
	                        }
	                        break;
	                    }
                    }
	            }
	        }
        return lineList.toArray(new String[lineList.size()]);
    }

    private String[] generateCountryCodeList(String[] lines, Cwp dataSource) {
       if(lines == null || dataSource == null ) {
            return null;
        }
        if(dataSource.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
            return null;
        }
        if(dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH && dataSource.isIdmSelfReg() && isCountryCodeEnabled) {
            String codeSnippet = null; int index = 0;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if(StringUtils.isNotBlank(line) && line.contains(PAGE_ELEMENT_REGISTRATION_COUNTRYLIST)) {
                    final String filename = DEFAULT_RESOURCE_PATH + DEFAULT_RESORUCE_IDM_DIRECTORY 
                            + File.separator +  PAGE_ELEMENT_REGISTRATION_COUNTRYLIST_SNIPPET;
                    File file = new File(filename);
                    if(file.exists()) {
                        try {
                            codeSnippet = FileUtils.readFileToString(file, "utf-8");
                            index = i;
                            break;
                        } catch (IOException e) {
                            log.error("generateCountryCodeList", "Error when read the snippet.", e);
                        }
                    } else {
                        log.warn("generateCountryCodeList", "Unable to find the the snippet.");
                        break;
                    }
                }
            }
            if(StringUtils.isNotBlank(codeSnippet)) {
                lines = ArrayUtils.add(lines, index+1, codeSnippet);
            }
        }
                
        return lines;
    }

    private void replaceSuccIDMResource(String[] htmlLines) {
	    replaceHardcode(htmlLines, SUCCESS_PROMPT_IDM_REPLACEWORD,
                "document.title='Register Successful';if(document.getElementById(\"idm_register\") && document.getElementById(\"idm_register\").innerHTML !== '') {document.getElementById(\"h2Title\").innerHTML = document.getElementById(\"idm_register\").innerHTML;}"
                + "if(!waitApproval && document.getElementById(\"idm_reminder\") && document.getElementById(\"idm_reminder\").innerHTML !== '') {document.getElementById(\"notice\").innerHTML = document.getElementById(\"idm_reminder\").innerHTML;}"
                + "if(waitApproval && document.getElementById(\"idm_reminder2\") && document.getElementById(\"idm_reminder2\").innerHTML !== '') {document.getElementById(\"notice\").innerHTML = document.getElementById(\"idm_reminder2\").innerHTML;}"
                + "if(document.getElementById(\"idm_buttonSection\")) {document.getElementById(\"idm_buttonSection\").style.display = '';}");
	}
    private void replaceHardcode(String[] htmlLines, String target, String replacement) {
        for (int i = 0; i < htmlLines.length; i++) {
            String line = htmlLines[i];
            if (line.contains(target)) {
                htmlLines[i] = line.replace(target,replacement);
            }
        }
    }
    
    private void replaceHardcodeinFile(String destFilePath, String target, String replacement) {
        try {
            Path path = Paths.get(destFilePath);
            Charset charset = StandardCharsets.UTF_8;
            String content = new String(Files.readAllBytes(path), charset);
            content = content.replace(target, replacement);
            Files.write(path, content.getBytes(charset));
        } catch (IOException e) {
            log.error("replaceHardcodeinFile", "Error when replace the hardcode in file", e);
        }
    }
    
    private void appendCountryCode2JS(Cwp dataSource, short pageType,
            String fileNamePrefix, String jsDestFilePath) {
        if(pageType == PAGE_BOTH && dataSource.isIdmSelfReg() && isCountryCodeEnabled) {
              final String replacement = DEFAULT_RESOURCE_PATH + DEFAULT_RESORUCE_IDM_DIRECTORY 
                        + File.separator + DEFAULT_RESORUCE_DIRECTORY + File.separator + "countrycodes";
              final String countrycodePath = jsDestFilePath.replace(fileNamePrefix, replacement);
                File file = new File(countrycodePath);
                if(file.exists()) {
                    try {
                        String countrycodes = FileUtils.readFileToString(file, "utf-8");
                        replaceHardcodeinFile(jsDestFilePath, COUNTRYCODE_REPLACEWORD, countrycodes);
                    } catch (IOException e) {
                        log.error("appendCountryCode2JS", "Error when read the snippet.", e);
                    }
                } else {
                    log.warn("appendCountryCode2JS", "Unable to find the the country code snippet.");
                }
         }
    }
	
	private String getCreatedDirectory(Cwp dataSource) {
		if(dataSource == null) {
			return null;
		}
		
		if((dataSource.getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL 
				&& dataSource.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT)
				|| dataSource.getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_IMPORT
				|| dataSource.getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
			return AhDirTools.getCwpWebDir(domainName)
						+ dataSource.getDirectoryName()
						+ File.separator;
		} else {
			return AhDirTools.getCwpWebDir(domainName)
						+ dataSource.getCwpName()
						+ File.separator;
		}
	}
	
	private void modifyField(String[] lines, String id, String value) {
		if(lines == null || lines.length == 0) {
			return ;
		}
		
		if(id == null || value == null) {
			return ;
		}
		
		// search the Array for the element
		int lineNum = -1;
		
		for(int i=0; i<lines.length; i++) {
			if(lines[i] != null && lines[i].contains(id)) {
				lineNum = i;
				break;
			}
		}
		
		if(lineNum == -1) { // the element is not found
			return ;
		}
		
		int position = lines[lineNum].indexOf(id);
		StringBuffer line = new StringBuffer(lines[lineNum]);
		
		if(PAGE_ELEMENT_BACKGROUND.equals(id)) {
			/*
			 * background-image: url();
			 */
			line.insert(line.indexOf("background-image: url(") + "background-image: url(".length(), value);
		} else if(PAGE_ELEMENT_HEAD_IMAGE.equals(id)) {
			customizeHeadFoot(line, position, value);
		} else if(PAGE_ELEMENT_FOOT_IMAGE.equals(id)) {
			customizeHeadFoot(line, position, value);
		} else if(PAGE_ELEMENT_USER_POLICY.equals(id)) {
			/*
			 * <textarea name="textfield" cols="45" rows="8" id="userPolicy"></textarea>
			 */
			position += 12; // userPolicy">
			line.insert(position, getUserPolicy(value));
		} else if(PAGE_ELEMENT_NOTICE.equals(id)) {
			/*
			 * <p class="style5" align=left id="notice"></p>
			 */
			position += 8; // notice">
			line.insert(position, value);
		}  else if(PAGE_ELEMENT_PAUSE_TIME.equals(id)
				|| PAGE_ELEMENT_TRANSFER_URL.equals(id)) {
			line.replace(position, position + id.length(), value);
		} else if(PAGE_ELEMENT_TILE.equals(id)) {
			line.replace(position, position + id.length(), value);
		} else if(PAGE_ELEMENT_SIP_OK.equals(id)) {
			/*
			 * <div id="ok">
			 */
			position += "ok\">".length();
			line.insert(position, value);
		} else if(PAGE_ELEMENT_SIP_FINES.equals(id)) {
			/*
			 * <div id="fines">
			 */
			position += "fines\">".length();
			line.insert(position, value);
		} else if(PAGE_ELEMENT_SIP_BLOCK.equals(id)) {
			/*
			 * <div id="sipDeny">
			 */
			position += "sipDeny\">".length();
			line.insert(position, value);
		}
		
		lines[lineNum] = line.toString();
	}

	private void customizeCommonPage(String[] lines) {
		/*
		 * Secure Internal Portal
		 * if it is OEM system, do not add this title
		 */
		if(lines == null) {
			return ;
		}
		
		for(int i=0; i<lines.length; i++) {
			if(lines[i] == null) {
				continue;
			}
			
			if(lines[i].contains(PAGE_ELEMENT_H1_TITLE)) {
				if(NmsUtil.isHMForOEM()) {
					lines[i] = "";
				}
			}
			
			if(lines[i].contains(PAGE_ELEMENT_FOOT_IMAGE)){
				if(NmsUtil.isHMForOEM()) {
					lines[i] = "";
				}
			}
		}
	}
	
	private void customizeForeground(String[] lines, String color) {
		/*
		 * modify the color defined in css style
		 * 
		 * .style1 {
			font-family: Arial, Helvetica, sans-serif;
			font-weight: bold;
			font-size: 14px;
			color: ;
			}
		 */
		if(lines == null || color == null) {
			return ;
		}
		
		for(int i=0; i<lines.length; i++) {
			if(lines[i] == null) {
				continue;
			}
			
			if(lines[i].contains(PAGE_ELEMENT_FOREGROUND)) {
				StringBuilder line = new StringBuilder(lines[i]);
				
				line.insert(lines[i].indexOf(PAGE_ELEMENT_FOREGROUND) + 7, color);
				
				lines[i] = line.toString();
			}
		}
	}
	
	private String[] customizeRegistrationFields(String[] lines, Cwp dataSource) {
		if(lines == null || dataSource == null ) {
			return null;
		}
		
		if(dataSource.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_AUTOGENERATE) {
			return null;
		}
		
		// store source String lines into a LinkedList
		List<String> lineList = new LinkedList<String>();
		
		for(String line : lines) {
			if(line != null && line.contains(PAGE_ELEMENT_REGISTRATION_FIELDS)) {
				lineList.add(line);
				
				// insert the lines for registration fields
				List<CwpPageField> enabledFields = getEnabledPageFields(dataSource);
				
				if(enabledFields.size() > 0) {
					StringBuilder html = new StringBuilder();
					int countRequired = 0, countOptional = 0;
					lineList.add("<table>");
					for(CwpPageField field : enabledFields) {
						html.delete(0, html.length());
				        html.append("<tr><td>");
				        // country code
				        appendCountryCode(dataSource, html, field);
				        
						html.append("<input ");
						
						final String label = field.getLabel();
                        if(label == null) {
							continue;
						}
						
						if(label.toLowerCase().contains("phone")) {
							html.append("type=\"tel\" ");
							// country code
							if(isCountryCodeEnabled) {
							    html.append("style=\"width:228px;border-left-width:0;\" ");
							}
						} else if(label.toLowerCase().contains("mail")) {
							html.append("type=\"email\" ");
						} else {
							html.append("type=\"text\" ");
						}
						
                        if (label.toLowerCase().contains(CwpPageField.EMAIL.toLowerCase())
                                || label.toLowerCase().contains(CwpPageField.VISITING.toLowerCase())) {
                            html.append("maxlength=\"128\" ");
                        } else {
                            html.append("maxlength=\"32\" ");
                        }
						
						if(field.getRequired()) {
							countRequired++;
							if(!isOpenSSIDIDMSelfReg(dataSource.getRegistrationType())){
								html.append("name=\"field").append(countRequired).append("\" ");
							}else{
								html.append("name=\"").append(field.getLabelName()).append("\" ");
							}
							html.append("id=\"field").append(field.getFieldMark()).append("\" ");
							html.append("placeholder=\"").append(getFieldShowLabel(field,customizeLanguage)).append("*\" ");
							
							disableAutoComplete(label, html);
							
							html.append("required");
						} else {
							countOptional++;
							if(isOpenSSIDIDMSelfReg(dataSource.getRegistrationType())){
								if(!label.toLowerCase().contains(CwpPageField.COMMENT.toLowerCase())){
								    html.append("name=\"").append(field.getLabelName()).append("\" ");
								}else{
								    continue;
								}
							}else{
							    if(!label.toLowerCase().contains(CwpPageField.REPRESENTING.toLowerCase())) {
							        html.append("name=\"opt_field").append(countOptional).append("\" ");
							    } else {
							        continue;
							    }
							}
							html.append("id=\"opt_field").append(field.getFieldMark()).append("\" ");
							html.append("placeholder=\"").append(getFieldShowLabel(field,customizeLanguage)).append("\" ");
						}
						
						html.append("/><br />");
						html.append("</td></tr>");
						
						lineList.add(html.toString());
					}
					
					lineList.add("</table>");
				}
			} else {
				lineList.add(line);
			}
		}
		
		// turn the String list into String array and return
		String[] newLines = new String[lineList.size()];
		int lineNum = 0;
		
		for(String line : lineList) {
			newLines[lineNum++] = line;
		}
		
		return newLines;
	}

	private boolean isCountryCodeEnabled = false;
	
    public boolean isCountryCodeEnabled() {
        return isCountryCodeEnabled;
    }

    public void setCountryCodeEnabled(boolean isCountryCodeEnabled) {
        this.isCountryCodeEnabled = isCountryCodeEnabled;
    }

    private void appendCountryCode(Cwp dataSource, StringBuilder html,
            CwpPageField field) {
        if(field.getLabel().toLowerCase().contains("phone")
                && isOpenSSIDIDMSelfReg(dataSource.getRegistrationType())) {
            html.append("<input type=\"text\" class=\"countryCode\" name=\"countryCode\" value=\"+1\" maxlength=\"5\" autocomplete=\"off\" pattern=\"\\+[0-9]+\" readonly/>");
            isCountryCodeEnabled = true;
        }
    }

    private void disableAutoComplete(String label, StringBuilder html) {
        if(label.toLowerCase().contains(CwpPageField.FIRSTNAME.toLowerCase())
                || label.toLowerCase().contains(CwpPageField.LASTNAME.toLowerCase())) {
            html.append("autocomplete=\"off\" autocorrect=\"off\" autocapitalize=\"off\" spellcheck=\"false\" ");
        }
    }
	
	/**
	 * copy page resources to destination if the total size of directory 
	 * doesn't exceed the limit
	 *  
	 * @param dataSource -
	 * @param pageType -
	 * @param destinationPath -
	 * @param templatePath -
	 * @return -
	 * @author Joseph Chen
	 */
	private boolean copyPageResources(Cwp dataSource, 
									short pageType, 
									String destinationPath,
									String templatePath) {
		if(destinationPath == null 
				|| dataSource == null
				|| templatePath == null) {
			return false;
		}
		
		CwpPageCustomization pageCustomization = dataSource.getPageCustomization();
		
		if(pageCustomization == null) {
			return false;
		}
		
		String backgroundPath;
//		String headImagePath;
		String footImagePath;
		String backgroundName;
//		String headImageName;
		String footImageName;
		String imgLogin = DEFAULT_IMAGE_PATH + "login.png"; 
		String imgRegister = DEFAULT_IMAGE_PATH + "register.png"; 
		String imgbutton = DEFAULT_IMAGE_PATH + "button.png"; 
		String imgSuccess = DEFAULT_IMAGE_PATH + "success.png";
		String imgFail = DEFAULT_IMAGE_PATH + "fail.png"; 
		String jsCwpUtil = DEFAULT_RESOURCE_PATH + "hm.cwp.util.js"; 
		String jsJQueryMin = DEFAULT_RESOURCE_PATH + "jquery.min.js";
		
		if(pageType == PAGE_AUTHENTICATION 
				|| pageType == PAGE_REGISTRATION
				|| pageType == PAGE_BOTH
				|| pageType == PAGE_EULA
				|| pageType == PAGE_PPSK) {
			backgroundPath = getPageResource(pageCustomization, RESOURCE_TYPE_BACKGROUND_IMAGE, false);
//			headImagePath = getPageResource(pageCustomization, RESOURCE_TYPE_HEAD_IMAGE, false);
			footImagePath = getPageResource(pageCustomization, RESOURCE_TYPE_FOOT_IMAGE, false);
			backgroundName = pageCustomization.getBackgroundImage();
//			headImageName = pageCustomization.getHeadImage();
			footImageName = pageCustomization.getFootImage();
		} else if (pageType == PAGE_SUCCESS) {
			backgroundPath = getPageResource(pageCustomization, RESOURCE_TYPE_SUCCESS_BACKGROUND_IMAGE, false);
//			headImagePath = getPageResource(pageCustomization, RESOURCE_TYPE_SUCCESS_HEAD_IMAGE, false);
			footImagePath = getPageResource(pageCustomization, RESOURCE_TYPE_SUCCESS_FOOT_IMAGE, false);
			backgroundName = pageCustomization.getSuccessBackgroundImage();
//			headImageName = pageCustomization.getSuccessHeadImage();
			footImageName = pageCustomization.getSuccessFootImage();
		} else if (pageType == PAGE_FAILURE) {
			backgroundPath = getPageResource(pageCustomization, RESOURCE_TYPE_FAILURE_BACKGROUND_IMAGE, false);
//			headImagePath = getPageResource(pageCustomization, RESOURCE_TYPE_FAILURE_HEAD_IMAGE, false);
			footImagePath = getPageResource(pageCustomization, RESOURCE_TYPE_FAILURE_FOOT_IMAGE, false);
			backgroundName = pageCustomization.getFailureBackgroundImage();
//			headImageName = pageCustomization.getFailureHeadImage();
			footImageName = pageCustomization.getFailureFootImage();
		} else {
			/*
			 * do not need to copy page resources when creating USER_POLICY page
			 */
			return true;
		}
		
		Set<String> fileList = new HashSet<String>();
		fileList.add(templatePath);
		fileList.add(backgroundPath);
//		fileList.add(headImagePath);
		fileList.add(footImagePath);
		fileList.add(imgLogin);
		fileList.add(imgRegister);
		fileList.add(imgbutton);
		fileList.add(imgSuccess);
		fileList.add(imgFail);
		fileList.add(jsCwpUtil);
		fileList.add(jsJQueryMin);
		
		fileList.addAll(getCountryCodeFiles(dataSource));
		
		if(isFilesTooBig(destinationPath, fileList)) {
			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.filesTooBig",
					HiveApFileAction.CWP_RESOURCE_MAX_SIZE+ "K Bytes"));
			/*
			 * need to delete the newly created directory
			 */
			String newDirPath = AhDirTools.getCwpWebDir(domainName)
				+ dataSource.getCwpName()
				+ File.separator;
			
			if(dataSource.getId() == null && needDeleteDirectory(dataSource)) {
				try {
					FileManager.getInstance().deleteDirectory(newDirPath);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			return false;
		}
		
		try {
			FileManager.getInstance().copyFile(backgroundPath, destinationPath + backgroundName);
//			FileManager.getInstance().copyFile(headImagePath, destinationPath + headImageName);
			if(null != footImageName){
				FileManager.getInstance().copyFile(footImagePath, destinationPath + footImageName);
			}
			FileManager.getInstance().copyFile(imgLogin, destinationPath + "login.png");
			FileManager.getInstance().copyFile(imgRegister, destinationPath + "register.png");
			FileManager.getInstance().copyFile(imgbutton, destinationPath + "button.png");
			FileManager.getInstance().copyFile(imgSuccess, destinationPath + "success.png");
			FileManager.getInstance().copyFile(imgFail, destinationPath + "fail.png");
			FileManager.getInstance().copyFile(jsCwpUtil, destinationPath + "hm.cwp.util.js");
			FileManager.getInstance().copyFile(jsJQueryMin, destinationPath + "jquery.min.js");
			
			copyCountryCodeFiles(dataSource, destinationPath);
		} catch (Exception exception) {
			log.error("createPage", "failed to copy resource file to " + destinationPath, exception);
			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
			return false;
		}
		
		return true;
	}
	
	private void copyCountryCodeFiles(Cwp dataSource, String destPath) throws IllegalArgumentException, IOException, BeNoPermissionException {
	    if(isOpenSSIDIDMSelfReg(dataSource.getRegistrationType()) && isCountryCodeEnabled) {
	        FileManager.getInstance().copyFile(DEFAULT_IMAGE_PATH + "arrow.png", destPath + "arrow.png");
	        FileManager.getInstance().copyFile(DEFAULT_IMAGE_PATH + "custom-flags32.png", destPath + "custom-flags32.png");
	        FileManager.getInstance().copyFile(DEFAULT_RESOURCE_PATH + DEFAULT_RESORUCE_IDM_DIRECTORY 
	                + File.separator + DEFAULT_RESORUCE_DIRECTORY + File.separator + "custom-flags32.css", destPath + "custom-flags32.css");
	    }
    }

    private List<String> getCountryCodeFiles(Cwp dataSource) {
	    List<String> pathOfFiles = new ArrayList<>(); 
	    if(isOpenSSIDIDMSelfReg(dataSource.getRegistrationType()) && isCountryCodeEnabled) {
	        pathOfFiles.add(DEFAULT_IMAGE_PATH + "arrow.png");
	        pathOfFiles.add(DEFAULT_IMAGE_PATH + "custom-flags32.png");
	        pathOfFiles.add(DEFAULT_RESOURCE_PATH + DEFAULT_RESORUCE_IDM_DIRECTORY 
                    + File.separator + DEFAULT_RESORUCE_DIRECTORY + File.separator + "custom-flags32.css");
	    }
        return pathOfFiles;
    }

    /**
	 * check if the total size of files in a directory exceeds the limit 
	 * @param path 			the given path
	 * @param fileList		the files which are going to be copied into the directory
	 * @return -
	 * @author Joseph Chen
	 */
	private boolean isFilesTooBig(String path, Set<String> fileList) {
		if(path == null) {
			return false;
		}
		
		File target = new File(path);
		long totalSize = 0;
		
		/*
		 * calculate the size of the existing directory
		 */
		if(target.isDirectory()) {
			File[] files = target.listFiles();
			
			if(files != null) {
				for(File file : files) {
					totalSize += file.length();
					
					/*
					 * remove the file in the fileList with the same name
					 */
					for(String fileName : fileList) {
						if(fileName == null) {
							continue;
						}
						
						if(fileName.contains(file.getName())){
							fileList.remove(fileName);
							break;
						}
					}
				}
			}
		} else {
			totalSize = target.length();
		}
		
		/*
		 * add the size of files into the total size
		 */
		for(String fileName : fileList) {
			totalSize += new File(fileName).length();
		}

		return totalSize > HiveApFileAction.CWP_RESOURCE_MAX_SIZE * 1024;
	}
	
	/**
	 * check if the name has been used by imported directory in CwpAction
	 *
	 * @param name -
	 * @return -
	 * @author Joseph Chen
	 */
	private boolean isNameImported(String name) {
		if(name == null) {
			return false;
		}
		
		List<String> existedNames = null;
		
		try {
			existedNames = FileManager.getInstance().getFileAndSubdirectoryNames(AhDirTools.getCwpWebDir(domainName),
					BeOsLayerModule.ONLYDIRECTORY, false);
		} catch (Exception exception) {
			log.error("isNameImported", "failed to get the existed names", exception);
		} 
		
		if(existedNames == null) {
			return false;
		}
		
		/*
		 * the existed name could contain the name of VHM, like aName(home)
		 */
		for(String existedName : existedNames) {
			if(name.equals(existedName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getInputResult() {
		if(this.isJsonMode()) {
			if(this.isContentShownInDlg()) {
				return "cwpDialog";
			} else {
				return "cwpDrawer";
			}
		} else {
			if (this.getLastExConfigGuide() != null){
				 return "ssidEx";
			} else {
				return INPUT;
			}
		}
	}
	
	/**
	 * get the RGB color in the form needed in JSP/HTML page
	 * 
	 * @param state -
	 * @param type -
	 * @return string 
	 * @author Joseph Chen
	 */
	private String getForegroundColorRGB(short state, short type) {
		// get original BO from database
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			return "null"; 
		}
		
		if(type == PAGE_INDEX) {
			return bo.getPageCustomization().getForegroundColor();
		} else if(type == PAGE_SUCCESS) {
			return bo.getPageCustomization().getSuccessForegroundColor();
		} else if(type == PAGE_FAILURE) {
			return bo.getPageCustomization().getFailureForegroundColor();
		}
		
		return "null";
	}
	
	private void setWebPages(Cwp dataSource) {
		/*
		 * after auto-generating web pages, set some values to 
		 * legacy parameters: directoryName, webPageName, resultPageName
		 * 						requestField, numberField
		 */
		if(dataSource == null) {
			return ;
		}
		
		/*
		 * directory name
		 */
		
		String tempDir = dataSource.getDirectoryName();
		
		if(dataSource.isShowSuccessPage()
				&& dataSource.getSuccessPageSource() == Cwp.SUCCESS_PAGE_SOURCE_IMPORT) {
			dataSource.setDirectoryName(this.getSuccessDirectoryName());
		} else if(dataSource.isShowFailurePage()
				&& dataSource.getFailurePageSource() == Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
			dataSource.setDirectoryName(this.getFailureDirectoryName());
		} else {
			dataSource.setDirectoryName(dataSource.getCwpName());
		}
		
		if(dataSource.getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL) {
			if(dataSource.getWebPageSource() == Cwp.WEB_PAGE_SOURCE_IMPORT) {
				dataSource.setDirectoryName(tempDir);
			}
		} 
		
		/*
		 * login page
		 */
		if(dataSource.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_IMPORT) {
			if(dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED) {
				dataSource.setWebPageName(AUTO_GENERATED_PAGE_AUTHENTICATION);
			} else if (dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED) {
				dataSource.setWebPageName(AUTO_GENERATED_PAGE_REGISTRATION);
			} else if (dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) {
				dataSource.setWebPageName(AUTO_GENERATED_PAGE_BOTH);
			} else if (dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA) {
				dataSource.setWebPageName(AUTO_GENERATED_PAGE_EULA);
			} else if (dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK) {
					dataSource.setWebPageName(AUTO_GENERATED_PAGE_PPSK);					
				}
			}
		
		/*
		 * success page name
		 */
		if(dataSource.isShowSuccessPage()
				&& dataSource.getSuccessPageSource() != Cwp.SUCCESS_PAGE_SOURCE_IMPORT) {
			dataSource.setResultPageName(AUTO_GENERATED_RESULT_PAGE);
		}

		/*
		 * failure page name
		 */
		if(dataSource.isShowFailurePage()
				&& dataSource.getFailurePageSource() != Cwp.FAILURE_PAGE_SOURCE_IMPORT) {
			dataSource.setFailurePageName(AUTO_GENERATED_FAILURE_PAGE);
		}
		
		/*
		 * fields
		 */
		if(dataSource.getWebPageSource() != Cwp.WEB_PAGE_SOURCE_IMPORT) {
			if(dataSource.getRegistrationType()	== Cwp.REGISTRATION_TYPE_REGISTERED 
					|| dataSource.getRegistrationType()	== Cwp.REGISTRATION_TYPE_BOTH
					|| (dataSource.getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK && getDataSource().getPpskServerType() == Cwp.PPSK_SERVER_TYPE_REG)) {
			    // add PPSK option for IDM PPSK server
				int requestField = 0;
				int optionalField = 0;
				
				for(CwpPageField field : getEnabledPageFields(dataSource)) {
					if(field.getRequired()) {
						requestField++;
					} else {
						optionalField++;
					}
				}
				
				dataSource.setRequestField(requestField);
				dataSource.setNumberField(optionalField);
			}	
		}
	}
	
	private boolean isDefaultResource(String fileName) {
		if(fileName == null) {
			return false;
		}
		
		String[] defaultFiles = {CwpPageCustomization.DEFAULT_3D_BACKGROUND_IMAGE,
				CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_3D,
				CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_DARK,
				CwpPageCustomization.DEFAULT_BACKGROUND_IMAGE_LIGHT,
				CwpPageCustomization.DEFAULT_FOOT_IMAGE,
				CwpPageCustomization.DEFAULT_HEAD_IMAGE,
				CwpPageCustomization.DEFAULT_USER_POLICY
				};
		
		for(String defaultFile : defaultFiles) {
			if(defaultFile.equals(fileName)) {
				return true;
			}
		}
		
		return false;
	}
	
	private void customizeHeadFoot(StringBuffer line, int position, String value) {
		if(line == null || value == null) {
			return ;
		}
		
		/*
		 * <td><img alt="" id="headImage" src="" ></td>
		 * <td><img alt="" id="footImage" src="" ></td>
		 */
		position += 16; // headImage" src="
		line.insert(position, value);
	}
	
	private short foregroundColorR = 255;
	
	private short foregroundColorG = 255;
	
	private short foregroundColorB = 255;
	
	/**
	 * getter of foregroundColorR
	 * @return the foregroundColorR
	 */
	public short getForegroundColorR() {
		return foregroundColorR;
	}

	/**
	 * setter of foregroundColorR
	 * @param foregroundColorR the foregroundColorR to set
	 */
	public void setForegroundColorR(short foregroundColorR) {
		this.foregroundColorR = foregroundColorR;
	}

	/**
	 * getter of foregroundColorG
	 * @return the foregroundColorG
	 */
	public short getForegroundColorG() {
		return foregroundColorG;
	}

	/**
	 * setter of foregroundColorG
	 * @param foregroundColorG the foregroundColorG to set
	 */
	public void setForegroundColorG(short foregroundColorG) {
		this.foregroundColorG = foregroundColorG;
	}

	/**
	 * getter of foregroundColorB
	 * @return the foregroundColorB
	 */
	public short getForegroundColorB() {
		return foregroundColorB;
	}

	/**
	 * setter of foregroundColorB
	 * @param foregroundColorB the foregroundColorB to set
	 */
	public void setForegroundColorB(short foregroundColorB) {
		this.foregroundColorB = foregroundColorB;
	}

	/**
	 * struts download support
	 *
	 * @return -
	 * @author Joseph Chen
	 */
	public String getLocalFileName()
	{
		if("exportWebPages".equals(operation)) {
			return DEFAULT_CWP_WEB_PAGES;
		} else {
			return null;
		}
	}

	/**
	 * struts download support
	 *
	 * @return -
	 * @throws Exception -
	 * @author Joseph Chen
	 */
	public InputStream getInputStream() throws Exception
	{
		if("exportWebPages".equals(operation)) {
			String filePath = DEFAULT_RESOURCE_PATH + DEFAULT_CWP_WEB_PAGES;
			return new FileInputStream(filePath);
		} else {
			return null;
		}
	}
	
	private Long certificate;

	/**
	 * getter of certificate
	 * @return the certificate
	 */
	public Long getCertificate() {
		return certificate;
	}

	/**
	 * setter of certificate
	 * @param certificate the certificate to set
	 */
	public void setCertificate(Long certificate) {
		this.certificate = certificate;
	}
	
	private List<CheckItem> certificateList;

	/**
	 * getter of certificateList
	 * @return the certificateList
	 */
	public List<CheckItem> getCertificateList() {
		return certificateList;
	}

	/**
	 * setter of certificateList
	 * @param certificateList the certificateList to set
	 */
	public void setCertificateList(List<CheckItem> certificateList) {
		this.certificateList = certificateList;
	}
	
	protected List<CheckItem> vlanIdList;

	/**
	 * getter of vlanIdList
	 * @return the vlanIdList
	 */
	public List<CheckItem> getVlanIdList() {
		return vlanIdList;
	}

	/**
	 * setter of vlanIdList
	 * @param vlanIdList the vlanIdList to set
	 */
	public void setVlanIdList(List<CheckItem> vlanIdList) {
		this.vlanIdList = vlanIdList;
	}

	protected Long vlanId;

	/**
	 * getter of vlanId
	 * @return the vlanId
	 */
	public Long getVlanId() {
		return vlanId;
	}

	/**
	 * setter of vlanId
	 * @param vlanId the vlanId to set
	 */
	public void setVlanId(Long vlanId) {
		this.vlanId = vlanId;
	}

	private String inputVlanValue;

	/**
	 * getter of inputVlanValue
	 * @return the inputVlanValue
	 */
	public String getInputVlanValue() {
		if (null != vlanId) {
			for (CheckItem item : vlanIdList) {
				if (item.getId().longValue() == vlanId.longValue()) {
					inputVlanValue = item.getValue();
					break;
				}
			}
		}

		return inputVlanValue;
	}

	/**
	 * setter of inputVlanValue
	 * @param inputVlanValue the inputVlanValue to set
	 */
	public void setInputVlanValue(String inputVlanValue) {
		this.inputVlanValue = inputVlanValue;
	}
	
	public int getExternalURLLength() {
		return 128;
	}

	public String getOriginalBackgroundTile() {
		return getBackgroundTile(STATE_ORIGINAL, PAGE_INDEX);
	}
	
	public String getCurrentBackgroundTile() {
		return getBackgroundTile(STATE_CURRENT, PAGE_INDEX);
	}
	
	public String getOriginalSuccessBackgroundTile() {
		return getBackgroundTile(STATE_ORIGINAL, PAGE_SUCCESS);
	}
	
	public String getOriginalFailureBackgroundTile() {
		return getBackgroundTile(STATE_ORIGINAL, PAGE_FAILURE);
	}
	
	public String getCurrentSuccessBackgroundTile() {
		return getBackgroundTile(STATE_CURRENT, PAGE_SUCCESS);
	}
	
	public String getCurrentFailureBackgroundTile() {
		return getBackgroundTile(STATE_CURRENT, PAGE_FAILURE);
	}
	
	private String getBackgroundTile(short state, short pageType) {
		// get original BO from database
		Cwp bo = null;
		
		try {
			if(state == STATE_ORIGINAL) {
				if(getDataSource().getId() != null)
					bo = (Cwp) findBoById(boClass, getDataSource().getId(), this);
			}
			else {
				bo = getDataSource();
			}
		} catch(Exception e) {
			
		}
		
		if(bo == null) {
			return "no-repeat";
		}
		
		if(pageType == PAGE_INDEX) {
			return bo.getPageCustomization().getTileBackgroundString();
		} else if(pageType == PAGE_SUCCESS) {
			return bo.getPageCustomization().getTileSuccessBackgroundString();
		} else if(pageType == PAGE_FAILURE) {
			return bo.getPageCustomization().getTileFailureBackgroundString();
		}
		
		return "no-repeat";
	}

	public String getTypeFromSsid() {
		return typeFromSsid;
	}

	public void setTypeFromSsid(String typeFromSsid) {
		this.typeFromSsid = typeFromSsid;
	}
	
    public String getRedirectDisplay() {
    	if(getDataSource().getRegistrationType() 
    			== Cwp.REGISTRATION_TYPE_EXTERNAL) {
    		return "";
    	} else {
    		return getDataSource().getServerType()
					== Cwp.CWP_EXTERNAL ? "" : "none";
    	}
	}

	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		Cwp source = QueryUtil.findBoById(Cwp.class,
				paintbrushSource, this);

		if (null == source) {
			return null;
		}

		List<Cwp> list = QueryUtil.executeQuery(Cwp.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		
		if (list.isEmpty()) {
			return null;
		}
		
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		
		for (Cwp profile : list) {
			if (profile.isDefaultFlag()) {
				continue;
			}
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			Cwp cwp = source.clone();
			
			if (null == cwp) {
				continue;
			}
			
			cwp.setId(profile.getId());
			cwp.setVersion(profile.getVersion());
			cwp.setCwpName(profile.getCwpName());
			cwp.setOwner(profile.getOwner());
			cwp.setDirectoryName(profile.getDirectoryName());
			cwp.setDefaultFlag(false);
			hmBos.add(cwp);
		}
	
		return hmBos;
	}
	
	protected void paintbrushOperation() throws Exception {
		Set<Long> destinationIds = null;
		if (allItemsSelected) {
			this.getSessionFiltering();
			List<Long> list = (List<Long>) QueryUtil.executeQuery(
					"select id from " + boClass.getSimpleName(), null,
					filterParams, domainId);
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && list.removeAll(defaultIds)) {
				addActionMessage(MgrUtil
						.getUserMessage("error.use.paintbrush.default.item"));
			}
			destinationIds = new HashSet<Long>(list);
		} else if (getAllSelectedIds() != null && !getAllSelectedIds().isEmpty()) {
			Collection<Long> defaultIds = getDefaultIds();
			if (defaultIds != null && getAllSelectedIds().removeAll(defaultIds)) {
				addActionMessage(MgrUtil
						.getUserMessage("error.use.paintbrush.default.item"));
			}
			destinationIds = getAllSelectedIds();
		}
		log.info("execute", "operation:" + operation + ", paintbrushSource:"
				+ paintbrushSource + ", destination:"
				+ (destinationIds == null ? "null" : destinationIds));
		if (null == paintbrushSource || null == destinationIds
				|| destinationIds.isEmpty()) {
			setAllSelectedIds(null);
			setPaintbrushSource(null);
			return;
		}
		List<HmBo> bos = paintbrushBos(paintbrushSource, destinationIds);
		if (null != bos && !bos.isEmpty()) {
			Map<Long, Date> versions = new HashMap<Long, Date>();
			for (HmBo bo : bos) {
				versions.put(bo.getId(), bo.getVersion());
			}
			try {
				Collection<HmBo> hmBos = BoMgmt.bulkUpdateBos(bos,
						getUserContext(), getSelectedL2FeatureKey());
				for (HmBo bo : hmBos) {
					// update use policy
//					this.updateUsePolicy((Cwp)bo);
					// create web pages
					createCwpPage((Cwp)bo, true);
					// generate an event to configuration indication process
					HmBeEventUtil
							.eventGenerated(new ConfigurationChangedEvent(
									bo,
									ConfigurationChangedEvent.Operation.UPDATE,
									versions.get(bo.getId())));
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{getLastTitle(),bo.getLabel()}));
					addActionMessage(MgrUtil.getUserMessage(OBJECT_UPDATED, bo
							.getLabel()));
				}
			} catch (Exception e) {
				for (HmBo bo : bos) {
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.base.operation.update",new String[]{getLastTitle(),bo.getLabel()}));
				}
				throw e;
			}
		}
		setAllSelectedIds(null);
		setPaintbrushSource(null);
	}
	
	public EnumItem[] getEnumPasswordEncrypt() {
		return Cwp.ENUM_PASSWORD_ENCRYPTION;
	}
	
	private String hideCreateItem = "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}
	
	public void setHideCreateItem(String hide) {
		this.hideCreateItem = hide;
	}

	private String hideNewButton = "";

	public String getHideNewButton() {
		return hideNewButton;
	}
	
	public void setHideNewButton(String hide) {
		this.hideNewButton = hide;
	}
	
	private List<CheckItem> ipList;
	
	private Long WGIp;
	
	private String WGIpValue;
	
	private byte serviceId;
	
	private int protocolNumber;
	
	private int port;
	
	private Collection<String> itemIndices;
	
	/**
	 * getter of itemIndices
	 * @return the itemIndices
	 */
//	public Collection<String> getItemIndices() {
//		return itemIndices;
//	}

	/**
	 * setter of itemIndices
	 * @param itemIndices the itemIndices to set
	 */
	public void setItemIndices(Collection<String> itemIndices) {
		this.itemIndices = itemIndices;
	}

	/**
	 * getter of ipList
	 * @return the ipList
	 */
	public List<CheckItem> getIpList() {
		return ipList;
	}

	/**
	 * setter of ipList
	 * @param ipList the ipList to set
	 */
	public void setIpList(List<CheckItem> ipList) {
		this.ipList = ipList;
	}	
	
	/**
	 * getter of wGIp
	 * @return the wGIp
	 */
	public Long getWGIp() {
		return WGIp;
	}

	/**
	 * setter of wGIp
	 * @param ip the wGIp to set
	 */
	public void setWGIp(Long ip) {
		WGIp = ip;
	}

	/**
	 * getter of wGIpValue
	 * @return the wGIpValue
	 */
	public String getWGIpValue() {
		if (null != WGIp) {
			for (CheckItem item : ipList) {
				if (item.getId().longValue() == WGIp.longValue()) {
					WGIpValue = item.getValue();
					break;
				}
			}
		}
		
		return WGIpValue;
	}

	/**
	 * setter of wGIpValue
	 * @param ipValue the wGIpValue to set
	 */
	public void setWGIpValue(String ipValue) {
		WGIpValue = ipValue;
	}

	
	/**
	 * getter of serviceId
	 * @return the serviceId
	 */
	public byte getServiceId() {
		return serviceId;
	}

	/**
	 * setter of serviceId
	 * @param serviceId the serviceId to set
	 */
	public void setServiceId(byte serviceId) {
		this.serviceId = serviceId;
	}
	
	/**
	 * getter of protocolNumber
	 * @return the protocolNumber
	 */
	public int getProtocolNumber() {
		return protocolNumber;
	}

	/**
	 * setter of protocolNumber
	 * @param protocolNumber the protocolNumber to set
	 */
	public void setProtocolNumber(int protocolNumber) {
		this.protocolNumber = protocolNumber;
	}

	/**
	 * getter of port
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * setter of port
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public int getLoginURLLength() {
		return getAttributeLength("loginURL");
	}

	public int getSharedSecretLength() {
		return getAttributeLength("sharedSecret");
	}
	
	public Range getSessionAlertRange() {
		return getAttributeRange("sessionAlert");
	}

	public EnumItem[] getServiceList() {
		return WalledGardenItem.ENUM_SERVICE;
	}
	
	public int getGridCount() {
		return getDataSource().getWalledGarden().size() == 0 ? 3 : 0;
	}
	
	public String getServiceStatus() {
		if(this.serviceId == WalledGardenItem.SERVICE_PROTOCOL) {
			return "";
		} else {
			return "disabled";
		}
	}

    private boolean existBroadcastAddress(IpAddress server, SingleTableItem item) {
        if (null != server) {
            for (SingleTableItem sItem : server.getItems()) {
                if (existBroadcastAddress(null, sItem)) {
                    return true;
                }
            }
        } else if (null != item) {
            if (item.getType() == IpAddress.TYPE_IP_ADDRESS
                    && StringUtils.isNotBlank(item.getIpAddress())
                    && item.getIpAddress().equals("255.255.255.255")) {
                return true;
            }
        }
        return false;
    }
	private void addWalledGardenItem() throws Exception {
		List<WalledGardenItem> walledGarden = getDataSource().getWalledGarden();
		IpAddress server = null;
		jsonObject = new JSONObject();
		
		if(WGIp != null && WGIp != -1) {
			server = this.findBoById(IpAddress.class, WGIp, this);
			// check the ip address
			if(existBroadcastAddress(server, null)) {
                jsonObject.put("ok", false);
                jsonObject.put("msg", MgrUtil.getUserMessage(
                        "error.config.cwp.walled-garden.noAllow.broadcast"));
                return;
			}
		} else {
			SingleTableItem ipObj = NetTool.getIpObjectByInput(WGIpValue, true);
			// check the ip address
			if(existBroadcastAddress(null, ipObj)) {
			    jsonObject.put("ok", false);
			    jsonObject.put("msg", MgrUtil.getUserMessage(
			            "error.config.cwp.walled-garden.noAllow.broadcast"));
			    return;
			}
			if (null != ipObj) {
				server = CreateObjectAuto.createNewIP(ipObj.getIpAddress(), ipObj.getType(), getDomain(), "For CWP : " + getDataSource().getCwpName(), ipObj.getNetmask());
			}
		}
			
		
		if(walledGarden == null) {
			walledGarden = new ArrayList<WalledGardenItem>();
			getDataSource().setWalledGarden(walledGarden);
		} else {
			/*
			 * 63 servers in a walled-garden
			 */
			int count = countServer(walledGarden);
			
			if(count > WalledGardenItem.MAX_SERVER_COUNT) {
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.cwp.server.exceedCount",
						String.valueOf(WalledGardenItem.MAX_SERVER_COUNT)));
				
				return ;
			} else if(count == WalledGardenItem.MAX_SERVER_COUNT) {
				if(!inWalledGarden(walledGarden, server)) {
					jsonObject.put("ok", false);
					jsonObject.put("msg", MgrUtil.getUserMessage("error.config.cwp.server.exceedCount",
							String.valueOf(WalledGardenItem.MAX_SERVER_COUNT)));
					return ;
				}
			}
			
			/*
			 * 8 services in a server 
			 */
			if(countService(walledGarden, server) >= WalledGardenItem.MAX_SERVICE_COUNT) {
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.cwp.service.exceedCount",
						String.valueOf(WalledGardenItem.MAX_SERVICE_COUNT)));
				return ;
			}
		}
		
		WalledGardenItem item = new WalledGardenItem();
		
		item.setServer(server);
		item.setService(getServiceId());
		
		if(getServiceId() == WalledGardenItem.SERVICE_PROTOCOL) {
			item.setProtocol(getProtocolNumber());
			item.setPort(getPort());
		}
		
		item.setItemId(walledGarden.size() + 1);
		
		/*
		 * existence check
		 */
		if(!walledGarden.contains(item)) {
			walledGarden.add(item);
			jsonObject.put("ok", true);
			jsonObject.put("itemId", item.getItemId());
			jsonObject.put("serverId", item.getServer().getId());
			jsonObject.put("server", item.getServer().getAddressName());
			jsonObject.put("service", item.getServiceName());
			jsonObject.put("protocol", item.getProtocolValue());
			jsonObject.put("port", item.getPortValue());
		} else {
			jsonObject.put("ok", false);
			jsonObject.put("msg", MgrUtil.getUserMessage("error.config.cwp.service.existed"));
		}
	}
	
	private int countService(List<WalledGardenItem> walledGarden, IpAddress server) {
		if(walledGarden == null 
				|| server == null) {
			return 0;
		}
		
		int count = 0;
		
		for(WalledGardenItem item : walledGarden) {
			if(item == null) {
				continue;
			}
			
			if(server.equals(item.getServer())) {
				count++;
			}
		}
		
		return count;
	}
	
	private boolean inWalledGarden(List<WalledGardenItem> walledGarden, IpAddress server) {
		if(walledGarden == null 
				|| server == null) {
			return false;
		}
		
		for(WalledGardenItem item : walledGarden) {
			if(item == null) {
				continue;
			}
			
			if(server.equals(item.getServer())) {
				return true;
			}
		}
		
		return false;
	}
	
	private int countServer(List<WalledGardenItem> walledGarden) {
		if(walledGarden == null) {
			return 0;
		}
		
		List<WalledGardenItem> items = new ArrayList<WalledGardenItem>();

		for (WalledGardenItem item : walledGarden) {
			IpAddress ip = item.getServer();

			boolean existed = false;

			for (WalledGardenItem it : items) {
				if (it.getServer().getAddressName().equals(ip.getAddressName())) {
					existed = true;
					break;
				}
			}

			if (!existed) {
				items.add(item);
			}
		}
		
		return items.size();
	}
	
	private void removeWalledGardenItems() {
		Collection<WalledGardenItem> items = findItemsToRemove();
		getDataSource().getWalledGarden().removeAll(items);
		updateOrder();
		
		/*
		 * return left items
		 */
		jsonArray = new JSONArray();
		
		try {
			for(WalledGardenItem item : getDataSource().getWalledGarden()) {
				jsonObject = new JSONObject();
				
				jsonObject.put("itemId", item.getItemId());
				jsonObject.put("server", item.getServer().getAddressName());
				jsonObject.put("service", item.getServiceName());
				jsonObject.put("protocol", item.getProtocolValue());
				jsonObject.put("port", item.getPortValue());
				
				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
		}
	}
	
	private Collection<WalledGardenItem> findItemsToRemove() {
		/*
		 * no one to remove
		 */
		if(this.itemIndices == null) {
			return null;
		}
		
		Collection<WalledGardenItem> items = new ArrayList<WalledGardenItem>();
		
		for(String itemId : itemIndices) {
			try {
				int index = Integer.parseInt(itemId);
				
				for(WalledGardenItem item : getDataSource().getWalledGarden()) {
					if(item.getItemId() == index) {
						items.add(item);
						break;
					}
				}
			} catch(NumberFormatException nfe) {
				
			}
		}
		
		return items;
	}
	
	private void updateOrder() {
		Collection<WalledGardenItem> items = getDataSource().getWalledGarden();
		
		if(items == null) {
			return ;
		}
		
		int i=1;
		
		for(WalledGardenItem item : items) {
			item.setItemId(i++);
		}
	}
	
	public String getPasswordDisplay() {
		return getDataSource().getPasswordEncryption() == Cwp.PASSWORD_ENCRYPTION_SHARED
				? "" : "none";
	}
	
	private Range getRange(Class<?> aClass, String name) {
		try {
			Field field = aClass.getDeclaredField(name);
			Range range = field.getAnnotation(Range.class);
			
			if (range != null) {
				log.debug("getAttributeRange", "Attribute '" + name + "' min: "
						+ range.min() + ", max: " + range.max());
				return range;
			}
		} catch (NoSuchFieldException nsfe) {
			log.error("getAttributeRange", "Attribute '" + name
					+ "' does not exist in class '" + boClass.getName() + "'.");
		}
		
		return null;
	}
	
	private Range getWalledGardenRange(String name) {
		return getRange(WalledGardenItem.class, name);
	}
	
	public Range getWalledGardenPortRange() {
		return getWalledGardenRange("port");
	}
	
	public Range getWalledGardenProtocolRange() {
		return getWalledGardenRange("protocol");
	}
	
	public boolean getAuthMethodDisabled() {
		return getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL
				&& isCommonStyle()
				&& getDataSource().getPasswordEncryption() == Cwp.PASSWORD_ENCRYPTION_SHARED;
	}
	
	public String getSuccessExternalDisplay() {
		if(getDataSource().getSuccessRedirection() == Cwp.SUCCESS_REDIRECT_EXTERNAL) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getFailureExternalDisplay() {
		if(getDataSource().getFailureRedirection() == Cwp.FAILURE_REDIRECT_EXTERNAL) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getSuccessPageSourceDisplay() {
		return getDataSource().isShowSuccessPage() ? "" : "none";
	}
	
	public String getFailurePageSourceDisplay() {
		return getDataSource().isShowFailurePage() ? "" : "none";
	}

	public String getFailurePageCustomDisplay() {
		return getDataSource().isUseLoginAsFailure() ? "none" : "";
	}

	public String getLoginAsFailureDisplay() {
		return getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL
			|| getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_PPSK ? "none" : "";
	}

	/**
	 * getter of successPageRadioType
	 * @return the successPageRadioType
	 */
	public String getSuccessPageRadioType() {
		byte source = getDataSource().getSuccessPageSource() ;
		String type = "";
		
		switch (source) {
		case Cwp.SUCCESS_PAGE_SOURCE_CUSTOMIZE:
			type = "customize";
			break;
		case Cwp.SUCCESS_PAGE_SOURCE_IMPORT:
			type = "import";
			break;
		default:
			break;
		}
		
		return type;
	}

	/**
	 * setter of successPageRadioType
	 * @param successPageRadioType the successPageRadioType to set
	 */
	public void setSuccessPageRadioType(String successPageRadioType) {
		this.successPageRadioType = successPageRadioType;
	}
	
	/**
	 * getter of failurePageRadioType
	 * @return the failurePageRadioType
	 */
	public String getFailurePageRadioType() {
		byte source = getDataSource().getFailurePageSource() ;
		String type = "";
		
		switch (source) {
		case Cwp.FAILURE_PAGE_SOURCE_CUSTOMIZE:
			type = "customize";
			break;
		case Cwp.FAILURE_PAGE_SOURCE_IMPORT:
			type = "import";
			break;
		default:
			break;
		}
		
		return type;
	}

	/**
	 * setter of failurePageRadioType
	 * @param failurePageRadioType the failurePageRadioType to set
	 */
	public void setFailurePageRadioType(String failurePageRadioType) {
		this.failurePageRadioType = failurePageRadioType;
	}

	private String successDirectoryName = null;
	
	private String failureDirectoryName = null;

	/**
	 * getter of successDirectoryName
	 * @return the successDirectoryName
	 */
	public String getSuccessDirectoryName() {
		return successDirectoryName;
	}

	/**
	 * setter of successDirectoryName
	 * @param successDirectoryName the successDirectoryName to set
	 */
	public void setSuccessDirectoryName(String successDirectoryName) {
		this.successDirectoryName = successDirectoryName;
	}
	
	/**
	 * getter of failureDirectoryName
	 * @return the failureDirectoryName
	 */
	public String getFailureDirectoryName() {
		return failureDirectoryName;
	}

	/**
	 * setter of failureDirectoryName
	 * @param failureDirectoryName the failureDirectoryName to set
	 */
	public void setFailureDirectoryName(String failureDirectoryName) {
		this.failureDirectoryName = failureDirectoryName;
	}

	public Range getSuccessDelayRange() {
		return this.getAttributeRange("successDelay");
	}
	
	public Range getFailureDelayRange() {
		return this.getAttributeRange("failureDelay");
	}
	
	public boolean isCommonStyle() {
		return NmsUtil.isEcwpDefault();
	}
	
	public boolean isDepaulStyle() {
		return NmsUtil.isEcwpDepaul();
	}
	
	public boolean isNnuStyle() {
		return NmsUtil.isEcwpNnu();
	}
	
	/*
	 * reassociate; direct-access;
	 */
	private String reassociateRadioType;

	/**
	 * getter of reassociateRadioType
	 * @return the reassociateRadioType
	 */
	public String getReassociateRadioType() {
		return getDataSource().isNeedReassociate() ? "reassociate" : "direct-access";
	}

	/**
	 * setter of reassociateRadioType
	 * @param reassociateRadioType the reassociateRadioType to set
	 */
	public void setReassociateRadioType(String reassociateRadioType) {
		this.reassociateRadioType = reassociateRadioType;
	}
		
	public String getWalledGardenDisplay() {
		if(getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_EXTERNAL) {
			return "";
		} else {
			if(getDataSource().getServerType() == Cwp.CWP_EXTERNAL) {
				return "";
			}
		}
		
		return "none";
	}
	
	private String backgroundImage;
	private boolean tileBackgroundImage;
	private String foregroundColor;
	private String headerImage;
	private String footerImage;
	private String customPage;
	private byte registType;
//	private String usePolicy;
	private String notice;
	private String librarySIPStatus;
	private String librarySIPFines;
	/*
	 * in failure page
	 */
	private String librarySIPBlock;
	
	/**
	 * getter of backgroundImage
	 * @return the backgroundImage
	 */
	public String getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * setter of backgroundImage
	 * @param backgroundImage the backgroundImage to set
	 */
	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	
	/**
	 * getter of tileBackgroundImage
	 * @return the tileBackgroundImage
	 */
	public boolean isTileBackgroundImage() {
		return tileBackgroundImage;
	}

	/**
	 * setter of tileBackgroundImage
	 * @param tileBackgroundImage the tileBackgroundImage to set
	 */
	public void setTileBackgroundImage(boolean tileBackgroundImage) {
		this.tileBackgroundImage = tileBackgroundImage;
	}

	/**
	 * getter of foregroundColor
	 * @return the foregroundColor
	 */
	public String getForegroundColor() {
		return foregroundColor;
	}

	/**
	 * setter of foregroundColor
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(String foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * getter of headerImage
	 * @return the headerImage
	 */
	public String getHeaderImage() {
		return headerImage;
	}

	/**
	 * setter of headerImage
	 * @param headerImage the headerImage to set
	 */
	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	/**
	 * getter of footerImage
	 * @return the footerImage
	 */
	public String getFooterImage() {
		return footerImage;
	}

	/**
	 * setter of footerImage
	 * @param footerImage the footerImage to set
	 */
	public void setFooterImage(String footerImage) {
		this.footerImage = footerImage;
	}

	/**
	 * getter of customPage
	 * @return the customPage
	 */
	public String getCustomPage() {
		return customPage;
	}

	/**
	 * setter of customPage
	 * @param customPage the customPage to set
	 */
	public void setCustomPage(String customPage) {
		this.customPage = customPage;
	}
	
	
	/**
	 * getter of registType
	 * @return the registType
	 */
	public byte getRegistType() {
		return registType;
	}

	/**
	 * setter of registType
	 * @param registType the registType to set
	 */
	public void setRegistType(byte registType) {
		this.registType = registType;
	}

	/**
	 * getter of usePolicy
	 * @return the usePolicy
	 */
	public String getUsePolicy() {
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(1).getUserPolicy().replaceAll("<br>", "\r\n");
	}

//	/**
//	 * setter of usePolicy
//	 * @param usePolicy the usePolicy to set
//	 */
//	public void setUsePolicy(String usePolicy) {
//		this.usePolicy = usePolicy;
//	}

	/**
	 * getter of notice
	 * @return the notice
	 */
	public String getNotice() {
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(1).getSuccessNotice().replaceAll("<br>", "\r\n");
		//return notice;
	}

	/**
	 * setter of notice
	 * @param notice the notice to set
	 */
	public void setNotice(String notice) {
		this.notice = notice;
	}
	
	/**
	 * getter of librarySIPStatus
	 * @return the librarySIPStatus
	 */
	public String getLibrarySIPStatus() {
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(1).getSuccessLibrarySIPStatus().replaceAll("<br>", "\r\n");
		
		//return librarySIPStatus;
	}

	public String getLibrarySIPStatusString() {
		return librarySIPStatus.replace("\r\n", "<br>");
	}
	
	/**
	 * setter of librarySIPStatus
	 * @param librarySIPStatus the librarySIPStatus to set
	 */
	public void setLibrarySIPStatus(String librarySIPStatus) {
		this.librarySIPStatus = librarySIPStatus;
	}

	/**
	 * getter of librarySIPFines
	 * @return the librarySIPFines
	 */
	public String getLibrarySIPFines() {
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(1).getSuccessLibrarySIPFines().replaceAll("<br>", "\r\n");
		
		//return librarySIPFines;
	}

	public String getLibrarySIPFinesString() {
		return librarySIPFines.replace("\r\n", "<br>");
	}

	/**
	 * setter of librarySIPFines
	 * @param librarySIPFines the librarySIPFines to set
	 */
	public void setLibrarySIPFines(String librarySIPFines) {
		this.librarySIPFines = librarySIPFines;
	}

	
	
	/*----------- start of cwp multi language support      ------*/

//	 private Map<String,String> getEnabledMultiFields(CwpPageField pageField){
//		 Map<String,String> enableFields=new HashMap<String,String>();
//		 if(pageField.getEmailenable()){
//			 enableFields.put(CwpPageField.EMAIL,pageField.getEmail());
//		 }
//		 if(pageField.getFirstnameenable()){
//			 enableFields.put(CwpPageField.FIRSTNAME,pageField.getFirstname());
//		 }
//		 if(pageField.getLastnameenable()){
//			 enableFields.put(CwpPageField.LASTNAME,pageField.getLastname());
//		 }
//		 if(pageField.getPhoneenable()){
//			 enableFields.put(CwpPageField.PHONE,pageField.getPhone());
//		 }
//		 if(pageField.getVisitingenable()){
//			 enableFields.put(CwpPageField.VISITING,pageField.getVisiting());
//		 }
//		 if(pageField.getCommentenable()){
//			 enableFields.put(CwpPageField.COMMENT,pageField.getComment());
//		 }
//		 
//		 return enableFields;
//	 }
	
	private String getFieldShowLabel(CwpPageField field,int language){
		String result="";
		switch(language){
		case 1:
			result=field.getLabel();
			break;
		case 2:
			result=field.getLabel2();
			break;
		case 3:
			result=field.getLabel3();
			break;
		case 4:
			result=field.getLabel4();
			break;
		case 5:
			result=field.getLabel5();
			break;
		case 6:
			result=field.getLabel6();
			break;
		case 7:
			result=field.getLabel7();
			break;
		case 8:
			result=field.getLabel8();
			break;
		case 9:
			result=field.getLabel9();
			break;
			
		default:
				result=field.getLabel();
			
		}
		return result;
		
	}
	
	private void setFieldShowLabel(CwpPageField field,int language,String setWord){
		
		switch(language){
		case 1:
			field.setLabel(setWord);
			break;
		case 2:
			field.setLabel2(setWord);
			break;
		case 3:
			field.setLabel3(setWord);
			break;
		case 4:
			field.setLabel4(setWord);
			break;
		case 5:
			field.setLabel5(setWord);
			break;
		case 6:
			field.setLabel6(setWord);
			break;
		case 7:
			field.setLabel7(setWord);
			break;
		case 8:
			field.setLabel8(setWord);
			break;
		case 9:
			field.setLabel9(setWord);
			break;
			
		default:
			field.setLabel(setWord);
			
		}
		
	}
	
	public String getCurrentUserPolicy() {
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(previewLanguage).getUserPolicy().replaceAll("<br>", "\r\n");
		//return getUserPolicy(STATE_CURRENT);
	}
	
	
	
	boolean supportSimpleChinese;
	
    boolean supportEnglish;
	
	boolean supportGerman;
	
	boolean supportFrench;
	
	boolean supportKorean;
	
	boolean supportDutch;
	
	boolean supportSpanish;
	
	boolean supportTraditionalChinese;
	
	boolean supportItalian;
	
	private final static int DUTCH_NUM=6;
	
	private final static String DUTCHSUFFIX="_nl.js";
	
	private final static String  FRENCHSUFFIX="_fr.js";
	
	private final static int FRENCH_NUM=4;
	
	private final static String  ENGLISHSUFFIX="_en.js";
	
	private final static int ENGLISH_NUM=1;
	
	private final static String  GERMANSUFFIX="_de.js";
	
	private final static int GERMAN_NUM=3;
	
	private final static String  KOREANSUFFIX="_ko.js";
	
	private final static int KOREAN_NUM=5;
	
	private final static String  SPANISHSUFFIX="_es.js";
	
	private final static int  SPANISH_NUM=7;
	
	private final static String  SIMPLIFIEDCHINESESUFFIX="_zh-Hans.js";
	
	private final static int SIMPLECHINESE_NUM=2;
	
	private final static String  TRADITIONALCHINESESUFFIX="_zh-Hant.js";
	
	private final static int TRADITIONALCHINESE_NUM=8;
	
	private final static String  ITALIANSUFFIX="_it.js";
	
	private final static int ITALIAN_NUM=9;
	
	private final static String DYNAMIC_ENGLISH_RES="en.lang";
	
	private final static String DYNAMIC_SIMPLIFIEDCHINESE_RES="zh-Hans.lang";
	
	private final static String DYNAMIC_GERMAN_RES="de.lang";
	
	private final static String DYNAMIC_TRADITIONALCHINESE_RES="zh-Hant.lang";
	
	private final static String DYNAMIC_ITALIAN_RES="it.lang";
	
	private final static String DYNAMIC_KOREAN_RES="ko.lang";
	
	private final static String DYNAMIC_SPANISH_RES="es.lang";
	
	private final static String DYNAMIC_DUTCH_RES="nl.lang";
	
	private final static String DYNAMIC_FRENCH_RES="fr.lang";
	
	private final static String NOTICE_REPLACEWORD="/*#$Notice-Resource#$*/";
	
	private final static String LIB_SIP_STATUS_REPLACEWORD="/*#$LibSipStatus-Resource#$*/";
	
	private final static String LIB_SIP_SUCCESS_FINES_REPLACEWORD="/*#$LibSipSuccessFines-Resource#$*/";
	
	private final static String LIB_SIP_FAIL_FINES_REPLACEWORD="/*#$LibSipFailFines-Resource#$*/";

	private final static String USE_POLICY_REPLACEWORD="/*#$UserPolicy-Resource#$*/";
	
	private final static String FIRSTNAME_REPLACEWORD="/*#$FirstName-Resource#$*/";
	
	private final static String LASTNAME_REPLACEWORD="/*#$LastName-Resource#$*/";
	
	private final static String EMAIL_REPLACEWORD="/*#$Email-Resource#$*/";
	
	private final static String COMMENT_REPLACEWORD="/*#$Comment-Resource#$*/";
	
	private final static String PHONE_REPLACEWORD="/*#$Phone-Resource#$*/";
	
	private final static String VISITING_REPLACEWORD="/*#$Visiting-Resource#$*/";
	
	private final static String REPRESENTING_REPLACEWORD="/*#$Representing-Resource#$*/";
	
	private final static String SUCCESS_PROMPT_IDM_REPLACEWORD="/*#$SuccessPrompt-Resource#$*/";
	private final static String COUNTRYCODE_REPLACEWORD="/*#$CountryCode-Resource#$*/";

	
	public boolean getSupportSimpleChinese() {
		return supportSimpleChinese;
	}

	public void setSupportSimpleChinese(boolean supportSimpleChinese) {
		this.supportSimpleChinese = supportSimpleChinese;
	}

	public boolean getSupportEnglish() {
		return supportEnglish;
	}

	public void setSupportEnglish(boolean supportEnglish) {
		this.supportEnglish = supportEnglish;
	}

	public boolean getSupportGerman() {
		return supportGerman;
	}

	public void setSupportGerman(boolean supportGerman) {
		this.supportGerman = supportGerman;
	}

	public boolean getSupportFrench() {
		return supportFrench;
	}

	public void setSupportFrench(boolean supportFrench) {
		this.supportFrench = supportFrench;
	}

	public boolean getSupportKorean() {
		return supportKorean;
	}

	public void setSupportKorean(boolean supportKorean) {
		this.supportKorean = supportKorean;
	}

	public boolean getSupportDutch() {
		return supportDutch;
	}

	public void setSupportDutch(boolean supportDutch) {
		this.supportDutch = supportDutch;
	}

	public boolean getSupportSpanish() {
		return supportSpanish;
	}

	public void setSupportSpanish(boolean supportSpanish) {
		this.supportSpanish = supportSpanish;
	}

	public boolean getSupportTraditionalChinese() {
		return supportTraditionalChinese;
	}

	public void setSupportTraditionalChinese(boolean supportTraditionalChinese) {
		this.supportTraditionalChinese = supportTraditionalChinese;
	}
	
	public boolean getSupportItalian() {
		return supportItalian;
	}

	public void setSupportItalian(boolean supportItalian) {
		this.supportItalian = supportItalian;
	}

	private int languageNumber=0;
	private int[] multiLanguageArray=new int[]{};
	
	public String getShowMultiLanguagePageSection() {
			return "";
	}
	
	public void initMultiLanguageSupportCheckBox(){
		languageNumber=0;
		int value=getDataSource().getMultiLanguageSupport();
		if(value>=256){
			supportItalian=true;
			value=value-256;
			languageNumber+=1;
		}
		if(value>=128){
			supportEnglish=true;
			value=value-128;
			languageNumber+=1;
		}
		if(value>=64){
			supportSimpleChinese=true;
			value-=64;
			languageNumber+=1;
			//multiLanguageList.add(getText("config.cwp.language.support.simpleChinese"));
		}
		if(value>=32){
			supportGerman=true;
			value-=32;
			languageNumber+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.german"));
		}
		if(value>=16){
			supportFrench=true;
			value-=16;
			languageNumber+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.french"));
		}
		if(value>=8){
			supportKorean=true;
			value-=8;
			languageNumber+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.korean"));
		}
		if(value>=4){
			supportDutch=true;
			value-=4;
			languageNumber+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.dutch"));
		}
		if(value>=2){
			supportSpanish=true;
			value-=2;
			languageNumber+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.spanish"));
		}
		if(value>=1){
			supportTraditionalChinese=true;
			value-=1;
			languageNumber+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.traditionalChinese"));
		}
		
		
	}
	
	private int[] sortArray(int[] sortArray){
		int[] ne=sortArray;
		int[] so=new int[]{6,1,4,3,9,5,2,7,8};
		int[] result=new int[ne.length];
		int k=0;
		for(int i=0;i<so.length;i++){
			for(int j=0;j<ne.length;j++){
				if(ne[j]==so[i]){
					result[k]=so[i];
					k++;
					break;
				}
			}
		}
		
		return result;
	}
	
	private void initLanguageList(){
		multiLanguageArray=new int[languageNumber];
		int arrryN=0;
		int value=getDataSource().getMultiLanguageSupport();
		if(value>=256){
			value=value-256;
			multiLanguageArray[arrryN]=9;
			arrryN+=1;
		}
		if(value>=128){
			value=value-128;
			multiLanguageArray[arrryN]=1;
			arrryN+=1;
		}
		if(value>=64){
			value-=64;
			multiLanguageArray[arrryN]=2;
			arrryN+=1;
			//multiLanguageList.add(getText("config.cwp.language.support.simpleChinese"));
		}
		if(value>=32){
			value-=32;
			multiLanguageArray[arrryN]=3;
			arrryN+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.german"));
		}
		if(value>=16){
			value-=16;
			multiLanguageArray[arrryN]=4;
			arrryN+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.french"));
		}
		if(value>=8){
			value-=8;
			multiLanguageArray[arrryN]=5;
			arrryN+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.korean"));
		}
		if(value>=4){
			value-=4;
			multiLanguageArray[arrryN]=6;
			arrryN+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.dutch"));
		}
		if(value>=2){
			value-=2;
			multiLanguageArray[arrryN]=7;
			arrryN+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.spanish"));
		}
		if(value>=1){
			value-=1;
			multiLanguageArray[arrryN]=8;
			arrryN+=1;
//			multiLanguageList.add(getText("config.cwp.language.support.traditionalChinese"));
		}
		
	}
	
	
	private void preparePageMultiLanguageRes(Cwp cwp) {
		CwpPageCustomization pageCustomization = cwp.getPageCustomization();
		Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes = new LinkedHashMap<Integer, CwpPageMultiLanguageRes>();
		
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++) {
			CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(new Integer(i));
			
			if(multiRes == null) {
				multiRes = new CwpPageMultiLanguageRes();
				multiRes.setResLanguage(i);
				
				multiRes.setFailureLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(i)));
				multiRes.setSuccessLibrarySIPFines(cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(i)));
				multiRes.setSuccessLibrarySIPStatus(cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(i)));
				multiRes.setSuccessNotice(cwpResReader.getString("cwp.preview.customize.default_success_notice", getLocaleFromPreview(i)));
				multiRes.setUserPolicy(cwpResReader.getString("cwp.preview.customize.default_use_policy", getLocaleFromPreview(i)));
				
			}
			
			multiLanguageRes.put(new Integer(i), multiRes);
		}
		
		pageCustomization.setMultiLanguageRes(multiLanguageRes);
	}
	
	private int getMultiLanguageSupportValue(){
		
		int value=0;
		if(supportItalian){
			value+=256;
		}
		if(supportEnglish){
			value+=128;
		}
		if(supportSimpleChinese){
			value+=64;
		}
		if(supportGerman){
			value+=32;
		}
		if(supportFrench){
			value+=16;
		}
		if(supportKorean){
			value+=8;
		}
		if(supportDutch){
			value+=4;
		}
		if(supportSpanish){
			value+=2;
		}
		if(supportTraditionalChinese){
			value+=1;
		}
		
		return value;
	}

	
	private void updateMultiLanguageSupport(){
		Cwp dataSource = getDataSource();
		dataSource.setMultiLanguageSupport(getMultiLanguageSupportValue());
	}
	
	
	public void updateLanguageList(){
		multiLanguageList = new ArrayList<String>();
		if(supportEnglish){
			multiLanguageList.add(getText("config.cwp.language.support.english"));
		}
		if(supportSimpleChinese){
			multiLanguageList.add(getText("config.cwp.language.support.simpleChinese"));
		}
		if(supportGerman){
			multiLanguageList.add(getText("config.cwp.language.support.german"));
		}
		if(supportFrench){
			multiLanguageList.add(getText("config.cwp.language.support.french"));
		}
		if(supportKorean){
			multiLanguageList.add(getText("config.cwp.language.support.korean"));
		}
		if(supportDutch){
			multiLanguageList.add(getText("config.cwp.language.support.dutch"));
		}
		if(supportSpanish){
			multiLanguageList.add(getText("config.cwp.language.support.spanish"));
		}
		if(supportTraditionalChinese){
			multiLanguageList.add(getText("config.cwp.language.support.traditionalChinese"));
		}
		if(supportItalian){
			multiLanguageList.add(getText("config.cwp.language.support.italian"));
		}
		
	}
	
	private List<String> multiLanguageList = new ArrayList<String>();
	
	
	public List<String> getMultiLanguageList() {
		return multiLanguageList;
	}

	public void setMultiLanguageList(List<String> multiLanguageList) {
		this.multiLanguageList = multiLanguageList;
	}

	
	public EnumItem[] getEnumlanguage() {
		return MgrUtil.enumItems("enum.cwp.multilanguage.", sortArray(multiLanguageArray));
	}
	
	public EnumItem[] getPreviewenumlanguage() {
//		return MgrUtil.enumItems("enum.cwp.multilanguage.", new int[]{1,2,3,4,5,6,7,8,9});
		return MgrUtil.enumItems("enum.cwp.multilanguage.", new int[]{6,1,4,3,9,5,2,7,8});
	}
	
	private int previewLanguage=1;
	
	
	public int getPreviewLanguage() {
		return previewLanguage;
	}

	public void setPreviewLanguage(int previewLanguage) {
		this.previewLanguage = previewLanguage;
	}

	private int customizeLanguage=1;
		
	
	public int getCustomizeLanguage() {
		return customizeLanguage;
	}

	public void setCustomizeLanguage(int customizeLanguage) {
		this.customizeLanguage = customizeLanguage;
	}
	
	public EnumItem[] getCustomizeEnumLanguage() {
		//return MgrUtil.enumItems("enum.cwp.multilanguage.", new int[]{1,2,3,4,5,6,7,8,9});
		return MgrUtil.enumItems("enum.cwp.multilanguage.", new int[]{6,1,4,3,9,5,2,7,8});
		
	}

	/**
	 * copy js file to destination if the total size of directory 
	 * doesn't exceed the limit
	 *  
	 * @param dataSource -
	 * @param pageType -
	 * @param destinationPath -
	 * @param templatePath -
	 * @return -
	 * @author xxu
	 */
	private boolean copyMultiLanguageJSFiles(Cwp dataSource, 
									short pageType, 
									String destinationPath,
									String templatePath) {
		if(destinationPath == null 
				|| dataSource == null
				|| templatePath == null) {
			return false;
		}
		
		CwpPageCustomization pageCustomization = dataSource.getPageCustomization();
		
		if(pageCustomization == null) {
			return false;
		}
		
		

		
		String fileNamePrefix="";
		
		String removeFileNamePrefix="";
		
		switch (pageType)
		{
			case PAGE_AUTHENTICATION:
				fileNamePrefix="authentication";
				break;
			case PAGE_REGISTRATION:
				fileNamePrefix="registration";
				break;
			case PAGE_BOTH:
				fileNamePrefix="auth-reg";
				break;
			case PAGE_EULA:
			    fileNamePrefix="eula";
				break;
			case PAGE_PPSK:
				if(dataSource.getPpskServerType() == Cwp.PPSK_SERVER_TYPE_AUTH)
				{
					fileNamePrefix="ppsk_auth_index";
				}				
				else {
					fileNamePrefix="ppsk_index";
				}
				break;
			case PAGE_SUCCESS:
				fileNamePrefix="success";
				break;
			case PAGE_FAILURE:
				fileNamePrefix="failure";
				break;
			case PAGE_USER_POLICY:
				fileNamePrefix="use-policy";
				break;
				
		}
		if(fileNamePrefix.isEmpty() || fileNamePrefix.length()==0){
			return true;
		}
		
		fileNamePrefix=DEFAULT_RESOURCE_PATH+fileNamePrefix;
		removeFileNamePrefix=destinationPath+fileNamePrefix;
		
		Set<String> removeFileList=new HashSet<String>();
		
		Set<String> fileList = new HashSet<String>();
		fileList.add(templatePath);
		
		if(supportEnglish){
			fileList.add(fileNamePrefix+ENGLISHSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_ENGLISH_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+ENGLISHSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_ENGLISH_RES);
		}
		
		if(supportSimpleChinese){
			fileList.add(fileNamePrefix+SIMPLIFIEDCHINESESUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_SIMPLIFIEDCHINESE_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+SIMPLIFIEDCHINESESUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_SIMPLIFIEDCHINESE_RES);
		}
		
		
		if(supportGerman){
			fileList.add(fileNamePrefix+GERMANSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_GERMAN_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+GERMANSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_GERMAN_RES);
		}
		
		if(supportFrench){
			fileList.add(fileNamePrefix+FRENCHSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_FRENCH_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+FRENCHSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_FRENCH_RES);
		}
		
		if(supportKorean){
			fileList.add(fileNamePrefix+KOREANSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_KOREAN_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+KOREANSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_KOREAN_RES);
		}
		
		if(supportDutch){
			fileList.add(fileNamePrefix+DUTCHSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_DUTCH_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+DUTCHSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_DUTCH_RES);
		}
		
		if(supportSpanish){
			fileList.add(fileNamePrefix+SPANISHSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_SPANISH_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+SPANISHSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_SPANISH_RES);
		}
		
		if(supportTraditionalChinese){
			fileList.add(fileNamePrefix+TRADITIONALCHINESESUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_TRADITIONALCHINESE_RES);
			
		}else{
			removeFileList.add(removeFileNamePrefix+TRADITIONALCHINESESUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_TRADITIONALCHINESE_RES);
		}
		
		if(supportItalian){
			fileList.add(fileNamePrefix+ITALIANSUFFIX);
			fileList.add(DEFAULT_RESOURCE_PATH+DYNAMIC_ITALIAN_RES);
		}else{
			removeFileList.add(removeFileNamePrefix+ITALIANSUFFIX);
			removeFileList.add(destinationPath+DYNAMIC_ITALIAN_RES);
		}
		
//		String jsCwpUtil = DEFAULT_RESOURCE_PATH + "hm.cwp.util.js"; 
		
		if(isFilesTooBig(destinationPath, fileList)) {
			addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.filesTooBig",
					HiveApFileAction.CWP_RESOURCE_MAX_SIZE+ "K Bytes"));
			/*
			 * need to delete the newly created directory
			 */
			String newDirPath = AhDirTools.getCwpWebDir(domainName)
				+ dataSource.getCwpName()
				+ File.separator;
			
			if(dataSource.getId() == null && needDeleteDirectory(dataSource)) {
				try {
					FileManager.getInstance().deleteDirectory(newDirPath);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			return false;
		}
		
		//remove old file first
		
		for(String fileName:removeFileList){
			try {
				File file = new File(fileName);
				if(file.exists() && file.isFile()){
					FileManager.getInstance().deletefile(fileName);
				}
			}
			catch (Exception exception) {
				log.error("createPage", "failed to delete js file to " + destinationPath, exception);
				addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
				return false;
			}
		}
		
		
		//copy js files
		for(String fileName:fileList){
			try {
				FileManager.getInstance().copyFile(fileName, destinationPath + fileName.replace(DEFAULT_RESOURCE_PATH, ""));
			}
			catch (Exception exception) {
				log.error("createPage", "failed to copy resource file to " + destinationPath, exception);
				addActionError(MgrUtil.getUserMessage("error.config.cwp.page.customization.creationFailed"));
				return false;
			}
		}
		
		return true;
	}
	
	private boolean reWriteMultiLanguageJSFiles(Cwp dataSource, 
			short pageType, 
			String destinationPath,
			String templatePath) {
		boolean result=false;
		if(destinationPath == null 
				|| dataSource == null
				|| templatePath == null) {
			return false;
		}
		
		CwpPageCustomization pageCustomization = dataSource.getPageCustomization();
		
		if(pageCustomization == null) {
			return false;
		}
        String fileNamePrefix="";
		
		String removeFileNamePrefix="";
		switch (pageType)
		{
			case PAGE_AUTHENTICATION:
				fileNamePrefix="authentication";
				break;
			case PAGE_REGISTRATION:
				fileNamePrefix="registration";
				break;
			case PAGE_BOTH:
				fileNamePrefix="auth-reg";
				break;
			case PAGE_EULA:
			    fileNamePrefix="eula";
				break;
			case PAGE_PPSK:
				if(dataSource.getPpskServerType() == Cwp.PPSK_SERVER_TYPE_AUTH)
				{
					fileNamePrefix="ppsk_auth_index";
				}				
				else {
					fileNamePrefix="ppsk_index";
				}
				break;
			case PAGE_SUCCESS:
				fileNamePrefix="success";
				break;
			case PAGE_FAILURE:
				fileNamePrefix="failure";
				break;
			case PAGE_USER_POLICY:
				fileNamePrefix="use-policy";
				break;
				
		}
		if(fileNamePrefix.isEmpty() || fileNamePrefix.length()==0){
			return true;
		}
		fileNamePrefix=destinationPath+fileNamePrefix;
		
		Map<Integer,String> fileMap=new LinkedHashMap<Integer,String>();
		
		if(supportEnglish){
			fileMap.put(ENGLISH_NUM, fileNamePrefix+ENGLISHSUFFIX);
		}
		
		if(supportSimpleChinese){
			fileMap.put(SIMPLECHINESE_NUM, fileNamePrefix+SIMPLIFIEDCHINESESUFFIX);
		}
		
		
		if(supportGerman){
			fileMap.put(GERMAN_NUM,fileNamePrefix+GERMANSUFFIX);
		}
		
		if(supportFrench){
			fileMap.put(FRENCH_NUM,fileNamePrefix+FRENCHSUFFIX);
		}
		
		if(supportKorean){
			fileMap.put(KOREAN_NUM,fileNamePrefix+KOREANSUFFIX);
		}
		
		if(supportDutch){
			fileMap.put(DUTCH_NUM,fileNamePrefix+DUTCHSUFFIX);
		}
		
		if(supportSpanish){
			fileMap.put(SPANISH_NUM,fileNamePrefix+SPANISHSUFFIX);
		}
		
		if(supportTraditionalChinese){
			fileMap.put(TRADITIONALCHINESE_NUM,fileNamePrefix+TRADITIONALCHINESESUFFIX);
		}
		
		if(supportItalian){
			fileMap.put(ITALIAN_NUM,fileNamePrefix+ITALIANSUFFIX);
		}
		
		/*
		 * SUCCESS PAGE
		 */
		if (pageType == PAGE_SUCCESS) {
			// notice
			if(pageCustomization.isSuccessLibrarySIP()) {
				/*
				 * Library SIP
				 */
				 for(Map.Entry<Integer,String> entry : fileMap.entrySet())   
				 { 
					 CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(entry.getKey());
						if(multiRes != null) {
							String sipStatus=multiRes.getSuccessLibrarySIPStatus().replaceAll("\r\n", "<br>");
							String sipFines =multiRes.getSuccessLibrarySIPFines().replaceAll("\r\n", "<br>");
							sipStatus=sipStatus.replaceAll("\"", "\\\\\"");
							sipFines=sipFines.replaceAll("\"", "\\\\\"");	
							result=replaceJSFile(entry.getValue(),LIB_SIP_STATUS_REPLACEWORD,sipStatus);
							replaceJSFile(entry.getValue(),LIB_SIP_SUCCESS_FINES_REPLACEWORD,sipFines);
						}
					 
				 }
				
			} else {
				 for(Map.Entry<Integer,String> entry : fileMap.entrySet())   
				 {   
							CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(entry.getKey());
							if(multiRes != null) {
								String newWord=multiRes.getSuccessNotice();
								String changeWord=newWord.replaceAll("\\r\\n", "<br>");
								changeWord=changeWord.replaceAll("\"", "\\\\\"");	
								result=replaceJSFile(entry.getValue(),NOTICE_REPLACEWORD,changeWord);
							}
				 }   
			}
		}
		
		/*
		 * FAILURE PAGE
		 */
		if (pageType == PAGE_FAILURE) {
			// notice
			if(pageCustomization.isFailureLibrarySIP()) {
				/*
				 * Library SIP
				 */
				 for(Map.Entry<Integer,String> entry : fileMap.entrySet())   
				 {   
							CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(entry.getKey());
							if(multiRes != null) {
								String failSipFines=multiRes.getFailureLibrarySIPFines().replaceAll("\\r\\n", "<br>");
								failSipFines=failSipFines.replaceAll("\"", "\\\\\"");
								result=replaceJSFile(entry.getValue(),LIB_SIP_FAIL_FINES_REPLACEWORD,failSipFines);
							}
				 }   
//				modifyField(htmlLines, PAGE_ELEMENT_SIP_BLOCK, 
//						pageCustomization.getFailureLibrarySIPFines().replace("\r\n", "<br>"));
			}
		}
		
		if (pageType == PAGE_USER_POLICY
				|| pageType == PAGE_EULA) {
			 for(Map.Entry<Integer,String> entry : fileMap.entrySet())   
			 {   
						CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(entry.getKey());
						if(multiRes != null) {
							String usePolicy=multiRes.getUserPolicy().replaceAll("<br>", "\\\\r\\\\n");
							usePolicy=usePolicy.replaceAll("\"", "\\\\\"");
							result=replaceJSFile(entry.getValue(),USE_POLICY_REPLACEWORD,usePolicy);
						}
			 }   
			// user policy
			//modifyField(htmlLines, PAGE_ELEMENT_USER_POLICY, pageCustomization.getUserPolicy());
		}
		
		if (pageType == PAGE_REGISTRATION 
				|| pageType == PAGE_BOTH) {
			 for(Map.Entry<Integer,String> entry : fileMap.entrySet())   
			 {   
				 Map<String, CwpPageField> fieldMap=pageCustomization.getFields();
				 for(Map.Entry<String, CwpPageField> fieldEntry : fieldMap.entrySet()) {
					 if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.COMMENTMARK)){
						 if(fieldEntry.getValue().getRequired()){
							 replaceJSFile(entry.getValue(),COMMENT_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),COMMENT_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }else if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.EMAILMARK)){
                         if(fieldEntry.getValue().getRequired()){
                        	 replaceJSFile(entry.getValue(),EMAIL_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),EMAIL_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }else if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.FIRSTNAMEMARK)){
                         if(fieldEntry.getValue().getRequired()){
                        	 replaceJSFile(entry.getValue(),FIRSTNAME_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),FIRSTNAME_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }else if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.LASTNAMEMARK)){
						 if(fieldEntry.getValue().getRequired()){
							 replaceJSFile(entry.getValue(),LASTNAME_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),LASTNAME_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }else if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.PHONEMARK)){
						 if(fieldEntry.getValue().getRequired()){
							 replaceJSFile(entry.getValue(),PHONE_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),PHONE_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }else if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.VISITINGMARK)){
						 if(fieldEntry.getValue().getRequired()){
							 replaceJSFile(entry.getValue(),VISITING_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),VISITING_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }else if(fieldEntry.getValue().getFieldMark().equals(CwpPageField.REPRESENTINGMARK)){
						 if(fieldEntry.getValue().getRequired()){
							 replaceJSFile(entry.getValue(),REPRESENTING_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()) + "*");
						 }else{
							 replaceJSFile(entry.getValue(),REPRESENTING_REPLACEWORD,getFieldShowLabel(fieldEntry.getValue(),entry.getKey()));
						 }
					 }
				 }
				 
				 appendCountryCode2JS(dataSource, pageType, fileNamePrefix, entry.getValue());
			 }   
		} 
		
		
		
		return true;
		
		
		
		//return result;
	}
	
	private String[] readFile(String filePath)
			throws IllegalArgumentException,
			IOException,
			BeNoPermissionException
		{
			if (filePath == null)
			{
				throw new IllegalArgumentException("Invalid argument");
			}

			File file = new File(filePath);

			if (!file.exists())
			{
				throw new FileNotFoundException(filePath + " is not exist");
			}

			FileInputStream fis = new FileInputStream(file);
			BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
			String line;
			Vector<String> vct = new Vector<String>();
			while ((line = bf.readLine()) != null)
			{
				vct.addElement(line);
			}

			bf.close();
			fis.close();

			return vct.toArray(new String[vct.size()]);
		}

	private boolean replaceJSFile(String desFile,String oldStr,String newStr){
		boolean result=false;
		String temp = "";  
		String leftWord="[\"";
		String rightWord="\"],";
		String src=desFile;
		
		// avoid NPE
        if (StringUtils.isBlank(src))
            return false;
		
        File file = new File(src);
        try (FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr)) {

            log.debug("replaceJSFile", "--- operating " + file.getCanonicalPath() + "--- start");

            StringBuffer buf = new StringBuffer();
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                if (!temp.contains(oldStr)) {
                    buf.append(temp);
                } else {
                    buf.append(leftWord + newStr + rightWord + oldStr);
                    log.debug("replaceJSFile", "replacing [ " + oldStr + " ] with [ "+ newStr +" ]");
                    
                    debugPrinter(newStr);
                }
                buf = buf.append(System.getProperty("line.separator"));
            }
            FileOutputStream fos = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();

            result = true;
            log.debug("replaceJSFile", "--- operating " + file.getCanonicalPath() + "--- end");
        } catch (Exception e) {
            log.error("replaceJSFile", "replace failed", e);
        }
		 return result;

	}
	
	private void debugPrinter(String original) {
        try {
            log.debug("debugPrinter", "file.encoding="+System.getProperty("file.encoding"));
            log.debug("debugPrinter", "NIO default Charset="+Charset.defaultCharset().displayName());
            log.debug("debugPrinter", original);
            
            boolean utf8Supported = Charset.isSupported("UTF8");
            
            byte[] defaultBytes = original.getBytes();
            byte[] utf8Bytes = null;
            if(utf8Supported) {
                utf8Bytes = original.getBytes("UTF8");
                
                String roundTrip = new String(utf8Bytes, "UTF8");
                log.debug("debugPrinter", "roundTrip = " + roundTrip);
            }
            
            printBytes(defaultBytes, "defaultBytes", true);
            
            if(utf8Supported) {
                printBytes(utf8Bytes, "utf8Bytes", true);
            }
        } catch (UnsupportedEncodingException e) {
        }
    }

	private void printBytes(byte[] array, String name, boolean complete) {
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < array.length; i++) {
	        if(complete) {
	            builder.append("0x" + UnicodeFormatter.byteToHex(array[i]));
	        } else {
	            log.debug("printBytes", name + "[" + i + "] = " + "0x" + UnicodeFormatter.byteToHex(array[i]));
	        }
        }
	    if(complete) {
	        log.debug("printBytes", name + " = [" + builder.toString() + "]");
	    }
	}
	
    BeResModule_CWPImpl cwpResReader=new BeResModule_CWPImpl();
	
	
	
	private Locale getLocaleFromPreview(int pLanguage ){
		Locale resultLocale;
		switch(pLanguage){
		case 1:
			resultLocale=Locale.ENGLISH;
			break;
		case 2:
			resultLocale=Locale.CHINA;
			break;
		case 3:
			resultLocale=Locale.GERMAN;
			break;
		case 4:
			resultLocale=Locale.FRANCE;
			break;
		case 5:
			resultLocale=Locale.KOREA;
			break;
		case 6:
			resultLocale=new Locale("nl","DU");;
			break;
		case 7:
			resultLocale=new Locale("es","SP");
			break;
		case 8:
			resultLocale=Locale.TAIWAN;
			break;
		case 9:
			resultLocale=Locale.ITALIAN;
			break;
		
			
		default:
				resultLocale=Locale.ENGLISH;
				break;
		}
		return resultLocale;
	}
	
	//multi cwp user input support 
	
	public static final int MAX_LANGUAGE_SUPPORT=9;
	
	private String noticePostValue;
	
	private String librarySIPBlockPostValue;
	


	private String librarySIPStatusPostValue;
	
	private String librarySIPFinesPostValue;
	
	private String usePolicyPostValue;
	
	private String labelFirstNameMarkPostValue;
	
	private String labelLastNameMarkPostValue;
	
	private String labelEmailMarkPostValue;
	
	private String labelPhoneMarkPostValue;
	
	private String labelVisitingMarkPostValue;
	
	private String labelCommentMarkPostValue;
	
	private String labelRepresentingMarkPostValue;
	
	public String getLabelFirstNameMarkPostValue() {
		return labelFirstNameMarkPostValue;
	}

	public void setLabelFirstNameMarkPostValue(String labelFirstNameMarkPostValue) {
		this.labelFirstNameMarkPostValue = labelFirstNameMarkPostValue;
	}

	public String getLabelLastNameMarkPostValue() {
		return labelLastNameMarkPostValue;
	}

	public void setLabelLastNameMarkPostValue(String labelLastNameMarkPostValue) {
		this.labelLastNameMarkPostValue = labelLastNameMarkPostValue;
	}

	public String getLabelEmailMarkPostValue() {
		return labelEmailMarkPostValue;
	}

	public void setLabelEmailMarkPostValue(String labelEmailMarkPostValue) {
		this.labelEmailMarkPostValue = labelEmailMarkPostValue;
	}

	public String getLabelPhoneMarkPostValue() {
		return labelPhoneMarkPostValue;
	}

	public void setLabelPhoneMarkPostValue(String labelPhoneMarkPostValue) {
		this.labelPhoneMarkPostValue = labelPhoneMarkPostValue;
	}

	public String getLabelVisitingMarkPostValue() {
		return labelVisitingMarkPostValue;
	}

	public void setLabelVisitingMarkPostValue(String labelVisitingMarkPostValue) {
		this.labelVisitingMarkPostValue = labelVisitingMarkPostValue;
	}

	public String getLabelCommentMarkPostValue() {
		return labelCommentMarkPostValue;
	}

	public void setLabelCommentMarkPostValue(String labelCommentMarkPostValue) {
		this.labelCommentMarkPostValue = labelCommentMarkPostValue;
	}

	public String getUsePolicyPostValue() {
		return usePolicyPostValue;
	}

	public void setUsePolicyPostValue(String usePolicyPostValue) {
		this.usePolicyPostValue = usePolicyPostValue;
	}

	public String getLibrarySIPBlockPostValue() {
		return librarySIPBlockPostValue;
	}

	public void setLibrarySIPBlockPostValue(String librarySIPBlockPostValue) {
		this.librarySIPBlockPostValue = librarySIPBlockPostValue;
	}
	
	public int[] getMultiLanguageArray() {
		return multiLanguageArray;
	}

	public void setMultiLanguageArray(int[] multiLanguageArray) {
		this.multiLanguageArray = multiLanguageArray;
	}

	public String getLibrarySIPStatusPostValue() {
		return librarySIPStatusPostValue;
	}

	public void setLibrarySIPStatusPostValue(String librarySIPStatusPostValue) {
		this.librarySIPStatusPostValue = librarySIPStatusPostValue;
	}

	public String getLibrarySIPFinesPostValue() {
		return librarySIPFinesPostValue;
	}

	public void setLibrarySIPFinesPostValue(String librarySIPFinesPostValue) {
		this.librarySIPFinesPostValue = librarySIPFinesPostValue;
	}

	
	
	
	public String getNoticePostValue() {
		return noticePostValue;
	}

	public void setNoticePostValue(String noticePostValue) {
		this.noticePostValue = noticePostValue;
	}
	
	private int languagePostValue;
	
	
	
	public int getLanguagePostValue() {
		return languagePostValue;
	}

	public void setLanguagePostValue(int languagePostValue) {
		this.languagePostValue = languagePostValue;
	}
	
	private boolean saveMultiFields(Cwp cwp,int language){
		Map<String, CwpPageField> fieldMap= cwp.getPageCustomization().getFields();
		 Map<String,String> field=new HashMap<String,String>();
		 for(Map.Entry<String, CwpPageField> entry : fieldMap.entrySet()) {
			 if(entry.getValue().getFieldMark().equals(CwpPageField.COMMENTMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelCommentMarkPostValue());
			 }else if(entry.getValue().getFieldMark().equals(CwpPageField.EMAILMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelEmailMarkPostValue());
			 }else if(entry.getValue().getFieldMark().equals(CwpPageField.FIRSTNAMEMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelFirstNameMarkPostValue());
			 }else if(entry.getValue().getFieldMark().equals(CwpPageField.LASTNAMEMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelLastNameMarkPostValue());
			 }else if(entry.getValue().getFieldMark().equals(CwpPageField.PHONEMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelPhoneMarkPostValue());
			 }else if(entry.getValue().getFieldMark().equals(CwpPageField.VISITINGMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelVisitingMarkPostValue());
			 }else if(entry.getValue().getFieldMark().equals(CwpPageField.REPRESENTINGMARK)){
				 setFieldShowLabel(entry.getValue(),language,this.getLabelRepresentingMarkPostValue());
			 }
		 }
		 return true;
			
		}
	

	private boolean saveMultiLanguageRes() throws JSONException{
		Cwp cwp=getDataSource();
		CwpPageCustomization pageCustomization = cwp.getPageCustomization();
		Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes = new LinkedHashMap<Integer, CwpPageMultiLanguageRes>();
		for(int i=1;i<=MAX_LANGUAGE_SUPPORT;i++){
			CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(new Integer(i));
			if(i==languagePostValue){
				if(isCustomizeLoginPage()||isCustomizePPSKPage()){

					if(StringUtils.isNotBlank(getUsePolicyPostValue())){
					    if(getUsePolicyPostValue().length() > MAXIMUM_USE_POLICY_SIZE) {
					        jsonObject.put("msg", MgrUtil.getUserMessage("error.config.cwp.page.customization.usepolicy.limitation", Integer.toString(MAXIMUM_USE_POLICY_SIZE)));
					        return false;
					    } else {
					        multiRes.setUserPolicy(getUsePolicyPostValue());
					    }
					}
				}else if(isCustomizeSuccessPage()){
					if(getNoticePostValue()!=null){
						multiRes.setSuccessNotice(getNoticePostValue());
					}
					if(getLibrarySIPFinesPostValue()!=null){
						
						multiRes.setSuccessLibrarySIPFines(getLibrarySIPFinesPostValue());
					}
					if(getLibrarySIPStatusPostValue()!=null){
					
						multiRes.setSuccessLibrarySIPStatus(getLibrarySIPStatusPostValue());
						
					}
					
				}else if(isCustomizeFailurePage()){
					if(getLibrarySIPBlockPostValue()!=null){
						
						multiRes.setFailureLibrarySIPFines(getLibrarySIPBlockPostValue());
					}
					
				}
			}
			sanitizeHTMLContent(multiRes);
			multiLanguageRes.put(new Integer(i), multiRes);
		}
		
		if(isCustomizeLoginPage()||isCustomizePPSKPage()){
			if(getUsePolicyPostValue()!=null){
				saveMultiFields(cwp,languagePostValue);
			}
		}
		
		
		//save
//		try {
//			
//			QueryUtil.updateBo(cwp);
////			if(dataSource.getId()!=null){
////				setId(dataSource.getId());
////				updateBo( cwp);
////			}
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}

		return true;
	}
	
//	private String getMultiField(CwpPageField cwpField,int language){
//		String result=""
//				
//				switch(language){
//				case 1:
//					result=
//				}
//				
//				
//				return result;
//	}

	/**
	 * For XSS issue reported by LinkLater, sanitize the HTML content to avoid stored XSS 
	 * 
	 * @author Yunzhi Lin
	 * - Time: Dec 20, 2013 5:37:18 PM
	 * @param multiRes
	 */
	private void sanitizeHTMLContent(CwpPageMultiLanguageRes multiRes) {
	    multiRes.setUserPolicy(cleanHTML(multiRes.getUserPolicy()));
	    multiRes.setSuccessNotice(cleanHTML(multiRes.getSuccessNotice()));
	    multiRes.setSuccessLibrarySIPStatus(cleanHTML(multiRes.getSuccessLibrarySIPStatus()));
	    multiRes.setSuccessLibrarySIPStatus(cleanHTML(multiRes.getSuccessLibrarySIPStatus()));
	    multiRes.setFailureLibrarySIPFines(cleanHTML(multiRes.getFailureLibrarySIPFines()));
    }
	
	private String cleanHTML(String untrustedHTML) {
	    if(StringUtils.isNotBlank(untrustedHTML)) {
            return Jsoup.clean(untrustedHTML, "", Whitelist.relaxed(),
                    new Document.OutputSettings().prettyPrint(false))
                    .replace("<br />", "<br>");
	    } else {
	        return untrustedHTML;
	    }
	}

    private JSONObject getRefreshMultiLanguageRes(Integer language){
		CwpPageCustomization pageCustomization = getDataSource().getPageCustomization();
		Map<Integer, CwpPageMultiLanguageRes> multiLanguageRes = new LinkedHashMap<Integer, CwpPageMultiLanguageRes>();
		
		CwpPageMultiLanguageRes multiRes=pageCustomization.getCwpPageMultiLanguageRes(language);
		
		 
		JSONObject o=new JSONObject();
		
		
		
		if(multiRes!=null){
		
		try {
			 Map<String, CwpPageField> fieldMap= pageCustomization.getFields();
			 Map<String,String> field=new HashMap<String,String>();
			 for(Map.Entry<String, CwpPageField> entry : fieldMap.entrySet()) {
				 o.put(entry.getValue().getFieldMark(), getFieldShowLabel(entry.getValue(),language));
			 }
			o.put("notice", multiRes.getSuccessNotice());
			o.put("libSipBlock", multiRes.getFailureLibrarySIPFines());
			o.put("libSipStatus", multiRes.getSuccessLibrarySIPStatus());
			o.put("libSipFines", multiRes.getSuccessLibrarySIPFines());
			o.put("usePolicy", multiRes.getUserPolicy());
			
			
		} catch (JSONException e) {
		}
		}
		return o;
		
	}
	
	private JSONObject resetOneLanguageRes(Integer language) {
		CwpPageCustomization pageCustomization = getDataSource()
				.getPageCustomization();

		CwpPageMultiLanguageRes multiRes = pageCustomization
				.getCwpPageMultiLanguageRes(language);
		JSONObject o = new JSONObject();

		
		if (multiRes != null) {

			try {
				for(String field : CwpPageField.FIELDS) {
					String searchWord="";
					String mark="";
					if(field.equals(CwpPageField.FIRSTNAME)){
						searchWord="cwp.preview.ppsk.firstname_label";
						mark=CwpPageField.FIRSTNAMEMARK;
					}else if(field.equals(CwpPageField.LASTNAME)){
						searchWord="cwp.preview.ppsk.lastname_label";
						mark=CwpPageField.LASTNAMEMARK;
					}else if(field.equals(CwpPageField.EMAIL)){
					    searchWord="cwp.preview.ppsk.email_label";
						mark=CwpPageField.EMAILMARK;
					}else if(field.equals(CwpPageField.PHONE)){
						searchWord="cwp.preview.ppsk.phone_label";
						mark=CwpPageField.PHONEMARK;
					}else if(field.equals(CwpPageField.VISITING)){
						if(isOpenSSIDIDMSelfReg(getDataSource().getRegistrationType())){
							searchWord="cwp.preview.ppsk.idm.visiting_label";
						}else{
							searchWord="cwp.preview.ppsk.visiting_label";
						}
						mark=CwpPageField.VISITINGMARK;
					}else if(field.equals(CwpPageField.COMMENT)){
						searchWord="cwp.preview.ppsk.reason_label";
						mark=CwpPageField.COMMENTMARK;
					}else if(field.equals(CwpPageField.REPRESENTING)){
						searchWord="cwp.preview.ppsk.idm.reason_label";
						mark=CwpPageField.REPRESENTINGMARK;
					}
					
					o.put(mark, cwpResReader.getString(searchWord, getLocaleFromPreview(language)));
				}

				o.put("notice",
						cwpResReader.getString(
								"cwp.preview.customize.default_success_notice",
								getLocaleFromPreview(language)).replaceAll(
								"\\r\\n", "<br>"));
				o.put("libSipBlock",
						cwpResReader.getString(
								"cwp.preview.customize.default_lib_sip_block",
								getLocaleFromPreview(language)).replaceAll(
								"\\r\\n", "<br>"));
				o.put("libSipStatus",
						cwpResReader.getString(
								"cwp.preview.customize.default_lib_sip_status",
								getLocaleFromPreview(language)).replaceAll(
								"\\r\\n", "<br>"));
				o.put("libSipFines",
						cwpResReader.getString(
								"cwp.preview.customize.default_lib_sip_fines",
								getLocaleFromPreview(language)).replaceAll(
								"\\r\\n", "<br>"));
				o.put("usePolicy",
						cwpResReader.getString(
								"cwp.preview.customize.default_use_policy",
								getLocaleFromPreview(language)).replaceAll(
								"\\r\\n", "<br>"));

			} catch (JSONException e) {
			}
		}
		return o;

	}
	
	private String removeMulitSuffix(String str){
		String result=str;
		int endIndex=str.indexOf("MultiLan");
		if(endIndex!=-1){
			result=result.substring(0, endIndex);
		}
		return result;
		
	}
	
	//previewLanguage
	//success page preview
	public String getCWPPreviewSuccesscurrentSuccessNotice(){
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(previewLanguage).getSuccessNotice().replaceAll("\\r\\n", "<br>");
	}
	
	public String getCWPPreviewSuccesslibrarySIPStatus(){
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(previewLanguage).getSuccessLibrarySIPStatus().replaceAll("\\r\\n", "<br>");
	}
	
	public String getCWPPreviewSuccesslibrarySIPFines(){
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(previewLanguage).getSuccessLibrarySIPFines().replaceAll("\\r\\n", "<br>");
	}
	
	public String getCWPPreviewSuccessLoginSuccessful_label(){
		return cwpResReader.getString("cwp.preview.success.LoginSuccessful_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewSuccessSecureInternetPortal_label(){
		return cwpResReader.getString("cwp.preview.success.SecureInternetPortal_label", getLocaleFromPreview(previewLanguage));
	}
	//index page preview
	public String getCWPPreviewIndexUsername_label(){
		return cwpResReader.getString("cwp.preview.index.username_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexPassword_label(){
		return cwpResReader.getString("cwp.preview.index.password_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexAcceptButton_label(){
		return cwpResReader.getString("cwp.preview.index.accept_button_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexCancelButton_label(){
		return cwpResReader.getString("cwp.preview.index.cancel_button_label", getLocaleFromPreview(previewLanguage));
	}
	
	
	public String getCWPPreviewIndexSecureInternetPortal_label(){
		return cwpResReader.getString("cwp.preview.index.SecureInternetPortal_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexAcceptableUsePolicy_label(){
		return cwpResReader.getString("cwp.preview.index.AcceptableUsePolicy_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexExistingUsers_label(){
		return cwpResReader.getString("cwp.preview.index.ExistingUsers_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexLoginforSIA_label(){
		return cwpResReader.getString("cwp.preview.index.LoginforSIA_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexRegistertoaccess_label(){
		return cwpResReader.getString("cwp.preview.index.Registertoaccess_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexNewUsers_label(){
		return cwpResReader.getString("cwp.preview.index.NewUsers_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexRegPrompt_label(){
		return cwpResReader.getString("cwp.preview.index.regprompt_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexRegister_button_label(){
		return cwpResReader.getString("cwp.preview.index.register_button_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexloginprompt2_label(){
		return cwpResReader.getString("cwp.preview.index.loginprompt2_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewIndexloginbutton_label(){
		return cwpResReader.getString("cwp.preview.index.login_button_label", getLocaleFromPreview(previewLanguage));
	}
	
	//failure page preview
	public String getCWPPreviewFailureSecureInternetPortal_label(){
		return cwpResReader.getString("cwp.preview.failure.SecureInternetPortal_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewFailureLoginFailed_label(){
		return cwpResReader.getString("cwp.preview.failure.LoginFailed_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewFailurefailurecontent1_label(){
		return cwpResReader.getString("cwp.preview.failure.failurecontent1_label", getLocaleFromPreview(previewLanguage));
	}
	
	
	public String getCWPPreviewFailurefailurecontent2_label(){
		return cwpResReader.getString("cwp.preview.failure.failurecontent2_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewFailurelibrarySIPBlock(){
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(previewLanguage).getFailureLibrarySIPFines().replaceAll("\\r\\n", "<br>");
	}
	
	
	
	//ppsk page preview
	public String getCWPPreviewPpskSecureInternetPortal_label(){
		return cwpResReader.getString("cwp.preview.ppsk.SecureInternetPortal_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskAuthNotes_label(){
		return cwpResReader.getString("cwp.preview.ppsk.auth.notes_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskRegNotes_label(){
		return cwpResReader.getString("cwp.preview.ppsk.reg.notes_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskRegPrompt_label(){
		return cwpResReader.getString("cwp.preview.ppsk.regprompt_label", getLocaleFromPreview(previewLanguage));
	}
	
	
	public String getCWPPreviewPpskUsername_label(){
		return cwpResReader.getString("cwp.preview.ppsk.username_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskPassword_label(){
		return cwpResReader.getString("cwp.preview.ppsk.password_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskFirstName_label(){
		try{
			if(getDataSource().getPageCustomization().getPageField(CwpPageField.FIRSTNAME).getRequired())
				return cwpResReader.getString("cwp.preview.ppsk.firstname_label", getLocaleFromPreview(previewLanguage)) + "*";
			else{
				return cwpResReader.getString("cwp.preview.ppsk.firstname_label", getLocaleFromPreview(previewLanguage));
			}
		}catch(Exception e){
			return cwpResReader.getString("cwp.preview.ppsk.firstname_label", getLocaleFromPreview(previewLanguage));
		}
	}
	
	public String getCWPPreviewPpskLastName_label(){
		try{
			if(getDataSource().getPageCustomization().getPageField(CwpPageField.LASTNAME).getRequired())
				return cwpResReader.getString("cwp.preview.ppsk.lastname_label", getLocaleFromPreview(previewLanguage)) + "*";
			else{
				return cwpResReader.getString("cwp.preview.ppsk.lastname_label", getLocaleFromPreview(previewLanguage));
			}
		}catch(Exception e){
			return cwpResReader.getString("cwp.preview.ppsk.lastname_label", getLocaleFromPreview(previewLanguage));
		}
	}
	
	public String getCWPPreviewPpskEmail_label(){
		try{
			if(getDataSource().getPageCustomization().getPageField(CwpPageField.EMAIL).getRequired())
				return cwpResReader.getString("cwp.preview.ppsk.email_label", getLocaleFromPreview(previewLanguage)) + "*";
			else{
				return cwpResReader.getString("cwp.preview.ppsk.email_label", getLocaleFromPreview(previewLanguage));
			}
		}catch(Exception e){
			return cwpResReader.getString("cwp.preview.ppsk.email_label", getLocaleFromPreview(previewLanguage));
		}
		//return cwpResReader.getString("cwp.preview.ppsk.email_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskPhone_label(){
		try{
			if(getDataSource().getPageCustomization().getPageField(CwpPageField.PHONE).getRequired())
				return cwpResReader.getString("cwp.preview.ppsk.phone_label", getLocaleFromPreview(previewLanguage)) + "*";
			else{
				return cwpResReader.getString("cwp.preview.ppsk.phone_label", getLocaleFromPreview(previewLanguage));
			}
		}catch(Exception e){
			return cwpResReader.getString("cwp.preview.ppsk.phone_label", getLocaleFromPreview(previewLanguage));
		}
		//return cwpResReader.getString("cwp.preview.ppsk.phone_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskVisiting_label(){
		try{
			if(getDataSource().getPageCustomization().getPageField(CwpPageField.VISITING).getRequired())
				return cwpResReader.getString("cwp.preview.ppsk.visiting_label", getLocaleFromPreview(previewLanguage)) + "*";
			else{
				return cwpResReader.getString("cwp.preview.ppsk.visiting_label", getLocaleFromPreview(previewLanguage));
			}
		}catch(Exception e){
			return cwpResReader.getString("cwp.preview.ppsk.visiting_label", getLocaleFromPreview(previewLanguage));
		}
		//return cwpResReader.getString("cwp.preview.ppsk.visiting_label", getLocaleFromPreview(previewLanguage));
	}
	
	public String getCWPPreviewPpskReason_label(){
		try{
			if(getDataSource().getPageCustomization().getPageField(CwpPageField.COMMENT).getRequired())
				return cwpResReader.getString("cwp.preview.ppsk.reason_label", getLocaleFromPreview(previewLanguage)) + "*";
			else{
				return cwpResReader.getString("cwp.preview.ppsk.reason_label", getLocaleFromPreview(previewLanguage));
			}
		}catch(Exception e){
			return cwpResReader.getString("cwp.preview.ppsk.reason_label", getLocaleFromPreview(previewLanguage));
		}
		//return cwpResReader.getString("cwp.preview.ppsk.reason_label", getLocaleFromPreview(previewLanguage));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*----------- end of cwp multi language support      ------*/
	
	public boolean isCustomizeLoginPage() {
		return "Login".equals(this.customPage);
	}
	
	public boolean isCustomizePPSKPage() {
		return "PPSK".equals(this.customPage);
	}
	
	public boolean isCustomizeSuccessPage() {
		return "Success".equals(this.customPage);
	}
	
	public boolean isCustomizeFailurePage() {
		return "Failure".equals(this.customPage);
	}
	
	public boolean isShowPrompt() {
		return getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_AUTHENTICATED
				|| getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED;
	}
	
	public String isShowVlan() {
		return getDataSource().isOverrideVlan() ? "" : "none";
	}
	
	private String successMessageType;

	/**
	 * getter of successMessageType
	 * @return the successMessageType
	 */
	public String getSuccessMessageType() {
		if(getDataSource().getPageCustomization().isSuccessLibrarySIP()) {
			return "LibrarySIP";
		} else {
			return "General";
		}
	}

	/**
	 * setter of successMessageType
	 * @param successMessageType the successMessageType to set
	 */
	public void setSuccessMessageType(String successMessageType) {
		this.successMessageType = successMessageType;
	}
	
	public String getShowGeneralSection() {
		return getDataSource().getPageCustomization().isSuccessLibrarySIP() ? "none" : "";
	}
	
	public String getShowLibrarySection() {
		return getDataSource().getPageCustomization().isSuccessLibrarySIP() ? "" : "none";
	}
	
	public String getShowFailureGeneralSection() {
		return getDataSource().getPageCustomization().isFailureLibrarySIP() ? "none" : "";
	}

	public String getShowFailureLibrarySection() {
		return getDataSource().getPageCustomization().isFailureLibrarySIP() ? "" : "none";
	}
	
	public String getDefaultSIPStatus() {
		//return CwpPageCustomization.DEFAULT_LIB_SIP_STATUS.replaceAll("\\r\\n", "<br>");
		return cwpResReader.getString("cwp.preview.customize.default_lib_sip_status", getLocaleFromPreview(getCustomizeLanguage())).replaceAll("\\r\\n", "<br>");
	}

	public String getDefaultSIPFines() {
		//return CwpPageCustomization.DEFAULT_LIB_SIP_FINES.replaceAll("\\r\\n", "<br>");
		return cwpResReader.getString("cwp.preview.customize.default_lib_sip_fines", getLocaleFromPreview(getCustomizeLanguage())).replaceAll("\\r\\n", "<br>");
	}
	
	public String getDefaultSIPBlock() {
		return cwpResReader.getString("cwp.preview.customize.default_lib_sip_block", getLocaleFromPreview(getCustomizeLanguage())).replaceAll("\\r\\n", "<br>");
	}
	
	private String failureMessageType;

	/**
	 * getter of failureMessageType
	 * @return the failureMessageType
	 */
	public String getFailureMessageType() {
		if(getDataSource().getPageCustomization().isFailureLibrarySIP()) {
			return "LibrarySIP";
		} else {
			return "General";
		}
	}

	/**
	 * setter of failureMessageType
	 * @param failureMessageType the failureMessageType to set
	 */
	public void setFailureMessageType(String failureMessageType) {
		this.failureMessageType = failureMessageType;
	}

	/**
	 * getter of librarySIPBlock
	 * @return the librarySIPBlock
	 */
	public String getLibrarySIPBlock() {
		return getDataSource().getPageCustomization().getCwpPageMultiLanguageRes(1).getFailureLibrarySIPFines().replaceAll("<br>", "\r\n");
		
		//return librarySIPBlock;
	}
	
	public String getLibrarySIPBlockString() {
		return librarySIPBlock.replace("\r\n", "<br>");
	}

	/**
	 * setter of librarySIPBlock
	 * @param librarySIPBlock the librarySIPBlock to set
	 */
	public void setLibrarySIPBlock(String librarySIPBlock) {
		this.librarySIPBlock = librarySIPBlock;
	}
	
	public String getShowSIP() {
		return this.isEasyMode() ? "none" : "";
	}
	
	private Long selectedCwp;

	public Long getSelectedCwp() {
		return selectedCwp;
	}

	public void setSelectedCwp(Long selectedCwp) {
		this.selectedCwp = selectedCwp;
	}
	
	private List<Long> selectedCwps;

	public List<Long> getSelectedCwps() {
		selectedCwps = new ArrayList<Long>();
		
		/*
		 * get set cwp profile in SSID profile
		 */
		if(this.ssidId != null) {
			if(this.bindTarget == CWP_BIND_TARGET_SSID) { // for SSID
				SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId);
				int accessMode = ssid.getAccessMode();
				
				if(accessMode == SsidProfile.ACCESS_MODE_PSK) {
					if(this.isPpskCwp()) {
						if(ssid.getPpskECwp() != null) {
							selectedCwps.add(ssid.getPpskECwp().getId());
						}
					} else {
						if(ssid.getUserPolicy() != null) {
							selectedCwps.add(ssid.getUserPolicy().getId());
						}
					}
				} else if(accessMode == SsidProfile.ACCESS_MODE_WPA  && ssid.isEnableProvisionPersonal()){
					if(ssid.getWpaECwp() != null){
						selectedCwps.add(ssid.getWpaECwp().getId());
					}
				} else if(accessMode == SsidProfile.ACCESS_MODE_8021X
						|| (accessMode == SsidProfile.ACCESS_MODE_WEP
								&& ssid.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP)) {
					if(ssid.getUserPolicy() != null) {
						selectedCwps.add(ssid.getUserPolicy().getId());
					}
				} else {
					if(ssid.getCwp() != null)
						selectedCwps.add(ssid.getCwp().getId());
				}
			} else { // for PortAccess
			    PortAccessProfile acc = QueryUtil.findBoById(PortAccessProfile.class, ssidId, this);
				selectedCwps.add(acc.getCwp().getId());
			}
		}
		return selectedCwps;
	}

	public void setSelectedCwps(List<Long> selectedCwps) {
		this.selectedCwps = selectedCwps;
	}
	
	public List<CheckItem> getAvailableCwps() {
		FilterParams params = null;
		
		/*
		 * set the filter by ssid type
		 */
		if(ssidId != null && bindTarget == CWP_BIND_TARGET_SSID) {
			SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId);
			
			if(ssid != null) {
				int accessMode = ssid.getAccessMode();
				boolean idmEnabled = ssid.isEnabledIDM();
				
				if(accessMode == SsidProfile.ACCESS_MODE_PSK) {
					if(this.isPpskCwp()) {
					    if(idmEnabled) {
					        /*
	                        params = new FilterParams(
	                                "((registrationType = :s1 and ppskServerType = :s2) or " +
	                                "(idmSelfReg = :s3 and registrationType = :s4 and ppskServerType = :s5))",
	                                new Object[] {Cwp.REGISTRATION_TYPE_PPSK, Cwp.PPSK_SERVER_TYPE_AUTH, 
	                                        true, Cwp.REGISTRATION_TYPE_PPSK, Cwp.PPSK_SERVER_TYPE_REG});
	                        */
	                        params = new FilterParams(
	                                "registrationType = :s1 and ppskServerType = :s2",
	                                        new Object[] {Cwp.REGISTRATION_TYPE_PPSK, Cwp.PPSK_SERVER_TYPE_AUTH});
					    } else {
                            params = new FilterParams(
                                    "idmSelfReg = :s1 and registrationType = :s2",
                                    new Object[] { false, Cwp.REGISTRATION_TYPE_PPSK });
					    }
					} else {
						params = new FilterParams("registrationType", Cwp.REGISTRATION_TYPE_EULA);
					}
				} else if(accessMode == SsidProfile.ACCESS_MODE_8021X
						|| (accessMode == SsidProfile.ACCESS_MODE_WEP
								&& ssid.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP)) {
					params = new FilterParams("registrationType", Cwp.REGISTRATION_TYPE_EULA);
				} else if(accessMode == SsidProfile.ACCESS_MODE_WPA && ssid.isEnableProvisionPersonal()) {
					Collection<Byte> collection = new ArrayList<Byte>();
					collection.add(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
					collection.add(Cwp.REGISTRATION_TYPE_EXTERNAL);
					
					params = new FilterParams("registrationType", collection);
		        }else {
					Collection<Byte> collection = new ArrayList<Byte>();
					
					if(idmEnabled) {
					    // filter the CWP for the IDM
					    Collection<Byte> collection2 = new ArrayList<Byte>();
					    if((accessMode == SsidProfile.ACCESS_MODE_WPA)
					            || (accessMode == SsidProfile.ACCESS_MODE_WEP
					            && ssid.getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK)) {
					        collection.add(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
					        
					        collection2.add(Cwp.REGISTRATION_TYPE_BOTH);
					    } else if(accessMode == SsidProfile.ACCESS_MODE_OPEN) {
					        collection.add(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
					        collection.add(Cwp.REGISTRATION_TYPE_EULA);
					        
					        collection2.add(Cwp.REGISTRATION_TYPE_BOTH);
					    } 
                        params = new FilterParams(
                                "((idmSelfReg = :s1 and registrationType in (:s2)) or " +
                                "(idmSelfReg = :s3 and registrationType in (:s4)))",
                                new Object[] {false, collection, true, collection2});
					} else {
					    collection.add(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
					    collection.add(Cwp.REGISTRATION_TYPE_REGISTERED);
					    collection.add(Cwp.REGISTRATION_TYPE_BOTH);
					    collection.add(Cwp.REGISTRATION_TYPE_EXTERNAL);
					    collection.add(Cwp.REGISTRATION_TYPE_EULA);
					    params = new FilterParams("idmSelfReg = :s1 and registrationType in (:s2)", new Object[]{false, collection});
					}
				}
			}
		}
		if(this.bindTarget == CWP_BIND_TARGET_LAN){
		    PortAccessProfile access = QueryUtil.findBoById(PortAccessProfile.class, ssidId);
		    if(null != access) {
		        
		        if(access.isEnabledIDM() && access.isEnabledCWP()) {
	                Collection<Byte> collection = new ArrayList<Byte>();
	                Collection<Byte> collection2 = new ArrayList<Byte>();
	                
	                collection.add(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
                    /*collection.add(Cwp.REGISTRATION_TYPE_EULA);*/
                    
                    collection2.add(Cwp.REGISTRATION_TYPE_BOTH);
                    
                    params = new FilterParams(
                            "((idmSelfReg = :s1 and registrationType in (:s2)) or " +
                            "(idmSelfReg = :s3 and registrationType in (:s4)))",
                            new Object[] {false, collection, true, collection2});
		        } else {
		            Collection<Byte> collection = new ArrayList<Byte>();
		            collection.add(Cwp.REGISTRATION_TYPE_AUTHENTICATED);
		            collection.add(Cwp.REGISTRATION_TYPE_REGISTERED);
		            collection.add(Cwp.REGISTRATION_TYPE_BOTH);
		            collection.add(Cwp.REGISTRATION_TYPE_EXTERNAL);
		            collection.add(Cwp.REGISTRATION_TYPE_EULA);
		            
		            params = new FilterParams("idmSelfReg = :s1 and registrationType in (:s2)", new Object[]{false, collection});
		        }
		    }
		    
		}
		
		return this.getBoCheckItems("cwpName", Cwp.class, params);
	}
	
	private void setCwpToSSID() throws JSONException {
		jsonObject = new JSONObject();
		
		if(this.selectedCwps == null
				|| this.selectedCwps.isEmpty()) {
			jsonObject.put("ok", false);
			
			if(this.bindTarget == CWP_BIND_TARGET_SSID) {
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.cwp.failed"));
			} else {
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.cwp.failed"));
			}
			
			return ;
		}
		
		Cwp cwp = QueryUtil.findBoById(Cwp.class, this.selectedCwps.get(0));
		
		if(this.bindTarget == CWP_BIND_TARGET_SSID) { // for SSID
			if(ssidId != null) {
				SsidProfile ssid = QueryUtil.findBoById(SsidProfile.class, ssidId, this);
				changeSSIDByCWP(ssid, cwp);
			} else {
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.cwp.failed"));
			}
		} else { // for LAN
			if(ssidId != null) {
			    PortAccessProfile acc = QueryUtil.findBoById(PortAccessProfile.class, ssidId, this);
				changeAccessByCWP(acc, cwp);
			} else {
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.cwp.failed"));
			}
		}
	}
	
	/* ssidId could the id of an SSID or LAN */
	private Long ssidId;

	public Long getSsidId() {
		return ssidId;
	}

	public void setSsidId(Long ssidId) {
		this.ssidId = ssidId;
	}
	
	private boolean ppskCwp;
	private boolean wpaCwp;
	public boolean isWpaCwp() {
		return wpaCwp;
	}
	public void setWpaCwp(boolean wpaCwp) {
		this.wpaCwp = wpaCwp;
	}

	public boolean isPpskCwp() {
		return ppskCwp;
	}

	public void setPpskCwp(boolean ppskCwp) {
		this.ppskCwp = ppskCwp;
	}
	
	private static final int CWP_BIND_TARGET_SSID = 1;
	private static final int CWP_BIND_TARGET_LAN = 2;
	
	/*
	 * 0: No target
	 * 1: SSID
	 * 2: LAN
	 */
	private int bindTarget;

	public int getBindTarget() {
		return bindTarget;
	}

	public void setBindTarget(int bindTarget) {
		this.bindTarget = bindTarget;
	}
	
	private String enableRegistrationType = "";

	public String getEnableRegistrationType() {
		return enableRegistrationType;
	}

	public void setEnableRegistrationType(String enableRegistrationType) {
		this.enableRegistrationType = enableRegistrationType;
	}
	
	private void changeSSIDByCWP(SsidProfile ssid, Cwp cwp) throws JSONException {
		if(ssid != null) {
			int accessMode = ssid.getAccessMode();
			
			if(accessMode == SsidProfile.ACCESS_MODE_PSK) {
				if(this.isPpskCwp()) {
					ssid.setPpskECwp(cwp);
					if (cwp.getPpskServerType()!=Cwp.PPSK_SERVER_TYPE_AUTH || ssid.isEnabledIDM()) {
						ssid.setRadiusAssignmentPpsk(null);
					}
					if(!(cwp.getPpskServerType()==Cwp.PPSK_SERVER_TYPE_AUTH && ssid.isEnabledIDM())) {
					    ssid.setRadiusAssignment(null);
					}
				} else {
					ssid.setUserPolicy(cwp);
				}
			} else if(accessMode == SsidProfile.ACCESS_MODE_8021X
					|| (accessMode == SsidProfile.ACCESS_MODE_WEP
							&& ssid.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP)) {
				ssid.setUserPolicy(cwp);
			} else if(accessMode == SsidProfile.ACCESS_MODE_WPA && ssid.isEnableProvisionPersonal() && cwp.isAuthRegType()) {
					ssid.setWpaECwp(cwp);
					ssid.setRadiusAssignmentPpsk(null);
			} 
			else {
				ssid.setCwp(cwp);
			}
			
			if(!this.isPpskCwp()) {
				if (cwp.getRegistrationType()==Cwp.REGISTRATION_TYPE_REGISTERED
						&& !ssid.getMacAuthEnabled()
						&& !ssid.getEnabledUseGuestManager()){
					ssid.setUserProfileDefault(null);
					ssid.setRadiusAssignment(null);
					if (ssid.getRadiusUserGroups()!=null) {
						ssid.getRadiusUserGroups().clear();
					}
					ssid.setEnableAssignUserProfile(false);
					
					ssid.getRadiusUserProfile().clear();
				}
				
				if (cwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_REGISTERED &&
						cwp.getRegistrationType()!=Cwp.REGISTRATION_TYPE_BOTH){
					ssid.setUserProfileSelfReg(null);
				}
				
				if (cwp.getRegistrationType()==Cwp.REGISTRATION_TYPE_EULA
						&& !ssid.getMacAuthEnabled()
						&& !ssid.getEnabledUseGuestManager()
						&& ssid.getMgmtKey() != SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X
						&& ssid.getMgmtKey() != SsidProfile.KEY_MGMT_WPA_EAP_802_1_X
						&& ssid.getMgmtKey() != SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X
						&& ssid.getMgmtKey() != SsidProfile.KEY_MGMT_DYNAMIC_WEP) {
					ssid.setRadiusAssignment(null);
					if (ssid.getRadiusUserGroups()!=null) {
						ssid.getRadiusUserGroups().clear();
					}
					ssid.setEnableAssignUserProfile(false);
					if (ssid.getAccessMode()!=SsidProfile.ACCESS_MODE_PSK) {
					    if(ssid.isEnabledIDM() && ssid.getAccessMode()==SsidProfile.ACCESS_MODE_OPEN) {
					        // do nothing
					    } else {
					        ssid.getRadiusUserProfile().clear();
					    }
					}
				}
			}
			
            alterSSIDIDMFlag(ssid, cwp);
			
			try {
				updateBoWithEvent(ssid);
				jsonObject.put("ok", true);
			} catch (Exception e) {
				log.error(MgrUtil.getUserMessage("error.config.networkPolicy.ssid.cwp.failed"), e);
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.cwp.failed"));
			}
		} else {
			jsonObject.put("ok", false);
			jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.ssid.cwp.failed"));
		}
	}

    private void alterSSIDIDMFlag(SsidProfile ssid, Cwp cwp) {
        final int accessMode = ssid.getAccessMode();
        final byte registrationType = cwp.getRegistrationType();
        if (accessMode == SsidProfile.ACCESS_MODE_8021X
                && registrationType != Cwp.REGISTRATION_TYPE_EULA) {
            // for 802.1X SSID only support IDM + Use Policy Acceptence CWP
            ssid.setEnabledIDM(false);
        } else if (accessMode == SsidProfile.ACCESS_MODE_PSK
                && !(registrationType == Cwp.REGISTRATION_TYPE_EULA 
                || registrationType == Cwp.REGISTRATION_TYPE_PPSK)) {
            // for PPSK SSID now support,
            // 1 - IDM + Use Policy Acceptence CWP
            // 2 - IDM + PPSK CWP
            ssid.setEnabledIDM(false);
        } else if (accessMode == SsidProfile.ACCESS_MODE_WPA
                && !(registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                || registrationType == Cwp.REGISTRATION_TYPE_BOTH)) {
            // for PSK SSID, now support authentication (Bug 19000) and Self-reg (both/self-reg) via IDM CWP
            ssid.setEnabledIDM(false);
        } else if (accessMode == SsidProfile.ACCESS_MODE_OPEN
                && !(registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED 
                || registrationType == Cwp.REGISTRATION_TYPE_EULA
                || registrationType == Cwp.REGISTRATION_TYPE_BOTH)) {
            // for Open SSID,
            // 1 - support IDM + Authentication as Auth Proxy (Bug 19000)
            // 2 - IDM + Use Policy Acceptence as Anonymous Access 
            // 3 - Self-reg via IDM
            ssid.setEnabledIDM(false);
        } else if (accessMode == SsidProfile.ACCESS_MODE_WEP) {
            if (ssid.getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
                    && !(registrationType == Cwp.REGISTRATION_TYPE_AUTHENTICATED
                            || registrationType == Cwp.REGISTRATION_TYPE_BOTH)) {
                // For Key = WEP SSID, support IDM + Authentication as Auth Proxy (Bug 19000) and Self-reg (both/self-reg) via IDM CWP
                ssid.setEnabledIDM(false);
            } else if (ssid.getMgmtKey() == SsidProfile.KEY_MGMT_DYNAMIC_WEP
                    && registrationType != Cwp.REGISTRATION_TYPE_EULA) {
                ssid.setEnabledIDM(false);
            }
        }
    }
	
	private void changeAccessByCWP(PortAccessProfile acc, Cwp cwp) throws JSONException {
		if(acc != null) {
			acc.setCwp(cwp);
			
            if (cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
                    && !acc.isEnabledMAC()) {
                acc.setDefUserProfile(null);
                acc.getAuthOkUserProfile().clear();
                acc.getAuthFailUserProfile().clear();
                acc.getAuthOkDataUserProfile().clear();
            }

            if (cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED
                    && cwp.getRegistrationType() != Cwp.REGISTRATION_TYPE_BOTH) {
                acc.setSelfRegUserProfile(null);
            }
            
            if (acc.getPortType() != PortAccessProfile.PORT_TYPE_ACCESS
                    || acc.isEnabledMAC()
                    || (!acc.isEnabled8021X() && !acc.isEnabledCWP())) {
                // for wired IDM, only access + 802.1x or access + CWP can enabled IDM
                acc.setEnabledIDM(false);
            }
            
            
            if(acc.isIDMAuthEnabled()) {
                acc.setEnabledMAC(false);
                acc.setRadiusAssignment(null);
            } else if(!acc.isRadiusAuthEnable()) {
				acc.setEnabledMAC(false);
				acc.setEnabled8021X(false);
				acc.setRadiusAssignment(null);
				if (acc.getRadiusUserGroups()!=null) {
					acc.getRadiusUserGroups().clear();
				}
				acc.setEnableAssignUserProfile(false);
			}
			
			
			try {
				updateBoWithEvent(acc);
				jsonObject.put("ok", true);
			} catch (Exception e) {
				log.error(MgrUtil.getUserMessage("error.config.networkPolicy.lan.cwp.failed"), e);
				jsonObject.put("ok", false);
				jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.cwp.failed"));
			}
		} else {
			jsonObject.put("ok", false);
			jsonObject.put("msg", MgrUtil.getUserMessage("error.config.networkPolicy.lan.cwp.failed"));
		}
	}
	
	public String getEnableUsePolicyStyle() {
		if (getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_EXTERNAL
				&& getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED
				&& getDataSource().getRegistrationType() != Cwp.REGISTRATION_TYPE_EULA) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getShowFootImage() {
		return NmsUtil.isHMForOEM() ? "none" : "";
	}
	
	public boolean isHMForOEM(){
		return NmsUtil.isHMForOEM() ? true : false;
	}
	
	private boolean idmSelfReg;

	public boolean isIdmSelfReg() {
		return idmSelfReg;
	}

	public void setIdmSelfReg(boolean idmSelfReg) {
		this.idmSelfReg = idmSelfReg;
	}

	public String getLabelRepresentingMarkPostValue() {
		return labelRepresentingMarkPostValue;
	}

	public void setLabelRepresentingMarkPostValue(
			String labelRepresentingMarkPostValue) {
		this.labelRepresentingMarkPostValue = labelRepresentingMarkPostValue;
	}
	
	public String getShowLibrarySIPStyle(){
		if(null != getDataSource()){
			return idmSelfReg && (getDataSource().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH) ? "none":"";
		}
		return "";
	}
	
	protected Long successExternalURLId;
	
	protected Long failureExternalURLId;
	
	protected List<CheckItem> ipAddressIdList;

	public List<CheckItem> getIpAddressIdList() {
		return ipAddressIdList;
	}

	public void setIpAddressIdList(List<CheckItem> ipAddressIdList) {
		this.ipAddressIdList = ipAddressIdList;
	}

	public Long getSuccessExternalURLId() {
		return successExternalURLId;
	}

	public void setSuccessExternalURLId(Long successExternalURLId) {
		this.successExternalURLId = successExternalURLId;
	}

	public Long getFailureExternalURLId() {
		return failureExternalURLId;
	}

	public void setFailureExternalURLId(Long failureExternalURLId) {
		this.failureExternalURLId = failureExternalURLId;
	}
	
	public String getSingleSuccessURL(){
		if(null != getDataSource()){
			return getDataSource().getExternalURLSuccessType() == Cwp.EXTERNAL_URL_SINGLE ? "":"none";
		}
		return "";
	}
	
	public String getTaggedSuccessURL(){
		if(null != getDataSource()){
			return getDataSource().getExternalURLSuccessType() ==  Cwp.EXTERNAL_URL_SINGLE ? "none":"";
		}
		return "none";
	}
	
	public String getSingleFailureURL(){
		if(null != getDataSource()){
			return getDataSource().getExternalURLFailureType() == Cwp.EXTERNAL_URL_SINGLE? "":"none";
		}
		return "";
	}
	
	public String getTaggedFailureURL(){
		if(null != getDataSource()){
			return getDataSource().getExternalURLFailureType() == Cwp.EXTERNAL_URL_SINGLE? "none":"";
		}
		return "none";
	}
	
	public EnumItem[] getExternalURLSingle() {
		return new EnumItem[] { new EnumItem(Cwp.EXTERNAL_URL_SINGLE,
				getText("glasgow_08.config.cwp.external.url.single")) };
	}
	
	public EnumItem[] getExternalURLTagged() {
		return new EnumItem[] { new EnumItem(Cwp.EXTERNAL_URL_TAGGED,
				getText("glasgow_08.config.cwp.external.url.tagged")) };
	}
	
}