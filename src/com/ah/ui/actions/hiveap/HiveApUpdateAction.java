package com.ah.ui.actions.hiveap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.BeConfigModule;
import com.ah.be.config.event.AhBootstrapGeneratedEvent;
import com.ah.be.config.hiveap.BootstrapConfigObject;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateObjectException;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.autoserver.AutoSelectDeviceServer;
import com.ah.be.config.hiveap.distribution.ImageUploadGroup;
import com.ah.be.db.configuration.ConfigAuditProcessor;
import com.ah.be.hiveap.ImageManager;
import com.ah.be.hiveap.L7SignatureMng;
import com.ah.be.parameter.BeParaModule;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.be.performance.BeDAInforProcessor.DAInformationTimer;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.be.topo.BeTopoModuleParameters;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.CapwapSettings;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.ConfigTemplateSsid;
import com.ah.bo.hiveap.ConfigTemplateVlanNetwork;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInterface;
import com.ah.bo.hiveap.DeviceStpSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.hiveap.HiveApUpdateResult;
import com.ah.bo.hiveap.HiveApUpdateSettings;
import com.ah.bo.hiveap.HiveApUpdateSettings.ActivateType;
import com.ah.bo.hiveap.HiveApUpdateSettings.ConfigSelectType;
import com.ah.bo.hiveap.HiveApUpdateSettings.ImageSelectionType;
import com.ah.bo.hiveap.HiveApUpdateSettings.TransferType;
import com.ah.bo.hiveap.LSevenSignatures;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.DevicePolicyRule;
import com.ah.bo.network.DnsServiceProfile;
import com.ah.bo.network.FirewallPolicyRule;
import com.ah.bo.network.MacFilter;
import com.ah.bo.network.MacFilterInfo;
import com.ah.bo.network.PortForwarding;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.network.StpSettings;
import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;
import com.ah.bo.network.VpnNetworkSub;
import com.ah.bo.network.VpnService;
import com.ah.bo.port.PortAccessProfile;
import com.ah.bo.port.PortBasicProfile;
import com.ah.bo.port.PortGroupProfile;
import com.ah.bo.useraccess.LdapServerOuUserProfile;
import com.ah.bo.useraccess.LocalUser;
import com.ah.bo.useraccess.LocalUserGroup;
import com.ah.bo.useraccess.RadiusOnHiveap;
import com.ah.bo.useraccess.UserProfile;
import com.ah.bo.useraccess.UserProfileAttribute;
import com.ah.bo.useraccess.UserProfileVlanMapping;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.RadioProfile;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.config.MacFiltersAction;
import com.ah.ui.actions.config.SsidProfilesAction;
import com.ah.ui.actions.config.VpnServiceAction;
import com.ah.ui.actions.gml.TemporaryAccountAction;
import com.ah.util.CheckItem;
import com.ah.util.CountryCode;
import com.ah.util.EnumItem;
import com.ah.util.HiveApUtils;
import com.ah.util.HmException;
import com.ah.util.HmMessageCodes;
import com.ah.util.MgrUtil;
import com.ah.util.NameValuePair;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.bo.np.NpUserProfileVlanMappingUtil;
import com.ah.util.devices.impl.Device;
import com.ah.util.http.HttpCommunication;

public class HiveApUpdateAction extends BaseAction implements QueryBo,
		UpdateParameters {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(HiveApUpdateAction.class
			.getSimpleName());

	private static final String UPDATE_LIST_TYPE = "hiveAp_update_list_type";

	private static final String UPDATE_RESULT_LIST = "hiveApUpdateResultsList";
	private static final String UPDATE_LIST_TYPE_IMAGE = "imageInput";
	private static final String UPDATE_LIST_TYPE_L7_SIGNATURE = "signatureInput";
	private static final String UPDATE_LIST_TYPE_CONFIG = "configInput";
	private static final String UPDATE_LIST_TYPE_BOOTSTRAP = "bootstrapInput";
	private static final String UPDATE_LIST_TYPE_COUNTRYCODE = "countryCodeInput";
	private static final String UPDATE_LIST_TYPE_POE = "poeInput";
	private static final String UPDATE_LIST_TYPE_NETDUMP = "netdumpInput";
	private static final String UPDATE_OUTDOOR_SETTINGS = "outdoorSettingsInput";

	public static final String UPDATE_INITIAL_IDs = "initialSelectedIds";
	public static final String UPDATE_IMAGE_SELECTED_IDs = "imageSelectedIds";
	public static final String UPDATE_OS_DETECTION_IDs = "osDetectionIds";
	public static final String UPDATE_CONFIG_SELECTED_IDS = "configSelectedIds";
	public static final String UPDATE_CONFIG_SELECTED_IDS_EX = "configSelectedIds_ex";
	public static final String UPDATE_BOOTSTRAP_SELECTED_IDs = "bootstrapSelectedIds";
	public static final String UPDATE_COUNTRYCODE_SELECTED_IDs = "countryCodeSelectedIds";
	public static final String UPDATE_POE_SELECTED_IDs = "poeSelectedIds";
	public static final String UPDATE_NETDUMP_SELECTED_IDs = "netdumpSelectedIds";
	public static final String UPDATE_OUTDOORSETTINGS_SELECTED_IDS = "outdoorSettingsSelectedids";
	public static final String UPDATE_DISTRIBUTED_SERVER_LIST_GROUP_BY_HIVE = "disSerListGPHive";
	public static final String UPDATE_ID_AND_HOSTNAME_MAPPING = "idAndHostnameMapping";
	public static final String UPDATE_ID_AND_HIVENAME_MAPPING = "idAndHivenameMapping";
	public static final String UPDATE_CURRENT_DISTRIBUTED_SERVER = "currentDistributedServer";
	public static final String UPDATE_HIVEAP_NETWORKPOLICY_CHANGED = "hiveApsNWPolicyChanged";
	public static final String UPDATE_L7_SIGNATURE_SELECTED_IDs = "l7SignatureSelectedIds";
	public static final String SIMPLIFIED_UPDATE_SELECTED_IDs = "simplifiedUpdateSelectedIds";
	
	public static final String JSON_RESULT_TYPE_KEY = "resultType";
	public static final String JSON_RESULT_TYPE_SUFFIX = "_result";

	// private String domainName = "";
	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {		
			// check session token for CSRF attack
			if (!isCSRFTokenValida() && ("updateCountryCode".equals(operation) 
										|| "updateBootstrap".equals(operation)
										|| "uploadWizard".equals(operation)
										|| "uploadImage".equals(operation)
										|| "uploadSignature".equals(operation)
										|| "updateBootstrap".equals(operation)
										|| "updatePoe".equals(operation)
										|| "updateNetdump".equals(operation))) {
				generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("security.csrfattack") + getLastTitle());

				throw new HmException(MgrUtil.getUserMessage("error.security.invalidRequest"),						
						HmMessageCodes.SECURITY_REQUEST_INVALID, new String[] { "mitigate" });
			}
			
			if ("wizard".equals(operation)) {
				removeSessionAttrs();
				prepareConfigtBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_CONFIG);
				return UPDATE_LIST_TYPE_CONFIG;
			} else if ("imageInput".equals(operation)) {
				new Thread(new DAInformationTimer()).start();
				removeSessionAttrs();
				prepareImageBoList();
				checkSupportedUpdateImage();
				prepareDistributedDownload();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_IMAGE);
				return UPDATE_LIST_TYPE_IMAGE;
			} else if ("signatureInput".equals(operation)) {
				removeSessionAttrs();
				prepareL7SignatureBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_L7_SIGNATURE);
				return UPDATE_LIST_TYPE_L7_SIGNATURE;
			} else if ("bootstrapInput".equals(operation)) {
				removeSessionAttrs();
				prepareBootstrapBoList();
				prepareBootstrapParams();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_BOOTSTRAP);
				return UPDATE_LIST_TYPE_BOOTSTRAP;
			} else if ("countryCodeInput".equals(operation)) {
				removeSessionAttrs();
				prepareCountryCodeBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_COUNTRYCODE);
				return UPDATE_LIST_TYPE_COUNTRYCODE;
			} else if ("countryCodeInputJson".equals(operation)) {
				removeSessionAttrs();
				prepareCountryCodeBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_COUNTRYCODE);
				return "countryCodeInputJson";
			} else if (UPDATE_OUTDOOR_SETTINGS.equals(operation)) {
				removeSessionAttrs();
				prepareOutdoorSettingsBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_OUTDOOR_SETTINGS);
				return UPDATE_OUTDOOR_SETTINGS;
			} else if ("poeInput".equals(operation)) {
				removeSessionAttrs();
				preparePoeBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_POE);
				return UPDATE_LIST_TYPE_POE;
			} else if ("netdumpInput".equals(operation)) {
				removeSessionAttrs();
				prepareNetdumpBoList();
				MgrUtil.setSessionAttribute(UPDATE_LIST_TYPE,
						UPDATE_LIST_TYPE_NETDUMP);
				return UPDATE_LIST_TYPE_NETDUMP;
			} else if ("uploadWizard".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", configConfiguration:" + configConfiguration
						+ ", configCwp:" + configCwp + ", configCertificate:"
						+ configCertificate + ", configUserDatabase:"
						+ configUserDatabase);
				if("uploadConfigEx".equalsIgnoreCase(this.getExConfigGuideFeature())) {
					removeSessionAttrs();
					removeUploadWaringSession();
				}
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_SCRIPT_WIZARD);
				storeIdListToSession(UPDATE_CONFIG_SELECTED_IDS,
						selectedConfigIds, getPageIds());
				if("uploadConfigEx".equalsIgnoreCase(this.getExConfigGuideFeature())) {
					storeExIdListToSession(UPDATE_CONFIG_SELECTED_IDS_EX, selectedConfigIds);
				}
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					removeUploadWaringSession();
					removeNWPolicyChangeSession();
					logUpdateOperation();
					if("uploadConfigEx".equalsIgnoreCase(this.getExConfigGuideFeature())) {
						jsonObject = getUploadConfigExJson(operation);
						if(getUserContext().isSourceFromIdm()){
							jsonObject.put("disDone", true);
						}
						return "json";
					}else{
					return UPDATE_RESULT_LIST;
					}
				} else {
					prepareConfigtBoList();
					if("uploadConfigEx".equalsIgnoreCase(this.getExConfigGuideFeature())) {
						jsonObject = getUploadConfigExJson(operation);
						if(getUserContext().isSourceFromIdm()){
							jsonObject.put("disDone", true);
						}
						return "json";
					}else{
						return UPDATE_LIST_TYPE_CONFIG;
					}
				}
			} else if ("doneToIDM".equals(operation)){
				jsonObject = new JSONObject();
				try {
					JSONObject js;
					JSONArray ja = new JSONArray();

					String sql ="select distinct s.ssidname,s.accessMode,s.cwpSelectEnabled from config_template_ssid c, ssid_profile s " +
							"where c.ssid_profile_id=s.id and s.enabledIDM=true and s.owner=" + getDomain().getId();
					List<?> lst = QueryUtil.executeNativeQuery(sql);
					if(!lst.isEmpty()) {
						for(Object obj: lst){
							Object[] items = (Object[]) obj;
							String ssidName = items[0].toString();
							int accessMode = Integer.parseInt(items[1].toString());
							boolean cwpEnabled = Boolean.valueOf(items[2].toString());
							if(accessMode==SsidProfile.ACCESS_MODE_8021X || accessMode==SsidProfile.ACCESS_MODE_PSK) {
								js= new JSONObject();
								js.put("n", ssidName);
								js.put("t", accessMode);
								ja.put(js);
							} else {
								if(cwpEnabled) {
									js= new JSONObject();
									js.put("n", ssidName);
									js.put("t", accessMode);
									ja.put(js);
								}
							}
						}
					}

					jsonObject.put("url", getUserContext().getSourceUrl());
					if(getUserContext().getSourceUrl()!=null && getUserContext().getSourceUrl().indexOf("?")>0){
						jsonObject.put("params", "&ssids=");
						jsonObject.put("paramsValue", ja.toString());
					} else {
						jsonObject.put("params", "?ssids=");
						jsonObject.put("paramsValue", ja.toString());
					}

					//jsonObject.put("url", "hm/reports.action?operation=summary&tid=1001");
					//jsonObject.put("params", "&v=" + ja.toString());

					jsonObject.put("t", true);
				} catch (Exception e) {
					jsonObject.put("t", false);
					jsonObject.put("m", e.getMessage());
				}
				return "json";
			} else if ("checkNetworkPolicy".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", configConfiguration:" + configConfiguration
						+ ", configCwp:" + configCwp + ", configCertificate:"
						+ configCertificate + ", configUserDatabase:"
						+ configUserDatabase);
				jsonObject = new JSONObject();
				jsonObject.put(JSON_RESULT_TYPE_KEY, operation+JSON_RESULT_TYPE_SUFFIX);
				if (isEasyMode()) {
					jsonObject.put("t", true);
					return "json";
				} else {
					clearErrorsAndMessages();
					Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_SCRIPT_WIZARD);
					StringBuilder idLstStr = new StringBuilder();
					for(Long idObj:selectedConfigIds){
						if (idLstStr.length()!=0) {
							idLstStr.append(",");
						}
						idLstStr.append(idObj.toString());
					}

					String strSql = "select _t1.hostName, _t2.enabledhcp, _t1.TEMPLATE_ID, _t1.id from HIVE_AP _t1 left join hiveap_device_interface _t2 on _t1.id= _t2.hiveap_id and _t2.deviceiftype=" + AhInterface.DEVICE_IF_TYPE_ETH0 + " where _t1.id in (" + idLstStr.toString() + ")";
					List<?> apList = QueryUtil.executeNativeQuery(strSql);
					StringBuffer warningMsg=new StringBuffer();
					//when the port type is Phone&Data,the lldp should be enabled
					StringBuffer lldpWarningMsg = new StringBuffer();
					Set<String> setPolicyName = new HashSet<>();
					boolean blnWarnMsgDisplay=false;
					StringBuilder bufferWarningMsg = new StringBuilder();
					HashMap<Long,ConfigTemplate> mapConfigTemplate = new HashMap<>();

					for(Object oneObj: apList){
						Object[] oneAp = (Object[])oneObj;
						ConfigTemplate wlanPolicy;
						if (mapConfigTemplate.get(Long.parseLong(oneAp[2].toString()))!=null) {
							wlanPolicy = mapConfigTemplate.get(Long.parseLong(oneAp[2].toString()));
						} else {
							wlanPolicy = findBoById(ConfigTemplate.class, Long.parseLong(oneAp[2].toString()), this);
							mapConfigTemplate.put(wlanPolicy.getId(), wlanPolicy);
						}

						HiveAp oneHiveAP = findBoById(HiveAp.class, Long.parseLong(oneAp[3].toString()), this);

						if (oneHiveAP.isBranchRouter()){
							blnWarnMsgDisplay=true;
						}
						if (oneHiveAP.isVpnGateway()) {
							if (oneHiveAP.getEth0Interface()==null ||
									oneHiveAP.getEth0Interface().getIpAddress()==null ||
									oneHiveAP.getEth0Interface().getIpAddress().equals("")) {
								jsonObject.put("t", false);
								jsonObject.put("errorMsg", getText("error.hiveap.cvg.wan.ip.empty",
														new String[]{oneHiveAP.getHostName()}));
								return "json";
							}
						}

//						if (oneAp[1]==null || oneAp[1].toString().equals("") ||
//								Boolean.valueOf(oneAp[1].toString())
//								/*|| !wlanPolicy.isBlnWirelessRouter()*/
//								||
//						if (!wlanPolicy.getConfigType().isRouterContained()
//								|| !oneHiveAP.isBranchRouter()
//								|| (oneHiveAP.isBranchRouter() && !checkDhcpWhenUpload(oneHiveAP))){
										// connection type is pppoe and eth0 is primary
//							oneHiveAP.getEth0Interface().getConnectionType().equals("3") &&
//							oneHiveAP.getEth0Interface().getWanOrder() == 1 )){

						String ret="";
						if (!wlanPolicy.getConfigType().isBonjourOnly()) {
							ret= checkNetworkPolicy(wlanPolicy, false, warningMsg,setPolicyName,oneHiveAP,lldpWarningMsg);
						}
						if (!"".equals(ret)){
							jsonObject.put("t", false);
							jsonObject.put("errorMsg", ret);
							return "json";
						}

//						} else {
//								String ret="";
//								if (!wlanPolicy.getConfigType().isBonjourOnly()) {
//									ret= checkNetworkPolicy(wlanPolicy, true, warningMsg, setPolicyName,oneHiveAP,lldpWarningMsg);
//								}
//								if (!"".equals(ret)){
//									jsonObject.put("t", false);
//									jsonObject.put("m", ret);
//									return "json";
//								}
//						}
						if(oneHiveAP.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER){
							if(mapConfigTemplate.get(Long.parseLong(oneAp[2].toString()))!=null){
								if(mapConfigTemplate.get(Long.parseLong(oneAp[2].toString())).getRoutingProfilePolicy()!=null){
									if(NmsUtil.compareSoftwareVersion(oneHiveAP.getSoftVer(), "6.0.1.0")<0){
										jsonObject.put("t",false);
										jsonObject.put("errorMsg", getText("error.hiveAp.upload.RoutigPolicy.notmatach.ApVersion",new String []{oneHiveAP.getHostName()}));
										return "json";
									}
								}
							}
						}

						//check stp settings
						if(oneHiveAP.getDeviceStpSettings() != null
								&& oneHiveAP.getDeviceStpSettings().isOverrideStp()
								&& oneHiveAP.getDeviceStpSettings().isEnableStp()){
							String ret1 = checkStpSettings(wlanPolicy,oneHiveAP);
							if (!"".equals(ret1)){
								jsonObject.put("t", false);
								jsonObject.put("errorMsg", ret1);
								return "json";
							}
						}

						if (oneHiveAP.isWifi1Available() && oneHiveAP.getDeviceType()!=HiveAp.Device_TYPE_VPN_GATEWAY) {
							if (oneHiveAP.getWifi0RadioProfile()!=null && oneHiveAP.getWifi1RadioProfile()!=null) {
								boolean wifi0Model = false;
								if (oneHiveAP.getWifi0RadioProfile().isEnableClientLoadBalance() &&
										oneHiveAP.getWifi0RadioProfile().getLoadBalancingMode() ==
											RadioProfile.LOAD_BALANCE_MODE_STATION_NUMBER) {
									wifi0Model = true;
								}

								boolean wifi1Model = false;
								if (oneHiveAP.getWifi1RadioProfile().isEnableClientLoadBalance() &&
										oneHiveAP.getWifi1RadioProfile().getLoadBalancingMode() ==
											RadioProfile.LOAD_BALANCE_MODE_STATION_NUMBER) {
									wifi1Model = true;
								}

								if (wifi0Model ^ wifi1Model) {
									blnWarnMsgDisplay=true;
									if (!bufferWarningMsg.toString().equals("")) {
										bufferWarningMsg.append(", (").append(oneHiveAP.getHostName()).append(")");
									} else {
										bufferWarningMsg.append("(").append(oneHiveAP.getHostName()).append(")");
									}
								}
							}
						}
					}

					if (!getActionMessages().isEmpty()) {
						blnWarnMsgDisplay = true;
					}

					if(lldpWarningMsg.length() > 0){
						blnWarnMsgDisplay = true;
					}

					if ((!warningMsg.toString().equals("") || !bufferWarningMsg.toString().equals("")
							|| !lldpWarningMsg.toString().equals("") || !getActionMessages().isEmpty()) && blnWarnMsgDisplay) {
						StringBuilder wn = new StringBuilder();
						if (!warningMsg.toString().equals("")) {
							wn.append(warningMsg.toString()).append("<br/><br/>");
						}
						if (!lldpWarningMsg.toString().equals("")) {
							wn.append(lldpWarningMsg.toString()).append(".<br/><br/>");
						}
						if (!bufferWarningMsg.toString().equals("")) {
							wn.append(getText("warning.hiveap.config.radio.loadbalance", new String[]{bufferWarningMsg.toString()}));
							wn.append("<br/><br/>");
						}

						if (!getActionMessages().isEmpty()) {
							for(Object obj: getActionMessages()){
								wn.append(obj.toString());
								wn.append("<br/><br/>");
							}
						}
						jsonObject.put("wn", wn.toString());
					}
					clearErrorsAndMessages();
					jsonObject.put("t", true);
					return "json";
				}
			} else if("checkWiFiClientMode".equals(operation)){
				jsonObject = new JSONObject();
				StringBuilder wfcmHosts = new StringBuilder();
				boolean wfcmMsgDisplay = false;
				clearErrorsAndMessages();
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_SCRIPT_WIZARD);
				for(Long idObj:selectedConfigIds){
					HiveAp oneHiveAP = findBoById(HiveAp.class, idObj, this);
					if((oneHiveAP.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_WP
							|| oneHiveAP.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ)
							&& oneHiveAP.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER
							&& oneHiveAP.getRadioConfigType() == HiveAp.RADIO_MODE_ACCESS_WAN){
						if(configSelectType.equals(HiveApUpdateSettings.ConfigSelectType.deltaRunning.toString()) ||
								configSelectType.equals(HiveApUpdateSettings.ConfigSelectType.auto.toString()) && oneHiveAP.getLastCfgTime() != 0){
							wfcmMsgDisplay = true;
							wfcmHosts.append(oneHiveAP.getHostName()).append(",");
						}
					}
				}

				if(wfcmMsgDisplay){
					int index = wfcmHosts.lastIndexOf(",");
					if(index > 0){
						wfcmHosts.deleteCharAt(index);
					}
					jsonObject.put("msg", MgrUtil.getUserMessage("info.wificlientmode.clioverride",wfcmHosts.toString()));
				}

				jsonObject.put("t", wfcmMsgDisplay);
				return "json";
			} else if ("uploadImage".equals(operation)) {
//				if(!checkUploadQueue()){
//					prepareImageBoList();
//					return UPDATE_LIST_TYPE_IMAGE;
//				}
				Set<Long> selectedImageIds = getCurrentSelectedBoIds(AH_DOWNLOAD_IMAGE);
				storeIdListToSession(UPDATE_IMAGE_SELECTED_IDs,
						selectedImageIds, getPageIds());
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					prepareImageBoList();
					return UPDATE_LIST_TYPE_IMAGE;
				}
			} else if("uploadSignature".equals(operation)){
				Set<Long> selectedSignatureIds = getCurrentSelectedBoIds(AH_DOWNLOAD_L7_SIGNATURE);
				storeIdListToSession(UPDATE_L7_SIGNATURE_SELECTED_IDs,
						selectedSignatureIds, getPageIds());
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					prepareL7SignatureBoList();
					return UPDATE_LIST_TYPE_L7_SIGNATURE;
				}
			} else if ("updateBootstrap".equals(operation)) {
				Set<Long> selectedBootstrapIds = getCurrentSelectedBoIds(AH_DOWNLOAD_BOOTSTRAP);
				storeIdListToSession(UPDATE_BOOTSTRAP_SELECTED_IDs,
						selectedBootstrapIds, getPageIds());
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					prepareBootstrapBoList();
					return UPDATE_LIST_TYPE_BOOTSTRAP;
				}
			} else if ("updateCountryCode".equals(operation)) {
				Set<Long> selectedDtlsIds = getCurrentSelectedBoIds(AH_DOWNLOAD_COUNTRY_CODE);
				storeIdListToSession(UPDATE_COUNTRYCODE_SELECTED_IDs,
						selectedDtlsIds, getPageIds());
				if (updateCountryCodeParams() && generateUpdateList()
						&& addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					prepareCountryCodeBoList();
					return UPDATE_LIST_TYPE_COUNTRYCODE;
				}
			} else if ("updateCountryCodeJson".equals(operation)) {
				Set<Long> selectedDtlsIds = getCurrentSelectedBoIds(AH_DOWNLOAD_COUNTRY_CODE);
				storeIdListToSession(UPDATE_COUNTRYCODE_SELECTED_IDs,
						selectedDtlsIds, getPageIds());
				jsonObject = new JSONObject();
				if (updateCountryCodeParams() && generateUpdateList()
						&& addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					jsonObject.put("t", true);
					return "json";
				} else {
					jsonObject.put("t", false);
					for(Object errorMsg : getActionErrors()){
						jsonObject.put("m", errorMsg);
						break;
					}
					return "json";
				}
			}else if ("updateOutdoorSettings".equals(operation)) {
				Set<Long> selectedDtlsIds = getCurrentSelectedBoIds(AH_DOWNLOAD_OUTDOORSTTINGS);
				storeIdListToSession(UPDATE_OUTDOORSETTINGS_SELECTED_IDS,
						selectedDtlsIds, getPageIds());
				if (generateUpdateList()&& addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					prepareOutdoorSettingsBoList();
					return UPDATE_OUTDOOR_SETTINGS;
				}
			} else if ("updatePoe".equals(operation)) {
				Set<Long> selectedPoeIds = getCurrentSelectedBoIds(AH_DOWNLOAD_POE);
				storeIdListToSession(UPDATE_POE_SELECTED_IDs, selectedPoeIds,
						getPageIds());
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					preparePoeBoList();
					return UPDATE_LIST_TYPE_POE;
				}
			}else if ("uploadOsDetection".equals(operation)) {
				jsonObject = new JSONObject();
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					jsonObject.put("suc",true);
				}else{
					jsonObject.put("suc",false);
				}
				return "json";
			}else if ("updateOsDetection".equals(operation)) {
				prepareOsDetection();
				return "json";
			}else if ("updateNetdump".equals(operation)) {
				Set<Long> selectedNetdumpIds = getCurrentSelectedBoIds(AH_DOWNLOAD_NET_DUMP);
				storeIdListToSession(UPDATE_NETDUMP_SELECTED_IDs, selectedNetdumpIds,
						getPageIds());
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
					logUpdateOperation();
					return UPDATE_RESULT_LIST;
				} else {
					preparePoeBoList();
					return UPDATE_LIST_TYPE_NETDUMP;
				}
			} else if ("importImage".equals(operation)) {
				addLstTitle(getText("hiveAp.update.image"));
				addLstForward("hiveApImageUpdate");
				Set<Long> selectedImageIds = getCurrentSelectedBoIds(AH_DOWNLOAD_IMAGE);
				storeIdListToSession(UPDATE_IMAGE_SELECTED_IDs,
						selectedImageIds, getPageIds());
				return operation;
			} else if ("continueImage".equals(operation)) {
				prepareImageBoList();
				removeLstTitle();
				removeLstForward();
				return UPDATE_LIST_TYPE_IMAGE;
			} else if ("viewScript".equals(operation)) {
				log.info("execute", "operation:" + operation + ", ap id:" + id);

				String strSql = "select _t1.hostName, _t2.enabledhcp, _t1.TEMPLATE_ID, _t1.id from HIVE_AP _t1 left join hiveap_device_interface _t2 on _t1.id= _t2.hiveap_id and _t2.deviceiftype=" + AhInterface.DEVICE_IF_TYPE_ETH0 + " where _t1.id=" + id;

//				String strSql = "select hostName, dhcp, TEMPLATE_ID, id from HIVE_AP where id =" + id;
				List<?> apList = QueryUtil.executeNativeQuery(strSql);
				StringBuffer warningMsg=new StringBuffer();
				HashMap<Long,ConfigTemplate> mapConfigTemplate = new HashMap<>();
				Set<String> setPolicyName = new HashSet<>();
				ConfigTemplate wlanPolicy = null;
				for(Object oneObj: apList){
					Object[] oneAp = (Object[])oneObj;
					if (mapConfigTemplate.get(Long.parseLong(oneAp[2].toString()))!=null) {
						wlanPolicy = mapConfigTemplate.get(Long.parseLong(oneAp[2].toString()));
					} else {
						wlanPolicy = findBoById(ConfigTemplate.class, Long.parseLong(oneAp[2].toString()), this);
						mapConfigTemplate.put(wlanPolicy.getId(), wlanPolicy);
					}

					HiveAp oneHiveAP = findBoById(HiveAp.class, Long.parseLong(oneAp[3].toString()), this);
					if (oneHiveAP.isVpnGateway()) {
						if (oneHiveAP.getEth0Interface()==null ||
								oneHiveAP.getEth0Interface().getIpAddress()==null ||
								oneHiveAP.getEth0Interface().getIpAddress().equals("")) {
							jsonObject = new JSONObject();
							jsonObject.put("err",
									getText("error.hiveap.cvg.wan.ip.empty",
											new String[]{oneHiveAP.getHostName()}));
							return "json";
						}
					}

//					if (oneAp[1]==null || oneAp[1].toString().equals("") ||
//							Boolean.valueOf(oneAp[1].toString())
							/*|| !wlanPolicy.isBlnWirelessRouter()*/
//					if(!wlanPolicy.getConfigType().isRouterContained()
//							|| !oneHiveAP.isBranchRouter()
//							|| (oneHiveAP.isBranchRouter() && !checkDhcpWhenUpload(oneHiveAP))){
									// connection type is pppoe and eth0 is primary
//									oneHiveAP.getEth0Interface().getConnectionType().equals("3") &&
//									oneHiveAP.getEth0Interface().getWanOrder() == 1 )){
//						String ret="";
						/*if (!wlanPolicy.isBlnBonjourOnly()) {*/
//						if (!wlanPolicy.getConfigType().isBonjourOnly()) {
					String	ret= checkNetworkPolicy(wlanPolicy, false, warningMsg,setPolicyName, oneHiveAP,null);
//						}
					if (!"".equals(ret)){
						jsonObject = new JSONObject();
						jsonObject.put("err", ret);
						return "json";
					}
//					} else {
//						String ret="";
//						if (!wlanPolicy.getConfigType().isBonjourOnly()) {
//							ret= checkNetworkPolicy(wlanPolicy, true, warningMsg, setPolicyName,oneHiveAP,null);
//						}
//						if (!"".equals(ret)){
//							jsonObject = new JSONObject();
//							jsonObject.put("err", ret);
//							return "json";
//						}
//					}

					//check stp settings
					if(oneHiveAP.getDeviceStpSettings() != null
							&& oneHiveAP.getDeviceStpSettings().isOverrideStp()
							&& oneHiveAP.getDeviceStpSettings().isEnableStp()){
						String ret1 = checkStpSettings(wlanPolicy,oneHiveAP);
						if (!"".equals(ret1)){
							jsonObject = new JSONObject();
							jsonObject.put("err", ret1);
							return "json";
						}
					}
				}
				jsonObject = getScriptDetails(id, wlanPolicy);
				return "json";
			} else if ("viewBootstrap".equals(operation)) {
				log.info("execute", "operation:" + operation + ", ap id:" + id);
				jsonObject = getBootstrapDetails(id);
				return "json";
			} else if ("viewPsk".equals(operation)) {
				log.info("execute", "operation:" + operation + ", ap id:" + id);
				jsonObject = getPskDetails(id);
				return "json";
			} else if ("enableTftp".equals(operation)) {
				jsonObject = getTftpResult();
				return "json";
			} else if ("saveSignatureOption".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = saveSignatureOptions();
				return "json";
			} else if ("saveImageOption".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = saveImageOptions();
				return "json";
			} else if ("saveConfigOption".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = saveConfigOptions();
				return "json";
			} else if ("fetchImageInfo".equals(operation)) {
				log.info("operation: " + operation + ", selectedVersion: "
						+ selectedVersion + ", selectedImage: " + selectedImage);
				jsonObject = fetchImageInfo();
				return "json";
			} else if ("fetchSignatureInfo".equals(operation)) {
				log.info("operation: " + operation + ", selectedVersion: "
						+ selectedVersion + ", selectedImage: " + selectedImage);
				jsonObject = fetchSignatureInfo();
				return "json";
			} else if ("fetchImageList".equals(operation)) {
				log.info("operation: " + operation);
				jsonObject = fetchImageList();
				return "json";
			} else if ("fetchSignatureList".equals(operation)) {
				log.info("operation: " + operation);
				jsonObject = fetchSignatureList();
				return "json";
			} else if ("distributedDownloadList".equals(operation)) {
				log.info("execute", "operation:" + operation);
				Set<Long> selectedImageIds = getCurrentSelectedBoIds(AH_DOWNLOAD_IMAGE);
				storeIdListToSession(UPDATE_IMAGE_SELECTED_IDs,
						selectedImageIds, getPageIds());
				jsonObject = getDistributedDownloadList();
				return "json";
			} else if("saveSelectedDistServer".equals(operation)) {
				log.info("execute", "operation:" + operation);
				return "json";
			}
