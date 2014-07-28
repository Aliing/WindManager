package com.ah.ui.actions.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBePerformUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.ICloudAuthCertMgmt;
import com.ah.be.cloudauth.IDMConfig;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.ga.GAConfigHepler;
import com.ah.be.ga.IGAConfigHepler;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.license.LicenseOperationTool;
import com.ah.be.topo.BeTopoModuleParameters;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitor;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorMgmt;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.OsVersion;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.performance.ActiveClientFilter;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhBandWidthSentinelHistory;
import com.ah.bo.performance.AhClientEditValues;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhClientUtil;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.config.ImportTextFileAction;
import com.ah.ui.actions.monitor.enrolledclient.tools.DeviceForClient;
import com.ah.ui.actions.monitor.enrolledclient.tools.ResponseModelServiceImpl;
import com.ah.ui.actions.monitor.enrolledclient.tools.TransXMLToObjectImpl;
import com.ah.ui.actions.monitor.enrolledclient.tools.URLUtils;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientActivityLogItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientActivityLogList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientCertificateItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientCertificateList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientDetail;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientNetworkInfo;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientProfileItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientProfileList;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientScanResultItem;
import com.ah.ui.actions.monitor.enrolledclients.entity.EnrolledClientScanResultList;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

public class ClientMonitorAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(ClientMonitorAction.class
			.getSimpleName());
	
	private final ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> clientDebugger = AhAppContainer
	.getBeTsModule().getClientMonitorMgmt();

	private String selectedClientIDStr;

	private boolean selectAll;

	private boolean clearCache;

	private AhClientSession activeClient;

	private static final String AH_CLI_SUFFIX = "\n";

	private static final String PAGE_FROM_DEVICE_MONITOR = "deviceMonitor";

	private final CacheMgmt cacheMgmt = CacheMgmt.getInstance();

	private String editUserName;

	private String editHostName;

	private String editIP;

	private String editComment1;

	private String editComment2;

	private boolean flagEditUserName;

	private boolean flagEditHostName;

	private boolean flagEditIP;

	private boolean flagEditComment1;

	private boolean flagEditComment2;

	private int cacheId;

	private String clientWatchName;

	private String selectedClientsMac;
	private String selectedClientsName;
	
	private List<CheckItem> client_trans_totalData;
	private List<CheckItem> client_trans_beData;
	private List<CheckItem> client_trans_bgData;
	private List<CheckItem> client_trans_viData;
	private List<CheckItem> client_trans_voData;
	private List<CheckItem> client_trans_mgtData;
	private List<CheckItem> client_trans_unicastData;
	private List<CheckItem> client_trans_dataOctets;
	private List<CheckItem> client_trans_lastrate;
	private List<CheckItem> client_rec_totalData;
	private List<CheckItem> client_rec_mgtData;
	private List<CheckItem> client_rec_unicastData;
	private List<CheckItem> client_rec_multicastData;
	private List<CheckItem> client_rec_broadcastData;
	private List<CheckItem> client_rec_micfailures;
	private List<CheckItem> client_rec_dataOctets;
	private List<CheckItem> client_rec_lastrate;
	private List<CheckItem> client_rssi;
	private List<CheckItem> client_signal_to_noise;
	private List<CheckItem> client_sla;
//	private List<CheckItem> client_actualSla;
	
	private List<CheckItem> client_rec_drop;
	private List<CheckItem> client_trans_drop;
	private List<CheckItem> client_bandwidth;
	private List<CheckItem> client_slacount;
	private List<CheckItem> client_rec_airTime;
	private List<CheckItem> client_trans_airTime;
	
	private List<CheckItem> client_score;
	private List<CheckItem> client_radio_score;
	private List<CheckItem> client_ipnetwork_score;
	private List<CheckItem> client_application_score;

	private List<String> client_rec_rateTypeList;
	private List<String> client_rec_dateTimeList;
	private List<String> client_trans_rateTypeList;
	private List<String> client_trans_dateTimeList;
 	
	private Map<String, List<CheckItem>> client_rec_rate_dis;
	private List<TextItem> client_rec_rate_succ_dis;
	private Map<String, List<CheckItem>> client_trans_rate_dis;
	private List<TextItem> client_trans_rate_succ_dis;
	