//			else if("uploadMsg".equals(operation)){
//				log.info("execute", "operation:" + operation);
//				jsonObject = getUploadWaitingMessage();
//				return "json";
//			}
			else if ("updateOptionEx".equals(operation)){
				return "updateOptionEx";
			} else if ("updateOptionJson".equals(operation)){
				return "updateOptionJson";
			}else if("checkSelectedNWPolicy".equals(operation)){
				jsonObject = new JSONObject();
				if(selectedNWPolicy == null){
					jsonObject.put("t", false);
					jsonObject.put("errorMsg", "Update selected Network Policy error");
				}
				jsonObject.put(JSON_RESULT_TYPE_KEY, operation+JSON_RESULT_TYPE_SUFFIX);
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_SCRIPT_WIZARD);
				for(Long hiveApId : selectedConfigIds){
					HiveAp hiveApBo = QueryUtil.findBoById(HiveAp.class, hiveApId, this);
					if(!hiveApBo.getConfigTemplate().getId().equals(selectedNWPolicy)
							&& hiveApBo.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
						jsonObject.put("t", true);
						jsonObject.put("update", true);
						return "json";
					}
				}
				jsonObject.put("t", true);
				jsonObject.put("update", false);
				return "json";
			} else if("updateNetworkPolicy".equals(operation)){
				jsonObject = new JSONObject();
				jsonObject.put(JSON_RESULT_TYPE_KEY, operation+JSON_RESULT_TYPE_SUFFIX);
				if(selectedNWPolicy == null){
					jsonObject.put("t", false);
					jsonObject.put("errorMsg", "Update selected Network Policy error");
				}
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_SCRIPT_WIZARD);
				Set<Long> hiveApsNWPolicyChg = new HashSet<>();
				List<Long> ignoreList = new ArrayList<>();
				StringBuffer ignoreMsg = new StringBuffer("");
				ConfigTemplate nwPolicyBo = QueryUtil.findBoById(ConfigTemplate.class, selectedNWPolicy);
				for(Long hiveApId : selectedConfigIds){
					HiveAp hiveApBo = QueryUtil.findBoById(HiveAp.class, hiveApId, this);
					if(hiveApBo.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY && !hiveApBo.getConfigTemplate().getId().equals(selectedNWPolicy)){
						if(isMatchingForDeviceAndNetworkPolicy(hiveApBo, nwPolicyBo, ignoreMsg)){
							hiveApBo.setConfigTemplate(nwPolicyBo);

							//fix bug 22808 start
							// generate realm name when network policy changes
							Long topologyMapId = hiveApBo.getMapContainer() == null ?  null : hiveApBo.getMapContainer().getId();
							String realmName = HiveApAction.generateRealmName(nwPolicyBo.getId(),topologyMapId,hiveApBo.isLockRealmName(),hiveApBo.getRealmName());
							hiveApBo.setRealmName(realmName);
							// end

							QueryUtil.updateBo(hiveApBo);
							hiveApsNWPolicyChg.add(hiveApId);
							//update device network history data
							final Long vHMdomain = hiveApBo.getOwner().getId();
							String[] tags = null;
							List<String> tagsStr = new ArrayList<>();

							if(null != hiveApBo.getClassificationTag1() && !"".equals(hiveApBo.getClassificationTag1())){
								tagsStr.add(hiveApBo.getClassificationTag1());
							}
							if(null != hiveApBo.getClassificationTag2() && !"".equals(hiveApBo.getClassificationTag2())){
								tagsStr.add(hiveApBo.getClassificationTag2());
							}
							if(null != hiveApBo.getClassificationTag3() && !"".equals(hiveApBo.getClassificationTag3())){
								tagsStr.add(hiveApBo.getClassificationTag3());
							}
							if(null != tagsStr && tagsStr.size() > 0){
								tags = new String[tagsStr.size()];
								tagsStr.toArray(tags);
							}
							if(hiveApBo.getMapContainer() == null){
								String sql = "select id from map_node where parent_map_id = " +
										"(select id from map_node where parent_map_id is null) and owner="+vHMdomain;
								List<?> list = QueryUtil.executeNativeQuery(sql, 1);
								if(!list.isEmpty()){
									NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
											vHMdomain, hiveApBo.getMacAddress(), hiveApBo.getTimeZoneOffset(), new long[]{Long.parseLong(list.get(0).toString())}, tags);
								}
							}else{
								NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(),
										vHMdomain, hiveApBo.getMacAddress(), hiveApBo.getTimeZoneOffset(), new long[]{hiveApBo.getMapContainer().getId()}, tags);   //TODO  topologyGroupPkFromTop(exclusive)ToBottom(inclusive)
							}
						}else{
							ignoreList.add(hiveApId);
						}
					}
				}
				MgrUtil.setSessionAttribute(UPDATE_HIVEAP_NETWORKPOLICY_CHANGED,hiveApsNWPolicyChg);
				jsonObject.put("t", true);
				jsonObject.put("ignore", ignoreList);
				if(!"".equals(ignoreMsg.toString())){
					jsonObject.put("ignoreMsg", ignoreMsg.toString());
				}
				return "json";
			} else if("rebootHiveAPs".equals(operation)){
				String errorMsg = null;
				if (generateUpdateList() && addToUploadList()) {
					removeSessionAttrs();
				}else{
					errorMsg = getAllActionErrorMsg();
				}
				if(this.isJsonMode()){
					jsonObject = new JSONObject();
					if(errorMsg != null){
						jsonObject.put("r", false);
						jsonObject.put("m", errorMsg);
					}else{
						jsonObject.put("r", true);
					}
					return "json";
				}else{
					return UPDATE_RESULT_LIST;
				}
			} else if("getDeviceCounts".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put(JSON_RESULT_TYPE_KEY, operation + JSON_RESULT_TYPE_SUFFIX);

				//get all devices
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_REBOOT);
				jsonObject.put("all", selectedConfigIds == null ? 0 : selectedConfigIds.size());

				//get image upgrade devices
				Map<Long, String> lowerDeviceMap = getSimpleUpdateImageIds(selectedConfigIds, false);
				jsonObject.put("lowerImage", lowerDeviceMap == null ? 0 : lowerDeviceMap.size());
				
				//get image upgrade devices
				Map<Long, String> forceDeviceMap = getSimpleUpdateImageIds(selectedConfigIds, true);
				jsonObject.put("forceImage", forceDeviceMap == null ? 0 : forceDeviceMap.size());
				
				String imageNote = getSimpleImageUpdateNotes(selectedConfigIds);
				if(StringUtils.isEmpty(imageNote)){
					jsonObject.put("imageWarning", MgrUtil.getResourceString("geneva_06.update.ui.image.desc"));
				}else{
					jsonObject.put("imageWarning", imageNote);
				}
				
				//clear session
				removeSessionAttrs();

				jsonObject.put("t", true);
				return "json";
			} else if("getRebootDevices".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("t", true);
				jsonObject.put(JSON_RESULT_TYPE_KEY, operation+JSON_RESULT_TYPE_SUFFIX);
				
				//get all devices
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_REBOOT);
				
				Map<Long, HiveAp> rebootAps = getSimpleUpdateRebootDevices(selectedConfigIds);
				
				jsonObject.put("counts", rebootAps == null ? 0 : rebootAps.size());
				
				JSONArray jArray = new JSONArray();
				jsonObject.put("names", jsonArray);
				for(HiveAp ap: rebootAps.values()){
					jArray.put(ap.getHostName());
				}
				
				//clear session
				removeSessionAttrs();
				
				return "json";
			} else if("checkConnectStatus".equals(operation)){
				Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_SCRIPT_WIZARD);
				jsonObject = getConnectionStatusCheckRes(selectedConfigIds);
				
				//clear session
				removeSessionAttrs();
				return "json"; 
			} else if (baseOperation()) {
				String listType = getStringSessionAttribute(UPDATE_LIST_TYPE);
				if (null == listType) {
					removeSessionAttrs();
					return "managedHiveAps";
				} else {
					preparePage(listType);
					return listType;
				}
			} else {
				removeSessionAttrs();
				return "managedHiveAps";
			}
		} catch (Exception e) {
			setL3Features(null);
			log.error("prepareActionError", MgrUtil.getUserMessage(e), e);
			addActionPermanentErrorMsg(MgrUtil.getUserMessage(e));
			return "managedHiveAps";
		}
	}

	private boolean checkDhcpWhenUpload(HiveAp ap){
		for(Long key:ap.getDeviceInterfaces().keySet()){
			DeviceInterface di = ap.getDeviceInterfaces().get(key);
			if (di!=null && di.getWanOrder()!=0 && di.getConnectionType().equals("2"))  {
				return true;
			}
		}
		return false;
	}

	private Long selectedNWPolicy;
	private String selectedDeviceIdStr;

	public Long getSelectedNWPolicy(){
		return selectedNWPolicy;
	}

	public void setSelectedNWPolicy(Long selectedNWPolicy){
		this.selectedNWPolicy = selectedNWPolicy;
	}

	public String getSelectedDeviceIdStr() {
		return selectedDeviceIdStr;
	}

	public void setSelectedDeviceIdStr(String selectedDeviceIdStr) {
		this.selectedDeviceIdStr = selectedDeviceIdStr;
	}

	private JSONObject getTftpResult() throws JSONException {
		JSONObject result = new JSONObject();
		try {
			if (HmBeAdminUtil.setTftpEnable(true)) {
				result.put("suc", true);
			}
		} catch (RuntimeException e) {
			log.error("getTftpResult", e.getMessage());
		}
		return result;
	}

	private JSONObject saveSignatureOptions() {
		JSONObject result = new JSONObject();
		try {
			if (null != getDataSource()) {
				getDataSource().setSignatureSelectType(
						ImageSelectionType.valueOf(imageSelectType));
			}
			if (null == getDataSource().getId()) {
				getDataSource().setOwner(getDomain());
				QueryUtil.createBo(getDataSource());
			} else {
				QueryUtil.updateBo(getDataSource());
			}
			result.put("suc", true);
			result.put("select", getDataSource().getSignatureSelectType()
					.toString());
			result.put("conn", getDataSource().getSignatureConnectionString());
			result.put("timedout", getDataSource().getSignatureTimedoutString());
		} catch (Exception e) {
			log.error("saveSignatureOptions", e);
		}
		return result;
	}

	private JSONObject saveImageOptions() {
		JSONObject result = new JSONObject();
		try {
			if (null != getDataSource()) {
				if (ActivateType.activateAtTime.toString().equals(
						imageActivateType)) {
					long longTime = parseTime(imageDate, imageHour, imageMin);
					getDataSource().setImageActivateTime(longTime);
					getDataSource().setImageActivateOffset(5);
				}
				getDataSource().setImageActivateType(
						ActivateType.valueOf(imageActivateType));
				getDataSource().setImageSelectType(
						ImageSelectionType.valueOf(imageSelectType));
//				getDataSource().setDistributedUpgrades(distributedUpgrades);
				if (TransferType.tftp.toString().equals(imageTransfer)) {
					getDataSource().setImageConnType(
							HiveApUpdateSettings.CONNECT_TYPE_LOCAL);
					getDataSource().setImageTransfer(TransferType.tftp);
				} else {
					getDataSource().setImageTransfer(TransferType.scp);
				}
			}
			if (null == getDataSource().getId()) {
				getDataSource().setOwner(getDomain());
				QueryUtil.createBo(getDataSource());
			} else {
				QueryUtil.updateBo(getDataSource());
			}
			result.put("suc", true);
			result.put("select", getDataSource().getImageSelectType()
					.toString());
			result
					.put("time", getDataSource()
							.getImageActivateTimeHtmlString());
			result.put("protocol", getDataSource().getProtocolString());
			result.put("conn", getDataSource().getConnectionString());
			result.put("timedout", getDataSource().getTimedoutString());
			result.put("distributed", getDataSource().isDistributedUpgrades());
		} catch (Exception e) {
			log.error("saveImageOptions", e);
		}
		return result;
	}

	private JSONObject saveConfigOptions() {
		JSONObject result = new JSONObject();
		try {
			if (null != getDataSource()) {
				if (ActivateType.activateAtTime.toString().equals(
						configActivateType)) {
					long longTime = parseTime(configDate, configHour, configMin);
					getDataSource().setConfigActivateTime(longTime);
					getDataSource().setConfigActivateOffset(5);
				}
				getDataSource().setConfigActivateType(
						ActivateType.valueOf(configActivateType));
				getDataSource().setConfigSelectType(
						ConfigSelectType.valueOf(configSelectType));
			}
			if (null == getDataSource().getId()) {
				getDataSource().setOwner(getDomain());
				QueryUtil.createBo(getDataSource());
			} else {
				QueryUtil.updateBo(getDataSource());
			}
			boolean showLabel = getDataSource().getConfigSelectType() == ConfigSelectType.auto
					|| getDataSource().getConfigSelectType() == ConfigSelectType.full;
			result.put("suc", true);
			result.put("type", getDataSource().getUploadTypeHtmlString());
			result.put("items", getDataSource().getConfigItemHtmlString());
			result.put("time", getDataSource()
					.getConfigActivateTimeHtmlString());
			result.put("show", showLabel);
		} catch (Exception e) {
			log.error("saveConfigOptions", e);
		}
		return result;
	}
	private JSONObject fetchImageInfo() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (!StringUtils.isEmpty(selectedImage)) {
			selectedImage = selectedImage.trim();
			HiveApImageInfo image = ImageManager.findImageByFileName(selectedImage);
			JSONObject fileInfo = new JSONObject();
			if (null != image) {
				fileInfo.put("fn", image.getImageName());
				fileInfo.put("ver", image.getImageVersion());
				//fileInfo.put("dr", imageimage.getDateReleased());
				fileInfo.put("pf", image.getImagePlatformString());
				fileInfo.put("sz", image.getImageSizeString());
			}
			jsonObject.put("file", fileInfo);
		} else if (!StringUtils.isEmpty(selectedVersion)) {
			selectedVersion = selectedVersion.trim();
			List<HiveApImageInfo> list = ImageManager.findImagesByFullVersion(selectedVersion);
			JSONArray verInfo = new JSONArray();
			for (HiveApImageInfo image : list) {
				JSONObject fileInfo = new JSONObject();
				fileInfo.put("fn", image.getImageName());
				fileInfo.put("ver", image.getImageVersion());
				//fileInfo.put("dr", ls.getDateReleased());
				fileInfo.put("pf", image.getImagePlatformString());
				fileInfo.put("sz", image.getImageSizeString());
				verInfo.put(fileInfo);
			}
			jsonObject.put("ver", verInfo);
		}
		return jsonObject;
	}

	private JSONObject fetchSignatureInfo() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (!StringUtils.isEmpty(selectedImage)) {
			selectedImage = selectedImage.trim();
			LSevenSignatures ls = QueryUtil.findBoByAttribute(
					LSevenSignatures.class, "fileName", selectedImage);
			JSONObject fileInfo = new JSONObject();
			if (null != ls) {
				fileInfo.put("fn", ls.getFileName());
				fileInfo.put("ver", ls.getAhVersion());
				fileInfo.put("dr", ls.getDateReleased());
				fileInfo.put("pf", ls.getPlatformIdString());
				fileInfo.put("pt", ls.getPackageTypeString());
			}
			jsonObject.put("file", fileInfo);
		} else if (!StringUtils.isEmpty(selectedVersion)) {
			selectedVersion = selectedVersion.trim();
			List<LSevenSignatures> list = QueryUtil.executeQuery(
					LSevenSignatures.class, null, new FilterParams("ahVersion",
							selectedVersion));
			JSONArray verInfo = new JSONArray();
			for (LSevenSignatures ls : list) {
				JSONObject fileInfo = new JSONObject();
				fileInfo.put("fn", ls.getFileName());
				fileInfo.put("ver", ls.getAhVersion());
				fileInfo.put("dr", ls.getDateReleased());
				fileInfo.put("pf", ls.getPlatformIdString());
				fileInfo.put("pt", ls.getPackageTypeString());
				verInfo.put(fileInfo);
			}
			jsonObject.put("ver", verInfo);
		}
		return jsonObject;
	}
	
	private JSONObject fetchImageList() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		List<TextItem> imageFiles = getAvailableImageFiles();
		List<String> imageVers = getAvailableImageVersions();
		JSONArray files = new JSONArray();
		JSONArray vers = new JSONArray();
		for (TextItem file : imageFiles) {
			JSONObject obj = new JSONObject();
			obj.put("fn", file.getKey());
			obj.put("fnDisp", file.getValue());
			files.put(obj);
		}
		for (String ver : imageVers) {
			JSONObject obj = new JSONObject();
			obj.put("ver", ver);
			obj.put("verDisp", ver);
			vers.put(obj);
		}
		jsonObject.put("files", files);
		jsonObject.put("vers", vers);
		return jsonObject;
	}

	private JSONObject fetchSignatureList() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		List<String> signatureFiles = getAvailableSignatureFiles();
		List<TextItem> signatureVers = getAvailableSignatureVersions();
		JSONArray files = new JSONArray();
		JSONArray vers = new JSONArray();
		for (String file : signatureFiles) {
			JSONObject obj = new JSONObject();
			obj.put("fn", file);
			files.put(obj);
		}
		for (TextItem ver : signatureVers) {
			JSONObject obj = new JSONObject();
			obj.put("ver", ver.getKey());
			obj.put("verDisp", ver.getValue());
			vers.put(obj);
		}
		jsonObject.put("files", files);
		jsonObject.put("vers", vers);
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getDistributedDownloadList() {
		JSONObject result = new JSONObject();
		try {
			Set<Long> select_ids = getIdListFromSession(UPDATE_IMAGE_SELECTED_IDs);
			String sessHive = this.getCurrentHiveName();
			Map<Long, String> idHivenameMapping = (Map<Long, String>)MgrUtil.getSessionAttribute(UPDATE_ID_AND_HIVENAME_MAPPING);
			String currHive;
			Long distributedServer;
			if(select_ids != null && !select_ids.isEmpty()){
				Set<String> hiveSet = new HashSet<>();
				for(Long id : select_ids){
					hiveSet.add(idHivenameMapping.get(id));
				}
				currHive = (String)hiveSet.toArray()[0];
				if(hiveSet.isEmpty() || currHive == null || "".equals(currHive)){
					result.put("noneHive", true);
					distributedServer = null;
					this.setDistributedServer(distributedServer);
					return result;
				}
				if(hiveSet.size() > 1){
					result.put("multiHive", true);
					distributedServer = null;
					this.setDistributedServer(distributedServer);
					return result;
				}
				if(hiveSet.size() ==1 && currHive.equals(sessHive)){
					result.put("sameHive", true);
//					distributedServer = this.getDistributedServer();
//					this.setDistributedServer(distributedServer);
					return result;
				}
				List<CheckItem> disList = getDistributedServerList(currHive);
				if(disList != null && !disList.isEmpty()){
					setDistributedServer(disList.get(0).getId());
					result.put("suc", true);
					result.put("disServerId", disList.get(0).getId());
					result.put("disServerStr", disList.get(0).getValue());
					JSONArray jsonArray = new JSONArray();
					result.put("disList", jsonArray);
					for(CheckItem item : disList){
						JSONObject jItem = new JSONObject();
						jItem.put("key", item.getId());
						jItem.put("value", item.getValue());
						jsonArray.put(jItem);
					}
				}
			}else{
				setDistributedServer(null);
			}
		}catch(Exception ex){
			log.error("getDistributedDownloadList", ex);
		}

		return result;
	}

	private JSONObject getUploadConfigExJson(String operation){
		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(Object errorMsg : getActionErrors()){
			jsonArray.put(errorMsg);
		}
		try{
			result.put("t", true);
			result.put(JSON_RESULT_TYPE_KEY, operation+JSON_RESULT_TYPE_SUFFIX);
			result.put("actionErrors", jsonArray);
		}catch(Exception ex){
			log.error("getUploadConfigExJson", ex);
		}
		return result;
	}

	private long parseTime(String date, String hour, String minute) {
		long value = System.currentTimeMillis();
		value += 24 * 60 * 60 * 1000; // default value is one day later
		if (null != date && null != hour && null != minute) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date day = formatter.parse(date);
				Calendar cd = Calendar.getInstance();
				cd.setTime(day);
				cd.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
				cd.set(Calendar.MINUTE, Integer.parseInt(minute));
				value = cd.getTimeInMillis();
			} catch (ParseException e) {
				log.error("parseTime", e);
			}
		}
		return value;
	}

	private void removeSessionAttrs() {
		MgrUtil.removeSessionAttribute(UPDATE_IMAGE_SELECTED_IDs);
		MgrUtil.removeSessionAttribute(UPDATE_CONFIG_SELECTED_IDS);
		MgrUtil.removeSessionAttribute(UPDATE_BOOTSTRAP_SELECTED_IDs);
		MgrUtil.removeSessionAttribute(UPDATE_COUNTRYCODE_SELECTED_IDs);
		MgrUtil.removeSessionAttribute(UPDATE_POE_SELECTED_IDs);
		MgrUtil.removeSessionAttribute(UPDATE_NETDUMP_SELECTED_IDs);
		MgrUtil.removeSessionAttribute(UPDATE_OUTDOORSETTINGS_SELECTED_IDS);
		MgrUtil.removeSessionAttribute(UPDATE_L7_SIGNATURE_SELECTED_IDs);

		MgrUtil.removeSessionAttribute(UPDATE_DISTRIBUTED_SERVER_LIST_GROUP_BY_HIVE);
		MgrUtil.removeSessionAttribute(UPDATE_ID_AND_HOSTNAME_MAPPING);
		MgrUtil.removeSessionAttribute(UPDATE_ID_AND_HIVENAME_MAPPING);
		MgrUtil.removeSessionAttribute(UPDATE_CURRENT_DISTRIBUTED_SERVER);
		MgrUtil.removeSessionAttribute(SIMPLIFIED_UPDATE_SELECTED_IDs);
	}

	private void removeUploadWaringSession(){
		MgrUtil.removeSessionAttribute(GUIDED_CONFIG_WARNING_MSG);
	}

	private void removeNWPolicyChangeSession(){
		MgrUtil.removeSessionAttribute(UPDATE_HIVEAP_NETWORKPOLICY_CHANGED);
	}

	private void removeUploadOsDetectionSession(){
		MgrUtil.removeSessionAttribute(UPDATE_OS_DETECTION_IDs);
	}

	private void logUpdateOperation() {
		try {
			if (null != updateHiveAps && updateHiveAps.size() > 0) {
				String label = "";
				if ("uploadWizard".equals(operation)) {
					label = "Configuration";
				} else if ("uploadImage".equals(operation)) {
					label = "Image";
				} else if ("updateBootstrap".equals(operation)) {
					label = "Bootstrap";
				} else if ("updateCountryCode".equals(operation)) {
					label = "Country Code";
				} else if ("updatePoe".equals(operation)) {
					label = "PoE Max Power";
				} else if ("updateOutdoorSettings".equals(operation)) {
					label = "Outdoor Settings";
				} else if("uploadOsDetection".equals(operation)){
					label = "Os Detection";
				} else if("uploadSignature".equals(operation)){
					label = "Application Signature";
				}
				if (!"".equals(label)) {
					if (updateHiveAps.size() > 50) {
						// just combine into one audit log
						generateAuditLog(HmAuditLog.STATUS_EXECUTE,
								updateHiveAps.size() + " "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s "+MgrUtil.getUserMessage("hm.audit.log.hiveap.update")
										+" "+ label + ".");
					} else {
						for (UpdateHiveAp hiveAp : updateHiveAps) {
							generateAuditLog(HmAuditLog.STATUS_EXECUTE,
									NmsUtil.getOEMCustomer().getAccessPonitName()+" "
											+ hiveAp.getHiveAp().getHostName()+" "
											+ MgrUtil.getUserMessage("hm.audit.log.hiveap.update")+" " + label + ".");
						}
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	protected JSONObject getPskDetails(Long id) throws Exception {
		HiveAp hiveAp = findBoById(HiveAp.class, id, this);
		JSONObject scriptDetails = new JSONObject();
		String message;

		if (null != hiveAp) {
			scriptDetails.put("t", MgrUtil
					.getUserMessage("hiveAp.update.configuration.menu.item2")
					+ " - " + hiveAp.getHostName());
			BeConfigModule.ConfigType type;
			HiveApUpdateSettings setting = getUpdateSetting();

			if (ConfigSelectType.full.equals(setting.getConfigSelectType())) {
				type = BeConfigModule.ConfigType.USER_FULL;
			} else if (ConfigSelectType.deltaConfig.equals(setting
					.getConfigSelectType())) {
				type = BeConfigModule.ConfigType.USER_DELTA;
			} else if (ConfigSelectType.deltaRunning.equals(setting
					.getConfigSelectType())) {
				type = BeConfigModule.ConfigType.USER_AUDIT;
			} else {
				if (hiveAp.getLastCfgTime() == 0) {
					type = BeConfigModule.ConfigType.USER_FULL;
				} else {
					type = BeConfigModule.ConfigType.USER_AUDIT;
				}
			}
			message = ConfigAuditProcessor.view(type, hiveAp);
		} else {
			message = getText("error.hiveap.update.notExisted");
		}
		scriptDetails.put("v", message);
		return scriptDetails;
	}

	protected JSONObject getScriptDetails(Long id, ConfigTemplate ct) throws Exception {
		HiveAp hiveAp = findBoById(HiveAp.class, id, this);
		JSONObject scriptDetails = new JSONObject();
		String message;

		if (null != hiveAp) {
			hiveAp.setConfigTemplate(ct);
			scriptDetails.put("t", MgrUtil
					.getUserMessage("hiveAp.update.configuration.menu.item1")
					+ " - " + hiveAp.getHostName());
			BeConfigModule.ConfigType type;
			HiveApUpdateSettings setting = getUpdateSetting();

			if (ConfigSelectType.full.equals(setting.getConfigSelectType())) {
				type = BeConfigModule.ConfigType.AP_FULL;
			} else if (ConfigSelectType.deltaConfig.equals(setting
					.getConfigSelectType())) {
				type = BeConfigModule.ConfigType.AP_DELTA;
			} else if (ConfigSelectType.deltaRunning.equals(setting
					.getConfigSelectType())) {
				type = BeConfigModule.ConfigType.AP_AUDIT;
			} else {
				if (hiveAp.getLastCfgTime() == 0) {
					type = BeConfigModule.ConfigType.AP_FULL;
				} else {
					type = BeConfigModule.ConfigType.AP_AUDIT;
				}
			}
			message = ConfigAuditProcessor.view(type, hiveAp);
		} else {
			message = getText("error.hiveap.update.notExisted");
		}
		scriptDetails.put("v", message);
		scriptDetails.put("h", MgrUtil.getUserMessage(""));
		return scriptDetails;
	}

	protected JSONObject getBootstrapDetails(Long id) throws Exception {
		HiveAp hiveAp = findBoById(HiveAp.class, id, this);
		JSONObject scriptDetails = new JSONObject();
		String message;
		try {
			AhBootstrapGeneratedEvent b_event = new AhBootstrapGeneratedEvent(
					hiveAp);
			b_event.setCapwapServer("["
					+ MgrUtil.getUserMessage("admin.capwap.primaryCapwapIP")
					+ "]");
			b_event.setCapwapServerBackup("["
					+ MgrUtil.getUserMessage("admin.capwap.backupCapwapIP")
					+ "]");
			b_event.setVhmName("["
					+ MgrUtil.getUserMessage("admin.vhmMgr.vhmName") + "]");
			b_event
					.setAdminUser("["
							+ MgrUtil
									.getUserMessage("hiveAp.update.bootstrap.adminName")
							+ "]");
			b_event.setAdminPwd("["
					+ MgrUtil
							.getUserMessage("hiveAp.update.bootstrap.password")
					+ "]");

			b_event.setCwpUdpPort(12222);
			b_event.setEchoTimeOut(30);
			b_event.setDeadInterval(105);
			b_event.setEnableDtls(true);
			b_event.setDtlsPassWord("******");
			message = AhAppContainer.getBeConfigModule()
					.generateBootstrapConfig(b_event);
		} catch (Exception e) {
			log.error("getBootstrapDetails",
					"get bootstrap details error for HiveAP:" + id, e);
			message = e.getMessage() == null ? "Unknow Error while generate script file."
					: e.getMessage();
		}
		scriptDetails.put("v", message);
		scriptDetails.put("t", hiveAp.getHostName());
		return scriptDetails;
	}

	private void addIntoUpdateList(Map<String, UpdateHiveAp> updateList,
			Map<HiveAp, UpdateObject> updateObjects, short updateType,
			HiveApUpdateSettings setting) {
		if (null != updateObjects) {
			for (HiveAp hiveAp : updateObjects.keySet()) {
				UpdateHiveAp upHiveAp = updateList.get(hiveAp.getMacAddress());
				if (null == upHiveAp) {
					boolean withReboot = isHiveApWithReboot(updateType,
							setting, hiveAp);

					upHiveAp = new UpdateHiveAp();
					upHiveAp.setHiveAp(hiveAp);
					upHiveAp.setUpdateType(updateType);
					upHiveAp.setWithReboot(withReboot);
					upHiveAp.setAutoProvision(false);
					updateList.put(hiveAp.getMacAddress(), upHiveAp);
				}
				upHiveAp.addUpdateObject(updateObjects.get(hiveAp));
			}
		}
	}

	private void addIntoUpdateListCfg(Map<String, UpdateHiveAp> updateList,
			Map<HiveAp, List<UpdateObject>> updateObjects, short updateType,
			HiveApUpdateSettings setting) {
		if (null != updateObjects) {
			for (HiveAp hiveAp : updateObjects.keySet()) {
				UpdateHiveAp upHiveAp = updateList.get(hiveAp.getMacAddress());
				if (null == upHiveAp) {
					boolean withReboot = isHiveApWithReboot(updateType,
							setting, hiveAp);

					upHiveAp = new UpdateHiveAp();
					upHiveAp.setHiveAp(hiveAp);
					upHiveAp.setUpdateType(updateType);
					upHiveAp.setWithReboot(withReboot);
					upHiveAp.setAutoProvision(false);
					updateList.put(hiveAp.getMacAddress(), upHiveAp);
				}
				for(UpdateObject obj : updateObjects.get(hiveAp)){
					upHiveAp.addUpdateObject(obj);
				}
			}
		}
	}

	private boolean isHiveApWithReboot(short updateType,
			HiveApUpdateSettings setting, HiveAp hiveAp) {
		boolean withReboot = false;
		if (null != setting) {
			if (updateType == AH_DOWNLOAD_IMAGE) {
				//for fix bug 16604, BR100 upload image auto reboot
				withReboot = hiveAp.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100 && !ActivateType.activateNextTime.equals(setting.getImageActivateType());
			} else {
				if (ConfigSelectType.full.equals(setting.getConfigSelectType())) {
					if (!ActivateType.activateNextTime.equals(setting
							.getConfigActivateType())) {
						withReboot = true;
					}
				} else if (ConfigSelectType.auto.equals(setting
						.getConfigSelectType())) {
					if (hiveAp.getLastCfgTime() == 0
							&& !ActivateType.activateNextTime.equals(setting
									.getConfigActivateType())) {
						withReboot = true;
					}
				}
			}
		}
		return withReboot;
	}

	private List<UpdateHiveAp> updateHiveAps;

	private boolean generateUpdateList() throws Exception {
		Map<String, UpdateHiveAp> updateList = new HashMap<>();

		if ("uploadWizard".equals(operation)) {
			Set<Long> selectedConfigIds = getIdListFromSession(UPDATE_CONFIG_SELECTED_IDS);

			Set<Long> selectedCwpIds = null, selectedCertIds = null, selectedVpnIds = null,
					selectedCAIds=null, selectedScriptIds = null, selectedPskIds = null, selectedImageIds = null;
			Map<Long, String> imageUpgradeMap = null;
			boolean saveCwpFiles = false, saveRadiusFiles = false, saveVpnFiles = false, saveCAFile = false, saveConfigFiles = false, saveUdFiles = false;
			//
			// get setting
			HiveApUpdateSettings setting = getDataSource();
			if (null == setting) {
				setting = getUpdateSetting();
			}

			//auto select IDM proxy
			try{
				AutoSelectDeviceServer.getInstance().autoSelectIDManagerProxy(selectedConfigIds, false);
			}catch(Exception ex){
				addActionPermanentErrorMsg(ex.getMessage());
				return false;
			}

			if(simpleUpdate){
				setting = new HiveApUpdateSettings();
				if(simplifiedRebootType == REBOOT_TYPE_AUTO){
					setting.setConfigActivateType(ActivateType.activateAfterTime);
				}else{
					setting.setConfigActivateType(ActivateType.activateNextTime);
				}
				if(completeCfgUpdate){
					setting.setConfigSelectType(ConfigSelectType.full);
				}

				configConfiguration = setting.isConfigConfiguration();
				configCwp = setting.isConfigCwp();
				configCertificate = setting.isConfigCertificate();
				configUserDatabase = setting.isConfigUserDatabase();
			} else if("uploadConfigEx".equalsIgnoreCase(this.getExConfigGuideFeature())){
				if(!saveUploadSetting){
					setting.setConfigConfiguration(this.getDataSource().isConfigConfiguration());
					setting.setConfigCwp(this.getDataSource().isConfigCwp());
					setting.setConfigCertificate(this.getDataSource().isConfigCertificate());
					setting.setConfigUserDatabase(this.getDataSource().isConfigUserDatabase());

					long longTime = parseTime(configDate, configHour, configMin);
					setting.setConfigActivateTime(longTime);
					setting.setConfigActivateOffset(5);

					if(configActivateType != null){
						setting.setConfigActivateType(
								ActivateType.valueOf(configActivateType));
					}
					if(configSelectType != null){
						setting.setConfigSelectType(
								ConfigSelectType.valueOf(configSelectType));
					}
				}else{
					configConfiguration = setting.isConfigConfiguration();
					configCwp = setting.isConfigCwp();
					configCertificate = setting.isConfigCertificate();
					configUserDatabase = setting.isConfigUserDatabase();
				}
			}else{
				setting.setConfigConfiguration(configConfiguration);
				setting.setConfigCwp(configCwp);
				setting.setConfigCertificate(configCertificate);
				setting.setConfigUserDatabase(configUserDatabase);
				if (null == setting.getId()) {
					setting.setOwner(getDomain());
					QueryUtil.createBo(setting);
				} else {
					QueryUtil.updateBo(setting);
				}
			}

			if (configCwp) { // CWP
				saveCwpFiles = true;
				selectedCwpIds = new HashSet<>();
				FilterParams filter = getListFilterParams(AH_DOWNLOAD_CWP,
						selectedConfigIds);
				if (null != filter) {
					List<?> selectedIds = QueryUtil.executeQuery(
							"select id from " + boClass.getSimpleName()+ " ap", null,
							filter, domainId);
					if (null != selectedIds) {
						for (Object id : selectedIds) {
							selectedCwpIds.add((Long) id);
						}
					}
				}
			}
			if (configCertificate) {// RADIUS, VPN, Cloud Auth
				saveRadiusFiles = saveVpnFiles = saveCAFile = true;
				selectedCertIds = new HashSet<>();
				FilterParams filter = getListFilterParams(
						AH_DOWNLOAD_RADIUS_CERTIFICATE, selectedConfigIds);
				if (null != filter) {
					List<?> selectedIds = QueryUtil.executeQuery(
							"select id from " + boClass.getSimpleName() +" as ap", null,
							filter, domainId);
					if (null != selectedIds) {
						for (Object id : selectedIds) {
							selectedCertIds.add((Long) id);
						}
					}
				}
				selectedVpnIds = new HashSet<>();
				filter = getListFilterParams(AH_DOWNLOAD_VPN_CERTIFICATE,
						selectedConfigIds);
				if (null != filter) {
					List<?> selectedIds = QueryUtil.executeQuery(
							"select id from " + boClass.getSimpleName()+" as ap", null,
							filter, domainId);
					if (null != selectedIds) {
						for (Object id : selectedIds) {
							selectedVpnIds.add((Long) id);
						}
					}
				}

				//upload cloud auth certificate
				if(NmsUtil.isVhmEnableIdm(domainId)){
					selectedCAIds = new HashSet<>();

					filter = getListFilterParams(AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE,
							selectedConfigIds);
					if (null != filter) {
						List<?> selectedIds = QueryUtil.executeQuery(
								"select id from " + boClass.getSimpleName()+" as ap", null,
								filter, domainId);
						if (null != selectedIds) {
							for (Object id : selectedIds) {
								selectedCAIds.add((Long) id);
							}
						}
					}

					//radius server auto use for IDM auth proxy.
					if(selectedCertIds != null && !selectedCertIds.isEmpty()){
						List<Short> idmSupportModel = DevicePropertyManage.getInstance().getSupportDeviceList(DeviceInfo.SPT_IDM_PROXY);
						filter = new FilterParams("id in (:s1) and hiveApModel in (:s2)",
								new Object[]{selectedCertIds, idmSupportModel});
						List<?> selectedIds = QueryUtil.executeQuery(
								"select id from " + boClass.getSimpleName()+" as ap", null,
								filter, domainId);
						if (null != selectedIds) {
							for (Object id : selectedIds) {
								selectedCAIds.add((Long) id);
							}
						}
					}

				}
			}
			if (configUserDatabase) {// User Database
				saveUdFiles = true;
//				saveCwpFiles = saveRadiusFiles = saveVpnFiles = false;
				selectedPskIds = new HashSet<>();
				FilterParams filter = getListFilterParams(AH_DOWNLOAD_PSK,
						selectedConfigIds);
				if (null != filter) {
					List<?> selectedIds = QueryUtil.executeQuery(
							"select id from " + boClass.getSimpleName(), null,
							filter, domainId);
					if (null != selectedIds) {
						for (Object id : selectedIds) {
							selectedPskIds.add((Long) id);
						}
					}

					//fix bug 26578: 1.	Before uploading user data to APs, need to clear the status of the user accounts. The methods can be found in User Manager.
					TemporaryAccountAction.clearRotatedAccounts(this.getDomain());
				}
			}
			if (configConfiguration) {// Configuration
				saveConfigFiles = true;
//				saveUdFiles = saveCwpFiles = saveRadiusFiles = saveVpnFiles = saveCAFile = false;
				selectedScriptIds = selectedConfigIds;
			}

			if(imageUpgrade || forceImageUpgrade){
				imageUpgradeMap = getSimpleUpdateImageIds(selectedConfigIds, forceImageUpgrade);
				if(imageUpgradeMap != null){
					selectedImageIds = imageUpgradeMap.keySet();
				}
			}

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedCwpIds == null ? new HashSet<Long>()
					: selectedCwpIds);// index=0
			idList.add(selectedCertIds == null ? new HashSet<Long>()
					: selectedCertIds);// index=1
			idList.add(selectedVpnIds == null ? new HashSet<Long>()
					: selectedVpnIds);// index=2
			idList.add(selectedScriptIds == null ? new HashSet<Long>()
					: selectedScriptIds);// index=3
			idList.add(selectedPskIds == null ? new HashSet<Long>()
					: selectedPskIds);// index=4
			idList.add(selectedCAIds == null ? new HashSet<Long>()
					: selectedCAIds);// index=5
			idList.add(selectedImageIds == null ? new HashSet<Long>()
					: selectedImageIds);// index=6

			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedCwpHiveAps = hiveAps.get(0);
			Set<HiveAp> selectedCertHiveAps = hiveAps.get(1);
			Set<HiveAp> selectedVpnHiveAps = hiveAps.get(2);
			Set<HiveAp> selectedScriptHiveAps = hiveAps.get(3);
			Set<HiveAp> selectedPskHiveAps = hiveAps.get(4);
			Set<HiveAp> selectedCAHiveAps = hiveAps.get(5);
			Set<HiveAp> selectedImageHiveAps = hiveAps.get(6);

			try {
				Map<HiveAp, UpdateObject> cwpObjects = getCwpUpdateObjects(
						selectedCwpHiveAps, saveCwpFiles, false);
				addIntoUpdateList(updateList, cwpObjects,
						AH_DOWNLOAD_SCRIPT_WIZARD, setting);
				Map<HiveAp, UpdateObject> certObjects = getCertUpdateObjects(
						selectedCertHiveAps, saveRadiusFiles, false);
				addIntoUpdateList(updateList, certObjects,
						AH_DOWNLOAD_SCRIPT_WIZARD, setting);
				Map<HiveAp, UpdateObject> vpnObjects = getVpnUpdateObjects(
						selectedVpnHiveAps, saveVpnFiles, false);
				addIntoUpdateList(updateList, vpnObjects,
						AH_DOWNLOAD_SCRIPT_WIZARD, setting);

				Map<HiveAp, UpdateObject> caRequestObjects = getCAUpdateObjects(
						selectedCAHiveAps, UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE, false);
				addIntoUpdateList(updateList, caRequestObjects,
						AH_DOWNLOAD_SCRIPT_WIZARD, setting);

				//config update
				Set<HiveAp> selectCfgHiveAps = new HashSet<>();
				selectCfgHiveAps.addAll(selectedScriptHiveAps);
				selectCfgHiveAps.addAll(selectedPskHiveAps);
				Map<HiveAp, List<UpdateObject>> cfgMap = getConfigUpdateObjects(selectCfgHiveAps,
						saveConfigFiles, false, setting, configConfiguration, configUserDatabase, selectedImageHiveAps);
				addIntoUpdateListCfg(updateList, cfgMap,
						AH_DOWNLOAD_SCRIPT_WIZARD, setting);

				//image upgrade
				Map<HiveAp, UpdateObject> imageObjects = getImageUpdateObjects(
						selectedImageHiveAps, false, setting, imageUpgradeMap);
				addIntoUpdateList(updateList, imageObjects, AH_DOWNLOAD_SCRIPT_WIZARD,
						setting);

				//device reboot
				Set<HiveAp> deviceNeedReboot = new HashSet<>();
				Set<HiveAp> cfgNeedReboot = getCompleteUpdateDevices(selectCfgHiveAps, setting);
				if(cfgNeedReboot != null && !cfgNeedReboot.isEmpty()){
					deviceNeedReboot.addAll(cfgNeedReboot);
				}
				if(selectedImageHiveAps != null && !selectedImageHiveAps.isEmpty()){
					deviceNeedReboot.addAll(selectedImageHiveAps);
				}
				Map<HiveAp, UpdateObject> rebootObjects = getRebootUpdateObjects(deviceNeedReboot, setting);
				addIntoUpdateList(updateList, rebootObjects, AH_DOWNLOAD_REBOOT,setting);
				
				//simplify update cache every device for L7 signature update.
				if(simpleUpdate){
					for(Set<HiveAp> deviceSet : hiveAps){
						if(deviceSet != null){
							for(HiveAp device : deviceSet){
								HmBeConfigUtil.getUpdateManager().simplifyUpdateTag(device.getMacAddress());
							}
						}
					}
				}
				
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("uploadImage".equals(operation)) {
			Set<Long> selectedImageIds = getIdListFromSession(UPDATE_IMAGE_SELECTED_IDs);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedImageIds == null ? new HashSet<Long>()
					: selectedImageIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);

			//If upload image with image name check all device with same image type.
			boolean isSelectVer = ImageSelectionType.softVer.equals(getUpdateSetting().getImageSelectType());
			if(!isSelectVer){
				boolean isSameImage = this.validateImageType(selectedHiveAps);
				if(!isSameImage){
					return false;
				}
			}

			// get settings
			HiveApUpdateSettings setting = getUpdateSetting();
			try {
				Map<HiveAp, UpdateObject> imageObjects = getImageUpdateObjects(
						selectedHiveAps, true, setting, null);
				addIntoUpdateList(updateList, imageObjects, AH_DOWNLOAD_IMAGE,
						setting);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("uploadSignature".equals(operation)) {
			Set<Long> selectedSignatureIds = getIdListFromSession(UPDATE_L7_SIGNATURE_SELECTED_IDs);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedSignatureIds == null ? new HashSet<Long>()
					: selectedSignatureIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);
			// get settings
			HiveApUpdateSettings setting = getUpdateSetting();
			L7SignatureMng mgmt = new L7SignatureMng();
			Map<HiveAp, LSevenSignatures> map;
			boolean isSelectFileName = ImageSelectionType.imgName
					.equals(setting.getSignatureSelectType());
			int limit = setting.getSignatureConnectionLimit();
			int maxTimeout = setting.getSignatureTimedout() == 0 ? UpdateParameters.L7_SIGNATURE_TIMEOUT_MAX
					: (int) setting.getSignatureTimedout() * 60 * 1000;
			if(isSelectFileName){
				// validate file
				String fileName = getSelectedImage();
				log.info("selected signature file: " + fileName);
				LSevenSignatures signature = mgmt
						.findSignatureByFileName(fileName);
				if (null == signature) {
					log.error(String
							.format("Cannot file LSevenSignatures record for selected signature file name: %s",
									fileName));
					return false;
				}
				map = validateSignatureFile(selectedHiveAps, signature, mgmt);
				if (null == map) {
					return false;
				}
			} else {
				// validate version
				String version;
				if (VER_SELECT_LATEST.equals(signatureVersionSelectType)) {
					version = this.latestVersion;
				} else {
					version = this.selectedVersion;
				}
				log.info("selected signature version: " + version);
				List<LSevenSignatures> signatures = mgmt
						.findSignaturesByVersion(version);
				if (null == signatures || signatures.isEmpty()) {
					log.error(String
							.format("Cannot file LSevenSignatures record for selected signature version: %s",
									version));
					return false;
				}
				map = validateSignatureVersion(selectedHiveAps, signatures,
						mgmt, version);
				if (null == map) {
					return false;
				}
			}

			try {
				Map<HiveAp, UpdateObject> signatureObjects = getSignatureUpdateObjects(
						map, limit, maxTimeout);
				addIntoUpdateList(updateList, signatureObjects,
						AH_DOWNLOAD_L7_SIGNATURE, null);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("updateBootstrap".equals(operation)) {
			Set<Long> selectedBootstrapIds = getIdListFromSession(UPDATE_BOOTSTRAP_SELECTED_IDs);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedBootstrapIds == null ? new HashSet<Long>()
					: selectedBootstrapIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);
			try {
				Map<HiveAp, UpdateObject> bootstrapObjects = getBootstrapUpdateObjects(selectedHiveAps);
				addIntoUpdateList(updateList, bootstrapObjects,
						AH_DOWNLOAD_BOOTSTRAP, null);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("updateCountryCode".equals(operation) || "updateCountryCodeJson".equals(operation)) {
			Set<Long> selectedCodeIds = getIdListFromSession(UPDATE_COUNTRYCODE_SELECTED_IDs);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedCodeIds == null ? new HashSet<Long>()
					: selectedCodeIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);
			try {
				Map<HiveAp, UpdateObject> countryCodeObjects = getCountryCodeUpdateObjects(selectedHiveAps);
				addIntoUpdateList(updateList, countryCodeObjects,
						AH_DOWNLOAD_COUNTRY_CODE, null);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("updateOutdoorSettings".equals(operation)) {
			Set<Long> selectedCodeIds = getIdListFromSession(UPDATE_OUTDOORSETTINGS_SELECTED_IDS);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedCodeIds == null ? new HashSet<Long>()
					: selectedCodeIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);
			try {
				Map<HiveAp, UpdateObject> outdoorSettingsObjects = getOutdoorSettingsUpdateObjects(selectedHiveAps);
				addIntoUpdateList(updateList, outdoorSettingsObjects,
						AH_DOWNLOAD_OUTDOORSTTINGS, null);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("updatePoe".equals(operation)) {
			Set<Long> selectedPoeIds = getIdListFromSession(UPDATE_POE_SELECTED_IDs);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedPoeIds == null ? new HashSet<Long>()
					: selectedPoeIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);
			try {
				Map<HiveAp, UpdateObject> poeObjects = getPoeUpdateObjects(selectedHiveAps);
				addIntoUpdateList(updateList, poeObjects, AH_DOWNLOAD_POE, null);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		}else if ("uploadOsDetection".equals(operation)) {
			filterParams = getListFilterParams(AH_DOWNLOAD_OS_DETECTION,null);
			List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, sortParams, filterParams);

			HiveApUpdateSettings setting = getUpdateSetting();
			Set<HiveAp> selectedHiveAps = new HashSet<>(hiveAps);
			try {
				Map<HiveAp, UpdateObject> osDetectionObjects = getOsDetectionUpdateObjects(
						selectedHiveAps, false, setting);
				addIntoUpdateList(updateList, osDetectionObjects, AH_DOWNLOAD_OS_DETECTION, null);
				if(osDetectionObjects != null){
					Object objects = new Object[] {System.currentTimeMillis(),osDetectionObjects.size()};
					MgrUtil.setSessionAttribute(UPDATE_OS_DETECTION_IDs,objects);
				}
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		} else if ("updateNetdump".equals(operation)) {
			Set<Long> selectedNetdumpIds = getIdListFromSession(UPDATE_NETDUMP_SELECTED_IDs);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedNetdumpIds == null ? new HashSet<Long>()
					: selectedNetdumpIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);
			try {
				Map<HiveAp, UpdateObject> netdumpObjects = getNetdumpUpdateObjects(selectedHiveAps, this.isEnableNetdump(), this.getNetdumpServer(),this.getNetdumpVlan(),
						this.getNetdumpNVlan(),this.getNetdumpDevice(),this.getNetdumpGateway(),this.getIpMode(),this.getManagerPortStr());
				addIntoUpdateList(updateList, netdumpObjects, AH_DOWNLOAD_NET_DUMP, null);
			} catch (UpdateObjectException e) {
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		}else if ("rebootHiveAPs".equals(operation)){
			Set<Long> selectedConfigIds = getCurrentSelectedBoIds(AH_DOWNLOAD_REBOOT);

			List<Set<Long>> idList = new ArrayList<>();
			idList.add(selectedConfigIds == null ? new HashSet<Long>()
					: selectedConfigIds);// index=0
			List<Set<HiveAp>> hiveAps = convertToHiveAp(idList);
			Set<HiveAp> selectedHiveAps = hiveAps.get(0);

			HiveApUpdateSettings settings = new HiveApUpdateSettings();
			settings.setConfigActivateType(ActivateType.activateAfterTime);
			try {
				Map<HiveAp, UpdateObject> rebootObjects = getRebootUpdateObjects(selectedHiveAps, settings);
				addIntoUpdateList(updateList, rebootObjects, AH_DOWNLOAD_REBOOT, null);
			} catch(UpdateObjectException e){
				addActionPermanentErrorMsg(e.getMessage());
				return false;
			}
		}
		updateHiveAps = new ArrayList<>();
		for (String sn : updateList.keySet()) {
			UpdateHiveAp upHiveAp = updateList.get(sn);
			if (upHiveAp.getRemainUpdateObjectCount() > 0) {
				updateHiveAps.add(upHiveAp);
			}
		}
		if (updateHiveAps.size() == 0) {
			addActionPermanentErrorMsg(MgrUtil
					.getUserMessage("error.hiveap.config.item.notfound"));
			return false;
		}
		return true;
	}

	/* pass in id list, return HiveAP list */
	private List<Set<HiveAp>> convertToHiveAp(List<Set<Long>> idList) {
		List<Set<HiveAp>> list = new ArrayList<>(idList.size());
		Set<Long> total = new HashSet<>();
		for (Set<Long> ids : idList) {
			total.addAll(ids);
		}
		Map<Long, HiveAp> totalHiveAps = queryHiveApsLazyInfo(total);
		for (Set<Long> ids : idList) {
			Set<HiveAp> hiveAps = new HashSet<>(ids.size());
			for (Long id : ids) {
				HiveAp hiveAp = totalHiveAps.get(id);
				if (null != hiveAp) {
					hiveAps.add(hiveAp);
				}
			}
			list.add(hiveAps);
		}
		return list;
	}

	private Map<Long, HiveAp> queryHiveApsLazyInfo(Set<Long> ids) {
		Map<Long, HiveAp> map = new HashMap<>();
		if (null != ids && !ids.isEmpty()) {
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
					new FilterParams("id", ids), domainId, this);
			for (HiveAp hiveAp : list) {
				map.put(hiveAp.getId(), hiveAp);
			}
		}
		return map;
	}

    private void prepareOsDetection() throws JSONException {
        String where;
        Object[] objects;
        Object[] obj;
        List<String> errList = new ArrayList<>();
        jsonObject = new JSONObject();
        String succid = String.valueOf(UpdateParameters.UPDATE_SUCCESSFUL);
        where = "updateType = :s1 AND starttime >= :s2";
        obj = (Object[]) MgrUtil.getSessionAttribute(UPDATE_OS_DETECTION_IDs);
        objects = new Object[] {UpdateParameters.AH_DOWNLOAD_OS_DETECTION,obj[0] };
        List<?> list = QueryUtil.executeQuery("select hostname,result from "
                + HiveApUpdateResult.class.getSimpleName(), new SortParams(
                "id", true), new FilterParams(where, objects));
        for (Object object : list) {
            Object[] attr = (Object[]) object;
            if(!attr[1].toString().equals(succid)){
                String hostname = attr[0] +",";
                errList.add(hostname);
            }
        }
        if(list.size() == (Integer)obj[1]){
            jsonObject.put("end",true);
            removeUploadOsDetectionSession();
        }
        jsonObject.put("err",errList.size());
        jsonObject.put("errhost",errList.toString());
        jsonObject.put("succ",list.size() - errList.size());
    }

	private Map<HiveAp, UpdateObject> getPoeUpdateObjects(
			Set<HiveAp> selectedHiveAps) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getPoeUpdateObject(hiveAp, maxPower);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getNetdumpUpdateObjects(
			Set<HiveAp> selectedHiveAps, boolean enableNetdump, String netdumpServer,String vlanId,String nVlanId,String device,String gateway,String ipMode,String managerPortStr) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();
			for (HiveAp hiveAp : selectedHiveAps) {
				String portStr="";
				if(isSupportPort(hiveAp)){
					portStr=managerPortStr;
				}
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getNetdumpUpdateObject(hiveAp, enableNetdump, netdumpServer,vlanId,nVlanId,device,gateway,ipMode,portStr);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getCwpUpdateObjects(
			Set<HiveAp> selectedHiveAps, boolean saveServerFiles,
			boolean continued) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();
			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getCwpUpdateObject(hiveAp, saveServerFiles, continued,
								false);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getCertUpdateObjects(
			Set<HiveAp> selectedHiveAps, boolean saveServerFiles,
			boolean continued) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getCertUpdateObject(hiveAp, saveServerFiles,
								continued, false);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getVpnUpdateObjects(
			Set<HiveAp> selectedHiveAps, boolean saveServerFiles,
			boolean continued) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getVpnUpdateObject(hiveAp, saveServerFiles, continued,
								false);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getCountryCodeUpdateObjects(
			Set<HiveAp> selectedHiveAps) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getCountryCodeUpdateObject(hiveAp, false, countryCode,
								str_countryCodeOffset);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private String outdoorString ;
	public String getOutdoorString() {
		return outdoorString;
	}
	public void setOutdoorString(String outdoorString) {
		this.outdoorString = outdoorString;
	}
	// offSet for Outdoor Settings ..
	private int commonOffSet = 5;
	public int getCommonOffSet() {
		return commonOffSet;
	}
	public void setCommonOffSet(int commonOffSet) {
		this.commonOffSet = commonOffSet;
	}

	private Map<HiveAp, UpdateObject> getOutdoorSettingsUpdateObjects(
			Set<HiveAp> selectedHiveAps) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getOutdoorSettingsUpdateObject(hiveAp, false, Boolean.valueOf(getOutdoorString()),
								NmsUtil.getCLIFormatString(commonOffSet
										+ BeTopoModuleParameters.DEFAULT_REBOOT_DELAY));
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}
	private Map<HiveAp, UpdateObject> getBootstrapUpdateObjects(
			Set<HiveAp> selectedHiveAps) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();
			for (HiveAp hiveAp : selectedHiveAps) {
				BootstrapConfigObject bcObject = new BootstrapConfigObject();
				bcObject.setAdminName(bootstrapAdmin);
				bcObject.setPassword(bootstrapPassword);
				bcObject.setCapwapServer(capwapServer);
				bcObject.setUdpPort(udpPort);
				bcObject.setEchoTimeout(timeOut);
				bcObject.setDeadInterval(deadInterval);
				bcObject.setEnableDtls(enabledDTLS);
				if (enabledDTLS && (!defaultPassPhrase)) {
					bcObject.setPassPhrase(newPassPhrase);
				} else {
					bcObject.setPassPhrase("");
				}
				bcObject.setCapwapServerBackup(capwapServerBackup);
				bcObject.setVhmName(vhmName);
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getBootstrapUpdateObject(hiveAp, bcObject);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getImageUpdateObjects(
			Set<HiveAp> selectedHiveAps, boolean continued,
			HiveApUpdateSettings setting, Map<Long, String> imageMapping) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();
			boolean tftp = TransferType.tftp.equals(setting.getImageTransfer());
			boolean isRelative = ActivateType.activateAfterTime.equals(setting
					.getImageActivateType());
			boolean isSelectVer = ImageSelectionType.softVer.equals(setting
					.getImageSelectType());
			String activateTime = setting.getImageActivateTimeString();
			String br100ActivateTime = setting.getBr100ImageActivateTimeString();
			int limit = setting.getConnectionLimit();
			int maxTimeout = setting.getImageTimedout() == 0 ? UpdateParameters.IMAGE_TIMEOUT_MAX
					: (int) setting.getImageTimedout() * 60 * 1000;
			String version;
			if (VER_SELECT_LATEST.equals(imageVersionSelectType)) {
				version = this.latestVersion;
			} else {
				version = this.selectedVersion;
			}
			for (HiveAp hiveAp : selectedHiveAps) {
				String imageName;
				if(imageMapping != null && imageMapping.containsKey(hiveAp.getId())){
					imageName = imageMapping.get(hiveAp.getId());
				}else if(MgrUtil.isEnableDownloadServer()){
					imageName = isSelectVer ? null : getSelectedImage();
				}else{
					if(isSelectVer){
						short apModel;
						if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_SR2124P && "01".equals(hiveAp.getHardwareRevision())){
							apModel = (short)(0 - HiveAp.HIVEAP_MODEL_SR2124P);
						}else{
							apModel = hiveAp.getHiveApModel();
						}
						HiveApImageInfo imageInfo = com.ah.be.config.image.ImageManager.getLatestImageName(apModel, version);
						imageName = imageInfo == null ? null : imageInfo.getImageName();
					}else{
						imageName = getSelectedImage();
					}
				}
				UpdateObject updateObj;
				//for fix bug 16604, BR100 upload image auto reboot
				if(hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100){
					updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
							.getImageUpdateObject(hiveAp, br100ActivateTime, true,
									imageName, continued, false, tftp, limit,
									maxTimeout, isSelectVer, version);
				}else{
					updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
							.getImageUpdateObject(hiveAp, activateTime, isRelative,
									imageName, continued, false, tftp, limit,
									maxTimeout, isSelectVer, version);
				}
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getSignatureUpdateObjects(
			Map<HiveAp, LSevenSignatures> map, int limit, int maxTimeout)
			throws Exception {
		if (null != map && map.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();
			for (HiveAp hiveAp : map.keySet()) {
				String signatureName = map.get(hiveAp).getFileName();
				log.info(String
						.format("Device '%s' wrap into a signature update object with signature file name '%s', connection limit '%s', max timeout '%s'",
								hiveAp.getHostName(), signatureName, limit,
								maxTimeout));
				UpdateObject updateObj = HmBeConfigUtil
						.getUpdateObjectBuilder().getSignatureUpdateObject(
								hiveAp, signatureName, limit, maxTimeout);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, List<UpdateObject>> getConfigUpdateObjects(Set<HiveAp> selectedHiveAps,
			boolean saveServerFiles, boolean continued, HiveApUpdateSettings setting,
			boolean upConfigs, boolean upUsers, Set<HiveAp> selectedImageHiveAps) throws Exception {
		if(selectedHiveAps == null || selectedHiveAps.isEmpty()){
			return null;
		}
		if(!upConfigs && !upUsers){
			return null;
		}

		Map<HiveAp, List<UpdateObject>> resMap = new HashMap<>();
		boolean isRelative = ActivateType.activateAfterTime.equals(setting
				.getConfigActivateType());
//		String activateTime = setting.getConfigActivateTimeString();
		//reboot device at last use reboot cli.
		String activateTime = null;
//		Object netChangeObj = MgrUtil.getSessionAttribute(UPDATE_HIVEAP_NETWORKPOLICY_CHANGED);
//		Set<?> hiveApsNWPolicyChg = null;
//		if(netChangeObj instanceof Set<?>){
//			hiveApsNWPolicyChg = (Set<?>)netChangeObj;
//		}

		String hmVersion = NmsUtil.getHMCurrentVersion();
		for (HiveAp hiveAp : selectedHiveAps) {
			resMap.put(hiveAp, new ArrayList<UpdateObject>());
			short type;
			if(selectedImageHiveAps.contains(hiveAp)){
				//exists image upgrade.
				hiveAp.setSoftVer(hmVersion);
				type = UpdateParameters.COMPLETE_SCRIPT;
			}
//			else if(hiveApsNWPolicyChg != null && hiveApsNWPolicyChg.contains(hiveAp.getId())){
//				type = UpdateParameters.COMPLETE_SCRIPT;
//			}
			else if (ConfigSelectType.full.equals(setting.getConfigSelectType())) {
				type = UpdateParameters.COMPLETE_SCRIPT;
			} else if (ConfigSelectType.deltaConfig.equals(setting
					.getConfigSelectType())) {
				type = UpdateParameters.DELTA_SCRIPT_LAST;
			} else if (ConfigSelectType.deltaRunning.equals(setting
					.getConfigSelectType())) {
				type = UpdateParameters.DELTA_SCRIPT_RUNNING;
			} else {
				if(hiveAp.isNeedCompleteUpdate()){
					type = UpdateParameters.COMPLETE_SCRIPT;
				}else{
					type = UpdateParameters.DELTA_SCRIPT_RUNNING;
				}
			}

			//All complete config and audit config version > 6.0.0.0 support download server download.
			boolean completeUpload = type == UpdateParameters.COMPLETE_SCRIPT;
			boolean useDSDownload = MgrUtil.isEnableDownloadServer() &&
					(completeUpload || NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "6.0.0.0") >= 0);

			if(completeUpload || !useDSDownload){
				if(upConfigs){
					UpdateObject updateObj;
					if (isHiveApWithReboot(AH_DOWNLOAD_SCRIPT, setting, hiveAp) && upUsers) {
						updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
								.getConfigUpdateObject(hiveAp, type, null, false,
										saveServerFiles, continued, false, useDSDownload);
					} else {
						updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
								.getConfigUpdateObject(hiveAp, type, activateTime,
										isRelative, saveServerFiles, continued,
										false, useDSDownload);
					}
					if(updateObj != null){
						resMap.get(hiveAp).add(updateObj);
					}
				}
				if(upUsers){
					UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
							.getPskUpdateObject(hiveAp, type, activateTime,
									isRelative, saveServerFiles, continued, false, useDSDownload);
					if(updateObj != null){
						resMap.get(hiveAp).add(updateObj);
					}
				}
			}else{
				short dsUploadType;
				if(upConfigs && upUsers){
					dsUploadType = UpdateParameters.UPLOAD_TYPE_AUDIT_ALL;
				}else if(upConfigs){
					dsUploadType = UpdateParameters.UPLOAD_TYPE_AUDIT_CONFIG;
				}else if(upUsers){
					dsUploadType = UpdateParameters.UPLOAD_TYPE_AUDIT_USER;
				}else {
					dsUploadType = UpdateParameters.UPLOAD_TYPE_AUDIT_ALL;
				}

				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getDsConfigUpdateObject(hiveAp, dsUploadType, false);
				if(updateObj != null){
					resMap.get(hiveAp).add(updateObj);
				}
			}
		}
		return resMap;
	}

//	private Map<HiveAp, UpdateObject> getScriptUpdateObjects(
//			Set<HiveAp> selectedHiveAps, Set<HiveAp> selectedPskHiveAPs,
//			boolean saveServerFiles, boolean continued,
//			HiveApUpdateSettings setting) throws Exception {
//		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
//			Map<HiveAp, UpdateObject> ht_up = new HashMap<HiveAp, UpdateObject>();
//			boolean isRelative = ActivateType.activateAfterTime.equals(setting
//					.getConfigActivateType());
//			String activateTime = setting.getConfigActivateTimeString();
//
//			Object netChangeObj = MgrUtil.getSessionAttribute(UPDATE_HIVEAP_NETWORKPOLICY_CHANGED);
//			Set<?> hiveApsNWPolicyChg = null;
//			if(netChangeObj instanceof Set<?>){
//				hiveApsNWPolicyChg = (Set<?>)netChangeObj;
//			}
//
//			for (HiveAp hiveAp : selectedHiveAps) {
//				short type;
//				if(hiveApsNWPolicyChg != null && hiveApsNWPolicyChg.contains(hiveAp.getId())){
//					type = UpdateParameters.COMPLETE_SCRIPT;
//				}else if (ConfigSelectType.full.equals(setting.getConfigSelectType())) {
//					type = UpdateParameters.COMPLETE_SCRIPT;
//				} else if (ConfigSelectType.deltaConfig.equals(setting
//						.getConfigSelectType())) {
//					type = UpdateParameters.DELTA_SCRIPT_LAST;
//				} else if (ConfigSelectType.deltaRunning.equals(setting
//						.getConfigSelectType())) {
//					type = UpdateParameters.DELTA_SCRIPT_RUNNING;
//				} else {
//					if (hiveAp.getLastCfgTime() == 0) {
//						type = UpdateParameters.COMPLETE_SCRIPT;
//					} else {
//						type = UpdateParameters.DELTA_SCRIPT_RUNNING;
//					}
//				}
//				UpdateObject updateObj;
//				if (isHiveApWithReboot(AH_DOWNLOAD_SCRIPT, setting, hiveAp)
//						&& selectedPskHiveAPs.contains(hiveAp)) {
//					updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
//							.getConfigUpdateObject(hiveAp, type, null, false,
//									saveServerFiles, continued, false);
//				} else {
//					updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
//							.getConfigUpdateObject(hiveAp, type, activateTime,
//									isRelative, saveServerFiles, continued,
//									false);
//				}
//				if (null != updateObj) {
//					ht_up.put(hiveAp, updateObj);
//				}
//			}
//			return ht_up;
//		}
//		return null;
//	}
//
//	private Map<HiveAp, UpdateObject> getPskUpdateObjects(
//			Set<HiveAp> selectedHiveAps, boolean saveServerFiles,
//			boolean continued, HiveApUpdateSettings setting) throws Exception {
//		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
//			Map<HiveAp, UpdateObject> ht_up = new HashMap<HiveAp, UpdateObject>();
//			boolean isRelative = ActivateType.activateAfterTime.equals(setting
//					.getConfigActivateType());
//			String activateTime = setting.getConfigActivateTimeString();
//			for (HiveAp hiveAp : selectedHiveAps) {
//				short type;
//				if (ConfigSelectType.full.equals(setting.getConfigSelectType())) {
//					type = UpdateParameters.COMPLETE_SCRIPT;
//				} else if (ConfigSelectType.deltaConfig.equals(setting
//						.getConfigSelectType())) {
//					type = UpdateParameters.DELTA_SCRIPT_LAST;
//				} else if (ConfigSelectType.deltaRunning.equals(setting
//						.getConfigSelectType())) {
//					type = UpdateParameters.DELTA_SCRIPT_RUNNING;
//				} else {
//					if (hiveAp.getLastCfgTime() == 0) {
//						type = UpdateParameters.COMPLETE_SCRIPT;
//					} else {
//						type = UpdateParameters.DELTA_SCRIPT_RUNNING;
//					}
//				}
//				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
//						.getPskUpdateObject(hiveAp, type, activateTime,
//								isRelative, saveServerFiles, continued, false);
//				if (null != updateObj) {
//					ht_up.put(hiveAp, updateObj);
//				}
//			}
//			return ht_up;
//		}
//		return null;
//	}

	private Map<HiveAp, UpdateObject> getOsDetectionUpdateObjects(
			Set<HiveAp> selectedHiveAps, boolean continued,
			HiveApUpdateSettings setting) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();
			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getOsDetectionUpdateObject(hiveAp,
								continued, false);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getCAUpdateObjects(
			Set<HiveAp> selectedHiveAps, short type, boolean continued) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0) {
			Map<HiveAp, UpdateObject> ht_up = new HashMap<>();

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getCloadAuthUpdateObject(hiveAp, continued, true);
				if (null != updateObj) {
					ht_up.put(hiveAp, updateObj);
				}
			}
			return ht_up;
		}
		return null;
	}

	private Map<HiveAp, UpdateObject> getRebootUpdateObjects(
			Set<HiveAp> selectedHiveAps, HiveApUpdateSettings settings) throws Exception {
		if (null != selectedHiveAps && selectedHiveAps.size() > 0 && settings != null) {
			Map<HiveAp, UpdateObject> reboot_up = new HashMap<>();
			String offset = null;
			String date = null, time = null;
			if(settings.getConfigActivateType() == ActivateType.activateNextTime){
				return null;
			}else if(settings.getConfigActivateType() == ActivateType.activateAtTime){
				String atTimeStr = settings.getConfigActivateTimeString();
				String[] paramArg = atTimeStr.split(" ");
				if(paramArg != null && paramArg.length == 2){
					time = paramArg[0];
					date = paramArg[1];
				}
			}else{
				offset = settings.getConfigActivateTimeString();
			}
			if(offset == null){
				offset = NmsUtil.getCLIFormatString(BeTopoModuleParameters.DEFAULT_REBOOT_DELAY);
			}

			for (HiveAp hiveAp : selectedHiveAps) {
				UpdateObject updateObj = HmBeConfigUtil.getUpdateObjectBuilder()
						.getRebootUpdateObject(hiveAp, offset, date, time);
				if (null != updateObj) {
					reboot_up.put(hiveAp, updateObj);
				}
			}
			return reboot_up;
		}
		return null;
	}

	protected void preparePage(String listType) throws Exception {
		Set<Long> selected = new HashSet<>();
		if (null != selectedIds) {
			for (Long id : selectedIds) {
				if (null != id) {
					selected.add(id);
				}
			}
		}
		if (UPDATE_LIST_TYPE_CONFIG.equals(listType)) {
			storeIdListToSession(UPDATE_CONFIG_SELECTED_IDS, selected,
					getPageIds());
			prepareConfigtBoList();
		} else if (UPDATE_LIST_TYPE_IMAGE.equals(listType)) {
			storeIdListToSession(UPDATE_IMAGE_SELECTED_IDs, selected,
					getPageIds());
			prepareImageBoList();
		} else if (UPDATE_LIST_TYPE_L7_SIGNATURE.equals(listType)) {
			storeIdListToSession(UPDATE_L7_SIGNATURE_SELECTED_IDs, selected,
					getPageIds());
			prepareL7SignatureBoList();
		} else if (UPDATE_LIST_TYPE_BOOTSTRAP.equals(listType)) {
			storeIdListToSession(UPDATE_BOOTSTRAP_SELECTED_IDs, selected,
					getPageIds());
			prepareBootstrapBoList();
		} else if (UPDATE_LIST_TYPE_COUNTRYCODE.equals(listType)) {
			storeIdListToSession(UPDATE_COUNTRYCODE_SELECTED_IDs, selected,
					getPageIds());
			prepareCountryCodeBoList();
		} else if (UPDATE_LIST_TYPE_POE.equals(listType)) {
			storeIdListToSession(UPDATE_POE_SELECTED_IDs, selected,
					getPageIds());
			preparePoeBoList();
		} else if (UPDATE_LIST_TYPE_NETDUMP.equals(listType)) {
			storeIdListToSession(UPDATE_NETDUMP_SELECTED_IDs, selected,
					getPageIds());
			prepareNetdumpBoList();
		} else if (UPDATE_OUTDOOR_SETTINGS.equals(listType)) {
			storeIdListToSession(UPDATE_OUTDOORSETTINGS_SELECTED_IDS, selected,
					getPageIds());
			prepareOutdoorSettingsBoList();
		}
	}

	@SuppressWarnings("unchecked")
	private void prepareOutdoorSettingsBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_OUTDOORSETTINGS_SELECTED_IDS);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_OUTDOORSTTINGS, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<HiveAp> selectedBos = (
						List<HiveAp>) QueryUtil
										.executeQuery(boClass, sortParams , filterParams,domainId);
				checkAPVersion(init_ids , selectedBos ,"4.1.1.0");
				selected_ids = new HashSet<>(selectedBos.size());
				for (HiveAp ap : selectedBos) {
					selected_ids.add(ap.getId());
				}
				if(selected_ids.isEmpty()){
					selected_ids = null ;
				}
				filterParams = getListFilterParams(AH_DOWNLOAD_OUTDOORSTTINGS,selected_ids);

				storeIdListToSession(UPDATE_OUTDOORSETTINGS_SELECTED_IDS,
						selected_ids, null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
	}

	private void prepareImageBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_IMAGE_SELECTED_IDs);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_IMAGE, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<?> selectedIds = QueryUtil
						.executeQuery("select id from "
								+ boClass.getSimpleName(), null, filterParams,
								domainId);
				selected_ids = new HashSet<>(selectedIds.size());
				for (Object object : selectedIds) {
					selected_ids.add((Long) object);
				}
				storeIdListToSession(UPDATE_IMAGE_SELECTED_IDs, selected_ids,
						null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
	}

	private void prepareConfigtBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_CONFIG_SELECTED_IDS);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_SCRIPT_WIZARD, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<?> selectedIds = QueryUtil
						.executeQuery("select id from "
								+ boClass.getSimpleName(), null, filterParams,
								domainId);
				selected_ids = new HashSet<>(selectedIds.size());
				for (Object object : selectedIds) {
					selected_ids.add((Long) object);
				}
				storeIdListToSession(UPDATE_CONFIG_SELECTED_IDS, selected_ids,
						null);
			}
			paging.setSelectedIds(selected_ids);

			if(null == selected_ids || selected_ids.isEmpty()) {
				rebuildFilterParam();
			}

			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
	}

	/**
	 * filter the device by device type
	 *
	 * @author Yunzhi Lin
	 * - Time: Oct 19, 2011 2:32:19 PM
	 */
	private void rebuildFilterParam() {
		String listType = (String) MgrUtil.getSessionAttribute(HiveApAction.HM_LIST_TYPE);

		List<Short> deviceTypes = new ArrayList<>();
		if ("managedVPNGateways".equals(listType)) {
			deviceTypes.add(HiveAp.Device_TYPE_VPN_GATEWAY);
			deviceTypes.add(HiveAp.Device_TYPE_VPN_BR);
		} else if ("managedRouters".equals(listType)) {
			deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);
		} else if ("managedSwitches".equals(listType)) {
			deviceTypes.add(HiveAp.Device_TYPE_SWITCH);
		} else if ("managedDeviceAPs".equals(listType)) {
			deviceTypes.add(HiveAp.Device_TYPE_HIVEAP);
		} else {
			// All devices
			return;
		}
		String where = filterParams.getWhere();
		Object[] bingds = filterParams.getBindings();
		Object[] newbingds;
		if(null == bingds) {
			where = "deviceType in :s1";
			newbingds = new Object[]{deviceTypes};
		} else {
			int newLength = bingds.length+1;
			where += " and deviceType in :s"+newLength;
			newbingds = new Object[newLength];
			System.arraycopy(bingds, 0, newbingds, 0, bingds.length);
			newbingds[bingds.length] = deviceTypes;
		}
		filterParams = new FilterParams(where, newbingds);
	}

	private void preparePoeBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_POE_SELECTED_IDs);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_POE, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<?> selectedIds = QueryUtil
						.executeQuery("select id from "
								+ boClass.getSimpleName(), null, filterParams,
								domainId);
				selected_ids = new HashSet<>(selectedIds.size());
				for (Object object : selectedIds) {
					selected_ids.add((Long) object);
				}
				storeIdListToSession(UPDATE_POE_SELECTED_IDs, selected_ids,
						null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
	}

	@SuppressWarnings("unchecked")
	private void prepareNetdumpBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_NETDUMP_SELECTED_IDs);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_NET_DUMP, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<HiveAp> selectedBos = (List<HiveAp>) QueryUtil
						.executeQuery(boClass, null , filterParams,domainId);

				checkAPDeviceType(init_ids,selectedBos,HiveAp.Device_TYPE_VPN_GATEWAY);
				selected_ids = new HashSet<>(selectedBos.size());
				for (HiveAp ap : selectedBos) {
					selected_ids.add(ap.getId());
				}
				if(selected_ids.isEmpty()){
					selected_ids = null ;
				}
				filterParams = getListFilterParams(AH_DOWNLOAD_NET_DUMP,selected_ids);
				storeIdListToSession(UPDATE_NETDUMP_SELECTED_IDs, selected_ids,
						null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
		for(Object obj:page){
			HiveAp hiveAp=(HiveAp) obj;
			short hiveApModel=hiveAp.getHiveApModel();
			if(isSupportPort(hiveAp)){
				showPort=true;
				if(portRange>HiveAp.getPortRange(hiveApModel)){
					portRange=HiveAp.getPortRange(hiveApModel);
				}
			}
		}
	}

	private void prepareCountryCodeBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_COUNTRYCODE_SELECTED_IDs);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_COUNTRY_CODE, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<?> selectedIds = QueryUtil
						.executeQuery("select id from "
								+ boClass.getSimpleName(), null, filterParams,
								domainId);
				selected_ids = new HashSet<>(selectedIds.size());
				for (Object object : selectedIds) {
					selected_ids.add((Long) object);
				}
				storeIdListToSession(UPDATE_COUNTRYCODE_SELECTED_IDs,
						selected_ids, null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
	}

	private void prepareBootstrapBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_BOOTSTRAP_SELECTED_IDs);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_BOOTSTRAP, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				List<?> selectedIds = QueryUtil
						.executeQuery("select id from "
								+ boClass.getSimpleName(), null, filterParams,
								domainId);
				selected_ids = new HashSet<>(selectedIds.size());
				for (Object object : selectedIds) {
					selected_ids.add((Long) object);
				}
				storeIdListToSession(UPDATE_BOOTSTRAP_SELECTED_IDs,
						selected_ids, null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
		}
	}


	private void prepareL7SignatureBoList() throws Exception {
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> selected_ids = getIdListFromSession(UPDATE_L7_SIGNATURE_SELECTED_IDs);

		enableSorting();
		enablePaging();

		filterParams = getListFilterParams(AH_DOWNLOAD_L7_SIGNATURE, init_ids);
		if (null == filterParams) {
			page = new ArrayList<HiveAp>();
		} else {
			if (null == selected_ids && null != init_ids && !init_ids.isEmpty()) {
				generateFilteredMessage(init_ids,
						LSevenSignatures.getAllSuppportedPlatform(), "6.0.2.0");
				List<?> selectedIds = QueryUtil
						.executeQuery(
								"select id from " + boClass.getSimpleName(),
								null, filterParams, domainId);
				selected_ids = new HashSet<>(selectedIds.size());
				for (Object object : selectedIds) {
					selected_ids.add((Long) object);
				}
				storeIdListToSession(UPDATE_L7_SIGNATURE_SELECTED_IDs,
						selected_ids, null);
			}
			paging.setSelectedIds(selected_ids);
			page = paging.executeQuery(sortParams, filterParams, domainId);
			// smart select signature file if just one device is selected, bug
			// 28981
			if (paging.getAvailableRowCount() == 1 && null != selected_ids && selected_ids.size() == 1) {
				HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class,
						selected_ids.toArray(new Long[0])[0]);
				L7SignatureMng l7MngObj = new L7SignatureMng();
				// get latest signature file name
				String latestVer = l7MngObj
						.findLatestSupportedVersion(
								hiveAp.getSignatureVerString(),
								hiveAp.getHiveApModel());
				String signatureName = l7MngObj.findSignatureFileNameByVersion(
						latestVer, hiveAp.getHiveApModel());
				this.selectedImage = signatureName;
			}
		}
	}

	private void prepareBootstrapParams() {
		try {
			List<CapwapSettings> capwapSettings = QueryUtil.executeQuery(
					CapwapSettings.class, null, null);
			if (!capwapSettings.isEmpty()) {
				CapwapSettings setting = capwapSettings.get(0);
				capwapServer = setting.getPrimaryCapwapIP();
				if (null == capwapServer || "".equals(capwapServer)) {
					capwapServer = HmBeOsUtil.getHiveManagerIPAddr();
				}
				capwapServerBackup = setting.getBackupCapwapIP();
				udpPort = setting.getUdpPort();
				timeOut = setting.getTimeOut();
				deadInterval = setting.getNeighborDeadInterval();
				newPassPhrase = setting.getBootStrap();
				byte capability = setting.getDtlsCapability();
				enabledDTLS = CapwapSettings.DTLS_AUTO == capability
						|| CapwapSettings.DTLS_DTLSONLY == capability;
				newPassPhrase = setting.getBootStrap();
				confirmPassPhrase = newPassPhrase;
				defaultPassPhrase = (newPassPhrase == null || newPassPhrase.length() == 0);
			}
		} catch (Exception e) {
			log.debug("prepareBootstrapParams", e.getMessage());
		}
	}

	private FilterParams getListFilterParams(short updateType,
			Set<Long> init_ids) {
		List<Short> status = new ArrayList<>(2);
		status.add(HiveAp.STATUS_MANAGED);
		status.add(HiveAp.STATUS_NEW);
		FilterParams filter;
		if (AH_DOWNLOAD_CWP == updateType) {
			if (null == init_ids || init_ids.isEmpty()) {
				String where = "manageStatus in (:s1) AND (" +
						"ethCwpCwpProfile != null " +
						"or exists (select 1 from ap.configTemplate c1 inner join c1.ssidInterfaces si " +
								"where si.ssidProfile != null and (si.ssidProfile.cwp != null or si.ssidProfile.userPolicy != null or si.ssidProfile.ppskECwp != null or si.ssidProfile.wpaECwp != null)) " +
						"or exists (select 1 from ap.configTemplate c1 inner join c1.portProfiles p inner join p.basicProfiles b " +
								"where b.accessProfile != null and b.accessProfile.cwp != null and ap.deviceType != :s2)" +
						")";
				Object[] values = new Object[2];
				values[0] = status;
				values[1] = HiveAp.Device_TYPE_SWITCH;
				filter = new FilterParams(where, values);
			} else {
				String where = "manageStatus in (:s1) and id in (:s2) and (" +
						"ethCwpCwpProfile != null " +
						"or exists (select 1 from ap.configTemplate c1 inner join c1.ssidInterfaces si " +
								"where si.ssidProfile != null and (si.ssidProfile.cwp != null or si.ssidProfile.userPolicy != null or si.ssidProfile.ppskECwp != null or si.ssidProfile.wpaECwp != null)) " +
						"or exists (select 1 from ap.configTemplate c1 inner join c1.portProfiles p inner join p.basicProfiles b " +
								"where b.accessProfile != null and b.accessProfile.cwp != null and ap.deviceType != :s3)" +
						")";
				Object[] values = new Object[3];
				values[0] = status;
				values[1] = init_ids;
				values[2] = HiveAp.Device_TYPE_SWITCH;
				filter = new FilterParams(where, values);
			}
		} else if (AH_DOWNLOAD_RADIUS_CERTIFICATE == updateType) {
			if (null == init_ids || init_ids.isEmpty()) {
				filter = HiveApUtils.getRadiusServerApFilter(false, false);
			} else {
				filter = HiveApUtils.getRadiusServerApFilter(false, false, init_ids);
			}
		} else if (AH_DOWNLOAD_POE == updateType) {
			if (null == init_ids || init_ids.size() == 0) {
				String where = "manageStatus in (:s1) AND hiveApModel in (:s2)";
				Object[] values = new Object[2];
				values[0] = status;
				Set<Short> models = new HashSet<>();
				models.add(HiveAp.HIVEAP_MODEL_320);
				models.add(HiveAp.HIVEAP_MODEL_340);
				models.add(HiveAp.HIVEAP_MODEL_380);
				values[1] = models;
				filter = new FilterParams(where, values);
			} else {
				String where = "manageStatus in (:s1) AND hiveApModel in (:s2) AND id in (:s3)";
				Object[] values = new Object[3];
				values[0] = status;
				Set<Short> models = new HashSet<>();
				models.add(HiveAp.HIVEAP_MODEL_320);
				models.add(HiveAp.HIVEAP_MODEL_340);
				models.add(HiveAp.HIVEAP_MODEL_380);
				values[1] = models;
				values[2] = init_ids;
				filter = new FilterParams(where, values);
			}
		}else if (AH_DOWNLOAD_OS_DETECTION == updateType) {
			List<Short> status2 = new ArrayList<>();
			status2.add(HiveAp.STATUS_MANAGED);
			String where = "manageStatus in (:s1) AND SIMULATED = 'false' AND softVer >= '5.1.2.0'";
			Object[] values = new Object[1];
			values[0] = status2;
			filter = new FilterParams(where, values);
		} else if (AH_DOWNLOAD_NET_DUMP == updateType){
			if (null == init_ids) {
				String where = "manageStatus in (:s1) AND 1=2 AND deviceType<>"+String.valueOf(HiveAp.Device_TYPE_VPN_GATEWAY);
				Object[] values = new Object[1];
				values[0] = status;
				filter = new FilterParams(where, values);
			} else if(init_ids.size() == 0){
				String where = "manageStatus in (:s1) AND deviceType<>"+String.valueOf(HiveAp.Device_TYPE_VPN_GATEWAY);
				Object[] values = new Object[1];
				values[0] = status;
				filter = new FilterParams(where, values);
			} else {
				String where = "manageStatus in (:s1) AND id in (:s2)";
				Object[] values = new Object[2];
				values[0] = status;
				values[1] = init_ids;
				filter = new FilterParams(where, values);
			}
		} else if (AH_DOWNLOAD_VPN_CERTIFICATE == updateType) {
			if (null == init_ids || init_ids.size() == 0) {
				String where = "manageStatus in (:s1) AND vpnMark != :s2 AND (configTemplate.vpnService != null or exists (select 1 from "+
							VpnService.class.getSimpleName()+" vpn join vpn.vpnGateWaysSetting as joined where ap.id = joined.hiveApId))";
				Object[] values = new Object[2];
				values[0] = status;
				values[1] = HiveAp.VPN_MARK_NONE;
				filter = new FilterParams(where, values);
			} else {
				String where = "manageStatus in (:s1) AND vpnMark != :s2 AND id in (:s3) AND (configTemplate.vpnService != null or exists (select 1 from "+
						VpnService.class.getSimpleName()+" vpn join vpn.vpnGateWaysSetting as joined where ap.id = joined.hiveApId))";
				Object[] values = new Object[3];
				values[0] = status;
				values[1] = HiveAp.VPN_MARK_NONE;
				values[2] = init_ids;
				filter = new FilterParams(where, values);
			}
		} else if(AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE == updateType) {
			filter = HiveApUtils.getIdmCertFilter(init_ids);
		} else if (AH_DOWNLOAD_OUTDOORSTTINGS == updateType) {// all selected ap are not pass version check so add '1=2' to show no records
			if (null == init_ids ) {
				String where = "manageStatus in (:s1) AND 1=2 AND hiveApModel="+String.valueOf(HiveAp.HIVEAP_MODEL_170);
				Object[] values = new Object[1];
				values[0] = status;
				filter = new FilterParams(where, values);
			}
			else if ( init_ids.size() == 0 ) {
				String where = "manageStatus in (:s1) AND softVer>='4.1.1.0' AND hiveApModel="+String.valueOf(HiveAp.HIVEAP_MODEL_170);
				Object[] values = new Object[1];
				values[0] = status;
				filter = new FilterParams(where, values);
			}
			else {
				String where = "manageStatus in (:s1) AND id in (:s2) AND hiveApModel="+String.valueOf(HiveAp.HIVEAP_MODEL_170);
				Object[] values = new Object[2];
				values[0] = status;
				values[1] = init_ids;
				filter = new FilterParams(where, values);
			}
		}else if(AH_DOWNLOAD_COUNTRY_CODE == updateType){
			if (null == init_ids || init_ids.size() == 0) {
				String where = "manageStatus in (:s1) and deviceType != :s2";
				Object[] values = new Object[2];
				values[0] = status;
				values[1] = HiveAp.Device_TYPE_VPN_GATEWAY;
				filter = new FilterParams(where, values);
			} else {
				String where = "manageStatus in (:s1) AND id in (:s2) AND deviceType != :s3";
				Object[] values = new Object[3];
				values[0] = status;
				values[1] = init_ids;
				values[2] = HiveAp.Device_TYPE_VPN_GATEWAY;
				filter = new FilterParams(where, values);
			}
		} else if (AH_DOWNLOAD_L7_SIGNATURE == updateType) {
			if (null == init_ids || init_ids.isEmpty()) {
				filter = HiveApUtils.getL7SignatureDeviceFilter(null);
			} else {
				filter = HiveApUtils.getL7SignatureDeviceFilter(init_ids);
			}
		} else {
			if (null == init_ids || init_ids.size() == 0) {
				String where = "manageStatus in (:s1)";
				Object[] values = new Object[1];
				values[0] = status;
				filter = new FilterParams(where, values);
			} else {
				String where = "manageStatus in (:s1) AND id in (:s2)";
				Object[] values = new Object[2];
				values[0] = status;
				values[1] = init_ids;
				filter = new FilterParams(where, values);
			}
		}
		return filter;
	}

	private Set<Long> getCurrentSelectedBoIds(short updateType)
			throws Exception {
		Set<Long> s_ids = new HashSet<>();
		if(selectedDeviceIdStr != null && !"".equals(selectedDeviceIdStr)){
			String[] ids = selectedDeviceIdStr.split(",");
			for (String id : ids) {
				s_ids.add(Long.valueOf(id));
			}
		}else if (allItemsSelected) {
			Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
			FilterParams filter = getListFilterParams(updateType, init_ids);
			if (null != filter) {
				List<?> selectedIds = QueryUtil.executeQuery("select id from "
						+ boClass.getSimpleName(), null, filter, domainId);
				if (null != selectedIds) {
					for (Object id : selectedIds) {
						s_ids.add((Long) id);
					}
				}
			}
		}else if(getIdListFromSession(SIMPLIFIED_UPDATE_SELECTED_IDs) != null){
			//redirect from hiveAp.action?
			s_ids.addAll(getIdListFromSession(SIMPLIFIED_UPDATE_SELECTED_IDs));
		} else {
			List<Long> selectedIds = getSelectedIds();
			if (null != selectedIds) {
				for (Long id : selectedIds) {
					s_ids.add(id);
				}
			}
		}
		return s_ids;
	}

	private void prepareDistributedDownload(){
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		Set<Long> allHiveAps = new HashSet<>();
		if(init_ids == null || (init_ids.isEmpty())){
			List<?> selectedIds = QueryUtil.executeQuery("select id from "
					+ boClass.getSimpleName(), null, filterParams,
					domainId);
			for (Object object : selectedIds) {
				allHiveAps.add((Long) object);
			}
		}else{
			allHiveAps.addAll(init_ids);
		}

		Map<String, List<Long>> disServerMap = HmBeConfigUtil.getImageDistributor().getPortalAPListByHive(allHiveAps, domainId);
		Map<Long, String> idHostMapping = HmBeConfigUtil.getImageDistributor().getIdAndHostnameMapping(domainId);
		Map<Long, String> idHivenameMapping = HmBeConfigUtil.getImageDistributor().getIdAndHiveNameMapping(domainId);
		MgrUtil.setSessionAttribute(UPDATE_DISTRIBUTED_SERVER_LIST_GROUP_BY_HIVE, disServerMap);
		MgrUtil.setSessionAttribute(UPDATE_ID_AND_HOSTNAME_MAPPING, idHostMapping);
		MgrUtil.setSessionAttribute(UPDATE_ID_AND_HIVENAME_MAPPING, idHivenameMapping);

		//set distributed server
		String currentHive = getUniqueHiveName(init_ids, domainId);
		if(currentHive != null){
			List<Long> disListIds = disServerMap.get(currentHive);
			if(disListIds != null && !disListIds.isEmpty()){
				this.setDistributedServer(disListIds.get(0));
			}
		}
	}

	private String getUniqueHiveName(Set<Long> hiveApIds,  Long domainId){
		String currentHive;
		if(hiveApIds == null || (hiveApIds.isEmpty())){
			currentHive = null;
		}else{
			Set<String> hiveSet = HmBeConfigUtil.getImageDistributor().getHiveName(hiveApIds, domainId);
			if(hiveSet != null && hiveSet.size() == 1){
				currentHive = (String)(hiveSet.toArray())[0];
			}else{
				currentHive = null;
			}
		}
		return currentHive;
	}

	public static final EnumItem[] ENUM_HOURS = enumItems(24, "hr");

	public static final EnumItem[] ENUM_MINUTES = enumItems(60, "min");

	private static EnumItem[] enumItems(int len, String unit) {
		EnumItem[] enumItems = new EnumItem[len];
		for (int i = 0; i < len; i++) {
			String tmp = String.valueOf(i);
			if (tmp.length() == 1)
				tmp = "0" + tmp;
			enumItems[i] = new EnumItem(i, tmp +" "+ unit);
		}
		return enumItems;
	}

	public static EnumItem[] getENUM_HOURS() {
		return ENUM_HOURS;
	}

	public static EnumItem[] getENUM_MINUTES() {
		return ENUM_MINUTES;
	}

	public TextItem[] getImageSelectType1() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ImageSelectionType.softVer.toString(),
				getText("hiveAp.update.images.selection.type.ver")) };
	}

	public TextItem[] getImageSelectType2() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ImageSelectionType.imgName.toString(),
				getText("hiveAp.update.images.selection.type.file")) };
	}

	public TextItem[] getImageVersionSelectType1() {
		return new TextItem[] { new TextItem(VER_SELECT_LATEST,
				getText("hiveAp.update.images.version.latest.label")) };
	}

	public TextItem[] getImageVersionSelectType2() {
		return new TextItem[] { new TextItem("others",
				getText("hiveAp.update.images.version.others.label")) };
	}

	public TextItem[] getImageActivateType1() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ActivateType.activateAtTime.toString(),
				getText("hiveAp.update.configuration.activateAt")) };
	}

	public TextItem[] getImageActivateType2() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ActivateType.activateAfterTime.toString(),
				getText("hiveAp.update.configuration.activateAfter")) };
	}

	public TextItem[] getImageActivateType3() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ActivateType.activateNextTime.toString(),
				getText("hiveAp.update.configuration.activateNext")) };
	}

	public TextItem[] getImageTransfer1() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.TransferType.scp.toString(),
				getText("hiveAp.update.images.transfer.type.scp")) };
	}

	public TextItem[] getImageTransfer2() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.TransferType.tftp.toString(),
				getText("hiveAp.update.images.transfer.type.tftp")) };
	}

	public TextItem[] getConfigSelectType1() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ConfigSelectType.auto.toString(),
				getText("hiveAp.update.configuration.uploadType.auto")) };
	}

	public TextItem[] getConfigSelectType2() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ConfigSelectType.full.toString(),
				getText("hiveAp.update.configuration.uploadType.full")) };
	}

	public TextItem[] getConfigSelectType3() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ConfigSelectType.deltaConfig.toString(),
				getText("hiveAp.update.configuration.uploadType.deltaConfig")) };
	}

	public TextItem[] getConfigSelectType4() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ConfigSelectType.deltaRunning.toString(),
				getText("hiveAp.update.configuration.uploadType.deltaRunning")) };
	}

	public TextItem[] getConfigActivateType1() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ActivateType.activateAtTime.toString(),
				getText("hiveAp.update.configuration.activateAt")) };
	}

	public TextItem[] getConfigActivateType2() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ActivateType.activateAfterTime.toString(),
				getText("hiveAp.update.configuration.activateAfter")) };
	}

	public TextItem[] getConfigActivateType3() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ActivateType.activateNextTime.toString(),
				getText("hiveAp.update.configuration.activateNext")) };
	}

	public TextItem[] getSignatureSelectType1() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ImageSelectionType.softVer.toString(),
				getText("hiveAp.update.l7.signature.selection.type.ver")) };
	}

	public TextItem[] getSignatureSelectType2() {
		return new TextItem[] { new TextItem(
				HiveApUpdateSettings.ImageSelectionType.imgName.toString(),
				getText("hiveAp.update.l7.signature.selection.type.file")) };
	}

	public TextItem[] getSignatureVersionSelectType1() {
		return new TextItem[] { new TextItem(VER_SELECT_LATEST,
				getText("hiveAp.file.l7.signature.version.latest.label")) };
	}

	public TextItem[] getSignatureVersionSelectType2() {
		return new TextItem[] { new TextItem("others",
				getText("hiveAp.file.l7.signature.version.others.label")) };
	}

	private String bootstrapAdmin;

	private String bootstrapPassword;

	/* for image upload settings and L7 signature upload settings */

	private String selectedImage;

	private String selectedVersion;

	private String imageTransfer;

	private String imageSelectType;

	private short connectType = HiveApUpdateSettings.CONNECT_TYPE_LOCAL;

	private String latestVersion;

	/* for image upload settings */
	private String imageDate;

	private String imageHour;

	private String imageMin;

	private String imageActivateType;
	private String imageVersionSelectType = VER_SELECT_LATEST;

	/* for signature upload settings */
	private static final String VER_SELECT_LATEST = "latest";
	private String signatureVersionSelectType = VER_SELECT_LATEST;

	/* for configure upload settings */
	private String configDate;

	private String configHour;

	private String configMin;

	private String configActivateType;

	private String configSelectType;

	private boolean configConfiguration;

	private boolean configCwp;

	private boolean configCertificate;

	private boolean configUserDatabase;

	private boolean imageUpgrade = false;
	
	private boolean forceImageUpgrade = false;

	private boolean completeCfgUpdate = false;

	private boolean saveUploadSetting = true;

	private boolean simpleUpdate = false;
	
	public static final short REBOOT_TYPE_AUTO = 0;
	public static final short REBOOT_TYPE_MANUAL = 1;
	private short simplifiedRebootType = REBOOT_TYPE_AUTO;

//	private Long distributedServer;

	public String getConfigDate() {
		if (null == configDate) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.DAY_OF_YEAR, 1);
			configDate = formatter.format(cd.getTime());
		}
		return configDate;
	}

	public void setConfigDate(String configDate) {
		this.configDate = configDate;
	}

	public String getConfigHour() {
		return configHour;
	}

	public void setConfigHour(String configHour) {
		this.configHour = configHour;
	}

	public String getConfigMin() {
		return configMin;
	}

	public void setConfigMin(String configMin) {
		this.configMin = configMin;
	}

	public String getConfigActivateType() {
		return configActivateType;
	}

	public void setConfigActivateType(String configActivateType) {
		this.configActivateType = configActivateType;
	}

	public String getConfigSelectType() {
		return configSelectType;
	}

	public void setConfigSelectType(String configSelectType) {
		this.configSelectType = configSelectType;
	}

	public void setConfigConfiguration(boolean configConfiguration) {
		this.configConfiguration = configConfiguration;
	}

	public void setConfigCwp(boolean configCwp) {
		this.configCwp = configCwp;
	}

	public void setConfigCertificate(boolean configCertificate) {
		this.configCertificate = configCertificate;
	}

	public void setConfigUserDatabase(boolean configUserDatabase) {
		this.configUserDatabase = configUserDatabase;
	}

	public boolean isSaveUploadSetting(){
		return this.saveUploadSetting;
	}

	public void setSaveUploadSetting(boolean saveUploadSetting){
		this.saveUploadSetting = saveUploadSetting;
	}

	public String getImageDate() {
		if (null == imageDate) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cd = Calendar.getInstance();
			cd.add(Calendar.DAY_OF_YEAR, 1);
			imageDate = formatter.format(cd.getTime());
		}
		return imageDate;
	}

	public void setImageDate(String imageDate) {
		this.imageDate = imageDate;
	}

	public String getImageHour() {
		return imageHour;
	}

	public void setImageHour(String imageHour) {
		this.imageHour = imageHour;
	}

	public String getImageMin() {
		return imageMin;
	}

	public void setImageMin(String imageMin) {
		this.imageMin = imageMin;
	}

	public String getImageTransfer() {
		return imageTransfer;
	}

	public void setImageTransfer(String imageTransfer) {
		this.imageTransfer = imageTransfer;
	}

	public String getImageActivateType() {
		return imageActivateType;
	}

	public void setImageActivateType(String imageActivateType) {
		this.imageActivateType = imageActivateType;
	}

	public String getSignatureVersionSelectType() {
		return signatureVersionSelectType;
	}

	public void setSignatureVersionSelectType(String signatureVersionSelectType) {
		this.signatureVersionSelectType = signatureVersionSelectType;
	}

	public String getImageVersionSelectType() {
		return imageVersionSelectType;
	}

	public void setImageVersionSelectType(String imageVersionSelectType) {
		this.imageVersionSelectType = imageVersionSelectType;
	}

	public String getImageSelectType() {
		return imageSelectType;
	}

	public void setImageSelectType(String imageSelectType) {
		this.imageSelectType = imageSelectType;
	}

	public short getConnectType() {
		return connectType;
	}

	public void setConnectType(short connectType) {
		this.connectType = connectType;
	}

	public EnumItem[] getConnectTypes() {
		return HiveApUpdateSettings.CONNECT_TYPE;
	}

	// PoE power parameters
	public static final int NO_MAX_POWER = 0;
	public static final int MAX_POWER_802_3AF = 1;
	public static final int MAX_POWER_HIGH = 20000;
	public static final int MAX_POWER_MEDIUM_HIGH = 18000;
	public static final int MAX_POWER_MEDIUM = 16500;
	public static final int MAX_POWER_MEDIUM_LOW = 15000;
	public static final int MAX_POWER_LOW = 13600;
	private int maxPower;

	public int getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(int maxPower) {
		this.maxPower = maxPower;
	}

	public EnumItem[] getMaxPowers() {
		return MgrUtil.enumItems("enum.poe.max.power.", new int[] {
				NO_MAX_POWER, MAX_POWER_802_3AF, MAX_POWER_HIGH,
				MAX_POWER_MEDIUM_HIGH, MAX_POWER_MEDIUM, MAX_POWER_MEDIUM_LOW, MAX_POWER_LOW });
	}

	//Netdump parameters
	private boolean enableNetdump;
	private String netdumpServer = "";
	private String netdumpVlan = "";
	private String netdumpNVlan = "";
	private String netdumpDevice = "";
	private String netdumpGateway = "";
	private String ipMode = "1";
	//Specified transport port Settings for Switch
	private String transportEth;
	private int    transportY=1;
	private boolean showPort;
	private int portRange=104;

	public boolean isEnableNetdump(){
		return this.enableNetdump;
	}

	public void setEnableNetdump(boolean enableNetdump){
		this.enableNetdump = enableNetdump;
	}

	public String getNetdumpServer(){
		return this.netdumpServer;
	}

	public void setNetdumpServer(String netdumpServer){
		this.netdumpServer = netdumpServer;
	}

	public String getNetdumpVlan() {
		return netdumpVlan;
	}

	public void setNetdumpVlan(String netdumpVlan) {
		this.netdumpVlan = netdumpVlan;
	}

	public String getNetdumpNVlan() {
		return netdumpNVlan;
	}

	public void setNetdumpNVlan(String netdumpNVlan) {
		this.netdumpNVlan = netdumpNVlan;
	}

	public String getNetdumpDevice() {
		return netdumpDevice;
	}

	public void setNetdumpDevice(String netdumpDevice) {
		this.netdumpDevice = netdumpDevice;
	}

	public String getNetdumpGateway() {
		return netdumpGateway;
	}

	public void setNetdumpGateway(String netdumpGateway) {
		this.netdumpGateway = netdumpGateway;
	}

	public String getIpMode() {
		return ipMode;
	}

	public void setIpMode(String ipMode) {
		this.ipMode = ipMode;
	}

	public String getTransportEth() {
		return transportEth;
	}

	public void setTransportEth(String transportEth) {
		this.transportEth = transportEth;
	}

	public int getTransportY() {
		return transportY;
	}

	public void setTransportY(int transportY) {
		this.transportY = transportY;
	}

	public boolean isShowPort() {
		return showPort;
	}

	public String getManagerPortStr() {
		if(!StringUtils.isBlank(transportEth)){
			return transportEth+"/"+transportY;
		}
		return "";
	}

	public List<CheckItem> getPortList(){
		List<CheckItem> portList = new ArrayList<>();
		for(long i=1;i<=portRange;i++){
			CheckItem item=new CheckItem(i,String.valueOf(i));
			portList.add(item);
		}
		return portList;
	}

	public static boolean isSupportPort(HiveAp hiveAp) {
		if(null==hiveAp){
			return false;
		}
		String version="6.1.2.0";
		String softVer = hiveAp.getSoftVer();
		return softVer != null && HiveAp.isSwitchProduct(hiveAp.getHiveApModel())
				&& NmsUtil.compareSoftwareVersion(version, softVer) <= 0;
	}


	// Capwap settings parameters
	private String capwapServer = "";
	private String capwapServerBackup = "";
	private String vhmName ;
	private int udpPort = 12222;
	private short timeOut = 30;
	private short deadInterval = 105;
	private String newPassPhrase = "";
	private String confirmPassPhrase="";
	private boolean enabledDTLS;
	private boolean defaultPassPhrase;

	// Country Code parameters
	private int countryCode;
	private int countryCode_offSet = 5;

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public int getCountryCode_offSet() {
		return countryCode_offSet;
	}

	public void setCountryCode_offSet(int countryCode_offSet) {
		this.countryCode_offSet = countryCode_offSet;
	}

	public String getCapwapServer() {
		return capwapServer;
	}

	public void setCapwapServer(String capwapServer) {
		this.capwapServer = capwapServer;
	}

	public String getCapwapServerBackup() {
		return capwapServerBackup;
	}

	public void setCapwapServerBackup(String capwapServerBackup) {
		this.capwapServerBackup = capwapServerBackup;
	}

	public String getVhmName() {
		if(vhmName == null || "".equals(vhmName)){
			List<?> resList = QueryUtil.executeQuery("select domainName from "+HmDomain.class.getSimpleName(), null,
					new FilterParams("id", this.getDomainId()));
			if(resList != null && !resList.isEmpty()){
				return resList.get(0).toString();
			}
		}
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public short getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(short timeOut) {
		this.timeOut = timeOut;
	}

	public short getDeadInterval() {
		return deadInterval;
	}

	public void setDeadInterval(short deadInterval) {
		this.deadInterval = deadInterval;
	}

	public String getNewPassPhrase() {
		return newPassPhrase;
	}

	public void setNewPassPhrase(String newPassPhrase) {
		this.newPassPhrase = newPassPhrase;
	}

	public String getConfirmPassPhrase() {
		return confirmPassPhrase;
	}

	public void setConfirmPassPhrase(String confirmPassPhrase) {
		this.confirmPassPhrase = confirmPassPhrase;
	}

	public boolean isEnabledDTLS() {
		return enabledDTLS;
	}

	public void setEnabledDTLS(boolean enabledDTLS) {
		this.enabledDTLS = enabledDTLS;
	}

	public boolean isDefaultPassPhrase() {
		return defaultPassPhrase;
	}

	public void setDefaultPassPhrase(boolean defaultPassPhrase) {
		this.defaultPassPhrase = defaultPassPhrase;
	}

	public String getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(String selectedImage) {
		this.selectedImage = selectedImage;
	}

	public String getSelectedVersion() {
		return selectedVersion;
	}

	public void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

	public void setLatestVersion(String latestVersion) {
		this.latestVersion = latestVersion;
	}

	public String getBootstrapAdmin() {
		return bootstrapAdmin;
	}

	public void setBootstrapAdmin(String bootstrapAdmin) {
		this.bootstrapAdmin = bootstrapAdmin;
	}

	public String getBootstrapPassword() {
		return bootstrapPassword;
	}

	public void setBootstrapPassword(String bootstrapPassword) {
		this.bootstrapPassword = bootstrapPassword;
	}

	public boolean isSimpleUpdate() {
		return simpleUpdate;
	}

	public void setSimpleUpdate(boolean simpleUpdate) {
		this.simpleUpdate = simpleUpdate;
	}

	public String getDtlsStyle() {
		if (enabledDTLS) {
			return "false";
		} else {
			return "true";
		}
	}

	public String getSignatureSelTypeVerStyle() {
		if (ImageSelectionType.softVer.equals(getDataSource()
				.getSignatureSelectType())) {
			return "";
		}
		return "none";
	}

	public String getSignatureSelTypeFileStyle() {
		if (ImageSelectionType.imgName.equals(getDataSource()
				.getSignatureSelectType())) {
			return "";
		}
		return "none";
	}

	public String getOtherSignatureVersionDisabled() {
		if (VER_SELECT_LATEST.equals(signatureVersionSelectType)) {
			return "true";
		}
		return "false";
	}

	public String getImageActiveAtDisabled() {
		if (null != getDataSource()) {
			if (getDataSource().getImageActivateType() == ActivateType.activateAtTime) {
				return "false";
			}
		}
		return "true";
	}

	public String getImageActiveAfterDisabled() {
		if (null != getDataSource()) {
			if (getDataSource().getImageActivateType() == ActivateType.activateAfterTime) {
				return "false";
			}
		}
		return "true";
	}

	public String getImageSelTypeVerStyle() {
		if (null != getDataSource()) {
			if (ImageSelectionType.softVer.equals(getDataSource()
					.getImageSelectType())) {
				return "";
			}
		}
		return "none";
	}

	public String getImageSelTypeFileStyle() {
		if (null != getDataSource()) {
			if (ImageSelectionType.imgName.equals(getDataSource()
					.getImageSelectType())) {
				return "";
			}
		}
		return "none";
	}

	public String getOtherImageVersionDisabled() {
		if (VER_SELECT_LATEST.equals(imageVersionSelectType)) {
			return "true";
		}
		return "false";
	}

	public String getDistributedUpgradesStyle() {
		if(this.isDsEnable()){
			return "none";
		}
		if (null != getDataSource()){
			if(getDataSource().isDistributedUpgrades()){
				return "";
			}
		}
		return "none";
	}

	public String getConfigActiveAtDisabled() {
		if (null != getDataSource()) {
			if (getDataSource().getConfigActivateType() == ActivateType.activateAtTime) {
				return "false";
			}
		}
		return "true";
	}

	public String getConfigActiveAfterDisabled() {
		if (null != getDataSource()) {
			if (getDataSource().getConfigActivateType() == ActivateType.activateAfterTime) {
				return "false";
			}
		}
		return "true";
	}

	public String getActivateTimeStyle() {
		if (null != getDataSource()) {
			if (getDataSource().getConfigSelectType() == ConfigSelectType.deltaConfig
					|| getDataSource().getConfigSelectType() == ConfigSelectType.deltaRunning) {
				return "none";
			}
		}
		return "";
	}

	public String getActiveTimeLabelStyle() {
		if (null != getDataSource()) {
			if (getDataSource().getConfigSelectType() == ConfigSelectType.deltaConfig
					|| getDataSource().getConfigSelectType() == ConfigSelectType.deltaRunning) {
				return "hidden";
			}
		}
		return "visible";
	}

	private String str_countryCodeOffset;

	protected boolean updateCountryCodeParams() throws Exception {
		str_countryCodeOffset = NmsUtil.getCLIFormatString(countryCode_offSet
				+ BeTopoModuleParameters.DEFAULT_REBOOT_DELAY);
		return true;
	}

	protected boolean addToUploadList() throws Exception {
		// check license first!
		int newHiveApCount = 0, newSimHiveApCount = 0, newCvgCount = 0;
		if (null != updateHiveAps) {
			for (UpdateHiveAp hiveAp : updateHiveAps) {
				if (hiveAp.getHiveAp().getManageStatus() == HiveAp.STATUS_NEW) {
					if (hiveAp.getHiveAp().isSimulated()) {
						newSimHiveApCount++;
					} else {
						if (hiveAp.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA) {
							newCvgCount++;
						} else {
							newHiveApCount++;
						}
					}
				}
			}
		}
		if (newHiveApCount > 0) {
			String error = BeTopoModuleUtil.isDomainAllowManageRealHiveAP(
					domainId, newHiveApCount, false);
			if (null != error && !"".equals(error.trim())) {
				addActionPermanentErrorMsg(error);
				return false;
			}
		}
		if (newCvgCount > 0) {
			String error = BeTopoModuleUtil.isDomainAllowManageRealHiveAP(
					domainId, newCvgCount, true);
			if (null != error && !"".equals(error.trim())) {
				addActionPermanentErrorMsg(error);
				return false;
			}
		}
		if (newSimHiveApCount > 0) {
			String error = BeTopoModuleUtil.isDomainAllowManageSimHiveAP(
					domainId, newSimHiveApCount);
			if (null != error && !"".equals(error.trim())) {
				addActionPermanentErrorMsg(error);
				return false;
			}
		}
		if ("uploadImage".equals(operation)) {
			// try image distributor first!
			if (null != updateHiveAps && !updateHiveAps.isEmpty()) {
				HiveApUpdateSettings setting = getUpdateSetting();
				boolean isSelectVer = ImageSelectionType.softVer.equals(setting.getImageSelectType());
				String version;
				if (VER_SELECT_LATEST.equals(imageVersionSelectType)) {
					version = this.latestVersion;
				} else {
					version = this.selectedVersion;
				}
				if (setting.isDistributedUpgrades() || MgrUtil.isEnableDownloadServer()) {
					try {
						Collection<ImageUploadGroup> resGoups = HmBeConfigUtil.getImageDistributor().generateUploadGroup(
								updateHiveAps, getSelectedImage(), isSelectVer, version, setting);
						Iterator<ImageUploadGroup> iteGoups = resGoups.iterator();
						updateHiveAps.clear();
						while(iteGoups.hasNext()){
							ImageUploadGroup group = iteGoups.next();
							if(group.getUpdateList().size() > 1){
								group.countDistributedServer();
								HmBeConfigUtil.getImageDistributor().addImageRequest(group);
							}else if (group.getUpdateList().size() == 1){
								updateHiveAps.add(group.getUpdateList().get(0));
							}
						}
					} catch (UpdateObjectException ue) {
						log.error("addToUploadList",
								"Try to upload image distributor failed, try to use old way. Reason:"
										+ ue.getMessage());
					} catch (Exception e) {
						log
								.error(
										"addToUploadList",
										"Try to upload image distributor failed, try to use old way.",
										e);
					}
				} else {
					log.info("addToUploadList",
							"distributed turned off, try to use old way.");
				}
			}
		}
		List<String[]> errList = HmBeConfigUtil.getUpdateManager().addUpdateObjects(
				updateHiveAps);
		if (null != errList && errList.size() > 0) {
			for (String[] errInfo : errList) {
				addActionPermanentErrorMsg(errInfo[1]);
			}
			return false;
		} else {
			return true;
		}
	}

	public List<TextItem> getAvailableImageFiles() {
		List<Short> disApModels = getDistinctApModel();
		if(MgrUtil.isEnableDownloadServer()){
			List<String> images = getAllImageFromDS(disApModels, false);
			List<TextItem> imageItems = new ArrayList<>(images.size());
			for(String image : images){
				imageItems.add(new TextItem(image, image));
			}
			return imageItems;
		}else{
			List<HiveApImageInfo> imageInfoList = com.ah.be.config.image.ImageManager.getImageList(disApModels);
			List<TextItem> imageFiles = new ArrayList<TextItem>();
			if(imageInfoList != null){
				for(HiveApImageInfo imgInfo : imageInfoList){
					imageFiles.add(new TextItem(imgInfo.getImageName(), imgInfo
							.getImageVersion() + " - " + imgInfo.getImageName()));
				}
			}
			return imageFiles;
		}
	}
	
	private List<Short> getDistinctApModel(){
		Set<Long> init_ids = getIdListFromSession(UPDATE_IMAGE_SELECTED_IDs);
		FilterParams params = null;
		if(init_ids != null && !init_ids.isEmpty()){
			params = new FilterParams("id in (:s1)", new Object[]{init_ids});
		}
		List<?> results = QueryUtil.executeQuery("select distinct hiveApModel from "+HiveAp.class.getSimpleName(), null, params);
		List<Short> allModel = new ArrayList<Short>();
		if(results != null && !results.isEmpty()){
			for(Object obj : results){
				allModel.add((Short)obj);
			}
		}
		return allModel;
	}

	public List<String> getAvailableImageVersions() {
		List<Short> disApModels = getDistinctApModel();
		Collection<String> versions = null;
		if(MgrUtil.isEnableDownloadServer()){
			versions = getAllImageFromDS(disApModels, true);
		}else{
			List<HiveApImageInfo> imageInfoList = com.ah.be.config.image.ImageManager.getImageList(disApModels);
			versions = new HashSet<String>();
			if(imageInfoList != null){
				for (HiveApImageInfo image : imageInfoList) {
					versions.add(image.getImageVersion().trim());
				}
			}
			if(versions.isEmpty()){
				versions.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
			}
		}
		
		List<String> list = new ArrayList<String>(versions);
		Collections.sort(list,new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					return o2.hashCode()-o1.hashCode();
				} catch (Exception e) {
					return 0;
				}
			}
		});
		return list;
	}

	public String getLatestImageVersion() {
		return getAvailableImageVersions().get(0);
	}

	public List<String> getAvailableSignatureFiles() {
		SortParams sp = new SortParams("fileName");
		sp.setPrimaryOrderBy("ahVersion");
		sp.setPrimaryAscending(false);
		List<String> items = new ArrayList<>();
		List<?> list = QueryUtil.executeQuery("select fileName from "
				+ LSevenSignatures.class.getCanonicalName(), sp, null);
		for(Object object : list){
			items.add((String)object);
		}
		if(list.isEmpty()){
			items.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		return items;
	}

	public List<TextItem> getAvailableSignatureVersions() {
		List<TextItem> items = new ArrayList<>();
		List<?> list = QueryUtil.executeQuery(
				"select distinct ahVersion, dateReleased from "
						+ LSevenSignatures.class.getCanonicalName(),
				new SortParams("ahVersion", false), null);
		for(Object object : list){
			Object[] objects = (Object[]) object;
			String version = (String) objects[0];
			String date = LSevenSignatures
					.getDateReleasedString((String) objects[1]);
			items.add(new TextItem(version, version + " (" + date + ")"));
		}
		if(list.isEmpty()){
			String none = MgrUtil.getUserMessage("config.optionsTransfer.none");
			items.add(new TextItem(none, none));
		}
		return items;
	}

	public TextItem getLatestSignatureVersion() {
		return getAvailableSignatureVersions().get(0);
	}
	
	public List<Entry<Integer, String>> getCountryCodeValues() {
		return CountryCode.getCountryCodeList();
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(HiveAp.class);
		prepareDataSource();
		// setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		String listTypeFromSession = (String) MgrUtil
				.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
		String viewType = (String) MgrUtil
				.getSessionAttribute(HiveApAction.MANAGED_LIST_VIEW);
		if ("managedVPNGateways".equals(listTypeFromSession)) {
			if ("config".equals(viewType)) {
				setSelectedL2Feature(L2_FEATURE_CONFIG_VPN_GATEWAYS);
			} else {
				setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
			}
		} else if ("managedRouters".equals(listTypeFromSession)) {
			if ("config".equals(viewType)) {
				setSelectedL2Feature(L2_FEATURE_CONFIG_BRANCH_ROUTERS);
			} else {
				setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
			}
		} else if ("managedSwitches".equals(listTypeFromSession)) {
			if ("config".equals(viewType)) {
				setSelectedL2Feature(L2_FEATURE_CONFIG_SWITCHES);
			} else {
				setSelectedL2Feature(L2_FEATURE_SWITCHES);
			}
		} else if ("managedDeviceAPs".equals(listTypeFromSession)) {
			if ("config".equals(viewType)) {
				setSelectedL2Feature(L2_FEATURE_CONFIG_DEVICE_HIVEAPS);
			} else {
				setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
			}
		} else if ("managedHiveAps".equals(listTypeFromSession)) {
			if ("config".equals(viewType)) {
				setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
			} else {
				setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
			}
		} else {
			if ("config".equals(viewType)) {
				setSelectedL2Feature(L2_FEATURE_CONFIG_HIVE_APS);
			} else {
				setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
			}
		}
	}

	private void prepareDataSource() {
		setSessionDataSource(getUpdateSetting());
	}

	private HiveApUpdateSettings getUpdateSetting() {
		List<HiveApUpdateSettings> list = QueryUtil.executeQuery(
				HiveApUpdateSettings.class, null, null, domainId);
		if (list.isEmpty()) {
			return new HiveApUpdateSettings();
		} else {
			return list.get(0);
		}
	}

	@Override
	public HiveApUpdateSettings getDataSource() {
		if (dataSource != null && dataSource instanceof HiveApUpdateSettings) {
			return (HiveApUpdateSettings) dataSource;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Set<Long> getIdListFromSession(String sessionKey) {
		return (Set<Long>) MgrUtil.getSessionAttribute(sessionKey);
	}

	private String getStringSessionAttribute(String sessionKey) {
		return (String) MgrUtil.getSessionAttribute(sessionKey);
	}

	private void storeIdListToSession(String sessionKey, Set<Long> newIds,
			Set<Long> pageIds) {
		if (null != pageIds) {
			Set<Long> previousIds = getIdListFromSession(sessionKey);
			if (null != previousIds) {
				previousIds.removeAll(pageIds);
			}
		}
		if (null != newIds) {
			Set<Long> previousIds = getIdListFromSession(sessionKey);
			if (null != previousIds) {
				previousIds.addAll(newIds);
			} else {
				MgrUtil.setSessionAttribute(sessionKey, newIds);
			}
		}
	}

	private void storeExIdListToSession(String sessionKey,  Set<Long> newIds){
		if (null != newIds){
			Set<Long> previousIds = getIdListFromSession(sessionKey);
			if(null != previousIds){
				previousIds.addAll(newIds);
			}else{
				MgrUtil.setSessionAttribute(sessionKey, newIds);
			}
		}
	}

	public boolean isTftpBtnEnabled() {
		try {
			if (HmBeAdminUtil.isTftpEnable()) {
				return true;
			}
		} catch (Exception e) {
			log.error("getTftpBtnDisabled", e.getMessage());
		}
		return false;
	}

	public boolean isNeedShowEnableLabel() {
		return !isTftpBtnEnabled() && getIsInHomeDomain();
	}

	public boolean isUsingTftpTransfer() {
		return null != getDataSource()
				&& TransferType.tftp.equals(getDataSource().getImageTransfer());
	}

	public List<CheckItem> getDistributedServers(){
		return getDistributedServerList(this.getCurrentHiveName());
	}

	@SuppressWarnings("unchecked")
	private List<CheckItem> getDistributedServerList(String hiveName){
		Map<String, List<Long>> disServerMap = (Map<String, List<Long>>)MgrUtil.getSessionAttribute(UPDATE_DISTRIBUTED_SERVER_LIST_GROUP_BY_HIVE);
		Map<Long, String> idHostMapping = (Map<Long, String>)MgrUtil.getSessionAttribute(UPDATE_ID_AND_HOSTNAME_MAPPING);
		List<Long> disServerList = disServerMap.get(hiveName);

		List<CheckItem> list = new ArrayList<>();
		if(disServerList != null){
			for(Long id : disServerList){
				list.add(new CheckItem(id, idHostMapping.get(id)));
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public String getDistributedServerHost(){
		Map<Long, String> idHostMapping = (Map<Long, String>)MgrUtil.getSessionAttribute(UPDATE_ID_AND_HOSTNAME_MAPPING);
		if(getDistributedServer() != null){
			return idHostMapping.get(getDistributedServer());
		}else{
			return "None";
		}
	}

	@SuppressWarnings("unchecked")
	public String getCurrentHiveName(){
		Map<Long, String> idHiveMapping = (Map<Long, String>)MgrUtil.getSessionAttribute(UPDATE_ID_AND_HIVENAME_MAPPING);
		return idHiveMapping.get(getDistributedServer());
	}

	public Long getDistributedServer(){
		return (Long)MgrUtil.getSessionAttribute(UPDATE_CURRENT_DISTRIBUTED_SERVER);
	}

	public void setDistributedServer(Long distributedServer){
		MgrUtil.setSessionAttribute(UPDATE_CURRENT_DISTRIBUTED_SERVER, distributedServer);
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp) bo;
			if (hiveAp.getConfigTemplate() != null) {
				hiveAp.getConfigTemplate().getId();
				if(hiveAp.getConfigTemplate() != null){
					loadRadiusOnHiveap(hiveAp.getConfigTemplate().getRadiusServerProfile());
				}
				if (hiveAp.getConfigTemplate().getFwPolicy()!=null) {
					hiveAp.getConfigTemplate().getFwPolicy().getId();
					if (hiveAp.getConfigTemplate().getFwPolicy().getRules()!=null) {
						hiveAp.getConfigTemplate().getFwPolicy().getRules().size();
					}
				}
				if (hiveAp.getEth0UserProfile() != null) {
					hiveAp.getEth0UserProfile().getId();
				}
				if (hiveAp.getEth1UserProfile() != null) {
					hiveAp.getEth1UserProfile().getId();
				}
				if (hiveAp.getAgg0UserProfile() != null) {
					hiveAp.getAgg0UserProfile().getId();
				}
				if (hiveAp.getRed0UserProfile() != null) {
					hiveAp.getRed0UserProfile().getId();
				}

				if (hiveAp.getWifi0RadioProfile() !=null) {
					hiveAp.getWifi0RadioProfile().getId();
				}
				if (hiveAp.getWifi1RadioProfile() !=null) {
					hiveAp.getWifi1RadioProfile().getId();
				}
				if(hiveAp.getPppoeAuthProfile() != null) {
					hiveAp.getPppoeAuthProfile().getId();
				}
				if (hiveAp.getEthCwpDefaultAuthUserProfile() != null) {
					hiveAp.getEthCwpDefaultAuthUserProfile().getId();
				}
				if (hiveAp.getEthCwpDefaultRegUserProfile() != null) {
					hiveAp.getEthCwpDefaultRegUserProfile().getId();
				}
				if (hiveAp.getEthCwpRadiusUserProfiles() != null) {
					hiveAp.getEthCwpRadiusUserProfiles().size();
				}

				Collection<ConfigTemplateSsid> cfSsids = hiveAp
						.getConfigTemplate().getSsidInterfaces().values();
				for (ConfigTemplateSsid cfSsid : cfSsids) {
					SsidProfile ssid = cfSsid.getSsidProfile();
					if (null != ssid && null != ssid.getCwp()
							&& null != ssid.getCwp().getCertificate()) {
						ssid.getCwp().getCertificate().getId();
					}
					if (null != ssid && null != ssid.getUserPolicy()
							&& null != ssid.getUserPolicy().getCertificate()) {
						ssid.getUserPolicy().getCertificate().getId();
					}
					if (null != ssid && null != ssid.getPpskECwp()
							&& null != ssid.getPpskECwp().getCertificate()) {
						ssid.getPpskECwp().getCertificate().getId();
					}
					if (null != ssid && null != ssid.getWpaECwp()
							&& null != ssid.getWpaECwp().getCertificate()) {
						ssid.getWpaECwp().getCertificate().getId();
					}
				}

				Collection<PortGroupProfile> ports = hiveAp.getConfigTemplate().getPortProfiles();
				if(ports != null) {
					for(PortGroupProfile pgProfile : ports){
						if(pgProfile.getBasicProfiles() == null){
							continue;
						}
						for (PortBasicProfile base : pgProfile.getBasicProfiles()){
							if(base.getAccessProfile() != null
									&& base.getAccessProfile().getCwp() != null
									&& base.getAccessProfile().getCwp().getCertificate() != null){
								base.getAccessProfile().getCwp().getCertificate().getId();
							}
						}
					}
				}

				if (hiveAp.getConfigTemplate().getVpnService() != null) {
					hiveAp.getConfigTemplate().getVpnService().getId();
					if (hiveAp.getConfigTemplate().getVpnService().getVpnGateWaysSetting()!=null) {
						hiveAp.getConfigTemplate().getVpnService().getVpnGateWaysSetting().size();
					}
				}

				if (hiveAp.getConfigTemplate().getSwitchSettings() != null){
					if (hiveAp.getConfigTemplate().getSwitchSettings().getStpSettings() != null){
						hiveAp.getConfigTemplate().getSwitchSettings().getStpSettings().getId();
					}
				}
			}

			loadRadiusOnHiveap(hiveAp.getRadiusServerProfile());

			Cwp ethCwp = hiveAp.getEthCwpCwpProfile();
			if (ethCwp != null) {
				ethCwp.getId();
				if (null != ethCwp.getCertificate()) {
					ethCwp.getCertificate().getId();
				}
			}

			if (hiveAp.getDeviceInterfaces()!=null) {
				hiveAp.getDeviceInterfaces().values();
			}

			if (hiveAp.getDeviceStpSettings() != null){
				hiveAp.getDeviceStpSettings().getId();
			}
		}

		if (bo instanceof ConfigTemplate) {
			ConfigTemplate wlanPolicy = (ConfigTemplate)bo;

			if (wlanPolicy.getHiveProfile() != null)
				wlanPolicy.getHiveProfile().getId();
			if (wlanPolicy.getMgmtServiceDns() != null)
				wlanPolicy.getMgmtServiceDns().getId();
			if (wlanPolicy.getMgmtServiceOption() != null)
				wlanPolicy.getMgmtServiceOption().getId();
			if (wlanPolicy.getVlan() != null)
				wlanPolicy.getVlan().getId();

			if (wlanPolicy.getVpnService() != null){
				wlanPolicy.getVpnService().getId();
				if (wlanPolicy.getVpnService().getVpnGateWaysSetting()!=null) {
					wlanPolicy.getVpnService().getVpnGateWaysSetting().size();
				}
			}

			if (wlanPolicy.getVlanNetwork()!=null) {
				wlanPolicy.getVlanNetwork().size();
				for(ConfigTemplateVlanNetwork cvn: wlanPolicy.getVlanNetwork()){
					if (cvn.getNetworkObj()!=null) {
						cvn.getNetworkObj().getId();
						if (cvn.getNetworkObj().getSubItems()!=null) {
							cvn.getNetworkObj().getSubItems().size();
						}
						if (cvn.getNetworkObj().getPortForwardings()!=null) {
							cvn.getNetworkObj().getPortForwardings().size();
						}
						if (cvn.getNetworkObj().getVpnDnsService()!=null) {
							cvn.getNetworkObj().getVpnDnsService().getId();
						}
					}
					if (cvn.getVlan()!=null) {
						cvn.getVlan().getId();
						if (cvn.getVlan().getItems()!=null) {
							cvn.getVlan().getItems().size();
						}
					}
				}
			}
			if (wlanPolicy.getUpVlanMapping() != null) {
				wlanPolicy.getUpVlanMapping().size();
				for (UserProfileVlanMapping mapping : wlanPolicy.getUpVlanMapping()) {
					if (mapping.getUserProfile() != null) {
						mapping.getUserProfile().getId();
					}
					if (mapping.getVlan() != null) {
						mapping.getVlan().getId();
					}
				}
			}

			if (wlanPolicy.getVlanNetwork() != null) {
				wlanPolicy.getVlanNetwork().size();
			}

			for (ConfigTemplateSsid tmpTemplate : wlanPolicy.getSsidInterfaces().values()) {
				if (tmpTemplate.getSsidProfile() != null) {
					if (tmpTemplate.getSsidProfile().getRadiusUserProfile() != null) {
						tmpTemplate.getSsidProfile().getRadiusUserProfile().size();
                        for (UserProfile up : tmpTemplate.getSsidProfile().getRadiusUserProfile()) {
                            up.getId();
                        }
                    }
					if (null != tmpTemplate.getSsidProfile().getLocalUserGroups()) {
						tmpTemplate.getSsidProfile().getLocalUserGroups().size();
					}
					if (null != tmpTemplate.getSsidProfile().getRadiusUserGroups()) {
						tmpTemplate.getSsidProfile().getRadiusUserGroups().size();
					}
					if (null != tmpTemplate.getSsidProfile().getMacFilters()) {
						tmpTemplate.getSsidProfile().getMacFilters().size();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileDefault() != null) {
						tmpTemplate.getSsidProfile().getUserProfileDefault().getId();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileSelfReg() != null) {
						tmpTemplate.getSsidProfile().getUserProfileSelfReg().getId();
					}
					if (tmpTemplate.getSsidProfile().getUserProfileGuest() != null) {
					    tmpTemplate.getSsidProfile().getUserProfileGuest().getId();
					}
				}
			}

            for (PortAccessProfile pap : wlanPolicy.getAccessProfiles()) {
                if (pap == null)
                    continue;
                if (pap.getVoiceVlan() != null) {
                    pap.getVoiceVlan();
                }
                if (pap.getDataVlan() != null) {
                    pap.getDataVlan();
                }
                if (pap.getDefUserProfile() != null) {
                    pap.getDefUserProfile().getVlan();
                }
                if (pap.getSelfRegUserProfile() != null) {
                    pap.getSelfRegUserProfile().getVlan();
                }
                if (pap.getGuestUserProfile() != null) {
                    pap.getGuestUserProfile().getVlan();
                }
                if (pap.getAuthOkDataUserProfile() != null) {
                    for (UserProfile up : pap.getAuthOkDataUserProfile())
                        if (up != null)
                            up.getVlan();
                }
                if (pap.getAuthOkUserProfile() != null) {
                    for (UserProfile up : pap.getAuthOkUserProfile())
                        if (up != null)
                            up.getVlan();
                }
                if (pap.getAuthFailUserProfile() != null) {
                    for (UserProfile up : pap.getAuthFailUserProfile())
                        if (up != null)
                            up.getVlan();
                }
            }


            if(null != wlanPolicy.getSwitchSettings()){
				wlanPolicy.getSwitchSettings().getId();
			}

			if(null != wlanPolicy.getLldpCdp()){
				wlanPolicy.getLldpCdp().getId();
			}

			// load LAN profiles
			if (null != wlanPolicy.getPortProfiles()) {
				wlanPolicy.getPortProfiles().size();
				for(PortGroupProfile gp: wlanPolicy.getPortProfiles()){
					if (gp.getBasicProfiles()!=null){
						gp.getBasicProfiles().size();
						for(PortBasicProfile pbp: gp.getBasicProfiles()) {
							if(pbp.getAccessProfile()!=null) {
								pbp.getAccessProfile().getId();
								if(null != pbp.getAccessProfile().getCwp()){
									pbp.getAccessProfile().getCwp().getId();
								}
								if(null != pbp.getAccessProfile().getNativeVlan()){
									pbp.getAccessProfile().getNativeVlan().getId();
								}
								if(null != pbp.getAccessProfile().getVoiceVlan()){
								    pbp.getAccessProfile().getVoiceVlan().getId();
								}
								if(null != pbp.getAccessProfile().getDataVlan()){
								    pbp.getAccessProfile().getDataVlan().getId();
								}
								if(null != pbp.getAccessProfile().getRadiusAssignment()){
									pbp.getAccessProfile().getRadiusAssignment().getId();
								}
								if(null != pbp.getAccessProfile().getDefUserProfile()){
									pbp.getAccessProfile().getDefUserProfile().getId();
								}
								if(null != pbp.getAccessProfile().getSelfRegUserProfile()){
									pbp.getAccessProfile().getSelfRegUserProfile().getId();
								}
								if(null != pbp.getAccessProfile().getGuestUserProfile()){
								    pbp.getAccessProfile().getGuestUserProfile().getId();
								}
								if (null != pbp.getAccessProfile().getAuthOkUserProfile()) {
									pbp.getAccessProfile().getAuthOkUserProfile().size();
								}
								if (null != pbp.getAccessProfile().getAuthFailUserProfile()) {
									pbp.getAccessProfile().getAuthFailUserProfile().size();
								}
								if (null != pbp.getAccessProfile().getAuthOkDataUserProfile()) {
									pbp.getAccessProfile().getAuthOkDataUserProfile().size();
								}
								if (null != pbp.getAccessProfile().getRadiusUserGroups()) {
									pbp.getAccessProfile().getRadiusUserGroups().size();
								}
							}
						}
					}
				}
			}

			if (wlanPolicy.getSwitchSettings() != null){
				if(wlanPolicy.getSwitchSettings().getStpSettings() != null){
					wlanPolicy.getSwitchSettings().getStpSettings().getId();
				}
			}
		}

		if (bo instanceof UserProfile) {
			UserProfile userp = (UserProfile) bo;
			if (userp.getVlan() != null)
				userp.getVlan().getId();
			if (userp.getQosRateControl() != null)
				userp.getQosRateControl().getId();
			if (userp.getIpPolicyFrom() != null)
				userp.getIpPolicyFrom().getId();
			if (userp.getIpPolicyTo() != null)
				userp.getIpPolicyTo().getId();
			if (userp.getMacPolicyFrom() != null)
				userp.getMacPolicyFrom().getId();
			if (userp.getMacPolicyTo() != null)
				userp.getMacPolicyTo().getId();
			if (userp.getUserProfileAttribute() != null){
				userp.getUserProfileAttribute().getId();
				if (userp.getUserProfileAttribute().getItems()!=null) {
					userp.getUserProfileAttribute().getItems().size();
				}
			}
			if (null != userp.getAssignRules()) {
				userp.getAssignRules().size();
			}
		}

		if (bo instanceof MacFilter) {
			MacFilter macFilter = (MacFilter) bo;
			macFilter.getFilterInfo().size();
		}
		if (bo instanceof Vlan) {
			Vlan profile = (Vlan) bo;
			if (profile.getItems()!=null) {
				profile.getItems().size();
			}
		}
		if (bo instanceof VpnNetwork) {
			VpnNetwork profile = (VpnNetwork) bo;
			if (profile.getSubItems()!=null) {
				profile.getSubItems().size();
			}
			if (profile.getPortForwardings()!=null) {
				profile.getPortForwardings().size();
			}
			if (profile.getVpnDnsService()!=null) {
				profile.getVpnDnsService().getId();
			}
		}
		return null;
	}

	private void loadRadiusOnHiveap(RadiusOnHiveap radiusObj){
		if(radiusObj == null){
			return;
		}
		radiusObj.getId();
		if (radiusObj.getDirectoryOrLdap() != null) {
			radiusObj.getDirectoryOrLdap().size();
		}
		if (radiusObj.getLdapOuUserProfiles()!=null) {
			radiusObj.getLdapOuUserProfiles().size();
		}
	}

//	private JSONObject getUploadWaitingMessage() throws JSONException{
//		JSONObject result = new JSONObject();
//		String waitingMsg;
//		String resultMsg = null;
//		if(HmBeConfigUtil.getUpdateManager().getWaitingQueueSize() > 0){
//			waitingMsg = this.getText("info.hiveAp.update.image.howManyApWaiting",
//					new String[]{String.valueOf(HmBeConfigUtil.getUpdateManager().getWaitingQueueSize())});
//			resultMsg = "<html><body>"+waitingMsg+"</body></html>";
//
//			if(this.getDataSource().getImageActivateType() == ActivateType.activateAfterTime ||
//					this.getDataSource().getImageActivateType() == ActivateType.activateAtTime){
//				String rebootMsg = this.getText("info.hiveAp.update.image.manualReboot");
//
//				if(waitingMsg != null && !"".equals(waitingMsg)){
//					resultMsg = "<html><body>"+waitingMsg+"<br/><br/>"+rebootMsg+"</body></html>";
//				}else{
//					resultMsg = "<html><body>"+rebootMsg+"</body></html>";
//				}
//			}
//		}
//		result.put("message", resultMsg);
//
//		return result;
//	}

//		return result;
//	}

//	private boolean checkUploadQueue(){
//		if(this.isHMOnline() && HmBeConfigUtil.getUpdateManager().isExistsHiveApUploadImage(this.domainId)){
//			String errorMsg = MgrUtil .getUserMessage("error.hiveAp.update.image.inQueue");
//			addActionPermanentErrorMsg(errorMsg);
//			return false;
//		}else{
//			return true;
//		}
//	}

	public int getMaxUploadNum(){
		if(!this.isHMOnline()){
			return Integer.MAX_VALUE;
		}
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null,
				new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN));
		if(list.isEmpty()){
			return HMServicesSettings.MAX_HIVEOS_SOFTVER_UPDATE_NUM;
		}else{
			return list.get(0).getMaxUpdateNum();
		}
	}

	@SuppressWarnings("unchecked")
	public void checkSupportedUpdateImage() throws JSONException{
		List<HiveAp> selectedAps = page;
			List<HiveAp> unSupportAps = new ArrayList<>();
			for(HiveAp ap : selectedAps){
				Boolean enabled = AhConstantUtil.isTrueAll(Device.SUPPORTED_UPDATE_IMAGE , ap.getHiveApModel());
				if(!(null == enabled || enabled)){  // unsupport
					unSupportAps.add(ap);
				}
			}
			if(unSupportAps.size() > 0) {
				String hostNames = "" ;
				for(HiveAp uap :unSupportAps){
					hostNames  += uap.getHostName()+",";
				}
				unsupportedApsMessage =  MgrUtil.getUserMessage("error.config.supportedUpdateImage.status.off",hostNames.substring(0, hostNames.length()-1)) ;
				page.removeAll(unSupportAps); // don't show in next page
			}
	}

	public void checkAPVersion(Set<Long> initIds , List<HiveAp> selectedBos ,String version) throws JSONException{
		List<HiveAp> unSupportAps =  new ArrayList<>();
		for(HiveAp ap : selectedBos){
			// check ap version
			if(NmsUtil.compareSoftwareVersion(ap.getSoftVer(), version) < 0){  // is not 4.0r2 or high
				unSupportAps.add(ap);
				// update UPDATE_INITIAL_IDs
				initIds.remove(ap.getId());
			}
		}
		if(unSupportAps.size() > 0) {
			String hostNames = "" ;
			for(HiveAp uap :unSupportAps){
				hostNames  += uap.getHostName()+",";
			}
			unsupportedApsMessage +=  MgrUtil.getUserMessage("error.config.outdoor.version.unsupport",hostNames.substring(0, hostNames.length()-1)) ;
			selectedBos.removeAll(unSupportAps); // don't show in next page
		}
	}

	public void checkAPDeviceType(Set<Long> initIds , List<HiveAp> selectedBos ,short deviceType) throws JSONException{
		List<HiveAp> unSupportAps =  new ArrayList<>();
		for(HiveAp ap : selectedBos){
			// check ap deviceType
			if (ap.isCVGAppliance()) { // is  CVG
				unSupportAps.add(ap);
				// update UPDATE_INITIAL_IDs
				initIds.remove(ap.getId());
			}
		}
		if(unSupportAps.size() > 0) {
			String hostNames = "" ;
			for(HiveAp uap :unSupportAps){
				hostNames  += uap.getHostName()+",";
			}
			unsupportedApsMessage +=  MgrUtil.getUserMessage("error.config.netdump.devicetype.unsupport",hostNames.substring(0, hostNames.length()-1)) ;
			selectedBos.removeAll(unSupportAps); // don't show in next page
		}
	}

	/**
	 *
	 * @param initIds -
	 * @param supportPlatforms -
	 * @param supportVer -
	 */
	private void generateFilteredMessage(Set<Long> initIds,
			short[] supportPlatforms, String supportVer) {
		boolean hasUnsupportVer = false;
		boolean hasUnsupportPlat = false;
		String query = "select softVer, hiveApModel from "
				+ HiveAp.class.getCanonicalName();
		List<?> list = QueryUtil.executeQuery(query, null, new FilterParams(
				"id", initIds));
		Arrays.sort(supportPlatforms);
		for (Object object : list) {
			Object[] objects = (Object[]) object;
			String deviceVer = (String) objects[0];
			Short platform = (Short) objects[1];
			if (NmsUtil.compareSoftwareVersion(deviceVer, supportVer) < 0) {
				hasUnsupportVer = true;
				break;
			}
			if (null != platform) {
				int pos = Arrays.binarySearch(supportPlatforms,
						platform.shortValue());
				if (pos < 0) {
					hasUnsupportPlat = true;
					break;
				}
			}
		}
		if (hasUnsupportVer) {
			unsupportedApsMessage = MgrUtil.getUserMessage(
					"error.hiveAp.feature.discard.unsupport.version",
					supportVer);
		}
		if (hasUnsupportPlat) {
			addActionPermanentErrorMsg( MgrUtil.getUserMessage(
					"error.hiveAp.feature.discard.unsupport.model",
					LSevenSignatures.getAllSupportedPlatformsString()));
		}
	}

	private String unsupportedApsMessage= "";

	public String getUnsupportedApsMessage() {
		return unsupportedApsMessage;
	}

	public void setUnsupportedApsMessage(String unsupportedApsMessage) {
		this.unsupportedApsMessage = unsupportedApsMessage;
	}



	// fnr code for check network policy begin 2011/08/18
	private String checkNetworkPolicy(ConfigTemplate wlanPolicy, boolean checkDhcp,
			StringBuffer warningMsg, Set<String> setPolicyName, HiveAp oneHiveAp,StringBuffer lldpWarningMsg){
		if (wlanPolicy.isDefaultFlag() && wlanPolicy.getConfigName().equals(BeParaModule.DEFAULT_DEVICE_GROUP_NAME) &&
				oneHiveAp.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
			return getText("error.config.networkPolicy.check.cannotusedefault",
					new String[]{wlanPolicy.getConfigName(), oneHiveAp.getHostName()});
		}

		if(oneHiveAp.getDeviceType() == HiveAp.Device_TYPE_BRANCH_ROUTER && oneHiveAp.getRoutingProfilePolicy()!=null){
			if(NmsUtil.compareSoftwareVersion(oneHiveAp.getSoftVer(), "6.0.1.0")<0){
				return getText("error.hiveAp.upload.RoutigPolicy.notmatach.ApVersion",new String []{oneHiveAp.getHostName()});
			}
		}

		if (oneHiveAp.isBranchRouter() &&
				!wlanPolicy.getConfigType().isRouterContained()) {
			return MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{wlanPolicy.getConfigName(), "routers", "routing"});
		}
		if (oneHiveAp.isSwitch() &&
				!wlanPolicy.getConfigType().isSwitchContained() &&
				!wlanPolicy.getConfigType().isBonjourOnly()) {
			return MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{wlanPolicy.getConfigName(), "switches", "switching"});
		}
		if(oneHiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP){
			boolean checkPass;
			if(oneHiveAp.getDeviceInfo().isSupportAttribute(DeviceInfo.SPT_BONJOUR_SERVICE)){
				checkPass = wlanPolicy.getConfigType().isWirelessContained() ||
						wlanPolicy.getConfigType().isBonjourOnly();
			}else{
				checkPass = wlanPolicy.getConfigType().isWirelessContained();
			}

			if(!checkPass){
				return MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
						new String[]{oneHiveAp.getConfigTemplate().getConfigName(), "APs", "wireless access"});
			}
		}

		Map<String, Long> userGoupUserIdMap = new HashMap<>();
		HashSet<String> ssidNameSet = new HashSet<>();

		if (NpUserProfileVlanMappingUtil.wirelessIncludeByDevice(oneHiveAp.getHiveApModel())) {
			for(ConfigTemplateSsid cs: wlanPolicy.getSsidInterfaces().values()){
				if (cs.getSsidProfile()!=null) {
					if (cs.getSsidProfile().isEnablePpskSelfReg()) {
						if (cs.getSsidProfile().getLocalUserGroups()!=null &&
								cs.getSsidProfile().getLocalUserGroups().size()>1) {
							return getText("error.config.networkPolicy.check.moreLocalusergroup",
									new String[]{cs.getSsidProfile().getSsidName(),
									wlanPolicy.getConfigName()});
						}
					}
					String ret=checkSsidAndUserAndRadius(cs.getSsidProfile(),wlanPolicy, userGoupUserIdMap);
					if (!ret.equals("")){
						return ret;
					}
					ret=checkUserProfileSize(cs.getSsidProfile(),wlanPolicy);
					if (!ret.equals("")){
						return ret;
					}
					ret=checkMacFilterAction(cs.getSsidProfile());
					if (!ret.equals("")){
						return ret;
					}
					ret=checkPskUserSize(cs.getSsidProfile());
					if (!ret.equals("")){
						return ret;
					}
					if (cs.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK && 
							cs.getSsidProfile().isEnablePpskSelfReg()) {
						ssidNameSet.add(cs.getSsidProfile().getPpskOpenSsid());
					}
					ssidNameSet.add(cs.getSsidProfile().getSsid());
				}
			}
		}
		
		ssidNameSet.add(wlanPolicy.getHiveProfile().getHiveName());

		String ret=checkPortProfile(wlanPolicy,oneHiveAp, ssidNameSet);
		if (!ret.equals("")){
			return ret;
		}

		if (isSwitchTypeForDevice(oneHiveAp)){
			ret = checkInterfaceLLDPConfig(wlanPolicy,oneHiveAp,lldpWarningMsg);
			if(!ret.equals("")){
				return ret;
			}
		}

		//check Lan profile
		// each network object should be has his owner VLAN, vlan cannot same
		// how many VLAN (16) and how many network object(16) for lan policy and ssid profile
		// check vpn when enabled layer2 or layer3
		// check management network, cannot be in same subnet for all policy
		// user profile is for both ssid and lan

		ret=checkVpnSize(wlanPolicy);
		if (!ret.equals("")){
			return ret;
		}

		ret=checkManagementNetwork(wlanPolicy,oneHiveAp);
		if (!ret.equals("")){
			return ret;
		}

		if (NpUserProfileVlanMappingUtil.wirelessIncludeByDevice(oneHiveAp.getHiveApModel())) {
			ret=checkRadioModeSize(wlanPolicy);
			if (!ret.equals("")){
				return ret;
			}
		}

		Map<Long, UserProfile> mapUserProfile = new HashMap<>();
		Map<Long, UserProfile> mapUserProfileAssign = new HashMap<>();

		ret=checkCacAirTime(mapUserProfile, wlanPolicy,mapUserProfileAssign, oneHiveAp);
		if (!ret.equals("")){
			return ret;
		}

		Map<Long, UserProfile> allUserProfileMaps = new HashMap<>();
		allUserProfileMaps.putAll(mapUserProfile);
		allUserProfileMaps.putAll(mapUserProfileAssign);
		if (allUserProfileMaps.values().size() > 64) {
			return getText("error.template.moreUserProfileForNetworkPolicy", new String []{wlanPolicy.getConfigName()});
		}

		ret=checkIpPolicyAndMacPolicySize(mapUserProfile, mapUserProfileAssign);
		if (!ret.equals("")){
			return ret;
		}

		ret=checkUserProfileAttribute(mapUserProfile, mapUserProfileAssign);
		if (!ret.equals("")){
			return ret;
		}

		ret=checkNetworkObjectVlan(wlanPolicy, checkDhcp, oneHiveAp,warningMsg);
		if (!ret.equals("")){
			return ret;
		}

		//if (NpUserProfileVlanMappingUtil.wirelessIncludeByDevice(oneHiveAp.getHiveApModel())) {
			ret=checkTotalPskGroupSize(wlanPolicy);
			if (!ret.equals("")){
				return ret;
			}
		//}

		//if (NpUserProfileVlanMappingUtil.wirelessIncludeByDevice(oneHiveAp.getHiveApModel())) {
			ret=checkTotalPmkUserSize(wlanPolicy,oneHiveAp);
			if (!ret.equals("")){
				return ret;
			}
		//}

		return "";
	}

	private boolean isSwitchTypeForDevice(HiveAp oneHiveAp){
		return oneHiveAp.isSwitchProduct();
	}

    private String checkPortProfile(ConfigTemplate wlanPolicy, HiveAp oneHiveAp, HashSet<String> ssidNames) {
        // check the duplicate name for both Access and SSID
        if(null != wlanPolicy && null != wlanPolicy.getAccessProfiles()) {
            for (PortAccessProfile access : wlanPolicy.getAccessProfiles()) {
                if(null != access) {
                    if (!ssidNames.isEmpty() && ssidNames.contains(access.getName())) {
                        return getText("error.config.networkPolicy.check.cannotsame",
                                new String[]{access.getName(), wlanPolicy.getConfigName()});
                    }
                }
            }
        }
        //Port Template Profiles
    	//validate only "Use external DHCP and DNS servers on the network" is supported on switch ports.
    	if(null != wlanPolicy && null != wlanPolicy.getPortProfiles()){
    		Set<PortGroupProfile> pgps = wlanPolicy.getPortProfiles();
    		for(PortGroupProfile pgp :pgps){
    			if (pgp.getDeviceType()!=oneHiveAp.getDeviceType()){
    				continue;
    			}
    			String[] models = pgp.getDeviceModelStrs();
        		if (models==null) {
        			continue;
        		}
        		boolean continueOp = true;
        		for(String key: models){
        			if (key.equals(String.valueOf(oneHiveAp.getHiveApModel()))){
        				continueOp=false;
        			}
        		}
        		if (continueOp) {
        			continue;
        		}

        		if (isSwitchTypeForDevice(oneHiveAp)) {
        			// init usb count
        			int wanCount=1;
        			int mirrorCount=0;
        			if (oneHiveAp.isSwitch()) {
        				if(null != pgp.getBasicProfiles()){
        					for(PortBasicProfile pbp : pgp.getBasicProfiles()){
        						if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_WAN) {
        							return getText("error.config.networkPolicy.check.connotConfig3Args",
        									new String[]{"WAN type","switch port",
        									wlanPolicy.getConfigName()});
        						}
        						if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_MONITOR) {
        							if (pbp.getSFPs()!=null) {
        								mirrorCount = mirrorCount + pbp.getSFPs().length;
        							}
        							if (pbp.getETHs()!=null) {
        								mirrorCount = mirrorCount + pbp.getETHs().length;
        							}
        						}
        					}
        				}
        				if (mirrorCount>4) {
        					return getText("error.config.networkPolicy.check.moreport",
									new String[]{"4",
									"mirror type",
									wlanPolicy.getConfigName()});
        				}
        			} else {
        				if(null != pgp.getBasicProfiles()){
        					for(PortBasicProfile pbp : pgp.getBasicProfiles()){
        						if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_WAN) {
//        							if (pbp.getSFPs()!=null && pbp.getSFPs().length>0) {
//        								return getText("error.config.networkPolicy.check.connotConfig",
//            									new String[]{"WAN type","SFP port",
//            									wlanPolicy.getConfigName()});
//        							}
        							if (pbp.getSFPs()!=null) {
        								wanCount = wanCount + pbp.getSFPs().length;
        							}
        							if (pbp.getETHs()!=null) {
        								wanCount = wanCount + pbp.getETHs().length;
        							}
//        							if (pbp.getUSBs()!=null) {
//        								wanCount = wanCount + pbp.getUSBs().length;
//        							} else {
//        								// USB will taken one wan port
//        								wanCount = wanCount + 1;
//        							}
        						} else {
        							if (pbp.getUSBs()!=null && pbp.getUSBs().length>0) {
        								return getText("error.config.networkPolicy.check.mustConfig",
            									new String[]{"USB port","WAN type",
            									wlanPolicy.getConfigName()});
        							}
        						}
        						if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_MONITOR) {
        							if (pbp.getSFPs()!=null) {
        								mirrorCount = mirrorCount + pbp.getSFPs().length;
        							}
        							if (pbp.getETHs()!=null) {
        								mirrorCount = mirrorCount + pbp.getETHs().length;
        							}
        						}
        					}
        				}
        				if (wanCount>3) {
        					return getText("error.config.networkPolicy.check.moreport",
									new String[]{"3",
									"WAN type",
									wlanPolicy.getConfigName()});
        				}
        				if (mirrorCount>4) {
        					return getText("error.config.networkPolicy.check.moreport",
									new String[]{"4",
									"mirror type",
									wlanPolicy.getConfigName()});
        				}
        			}
        		} else if (oneHiveAp.isBranchRouter()){
        			int wanCount=2;
        			if(null != pgp.getBasicProfiles()){
    					for(PortBasicProfile pbp : pgp.getBasicProfiles()){
    						if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_WAN) {
    							//wanCount = wanCount + 1;
    							if (pbp.getETHs()!=null) {
    								wanCount = wanCount + pbp.getETHs().length;
    							}
//    							if (pbp.getUSBs()!=null) {
//    								wanCount = wanCount + pbp.getUSBs().length;
//    							} else {
//    								// USB will taken one wan port
//    								wanCount = wanCount + 1;
//    							}
    						} else {
    							if (pbp.getUSBs()!=null && pbp.getUSBs().length>0) {
    								return getText("error.config.networkPolicy.check.mustConfig",
        									new String[]{"USB port","WAN type",
        									wlanPolicy.getConfigName()});
    							}
    						}
    					}
    				}
    				if (wanCount>3) {
    					return getText("error.config.networkPolicy.check.moreport",
								new String[]{"2",
								"WAN type",
								wlanPolicy.getConfigName()});
    				}
        		}

    			if(null != pgp.getBasicProfiles()){
					for(PortBasicProfile pbp : pgp.getBasicProfiles()){
						if(null != pbp.getAccessProfile()){
							if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_MONITOR ||
									pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_WAN) {
								continue;
							}
							if(pbp.getAccessProfile().isEnabledCWP()){
								if( pbp.getAccessProfile().getCwp()==null){
									return getText("error.config.networkPolicy.check.mustConfig",
											new String[]{"CWP",
											"Port Type (" + pbp.getAccessProfile().getName() + ")",
											wlanPolicy.getConfigName()});
								} else {
									if (isSwitchTypeForDevice(oneHiveAp)){
										if(pbp.getAccessProfile().isEnabledCWP()){
											if(null != pbp.getAccessProfile().getCwp() && pbp.getAccessProfile().getCwp().getServerType() != Cwp.CWP_EXTERNAL){
												return getText("error.port.access.dhcp.server.type",
														new String[]{"Port Type (" + pbp.getAccessProfile().getName() + ")",
														wlanPolicy.getConfigName()});
											}
										}
									}
								}
							}

							if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_8021Q){
								if (pbp.getAccessProfile().getNativeVlan()==null) {
									return getText("error.config.networkPolicy.check.mustConfig",
											new String[]{"Native VLAN",
											"Port Type (" + pbp.getAccessProfile().getName() + ")",
											wlanPolicy.getConfigName()});
								}
							} else {
								if (pbp.getAccessProfile().isEnabled8021X() || pbp.getAccessProfile().isEnabledMAC() ||
										(pbp.getAccessProfile().isEnabledCWP() &&
												(pbp.getAccessProfile().getCwp().getRegistrationType() ==Cwp.REGISTRATION_TYPE_AUTHENTICATED||
												pbp.getAccessProfile().getCwp().getRegistrationType() ==Cwp.REGISTRATION_TYPE_BOTH||
												pbp.getAccessProfile().getCwp().getRegistrationType() ==Cwp.REGISTRATION_TYPE_EXTERNAL))) {
									if (pbp.getAccessProfile().getRadiusAssignment()==null && !pbp.getAccessProfile().isEnabledIDM()) {
										return getText("error.config.networkPolicy.check.mustConfig",
												new String[]{"RADIUS Server",
												"Port Type (" + pbp.getAccessProfile().getName() + ")",
												wlanPolicy.getConfigName()});
									}
									if (pbp.getAccessProfile().isEnableAssignUserProfile()) {
										if(pbp.getAccessProfile().getRadiusUserGroups() != null){
											if(pbp.getAccessProfile().getRadiusUserGroups().isEmpty()){
												return getText("error.config.networkPolicy.check.mustConfig",
														new String[]{"RADIUS User Groups",
														"Port Type (" + pbp.getAccessProfile().getName() + ")",
														wlanPolicy.getConfigName()});
											}
										}
									}
								}

								if (pbp.getAccessProfile().getRadiusAssignment()!=null) {
									if (pbp.getAccessProfile().getDefUserProfile() == null) {
										return getText("error.config.networkPolicy.check.mustConfig",
												new String[]{"default User Profile",
												"Port Type (" + pbp.getAccessProfile().getName() + ")",
												wlanPolicy.getConfigName()});
									}
									if (pbp.getAccessProfile().isEnabledCWP() &&
											pbp.getAccessProfile().getCwp().getRegistrationType() ==Cwp.REGISTRATION_TYPE_BOTH) {
										if (pbp.getAccessProfile().getSelfRegUserProfile() == null) {
											return getText("error.config.networkPolicy.check.mustConfig",
													new String[]{"registration User Profile",
													"Port Type (" + pbp.getAccessProfile().getName() + ")",
													wlanPolicy.getConfigName()});
										}
									}
								} else {
									if (pbp.getAccessProfile().isEnabledCWP() &&
											pbp.getAccessProfile().getCwp().getRegistrationType() ==Cwp.REGISTRATION_TYPE_REGISTERED) {
										if (pbp.getAccessProfile().getSelfRegUserProfile() == null) {
											return getText("error.config.networkPolicy.check.mustConfig",
													new String[]{"registration User Profile",
													"Port Type (" + pbp.getAccessProfile().getName() + ")",
													wlanPolicy.getConfigName()});
										}
									} else {
										if (pbp.getAccessProfile().getDefUserProfile() == null) {
											return getText("error.config.networkPolicy.check.mustConfig",
													new String[]{"default User Profile",
													"Port Type (" + pbp.getAccessProfile().getName() + ")",
													wlanPolicy.getConfigName()});
										}

										if (pbp.getAccessProfile().getPortType()==PortAccessProfile.PORT_TYPE_PHONEDATA) {
										    if(!pbp.getAccessProfile().isRadiusAuthEnable()) {
										        if (pbp.getAccessProfile().getDataVlan() == null
										                && pbp.getAccessProfile().getVoiceVlan() == null) {
										            return getText("error.config.networkPolicy.check.mustConfig",
										                    new String[]{"Voice VLAN and Data VLAN",
										                    "Port Type (" + pbp.getAccessProfile().getName() + ")",
										                    wlanPolicy.getConfigName()});
										        } else if (pbp.getAccessProfile().getDataVlan() == null) {
										            return getText("error.config.networkPolicy.check.mustConfig",
										                    new String[]{"Data VLAN",
										                    "Port Type (" + pbp.getAccessProfile().getName() + ")",
										                    wlanPolicy.getConfigName()});
										        } else if (pbp.getAccessProfile().getVoiceVlan() == null) {
										            return getText("error.config.networkPolicy.check.mustConfig",
										                    new String[]{"Voice VLAN",
										                    "Port Type (" + pbp.getAccessProfile().getName() + ")",
										                    wlanPolicy.getConfigName()});
										        }
										    }
										}
									}
								}
							}
						}
					}
    			}
    		}
    	}
        return "";
    }

    private String checkInterfaceLLDPConfig(ConfigTemplate wlanPolicy,HiveAp oneHiveAp,StringBuffer lldpWarningMsg){
    	if(null != wlanPolicy
    			&& null != wlanPolicy.getLldpCdp()
    			&& !wlanPolicy.getPortProfiles().isEmpty()
    			&& null != oneHiveAp
    			&&  null != lldpWarningMsg){

			List<CheckItem> interfaceOptions = new ArrayList<>();
			if(null != wlanPolicy.getPortProfiles()){
				Set<PortGroupProfile> pgps = wlanPolicy.getPortProfiles();
				for(PortGroupProfile pgp :pgps){
					if(null != pgp.getBasicProfiles()){
						for(PortBasicProfile pbp : pgp.getBasicProfiles()){
							if(null != pbp.getAccessProfile() &&  pbp.getAccessProfile().getPortType() == PortAccessProfile.PORT_TYPE_PHONEDATA){
								String[] ethPorts = pbp.getETHs();
								String[] sfpPorts = pbp.getSFPs();
								if(null != ethPorts){
									for (String ethPort : ethPorts) {
										short tempConst = DeviceInfType.Gigabit.getFinalValue(Integer.valueOf(ethPort), oneHiveAp.getHiveApModel());
										CheckItem item = new CheckItem((long) tempConst, "ETH" + ethPort);
										interfaceOptions.add(item);
									}
								}
								if(null != sfpPorts){
									for (String sfpPort : sfpPorts) {
										short tempConst = DeviceInfType.SFP.getFinalValue(Integer.valueOf(sfpPort), oneHiveAp.getHiveApModel());
										CheckItem item = new CheckItem((long) tempConst, "SFP" + sfpPort);
										interfaceOptions.add(item);
									}
								}
							}
						}
					}
				}
			}
			//Phone&Data port is defined
			if(!interfaceOptions.isEmpty()){
				if(!wlanPolicy.getLldpCdp().isEnableLLDPNonHostPorts()){
					lldpWarningMsg.append(getText("warn.template.switchsetting.nonhost.lldp.disable"));
					return "";
				}else if(oneHiveAp.isOverrideNetworkPolicySetting()){
					for (CheckItem interfaceOption : interfaceOptions) {
						for (Long key : oneHiveAp.getDeviceInterfaces().keySet()) {
							DeviceInterface di = oneHiveAp.getDeviceInterfaces().get(key);
							if (interfaceOption.getId().intValue() == di.getDeviceIfType()) {
								if (!di.isLldpTransmit() || !di.isLldpReceive()) {
									lldpWarningMsg.append(getText("warn.template.switchsetting.nonhost.lldp.disable"));
									return "";
								}
							}
						}
					}
				}
			}

    	}

    	return "";
    }

    private String checkNetworkObjectVlan(ConfigTemplate wlan, boolean checkDhcp,HiveAp oneHiveAp, StringBuffer warningMsg){
    	Set<VpnNetwork> setNetWork = new HashSet<>();
    	try {
	    	if (wlan.getConfigType().isRouterContained() && oneHiveAp.isBranchRouter()) {
		    	Set<Vlan> vlanSet = new HashSet<>();
				Map<Long, UserProfileVlanMapping> mappings = NpUserProfileVlanMappingUtil.getFullUpVlanMappings(wlan, true, true, oneHiveAp.getHiveApModel());
				if (mappings != null) {
					for (UserProfileVlanMapping mapping : mappings.values()) {
						vlanSet.add(mapping.getVlan());
					}
				}
				// fetch all native Vlan in port assess profile when routing mode
				List<String> allowVlans = new ArrayList<>();
				Set<Vlan> nativeVlan = NpUserProfileVlanMappingUtil.getAllRoutingNativeVlan(wlan, oneHiveAp.getHiveApModel(), allowVlans);
				if (!nativeVlan.isEmpty()) {
					vlanSet.addAll(nativeVlan);
				}
				if (wlan.getVlan()!=null) {
					vlanSet.add(wlan.getVlan());
				}

				List<Long> vlanIds = new ArrayList<>();
				for(Vlan v: vlanSet){
					vlanIds.add(v.getId());
				}

				Set<String> allVlanList = MgrUtil.convertRangeToVlaue(allowVlans);

				StringBuilder vlanNames = new StringBuilder();
				for(ConfigTemplateVlanNetwork cvn:wlan.getVlanNetwork()){
					if (cvn.isBlnRemoved()) {
						continue;
					}
					if (!cvn.isBlnUserAdd()){
						if (vlanIds.contains(cvn.getVlan().getId())){
							if(cvn.getNetworkObj()==null) {
								if (vlanNames.length()==0) {
									vlanNames.append(cvn.getVlan().getVlanName());
								} else {
									vlanNames.append(", ").append(cvn.getVlan().getVlanName());
								}

//								return getText("error.config.networkPolicy.check.mustConfig",
//										new String[]{"Network mapping by VLAN(" + cvn.getVlan().getVlanName() +")" ,
//										"VLAN-to-Network Mapping section",
//										wlan.getConfigName()});
							} else {
								setNetWork.add(cvn.getNetworkObj());
							}
						} else {
							if(cvn.getNetworkObj()!=null) {
								for(SingleTableItem st1: cvn.getVlan().getItems()){
									if (allVlanList.contains("all") || allVlanList.contains(String.valueOf(st1.getVlanId()))) {
										vlanIds.add(cvn.getVlan().getId());
										setNetWork.add(cvn.getNetworkObj());
										break;
									}
								}
							}
						}
					} else {
						if(cvn.getNetworkObj()!=null) {
							for(SingleTableItem st1: cvn.getVlan().getItems()){
								if (allVlanList.contains("all") || allVlanList.contains(String.valueOf(st1.getVlanId()))) {
									vlanIds.add(cvn.getVlan().getId());
									setNetWork.add(cvn.getNetworkObj());
									break;
								}
							}
						}
					}
                }
				if (vlanNames.length()!=0) {
					StringBuilder temp = new StringBuilder();
					temp.append("The network policy (")
					.append(wlan.getConfigName()).append(")")
					.append(" does not have a network assigned to the following VLANs: ")
					.append(vlanNames.toString()).append(".");
					if (warningMsg.length()==0) {
						warningMsg.append(temp.toString());
					} else if (!warningMsg.toString().contains(temp.toString())) {
						warningMsg.append("<br/><br/>");
						warningMsg.append(temp.toString());
					}
				}

				List<Vlan> vlanList = QueryUtil.executeQuery(Vlan.class, null, new FilterParams("id",vlanIds), null, this);
				for (int i=0; i<vlanList.size(); i++) {
					for (int j=i+1; j<vlanList.size(); j++) {
						for(SingleTableItem st1: vlanList.get(i).getItems()){
							for(SingleTableItem stn: vlanList.get(j).getItems()){
								if (st1.getVlanId()==stn.getVlanId()) {
									return getText("error.config.networkPolicy.check.vlansame",
											new String[]{vlanList.get(i).getVlanName(),
											vlanList.get(j).getVlanName(),
											wlan.getConfigName()});
								}
							}
						}
					}
				}
				// this check moved to hiveap upload check
//				if (vlanList.size()!=wlan.getVlanNetwork().size()) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"Network",
//							"VLAN-to-Network Mapping section",
//							wlan.getConfigName()});
//				}

//				for(ConfigTemplateVlanNetwork cvn:wlan.getVlanNetwork()){
//					if(!cvn.getVlan().equals(wlan.getVlan())) {
//						if (cvn.getNetworkObj()==null) {
//							return getText("error.config.networkPolicy.check.mustConfig",
//									new String[]{"Network",
//									"VLAN-to-Network Mapping section",
//									wlan.getConfigName()});
//						} else {
//							setNetWork.add(cvn.getNetworkObj());
//						}
//					}
//				}
				if (oneHiveAp.isBranchRouter()){
					if (oneHiveAp.isSwitchProduct()){
						if (setNetWork.size()>65) {
							return getText("error.config.networkPolicy.check.moreVlan",
									new String[]{"65", wlan.getConfigName()});
						}
					} else {
						if (setNetWork.size()>17) {
							return getText("error.config.networkPolicy.check.moreVlan",
									new String[]{"17", wlan.getConfigName()});
						}
					}
				}

				String ret = checkSubNetworkInSameNet(setNetWork, wlan);
				if (!"".equals(ret)) {
					return ret;
				}
				ret = checkSubLocalNetworkInSameNet(setNetWork, wlan);
				if (!"".equals(ret)) {
					return ret;
				}

				if (checkDhcp) {
					if (wlan.getMgmtServiceDns()!=null) {
						return "";
					} else {
						for(VpnNetwork vn: setNetWork) {
							if (vn.getVpnDnsService() != null
									&& vn.getVpnDnsService().getExternalServerType() == DnsServiceProfile.LOCAL_DNS_TYPE) {
								String tempStr[] = { getText("config.configTemplate.mgtDns"),
										"Additional Settings", wlan.getConfigName() };
								return getText("error.config.networkPolicy.check.mustConfig", tempStr);
							}
						}
					}
				}

				//check the DestinationPortNumber Duplicate
				List<String> outsidePortList = new LinkedList<>();
				for(VpnNetwork vn: setNetWork) {
					if(null != vn.getPortForwardings()) {
						for(PortForwarding portForwarding : vn.getPortForwardings()) {
							if(PortForwarding.PROTOCOL_ANY == portForwarding.getProtocol()) {
								if(outsidePortList.contains(portForwarding.getDestinationPortNumber() + "_" + PortForwarding.PROTOCOL_TCP) || outsidePortList.contains(portForwarding.getDestinationPortNumber() + "_" + PortForwarding.PROTOCOL_UDP)) {
									return getText("error.config.networkPolicy.check.destinationPortNumberDuplicate", new String[]{portForwarding.getDestinationPortNumber()});
								}
								outsidePortList.add(portForwarding.getDestinationPortNumber() + "_" + PortForwarding.PROTOCOL_TCP);
								outsidePortList.add(portForwarding.getDestinationPortNumber() + "_" + PortForwarding.PROTOCOL_UDP);
							} else {
								if(outsidePortList.contains(portForwarding.getDestinationPortNumber() + "_" + portForwarding.getProtocol())) {
									return getText("error.config.networkPolicy.check.destinationPortNumberDuplicate", new String[]{portForwarding.getDestinationPortNumber()});
								}
								outsidePortList.add(portForwarding.getDestinationPortNumber() + "_" + portForwarding.getProtocol());
							}
						}
					}
				}
	    	}
    	} catch (Exception e) {
    		return e.getMessage();
    	}

		return "";
	}

	private String checkSubLocalNetworkInSameNet(Set<VpnNetwork> setNetWork, ConfigTemplate wlan){
		for(VpnNetwork vn1 : setNetWork) {
			Set<String> vn1NetIP = new HashSet<>();
			if (vn1.getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
				vn1NetIP.add(vn1.getIpAddressSpace());
			} else {
				for(VpnNetworkSub vn1Sub: vn1.getSubItems()) {
					vn1NetIP.add(vn1Sub.getLocalIpNetwork());
				}
			}
			if (!vn1NetIP.isEmpty()) {
				for(VpnNetwork vn2 : setNetWork) {
					if (!vn1.getNetworkName().equals(vn2.getNetworkName())) {
						Set<String> vn2NetIP = new HashSet<>();
						if (vn2.getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
							vn2NetIP.add(vn2.getIpAddressSpace());
						} else {
							for(VpnNetworkSub vn2Sub: vn2.getSubItems()) {
								vn2NetIP.add(vn2Sub.getLocalIpNetwork());
							}
						}
						if (!vn2NetIP.isEmpty()) {
							for(String vnIp1: vn1NetIP) {
								for(String vnIp2: vn2NetIP) {
									if (MgrUtil.checkIpInSameNetwork(vnIp1, vnIp2)) {
										String tempStr[] = { vn1.getNetworkName(),
												vn2.getNetworkName(), wlan.getConfigName() };
										return getText("error.config.networkPolicy.check.sublocalnetworksame", tempStr);
									}
								}
							}
						}
					}
				}
			}
		}

		return "";
	}

	private String checkSubNetworkInSameNet(Set<VpnNetwork> setNetWork, ConfigTemplate wlan){
		for(VpnNetwork vn1 : setNetWork) {
			Set<String> vn1NetIP = new HashSet<>();
			if (vn1.getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
				vn1NetIP.add(vn1.getIpAddressSpace());
			} else {
				for(VpnNetworkSub vn1Sub: vn1.getSubItems()) {
					vn1NetIP.add(vn1Sub.getIpNetwork());
				}
			}
			if (!vn1NetIP.isEmpty()) {
				for(VpnNetwork vn2 : setNetWork) {
					if (!vn1.getNetworkName().equals(vn2.getNetworkName())) {
						Set<String> vn2NetIP = new HashSet<>();
						if (vn2.getNetworkType()==VpnNetwork.VPN_NETWORK_TYPE_GUEST) {
							vn2NetIP.add(vn2.getIpAddressSpace());
						} else {
							for(VpnNetworkSub vn2Sub: vn2.getSubItems()) {
								vn2NetIP.add(vn2Sub.getIpNetwork());
							}
						}
						if (!vn2NetIP.isEmpty()) {
							for(String vnIp1: vn1NetIP) {
								for(String vnIp2: vn2NetIP) {
									if (MgrUtil.checkIpInSameNetwork(vnIp1, vnIp2)) {
										String tempStr[] = { vn1.getNetworkName(),
												vn2.getNetworkName()};
										return getText("error.config.networkPolicy.check.subnetworksame", tempStr);
									}
								}
							}
						}
					}
				}
			}
		}

		return "";
	}