//	private List<TextItem> client_trans_total_rate_succ_dis=null;
//	private List<TextItem> client_rec_total_rate_succ_dis=null;
	
	private String swf, width, height, application, bgcolor;
	
	private int					clientRefreshInterval;
	
	private boolean				disableClientRefresh;
	
	private final String		CLIENTREFRESH_ENABLE		= "enableClientRefresh";

	private final String		CLIENTREFRESH_DISABLE		= "disableClientRefresh";

	private String				clientRefreshFlag			= CLIENTREFRESH_DISABLE;
	
	private Long				clientRefreshFilter;
	
	private String				client_os_option55;
	
	public static final int RESPONSE_CODE_NOT_DATA_AVAILABLE = 400;


	public String getClient_os_option55() {
		return client_os_option55;
	}

	public void setClient_os_option55(String client_os_option55) {
		this.client_os_option55 = client_os_option55;
	}

	private String				client_os_type;

	public String getClient_os_type() {
		return client_os_type;
	}

	public void setClient_os_type(String client_os_type) {
		this.client_os_type = client_os_type;
	}

	private boolean				append_marking;
	
	public boolean isAppend_marking() {
		return append_marking;
	}

	public void setAppend_marking(boolean append_marking) {
		this.append_marking = append_marking;
	}


	/**
	 * @see com.opensymphony.xwork2.ActionSupport#execute()
	 */
	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if (null != operation
					&& !("search".equals(operation) && !isBlnInnerSearch())
					&& !("showDetail".equals(operation))) {
				listType = getSessionListType();
			}
			
			if ("showDetail".equals(operation)) {
				// refresh listType if show detail from 'Device monitor' page(like AP monitor, Switch monitor page).
				if (PAGE_FROM_DEVICE_MONITOR.equals(pageFrom)) {
					MgrUtil.setSessionAttribute(boClass.getSimpleName() + "ListType", getListType());
				}
				// get listType from session, in order to stay on 'Active Clients' menu if show detail from 'Active Clients' list.
				if ("wired".equals(getSessionListType())) {
					setSelectedL2Feature(L2_FEATURE_WIREDCLIENT);
					tableId = HmTableColumn.TABLE_WIRED_CLIENTS;
				} else if ("wireless".equals(getSessionListType())) {
					setSelectedL2Feature(L2_FEATURE_WIRELESSCLIENT);
					tableId = HmTableColumn.TABLE_WIRELESS_CLIENTS;
				}
			} else {
				if (isWiredClientList()) {
					setSelectedL2Feature(L2_FEATURE_WIREDCLIENT);
					tableId = HmTableColumn.TABLE_WIRED_CLIENTS;
				} else if (isWirelessClientList()) {
					setSelectedL2Feature(L2_FEATURE_WIRELESSCLIENT);
					tableId = HmTableColumn.TABLE_WIRELESS_CLIENTS;
				}
			}
			
			if (null == operation) {
				resetFilterParams();
				String rtnStr = prepareActiveClientList(true);
				MgrUtil.setSessionAttribute(boClass.getSimpleName() + "ListType", getListType());
				return rtnStr;
			} else if ("deauthClient".equals(operation)) {
				boolean isSuccess = false;

				try {
					if (deauthClientSelect == DEAUTH_CLIENT_ALL) {
						// consider for VHM, deauthAllClients just for home
						// domain
						isSuccess = deauthAllClients();
					} else if (deauthClientSelect == DEAUTH_CLIENT_SELECTED) {
						List<Long> clientIDs = getSelectedClientIds();

						if (clientIDs == null || clientIDs.isEmpty()) {
							addActionError(MgrUtil.getUserMessage("action.error.no.item.to.operation"));
							return SUCCESS;
						}

						isSuccess = deauthClient(clientIDs);
					}
				} catch (Exception e) {
					log.error("execute",
							"deauthClient operation catch exception", e);
					isSuccess = false;
				}

				jsonObject = new JSONObject();
				jsonObject.put("success", isSuccess);
				jsonObject.put("listType", getSessionListType());
				return "json";
			} else if("showEnrollStatus".equals(operation)){
				jsonObject = new JSONObject();
				boolean isSuccess;
				try {
					List<Long> clientIDs = getSelectedClientIds();

					if (clientIDs == null || clientIDs.isEmpty()) {
						jsonObject = new JSONObject();
						jsonObject.put("suc", false);
						jsonObject.put("err", MgrUtil.getUserMessage("action.error.no.item.to.operation"));
						return "json";
					}

					isSuccess = showEnrollStatus(clientIDs);
				} catch (Exception e) {
					log.error("execute",
							"show enroll status operation catch exception", e);
					isSuccess = false;
				}
				if(isSuccess){
					jsonObject.put("suc", isSuccess);
				}else{
					jsonObject.put("suc", false);
				}
				
				return "json";
			}else if ("deauthClientDetail".equals(operation)) {
				try {
//					AhClientSession client = QueryUtil.findBoById(AhClientSession.class,
//							id, this);
					AhClientSession client = DBOperationUtil.findBoById(AhClientSession.class,
							id);
					if (null == client) {
						jsonObject = new JSONObject();
						jsonObject.put("success", false);
						jsonObject.put("message", MgrUtil.getUserMessage("error.clientmonitor.entry.notExist"));
						return "json";
					}
					
					List<Long> clientIDList = new ArrayList<Long>();
					clientIDList.add(id);
					boolean isSuccess = deauthClient(clientIDList);
					
					jsonObject = new JSONObject();
					jsonObject.put("success", isSuccess);
					jsonObject.put("listType", getSessionListType());
					return "json";
				} catch (Exception e) {
					log.error("execute",
							"deauthClient operation catch exception", e);
					
					jsonObject = new JSONObject();
					jsonObject.put("success", false);
					jsonObject.put("message", MgrUtil.getUserMessage("unable.to.deauth.client.message") + e.getMessage());
					return "json";
				}
			} else if ("refreshClient".equals(operation)) {
				int result;

				try {
					if (selectAll) {
						Collection<SimpleHiveAp> apList = cacheMgmt
								.getManagedApList(getDomainId());

						result = HmBePerformUtil
								.syncRequestActiveClients(apList);
					} else {
						List<Long> clientIDs = getSelectedClientIds();
						Collection<SimpleHiveAp> apList = getHiveAPList(clientIDs);

						result = HmBePerformUtil
								.syncRequestActiveClients(apList);
					}
					
					// sleep 5 seconds, waiting for client query response be inserted into DB (to fix bug: clients lost when do refresh)
					MgrUtil.sleepTime(5);
					
				} catch (Exception e) {
					log.error("execute",
							"refreshClient operation catch exception", e);
					result = 1;
				}

				jsonObject = new JSONObject();
				jsonObject.put("success", result);
				jsonObject.put("listType", getSessionListType());
				return "json";
			} else if ("showAcmScanResultPanel".equals(operation)) {
				jsonArray = new JSONArray();
				getEnrolledClientDetails_ScanResult(currentCustomId,currentDeMacAddress);
				return "json";
			} else if ("showAcmActivityLogPanel".equals(operation)) {
				jsonArray = new JSONArray();
				getEnrolledClientDetails_ActivityLog(currentCustomId,currentDeMacAddress);
				return "json";
			} else if ("showAcmProfilePanel".equals(operation)) {
				jsonArray = new JSONArray();
				getEnrolledClientDetails_Profile(currentCustomId,currentDeMacAddress);
				return "json";
			} else if ("showAcmCertificatePanel".equals(operation)) {
				jsonArray = new JSONArray();
				getEnrolledClientDetails_Certificate(currentCustomId,currentDeMacAddress);
				return "json";
			} else if ("showDetail".equals(operation)) {
				log.info("execute", "show detail, Client id:" + id);
				activeClient = getSelectedClient();
				if (null == activeClient
						|| activeClient.getConnectstate() == AhClientSession.CONNECT_STATE_DOWN) {
					addActionError(MgrUtil
							.getUserMessage("error.clientmonitor.entry.notExist"));
					return INPUT;
				}
				if (activeClient.getStartTimeStamp()<(System.currentTimeMillis()-1000l*60*60*24*200)) {
					addActionMessage(MgrUtil.getUserMessage("message.client.time.invalid"));
				}
				prepareDetails(activeClient);
				prepareNewReportData(activeClient);
				
				if (getEnableClientManagement() && activeClient.isWirelessClient()) {
					try {
					
						currentCustomId = getCustomerIdFromRemote(activeClient.getOwner());
						if(getEnrolledClientFlag(currentCustomId, activeClient.getClientMac())){
							//add to make a choice on using google map key 
							validateGoogleMapKey();
							getEnrolledClientDetails(currentCustomId, activeClient.getClientMac());
						}
					} catch (Exception e) {
						log.error(e);
					}
				}
				if (newReportDataList!=null && !newReportDataList.isEmpty()){
					MgrUtil.setSessionAttribute("LAST_CLIENT_HISTORY_SESSION_NEW", newReportDataList);
					prepareFlash("activeClientNewSwf");
				} else {
					if (association_stats!=null && association_stats.size()>1){
						MgrUtil.setSessionAttribute("LAST_CLIENT_HISTORY_SESSION", association_stats);
						MgrUtil.setSessionAttribute("LAST_CLIENT_HISTORY_CURRENT", activeClient);
						prepareFlash("activeClientSwf");
					} else if (association_stats !=null && association_stats.size()==1){
						oneAssociation = association_stats.get(0);
					} else {
						oneAssociation = null;
					}
				}
				return INPUT;
			} else if ("getFlashDataNew".equals(operation)) {
				log.info("execute", "get flash data, Client id:" + id);
				newReportDataList = (List<AhClientStats>)MgrUtil.getSessionAttribute("LAST_CLIENT_HISTORY_SESSION_NEW");
				initFlashDataNew();
				MgrUtil.removeSessionAttribute("LAST_CLIENT_HISTORY_SESSION_NEW");
				return "activeClientSwf";
			} else if ("getFlashData".equals(operation)) {
				log.info("execute", "get flash data, Client id:" + id);
				association_stats = (List<AhAssociation>)MgrUtil.getSessionAttribute("LAST_CLIENT_HISTORY_SESSION");
				initFlashData();
				MgrUtil.removeSessionAttribute("LAST_CLIENT_HISTORY_SESSION");
				return "activeClientSwf";
			} else if ("syncClientInfo".equals(operation)) {
				log.info("execute",
						"synchronize client info from device, clientID :" + id);
				updateClientInfo();
				return "json";
			} else if ("requestFilterValues".equals(operation)) {
				log.info("execute", "requestFilterValues operation");
				prepareFilterValues();
				return "json";
			} else if ("view".equals(operation)) {
			    prepareViewOperation();
				return prepareActiveClientList(true);
			} else if ("search".equals(operation)) {
				log.info("execute", "search operation");
				prepareSearchOperation();
				saveToSessionFilterList();
				String rtnStr = prepareActiveClientList(true);
				if (!isBlnInnerSearch()) {
					MgrUtil.setSessionAttribute(boClass.getSimpleName() + "ListType", getListType());
				}
				return rtnStr;
			} else if ("initEditValues".equals(operation)) {
				prepareEditValues();
				return "json";
			} else if ("saveEditResults".equals(operation)) {
				if (!(flagEditUserName || flagEditHostName || flagEditIP
						|| flagEditComment1 || flagEditComment2)) {
					return prepareActiveClientList(false);
				}

				try {
					saveEditResults();

					addActionMessage(MgrUtil.getUserMessage("message.client.modified.success"));
					// }
				} catch (Exception e) {
					log.error("execute", "save edit results catch exception.",
							e);
					addActionError(MgrUtil.getUserMessage("action.error.modify.client.fail"));
				}

				return prepareActiveClientList(true);
			} else if ("removeFilter".equals(operation)) {
				// log.info("execute", "removeFilter operation");
//				prepareSearchOperation();
				resetFilterParams();
				removeActiveClientFilter();
				return prepareActiveClientList(true);
			} else if ("pollClientList".equals(operation)) {
				ClientPagingCache clientPagingCache = getClientListCache();
				jsonArray = new JSONArray(clientPagingCache.getUpdates(cacheId));
				return "json";
			} else if ("refreshFromCache".equals(operation)) {
				// refresh client list from cache
				return prepareActiveClientList(false);
			} else if ("newClientWatch".equals(operation)) {
				clearErrorsAndMessages();
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());

				return "newClientWatch";
			} else if ("addToClientWatch".equals(operation) || "addToComputerCart".equals(operation)) {
				if (selectAll) {
//					long clientCount = QueryUtil.findRowCount(
//							AhClientSession.class, new FilterParams(
//									"connectstate=:s1 and owner.id=:s2",
//									new Object[] {
//											AhClientSession.CONNECT_STATE_UP,
//											getDomainId() }));
					long clientCount = DBOperationUtil.findRowCount(AhClientSession.class, new FilterParams(
									"connectstate=? and owner=?",
									new Object[] {
											AhClientSession.CONNECT_STATE_UP,
											getDomainId() }));
					if (clientCount > LocationClientWatch.MAXCOUNT_STATION) {
						addActionError(MgrUtil.getUserMessage("action.error.select.client.exceed.mac.entry", String.valueOf(LocationClientWatch.MAXCOUNT_STATION)));
						return prepareActiveClientList(false);
					}
				}

				// convert clientid->clientmac
				if (selectedClientIDStr.isEmpty()) {
					addActionError(MgrUtil.getUserMessage("action.error.select.one.item"));
					return prepareActiveClientList(false);
				}

				List<Long> clientIDList = getSelectedClientIds();

				if (clientIDList.size() > LocationClientWatch.MAXCOUNT_STATION) {
					addActionError(MgrUtil.getUserMessage("action.error.select.client.exceed.mac.entry",
							 String.valueOf(LocationClientWatch.MAXCOUNT_STATION)));
					return prepareActiveClientList(false);
				}

				selectedClientsMac = "";
				selectedClientsName = "";
				for (Long clientID : clientIDList) {
//					List<AhClientSession> list = QueryUtil.executeQuery(
//							AhClientSession.class, null, new FilterParams("id",
//									clientID));
					List<AhClientSession> list = DBOperationUtil.executeQuery(
							AhClientSession.class, null, new FilterParams("id",
									clientID));
					String clientMac = list.get(0).getClientMac();
					selectedClientsMac += "," + clientMac;
					String clientName = list.get(0).getClientHostname();
					selectedClientsName += "," + (clientName == null ? "" : clientName);
				}
				selectedClientsMac = selectedClientsMac.substring(1); //get rid of the first ','
				selectedClientsName = selectedClientsName.substring(1); //get rid of the first ','

				if (selectedClientsMac.split(",").length != selectedClientsName.split(",").length) {
					addActionError(MgrUtil.getUserMessage("action.error.select.client.refresh"));
					log.warn(operation, "selectedClientsName is " + selectedClientsName);
					log.warn(operation, "selectedClientsMac is " + selectedClientsMac);
					return prepareActiveClientList(false);
				}
				
				addLstTitle(getSelectedL2Feature().getDescription());
				addLstForward(getSelectedL2Feature().getKey());
				return operation;
			} else if ("updateRefreshSetting".equals(operation)) {
				boolean isSucc = updateClientRefresh();
				jsonObject = new JSONObject();
				jsonObject.put("success", isSucc);
				return "json";
			} else if ("updateOSVersion".equals(operation)) {
				String versionName = new String(client_os_type.getBytes("iso-8859-1"), "utf-8");
				HmDomain gDomain = QueryUtil.findBoByAttribute(HmDomain.class,
						"domainName", HmDomain.GLOBAL_DOMAIN);
				OsVersion osVersion = QueryUtil.findBoByAttribute(
						OsVersion.class, "option55", client_os_option55,
						gDomain.getId());
				OsVersion osVersion_home = null;
				List<OsVersion> osVersion_home_list = QueryUtil.executeQuery(
						OsVersion.class, null, new FilterParams(
								"option55 = :s1 AND owner.id = :s2",
								new Object[] { client_os_option55,
								getDomainId() }));
				if (!osVersion_home_list.isEmpty()) {
					osVersion_home = osVersion_home_list.get(0);
				}

				if (userContext.getUserName().equalsIgnoreCase(HmUser.ADMIN_USER) && append_marking) {
					appendContentToFile(versionName, client_os_option55);
					if (osVersion == null && osVersion_home == null) {
						osVersion = new OsVersion();
						osVersion.setOwner(gDomain);
						osVersion.setOption55(client_os_option55);
						osVersion.setOsVersion(versionName);
						QueryUtil.createBo(osVersion);
					} else if (osVersion == null && osVersion_home != null) {
						osVersion_home.setOwner(gDomain);
						osVersion_home.setOsVersion(versionName);
						QueryUtil.updateBo(osVersion_home);
						cacheMgmt.removeClientOsInfoToCache(versionName,client_os_option55, getDomain());
						ReportCacheMgmt.getInstance().option55ToOsInfoUpdateEvent(client_os_option55, getDomain());
					} else {
						osVersion.setOsVersion(versionName);
						QueryUtil.updateBo(osVersion);
					}
					cacheMgmt.updateClientOsInfoToCache(versionName, client_os_option55, gDomain);
					ReportCacheMgmt.getInstance().option55ToOsInfoUpdateEvent(client_os_option55, gDomain);
					updateFileVersion();

					// compress file
					String strCmd = "";
					StringBuffer strCmdBuf = new StringBuffer();
					strCmdBuf.append("tar zcvf ");
					strCmdBuf.append(ImportTextFileAction.OS_VERSION_FILE_PATH
							+ ImportTextFileAction.OS_VERSION_FILE_NAME_TAR);
					strCmdBuf.append(" -C ");
					strCmdBuf.append(ImportTextFileAction.OS_VERSION_FILE_PATH);
					strCmdBuf.append(" "
							+ ImportTextFileAction.OS_VERSION_FILE_NAME);
					strCmd = strCmdBuf.toString();
					boolean compressResult = BeAdminCentOSTools
							.exeSysCmd(strCmd);
					if (!compressResult) {
						addActionError(MgrUtil.getUserMessage(
								"error.file.upload.fail",
								ImportTextFileAction.OS_VERSION_FILE_NAME_TAR));
					}
				} else if (userContext.getUserName().equalsIgnoreCase(HmUser.ADMIN_USER) && !append_marking) {
					if (osVersion == null && osVersion_home == null) {
						osVersion = new OsVersion();
						osVersion.setOwner(getDomain());
						osVersion.setOption55(client_os_option55);
						osVersion.setOsVersion(versionName);
						QueryUtil.createBo(osVersion);
					} else if (osVersion == null && osVersion_home != null && userContext.getUserName().equalsIgnoreCase(
									HmUser.ADMIN_USER)) {
						osVersion_home.setOsVersion(versionName);
						osVersion_home.setOwner(getDomain());
						QueryUtil.updateBo(osVersion_home);
						
					} else {
						QueryUtil.removeBo(OsVersion.class, osVersion.getId());
						deleteContentFromFile(client_os_option55);
						osVersion_home = new OsVersion();
						osVersion_home.setOwner(getDomain());
						osVersion_home.setOption55(client_os_option55);
						osVersion_home.setOsVersion(versionName);
						QueryUtil.createBo(osVersion_home);
						
						updateFileVersion();

						// compress file
						String strCmd = "";
						StringBuffer strCmdBuf = new StringBuffer();
						strCmdBuf.append("tar zcvf ");
						strCmdBuf
								.append(ImportTextFileAction.OS_VERSION_FILE_PATH
										+ ImportTextFileAction.OS_VERSION_FILE_NAME_TAR);
						strCmdBuf.append(" -C ");
						strCmdBuf
								.append(ImportTextFileAction.OS_VERSION_FILE_PATH);
						strCmdBuf.append(" "
								+ ImportTextFileAction.OS_VERSION_FILE_NAME);
						strCmd = strCmdBuf.toString();
						boolean compressResult = BeAdminCentOSTools
								.exeSysCmd(strCmd);
						if (!compressResult) {
							addActionError(MgrUtil.getUserMessage("error.file.upload.fail",
											ImportTextFileAction.OS_VERSION_FILE_NAME_TAR));
						}
					}
					cacheMgmt.updateClientOsInfoToCache(versionName,
							client_os_option55, getDomain());
					ReportCacheMgmt.getInstance().option55ToOsInfoUpdateEvent(client_os_option55, getDomain());
				} else {
					if (osVersion_home == null) {
						osVersion_home = new OsVersion();
						osVersion_home.setOwner(getDomain());
						osVersion_home.setOption55(client_os_option55);
						osVersion_home.setOsVersion(versionName);
						QueryUtil.createBo(osVersion_home);
					} else {
						osVersion_home.setOsVersion(versionName);
						QueryUtil.updateBo(osVersion_home);
					}
					cacheMgmt.updateClientOsInfoToCache(versionName,
							client_os_option55, getDomain());
					ReportCacheMgmt.getInstance().option55ToOsInfoUpdateEvent(client_os_option55, getDomain());
				}

				int result = DBOperationUtil.executeUpdate("update ah_clientsession set clientosinfo = ? where owner = ? and connectstate = ? and os_option55 = ?",
						new Object[] {
						versionName, getDomainId(),
						AhClientSession.CONNECT_STATE_UP, client_os_option55});
				jsonObject = new JSONObject();
				if(result < 0){
					jsonObject.put("succ",false);
				}else{
					jsonObject.put("succ",true);
				}
				return prepareActiveClientList(true);
			} else if ("validateOSVersion".equals(operation)) {
				int results = 0;
				jsonObject = new JSONObject();
				String os_version = null;
				String versionName = new String(client_os_type.getBytes("iso-8859-1"), "utf-8");
				if(versionName.equalsIgnoreCase("unknown") || versionName.equalsIgnoreCase("null") || versionName.trim().equalsIgnoreCase("")){
					results = 1;
				}else{
					os_version = cacheMgmt.getClientOsInfoFromCacheByOsVersion(versionName,getDomain());
				}
				
				if (os_version != null){
					results = 2;
				}else{
					String exportFileName = "os_dhcp_fingerprints.txt";
					File file = new File(AhDirTools.getOsDetectionDir() + exportFileName);
					
					String defaultFileName = "os_dhcp_fingerprints_default.txt";
					File defaultFile = new File(AhDirTools.getOsDetectionDir() + defaultFileName);
					
					if(!(file.exists()) && !(defaultFile.exists())){
						results = 3;	//default configuration file is not exists
					}
				}
			
				jsonObject.put("results", results);
				return "json";
			} else if ("showTsinfo".equals(operation)){
				jsonObject = new JSONObject();
				String ssid = null;
				String macAddress = null;
				String msg = null;
				String apMacAdd = null;
				
				List<Long> list = getSelectedClientIds();
				if(list != null && !list.isEmpty() && list.size() == 1){
					AhClientSession clientInfo = DBOperationUtil.findBoById(AhClientSession.class, list.get(0));
					if(clientInfo != null){
						macAddress = clientInfo.getClientMac();
						ssid = clientInfo.getClientSSID();
						apMacAdd = clientInfo.getApMac();
						
						SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(apMacAdd);
						if(ap.getHiveApModel() == HiveAp.HIVEAP_MODEL_370 ||
								ap.getHiveApModel() == HiveAp.HIVEAP_MODEL_390){
							msg = MgrUtil.getUserMessage("monitor.activeClient.operation.show.tsinfo.error");
							jsonObject.put("errMsg", msg);
							return "json";
						}
						if(NmsUtil.compareSoftwareVersion(ap.getSoftVer(),"6.0.2.0") >= 0 
								&&	ap.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
							BeCommunicationEvent result = sendSyncCliRequest(
									ap, new String[] {AhCliFactory.getTsInfoForSSID(ssid,formatMac(macAddress))}, BeCliEvent.CLITYPE_NORMAL,
									BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000);
							msg = BeTopoModuleUtil.parseCliRequestResult(result);
							boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
							if(isSuccess){
								clientTsinfo = getResultMessage(msg);
								return "clientTsInfoDlgJson";
							}else{
								jsonObject.put("errMsg", msg);
								return "json";
							}
						}else{
							msg = MgrUtil.getUserMessage("error.mdm.object.lowversion","6.0.2.0");
							jsonObject.put("errMsg", msg);
							return "json";
						}
					}
				}
				
				msg = MgrUtil.getUserMessage("error.capwap.server.nofsm");
				jsonObject.put("errMsg", msg);
				jsonObject.put("succ", false);
				return "json";
			} else if ("deleteTsinfo".equals(operation)){
				jsonObject = new JSONObject();
				String ssid = null;
				String macAddress = null;
				String msg = null;
				String apMacAdd = null;
				JSONArray tidList = new JSONArray();
				
				List<Long> list = getSelectedClientIds();
				if(list != null && !list.isEmpty() && list.size() == 1){
					AhClientSession clientInfo = DBOperationUtil.findBoById(AhClientSession.class, list.get(0));
					if(clientInfo != null){
						macAddress = clientInfo.getClientMac();
						ssid = clientInfo.getClientSSID();
						apMacAdd = clientInfo.getApMac();
						SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(apMacAdd);
						if(NmsUtil.compareSoftwareVersion(ap.getSoftVer(),"6.0.2.0") >= 0 
								&&	ap.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
							if(tids != null && tids.length() > 0){
								for(int number : getTidNumber()){
									BeCommunicationEvent result = sendSyncCliRequest(
											ap, new String[] {AhCliFactory.deleteTsInfo(ssid,formatMac(macAddress),number)}, BeCliEvent.CLITYPE_NORMAL,
											BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000);
									boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);
									msg = BeTopoModuleUtil.parseCliRequestResult(result);
									if(isSuccess){
										tidList.put(number);
									}else{
										jsonObject.put("errMsg", msg);
										jsonObject.put("succ", false);
										return "json";
									}
								}
									
								jsonObject.put("succ", true);
								jsonObject.put("deletedId", tidList);
								return "json";
							}
						}else{
							msg = "device "+ formatMac(macAddress) +" "+ MgrUtil.getUserMessage("error.mdm.object.lowversion","6.0.2.0");
							jsonObject.put("errMsg", msg);
							jsonObject.put("succ", false);
							return "json";
						}
					}
					
				}
				msg = MgrUtil.getUserMessage("error.capwap.server.nofsm");
				jsonObject.put("errMsg", msg);
				jsonObject.put("succ", false);
				return "json";
			} else {
				// auto refresh setting start
				baseCustomizationOperationJson();
				if (jsonObject != null && jsonObject.length() > 0) {
					return "json";
				}
				// auto refresh setting end
				baseOperation();
//				HmBePerformUtil.syncRequestActiveClients(cacheMgmt.getManagedApList(getDomainId()));
				return prepareActiveClientList(true);
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
  

	private void prepareViewOperation() {
        List<Object> lstCondition = new ArrayList<Object>();

        // get active clients
        String searchSQL = "connectstate=?";
        lstCondition.add(AhClientSession.CONNECT_STATE_UP);

        if (StringUtils.isNotBlank(mapContainerId)) {
            try {
                Long mapId = Long.parseLong(mapContainerId);

                List<SimpleHiveAp> apList = cacheMgmt.getApListByMapContainer(mapId == -1 ? null
                        : mapId);
                // get the sub map
                Set<Long> subMapIds = BoMgmt.getMapMgmt().getContainerDownIds(mapId);
                for (Long subMapId : subMapIds) {
                    List<SimpleHiveAp> list = cacheMgmt
                            .getApListByMapContainer(subMapId == -1 ? null : subMapId);
                    if (null != list) {
                        apList.addAll(list);
                    }
                }
                searchSQL = searchSQL + " AND apMac in ('0'";

                for (SimpleHiveAp ap : apList) {
                    searchSQL += ",?";
                    lstCondition.add(ap.getMacAddress());
                }
                searchSQL += ")";
            } catch (NumberFormatException e) {
                log.error("prepareViewOperation", "Error to formate the String value to Number.", e);
            }
        }

        if (lstCondition.isEmpty()) {
            filterParams = null;
            return;
        }

        filterParams = new FilterParams(searchSQL, lstCondition.toArray());
        setSessionFiltering();

    }
	/**
	 * update client refresh interval
	 * 
	 * @return -
	 */
	private boolean updateClientRefresh() {
		// update db
		HMServicesSettings bo;
		boolean isUpdate = true;

		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams("owner.id",getDomainId()));
		if (list.isEmpty()) {
			bo = new HMServicesSettings();
			bo.setOwner(getDomain());
			isUpdate = false;
		} else {
			bo = list.get(0);
		}

		boolean isEnableRefresh = clientRefreshFlag.equals(CLIENTREFRESH_ENABLE);
		bo.setEnableClientRefresh(isEnableRefresh);
		if (isEnableRefresh) {
			bo.setRefreshInterval(clientRefreshInterval);
			ActiveClientFilter clientFilter = QueryUtil.findBoById(
					ActiveClientFilter.class, clientRefreshFilter);
			bo.setRefreshFilterName(clientFilter == null ? null : clientFilter.getFilterName());
		}

		try {
			if (isUpdate) {
				QueryUtil.updateBo(bo);
			} else {
				createBo(bo);
			}

			BoObserver.notifyListeners(new BoEvent<HmDomain>(getDomain(), BoEventType.UPDATED));

			return true;
		} catch (Exception e) {
			log.error("updateClientRefresh",
					"Update active client settings catch exception!", e);
			return false;
		}
	}
	
	/**
	 * disabled client refresh interval field value will be reset to 0 when execute operation
	 */
	private void initClientRefreshInterval() {
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams("owner.id",getDomainId()));
		if (list.isEmpty()) {
			// set default value
			clientRefreshInterval = 60;
			clientRefreshFilter = (long) -2;
			disableClientRefresh = true;

			return;
		}

		HMServicesSettings currentSetting = list.get(0);
		clientRefreshInterval = currentSetting.getRefreshInterval();
		clientRefreshFlag = currentSetting.isEnableClientRefresh() ? CLIENTREFRESH_ENABLE
				: CLIENTREFRESH_DISABLE;
		disableClientRefresh = !currentSetting.isEnableClientRefresh();

		if (currentSetting.getRefreshFilterName() == null
				|| currentSetting.getRefreshFilterName().trim().isEmpty()) {
			clientRefreshFilter = (long) -2;
		} else {
			ActiveClientFilter filter = QueryUtil.findBoByAttribute(
					ActiveClientFilter.class, "filterName", currentSetting.getRefreshFilterName(),
					domainId);
			if (filter == null) {
				clientRefreshFilter = (long) -2;
			} else {
				clientRefreshFilter = filter.getId();
			}
		}
	}
	
	public String getChangedName() {
		return macAddress.replace("\\", "\\\\").replace("'", "\\'");
	}
	
	protected void prepareFlash(String swfname) {
		swf = swfname;
		width = "100%";
		height = "420";
		application = swfname;
		bgcolor = "ffffff";
	}
	
	public void initFlashData(){
		if (association_stats!=null) {
			AhAssociation tmpAhAssociationValue = new AhAssociation();
			client_trans_totalData = new ArrayList<CheckItem>();
			client_trans_beData = new ArrayList<CheckItem>();
			client_trans_bgData = new ArrayList<CheckItem>();
			client_trans_viData = new ArrayList<CheckItem>();
			client_trans_voData = new ArrayList<CheckItem>();
			client_trans_mgtData = new ArrayList<CheckItem>();
			client_trans_unicastData = new ArrayList<CheckItem>();
			client_trans_dataOctets = new ArrayList<CheckItem>();
			client_trans_lastrate = new ArrayList<CheckItem>();
			client_rec_totalData = new ArrayList<CheckItem>();
			client_rec_mgtData = new ArrayList<CheckItem>();
			client_rec_unicastData = new ArrayList<CheckItem>();
			client_rec_multicastData = new ArrayList<CheckItem>();
			client_rec_broadcastData = new ArrayList<CheckItem>();
			client_rec_micfailures = new ArrayList<CheckItem>();
			client_rec_dataOctets = new ArrayList<CheckItem>();
			client_rec_lastrate = new ArrayList<CheckItem>();
			client_rssi = new ArrayList<CheckItem>();
			client_signal_to_noise = new ArrayList<CheckItem>();
			client_sla = new ArrayList<CheckItem>();
//			client_actualSla = new ArrayList<CheckItem>();
			
			for (int i = 0; i < association_stats.size(); i++) {
				AhAssociation tmpClass = association_stats.get(i);
				if (i==0){
					tmpAhAssociationValue = tmpClass;
					String formatTime = AhDateTimeUtil.getSpecifyDateTimeReport(
							tmpClass.getTimeStamp().getTime(), getUserTimeZone());
					client_trans_totalData.add(new CheckItem((long) 0, formatTime));
					client_trans_beData.add(new CheckItem((long) 0, formatTime));
					client_trans_bgData.add(new CheckItem((long) 0, formatTime));
					client_trans_viData.add(new CheckItem((long) 0, formatTime));
					client_trans_voData.add(new CheckItem((long) 0, formatTime));
					client_trans_mgtData.add(new CheckItem((long) 0, formatTime));
					client_trans_unicastData.add(new CheckItem((long) 0, formatTime));
					client_trans_dataOctets.add(new CheckItem((long) 0, formatTime));
					client_trans_lastrate.add(new CheckItem((long) 0, formatTime));
					client_rec_totalData.add(new CheckItem((long) 0, formatTime));
					client_rec_mgtData.add(new CheckItem((long) 0, formatTime));
					client_rec_unicastData.add(new CheckItem((long) 0, formatTime));
					client_rec_multicastData.add(new CheckItem((long) 0, formatTime));
					client_rec_broadcastData.add(new CheckItem((long) 0, formatTime));
					client_rec_micfailures.add(new CheckItem((long) 0, formatTime));
					client_rec_dataOctets.add(new CheckItem((long) 0, formatTime));
					client_rec_lastrate.add(new CheckItem((long) 0, formatTime));
					client_rssi.add(new CheckItem((long) 0, formatTime));
					client_signal_to_noise.add(new CheckItem((long) 0, formatTime));
				} else {
					String formatTime = AhDateTimeUtil.getSpecifyDateTimeReport(
							tmpClass.getTimeStamp().getTime(), getUserTimeZone());
					// transmit totalData
					long tmpCount = checkValueLessThanZero(tmpClass
							.getClientTxDataFrames(), tmpAhAssociationValue
							.getClientTxDataFrames());
					client_trans_totalData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_beData
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxBeDataFrames(),
							tmpAhAssociationValue.getClientTxBeDataFrames());
					client_trans_beData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_bgData
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxBgDataFrames(),
							tmpAhAssociationValue.getClientTxBgDataFrames());
					client_trans_bgData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_viData
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxViDataFrames(),
							tmpAhAssociationValue.getClientTxViDataFrames());
					client_trans_viData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_voData
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxVoDataFrames(),
							tmpAhAssociationValue.getClientTxVoDataFrames());
					client_trans_voData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_mgtData
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxMgtFrames(),
							tmpAhAssociationValue.getClientTxMgtFrames());
					client_trans_mgtData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_unicastData
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxUnicastFrames(),
							tmpAhAssociationValue.getClientTxUnicastFrames());
					client_trans_unicastData.add(new CheckItem(tmpCount, formatTime));
					// transmit client_dataOctets
					tmpCount = checkValueLessThanZero(tmpClass.getClientTxDataOctets(),
							tmpAhAssociationValue.getClientTxDataOctets());
					client_trans_dataOctets.add(new CheckItem(tmpCount, formatTime));
					// transmit client_lastrate
					tmpCount = tmpClass.getClientLastTxRate();
					client_trans_lastrate.add(new CheckItem(tmpCount, formatTime));
					// receive client_totalDataFrame
					tmpCount = checkValueLessThanZero(tmpClass.getClientRxDataFrames(),
							tmpAhAssociationValue.getClientRxDataFrames());
					client_rec_totalData.add(new CheckItem(tmpCount, formatTime));
					// receive client_mgtData
					tmpCount = checkValueLessThanZero(tmpClass.getClientRxMgtFrames(),
							tmpAhAssociationValue.getClientRxMgtFrames());
					client_rec_mgtData.add(new CheckItem(tmpCount, formatTime));
					// receive client_unicastData
					tmpCount = checkValueLessThanZero(tmpClass.getClientRxUnicastFrames(),
							tmpAhAssociationValue.getClientRxUnicastFrames());
					client_rec_unicastData.add(new CheckItem(tmpCount, formatTime));
					// receive client_multicastData
					tmpCount = checkValueLessThanZero(tmpClass
							.getClientRxMulticastFrames(), tmpAhAssociationValue
							.getClientRxMulticastFrames());
					client_rec_multicastData.add(new CheckItem(tmpCount, formatTime));
					// receive client_broadcastData
					tmpCount = checkValueLessThanZero(tmpClass
							.getClientRxBroadcastFrames(), tmpAhAssociationValue
							.getClientRxBroadcastFrames());
					client_rec_broadcastData.add(new CheckItem(tmpCount, formatTime));
					// receive client_micfailures
					tmpCount = checkValueLessThanZero(tmpClass.getClientRxMICFailures(),
							tmpAhAssociationValue.getClientRxMICFailures());
					client_rec_micfailures.add(new CheckItem(tmpCount, formatTime));
					// receive client_dataOctets
					tmpCount = checkValueLessThanZero(tmpClass.getClientRxDataOctets(),
							tmpAhAssociationValue.getClientRxDataOctets());
					client_rec_dataOctets.add(new CheckItem(tmpCount, formatTime));
					// receive client_lastrate
					tmpCount = tmpClass.getClientLastRxRate();
					client_rec_lastrate.add(new CheckItem(tmpCount, formatTime));
					// client_rssi
					tmpCount = tmpClass.getClientRSSI();
					client_rssi.add(new CheckItem(tmpCount, formatTime));
					
					client_signal_to_noise.add(new CheckItem((long)tmpClass.getSNR(),
							formatTime));
					
					tmpAhAssociationValue = tmpClass;
				}
			}
		}
		activeClient = (AhClientSession)MgrUtil.getSessionAttribute("LAST_CLIENT_HISTORY_CURRENT");
		
		String where = "clientMac = :s1 AND time >= :s2";
		Object values[] = new Object[2];
		values[0] = activeClient.getClientMac();
		values[1] = activeClient.getStartTimeStamp();
		FilterParams f_params = new FilterParams(where, values);
		SortParams s_params = new SortParams("time");

		List<AhBandWidthSentinelHistory> lstBindWidth = new ArrayList<AhBandWidthSentinelHistory>();
		if (activeClient.getStartTimeStamp()>(System.currentTimeMillis()-1000l*60*60*24*200)){
			lstBindWidth = QueryUtil.executeQuery(AhBandWidthSentinelHistory.class,
					s_params, f_params,getDomain().getId());
		}
		Calendar cale = Calendar.getInstance();
		cale.setTimeInMillis(activeClient.getStartTimeStamp());
		long systime = System.currentTimeMillis();
		String timeString = AhDateTimeUtil.getSpecifyDateTimeReport(
				cale.getTimeInMillis(), getUserTimeZone());
		client_sla.add(new CheckItem((long)100,timeString));
		cale.add(Calendar.MINUTE, 10);
		if (activeClient.getStartTimeStamp()>(System.currentTimeMillis()-1000l*60*60*24*200)){
			while (cale.getTimeInMillis()<systime && client_sla.size()<1000){
				long count;
				boolean saveBefore = false;
				for(AhBandWidthSentinelHistory myClass:lstBindWidth){
					if (myClass.getTimeStamp().getTime()<=cale.getTimeInMillis() - 600000){
						continue;
					}
					if (myClass.getTimeStamp().getTime()>cale.getTimeInMillis()){
						break;
					}
					count = myClass.getActualBandWidth()*100 /myClass.getGuaranteedBandWidth();
					timeString = AhDateTimeUtil.getSpecifyDateTimeReport(
							myClass.getTimeStamp().getTime(), getUserTimeZone());
					client_sla.add(new CheckItem(count>100?100:count,timeString));
					saveBefore=true;
				}
				if (!saveBefore){
					timeString = AhDateTimeUtil.getSpecifyDateTimeReport(
							cale.getTimeInMillis(), getUserTimeZone());
					client_sla.add(new CheckItem((long)100,timeString));
				}
				cale.add(Calendar.MINUTE, 10);
			}
		}
	}
	
	public void initFlashDataNew(){
		if (newReportDataList!=null) {
			client_trans_totalData = new ArrayList<CheckItem>();
			client_rec_totalData = new ArrayList<CheckItem>();
			
			client_rec_airTime= new ArrayList<CheckItem>();
			client_trans_airTime= new ArrayList<CheckItem>();
			client_score=new ArrayList<CheckItem>();
			client_radio_score=new ArrayList<CheckItem>();
			client_ipnetwork_score=new ArrayList<CheckItem>();
			client_application_score=new ArrayList<CheckItem>();
			
			client_rec_drop = new ArrayList<CheckItem>();
			client_trans_drop = new ArrayList<CheckItem>();
			client_bandwidth = new ArrayList<CheckItem>();
			client_slacount = new ArrayList<CheckItem>();
			
			client_rec_rate_dis = new HashMap<String, List<CheckItem>>();
			client_rec_rate_succ_dis = new ArrayList<TextItem>();
			client_trans_rate_dis = new HashMap<String, List<CheckItem>>();
			client_trans_rate_succ_dis = new ArrayList<TextItem>();
//			client_trans_total_rate_succ_dis = new ArrayList<TextItem>();
//			client_rec_total_rate_succ_dis = new ArrayList<TextItem>();
			
			client_rec_dateTimeList= new ArrayList<String>();
			client_trans_dateTimeList= new ArrayList<String>();
			client_rec_rateTypeList= new ArrayList<String>();
			client_trans_rateTypeList= new ArrayList<String>();
			
			for(AhClientStats oneBo:newReportDataList){
				//String starTime = AhDateTimeUtil.getSpecifyDateTime(oneBo.getTimeStamp(),getUserTimeZone(), getDomain());
				String starTime = AhDateTimeUtil.getSpecifyDateTimeReport(oneBo.getTimeStamp(),getUserTimeZone());
				
				client_trans_totalData.add(new CheckItem((long) oneBo.getTxFrameCount(), starTime));
				client_rec_totalData.add(new CheckItem((long) oneBo.getRxFrameCount(), starTime));
				
				client_trans_airTime.add(new CheckItem((long)oneBo.getTxAirTime(), starTime));
				client_rec_airTime.add(new CheckItem((long)oneBo.getRxAirTime(), starTime));
				client_score.add(new CheckItem((long)oneBo.getOverallClientHealthScore(), starTime));
				client_radio_score.add(new CheckItem((long)oneBo.getSlaConnectScore(), starTime));
				client_ipnetwork_score.add(new CheckItem((long)oneBo.getIpNetworkConnectivityScore(), starTime));
				client_application_score.add(new CheckItem((long)oneBo.getApplicationHealthScore(), starTime));
				client_trans_drop.add(new CheckItem((long) oneBo.getTxFrameDropped(), starTime));
				client_rec_drop.add(new CheckItem((long) oneBo.getRxFrameDropped(), starTime));
				
				client_bandwidth.add(new CheckItem((long) oneBo.getBandWidthUsage(), starTime));
				client_slacount.add(new CheckItem((long) oneBo.getSlaViolationTraps(), starTime));
				
				int totalPencent=0;
				int rateCount=0;
				StringBuilder rateSucDisTxValue=new StringBuilder();
				if (oneBo.getTxRateInfo()!=null && !oneBo.getTxRateInfo().equals("")){
					String[] txRate=oneBo.getTxRateInfo().split(";");
					client_trans_dateTimeList.add(starTime);
					for (String rx : txRate) {
						if (!rx.equals("")) {
							String[] oneRec = rx.split(",");
							if (oneRec.length == 3) {
								rateCount++;
								if (!client_trans_rateTypeList.contains(oneRec[0])) {
									client_trans_rateTypeList.add(oneRec[0]);
								}
								if (client_trans_rate_dis.get(starTime) == null) {
									List<CheckItem> tmpArray = new ArrayList<CheckItem>();
									tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
									client_trans_rate_dis.put(starTime, tmpArray);
								} else {
									client_trans_rate_dis.get(starTime).add(
											new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								}
								totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
								rateSucDisTxValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
										.append(oneRec[2]).append("%; ");

//								if (client_trans_rate_succ_dis.get(starTime)==null) {
//									List<CheckItem> tmpArray= new ArrayList<CheckItem>();
//									tmpArray.add(new CheckItem(Long.parseLong(oneRec[2]),oneRec[0]));
//									client_trans_rate_succ_dis.put(starTime, tmpArray);
//								} else {
//									client_trans_rate_succ_dis.get(starTime).add(
//											new CheckItem(Long.parseLong(oneRec[2]),oneRec[0]));
//								}
							}
						}
					}
				}
				
				
				if (oneBo.getTotalTxBitSuccessRate()==0) {
					if (rateCount!=0) {
						client_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),rateSucDisTxValue.toString()));
//						client_trans_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
					} else {
						client_trans_rate_succ_dis.add(new TextItem(starTime,"0",rateSucDisTxValue.toString()));
//						client_trans_total_rate_succ_dis.add(new TextItem(starTime,"0%"));
					}
				} else {
					client_trans_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate()),rateSucDisTxValue.toString()));