//	private String checkLanAndUserAndRadius(LanProfile sp, ConfigTemplate wlan){
//		if (!sp.isEnabled8021Q()){
//			if (sp.isCwpSelectEnabled() && sp.getCwp()==null) {
//				return getText("error.config.networkPolicy.check.mustConfig",
//						new String[]{"CWP",
//						"Lan Profile (" + sp.getName() + ")",
//						wlan.getConfigName()});
//			}
//
//			if (sp.isRadiusAuthEnable() && sp.getRadiusAssignment()==null) {
//				return getText("error.config.networkPolicy.check.mustConfig",
//						new String[]{"RADIUS Server",
//						"Lan Profile (" + sp.getName() + ")",
//						wlan.getConfigName()});
//			}
//
//			if (sp.isRadiusAuthEnable() && sp.isEnableAssignUserProfile()) {
//				if (sp.getRadiusUserGroups().isEmpty()) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//    						new String[]{"RADIUS User Groups",
//    						"Lan Profile (" + sp.getName() + ")",
//    						wlan.getConfigName()});
//				}
//			}
//
//			if (sp.isRadiusAuthEnable() && sp.getUserProfileDefault()==null){
//				return getText("error.config.networkPolicy.check.mustConfig",
//						new String[]{"default User Profile",
//						"Lan Profile (" + sp.getName() + ")",
//						wlan.getConfigName()});
//			}
//
//			if (sp.isCwpSelectEnabled() && null != sp.getCwp()
//	                && (sp.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_REGISTERED
//	                        || sp.getCwp().getRegistrationType() == Cwp.REGISTRATION_TYPE_BOTH)) {
//				if (sp.getUserProfileSelfReg()==null) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"registration User Profile",
//							"Lan Profile (" + sp.getName() + ")",
//							wlan.getConfigName()});
//				}
//			}
//			if (sp.isCwpSelectEnabled() && null != sp.getCwp()
//	                && sp.getCwp().getRegistrationType() != Cwp.REGISTRATION_TYPE_REGISTERED) {
//				if (sp.getUserProfileDefault()==null) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"default User Profile",
//							"Lan Profile (" + sp.getName() + ")",
//							wlan.getConfigName()});
//				}
//			}
//			if (!sp.isEnabled8021X() && !sp.isCwpSelectEnabled()){
//				if (sp.getUserProfileDefault()==null) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"default User Profile",
//							"Lan Profile (" + sp.getName() + ")",
//							wlan.getConfigName()});
//				}
//			}
//
//		} else {
//			/*if (wlan.isBlnWirelessRouter()) {*/
//			if (wlan.getConfigType().isWirelessAndRouterContainedNeedCheck()) {
//				if (sp.getNativeNetwork()==null) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"Untagged Network/VLAN",
//							"Lan Profile (" + sp.getName() + ")",
//							wlan.getConfigName()});
//				}
//			} else {
//				if (sp.getNativeVlan()==null) {
//					return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"Untagged VLAN",
//							"Lan Profile (" + sp.getName() + ")",
//							wlan.getConfigName()});
//				}
//			}
////			else {
////				if (!sp.getNativeNetwork().getVlan().getId().equals(wlan.getVlanNative().getId())){
////					return getText("error.config.networkPolicy.check.mustsame",
////							new String[]{"VLAN in untagged Network Object ("
////							+ sp.getNativeNetwork().getNetworkName() + ") in Lan Profile (" +
////							sp.getName() + ")",
////							"Native (Untagged) VLAN",
////							wlan.getConfigName()});
////				}
////			}
//		}
//		return "";
//	}
//
//	private String checkLanProfileUserProfile(LanProfile sp, ConfigTemplate wlan) {
////		boolean isWirelessRouterMode = wlan.getConfigType().isWirelessAndRouterContainedNeedCheck();
////		if (sp.getUserProfileDefault() != null) {
////			if (isWirelessRouterMode? sp.getUserProfileDefault().getNetworkObj()==null :
////				(sp.getUserProfileDefault().getNetworkObj()==null && sp.getUserProfileDefault().getVlan()==null)) {
////				return getText("error.config.networkPolicy.check.mustConfig",
////						new String[]{"Network/VLAN",
////						"User Profile (" + sp.getUserProfileDefault().getUserProfileName() + ")",
////						wlan.getConfigName()});
////			}
////		}
////		if (sp.getUserProfileSelfReg() != null) {
////			if (isWirelessRouterMode ? sp.getUserProfileSelfReg().getNetworkObj()==null :
////				(sp.getUserProfileSelfReg().getNetworkObj()==null && sp.getUserProfileSelfReg().getVlan()==null)) {
////				return getText("error.config.networkPolicy.check.mustConfig",
////						new String[]{"Network/VLAN",
////						"User Profile (" + sp.getUserProfileSelfReg().getUserProfileName() + ")",
////						wlan.getConfigName()});
////			}
////		}
////		if (sp.getRadiusUserProfile() != null) {
////			for(UserProfile us: sp.getRadiusUserProfile()){
////				if (isWirelessRouterMode ? us.getNetworkObj()==null : (us.getNetworkObj()==null && us.getVlan()==null)) {
////					return getText("error.config.networkPolicy.check.mustConfig",
////							new String[]{"Network/VLAN",
////							"User Profile (" + us.getUserProfileName() + ")",
////							wlan.getConfigName()});
////				}
////			}
////		}
//
//		return "";
//	}

	private String checkSsidAndUserAndRadius(SsidProfile sp, ConfigTemplate wlan, Map<String, Long> userGoupUserIdMap){
		if (sp.getAccessMode()==SsidProfile.ACCESS_MODE_PSK){
			if (!sp.isEnabledIDM() && (sp.getLocalUserGroups()==null || sp.getLocalUserGroups().size()<1)){
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"Local User Groups",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}
			if (sp.getUserProfileDefault()==null) {
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"default User Profile",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}

			for(LocalUserGroup up: sp.getLocalUserGroups()){
				if (up.getUserProfileId()<0) {
					if (userGoupUserIdMap.get(up.getGroupName())==null) {
						userGoupUserIdMap.put(up.getGroupName(), sp.getUserProfileDefault().getId());
					} else {
						if (!userGoupUserIdMap.get(up.getGroupName()).equals(sp.getUserProfileDefault().getId())){
							return getText("error.config.networkPolicy.check.sameusergroup.diffuser",
		    						new String[]{up.getGroupName(),
		    						wlan.getConfigName()});
						}
					}
				}
			}

//			if (sp.getRadiusUserProfile()==null || sp.getRadiusUserProfile().size()<1){
//				return getText("error.config.networkPolicy.check.mustConfig",
//						new String[]{"User Profiles",
//						"SSID Profile (" + sp.getSsidName() + ")",
//						wlan.getConfigName()});
//			}
			if (sp.isCwpSelectEnabled() && sp.getUserPolicy()==null) {
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"CWP",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}
			if (sp.isMacAuthEnabled() || sp.getEnabledUseGuestManager()) {
				if (sp.getRadiusAssignment()==null && !sp.isEnabledIDM()) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"RADIUS Server",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				}
				if (sp.isEnableAssignUserProfile()) {
					if (sp.getRadiusUserGroups().isEmpty()) {
						return getText("error.config.networkPolicy.check.mustConfig",
	    						new String[]{"RADIUS User Groups",
	    						"SSID Profile (" + sp.getSsidName() + ")",
	    						wlan.getConfigName()});
					}
				}
			}
			if (sp.isEnablePpskSelfReg() || sp.getSsidSecurity().isBlnMacBindingEnable()) {
				/*if (wlan.isBlnWirelessRouter() || wlan.isBlnWirelessOnly()) {*/
//				if (wlan.getConfigType().isWirelessContained()) {
					if (!sp.isBlnBrAsPpskServer()
							&& !sp.isEnabledIDM()
							&& (sp.getPpskServer()==null || sp.getPpskServer().isDhcp())){
						return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"Device Private PSK Server",
								"SSID Profile (" + sp.getSsidName() + ")",
								wlan.getConfigName()});
					}