//					client_trans_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalTxBitSuccessRate())+ "%"));
				}
				
				totalPencent=0;
				rateCount=0;
				StringBuilder rateSucDisRxValue=new StringBuilder();
				if (oneBo.getRxRateInfo()!=null && !oneBo.getRxRateInfo().equals("")){
					String[] rxRate=oneBo.getRxRateInfo().split(";");
					client_rec_dateTimeList.add(starTime);
					for (String rx : rxRate) {
						if (!rx.equals("")) {
							String[] oneRec = rx.split(",");
							if (oneRec.length == 3) {
								rateCount++;
								if (!client_rec_rateTypeList.contains(oneRec[0])) {
									client_rec_rateTypeList.add(oneRec[0]);
								}
								if (client_rec_rate_dis.get(starTime) == null) {
									List<CheckItem> tmpArray = new ArrayList<CheckItem>();
									tmpArray.add(new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
									client_rec_rate_dis.put(starTime, tmpArray);
								} else {
									client_rec_rate_dis.get(starTime).add(
											new CheckItem(Long.parseLong(oneRec[1]), oneRec[0]));
								}
								totalPencent=totalPencent+ Integer.parseInt(oneRec[2])*Integer.parseInt(oneRec[1]);
								rateSucDisRxValue.append(convertRateToM(Integer.parseInt(oneRec[0]))).append("Mbps:")
										.append(oneRec[2]).append("%; ");
//								if (client_rec_rate_succ_dis.get(starTime)==null) {
//									List<CheckItem> tmpArray= new ArrayList<CheckItem>();
//									tmpArray.add(new CheckItem(Long.parseLong(oneRec[2]),oneRec[0]));
//									client_rec_rate_succ_dis.put(starTime, tmpArray);
//								} else {
//									client_rec_rate_succ_dis.get(starTime).add(
//											new CheckItem(Long.parseLong(oneRec[2]),oneRec[0]));
//								}
							}
						}
					}
				}
				
				if (oneBo.getTotalRxBitSuccessRate()==0) {
					if (rateCount!=0) {
						client_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/100),rateSucDisRxValue.toString()));
//						client_rec_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(totalPencent/rateCount)+ "%"));
					} else {
						client_rec_rate_succ_dis.add(new TextItem(starTime,"0",rateSucDisRxValue.toString()));
//						client_rec_total_rate_succ_dis.add(new TextItem(starTime,"0%"));
					}
				} else {
					client_rec_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate()),rateSucDisRxValue.toString()));
//					client_rec_total_rate_succ_dis.add(new TextItem(starTime,String.valueOf(oneBo.getTotalRxBitSuccessRate())+ "%"));
				}
			}
			Collections.sort(client_rec_rateTypeList, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					try {
						long rate1 = Long.parseLong(o1);
						long rate2 = Long.parseLong(o2);
						if (rate1-rate2>0) {
							return 1;
						} else if (rate1-rate2<0) {
							return -1;
						} else {
							return 0;
						}
					} catch (Exception e) {
						return 0;
					}
				}
			});
			Collections.sort(client_trans_rateTypeList, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					try {
						
						long rate1 = Long.parseLong(o1);
						long rate2 = Long.parseLong(o2);
						if (rate1-rate2>0) {
							return 1;
						} else if (rate1-rate2<0) {
							return -1;
						} else {
							return 0;
						}
					} catch (Exception e) {
						return 0;
					}
				}
			});
		}
	}
	
	public long checkValueLessThanZero(long value1, long value2) {
		if (value1 - value2 < 0) {
			return value1;
		}
		return value1 - value2;
	}

	private List<Long> getSelectedClientIds() {
		String[] ids = selectedClientIDStr.split(",");
		List<Long> idList = new ArrayList<Long>(ids.length);
		for (String str_id : ids) {
			idList.add(Long.parseLong(str_id));
		}

		return idList;
	}

	/**
	 * sync query client information
	 *
	 * @throws Exception -
	 */
	private void updateClientInfo() throws Exception {
		jsonObject = new JSONObject();
		AhClientSession client = getSelectedClient();
		if (null == client) {
			return;
		}

		boolean isSuccess = HmBePerformUtil.updateClientInfo(client);
		jsonObject.put("success", isSuccess);
		jsonObject.put("clientId", id);
		jsonObject.put("listType", "wired");
		if (client.getClientChannel() > 0) {
			jsonObject.put("listType", "wireless");
		}

		// // to request client info first;
		// HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class,
		// "macAddress", client.getApMac());
		// if (ap == null)
		// {
		// return;
		// }
		//
		// List<Byte> tableIDList = new ArrayList<Byte>();
		// tableIDList.add(BeCommunicationConstant.STATTABLE_AHASSOCIATION);
		// Map<Byte, List<HmBo>> results =
		// HmBePerformUtil.syncQueryStatistics(ap,
		// tableIDList);
		// if (null != results)
		// {
		// // retrieve successfully.
		// jsonObject.put("success", true);
		// jsonObject.put("clientId", clientId);
		// }
	}

	/**
	 * return relative ap list, class: SimpleHiveAP
	 * 
	 * @param clientIDList -
	 * @return -
	 */
	private Collection<SimpleHiveAp> getHiveAPList(List<Long> clientIDList) {
		// get ap mac at first
		Set<String> apMacSet = new HashSet<String>();
		for (Long clientID : clientIDList) {
//			List<AhClientSession> list = QueryUtil.executeQuery(AhClientSession.class, null,
//					new FilterParams("id", clientID));
			List<AhClientSession> list = DBOperationUtil.executeQuery(AhClientSession.class, null,
					new FilterParams("id", clientID));
			if (!list.isEmpty()) {
				apMacSet.add(list.get(0).getApMac());
			}
		}

		// get ap list
		Collection<SimpleHiveAp> apList = new ArrayList<SimpleHiveAp>(apMacSet
				.size());
		CacheMgmt cacheInstance = CacheMgmt.getInstance();
		for (String apMac : apMacSet) {
			// List<?> list = QueryUtil.executeQuery(HiveAp.class, null,
			// new FilterParams("macAddress", apMac));
			// if (!list.isEmpty())
			// {
			// apList.add((HiveAp) list.get(0));
			// }
			SimpleHiveAp ap = cacheInstance.getSimpleHiveAp(apMac);
			apList.add(ap);
		}

		return apList;
	}

	private void prepareDetails(AhClientSession client) {
	//	prepareEnrolledClientDetails();
		if (null == client) {
			return;
		}
		
		// show as user context timezone
		client.setStartTimeZone(userContext.getTimeZone());
		client.setEndTimeZone(userContext.getTimeZone());

		memo = client.getMemo();
		hostName = client.getClientHostname();
		macAddress = client.getClientMac();
		associationTimeString = client.getStartTimeString();
		duration = client.getDurationString();
		clientAuthMethodString = client.getClientAuthMethodString();
		if (client.getClientChannel()<=0) {
			ssid = client.getClientSSID();
			clientChannel="";
			clientBSSID="";
			radioModeString = "";
			clientEncryptionMethodString = "";
		} else {
			ssid = client.getClientSSID();
			clientChannel=client.getClientChannelString();
			clientBSSID=client.getClientBSSID();
			radioModeString = client.getClientMacPtlString();
			clientEncryptionMethodString = client.getClientEncryptionMethodString();
		}
		
		associationApMac = client.getApMac();
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class,
				"macAddress", associationApMac, client.getOwner().getId());
		associationApId = String.valueOf(ap.getId());

		associationApName = client.getApName();
		
		// rssi = client.getClientRSSI4Show();
		// tx = client.getClientLastTxRate4Show();
		// rx = client.getClientLastRxRate4Show();
		ipAddress = client.getClientIP();
		List<SubNetworkResource> resourceList=null;
		FilterParams filterParam=null;
		if(AhClientUtil.deviceIsRouter(client.getApMac())){
			filterParam=new FilterParams("hiveApMac",client.getApMac());
			resourceList=AhClientUtil.querySubNetworkResources(filterParam);
		}
		if(null==resourceList || resourceList.isEmpty()){
			natIpAddress="";
		}else{
			for(SubNetworkResource resource:resourceList){
				natIpAddress=AhClientUtil.getClientNatIP(resource, client.getClientIP());
				if(!"".equals(natIpAddress) && null!=natIpAddress){
					break;
				}
			}
		}
		userProfileID = client.getClientUserProfId4Show();
		
		clientCWPUsedString=client.getClientCWPUsedString();
		
		clientVLAN=client.getClientVLANString();
		
		userName = client.getClientUsername();
		clientScore = client.getOverallClientHealthScore();
		
		clientRadioScore = client.getSlaConnectScore();
		clientIpNetworkScore = client.getIpNetworkConnectivityScore();
		clientApplicationScore = client.getApplicationHealthScore();

		userEmail=client.getEmail();
		userCompany=client.getCompanyName();
		
		clientOsInfo=client.getClientOsInfoInDb();
		client_os_option55 = client.getOs_option55();
		rssi = client.getClientRSSI4Show();
		snr = client.getClientSNRShow();
		// log.debug("prepareDetails",
		// "Start querying association data, filter params: clientMac ="
		// + client.getClientMac() + " AND statTime >= "
		// + client.getStartTimeString());
		prepareStatisticsInfo(client);

		if (!association_stats.isEmpty()) {
			AhAssociation latestItem = association_stats
					.get(association_stats.size() - 1);
			tx = latestItem.getClientLastTxRate4Show();
			rx = latestItem.getClientLastRxRate4Show();
		}

		// log.debug("prepareDetails",
		// "Finish querying association data, rows number:"
		// + association_stats.size());

		prepareHistorySessionInfo(client);
	}
	
	
	private void prepareNewReportData(AhClientSession client){
		String where = "timeStamp >= :s1 AND clientMac = :s2";
		Object values[] = new Object[2];
		values[0] = client.getStartTimeStamp();
		values[1] = client.getClientMac();
		FilterParams f_params = new FilterParams(where, values);
		SortParams s_params = new SortParams("timeStamp");
		if (client.getStartTimeStamp()<(System.currentTimeMillis()-1000l*60*60*24*200)) {
			newReportDataList = QueryUtil.executeQuery(AhClientStats.class,
					s_params, f_params, 50);
		} else {
			newReportDataList = QueryUtil.executeQuery(AhClientStats.class,
					s_params, f_params);
		}
	}

	private void prepareHistorySessionInfo(AhClientSession client) {
		String query = "from " + AhClientSessionHistory.class.getSimpleName() + " where clientMac = '"
				+ client.getClientMac() + "' and owner.id = "
				+ client.getOwner().getId() + " order by endTimestamp desc";
		history_sessions = QueryUtil.executeQuery(query, 5);
		
		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
		for (Object history_session : history_sessions) {
			AhClientSessionHistory clientHistory = (AhClientSessionHistory) history_session;
			clientHistory.setStartTimeZone(userTimeZone);
			clientHistory.setEndTimeZone(userTimeZone);
		}
	}

	private void prepareStatisticsInfo(AhClientSession client) {
		String where = "clientMac = :s1 AND time >= :s2";
		Object values[] = new Object[2];
		values[0] = client.getClientMac();
		values[1] = client.getStartTimeStamp();
		FilterParams f_params = new FilterParams(where, values);
		SortParams s_params = new SortParams("time");
		if (client.getStartTimeStamp()<(System.currentTimeMillis()-1000l*60*60*24*200)) {
			association_stats = QueryUtil.executeQuery(AhAssociation.class,
					s_params, f_params,50);
		} else {
			association_stats = QueryUtil.executeQuery(AhAssociation.class,
					s_params, f_params);
		}
		String localTimeZone = userContext.getTimeZone();
		for(AhAssociation tmpClass:association_stats){
			tmpClass.getTimeStamp().setTimeZone(localTimeZone);
		}
	}

	private AhClientSession getSelectedClient() {
//		return QueryUtil.findBoById(AhClientSession.class,
//				id, this);
		return DBOperationUtil.findBoById(AhClientSession.class,
				id);
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLIENTMONITOR);
		setDataSource(AhClientSession.class);
		keyColumnId = COLUMN_MAC;
		this.tableId = HmTableColumn.TABLE_ACTIVE_CLIENTS;
	}

	@Override
	public AhClientSession getDataSource() {
		return (AhClientSession) dataSource;
	}

	// the parameters on the page.
	private String filterApName;
	
	// fix bug 33035, come from hiveap list page
	private String filterApMac;

	private String filterClientMac;

	private String filterClientIP;

	private String filterClientHostName;

	private String filterClientUserName;

	private Long filterMap;

	private String mapContainerId;

	private List<CheckItem> filterMaps;
	
	//Added from Dakar
	private byte    filterOverallClientHealth=-1;
	
	private String	filterClientOsInfo;
	
	private int		filterClientVLAN=-1;

	private int		filterClientUserProfId=-1;

	private int		filterClientChannel=-1;

	public List<CheckItem> getFilterMaps() {
		/*-
		 filterMaps = getBoCheckItems("mapName", MapContainerNode.class, null);
		 if (filterMaps.isEmpty())
		 {
		 filterMaps.add(new CheckItem((long) -1, MgrUtil
		 .getUserMessage("config.optionsTransfer.none")));
		 }

		 return filterMaps;*/
		List<CheckItem> maps = getMapListView();
		filterMaps = new ArrayList<CheckItem>();
		if (maps.isEmpty()) {
			filterMaps.add(new CheckItem((long) -1, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));

		} else {
			filterMaps.add(new CheckItem((long) -1, ""));
			// filterMaps.add(new CheckItem((long) -1, "<no-map>"));
		}
		filterMaps.addAll(maps);
		return filterMaps;
	}

	public String getFilterApName() {
		return filterApName;
	}

	public void setFilterApName(String apName) {
		this.filterApName = apName;
	}

	public String getFilterClientMac() {
		return filterClientMac;
	}

	public void setFilterClientMac(String clientMac) {
		this.filterClientMac = clientMac;
	}

	public void setMapContainerId(String mapContainerId) {
		this.mapContainerId = mapContainerId;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		// if (bo instanceof AhCurrentClientSession)
		// {
		// for (WirelessIDS idp : ((AhCurrentClientSession) bo)
		// .getIdpDetails())
		// {
		// // Just to trigger load from database
		// }
		// }

		// if (bo instanceof HiveAp)
		// {
		// HiveAp ap = (HiveAp) bo;
		//
		// // Just calling the get method will fetch the LAZY attributes
		// // bo.getUserProfileAttribute();
		// // Call additional LAZY methods
		// if (ap.getConfigTemplate() != null)
		// ap.getConfigTemplate().getId();
		// }
		//
		// if (bo instanceof ConfigTemplate)
		// {
		// ConfigTemplate configTemplate = (ConfigTemplate) bo;
		//
		// // Just calling the get method will fetch the LAZY attributes
		// // bo.getUserProfileAttribute();
		// // Call additional LAZY methods
		// if (configTemplate.getHiveProfile() != null)
		// configTemplate.getHiveProfile().getId();
		//
		// }

		return null;
	}

	// statistics detail table grid count
	public int getGridCount_s() {
		if (null == association_stats || association_stats.isEmpty()) {
			return 3;
		} else {
			return 0;
		}
	}

	// session history detail table grid count
	public int getGridCount_h() {
		if (null == history_sessions || history_sessions.isEmpty()) {
			return 3;
		} else {
			return 0;
		}
	}

	private String userName;

	private String hostName;
	
	private String clientOsInfo;

	private String macAddress;

	private String associationTimeString;

	private String duration;

	private String ssid;

	private String associationApId;

	private String associationApMac;

	private String associationApName;

	private String radioModeString;

	private String rssi;
	
	private String snr;

	private String tx;

	private String rx;

	private String ipAddress;
    private String natIpAddress;
	private String userProfileID;
	
	private String clientAuthMethodString;
	private String clientEncryptionMethodString;
	private String clientCWPUsedString;
	private String clientBSSID;
	private String clientVLAN;
	private String clientChannel;
	
	private String userEmail;
	private String userCompany;

	private String memo;
	
	private int		clientScore;
	
	private int clientRadioScore;
	private int clientIpNetworkScore;
	private int clientApplicationScore;

	public int getClientScore() {
		return clientScore;
	}

	public void setClientScore(int clientScore) {
		this.clientScore = clientScore;
	}

	private List<AhAssociation> association_stats;
	
	private List<AhClientStats> newReportDataList;
	
	private AhAssociation oneAssociation;

	private List<?> history_sessions;

	public String getUserProfileID() {
		return userProfileID;
	}

	public String getMemo() {
		return memo;
	}

	public String getHostName() {
		return hostName;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getDuration() {
		return duration;
	}

	public String getSsid() {
		return ssid;
	}

	public String getRadioModeString() {
		return radioModeString;
	}

	public String getRssi() {
		return rssi;
	}

	public String getTx() {
		return tx;
	}

	public String getRx() {
		return rx;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getNatIpAddress() {
		return natIpAddress;
	}

	public void setNatIpAddress(String natIpAddress) {
		this.natIpAddress = natIpAddress;
	}

	public String getAssociationTimeString() {
		return associationTimeString;
	}

	public String getAssociationApMac() {
		return associationApMac;
	}

	public List<AhAssociation> getAssociation_stats() {
		return association_stats;
	}

	public void setAssociation_stats(List<AhAssociation> association_stats) {
		this.association_stats = association_stats;
	}

	public List<?> getHistory_sessions() {
		return history_sessions;
	}

	public void setHistory_sessions(List<?> history_sessions) {
		this.history_sessions = history_sessions;
	}

	public String getCurrentDate() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		return sf.format(new Date());
	}

	public String getAssociationApName() {
		return associationApName;
	}

	public void setAssociationApName(String associationApName) {
		this.associationApName = associationApName;
	}

	public int getIndent() {
		return indent;
	}

	private void prepareSearchOperation() {
		List<Object> lstCondition = new ArrayList<Object>();

		// get active clients
		String searchSQL = "connectstate=?";
		lstCondition.add(AhClientSession.CONNECT_STATE_UP);
		
		if (isWiredClientList()) {
			searchSQL = searchSQL + " AND (clientChannel is null OR clientChannel < ?)";
			lstCondition.add(1);
		} else if(isWirelessClientList()) {
			searchSQL = searchSQL + " AND clientChannel > ?";
			lstCondition.add(0);
		} 

		if (filterApName != null && !filterApName.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(apName) like ?";
			lstCondition.add("%" + filterApName.trim().toLowerCase() + "%");
		}
		
		// fix bug 33035 come from device list page
		if (filterApMac != null && !filterApMac.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(apMac) = ?";
			lstCondition.add(filterApMac.trim().toLowerCase());
		}
		
		if (filterClientMac != null && !filterClientMac.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(clientmac) like ?";
			lstCondition.add("%" + filterClientMac.trim().toLowerCase() + "%");
		}

		if (filterClientIP != null && !filterClientIP.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(clientIP) like ?";
			lstCondition.add("%" + filterClientIP.trim().toLowerCase() + "%");
		}

		if (filterClientHostName != null
				&& !filterClientHostName.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(clientHostname) like ?";
			lstCondition.add("%" + filterClientHostName.trim().toLowerCase()
					+ "%");
		}

		if (filterClientUserName != null
				&& !filterClientUserName.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(clientUsername) like ?";
			lstCondition.add("%" + filterClientUserName.trim().toLowerCase()
					+ "%");
		}
		
		if(filterOverallClientHealth >= 0 ){
			searchSQL = searchSQL + " AND overallClientHealthScore = ?";
			lstCondition.add(filterOverallClientHealth);
		}
		
		if (filterClientOsInfo != null
				&& !filterClientOsInfo.trim().isEmpty()) {
			searchSQL = searchSQL + " AND lower(clientOsInfo) like ?";
			lstCondition.add("%" + filterClientOsInfo.trim().toLowerCase()
					+ "%");
		}
		
		if(filterClientVLAN >= 0 ){
			searchSQL = searchSQL + " AND clientVLAN = ?";
			lstCondition.add(filterClientVLAN);
		}
		
		if(filterClientUserProfId >= 0 ){
			searchSQL = searchSQL + " AND clientUserProfId = ?";
			lstCondition.add(filterClientUserProfId);
		}
		
		if(filterClientChannel >= 0 ){
			searchSQL = searchSQL + " AND clientChannel = ?";
			lstCondition.add(filterClientChannel);
		}
		

		// All: -2, no-map: -1
		if (null != filterMap && filterMap != -2) {
			// if (filterMap.longValue() == -1) {
			// filterMap = null; //MARK: modify filter Map will cause db data error
			// }

			List<SimpleHiveAp> apList = cacheMgmt
					.getApListByMapContainer(filterMap == -1 ? null
							: filterMap);
			searchSQL = searchSQL + " AND apMac in ('0'";
				
			for (SimpleHiveAp ap: apList) {
				searchSQL += ",?";
				lstCondition.add(ap.getMacAddress());
			}
			searchSQL += ")";
		}

		// Frazer add for search activeClient one map.
		if (null != mapContainerId) {
			// can not get the specify clients, comment it right now.
			// Long mapId = Long.parseLong(mapContainerId);
			// searchSQL = searchSQL + " AND mapId = :s" + (lstCondition.size()
			// + 1);
			// lstCondition.add(mapId);
			Long mapId = Long.parseLong(mapContainerId);
			Set<Long> mapIds = BoMgmt.getMapMgmt().getContainerDownIds(mapId);
			searchSQL = "mapId in(0";
			lstCondition = new ArrayList<Object>();
			
			for(Long mId: mapIds) {
				searchSQL +=",?";
				lstCondition.add(mId);
			}
			searchSQL +=")";
		}

		if (lstCondition.isEmpty()) {
			filterParams = null;
			return;
		}

		filterParams = new FilterParams(searchSQL, lstCondition.toArray());
		setSessionFiltering();

//		// clear current filter on the left page
//		MgrUtil.removeSessionAttribute(MANAGED_ACTIVECLIENT_CURRENT_FILTER);
	}

	private void removeActiveClientFilter() {
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}

		// check whether used by client refresh
		List<HMServicesSettings> clientMonitorSettings = QueryUtil.executeQuery(
				HMServicesSettings.class, null, null);
		if (!clientMonitorSettings.isEmpty()) {
			HMServicesSettings clientConfiguration = clientMonitorSettings
					.get(0);
			if (clientConfiguration.isEnableClientRefresh()) {
				String refreshFilterName = clientConfiguration
						.getRefreshFilterName();
				if (refreshFilterName != null
						&& refreshFilterName.equals(filterName)) {
					addActionError(MgrUtil.getUserMessage("action.error.remove.filter.setting.fail",filterName));
					return;
				}
			}
		}

		try {
			List<ActiveClientFilter> list = QueryUtil
					.executeQuery(ActiveClientFilter.class, null,
							new FilterParams("filterName=:s1 AND userName=:s2",
									new Object[] { filterName,
											getUserContext().getUserName() }),
							domainId);
			if (!list.isEmpty()) {
				ActiveClientFilter filter = list.get(0);
				ActiveClientFilter rmbos = findBoById(ActiveClientFilter.class, filter
						.getId());
				QueryUtil.removeBoBase(rmbos);
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.remove.active.client.filter.fail"));
		}
	}

	// public static final String MANAGED_ACTIVECLIENT_FILTERS = "managed_activeclient_filters";

	public static final String MANAGED_ACTIVECLIENT_CURRENT_FILTER = "managed_activeclient_current_filter";

	private void saveToSessionFilterList() {
		if (null == filterName || "".equals(filterName.trim())) {
			return;
		}

		try {
			List<ActiveClientFilter> filterList = QueryUtil
					.executeQuery(ActiveClientFilter.class, null,
							new FilterParams("filterName=:s1 AND userName=:s2",
									new Object[] { filterName,
											getUserContext().getUserName() }),
							domainId);
			if (!filterList.isEmpty()) {
				ActiveClientFilter filter = filterList
						.get(0);
				filter.setFilterApName(filterApName);
				filter.setFilterClientMac(filterClientMac);
				filter.setFilterTopologyMap(filterMap);
				filter.setFilterClientIP(filterClientIP);
				filter.setFilterClientHostName(filterClientHostName);
				filter.setFilterClientUserName(filterClientUserName);
				filter.setFilterOverallClientHealth(filterOverallClientHealth);
				filter.setFilterClientOsInfo(filterClientOsInfo);
				filter.setFilterClientVLAN(filterClientVLAN);
				filter.setFilterClientUserProfId(filterClientUserProfId);
				filter.setFilterClientChannel(filterClientChannel);
				setId(filter.getId());
				QueryUtil.updateBo(filter);
			} else {
				ActiveClientFilter filter = new ActiveClientFilter();
				filter.setFilterName(filterName);
				filter.setUserName(getUserContext().getUserName());
				filter.setFilterApName(filterApName);
				filter.setFilterClientMac(filterClientMac);
				filter.setFilterTopologyMap(filterMap);
				filter.setFilterClientIP(filterClientIP);
				filter.setFilterClientHostName(filterClientHostName);
				filter.setFilterClientUserName(filterClientUserName);
				filter.setFilterOverallClientHealth(filterOverallClientHealth);
				filter.setFilterClientOsInfo(filterClientOsInfo);
				filter.setFilterClientVLAN(filterClientVLAN);
				filter.setFilterClientUserProfId(filterClientUserProfId);
				filter.setFilterClientChannel(filterClientChannel);
				filter.setOwner(getDomain());
				QueryUtil.createBo(filter);
			}

			MgrUtil.setSessionAttribute(MANAGED_ACTIVECLIENT_CURRENT_FILTER,
					filterName);
			filter = filterName;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage("action.error.add.filter.fail",NmsUtil.getOEMCustomer().getAccessPonitName()));
		}
	}

	private String filter;

	private String filterName;

	private void prepareFilterValues() throws JSONException {
		jsonObject = new JSONObject();
		if (null == filter) {
			return;
		}
		jsonObject.put("fname", filter);
		// Map<String, List<Object>> filterCache = (Map<String, List<Object>>) MgrUtil
		// .getSessionAttribute(MANAGED_ACTIVECLIENT_FILTERS);
		List<?> filterMap = QueryUtil
				.executeQuery(
						"select filterApName,filterClientMac,filterTopologyMap,filterClientIP,filterClientHostName,filterClientUserName," +
						"filterOverallClientHealth,filterClientOsInfo,filterClientVLAN,filterClientUserProfId,filterClientChannel from "
								+ ActiveClientFilter.class.getSimpleName(),
						null, new FilterParams(
								"filterName=:s1 and userName=:s2",
								new Object[] { filter,
										getUserContext().getUserName() }),
						domainId);

		if (filterMap.isEmpty()) {
			return;
		}
		Object[] objArray = (Object[]) filterMap.get(0);
		if (null == objArray) {
			return;
		}

		if (objArray.length > 0) {
			jsonObject.put("fApName", objArray[0]);
		}
		if (objArray.length > 1) {
			jsonObject.put("fClientMac", objArray[1]);
		}
		if (objArray.length > 2) {
			jsonObject.put("fMap", objArray[2]);
		}
		if (objArray.length > 3) {
			jsonObject.put("fClientIP", objArray[3]);
		}
		if (objArray.length > 4) {
			jsonObject.put("fClientHostName", objArray[4]);
		}
		if (objArray.length > 5) {
			jsonObject.put("fClientUserName", objArray[5]);
		}
		if (objArray.length > 6) {
			jsonObject.put("fOverallClientHealth", objArray[6]);
		}
		if (objArray.length > 7) {
			jsonObject.put("fClientOsInfo", objArray[7]);
		}
		if (objArray.length > 8) {
			jsonObject.put("fClientVLAN", objArray[8]);
		}
		if (objArray.length > 9) {
			jsonObject.put("fClientUserProfId", objArray[9]);
		}
		if (objArray.length > 10) {
			jsonObject.put("fClientChannel", objArray[10]);
		}
	}

	private void prepareEditValues() throws JSONException {
		List<Long> clientIDs = getSelectedClientIds();

		if (clientIDs == null || clientIDs.isEmpty()) {
			// addActionError("There are no items to operation.");
			return;
		}

		if (clientIDs.size() == 1) {
//			AhClientSession client = QueryUtil.findBoById(
//					AhClientSession.class, clientIDs.get(0));
			AhClientSession client = DBOperationUtil.findBoById(
					AhClientSession.class, clientIDs.get(0));
			// to avoid Null-Point error
			if(null == client) return;
			
			editUserName = client.getClientUsername();
			editHostName = client.getClientHostname();
			editIP = client.getClientIP();
			editComment1 = client.getComment1();
			editComment2 = client.getComment2();
		}

		jsonObject = new JSONObject();
		jsonObject.put("eUserName", editUserName);
		jsonObject.put("eHostName", editHostName);
		jsonObject.put("eIP", editIP);
		jsonObject.put("eComment1", editComment1);
		jsonObject.put("eComment2", editComment2);
	}

	// private void clearEditResults() {
	// List<Long> selectIDS = getSelectedClientIds();
	// if (selectIDS == null || selectIDS.isEmpty()) {
	// return;
	// }
	//
	// try {
	// String where = "id in (:s1)";
	// Object[] values = new Object[1];
	// values[0] = selectIDS;
	//
	// List<?> clientMacList = QueryUtil.executeQuery("select clientMac from "
	// + AhClientSession.class.getSimpleName(), null, new FilterParams(where, values));
	//
	// where = "clientMac in (:s1) AND owner=:s2";
	// values = new Object[2];
	// values[0] = clientMacList;
	// values[1] = getDomain();
	//
	// QueryUtil.bulkRemoveBos(AhClientEditValues.class, new FilterParams(where, values),
	// null, null);
	//
	// // cache
	// cacheMgmt.removeClientEditValues(clientMacList, getDomain());
	//
	// } catch (Exception e) {
	// log.error("clearEditResults", "bulk remove", e);
	// }
	// }

	private void saveEditResults() {
		List<Long> selectIDS = getSelectedClientIds();
		List<HmBo> updateList = new ArrayList<HmBo>();
		List<HmBo> insertList = new ArrayList<HmBo>();
		List<AhClientEditValues> editValuesList = new ArrayList<AhClientEditValues>();
		
		StringBuilder clientSql = new StringBuilder();
		clientSql.append("update ah_clientsession set clienthostname=?,clientip=?,clientusername=?,comment1=?,comment2=? where id=?");
		List<Object[]> paraList = new ArrayList<Object[]>();
		
		for (Long clientId : selectIDS) {
//			AhClientSession client = QueryUtil.findBoById(
//					AhClientSession.class, clientId);
			AhClientSession client = DBOperationUtil.findBoById(
					AhClientSession.class, clientId);
			if (client == null) {
				continue;
			}

			AhClientEditValues clientEditValues = QueryUtil
					.findBoByAttribute(AhClientEditValues.class, "clientMac",
							client.getClientMac(), domainId);
			if (clientEditValues == null) {
				clientEditValues = new AhClientEditValues();
				clientEditValues.setClientMac(client.getClientMac());
				clientEditValues.setOwner(client.getOwner());
				insertList.add(clientEditValues);
			} else {
				// update AhClientEditValues
				updateList.add(clientEditValues);
			}

			// client edit values bo
			if (flagEditUserName) {
				clientEditValues.setClientUsername(editUserName);
			}

			if (flagEditHostName) {
				clientEditValues.setClientHostname(editHostName);
			}

			if (flagEditIP) {
				clientEditValues.setClientIP(editIP);
			}

			if (flagEditComment1) {
				clientEditValues.setComment1(editComment1);
			}

			if (flagEditComment2) {
				clientEditValues.setComment2(editComment2);
			}

			// client bo
			boolean isUpdate = false;
			if (flagEditHostName
					&& (client.getClientHostname() == null || client
					.getClientHostname().isEmpty())) {
				client.setClientHostname(editHostName);
				isUpdate = true;
			}

			if (flagEditIP
					&& (client.getClientIP() == null
					|| client.getClientIP().isEmpty() || client
					.getClientIP().equals("0.0.0.0"))) {
				client.setClientIP(editIP);
				isUpdate = true;
			}

			if (flagEditUserName
					&& (client.getClientUsername() == null || client
					.getClientUsername().isEmpty())) {
				client.setClientUsername(editUserName);
				isUpdate = true;
			}

			if (flagEditComment1) {
				client.setComment1(editComment1);
				isUpdate = true;
			}

			if (flagEditComment2) {
				client.setComment2(editComment2);
				isUpdate = true;
			}

			// update AhClientSession
			if (isUpdate) {
//				updateList.add(client);
				Object[] objs = new Object[6];
				objs[0] = client.getClientHostname();
				objs[1] = client.getClientIP();
				objs[2] = client.getClientUsername();
				objs[3] = client.getComment1();
				objs[4] = client.getComment2();
				objs[5] = client.getId();
				paraList.add(objs);
			}

			editValuesList.add(clientEditValues);
		}

		try {
			if (!insertList.isEmpty()) {
				QueryUtil.bulkCreateBos(insertList);
			}

			if (!updateList.isEmpty()) {
				QueryUtil.bulkUpdateBos(updateList);
			}

			//udpate client
			if (!paraList.isEmpty()) {
				DBOperationUtil.executeBatchUpdate(clientSql.toString(), paraList);
			}
			// cache
			if (!editValuesList.isEmpty()) {
				cacheMgmt.addClientEditValues(editValuesList);
			}
		} catch (Exception e) {
			log.error("saveEditResults", "catch exception", e);
		}
	}

	private String prepareActiveClientList(boolean initCache) throws Exception {
		enablePageAutoRefreshSetting = true;
		setCurrentListType();
		if (isWiredClientList()) {
			//get wired client list
			return prepareWiredClientList(initCache);
		}else if (isWirelessClientList()) {
			//get wired client list
			return prepareWirelessClientList(initCache);
		} else {
			return prepareAllClientList(initCache);
		}
	}
	
	public String prepareAllClientList(boolean initCache) throws Exception {
		clearDataSource();
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");

		// default sorting
		enableSorting();
		if (sortParams.getOrderBy() == null) {
			sortParams.setOrderBy("startTimeStamp");
			sortParams.setAscending(false);
		}
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Sorting", sortParams);
		
		getSessionFiltering();
		//String searchSQL = "connectstate = :s1";
		//Object values[] = new Object[1];
		//values[0] = AhClientSession.CONNECT_STATE_UP;
		if (filterParams == null) {
			filterParams = new FilterParams("connectstate",
					AhClientSession.CONNECT_STATE_UP);
			//filterParams = new FilterParams(searchSQL, values);
		}
		setSessionFiltering();
		setTableColumns();

		ClientPagingCache clientPagingCache = getClientListCache();
		if (initCache) {
			enablePaging();
			cacheId = clientPagingCache.init();
			page = clientPagingCache.getBos(cacheId);
		} else {
			paging = (Paging<?>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName()
					+ "Paging");
			page = clientPagingCache.getBos(cacheId);
		}
		
		for (HmTableColumn column : selectedColumns) {
			if (column.getColumnId() == COLUMN_LOCATION) {
				// Only fill in this field if this column is amogn the selected.
				BoMgmt.getLocationTracking().findClientRssi(getDomain(),
						page);
			}
		}
		
		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
//		for (Iterator iter = page.iterator(); iter.hasNext();) {
//			AhClientSession client = (AhClientSession) iter.next();
//			client.setStartTimeZone(userTimeZone);
//			client.setEndTimeZone(userTimeZone);
//		}
		setUserTimeZone(page, userTimeZone);
		
		setMonitorStatus(page);
		initClientRefreshInterval();
		
		setLastHourDataUsage(page);
		
		setOsInfo(page);
		AhClientUtil.setClientNatIp(page);
		prepareEnrolledClientList(page);
		printClientHealthInfo(page);
		return SUCCESS;
	}
	
	public String prepareWirelessClientList(boolean initCache) throws Exception {
		clearDataSource();
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");

		// default sorting
		enableSorting();
		if (sortParams.getOrderBy() == null) {
			sortParams.setOrderBy("startTimeStamp");
			sortParams.setAscending(false);
		}
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Sorting", sortParams);
		
		getSessionFiltering();
		String searchSQL = "connectstate = :s1 AND clientChannel is not null AND clientChannel > :s2";
		Object values[] = new Object[2];
		values[0] = AhClientSession.CONNECT_STATE_UP;
		values[1] = 0;
		if (filterParams == null) {
			//filterParams = new FilterParams("connectstate",
			//		AhClientSession.CONNECT_STATE_UP);
			filterParams = new FilterParams(searchSQL, values);
		}
		setSessionFiltering();
		setTableColumns();

		ClientPagingCache clientPagingCache = getClientListCache();
		if (initCache) {
			enablePaging();
			cacheId = clientPagingCache.init();
			page = clientPagingCache.getBos(cacheId);
		} else {
			paging = (Paging<?>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName()
					+ "Paging");
			page = clientPagingCache.getBos(cacheId);
		}
		
		for (HmTableColumn column : selectedColumns) {
			if (column.getColumnId() == COLUMN_LOCATION) {
				// Only fill in this field if this column is amogn the selected.
				BoMgmt.getLocationTracking().findClientRssi(getDomain(),
						page);
			}
		}
		
		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
//		for (Iterator iter = page.iterator(); iter.hasNext();) {
//			AhClientSession client = (AhClientSession) iter.next();
//			client.setStartTimeZone(userTimeZone);
//			client.setEndTimeZone(userTimeZone);
//		}
		setUserTimeZone(page, userTimeZone);
		
		setMonitorStatus(page);
		initClientRefreshInterval();
		
		setLastHourDataUsage(page);
		
		setOsInfo(page);
		AhClientUtil.setClientNatIp(page);
		prepareEnrolledClientList(page);
		printClientHealthInfo(page);
		return SUCCESS;
	}
	
	public void prepareEnrolledClientList(List<?> page) {
		if (null == page || page.isEmpty()) {
			return;
		}

//		int i=0;
		boolean has62r1beforeAp=false;
		String customer = getCustomerIdFromRemote(getDomain());
		
		IGAConfigHepler gaConfigHelper= new GAConfigHepler(getDomain().getId());
		ICloudAuthCertMgmt<?> hmCloudAuthCertMgmtImpl = new HmCloudAuthCertMgmtImpl();
		IDMConfig idmConfig = hmCloudAuthCertMgmtImpl.getRadSecConfig(getDomain().getId());
		String cMUrl = ConfigUtil.getACMConfigServerViewUrl();
		String sLUrl = gaConfigHelper==null?null : gaConfigHelper.getWebAccessURL();
		String idmUrl = idmConfig==null? null: idmConfig.getIdmWebServer();
		List<String> clientMacList = new ArrayList<String>();
		for (Object obj : page) {
			AhClientSession client = (AhClientSession) obj;
			clientMacList.add(client.getClientMac());
			
//			if (i%2==0) {
//				client.setClientEnrolled(true);
//				client.setEnrolledSLURL(sLUrl);
//				client.setEnrolledIDMURL(idmUrl);
//				client.setEnrolledCMURL(cMUrl);
//			} else {
//				client.setClientEnrolled(false);
//				client.setEnrolledSLURL(sLUrl);
//				client.setEnrolledIDMURL(idmUrl);
//				client.setEnrolledCMURL(cMUrl);
//			}
//			client.setManagedStatus(Short.valueOf(String.valueOf(i%7)));
//			i++;
			
			client.setCustomerID(customer);
			client.setEnrolledSLURL(sLUrl);
			client.setEnrolledIDMURL(idmUrl);
			client.setEnrolledCMURL(cMUrl);
			
			if (!has62r1beforeAp) {
				SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(client.getApMac());
				if (ap!=null && !ap.isSimulated()) {
					if (NmsUtil.compareSoftwareVersion("6.2.1.0",ap.getSoftVer())>0) {
						has62r1beforeAp=true;
					}
				}
			}
		}
		if (!getEnableClientManagement()) {
			return;
		}
		if (has62r1beforeAp) {
			try {
				EnrolledClientList  lst = new TransXMLToObjectImpl().getActiveClientListEnrolledInfo(
						new ResponseModelServiceImpl().getActiveClientListEnrolledInfo(
								customer, getDomain().getInstanceId(), clientMacList));
				if (lst == null) {
					return;
				}
				String urlSuffix = cMUrl + lst.getDeviceUrlSuffix();
				Map<String, DeviceForClient> clientMacMap = new HashMap<String, DeviceForClient>();
				if (lst.getDeviceList()!=null) {
					for (DeviceForClient dfc: lst.getDeviceList()){
						clientMacMap.put(dfc.getMacAddress(), dfc);
					}
				}
	
				for (Object obj : page) {
					AhClientSession client = (AhClientSession) obj;
					DeviceForClient dfc = clientMacMap.get(client.getClientMac());
					if (dfc!=null) {
						client.setClientEnrolled(dfc.isEnrolled());
						if (client.isClientEnrolled()) {
							client.setClientEnrolledURL(urlSuffix + dfc.getDeviceId());
						}
					}
				}
			
			} catch (Exception e) {
				log.error(e);
			}
		}

	}
	
	public String getCustomerIdFromRemote(HmDomain hmdomain) {
		String customer=null;
		try {
			customer = LicenseOperationTool.getCustomerIdFromRemote(hmdomain.getInstanceId());
		} catch (Exception e) {
			log.error(e);
		}
		if (customer==null || customer.isEmpty()) {
			customer=hmdomain.getInstanceId();
		}
		return customer;
	}
	
	public String prepareWiredClientList(boolean initCache) throws Exception {
		clearDataSource();
		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");
		
		// default sorting
		enableSorting();
		if (sortParams.getOrderBy() == null) {
			sortParams.setOrderBy("startTimeStamp");
			sortParams.setAscending(false);
		}
		MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Sorting", sortParams);
		
		getSessionFiltering();
		String searchSQL = "connectstate = :s1 AND (clientChannel is null OR clientChannel < :s2)";
		Object values[] = new Object[2];
		values[0] = AhClientSession.CONNECT_STATE_UP;
		values[1] = 1;
		if (filterParams == null) {
			//filterParams = new FilterParams("connectstate",
			//		AhClientSession.CONNECT_STATE_UP);
			filterParams = new FilterParams(searchSQL, values);
		}
		setSessionFiltering();
		setTableColumns();

		ClientPagingCache clientPagingCache = getClientListCache();
		if (initCache) {
			enablePaging();
			cacheId = clientPagingCache.init();
			page = clientPagingCache.getBos(cacheId);
		} else {
			paging = (Paging<?>) MgrUtil.getSessionAttribute(boClass
					.getSimpleName()
					+ "Paging");
			page = clientPagingCache.getBos(cacheId);
		}
		
		for (HmTableColumn column : selectedColumns) {
			if (column.getColumnId() == COLUMN_LOCATION) {
				// Only fill in this field if this column is amogn the selected.
				BoMgmt.getLocationTracking().findClientRssi(getDomain(),
						page);
			}
		}
		
		// show as user context timezone
		String userTimeZone = userContext.getTimeZone();
		setUserTimeZone(page, userTimeZone);
		
		setMonitorStatus(page);
		initClientRefreshInterval();
		
		setLastHourDataUsage(page);
		
		setOsInfo(page);
		AhClientUtil.setClientNatIp(page);
		return SUCCESS;
	}
	
	private int indent = 3;
	private boolean anyMonitoring;
	
	public boolean isAnyMonitoring() {
		return anyMonitoring;
	}
	
	private void setOsInfo(List<?> page){
		if (null == page) {
			return;
		}
		StringBuilder osBuffer = new StringBuilder();
		boolean addFlg = false;
		for(Object oneObj:page){
			AhClientSession asection = (AhClientSession)oneObj;
			if (!addFlg) {
				if (asection.getClientOsInfo()!=null && !asection.getClientOsInfo().equals("")) {
					osBuffer.append("'").append(NmsUtil.convertSqlStr(asection.getClientOsInfo().toUpperCase())).append("'");
					addFlg=true;
				}
			} else {
				if (asection.getClientOsInfo()!=null && !asection.getClientOsInfo().equals("")) {
					osBuffer.append(",'").append(NmsUtil.convertSqlStr(asection.getClientOsInfo().toUpperCase())).append("'");
				}
				
			}
		}
		if (!page.isEmpty()) {
			if (osBuffer.length()>0) {
				try {
					List<?> detailInfo = QueryUtil.executeNativeQuery("select distinct on (osversion) osversion,description " +
						"from os_object_version where upper(osversion) in (" + osBuffer.toString() + ")");
					for(Object oneObj:page){
						AhClientSession asection = (AhClientSession)oneObj;
						if (asection.getClientOsInfo()!=null ) {
							for(Object oneSearch:detailInfo){
								Object[] oneSearchDate = (Object[])oneSearch;
								if (asection.getClientOsInfo().equalsIgnoreCase(oneSearchDate[0].toString())){
									if (oneSearchDate[1]!=null && !oneSearchDate[1].toString().equals("")) {
										asection.setClientOsInfo(oneSearchDate[1].toString());
										break;
									}
								}
							}
						}
					}
				} catch (Exception e) {
					log.error("setOsInfo", "Set OS information error.", e);
				}
			}
		}
	}
	
	private void printClientHealthInfo(List<?> page){
		if (null == page) {
			return;
		}
		if (operation==null || !operation.equals("debugClientHealth")) {
			return;
		}
		try {
			for(Object oneObj:page){
				StringBuilder sb = new StringBuilder();
				AhClientSession asection = (AhClientSession)oneObj;
				sb.append("======Debug begin=====");
				sb.append("ClientMac:").append(asection.getClientMac());
				sb.append("; AllHealth:").append(asection.getOverallClientHealthScore());
				sb.append("; AppHealth:").append(asection.getApplicationHealthScore());
				sb.append("; NetworkHealth:").append(asection.getIpNetworkConnectivityScore());
				sb.append("; SlaHealth:").append(asection.getSlaConnectScore());
				sb.append("; ======End=====");
				log.error(sb.toString());
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private void setLastHourDataUsage(List<?> page){
		if (null == page) {
			return;
		}
		StringBuilder macLstBuffer = new StringBuilder();
		int i=0;
		for(Object oneObj:page){
			AhClientSession asection = (AhClientSession)oneObj;
			if (i==0) {
				macLstBuffer.append("'").append(asection.getClientMac()).append("'");
			} else {
				macLstBuffer.append(",'").append(asection.getClientMac()).append("'");
			}
			i++;
		}
		if (!page.isEmpty()) {
			long curTime = System.currentTimeMillis();
			String sqlRate = "select bo.clientMac,sum(bo.txFrameByteCount + bo.rxFrameByteCount) from " + AhClientStats.class.getSimpleName() + " bo where bo.timeStamp >= "
				+ (curTime-1000*60*60*2);
	
			sqlRate = sqlRate + " and bo.timeStamp < " + curTime;
			sqlRate = sqlRate + " and bo.clientMac in (" + macLstBuffer.toString() + ")";
			sqlRate = sqlRate + " group by bo.clientMac";
			try {
				List<?> profilesRate = QueryUtil.executeQuery(sqlRate, null, null);
				for(Object oneObj:page){
					AhClientSession asection = (AhClientSession)oneObj;
					if (asection.getClientChannel()<=0) {
						asection.setLast2HourData(0);
					} else {
						for(Object oneSearch:profilesRate){
							Object[] oneSearchDate = (Object[])oneSearch;
							if (asection.getClientMac().equalsIgnoreCase(oneSearchDate[0].toString())){
								asection.setLast2HourData(Long.parseLong(oneSearchDate[1].toString()));
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("setLastHourDataUsage", "Set last hour data usage error.", e);
			}
		}
	}

	private void setMonitorStatus(List<?> page) {
		if (null == page) {
			return;
		}
		for (Object obj : page) {
			AhClientSession client = (AhClientSession) obj;
			String domainName = client.getOwner().getDomainName();
			String clientMac = client.getClientMac();
			Collection<ClientMonitor> debugs = clientDebugger.getRequests(domainName, clientMac);
			if(null != debugs && !debugs.isEmpty()){
				client.setMonitoring(true);
				anyMonitoring = true;
				indent = 24;
			}
		}
	}

	public static void setUserTimeZone(List<?> page, String userTimeZone) {
		if (null == page) {
			return;
		}
		for (Object obj : page) {
			AhClientSession client = (AhClientSession) obj;
			client.setStartTimeZone(userTimeZone);
			client.setEndTimeZone(userTimeZone);
		}
	}

	/*
	 * one client pagingCache object per session.
	 */
	protected ClientPagingCache getClientListCache() {
		ClientPagingCache clientPagingCache = (ClientPagingCache) MgrUtil
				.getSessionAttribute(SessionKeys.CLIENT_PAGING_CACHE);
		if (clientPagingCache == null) {
			clientPagingCache = new ClientPagingCache(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.CLIENT_PAGING_CACHE,
					clientPagingCache);
		}
		return clientPagingCache;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_MAC = 1;

	public static final int COLUMN_VENDOR = 2;

	public static final int COLUMN_IP = 3;

	public static final int COLUMN_HOSTNAME = 4;

	public static final int COLUMN_USERNAME = 5;

	public static final int COLUMN_SESSIONSTART = 6;

	public static final int COLUMN_APNAME = 7;

	public static final int COLUMN_APMAC = 8;

	public static final int COLUMN_SSID = 9;

	public static final int COLUMN_BSSID = 10;

	public static final int COLUMN_MACPROTOCOL = 11;

	public static final int COLUMN_VLAN = 12;

	public static final int COLUMN_USERPROFILEID = 13;

	public static final int COLUMN_CLIENTAUTH = 14;

	public static final int COLUMN_ENCRYPTION = 15;

	public static final int COLUMN_COMMENT1 = 16;

	public static final int COLUMN_COMMENT2 = 17;

	public static final int COLUMN_LOCATION = 18;
	
	public static final int COLUMN_CHANNEL = 19;
	
	public static final int COLUMN_HEALTH = 20;
	
	public static final int COLUMN_IFNAME = 21;
	
	public static final int COLUMN_OSINFO=22;
	
	public static final int COLUMN_LASTHOUR_DATA=23;
	
	public static final int COLUMN_USER_EMAIL=24;
	
	public static final int COLUMN_USER_COMPANY=25;
	
	public static final int COLUMN_CLIENT_RSSI = 26;
	
	public static final int COLUMN_CLIENT_SNR = 27;
	
	public static final int COLUMN_CLIENTTYPE = 28;
	public static final int COLUMN_NATIP = 29;
	
	public static final int COLUMN_ENROLLED=30;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 * @author Joseph Chen
	 */
	@Override
	public String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_MAC:
			code = "monitor.client.clientMac";
			break;
		case COLUMN_VENDOR:
			code = "report.reportList.title.vendorName";
			break;
		case COLUMN_IP:
			code = "monitor.client.clientIP";
			break;
		case COLUMN_HOSTNAME:
			code = "monitor.client.clientHostname";
			break;
		case COLUMN_USERNAME:
			code = "monitor.client.clientUserName";
			break;
		case COLUMN_LOCATION:
			code = "monitor.client.clientLocation";
			break;
		case COLUMN_SESSIONSTART:
			code = "monitor.client.startTime";
			break;
		case COLUMN_APNAME:
			if (isWiredClientList()) {
				code = "monitor.client.deviceName";
			} else {
				code = "monitor.client.apName";
			}
			break;
		case COLUMN_APMAC:
			code = "monitor.client.associationAP";
			break;
		case COLUMN_SSID:
			if (isWiredClientList()) {
				code = "monitor.client.ssid.lan";
			} else {
				code = "monitor.client.ssid";
			}
			break;
		case COLUMN_BSSID:
			code = "monitor.client.bssid";
			break;
		case COLUMN_MACPROTOCOL:
			code = "monitor.client.associateMode";
			break;
		case COLUMN_VLAN:
			code = "monitor.client.vlan";
			break;
		case COLUMN_USERPROFILEID:
			code = "monitor.client.userProfileID";
			break;
		case COLUMN_CLIENTAUTH:
			code = "monitor.client.clientAuth";
			break;
		case COLUMN_ENCRYPTION:
			code = "monitor.client.encryption";
			break;
		case COLUMN_COMMENT1:
			code = "monitor.client.comment1";
			break;
		case COLUMN_COMMENT2:
			code = "monitor.client.comment2";
			break;
		case COLUMN_CHANNEL:
			code = "monitor.client.channel";
			break;
		case COLUMN_HEALTH:
			code = "monitor.client.health";
			break;
		case COLUMN_IFNAME:
			code = "monitor.client.ifName";
			break;
		case COLUMN_OSINFO:
			code= "monitor.client.osInfo";
			break;
		case COLUMN_LASTHOUR_DATA:
			code= "monitor.client.lastHourData";
			break;
		case COLUMN_USER_EMAIL:
			code= "monitor.client.email";
			break;
		case COLUMN_USER_COMPANY:
			code= "monitor.client.company";
			break;
		case COLUMN_CLIENT_RSSI:
			code = "monitor.client.rssi";
			break;
		case COLUMN_CLIENT_SNR:
			code = "monitor.client.snr";
			break;
		case COLUMN_CLIENTTYPE:
			code = "monitor.client.type";
			break;
		case COLUMN_NATIP:
			code = "monitor.client.clientNatIP";
			break;
		case COLUMN_ENROLLED: 
			code= "monitor.enrolled.client.managed";
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getInitSelectedColumns() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(18);

		columns.add(new HmTableColumn(COLUMN_HEALTH));
		columns.add(new HmTableColumn(COLUMN_MAC));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_NATIP));
		columns.add(new HmTableColumn(COLUMN_HOSTNAME));
		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_OSINFO));
		if (!isEasyMode()) {
			//columns.add(new HmTableColumn(COLUMN_USER_EMAIL));
			//columns.add(new HmTableColumn(COLUMN_USER_COMPANY));
			if (!isWiredClientList()) {
				columns.add(new HmTableColumn(COLUMN_LOCATION)); // not show on wired client list
			}
		}
		columns.add(new HmTableColumn(COLUMN_VLAN));
		if("all".equals(listType)){
			columns.add(new HmTableColumn(COLUMN_CLIENTTYPE));
		}
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_LASTHOUR_DATA)); // not show on wired client list
		}
		columns.add(new HmTableColumn(COLUMN_SESSIONSTART));
		columns.add(new HmTableColumn(COLUMN_APNAME));
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_CLIENT_RSSI)); // not show on wired client list
			columns.add(new HmTableColumn(COLUMN_CLIENT_SNR)); // not show on wired client list
		}
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_IFNAME));
		columns.add(new HmTableColumn(COLUMN_CLIENTAUTH));
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_CHANNEL)); // not show on wired client list
		}
		/*columns.add(new HmTableColumn(COLUMN_COMMENT1));
		columns.add(new HmTableColumn(COLUMN_COMMENT2));*/
		return columns;
	}
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(28);

		columns.add(new HmTableColumn(COLUMN_HEALTH));
		columns.add(new HmTableColumn(COLUMN_MAC));
		columns.add(new HmTableColumn(COLUMN_VENDOR));
		columns.add(new HmTableColumn(COLUMN_IP));
		columns.add(new HmTableColumn(COLUMN_NATIP));
		columns.add(new HmTableColumn(COLUMN_HOSTNAME));
		columns.add(new HmTableColumn(COLUMN_USERNAME));
		columns.add(new HmTableColumn(COLUMN_OSINFO));
		if (!isEasyMode()) {
			columns.add(new HmTableColumn(COLUMN_USER_EMAIL));
			columns.add(new HmTableColumn(COLUMN_USER_COMPANY));
			if (!isWiredClientList()) {
				columns.add(new HmTableColumn(COLUMN_LOCATION)); // not show on wired client list
			}
		}
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_LASTHOUR_DATA)); // not show on wired client list
		}
		columns.add(new HmTableColumn(COLUMN_SESSIONSTART));
		columns.add(new HmTableColumn(COLUMN_APNAME));
		columns.add(new HmTableColumn(COLUMN_APMAC));
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_CLIENT_RSSI)); // not show on wired client list
			columns.add(new HmTableColumn(COLUMN_CLIENT_SNR)); // not show on wired client list
		}
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_IFNAME));
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_BSSID)); // not show on wired client list
			columns.add(new HmTableColumn(COLUMN_MACPROTOCOL)); // not show on wired client list
		}
		columns.add(new HmTableColumn(COLUMN_VLAN));
		columns.add(new HmTableColumn(COLUMN_USERPROFILEID));
		columns.add(new HmTableColumn(COLUMN_CLIENTAUTH));
		if (!isWiredClientList()) {
			columns.add(new HmTableColumn(COLUMN_ENCRYPTION)); // not show on wired client list
			columns.add(new HmTableColumn(COLUMN_CHANNEL)); // not show on wired client list
		}
		columns.add(new HmTableColumn(COLUMN_COMMENT1));
		columns.add(new HmTableColumn(COLUMN_COMMENT2));
		if("all".equals(listType)){
			columns.add(new HmTableColumn(COLUMN_CLIENTTYPE));
		}