//				} else {
//					if (sp.getPpskServer()==null || sp.getPpskServer().isDhcp()){
//						return getText("error.config.networkPolicy.check.mustConfig",
//							new String[]{"Device Private PSK Server",
//								"SSID Profile (" + sp.getSsidName() + ")",
//								wlan.getConfigName()});
//					}
//				}
			}
			if (sp.isEnablePpskSelfReg()){
				if (sp.getPpskECwp()==null) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"Private PSK CWP",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				} else {
					if (sp.getPpskECwp().getPpskServerType()==Cwp.PPSK_SERVER_TYPE_AUTH){
						if (sp.getRadiusAssignmentPpsk()==null && !sp.isEnabledIDM()) {
							return getText("error.config.networkPolicy.check.mustConfig",
									new String[]{"Private PSK RADIUS Server",
									"SSID Profile (" + sp.getSsidName() + ")",
									wlan.getConfigName()});
						}
					}
				}
			}
		} else if (sp.getAccessMode()==SsidProfile.ACCESS_MODE_8021X || sp.getMgmtKey()==SsidProfile.KEY_MGMT_DYNAMIC_WEP){
			if (sp.isCwpSelectEnabled() && sp.getUserPolicy()==null) {
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"CWP",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}
			if (sp.getRadiusAssignment()==null && !sp.isEnabledIDM()) {
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"RADIUS Server",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}
			if (sp.isEnableAssignUserProfile()) {
				if (sp.getRadiusUserGroups().isEmpty()) {
					return getText("error.config.networkPolicy.check.mustConfig",
    						new String[]{"RADIUS User Groups",
    						"SSID Profile (" + sp.getSsidName() + ")",
    						wlan.getConfigName()});
				}
			}
			if (sp.getUserProfileDefault()==null) {
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"default User Profile",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}
		} else {
			if (sp.isCwpSelectEnabled() && sp.getCwp()==null) {
				return getText("error.config.networkPolicy.check.mustConfig",
						new String[]{"CWP",
						"SSID Profile (" + sp.getSsidName() + ")",
						wlan.getConfigName()});
			}
			if (sp.getCwp()!=null && (sp.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_REGISTERED
					|| sp.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_BOTH )) {
				if (sp.getUserProfileSelfReg()==null) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"registration User Profile",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				}
			}
			if (sp.getCwp()==null || (sp.getCwp().getRegistrationType()!=Cwp.REGISTRATION_TYPE_REGISTERED)) {
				if (sp.getUserProfileDefault()==null) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"default User Profile",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				}
			}

			if (sp.isMacAuthEnabled()) {
				if (sp.getRadiusAssignment()==null && !sp.isEnabledIDM()) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"RADIUS Server",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				}
				if (sp.isEnableAssignUserProfile()) {
					if (sp.getRadiusUserGroups().isEmpty()) {
						return getText("error.config.networkPolicy.check.mustConfig",
	    						new String[]{"RADIUS User Groups",
	    						"SSID Profile (" + sp.getSsidName() + ")",
	    						wlan.getConfigName()});
					}
				}
				if (sp.getUserProfileDefault()==null) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"default User Profile",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				}
			}

			if (sp.getCwp()!=null && (sp.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_AUTHENTICATED
					|| sp.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_BOTH
					|| sp.getCwp().getRegistrationType()==Cwp.REGISTRATION_TYPE_EXTERNAL)) {
				if (sp.getRadiusAssignment()==null && !sp.isEnabledIDM()) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"RADIUS Server",
							"SSID Profile (" + sp.getSsidName() + ")",
							wlan.getConfigName()});
				}
				if (sp.isEnableAssignUserProfile()) {
					if (sp.getRadiusUserGroups().isEmpty()) {
						return getText("error.config.networkPolicy.check.mustConfig",
	    						new String[]{"RADIUS User Groups",
	    						"SSID Profile (" + sp.getSsidName() + ")",
	    						wlan.getConfigName()});
					}
				}
			}
		}
		return "";
	}

	private String checkUserProfileSize(SsidProfile sp, ConfigTemplate wlan) {
		int usSize = 0;
		if (sp.getUserProfileDefault() != null) {
			usSize++;
		}
		if (sp.getUserProfileSelfReg() != null) {
			usSize++;
		}
		if (sp.getRadiusUserProfile() != null) {
			usSize = usSize + sp.getRadiusUserProfile().size();
		}
		if (usSize > 64) {
			return getText("error.template.moreUserProfile");
		}
		return "";
	}

	private String checkMacFilterAction(SsidProfile sp) {
		try {
			Map<String, String> tmpMacFilter = new HashMap<>();
			Set<String> totalMacOUI = new HashSet<>();
			for (MacFilter lazyMacfilter : sp.getMacFilters()) {
				MacFilter filter = findBoById(MacFilter.class, lazyMacfilter.getId(), this);
				for (MacFilterInfo filterInfo : filter.getFilterInfo()) {
					totalMacOUI.add(filterInfo.getMacOrOui().getMacOrOuiName());
					if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_PERMIT) {
						if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_DENY) != null) {
							return MgrUtil.getUserMessage("error.differentMacOuiAction",
									filterInfo.getMacOrOui().getMacOrOuiName());
						} else {
							tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
									+ MacFilter.FILTER_ACTION_PERMIT, "true");
						}
					} else if (filterInfo.getFilterAction() == MacFilter.FILTER_ACTION_DENY) {
						if (tmpMacFilter.get(filterInfo.getMacOrOui().getMacOrOuiName()
								+ MacFilter.FILTER_ACTION_PERMIT) != null) {
							return MgrUtil.getUserMessage("error.differentMacOuiAction",
									filterInfo.getMacOrOui().getMacOrOuiName());
						} else {
							tmpMacFilter.put(filterInfo.getMacOrOui().getMacOrOuiName()
									+ MacFilter.FILTER_ACTION_DENY, "true");
						}
					}
				}
			}
			if (totalMacOUI.size()>MacFiltersAction.MAX_MACFILTER_ENTER) {
				return getText("error.config.macFilter.maxNumber.reference",
						new String[]{String.valueOf(MacFiltersAction.MAX_MACFILTER_ENTER)});

			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return "";
	}

	private String checkPskUserSize(SsidProfile sp) {
		long count = SsidProfilesAction.getPskUserCount(sp);
		if (count > LocalUser.MAX_COUNT_AP30_USERPERSSID) {
			return getText("error.template.morePskUsers");
		}
		return "";
	}

	private String checkManagementNetwork(ConfigTemplate wlan,HiveAp oneHiveAp) {
		try {
			if (wlan.getConfigType().isRouterContained() && oneHiveAp.isBranchRouter()) {
				VpnNetwork mgtNetwork = wlan.getNetworkByVlan(wlan.getVlan());

				if (mgtNetwork==null) {
					return getText("error.config.networkPolicy.check.mnk.empty",
							new String[]{wlan.getVlan().getVlanName(),wlan.getConfigName()});
				} else {
					mgtNetwork = findBoById(VpnNetwork.class,mgtNetwork.getId(), this);
					if (mgtNetwork.getNetworkType()!=VpnNetwork.VPN_NETWORK_TYPE_MANAGERMENT){
						return getText("error.config.networkPolicy.check.mnk.guest",
								new String[]{wlan.getVlan().getVlanName(),wlan.getConfigName()});
					} else if (mgtNetwork.getVpnDnsService()==null){
						return getText("error.config.networkPolicy.check.mnk.noDns",
								new String[]{wlan.getVlan().getVlanName(),wlan.getConfigName()});
					} else if (mgtNetwork.getSubItems()==null || mgtNetwork.getSubItems().isEmpty()){
						return getText("error.config.networkPolicy.check.mnk.nosubnet",
								new String[]{wlan.getVlan().getVlanName(),wlan.getConfigName()});
					} else {
						for (VpnNetworkSub vpnSub: mgtNetwork.getSubItems()) {
							if (!vpnSub.isEnableDhcp()) {
								return getText("error.config.networkPolicy.check.mnk.noDhcp",
										new String[]{wlan.getVlan().getVlanName(),wlan.getConfigName()});
							}
							if (vpnSub.getIpBranches()==1) {
								return getText("error.config.networkPolicy.check.mnk.subnetBranchError",
										new String[]{wlan.getVlan().getVlanName(),wlan.getConfigName()});
							}
							if(!vpnSub.isUniqueSubnetworkForEachBranches()) {
								return getText("error.config.networkPolicy.check.mnk.subnetSameMode");
							}
							if(vpnSub.isEnableNat()) {
								return getText("error.config.networkPolicy.check.mnk.subnetEnableNat");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return "";
	}
//
//	private boolean checkIpInSameSubNet(String ip1, String ip2) {
//		String[] ipArray1 = ip1.split("/");
//		String[] ipArray2 = ip2.split("/");
//		int minMask=Math.min(Integer.parseInt(ipArray1[1]), Integer.parseInt(ipArray2[1]));
//		return MgrUtil.checkIpInSameSubnet(ipArray1[0], ipArray2[0],
//				AhDecoder.int2Netmask(minMask));
//	}

	private String checkRadioModeSize(ConfigTemplate wlan) {
		int amodelCount = 0;
		int bmodelConnt = 0;

		for (ConfigTemplateSsid configTemplateSsid : wlan.getSsidInterfaces().values()) {
			if (configTemplateSsid.getSsidProfile() != null) {
				if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_A) {
					amodelCount++;
					if (configTemplateSsid.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK &&
							configTemplateSsid.getSsidProfile().isEnablePpskSelfReg()) {
						amodelCount++;
					}
				} else if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BG) {
					bmodelConnt++;
					if (configTemplateSsid.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK &&
							configTemplateSsid.getSsidProfile().isEnablePpskSelfReg()) {
						bmodelConnt++;
					}
				} else if (configTemplateSsid.getSsidProfile().getRadioMode() == SsidProfile.RADIOMODE_BOTH) {
					amodelCount++;
					bmodelConnt++;
					if (configTemplateSsid.getSsidProfile().getAccessMode()==SsidProfile.ACCESS_MODE_PSK &&
							configTemplateSsid.getSsidProfile().isEnablePpskSelfReg()) {
						amodelCount++;
						bmodelConnt++;
					}
				}
			}
		}
		if (amodelCount > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeA") };
			return getText("error.assignSsid.range", tempStr);
		}

		if (bmodelConnt > 16) {
			String tempStr[] = { "16", getText("config.configTemplate.model.typeBG") };
			return getText("error.assignSsid.range", tempStr);
		}
		return "";
	}

	private String checkIpPolicyAndMacPolicySize(Map<Long, UserProfile> mapUserProfile, Map<Long, UserProfile> mapUserProfileAssign) {
		Set<String> ipPolicyName = new HashSet<>();
		Set<String> macPolicyName = new HashSet<>();
		Map<Long, UserProfile> allUserProfileMap = new HashMap<>();
		allUserProfileMap.putAll(mapUserProfile);
		allUserProfileMap.putAll(mapUserProfileAssign);

		for (UserProfile tempUserProfile: allUserProfileMap.values()) {
			if (tempUserProfile.getIpPolicyTo() != null) {
				ipPolicyName.add(tempUserProfile.getIpPolicyTo().getPolicyName());
			}
			if (tempUserProfile.getMacPolicyTo() != null) {
				macPolicyName.add(tempUserProfile.getMacPolicyTo().getPolicyName());
			}
			if (tempUserProfile.getIpPolicyFrom() != null) {
				ipPolicyName.add(tempUserProfile.getIpPolicyFrom().getPolicyName());
			}
			if (tempUserProfile.getMacPolicyFrom() != null) {
				macPolicyName.add(tempUserProfile.getMacPolicyFrom().getPolicyName());
			}
		}

		if (ipPolicyName.size() > 32) {
			// ipPolicyName size must less than 32
			return getText("error.template.moreIPPolicy");
		}

		if (macPolicyName.size() > 32) {
			// macPolicyName size must less than 32
			return getText("error.template.moreMACPolicy");
		}
		return "";
	}

	private String checkUserProfileAttribute(Map<Long, UserProfile> mapUserProfile, Map<Long, UserProfile> mapUserProfileAssign) {
		Set<String> setUsedUserProfile = new HashSet<>();
		Set<String> setUsedAttrValue = new HashSet<>();
		Set<String> userProfileCount = new HashSet<>();
		Set<Long> qosIds = new HashSet<>();

		Map<Long, UserProfile> allUserProfileMap = new HashMap<>();
		allUserProfileMap.putAll(mapUserProfile);
		allUserProfileMap.putAll(mapUserProfileAssign);

		for (UserProfile forAttrUserProfile : allUserProfileMap.values()) {
			if (forAttrUserProfile.getQosRateControl()!=null) {
				qosIds.add(forAttrUserProfile.getQosRateControl().getId());
			}

			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& setUsedAttrValue.contains(String.valueOf(forAttrUserProfile
							.getAttributeValue()))) {
				return getText("error.template.sameAttribute");
			}
			UserProfileAttribute userProfileAttr = forAttrUserProfile.getUserProfileAttribute();
			if (!setUsedUserProfile.contains(forAttrUserProfile.getId().toString())
					&& userProfileAttr != null) {
				for (SingleTableItem singleTable : userProfileAttr.getItems()) {
					String[] strAttrValue = singleTable.getAttributeValue().split(",");
					for (String attrValue : strAttrValue) {
						String[] attrRange = attrValue.split("-");
						if (attrRange.length > 1) {
							for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
									.parseInt(attrRange[1]) + 1; addCount++) {
								if (setUsedAttrValue.contains(String.valueOf(addCount))) {
									return getText("error.template.sameAttribute");
								}
							}
						} else {
							if (setUsedAttrValue.contains(attrRange[0])) {
								return getText("error.template.sameAttribute");
							}
						}
					}
				}

				for (SingleTableItem singleTable : userProfileAttr.getItems()) {
					String[] strAttrValue = singleTable.getAttributeValue().split(",");
					for (String attrValue : strAttrValue) {
						String[] attrRange = attrValue.split("-");
						if (attrRange.length > 1) {
							for (int addCount = Integer.parseInt(attrRange[0]); addCount < Integer
									.parseInt(attrRange[1]) + 1; addCount++) {
								setUsedAttrValue.add(String.valueOf(addCount));
							}
						} else {
							setUsedAttrValue.add(attrRange[0]);
						}
					}
				}
			}
			setUsedUserProfile.add(forAttrUserProfile.getId().toString());
			setUsedAttrValue.add(String.valueOf(forAttrUserProfile.getAttributeValue()));
			if (userProfileAttr != null) {
				userProfileCount.add(userProfileAttr.getId().toString());
			}
		}

		if (userProfileCount.size() > 64) {
			return getText("error.template.moreUserProfileAttributeGroup");
		}

		if (qosIds.size()>16) {
			return getText("error.template.moreUserProfileQos");
		}
		return "";
	}

	private boolean wirelessCheckUserprofileIncludeByDevice(HiveAp oneHiveAp){
		return oneHiveAp.isBranchRouter() 
				|| (!oneHiveAp.isCVGAppliance())
				|| ((oneHiveAp.isCVGAppliance()) 
					&& oneHiveAp.getDeviceType()== HiveAp.Device_TYPE_HIVEAP);
	}

	public String checkCacAirTime(Map<Long, UserProfile> mapUserProfile,
			ConfigTemplate wlan, Map<Long, UserProfile> mapUserProfileAssign,
			HiveAp oneHiveAp) {
		int cacPercent = 0;
		if (wlan.getMgmtServiceOption() != null) {
			if (!wlan.getMgmtServiceOption().getDisableCallAdmissionControl()){
				cacPercent = cacPercent
						+ wlan.getMgmtServiceOption().getRoamingGuaranteedAirtime();
			}
		} else {
			cacPercent = cacPercent + 20;
		}
		Set<Long> setUserProfile = new HashSet<>();

		if (wirelessCheckUserprofileIncludeByDevice(oneHiveAp)) {
			for (ConfigTemplateSsid configTemplateSsid : wlan.getSsidInterfaces().values()) {
				if (configTemplateSsid.getSsidProfile() != null) {
					if (configTemplateSsid.getSsidProfile().getUserProfileDefault() != null) {
						if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId())) {
							cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileDefault().getGuarantedAirTime();
							setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileDefault().getId());
						}
					}
					if (configTemplateSsid.getSsidProfile().getUserProfileSelfReg() != null) {
						if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId())) {
							cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getGuarantedAirTime();
							setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileSelfReg().getId());
						}
					}
					if (configTemplateSsid.getSsidProfile().getUserProfileGuest() != null) {
					    if (!setUserProfile.contains(configTemplateSsid.getSsidProfile().getUserProfileGuest().getId())) {
					        cacPercent = cacPercent + configTemplateSsid.getSsidProfile().getUserProfileGuest().getGuarantedAirTime();
					        setUserProfile.add(configTemplateSsid.getSsidProfile().getUserProfileGuest().getId());
					    }
					}
					if (configTemplateSsid.getSsidProfile().getRadiusUserProfile() != null) {
						for (UserProfile tempUser : configTemplateSsid.getSsidProfile()
								.getRadiusUserProfile()) {
							if (!setUserProfile.contains(tempUser.getId())) {
								cacPercent = cacPercent + tempUser.getGuarantedAirTime();
								setUserProfile.add(tempUser.getId());
							}
						}
					}
				}
			}
		}

		// Port Template Profiles
		if(null != wlan && null != wlan.getPortProfiles()){
    		Set<PortGroupProfile> pgps = wlan.getPortProfiles();
    		for(PortGroupProfile pgp :pgps){
    			if (pgp.getDeviceType()!=oneHiveAp.getDeviceType()){
    				continue;
    			}
    			String[] models = pgp.getDeviceModelStrs();
        		if (models==null) {
        			continue;
        		}
        		boolean continueOp = true;
        		for(String key: models){
        			if (key.equals(String.valueOf(oneHiveAp.getHiveApModel()))){
        				continueOp=false;
        			}
        		}
        		if (continueOp) {
        			continue;
        		}

    			if(null != pgp.getBasicProfiles()){
					for(PortBasicProfile pbp : pgp.getBasicProfiles()){
						if(null != pbp.getAccessProfile()){
							if (pbp.getAccessProfile().getDefUserProfile() != null) {
								if (!setUserProfile.contains(pbp.getAccessProfile().getDefUserProfile().getId())) {
									cacPercent = cacPercent + pbp.getAccessProfile().getDefUserProfile().getGuarantedAirTime();
									setUserProfile.add(pbp.getAccessProfile().getDefUserProfile().getId());
								}
							}
							if (pbp.getAccessProfile().getSelfRegUserProfile() != null) {
								if (!setUserProfile.contains(pbp.getAccessProfile().getSelfRegUserProfile().getId())) {
									cacPercent = cacPercent + pbp.getAccessProfile().getSelfRegUserProfile().getGuarantedAirTime();
									setUserProfile.add(pbp.getAccessProfile().getSelfRegUserProfile().getId());
								}
							}
							if (pbp.getAccessProfile().getGuestUserProfile() != null) {
							    if (!setUserProfile.contains(pbp.getAccessProfile().getGuestUserProfile().getId())) {
							        cacPercent = cacPercent + pbp.getAccessProfile().getGuestUserProfile().getGuarantedAirTime();
							        setUserProfile.add(pbp.getAccessProfile().getGuestUserProfile().getId());
							    }
							}
							if (pbp.getAccessProfile().getAuthOkUserProfile() != null) {
								for (UserProfile tempUser : pbp.getAccessProfile().getAuthOkUserProfile()) {
									if (!setUserProfile.contains(tempUser.getId())) {
										cacPercent = cacPercent + tempUser.getGuarantedAirTime();
										setUserProfile.add(tempUser.getId());
									}
								}
							}
							if (pbp.getAccessProfile().getAuthFailUserProfile() != null) {
								for (UserProfile tempUser : pbp.getAccessProfile().getAuthFailUserProfile()) {
									if (!setUserProfile.contains(tempUser.getId())) {
										cacPercent = cacPercent + tempUser.getGuarantedAirTime();
										setUserProfile.add(tempUser.getId());
									}
								}
							}
							if (pbp.getAccessProfile().getAuthOkDataUserProfile() != null) {
								for (UserProfile tempUser : pbp.getAccessProfile().getAuthOkDataUserProfile()) {
									if (!setUserProfile.contains(tempUser.getId())) {
										cacPercent = cacPercent + tempUser.getGuarantedAirTime();
										setUserProfile.add(tempUser.getId());
									}
								}
							}
						}
					}
    			}
    		}
		}

		Set<Long> radiusServerUspId = new HashSet<>();
		if (oneHiveAp.getDeviceType()!=HiveAp.Device_TYPE_VPN_GATEWAY) {
			 if(oneHiveAp.getRadiusServerProfile() != null){
				 List<LdapServerOuUserProfile> ldapOuUserProfiles = oneHiveAp.getRadiusServerProfile().getLdapOuUserProfiles();
				 if(ldapOuUserProfiles != null && !ldapOuUserProfiles.isEmpty()){
					 for(LdapServerOuUserProfile upItem : ldapOuUserProfiles){
						if (upItem.getUserProfileId()!=null) {
							radiusServerUspId.add(upItem.getUserProfileId());
						}
					}
				 }
			 }
		}


		/** UserProfile from Network Firewall */
		if(oneHiveAp.getConfigTemplate().getFwPolicy() != null && oneHiveAp.getConfigTemplate().getFwPolicy().getRules() != null){
			for(FirewallPolicyRule rule : oneHiveAp.getConfigTemplate().getFwPolicy().getRules()){
				if(rule != null && !rule.isDisableRule() && rule.getSourceUp() != null){
					radiusServerUspId.add(rule.getSourceUp().getId());
				}
			}
		}

		/** interface eth0 */
		if(oneHiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH &&
				oneHiveAp.getEth0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				oneHiveAp.getEth0UserProfile() != null){
			radiusServerUspId.add(oneHiveAp.getEth0UserProfile().getId());
		}

		/** interface eth1 */
		if(oneHiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_BOTH &&
				oneHiveAp.getEth1().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				oneHiveAp.getEth1UserProfile() != null){
			radiusServerUspId.add(oneHiveAp.getEth1UserProfile().getId());
		}

		/** interface agg0 */
		if(oneHiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_AGG0 &&
				oneHiveAp.getAgg0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				oneHiveAp.getAgg0UserProfile() != null){
			radiusServerUspId.add(oneHiveAp.getAgg0UserProfile().getId());
		}

		/** interface red0 */
		if(oneHiveAp.getEthConfigType() == HiveAp.USE_ETHERNET_RED0 &&
				oneHiveAp.getRed0().getOperationMode() != AhInterface.OPERATION_MODE_BACKHAUL &&
				oneHiveAp.getRed0UserProfile() != null){
			radiusServerUspId.add(oneHiveAp.getRed0UserProfile().getId());
		}

		/** interface eth0|eth1 cwp */
		if(oneHiveAp.isEthCwpEnableEthCwp() || oneHiveAp.isEthCwpEnableMacAuth()){

			if(oneHiveAp.getEthCwpDefaultAuthUserProfile() != null){
				radiusServerUspId.add(oneHiveAp.getEthCwpDefaultAuthUserProfile().getId());
			}

			if(oneHiveAp.getEthCwpDefaultRegUserProfile() != null){
				radiusServerUspId.add(oneHiveAp.getEthCwpDefaultRegUserProfile().getId());
			}

			if(oneHiveAp.getEthCwpRadiusUserProfiles() != null){
				for(UserProfile up : oneHiveAp.getEthCwpRadiusUserProfiles()){
					radiusServerUspId.add(up.getId());
				}
			}
		}

		// check reassign user profile
		if (!setUserProfile.isEmpty()) {
			Set<Long> newSetUserProfile = new HashSet<>();
			for (Long upId : setUserProfile) {
				UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
				if (mapUserProfile.get(upObj.getId())==null) {
					mapUserProfile.put(upObj.getId(), upObj);
				}

				cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
						newSetUserProfile, setUserProfile, cacPercent);

			}

			for (Long upId : radiusServerUspId) {
				if (!setUserProfile.contains(upId) && !newSetUserProfile.contains(upId)) {
					UserProfile upObj = QueryUtil.findBoById(UserProfile.class, upId, this);
					newSetUserProfile.add(upId);
					if (mapUserProfileAssign.get(upId)==null) {
						mapUserProfileAssign.put(upId, upObj);
					}
					cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
							newSetUserProfile, setUserProfile, cacPercent);
				}
			}
		}

		if (cacPercent > 100) {
			return getText("error.template.guaranteedAirTime");
		}
		return "";
	}

	private int loadUserProfileFromDPRule(UserProfile upObj,
			Map<Long, UserProfile> mapUserProfileAssign,
			Set<Long> newSetUserProfile,
			Set<Long> setUserProfile, int cacPercent){
		if (upObj.isEnableAssign()) {
			for (DevicePolicyRule rule : upObj.getAssignRules()) {
				if (!setUserProfile.contains(rule.getUserProfileId()) && !newSetUserProfile.contains(rule.getUserProfileId())) {
					UserProfile upObj2 = QueryUtil.findBoById(UserProfile.class, rule.getUserProfileId(), this);
					cacPercent = cacPercent + upObj2.getGuarantedAirTime();
					newSetUserProfile.add(rule.getUserProfileId());
					if (mapUserProfileAssign.get(upObj2.getId())==null) {
						mapUserProfileAssign.put(upObj2.getId(), upObj2);
					}
					cacPercent = loadUserProfileFromDPRule(upObj, mapUserProfileAssign,
							newSetUserProfile, setUserProfile, cacPercent);
				}
			}
		}
		return cacPercent;
	}

	private String checkTotalPskGroupSize(ConfigTemplate wlan) {
		long totalUserCount = ConfigTemplateAction.getTotalPskGroupId(wlan).size();
		if (totalUserCount > LocalUserGroup.MAX_COUNT_AP_USERGROUPPERAP) {
			return getText("error.template.morePskGroupPerTemplate");
		}
		return "";
	}

	private String checkTotalPmkUserSize(ConfigTemplate wlan, HiveAp oneAp) {
		long count = ConfigTemplateAction.getTotalPmkUserSize(wlan);
		int maxPmkUserCount = oneAp.isDevicePPSK9999Support() ? LocalUser.MAX_COUNT_AP30_USERPERAP
				: LocalUser.MAX_COUNT_AP10_USERPERAP;
		int maxUserCount = oneAp.isDevicePPSK9999Support() ? LocalUser.MAX_COUNT_AP30_USERCOUNT_PERAP
				: LocalUser.MAX_COUNT_AP10_USERCOUNT_PERAP;
		if (count > maxPmkUserCount) {
			return getText("error.template.morePskUsersPerTemplate", new String[] {String.valueOf(maxPmkUserCount)});
		}

		long totalCount = ConfigTemplateAction.getTotalPSKUserSize(wlan);
		if (totalCount > maxUserCount) {
			return getText("error.template.morePskUsersPerTemplate.psk", new String[] {String.valueOf(maxUserCount)});
		}
		if (oneAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 && totalCount > LocalUser.MAX_COUNT_BR100_USERPERBR) {
			if(getActionMessages().isEmpty()) {
				addActionMessage(getText("config.localUser.br100.limit.warning"));
			}
		}

		return "";
	}

	private String checkVpnSize(ConfigTemplate wlan) {
		if (wlan.getVpnService()!=null){
			/*if (wlan.isBlnWirelessRouter()) {*/
			if (wlan.getConfigType().isRouterContained()) {
				if (wlan.getVpnService().getVpnServerType()==VpnService.IPSEC_VPN_LAYER_2){
					return getText("error.config.networkPolicy.check.connotConfig",
							new String[]{"Layer 2 IPsec VPN", wlan.getConfigName()});
				}
				if (wlan.getVpnService().getVpnGateWaysSetting().size()<1) {
					return getText("error.config.networkPolicy.check.mustConfig",
							new String[]{"VPN Gateway", "Layer 3 IPsec VPN", wlan.getConfigName()});
				}
			} else {
				if (wlan.getVpnService().getVpnServerType()==VpnService.IPSEC_VPN_LAYER_3){
					return getText("error.config.networkPolicy.check.connotConfig",
							new String[]{"Layer 3 IPsec VPN", wlan.getConfigName()});

				}
			}
			if (!wlan.getConfigType().isRouterContained() && !wlan.getConfigType().isBonjourOnly()) {
				String msg = VpnServiceAction.validateIpPoolCapability(
						wlan.getId(),wlan.getVpnService().getId());
				if (!msg.equals("")) {
					return msg;
				}
			}
		}

		return "";
	}

	private boolean isMatchingForDeviceAndNetworkPolicy(HiveAp hiveAp, ConfigTemplate networkPol, StringBuffer errorMsg){
		if (hiveAp.isBranchRouter() &&
				!networkPol.getConfigType().isRouterContained()) {
			String message = MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{networkPol.getConfigName(), "routers", "routing"});
			if(errorMsg.length() == 0){
				errorMsg.append(message);
			}else{
				errorMsg.append("<br>");
				errorMsg.append(message);
			}
			return false;
		}
		if (hiveAp.isSwitch() &&
				!networkPol.getConfigType().isSwitchContained() &&
				!networkPol.getConfigType().isBonjourOnly()) {
			String message = MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{networkPol.getConfigName(), "switches", "switching"});
			if(errorMsg.length() == 0){
				errorMsg.append(message);
			}else{
				errorMsg.append("<br>");
				errorMsg.append(message);
			}
			return false;
		}

		if (hiveAp.getDeviceType() == HiveAp.Device_TYPE_HIVEAP &&
				!networkPol.getConfigType().isWirelessContained()&&
				!networkPol.getConfigType().isBonjourOnly()) {
			String message = MgrUtil.getUserMessage("error.config.hiveAp.networkpolicy.type.notmatch",
					new String[]{networkPol.getConfigName(), "APs", "wireless access"});
			if(errorMsg.length() == 0){
				errorMsg.append(message);
			}else{
				errorMsg.append("<br>");
				errorMsg.append(message);
			}
			return false;
		}

		return true;

	}

	//fnr code for check network policy end 2011/08/18

	//for fix bug 16604, BR100 upload image auto reboot
	public boolean isUploadImageAllBr100(){
		Set<Long> init_ids = getIdListFromSession(UPDATE_INITIAL_IDs);
		if(init_ids == null || init_ids.isEmpty()){
			return false;
		}
		String sql = "select count(id) from " + HiveAp.class.getSimpleName();
		List<?> rsList = QueryUtil.executeQuery(sql, null,
				new FilterParams("id in (:s1) and hiveApModel != :s2", new Object[]{init_ids, HiveAp.HIVEAP_MODEL_BR100}));
		if(rsList == null || rsList.size() != 1)
			return false;
		try {
			long count = Long.parseLong(rsList.get(0).toString());
			return count == 0;
		} catch (NumberFormatException e) {
		}
		return false;
	}

	private boolean validateImageType(Collection<HiveAp> hiveAps) {
		boolean result = ImageManager.isSameImageType(hiveAps);
		if (!result) {
			addActionPermanentErrorMsg(MgrUtil.getUserMessage("image.distributor.imageType"));
		}
		return result;
	}

	/**
	 * See if selected signature file fit for all devices to be updated.
	 *
	 * @param devices -
	 * @param signature -
	 * @param mgmt -
	 * @return -
	 */
	private Map<HiveAp, LSevenSignatures> validateSignatureFile(
			Collection<HiveAp> devices, LSevenSignatures signature,
			L7SignatureMng mgmt) {
		String version = signature.getAhVersion();
		// 1) is selected devices using same signature file?
		Map<HiveAp, LSevenSignatures> map = new HashMap<>(
				devices.size());
		List<String> invalid = new ArrayList<>();
		for (HiveAp device : devices) {
			// platform check
			boolean isSupport = signature.isMatchDevicePlatform(device
					.getHiveApModel());
			if (!isSupport) {
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.file.multiple.platform",
						new String[] { signature.getPlatformIdString(),
								device.getHostName() }));
				return null;
			}
			// version check
			String currentVer = device.getSignatureVerString();
			if (StringUtils.isEmpty(currentVer)) {
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.current.version.empty",
						device.getHostName()));
				return null;
			}
			try {
				boolean support = signature
						.isMatchDeviceSignatureVersion(currentVer);
				if (!support) {
					invalid.add(device.getHostName());
					continue;
				}
			} catch (Exception e) {
				log.error(
						"validateSignatureFile",
						"error to compare signature config version to current version",
						e);
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compare", new String[] {
								version, device.getHostName(), currentVer }));
				return null;
			}
			map.put(device, signature);
		}
		if (!invalid.isEmpty()) {
			if (signature.isPreFujiSignatureVersion()) {
				addActionPermanentErrorMsg(MgrUtil
						.getUserMessage("error.hiveap.l7.signature.pre.fuji.compatible.m1"));
				addActionPermanentErrorMsg(MgrUtil
						.getUserMessage("error.hiveap.l7.signature.pre.fuji.compatible.m2"));
			} else {
				String sigVer = signature.getDPIEngineVersion();
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compatible.m1", sigVer));
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compatible.m2", sigVer));
			}
			// list device name only if the list < 10.
			if (invalid.size() < 10) {
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.comm.compatible.m",
						StringUtils.join(invalid, ", ")));
			}
			return null;
		}
		// 2) is patch package, and if patch support all devices with current
		// version?
		boolean isPatch = signature.isPatchPackage();
		if (isPatch) {
			for (HiveAp device : devices) {
				String currentVer = device.getSignatureVerString();
				boolean isSupport;
				try {
					isSupport = signature.isSupportVersionUpdate(currentVer);
					if (!isSupport) {
						addActionPermanentErrorMsg(MgrUtil.getUserMessage(
								"error.hiveap.l7.signature.patch.unsupport",
								new String[] { device.getHostName(),
										currentVer, version }));
						return null;
					}
				} catch (Exception e) {
					log.error(
							"validateSignatureFile",
							"error to compare signature config version to current version",
							e);
					addActionPermanentErrorMsg(MgrUtil
							.getUserMessage(
									"error.hiveap.l7.signature.compare",
									new String[] { version,
											device.getHostName(), currentVer }));
					return null;
				}
			}
		}
		return map;
	}

	private Map<HiveAp, LSevenSignatures> validateSignatureVersion(
			Collection<HiveAp> devices, List<LSevenSignatures> signatures,
			L7SignatureMng mgmt, String version) {
		Map<HiveAp, LSevenSignatures> map = new HashMap<>(
				devices.size());
		List<String> invalid = new ArrayList<>();
		LSevenSignatures sig = null;
		// 1) is selected devices has matched signature file?
		for (HiveAp device : devices) {
			// platform check
			LSevenSignatures matched = null;
			for (LSevenSignatures signature : signatures) {
				if (signature.isMatchDevicePlatform(device.getHiveApModel())) {
					matched = signature;
					break;
				}
			}
			if (null == matched) {
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.version.no.platform",
						device.getHostName()));
				return null;
			}
			sig = matched;
			// version check
			String currentVer = device.getSignatureVerString();
			if (StringUtils.isEmpty(currentVer)) {
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.current.version.empty",
						device.getHostName()));
				return null;
			}
			try {
				boolean support = matched
						.isMatchDeviceSignatureVersion(currentVer);
				if (!support) {
					invalid.add(device.getHostName());
					continue;
				}
			} catch (Exception e) {
				log.error(
						"validateSignatureVersion",
						"error to compare signature config version to current version",
						e);
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compare", new String[] {
								version, device.getHostName(), currentVer }));
				return null;
			}
			map.put(device, matched);
		}
		if (!invalid.isEmpty()) {
			if (sig.isPreFujiSignatureVersion()) {
				addActionPermanentErrorMsg(MgrUtil
						.getUserMessage("error.hiveap.l7.signature.pre.fuji.compatible.m1"));
				addActionPermanentErrorMsg(MgrUtil
						.getUserMessage("error.hiveap.l7.signature.pre.fuji.compatible.m2"));
			} else {
				String sigVer = sig.getDPIEngineVersion();
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compatible.m1", sigVer));
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compatible.m2", sigVer));
			}
			// list device name only if the list < 10.
			if (invalid.size() < 10) {
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.comm.compatible.m",
						StringUtils.join(invalid, ", ")));
			}
			return null;
		}
		// 2) is patch package, and if patch support all devices with current
		// version?
		for (HiveAp device : map.keySet()) {
			LSevenSignatures signature = map.get(device);
			if (!signature.isPatchPackage()) {
				// full package always works for devices
				continue;
			}
			String currentVer = device.getSignatureVerString();
			boolean isSupport;
			try {
				isSupport = signature.isSupportVersionUpdate(currentVer);
				if (!isSupport) {
					addActionPermanentErrorMsg(MgrUtil.getUserMessage(
							"error.hiveap.l7.signature.patch.unsupport",
							new String[] { device.getHostName(), currentVer,
									version }));
					return null;
				}
			} catch (Exception e) {
				log.error(
						"validateSignatureVersion",
						"error to compare signature config version to current version",
						e);
				addActionPermanentErrorMsg(MgrUtil.getUserMessage(
						"error.hiveap.l7.signature.compare", new String[] {
								version, device.getHostName(), currentVer }));
				return null;
			}
		}
		return map;
	}

	private List<String> getAllImageFromDS(List<Short> apModels, boolean byVersion){
		try{
			String url = "https://" + MgrUtil.getDownloadServerHost() + "/ds/imagemanage.action";
			String dsOperation;
			if(byVersion){
				dsOperation = "getAllImageVer";
			}else{
				dsOperation = "getAllImageName";
			}
			StringBuffer sBuffer = new StringBuffer();
			for(Short apModel : apModels){
				if(sBuffer.length() > 0){
					sBuffer.append(",");
				}
				sBuffer.append(apModel);
			}
			HttpCommunication httpCommunication = new HttpCommunication(url);
			List<NameValuePair> parms = new ArrayList<>();
			parms.add(new NameValuePair("dsOperation", dsOperation));
			if(sBuffer.length() > 0){
				parms.add(new NameValuePair("deviceModels", sBuffer.toString()));
			}
			HttpEntity entity = httpCommunication.sendRequestByGet(parms);
			String results = EntityUtils.toString(entity);

			results = results.replace("[", "");
			results = results.replace("]", "");
			results = results.replace("\"", "");
			results = results.replace("\r", "");
			results = results.replace("\n", "");
			String[] resArg = results.split(",");
			return Arrays.asList(resArg);
		}catch(Exception e){
			log.error("getAllImageFromDS", e);
		}
		return new ArrayList<>();
	}

	public boolean isDsEnable(){
		return MgrUtil.isEnableDownloadServer();
	}

	public boolean isImageUpgrade() {
		return imageUpgrade;
	}

	public void setImageUpgrade(boolean imageUpgrade) {
		this.imageUpgrade = imageUpgrade;
	}

	public boolean isForceImageUpgrade() {
		return forceImageUpgrade;
	}

	public void setForceImageUpgrade(boolean forceImageUpgrade) {
		this.forceImageUpgrade = forceImageUpgrade;
	}

	public boolean isCompleteCfgUpdate() {
		return completeCfgUpdate;
	}

	public void setCompleteCfgUpdate(boolean completeCfgUpdate) {
		this.completeCfgUpdate = completeCfgUpdate;
	}

	private String checkStpSettings(ConfigTemplate policy,HiveAp hiveAp){
		if(policy.getSwitchSettings() != null
				&& policy.getSwitchSettings().getStpSettings() != null){
			short stp_mode = policy.getSwitchSettings().getStpSettings().getStp_mode();
			switch(stp_mode){
				case StpSettings.STP_MODE_STP:
					if(!(hiveAp.getDeviceStpSettings().getForceVersion() == 0
							|| hiveAp.getDeviceStpSettings().getForceVersion() == DeviceStpSettings.DEFAULT_FORCE_VERSION)){
						return MgrUtil.getUserMessage("error.stp.force.version.not.support.mode",
								new String[]{Short.toString(hiveAp.getDeviceStpSettings().getForceVersion()),
									MgrUtil.getUserMessage("config.switchSettings.STPmode.stp")});
					}else{
						return "";
					}
				case StpSettings.STP_MODE_RSTP:
					if(!(hiveAp.getDeviceStpSettings().getForceVersion() == 0
						|| hiveAp.getDeviceStpSettings().getForceVersion() == 2
						|| hiveAp.getDeviceStpSettings().getForceVersion() == DeviceStpSettings.DEFAULT_FORCE_VERSION)){
						return MgrUtil.getUserMessage("error.stp.force.version.not.support.mode",
								new String[]{Short.toString(hiveAp.getDeviceStpSettings().getForceVersion()),
									MgrUtil.getUserMessage("config.switchSettings.STPmode.rstp")});
					}else{
						return "";
					}
				case StpSettings.STP_MODE_MSTP:
					if(!(hiveAp.getDeviceStpSettings().getForceVersion() == 0
						|| hiveAp.getDeviceStpSettings().getForceVersion() == 2
						|| hiveAp.getDeviceStpSettings().getForceVersion() == 3
						|| hiveAp.getDeviceStpSettings().getForceVersion() == DeviceStpSettings.DEFAULT_FORCE_VERSION)){
						return MgrUtil.getUserMessage("error.stp.force.version.not.support.mode",
								new String[]{Short.toString(hiveAp.getDeviceStpSettings().getForceVersion()),
									MgrUtil.getUserMessage("config.switchSettings.STPmode.mstp")});
					}else{
						return "";
					}
			}
		}
		return "";
	}

	private Map<Long, String> getSimpleUpdateImageIds(Set<Long> selectedIds, boolean forceUpdate){
		FilterParams filter = null;
		if (selectedIds != null && !selectedIds.isEmpty()){
			filter = new FilterParams("id in (:s1)", new Object[]{selectedIds});
		}
		List<?> resList = QueryUtil.executeQuery("select id, hiveApModel, softVer, hardwareRevision from "+HiveAp.class.getSimpleName(),
				null , filter);
		if(resList == null || resList.isEmpty()){
			return null;
		}
		
		//get all devices model
		List<Short> allDeviceModels = DevicePropertyManage.getInstance().getAllDeviceKey();
		
		//mapping between hiveApModel and image file name.
		Map<Short, HiveApImageInfo> model_ImageInfo_Map = new HashMap<>();
		for(Short apModel : allDeviceModels){
			HiveApImageInfo imageInfo = com.ah.be.config.image.ImageManager.getLatestImageName(apModel);
			if(imageInfo != null){
				model_ImageInfo_Map.put(apModel, imageInfo);
			}
		}
		
		//mapping between device ID and image Name.
		Map<Long, String> idImageMap = new HashMap<>();
		Long devcieId;
		Short hiveApModel;
		String softver, hardwareRevision;
		for(Object queryObj : resList){
			Object[] queryArg = (Object[])queryObj;
			devcieId = Long.valueOf(queryArg[0].toString());
			hiveApModel = Short.valueOf(queryArg[1].toString());
			softver = queryArg[2].toString();
			hardwareRevision = queryArg[3] != null? queryArg[3].toString() : null;
			if(hiveApModel == HiveAp.HIVEAP_MODEL_SR2124P && "01".equals(hardwareRevision)){
				hiveApModel = (short)(0 - HiveAp.HIVEAP_MODEL_SR2124P);
			}
			
			if(!model_ImageInfo_Map.containsKey(hiveApModel)){
				continue;
			}
			HiveApImageInfo imageInfo = model_ImageInfo_Map.get(hiveApModel);
			int verCompareInt = NmsUtil.compareSoftwareVersion(imageInfo.getImageVersionNum(), softver);
			if(verCompareInt > 0){
				idImageMap.put(devcieId, imageInfo.getImageName());
			}else if(verCompareInt == 0 && forceUpdate){
				idImageMap.put(devcieId, imageInfo.getImageName());
			}
		}
		
		return idImageMap;
	}
	
	private String getSimpleImageUpdateNotes(Set<Long> selectedIds){
		if(selectedIds == null || selectedIds.isEmpty()){
			return null;
		}
		
		String sqlStr = "select count(id) from "+HiveAp.class.getSimpleName();
		List<Short> series_100 = new ArrayList<Short>();
		series_100.add(HiveAp.HIVEAP_MODEL_110);
		series_100.add(HiveAp.HIVEAP_MODEL_120);
		series_100.add(HiveAp.HIVEAP_MODEL_121);
		series_100.add(HiveAp.HIVEAP_MODEL_141);
		series_100.add(HiveAp.HIVEAP_MODEL_170);
		List<?> resList = QueryUtil.executeQuery(sqlStr, null, 
				new FilterParams("id in (:s1) and softver <= :s2 and hiveApModel in (:s3)", new Object[]{
						selectedIds, "6.1.2.0", series_100
				})
		);
		int count = resList.isEmpty() ? 0 : Integer.parseInt(resList.get(0).toString());
		if(count > 0){
			return MgrUtil.getResourceString("geneva_06.update.ui.reboot.warning");
		}else{
			return null;
		}
	}
	
	private Map<Long, HiveAp> getSimpleUpdateRebootDevices(Set<Long> selectedIds){
		if(selectedIds == null || selectedIds.isEmpty()){
			return null;
		}
		//query all device.
		List<HiveAp> deviceList = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("id", selectedIds));
		if(deviceList == null || deviceList.isEmpty()){
			return null;
		}
		Map<Long, HiveAp> allDeviceMap = new HashMap<>();
		for(HiveAp device : deviceList){
			allDeviceMap.put(device.getId(), device);
		}
		
		//complete configuration upload return all device.
		if(completeCfgUpdate){
			return allDeviceMap;
		}
		
		Map<Long, HiveAp> resMap = new HashMap<>();
		
		//device need upgrade image.
		Map<Long, String> imageMap = null;
		if(imageUpgrade){
			imageMap = getSimpleUpdateImageIds(selectedIds, false);
		}else if(forceImageUpgrade){
			imageMap = getSimpleUpdateImageIds(selectedIds, true);
		}
		if(imageMap != null && !imageMap.isEmpty()){
			for(Long id : imageMap.keySet()){
				resMap.put(id, allDeviceMap.get(id));
			}
		}
		
		//simplified update need complete update.
		for(HiveAp ap : allDeviceMap.values()){
			if(ap.isNeedCompleteUpdate()){
				resMap.put(ap.getId(), ap);
			}
		}
		
		return resMap;
	}
	
	public short getSimplifiedRebootType() {
		return simplifiedRebootType;
	}

	public void setSimplifiedRebootType(short simplifiedRebootType) {
		this.simplifiedRebootType = simplifiedRebootType;
	}
	
	public String getImageUpgradeNote(){
		return getSimpleImageUpdateNotes(getIdListFromSession(UPDATE_IMAGE_SELECTED_IDs));
	}

	private Set<HiveAp> getCompleteUpdateDevices(Set<HiveAp> selectedHiveAps, HiveApUpdateSettings setting){
		if(selectedHiveAps == null || selectedHiveAps.isEmpty() || setting == null){
			return null;
		}

		if(setting.getConfigSelectType() == ConfigSelectType.full){
			return selectedHiveAps;
		}else if(setting.getConfigSelectType() == ConfigSelectType.auto){
			Set<HiveAp> results = new HashSet<>();
			for(HiveAp ap : selectedHiveAps){
				if(ap.isNeedCompleteUpdate()){
					results.add(ap);
				}
			}
			return results;
		}else {
			return null;
		}
	}

	private String getAllActionErrorMsg(){
		Collection<String> allErrors = this.getActionErrors();
		StringBuilder allMsg = new StringBuilder();
		for(String msg : allErrors){
			if(allMsg.length() == 0){
				allMsg.append(msg);
			}else{
				allMsg.append("<br>").append(msg);
			}
		}

		if(allMsg.length() > 0){
			return allMsg.toString();
		}else{
			return null;
		}
	}
	
	private JSONObject getConnectionStatusCheckRes(Set<Long> selectedIds) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put(JSON_RESULT_TYPE_KEY, this.operation + JSON_RESULT_TYPE_SUFFIX);
		
		try{
			String querySql = "select count(id) from "+HiveAp.class.getName();
			List<?> reslistMajor = QueryUtil.executeQuery(querySql, null, 
					new FilterParams("id in :s1 and connectStatus = :s2", new Object[]{selectedIds, HiveAp.CONNECT_UP_MAJOR}));
//			List<?> reslistDisconn = QueryUtil.executeQuery(querySql, null, 
//					new FilterParams("id in :s1 and connectStatus = :s2", new Object[]{selectedIds, HiveAp.CONNECT_DOWN}));
			
			long majorCounts = (Long)reslistMajor.get(0);
//			long disconnectCounts = (Long)reslistDisconn.get(0);
			String message = null;
			
//			if(majorCounts > 0 && disconnectCounts > 0){
//				message = getText("config.guid.hiveAp.update.networkMajor.disconnect.warning", 
//						new String[]{String.valueOf(disconnectCounts), String.valueOf(majorCounts)});
//			}else if(disconnectCounts > 0){
//				message = getText("config.guid.hiveAp.update.disconnect.warning", 
//						new String[]{String.valueOf(disconnectCounts)});
//			}else 
			if(majorCounts > 0){
				message = getText("warning.hiveAp.update.networkMajor");
			}
			
			if(message != null){
				jsonObj.put("msg", message);
			}
			jsonObj.put("t", true);
		}catch(Exception e){
			jsonObj.put("errorMsg", e.getMessage());
			jsonObj.put("t", false);
		}
		
		return jsonObj;
	}

}