//		if (!isWiredClientList() && getEnableClientManagement()) {
		if (!isWiredClientList() && isFullMode()) {
			columns.add(new HmTableColumn(COLUMN_ENROLLED));
		}
		return columns;
	}
	
	public String getFilter() {
		if (null == filter) {
			filter = (String) MgrUtil
					.getSessionAttribute(MANAGED_ACTIVECLIENT_CURRENT_FILTER);
		}
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	private void resetFilterParams() throws Exception {
		// get active clients
		//filterParams = new FilterParams("connectstate",
		//		AhClientSession.CONNECT_STATE_UP);
		filterParams = null;
		setSessionFiltering();
		MgrUtil.removeSessionAttribute(MANAGED_ACTIVECLIENT_CURRENT_FILTER);
	}

	public List<String> getFilterList() {
		// Map<String, List> filterMap = (Map<String, List>) MgrUtil
		// .getSessionAttribute(MANAGED_ACTIVECLIENT_FILTERS);
		// if (null != filterMap) {
		// return new ArrayList<String>(filterMap.keySet());
		// }
		// return new ArrayList<String>();
		List<String> filterMap = (List<String>) QueryUtil.executeQuery(
				"select filterName from "
						+ ActiveClientFilter.class.getSimpleName(), null,
				new FilterParams("userName", getUserContext().getUserName()),
				domainId);

		// order by name
		Collections.sort(filterMap, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		return filterMap;
	}

	private final int DEAUTH_CLIENT_ALL = 1;

	private final int DEAUTH_CLIENT_SELECTED = 2;

	private int deauthCacheSelect;

	private int deauthClientSelect = DEAUTH_CLIENT_SELECTED;

	private final int DEAUTH_CACHE_ALL = 1;

	private final int DEAUTH_CACHE_LOCAL = 2;

	private final int DEAUTH_CACHE_ROAMING = 3;

	// private final int DEAUTH_CACHE_STATION = 4;

	public EnumItem[] getDeauthCacheSelectList() {
		return MgrUtil.enumItems("enum.activeClient.deauth.cache.", new int[] {
				DEAUTH_CACHE_ALL, DEAUTH_CACHE_LOCAL, DEAUTH_CACHE_ROAMING });
	}

	public EnumItem[] getDeauthClientSelectList() {
		return MgrUtil.enumItems("enum.activeClient.deauth.clientMac.",
				new int[] { DEAUTH_CLIENT_ALL, DEAUTH_CLIENT_SELECTED });
	}

	/**
	 * deauth all clients from AP
	 *
	 * @return -
	 */
	private boolean deauthAllClients() {
		List<SimpleHiveAp> list = cacheMgmt.getManagedApList(getDomainId());

		if (list == null || list.isEmpty()) {
			log.warning("deauthAllClients",
					"Deauth all clients, but there are no managed APs");
			return true;
		}

		// cli array
		String[] clis = null;

		if (clearCache) {
			switch (deauthCacheSelect) {
			case DEAUTH_CACHE_LOCAL: {
				clis = new String[1];
				clis[0] = "clear auth local-cache" + AH_CLI_SUFFIX;

				break;
			}

			case DEAUTH_CACHE_ROAMING: {
				clis = new String[2];
				clis[0] = "clear auth roaming-cache hive-neighbors" + AH_CLI_SUFFIX;
				clis[1] = "clear auth roaming-cache" + AH_CLI_SUFFIX;

				break;
			}

			case DEAUTH_CACHE_ALL: {
				clis = new String[4];
				clis[0] = "clear auth local-cache" + AH_CLI_SUFFIX;
				clis[1] = "clear auth roaming-cache hive-neighbors" + AH_CLI_SUFFIX;
				clis[2] = "clear auth roaming-cache" + AH_CLI_SUFFIX;
				clis[3] = "clear auth station" + AH_CLI_SUFFIX;

				break;
			}

			default:
				break;
			}
		} else {
			// just deauth
			clis = new String[1];
			clis[0] = "clear auth station" + AH_CLI_SUFFIX;
		}

		/**
		 * send clis to all ap
		 */
		for (SimpleHiveAp ap : list) {
			boolean isSuccess = sendCliRequest(ap, clis);
			if (!isSuccess) {
				return false;
			}
		}

		return true;
	}

	/**
	 * key: AhCurrentClientSession.getClientMac<br>
	 * value: SimpleHiveAp
	 */
	private final Map<String, SimpleHiveAp> clientAPMap = new HashMap<String, SimpleHiveAp>();

	private final Map<String, SimpleHiveAp> clientSimpleAPMapMDM = new HashMap<String, SimpleHiveAp>();
	
	private final Map<String, String> mapClisMDM = new HashMap<String, String>();

	/**
	 * key: hiveprofile ID<br>
	 * value: ap list
	 */
	private final Map<BigInteger, List<SimpleHiveAp>> hiveAPMap = new HashMap<BigInteger, List<SimpleHiveAp>>();

	/**
	 * key: ap Mac<br>
	 * value: hive profile ID
	 */
	private final Map<String, BigInteger> apHiveIDMap = new HashMap<String, BigInteger>();

	/**
	 * create cache for deauth
	 * 
	 * @param ids -
	 */
	private void createCache4Deauth(List<Long> ids) {
		try {
			// String where = "macAddress = :s1 AND manageStatus = :s2";
			// Object[] values = new Object[2];

			for (Long clientID : ids) {
				// 1. get client obj
//				AhClientSession client = findBoById(AhClientSession.class, clientID);
				//there is different than before ,please check
				AhClientSession client = DBOperationUtil.findBoById(AhClientSession.class, clientID);
				if (client == null) {
					continue;
				}

				// 2. get ap obj
				// values[0] = client.getApMac();
				// values[1] = HiveAp.STATUS_MANAGED;
				// List<?> list = QueryUtil.executeQuery(HiveAp.class, null,
				// new FilterParams(where, values));
				// if (list.isEmpty())
				// {
				// continue;
				// }
				// HiveAp ap = (HiveAp) list.get(0);

				SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(client.getApMac());
				if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
					continue;
				}

				// cache client->ap
				clientAPMap.put(client.getClientMac(), ap);

				// 3. get hiveID,
				// 'apHiveIDMap' for reduce request counts with DB
				BigInteger hiveID;
				if (apHiveIDMap.containsKey(ap.getMacAddress())) {
					hiveID = apHiveIDMap.get(ap.getMacAddress());
				} else {
					List<?> listHiveID = QueryUtil
							.executeNativeQuery("select b.hive_profile_id from hive_ap a,config_template b where a.template_id=b.id and a.macAddress='"
									+ ap.getMacAddress() + "'");
					if (listHiveID.isEmpty()) {
						continue;
					}

					hiveID = (BigInteger) listHiveID.get(0);

					apHiveIDMap.put(ap.getMacAddress(), hiveID);
				}

				// 4. cache hive ID-> ap list
				if (hiveAPMap.containsKey(hiveID)) {
					List<SimpleHiveAp> apList = hiveAPMap.get(hiveID);
					apList.add(ap);
				} else {
					List<SimpleHiveAp> apList = new ArrayList<SimpleHiveAp>();
					apList.add(ap);
					hiveAPMap.put(hiveID, apList);
				}
			}
		} catch (Exception e) {
			log.error("createCache4Deauth", "catch exception", e);
		}
	}

	/**
	 * deauth clients
	 * 
	 * @param ids -
	 * @return -
	 */
	private boolean deauthClient(List<Long> ids) {
		createCache4Deauth(ids);

		if (!clearCache) {
			// just deauth
			for (String clientMac : clientAPMap.keySet()) {
				String[] clis = new String[1];
				clis[0] = "clear auth station mac " + clientMac + AH_CLI_SUFFIX;

				if (!sendCliRequest(clientAPMap.get(clientMac), clis)) {
					return false;
				}
			}
		} else {
			for (String clientMac : clientAPMap.keySet()) {
				switch (deauthCacheSelect) {
				case DEAUTH_CACHE_LOCAL: {
					String[] clis = new String[2];
					clis[0] = "clear auth station mac " + clientMac
							+ AH_CLI_SUFFIX;
					clis[1] = "clear auth local-cache mac " + clientMac
							+ AH_CLI_SUFFIX;

					if (!sendCliRequest(clientAPMap.get(clientMac), clis)) {
						return false;
					}

					break;
				}

				case DEAUTH_CACHE_ROAMING: {
					String[] clis = new String[1];
					clis[0] = "clear auth station mac " + clientMac
							+ AH_CLI_SUFFIX;

					if (!sendCliRequest(clientAPMap.get(clientMac), clis)) {
						return false;
					}

					if (!deauthRoaming(clientMac)) {
						return false;
					}

					break;
				}

				case DEAUTH_CACHE_ALL: {
					String[] clis = new String[2];
					clis[0] = "clear auth station mac " + clientMac
							+ AH_CLI_SUFFIX;
					clis[1] = "clear auth local-cache mac " + clientMac
							+ AH_CLI_SUFFIX;

					if (!sendCliRequest(clientAPMap.get(clientMac), clis)) {
						return false;
					}

					if (!deauthRoaming(clientMac)) {
						return false;
					}

					break;
				}

				default:
					break;
				}
			}
		}

		return true;
	}

	/**
	 * clear roaming cache special<br>
	 * we need clear roaming cache on all ap in the same Hive
	 * 
	 * @param clientMac -
	 * @return -
	 */
	private boolean deauthRoaming(String clientMac) {
		//mark: add 'hive_neighbors' in another cli, this key only supported by 3.4 and later version AP, 
		//for avoiding chech the version of ap, we add this item at the tail.
		String[] clis = new String[2];
		clis[0] = "clear auth roaming-cache mac " + clientMac + " hive-neighbors" + AH_CLI_SUFFIX;
		clis[1] = "clear auth roaming-cache mac " + clientMac + AH_CLI_SUFFIX;

		List<?> listHiveID = QueryUtil
				.executeNativeQuery("select b.hive_profile_id from hive_ap a,config_template b where a.template_id=b.id and a.macAddress='"
						+ clientAPMap.get(clientMac).getMacAddress() + "'");
		if (listHiveID.isEmpty()) {
			log.error("deauthRoaming",
					"Can't query valid hive_profile id from DB");
			return false;
		}

		BigInteger hiveID = (BigInteger) listHiveID.get(0);

		List<SimpleHiveAp> list = hiveAPMap.get(hiveID);

		for (SimpleHiveAp hiveap : list) {
			if (!sendCliRequest(hiveap, clis)) {
				return false;
			}
		}

		return true;
	}

	private static final int TIMEOUT_CLI = 35; // second

	/**
	 * send cli request
	 * 
	 * @param ap -
	 * @param clis -
	 * @return -
	 */
	private boolean sendCliRequest(SimpleHiveAp ap, String[] clis) {
		try {
			BeCliEvent c_event = new BeCliEvent();
			c_event.setSimpleHiveAp(ap);
			c_event.setClis(clis);
			c_event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			// c_event.setCliData(new BeCapwapCliEvent(clis,
			// HmBeCommunicationUtil
			// .getSequenceNumber()).buildPacket());
			c_event.buildPacket();
			int serialNum = HmBeCommunicationUtil.sendRequest(c_event,
					TIMEOUT_CLI);
			return (serialNum != BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED);
		} catch (Exception e) {
			log.error("sendCliRequest",
					"Communication closed or build packet error.", e);
			return false;
		}
	}

	public String getSelectedClientIDStr() {
		return selectedClientIDStr;
	}

	public void setSelectedClientIDStr(String selectedClientIDStr) {
		this.selectedClientIDStr = selectedClientIDStr;
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean isSelectAll) {
		this.selectAll = isSelectAll;
	}

	public int getDeauthCacheSelect() {
		return deauthCacheSelect;
	}

	public void setDeauthCacheSelect(int deauthCacheSelect) {
		this.deauthCacheSelect = deauthCacheSelect;
	}

	public int getDeauthClientSelect() {
		return deauthClientSelect;
	}

	public void setDeauthClientSelect(int deauthClientSelect) {
		this.deauthClientSelect = deauthClientSelect;
	}

	public boolean isClearCache() {
		return clearCache;
	}

	public void setClearCache(boolean isClearCache) {
		this.clearCache = isClearCache;
	}

	// public String getFilterVHM() {
	// return filterVHM;
	// }
	//
	// public void setFilterVHM(String filterVHM) {
	// this.filterVHM = filterVHM;
	// }

	public AhClientSession getActiveClient() {
		return activeClient;
	}

	public void setActiveClient(AhClientSession activeClient) {
		this.activeClient = activeClient;
	}

	public Long getFilterMap() {
		return filterMap;
	}

	public void setFilterMap(Long filterMap) {
		this.filterMap = filterMap;
	}

	public String getEditComment1() {
		return editComment1;
	}

	public void setEditComment1(String editComment1) {
		this.editComment1 = editComment1;
	}

	public String getEditComment2() {
		return editComment2;
	}

	public void setEditComment2(String editComment2) {
		this.editComment2 = editComment2;
	}

	public String getEditHostName() {
		return editHostName;
	}

	public void setEditHostName(String editHostName) {
		this.editHostName = editHostName;
	}

	public String getEditIP() {
		return editIP;
	}

	public void setEditIP(String editIP) {
		this.editIP = editIP;
	}

	public String getEditUserName() {
		return editUserName;
	}

	public void setEditUserName(String editUserName) {
		this.editUserName = editUserName;
	}

	public boolean isFlagEditComment1() {
		return flagEditComment1;
	}

	public void setFlagEditComment1(boolean flagEditComment1) {
		this.flagEditComment1 = flagEditComment1;
	}

	public boolean isFlagEditComment2() {
		return flagEditComment2;
	}

	public void setFlagEditComment2(boolean flagEditComment2) {
		this.flagEditComment2 = flagEditComment2;
	}

	public boolean isFlagEditHostName() {
		return flagEditHostName;
	}

	public void setFlagEditHostName(boolean flagEditHostName) {
		this.flagEditHostName = flagEditHostName;
	}

	public boolean getFlagEditIP() {
		return flagEditIP;
	}

	public void setFlagEditIP(boolean flagEditIP) {
		this.flagEditIP = flagEditIP;
	}

	public boolean getFlagEditUserName() {
		return flagEditUserName;
	}

	public void setFlagEditUserName(boolean flagEditUserName) {
		this.flagEditUserName = flagEditUserName;
	}

	public void setFilterMaps(List<CheckItem> filterMaps) {
		this.filterMaps = filterMaps;
	}

	// public boolean isFlagClearEdit() {
	// return flagClearEdit;
	// }
	//
	// public void setFlagClearEdit(boolean flagClearEdit) {
	// this.flagClearEdit = flagClearEdit;
	// }

	public String getAssociationApId() {
		return associationApId;
	}

	public String getFilterClientHostName() {
		return filterClientHostName;
	}

	public void setFilterClientHostName(String filterClientHostName) {
		this.filterClientHostName = filterClientHostName;
	}

	public String getFilterClientIP() {
		return filterClientIP;
	}

	public void setFilterClientIP(String filterClientIP) {
		this.filterClientIP = filterClientIP;
	}

	public String getFilterClientUserName() {
		return filterClientUserName;
	}

	public void setFilterClientUserName(String filterClientUserName) {
		this.filterClientUserName = filterClientUserName;
	}

	public int getCacheId() {
		return cacheId;
	}

	public void setCacheId(int cacheId) {
		this.cacheId = cacheId;
	}

	public boolean getDisableAddToWatchList() {
		long locationWatchListSize = QueryUtil.findRowCount(
				LocationClientWatch.class, new FilterParams(
						"owner.id=:s1 AND defaultFlag=:s2", new Object[] {
								getDomainId(), false }));

		return locationWatchListSize <= 0;
	}

	public List<LocationClientWatch> getLocationWatchList() {
		try {
			return QueryUtil.executeQuery(LocationClientWatch.class, null,
					new FilterParams("defaultFlag", false), getDomainId());
		} catch (Exception e) {
			log.error("getLocationWatchList",
					"query location client watch list catch exception", e);
		}

		return new ArrayList<LocationClientWatch>(0);
	}
	
	public boolean getDisableAddToComputerCartList() {
		long size = QueryUtil.findRowCount(TvComputerCart.class, new FilterParams("owner.id",
				getDomainId()));

		return size <= 0;
	}
	
	public List<TvComputerCart> getComputerCartList() {
		try {
			return QueryUtil.executeQuery(TvComputerCart.class, null, null, getDomainId());
		} catch (Exception e) {
			log.error("getComputerCartList", "catch exception", e);
		}

		return new ArrayList<TvComputerCart>(0);
	}

	public String getClientWatchName() {
		return clientWatchName;
	}

	public void setClientWatchName(String clientWatchName) {
		this.clientWatchName = clientWatchName;
	}

	public String getSelectedClientsMac() {
		return selectedClientsMac;
	}

	public void setSelectedClientsMac(String selectedClientsMac) {
		this.selectedClientsMac = selectedClientsMac;
	}

	public List<CheckItem> getClient_trans_totalData() {
		return client_trans_totalData;
	}

	public List<CheckItem> getClient_trans_beData() {
		return client_trans_beData;
	}

	public List<CheckItem> getClient_trans_bgData() {
		return client_trans_bgData;
	}

	public List<CheckItem> getClient_trans_viData() {
		return client_trans_viData;
	}

	public List<CheckItem> getClient_trans_voData() {
		return client_trans_voData;
	}

	public List<CheckItem> getClient_trans_mgtData() {
		return client_trans_mgtData;
	}

	public List<CheckItem> getClient_trans_unicastData() {
		return client_trans_unicastData;
	}

	public List<CheckItem> getClient_trans_dataOctets() {
		return client_trans_dataOctets;
	}

	public List<CheckItem> getClient_trans_lastrate() {
		return client_trans_lastrate;
	}

	public List<CheckItem> getClient_rec_totalData() {
		return client_rec_totalData;
	}

	public List<CheckItem> getClient_rec_mgtData() {
		return client_rec_mgtData;
	}

	public List<CheckItem> getClient_rec_unicastData() {
		return client_rec_unicastData;
	}

	public List<CheckItem> getClient_rec_multicastData() {
		return client_rec_multicastData;
	}

	public List<CheckItem> getClient_rec_broadcastData() {
		return client_rec_broadcastData;
	}

	public List<CheckItem> getClient_rec_micfailures() {
		return client_rec_micfailures;
	}

	public List<CheckItem> getClient_rec_dataOctets() {
		return client_rec_dataOctets;
	}

	public List<CheckItem> getClient_rec_lastrate() {
		return client_rec_lastrate;
	}

	public List<CheckItem> getClient_rssi() {
		return client_rssi;
	}
	
	public List<CheckItem> getClient_signal_to_noise() {
		return client_signal_to_noise;
	}

	public List<CheckItem> getClient_sla() {
		return client_sla;
	}

//	public List<CheckItem> getClient_actualSla() {
//		return client_actualSla;
//	}

	public String getSwf() {
		return swf;
	}

	public void setSwf(String swf) {
		this.swf = swf;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public AhAssociation getOneAssociation() {
		return oneAssociation;
	}

	public void setOneAssociation(AhAssociation oneAssociation) {
		this.oneAssociation = oneAssociation;
	}

	public String getClientAuthMethodString() {
		return clientAuthMethodString;
	}

	public String getClientEncryptionMethodString() {
		return clientEncryptionMethodString;
	}

	public String getClientCWPUsedString() {
		return clientCWPUsedString;
	}

	public String getClientBSSID() {
		return clientBSSID;
	}

	public String getClientVLAN() {
		return clientVLAN;
	}

	public String getClientChannel() {
		return clientChannel;
	}
	
	public List<CheckItem> getClientRefreshFilterList() {
		List<?> filters = QueryUtil.executeQuery(
				"select bo.id, bo.filterName from " + ActiveClientFilter.class.getSimpleName() + " bo", new SortParams("id"),
				null, getDomainId());
		List<CheckItem> filterList = new ArrayList<CheckItem>();
		for (Object obj : filters) {
			Object[] filter = (Object[]) obj;
			CheckItem checkItem = new CheckItem((Long) filter[0], (String) filter[1]);
			filterList.add(checkItem);
		}

		return filterList;
	}

	public Long getClientRefreshFilter() {
		return clientRefreshFilter;
	}

	public void setClientRefreshFilter(Long clientRefreshFilter) {
		this.clientRefreshFilter = clientRefreshFilter;
	}

	public String getClientRefreshFlag() {
		return clientRefreshFlag;
	}

	public void setClientRefreshFlag(String clientRefreshFlag) {
		this.clientRefreshFlag = clientRefreshFlag;
	}

	public int getClientRefreshInterval() {
		return clientRefreshInterval;
	}

	public void setClientRefreshInterval(int clientRefreshInterval) {
		this.clientRefreshInterval = clientRefreshInterval;
	}

	public boolean isDisableClientRefresh() {
		return disableClientRefresh;
	}

	public void setDisableClientRefresh(boolean disableClientRefresh) {
		this.disableClientRefresh = disableClientRefresh;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<CheckItem> getClient_rec_drop() {
		return client_rec_drop;
	}

	public List<CheckItem> getClient_trans_drop() {
		return client_trans_drop;
	}

	public List<CheckItem> getClient_bandwidth() {
		return client_bandwidth;
	}

	public List<CheckItem> getClient_slacount() {
		return client_slacount;
	}

	public List<CheckItem> getClient_rec_airTime() {
		return client_rec_airTime;
	}

	public List<CheckItem> getClient_trans_airTime() {
		return client_trans_airTime;
	}

	public List<String> getClient_rec_rateTypeList() {
		return client_rec_rateTypeList;
	}

	public List<String> getClient_rec_dateTimeList() {
		return client_rec_dateTimeList;
	}

	public List<String> getClient_trans_rateTypeList() {
		return client_trans_rateTypeList;
	}

	public List<String> getClient_trans_dateTimeList() {
		return client_trans_dateTimeList;
	}

	public Map<String, List<CheckItem>> getClient_rec_rate_dis() {
		return client_rec_rate_dis;
	}

	public List<TextItem> getClient_rec_rate_succ_dis() {
		return client_rec_rate_succ_dis;
	}

	public Map<String, List<CheckItem>> getClient_trans_rate_dis() {
		return client_trans_rate_dis;
	}

	public List<TextItem> getClient_trans_rate_succ_dis() {
		return client_trans_rate_succ_dis;
	}

	public List<AhClientStats> getNewReportDataList() {
		return newReportDataList;
	}
	
	public String convertRateToM(int rateValue){
		if (rateValue%1000==0) {
			return String.valueOf(rateValue/1000);
		} else {
			return String.valueOf(((float)rateValue)/1000);
		}
	}

	/**
	 * @return the client_score
	 */
	public List<CheckItem> getClient_score() {
		return client_score;
	}

	public String getSelectedClientsName() {
		return selectedClientsName;
	}

	public void setSelectedClientsName(String selectedClientsName) {
		this.selectedClientsName = selectedClientsName;
	}

	/**
	 * @return the clientRadioScore
	 */
	public int getClientRadioScore() {
		return clientRadioScore;
	}

	/**
	 * @param clientRadioScore the clientRadioScore to set
	 */
	public void setClientRadioScore(int clientRadioScore) {
		this.clientRadioScore = clientRadioScore;
	}

	/**
	 * @return the clientIpNetworkScore
	 */
	public int getClientIpNetworkScore() {
		return clientIpNetworkScore;
	}

	/**
	 * @param clientIpNetworkScore the clientIpNetworkScore to set
	 */
	public void setClientIpNetworkScore(int clientIpNetworkScore) {
		this.clientIpNetworkScore = clientIpNetworkScore;
	}

	/**
	 * @return the clientApplicationScore
	 */
	public int getClientApplicationScore() {
		return clientApplicationScore;
	}

	/**
	 * @param clientApplicationScore the clientApplicationScore to set
	 */
	public void setClientApplicationScore(int clientApplicationScore) {
		this.clientApplicationScore = clientApplicationScore;
	}

	/**
	 * @return the client_radio_score
	 */
	public List<CheckItem> getClient_radio_score() {
		return client_radio_score;
	}

	/**
	 * @return the client_ipnetwork_score
	 */
	public List<CheckItem> getClient_ipnetwork_score() {
		return client_ipnetwork_score;
	}

	/**
	 * @return the client_application_score
	 */
	public List<CheckItem> getClient_application_score() {
		return client_application_score;
	}

	/**
	 * @return the clientOsInfo
	 */
	public String getClientOsInfo() {
		return clientOsInfo;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public String getUserCompany() {
		return userCompany;
	}

//	/**
//	 * @return the client_trans_total_rate_succ_dis
//	 */
//	public List<TextItem> getClient_trans_total_rate_succ_dis() {
//		return client_trans_total_rate_succ_dis;
//	}
//
//	/**
//	 * @return the client_rec_total_rate_succ_dis
//	 */
//	public List<TextItem> getClient_rec_total_rate_succ_dis() {
//		return client_rec_total_rate_succ_dis;
//	}
	
	/**
	 * wired/wireless/both
	 */
	private String listType = "all";
	
	private String pageFrom;

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}
	
	public boolean isWiredClientList() {
		return "wired".equals(getListType());
	}
	
	public boolean isWirelessClientList() {
		return "wireless".equals(getListType());
	}
	
	public boolean isWiredClient() {
		return "wired".equals(getListType());
	}
	
	private void setCurrentListType() {
		String sessionKey = boClass.getSimpleName() + "ListType";
		String lstType = (String) MgrUtil.getSessionAttribute(sessionKey);
		if (lstType == null || "".equals(lstType)) return;
		if (getListType() != null && !"".equals(getListType()) 
				&& ((!getListType().equals(lstType) && !("search".equals(operation) && !isBlnInnerSearch()))
						|| null == operation 
						|| "view".equals(operation))
				) {
			//remove previous sort and filter
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Filtering", null);
			MgrUtil.setSessionAttribute(boClass.getSimpleName() + "Sorting", null);
		}
	}
	
	private String getSessionListType() {
		String sessionKey = boClass.getSimpleName() + "ListType";
		return (String) MgrUtil.getSessionAttribute(sessionKey);
	}
	
	private boolean blnInnerSearch;

	public boolean isBlnInnerSearch() {
		return blnInnerSearch;
	}

	public void setBlnInnerSearch(boolean blnInnerSearch) {
		this.blnInnerSearch = blnInnerSearch;
	}

	public String getFilterClientOsInfo() {
		return filterClientOsInfo;
	}

	public void setFilterClientOsInfo(String filterClientOsInfo) {
		this.filterClientOsInfo = filterClientOsInfo;
	}

	public int getFilterClientVLAN() {
		return filterClientVLAN;
	}

	public void setFilterClientVLAN(int filterClientVLAN) {
		this.filterClientVLAN = filterClientVLAN;
	}

	public int getFilterClientUserProfId() {
		return filterClientUserProfId;
	}

	public void setFilterClientUserProfId(int filterClientUserProfId) {
		this.filterClientUserProfId = filterClientUserProfId;
	}

	public int getFilterClientChannel() {
		return filterClientChannel;
	}

	public void setFilterClientChannel(int filterClientChannel) {
		this.filterClientChannel = filterClientChannel;
	}

	public byte getFilterOverallClientHealth() {
		return filterOverallClientHealth;
	}

	public void setFilterOverallClientHealth(byte filterOverallClientHealth) {
		this.filterOverallClientHealth = filterOverallClientHealth;
	}
	
	public boolean showEnrollStatus(List<Long> clientIDs){
		prepareMDMData(clientIDs);
		StringBuilder content = new StringBuilder("");
		
		try {
			if(!clientSimpleAPMapMDM.isEmpty()){
				if(clientSimpleAPMapMDM.size() == 1){
					for(String mac : clientSimpleAPMapMDM.keySet()){
						String msg;
						SimpleHiveAp ap = clientSimpleAPMapMDM.get(mac);
						if(NmsUtil.compareSoftwareVersion(ap.getSoftVer(),"5.1.1.0") >= 0 && ap.getHiveApModel() != HiveAp.HIVEAP_MODEL_BR100 
								&& ap.getHiveApModel() !=  HiveAp.HIVEAP_MODEL_BR200 && ap.getDeviceType() != HiveAp.Device_TYPE_VPN_GATEWAY){
							BeCommunicationEvent result = sendSyncCliRequest(
									clientSimpleAPMapMDM.get(mac), new String[] {AhCliFactory.getEnrollStatusCli(formatMac(mac))}, BeCliEvent.CLITYPE_NORMAL,
									BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000);
							if(null == result){
								msg = MgrUtil.getUserMessage("error.cli.obj.buildRequest");
							}else{
								msg = parseResult(result);
							}
						}else{
							msg = "device "+ formatMac(mac) +" "+ MgrUtil.getUserMessage("error.mdm.object.lowversion","5.1.1.0");
						}
						if (null != msg) {
							jsonObject.put("msg", msg);
							return true;
						}
					}
				}else{
					List<String> notSupportClientMac = new ArrayList<String>(); 
					for(String mac : clientSimpleAPMapMDM.keySet()){
						SimpleHiveAp ap = clientSimpleAPMapMDM.get(mac);
						if(NmsUtil.compareSoftwareVersion(ap.getSoftVer(),"5.1.1.0") < 0 || ap.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR100 
								|| ap.getHiveApModel() == HiveAp.HIVEAP_MODEL_BR200 || ap.getDeviceType() == HiveAp.Device_TYPE_VPN_GATEWAY){
							notSupportClientMac.add(mac);
						}
					}
					for(String mac:notSupportClientMac){
						mapClisMDM.remove(mac);
					}
					
					List<BeCommunicationEvent> resultList = sendGroupSyncCliRequests(mapClisMDM,BeCliEvent.CLITYPE_NORMAL ,
							BeTopoModuleParameters.DEFAULT_CLI_TIMEOUT_MAX / 1000);
					
					if (null == resultList) {
						String msg = MgrUtil.getUserMessage("error.cli.obj.buildRequest");
						content.append(getSeparator()).append(msg).append(getBr());
						jsonObject.put("err", msg);
						return false;
					} else {
						for (BeCommunicationEvent be : resultList) {
							String msg = parseResult(be);
							if (null != msg) {
								content.append(msg).append(getBr());
							}
						}
						for(String mac:notSupportClientMac){
							String msg = "device "+ formatMac(mac) +" "+ MgrUtil.getUserMessage("error.mdm.object.lowversion","5.1.1.0");
							content.append(msg).append(getBr());
						}
						jsonObject.put("msg", content.toString());
						return true;
					}
				}
			}else{
				jsonObject.put("err", MgrUtil
						.getUserMessage("error.mdm.object.notfind"));
				return false;
			}
		} catch (Exception e) {
			log.error("showEnrollStatus", "catch build packet exception", e);
		}
		
		return false;
	}
	
	private void prepareMDMData(List<Long> clientIDs){
		try {
			for (Long clientID : clientIDs) {
				AhClientSession client = DBOperationUtil.findBoById(AhClientSession.class, clientID);
				if (client == null) {
					continue;
				}
				
				SimpleHiveAp ap = cacheMgmt.getSimpleHiveAp(client.getApMac());
				if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED) {
					continue;
				}

				clientSimpleAPMapMDM.put(client.getClientMac(),ap);
				mapClisMDM.put(client.getClientMac(),AhCliFactory.getEnrollStatusCli(formatMac(client.getClientMac())));
			}
		} catch (Exception e) {
			log.error("prepareMDMData", "catch exception", e);
		}
	}
	
	private List<BeCommunicationEvent> sendGroupSyncCliRequests(Map<String, String> cliMap, byte cliType,
			int timeout) {
		List<BeCliEvent> requests = new ArrayList<BeCliEvent>();
		List<BeCommunicationEvent> responses = new ArrayList<BeCommunicationEvent>();
		try {
			for(String mac: cliMap.keySet()){
				String[] clis = new String[1];
				clis[0] = cliMap.get(mac);
				BeCliEvent cliRequest = getCliEvent(clientSimpleAPMapMDM.get(mac),
						clis, cliType);
				requests.add(cliRequest);
			}
			if(!requests.isEmpty()){
				responses = HmBeCommunicationUtil.sendSyncGroupRequest(requests,
						timeout);
			}
			return responses;
		} catch (Exception e) {
			log.error("sendGroupSyncCliRequests",
					"catch build packet exception", e);
			return null;
		}
	}
    
	public BeCommunicationEvent sendSyncCliRequest(SimpleHiveAp hiveAp,
			String[] clis, byte cliType, int timeout) {
		try {
			BeCliEvent cliRequest = getCliEvent(hiveAp, clis, cliType);
			return HmBeCommunicationUtil.sendSyncRequest(cliRequest, timeout);
		} catch (Exception e) {
			log.error("sendSyncCliRequest", "catch build packet exception", e);
			return null;
		}
	}
	
	public BeCliEvent getCliEvent(SimpleHiveAp hiveAp, String[] clis,
			byte cliType) throws BeCommunicationEncodeException {
		BeCliEvent cliRequest = new BeCliEvent();
		int sequenceNum = HmBeCommunicationUtil.getSequenceNumber();
		cliRequest.setSimpleHiveAp(hiveAp);
		cliRequest.setClis(clis);
		cliRequest.setCliType(cliType);
		cliRequest.setSequenceNum(sequenceNum);
		cliRequest.buildPacket();
		return cliRequest;
	}
	
	private String parseResult(BeCommunicationEvent result) throws Exception {
		return BeTopoModuleUtil.parseCliRequestResultForShow(result);
	}
	
	private String getBr() {
		return "\n\n\n\n";
	}
	
	private String getSeparator() {
		return "----------------------------------------------------------\n\n";
	}
	
	private String formatMac(String mac){
		if(mac != null && !"".equals(mac)){
			StringBuilder tempBuffer = new StringBuilder(mac.length());
			for(int i=0;i<mac.length();i++){
				if(i>0 && i%4==0) {
					tempBuffer.append(":");
				}
				tempBuffer.append(mac.charAt(i));
			}
			return tempBuffer.toString();
		}
		return null;
	}

	public String getSnr() {
		return snr;
	}

	public void setSnr(String snr) {
		this.snr = snr;
	}
	
	private void appendContentToFile(String os_type,String os_option55){
		String exportFileName = "os_dhcp_fingerprints.txt";
		String exportFilePath = AhDirTools.getOsDetectionDir() + exportFileName;
		
		HmDomain gDomain = QueryUtil.findBoByAttribute(HmDomain.class,
				"domainName", HmDomain.GLOBAL_DOMAIN);
		
		StringBuffer content = new StringBuffer();
		content.append("OS=" + os_type);
		content.append("\r\n");
		content.append(os_option55);
		content.append("\r\n");
		content.append("END");
		content.append("\r\n");
		
		String temp = "";
		
		try {
			File file = new File(exportFilePath);
			
			if(!(file.exists())){
				String defaultFileName = "os_dhcp_fingerprints_default.txt";
				String defaultFilePath = AhDirTools.getOsDetectionDir() + defaultFileName;
				file = new File(defaultFilePath);
				if(!(file.exists())){
					addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
					return;
				}
			}
			
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			
			List<String> list = new ArrayList<String>();
			
			while((temp = br.readLine()) != null){
				list.add(temp);
			}
			
			OsVersion osVersion = null;
			if(userContext.getUserName().equalsIgnoreCase(HmUser.ADMIN_USER)){
				osVersion = QueryUtil.findBoByAttribute(OsVersion.class,"option55",client_os_option55,gDomain.getId());
			}else{
				osVersion = QueryUtil.findBoByAttribute(OsVersion.class,"option55",client_os_option55,getDomainId());
			}
			
			if(osVersion == null){
				for(int i = 0; i < list.size() ; i++){
					buf.append(list.get(i));
					buf.append("\r\n");
					if(list.get(i).toString().startsWith(ImportTextFileAction.VERSION_STR)){
						buf.append(content);
					}
				}
			}else{
				for(int i = 0; i < list.size() ; i++){
					if(list.get(i).toString().startsWith(os_option55)){
						for(int j = i ; j < list.size();j--){
							if(list.get(j).toString().startsWith("OS=")){
								list.set(j, "OS=" + os_type);
								break;
							}
						}
					}
				}
				
				for(int i = 0; i < list.size() ; i++){
					buf.append(list.get(i));
					buf.append("\r\n");
				}
			}
			
			br.close();
			file = new File(exportFilePath);
			FileOutputStream fos = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e1) {
			addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	private void deleteContentFromFile(String os_option55){
		String exportFileName = "os_dhcp_fingerprints.txt";
		String exportFilePath = AhDirTools.getOsDetectionDir() + exportFileName;
		
		String temp = "";
		
		try {
			File file = new File(exportFilePath);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			
			if(file.exists()){
				List<String> list = new ArrayList<String>();
				
				while((temp = br.readLine()) != null){
					
					list.add(temp);
				}
				
				for(int i = 0; i < list.size() ; i++){
					if(list.get(i).startsWith(os_option55)){
						for(int j = i ; j < list.size();j--){
							if(list.get(j).startsWith("OS=")){
								list.remove(j);
								i = i - 1;
								break;
							}
						}
						
						for(int j = i ; j < list.size();j++){
							if(list.get(j).startsWith("END")){
								list.remove(j);
								break;
							}
						}
						
						list.remove(i);
					}
				}
				
				for(int i = 0; i < list.size() ; i++){
					buf.append(list.get(i));
					buf.append("\r\n");
				}
					
				br.close();
				file = new File(exportFilePath);
				FileOutputStream fos = new FileOutputStream(file);
				PrintWriter pw = new PrintWriter(fos);
				pw.write(buf.toString().toCharArray());
				pw.flush();
				pw.close();
			}
		} catch (FileNotFoundException e1) {
			addActionError(MgrUtil.getUserMessage("error.fileNotExist"));
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	public String getDisplayInHomeDomain() {
		if(HmUser.ADMIN_USER.equals(getUserContext().getUserName())) {
			return "";
		}
		return "none";
	}
	
	public void updateFileVersion(){
		String exportFileName = "os_dhcp_fingerprints.txt";
		String exportFilePath = AhDirTools.getOsDetectionDir() + exportFileName;
		String temp ="";
		try {
			String version = NmsUtil.getOSOptionFileVersion(exportFilePath);
			BigDecimal b1 = new BigDecimal(version);
			BigDecimal b2 = new BigDecimal("0.1");
			float fVer = b1.add(b2).floatValue();
			String versionStr = ImportTextFileAction.VERSION_STR + String.valueOf(fVer) + "\r\n";
			File file = new File(exportFilePath);
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer buf = new StringBuffer();
			
			for(int i = 1; (temp = br.readLine()) != null && !(temp.startsWith(ImportTextFileAction.VERSION_STR));i++){
				buf = buf.append(temp);
				buf = buf.append("\r\n");
			}
			
			buf = buf.append(versionStr);
			
			while((temp = br.readLine()) != null){
				buf = buf.append(temp);
				buf = buf.append("\r\n");
			}
			
			br.close();
			FileOutputStream fos = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(fos);
			pw.write(buf.toString().toCharArray());
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
		case when position('eth' in ifname) > 0 then 
		     case when position('/' in ifname) > 0 then 
		         'eth1/' || lpad(substring(ifname from position('/' in ifname) + 1), 3, '0')
		     else
		         'eth' || lpad(substring(ifname from 4), 3, '0')
		     end
		when position('agg' in ifname) > 0 then
		    'agg' || lpad(substring(ifname from 4), 3, '0')
		end
	 * 
	 * e.g: 
	 * eth1 -> eth001
	 * eth1/1 -> eth1/001, eth1/11 -> eth1/011
	 * agg1 -> agg001, agg64 -> agg064
	 * 
	 */
	@Override
	protected void updateSortParams() {
		super.updateSortParams();
		
		// fix bug 23296
		if ("ifName".equals(orderBy)) {
			StringBuffer orderByStr = new StringBuffer();
			orderByStr.append(" case when position('eth' in ifName) > 0 then");
			orderByStr.append("      case when position('/' in ifName) > 0 then");
			orderByStr.append("           'eth1/' || lpad(substring(ifName from position('/' in ifName) + 1), 3, '0')");
			orderByStr.append("      else");
			orderByStr.append("           'eth' || lpad(substring(ifName from 4), 3, '0')");
			orderByStr.append("      end");
			orderByStr.append(" when position('agg' in ifName) > 0 then");
			orderByStr.append("     'agg' || lpad(substring(ifName from 4), 3, '0')");
			orderByStr.append(" else");
			orderByStr.append("     ifName");
			orderByStr.append(" end");
//System.out.println("======>order by " + orderByStr.toString());
			sortParams.setOrderBy(orderByStr.toString());
			
			// set primary sort column
			sortParams.setPrimaryOrderBy("apMac");
			sortParams.setPrimaryAscending(true);
		}else {
			sortParams.setPrimaryOrderBy(null);
			sortParams.setPrimaryAscending(false);
		}
	}
	
	public List<ClientTsInfo> clientTsinfo = null;
	public String tids;
	
	public List<Integer> getTidNumber(){
		if(tids != null && tids.length() > 0){
			String[] ids = tids.split(",");
			List<Integer> idList = new ArrayList<Integer>(ids.length);
			for (String str_id : ids) {
				idList.add(Integer.valueOf(str_id));
			}
			return idList;
		}
		return null;
	}

	public String getTids() {
		return tids;
	}

	public void setTids(String tids) {
		this.tids = tids;
	}

	public List<ClientTsInfo> getClientTsinfo() {
		return clientTsinfo;
	}

	public void setClientTsinfo(List<ClientTsInfo> clientTsinfo) {
		this.clientTsinfo = clientTsinfo;
	}

	private List<ClientTsInfo> getResultMessage(String message) throws Exception {
//		message = "station&nbsp;0022:5800:ccbb:<br />AC:&nbsp;BE<br />ADMCTL:&nbsp;Disable<br /><br />AC:&nbsp;BK<br />ADMCTL:&nbsp;Disable<br /><br />AC:&nbsp;Vi<br />ADMCTL:&nbsp;Enable<br />Ts&nbsp;Info&nbsp;Tid:&nbsp;6<br />direction:&nbsp;BIDI<br />min_data_rate:&nbsp;0<br />mean_data_rate:&nbsp;360000<br />peak_data_rate:&nbsp;0&nbsp;<br />inactivity_interval:&nbsp;0<br />suspension_interval:&nbsp;0&nbsp;<br />service_start_time:&nbsp;0&nbsp;<br />min_phy_rate:&nbsp;6500000&nbsp;<br />medium_time:&nbsp;2776&nbsp;<br />dot1Dtag:&nbsp;4&nbsp;<br />psb:&nbsp;0&nbsp;<br /><br /><br />AC:&nbsp;Vo<br />ADMCTL:&nbsp;Enable<br />Ts&nbsp;Info&nbsp;Tid:&nbsp;3<br />direction:&nbsp;BIDI<br />min_data_rate:&nbsp;0<br />mean_data_rate:&nbsp;83200<br />peak_data_rate:&nbsp;0&nbsp;<br />inactivity_interval:&nbsp;0<br />suspension_interval:&nbsp;0&nbsp;<br />service_start_time:&nbsp;0&nbsp;<br />min_phy_rate:&nbsp;6500000&nbsp;<br />medium_time:&nbsp;905&nbsp;<br />dot1Dtag:&nbsp;6&nbsp;<br />psb:&nbsp;0&nbsp;<br /><br /><br /><br />";
		List<ClientTsInfo> array = new ArrayList<ClientTsInfo>();
		if(message.startsWith(ClientTsInfo.STATION)){
			String[] section = message.split("<br /><br />");
			for(int i = 0; i < section.length; i ++){
				String[] tmpObj = section[i].split("<br />");
				ClientTsInfo object = new ClientTsInfo();
				for(int s = 0; s < tmpObj.length; s ++){
					if(tmpObj[s] != null && tmpObj[s] != ""){
						String msg = null;
						String[] tmp = null;
						if(tmpObj[s].indexOf(":") > 0 && tmpObj[s].indexOf("&nbsp;") > 0){
							if(tmpObj[s].indexOf(ClientTsInfo.TID) > 0){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 3){
									msg = tmp[3];
									object.setTid(msg);
								}
							}
							
							if(tmpObj[s].startsWith(ClientTsInfo.ADMCTL)){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 1){
									msg = tmp[1];
									object.setAdmctl(msg);
								}
							}
							
							if(tmpObj[s].startsWith(ClientTsInfo.AC)){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 1){
									msg = tmp[1];
									object.setAc(msg);
									
								}
							}
							
							if(tmpObj[s].startsWith(ClientTsInfo.UP_DOT1DTAG)){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 1){
									msg = tmp[1];
									object.setUp(msg);
								}
							}
							
							if(tmpObj[s].startsWith(ClientTsInfo.PSB)){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 1){
									if(NumberUtils.isNumber(tmp[1])){
										switch(Short.valueOf(tmp[1])){
											case 0:{
												msg = ClientTsInfo.UP_LEGACY;
												break;
											}
												
											case 1:{
												msg = ClientTsInfo.UP_UAPSD;
												break;
											}
										}
									}
									object.setPsb(msg);
								}
							}
							
							if(tmpObj[s].startsWith(ClientTsInfo.DIRECTION)){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 1){
									if(tmp[1].equalsIgnoreCase(ClientTsInfo.DIRECTION_BIDI)){
										msg = ClientTsInfo.DIRECTION_BI_DIRECTION;
									}else if(tmp[1].equalsIgnoreCase(ClientTsInfo.UP)){
										msg = ClientTsInfo.DIRECTION_UPLINK;
									}else{
										msg = ClientTsInfo.DIRECTION_DOWNLINK;
									}
									object.setDirection(msg);
								}
							}
							
							if(tmpObj[s].startsWith(ClientTsInfo.MEDIUM_TIME)){
								tmp = tmpObj[s].split("&nbsp;");
								if(tmp.length > 1){
									msg = tmp[1];
									object.setMediumTime(msg);
								}
							}
							
						}
					}
				}
				if(object != null && object.getTid() != null){
					array.add(object);
				}
			}
		}
		
		Collections.sort(array, new Comparator<ClientTsInfo>() {
			@Override
			public int compare(ClientTsInfo o1, ClientTsInfo o2) {
				return Integer.parseInt(o1.getTid()) - Integer.parseInt(o2.getTid());
			}
		});
		return array;
	}
	
	// fnr add for enrolled client detail information begin
	
	private boolean enableClientManagementAndEnrolled = false;
	private EnrolledClientDetail enrolledClientDetail;
	private EnrolledClientNetworkInfo enrolledClientNetworkInfo;
	private List<EnrolledClientScanResultItem> enrolledClientScanResultList;
	private List<EnrolledClientProfileItem> enrolledClientProfileList;
	private List<EnrolledClientCertificateItem> enrolledClientCertificateList;
	private List<EnrolledClientActivityLogItem> enrolledClientActivityLogList;
	private String currentCustomId;
	private String currentDeMacAddress;
	private String acmUrlSuffix;
		
	public boolean isEnableClientManagementAndEnrolled() {
		return enableClientManagementAndEnrolled;
	}

	public void setEnableClientManagementAndEnrolled(
			boolean enableClientManagementAndEnrolled) {
		this.enableClientManagementAndEnrolled = enableClientManagementAndEnrolled;
	}

	public EnrolledClientDetail getEnrolledClientDetail() {
		return enrolledClientDetail;
	}

	public EnrolledClientNetworkInfo getEnrolledClientNetworkInfo() {
		return enrolledClientNetworkInfo;
	}

	public List<EnrolledClientScanResultItem> getEnrolledClientScanResultList() {
		return enrolledClientScanResultList;
	}
	
	
	// fnr add for enrolled client detail information end
	
	
	/**
	 * The beginning of showing enrolled client detail information
	 * Author: She 2013,04,19
	 * 
	 */
	
//	private List<AppInfoForUI> enrolledAppList;
//	
//	private NetworkInfoForUI enrolledNetwork;
//	
//	private List<RestrictionsInfo> enrolledRestriction;
//	
//	private EnrolledDeviceDetailInfo enrolledDeviceInfo;
//	
//	private GeneralInfoForUI enrolledGeneral;
//	
//	private String enrolledActiveclientId;
//	
//	private String enrolledClientMacAddress;
//	
//	private DeviceSecurityInfo enrolledSecurity;
	
	private boolean enableGoogleMapKey = false;
	
	private boolean markOnPrimess = false;
	
	public boolean isMarkOnPrimess() {
		return markOnPrimess;
	}

	public void setMarkOnPrimess(boolean markOnPrimess) {
		this.markOnPrimess = markOnPrimess;
	}
	
	public boolean isEnableGoogleMapKey() {
		return enableGoogleMapKey;
	}

	public void setEnableGoogleMapKey(boolean enableGoogleMapKey) {
		this.enableGoogleMapKey = enableGoogleMapKey;
	}
	
//    public DeviceSecurityInfo getEnrolledSecurity() {
//		return enrolledSecurity;
//	}
//
//	public void setEnrolledSecurity(DeviceSecurityInfo enrolledSecurity) {
//		this.enrolledSecurity = enrolledSecurity;
//	}
//
//	public String getEnrolledClientMacAddress() {
//		return enrolledClientMacAddress;
//	}
//
//	public void setEnrolledClientMacAddress(String enrolledClientMacAddress) {
//		this.enrolledClientMacAddress = enrolledClientMacAddress;
//	}
//
//	public String getEnrolledActiveclientId() {
//		return enrolledActiveclientId;
//	}
//
//	public void setEnrolledActiveclientId(String enrolledActiveclientId) {
//		this.enrolledActiveclientId = enrolledActiveclientId;
//	}

	public String getViewClientListURL(){
		return URLUtils.getViewClientListURL();
	}
	
//  same as method getEnableClientManagement(), remove it.
//	public boolean getHideViewClientList(){
//		return QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain()).isEnableClientManagement();
//	
//	}

	public boolean getEnableClientManagement(){
		HMServicesSettings ser =  QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", getDomain());
		if (ser!=null && ser.isEnableClientManagement()) {
			return true;
		}
		return false;
	}
	
	public boolean getEnrolledClientFlag(String customId, String clientMacAdress) {
    	//Add code here to know whether this client is enrolled or not
		List<String> macList = new ArrayList<String>();
//		fnr for test
//		if (customId==null) {
//			enableClientManagementAndEnrolled=true;
//			return true;
//		}
				
		macList.add(clientMacAdress);
		EnrolledClientList  lst = new TransXMLToObjectImpl().getActiveClientListEnrolledInfo(
				new ResponseModelServiceImpl().getActiveClientListEnrolledInfo(
						customId, getDomain().getInstanceId(), macList));
		if (lst == null) {
			return false;
		}
		
		if (lst.getDeviceList()==null || lst.getDeviceList().isEmpty()) {
			return false;
		}
		
		acmUrlSuffix = ConfigUtil.getACMConfigServerViewUrl() + lst.getDeviceUrlSuffix() + lst.getDeviceList().get(0).getDeviceId();
		
		enableClientManagementAndEnrolled = lst.getDeviceList().get(0).isEnrolled();
		
		return enableClientManagementAndEnrolled;

	}

	public boolean getClientReportsPermission() {

		try {
			AccessControl.checkUserAccess(getUserContext(),
					Navigation.L2_FEATURE_CLIENTREPORT, CrudOperation.CREATE);
		} catch (HmException ex) {
			return false;
		}
    	return true;
	}

//	public List<AppInfoForUI> getEnrolledAppList() {
//		return enrolledAppList;
//	}
//
//	public void setEnrolledAppList(List<AppInfoForUI> enrolledAppList) {
//		this.enrolledAppList = enrolledAppList;
//	}
//
//	public NetworkInfoForUI getEnrolledNetwork() {
//		return enrolledNetwork;
//	}
//
//	public void setEnrolledNetwork(NetworkInfoForUI enrolledNetwork) {
//		this.enrolledNetwork = enrolledNetwork;
//	}
//
//	public List<RestrictionsInfo> getEnrolledRetriction() {
//		return enrolledRestriction;
//	}
//
//	public void setEnrolledRetriction(List<RestrictionsInfo> enrolledRestriction) {
//		this.enrolledRestriction = enrolledRestriction;
//	}
//
//	public EnrolledDeviceDetailInfo getEnrolledDeviceInfo() {
//		return enrolledDeviceInfo;
//	}
//
//	public void setEnrolledDeviceInfo(EnrolledDeviceDetailInfo enrolledDeviceInfo) {
//		this.enrolledDeviceInfo = enrolledDeviceInfo;
//	}
//
//	public GeneralInfoForUI getEnrolledGeneral() {
//		return enrolledGeneral;
//	}
//
//	public void setEnrolledGeneral(GeneralInfoForUI enrolledGeneral) {
//		this.enrolledGeneral = enrolledGeneral;
//	}
	
//	private void prepareEnrolledClientDetails() {
//		getEnrolledClientDetails();
//	}
	
	private void getEnrolledClientDetails_ScanResult(String customerId, String macAddress) {
		EnrolledClientScanResultList  lst = new TransXMLToObjectImpl().getActiveClientScanResultInfo(new ResponseModelServiceImpl().getActiveClientScanResultInfo(customerId, getDomain().getInstanceId(), macAddress));
		if (lst!=null && lst.getScanResultList()!=null && !lst.getScanResultList().isEmpty() ) {
			enrolledClientScanResultList = lst.getScanResultList();
		}
		if (enrolledClientScanResultList==null) {
			enrolledClientScanResultList = new ArrayList<EnrolledClientScanResultItem>();
		}
//		fnr for test
//		for( int i=1; i<15;i++) {
//			EnrolledClientScanResultItem  aa = new EnrolledClientScanResultItem();
//			aa.setBand(String.valueOf(i%2 +1) );
//			aa.setBssid("BSSID" + i);
//			aa.setSsid("SSID" + i);
//			if (i==1) {
//				aa.setSecurity("802_1x");
//			} else if (i==2) {
//				aa.setSecurity("wep");
//			} else if (i==3) {
//				aa.setSecurity("psk");
//			} else {
//				aa.setSecurity("none");
//			}
//			aa.setFrequency("Frequency" + i);
//			aa.setChannel("Channel" + i);
//			aa.setStrength("sdv  xsdvsvsv  Maintainers for YUI DataTable jeffcd - 5 commits last: 5 years ago, first: 5 years ago View all   " + i);
//			aa.setRssi("RSSI" + i);
//			enrolledClientScanResultList.add(aa);
//		}
		// END testCODE
		for (EnrolledClientScanResultItem aa: enrolledClientScanResultList){
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("ssid", aa.getSsid());
				jsonObj.put("bssid", aa.getBssid());
				jsonObj.put("security", aa.getSecurityString());
				jsonObj.put("band", aa.getBandString());
				jsonObj.put("channelNumber", aa.getChannel());
				jsonObj.put("strength", aa.getStrengthString());
				jsonObj.put("rssi", aa.getRssiString());
			} catch (JSONException e) {
				log.error(e);
			}
			jsonArray.put(jsonObj);
		}
	}
	
	private void getEnrolledClientDetails_ActivityLog(String customerId, String macAddress) {
		EnrolledClientActivityLogList  lsta = new TransXMLToObjectImpl().getActiveClientActivityLogInfo(new ResponseModelServiceImpl().getActiveClientActivityLogInfo(customerId, getDomain().getInstanceId(), macAddress, 200));
		if (lsta!=null && lsta.getLogList()!=null && !lsta.getLogList().isEmpty() ) {
			enrolledClientActivityLogList = lsta.getLogList();
		}
		if (enrolledClientActivityLogList==null) {
			enrolledClientActivityLogList = new ArrayList<EnrolledClientActivityLogItem>();
		}
		
//		fnr for test
//		for( int i=1; i<15;i++) {
//			EnrolledClientActivityLogItem  aa = new EnrolledClientActivityLogItem();
//			aa.setActionName("ActionName" + i);
//			aa.setDeviceName("deviceName" + i);
//			aa.setStartTime(System.currentTimeMillis());
//			aa.setEndTime(System.currentTimeMillis());
//
//			if (i==1) {
//				aa.setStatus(String.valueOf(i));
//			} else if (i==2) {
//				aa.setStatus(String.valueOf(i));
//			} else if (i==3) {
//				aa.setStatus(String.valueOf(i));
//			} else {
//				aa.setStatus(String.valueOf(4));
//			}
//			aa.setFailedReason("setFailedReason-" + i);
//			enrolledClientActivityLogList.add(aa);
//		}
		// END testCODE
		for (EnrolledClientActivityLogItem aa: enrolledClientActivityLogList){
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("status", aa.getStatusString());
				jsonObj.put("name", aa.getActionName());
				jsonObj.put("deviceName", aa.getDeviceName());
				jsonObj.put("startTime", AhDateTimeUtil.getSpecifyDateTime(aa.getStartTime(), getUserTimeZone(), getDomain()));
				jsonObj.put("endTime", AhDateTimeUtil.getSpecifyDateTime(aa.getEndTime(), getUserTimeZone(), getDomain()));
				jsonObj.put("failedReason", aa.getFailedReason());
			} catch (JSONException e) {
				log.error(e);
			}
			jsonArray.put(jsonObj);
		}
	}
	
	private void getEnrolledClientDetails_Certificate(String customerId, String macAddress) {
		EnrolledClientCertificateList  lstc = new TransXMLToObjectImpl().getActiveClientCertificateInfo(new ResponseModelServiceImpl().getActiveClientCertificateInfo(customerId, getDomain().getInstanceId(), macAddress));
		if (lstc!=null && lstc.getCertificateList()!=null && !lstc.getCertificateList().isEmpty() ) {
			enrolledClientCertificateList = lstc.getCertificateList();
		}
		if (enrolledClientCertificateList==null) {
			enrolledClientCertificateList = new ArrayList<EnrolledClientCertificateItem>();
		}
//		fnr for test
//				for( int i=1; i<15;i++) {
//					EnrolledClientCertificateItem  aa = new EnrolledClientCertificateItem();
//					aa.setIsIdentity("getIsIdentity" + i);
//					aa.setCommonName("getCommonName" + i);
//					aa.setIssued("setIssued" + i);
//					aa.setNotBefore(999999999L+ i);
//					aa.setNotAfter(999999999L + i);
//					enrolledClientCertificateList.add(aa);
//				}
		// END testCODE
				
//		JSONArray jsa = new JSONArray();
		for (EnrolledClientCertificateItem aa: enrolledClientCertificateList){
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("identity", aa.getIsIdentity());
				jsonObj.put("name", aa.getCommonName());
				jsonObj.put("issuedby", aa.getIssued());
				jsonObj.put("notBefore", AhDateTimeUtil.getSpecifyDateTime(aa.getNotBefore(), getUserTimeZone(), getDomain()));
				jsonObj.put("notAfter", AhDateTimeUtil.getSpecifyDateTime(aa.getNotAfter(), getUserTimeZone(), getDomain()));
			} catch (JSONException e) {
				log.error(e);
			}
			jsonArray.put(jsonObj);
		}
//		return jsa.toString();
	}
	
	private void getEnrolledClientDetails_Profile(String customerId, String macAddress) {
		EnrolledClientProfileList  lstp = new TransXMLToObjectImpl().getActiveClientProfileInfo(new ResponseModelServiceImpl().getActiveClientProfileInfo(customerId, getDomain().getInstanceId(), macAddress));
		if (lstp!=null && lstp.getProfileList()!=null && !lstp.getProfileList().isEmpty() ) {
			enrolledClientProfileList = lstp.getProfileList();
		}
		if (enrolledClientProfileList==null) {
			enrolledClientProfileList = new ArrayList<EnrolledClientProfileItem>();
		}
//		fnr for test
//		for( int i=1; i<15;i++) {
//			EnrolledClientProfileItem  aa = new EnrolledClientProfileItem();
//			aa.setDisplayName("getDisplayName" + i);
//			aa.setOrgnization("getOrgnization" + i);
//			aa.setIsEncrypted(i%2==0? "true": "false");
//			aa.setIsManaged("getIsManaged" + i);
//			aa.setConfigedItems("setConfigedItems" + i);
//			enrolledClientProfileList.add(aa);
//		}
		// END testCODE
		
//		JSONArray jsa = new JSONArray();
		for (EnrolledClientProfileItem aa: enrolledClientProfileList){
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("displayname", aa.getDisplayName());
				jsonObj.put("organization", aa.getOrgnization());
				jsonObj.put("encrypted", aa.getIsEncrypted().equalsIgnoreCase("true")? 
						"<span class=\"css_icon_encrypt_yes\" title=\"Yes\"></span>": "<span class=\"css_icon_encrypt_no\" title=\"No\"></span>");
				jsonObj.put("managed", aa.getIsManaged().equalsIgnoreCase("true")? 
						"<span class=\"css_icon_managed_yes\" title=\"Yes\"></span>": "<span class=\"css_icon_managed_no\" title=\"No\"></span>");
				jsonObj.put("configuredItems", aa.getConfigedItems());
			} catch (JSONException e) {
				log.error(e);
			}
			jsonArray.put(jsonObj);
		}
//		return jsa.toString();
		
	}
	
	private void getEnrolledClientDetails(String customerId, String macAddress) {
		enrolledClientDetail = new TransXMLToObjectImpl().getActiveClientDetailInfo(new ResponseModelServiceImpl().getActiveClientDetailInfo(customerId, getDomain().getInstanceId(), macAddress));
		enrolledClientNetworkInfo = new TransXMLToObjectImpl().getActiveClientNetworkInfo(new ResponseModelServiceImpl().getActiveClientNetworkInfo(customerId, getDomain().getInstanceId(), macAddress));

		if (enrolledClientDetail==null) {
			enrolledClientDetail = new EnrolledClientDetail();
		}
		enrolledClientDetail.setTz(getUserTimeZone());
		enrolledClientDetail.setOwner(getDomain());
		
		if (enrolledClientNetworkInfo==null) {
			enrolledClientNetworkInfo= new EnrolledClientNetworkInfo();
		}
		
//		fnr for test
//		enrolledClientDetail.setActiveStatus("1");
//		enrolledClientDetail.setUdid("Udid");
//		enrolledClientDetail.setEnrollmentStatus("1");
//		enrolledClientDetail.setLastConnectedTime(999999L);
//		enrolledClientDetail.setOwnerType("1");
//		enrolledClientDetail.setPublicIp("publicIP10.1.1.1");
//		long aa = System.currentTimeMillis()%4;
//		if (aa==0) {
//			enrolledClientDetail.setOsType("4");
//		} else {
//			enrolledClientDetail.setOsType(String.valueOf(aa));
//		}
//		//enrolledClientDetail.setOsType("2");
//		enrolledClientDetail.setOsVersion("OSVERSION");
//		enrolledClientDetail.setBuildVersion("buildVersion");
//		enrolledClientDetail.setModelName("ModelName");
//		enrolledClientDetail.setModel("model");
//		enrolledClientDetail.setLongitude("Longitude");
//		enrolledClientDetail.setLatitude("Latitude");
//		enrolledClientDetail.setAddress("10.1.11.1");
//		enrolledClientDetail.setDeviceName("DeviceName");
//		enrolledClientDetail.setProductName("ProductName");
//		enrolledClientDetail.setSerialNumber("SerialNumber");
//		enrolledClientDetail.setDeviceCapacity("80");
//		enrolledClientDetail.setAvailableDeviceCapacity("21");
//		enrolledClientDetail.setBatteryLevel("0.45");
//		enrolledClientDetail.setCellularTechnology("1");
//		enrolledClientDetail.setImei("IMEI");
//		enrolledClientDetail.setMeid("MEID");
//		enrolledClientDetail.setModemFirmwareVersion("ModemFirmwareVersion");
//		enrolledClientDetail.setCurrentMNC("CurrentMNC");
//		enrolledClientDetail.setChallenge("Challenge");
//		enrolledClientDetail.setUserId("UserId");
//		enrolledClientDetail.setUserLongName("UserLongName");
//		enrolledClientDetail.setUserShortName("UserShortName");
//		
//		
//		enrolledClientNetworkInfo.setIccid("ICCID");
//		enrolledClientNetworkInfo.setBlueToothMAC("BluetoothMAC");
//		enrolledClientNetworkInfo.setWifiMac("WiFiMAC");
//		enrolledClientNetworkInfo.setCarrier("CurrentCarrierNetwork");
//		enrolledClientNetworkInfo.setSimCarrierNetwork("SimCarrierNetwork");
//		enrolledClientNetworkInfo.setSubscriberCarrierNetwork("SubscriberCarrierNetwork");
//		enrolledClientNetworkInfo.setCarrierVersion("CarrierSettingsVersion");
//		enrolledClientNetworkInfo.setPhoneNumber("PhoneNumber");
//		enrolledClientNetworkInfo.setVoiceRoamingEnabled(true);
//		enrolledClientNetworkInfo.setDataRoamingEnabled(false);
//		enrolledClientNetworkInfo.setIsRoaming("IsRoaming");
//		enrolledClientNetworkInfo.setSubscriberMCC("SubscriberMCC");
//		enrolledClientNetworkInfo.setSubscriberMNC("SubscriberMNC");
//		enrolledClientNetworkInfo.setCurrentMCC("CurrentMCC");
//		enrolledClientNetworkInfo.setCellRssi("CellRssi");
//		enrolledClientNetworkInfo.setWifiRssi("WifiRssi");
//		enrolledClientNetworkInfo.setSsid("WifiSSID");
//		enrolledClientNetworkInfo.setBssid("WifiBssid");
//		enrolledClientNetworkInfo.setLinkSpeed("LinkSpeed");
				
		//ENDTODO

//		enrolledAppList = new TransXMLToObjectImpl().getApplicationInfo(new ResponseModelServiceImpl().getApplicationInfo(getDomain().getInstanceId(), enrolledActiveclientId));
//		enrolledNetwork = new TransXMLToObjectImpl().getNetworkInfo(new ResponseModelServiceImpl().getNetworkInfo(getDomain().getInstanceId(), enrolledActiveclientId));
//		enrolledRestriction = new TransXMLToObjectImpl().getRestrictionsInfoList(new ResponseModelServiceImpl().getRestrictionInfo(getDomain().getInstanceId(), enrolledActiveclientId));
////		enrolledDeviceInfo = new TransXMLToObjectImpl().getDeviceDetail(new ResponseModelServiceImpl().getDeviceDetail(getDomain().getInstanceId(), enrolledActiveclientId));
//		enrolledSecurity = new TransXMLToObjectImpl().getSecurityInfo(new ResponseModelServiceImpl().getSecurityinfo(getDomain().getInstanceId(), enrolledActiveclientId));
//		
//		if(enrolledAppList == null){
//			enrolledAppList = new ArrayList<AppInfoForUI>();
//		}else{
//			handleApplicationDataSize(enrolledAppList);
//		}
//		if( enrolledNetwork == null){
//			enrolledNetwork = new NetworkInfoForUI();
//		}
//		if( enrolledRestriction == null){
//			enrolledRestriction = new ArrayList<RestrictionsInfo>();
//		}
//		if( enrolledDeviceInfo == null){
//			enrolledDeviceInfo = new EnrolledDeviceDetailInfo();
//		}
//		enrolledGeneral = new GeneralInfoForUI();
//		try{
//			enrolledNetwork.setCellularTech(enrolledDeviceInfo.getCellularTechnology());
//			enrolledNetwork.setIpAddress(enrolledDeviceInfo.getPublicIp());
//			enrolledNetwork.setModemFirmware(enrolledDeviceInfo.getModemFirmwareVersion());
//			enrolledNetwork.setWifiMac(MacAddressUtil.addDelimiter(enrolledNetwork.getWifiMac(), 2, ":"));
//			enrolledNetwork.setBlueToothMAC(MacAddressUtil.addDelimiter(enrolledNetwork.getBlueToothMAC(), 2, ":"));
//			
//			enrolledGeneral.setStatus(enrolledDeviceInfo.getActiveStatus());
//			enrolledGeneral.setLastConnect(new Date(Long.valueOf(enrolledDeviceInfo.getLastCon())).toString());
//			enrolledGeneral.setBatteryLevel(getPercentage(enrolledDeviceInfo.getBatteryLevel()) + "Capacity");
//			enrolledGeneral.setBatteryPercentage(getPercentage(enrolledDeviceInfo.getBatteryLevel()));
//			String tempStoragePercentage = String.valueOf((Double.parseDouble(enrolledDeviceInfo.getAvailableDeviceCapacity()))/(Double.parseDouble(enrolledDeviceInfo.getDeviceCapacity()))).toString();
//			enrolledGeneral.setStoragePercentage(getPercentage(tempStoragePercentage));
//			enrolledGeneral.setDeviceStorage(enrolledDeviceInfo.getAvailableDeviceCapacity().substring(0,enrolledDeviceInfo.getAvailableDeviceCapacity().indexOf(".")+3) 
//					+ "GB free of "
//					+ enrolledDeviceInfo.getDeviceCapacity().substring(0,enrolledDeviceInfo.getDeviceCapacity().indexOf(".")+3) + "GB");
//			enrolledGeneral.setDeviceType(enrolledDeviceInfo.getModelName());
//			enrolledGeneral.setPhoneNum(enrolledNetwork.getPhoneNumber());
//			enrolledGeneral.setUdid(enrolledDeviceInfo.getUdid());
//			enrolledGeneral.setPasswordPresent(enrolledSecurity.getPasscodePresent());
//			enrolledGeneral.setDataProtection(enrolledSecurity.getDataProtection() == true ? "true" : "false");
//		}catch(Exception e){
//			log.error(ClientMonitorAction.class.getSimpleName() + ": getEnrolledClientDetails()",e.getMessage());
//		}
		
	}
	
//	private String getPercentage(String value){
//		Double tempValue = Double.parseDouble(value)*100;
//		if(String.valueOf(tempValue).indexOf(".") > 0 
//				&& String.valueOf(tempValue).substring(String.valueOf(tempValue).indexOf(".") + 1).length() >= 2 ){
//					return String.valueOf(tempValue).substring(0,String.valueOf(tempValue).indexOf(".") + 3) + "%";
//			}
//		if(String.valueOf(tempValue).indexOf(".") > 0
//				&& String.valueOf(tempValue).substring(String.valueOf(tempValue).indexOf(".") + 1 ).length() < 2 ){
//			return String.valueOf(tempValue) + "0%";
//		}
//		return value + "%";
//	}
//	private void handleApplicationDataSize(List<AppInfoForUI> appList){
//		for(AppInfoForUI app : appList){
//			app.setBundleSize(applicationDataTransfer(Double.parseDouble(app.getBundleSize())));
//			app.setDynamicSize(applicationDataTransfer(Double.parseDouble(app.getDynamicSize())));
//		}
//	}
//	private String applicationDataTransfer(double size){
//		double tempsize = size/1024/1024/1024;
//		double tempMbSize = size/1024/1024;
//		double tempKbSize = size/1024;
//		try{
//			if(tempsize > 1){
//				if((tempsize + "").length() - (tempsize + "").indexOf(".") >= 2){
//					return (tempsize + "" ).substring(0,(tempsize + "").indexOf(".") + 3 ) + "GB";
//				}else{
//					return tempsize + "GB";
//				}
//			}else if(tempMbSize > 1){
//				if((tempMbSize + "").length() - (tempMbSize + "").indexOf(".") >= 2){
//					return (tempMbSize + "" ).substring(0,(tempMbSize + "").indexOf(".") + 3 ) + "MB";
//				}else{
//					return tempMbSize + "MB";
//				}
//			}else if(tempKbSize> 1){
//				if((tempKbSize+ "").length() - (tempKbSize+ "").indexOf(".") >= 2){
//					return (tempKbSize+ "" ).substring(0,(tempKbSize+ "").indexOf(".") + 3 ) + "KB";
//				}else{
//					return tempKbSize + "KB";
//				}
//			}else{
//				if((size + "").length() - (size + "").indexOf(".") >= 2){
//					return (size + "" ).substring(0,(size + "").indexOf(".") + 3 ) + "B";
//				}else{
//					return size + "KB";
//				}
//			}
//		}catch(Exception e){
//			return null;
//		}
//		
//	}
	// The end
	//add to get Google Map Key
	public void validateGoogleMapKey(){
		getGmeKey();
	}
	public String getGmeKey() {
		String licenseKey = NmsUtil.getGmLicenseKey();
		if (NmsUtil.isPlanner() || NmsUtil.isDemoHHM()) {
			// Usage is free for these servers
			markOnPrimess = false;
			String apiKey = NmsUtil.getGmAPIKey();
			if (apiKey != null && apiKey.length() > 0) {
				return "&key=" + apiKey;
			} else {
				log.info_ln("GM API key missing.");
				return "";
			}
		} else if (NmsUtil.isHostedHMApplication()) {
			// HMOL customers
			markOnPrimess = false;
			if (licenseKey != null && licenseKey.length() > 0) {
				String vhmId = getDomain().getVhmID();
				if (vhmId == null || vhmId.length() == 0) {
					log.info_ln("Missing - vhm - id");
					vhmId = "no-vhm-id";
				}
				return "&client=" + licenseKey + "&channel=" + vhmId;
			} else {
				log.info_ln("GM License key missing.");
				return "";
			}
		} else {
			// on-premise customers
			markOnPrimess = true;
			if (licenseKey != null && licenseKey.length() > 0) {
				String systemId = BeLicenseModule.HIVEMANAGER_SYSTEM_ID;
				if (systemId == null || systemId.length() == 0) {
					log.info_ln("Missing system id");
					/*systemId = "no-system-id";*/
					enableGoogleMapKey = true;
					return NmsUtil.getGmAPIKey();
				}
				return "&client=" + licenseKey + "&channel=" + systemId;
			} else {
				log.info_ln("GM License key missing.");
				enableGoogleMapKey = true;
				return NmsUtil.getGmAPIKey();
			}
		}
	}

	public String getCurrentCustomId() {
		return currentCustomId;
	}

	public void setCurrentCustomId(String currentCustomId) {
		this.currentCustomId = currentCustomId;
	}

	public String getCurrentDeMacAddress() {
		return currentDeMacAddress;
	}

	public void setCurrentDeMacAddress(String currentDeMacAddress) {
		this.currentDeMacAddress = currentDeMacAddress;
	}

	public List<EnrolledClientProfileItem> getEnrolledClientProfileList() {
		return enrolledClientProfileList;
	}

	public void setEnrolledClientProfileList(
			List<EnrolledClientProfileItem> enrolledClientProfileList) {
		this.enrolledClientProfileList = enrolledClientProfileList;
	}

	public List<EnrolledClientCertificateItem> getEnrolledClientCertificateList() {
		return enrolledClientCertificateList;
	}

	public void setEnrolledClientCertificateList(
			List<EnrolledClientCertificateItem> enrolledClientCertificateList) {
		this.enrolledClientCertificateList = enrolledClientCertificateList;
	}

	public List<EnrolledClientActivityLogItem> getEnrolledClientActivityLogList() {
		return enrolledClientActivityLogList;
	}

	public void setEnrolledClientActivityLogList(
			List<EnrolledClientActivityLogItem> enrolledClientActivityLogList) {
		this.enrolledClientActivityLogList = enrolledClientActivityLogList;
	}

	public String getAcmUrlSuffix() {
		return acmUrlSuffix;
	}

	public void setAcmUrlSuffix(String acmUrlSuffix) {
		this.acmUrlSuffix = acmUrlSuffix;
	}

	public String getFilterApMac() {
		return filterApMac;
	}

	public void setFilterApMac(String filterApMac) {
		this.filterApMac = filterApMac;
	}
}